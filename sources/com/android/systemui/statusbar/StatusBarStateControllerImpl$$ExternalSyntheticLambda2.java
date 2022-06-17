package com.android.systemui.statusbar;

import com.android.systemui.statusbar.SysuiStatusBarStateController;
import java.util.function.ToIntFunction;

public final /* synthetic */ class StatusBarStateControllerImpl$$ExternalSyntheticLambda2 implements ToIntFunction {
    public static final /* synthetic */ StatusBarStateControllerImpl$$ExternalSyntheticLambda2 INSTANCE = new StatusBarStateControllerImpl$$ExternalSyntheticLambda2();

    private /* synthetic */ StatusBarStateControllerImpl$$ExternalSyntheticLambda2() {
    }

    public final int applyAsInt(Object obj) {
        return ((SysuiStatusBarStateController.RankedListener) obj).mRank;
    }
}
