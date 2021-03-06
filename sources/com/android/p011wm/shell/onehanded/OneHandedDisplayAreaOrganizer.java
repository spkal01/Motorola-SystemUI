package com.android.p011wm.shell.onehanded;

import android.content.Context;
import android.graphics.Rect;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.util.ArrayMap;
import android.view.SurfaceControl;
import android.window.DisplayAreaAppearedInfo;
import android.window.DisplayAreaInfo;
import android.window.DisplayAreaOrganizer;
import android.window.WindowContainerToken;
import android.window.WindowContainerTransaction;
import com.android.p011wm.shell.C2219R;
import com.android.p011wm.shell.common.DisplayLayout;
import com.android.p011wm.shell.common.ShellExecutor;
import com.android.p011wm.shell.onehanded.OneHandedAnimationController;
import com.android.p011wm.shell.onehanded.OneHandedSurfaceTransactionHelper;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/* renamed from: com.android.wm.shell.onehanded.OneHandedDisplayAreaOrganizer */
public class OneHandedDisplayAreaOrganizer extends DisplayAreaOrganizer {
    /* access modifiers changed from: private */
    public OneHandedAnimationController mAnimationController;
    private OneHandedBackgroundPanelOrganizer mBackgroundPanelOrganizer;
    private final Rect mDefaultDisplayBounds = new Rect();
    private ArrayMap<WindowContainerToken, SurfaceControl> mDisplayAreaTokenMap = new ArrayMap<>();
    private DisplayLayout mDisplayLayout = new DisplayLayout();
    private int mEnterExitAnimationDurationMs;
    private boolean mIsReady;
    private final Rect mLastVisualDisplayBounds = new Rect();
    private float mLastVisualOffset = 0.0f;
    OneHandedAnimationCallback mOneHandedAnimationCallback = new OneHandedAnimationCallback() {
        public void onOneHandedAnimationStart(OneHandedAnimationController.OneHandedTransitionAnimator oneHandedTransitionAnimator) {
            boolean z = oneHandedTransitionAnimator.getTransitionDirection() == 1;
            if (!OneHandedDisplayAreaOrganizer.this.mTransitionCallbacks.isEmpty()) {
                for (int size = OneHandedDisplayAreaOrganizer.this.mTransitionCallbacks.size() - 1; size >= 0; size--) {
                    ((OneHandedTransitionCallback) OneHandedDisplayAreaOrganizer.this.mTransitionCallbacks.get(size)).onStartTransition(z);
                }
            }
        }

        public void onOneHandedAnimationEnd(SurfaceControl.Transaction transaction, OneHandedAnimationController.OneHandedTransitionAnimator oneHandedTransitionAnimator) {
            OneHandedDisplayAreaOrganizer.this.mAnimationController.removeAnimator(oneHandedTransitionAnimator.getToken());
            if (OneHandedDisplayAreaOrganizer.this.mAnimationController.isAnimatorsConsumed()) {
                OneHandedDisplayAreaOrganizer.this.finishOffset((int) oneHandedTransitionAnimator.getDestinationOffset(), oneHandedTransitionAnimator.getTransitionDirection());
            }
        }

        public void onOneHandedAnimationCancel(OneHandedAnimationController.OneHandedTransitionAnimator oneHandedTransitionAnimator) {
            OneHandedDisplayAreaOrganizer.this.mAnimationController.removeAnimator(oneHandedTransitionAnimator.getToken());
            if (OneHandedDisplayAreaOrganizer.this.mAnimationController.isAnimatorsConsumed()) {
                OneHandedDisplayAreaOrganizer.this.finishOffset((int) oneHandedTransitionAnimator.getDestinationOffset(), oneHandedTransitionAnimator.getTransitionDirection());
            }
        }
    };
    private final OneHandedSettingsUtil mOneHandedSettingsUtil;
    private OneHandedSurfaceTransactionHelper.SurfaceControlTransactionFactory mSurfaceControlTransactionFactory;
    /* access modifiers changed from: private */
    public List<OneHandedTransitionCallback> mTransitionCallbacks = new ArrayList();
    private OneHandedTutorialHandler mTutorialHandler;

