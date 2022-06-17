package com.android.p011wm.shell.legacysplitscreen;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.ActivityManager;
import android.graphics.Rect;
import android.os.IBinder;
import android.view.SurfaceControl;
import android.window.TransitionInfo;
import android.window.TransitionRequestInfo;
import android.window.WindowContainerTransaction;
import android.window.WindowContainerTransactionCallback;
import com.android.p011wm.shell.common.TransactionPool;
import com.android.p011wm.shell.transition.Transitions;
import java.util.ArrayList;

/* renamed from: com.android.wm.shell.legacysplitscreen.LegacySplitScreenTransitions */
public class LegacySplitScreenTransitions implements Transitions.TransitionHandler {
    private IBinder mAnimatingTransition = null;
    private final ArrayList<Animator> mAnimations = new ArrayList<>();
    private boolean mDismissFromSnap = false;
    private Transitions.TransitionFinishCallback mFinishCallback = null;
    private SurfaceControl.Transaction mFinishTransaction;
    private final LegacySplitScreenTaskListener mListener;
    private IBinder mPendingDismiss = null;
    private IBinder mPendingEnter = null;
    private final LegacySplitScreenController mSplitScreen;
    private final TransactionPool mTransactionPool;
    private final Transitions mTransitions;

    LegacySplitScreenTransitions(TransactionPool transactionPool, Transitions transitions, LegacySplitScreenController legacySplitScreenController, LegacySplitScreenTaskListener legacySplitScreenTaskListener) {
        this.mTransactionPool = transactionPool;
        this.mTransitions = transitions;
        this.mSplitScreen = legacySplitScreenController;
        this.mListener = legacySplitScreenTaskListener;
    }

    public WindowContainerTransaction handleRequest(IBinder iBinder, TransitionRequestInfo transitionRequestInfo) {
        ActivityManager.RunningTaskInfo triggerTask = transitionRequestInfo.getTriggerTask();
        int type = transitionRequestInfo.getType();
        if (this.mSplitScreen.isDividerVisible()) {
            WindowContainerTransaction windowContainerTransaction = new WindowContainerTransaction();
            if (triggerTask == null) {
                return windowContainerTransaction;
            }
            if (!(((type == 2 || type == 4) && triggerTask.parentTaskId == this.mListener.mPrimary.taskId) || ((type == 1 || type == 3) && !triggerTask.supportsMultiWindow))) {
                return windowContainerTransaction;
            }
            WindowManagerProxy.buildDismissSplit(windowContainerTransaction, this.mListener, this.mSplitScreen.getSplitLayout(), true);
            if (type == 1 || type == 3) {
                windowContainerTransaction.reorder(triggerTask.token, true);
            }
            this.mPendingDismiss = iBinder;
            return windowContainerTransaction;
        } else if (triggerTask == null || ((type != 1 && type != 3) || triggerTask.configuration.windowConfiguration.getWindowingMode() != 3)) {
            return null;
        } else {
            WindowContainerTransaction windowContainerTransaction2 = new WindowContainerTransaction();
            this.mSplitScreen.prepareEnterSplitTransition(windowContainerTransaction2);
            this.mPendingEnter = iBinder;
            return windowContainerTransaction2;
        }
    }

