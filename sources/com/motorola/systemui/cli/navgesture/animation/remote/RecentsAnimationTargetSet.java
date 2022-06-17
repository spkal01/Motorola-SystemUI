package com.motorola.systemui.cli.navgesture.animation.remote;

import android.graphics.Rect;
import com.android.systemui.shared.system.RemoteAnimationTargetCompat;

public class RecentsAnimationTargetSet extends RemoteAnimationTargetSet {
    public final Rect homeContentInsets;
    public final Rect minimizedHomeBounds;

    public RecentsAnimationTargetSet(RemoteAnimationTargetCompat[] remoteAnimationTargetCompatArr, RemoteAnimationTargetCompat[] remoteAnimationTargetCompatArr2, Rect rect, Rect rect2) {
        super(remoteAnimationTargetCompatArr, remoteAnimationTargetCompatArr2, 1);
        this.homeContentInsets = rect;
        this.minimizedHomeBounds = rect2;
    }

    public boolean hasTargets() {
        return this.unfilteredApps.length != 0;
    }
}
