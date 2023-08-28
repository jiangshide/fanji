package com.fanji.android.thread.ruler;

import android.text.TextUtils;

import com.fanji.android.thread.RulerCounter;
import com.fanji.android.thread.RulerLifecycleManager;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 用于替换 new Thread 的类，区别是增加 线程名的 prefix，以及添加生命周期回调
 *  * > Ruler SDK 内部的 Factory 创建的也是 RuleThread
 *
 * @author 蒋世德 @ fanji inc
 * @email jiangshide@fanji.com
 * @since 21-6-1 下午2:32
 */
public class RulerThread extends Thread {

    // Thread 的每个构造函数末尾添加 String 类型参数，用于传递创建线程的类名

    public RulerThread(String namePrefix) {
        super(null, null, fixName(namePrefix), 0);
        onInit();
    }

    public RulerThread(Runnable target, String namePrefix) {
        super(null, target, fixName(namePrefix), 0);
        onInit();
    }

    public RulerThread(Runnable target, String name, String namePrefix) {
        super(null, target, fixName(namePrefix, name), 0);
        onInit();
    }

    public RulerThread(String name, String namePrefix) {
        super(null, null, fixName(namePrefix, name), 0);
        onInit();
    }

    public RulerThread(ThreadGroup group, Runnable target, String namePrefix) {
        super(group, target, fixName(namePrefix), 0);
        onInit();
    }

    public RulerThread(ThreadGroup group, Runnable target, String name, String namePrefix) {
        super(group, target, fixName(namePrefix, name), 0);
        onInit();
    }

    public RulerThread(ThreadGroup group, Runnable target, String name, long stackSize, String namePrefix) {
        super(group, target, fixName(namePrefix, name), stackSize);
        onInit();
    }

    public RulerThread(ThreadGroup group, String name, String namePrefix) {
        super(group, null, fixName(namePrefix, name), 0);
        onInit();
    }

    private void onInit() {
        RulerLifecycleManager.INSTANCE.onInvokeConstructorInternal(getThreadGroup(), getName());
    }

    private static AtomicInteger threadNumber = new AtomicInteger(0);

    private static String fixName(String namePrefix) {
        return fixName(namePrefix, defaultName());
    }

    private static String fixName(String namePrefix, String name) {
        if (TextUtils.isEmpty(name)) {
            name = defaultName();
        }
        if (TextUtils.isEmpty(namePrefix)) {
            return name;
        } else {
            return namePrefix + "-" + name;
        }
    }

    private static String defaultName() {
        return "Ruler-" + threadNumber.incrementAndGet();
    }

    // 单独创建的 Thread 的计数器统一归类为 RulerThread
    private AtomicInteger threadCounter = RulerCounter.INSTANCE.getCounter("RulerThread");

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
