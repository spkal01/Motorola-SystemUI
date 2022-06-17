package com.android.systemui.moto;

import android.app.ActivityManager;
import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.telephony.ServiceState;
import android.telephony.TelephonyManager;
import android.util.Log;
import com.motorola.android.provider.MotorolaSettings;
import com.motorola.android.telephony.MotoExtTelephonyManager;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class VzwEri {
    /* access modifiers changed from: private */
    public static final boolean DBG = Log.isLoggable("VzwEri", 3);
    /* access modifiers changed from: private */
    public Context mContext;
    private long mLastRoamIndChangeTime;
    /* access modifiers changed from: private */
    public final Object mLock = new Object();
    private LossOfServiceDelayTask mLossOfServiceDelayTask = new LossOfServiceDelayTask();
    /* access modifiers changed from: private */
    public MediaPlayer mMediaPlayer = null;
    private boolean mPlayEriAlertAfterBoot = false;
    /* access modifiers changed from: private */
    public Uri mPreAlertUri = null;

    public VzwEri(Context context) {
        this.mContext = context;
    }

    public void updateEri(ServiceState serviceState, ServiceState serviceState2, TelephonyManager telephonyManager, Handler handler) {
        if (!needPlayEriAlert(serviceState, serviceState2, telephonyManager, handler)) {
            return;
        }
        if (!SystemProperties.get("sys.boot_completed", "0").equals("1")) {
            if (DBG) {
                Log.d("VzwEri", "boot not completed, do not play ERI alert sound.");
            }
            this.mPlayEriAlertAfterBoot = true;
            return;
        }
        if (DBG) {
            Log.d("VzwEri", "onServiceStateChanged: play ERI alert sound.");
        }
        try {
            playEriAlert();
        } catch (Exception e) {
            Log.e("VzwEri", "playEriAlert() caught ", e);
        }
        if (this.mPlayEriAlertAfterBoot && this.mPreAlertUri != null) {
            this.mPlayEriAlertAfterBoot = false;
        }
    }

    public void playEriAlertAfterBoot() {
        if (DBG) {
            Log.d("VzwEri", "Need play ERI alert after boot: " + this.mPlayEriAlertAfterBoot);
        }
        if (this.mPlayEriAlertAfterBoot) {
            this.mPlayEriAlertAfterBoot = false;
            try {
                playEriAlert();
            } catch (Exception e) {
                Log.e("VzwEri", "playEriAlert() caught ", e);
            }
        }
    }

    /* access modifiers changed from: private */
    public void stopMediaPlayer() {
        MediaPlayer mediaPlayer = this.mMediaPlayer;
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            this.mMediaPlayer.release();
            this.mMediaPlayer = null;
        }
    }

    private final void playEriAlert() throws IOException {
        Uri cdmaEriAlertUri = new MotoExtTelephonyManager(this.mContext).getCdmaEriAlertUri();
        Uri uri = this.mPreAlertUri;
        if (uri == null || cdmaEriAlertUri == null || uri.compareTo(cdmaEriAlertUri) != 0) {
            this.mPreAlertUri = cdmaEriAlertUri;
            if (cdmaEriAlertUri != null) {
                MediaPlayer mediaPlayer = new MediaPlayer();
                this.mMediaPlayer = mediaPlayer;
                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        if (VzwEri.DBG) {
                            Log.d("VzwEri", "playEriAlert() onCompletion");
                        }
                        VzwEri.this.stopMediaPlayer();
                    }
                });
                this.mMediaPlayer.setDataSource(this.mContext, cdmaEriAlertUri);
                this.mMediaPlayer.setAudioStreamType(5);
                this.mMediaPlayer.prepare();
                if (DBG) {
                    Log.d("VzwEri", "Call mediaPlayer.start(), Uri: " + cdmaEriAlertUri.toString());
                }
                this.mMediaPlayer.start();
            }
        }
    }

    private final boolean needPlayEriAlert(ServiceState serviceState, ServiceState serviceState2, TelephonyManager telephonyManager, Handler handler) {
        if (MotorolaSettings.Secure.getInt(this.mContext.getContentResolver(), "eri_alert_sounds", 1) == 0) {
            if (DBG) {
                Log.d("VzwEri", "ERI alert sound is disabled.");
            }
            return false;
        } else if (telephonyManager.getCallState() != 0) {
            if (DBG) {
                Log.d("VzwEri", "In call state, not to play ERI alert sound.");
            }
            return false;
        } else {
            if (!(serviceState2 == null || serviceState == null)) {
                int state = serviceState2.getState();
                int state2 = serviceState.getState();
                if (this.mLossOfServiceDelayTask.started && state == 1 && state2 == 0) {
                    if (DBG) {
                        Log.d("VzwEri", "In Service again, NOT play Loss Of Service alert");
                    }
                    LossOfServiceDelayTask lossOfServiceDelayTask = this.mLossOfServiceDelayTask;
                    lossOfServiceDelayTask.started = false;
                    handler.removeCallbacks(lossOfServiceDelayTask);
                }
                if ((state == 3 && state2 == 1) || (state == 1 && state2 == 3)) {
                    if (DBG) {
                        Log.d("VzwEri", "Service State changed: " + state + " ==> " + state2);
                    }
                    return false;
                }
                ActivityManager activityManager = (ActivityManager) this.mContext.getSystemService("activity");
                if (DBG) {
                    Log.d("VzwEri", "===== preState: " + state + "   newState: " + state2);
                }
                try {
                    List<ActivityManager.RunningTaskInfo> runningTasks = activityManager.getRunningTasks(2);
                    if (runningTasks != null) {
                        for (int i = 0; i < runningTasks.size(); i++) {
                            ActivityManager.RunningTaskInfo runningTaskInfo = runningTasks.get(i);
                            boolean z = DBG;
                            if (z) {
                                Log.d("VzwEri", "== baseActivity: " + runningTaskInfo.baseActivity.toString());
                            }
                            if (state == 3 || state == 1) {
                                if (!runningTaskInfo.baseActivity.getClassName().endsWith("AirplaneModeHandler")) {
                                    if (runningTaskInfo.baseActivity.getClassName().endsWith("EmergencyCallHandler")) {
                                    }
                                }
                                if (z) {
                                    Log.d("VzwEri", "Calling App will make a call, do not play ERI alert.");
                                }
                                return false;
                            }
                        }
                    }
                } catch (Exception e) {
                    if (DBG) {
                        Log.d("VzwEri", "catch exception: " + e);
                    }
                }
                if (serviceState2.getCdmaRoamingIndicator() == serviceState.getCdmaRoamingIndicator() && serviceState2.getCdmaDefaultRoamingIndicator() == serviceState.getCdmaDefaultRoamingIndicator()) {
                    if (state != 0 || state2 != 0 || serviceState2.getCdmaSystemId() == serviceState.getCdmaSystemId() || serviceState2.getCdmaSystemId() == 0) {
                        if (DBG) {
                            Log.d("VzwEri", "Roaming indicator does not change, not to play ERI alert sound.");
                        }
                        return false;
                    }
                    long uptimeMillis = SystemClock.uptimeMillis() - this.mLastRoamIndChangeTime;
                    long parseInt = (long) Integer.parseInt(SystemProperties.get("ro.mot.eri.sidalert.delay", "1000"));
                    if (uptimeMillis < parseInt) {
                        if (DBG) {
                            Log.d("VzwEri", "SystemId:" + serviceState2.getCdmaSystemId() + "->" + serviceState.getCdmaSystemId() + " Delay:" + uptimeMillis + "<" + parseInt + ", NOT play ERI alert sound.");
                        }
                        return false;
                    }
                    if (DBG) {
                        Log.d("VzwEri", "SystemId:" + serviceState2.getCdmaSystemId() + "->" + serviceState.getCdmaSystemId() + " Delay:" + uptimeMillis + ">" + parseInt + ", play ERI alert sound.");
                    }
                    return true;
                } else if (state == 0 && state2 == 1) {
                    long parseInt2 = (long) Integer.parseInt(SystemProperties.get("ro.mot.eri.losalert.delay", "2000"));
                    if (DBG) {
                        Log.d("VzwEri", "Loss Of Service alert delayed:" + parseInt2);
                    }
                    LossOfServiceDelayTask lossOfServiceDelayTask2 = this.mLossOfServiceDelayTask;
                    lossOfServiceDelayTask2.started = true;
                    handler.postDelayed(lossOfServiceDelayTask2, parseInt2);
                    return false;
                } else {
                    this.mLastRoamIndChangeTime = SystemClock.uptimeMillis();
                    if (DBG) {
                        Log.d("VzwEri", "Roaming indicator changed, play ERI alert sound.");
                    }
                }
            }
            return true;
        }
    }

    private class LossOfServiceDelayTask implements Runnable {
        boolean started;

        private LossOfServiceDelayTask() {
        }

        public void run() {
            synchronized (VzwEri.this.mLock) {
                Uri unused = VzwEri.this.mPreAlertUri = null;
                try {
                    MediaPlayer unused2 = VzwEri.this.mMediaPlayer = new MediaPlayer();
                    VzwEri.this.mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        public void onCompletion(MediaPlayer mediaPlayer) {
                            if (VzwEri.DBG) {
                                Log.d("VzwEri", "playEriAlert() onCompletion");
                            }
                            VzwEri.this.stopMediaPlayer();
                        }
                    });
                    VzwEri.this.mMediaPlayer.setDataSource(VzwEri.this.mContext, Uri.fromFile(new File("/system/product/media/audio/eri/LossofService.ogg")));
                    VzwEri.this.mMediaPlayer.setAudioStreamType(5);
                    VzwEri.this.mMediaPlayer.prepare();
                    if (VzwEri.DBG) {
                        Log.d("VzwEri", "LossOfService timeout, start alert");
                    }
                    VzwEri.this.mMediaPlayer.start();
                } catch (Exception e) {
                    Log.e("VzwEri", "Exception while attempting to play loss of service alert", e);
                }
                this.started = false;
            }
        }
    }
}
