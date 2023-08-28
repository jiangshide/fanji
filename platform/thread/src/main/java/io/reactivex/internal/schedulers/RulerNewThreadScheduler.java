package io.reactivex.internal.schedulers;

import com.fanji.android.thread.pool.RulerThreadFactory;

import java.util.concurrent.ThreadFactory;

import io.reactivex.Scheduler;
import io.reactivex.annotations.NonNull;

/**
 * <b>RxJava NewThreadScheduler 的替换类，增加监控相关功能，其他逻辑保持不变</b>
 * Schedules work on a new thread.
 *
 * @author 蒋世德 @ fanji inc
 * @email jiangshide@fanji.com
 * @since 21-6-1 下午3:12
 */
public final class RulerNewThreadScheduler extends Scheduler {

    private final ThreadFactory threadFactory;

    private static final String THREAD_NAME_PREFIX = "ZH_RxNewThreadScheduler";
    private static final RxThreadFactory THREAD_FACTORY;

    /**
     * The name of the system property for setting the thread priority for this Scheduler.
     */
    private static final String KEY_NEWTHREAD_PRIORITY = "rx2.newthread-priority";

    static {
        int priority = Math.max(Thread.MIN_PRIORITY, Math.min(Thread.MAX_PRIORITY,
                SafeInteger.getInteger(KEY_NEWTHREAD_PRIORITY, Thread.NORM_PRIORITY)));

        THREAD_FACTORY = new RxThreadFactory(THREAD_NAME_PREFIX, priority);
    }

    public RulerNewThreadScheduler() {
        this(THREAD_FACTORY);
    }

    public RulerNewThreadScheduler(ThreadFactory threadFactory) {
        this.threadFactory = new RulerThreadFactory("RulerNewThreadScheduler", threadFactory);
    }

    @NonNull
    @Override
    public Worker createWorker() {
        return new NewThreadWorker(threadFactory);
    }
}
