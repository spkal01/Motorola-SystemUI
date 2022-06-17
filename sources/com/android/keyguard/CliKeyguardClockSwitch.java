package com.android.keyguard;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextClock;
import com.android.keyguard.AnimatableClockView;
import com.android.systemui.Dependency;
import com.android.systemui.R$dimen;
import com.android.systemui.R$id;
import com.android.systemui.keyguard.ScreenLifecycle;
import com.android.systemui.statusbar.phone.CliStatusBar;
import com.android.systemui.statusbar.policy.ConfigurationController;
import com.android.systemui.statusbar.policy.KeyguardStateController;
import java.io.File;

public class CliKeyguardClockSwitch extends RelativeLayout implements ConfigurationController.ConfigurationListener, KeyguardStateController.Callback {
    /* access modifiers changed from: private */
    public TextClock mClockView;
    private CliStatusBar.DozeUpdateTimeCallback mDozeUpdateTimeCallback;
    private KeyguardStateController mKeyguardMonitor;
    private KeyguardUpdateMonitorCallback mKeyguardUpdateCallback;
    private ImageView mLockIcon;
    private ScreenLifecycle mScreenLifecycle;
    private final ScreenLifecycle.Observer mScreenObserver;
    private KeyguardUpdateMonitor mUpdateMonitor;

    public CliStatusBar.DozeUpdateTimeCallback getDozeUpdateTimeCallback() {
        return this.mDozeUpdateTimeCallback;
    }

    public CliKeyguardClockSwitch(Context context) {
        this(context, (AttributeSet) null);
    }

    public CliKeyguardClockSwitch(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, -1);
    }

    public CliKeyguardClockSwitch(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mDozeUpdateTimeCallback = new CliStatusBar.DozeUpdateTimeCallback() {
            public void dozeTimeTick() {
                if (CliKeyguardClockSwitch.this.mClockView != null) {
                    CliKeyguardClockSwitch.this.mClockView.refreshTime();
                }
            }
        };
        this.mKeyguardUpdateCallback = new KeyguardUpdateMonitorCallback() {
            public void onTimeChanged() {
                CliKeyguardClockSwitch.this.mClockView.refreshTime();
            }

            public void onKeyguardVisibilityChanged(boolean z) {
                if (z) {
                    CliKeyguardClockSwitch.this.mClockView.refreshTime();
                }
            }
        };
        this.mScreenObserver = new ScreenLifecycle.Observer() {
            public void onScreenTurningOn() {
                CliKeyguardClockSwitch.this.mClockView.refreshTime();
            }
        };
        this.mScreenLifecycle = (ScreenLifecycle) Dependency.get(ScreenLifecycle.class);
        this.mKeyguardMonitor = (KeyguardStateController) Dependency.get(KeyguardStateController.class);
        this.mUpdateMonitor = (KeyguardUpdateMonitor) Dependency.get(KeyguardUpdateMonitor.class);
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        super.onFinishInflate();
        this.mClockView = (TextClock) findViewById(R$id.default_clock_view);
        this.mLockIcon = (ImageView) findViewById(R$id.cli_lock_icon);
        this.mClockView.setTextSize(0, (float) getResources().getDimensionPixelSize(R$dimen.cli_widget_big_font_size));
        updateClockFont();
        refreshFormat();
        updateLockIconVisibility();
    }

    private void updateClockFont() {
        File file = new File("/system/fonts/Newfont_Light.ttf");
        if (file.exists()) {
            this.mClockView.setTypeface(Typeface.createFromFile(file));
            return;
        }
        Log.e("CliKeyguardClockSwitch", "Newfont files can not be found");
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.mScreenLifecycle.addObserver(this.mScreenObserver);
        this.mUpdateMonitor.registerCallback(this.mKeyguardUpdateCallback);
        ((ConfigurationController) Dependency.get(ConfigurationController.class)).addCallback(this);
        this.mKeyguardMonitor.addCallback(this);
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.mScreenLifecycle.removeObserver(this.mScreenObserver);
        this.mUpdateMonitor.removeCallback(this.mKeyguardUpdateCallback);
        ((ConfigurationController) Dependency.get(ConfigurationController.class)).removeCallback(this);
        this.mKeyguardMonitor.removeCallback(this);
    }

    private void refreshFormat() {
        AnimatableClockView.Patterns.update(this.mContext);
        this.mClockView.setFormat12Hour(AnimatableClockView.Patterns.sClockView12);
        this.mClockView.setFormat24Hour(AnimatableClockView.Patterns.sClockView24);
    }

    public void onLocaleListChanged() {
        Log.i("CliKeyguardClockSwitch", "Locale change, time format changed.");
        refreshFormat();
    }

    public void onKeyguardShowingChanged() {
        updateLockIconVisibility();
    }

    private void updateLockIconVisibility() {
        KeyguardStateController keyguardStateController = this.mKeyguardMonitor;
        if (keyguardStateController == null || !keyguardStateController.isMethodSecure() || !this.mKeyguardMonitor.isShowing() || this.mUpdateMonitor.getUserCanSkipBouncer(KeyguardUpdateMonitor.getCurrentUser())) {
            this.mLockIcon.setVisibility(4);
        } else {
            this.mLockIcon.setVisibility(0);
        }
    }
}
