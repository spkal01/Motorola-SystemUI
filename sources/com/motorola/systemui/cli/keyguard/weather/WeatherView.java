package com.motorola.systemui.cli.keyguard.weather;

import android.content.Context;
import android.database.ContentObserver;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;
import com.android.systemui.R$dimen;
import com.android.systemui.R$string;
import com.motorola.keyguard.WeatherSource;

public class WeatherView extends TextView {
    private final Handler mHandler;
    private ContentObserver mWeatherContentObserver;
    private Uri mWeatherUri;

    public WeatherView(Context context) {
        this(context, (AttributeSet) null);
    }

    public WeatherView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, -1);
    }

    public WeatherView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mWeatherUri = Uri.parse("content://com.motorola.commandcenter.weather");
        Handler handler = new Handler(Looper.getMainLooper());
        this.mHandler = handler;
        this.mWeatherContentObserver = new ContentObserver(handler) {
            public void onChange(boolean z) {
                Log.d("WeatherView", "Weather data change, update Weather view.");
                WeatherView.this.updateWeatherInfo();
            }
        };
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        updateWeatherInfo();
        registerObserver();
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        unregisterObserver();
    }

    /* access modifiers changed from: private */
    public void updateWeatherInfo() {
        try {
            Bundle call = getContext().getContentResolver().call(this.mWeatherUri, "get_weather_data", (String) null, (Bundle) null);
            int i = call.getInt("currentTempInt");
            int i2 = call.getInt("mWeatherIcon", -1);
            if (call.getBoolean("celsius", false)) {
                i = WeatherSource.getCelsiusTemperature(i);
            }
            setText(String.format(getResources().getString(R$string.weather_temp), new Object[]{Integer.valueOf(i)}));
            Drawable weatherIconDrawable = WeatherSource.getWeatherIconDrawable(getContext(), i2);
            if (weatherIconDrawable == null) {
                setVisibility(8);
                return;
            }
            setVisibility(0);
            weatherIconDrawable.setBounds(0, 0, weatherIconDrawable.getMinimumWidth(), weatherIconDrawable.getMinimumHeight());
            setCompoundDrawablesRelative((Drawable) null, (Drawable) null, weatherIconDrawable, (Drawable) null);
            setCompoundDrawablePadding(getResources().getDimensionPixelSize(R$dimen.cli_weather_view_compound_drawable_padding));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void registerObserver() {
        try {
            getContext().getContentResolver().registerContentObserver(this.mWeatherUri, true, this.mWeatherContentObserver);
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    private void unregisterObserver() {
        getContext().getContentResolver().unregisterContentObserver(this.mWeatherContentObserver);
    }
}
