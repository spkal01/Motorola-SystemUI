package com.android.systemui.volume;

import android.app.ActivityManager;
import android.app.NotificationManager;
import android.app.UserSwitchObserver;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.database.ContentObserver;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.AudioSystem;
import android.media.IAudioService;
import android.media.IVolumeController;
import android.media.VolumePolicy;
import android.media.session.MediaController;
import android.media.session.MediaSession;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.os.UserHandle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.provider.Settings;
import android.service.notification.Condition;
import android.service.notification.ZenModeConfig;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.Log;
import android.util.Slog;
import android.util.SparseArray;
import android.view.accessibility.AccessibilityManager;
import androidx.lifecycle.Observer;
import com.android.internal.annotations.GuardedBy;
import com.android.settingslib.volume.MediaSessions;
import com.android.settingslib.volume.Util;
import com.android.systemui.Dumpable;
import com.android.systemui.R$string;
import com.android.systemui.broadcast.BroadcastDispatcher;
import com.android.systemui.keyguard.WakefulnessLifecycle;
import com.android.systemui.moto.MotoFeature;
import com.android.systemui.p006qs.tiles.DndTile;
import com.android.systemui.plugins.VolumeDialogController;
import com.android.systemui.util.RingerModeLiveData;
import com.android.systemui.util.RingerModeTracker;
import com.android.systemui.util.concurrency.ThreadFactory;
import com.motorola.multivolume.AppVolumeState;
import com.motorola.multivolume.IMultiVolumeController;
import com.motorola.multivolume.IMultiVolumeService;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class VolumeDialogControllerImpl implements VolumeDialogController, Dumpable {
    public static final String[] HANDLE_REASONS = {"unknown", "display_safe_media_volume", "disable_safe_media_volume", "keep_safe_media_volume"};
    private static final AudioAttributes SONIFICIATION_VIBRATION_ATTRIBUTES = new AudioAttributes.Builder().setContentType(4).setUsage(13).build();
    static final ArrayMap<Integer, Integer> STREAMS;
    /* access modifiers changed from: private */
    public static final String TAG = Util.logTag(VolumeDialogControllerImpl.class);
    public final String[] CHANGE_REASONS;
    private AudioManager mAudio;
    private IAudioService mAudioService;
    protected final BroadcastDispatcher mBroadcastDispatcher;
    protected C2138C mCallbacks = new C2138C();
    /* access modifiers changed from: private */
    public final Context mContext;
    /* access modifiers changed from: private */
    public int mCurrentUserId;
    /* access modifiers changed from: private */
    public boolean mDestroyed;
    /* access modifiers changed from: private */
    public boolean mDeviceInteractive;
    private final boolean mHasVibrator;
    private long mLastToggledRingerOn;
    /* access modifiers changed from: private */
    public final MediaSessions mMediaSessions;
    protected final MediaSessionsCallbacks mMediaSessionsCallbacksW;
    private IMultiVolumeController mMultiVolumeController;
    private SparseArray<MultiVolumeServiceConnection> mMultiVolumeServiceConnections;
    private SparseArray<IMultiVolumeService> mMultiVolumeServices;
    /* access modifiers changed from: private */
    public final NotificationManager mNoMan;
    private final SettingObserver mObserver;
    private final PackageManager mPackageManager;
    private final Receiver mReceiver;
    private boolean mRelativeFeatureActive;
    private RelevantVolumeReceiver mRelevantVolumeReceiver;
    private final RingerModeObservers mRingerModeObservers;
    /* access modifiers changed from: private */
    public boolean mShowA11yStream;
    private boolean mShowDndTile;
    private boolean mShowSafetyWarning;
    private boolean mShowVolumeDialog;
    /* access modifiers changed from: private */
    public final VolumeDialogController.State mState = new VolumeDialogController.State();
    /* access modifiers changed from: private */
    public VolumeDialogController.Callbacks mUICallbacks;
    /* access modifiers changed from: private */
    public Handler mUIHandler;
    @GuardedBy({"this"})
    private UserActivityListener mUserActivityListener;
    private final UserSwitchObserver mUserSwitchCompleteObserver;
    private final Optional<Vibrator> mVibrator;
    protected final C2160VC mVolumeController;
    private VolumePolicy mVolumePolicy;
    private final WakefulnessLifecycle.Observer mWakefullnessLifecycleObserver;
    private final WakefulnessLifecycle mWakefulnessLifecycle;
    /* access modifiers changed from: private */
    public final C2161W mWorker;
    private final Looper mWorkerLooper;
    int musicMaxVolumeCalledTimes;
    boolean musicMaxVolumeMode;
    Object mvc_token;

    public interface UserActivityListener {
        void onUserActivity();
    }

    private static boolean isLogWorthy(int i) {
        return i == 0 || i == 1 || i == 2 || i == 3 || i == 4 || i == 6;
    }

    private static boolean isRinger(int i) {
        return i == 2 || i == 5;
    }

    public boolean isCaptionStreamOptedOut() {
        return false;
    }

    static {
        ArrayMap<Integer, Integer> arrayMap = new ArrayMap<>();
        STREAMS = arrayMap;
        arrayMap.put(4, Integer.valueOf(R$string.stream_alarm));
        arrayMap.put(6, Integer.valueOf(R$string.stream_bluetooth_sco));
        arrayMap.put(8, Integer.valueOf(R$string.stream_dtmf));
        arrayMap.put(3, Integer.valueOf(R$string.stream_music));
        arrayMap.put(10, Integer.valueOf(R$string.stream_accessibility));
        arrayMap.put(5, Integer.valueOf(R$string.stream_notification));
        arrayMap.put(2, Integer.valueOf(R$string.stream_ring));
        arrayMap.put(1, Integer.valueOf(R$string.stream_system));
        arrayMap.put(7, Integer.valueOf(R$string.stream_system_enforced));
        arrayMap.put(9, Integer.valueOf(R$string.stream_tts));
        arrayMap.put(0, Integer.valueOf(R$string.stream_voice_call));
    }

    public VolumeDialogControllerImpl(Context context, BroadcastDispatcher broadcastDispatcher, RingerModeTracker ringerModeTracker, ThreadFactory threadFactory, AudioManager audioManager, NotificationManager notificationManager, Optional<Vibrator> optional, IAudioService iAudioService, AccessibilityManager accessibilityManager, PackageManager packageManager, WakefulnessLifecycle wakefulnessLifecycle) {
        WakefulnessLifecycle wakefulnessLifecycle2 = wakefulnessLifecycle;
        Receiver receiver = new Receiver();
        this.mReceiver = receiver;
        boolean z = true;
        this.mDeviceInteractive = true;
        this.mShowDndTile = true;
        C2160VC vc = new C2160VC();
        this.mVolumeController = vc;
        C21361 r6 = new WakefulnessLifecycle.Observer() {
            public void onStartedWakingUp() {
                boolean unused = VolumeDialogControllerImpl.this.mDeviceInteractive = true;
            }

            public void onFinishedGoingToSleep() {
                boolean unused = VolumeDialogControllerImpl.this.mDeviceInteractive = false;
            }
        };
        this.mWakefullnessLifecycleObserver = r6;
        this.CHANGE_REASONS = new String[]{"unknown", "feature_configure_ready", "feature_configure_changed", "relevant_volume_changed", "playing_app_changed", "foreground_app_changed", "service_active_changed"};
        this.mRelativeFeatureActive = false;
        this.musicMaxVolumeCalledTimes = 0;
        this.mMultiVolumeServices = new SparseArray<>();
        this.mMultiVolumeServiceConnections = new SparseArray<>();
        this.mMultiVolumeController = new MVC();
        this.mUserSwitchCompleteObserver = new UserSwitchObserver() {
            public void onUserSwitchComplete(int i) throws RemoteException {
                VolumeDialogControllerImpl.this.getState();
                if (C2129D.BUG) {
                    Log.d(VolumeDialogControllerImpl.TAG + ".dv", "[MultiVolumeService] onUserSwitchComplete newUserId " + i);
                }
                int unused = VolumeDialogControllerImpl.this.mCurrentUserId = i;
            }
        };
        this.mvc_token = new Object();
        Context applicationContext = context.getApplicationContext();
        this.mContext = applicationContext;
        this.mPackageManager = packageManager;
        this.mWakefulnessLifecycle = wakefulnessLifecycle2;
        Events.writeEvent(5, new Object[0]);
        Looper buildLooperOnNewThread = threadFactory.buildLooperOnNewThread(VolumeDialogControllerImpl.class.getSimpleName());
        this.mWorkerLooper = buildLooperOnNewThread;
        C2161W w = new C2161W(buildLooperOnNewThread);
        this.mWorker = w;
        MediaSessionsCallbacks mediaSessionsCallbacks = new MediaSessionsCallbacks(applicationContext);
        this.mMediaSessionsCallbacksW = mediaSessionsCallbacks;
        this.mMediaSessions = createMediaSessions(applicationContext, buildLooperOnNewThread, mediaSessionsCallbacks);
        this.mAudio = audioManager;
        this.mNoMan = notificationManager;
        SettingObserver settingObserver = new SettingObserver(w);
        this.mObserver = settingObserver;
        RingerModeObservers ringerModeObservers = new RingerModeObservers((RingerModeLiveData) ringerModeTracker.getRingerMode(), (RingerModeLiveData) ringerModeTracker.getRingerModeInternal());
        this.mRingerModeObservers = ringerModeObservers;
        ringerModeObservers.init();
        this.mBroadcastDispatcher = broadcastDispatcher;
        settingObserver.init();
        receiver.init();
        if (MotoFeature.getInstance(applicationContext).isSupportRelativeVolume()) {
            RelevantVolumeReceiver relevantVolumeReceiver = new RelevantVolumeReceiver();
            this.mRelevantVolumeReceiver = relevantVolumeReceiver;
            relevantVolumeReceiver.init();
            this.mCurrentUserId = ActivityManager.getCurrentUser();
            if (C2129D.BUG) {
                Log.d(TAG + ".dv", "[MultiVolumeService] VolumeDialogControllerImpl init...... mCurrentUserId: " + this.mCurrentUserId);
            }
            w.obtainMessage(21, this.mCurrentUserId, 0).sendToTarget();
        }
        this.mVibrator = optional;
        this.mHasVibrator = (!optional.isPresent() || !optional.get().hasVibrator()) ? false : z;
        this.mAudioService = iAudioService;
        vc.setA11yMode(accessibilityManager.isAccessibilityVolumeStreamActive() ? 1 : 0);
        wakefulnessLifecycle2.addObserver(r6);
    }

    public AudioManager getAudioManager() {
        return this.mAudio;
    }

    public void dismiss() {
        this.mCallbacks.onDismissRequested(2);
    }

    /* access modifiers changed from: protected */
    public void setVolumeController() {
        try {
            this.mAudio.setVolumeController(this.mVolumeController);
        } catch (SecurityException e) {
            Log.w(TAG, "Unable to set the volume controller", e);
        }
    }

    /* access modifiers changed from: protected */
    public void setAudioManagerStreamVolume(int i, int i2, int i3) {
        this.mAudio.setStreamVolume(i, i2, i3);
    }

    /* access modifiers changed from: protected */
    public int getAudioManagerStreamVolume(int i) {
        return this.mAudio.getLastAudibleStreamVolume(i);
    }

    /* access modifiers changed from: protected */
    public int getAudioManagerStreamMaxVolume(int i) {
        return this.mAudio.getStreamMaxVolume(i);
    }

    /* access modifiers changed from: protected */
    public int getAudioManagerStreamMinVolume(int i) {
        return this.mAudio.getStreamMinVolumeInt(i);
    }

    public void register() {
        setVolumeController();
        setVolumePolicy(this.mVolumePolicy);
        showDndTile(this.mShowDndTile);
        try {
            this.mMediaSessions.init();
        } catch (SecurityException e) {
            Log.w(TAG, "No access to media sessions", e);
        }
        try {
            ActivityManager.getService().registerUserSwitchObserver(this.mUserSwitchCompleteObserver, TAG);
        } catch (RemoteException unused) {
        }
    }

    public void setVolumePolicy(VolumePolicy volumePolicy) {
        this.mVolumePolicy = volumePolicy;
        if (volumePolicy != null) {
            try {
                this.mAudio.setVolumePolicy(volumePolicy);
            } catch (NoSuchMethodError unused) {
                Log.w(TAG, "No volume policy api");
            }
        }
    }

    /* access modifiers changed from: protected */
    public MediaSessions createMediaSessions(Context context, Looper looper, MediaSessions.Callbacks callbacks) {
        return new MediaSessions(context, looper, callbacks);
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        printWriter.println(VolumeDialogControllerImpl.class.getSimpleName() + " state:");
        printWriter.print("  mDestroyed: ");
        printWriter.println(this.mDestroyed);
        printWriter.print("  mVolumePolicy: ");
        printWriter.println(this.mVolumePolicy);
        printWriter.print("  mState: ");
        printWriter.println(this.mState.toString(4));
        printWriter.print("  mShowDndTile: ");
        printWriter.println(this.mShowDndTile);
        printWriter.print("  mHasVibrator: ");
        printWriter.println(this.mHasVibrator);
        synchronized (this.mMediaSessionsCallbacksW.mRemoteStreams) {
            printWriter.print("  mRemoteStreams: ");
            printWriter.println(this.mMediaSessionsCallbacksW.mRemoteStreams.values());
        }
        printWriter.print("  mShowA11yStream: ");
        printWriter.println(this.mShowA11yStream);
        printWriter.println();
        this.mMediaSessions.dump(printWriter);
    }

    public void addCallback(VolumeDialogController.Callbacks callbacks, Handler handler) {
        this.mCallbacks.add(callbacks, handler);
        callbacks.onAccessibilityModeChanged(Boolean.valueOf(this.mShowA11yStream));
    }

    public void setUserActivityListener(UserActivityListener userActivityListener) {
        if (!this.mDestroyed) {
            synchronized (this) {
                this.mUserActivityListener = userActivityListener;
            }
        }
    }

    public void removeCallback(VolumeDialogController.Callbacks callbacks) {
        this.mCallbacks.remove(callbacks);
    }

    public void getState() {
        if (!this.mDestroyed) {
            this.mWorker.sendEmptyMessage(3);
        }
    }

    public boolean areCaptionsEnabled() {
        return Settings.Secure.getIntForUser(this.mContext.getContentResolver(), "odi_captions_enabled", 0, -2) == 1;
    }

    public void setCaptionsEnabled(boolean z) {
        Settings.Secure.putIntForUser(this.mContext.getContentResolver(), "odi_captions_enabled", z ? 1 : 0, -2);
    }

    public void getCaptionsComponentState(boolean z) {
        if (!this.mDestroyed) {
            this.mWorker.obtainMessage(16, Boolean.valueOf(z)).sendToTarget();
        }
    }

    public void notifyVisible(boolean z) {
        if (!this.mDestroyed) {
            this.mWorker.obtainMessage(12, z ? 1 : 0, 0).sendToTarget();
        }
    }

    public void userActivity() {
        if (!this.mDestroyed) {
            this.mWorker.removeMessages(13);
            this.mWorker.sendEmptyMessage(13);
        }
    }

    public void setRingerMode(int i, boolean z) {
        if (!this.mDestroyed) {
            this.mWorker.obtainMessage(4, i, z ? 1 : 0).sendToTarget();
        }
    }

    public void setStreamVolume(int i, int i2) {
        if (!this.mDestroyed) {
            this.mWorker.obtainMessage(10, i, i2).sendToTarget();
        }
    }

    public void setActiveStream(int i) {
        if (!this.mDestroyed) {
            this.mWorker.obtainMessage(11, i, 0).sendToTarget();
        }
    }

    public void setEnableDialogs(boolean z, boolean z2) {
        this.mShowVolumeDialog = z;
        this.mShowSafetyWarning = z2;
    }

    public void scheduleTouchFeedback() {
        this.mLastToggledRingerOn = System.currentTimeMillis();
    }

    private void playTouchFeedback() {
        if (System.currentTimeMillis() - this.mLastToggledRingerOn < 1000) {
            try {
                this.mAudioService.playSoundEffect(5);
            } catch (RemoteException unused) {
            }
        }
    }

    public void vibrate(VibrationEffect vibrationEffect) {
        this.mVibrator.ifPresent(new VolumeDialogControllerImpl$$ExternalSyntheticLambda0(vibrationEffect));
    }

    public boolean hasVibrator() {
        return this.mHasVibrator;
    }

    /* access modifiers changed from: private */
    public void onNotifyVisibleW(boolean z) {
        if (!this.mDestroyed) {
            this.mAudio.notifyVolumeControllerVisible(this.mVolumeController, z);
            if (!z && updateActiveStreamW(-1)) {
                this.mCallbacks.onStateChanged(this.mState);
            }
        }
    }

    /* access modifiers changed from: private */
    public void onUserActivityW() {
        synchronized (this) {
            UserActivityListener userActivityListener = this.mUserActivityListener;
            if (userActivityListener != null) {
                userActivityListener.onUserActivity();
            }
        }
    }

    /* access modifiers changed from: private */
    public void onShowSafetyWarningW(int i) {
        if (this.mShowSafetyWarning) {
            this.mCallbacks.onShowSafetyWarning(i);
        }
    }

    /* access modifiers changed from: private */
    public void onGetCaptionsComponentStateW(boolean z) {
        try {
            String string = this.mContext.getString(17039927);
            if (TextUtils.isEmpty(string)) {
                this.mCallbacks.onCaptionComponentStateChanged(Boolean.FALSE, Boolean.valueOf(z));
                return;
            }
            boolean z2 = false;
            if (C2129D.BUG) {
                Log.i(TAG, String.format("isCaptionsServiceEnabled componentNameString=%s", new Object[]{string}));
            }
            ComponentName unflattenFromString = ComponentName.unflattenFromString(string);
            if (unflattenFromString == null) {
                this.mCallbacks.onCaptionComponentStateChanged(Boolean.FALSE, Boolean.valueOf(z));
                return;
            }
            Context createContextAsUser = this.mContext.createContextAsUser(new UserHandle(this.mCurrentUserId), 0);
            C2138C c = this.mCallbacks;
            if (createContextAsUser.getPackageManager().getComponentEnabledSetting(unflattenFromString) == 1) {
                z2 = true;
            }
            c.onCaptionComponentStateChanged(Boolean.valueOf(z2), Boolean.valueOf(z));
        } catch (Exception e) {
            Log.e(TAG, "isCaptionsServiceEnabled failed to check for captions component", e);
            this.mCallbacks.onCaptionComponentStateChanged(Boolean.FALSE, Boolean.valueOf(z));
        }
    }

    /* access modifiers changed from: private */
    public void onAccessibilityModeChanged(Boolean bool) {
        this.mCallbacks.onAccessibilityModeChanged(bool);
    }

    /* access modifiers changed from: private */
    public boolean checkRoutedToBluetoothW(int i) {
        if (i != 3) {
            return false;
        }
        return false | updateStreamRoutedToBluetoothW(i, (this.mAudio.getDevicesForStream(3) & 896) != 0);
    }

    /* access modifiers changed from: private */
    public boolean shouldShowUI(int i) {
        int wakefulness = this.mWakefulnessLifecycle.getWakefulness();
        if (wakefulness == 0 || wakefulness == 3 || !this.mDeviceInteractive || (i & 1) == 0 || !this.mShowVolumeDialog) {
            return false;
        }
        return true;
    }

    /* access modifiers changed from: package-private */
    public boolean onVolumeChangedW(int i, int i2) {
        boolean shouldShowUI = shouldShowUI(i2);
        boolean z = (i2 & 4096) != 0;
        boolean z2 = (i2 & 2048) != 0;
        boolean z3 = (i2 & 128) != 0;
        boolean updateActiveStreamW = shouldShowUI ? updateActiveStreamW(i) | false : false;
        int audioManagerStreamVolume = getAudioManagerStreamVolume(i);
        boolean updateStreamLevelW = updateActiveStreamW | updateStreamLevelW(i, audioManagerStreamVolume) | checkRoutedToBluetoothW(shouldShowUI ? 3 : i);
        if (updateStreamLevelW) {
            this.mCallbacks.onStateChanged(this.mState);
        }
        if (shouldShowUI) {
            this.mCallbacks.onShowRequested(1);
        }
        if (z2) {
            this.mCallbacks.onShowVibrateHint();
        }
        if (z3) {
            this.mCallbacks.onShowSilentHint();
        }
        if (updateStreamLevelW && z) {
            Events.writeEvent(4, Integer.valueOf(i), Integer.valueOf(audioManagerStreamVolume));
        }
        if (MotoFeature.getInstance(this.mContext).isSupportRelativeVolume() && i == 3) {
            increaseVolumeIfNecessaryW(audioManagerStreamVolume);
        }
        return updateStreamLevelW;
    }

    /* access modifiers changed from: private */
    public boolean updateActiveStreamW(int i) {
        VolumeDialogController.State state = this.mState;
        if (i == state.activeStream) {
            return false;
        }
        state.activeStream = i;
        Events.writeEvent(2, Integer.valueOf(i));
        if (C2129D.BUG) {
            String str = TAG;
            Log.d(str, "updateActiveStreamW " + i);
        }
        if (i >= 100) {
            i = -1;
        }
        if (C2129D.BUG) {
            String str2 = TAG;
            Log.d(str2, "forceVolumeControlStream " + i);
        }
        this.mAudio.forceVolumeControlStream(i);
        return true;
    }

    /* access modifiers changed from: private */
    public VolumeDialogController.StreamState streamStateW(int i) {
        VolumeDialogController.StreamState streamState = this.mState.states.get(i);
        if (streamState != null) {
            return streamState;
        }
        VolumeDialogController.StreamState streamState2 = new VolumeDialogController.StreamState();
        this.mState.states.put(i, streamState2);
        return streamState2;
    }

    /* access modifiers changed from: private */
    public void onGetStateW() {
        for (Integer intValue : STREAMS.keySet()) {
            int intValue2 = intValue.intValue();
            updateStreamLevelW(intValue2, getAudioManagerStreamVolume(intValue2));
            streamStateW(intValue2).levelMin = getAudioManagerStreamMinVolume(intValue2);
            streamStateW(intValue2).levelMax = Math.max(1, getAudioManagerStreamMaxVolume(intValue2));
            updateStreamMuteW(intValue2, this.mAudio.isStreamMute(intValue2));
            VolumeDialogController.StreamState streamStateW = streamStateW(intValue2);
            streamStateW.muteSupported = this.mAudio.isStreamAffectedByMute(intValue2);
            streamStateW.name = STREAMS.get(Integer.valueOf(intValue2)).intValue();
            checkRoutedToBluetoothW(intValue2);
        }
        updateRingerModeExternalW(this.mRingerModeObservers.mRingerMode.getValue().intValue());
        updateZenModeW();
        updateZenConfig();
        updateEffectsSuppressorW(this.mNoMan.getEffectsSuppressor());
        this.mCallbacks.onStateChanged(this.mState);
    }

    private boolean updateStreamRoutedToBluetoothW(int i, boolean z) {
        VolumeDialogController.StreamState streamStateW = streamStateW(i);
        if (streamStateW.routedToBluetooth == z) {
            return false;
        }
        streamStateW.routedToBluetooth = z;
        if (!C2129D.BUG) {
            return true;
        }
        String str = TAG;
        Log.d(str, "updateStreamRoutedToBluetoothW stream=" + i + " routedToBluetooth=" + z);
        return true;
    }

    /* access modifiers changed from: private */
    public boolean updateStreamLevelW(int i, int i2) {
        VolumeDialogController.StreamState streamStateW = streamStateW(i);
        if (streamStateW.level == i2) {
            return false;
        }
        streamStateW.level = i2;
        if (isLogWorthy(i)) {
            Events.writeEvent(10, Integer.valueOf(i), Integer.valueOf(i2));
        }
        return true;
    }

    /* access modifiers changed from: private */
    public boolean updateStreamMuteW(int i, boolean z) {
        VolumeDialogController.StreamState streamStateW = streamStateW(i);
        if (streamStateW.muted == z) {
            return false;
        }
        streamStateW.muted = z;
        if (isLogWorthy(i)) {
            Events.writeEvent(15, Integer.valueOf(i), Boolean.valueOf(z));
        }
        if (z && isRinger(i)) {
            updateRingerModeInternalW(this.mRingerModeObservers.mRingerModeInternal.getValue().intValue());
        }
        return true;
    }

    /* access modifiers changed from: private */
    public boolean updateEffectsSuppressorW(ComponentName componentName) {
        if (Objects.equals(this.mState.effectsSuppressor, componentName)) {
            return false;
        }
        VolumeDialogController.State state = this.mState;
        state.effectsSuppressor = componentName;
        state.effectsSuppressorName = getApplicationName(this.mPackageManager, componentName);
        VolumeDialogController.State state2 = this.mState;
        Events.writeEvent(14, state2.effectsSuppressor, state2.effectsSuppressorName);
        return true;
    }

    private static String getApplicationName(PackageManager packageManager, ComponentName componentName) {
        if (componentName == null) {
            return null;
        }
        String packageName = componentName.getPackageName();
        try {
            String trim = Objects.toString(packageManager.getApplicationInfo(packageName, 0).loadLabel(packageManager), "").trim();
            return trim.length() > 0 ? trim : packageName;
        } catch (PackageManager.NameNotFoundException unused) {
        }
    }

    /* access modifiers changed from: private */
    public boolean updateZenModeW() {
        int i = Settings.Global.getInt(this.mContext.getContentResolver(), "zen_mode", 0);
        VolumeDialogController.State state = this.mState;
        if (state.zenMode == i) {
            return false;
        }
        state.zenMode = i;
        Events.writeEvent(13, Integer.valueOf(i));
        return true;
    }

    /* access modifiers changed from: private */
    public boolean updateZenConfig() {
        NotificationManager.Policy consolidatedNotificationPolicy = this.mNoMan.getConsolidatedNotificationPolicy();
        int i = consolidatedNotificationPolicy.priorityCategories;
        boolean z = (i & 32) == 0;
        boolean z2 = (i & 64) == 0;
        boolean z3 = (i & 128) == 0;
        boolean areAllPriorityOnlyRingerSoundsMuted = ZenModeConfig.areAllPriorityOnlyRingerSoundsMuted(consolidatedNotificationPolicy);
        VolumeDialogController.State state = this.mState;
        if (state.disallowAlarms == z && state.disallowMedia == z2 && state.disallowRinger == areAllPriorityOnlyRingerSoundsMuted && state.disallowSystem == z3) {
            return false;
        }
        state.disallowAlarms = z;
        state.disallowMedia = z2;
        state.disallowSystem = z3;
        state.disallowRinger = areAllPriorityOnlyRingerSoundsMuted;
        Events.writeEvent(17, "disallowAlarms=" + z + " disallowMedia=" + z2 + " disallowSystem=" + z3 + " disallowRinger=" + areAllPriorityOnlyRingerSoundsMuted);
        return true;
    }

    /* access modifiers changed from: private */
    public boolean updateRingerModeExternalW(int i) {
        VolumeDialogController.State state = this.mState;
        if (i == state.ringerModeExternal) {
            return false;
        }
        state.ringerModeExternal = i;
        Events.writeEvent(12, Integer.valueOf(i));
        return true;
    }

    /* access modifiers changed from: private */
    public boolean updateRingerModeInternalW(int i) {
        VolumeDialogController.State state = this.mState;
        if (i == state.ringerModeInternal) {
            return false;
        }
        state.ringerModeInternal = i;
        Events.writeEvent(11, Integer.valueOf(i));
        if (this.mState.ringerModeInternal == 2) {
            playTouchFeedback();
        }
        return true;
    }

    /* access modifiers changed from: private */
    public void onSetRingerModeW(int i, boolean z) {
        if (z) {
            this.mAudio.setRingerMode(i);
        } else {
            this.mAudio.setRingerModeInternal(i);
        }
    }

    /* access modifiers changed from: private */
    public void onSetStreamMuteW(int i, boolean z) {
        this.mAudio.adjustStreamVolume(i, z ? -100 : 100, 0);
    }

    /* access modifiers changed from: private */
    public void onSetStreamVolumeW(int i, int i2) {
        if (C2129D.BUG) {
            String str = TAG;
            Log.d(str, "onSetStreamVolume " + i + " level=" + i2);
        }
        if (i >= 100) {
            this.mMediaSessionsCallbacksW.setStreamVolume(i, i2);
        } else {
            setAudioManagerStreamVolume(i, i2, this.mShowVolumeDialog ? 1 : 0);
        }
    }

    /* access modifiers changed from: private */
    public void onSetActiveStreamW(int i) {
        if (updateActiveStreamW(i)) {
            this.mCallbacks.onStateChanged(this.mState);
        }
    }

    /* access modifiers changed from: private */
    public void onSetExitConditionW(Condition condition) {
        this.mNoMan.setZenMode(this.mState.zenMode, condition != null ? condition.id : null, TAG);
    }

    /* access modifiers changed from: private */
    public void onSetZenModeW(int i) {
        if (C2129D.BUG) {
            String str = TAG;
            Log.d(str, "onSetZenModeW " + i);
        }
        this.mNoMan.setZenMode(i, (Uri) null, TAG);
    }

    /* access modifiers changed from: private */
    public void onDismissRequestedW(int i) {
        this.mCallbacks.onDismissRequested(i);
    }

    public void showDndTile(boolean z) {
        if (C2129D.BUG) {
            Log.d(TAG, "showDndTile");
        }
        DndTile.setVisible(this.mContext, z);
    }

    /* renamed from: com.android.systemui.volume.VolumeDialogControllerImpl$VC */
    private final class C2160VC extends IVolumeController.Stub {
        private final String TAG;

        private C2160VC() {
            this.TAG = VolumeDialogControllerImpl.TAG + ".VC";
        }

        public void displaySafeVolumeWarning(int i) throws RemoteException {
            if (C2129D.BUG) {
                String str = this.TAG;
                Log.d(str, "displaySafeVolumeWarning " + Util.audioManagerFlagsToString(i));
            }
            if (!VolumeDialogControllerImpl.this.mDestroyed) {
                VolumeDialogControllerImpl.this.mWorker.obtainMessage(14, i, 0).sendToTarget();
            }
        }

        public void volumeChanged(int i, int i2) throws RemoteException {
            if (C2129D.BUG) {
                String str = this.TAG;
                Log.d(str, "volumeChanged " + AudioSystem.streamToString(i) + " " + Util.audioManagerFlagsToString(i2));
            }
            if (!VolumeDialogControllerImpl.this.mDestroyed) {
                VolumeDialogControllerImpl.this.mWorker.obtainMessage(1, i, i2).sendToTarget();
            }
        }

        public void masterMuteChanged(int i) throws RemoteException {
            if (C2129D.BUG) {
                Log.d(this.TAG, "masterMuteChanged");
            }
        }

        public void setLayoutDirection(int i) throws RemoteException {
            if (C2129D.BUG) {
                Log.d(this.TAG, "setLayoutDirection");
            }
            if (!VolumeDialogControllerImpl.this.mDestroyed) {
                VolumeDialogControllerImpl.this.mWorker.obtainMessage(8, i, 0).sendToTarget();
            }
        }

        public void dismiss() throws RemoteException {
            if (C2129D.BUG) {
                Log.d(this.TAG, "dismiss requested");
            }
            if (!VolumeDialogControllerImpl.this.mDestroyed) {
                VolumeDialogControllerImpl.this.mWorker.obtainMessage(2, 2, 0).sendToTarget();
                VolumeDialogControllerImpl.this.mWorker.sendEmptyMessage(2);
            }
        }

        public void setA11yMode(int i) {
            if (C2129D.BUG) {
                String str = this.TAG;
                Log.d(str, "setA11yMode to " + i);
            }
            if (!VolumeDialogControllerImpl.this.mDestroyed) {
                if (i == 0) {
                    boolean unused = VolumeDialogControllerImpl.this.mShowA11yStream = false;
                } else if (i != 1) {
                    String str2 = this.TAG;
                    Log.e(str2, "Invalid accessibility mode " + i);
                } else {
                    boolean unused2 = VolumeDialogControllerImpl.this.mShowA11yStream = true;
                }
                VolumeDialogControllerImpl.this.mWorker.obtainMessage(15, Boolean.valueOf(VolumeDialogControllerImpl.this.mShowA11yStream)).sendToTarget();
            }
        }
    }

    /* renamed from: com.android.systemui.volume.VolumeDialogControllerImpl$W */
    private final class C2161W extends Handler {
        C2161W(Looper looper) {
            super(looper);
        }

        public void handleMessage(Message message) {
            boolean z = true;
            switch (message.what) {
                case 1:
                    VolumeDialogControllerImpl.this.onVolumeChangedW(message.arg1, message.arg2);
                    return;
                case 2:
                    VolumeDialogControllerImpl.this.onDismissRequestedW(message.arg1);
                    return;
                case 3:
                    VolumeDialogControllerImpl.this.onGetStateW();
                    return;
                case 4:
                    VolumeDialogControllerImpl volumeDialogControllerImpl = VolumeDialogControllerImpl.this;
                    int i = message.arg1;
                    if (message.arg2 == 0) {
                        z = false;
                    }
                    volumeDialogControllerImpl.onSetRingerModeW(i, z);
                    return;
                case 5:
                    VolumeDialogControllerImpl.this.onSetZenModeW(message.arg1);
                    return;
                case 6:
                    VolumeDialogControllerImpl.this.onSetExitConditionW((Condition) message.obj);
                    return;
                case 7:
                    VolumeDialogControllerImpl volumeDialogControllerImpl2 = VolumeDialogControllerImpl.this;
                    int i2 = message.arg1;
                    if (message.arg2 == 0) {
                        z = false;
                    }
                    volumeDialogControllerImpl2.onSetStreamMuteW(i2, z);
                    return;
                case 8:
                    VolumeDialogControllerImpl.this.mCallbacks.onLayoutDirectionChanged(message.arg1);
                    return;
                case 9:
                    VolumeDialogControllerImpl.this.mCallbacks.onConfigurationChanged();
                    return;
                case 10:
                    VolumeDialogControllerImpl.this.onSetStreamVolumeW(message.arg1, message.arg2);
                    return;
                case 11:
                    VolumeDialogControllerImpl.this.onSetActiveStreamW(message.arg1);
                    return;
                case 12:
                    VolumeDialogControllerImpl volumeDialogControllerImpl3 = VolumeDialogControllerImpl.this;
                    if (message.arg1 == 0) {
                        z = false;
                    }
                    volumeDialogControllerImpl3.onNotifyVisibleW(z);
                    return;
                case 13:
                    VolumeDialogControllerImpl.this.onUserActivityW();
                    return;
                case 14:
                    VolumeDialogControllerImpl.this.onShowSafetyWarningW(message.arg1);
                    return;
                case 15:
                    VolumeDialogControllerImpl.this.onAccessibilityModeChanged((Boolean) message.obj);
                    return;
                case 16:
                    VolumeDialogControllerImpl.this.onGetCaptionsComponentStateW(((Boolean) message.obj).booleanValue());
                    return;
                case 17:
                    VolumeDialogControllerImpl.this.onChangeMusicRow(message.arg1, ((Double) message.obj).doubleValue());
                    return;
                case 18:
                    VolumeDialogControllerImpl.this.onChangeAppRow((AppRowStatus) message.obj);
                    return;
                case 19:
                    VolumeDialogControllerImpl.this.onHandleSafeMediaVolumeW(message.arg1);
                    return;
                case 20:
                    VolumeDialogControllerImpl.this.updateMultiVolumeStateW((List) message.obj);
                    return;
                case 21:
                    VolumeDialogControllerImpl.this.bindMultiVolumeServiceW(message.arg1);
                    return;
                default:
                    return;
            }
        }
    }

    /* renamed from: com.android.systemui.volume.VolumeDialogControllerImpl$C */
    class C2138C implements VolumeDialogController.Callbacks {
        private final Map<VolumeDialogController.Callbacks, Handler> mCallbackMap = new ConcurrentHashMap();

        C2138C() {
        }

        public void add(VolumeDialogController.Callbacks callbacks, Handler handler) {
            if (callbacks == null || handler == null) {
                throw new IllegalArgumentException();
            }
            this.mCallbackMap.put(callbacks, handler);
        }

        public void remove(VolumeDialogController.Callbacks callbacks) {
            this.mCallbackMap.remove(callbacks);
        }

        public void onShowRequested(final int i) {
            for (final Map.Entry next : this.mCallbackMap.entrySet()) {
                ((Handler) next.getValue()).post(new Runnable() {
                    public void run() {
                        ((VolumeDialogController.Callbacks) next.getKey()).onShowRequested(i);
                    }
                });
            }
        }

        public void onDismissRequested(final int i) {
            for (final Map.Entry next : this.mCallbackMap.entrySet()) {
                ((Handler) next.getValue()).post(new Runnable() {
                    public void run() {
                        ((VolumeDialogController.Callbacks) next.getKey()).onDismissRequested(i);
                    }
                });
            }
        }

        public void onStateChanged(VolumeDialogController.State state) {
            long currentTimeMillis = System.currentTimeMillis();
            final VolumeDialogController.State copy = state.copy();
            for (final Map.Entry next : this.mCallbackMap.entrySet()) {
                ((Handler) next.getValue()).post(new Runnable() {
                    public void run() {
                        ((VolumeDialogController.Callbacks) next.getKey()).onStateChanged(copy);
                    }
                });
            }
            Events.writeState(currentTimeMillis, copy);
        }

        public void onLayoutDirectionChanged(final int i) {
            for (final Map.Entry next : this.mCallbackMap.entrySet()) {
                ((Handler) next.getValue()).post(new Runnable() {
                    public void run() {
                        ((VolumeDialogController.Callbacks) next.getKey()).onLayoutDirectionChanged(i);
                    }
                });
            }
        }

        public void onConfigurationChanged() {
            for (final Map.Entry next : this.mCallbackMap.entrySet()) {
                ((Handler) next.getValue()).post(new Runnable() {
                    public void run() {
                        ((VolumeDialogController.Callbacks) next.getKey()).onConfigurationChanged();
                    }
                });
            }
        }

        public void onShowVibrateHint() {
            for (final Map.Entry next : this.mCallbackMap.entrySet()) {
                ((Handler) next.getValue()).post(new Runnable() {
                    public void run() {
                        ((VolumeDialogController.Callbacks) next.getKey()).onShowVibrateHint();
                    }
                });
            }
        }

        public void onShowSilentHint() {
            for (final Map.Entry next : this.mCallbackMap.entrySet()) {
                ((Handler) next.getValue()).post(new Runnable() {
                    public void run() {
                        ((VolumeDialogController.Callbacks) next.getKey()).onShowSilentHint();
                    }
                });
            }
        }

        public void onScreenOff() {
            for (final Map.Entry next : this.mCallbackMap.entrySet()) {
                ((Handler) next.getValue()).post(new Runnable() {
                    public void run() {
                        ((VolumeDialogController.Callbacks) next.getKey()).onScreenOff();
                    }
                });
            }
        }

        public void onShowSafetyWarning(final int i) {
            for (final Map.Entry next : this.mCallbackMap.entrySet()) {
                ((Handler) next.getValue()).post(new Runnable() {
                    public void run() {
                        ((VolumeDialogController.Callbacks) next.getKey()).onShowSafetyWarning(i);
                    }
                });
            }
        }

        public void onAccessibilityModeChanged(Boolean bool) {
            final boolean booleanValue = bool == null ? false : bool.booleanValue();
            for (final Map.Entry next : this.mCallbackMap.entrySet()) {
                ((Handler) next.getValue()).post(new Runnable() {
                    public void run() {
                        ((VolumeDialogController.Callbacks) next.getKey()).onAccessibilityModeChanged(Boolean.valueOf(booleanValue));
                    }
                });
            }
        }

        public void onCaptionComponentStateChanged(Boolean bool, Boolean bool2) {
            boolean booleanValue = bool == null ? false : bool.booleanValue();
            for (Map.Entry next : this.mCallbackMap.entrySet()) {
                ((Handler) next.getValue()).post(new VolumeDialogControllerImpl$C$$ExternalSyntheticLambda0(next, booleanValue, bool2));
            }
        }

        public void onIncreaseVolume(final int i) {
            for (final Map.Entry next : this.mCallbackMap.entrySet()) {
                ((Handler) next.getValue()).post(new Runnable() {
                    public void run() {
                        ((VolumeDialogController.Callbacks) next.getKey()).onIncreaseVolume(i);
                    }
                });
            }
        }

        public void onMusicRowChanged(int i, double d) {
            for (final Map.Entry next : this.mCallbackMap.entrySet()) {
                final int i2 = i;
                final double d2 = d;
                ((Handler) next.getValue()).post(new Runnable() {
                    public void run() {
                        ((VolumeDialogController.Callbacks) next.getKey()).onMusicRowChanged(i2, d2);
                    }
                });
            }
        }

        public void onAppRowChanged(String str, int i, int i2, double d) {
            for (final Map.Entry next : this.mCallbackMap.entrySet()) {
                final String str2 = str;
                final int i3 = i;
                final int i4 = i2;
                final double d2 = d;
                ((Handler) next.getValue()).post(new Runnable() {
                    public void run() {
                        ((VolumeDialogController.Callbacks) next.getKey()).onAppRowChanged(str2, i3, i4, d2);
                    }
                });
            }
        }

        public void onAppRowsChanged(final List<AppVolumeState> list) {
            for (final Map.Entry next : this.mCallbackMap.entrySet()) {
                ((Handler) next.getValue()).post(new Runnable() {
                    public void run() {
                        ((VolumeDialogController.Callbacks) next.getKey()).onAppRowsChanged(list);
                    }
                });
            }
        }

        public void onMultiVolumeRowsChanged(int i, double d, List<AppVolumeState> list) {
            for (final Map.Entry next : this.mCallbackMap.entrySet()) {
                final int i2 = i;
                final double d2 = d;
                final List<AppVolumeState> list2 = list;
                ((Handler) next.getValue()).post(new Runnable() {
                    public void run() {
                        ((VolumeDialogController.Callbacks) next.getKey()).onMultiVolumeRowsChanged(i2, d2, list2);
                    }
                });
            }
        }

        public void onMultiVolumeStateChanged(VolumeDialogController.State state) {
            final VolumeDialogController.State copy = state.copy();
            for (final Map.Entry next : this.mCallbackMap.entrySet()) {
                ((Handler) next.getValue()).post(new Runnable() {
                    public void run() {
                        ((VolumeDialogController.Callbacks) next.getKey()).onMultiVolumeStateChanged(copy);
                    }
                });
            }
        }
    }

    private final class RingerModeObservers {
        /* access modifiers changed from: private */
        public final RingerModeLiveData mRingerMode;
        /* access modifiers changed from: private */
        public final RingerModeLiveData mRingerModeInternal;
        private final Observer<Integer> mRingerModeInternalObserver = new Observer<Integer>() {
            public void onChanged(Integer num) {
                VolumeDialogControllerImpl.this.mWorker.post(new C2163xb7ffa954(this, num));
            }

            /* access modifiers changed from: private */
            public /* synthetic */ void lambda$onChanged$0(Integer num) {
                int intValue = num.intValue();
                if (RingerModeObservers.this.mRingerModeInternal.getInitialSticky()) {
                    VolumeDialogControllerImpl.this.mState.ringerModeInternal = intValue;
                }
                if (C2129D.BUG) {
                    String access$800 = VolumeDialogControllerImpl.TAG;
                    Log.d(access$800, "onChange internal_ringer_mode rm=" + Util.ringerModeToString(intValue));
                }
                if (VolumeDialogControllerImpl.this.updateRingerModeInternalW(intValue)) {
                    VolumeDialogControllerImpl volumeDialogControllerImpl = VolumeDialogControllerImpl.this;
                    volumeDialogControllerImpl.mCallbacks.onStateChanged(volumeDialogControllerImpl.mState);
                }
            }
        };
        private final Observer<Integer> mRingerModeObserver = new Observer<Integer>() {
            public void onChanged(Integer num) {
                VolumeDialogControllerImpl.this.mWorker.post(new C2162x7eae9893(this, num));
            }

            /* access modifiers changed from: private */
            public /* synthetic */ void lambda$onChanged$0(Integer num) {
                int intValue = num.intValue();
                if (RingerModeObservers.this.mRingerMode.getInitialSticky()) {
                    VolumeDialogControllerImpl.this.mState.ringerModeExternal = intValue;
                }
                if (C2129D.BUG) {
                    String access$800 = VolumeDialogControllerImpl.TAG;
                    Log.d(access$800, "onChange ringer_mode rm=" + Util.ringerModeToString(intValue));
                }
                if (VolumeDialogControllerImpl.this.updateRingerModeExternalW(intValue)) {
                    VolumeDialogControllerImpl volumeDialogControllerImpl = VolumeDialogControllerImpl.this;
                    volumeDialogControllerImpl.mCallbacks.onStateChanged(volumeDialogControllerImpl.mState);
                }
            }
        };

        RingerModeObservers(RingerModeLiveData ringerModeLiveData, RingerModeLiveData ringerModeLiveData2) {
            this.mRingerMode = ringerModeLiveData;
            this.mRingerModeInternal = ringerModeLiveData2;
        }

        public void init() {
            int intValue = this.mRingerMode.getValue().intValue();
            if (intValue != -1) {
                VolumeDialogControllerImpl.this.mState.ringerModeExternal = intValue;
            }
            this.mRingerMode.observeForever(this.mRingerModeObserver);
            int intValue2 = this.mRingerModeInternal.getValue().intValue();
            if (intValue2 != -1) {
                VolumeDialogControllerImpl.this.mState.ringerModeInternal = intValue2;
            }
            this.mRingerModeInternal.observeForever(this.mRingerModeInternalObserver);
        }
    }

    private final class SettingObserver extends ContentObserver {
        private final Uri ZEN_MODE_CONFIG_URI = Settings.Global.getUriFor("zen_mode_config_etag");
        private final Uri ZEN_MODE_URI = Settings.Global.getUriFor("zen_mode");

        public SettingObserver(Handler handler) {
            super(handler);
        }

        public void init() {
            VolumeDialogControllerImpl.this.mContext.getContentResolver().registerContentObserver(this.ZEN_MODE_URI, false, this);
            VolumeDialogControllerImpl.this.mContext.getContentResolver().registerContentObserver(this.ZEN_MODE_CONFIG_URI, false, this);
        }

        public void onChange(boolean z, Uri uri) {
            boolean access$3300 = this.ZEN_MODE_URI.equals(uri) ? VolumeDialogControllerImpl.this.updateZenModeW() : false;
            if (this.ZEN_MODE_CONFIG_URI.equals(uri)) {
                access$3300 |= VolumeDialogControllerImpl.this.updateZenConfig();
            }
            if (access$3300) {
                VolumeDialogControllerImpl volumeDialogControllerImpl = VolumeDialogControllerImpl.this;
                volumeDialogControllerImpl.mCallbacks.onStateChanged(volumeDialogControllerImpl.mState);
            }
        }
    }

    private final class Receiver extends BroadcastReceiver {
        private Receiver() {
        }

        public void init() {
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction("android.media.VOLUME_CHANGED_ACTION");
            intentFilter.addAction("android.media.STREAM_DEVICES_CHANGED_ACTION");
            intentFilter.addAction("android.media.STREAM_MUTE_CHANGED_ACTION");
            intentFilter.addAction("android.os.action.ACTION_EFFECTS_SUPPRESSOR_CHANGED");
            intentFilter.addAction("android.intent.action.CONFIGURATION_CHANGED");
            intentFilter.addAction("android.intent.action.SCREEN_OFF");
            intentFilter.addAction("android.intent.action.CLOSE_SYSTEM_DIALOGS");
            VolumeDialogControllerImpl.this.mContext.registerReceiver(this, intentFilter, (String) null, VolumeDialogControllerImpl.this.mWorker);
        }

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            boolean z = false;
            if (action.equals("android.media.VOLUME_CHANGED_ACTION")) {
                int intExtra = intent.getIntExtra("android.media.EXTRA_VOLUME_STREAM_TYPE", -1);
                int intExtra2 = intent.getIntExtra("android.media.EXTRA_VOLUME_STREAM_VALUE", -1);
                int intExtra3 = intent.getIntExtra("android.media.EXTRA_PREV_VOLUME_STREAM_VALUE", -1);
                if (C2129D.BUG) {
                    String access$800 = VolumeDialogControllerImpl.TAG;
                    Log.d(access$800, "onReceive VOLUME_CHANGED_ACTION stream=" + intExtra + " level=" + intExtra2 + " oldLevel=" + intExtra3);
                }
                z = VolumeDialogControllerImpl.this.updateStreamLevelW(intExtra, intExtra2);
            } else if (action.equals("android.media.STREAM_DEVICES_CHANGED_ACTION")) {
                int intExtra4 = intent.getIntExtra("android.media.EXTRA_VOLUME_STREAM_TYPE", -1);
                int intExtra5 = intent.getIntExtra("android.media.EXTRA_VOLUME_STREAM_DEVICES", -1);
                int intExtra6 = intent.getIntExtra("android.media.EXTRA_PREV_VOLUME_STREAM_DEVICES", -1);
                if (C2129D.BUG) {
                    String access$8002 = VolumeDialogControllerImpl.TAG;
                    Log.d(access$8002, "onReceive STREAM_DEVICES_CHANGED_ACTION stream=" + intExtra4 + " devices=" + intExtra5 + " oldDevices=" + intExtra6);
                }
                z = VolumeDialogControllerImpl.this.checkRoutedToBluetoothW(intExtra4) | VolumeDialogControllerImpl.this.onVolumeChangedW(intExtra4, 0);
            } else if (action.equals("android.media.STREAM_MUTE_CHANGED_ACTION")) {
                int intExtra7 = intent.getIntExtra("android.media.EXTRA_VOLUME_STREAM_TYPE", -1);
                boolean booleanExtra = intent.getBooleanExtra("android.media.EXTRA_STREAM_VOLUME_MUTED", false);
                if (C2129D.BUG) {
                    String access$8003 = VolumeDialogControllerImpl.TAG;
                    Log.d(access$8003, "onReceive STREAM_MUTE_CHANGED_ACTION stream=" + intExtra7 + " muted=" + booleanExtra);
                }
                z = VolumeDialogControllerImpl.this.updateStreamMuteW(intExtra7, booleanExtra);
            } else if (action.equals("android.os.action.ACTION_EFFECTS_SUPPRESSOR_CHANGED")) {
                if (C2129D.BUG) {
                    Log.d(VolumeDialogControllerImpl.TAG, "onReceive ACTION_EFFECTS_SUPPRESSOR_CHANGED");
                }
                VolumeDialogControllerImpl volumeDialogControllerImpl = VolumeDialogControllerImpl.this;
                z = volumeDialogControllerImpl.updateEffectsSuppressorW(volumeDialogControllerImpl.mNoMan.getEffectsSuppressor());
            } else if (action.equals("android.intent.action.CONFIGURATION_CHANGED")) {
                if (C2129D.BUG) {
                    Log.d(VolumeDialogControllerImpl.TAG, "onReceive ACTION_CONFIGURATION_CHANGED");
                }
                VolumeDialogControllerImpl.this.mCallbacks.onConfigurationChanged();
            } else if (action.equals("android.intent.action.SCREEN_OFF")) {
                if (C2129D.BUG) {
                    Log.d(VolumeDialogControllerImpl.TAG, "onReceive ACTION_SCREEN_OFF");
                }
                VolumeDialogControllerImpl.this.mCallbacks.onScreenOff();
            } else if (action.equals("android.intent.action.CLOSE_SYSTEM_DIALOGS")) {
                if (C2129D.BUG) {
                    Log.d(VolumeDialogControllerImpl.TAG, "onReceive ACTION_CLOSE_SYSTEM_DIALOGS");
                }
                VolumeDialogControllerImpl.this.dismiss();
            }
            if (z) {
                VolumeDialogControllerImpl volumeDialogControllerImpl2 = VolumeDialogControllerImpl.this;
                volumeDialogControllerImpl2.mCallbacks.onStateChanged(volumeDialogControllerImpl2.mState);
            }
        }
    }

    protected final class MediaSessionsCallbacks implements MediaSessions.Callbacks {
        private int mNextStream = 100;
        /* access modifiers changed from: private */
        public final HashMap<MediaSession.Token, Integer> mRemoteStreams = new HashMap<>();
        private final boolean mShowRemoteSessions;

        public MediaSessionsCallbacks(Context context) {
            this.mShowRemoteSessions = context.getResources().getBoolean(17891796);
        }

        public void onRemoteUpdate(MediaSession.Token token, String str, MediaController.PlaybackInfo playbackInfo) {
            int intValue;
            if (this.mShowRemoteSessions) {
                addStream(token, "onRemoteUpdate");
                synchronized (this.mRemoteStreams) {
                    intValue = this.mRemoteStreams.get(token).intValue();
                }
                Slog.d(VolumeDialogControllerImpl.TAG, "onRemoteUpdate: stream: " + intValue + " volume: " + playbackInfo.getCurrentVolume());
                boolean z = true;
                boolean z2 = VolumeDialogControllerImpl.this.mState.states.indexOfKey(intValue) < 0;
                VolumeDialogController.StreamState access$4000 = VolumeDialogControllerImpl.this.streamStateW(intValue);
                access$4000.dynamic = true;
                access$4000.levelMin = 0;
                access$4000.levelMax = playbackInfo.getMaxVolume();
                if (access$4000.level != playbackInfo.getCurrentVolume()) {
                    access$4000.level = playbackInfo.getCurrentVolume();
                    z2 = true;
                }
                if (!Objects.equals(access$4000.remoteLabel, str)) {
                    access$4000.name = -1;
                    access$4000.remoteLabel = str;
                } else {
                    z = z2;
                }
                if (z) {
                    Log.d(VolumeDialogControllerImpl.TAG, "onRemoteUpdate: " + str + ": " + access$4000.level + " of " + access$4000.levelMax);
                    VolumeDialogControllerImpl volumeDialogControllerImpl = VolumeDialogControllerImpl.this;
                    volumeDialogControllerImpl.mCallbacks.onStateChanged(volumeDialogControllerImpl.mState);
                }
            }
        }

        public void onRemoteVolumeChanged(MediaSession.Token token, int i) {
            int intValue;
            if (this.mShowRemoteSessions) {
                addStream(token, "onRemoteVolumeChanged");
                synchronized (this.mRemoteStreams) {
                    intValue = this.mRemoteStreams.get(token).intValue();
                }
                boolean access$4100 = VolumeDialogControllerImpl.this.shouldShowUI(i);
                String access$800 = VolumeDialogControllerImpl.TAG;
                Slog.d(access$800, "onRemoteVolumeChanged: stream: " + intValue + " showui? " + access$4100);
                boolean access$4200 = VolumeDialogControllerImpl.this.updateActiveStreamW(intValue);
                if (access$4100) {
                    access$4200 |= VolumeDialogControllerImpl.this.checkRoutedToBluetoothW(3);
                }
                if (access$4200) {
                    Slog.d(VolumeDialogControllerImpl.TAG, "onRemoteChanged: updatingState");
                    VolumeDialogControllerImpl volumeDialogControllerImpl = VolumeDialogControllerImpl.this;
                    volumeDialogControllerImpl.mCallbacks.onStateChanged(volumeDialogControllerImpl.mState);
                }
                if (access$4100) {
                    VolumeDialogControllerImpl.this.mCallbacks.onShowRequested(2);
                }
            }
        }

        /* JADX WARNING: Code restructure failed: missing block: B:12:0x003a, code lost:
            com.android.systemui.volume.VolumeDialogControllerImpl.access$2900(r3.this$0).states.remove(r4);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:13:0x004d, code lost:
            if (com.android.systemui.volume.VolumeDialogControllerImpl.access$2900(r3.this$0).activeStream != r4) goto L_0x0055;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:14:0x004f, code lost:
            com.android.systemui.volume.VolumeDialogControllerImpl.access$4200(r3.this$0, -1);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:15:0x0055, code lost:
            r3 = r3.this$0;
            r3.mCallbacks.onStateChanged(com.android.systemui.volume.VolumeDialogControllerImpl.access$2900(r3));
         */
        /* JADX WARNING: Code restructure failed: missing block: B:24:?, code lost:
            return;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onRemoteRemoved(android.media.session.MediaSession.Token r4) {
            /*
                r3 = this;
                boolean r0 = r3.mShowRemoteSessions
                if (r0 == 0) goto L_0x0064
                java.util.HashMap<android.media.session.MediaSession$Token, java.lang.Integer> r0 = r3.mRemoteStreams
                monitor-enter(r0)
                java.util.HashMap<android.media.session.MediaSession$Token, java.lang.Integer> r1 = r3.mRemoteStreams     // Catch:{ all -> 0x0061 }
                boolean r1 = r1.containsKey(r4)     // Catch:{ all -> 0x0061 }
                if (r1 != 0) goto L_0x002d
                java.lang.String r3 = com.android.systemui.volume.VolumeDialogControllerImpl.TAG     // Catch:{ all -> 0x0061 }
                java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ all -> 0x0061 }
                r1.<init>()     // Catch:{ all -> 0x0061 }
                java.lang.String r2 = "onRemoteRemoved: stream doesn't exist, aborting remote removed for token:"
                r1.append(r2)     // Catch:{ all -> 0x0061 }
                java.lang.String r4 = r4.toString()     // Catch:{ all -> 0x0061 }
                r1.append(r4)     // Catch:{ all -> 0x0061 }
                java.lang.String r4 = r1.toString()     // Catch:{ all -> 0x0061 }
                android.util.Log.d(r3, r4)     // Catch:{ all -> 0x0061 }
                monitor-exit(r0)     // Catch:{ all -> 0x0061 }
                return
            L_0x002d:
                java.util.HashMap<android.media.session.MediaSession$Token, java.lang.Integer> r1 = r3.mRemoteStreams     // Catch:{ all -> 0x0061 }
                java.lang.Object r4 = r1.get(r4)     // Catch:{ all -> 0x0061 }
                java.lang.Integer r4 = (java.lang.Integer) r4     // Catch:{ all -> 0x0061 }
                int r4 = r4.intValue()     // Catch:{ all -> 0x0061 }
                monitor-exit(r0)     // Catch:{ all -> 0x0061 }
                com.android.systemui.volume.VolumeDialogControllerImpl r0 = com.android.systemui.volume.VolumeDialogControllerImpl.this
                com.android.systemui.plugins.VolumeDialogController$State r0 = r0.mState
                android.util.SparseArray<com.android.systemui.plugins.VolumeDialogController$StreamState> r0 = r0.states
                r0.remove(r4)
                com.android.systemui.volume.VolumeDialogControllerImpl r0 = com.android.systemui.volume.VolumeDialogControllerImpl.this
                com.android.systemui.plugins.VolumeDialogController$State r0 = r0.mState
                int r0 = r0.activeStream
                if (r0 != r4) goto L_0x0055
                com.android.systemui.volume.VolumeDialogControllerImpl r4 = com.android.systemui.volume.VolumeDialogControllerImpl.this
                r0 = -1
                boolean unused = r4.updateActiveStreamW(r0)
            L_0x0055:
                com.android.systemui.volume.VolumeDialogControllerImpl r3 = com.android.systemui.volume.VolumeDialogControllerImpl.this
                com.android.systemui.volume.VolumeDialogControllerImpl$C r4 = r3.mCallbacks
                com.android.systemui.plugins.VolumeDialogController$State r3 = r3.mState
                r4.onStateChanged(r3)
                goto L_0x0064
            L_0x0061:
                r3 = move-exception
                monitor-exit(r0)     // Catch:{ all -> 0x0061 }
                throw r3
            L_0x0064:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.volume.VolumeDialogControllerImpl.MediaSessionsCallbacks.onRemoteRemoved(android.media.session.MediaSession$Token):void");
        }

        public void setStreamVolume(int i, int i2) {
            if (this.mShowRemoteSessions) {
                MediaSession.Token findToken = findToken(i);
                if (findToken == null) {
                    String access$800 = VolumeDialogControllerImpl.TAG;
                    Log.w(access$800, "setStreamVolume: No token found for stream: " + i);
                    return;
                }
                VolumeDialogControllerImpl.this.mMediaSessions.setVolume(findToken, i2);
            }
        }

        private MediaSession.Token findToken(int i) {
            synchronized (this.mRemoteStreams) {
                for (Map.Entry next : this.mRemoteStreams.entrySet()) {
                    if (((Integer) next.getValue()).equals(Integer.valueOf(i))) {
                        MediaSession.Token token = (MediaSession.Token) next.getKey();
                        return token;
                    }
                }
                return null;
            }
        }

        private void addStream(MediaSession.Token token, String str) {
            synchronized (this.mRemoteStreams) {
                if (!this.mRemoteStreams.containsKey(token)) {
                    this.mRemoteStreams.put(token, Integer.valueOf(this.mNextStream));
                    String access$800 = VolumeDialogControllerImpl.TAG;
                    Log.d(access$800, str + ": added stream " + this.mNextStream + " from token + " + token.toString());
                    this.mNextStream = this.mNextStream + 1;
                }
            }
        }
    }

    private final class RelevantVolumeReceiver extends BroadcastReceiver {
        private RelevantVolumeReceiver() {
        }

        public void init() {
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction("com.motorola.dynamicvolume.action.RELEVANT_VOLUME_CONFIGURATION_CHANGED_ACTION");
            intentFilter.addAction("android.intent.action.USER_SWITCHED");
            VolumeDialogControllerImpl.this.mContext.registerReceiverAsUser(this, UserHandle.ALL, intentFilter, "android.permission.MODIFY_AUDIO_ROUTING", VolumeDialogControllerImpl.this.mWorker);
        }

        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("com.motorola.dynamicvolume.action.RELEVANT_VOLUME_CONFIGURATION_CHANGED_ACTION") ? VolumeDialogControllerImpl.this.onRelevantVolumeConfigurationChangedW(intent) : false) {
                VolumeDialogControllerImpl volumeDialogControllerImpl = VolumeDialogControllerImpl.this;
                volumeDialogControllerImpl.mCallbacks.onStateChanged(volumeDialogControllerImpl.mState);
            }
            if ("android.intent.action.USER_SWITCHED".equals(intent.getAction())) {
                int intExtra = intent.getIntExtra("android.intent.extra.user_handle", -10000);
                if (intExtra == -10000) {
                    Log.e(VolumeDialogControllerImpl.TAG + ".dv", "[MultiVolumeService] userChangeReceiver received an invalid EXTRA_USER_HANDLE");
                    return;
                }
                if (C2129D.BUG) {
                    Log.d(VolumeDialogControllerImpl.TAG + ".dv", "[MultiVolumeService] receive Intent.ACTION_USER_SWITCHED userId: " + intExtra + ", ActivityManager.getCurrentUser userId: " + ActivityManager.getCurrentUser());
                }
                int unused = VolumeDialogControllerImpl.this.mCurrentUserId = intExtra;
                VolumeDialogControllerImpl.this.inactiveMultiVolumeStateW();
            }
        }
    }

    /* access modifiers changed from: private */
    public boolean onRelevantVolumeConfigurationChangedW(Intent intent) {
        String str;
        int intExtra = intent.getIntExtra("com.motorola.dynamicvolume.EXTRA_CHANGE_REASON", 0);
        if (C2129D.BUG) {
            String str2 = TAG + ".dv";
            StringBuilder sb = new StringBuilder();
            sb.append("onReceive RELEVANT_VOLUME_CONFIGURATION_CHANGED_ACTION, reason: ");
            if (intExtra >= 0) {
                String[] strArr = this.CHANGE_REASONS;
                if (intExtra < strArr.length) {
                    str = strArr[intExtra];
                    sb.append(str);
                    Log.d(str2, sb.toString());
                }
            }
            str = this.CHANGE_REASONS[0];
            sb.append(str);
            Log.d(str2, sb.toString());
        }
        if (intExtra != 6) {
            return false;
        }
        boolean booleanExtra = intent.getBooleanExtra("com.motorola.dynamicvolume.EXTRA_FEATURE_STATUS", false);
        int intExtra2 = intent.getIntExtra("com.motorola.dynamicvolume.EXTRA_USER_ID", -1);
        if (C2129D.BUG) {
            Log.d(TAG + ".dv", "[MultiVolumeService]  serviceActive: " + booleanExtra + ", userId: " + intExtra2);
        }
        if (booleanExtra) {
            bindMultiVolumeServiceW(intExtra2);
        } else {
            unBindMultiVolumeServiceW(intExtra2);
            inactiveMultiVolumeStateW(intExtra2);
        }
        return true;
    }

    private void increaseVolumeIfNecessaryW(int i) {
        if (i == getAudioManagerStreamMaxVolume(3)) {
            this.musicMaxVolumeCalledTimes++;
        } else {
            this.musicMaxVolumeCalledTimes = 0;
        }
        int i2 = this.musicMaxVolumeCalledTimes;
        if (i2 == 0) {
            this.musicMaxVolumeMode = false;
            if (C2129D.BUG) {
                Log.d(TAG + ".dv", "onVolumeChangedW, goto NotMaxVolumeStatus");
            }
        } else if (i2 == 2) {
            if (this.musicMaxVolumeMode) {
                this.musicMaxVolumeCalledTimes = 0;
                if (C2129D.BUG) {
                    Log.d(TAG + ".dv", "onVolumeChangedW, try to increase in MaxVolumeStatus");
                }
                this.mCallbacks.onIncreaseVolume(3);
            } else {
                this.musicMaxVolumeMode = true;
                this.musicMaxVolumeCalledTimes = 0;
                if (C2129D.BUG) {
                    Log.d(TAG + ".dv", "onVolumeChangedW, goto MaxVolumeStatus");
                }
            }
        } else if (this.musicMaxVolumeMode) {
            if (C2129D.BUG) {
                Log.d(TAG + ".dv", "onVolumeChangedW, wait for another Max in MaxVolumeStatus");
            }
        } else if (C2129D.BUG) {
            Log.d(TAG + ".dv", "onVolumeChangedW, wait for another Max in NotMaxVolumeStatus");
        }
        if (C2129D.BUG) {
            Log.d(TAG + ".dv", "onVolumeChangedW, musicMaxVolume:" + this.musicMaxVolumeMode + ", musicMaxVolumeCalledTimes:" + this.musicMaxVolumeCalledTimes);
        }
    }

    public void handleSafeMediaVolume(int i) {
        if (!this.mDestroyed) {
            this.mWorker.obtainMessage(19, i, 0).sendToTarget();
        }
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Removed duplicated region for block: B:23:0x0094 A[Catch:{ RemoteException | SecurityException -> 0x00af }] */
    /* JADX WARNING: Removed duplicated region for block: B:9:0x0015 A[Catch:{ RemoteException | SecurityException -> 0x00af }, LOOP:0: B:9:0x0015->B:22:0x0091, LOOP_START, PHI: r1 
      PHI: (r1v2 int) = (r1v0 int), (r1v3 int) binds: [B:8:0x0013, B:22:0x0091] A[DONT_GENERATE, DONT_INLINE]] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onHandleSafeMediaVolumeW(int r10) {
        /*
            r9 = this;
            java.lang.String r0 = ".dv"
            r1 = 0
            if (r10 < 0) goto L_0x000d
            java.lang.String[] r2 = HANDLE_REASONS
            int r3 = r2.length
            if (r10 >= r3) goto L_0x000d
            r2 = r2[r10]
            goto L_0x0011
        L_0x000d:
            java.lang.String[] r2 = HANDLE_REASONS
            r2 = r2[r1]
        L_0x0011:
            android.util.SparseArray<com.motorola.multivolume.IMultiVolumeService> r3 = r9.mMultiVolumeServices     // Catch:{ RemoteException | SecurityException -> 0x00af }
            if (r3 == 0) goto L_0x0094
        L_0x0015:
            android.util.SparseArray<com.motorola.multivolume.IMultiVolumeService> r3 = r9.mMultiVolumeServices     // Catch:{ RemoteException | SecurityException -> 0x00af }
            int r3 = r3.size()     // Catch:{ RemoteException | SecurityException -> 0x00af }
            if (r1 >= r3) goto L_0x00ca
            android.util.SparseArray<com.motorola.multivolume.IMultiVolumeService> r3 = r9.mMultiVolumeServices     // Catch:{ RemoteException | SecurityException -> 0x00af }
            int r3 = r3.keyAt(r1)     // Catch:{ RemoteException | SecurityException -> 0x00af }
            android.util.SparseArray<com.motorola.multivolume.IMultiVolumeService> r4 = r9.mMultiVolumeServices     // Catch:{ RemoteException | SecurityException -> 0x00af }
            java.lang.Object r4 = r4.valueAt(r1)     // Catch:{ RemoteException | SecurityException -> 0x00af }
            com.motorola.multivolume.IMultiVolumeService r4 = (com.motorola.multivolume.IMultiVolumeService) r4     // Catch:{ RemoteException | SecurityException -> 0x00af }
            java.lang.String r5 = " , action: "
            if (r4 == 0) goto L_0x0062
            boolean r6 = com.android.systemui.volume.C2129D.BUG     // Catch:{ RemoteException | SecurityException -> 0x00af }
            if (r6 == 0) goto L_0x005e
            java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch:{ RemoteException | SecurityException -> 0x00af }
            r6.<init>()     // Catch:{ RemoteException | SecurityException -> 0x00af }
            java.lang.String r7 = TAG     // Catch:{ RemoteException | SecurityException -> 0x00af }
            r6.append(r7)     // Catch:{ RemoteException | SecurityException -> 0x00af }
            r6.append(r0)     // Catch:{ RemoteException | SecurityException -> 0x00af }
            java.lang.String r6 = r6.toString()     // Catch:{ RemoteException | SecurityException -> 0x00af }
            java.lang.StringBuilder r7 = new java.lang.StringBuilder     // Catch:{ RemoteException | SecurityException -> 0x00af }
            r7.<init>()     // Catch:{ RemoteException | SecurityException -> 0x00af }
            java.lang.String r8 = "[MultiVolumeService] onHandleSafeMediaVolume, userId: "
            r7.append(r8)     // Catch:{ RemoteException | SecurityException -> 0x00af }
            r7.append(r3)     // Catch:{ RemoteException | SecurityException -> 0x00af }
            r7.append(r5)     // Catch:{ RemoteException | SecurityException -> 0x00af }
            r7.append(r2)     // Catch:{ RemoteException | SecurityException -> 0x00af }
            java.lang.String r3 = r7.toString()     // Catch:{ RemoteException | SecurityException -> 0x00af }
            android.util.Log.d(r6, r3)     // Catch:{ RemoteException | SecurityException -> 0x00af }
        L_0x005e:
            r4.handleSafeMediaVolume(r10)     // Catch:{ RemoteException | SecurityException -> 0x00af }
            goto L_0x0091
        L_0x0062:
            boolean r4 = com.android.systemui.volume.C2129D.BUG     // Catch:{ RemoteException | SecurityException -> 0x00af }
            if (r4 == 0) goto L_0x0091
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ RemoteException | SecurityException -> 0x00af }
            r4.<init>()     // Catch:{ RemoteException | SecurityException -> 0x00af }
            java.lang.String r6 = TAG     // Catch:{ RemoteException | SecurityException -> 0x00af }
            r4.append(r6)     // Catch:{ RemoteException | SecurityException -> 0x00af }
            r4.append(r0)     // Catch:{ RemoteException | SecurityException -> 0x00af }
            java.lang.String r4 = r4.toString()     // Catch:{ RemoteException | SecurityException -> 0x00af }
            java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch:{ RemoteException | SecurityException -> 0x00af }
            r6.<init>()     // Catch:{ RemoteException | SecurityException -> 0x00af }
            java.lang.String r7 = "[MultiVolumeService] MultiVolumeService is not ready, unable to onHandleSafeMediaVolume, userId: "
            r6.append(r7)     // Catch:{ RemoteException | SecurityException -> 0x00af }
            r6.append(r3)     // Catch:{ RemoteException | SecurityException -> 0x00af }
            r6.append(r5)     // Catch:{ RemoteException | SecurityException -> 0x00af }
            r6.append(r2)     // Catch:{ RemoteException | SecurityException -> 0x00af }
            java.lang.String r3 = r6.toString()     // Catch:{ RemoteException | SecurityException -> 0x00af }
            android.util.Log.d(r4, r3)     // Catch:{ RemoteException | SecurityException -> 0x00af }
        L_0x0091:
            int r1 = r1 + 1
            goto L_0x0015
        L_0x0094:
            boolean r9 = com.android.systemui.volume.C2129D.BUG     // Catch:{ RemoteException | SecurityException -> 0x00af }
            if (r9 == 0) goto L_0x00ca
            java.lang.StringBuilder r9 = new java.lang.StringBuilder     // Catch:{ RemoteException | SecurityException -> 0x00af }
            r9.<init>()     // Catch:{ RemoteException | SecurityException -> 0x00af }
            java.lang.String r10 = TAG     // Catch:{ RemoteException | SecurityException -> 0x00af }
            r9.append(r10)     // Catch:{ RemoteException | SecurityException -> 0x00af }
            r9.append(r0)     // Catch:{ RemoteException | SecurityException -> 0x00af }
            java.lang.String r9 = r9.toString()     // Catch:{ RemoteException | SecurityException -> 0x00af }
            java.lang.String r10 = "[MultiVolumeService] MultiVolumeService is not ready, unable to onHandleSafeMediaVolume"
            android.util.Log.d(r9, r10)     // Catch:{ RemoteException | SecurityException -> 0x00af }
            goto L_0x00ca
        L_0x00af:
            r9 = move-exception
            boolean r10 = com.android.systemui.volume.C2129D.BUG
            if (r10 == 0) goto L_0x00ca
            java.lang.StringBuilder r10 = new java.lang.StringBuilder
            r10.<init>()
            java.lang.String r1 = TAG
            r10.append(r1)
            r10.append(r0)
            java.lang.String r10 = r10.toString()
            java.lang.String r0 = "[MultiVolumeService] Unable to use MultiVolumeService, unable to onHandleSafeMediaVolume"
            android.util.Log.d(r10, r0, r9)
        L_0x00ca:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.volume.VolumeDialogControllerImpl.onHandleSafeMediaVolumeW(int):void");
    }

    public void changeMusicRow(int i, double d) {
        if (!this.mDestroyed) {
            onChangeMusicRow(i, d);
        }
    }

    public void onChangeMusicRow(int i, double d) {
        try {
            if (this.mMultiVolumeServices != null) {
                for (int i2 = 0; i2 < this.mMultiVolumeServices.size(); i2++) {
                    int keyAt = this.mMultiVolumeServices.keyAt(i2);
                    IMultiVolumeService valueAt = this.mMultiVolumeServices.valueAt(i2);
                    if (valueAt != null) {
                        if (C2129D.BUG) {
                            Log.d(TAG + ".dv", "[MultiVolumeService] changeMusicRow, progress: " + i + ", percentage: " + d);
                        }
                        valueAt.changeMusicRow(i, d);
                    } else if (C2129D.BUG) {
                        Log.d(TAG + ".dv", "[MultiVolumeService] MultiVolumeService is not ready, unable to onChangeMusicRow, userId: " + keyAt);
                    }
                }
            } else if (C2129D.BUG) {
                Log.d(TAG + ".dv", "[MultiVolumeService] MultiVolumeService is not ready, unable to onChangeMusicRow");
            }
        } catch (RemoteException | SecurityException e) {
            if (C2129D.BUG) {
                Log.d(TAG + ".dv", "[MultiVolumeService] Unable to use MultiVolumeService, unable to onChangeMusicRow", e);
            }
        }
    }

    public class AppRowStatus {
        String packageName;
        double percentage;
        int progress;
        int touchType;
        int uid;

        AppRowStatus(String str, int i, int i2, double d, int i3) {
            this.packageName = str;
            this.uid = i;
            this.progress = i2;
            this.percentage = d;
            this.touchType = i3;
        }
    }

    public void changeAppRow(String str, int i, int i2, double d, int i3) {
        if (!this.mDestroyed) {
            onChangeAppRow(new AppRowStatus(str, i, i2, d, i3));
        }
    }

    /* access modifiers changed from: private */
    public void onChangeAppRow(AppRowStatus appRowStatus) {
        try {
            if (this.mMultiVolumeServices != null) {
                for (int i = 0; i < this.mMultiVolumeServices.size(); i++) {
                    int keyAt = this.mMultiVolumeServices.keyAt(i);
                    IMultiVolumeService valueAt = this.mMultiVolumeServices.valueAt(i);
                    if (valueAt != null) {
                        if (C2129D.BUG) {
                            Log.d(TAG + ".dv", "[MultiVolumeService] onChangeAppRow, packageName: " + appRowStatus.packageName + ", progress: " + appRowStatus.progress + ", percentage: " + appRowStatus.percentage);
                        }
                        valueAt.changeAppRow(appRowStatus.packageName, appRowStatus.uid, appRowStatus.progress, appRowStatus.percentage, appRowStatus.touchType);
                    } else if (C2129D.BUG) {
                        Log.d(TAG + ".dv", "[MultiVolumeService] MultiVolumeService is not ready, unable to onChangeAppRow, userId: " + keyAt);
                    }
                }
            } else if (C2129D.BUG) {
                Log.d(TAG + ".dv", "[MultiVolumeService] MultiVolumeService is not ready, unable to onChangeAppRow");
            }
        } catch (RemoteException | SecurityException e) {
            if (C2129D.BUG) {
                Log.d(TAG + ".dv", "[MultiVolumeService] Unable to use MultiVolumeService, unable to onChangeAppRow", e);
            }
        }
    }

    public boolean isAppAutoMute(int i) {
        try {
            if (this.mMultiVolumeServices != null) {
                for (int i2 = 0; i2 < this.mMultiVolumeServices.size(); i2++) {
                    int keyAt = this.mMultiVolumeServices.keyAt(i2);
                    IMultiVolumeService valueAt = this.mMultiVolumeServices.valueAt(i2);
                    if (valueAt != null) {
                        return valueAt.isAppAutoMute(i);
                    }
                    if (C2129D.BUG) {
                        Log.d(TAG + ".dv", "[MultiVolumeService] MultiVolumeService is not ready, unable to isAppAutoMute, userId: " + keyAt);
                    }
                }
            } else if (C2129D.BUG) {
                Log.d(TAG + ".dv", "[MultiVolumeService] MultiVolumeService is not ready, unable to isAppAutoMute");
            }
        } catch (RemoteException | SecurityException e) {
            if (C2129D.BUG) {
                Log.d(TAG + ".dv", "[MultiVolumeService] Unable to use MultiVolumeService, unable to isAppAutoMute", e);
            }
        }
        return false;
    }

    public boolean isRelativeVolumeFeatureActive() {
        return this.mRelativeFeatureActive;
    }

    public void setUIHandlerCallbacks(VolumeDialogController.Callbacks callbacks, Handler handler) {
        if (C2129D.BUG) {
            Log.d(TAG + ".dv", "[MultiVolumeService] setUIHandlerCallbacks, mUICallbacks: " + this.mUICallbacks + ", mUIHandler: " + this.mUIHandler + ", callbacks: " + callbacks + ", handler: " + handler);
        }
        if ((this.mUICallbacks != null && this.mUIHandler != null) || callbacks == null || handler == null) {
            this.mUICallbacks = callbacks;
            this.mUIHandler = handler;
            return;
        }
        this.mUICallbacks = callbacks;
        this.mUIHandler = handler;
        onFetchMultiVolumeStatus();
    }

    private void onFetchMultiVolumeStatus() {
        try {
            if (this.mMultiVolumeServices != null) {
                for (int i = 0; i < this.mMultiVolumeServices.size(); i++) {
                    int keyAt = this.mMultiVolumeServices.keyAt(i);
                    IMultiVolumeService valueAt = this.mMultiVolumeServices.valueAt(i);
                    if (valueAt != null) {
                        if (C2129D.BUG) {
                            Log.d(TAG + ".dv", "[MultiVolumeService] onFetchMultiVolumeStatus");
                        }
                        valueAt.fetchMultiVolumeStatus();
                    } else if (C2129D.BUG) {
                        Log.d(TAG + ".dv", "[MultiVolumeService] MultiVolumeService is not ready, unable to onFetchMultiVolumeStatus, userId: " + keyAt);
                    }
                }
            } else if (C2129D.BUG) {
                Log.d(TAG + ".dv", "[MultiVolumeService] MultiVolumeService is not ready, unable to onFetchMultiVolumeStatus");
            }
        } catch (RemoteException | SecurityException e) {
            if (C2129D.BUG) {
                Log.d(TAG + ".dv", "[MultiVolumeService] Unable to use MultiVolumeService, unable to onFetchMultiVolumeStatus", e);
            }
        }
    }

    /* access modifiers changed from: private */
    public IMultiVolumeService getMultiVolumeServiceW(int i) {
        return this.mMultiVolumeServices.get(i);
    }

    /* access modifiers changed from: private */
    public void setMultiVolumeServiceW(int i, IMultiVolumeService iMultiVolumeService) {
        this.mMultiVolumeServices.put(i, iMultiVolumeService);
    }

    private MultiVolumeServiceConnection getMultiVolumeServiceConnectionW(int i) {
        MultiVolumeServiceConnection multiVolumeServiceConnection = this.mMultiVolumeServiceConnections.get(i);
        if (multiVolumeServiceConnection != null) {
            return multiVolumeServiceConnection;
        }
        MultiVolumeServiceConnection multiVolumeServiceConnection2 = new MultiVolumeServiceConnection(i);
        this.mMultiVolumeServiceConnections.put(i, multiVolumeServiceConnection2);
        return multiVolumeServiceConnection2;
    }

    /* access modifiers changed from: private */
    public void bindMultiVolumeServiceW(int i) {
        if (C2129D.BUG) {
            Log.d(TAG + ".dv", "[MultiVolumeService] bindMultiVolumeService, userId: " + i);
        }
        if (i >= 0) {
            Intent intent = new Intent("com.motorola.dynamicvolume.action.MULTI_VOLUME_SERVICE_BIND_ACTION");
            intent.setPackage("com.motorola.dynamicvolume");
            try {
                this.mContext.bindServiceAsUser(intent, getMultiVolumeServiceConnectionW(i), 1, this.mWorker, new UserHandle(i));
            } catch (Exception e) {
                if (C2129D.BUG) {
                    Log.d(TAG + ".dv", "[MultiVolumeService] Unable to bindServiceAsUser, userId: " + i, e);
                }
            }
        }
    }

    /* access modifiers changed from: private */
    public void unBindMultiVolumeServiceW(int i) {
        if (C2129D.BUG) {
            Log.d(TAG + ".dv", "[MultiVolumeService] unBindMultiVolumeService, userId: " + i);
        }
        unregisterVolumeControllerW(i);
        try {
            this.mContext.unbindService(getMultiVolumeServiceConnectionW(i));
        } catch (Exception e) {
            if (C2129D.BUG) {
                Log.d(TAG + ".dv", "[MultiVolumeService] Unable to unBindMultiVolumeService, userId: " + i, e);
            }
        }
        setMultiVolumeServiceW(i, (IMultiVolumeService) null);
    }

    /* access modifiers changed from: private */
    public void registerMultiVolumeControllerW(int i) {
        try {
            IMultiVolumeService multiVolumeServiceW = getMultiVolumeServiceW(i);
            if (multiVolumeServiceW != null) {
                multiVolumeServiceW.registerVolumeController(this.mMultiVolumeController);
                return;
            }
            Log.w(TAG + ".dv", "[MultiVolumeService] MultiVolumeService is not ready, unable to registerMultiVolumeController, userId: " + i);
        } catch (RemoteException | SecurityException e) {
            Log.w(TAG + ".dv", "[MultiVolumeService] Unable to registerMultiVolumeController, userId: " + i, e);
        }
    }

    private void unregisterVolumeControllerW(int i) {
        try {
            IMultiVolumeService multiVolumeServiceW = getMultiVolumeServiceW(i);
            if (multiVolumeServiceW != null) {
                multiVolumeServiceW.unregisterVolumeController(this.mMultiVolumeController);
                return;
            }
            Log.w(TAG + ".dv", "[MultiVolumeService] MultiVolumeService is not ready, unable to unregisterVolumeController, userId: " + i);
        } catch (RemoteException | SecurityException e) {
            Log.w(TAG + ".dv", "[MultiVolumeService] Unable to unregisterVolumeController, userId: " + i, e);
        }
    }

    private final class MultiVolumeServiceConnection implements ServiceConnection {
        int userId;

        public MultiVolumeServiceConnection(int i) {
            this.userId = i;
        }

        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            if (C2129D.BUG) {
                Log.d(VolumeDialogControllerImpl.TAG + ".dv", "[MultiVolumeService] onServiceConnected (ComponentName: " + componentName + "), userId: " + this.userId);
            }
            VolumeDialogControllerImpl.this.setMultiVolumeServiceW(this.userId, IMultiVolumeService.Stub.asInterface(iBinder));
            VolumeDialogControllerImpl.this.registerMultiVolumeControllerW(this.userId);
        }

        public void onServiceDisconnected(ComponentName componentName) {
            if (C2129D.BUG) {
                Log.d(VolumeDialogControllerImpl.TAG + ".dv", "[MultiVolumeService] onServiceDisconnected (ComponentName: " + componentName + "), userId: " + this.userId);
            }
            if (VolumeDialogControllerImpl.this.getMultiVolumeServiceW(this.userId) != null) {
                VolumeDialogControllerImpl.this.setMultiVolumeServiceW(this.userId, (IMultiVolumeService) null);
                VolumeDialogControllerImpl.this.inactiveMultiVolumeStateW(this.userId);
            }
        }

        public void onBindingDied(ComponentName componentName) {
            Log.w(VolumeDialogControllerImpl.TAG + ".dv", "[MultiVolumeService] onBindingDied (ComponentName: " + componentName + "), userId: " + this.userId);
            if (VolumeDialogControllerImpl.this.getMultiVolumeServiceW(this.userId) != null) {
                VolumeDialogControllerImpl.this.setMultiVolumeServiceW(this.userId, (IMultiVolumeService) null);
                Log.w(VolumeDialogControllerImpl.TAG + ".dv", "[MultiVolumeService] try to re-bindService, userId: " + this.userId);
                VolumeDialogControllerImpl.this.bindMultiVolumeServiceW(this.userId);
            }
        }

        public void onNullBinding(ComponentName componentName) {
            Log.w(VolumeDialogControllerImpl.TAG + ".dv", "[MultiVolumeService] onNullBinding (ComponentName: " + componentName + "), userId: " + this.userId);
            VolumeDialogControllerImpl.this.unBindMultiVolumeServiceW(this.userId);
        }
    }

    private final class MVC extends IMultiVolumeController.Stub {
        private MVC() {
        }

        public void musicRowChanged(final int i, final double d) throws RemoteException {
            if (C2129D.BUG) {
                Log.d(VolumeDialogControllerImpl.TAG + ".dv", "[MultiVolumeService] musicRowChanged (progress: " + i + " percentage: " + d + ")");
            }
            if (VolumeDialogControllerImpl.this.mUIHandler != null) {
                VolumeDialogControllerImpl.this.mUIHandler.removeCallbacksAndMessages(VolumeDialogControllerImpl.this.mvc_token);
                Message obtain = Message.obtain(VolumeDialogControllerImpl.this.mUIHandler, new Runnable() {
                    public void run() {
                        VolumeDialogControllerImpl.this.mUICallbacks.onMusicRowChanged(i, d);
                    }
                });
                VolumeDialogControllerImpl volumeDialogControllerImpl = VolumeDialogControllerImpl.this;
                obtain.obj = volumeDialogControllerImpl.mvc_token;
                volumeDialogControllerImpl.mUIHandler.sendMessageAtFrontOfQueue(obtain);
            } else if (C2129D.BUG) {
                Log.d(VolumeDialogControllerImpl.TAG + ".dv", "[MultiVolumeService] musicRowChanged (mUIHandler == null)");
            }
        }

        public void appRowsChanged(final List<AppVolumeState> list) throws RemoteException {
            if (C2129D.BUG) {
                Log.d(VolumeDialogControllerImpl.TAG + ".dv", "[MultiVolumeService] appRowsChanged (appVolumeStateList.size: " + list.size() + ", " + AppVolumeState.getAbbreviations(list, 0) + ")");
            }
            if (VolumeDialogControllerImpl.this.mUIHandler != null) {
                VolumeDialogControllerImpl.this.mUIHandler.removeCallbacksAndMessages(VolumeDialogControllerImpl.this.mvc_token);
                Message obtain = Message.obtain(VolumeDialogControllerImpl.this.mUIHandler, new Runnable() {
                    public void run() {
                        VolumeDialogControllerImpl.this.mUICallbacks.onAppRowsChanged(list);
                    }
                });
                VolumeDialogControllerImpl volumeDialogControllerImpl = VolumeDialogControllerImpl.this;
                obtain.obj = volumeDialogControllerImpl.mvc_token;
                volumeDialogControllerImpl.mUIHandler.sendMessageAtFrontOfQueue(obtain);
                VolumeDialogControllerImpl.this.mWorker.obtainMessage(20, list).sendToTarget();
            } else if (C2129D.BUG) {
                Log.d(VolumeDialogControllerImpl.TAG + ".dv", "[MultiVolumeService] appRowsChanged (mUIHandler == null)");
            }
        }

        public void multiVolumeRowsChanged(int i, double d, List<AppVolumeState> list) throws RemoteException {
            if (C2129D.BUG) {
                Log.d(VolumeDialogControllerImpl.TAG + ".dv", "[MultiVolumeService] multiVolumeRowsChanged (appVolumeStateList.size: " + list.size() + " musicRowProgress: " + i + " musicRowPercentage: " + d + ", " + AppVolumeState.getAbbreviations(list, 0) + ")");
            }
            if (VolumeDialogControllerImpl.this.mUIHandler != null) {
                VolumeDialogControllerImpl.this.mUIHandler.removeCallbacksAndMessages(VolumeDialogControllerImpl.this.mvc_token);
                final int i2 = i;
                final double d2 = d;
                final List<AppVolumeState> list2 = list;
                Message obtain = Message.obtain(VolumeDialogControllerImpl.this.mUIHandler, new Runnable() {
                    public void run() {
                        VolumeDialogControllerImpl.this.mUICallbacks.onMultiVolumeRowsChanged(i2, d2, list2);
                    }
                });
                VolumeDialogControllerImpl volumeDialogControllerImpl = VolumeDialogControllerImpl.this;
                obtain.obj = volumeDialogControllerImpl.mvc_token;
                volumeDialogControllerImpl.mUIHandler.sendMessageAtFrontOfQueue(obtain);
                VolumeDialogControllerImpl.this.mWorker.obtainMessage(20, list).sendToTarget();
            } else if (C2129D.BUG) {
                Log.d(VolumeDialogControllerImpl.TAG + ".dv", "[MultiVolumeService] multiVolumeRowsChanged (mUIHandler == null)");
            }
        }

        /* JADX WARNING: Removed duplicated region for block: B:7:0x0013  */
        /* JADX WARNING: Removed duplicated region for block: B:9:? A[RETURN, SYNTHETIC] */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void safeMediaVolumeHandled(int r3) throws android.os.RemoteException {
            /*
                r2 = this;
                if (r3 < 0) goto L_0x000a
                java.lang.String[] r2 = com.android.systemui.volume.VolumeDialogControllerImpl.HANDLE_REASONS
                int r0 = r2.length
                if (r3 >= r0) goto L_0x000a
                r2 = r2[r3]
                goto L_0x000f
            L_0x000a:
                java.lang.String[] r2 = com.android.systemui.volume.VolumeDialogControllerImpl.HANDLE_REASONS
                r3 = 0
                r2 = r2[r3]
            L_0x000f:
                boolean r3 = com.android.systemui.volume.C2129D.BUG
                if (r3 == 0) goto L_0x0041
                java.lang.StringBuilder r3 = new java.lang.StringBuilder
                r3.<init>()
                java.lang.String r0 = com.android.systemui.volume.VolumeDialogControllerImpl.TAG
                r3.append(r0)
                java.lang.String r0 = ".dv"
                r3.append(r0)
                java.lang.String r3 = r3.toString()
                java.lang.StringBuilder r0 = new java.lang.StringBuilder
                r0.<init>()
                java.lang.String r1 = "[MultiVolumeService] safeMediaVolumeHandled (action: "
                r0.append(r1)
                r0.append(r2)
                java.lang.String r2 = ")"
                r0.append(r2)
                java.lang.String r2 = r0.toString()
                android.util.Log.d(r3, r2)
            L_0x0041:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.volume.VolumeDialogControllerImpl.MVC.safeMediaVolumeHandled(int):void");
        }
    }

    /* access modifiers changed from: private */
    public void updateMultiVolumeStateW(List<AppVolumeState> list) {
        this.mState.appVolumeStates.clear();
        for (AppVolumeState next : list) {
            this.mState.appVolumeStates.put(next.packageUid, next);
        }
        if (C2129D.BUG) {
            Log.d(TAG + ".dv", "updateMultiVolumeState appVolumeStates: " + this.mState.dumpMultiVolumeString(0));
        }
        this.mCallbacks.onMultiVolumeStateChanged(this.mState);
    }

    /* access modifiers changed from: private */
    public void inactiveMultiVolumeStateW() {
        for (int i = 0; i < this.mState.appVolumeStates.size(); i++) {
            if (UserHandle.getUserId(this.mState.appVolumeStates.valueAt(i).packageUid) != this.mCurrentUserId) {
                this.mState.appVolumeStates.valueAt(i).active = false;
                this.mState.appVolumeStates.valueAt(i).shouldBeVisible = false;
                this.mState.appVolumeStates.valueAt(i).forceToShow = false;
            }
        }
        if (C2129D.BUG) {
            Log.d(TAG + ".dv", "inactiveMultiVolumeStateW according to current user : " + this.mState.dumpMultiVolumeString(0));
        }
        this.mCallbacks.onMultiVolumeStateChanged(this.mState);
    }

    /* access modifiers changed from: private */
    public void inactiveMultiVolumeStateW(int i) {
        for (int i2 = 0; i2 < this.mState.appVolumeStates.size(); i2++) {
            this.mState.appVolumeStates.valueAt(i2).active = false;
            this.mState.appVolumeStates.valueAt(i2).shouldBeVisible = false;
            this.mState.appVolumeStates.valueAt(i2).forceToShow = false;
        }
        if (C2129D.BUG) {
            Log.d(TAG + ".dv", "inactiveMultiVolumeStateW by userId: " + i + " : " + this.mState.dumpMultiVolumeString(0));
        }
        this.mCallbacks.onMultiVolumeStateChanged(this.mState);
    }
}
