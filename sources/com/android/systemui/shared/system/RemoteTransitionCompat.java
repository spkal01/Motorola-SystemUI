package com.android.systemui.shared.system;

import android.annotation.NonNull;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.ArrayMap;
import android.view.SurfaceControl;
import android.window.IRemoteTransition;
import android.window.IRemoteTransitionFinishedCallback;
import android.window.TransitionFilter;
import android.window.TransitionInfo;
import android.window.WindowContainerToken;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.util.AnnotationValidations;
import com.android.systemui.shared.recents.model.ThumbnailData;

public class RemoteTransitionCompat implements Parcelable {
    public static final Parcelable.Creator<RemoteTransitionCompat> CREATOR = new Parcelable.Creator<RemoteTransitionCompat>() {
        public RemoteTransitionCompat[] newArray(int i) {
            return new RemoteTransitionCompat[i];
        }

        public RemoteTransitionCompat createFromParcel(Parcel parcel) {
            return new RemoteTransitionCompat(parcel);
        }
    };
    TransitionFilter mFilter = null;
    final IRemoteTransition mTransition;

    public int describeContents() {
        return 0;
    }

    RemoteTransitionCompat(IRemoteTransition iRemoteTransition) {
        this.mTransition = iRemoteTransition;
    }

    @VisibleForTesting
    static class RecentsControllerWrap extends RecentsAnimationControllerCompat {
        private IRemoteTransitionFinishedCallback mFinishCB = null;
        private TransitionInfo mInfo = null;
        private ArrayMap<SurfaceControl, SurfaceControl> mLeashMap = null;
        private SurfaceControl mOpeningLeash = null;
        private WindowContainerToken mPausingTask = null;
        private RecentsAnimationControllerCompat mWrapped = null;

        RecentsControllerWrap() {
        }

        public ThumbnailData screenshotTask(int i) {
            RecentsAnimationControllerCompat recentsAnimationControllerCompat = this.mWrapped;
            if (recentsAnimationControllerCompat != null) {
                return recentsAnimationControllerCompat.screenshotTask(i);
            }
            return null;
        }

        public void setInputConsumerEnabled(boolean z) {
            RecentsAnimationControllerCompat recentsAnimationControllerCompat = this.mWrapped;
            if (recentsAnimationControllerCompat != null) {
                recentsAnimationControllerCompat.setInputConsumerEnabled(z);
            }
        }

        public void setAnimationTargetsBehindSystemBars(boolean z) {
            RecentsAnimationControllerCompat recentsAnimationControllerCompat = this.mWrapped;
            if (recentsAnimationControllerCompat != null) {
                recentsAnimationControllerCompat.setAnimationTargetsBehindSystemBars(z);
            }
        }

        public void hideCurrentInputMethod() {
            this.mWrapped.hideCurrentInputMethod();
        }

