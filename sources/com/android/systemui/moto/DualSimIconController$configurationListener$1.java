package com.android.systemui.moto;

import android.content.res.Configuration;
import com.android.systemui.statusbar.policy.ConfigurationController;
import org.jetbrains.annotations.Nullable;

/* compiled from: DualSimIconController.kt */
public final class DualSimIconController$configurationListener$1 implements ConfigurationController.ConfigurationListener {
    final /* synthetic */ DualSimIconController this$0;

    DualSimIconController$configurationListener$1(DualSimIconController dualSimIconController) {
        this.this$0 = dualSimIconController;
    }

    public void onConfigChanged(@Nullable Configuration configuration) {
        DualSimIconController dualSimIconController = this.this$0;
        dualSimIconController.isBiggerDisplaySize = dualSimIconController.isDisplayDensityBiggerThanDefualt();
        this.this$0.updateMobileControllers();
    }
}
