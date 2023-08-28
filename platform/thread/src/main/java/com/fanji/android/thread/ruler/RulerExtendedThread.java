package com.fanji.android.thread.ruler;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.fanji.android.thread.RulerCounter;
import com.fanji.android.thread.RulerLifecycleManager;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author 蒋世德 @ fanji inc
 * @email jiangshide@fanji.com
 * @since 21-6-1 下午2:26
 */
public abstract class RulerExtendedThread extends Thread{
    public RulerExtendedThread() {
        super(null, null, createName(), 0);
        onInit();
    }

    public RulerExtendedThread(@Nullable Runnable target) {
        super(null, target, createName(), 0);
        onInit();
    }

    public RulerExtendedThread(@Nullable ThreadGroup group, @Nullable Runnable target) {
        super(group, target, createName(), 0);
        onInit();
    }

    public RulerExtendedThread(@NonNull String name) {
        super(null, null, name, 0);
        onInit();
    }

    public RulerExtendedThread(@Nullable ThreadGroup group, @NonNull String name) {
        super(group, null, name, 0);
        onInit();
    }

    public RulerExtendedThread(@Nullable Runnable target, @NonNull String name) {
        super(null, target, name, 0);
        onInit();
    }

    public RulerExtendedThread(@Nullable ThreadGroup group, @Nullable Runnable target, @NonNull String name) {
        super(group, target, name, 0);
        onInit();
    }

    public RulerExtendedThread(@Nullable ThreadGroup group, @Nullable Runnable target, @NonNull String name, long stackSize) {
        super(group, target, name, stackSize);
        onInit();
    }

    public abstract String getNamePrefix();

    private static AtomicInteger threadNumber = new AtomicInteger(0);

    private static String createName() {
        return "RulerEx-" + threadNumber.incrementAndGet();
    }

    // 单独创建的 Thread 的计数器统一归类为 RulerThread
    private AtomicInteger threadCounter = RulerCounter.INSTANCE.getCounter("RulerExtendedThread");

    private void onInit() {
        setName(getNamePrefix() + "-" + getName());
        RulerLifecycleManager.INSTANCE.onInvokeConstructorInternal(getThreadGroup(), getName());
    }

    @Override
    public synchronized void start() {
        short result = RulerLifecycleManager.INSTANCE.onInvokeStartInternal(this);
        if (result != RulerLifecycleManager.THREAD_START_MAGIC) {
            // 当回调的结果是隐藏魔法时，不去执行 Thread.start()！
            // 目的是减少线程数量，但该方案存在风险，谨慎使用
            super.start();
        }
    }

    @Override
    public void run() {
        threadCounter.incrementAndGet();
        RulerLifecycleManager.INSTANCE.onThreadBeginInternal(this);
        try {
            super.run();
        } finally {
            threadCounter.decrementAndGet();
            RulerLifecycleManager.INSTANCE.onThreadEndInternal(this);
        }
    }

}
