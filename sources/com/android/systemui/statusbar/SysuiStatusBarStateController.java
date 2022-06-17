package com.android.systemui.statusbar;

import android.view.View;
import com.android.systemui.plugins.statusbar.StatusBarStateController;

public interface SysuiStatusBarStateController extends StatusBarStateController {
    @Deprecated
    void addCallback(StatusBarStateController.StateListener stateListener, int i);

    boolean fromShadeLocked();

    int getCurrentOrUpcomingState();

    float getInterpolatedDozeAmount();

    boolean goingToFullShade();

    boolean isKeyguardRequested();

    boolean leaveOpenOnKeyguardHide();

    void setAndInstrumentDozeAmount(View view, float f, boolean z);

    void setFullscreenState(boolean z);

    boolean setIsDozing(boolean z);

    void setKeyguardRequested(boolean z);

    void setLeaveOpenOnKeyguardHide(boolean z);

    boolean setPanelExpanded(boolean z);

    void setPulsing(boolean z);

    boolean setState(int i, boolean z);

    void setUpcomingState(int i);

    boolean setState(int i) {
        return setState(i, false);
    }

    public static class RankedListener {
        final StatusBarStateController.StateListener mListener;
        /* access modifiers changed from: package-private */
        public final int mRank;

        RankedListener(StatusBarStateController.StateListener stateListener, int i) {
            this.mListener = stateListener;
            this.mRank = i;
        }
    }
}
