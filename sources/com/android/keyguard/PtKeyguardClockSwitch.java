package com.android.keyguard;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.RelativeLayout;
import android.widget.TextClock;
import com.android.keyguard.AnimatableClockView;
import com.android.systemui.Dependency;
import com.android.systemui.R$dimen;
import com.android.systemui.R$id;
import com.android.systemui.keyguard.ScreenLifecycle;
import com.android.systemui.statusbar.policy.ConfigurationController;
import java.io.File;
import java.util.TimeZone;

public class PtKeyguardClockSwitch extends RelativeLayout implements ConfigurationController.ConfigurationListener {
    protected TextClock mClockView;
    private KeyguardUpdateMonitorCallback mKeyguardUpdateCallback;
    /* access modifiers changed from: private */
    public PtClockView mNewClockView;
    private ScreenLifecycle mScreenLifecycle;
    private final ScreenLifecycle.Observer mScreenObserver;

    public PtKeyguardClockSwitch(Context context) {
        this(context, (AttributeSet) null);
    }

    public PtKeyguardClockSwitch(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, -1);
    }

    public PtKeyguardClockSwitch(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mKeyguardUpdateCallback = new KeyguardUpdateMonitorCallback() {
            public void onTimeChanged() {
                PtKeyguardClockSwitch.this.mClockView.refreshTime();
                PtKeyguardClockSwitch.this.refreshNewClockTime();
            }

            public void onKeyguardVisibilityChanged(boolean z) {
                if (z) {
                    PtKeyguardClockSwitch.this.mClockView.refreshTime();
                    PtKeyguardClockSwitch.this.refreshNewClockTime();
                }
            }

            public void onTimeFormatChanged(String str) {
                if (PtKeyguardClockSwitch.this.mNewClockView != null) {
                    PtKeyguardClockSwitch.this.mNewClockView.refreshFormat();
                }
            }

            public void onTimeZoneChanged(TimeZone timeZone) {
                if (PtKeyguardClockSwitch.this.mNewClockView != null) {
                    PtKeyguardClockSwitch.this.mNewClockView.onTimeZoneChanged(timeZone);
                }
            }
        };
        this.mScreenObserver = new ScreenLifecycle.Observer() {
            public void onScreenTurningOn() {
                PtKeyguardClockSwitch.this.mClockView.refreshTime();
                PtKeyguardClockSwitch.this.refreshNewClockTime();
            }
        };
        this.mScreenLifecycle = (ScreenLifecycle) Dependency.get(ScreenLifecycle.class);
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        super.onFinishInflate();
        this.mNewClockView = (PtClockView) findViewById(R$id.new_clock_view);
        TextClock textClock = (TextClock) findViewById(R$id.default_clock_view);
        this.mClockView = textClock;
        textClock.setTextSize(0, (float) getResources().getDimensionPixelSize(R$dimen.clock_simple_widget_big_font_default_size));
        updateClockFont();
        refreshFormat();
        updateClockSize();
    }

    /* access modifiers changed from: private */
    public void refreshNewClockTime() {
        Log.d("KeyguardClockSwitchSimple", "refreshNewClockTime: ");
        PtClockView ptClockView = this.mNewClockView;
        if (ptClockView != null) {
            ptClockView.refreshTime();
        }
    }

    private void updateClockFont() {
        File file = new File("/system/fonts/Newfont_Regular.ttf");
        if (file.exists()) {
            Typeface createFromFile = Typeface.createFromFile(file);
            this.mClockView.setTypeface(createFromFile);
            this.mNewClockView.setTypeface(createFromFile);
            return;
        }
        Log.e("KeyguardClockSwitchSimple", "Newfont files can not be found");
    }

    private void updateClockSize() {
        this.mClockView.setTextSize(0, ((float) (getResources().getDimensionPixelSize(R$dimen.pt_widget_big_font_size) * PtDisplayFontUtils.getScreenHeight(this.mContext.getDisplay()))) / 1080.0f);
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.mScreenLifecycle.addObserver(this.mScreenObserver);
        ((KeyguardUpdateMonitor) Dependency.get(KeyguardUpdateMonitor.class)).registerCallback(this.mKeyguardUpdateCallback);
        ((ConfigurationController) Dependency.get(ConfigurationController.class)).addCallback(this);
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.mScreenLifecycle.removeObserver(this.mScreenObserver);
        ((KeyguardUpdateMonitor) Dependency.get(KeyguardUpdateMonitor.class)).removeCallback(this.mKeyguardUpdateCallback);
        ((ConfigurationController) Dependency.get(ConfigurationController.class)).removeCallback(this);
    }

    private void refreshFormat() {
        AnimatableClockView.Patterns.update(this.mContext);
        this.mClockView.setFormat12Hour(AnimatableClockView.Patterns.sClockView12);
        this.mClockView.setFormat24Hour(AnimatableClockView.Patterns.sClockView24);
    }

    public void onLocaleListChanged() {
        Log.i("KeyguardClockSwitchSimple", "Locale change, time format changed.");
        refreshFormat();
        PtClockView ptClockView = this.mNewClockView;
        if (ptClockView != null) {
            ptClockView.updateLocale();
        }
    }
}
