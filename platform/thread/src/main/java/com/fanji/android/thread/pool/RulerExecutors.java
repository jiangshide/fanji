package com.fanji.android.thread.pool;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.security.PrivilegedAction;
import java.security.PrivilegedExceptionAction;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

/**
 * Executors 的包装类，增加对线程池的管理功能，其他逻辑与 Executors 实现保持一致
 *
 * @author 蒋世德 @ fanji inc
 * @email jiangshide@fanji.com
 * @since 21-6-1 下午2:34
 */
public class RulerExecutors {

    /**
     * Creates a thread pool that reuses a fixed number of threads
     * operating off a shared unbounded queue.  At any point, at most
     * {@code nThreads} threads will be active processing tasks.
     * If additional tasks are submitted when all threads are active,
     * they will wait in the queue until a thread is available.
     * If any thread terminates due to a failure during execution
     * prior to shutdown, a new one will take its place if needed to
     * execute subsequent tasks.  The threads in the pool will exist
     * until it is explicitly {@link ExecutorService#shutdown shutdown}.
     *
     * @param nThreads the number of threads in the pool
     * @return the newly created thread pool
     * @throws IllegalArgumentException if {@code nThreads <= 0}
     */
    public static ExecutorService newFixedThreadPool(int nThreads, String poolName) {
        return new RulerThreadPoolExecutor(nThreads, nThreads,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(), poolName);
    }


    /**
     * Creates a thread pool that reuses a fixed number of threads
     * operating off a shared unbounded queue, using the provided
     * ThreadFactory to create new threads when needed.  At any point,
     * at most {@code nThreads} threads will be active processing
     * tasks.  If additional tasks are submitted when all threads are
     * active, they will wait in the queue until a thread is
     * available.  If any thread terminates due to a failure during
     * execution prior to shutdown, a new one will take its place if
     * needed to execute subsequent tasks.  The threads in the pool will
     * exist until it is explicitly {@link ExecutorService#shutdown
     * shutdown}.
     *
     * @param nThreads      the number of threads in the pool
     * @param threadFactory the factory to use when creating new threads
     * @return the newly created thread pool
     * @throws NullPointerException     if threadFactory is null
     * @throws IllegalArgumentException if {@code nThreads <= 0}
     */
    public static ExecutorService newFixedThreadPool(int nThreads, ThreadFactory threadFactory, String poolName) {
        return new RulerThreadPoolExecutor(nThreads, nThreads,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(), threadFactory, poolName);
    }

    /**
     * Creates an Executor that uses a single worker thread operating
     * off an unbounded queue. (Note however that if this single
     * thread terminates due to a failure during execution prior to
     * shutdown, a new one will take its place if needed to execute
     * subsequent tasks.)  Tasks are guaranteed to execute
     * sequentially, and no more than one task will be active at any
     * given time. Unlike the otherwise equivalent
     * {@code newFixedThreadPool(1)} the returned executor is
     * guaranteed not to be reconfigurable to use additional threads.
     *
     * @return the newly created single-threaded Executor
     */
    public static ExecutorService newSingleThreadExecutor(String poolName) {
        return Executors.newSingleThreadExecutor(new RulerThreadFactory(poolName));
    }

    /**
     * Creates an Executor that uses a single worker thread operating
     * off an unbounded queue, and uses the provided ThreadFactory to
     * create a new thread when needed. Unlike the otherwise
     * equivalent {@code newFixedThreadPool(1, threadFactory)} the
     * returned executor is guaranteed not to be reconfigurable to use
     * additional threads.
     *
     * @param threadFactory the factory to use when creating new
     *                      threads
     * @return the newly created single-threaded Executor
     * @throws NullPointerException if threadFactory is null
     */
    public static ExecutorService newSingleThreadExecutor(ThreadFactory threadFactory, String poolName) {
        return Executors.newSingleThreadExecutor(new RulerThreadFactory(poolName, threadFactory));
    }

    /**
     * Creates a thread pool that creates new threads as needed, but
     * will reuse previously constructed threads when they are
     * available.  These pools will typically improve the performance
     * of programs that execute many short-lived asynchronous tasks.
     * Calls to {@code execute} will reuse previously constructed
     * threads if available. If no existing thread is available, a new
     * thread will be created and added to the pool. Threads that have
     * not been used for sixty seconds are terminated and removed from
     * the cache. Thus, a pool that remains idle for long enough will
     * not consume any resources. Note that pools with similar
     * properties but different details (for example, timeout parameters)
     * may be created using {@link RulerThreadPoolExecutor} constructors.
     *
     * @return the newly created thread pool
     */
    public static ExecutorService newCachedThreadPool(String poolName) {
        return new RulerThreadPoolExecutor(0, Integer.MAX_VALUE,
                60L, TimeUnit.SECONDS,
                new SynchronousQueue<>(), poolName);
    }

