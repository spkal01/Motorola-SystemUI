package com.android.keyguard;

import android.content.res.ColorStateList;
import com.android.systemui.statusbar.policy.ConfigurationController;
import com.android.systemui.util.ViewController;

public class KeyguardMessageAreaController extends ViewController<KeyguardMessageArea> {
    private final ConfigurationController mConfigurationController;
    private ConfigurationController.ConfigurationListener mConfigurationListener;
    private KeyguardUpdateMonitorCallback mInfoCallback;
    private final KeyguardUpdateMonitor mKeyguardUpdateMonitor;

    private KeyguardMessageAreaController(KeyguardMessageArea keyguardMessageArea, KeyguardUpdateMonitor keyguardUpdateMonitor, ConfigurationController configurationController) {
        super(keyguardMessageArea);
        this.mInfoCallback = new KeyguardUpdateMonitorCallback() {
            public void onFinishedGoingToSleep(int i) {
                ((KeyguardMessageArea) KeyguardMessageAreaController.this.mView).setSelected(false);
            }

            public void onStartedWakingUp() {
                ((KeyguardMessageArea) KeyguardMessageAreaController.this.mView).setSelected(true);
            }

            public void onKeyguardBouncerChanged(boolean z) {
                ((KeyguardMessageArea) KeyguardMessageAreaController.this.mView).setBouncerVisible(z);
                ((KeyguardMessageArea) KeyguardMessageAreaController.this.mView).update();
            }
        };
        this.mConfigurationListener = new ConfigurationController.ConfigurationListener() {
            public void onThemeChanged() {
                ((KeyguardMessageArea) KeyguardMessageAreaController.this.mView).onThemeChanged();
            }

            public void onDensityOrFontScaleChanged() {
                ((KeyguardMessageArea) KeyguardMessageAreaController.this.mView).onDensityOrFontScaleChanged();
            }
        };
        this.mKeyguardUpdateMonitor = keyguardUpdateMonitor;
        this.mConfigurationController = configurationController;
    }

    /* access modifiers changed from: protected */
    public void onViewAttached() {
        this.mConfigurationController.addCallback(this.mConfigurationListener);
        this.mKeyguardUpdateMonitor.registerCallback(this.mInfoCallback);
        ((KeyguardMessageArea) this.mView).setSelected(this.mKeyguardUpdateMonitor.isDeviceInteractive());
        ((KeyguardMessageArea) this.mView).onThemeChanged();
    }

    /* access modifiers changed from: protected */
    public void onViewDetached() {
        this.mConfigurationController.removeCallback(this.mConfigurationListener);
        this.mKeyguardUpdateMonitor.removeCallback(this.mInfoCallback);
    }

    public void setAltBouncerShowing(boolean z) {
        ((KeyguardMessageArea) this.mView).setAltBouncerShowing(z);
    }

    public void setMessage(CharSequence charSequence) {
        ((KeyguardMessageArea) this.mView).setMessage(charSequence);
    }

    public void setMessage(int i) {
        ((KeyguardMessageArea) this.mView).setMessage(i);
    }

    public void setNextMessageColor(ColorStateList colorStateList) {
        ((KeyguardMessageArea) this.mView).setNextMessageColor(colorStateList);
    }

    public void reloadColors() {
        ((KeyguardMessageArea) this.mView).reloadColor();
    }

    public static class Factory {
        private final ConfigurationController mConfigurationController;
        private final KeyguardUpdateMonitor mKeyguardUpdateMonitor;

        public Factory(KeyguardUpdateMonitor keyguardUpdateMonitor, ConfigurationController configurationController) {
            this.mKeyguardUpdateMonitor = keyguardUpdateMonitor;
            this.mConfigurationController = configurationController;
        }

        public KeyguardMessageAreaController create(KeyguardMessageArea keyguardMessageArea) {
            return new KeyguardMessageAreaController(keyguardMessageArea, this.mKeyguardUpdateMonitor, this.mConfigurationController);
        }
    }

    public void restoreMessageArea() {
        ((KeyguardMessageArea) this.mView).setTranslationY(0.0f);
        ((KeyguardMessageArea) this.mView).setAlpha(1.0f);
    }
}
