package com.android.p011wm.shell.transition;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Rect;
import android.os.IBinder;
import android.util.ArrayMap;
import android.view.Choreographer;
import android.view.SurfaceControl;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.window.TransitionInfo;
import android.window.TransitionRequestInfo;
import android.window.WindowContainerTransaction;
import android.window.WindowContainerTransactionCallback;
import com.android.internal.policy.AttributeCache;
import com.android.internal.policy.TransitionAnimation;
import com.android.p011wm.shell.common.ShellExecutor;
import com.android.p011wm.shell.common.TransactionPool;
import com.android.p011wm.shell.legacysplitscreen.LegacySplitScreenTransitions$$ExternalSyntheticLambda2;
import com.android.p011wm.shell.protolog.ShellProtoLogCache;
import com.android.p011wm.shell.protolog.ShellProtoLogGroup;
import com.android.p011wm.shell.protolog.ShellProtoLogImpl;
import com.android.p011wm.shell.transition.Transitions;
import java.util.ArrayList;

/* renamed from: com.android.wm.shell.transition.DefaultTransitionHandler */
public class DefaultTransitionHandler implements Transitions.TransitionHandler {
    private final ShellExecutor mAnimExecutor;
    private final ArrayMap<IBinder, ArrayList<Animator>> mAnimations = new ArrayMap<>();
    private final Rect mInsets = new Rect(0, 0, 0, 0);
    private final ShellExecutor mMainExecutor;
    private final TransactionPool mTransactionPool;
    private final TransitionAnimation mTransitionAnimation;
    private float mTransitionAnimationScaleSetting = 1.0f;

    public WindowContainerTransaction handleRequest(IBinder iBinder, TransitionRequestInfo transitionRequestInfo) {
        return null;
    }

    DefaultTransitionHandler(TransactionPool transactionPool, Context context, ShellExecutor shellExecutor, ShellExecutor shellExecutor2) {
        this.mTransactionPool = transactionPool;
        this.mMainExecutor = shellExecutor;
        this.mAnimExecutor = shellExecutor2;
        this.mTransitionAnimation = new TransitionAnimation(context, false, "ShellTransitions");
        AttributeCache.init(context);
    }