    public OneHandedDisplayAreaOrganizer(Context context, DisplayLayout displayLayout, OneHandedSettingsUtil oneHandedSettingsUtil, OneHandedAnimationController oneHandedAnimationController, OneHandedTutorialHandler oneHandedTutorialHandler, OneHandedBackgroundPanelOrganizer oneHandedBackgroundPanelOrganizer, ShellExecutor shellExecutor) {
        super(shellExecutor);
        this.mDisplayLayout.set(displayLayout);
        this.mOneHandedSettingsUtil = oneHandedSettingsUtil;
        updateDisplayBounds();
        this.mAnimationController = oneHandedAnimationController;
        this.mEnterExitAnimationDurationMs = SystemProperties.getInt("persist.debug.one_handed_translate_animation_duration", context.getResources().getInteger(C2219R.integer.config_one_handed_translate_animation_duration));
        this.mSurfaceControlTransactionFactory = C2333x55a9da47.INSTANCE;
        this.mBackgroundPanelOrganizer = oneHandedBackgroundPanelOrganizer;
        this.mTutorialHandler = oneHandedTutorialHandler;
    }

    public void onDisplayAreaAppeared(DisplayAreaInfo displayAreaInfo, SurfaceControl surfaceControl) {
        this.mDisplayAreaTokenMap.put(displayAreaInfo.token, surfaceControl);
    }

    public void onDisplayAreaVanished(DisplayAreaInfo displayAreaInfo) {
        this.mDisplayAreaTokenMap.remove(displayAreaInfo.token);
    }

    public List<DisplayAreaAppearedInfo> registerOrganizer(int i) {
        List<DisplayAreaAppearedInfo> registerOrganizer = OneHandedDisplayAreaOrganizer.super.registerOrganizer(i);
        for (int i2 = 0; i2 < registerOrganizer.size(); i2++) {
            DisplayAreaAppearedInfo displayAreaAppearedInfo = registerOrganizer.get(i2);
            onDisplayAreaAppeared(displayAreaAppearedInfo.getDisplayAreaInfo(), displayAreaAppearedInfo.getLeash());
        }
        this.mIsReady = true;
        updateDisplayBounds();
        return registerOrganizer;
    }

    public void unregisterOrganizer() {
        OneHandedDisplayAreaOrganizer.super.unregisterOrganizer();
        this.mIsReady = false;
        resetWindowsOffset();
    }

    /* access modifiers changed from: package-private */
    public boolean isReady() {
        return this.mIsReady;
    }

    public void onRotateDisplay(Context context, int i, WindowContainerTransaction windowContainerTransaction) {
        if (this.mDisplayLayout.rotation() != i && this.mOneHandedSettingsUtil.getSettingsOneHandedModeEnabled(context.getContentResolver(), UserHandle.myUserId())) {
            this.mDisplayLayout.rotateTo(context.getResources(), i);
            updateDisplayBounds();
            if (!this.mOneHandedSettingsUtil.getSettingsSwipeToNotificationEnabled(context.getContentResolver(), UserHandle.myUserId())) {
                finishOffset(0, 2);
            }
        }
    }

