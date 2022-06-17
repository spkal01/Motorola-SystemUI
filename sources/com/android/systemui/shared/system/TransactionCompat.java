package com.android.systemui.shared.system;

import android.view.SurfaceControl;

public class TransactionCompat {
    final float[] mTmpValues = new float[9];
    final SurfaceControl.Transaction mTransaction = new SurfaceControl.Transaction();

    public void apply() {
        this.mTransaction.apply();
    }

    public TransactionCompat setAlpha(SurfaceControlCompat surfaceControlCompat, float f) {
        this.mTransaction.setAlpha(surfaceControlCompat.mSurfaceControl, f);
        return this;
    }
}
