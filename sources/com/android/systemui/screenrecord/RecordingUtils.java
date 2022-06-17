package com.android.systemui.screenrecord;

import android.content.Context;
import android.media.AudioManager;
import android.media.MicrophoneInfo;
import android.os.Environment;
import android.os.StatFs;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class RecordingUtils {
    public static long getFileLength(File file) {
        if (file == null) {
            return -1;
        }
        long j = 0;
        if (!file.isDirectory()) {
            return file.length();
        }
        for (File fileLength : file.listFiles()) {
            j += getFileLength(fileLength);
        }
        return j;
    }

    public static boolean isRecordingFileExist(Context context) {
        File recordingDir = getRecordingDir(context);
        if (!recordingDir.exists() || recordingDir.list().length <= 0) {
            return false;
        }
        return true;
    }

    public static File getRecordingDir(Context context) {
        return new File(context.getCacheDir(), "recording");
    }

    public static boolean deleteFile(File file) {
        if (file == null) {
            return false;
        }
        if (!file.isDirectory()) {
            return file.delete();
        }
        boolean z = true;
        for (File delete : file.listFiles()) {
            z &= delete.delete();
        }
        return z;
    }

    public static long getFreeMem() {
        StatFs statFs = new StatFs(Environment.getExternalStorageDirectory().getAbsolutePath());
        return ((long) statFs.getAvailableBlocks()) * ((long) statFs.getBlockSize());
    }

    public static boolean isRecordMemEnough() {
        return getFreeMem() > 209715200;
    }

    public static boolean isRecordingMemEnough(Context context) {
        return getFreeMem() > getFileLength(getRecordingDir(context)) + 209715200;
    }

    public static boolean isLowResolutionDevice(Context context) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((WindowManager) context.getSystemService("window")).getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.widthPixels <= 720 || displayMetrics.heightPixels <= 720;
    }

    public static void updateAudioParameter(Context context, boolean z) {
        List<MicrophoneInfo> list;
        AudioManager audioManager = (AudioManager) context.getSystemService("audio");
        try {
            list = audioManager.getMicrophones();
        } catch (IOException e) {
            Log.e("Recording_Utils", "getMicrophones failed=" + e);
            list = null;
        }
        if (list == null) {
            Log.e("Recording_Utils", "updateAudioParameter failed!");
        } else if (list.size() > 1) {
            String str = "moto_screen_record=on";
            audioManager.setParameters(z ? str : "moto_screen_record=off");
            audioManager.setParameters(z ? "mono_speaker=right" : "mono_speaker=default");
            StringBuilder sb = new StringBuilder();
            sb.append("turn off top speaker and move mic to top=");
            if (!z) {
                str = "moto_screen_record=off";
            }
            sb.append(str);
            Log.d("Recording_Utils", sb.toString());
        } else {
            int streamMaxVolume = audioManager.getStreamMaxVolume(3);
            audioManager.limitMusicVolumeForSpeaker(z ? 9 : streamMaxVolume);
            StringBuilder sb2 = new StringBuilder();
            sb2.append("limit volume=");
            if (z) {
                streamMaxVolume = 9;
            }
            sb2.append(streamMaxVolume);
            Log.d("Recording_Utils", sb2.toString());
        }
    }
}
