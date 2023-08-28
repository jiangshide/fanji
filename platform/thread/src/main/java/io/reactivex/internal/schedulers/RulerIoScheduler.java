package io.reactivex.internal.schedulers;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.fanji.android.thread.pool.RulerExecutors;
import com.fanji.android.thread.pool.RulerThreadFactory;
import com.fanji.android.util.LogUtil;

import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import io.reactivex.Scheduler;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.internal.disposables.EmptyDisposable;

import static com.fanji.android.thread.FJThread.isDebug;

/**
 * Scheduler that creates and caches a set of thread pools and reuses them if possible.
 * <p><b>RxJava IoScheduler 的替换类，改动如下：</b></p>
 * <p><b>原逻辑：</b></p>
 * <p>1.Worker 执行完放入过期队列，等待复用或过期清理</p>
 * <p>2.执行新任务时，优先使用等待复用的 Worker</p>
 * <p>3.如果没有复用 Worker，便创建新的 Worker</p>
 * <p>4.清理任务只清理过期队列中已过期的 Worker</p>
 * <p><b>新逻辑：</b></p>
 * <p>1.Worker 增加复用次数</p>
 * <p>2.当 Worker 的复用次数为 0 时，才表明 Worker 空闲</p>
 * <p>3.增加线程数量阈值，当超过阈值时复用目前正在繁忙的 Worker（当然优先还是复用空闲 Worker）</p>
 *
 * @author 蒋世德 @ fanji inc
 * @email jiangshide@fanji.com
 * @since 21-6-1 下午3:11
 */
