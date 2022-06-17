package com.android.systemui.biometrics;

import android.content.res.Configuration;
import com.android.systemui.statusbar.policy.ConfigurationController;
import org.jetbrains.annotations.Nullable;

/* compiled from: AuthRippleController.kt */
public final class AuthRippleController$configurationChangedListener$1 implements ConfigurationController.ConfigurationListener {
    final /* synthetic */ AuthRippleController this$0;

    AuthRippleController$configurationChangedListener$1(AuthRippleController authRippleController) {
        this.this$0 = authRippleController;
    }

    public void onConfigChanged(@Nullable Configuration configuration) {
        this.this$0.updateSensorLocation();
    }

    public void onUiModeChanged() {
        this.this$0.updateRippleColor();
    }

    public void onThemeChanged() {
        this.this$0.updateRippleColor();
    }

    public void onOverlayChanged() {
        this.this$0.updateRippleColor();
    }
}