    public boolean startAnimation(IBinder iBinder, TransitionInfo transitionInfo, SurfaceControl.Transaction transaction, Transitions.TransitionFinishCallback transitionFinishCallback) {
        Animation loadAnimation;
        if (ShellProtoLogCache.WM_SHELL_TRANSITIONS_enabled) {
            ShellProtoLogImpl.m93v(ShellProtoLogGroup.WM_SHELL_TRANSITIONS, -146110597, 0, "start default transition animation, info = %s", String.valueOf(transitionInfo));
        }
        if (!this.mAnimations.containsKey(iBinder)) {
            ArrayList arrayList = new ArrayList();
            this.mAnimations.put(iBinder, arrayList);
            DefaultTransitionHandler$$ExternalSyntheticLambda2 defaultTransitionHandler$$ExternalSyntheticLambda2 = new DefaultTransitionHandler$$ExternalSyntheticLambda2(this, arrayList, iBinder, transitionFinishCallback);
            for (int size = transitionInfo.getChanges().size() - 1; size >= 0; size--) {
                TransitionInfo.Change change = (TransitionInfo.Change) transitionInfo.getChanges().get(size);
                if (change.getMode() == 6) {
                    transaction.setPosition(change.getLeash(), (float) (change.getEndAbsBounds().left - change.getEndRelOffset().x), (float) (change.getEndAbsBounds().top - change.getEndRelOffset().y));
                    if (change.getTaskInfo() != null) {
                        transaction.setWindowCrop(change.getLeash(), change.getEndAbsBounds().width(), change.getEndAbsBounds().height());
                    }
                }
                if (TransitionInfo.isIndependent(change, transitionInfo) && (loadAnimation = loadAnimation(transitionInfo.getType(), transitionInfo.getFlags(), change)) != null) {
                    startAnimInternal(arrayList, loadAnimation, change.getLeash(), defaultTransitionHandler$$ExternalSyntheticLambda2);
                }
            }
            transaction.apply();
            defaultTransitionHandler$$ExternalSyntheticLambda2.run();
            return true;
        }
        throw new IllegalStateException("Got a duplicate startAnimation call for " + iBinder);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$startAnimation$0(ArrayList arrayList, IBinder iBinder, Transitions.TransitionFinishCallback transitionFinishCallback) {
        if (arrayList.isEmpty()) {
            this.mAnimations.remove(iBinder);
            transitionFinishCallback.onTransitionFinished((WindowContainerTransaction) null, (WindowContainerTransactionCallback) null);
        }
    }

    public void setAnimScaleSetting(float f) {
        this.mTransitionAnimationScaleSetting = f;
    }

    private Animation loadAnimation(int i, int i2, TransitionInfo.Change change) {
        boolean isOpeningType = Transitions.isOpeningType(i);
        int mode = change.getMode();
        int flags = change.getFlags();
        Animation animation = null;
        if (i == 5) {
            animation = this.mTransitionAnimation.createRelaunchAnimation(change.getStartAbsBounds(), this.mInsets, change.getEndAbsBounds());
        } else {
            boolean z = false;
            if (i == 7) {
                TransitionAnimation transitionAnimation = this.mTransitionAnimation;
                if ((flags & 1) != 0) {
                    z = true;
                }
                animation = transitionAnimation.loadKeyguardExitAnimation(i2, z);
            } else if (i == 9) {
                animation = this.mTransitionAnimation.loadKeyguardUnoccludeAnimation();
            } else if (mode != 1 || !isOpeningType) {
                if (mode != 3 || !isOpeningType) {
                    if (mode != 2 || isOpeningType) {
                        if (mode == 4 && !isOpeningType) {
                            animation = (flags & 16) != 0 ? this.mTransitionAnimation.loadVoiceActivityExitAnimation(false) : this.mTransitionAnimation.loadDefaultAnimationAttr(15);
                        } else if (mode == 6) {
                            animation = new AlphaAnimation(1.0f, 1.0f);
                            animation.setDuration(336);
                        }
                    } else if ((flags & 16) != 0) {
                        animation = this.mTransitionAnimation.loadVoiceActivityExitAnimation(false);
                    } else if (change.getTaskInfo() != null) {
                        animation = this.mTransitionAnimation.loadDefaultAnimationAttr(11);
                    } else {
                        animation = this.mTransitionAnimation.loadDefaultAnimationRes((4 & flags) == 0 ? 17432590 : 17432593);
                    }
                } else if ((flags & 8) != 0) {
                    return null;
                } else {
                    animation = (flags & 16) != 0 ? this.mTransitionAnimation.loadVoiceActivityOpenAnimation(true) : this.mTransitionAnimation.loadDefaultAnimationAttr(12);
                }
            } else if ((flags & 8) != 0) {
                return null;
            } else {
                if ((flags & 16) != 0) {
                    animation = this.mTransitionAnimation.loadVoiceActivityOpenAnimation(true);
                } else if (change.getTaskInfo() != null) {
                    animation = this.mTransitionAnimation.loadDefaultAnimationAttr(8);
                } else {
                    animation = this.mTransitionAnimation.loadDefaultAnimationRes((4 & flags) == 0 ? 17432591 : 17432594);
                }
            }
        }
        if (animation != null) {
            Rect startAbsBounds = change.getStartAbsBounds();
            Rect endAbsBounds = change.getEndAbsBounds();
            animation.restrictDuration(3000);
            animation.initialize(endAbsBounds.width(), endAbsBounds.height(), startAbsBounds.width(), startAbsBounds.height());
            animation.scaleCurrentDuration(this.mTransitionAnimationScaleSetting);
        }
        return animation;
    }

    private void startAnimInternal(ArrayList<Animator> arrayList, Animation animation, SurfaceControl surfaceControl, Runnable runnable) {
        SurfaceControl.Transaction acquire = this.mTransactionPool.acquire();
        ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
        Transformation transformation = new Transformation();
        ofFloat.overrideDurationScale(1.0f);
        ofFloat.setDuration(animation.computeDurationHint());
        ValueAnimator valueAnimator = ofFloat;
        SurfaceControl.Transaction transaction = acquire;
        SurfaceControl surfaceControl2 = surfaceControl;
        Animation animation2 = animation;
        Transformation transformation2 = transformation;
        float[] fArr = new float[9];
        ofFloat.addUpdateListener(new DefaultTransitionHandler$$ExternalSyntheticLambda0(valueAnimator, transaction, surfaceControl2, animation2, transformation2, fArr));
        final DefaultTransitionHandler$$ExternalSyntheticLambda1 defaultTransitionHandler$$ExternalSyntheticLambda1 = new DefaultTransitionHandler$$ExternalSyntheticLambda1(this, valueAnimator, transaction, surfaceControl2, animation2, transformation2, fArr, arrayList, runnable);
        ofFloat.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animator) {
                defaultTransitionHandler$$ExternalSyntheticLambda1.run();
            }

            public void onAnimationCancel(Animator animator) {
                defaultTransitionHandler$$ExternalSyntheticLambda1.run();
            }
        });
        ArrayList<Animator> arrayList2 = arrayList;
        arrayList.add(ofFloat);
        this.mAnimExecutor.execute(new LegacySplitScreenTransitions$$ExternalSyntheticLambda2(ofFloat));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$startAnimInternal$3(ValueAnimator valueAnimator, SurfaceControl.Transaction transaction, SurfaceControl surfaceControl, Animation animation, Transformation transformation, float[] fArr, ArrayList arrayList, Runnable runnable) {
        applyTransformation(valueAnimator.getDuration(), transaction, surfaceControl, animation, transformation, fArr);
        this.mTransactionPool.release(transaction);
        this.mMainExecutor.execute(new DefaultTransitionHandler$$ExternalSyntheticLambda3(arrayList, valueAnimator, runnable));
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$startAnimInternal$2(ArrayList arrayList, ValueAnimator valueAnimator, Runnable runnable) {
        arrayList.remove(valueAnimator);
        runnable.run();
    }

    /* access modifiers changed from: private */
    public static void applyTransformation(long j, SurfaceControl.Transaction transaction, SurfaceControl surfaceControl, Animation animation, Transformation transformation, float[] fArr) {
        animation.getTransformation(j, transformation);
        transaction.setMatrix(surfaceControl, transformation.getMatrix(), fArr);
        transaction.setAlpha(surfaceControl, transformation.getAlpha());
        transaction.setFrameTimelineVsync(Choreographer.getInstance().getVsyncId());
        transaction.apply();
    }
}
