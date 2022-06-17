package com.android.systemui.screenrecord;

import android.content.Context;
import com.motorola.android.provider.MotorolaSettings;

public class RecordingSettings {
    public static boolean sCameraStatus = false;

    public static boolean getTouchPoint(Context context) {
        return MotorolaSettings.Secure.getInt(context.getContentResolver(), "record_touch_point", 0) != 0;
    }

    public static int getViewFinderSize(Context context) {
        return MotorolaSettings.Secure.getInt(context.getContentResolver(), "record_viewfinder_size", 0);
    }

    public static int getResolution(Context context) {
        return MotorolaSettings.Secure.getInt(context.getContentResolver(), "record_resolution", RecordingUtils.isLowResolutionDevice(context) ? 720 : 1080);
    }

    public static int getTimeLimit(Context context) {
        return MotorolaSettings.Secure.getInt(context.getContentResolver(), "record_time_limit", 0);
    }

    public static int getFileLimit(Context context) {
        return MotorolaSettings.Secure.getInt(context.getContentResolver(), "record_size_limit", 0);
    }

    public static int getAudioResource(Context context) {
        return MotorolaSettings.Secure.getInt(context.getContentResolver(), "record_audio_resource", 0);
    }

    public static void setAudioResource(Context context, int i) {
        MotorolaSettings.Secure.putInt(context.getContentResolver(), "record_audio_resource", i);
    }

    public static int getScreenRecordingStatus(Context context) {
        return MotorolaSettings.Secure.getInt(context.getContentResolver(), "screen_recording_status", 0);
    }

    public static void setScreenRecordingStatus(Context context, int i) {
        MotorolaSettings.Secure.putInt(context.getContentResolver(), "screen_recording_status", i);
    }
}
