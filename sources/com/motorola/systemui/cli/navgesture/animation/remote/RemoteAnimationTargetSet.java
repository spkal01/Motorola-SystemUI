package com.motorola.systemui.cli.navgesture.animation.remote;

import com.android.systemui.shared.system.RemoteAnimationTargetCompat;
import com.android.systemui.shared.system.SyncRtSurfaceTransactionApplierCompat;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Queue;

public class RemoteAnimationTargetSet {
    public final RemoteAnimationTargetCompat[] apps;
    public final boolean hasRecents;
    private final Queue<SyncRtSurfaceTransactionApplierCompat> mDependentTransactionAppliers = new ArrayDeque(1);
    public final int targetMode;
    public final RemoteAnimationTargetCompat[] unfilteredApps;
    public final RemoteAnimationTargetCompat[] wallpapers;

    public RemoteAnimationTargetSet(RemoteAnimationTargetCompat[] remoteAnimationTargetCompatArr, RemoteAnimationTargetCompat[] remoteAnimationTargetCompatArr2, int i) {
        ArrayList arrayList = new ArrayList();
        boolean z = false;
        if (remoteAnimationTargetCompatArr != null) {
            boolean z2 = false;
            for (RemoteAnimationTargetCompat remoteAnimationTargetCompat : remoteAnimationTargetCompatArr) {
                if (remoteAnimationTargetCompat.mode == i) {
                    arrayList.add(remoteAnimationTargetCompat);
                }
                z2 |= remoteAnimationTargetCompat.activityType == 3;
            }
            z = z2;
        }
        this.unfilteredApps = remoteAnimationTargetCompatArr;
        this.apps = (RemoteAnimationTargetCompat[]) arrayList.toArray(new RemoteAnimationTargetCompat[arrayList.size()]);
        this.wallpapers = remoteAnimationTargetCompatArr2;
        this.targetMode = i;
        this.hasRecents = z;
    }

    public RemoteAnimationTargetCompat findTask(int i) {
        for (RemoteAnimationTargetCompat remoteAnimationTargetCompat : this.apps) {
            if (remoteAnimationTargetCompat.taskId == i) {
                return remoteAnimationTargetCompat;
            }
        }
        return null;
    }

    public boolean isAnimatingHome() {
        for (RemoteAnimationTargetCompat remoteAnimationTargetCompat : this.apps) {
            if (remoteAnimationTargetCompat.activityType == 2) {
                return true;
            }
        }
        return false;
    }

    public void addDependentTransactionApplier(SyncRtSurfaceTransactionApplierCompat syncRtSurfaceTransactionApplierCompat) {
        this.mDependentTransactionAppliers.add(syncRtSurfaceTransactionApplierCompat);
    }

    public void release() {
        SyncRtSurfaceTransactionApplierCompat poll = this.mDependentTransactionAppliers.poll();
        if (poll == null) {
            for (RemoteAnimationTargetCompat release : this.unfilteredApps) {
                release.release();
            }
            for (RemoteAnimationTargetCompat release2 : this.wallpapers) {
                release2.release();
            }
            return;
        }
        poll.addAfterApplyCallback(new RemoteAnimationTargetSet$$ExternalSyntheticLambda0(this));
    }
}