    private void startExampleAnimation(SurfaceControl surfaceControl, boolean z) {
        float f = z ? 1.0f : 0.0f;
        float f2 = 1.0f - f;
        SurfaceControl.Transaction acquire = this.mTransactionPool.acquire();
        ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{f2, f});
        ofFloat.setDuration(500);
        ofFloat.addUpdateListener(new LegacySplitScreenTransitions$$ExternalSyntheticLambda0(acquire, surfaceControl, f2, f));
        final LegacySplitScreenTransitions$$ExternalSyntheticLambda5 legacySplitScreenTransitions$$ExternalSyntheticLambda5 = new LegacySplitScreenTransitions$$ExternalSyntheticLambda5(this, acquire, surfaceControl, f, ofFloat);
        ofFloat.addListener(new Animator.AnimatorListener() {
            public void onAnimationRepeat(Animator animator) {
            }

            public void onAnimationStart(Animator animator) {
            }

            public void onAnimationEnd(Animator animator) {
                legacySplitScreenTransitions$$ExternalSyntheticLambda5.run();
            }

            public void onAnimationCancel(Animator animator) {
                legacySplitScreenTransitions$$ExternalSyntheticLambda5.run();
            }
        });
        this.mAnimations.add(ofFloat);
        this.mTransitions.getAnimExecutor().execute(new LegacySplitScreenTransitions$$ExternalSyntheticLambda2(ofFloat));
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$startExampleAnimation$0(SurfaceControl.Transaction transaction, SurfaceControl surfaceControl, float f, float f2, ValueAnimator valueAnimator) {
        float animatedFraction = valueAnimator.getAnimatedFraction();
        transaction.setAlpha(surfaceControl, (f * (1.0f - animatedFraction)) + (f2 * animatedFraction));
        transaction.apply();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$startExampleAnimation$2(SurfaceControl.Transaction transaction, SurfaceControl surfaceControl, float f, ValueAnimator valueAnimator) {
        transaction.setAlpha(surfaceControl, f);
        transaction.apply();
        this.mTransactionPool.release(transaction);
        this.mTransitions.getMainExecutor().execute(new LegacySplitScreenTransitions$$ExternalSyntheticLambda4(this, valueAnimator));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$startExampleAnimation$1(ValueAnimator valueAnimator) {
        this.mAnimations.remove(valueAnimator);
        onFinish();
    }

    private void startExampleResizeAnimation(SurfaceControl surfaceControl, Rect rect, Rect rect2) {
        SurfaceControl.Transaction acquire = this.mTransactionPool.acquire();
        ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
        ofFloat.setDuration(500);
        ofFloat.addUpdateListener(new LegacySplitScreenTransitions$$ExternalSyntheticLambda1(acquire, surfaceControl, rect, rect2));
        final LegacySplitScreenTransitions$$ExternalSyntheticLambda6 legacySplitScreenTransitions$$ExternalSyntheticLambda6 = new LegacySplitScreenTransitions$$ExternalSyntheticLambda6(this, acquire, surfaceControl, rect2, ofFloat);
        ofFloat.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animator) {
                legacySplitScreenTransitions$$ExternalSyntheticLambda6.run();
            }

            public void onAnimationCancel(Animator animator) {
                legacySplitScreenTransitions$$ExternalSyntheticLambda6.run();
            }
        });
        this.mAnimations.add(ofFloat);
        this.mTransitions.getAnimExecutor().execute(new LegacySplitScreenTransitions$$ExternalSyntheticLambda2(ofFloat));
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$startExampleResizeAnimation$3(SurfaceControl.Transaction transaction, SurfaceControl surfaceControl, Rect rect, Rect rect2, ValueAnimator valueAnimator) {
        float animatedFraction = valueAnimator.getAnimatedFraction();
        float f = 1.0f - animatedFraction;
        transaction.setWindowCrop(surfaceControl, (int) ((((float) rect.width()) * f) + (((float) rect2.width()) * animatedFraction)), (int) ((((float) rect.height()) * f) + (((float) rect2.height()) * animatedFraction)));
        transaction.setPosition(surfaceControl, (((float) rect.left) * f) + (((float) rect2.left) * animatedFraction), (((float) rect.top) * f) + (((float) rect2.top) * animatedFraction));
        transaction.apply();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$startExampleResizeAnimation$5(SurfaceControl.Transaction transaction, SurfaceControl surfaceControl, Rect rect, ValueAnimator valueAnimator) {
        transaction.setWindowCrop(surfaceControl, 0, 0);
        transaction.setPosition(surfaceControl, (float) rect.left, (float) rect.top);
        transaction.apply();
        this.mTransactionPool.release(transaction);
        this.mTransitions.getMainExecutor().execute(new LegacySplitScreenTransitions$$ExternalSyntheticLambda3(this, valueAnimator));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$startExampleResizeAnimation$4(ValueAnimator valueAnimator) {
        this.mAnimations.remove(valueAnimator);
        onFinish();
    }

    public boolean startAnimation(IBinder iBinder, TransitionInfo transitionInfo, SurfaceControl.Transaction transaction, Transitions.TransitionFinishCallback transitionFinishCallback) {
        IBinder iBinder2 = iBinder;
        SurfaceControl.Transaction transaction2 = transaction;
        boolean z = false;
        if (iBinder2 == this.mPendingDismiss || iBinder2 == this.mPendingEnter) {
            this.mFinishCallback = transitionFinishCallback;
            this.mFinishTransaction = this.mTransactionPool.acquire();
            this.mAnimatingTransition = iBinder2;
            for (int size = transitionInfo.getChanges().size() - 1; size >= 0; size--) {
                TransitionInfo.Change change = (TransitionInfo.Change) transitionInfo.getChanges().get(size);
                SurfaceControl leash = change.getLeash();
                int mode = ((TransitionInfo.Change) transitionInfo.getChanges().get(size)).getMode();
                if (mode == 6) {
                    if (change.getParent() != null) {
                        TransitionInfo.Change change2 = transitionInfo.getChange(change.getParent());
                        transaction2.show(change2.getLeash());
                        transaction2.setAlpha(change2.getLeash(), 1.0f);
                        transaction2.reparent(leash, transitionInfo.getRootLeash());
                        transaction2.setLayer(leash, transitionInfo.getChanges().size() - size);
                        this.mFinishTransaction.reparent(leash, change2.getLeash());
                        this.mFinishTransaction.setPosition(leash, (float) change.getEndRelOffset().x, (float) change.getEndRelOffset().y);
                    } else {
                        TransitionInfo transitionInfo2 = transitionInfo;
                    }
                    Rect rect = new Rect(change.getStartAbsBounds());
                    boolean z2 = (change.getTaskInfo() == null || change.getTaskInfo().getActivityType() != 2) ? z : true;
                    if (this.mPendingDismiss == iBinder2 && this.mDismissFromSnap && !z2) {
                        rect.offsetTo(z ? 1 : 0, z);
                    }
                    Rect rect2 = new Rect(change.getEndAbsBounds());
                    rect.offset(-transitionInfo.getRootOffset().x, -transitionInfo.getRootOffset().y);
                    rect2.offset(-transitionInfo.getRootOffset().x, -transitionInfo.getRootOffset().y);
                    startExampleResizeAnimation(leash, rect, rect2);
                } else {
                    TransitionInfo transitionInfo3 = transitionInfo;
                }
                if (change.getParent() == null) {
                    if ((iBinder2 == this.mPendingEnter && this.mListener.mPrimary.token.equals(change.getContainer())) || this.mListener.mSecondary.token.equals(change.getContainer())) {
                        transaction2.setWindowCrop(leash, change.getStartAbsBounds().width(), change.getStartAbsBounds().height());
                        if (this.mListener.mPrimary.token.equals(change.getContainer())) {
                            transaction2.setLayer(leash, transitionInfo.getChanges().size() + 1);
                        }
                    }
                    boolean isOpeningType = Transitions.isOpeningType(transitionInfo.getType());
                    if (isOpeningType && (mode == 1 || mode == 3)) {
                        startExampleAnimation(leash, true);
                    } else if (!isOpeningType && (mode == 2 || mode == 4)) {
                        if (iBinder2 != this.mPendingDismiss || !this.mDismissFromSnap) {
                            z = false;
                            startExampleAnimation(leash, false);
                        } else {
                            transaction2.setAlpha(leash, 0.0f);
                        }
                    }
                }
                z = false;
            }
            TransitionInfo transitionInfo4 = transitionInfo;
            if (iBinder2 == this.mPendingEnter) {
                int size2 = transitionInfo.getChanges().size() - 1;
                while (true) {
                    if (size2 < 0) {
                        break;
                    }
                    TransitionInfo.Change change3 = (TransitionInfo.Change) transitionInfo.getChanges().get(size2);
                    if (change3.getTaskInfo() == null || change3.getTaskInfo().getActivityType() != 2) {
                        size2--;
                    } else if (change3.getMode() == 1 || change3.getMode() == 3 || change3.getMode() == 6) {
                        z = true;
                    }
                }
                this.mSplitScreen.finishEnterSplitTransition(z);
            }
            transaction.apply();
            onFinish();
            return true;
        } else if (!this.mSplitScreen.isDividerVisible()) {
            return false;
        } else {
            for (int size3 = transitionInfo.getChanges().size() - 1; size3 >= 0; size3--) {
                TransitionInfo.Change change4 = (TransitionInfo.Change) transitionInfo.getChanges().get(size3);
                if (change4.getTaskInfo() != null && change4.getTaskInfo().getActivityType() == 2) {
                    if (change4.getMode() == 1 || change4.getMode() == 3) {
                        this.mSplitScreen.ensureMinimizedSplit();
                    } else if (change4.getMode() == 2 || change4.getMode() == 4) {
                        this.mSplitScreen.ensureNormalSplit();
                    }
                }
            }
            return false;
        }
    }

    /* access modifiers changed from: package-private */
    public void dismissSplit(LegacySplitScreenTaskListener legacySplitScreenTaskListener, LegacySplitDisplayLayout legacySplitDisplayLayout, boolean z, boolean z2) {
        WindowContainerTransaction windowContainerTransaction = new WindowContainerTransaction();
        WindowManagerProxy.buildDismissSplit(windowContainerTransaction, legacySplitScreenTaskListener, legacySplitDisplayLayout, z);
        this.mTransitions.getMainExecutor().execute(new LegacySplitScreenTransitions$$ExternalSyntheticLambda7(this, z2, windowContainerTransaction));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$dismissSplit$6(boolean z, WindowContainerTransaction windowContainerTransaction) {
        this.mDismissFromSnap = z;
        this.mPendingDismiss = this.mTransitions.startTransition(20, windowContainerTransaction, this);
    }

    private void onFinish() {
        if (this.mAnimations.isEmpty()) {
            this.mFinishTransaction.apply();
            this.mTransactionPool.release(this.mFinishTransaction);
            this.mFinishTransaction = null;
            this.mFinishCallback.onTransitionFinished((WindowContainerTransaction) null, (WindowContainerTransactionCallback) null);
            this.mFinishCallback = null;
            IBinder iBinder = this.mAnimatingTransition;
            if (iBinder == this.mPendingEnter) {
                this.mPendingEnter = null;
            }
            if (iBinder == this.mPendingDismiss) {
                this.mSplitScreen.onDismissSplit();
                this.mPendingDismiss = null;
            }
            this.mDismissFromSnap = false;
            this.mAnimatingTransition = null;
        }
    }
}
