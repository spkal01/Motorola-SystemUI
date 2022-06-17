package com.motorola.keyguard;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.util.Log;

public class WeatherSource {
    private static String getWeatherIconName(int i) {
        switch (i) {
            case 1:
            case 2:
            case 30:
            case 31:
                return "vic_sunny";
            case 3:
                return "vic_partly_cloudy";
            case 4:
            case 6:
            case 7:
            case 8:
                return "vic_cloudy";
            case 5:
            case 11:
                return "vic_fog";
            case 12:
            case 18:
            case 39:
            case 40:
                return "vic_rain";
            case 13:
            case 14:
                return "vic_partly_sunny_rain";
            case 15:
                return "vic_thunderstorms";
            case 16:
            case 17:
                return "vic_partly_sunny_thunderstorms";
            case 19:
            case 20:
            case 21:
            case 43:
            case 44:
                return "vic_snow_flurries";
            case 22:
            case 23:
                return "vic_snow";
            case 24:
            case 25:
                return "vic_sleet";
            case 26:
                return "vic_rain_ice_mix";
            case 29:
                return "vic_rain_snow_mix";
            case 32:
                return "vic_windy";
            case 33:
            case 34:
                return "vic_clear_night";
            case 35:
            case 36:
            case 37:
            case 38:
                return "vic_cloudy_night";
            case 41:
            case 42:
                return "vic_partly_night_thunerstorms";
            default:
                return null;
        }
    }

    public static Context getRemoteContext(Context context) {
        try {
            return context.createPackageContext("com.motorola.timeweatherwidget", 0);
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("WeatherSource", "Cannot found weather context", e);
            return null;
        }
    }

    public static Drawable getWeatherIconDrawable(Context context, int i) {
        int weatherIconResource = getWeatherIconResource(context, i);
        if (weatherIconResource == -999) {
            return null;
        }
        return getRemoteContext(context).getDrawable(weatherIconResource);
    }

    public static int getWeatherIconResource(Context context, int i) {
        String weatherIconName;
        Context remoteContext = getRemoteContext(context);
        if (remoteContext == null || (weatherIconName = getWeatherIconName(i)) == null) {
            return -999;
        }
        return remoteContext.getResources().getIdentifier(weatherIconName, "drawable", "com.motorola.timeweatherwidget");
    }

    public static int getCelsiusTemperature(int i) {
        return Math.round(((((float) i) - 32.0f) * 5.0f) / 9.0f);
    }
}
