package com.android.systemui.accessibility;

import android.content.Context;
import android.hardware.display.DisplayManager;
import android.os.Bundle;
import android.view.Display;
import com.android.internal.annotations.VisibleForTesting;

public class ModeSwitchesController {
    private final DisplayIdIndexSupplier<MagnificationModeSwitch> mSwitchSupplier;

    public ModeSwitchesController(Context context) {
        this.mSwitchSupplier = new SwitchSupplier(context, (DisplayManager) context.getSystemService(DisplayManager.class));
    }

    @VisibleForTesting
    ModeSwitchesController(DisplayIdIndexSupplier<MagnificationModeSwitch> displayIdIndexSupplier) {
        this.mSwitchSupplier = displayIdIndexSupplier;
    }

    /* access modifiers changed from: package-private */
    public void showButton(int i, int i2) {
        MagnificationModeSwitch magnificationModeSwitch = this.mSwitchSupplier.get(i);
        if (magnificationModeSwitch != null) {
            magnificationModeSwitch.showButton(i2);
        }
    }

    /* access modifiers changed from: package-private */
    public void removeButton(int i) {
        MagnificationModeSwitch magnificationModeSwitch = this.mSwitchSupplier.get(i);
        if (magnificationModeSwitch != null) {
            magnificationModeSwitch.lambda$new$2();
        }
    }

    /* access modifiers changed from: package-private */
    public void onConfigurationChanged(int i) {
        this.mSwitchSupplier.forEach(new ModeSwitchesController$$ExternalSyntheticLambda0(i));
    }

    private static class SwitchSupplier extends DisplayIdIndexSupplier<MagnificationModeSwitch> {
        private final Context mContext;

        SwitchSupplier(Context context, DisplayManager displayManager) {
            super(displayManager);
            this.mContext = context;
        }

        /* access modifiers changed from: protected */
        public MagnificationModeSwitch createInstance(Display display) {
            return new MagnificationModeSwitch(this.mContext.createWindowContext(display, 2039, (Bundle) null));
        }
    }
}
