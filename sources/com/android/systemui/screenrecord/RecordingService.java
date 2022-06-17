package com.android.systemui.screenrecord;

import android.app.ActivityOptions;
import android.app.ActivityTaskManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.Icon;
import android.hardware.display.DisplayManager;
import android.media.AudioAttributes;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.UserHandle;
import android.provider.Settings;
import android.util.Log;
import android.view.Display;
import android.widget.Toast;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.logging.UiEventLogger;
import com.android.systemui.R$color;
import com.android.systemui.R$drawable;
import com.android.systemui.R$string;
import com.android.systemui.moto.DesktopFeature;
import com.android.systemui.moto.MomentsHelper;
import com.android.systemui.screenrecord.ScreenMediaRecorder;
import com.android.systemui.settings.UserContextProvider;
import com.android.systemui.statusbar.phone.KeyguardDismissUtil;
import java.io.IOException;
import java.util.concurrent.Executor;

public class RecordingService extends Service implements MediaRecorder.OnInfoListener, MediaRecorder.OnErrorListener {
    private ScreenRecordingAudioSource mAudioSource;
    private RecordingController mController;
    private int mDisplayId = 0;
    private final KeyguardDismissUtil mKeyguardDismissUtil;
    private final Executor mLongExecutor;
    private final NotificationManager mNotificationManager;
    private boolean mOriginalShowTaps;
    private ScreenMediaRecorder mRecorder;
    private boolean mShowTaps;
    private String mTopPackage;
    private Context mUserContext;
    private final UserContextProvider mUserContextTracker;

    public IBinder onBind(Intent intent) {
        return null;
    }

    public RecordingService(RecordingController recordingController, Executor executor, UiEventLogger uiEventLogger, NotificationManager notificationManager, UserContextProvider userContextProvider, KeyguardDismissUtil keyguardDismissUtil) {
        this.mController = recordingController;
        this.mLongExecutor = executor;
        this.mNotificationManager = notificationManager;
        this.mUserContextTracker = userContextProvider;
        this.mKeyguardDismissUtil = keyguardDismissUtil;
    }

    public static Intent getStartIntent(Context context, int i, int i2, boolean z) {
        return new Intent(context, RecordingService.class).setAction("com.android.systemui.screenrecord.REAL.START").putExtra("extra_resultCode", i).putExtra("extra_useAudio", i2).putExtra("extra_showTaps", z).putExtra("extra_displayid", context.getDisplayId());
    }

