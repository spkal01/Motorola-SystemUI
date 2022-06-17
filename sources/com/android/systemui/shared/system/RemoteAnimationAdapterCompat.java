package com.android.systemui.shared.system;

import android.annotation.SuppressLint;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.ArrayMap;
import android.util.Log;
import android.view.IRemoteAnimationFinishedCallback;
import android.view.IRemoteAnimationRunner;
import android.view.RemoteAnimationAdapter;
import android.view.RemoteAnimationTarget;
import android.view.SurfaceControl;
import android.window.IRemoteTransition;
import android.window.IRemoteTransitionFinishedCallback;
import android.window.TransitionInfo;
import android.window.WindowContainerTransaction;
import java.util.ArrayList;

public class RemoteAnimationAdapterCompat {
    private final RemoteTransitionCompat mRemoteTransition;
    private final RemoteAnimationAdapter mWrapped;

    public RemoteAnimationAdapterCompat(RemoteAnimationRunnerCompat remoteAnimationRunnerCompat, long j, long j2) {
        this.mWrapped = new RemoteAnimationAdapter(wrapRemoteAnimationRunner(remoteAnimationRunnerCompat), j, j2);
        this.mRemoteTransition = buildRemoteTransition(remoteAnimationRunnerCompat);
    }

    /* access modifiers changed from: package-private */
    public RemoteAnimationAdapter getWrapped() {
        return this.mWrapped;
    }

    public static RemoteTransitionCompat buildRemoteTransition(RemoteAnimationRunnerCompat remoteAnimationRunnerCompat) {
        return new RemoteTransitionCompat((IRemoteTransition) wrapRemoteTransition(remoteAnimationRunnerCompat));
    }

    public RemoteTransitionCompat getRemoteTransition() {
        return this.mRemoteTransition;
    }

    private static IRemoteAnimationRunner.Stub wrapRemoteAnimationRunner(final RemoteAnimationRunnerCompat remoteAnimationRunnerCompat) {
        return new IRemoteAnimationRunner.Stub() {
            public void onAnimationStart(int i, RemoteAnimationTarget[] remoteAnimationTargetArr, RemoteAnimationTarget[] remoteAnimationTargetArr2, RemoteAnimationTarget[] remoteAnimationTargetArr3, final IRemoteAnimationFinishedCallback iRemoteAnimationFinishedCallback) {
                RemoteAnimationRunnerCompat.this.onAnimationStart(i, RemoteAnimationTargetCompat.wrap(remoteAnimationTargetArr), RemoteAnimationTargetCompat.wrap(remoteAnimationTargetArr2), RemoteAnimationTargetCompat.wrap(remoteAnimationTargetArr3), new Runnable() {
                    public void run() {
                        try {
                            iRemoteAnimationFinishedCallback.onAnimationFinished();
                        } catch (RemoteException e) {
                            Log.e("ActivityOptionsCompat", "Failed to call app controlled animation finished callback", e);
                        }
                    }
                });
            }

            public void onAnimationCancelled() {
                RemoteAnimationRunnerCompat.this.onAnimationCancelled();
            }
        };
    }

    private static class CounterRotator {
        ArrayList<SurfaceControl> mRotateChildren;
        SurfaceControl mSurface;

        private CounterRotator() {
            this.mSurface = null;
            this.mRotateChildren = null;
        }

        /* access modifiers changed from: package-private */
        public void setup(SurfaceControl.Transaction transaction, SurfaceControl surfaceControl, int i, float f, float f2) {
            if (i != 0) {
                this.mRotateChildren = new ArrayList<>();
                int i2 = 4 - ((i + 4) % 4);
                SurfaceControl build = new SurfaceControl.Builder().setName("Transition Unrotate").setContainerLayer().setParent(surfaceControl).build();
                this.mSurface = build;
                if (i2 == 1) {
                    transaction.setMatrix(build, 0.0f, 1.0f, -1.0f, 0.0f);
                    transaction.setPosition(this.mSurface, f, 0.0f);
                } else if (i2 == 2) {
                    transaction.setMatrix(build, -1.0f, 0.0f, 0.0f, -1.0f);
                    transaction.setPosition(this.mSurface, f, f2);
                } else if (i2 == 3) {
                    transaction.setMatrix(build, 0.0f, -1.0f, 1.0f, 0.0f);
                    transaction.setPosition(this.mSurface, 0.0f, f2);
                }
                transaction.show(this.mSurface);
            }
        }

