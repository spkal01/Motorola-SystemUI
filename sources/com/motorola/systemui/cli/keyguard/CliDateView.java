package com.motorola.systemui.cli.keyguard;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.icu.text.DateFormat;
import android.icu.text.DisplayContext;
import android.os.Handler;
import android.util.AttributeSet;
import android.widget.TextView;
import com.android.keyguard.KeyguardUpdateMonitor;
import com.android.keyguard.KeyguardUpdateMonitorCallback;
import com.android.systemui.Dependency;
import com.android.systemui.R$string;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class CliDateView extends TextView {
    private final Date mCurrentTime;
    final BroadcastReceiver mDateChangeReceiver;
    private DateFormat mDateFormat;
    private String mDatePattern;
    final KeyguardUpdateMonitorCallback mKeyguardUpdateMonitorCallback;
    private String mLastText;
    private Locale mLocale;
    private KeyguardUpdateMonitor mUpdateMonitor;

    public CliDateView(Context context) {
        this(context, (AttributeSet) null);
    }

    public CliDateView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, -1);
    }

    public CliDateView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mCurrentTime = new Date();
        this.mDateChangeReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if ("android.intent.action.DATE_CHANGED".equals(action)) {
                    synchronized (this) {
                        CliDateView.this.updateClockLocked();
                    }
                } else if ("android.intent.action.LOCALE_CHANGED".equals(action)) {
                    synchronized (this) {
                        CliDateView.this.cleanDateFormatLocked();
                    }
                }
            }
        };
        this.mKeyguardUpdateMonitorCallback = new KeyguardUpdateMonitorCallback() {
            public void onTimeChanged() {
                synchronized (this) {
                    CliDateView.this.updateClockLocked();
                }
            }

            public void onTimeZoneChanged(TimeZone timeZone) {
                synchronized (this) {
                    CliDateView.this.cleanDateFormatLocked();
                }
            }
        };
        this.mDatePattern = getResources().getString(R$string.system_ui_aod_date_pattern);
        this.mUpdateMonitor = (KeyguardUpdateMonitor) Dependency.get(KeyguardUpdateMonitor.class);
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        registerDateUpdate();
        updateClockLocked();
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        unregisterDateUpdate();
    }

    /* access modifiers changed from: protected */
    public void registerDateUpdate() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.DATE_CHANGED");
        intentFilter.addAction("android.intent.action.LOCALE_CHANGED");
        getContext().registerReceiver(this.mDateChangeReceiver, intentFilter, (String) null, (Handler) null);
        this.mUpdateMonitor.registerCallback(this.mKeyguardUpdateMonitorCallback);
    }

    private void unregisterDateUpdate() {
        getContext().unregisterReceiver(this.mDateChangeReceiver);
        this.mUpdateMonitor.removeCallback(this.mKeyguardUpdateMonitorCallback);
    }

    /* access modifiers changed from: private */
    public void updateClockLocked() {
        String formattedDateLocked = getFormattedDateLocked();
        if (!formattedDateLocked.equals(this.mLastText)) {
            this.mLastText = formattedDateLocked;
            setText(formattedDateLocked);
        }
    }

    private String getFormattedDateLocked() {
        if (this.mDateFormat == null) {
            DateFormat instanceForSkeleton = DateFormat.getInstanceForSkeleton(this.mDatePattern, Locale.getDefault());
            instanceForSkeleton.setContext(DisplayContext.CAPITALIZATION_FOR_STANDALONE);
            this.mDateFormat = instanceForSkeleton;
        }
        this.mCurrentTime.setTime(System.currentTimeMillis());
        return this.mDateFormat.format(this.mCurrentTime);
    }

    /* access modifiers changed from: package-private */
    public void cleanDateFormatLocked() {
        this.mDateFormat = null;
    }

    /* access modifiers changed from: protected */
    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        Locale locale = getContext().getResources().getConfiguration().locale;
        if (!locale.equals(this.mLocale)) {
            this.mLocale = locale;
            updateClockLocked();
        }
    }
}
