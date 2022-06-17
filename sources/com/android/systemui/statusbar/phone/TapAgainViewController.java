package com.android.systemui.statusbar.phone;

import com.android.internal.annotations.VisibleForTesting;
import com.android.systemui.statusbar.policy.ConfigurationController;
import com.android.systemui.util.ViewController;
import com.android.systemui.util.concurrency.DelayableExecutor;

public class TapAgainViewController extends ViewController<TapAgainView> {
    private final ConfigurationController mConfigurationController;
    @VisibleForTesting
    final ConfigurationController.ConfigurationListener mConfigurationListener = new ConfigurationController.ConfigurationListener() {
        public void onOverlayChanged() {
            ((TapAgainView) TapAgainViewController.this.mView).updateColor();
        }

        public void onUiModeChanged() {
            ((TapAgainView) TapAgainViewController.this.mView).updateColor();
        }

        public void onThemeChanged() {
            ((TapAgainView) TapAgainViewController.this.mView).updateColor();
        }
    };
    private final DelayableExecutor mDelayableExecutor;
    private final long mDoubleTapTimeMs;
    private Runnable mHideCanceler;

    protected TapAgainViewController(TapAgainView tapAgainView, DelayableExecutor delayableExecutor, ConfigurationController configurationController, long j) {
        super(tapAgainView);
        this.mDelayableExecutor = delayableExecutor;
        this.mConfigurationController = configurationController;
        this.mDoubleTapTimeMs = j;
    }

    /* access modifiers changed from: protected */
    public void onViewAttached() {
        this.mConfigurationController.addCallback(this.mConfigurationListener);
    }

    /* access modifiers changed from: protected */
    public void onViewDetached() {
        this.mConfigurationController.removeCallback(this.mConfigurationListener);
    }

    public void show() {
        Runnable runnable = this.mHideCanceler;
        if (runnable != null) {
            runnable.run();
        }
        ((TapAgainView) this.mView).animateIn();
        this.mHideCanceler = this.mDelayableExecutor.executeDelayed(new TapAgainViewController$$ExternalSyntheticLambda0(this), this.mDoubleTapTimeMs);
    }

    public void hide() {
        this.mHideCanceler = null;
        ((TapAgainView) this.mView).animateOut();
    }
}