@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public final class RulerIoScheduler extends Scheduler {
    private static final String WORKER_THREAD_NAME_PREFIX = "ZH_RxCachedThreadScheduler";
    private static final RxThreadFactory WORKER_THREAD_FACTORY;

    private static final String EVICTOR_THREAD_NAME_PREFIX = "ZH_RxCachedWorkerPoolEvictor";
    private static final RxThreadFactory EVICTOR_THREAD_FACTORY;

    private static final int DEFAULT_MAX_THREAD_COUNT = 100;

    private static final long KEEP_ALIVE_TIME = 60;
    private static final TimeUnit KEEP_ALIVE_UNIT = TimeUnit.SECONDS;

    private static final ThreadWorker SHUTDOWN_THREAD_WORKER;
    private final ThreadFactory threadFactory;
    private final AtomicReference<CachedWorkerPool> pool;
    private final int maxThreadCount;

    /**
     * The name of the system property for setting the thread priority for this Scheduler.
     */
    private static final String KEY_IO_PRIORITY = "rx2.io-priority";

    private static final CachedWorkerPool NONE;

    static {
        SHUTDOWN_THREAD_WORKER = new ThreadWorker(new RxThreadFactory("ZH_RxCachedThreadSchedulerShutdown"));
        SHUTDOWN_THREAD_WORKER.dispose();

        int priority = Math.max(Thread.MIN_PRIORITY, Math.min(Thread.MAX_PRIORITY,
                SafeInteger.getInteger(KEY_IO_PRIORITY, Thread.NORM_PRIORITY)));

        WORKER_THREAD_FACTORY = new RxThreadFactory(WORKER_THREAD_NAME_PREFIX, priority);

        EVICTOR_THREAD_FACTORY = new RxThreadFactory(EVICTOR_THREAD_NAME_PREFIX, priority);

        NONE = new CachedWorkerPool(0, null, WORKER_THREAD_FACTORY, 0);
        NONE.shutdown();
    }

    /**
     * 用于管理所有 ThreadWorker 的类，包括何时创建新线程，何时使用缓存线程等
     */
    static final class CachedWorkerPool implements Runnable {
        private final long keepAliveTime;
        private final ConcurrentLinkedQueue<ThreadWorker> idleWorkerQueue;
        private final ConcurrentLinkedDeque<ThreadWorker> allWorkersCopy;
        final CompositeDisposable allWorkers;
        private final ScheduledExecutorService evictorService;
        private final Future<?> evictorTask;
        private final ThreadFactory threadFactory;
        private volatile int maxThreadCount;

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        CachedWorkerPool(long keepAliveTime, TimeUnit unit, ThreadFactory threadFactory, int maxThreadCount) {
            this.keepAliveTime = unit != null ? unit.toNanos(keepAliveTime) : 0L;
            this.idleWorkerQueue = new ConcurrentLinkedQueue<>();
            this.allWorkersCopy = new ConcurrentLinkedDeque<>();
            this.allWorkers = new CompositeDisposable();
            this.threadFactory = threadFactory;
            this.maxThreadCount = maxThreadCount;

            ScheduledExecutorService evictor = null;
            Future<?> task = null;
            if (unit != null) {
                evictor = RulerExecutors.newScheduledThreadPool(1,
                        EVICTOR_THREAD_FACTORY, "RulerIoSchedulerEvictor");
                task = evictor.scheduleWithFixedDelay(this, this.keepAliveTime, this.keepAliveTime, TimeUnit.NANOSECONDS);
            }
            evictorService = evictor;
            evictorTask = task;
        }

        @Override
        public void run() {
            evictExpiredWorkers();
        }

        private void setMaxThreadCount(int maxThreadCount) {
            this.maxThreadCount = maxThreadCount;
        }

        ThreadWorker get() {
            if (allWorkers.isDisposed()) {
                return SHUTDOWN_THREAD_WORKER;
            }

            //优先从空闲队列取
            while (!idleWorkerQueue.isEmpty()) {
                ThreadWorker idleWorker = idleWorkerQueue.poll();
                if (idleWorker != null && idleWorker.getActionCount() == 0) {
                    if (isDebug())
                        LogUtil.d("zkw", "On get idle:" + idleWorker.getId());
                    idleWorker.incrementActionCount();
                    return idleWorker;
                }
                // actionCount 不为 0，丢弃并查找下一个
            }

            // 如果线程数量超过阈值 DEFAULT_MAX_THREAD_COUNT，则复用正在运行的 ThreadWorker
            if (allWorkers.size() >= maxThreadCount) {
                while (!allWorkersCopy.isEmpty()) {
                    ThreadWorker reuseWorker = allWorkersCopy.pollFirst();
                    if (reuseWorker != null && !reuseWorker.isDisposed()) {
                        if (isDebug())
                            LogUtil.d("zkw", "Count exceed! use copied ThreadWorker: " + reuseWorker.getId() +
                                    ", actionCount:" + reuseWorker.getActionCount() +
                                    "all:" + allWorkers.size() + ", all copy:" + allWorkersCopy.size());
                        reuseWorker.incrementActionCount();
                        allWorkersCopy.addLast(reuseWorker);
                        return reuseWorker;
                    }
                }
            }
            // 如果既没有空闲 Worker，线程数量又没有超过阈值，则使用新 ThreadWorker
            ThreadWorker newWorker = new ThreadWorker(threadFactory);
            if (isDebug())
                LogUtil.d("zkw", "Create new: " + newWorker.getId() + ", all:" + allWorkers.size());
            allWorkers.add(newWorker);
            allWorkersCopy.addLast(newWorker);
            return newWorker;
        }

        /**
         * 当一个 action 执行完成，回调 release：
         * <p/>worker 中的计数器减一;
         * <p/>如果计数器为 0，更新 worker 的过期时间，并添加到 {@link #idleWorkerQueue} 中
         *
         * @param threadWorker
         */
        void release(ThreadWorker threadWorker) {
            if (isDebug())
                LogUtil.d("zkw", "Release ThreadWorker ! --> " + threadWorker.getId() +
                        ", actionCount: " + threadWorker.getActionCount() +
                        ", all copy workers:" + allWorkersCopy.size() +
                        ", idle size:" + idleWorkerQueue.size());

            // 计数器减一
            int count = threadWorker.decrementActionCount();
            // 计数器为 0 时，说明 Worker 空闲
            if (count == 0) {
                // 更新过期时间
                threadWorker.setExpirationTime(now() + keepAliveTime);
                // 添加到空闲队列的队尾
                idleWorkerQueue.offer(threadWorker);
            }
        }

        /**
         * 周期清理任务的实现方法
         */
        void evictExpiredWorkers() {
            if (isDebug())
                LogUtil.d("zkw", "EvictExpired start allWorkers: " + allWorkers.size() +
                        ", allWorkersCopy: " + allWorkersCopy.size() +
                        ", idle: " + idleWorkerQueue.size());

            if (!idleWorkerQueue.isEmpty()) {
                long currentTimestamp = now();

                for (ThreadWorker threadWorker : idleWorkerQueue) {
                    // 无需判断 actionCount == 0，因为 idle 里的 ThreadWorker 必然是空闲的
                    if (threadWorker.getExpirationTime() <= currentTimestamp) {
                        if (isDebug())
                            LogUtil.d("zkw", "Remove threadWork:" + threadWorker.getId() + ", actionCount:" + threadWorker.getActionCount());
                        if (idleWorkerQueue.remove(threadWorker)) {
                            allWorkersCopy.remove(threadWorker);
                            allWorkers.remove(threadWorker);
                        }
                    } else {
                        // 由于是按时间顺序从旧到新添加到 idle 的，所以后面的都没有超时，直接终止循环
                        if (isDebug())
                            LogUtil.d("zkw", "Don't rm threadWork:" + threadWorker.getId() + ", actionCount:" + threadWorker.getActionCount());
                        break;

                    }
                }
            }
            if (isDebug())
                LogUtil.d("zkw", "EvictExpired finished allWorkers: " + allWorkers.size() +
                        ", allWorkersCopy: " + allWorkersCopy.size() +
                        ", idle: " + idleWorkerQueue.size());
        }

        long now() {
            return System.nanoTime();
        }

        void shutdown() {
            allWorkers.dispose();
            allWorkersCopy.clear();
            if (evictorTask != null) {
                evictorTask.cancel(true);
            }
            if (evictorService != null) {
                evictorService.shutdownNow();
            }
        }
    }

    public RulerIoScheduler() {
        this(WORKER_THREAD_FACTORY, DEFAULT_MAX_THREAD_COUNT);
    }

    public RulerIoScheduler(int maxThreadCount) {
        this(WORKER_THREAD_FACTORY, maxThreadCount);
    }

    /**
     * @param threadFactory thread factory to use for creating worker threads. Note that this takes precedence over any
     *                      system properties for configuring new thread creation. Cannot be null.
     */
    public RulerIoScheduler(ThreadFactory threadFactory, int maxThreadCount) {
        this.maxThreadCount = maxThreadCount;
        // 使用 Ruler，监控线程数量
        this.threadFactory = new RulerThreadFactory("RulerIoScheduler", threadFactory);
        this.pool = new AtomicReference<>(NONE);
        start();
    }

    /**
     * 动态调整最大线程数量
     *
     * @param maxThreadCount
     */
    public void setMaxThreadCount(int maxThreadCount) {
        if (maxThreadCount > 0) {
            pool.get().setMaxThreadCount(maxThreadCount);
        }
    }

    @Override
    public void start() {
        CachedWorkerPool update = new CachedWorkerPool(KEEP_ALIVE_TIME, KEEP_ALIVE_UNIT, threadFactory, maxThreadCount);
        if (!pool.compareAndSet(NONE, update)) {
            update.shutdown();
        }
    }

    @Override
    public void shutdown() {
        for (; ; ) {
            CachedWorkerPool curr = pool.get();
            if (curr == NONE) {
                return;
            }
            if (pool.compareAndSet(curr, NONE)) {
                curr.shutdown();
                return;
            }
        }
    }

    @NonNull
    @Override
    public Worker createWorker() {
        return new EventLoopWorker(pool.get());
    }

    public int size() {
        return pool.get().allWorkers.size();
    }

    /**
     * ThreadWorker 的一层包装，用于拦截 dispose 操作（避免 ThreadWorker 真的被 dispose 掉）
     */
    static final class EventLoopWorker extends Scheduler.Worker {
        private final CompositeDisposable tasks;
        private final CachedWorkerPool pool;
        private final ThreadWorker threadWorker;

        final AtomicBoolean once = new AtomicBoolean();

        EventLoopWorker(CachedWorkerPool pool) {
            this.pool = pool;
            this.tasks = new CompositeDisposable();
            this.threadWorker = pool.get();
        }

        @Override
        public void dispose() {
            // 一个 Action 执行完，回调 dispose
            if (once.compareAndSet(false, true)) {
                tasks.dispose();
                pool.release(threadWorker);
            }
        }

        @Override
        public boolean isDisposed() {
            return once.get();
        }

        @NonNull
        @Override
        public Disposable schedule(@NonNull Runnable action, long delayTime, @NonNull TimeUnit unit) {
            if (tasks.isDisposed()) {
                // don't schedule, we are unsubscribed
                return EmptyDisposable.INSTANCE;
            }

            return threadWorker.scheduleActual(action, delayTime, unit, tasks);
        }
    }

    /**
     * 真正执行任务的 Worker 线程，一个 Worker 对应一个 Single 线程池
     */
    static final class ThreadWorker extends NewThreadWorker {

        private static int workerCount = 1;

        private long expirationTime;

        // 初始化便直接调度 Action，所以从 1 开始
        private final AtomicInteger actionCount = new AtomicInteger(1);

        /**
         * ThreadWorker 的唯一 id，方便日志分析
         */
        private final int id;

        ThreadWorker(ThreadFactory threadFactory) {
            super(threadFactory);
            this.expirationTime = 0L;
            this.id = workerCount++;
        }

        long getExpirationTime() {
            return expirationTime;
        }

        void setExpirationTime(long expirationTime) {
            this.expirationTime = expirationTime;
        }

        int getActionCount() {
            return actionCount.get();
        }

        void incrementActionCount() {
            actionCount.incrementAndGet();
        }

        int decrementActionCount() {
            return actionCount.decrementAndGet();
        }

        int getId() {
            return id;
        }
    }
}
