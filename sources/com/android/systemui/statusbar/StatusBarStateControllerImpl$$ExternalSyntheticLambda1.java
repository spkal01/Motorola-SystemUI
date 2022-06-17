package com.android.systemui.statusbar;

import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.statusbar.SysuiStatusBarStateController;
import java.util.function.Predicate;

public final /* synthetic */ class StatusBarStateControllerImpl$$ExternalSyntheticLambda1 implements Predicate {
    public final /* synthetic */ StatusBarStateController.StateListener f$0;

    public /* synthetic */ StatusBarStateControllerImpl$$ExternalSyntheticLambda1(StatusBarStateController.StateListener stateListener) {
        this.f$0 = stateListener;
    }

    public final boolean test(Object obj) {
        return ((SysuiStatusBarStateController.RankedListener) obj).mListener.equals(this.f$0);
    }
}