    /**
     * Creates a thread pool that creates new threads as needed, but
     * will reuse previously constructed threads when they are
     * available, and uses the provided
     * ThreadFactory to create new threads when needed.
     *
     * @param threadFactory the factory to use when creating new threads
     * @return the newly created thread pool
     * @throws NullPointerException if threadFactory is null
     */
    public static ExecutorService newCachedThreadPool(ThreadFactory threadFactory, String poolName) {
        return new RulerThreadPoolExecutor(0, Integer.MAX_VALUE,
                60L, TimeUnit.SECONDS,
                new SynchronousQueue<>(),
                threadFactory, poolName);
    }

    /**
     * Creates a single-threaded executor that can schedule commands
     * to run after a given delay, or to execute periodically.
     * (Note however that if this single
     * thread terminates due to a failure during execution prior to
     * shutdown, a new one will take its place if needed to execute
     * subsequent tasks.)  Tasks are guaranteed to execute
     * sequentially, and no more than one task will be active at any
     * given time. Unlike the otherwise equivalent
     * {@code newScheduledThreadPool(1)} the returned executor is
     * guaranteed not to be reconfigurable to use additional threads.
     *
     * @return the newly created scheduled executor
     */
    public static ScheduledExecutorService newSingleThreadScheduledExecutor(String poolName) {
        return Executors.newSingleThreadScheduledExecutor(new RulerThreadFactory(poolName, null));
    }

    /**
     * Creates a single-threaded executor that can schedule commands
     * to run after a given delay, or to execute periodically.  (Note
     * however that if this single thread terminates due to a failure
     * during execution prior to shutdown, a new one will take its
     * place if needed to execute subsequent tasks.)  Tasks are
     * guaranteed to execute sequentially, and no more than one task
     * will be active at any given time. Unlike the otherwise
     * equivalent {@code newScheduledThreadPool(1, threadFactory)}
     * the returned executor is guaranteed not to be reconfigurable to
     * use additional threads.
     *
     * @param threadFactory the factory to use when creating new
     *                      threads
     * @return a newly created scheduled executor
     * @throws NullPointerException if threadFactory is null
     */
    public static ScheduledExecutorService newSingleThreadScheduledExecutor(ThreadFactory threadFactory, String poolName) {
        return Executors.newSingleThreadScheduledExecutor(new RulerThreadFactory(poolName, threadFactory));
    }

    /**
     * Creates a thread pool that can schedule commands to run after a
     * given delay, or to execute periodically.
     *
     * @param corePoolSize the number of threads to keep in the pool,
     *                     even if they are idle
     * @return a newly created scheduled thread pool
     * @throws IllegalArgumentException if {@code corePoolSize < 0}
     */
    public static ScheduledExecutorService newScheduledThreadPool(int corePoolSize, String poolName) {
        return new RulerScheduledThreadPoolExecutor(corePoolSize, poolName);
    }

    /**
     * Creates a thread pool that can schedule commands to run after a
     * given delay, or to execute periodically.
     *
     * @param corePoolSize  the number of threads to keep in the pool,
     *                      even if they are idle
     * @param threadFactory the factory to use when the executor
     *                      creates a new thread
     * @return a newly created scheduled thread pool
     * @throws IllegalArgumentException if {@code corePoolSize < 0}
     * @throws NullPointerException     if threadFactory is null
     */
    public static ScheduledExecutorService newScheduledThreadPool(
            int corePoolSize, ThreadFactory threadFactory, String poolName) {
        return new RulerScheduledThreadPoolExecutor(corePoolSize, threadFactory, poolName);
    }

    //<editor-fold desc="暂时无需监控，直接委托给 Executors 的一系列方法">

    /**
     * Creates a thread pool that maintains enough threads to support
     * the given parallelism level, and may use multiple queues to
     * reduce contention. The parallelism level corresponds to the
     * maximum number of threads actively engaged in, or available to
     * engage in, task processing. The actual number of threads may
     * grow and shrink dynamically. A work-stealing pool makes no
     * guarantees about the order in which submitted tasks are
     * executed.
     *
     * @param parallelism the targeted parallelism level
     * @return the newly created thread pool
     * @throws IllegalArgumentException if {@code parallelism <= 0}
     * @since 1.8
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public static ExecutorService newWorkStealingPool(int parallelism) {
        // 暂时无需监控，直接委托给 Executors
        return Executors.newWorkStealingPool(parallelism);
    }

    /**
     * Creates a work-stealing thread pool using the number of
     * {@linkplain Runtime#availableProcessors available processors}
     * as its target parallelism level.
     *
     * @return the newly created thread pool
     * @see #newWorkStealingPool(int)
     * @since 1.8
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public static ExecutorService newWorkStealingPool() {
        // 暂时无需监控，直接委托给 Executors
        return Executors.newWorkStealingPool();
    }

    /**
     * Returns an object that delegates all defined {@link
     * ExecutorService} methods to the given executor, but not any
     * other methods that might otherwise be accessible using
     * casts. This provides a way to safely "freeze" configuration and
     * disallow tuning of a given concrete implementation.
     *
     * @param executor the underlying implementation
     * @return an {@code ExecutorService} instance
     * @throws NullPointerException if executor null
     */
    public static ExecutorService unconfigurableExecutorService(ExecutorService executor) {
        // 暂时无需监控，直接委托给 Executors
        return Executors.unconfigurableExecutorService(executor);
    }