        /* access modifiers changed from: package-private */
        public void addChild(SurfaceControl.Transaction transaction, SurfaceControl surfaceControl) {
            SurfaceControl surfaceControl2 = this.mSurface;
            if (surfaceControl2 != null) {
                transaction.reparent(surfaceControl, surfaceControl2);
                this.mRotateChildren.add(surfaceControl);
            }
        }

        /* access modifiers changed from: package-private */
        public void cleanUp(SurfaceControl surfaceControl) {
            if (this.mSurface != null) {
                SurfaceControl.Transaction transaction = new SurfaceControl.Transaction();
                for (int size = this.mRotateChildren.size() - 1; size >= 0; size--) {
                    transaction.reparent(this.mRotateChildren.get(size), surfaceControl);
                }
                transaction.remove(this.mSurface);
                transaction.apply();
            }
        }
    }

    private static IRemoteTransition.Stub wrapRemoteTransition(final RemoteAnimationRunnerCompat remoteAnimationRunnerCompat) {
        return new IRemoteTransition.Stub() {
            public void mergeAnimation(IBinder iBinder, TransitionInfo transitionInfo, SurfaceControl.Transaction transaction, IBinder iBinder2, IRemoteTransitionFinishedCallback iRemoteTransitionFinishedCallback) {
            }

            public void startAnimation(IBinder iBinder, TransitionInfo transitionInfo, SurfaceControl.Transaction transaction, IRemoteTransitionFinishedCallback iRemoteTransitionFinishedCallback) {
                CounterRotator counterRotator;
                final CounterRotator counterRotator2;
                TransitionInfo transitionInfo2 = transitionInfo;
                SurfaceControl.Transaction transaction2 = transaction;
                ArrayMap arrayMap = new ArrayMap();
                RemoteAnimationTargetCompat[] wrap = RemoteAnimationTargetCompat.wrap(transitionInfo2, false, transaction2, arrayMap);
                RemoteAnimationTargetCompat[] wrap2 = RemoteAnimationTargetCompat.wrap(transitionInfo2, true, transaction2, arrayMap);
                RemoteAnimationTargetCompat[] remoteAnimationTargetCompatArr = new RemoteAnimationTargetCompat[0];
                int i = 0;
                int i2 = 0;
                boolean z = false;
                float f = 0.0f;
                float f2 = 0.0f;
                TransitionInfo.Change change = null;
                TransitionInfo.Change change2 = null;
                for (int size = transitionInfo.getChanges().size() - 1; size >= 0; size--) {
                    TransitionInfo.Change change3 = (TransitionInfo.Change) transitionInfo.getChanges().get(size);
                    if (change3.getTaskInfo() != null && change3.getTaskInfo().getActivityType() == 2) {
                        z = change3.getMode() == 1 || change3.getMode() == 3;
                        i = transitionInfo.getChanges().size() - size;
                        change = change3;
                    } else if ((change3.getFlags() & 2) != 0) {
                        change2 = change3;
                    }
                    if (change3.getParent() == null && change3.getEndRotation() >= 0 && change3.getEndRotation() != change3.getStartRotation()) {
                        i2 = change3.getEndRotation() - change3.getStartRotation();
                        f = (float) change3.getEndAbsBounds().width();
                        f2 = (float) change3.getEndAbsBounds().height();
                    }
                }
                CounterRotator counterRotator3 = new CounterRotator();
                CounterRotator counterRotator4 = new CounterRotator();
                if (change == null || i2 == 0 || change.getParent() == null) {
                    counterRotator = counterRotator4;
                } else {
                    counterRotator = counterRotator4;
                    int i3 = i;
                    counterRotator3.setup(transaction, transitionInfo2.getChange(change.getParent()).getLeash(), i2, f, f2);
                    SurfaceControl surfaceControl = counterRotator3.mSurface;
                    if (surfaceControl != null) {
                        transaction2.setLayer(surfaceControl, i3);
                    }
                }
                if (z) {
                    SurfaceControl surfaceControl2 = counterRotator3.mSurface;
                    if (surfaceControl2 != null) {
                        transaction2.setLayer(surfaceControl2, transitionInfo.getChanges().size() * 3);
                    }
                    for (int size2 = transitionInfo.getChanges().size() - 1; size2 >= 0; size2--) {
                        TransitionInfo.Change change4 = (TransitionInfo.Change) transitionInfo.getChanges().get(size2);
                        SurfaceControl surfaceControl3 = (SurfaceControl) arrayMap.get(change4.getLeash());
                        int mode = ((TransitionInfo.Change) transitionInfo.getChanges().get(size2)).getMode();
                        if (TransitionInfo.isIndependent(change4, transitionInfo2)) {
                            if (mode == 2 || mode == 4) {
                                transaction2.setLayer(surfaceControl3, (transitionInfo.getChanges().size() * 3) - size2);
                                counterRotator3.addChild(transaction2, surfaceControl3);
                            }
                        }
                    }
                    for (int length = wrap2.length - 1; length >= 0; length--) {
                        transaction2.show(wrap2[length].leash.getSurfaceControl());
                        transaction2.setAlpha(wrap2[length].leash.getSurfaceControl(), 1.0f);
                    }
                } else {
                    if (change != null) {
                        counterRotator3.addChild(transaction2, (SurfaceControl) arrayMap.get(change.getLeash()));
                    }
                    if (!(change2 == null || i2 == 0 || change2.getParent() == null)) {
                        counterRotator2 = counterRotator;
                        counterRotator2.setup(transaction, transitionInfo2.getChange(change2.getParent()).getLeash(), i2, f, f2);
                        SurfaceControl surfaceControl4 = counterRotator2.mSurface;
                        if (surfaceControl4 != null) {
                            transaction2.setLayer(surfaceControl4, -1);
                            counterRotator2.addChild(transaction2, (SurfaceControl) arrayMap.get(change2.getLeash()));
                        }
                        transaction.apply();
                        final CounterRotator counterRotator5 = counterRotator3;
                        final TransitionInfo transitionInfo3 = transitionInfo;
                        final ArrayMap arrayMap2 = arrayMap;
                        final IRemoteTransitionFinishedCallback iRemoteTransitionFinishedCallback2 = iRemoteTransitionFinishedCallback;
                        RemoteAnimationRunnerCompat.this.onAnimationStart(0, wrap, wrap2, remoteAnimationTargetCompatArr, new Runnable() {
                            @SuppressLint({"NewApi"})
                            public void run() {
                                try {
                                    counterRotator5.cleanUp(transitionInfo3.getRootLeash());
                                    counterRotator2.cleanUp(transitionInfo3.getRootLeash());
                                    for (int i = 0; i < transitionInfo3.getChanges().size(); i++) {
                                        ((TransitionInfo.Change) transitionInfo3.getChanges().get(i)).getLeash().release();
                                    }
                                    SurfaceControl.Transaction transaction = new SurfaceControl.Transaction();
                                    for (int i2 = 0; i2 < arrayMap2.size(); i2++) {
                                        if (arrayMap2.keyAt(i2) != arrayMap2.valueAt(i2)) {
                                            transaction.remove((SurfaceControl) arrayMap2.valueAt(i2));
                                        }
                                    }
                                    transaction.apply();
                                    iRemoteTransitionFinishedCallback2.onTransitionFinished((WindowContainerTransaction) null);
                                } catch (RemoteException e) {
                                    Log.e("ActivityOptionsCompat", "Failed to call app controlled animation finished callback", e);
                                }
                            }
                        });
                    }
                }
                counterRotator2 = counterRotator;
                transaction.apply();
                final CounterRotator counterRotator52 = counterRotator3;
                final TransitionInfo transitionInfo32 = transitionInfo;
                final ArrayMap arrayMap22 = arrayMap;
                final IRemoteTransitionFinishedCallback iRemoteTransitionFinishedCallback22 = iRemoteTransitionFinishedCallback;
                RemoteAnimationRunnerCompat.this.onAnimationStart(0, wrap, wrap2, remoteAnimationTargetCompatArr, new Runnable() {
                    @SuppressLint({"NewApi"})
                    public void run() {
                        try {
                            counterRotator52.cleanUp(transitionInfo32.getRootLeash());
                            counterRotator2.cleanUp(transitionInfo32.getRootLeash());
                            for (int i = 0; i < transitionInfo32.getChanges().size(); i++) {
                                ((TransitionInfo.Change) transitionInfo32.getChanges().get(i)).getLeash().release();
                            }
                            SurfaceControl.Transaction transaction = new SurfaceControl.Transaction();
                            for (int i2 = 0; i2 < arrayMap22.size(); i2++) {
                                if (arrayMap22.keyAt(i2) != arrayMap22.valueAt(i2)) {
                                    transaction.remove((SurfaceControl) arrayMap22.valueAt(i2));
                                }
                            }
                            transaction.apply();
                            iRemoteTransitionFinishedCallback22.onTransitionFinished((WindowContainerTransaction) null);
                        } catch (RemoteException e) {
                            Log.e("ActivityOptionsCompat", "Failed to call app controlled animation finished callback", e);
                        }
                    }
                });
            }
        };
    }
}
