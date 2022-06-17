package com.motorola.systemui.cli.navgesture.animation.remote;

import android.os.SystemClock;
import android.util.Log;
import android.view.InputEvent;
import android.view.KeyEvent;
import android.view.MotionEvent;
import com.android.systemui.shared.recents.model.ThumbnailData;
import com.android.systemui.shared.system.InputConsumerController;
import com.android.systemui.shared.system.RecentsAnimationControllerCompat;
import com.android.systemui.shared.system.RemoteAnimationTargetCompat;
import com.motorola.systemui.cli.navgesture.executors.AppExecutors;
import com.motorola.systemui.cli.navgesture.executors.LooperExecutor;
import com.motorola.systemui.cli.navgesture.inputconsumers.InputConsumer;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class RecentsAnimationTargetSetController {
    private final boolean mAllowMinimizeSplitScreen;
    private final RecentsAnimationControllerCompat mController;
    private boolean mDisableInputProxyPending;
    private InputConsumer mInputConsumer;
    private InputConsumerController mInputConsumerController;
    private Supplier<InputConsumer> mInputProxySupplier;
    private final Consumer<RecentsAnimationTargetSetController> mOnFinishedListener;
    private boolean mSplitScreenMinimized = false;
    private boolean mTouchInProgress;
    private boolean mUseLauncherSysBarFlags = false;

    public RecentsAnimationTargetSetController(RecentsAnimationControllerCompat recentsAnimationControllerCompat, boolean z, Consumer<RecentsAnimationTargetSetController> consumer) {
        this.mController = recentsAnimationControllerCompat;
        this.mOnFinishedListener = consumer;
        this.mAllowMinimizeSplitScreen = z;
    }

    public ThumbnailData screenshotTask(int i) {
        return this.mController.screenshotTask(i);
    }

    public void setUseLauncherSystemBarFlags(boolean z) {
        if (this.mUseLauncherSysBarFlags != z) {
            this.mUseLauncherSysBarFlags = z;
            AppExecutors.background().execute(new RecentsAnimationTargetSetController$$ExternalSyntheticLambda2(this, z));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$setUseLauncherSystemBarFlags$0(boolean z) {
        this.mController.setAnimationTargetsBehindSystemBars(!z);
    }

    public void setDeferCancelUntilNextTransition(boolean z, boolean z2) {
        this.mController.setDeferCancelUntilNextTransition(z, z2);
    }

    public void cleanupScreenshot() {
        LooperExecutor background = AppExecutors.background();
        RecentsAnimationControllerCompat recentsAnimationControllerCompat = this.mController;
        Objects.requireNonNull(recentsAnimationControllerCompat);
        background.execute(new RecentsAnimationTargetSetController$$ExternalSyntheticLambda1(recentsAnimationControllerCompat));
    }

    public boolean removeTaskTarget(RemoteAnimationTargetCompat remoteAnimationTargetCompat) {
        return this.mController.removeTask(remoteAnimationTargetCompat.taskId);
    }

    public void finishAnimationToHome() {
        finishAndDisableInputProxy(true, (Runnable) null, false);
    }

    public void finishAnimationToApp() {
        finishAndDisableInputProxy(false, (Runnable) null, false);
    }

    public void finish(boolean z, Runnable runnable) {
        finish(z, runnable, false);
    }

    public void finish(boolean z, Runnable runnable, boolean z2) {
        if (!z || !this.mTouchInProgress) {
            finishAndDisableInputProxy(z, runnable, z2);
            return;
        }
        this.mDisableInputProxyPending = true;
        finishController(z, runnable, z2);
    }

    private void finishAndDisableInputProxy(boolean z, Runnable runnable, boolean z2) {
        disableInputProxy();
        finishController(z, runnable, z2);
    }

    public void finishController(boolean z, Runnable runnable, boolean z2) {
        this.mOnFinishedListener.accept(this);
        AppExecutors.background().execute(new RecentsAnimationTargetSetController$$ExternalSyntheticLambda3(this, z, z2, runnable));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$finishController$1(boolean z, boolean z2, Runnable runnable) {
        this.mController.setInputConsumerEnabled(false);
        this.mController.finish(z, z2);
        if (runnable != null) {
            AppExecutors.m97ui().execute(runnable);
        }
    }

    public void enableInputConsumer() {
        AppExecutors.background().submit(new RecentsAnimationTargetSetController$$ExternalSyntheticLambda4(this));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ Boolean lambda$enableInputConsumer$2() throws Exception {
        this.mController.hideCurrentInputMethod();
        this.mController.setInputConsumerEnabled(true);
        return Boolean.TRUE;
    }

    public void enableInputProxy(InputConsumerController inputConsumerController, Supplier<InputConsumer> supplier) {
        this.mInputProxySupplier = supplier;
        this.mInputConsumerController = inputConsumerController;
        inputConsumerController.setInputListener(new RecentsAnimationTargetSetController$$ExternalSyntheticLambda0(this));
    }

    private void disableInputProxy() {
        if (this.mInputConsumer != null && this.mTouchInProgress) {
            long uptimeMillis = SystemClock.uptimeMillis();
            MotionEvent obtain = MotionEvent.obtain(uptimeMillis, uptimeMillis, 3, 0.0f, 0.0f, 0);
            this.mInputConsumer.onMotionEvent(obtain);
            obtain.recycle();
        }
        InputConsumerController inputConsumerController = this.mInputConsumerController;
        if (inputConsumerController != null) {
            inputConsumerController.setInputListener((InputConsumerController.InputListener) null);
        }
        this.mInputProxySupplier = null;
    }

    /* access modifiers changed from: private */
    public boolean onInputConsumerEvent(InputEvent inputEvent) {
        if (inputEvent instanceof MotionEvent) {
            onInputConsumerMotionEvent((MotionEvent) inputEvent);
            return false;
        } else if (!(inputEvent instanceof KeyEvent)) {
            return false;
        } else {
            if (this.mInputConsumer == null) {
                this.mInputConsumer = this.mInputProxySupplier.get();
            }
            this.mInputConsumer.onKeyEvent((KeyEvent) inputEvent);
            return true;
        }
    }

    private boolean onInputConsumerMotionEvent(MotionEvent motionEvent) {
        int action = motionEvent.getAction();
        boolean z = this.mTouchInProgress;
        if (!z && action != 0) {
            Log.w("RecentsAnimationController", "Received non-down motion before down motion: " + action);
            return false;
        } else if (!z || action != 0) {
            if (action == 0) {
                this.mTouchInProgress = true;
                if (this.mInputConsumer == null) {
                    this.mInputConsumer = this.mInputProxySupplier.get();
                }
            } else if (action == 3 || action == 1) {
                this.mTouchInProgress = false;
                if (this.mDisableInputProxyPending) {
                    this.mDisableInputProxyPending = false;
                    disableInputProxy();
                }
            }
            InputConsumer inputConsumer = this.mInputConsumer;
            if (inputConsumer != null) {
                inputConsumer.onMotionEvent(motionEvent);
            }
            return true;
        } else {
            Log.w("RecentsAnimationController", "Received down motion while touch was already in progress");
            return false;
        }
    }
}
