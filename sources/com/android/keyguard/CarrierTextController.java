package com.android.keyguard;

import com.android.keyguard.CarrierTextManager;
import com.android.systemui.util.ViewController;

public class CarrierTextController extends ViewController<CarrierText> {
    private final CarrierTextManager.CarrierTextCallback mCarrierTextCallback = new CarrierTextManager.CarrierTextCallback() {
        public void updateCarrierInfo(CarrierTextManager.CarrierTextCallbackInfo carrierTextCallbackInfo) {
            ((CarrierText) CarrierTextController.this.mView).setText(carrierTextCallbackInfo.carrierText);
        }

        public void startedGoingToSleep() {
            ((CarrierText) CarrierTextController.this.mView).setSelected(false);
        }

        public void finishedWakingUp() {
            ((CarrierText) CarrierTextController.this.mView).setSelected(true);
        }
    };
    private final CarrierTextManager mCarrierTextManager;
    private final KeyguardUpdateMonitor mKeyguardUpdateMonitor;

    public CarrierTextController(CarrierText carrierText, CarrierTextManager.Builder builder, KeyguardUpdateMonitor keyguardUpdateMonitor) {
        super(carrierText);
        this.mCarrierTextManager = builder.setShowAirplaneMode(((CarrierText) this.mView).getShowAirplaneMode()).setShowMissingSim(((CarrierText) this.mView).getShowMissingSim()).build();
        this.mKeyguardUpdateMonitor = keyguardUpdateMonitor;
    }

    /* access modifiers changed from: protected */
    public void onInit() {
        super.onInit();
        ((CarrierText) this.mView).setSelected(this.mKeyguardUpdateMonitor.isDeviceInteractive());
    }

    /* access modifiers changed from: protected */
    public void onViewAttached() {
        this.mCarrierTextManager.setListening(this.mCarrierTextCallback);
    }

    /* access modifiers changed from: protected */
    public void onViewDetached() {
        this.mCarrierTextManager.setListening((CarrierTextManager.CarrierTextCallback) null);
    }
}
