package com.android.systemui.screenrecord;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.CountDownTimer;
import android.util.Log;
import com.android.internal.annotations.VisibleForTesting;
import com.android.systemui.broadcast.BroadcastDispatcher;
import com.android.systemui.statusbar.policy.CallbackController;
import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;

public class RecordingController implements CallbackController<RecordingStateChangeCallback> {
    private BroadcastDispatcher mBroadcastDispatcher;
    private CountDownTimer mCountDownTimer = null;
    private boolean mIsRecording;
    private boolean mIsStarting;
    /* access modifiers changed from: private */
    public CopyOnWriteArrayList<RecordingStateChangeCallback> mListeners = new CopyOnWriteArrayList<>();
    private ScreenMediaRecorder mRecorder;
    private PendingIntent mStartIntent;
    @VisibleForTesting
    protected final BroadcastReceiver mStateChangeReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            if (intent != null && "com.android.systemui.screenrecord.UPDATE_STATE".equals(intent.getAction())) {
                if (intent.hasExtra("extra_state")) {
                    RecordingController.this.updateState(intent.getBooleanExtra("extra_state", false));
                    return;
                }
                Log.e("Recording_Controller", "Received update intent with no state");
            }
        }
    };
    private PendingIntent mStopIntent;
    @VisibleForTesting
    protected final BroadcastReceiver mUserChangeReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            RecordingController.this.stopRecording();
        }
    };

    public interface RecordingStateChangeCallback {
        void onCountdown(long j) {
        }

        void onCountdownCancel(boolean z) {
        }

        void onCountdownEnd() {
        }

        void onRecordingEnd() {
        }

        void onRecordingStart() {
        }
    }

    public RecordingController(BroadcastDispatcher broadcastDispatcher) {
        this.mBroadcastDispatcher = broadcastDispatcher;
    }

    public Intent getPromptIntent() {
        ComponentName componentName = new ComponentName("com.android.systemui", "com.android.systemui.screenrecord.ScreenRecordDialog");
        Intent intent = new Intent();
        intent.setComponent(componentName);
        intent.addFlags(268435456);
        return intent;
    }

    public void startCountdown(long j, long j2, PendingIntent pendingIntent, PendingIntent pendingIntent2) {
        this.mIsStarting = true;
        this.mStartIntent = pendingIntent;
        this.mStopIntent = pendingIntent2;
        C12753 r1 = new CountDownTimer(j, j2) {
            public void onTick(long j) {
                Iterator it = RecordingController.this.mListeners.iterator();
                while (it.hasNext()) {
                    ((RecordingStateChangeCallback) it.next()).onCountdown(j);
                }
            }

            public void onFinish() {
                Log.d("Recording_Controller", "=== startRecording ===");
                RecordingController.this.startRecording();
            }
        };
        this.mCountDownTimer = r1;
        r1.start();
    }

    public synchronized void cancelCountdown(boolean z) {
        Log.d("Recording_Controller", "Cancel Countdown!");
        CountDownTimer countDownTimer = this.mCountDownTimer;
        if (countDownTimer != null) {
            countDownTimer.cancel();
        } else {
            Log.e("Recording_Controller", "Timer was null");
        }
        this.mIsStarting = false;
        Iterator<RecordingStateChangeCallback> it = this.mListeners.iterator();
        while (it.hasNext()) {
            it.next().onCountdownCancel(z);
        }
        if (z) {
            try {
                this.mStopIntent.send();
            } catch (PendingIntent.CanceledException e) {
                Log.e("Recording_Controller", "Error stopping: " + e.getMessage());
            }
        }
        return;
    }

    public synchronized boolean isRecording() {
        return this.mIsRecording;
    }

    public synchronized void startRecording() {
        this.mIsStarting = false;
        this.mIsRecording = true;
        Iterator<RecordingStateChangeCallback> it = this.mListeners.iterator();
        while (it.hasNext()) {
            it.next().onCountdownEnd();
        }
        try {
            this.mStartIntent.send();
            Log.d("Recording_Controller", "sent start intent");
        } catch (PendingIntent.CanceledException e) {
            Log.e("Recording_Controller", "Pending intent was cancelled: " + e.getMessage());
        }
    }

    public synchronized void stopRecording() {
        if (this.mIsRecording) {
            Iterator<RecordingStateChangeCallback> it = this.mListeners.iterator();
            while (it.hasNext()) {
                it.next().onRecordingEnd();
            }
            try {
                this.mStopIntent.send();
                Log.d("Recording_Controller", "sent stop intent");
            } catch (PendingIntent.CanceledException e) {
                Log.e("Recording_Controller", "Error stopping: " + e.getMessage());
            }
        } else {
            return;
        }
        return;
    }

    public synchronized void forceStop() {
        this.mIsStarting = false;
        this.mIsRecording = false;
        CountDownTimer countDownTimer = this.mCountDownTimer;
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        Iterator<RecordingStateChangeCallback> it = this.mListeners.iterator();
        while (it.hasNext()) {
            it.next().onRecordingEnd();
        }
    }

    public synchronized void updateState(boolean z) {
        if (this.mIsRecording != z) {
            Log.d("Recording_Controller", "updateState mIsRecording=" + this.mIsRecording + ";isRecording=" + z);
            this.mIsRecording = z;
            Iterator<RecordingStateChangeCallback> it = this.mListeners.iterator();
            while (it.hasNext()) {
                RecordingStateChangeCallback next = it.next();
                if (z) {
                    next.onRecordingStart();
                } else {
                    next.onRecordingEnd();
                }
            }
        }
    }

    public synchronized void addCallback(RecordingStateChangeCallback recordingStateChangeCallback) {
        this.mListeners.add(recordingStateChangeCallback);
    }

    public synchronized void removeCallback(RecordingStateChangeCallback recordingStateChangeCallback) {
        this.mListeners.remove(recordingStateChangeCallback);
    }

    public void setRecorder(ScreenMediaRecorder screenMediaRecorder) {
        this.mRecorder = screenMediaRecorder;
    }

    public boolean pause() {
        ScreenMediaRecorder screenMediaRecorder;
        if (!this.mIsRecording || (screenMediaRecorder = this.mRecorder) == null) {
            return false;
        }
        return screenMediaRecorder.pause();
    }

    public boolean resume() {
        ScreenMediaRecorder screenMediaRecorder;
        if (!this.mIsRecording || (screenMediaRecorder = this.mRecorder) == null) {
            return false;
        }
        return screenMediaRecorder.resume();
    }
}
