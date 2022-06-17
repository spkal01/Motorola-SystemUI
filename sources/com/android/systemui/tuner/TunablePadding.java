package com.android.systemui.tuner;

import android.view.View;
import com.android.systemui.tuner.TunerService;

public class TunablePadding implements TunerService.Tunable {
    private final int mDefaultSize;
    private final float mDensity;
    private final int mFlags;
    private final View mView;

    public void onTuningChanged(String str, String str2) {
        int i = this.mDefaultSize;
        if (str2 != null) {
            try {
                i = (int) (((float) Integer.parseInt(str2)) * this.mDensity);
            } catch (NumberFormatException unused) {
            }
        }
        int i2 = 2;
        int i3 = this.mView.isLayoutRtl() ? 2 : 1;
        if (this.mView.isLayoutRtl()) {
            i2 = 1;
        }
        this.mView.setPadding(getPadding(i, i3, 0), getPadding(i, 4, this.mView.getPaddingTop()), getPadding(i, i2, 0), getPadding(i, 8, this.mView.getPaddingBottom()));
    }

    private int getPadding(int i, int i2, int i3) {
        return (this.mFlags & i2) != 0 ? i : i3;
    }

    public static class TunablePaddingService {
        private final TunerService mTunerService;

        public TunablePaddingService(TunerService tunerService) {
            this.mTunerService = tunerService;
        }
    }
}