    public void scheduleOffset(int i, int i2) {
        this.mDisplayAreaTokenMap.forEach(new OneHandedDisplayAreaOrganizer$$ExternalSyntheticLambda0(this, this.mLastVisualOffset, i2, i2 > 0 ? 1 : 2));
        this.mLastVisualOffset = (float) i2;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$scheduleOffset$0(float f, int i, int i2, WindowContainerToken windowContainerToken, SurfaceControl surfaceControl) {
        animateWindows(windowContainerToken, surfaceControl, f, (float) i, i2, this.mEnterExitAnimationDurationMs);
    }

    /* access modifiers changed from: package-private */
    public void resetWindowsOffset() {
        SurfaceControl.Transaction transaction = this.mSurfaceControlTransactionFactory.getTransaction();
        this.mDisplayAreaTokenMap.forEach(new OneHandedDisplayAreaOrganizer$$ExternalSyntheticLambda1(this, transaction));
        transaction.apply();
        this.mLastVisualOffset = 0.0f;
        this.mLastVisualDisplayBounds.offsetTo(0, 0);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$resetWindowsOffset$1(SurfaceControl.Transaction transaction, WindowContainerToken windowContainerToken, SurfaceControl surfaceControl) {
        OneHandedAnimationController.OneHandedTransitionAnimator remove = this.mAnimationController.getAnimatorMap().remove(windowContainerToken);
        if (remove != null && remove.isRunning()) {
            remove.cancel();
        }
        transaction.setPosition(surfaceControl, 0.0f, 0.0f).setWindowCrop(surfaceControl, -1, -1).setCornerRadius(surfaceControl, -1.0f);
    }

    private void animateWindows(WindowContainerToken windowContainerToken, SurfaceControl surfaceControl, float f, float f2, int i, int i2) {
        OneHandedAnimationController.OneHandedTransitionAnimator animator = this.mAnimationController.getAnimator(windowContainerToken, surfaceControl, f, f2, this.mLastVisualDisplayBounds);
        if (animator != null) {
            animator.setTransitionDirection(i).addOneHandedAnimationCallback(this.mOneHandedAnimationCallback).addOneHandedAnimationCallback(this.mTutorialHandler).addOneHandedAnimationCallback(this.mBackgroundPanelOrganizer).setDuration((long) i2).start();
        }
    }

    /* access modifiers changed from: package-private */
    public void finishOffset(int i, int i2) {
        if (i2 == 2) {
            resetWindowsOffset();
        }
        float f = i2 == 1 ? (float) i : 0.0f;
        this.mLastVisualOffset = f;
        this.mLastVisualDisplayBounds.offsetTo(0, Math.round(f));
        for (int size = this.mTransitionCallbacks.size() - 1; size >= 0; size--) {
            OneHandedTransitionCallback oneHandedTransitionCallback = this.mTransitionCallbacks.get(size);
            if (i2 == 1) {
                oneHandedTransitionCallback.onStartFinished(getLastVisualDisplayBounds());
            } else {
                oneHandedTransitionCallback.onStopFinished(getLastVisualDisplayBounds());
            }
        }
    }

    private Rect getLastVisualDisplayBounds() {
        return this.mLastVisualDisplayBounds;
    }

    /* access modifiers changed from: package-private */
    public Rect getLastDisplayBounds() {
        return this.mLastVisualDisplayBounds;
    }

    public DisplayLayout getDisplayLayout() {
        return this.mDisplayLayout;
    }

    /* access modifiers changed from: package-private */
    public void setDisplayLayout(DisplayLayout displayLayout) {
        this.mDisplayLayout.set(displayLayout);
    }

    /* access modifiers changed from: package-private */
    public ArrayMap<WindowContainerToken, SurfaceControl> getDisplayAreaTokenMap() {
        return this.mDisplayAreaTokenMap;
    }

    /* access modifiers changed from: package-private */
    public void updateDisplayBounds() {
        this.mDefaultDisplayBounds.set(0, 0, this.mDisplayLayout.width(), this.mDisplayLayout.height());
        this.mLastVisualDisplayBounds.set(this.mDefaultDisplayBounds);
    }

    public void registerTransitionCallback(OneHandedTransitionCallback oneHandedTransitionCallback) {
        this.mTransitionCallbacks.add(oneHandedTransitionCallback);
    }

    /* access modifiers changed from: package-private */
    public void dump(PrintWriter printWriter) {
        printWriter.println("OneHandedDisplayAreaOrganizer");
        printWriter.print("  mDisplayLayout.rotation()=");
        printWriter.println(this.mDisplayLayout.rotation());
        printWriter.print("  mDisplayAreaTokenMap=");
        printWriter.println(this.mDisplayAreaTokenMap);
        printWriter.print("  mDefaultDisplayBounds=");
        printWriter.println(this.mDefaultDisplayBounds);
        printWriter.print("  mIsReady=");
        printWriter.println(this.mIsReady);
        printWriter.print("  mLastVisualDisplayBounds=");
        printWriter.println(this.mLastVisualDisplayBounds);
        printWriter.print("  mLastVisualOffset=");
        printWriter.println(this.mLastVisualOffset);
        OneHandedAnimationController oneHandedAnimationController = this.mAnimationController;
        if (oneHandedAnimationController != null) {
            oneHandedAnimationController.dump(printWriter);
        }
    }
}
