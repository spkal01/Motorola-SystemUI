package com.android.systemui.shared.system;

import android.view.SurfaceControl;

public class SurfaceControlCompat {
    final SurfaceControl mSurfaceControl;

    public SurfaceControlCompat(SurfaceControl surfaceControl) {
        this.mSurfaceControl = surfaceControl;
    }

    public SurfaceControl getSurfaceControl() {
        return this.mSurfaceControl;
    }
}
