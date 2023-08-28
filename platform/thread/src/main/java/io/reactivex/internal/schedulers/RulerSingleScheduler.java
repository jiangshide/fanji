package io.reactivex.internal.schedulers;

import com.fanji.android.thread.pool.RulerThreadFactory;

import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import io.reactivex.Scheduler;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.internal.disposables.EmptyDisposable;
import io.reactivex.plugins.RxJavaPlugins;

/**
 *
 * <b>RxJava SingleScheduler 的替换类，增加监控相关功能，其他逻辑保持不变</b>
 * A scheduler with a shared, single threaded underlying ScheduledExecutorService.
 * @author 蒋世德 @ fanji inc
 * @email jiangshide@fanji.com
 * @since 21-6-1 下午3:13
 */
public class RulerSingleScheduler extends Scheduler {

    private final ThreadFactory threadFactory;
    private final AtomicReference<ScheduledExecutorService> executor = new AtomicReference<>();

    /**
     * The name of the system property for setting the thread priority for this Scheduler.
     */
    private static final String KEY_SINGLE_PRIORITY = "rx2.single-priority";

    private static final String THREAD_NAME_PREFIX = "ZH_RxSingleScheduler";

    private static final RxThreadFactory SINGLE_THREAD_FACTORY;

    private static final ScheduledExecutorService SHUTDOWN;

    static {
        SHUTDOWN = Executors.newScheduledThreadPool(0);
        SHUTDOWN.shutdown();
        int priority = Math.max(Thread.MIN_PRIORITY, Math.min(Thread.MAX_PRIORITY,
                SafeInteger.getInteger(KEY_SINGLE_PRIORITY, Thread.NORM_PRIORITY)));

        SINGLE_THREAD_FACTORY = new RxThreadFactory(THREAD_NAME_PREFIX, priority, true);
    }

    public RulerSingleScheduler() {
        this(SINGLE_THREAD_FACTORY);
    }

    /**
     * @param threadFactory thread factory to use for creating worker threads. Note that this takes precedence over any
     *                      system properties for configuring new thread creation. Cannot be null.
     */
    public RulerSingleScheduler(ThreadFactory threadFactory) {
        this.threadFactory = new RulerThreadFactory("RulerSingleScheduler", threadFactory);
        executor.lazySet(createExecutor(this.threadFactory));
    }

    private static ScheduledExecutorService createExecutor(ThreadFactory threadFactory) {
        return SchedulerPoolFactory.create(threadFactory);
    }

    @Override
    public void start() {
        ScheduledExecutorService next = null;
        for (; ; ) {
            ScheduledExecutorService current = executor.get();
            if (current != SHUTDOWN) {
                if (next != null) {
                    next.shutdown();
                }
                return;
            }
            if (next == null) {
                next = createExecutor(threadFactory);
            }
            if (executor.compareAndSet(current, next)) {
                return;
            }

        }
    }

    @Override
    public void shutdown() {
        ScheduledExecutorService current = executor.get();
        if (current != SHUTDOWN) {
            current = executor.getAndSet(SHUTDOWN);
            if (current != SHUTDOWN) {
                current.shutdownNow();
            }
        }
    }

    @NonNull
    @Override
    public Worker createWorker() {
        return new ScheduledWorker(executor.get());
    }

    @NonNull
    @Override
    public Disposable scheduleDirect(@NonNull Runnable run, long delay, TimeUnit unit) {
        ScheduledDirectTask task = new ScheduledDirectTask(RxJavaPlugins.onSchedule(run));
        try {
            Future<?> f;
            if (delay <= 0L) {
                f = executor.get().submit(task);
            } else {
                f = executor.get().schedule(task, delay, unit);
            }
            task.setFuture(f);
            return task;
        } catch (RejectedExecutionException ex) {
            RxJavaPlugins.onError(ex);
            return EmptyDisposable.INSTANCE;
        }
    }

    @NonNull
    @Override
    public Disposable schedulePeriodicallyDirect(@NonNull Runnable run, long initialDelay, long period, TimeUnit unit) {
        final Runnable decoratedRun = RxJavaPlugins.onSchedule(run);
        if (period <= 0L) {

            ScheduledExecutorService exec = executor.get();

            InstantPeriodicTask periodicWrapper = new InstantPeriodicTask(decoratedRun, exec);
            Future<?> f;
            try {
                if (initialDelay <= 0L) {
                    f = exec.submit(periodicWrapper);
                } else {
                    f = exec.schedule(periodicWrapper, initialDelay, unit);
                }
                periodicWrapper.setFirst(f);
            } catch (RejectedExecutionException ex) {
                RxJavaPlugins.onError(ex);
                return EmptyDisposable.INSTANCE;
            }

            return periodicWrapper;
        }
        ScheduledDirectPeriodicTask task = new ScheduledDirectPeriodicTask(decoratedRun);
        try {
            Future<?> f = executor.get().scheduleAtFixedRate(task, initialDelay, period, unit);
            task.setFuture(f);
            return task;
        } catch (RejectedExecutionException ex) {
            RxJavaPlugins.onError(ex);
            return EmptyDisposable.INSTANCE;
        }
    }

    static final class ScheduledWorker extends Scheduler.Worker {

        final ScheduledExecutorService executor;

        final CompositeDisposable tasks;

        volatile boolean disposed;

        ScheduledWorker(ScheduledExecutorService executor) {
            this.executor = executor;
            this.tasks = new CompositeDisposable();
        }

        @NonNull
        @Override
        public Disposable schedule(@NonNull Runnable run, long delay, @NonNull TimeUnit unit) {
            if (disposed) {
                return EmptyDisposable.INSTANCE;
            }

            Runnable decoratedRun = RxJavaPlugins.onSchedule(run);

            ScheduledRunnable sr = new ScheduledRunnable(decoratedRun, tasks);
            tasks.add(sr);

            try {
                Future<?> f;
                if (delay <= 0L) {
                    f = executor.submit((Callable<Object>) sr);
                } else {
                    f = executor.schedule((Callable<Object>) sr, delay, unit);
                }

                sr.setFuture(f);
            } catch (RejectedExecutionException ex) {
                dispose();
                RxJavaPlugins.onError(ex);
                return EmptyDisposable.INSTANCE;
            }

            return sr;
        }

        @Override
        public void dispose() {
            if (!disposed) {
                disposed = true;
                tasks.dispose();
            }
        }

        @Override
        public boolean isDisposed() {
            return disposed;
        }
    }
}

