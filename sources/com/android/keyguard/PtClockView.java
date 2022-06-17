package com.android.keyguard;

import android.content.Context;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.widget.TextView;
import com.android.systemui.R$string;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;

public class PtClockView extends TextView {
    private static final CharSequence DOUBLE_LINE_FORMAT_12_HOUR = "hh\nmm";
    private static final CharSequence DOUBLE_LINE_FORMAT_24_HOUR = "HH\nmm";
    private final NumberFormat mBurmeseNf;
    private final String mBurmeseNumerals;
    private CharSequence mDescFormat;
    private CharSequence mFormat;
    private Locale mLocale;
    private final Calendar mTime;

    public PtClockView(Context context) {
        this(context, (AttributeSet) null);
    }

    public PtClockView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, -1);
    }

    public PtClockView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        NumberFormat instance = NumberFormat.getInstance(Locale.forLanguageTag("my"));
        this.mBurmeseNf = instance;
        this.mTime = Calendar.getInstance();
        this.mBurmeseNumerals = instance.format(1234567890);
        updateLocale();
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        refreshFormat();
    }

    public void refreshTime() {
        this.mTime.setTimeInMillis(System.currentTimeMillis());
        setText(DateFormat.format(this.mFormat, this.mTime));
        setContentDescription(DateFormat.format(this.mDescFormat, this.mTime));
    }

    public void onTimeZoneChanged(TimeZone timeZone) {
        this.mTime.setTimeZone(timeZone);
        refreshFormat();
    }

    public void refreshFormat() {
        if (DateFormat.is24HourFormat(getContext())) {
            this.mFormat = DOUBLE_LINE_FORMAT_24_HOUR;
            this.mDescFormat = DateFormat.getBestDateTimePattern(Locale.getDefault(), getResources().getString(R$string.clock_24hr_format));
        } else {
            this.mFormat = DOUBLE_LINE_FORMAT_12_HOUR;
            this.mDescFormat = DateFormat.getBestDateTimePattern(Locale.getDefault(), getResources().getString(R$string.clock_12hr_format));
        }
        refreshTime();
    }

    public void updateLocale() {
        Locale locale = Locale.getDefault();
        if (!Objects.equals(locale, this.mLocale)) {
            this.mLocale = locale;
            if (NumberFormat.getInstance(locale).format(1234567890).equals(this.mBurmeseNumerals)) {
                setLineSpacing(0.0f, 1.0f);
            } else {
                setLineSpacing(0.0f, 0.92f);
            }
            refreshFormat();
        }
    }
}
