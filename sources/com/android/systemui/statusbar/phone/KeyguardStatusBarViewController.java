package com.android.systemui.statusbar.phone;

import com.android.keyguard.CarrierTextController;
import com.android.systemui.util.ViewController;

public class KeyguardStatusBarViewController extends ViewController<KeyguardStatusBarView> {
    private final CarrierTextController mCarrierTextController;

    /* access modifiers changed from: protected */
    public void onViewAttached() {
    }

    /* access modifiers changed from: protected */
    public void onViewDetached() {
    }

    public KeyguardStatusBarViewController(KeyguardStatusBarView keyguardStatusBarView, CarrierTextController carrierTextController) {
        super(keyguardStatusBarView);
        this.mCarrierTextController = carrierTextController;
    }

    /* access modifiers changed from: protected */
    public void onInit() {
        super.onInit();
        this.mCarrierTextController.init();
    }
}