    public int onStartCommand(Intent intent, int i, int i2) {
        ActivityTaskManager.RootTaskInfo focusedRootTaskInfo;
        Display display;
        if (intent == null) {
            return 2;
        }
        String action = intent.getAction();
        Log.d("Recording_Service", "onStartCommand " + action);
        int userId = this.mUserContextTracker.getUserContext().getUserId();
        UserHandle userHandle = new UserHandle(userId);
        action.hashCode();
        char c = 65535;
        boolean z = true;
        switch (action.hashCode()) {
            case -1688140755:
                if (action.equals("com.android.systemui.screenrecord.SHARE")) {
                    c = 0;
                    break;
                }
                break;
            case -1687783248:
                if (action.equals("com.android.systemui.screenrecord.START")) {
                    c = 1;
                    break;
                }
                break;
            case -1224647939:
                if (action.equals("com.android.systemui.screenrecord.DELETE")) {
                    c = 2;
                    break;
                }
                break;
            case -470086188:
                if (action.equals("com.android.systemui.screenrecord.STOP")) {
                    c = 3;
                    break;
                }
                break;
            case 1751696452:
                if (action.equals("com.android.systemui.screenrecord.REAL.START")) {
                    c = 4;
                    break;
                }
                break;
        }
        switch (c) {
            case 0:
                this.mKeyguardDismissUtil.executeWhenUnlocked(new RecordingService$$ExternalSyntheticLambda0(this, new Intent("android.intent.action.SEND").setType("video/mp4").putExtra("android.intent.extra.STREAM", Uri.parse(intent.getStringExtra("extra_path"))), userHandle), false, false);
                sendBroadcast(new Intent("android.intent.action.CLOSE_SYSTEM_DIALOGS"));
                break;
            case 1:
                if (RecordingSettings.getScreenRecordingStatus(getApplicationContext()) == 0) {
                    this.mDisplayId = intent.getIntExtra("extra_displayid", 0);
                    Log.d("Recording_Service", "ACTION_START displayId =" + this.mDisplayId);
                    Intent promptIntent = this.mController.getPromptIntent();
                    promptIntent.putExtra("extra_showCamera", intent.getBooleanExtra("extra_showCamera", false));
                    ActivityOptions makeBasic = ActivityOptions.makeBasic();
                    makeBasic.setLaunchDisplayId(this.mDisplayId);
                    makeBasic.setLaunchWindowingMode(1);
                    startActivity(promptIntent, makeBasic.toBundle());
                    break;
                } else {
                    return 2;
                }
            case 2:
                sendBroadcast(new Intent("android.intent.action.CLOSE_SYSTEM_DIALOGS"));
                ContentResolver contentResolver = getContentResolver();
                Uri parse = Uri.parse(intent.getStringExtra("extra_path"));
                try {
                    contentResolver.delete(parse, (String) null, (String[]) null);
                } catch (SecurityException | UnsupportedOperationException e) {
                    e.printStackTrace();
                }
                Context userContext = this.mUserContextTracker.getUserContext();
                try {
                    if (!(!DesktopFeature.isDesktopConnected(userContext) || (focusedRootTaskInfo = ActivityTaskManager.getService().getFocusedRootTaskInfo()) == null || focusedRootTaskInfo.displayId == 0 || (display = ((DisplayManager) userContext.getSystemService("display")).getDisplay(focusedRootTaskInfo.displayId)) == null)) {
                        userContext = userContext.createDisplayContext(display);
                    }
                } catch (RemoteException e2) {
                    e2.rethrowFromSystemServer();
                }
                if (userContext != null) {
                    Toast.makeText(userContext, R$string.screenrecord_delete_description, 1).show();
                }
                this.mNotificationManager.cancel(4273);
                Log.d("Recording_Service", "Deleted recording " + parse);
                break;
            case 3:
                int intExtra = intent.getIntExtra("extra_displayid", 0);
                boolean booleanExtra = intent.getBooleanExtra("extra_shortcut_key", false);
                Log.d("Recording_Service", "ACTION_STOP=" + this.mController.isRecording() + " mDisplayId :" + this.mDisplayId + " displayId :" + intExtra + " shortCutKey :" + booleanExtra);
                if (intExtra == this.mDisplayId || !booleanExtra) {
                    if (!this.mController.isRecording()) {
                        this.mController.forceStop();
                    } else if (this.mRecorder != null) {
                        stopRecording();
                        this.mNotificationManager.cancel(4274);
                        saveRecording(userId);
                    }
                    stopSelf();
                    break;
                }
            case 4:
                if (this.mRecorder == null) {
                    this.mAudioSource = ScreenRecordingAudioSource.values()[intent.getIntExtra("extra_useAudio", 0)];
                    this.mShowTaps = intent.getBooleanExtra("extra_showTaps", false);
                    if (Settings.System.getInt(getApplicationContext().getContentResolver(), "show_touches", 0) == 0) {
                        z = false;
                    }
                    this.mOriginalShowTaps = z;
                    setTapsVisible(this.mShowTaps);
                    this.mUserContext = this.mUserContextTracker.getUserContext();
                    this.mDisplayId = intent.getIntExtra("extra_displayid", 0);
                    Log.d("Recording_Service", "ACTION_REAL_START displayId =" + this.mDisplayId);
                    if (this.mDisplayId != 0) {
                        Display display2 = ((DisplayManager) this.mUserContext.getSystemService("display")).getDisplay(this.mDisplayId);
                        if (display2 != null) {
                            this.mUserContext = this.mUserContext.createDisplayContext(display2);
                        } else {
                            Log.e("Recording_Service", "get context failed =" + this.mDisplayId);
                        }
                    }
                    this.mRecorder = new ScreenMediaRecorder(this.mUserContext, userId, this, this);
                    startRecording();
                    break;
                } else {
                    return 2;
                }
        }
        return 2;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ boolean lambda$onStartCommand$0(Intent intent, UserHandle userHandle) {
        startActivity(Intent.createChooser(intent, getResources().getString(R$string.screenrecord_share_label)).setFlags(268435456));
        this.mNotificationManager.cancelAsUser((String) null, 4273, userHandle);
        return false;
    }

    public void onCreate() {
        Log.d("Recording_Service", "RecordingService onCreate");
        super.onCreate();
    }

    /* access modifiers changed from: protected */
    @VisibleForTesting
    public ScreenMediaRecorder getRecorder() {
        return this.mRecorder;
    }

    private void startRecording() {
        try {
            this.mTopPackage = MomentsHelper.getTopPackage();
            this.mRecorder.start();
            this.mController.updateState(true);
            this.mController.setRecorder(this.mRecorder);
            createRecordingNotification();
        } catch (RemoteException | IOException e) {
            recordingError();
            e.printStackTrace();
        }
    }

    /* access modifiers changed from: protected */
    @VisibleForTesting
    public void createErrorNotification() {
        Resources resources = getResources();
        int i = R$string.screenrecord_name;
        NotificationChannel notificationChannel = new NotificationChannel("screen_record", getString(i), 3);
        notificationChannel.setDescription(getString(R$string.screenrecord_channel_description));
        notificationChannel.enableVibration(true);
        this.mNotificationManager.createNotificationChannel(notificationChannel);
        Bundle bundle = new Bundle();
        bundle.putString("android.substName", resources.getString(i));
        startForeground(4274, new Notification.Builder(this, "screen_record").setSmallIcon(R$drawable.ic_screenrecord).setContentTitle(resources.getString(R$string.screenrecord_start_error)).addExtras(bundle).build());
    }

    private void recordingError() {
        onStartCommand(getStopIntent(this), 0, 0);
        Toast.makeText(this.mUserContext, R$string.screenrecord_start_error, 1).show();
    }

    /* access modifiers changed from: protected */
    @VisibleForTesting
    public void showErrorToast(int i) {
        Toast.makeText(this, i, 1).show();
    }

    /* access modifiers changed from: protected */
    @VisibleForTesting
    public void createRecordingNotification() {
        Resources resources = getResources();
        int i = R$string.screenrecord_name;
        NotificationChannel notificationChannel = new NotificationChannel("screen_record", getString(i), 3);
        notificationChannel.setDescription(getString(R$string.screenrecord_channel_description));
        notificationChannel.setSound((Uri) null, (AudioAttributes) null);
        notificationChannel.enableVibration(true);
        this.mNotificationManager.createNotificationChannel(notificationChannel);
        Bundle bundle = new Bundle();
        bundle.putString("android.substName", resources.getString(i));
        startForeground(4274, new Notification.Builder(this, "screen_record").setSmallIcon(R$drawable.ic_screenrecord).setContentTitle(resources.getString(R$string.screenrecord_ongoing_screen_only)).setContentText(getResources().getString(R$string.screenrecord_stop_text)).setColorized(true).setColor(getResources().getColor(R$color.GM2_red_700)).setOngoing(true).setForegroundServiceBehavior(1).setContentIntent(PendingIntent.getService(this, 2, getStopIntent(this), 201326592)).addExtras(bundle).build());
    }

    /* access modifiers changed from: protected */
    @VisibleForTesting
    public Notification createProcessingNotification() {
        Resources resources = getApplicationContext().getResources();
        String string = resources.getString(R$string.screenrecord_ongoing_screen_only);
        Bundle bundle = new Bundle();
        bundle.putString("android.substName", resources.getString(R$string.screenrecord_name));
        return new Notification.Builder(getApplicationContext(), "screen_record").setContentTitle(string).setContentText(getResources().getString(R$string.screenrecord_background_processing_label)).setSmallIcon(R$drawable.ic_screenrecord).addExtras(bundle).build();
    }

    /* access modifiers changed from: protected */
    @VisibleForTesting
    public Notification createSaveNotification(ScreenMediaRecorder.SavedRecording savedRecording) {
        Uri uri = savedRecording.getUri();
        Intent dataAndType = new Intent("android.intent.action.VIEW").setFlags(268435457).setDataAndType(uri, "video/mp4");
        int i = R$drawable.ic_screenrecord;
        Notification.Action build = new Notification.Action.Builder(Icon.createWithResource(this, i), getResources().getString(R$string.screenrecord_share_label), PendingIntent.getService(this, 2, getShareIntent(this, uri.toString()), 201326592)).build();
        Notification.Action build2 = new Notification.Action.Builder(Icon.createWithResource(this, i), getResources().getString(R$string.screenrecord_delete_label), PendingIntent.getService(this, 2, getDeleteIntent(this, uri.toString()), 201326592)).build();
        Bundle bundle = new Bundle();
        bundle.putString("android.substName", getResources().getString(R$string.screenrecord_name));
        Notification.Builder addExtras = new Notification.Builder(this, "screen_record").setSmallIcon(i).setContentTitle(getResources().getString(R$string.screenrecord_save_title)).setContentText(getResources().getString(R$string.screenrecord_save_text)).setContentIntent(PendingIntent.getActivity(this, 2, dataAndType, 67108864)).addAction(build).addAction(build2).setAutoCancel(true).addExtras(bundle);
        Bitmap thumbnail = savedRecording.getThumbnail();
        if (thumbnail != null) {
            addExtras.setStyle(new Notification.BigPictureStyle().bigPicture(thumbnail).showBigPictureWhenCollapsed(true));
        }
        return addExtras.build();
    }

    private void stopRecording() {
        setTapsVisible(this.mOriginalShowTaps);
        ScreenMediaRecorder screenMediaRecorder = this.mRecorder;
        if (screenMediaRecorder != null) {
            screenMediaRecorder.end();
        }
        this.mController.updateState(false);
        this.mController.setRecorder((ScreenMediaRecorder) null);
    }

    public void onDestroy() {
        super.onDestroy();
        RecordingUtils.updateAudioParameter(getApplicationContext(), false);
        RecordingSettings.setScreenRecordingStatus(getApplicationContext(), 0);
    }

    private void saveRecording(int i) {
        UserHandle userHandle = new UserHandle(i);
        this.mNotificationManager.notifyAsUser((String) null, 4275, createProcessingNotification(), userHandle);
        this.mLongExecutor.execute(new RecordingService$$ExternalSyntheticLambda1(this, userHandle));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$saveRecording$1(UserHandle userHandle) {
        try {
            Log.d("Recording_Service", "saving recording");
            ScreenMediaRecorder.SavedRecording save = getRecorder().save();
            Notification createSaveNotification = createSaveNotification(save);
            if (!this.mController.isRecording()) {
                this.mNotificationManager.notifyAsUser((String) null, 4273, createSaveNotification, userHandle);
                Log.d("Recording_Service", "showing saved notification");
                this.mNotificationManager.notify(4273, createSaveNotification);
                MomentsHelper.insertMoments(this.mUserContext, this.mTopPackage, 3, save.getUri());
                Toast.makeText(this.mUserContext, R$string.screenrecord_save_success, 1).show();
            }
        } catch (IOException e) {
            Log.e("Recording_Service", "Error saving screen recording: " + e.getMessage());
            Toast.makeText(this.mUserContext, R$string.screenrecord_delete_error, 1).show();
        } catch (Throwable th) {
            this.mNotificationManager.cancelAsUser((String) null, 4275, userHandle);
            throw th;
        }
        this.mNotificationManager.cancelAsUser((String) null, 4275, userHandle);
    }

    private void setTapsVisible(boolean z) {
        Settings.System.putInt(getContentResolver(), "show_touches", z ? 1 : 0);
    }

    public static Intent getStopIntent(Context context) {
        return new Intent(context, RecordingService.class).setAction("com.android.systemui.screenrecord.STOP");
    }

    private static Intent getShareIntent(Context context, String str) {
        return new Intent(context, RecordingService.class).setAction("com.android.systemui.screenrecord.SHARE").putExtra("extra_path", str);
    }

    private static Intent getDeleteIntent(Context context, String str) {
        return new Intent(context, RecordingService.class).setAction("com.android.systemui.screenrecord.DELETE").putExtra("extra_path", str);
    }

    public void onInfo(MediaRecorder mediaRecorder, int i, int i2) {
        Log.d("Recording_Service", "Media recorder info: " + i);
        onStartCommand(getStopIntent(this), 0, 0);
    }

    public void onError(MediaRecorder mediaRecorder, int i, int i2) {
        Log.d("Recording_Service", "Media recorder error: " + i);
        recordingError();
    }
}