        /* JADX WARNING: Removed duplicated region for block: B:24:0x0067  */
        /* JADX WARNING: Removed duplicated region for block: B:31:0x0093 A[LOOP:1: B:29:0x0087->B:31:0x0093, LOOP_END] */
        @android.annotation.SuppressLint({"NewApi"})
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void finish(boolean r5, boolean r6) {
            /*
                r4 = this;
                android.window.IRemoteTransitionFinishedCallback r0 = r4.mFinishCB
                java.lang.String r1 = "RemoteTransitionCompat"
                if (r0 != 0) goto L_0x0011
                java.lang.RuntimeException r4 = new java.lang.RuntimeException
                r4.<init>()
                java.lang.String r5 = "Duplicate call to finish"
                android.util.Log.e(r1, r5, r4)
                return
            L_0x0011:
                com.android.systemui.shared.system.RecentsAnimationControllerCompat r0 = r4.mWrapped
                if (r0 == 0) goto L_0x0018
                r0.finish(r5, r6)
            L_0x0018:
                r6 = 0
                if (r5 != 0) goto L_0x0034
                android.window.WindowContainerToken r5 = r4.mPausingTask     // Catch:{ RemoteException -> 0x0052 }
                if (r5 == 0) goto L_0x0034
                android.view.SurfaceControl r5 = r4.mOpeningLeash     // Catch:{ RemoteException -> 0x0052 }
                if (r5 != 0) goto L_0x0034
                android.window.WindowContainerTransaction r5 = new android.window.WindowContainerTransaction     // Catch:{ RemoteException -> 0x0052 }
                r5.<init>()     // Catch:{ RemoteException -> 0x0052 }
                android.window.WindowContainerToken r0 = r4.mPausingTask     // Catch:{ RemoteException -> 0x0052 }
                r2 = 1
                r5.reorder(r0, r2)     // Catch:{ RemoteException -> 0x0052 }
                android.window.IRemoteTransitionFinishedCallback r0 = r4.mFinishCB     // Catch:{ RemoteException -> 0x0052 }
                r0.onTransitionFinished(r5)     // Catch:{ RemoteException -> 0x0052 }
                goto L_0x0058
            L_0x0034:
                android.view.SurfaceControl r5 = r4.mOpeningLeash     // Catch:{ RemoteException -> 0x0052 }
                if (r5 == 0) goto L_0x004c
                android.view.SurfaceControl$Transaction r5 = new android.view.SurfaceControl$Transaction     // Catch:{ RemoteException -> 0x0052 }
                r5.<init>()     // Catch:{ RemoteException -> 0x0052 }
                android.view.SurfaceControl r0 = r4.mOpeningLeash     // Catch:{ RemoteException -> 0x0052 }
                r5.show(r0)     // Catch:{ RemoteException -> 0x0052 }
                android.view.SurfaceControl r0 = r4.mOpeningLeash     // Catch:{ RemoteException -> 0x0052 }
                r2 = 1065353216(0x3f800000, float:1.0)
                r5.setAlpha(r0, r2)     // Catch:{ RemoteException -> 0x0052 }
                r5.apply()     // Catch:{ RemoteException -> 0x0052 }
            L_0x004c:
                android.window.IRemoteTransitionFinishedCallback r5 = r4.mFinishCB     // Catch:{ RemoteException -> 0x0052 }
                r5.onTransitionFinished(r6)     // Catch:{ RemoteException -> 0x0052 }
                goto L_0x0058
            L_0x0052:
                r5 = move-exception
                java.lang.String r0 = "Failed to call animation finish callback"
                android.util.Log.e(r1, r0, r5)
            L_0x0058:
                android.view.SurfaceControl$Transaction r5 = new android.view.SurfaceControl$Transaction
                r5.<init>()
                r0 = 0
                r1 = r0
            L_0x005f:
                android.util.ArrayMap<android.view.SurfaceControl, android.view.SurfaceControl> r2 = r4.mLeashMap
                int r2 = r2.size()
                if (r1 >= r2) goto L_0x0084
                android.util.ArrayMap<android.view.SurfaceControl, android.view.SurfaceControl> r2 = r4.mLeashMap
                java.lang.Object r2 = r2.keyAt(r1)
                android.util.ArrayMap<android.view.SurfaceControl, android.view.SurfaceControl> r3 = r4.mLeashMap
                java.lang.Object r3 = r3.valueAt(r1)
                if (r2 != r3) goto L_0x0076
                goto L_0x0081
            L_0x0076:
                android.util.ArrayMap<android.view.SurfaceControl, android.view.SurfaceControl> r2 = r4.mLeashMap
                java.lang.Object r2 = r2.valueAt(r1)
                android.view.SurfaceControl r2 = (android.view.SurfaceControl) r2
                r5.remove(r2)
            L_0x0081:
                int r1 = r1 + 1
                goto L_0x005f
            L_0x0084:
                r5.apply()
            L_0x0087:
                android.window.TransitionInfo r5 = r4.mInfo
                java.util.List r5 = r5.getChanges()
                int r5 = r5.size()
                if (r0 >= r5) goto L_0x00a9
                android.window.TransitionInfo r5 = r4.mInfo
                java.util.List r5 = r5.getChanges()
                java.lang.Object r5 = r5.get(r0)
                android.window.TransitionInfo$Change r5 = (android.window.TransitionInfo.Change) r5
                android.view.SurfaceControl r5 = r5.getLeash()
                r5.release()
                int r0 = r0 + 1
                goto L_0x0087
            L_0x00a9:
                r4.mWrapped = r6
                r4.mFinishCB = r6
                r4.mPausingTask = r6
                r4.mInfo = r6
                r4.mOpeningLeash = r6
                r4.mLeashMap = r6
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.shared.system.RemoteTransitionCompat.RecentsControllerWrap.finish(boolean, boolean):void");
        }

        public void setDeferCancelUntilNextTransition(boolean z, boolean z2) {
            RecentsAnimationControllerCompat recentsAnimationControllerCompat = this.mWrapped;
            if (recentsAnimationControllerCompat != null) {
                recentsAnimationControllerCompat.setDeferCancelUntilNextTransition(z, z2);
            }
        }

        public void cleanupScreenshot() {
            RecentsAnimationControllerCompat recentsAnimationControllerCompat = this.mWrapped;
            if (recentsAnimationControllerCompat != null) {
                recentsAnimationControllerCompat.cleanupScreenshot();
            }
        }

        public boolean removeTask(int i) {
            RecentsAnimationControllerCompat recentsAnimationControllerCompat = this.mWrapped;
            if (recentsAnimationControllerCompat != null) {
                return recentsAnimationControllerCompat.removeTask(i);
            }
            return false;
        }
    }

    public IRemoteTransition getTransition() {
        return this.mTransition;
    }

    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeByte(this.mFilter != null ? (byte) 2 : 0);
        parcel.writeStrongInterface(this.mTransition);
        TransitionFilter transitionFilter = this.mFilter;
        if (transitionFilter != null) {
            parcel.writeTypedObject(transitionFilter, i);
        }
    }

    protected RemoteTransitionCompat(Parcel parcel) {
        TransitionFilter transitionFilter;
        byte readByte = parcel.readByte();
        IRemoteTransition asInterface = IRemoteTransition.Stub.asInterface(parcel.readStrongBinder());
        if ((readByte & 2) == 0) {
            transitionFilter = null;
        } else {
            transitionFilter = (TransitionFilter) parcel.readTypedObject(TransitionFilter.CREATOR);
        }
        this.mTransition = asInterface;
        AnnotationValidations.validate(NonNull.class, (NonNull) null, asInterface);
        this.mFilter = transitionFilter;
    }
}
