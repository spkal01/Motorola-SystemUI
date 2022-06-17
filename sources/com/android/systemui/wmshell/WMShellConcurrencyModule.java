package com.android.systemui.wmshell;

import android.animation.AnimationHandler;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import com.android.p011wm.shell.common.HandlerExecutor;
import com.android.p011wm.shell.common.ShellExecutor;
import com.android.systemui.R$bool;

public abstract class WMShellConcurrencyModule {
    private static boolean enableShellMainThread(Context context) {
        return context.getResources().getBoolean(R$bool.config_enableShellMainThread);
    }

    public static ShellExecutor provideSysUIMainExecutor(Handler handler) {
        return new HandlerExecutor(handler);
    }

    public static Handler provideShellMainHandler(Context context, Handler handler) {
        if (!enableShellMainThread(context)) {
            return handler;
        }
        HandlerThread handlerThread = new HandlerThread("wmshell.main", -4);
        handlerThread.start();
        if (Build.IS_DEBUGGABLE) {
            handlerThread.getLooper().setTraceTag(32);
            handlerThread.getLooper().setSlowLogThresholdMs(30, 30);
        }
        return Handler.createAsync(handlerThread.getLooper());
    }

    public static ShellExecutor provideShellMainExecutor(Context context, Handler handler, ShellExecutor shellExecutor) {
        return enableShellMainThread(context) ? new HandlerExecutor(handler) : shellExecutor;
    }

    public static ShellExecutor provideShellAnimationExecutor() {
        HandlerThread handlerThread = new HandlerThread("wmshell.anim", -4);
        handlerThread.start();
        if (Build.IS_DEBUGGABLE) {
            handlerThread.getLooper().setTraceTag(32);
            handlerThread.getLooper().setSlowLogThresholdMs(30, 30);
        }
        return new HandlerExecutor(Handler.createAsync(handlerThread.getLooper()));
    }

    public static ShellExecutor provideSplashScreenExecutor() {
        HandlerThread handlerThread = new HandlerThread("wmshell.splashscreen", -10);
        handlerThread.start();
        return new HandlerExecutor(handlerThread.getThreadHandler());
    }

    public static AnimationHandler provideShellMainExecutorSfVsyncAnimationHandler(ShellExecutor shellExecutor) {
        try {
            AnimationHandler animationHandler = new AnimationHandler();
            shellExecutor.executeBlocking(new WMShellConcurrencyModule$$ExternalSyntheticLambda0(animationHandler));
            return animationHandler;
        } catch (InterruptedException e) {
            throw new RuntimeException("Failed to initialize SfVsync animation handler in 1s", e);
        }
    }
}