    /**
     * Returns an object that delegates all defined {@link
     * ScheduledExecutorService} methods to the given executor, but
     * not any other methods that might otherwise be accessible using
     * casts. This provides a way to safely "freeze" configuration and
     * disallow tuning of a given concrete implementation.
     *
     * @param executor the underlying implementation
     * @return a {@code ScheduledExecutorService} instance
     * @throws NullPointerException if executor null
     */
    public static ScheduledExecutorService unconfigurableScheduledExecutorService(ScheduledExecutorService executor) {
        // 暂时无需监控，直接委托给 Executors
        return Executors.unconfigurableScheduledExecutorService(executor);
    }

    // Android-changed: Removed references to SecurityManager from javadoc.

    /**
     * Returns a default thread factory used to create new threads.
     * This factory creates all new threads used by an Executor in the
     * same {@link ThreadGroup}. Each new
     * thread is created as a non-daemon thread with priority set to
     * the smaller of {@code Thread.NORM_PRIORITY} and the maximum
     * priority permitted in the thread group.  New threads have names
     * accessible via {@link Thread#getName} of
     * <em>pool-N-thread-M</em>, where <em>N</em> is the sequence
     * number of this factory, and <em>M</em> is the sequence number
     * of the thread created by this factory.
     *
     * @return a thread factory
     */
    public static ThreadFactory defaultThreadFactory() {
        // 避免嵌套使用 RulerThreadFactory，这里直接委托给 Executors
        return Executors.defaultThreadFactory();
    }

    // Android-changed: Dropped documentation for legacy security code.

    /**
     * Legacy security code; do not use.
     */
    public static ThreadFactory privilegedThreadFactory() {
        // 暂时无需监控，直接委托给 Executors
        return Executors.privilegedThreadFactory();
    }

    /**
     * Returns a {@link Callable} object that, when
     * called, runs the given task and returns the given result.  This
     * can be useful when applying methods requiring a
     * {@code Callable} to an otherwise resultless action.
     *
     * @param task   the task to run
     * @param result the result to return
     * @param <T>    the type of the result
     * @return a callable object
     * @throws NullPointerException if task null
     */
    public static <T> Callable<T> callable(Runnable task, T result) {
        // 暂时无需监控，直接委托给 Executors
        return Executors.callable(task, result);
    }

    /**
     * Returns a {@link Callable} object that, when
     * called, runs the given task and returns {@code null}.
     *
     * @param task the task to run
     * @return a callable object
     * @throws NullPointerException if task null
     */
    public static Callable<Object> callable(Runnable task) {
        // 暂时无需监控，直接委托给 Executors
        return Executors.callable(task);
    }

    /**
     * Returns a {@link Callable} object that, when
     * called, runs the given privileged action and returns its result.
     *
     * @param action the privileged action to run
     * @return a callable object
     * @throws NullPointerException if action null
     */
    public static Callable<Object> callable(final PrivilegedAction<?> action) {
        // 暂时无需监控，直接委托给 Executors
        return Executors.callable(action);
    }

    /**
     * Returns a {@link Callable} object that, when
     * called, runs the given privileged exception action and returns
     * its result.
     *
     * @param action the privileged exception action to run
     * @return a callable object
     * @throws NullPointerException if action null
     */
    public static Callable<Object> callable(final PrivilegedExceptionAction<?> action) {
        // 暂时无需监控，直接委托给 Executors
        return Executors.callable(action);
    }

    // Android-changed: Dropped documentation for legacy security code.

    /**
     * Legacy security code; do not use.
     */
    public static <T> Callable<T> privilegedCallable(Callable<T> callable) {
        // 暂时无需监控，直接委托给 Executors
        return Executors.privilegedCallable(callable);
    }

    // Android-changed: Dropped documentation for legacy security code.

    /**
     * Legacy security code; do not use.
     */
    public static <T> Callable<T> privilegedCallableUsingCurrentClassLoader(Callable<T> callable) {
        // 暂时无需监控，直接委托给 Executors
        return Executors.privilegedCallableUsingCurrentClassLoader(callable);
    }

    //</editor-fold>

    private RulerExecutors() {

    }

}
