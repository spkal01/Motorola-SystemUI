package com.motorola.systemui.prc.media;

import android.content.Context;
import android.media.session.MediaController;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.service.notification.StatusBarNotification;
import android.util.Log;
import androidx.constraintlayout.widget.R$styleable;
import com.android.internal.statusbar.NotificationVisibility;
import com.android.systemui.Dependency;
import com.android.systemui.statusbar.NotificationLockscreenUserManager;
import com.android.systemui.statusbar.notification.NotificationEntryListener;
import com.android.systemui.statusbar.notification.NotificationEntryManager;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import com.motorola.systemui.cli.media.CliMediaNotificationData;
import com.motorola.systemui.cli.media.CliMediaPageModel;
import com.motorola.systemui.cli.media.CliMediaPreprocessor;
import com.motorola.systemui.cli.media.CliMediaViewPagerOwn;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class MediaTileController implements IMediaNotification {
    private static final boolean DEBUG = (!Build.IS_USER);
    private Context mContext;
    private CliMediaPageModel mCurrentDisplayMediaModel;
    private NotificationEntryListener mEntryListener = new NotificationEntryListener() {
        public void onNotificationAdded(NotificationEntry notificationEntry) {
            Log.d("MediaTileController", "onNotificationAdded key = " + notificationEntry.getKey());
            if (MediaTileController.this.isForCurrentUser(notificationEntry.getSbn())) {
                MediaTileController.this.mMediaActives.put(notificationEntry.getKey(), Boolean.TRUE);
                MediaTileController.this.onNotificationPosted(notificationEntry.getSbn());
            }
        }

        public void onEntryReinflated(NotificationEntry notificationEntry) {
            Log.d("MediaTileController", "onEntryReinflated key = " + notificationEntry.getKey());
            if (MediaTileController.this.isForCurrentUser(notificationEntry.getSbn())) {
                MediaTileController.this.onNotificationUpdate(notificationEntry.getSbn());
            }
        }

        public void onEntryRemoved(NotificationEntry notificationEntry, NotificationVisibility notificationVisibility, boolean z, int i) {
            Log.d("MediaTileController", "onEntryRemoved key = " + notificationEntry.getKey());
            if (MediaTileController.this.isForCurrentUser(notificationEntry.getSbn())) {
                MediaTileController.this.mMediaActives.remove(notificationEntry.getKey());
                MediaTileController.this.onNotificationRemoved(notificationEntry.getSbn());
            }
        }
    };
    /* access modifiers changed from: private */
    public NotificationEntryManager mEntryManager;
    /* access modifiers changed from: private */
    public Handler mHandler = new Handler(Looper.getMainLooper()) {
        public void handleMessage(Message message) {
            int i = message.what;
            if (i == 1) {
                MediaTileController.this.addMediaNotification((StatusBarNotification) message.obj);
            } else if (i == 2) {
                MediaTileController.this.removeMediaNotification((StatusBarNotification) message.obj);
            } else if (i == 3) {
                MediaTileController.this.updateMediaNotification((StatusBarNotification) message.obj);
            }
        }
    };
    private InitMediaPreprocessorRunnalbe mInitMediaPreprocessorRunnalbe;
    private NotificationLockscreenUserManager mLockscreenUserManager;
    /* access modifiers changed from: private */
    public Map mMediaActives = new HashMap();
    private ArrayList<CliMediaPageModel> mMediaPageModels = new ArrayList<>();
    /* access modifiers changed from: private */
    public CliMediaPreprocessor mMediaPreprocessor;
    /* access modifiers changed from: private */
    public MediaTileLayout mMediaTileLayout;
    private HandlerThread mParseNotificationThread;
    private Handler mParseNotificationWorkHandler;
    /* access modifiers changed from: private */
    public boolean mUserChanged;
    private NotificationLockscreenUserManager.UserChangedListener mUserChangedListener = new NotificationLockscreenUserManager.UserChangedListener() {
        public void onUserChanged(int i) {
            boolean unused = MediaTileController.this.mUserChanged = true;
            MediaTileController mediaTileController = MediaTileController.this;
            mediaTileController.onRetrieveNotifications(mediaTileController.mEntryManager.getActiveNotificationsForCurrentUser());
        }
    };

    public MediaTileController(MediaTileLayout mediaTileLayout, CliMediaViewPagerOwn cliMediaViewPagerOwn) {
        this.mContext = mediaTileLayout.getContext();
        this.mMediaTileLayout = mediaTileLayout;
        this.mMediaPreprocessor = new CliMediaPreprocessor(mediaTileLayout.getContext(), new MediaTileController$$ExternalSyntheticLambda0(this), cliMediaViewPagerOwn);
        initParseNotificationHandler();
        if (this.mEntryManager == null) {
            this.mEntryManager = (NotificationEntryManager) Dependency.get(NotificationEntryManager.class);
        }
        this.mEntryManager.addNotificationEntryListener(this.mEntryListener);
        NotificationLockscreenUserManager notificationLockscreenUserManager = (NotificationLockscreenUserManager) Dependency.get(NotificationLockscreenUserManager.class);
        this.mLockscreenUserManager = notificationLockscreenUserManager;
        notificationLockscreenUserManager.addUserChangedListener(this.mUserChangedListener);
        onRetrieveNotifications(this.mEntryManager.getActiveNotificationsForCurrentUser());
    }

    /* access modifiers changed from: private */
    public void onRetrieveNotifications(List<NotificationEntry> list) {
        InitMediaPreprocessorRunnalbe initMediaPreprocessorRunnalbe = this.mInitMediaPreprocessorRunnalbe;
        if (initMediaPreprocessorRunnalbe != null) {
            this.mHandler.removeCallbacks(initMediaPreprocessorRunnalbe);
        }
        InitMediaPreprocessorRunnalbe initMediaPreprocessorRunnalbe2 = new InitMediaPreprocessorRunnalbe(list);
        this.mInitMediaPreprocessorRunnalbe = initMediaPreprocessorRunnalbe2;
        this.mHandler.post(initMediaPreprocessorRunnalbe2);
    }

    private void initParseNotificationHandler() {
        HandlerThread handlerThread = this.mParseNotificationThread;
        if (handlerThread != null) {
            handlerThread.quitSafely();
            this.mParseNotificationThread = null;
        }
        HandlerThread handlerThread2 = new HandlerThread("Parse_Notification");
        this.mParseNotificationThread = handlerThread2;
        handlerThread2.start();
        this.mParseNotificationWorkHandler = new Handler(this.mParseNotificationThread.getLooper()) {
            StatusBarNotification notification;

            public void handleMessage(Message message) {
                switch (message.what) {
                    case R$styleable.Constraint_layout_goneMarginLeft:
                        StatusBarNotification access$900 = MediaTileController.this.preprocessNotification(message.obj, false);
                        this.notification = access$900;
                        if (access$900 != null) {
                            MediaTileController.this.mHandler.sendMessage(MediaTileController.this.mHandler.obtainMessage(1, this.notification));
                            return;
                        } else {
                            Log.d("MediaTileController", "onNotificationPosted: notification == null");
                            return;
                        }
                    case R$styleable.Constraint_layout_goneMarginRight:
                        StatusBarNotification access$9002 = MediaTileController.this.preprocessNotification(message.obj, true);
                        this.notification = access$9002;
                        if (access$9002 != null) {
                            MediaTileController.this.mHandler.sendMessage(MediaTileController.this.mHandler.obtainMessage(2, this.notification));
                            return;
                        } else {
                            Log.d("MediaTileController", "onNotificationRemoved: notification == null");
                            return;
                        }
                    case R$styleable.Constraint_layout_goneMarginStart:
                        StatusBarNotification access$9003 = MediaTileController.this.preprocessNotification(message.obj, false);
                        this.notification = access$9003;
                        if (access$9003 != null) {
                            MediaTileController.this.mHandler.sendMessage(MediaTileController.this.mHandler.obtainMessage(3, this.notification));
                            return;
                        } else {
                            Log.d("MediaTileController", "onNotificationUpdate: notification == null");
                            return;
                        }
                    default:
                        return;
                }
            }
        };
    }

    /* access modifiers changed from: private */
    public StatusBarNotification preprocessNotification(Object obj, boolean z) {
        return this.mMediaPreprocessor.preprocessNotification((StatusBarNotification) obj, z);
    }

    public void onNotificationPosted(StatusBarNotification statusBarNotification) {
        if (this.mParseNotificationWorkHandler == null) {
            initParseNotificationHandler();
        }
        this.mParseNotificationWorkHandler.sendMessage(this.mParseNotificationWorkHandler.obtainMessage(100, statusBarNotification));
    }

    /* access modifiers changed from: private */
    public void onNotificationUpdate(StatusBarNotification statusBarNotification) {
        if (this.mParseNotificationWorkHandler == null) {
            initParseNotificationHandler();
        }
        this.mParseNotificationWorkHandler.sendMessage(this.mParseNotificationWorkHandler.obtainMessage(R$styleable.Constraint_layout_goneMarginStart, statusBarNotification));
    }

    /* access modifiers changed from: private */
    public void onNotificationRemoved(StatusBarNotification statusBarNotification) {
        if (this.mParseNotificationWorkHandler == null) {
            initParseNotificationHandler();
        }
        this.mParseNotificationWorkHandler.sendMessage(this.mParseNotificationWorkHandler.obtainMessage(R$styleable.Constraint_layout_goneMarginRight, statusBarNotification));
    }

    /* access modifiers changed from: private */
    public void addMediaNotification(StatusBarNotification statusBarNotification) {
        if (DEBUG) {
            Log.d("MediaTileController", "addMediaNotification: sbn=" + statusBarNotification);
        }
        CliMediaPageModel cliMediaPageModel = new CliMediaPageModel(new CliMediaNotificationData(this.mContext, statusBarNotification));
        if (isContainedMedia(cliMediaPageModel.getPackageName())) {
            updateMediaNotification(statusBarNotification);
            return;
        }
        this.mMediaPageModels.add(cliMediaPageModel);
        dumpModels();
        updateMediaTileLayout();
    }

    /* access modifiers changed from: private */
    public void updateMediaNotification(StatusBarNotification statusBarNotification) {
        if (!isContainedMedia(statusBarNotification.getPackageName())) {
            addMediaNotification(statusBarNotification);
            return;
        }
        CliMediaPageModel cliMediaPageModel = new CliMediaPageModel(new CliMediaNotificationData(this.mContext, statusBarNotification));
        int i = 0;
        while (true) {
            if (i >= this.mMediaPageModels.size()) {
                break;
            }
            CliMediaPageModel cliMediaPageModel2 = this.mMediaPageModels.get(i);
            if (cliMediaPageModel.getPackageName().equals(cliMediaPageModel2.getPackageName())) {
                if (DEBUG) {
                    Log.d("MediaTileController", "updateMediaNotification: Update media model for " + cliMediaPageModel2.getPackageName());
                }
                this.mMediaPageModels.set(i, cliMediaPageModel);
            } else {
                i++;
            }
        }
        updateMediaTileLayout();
    }

    public void removeMediaNotification(StatusBarNotification statusBarNotification) {
        String packageName = statusBarNotification.getPackageName();
        if (isContainedMedia(packageName)) {
            if (DEBUG) {
                Log.d("MediaTileController", "removeMediaNotification: sbn=" + statusBarNotification);
            }
            removePageByName(packageName);
            dumpModels();
            updateMediaTileLayout();
        }
    }

    private void removePageByName(String str) {
        CliMediaPageModel cliMediaPageModel;
        Iterator<CliMediaPageModel> it = this.mMediaPageModels.iterator();
        while (true) {
            if (!it.hasNext()) {
                cliMediaPageModel = null;
                break;
            }
            cliMediaPageModel = it.next();
            if (cliMediaPageModel.getPackageName().equals(str)) {
                break;
            }
        }
        if (cliMediaPageModel != null) {
            this.mMediaPageModels.remove(cliMediaPageModel);
        }
    }

    private boolean isContainedMedia(String str) {
        Iterator<CliMediaPageModel> it = this.mMediaPageModels.iterator();
        while (it.hasNext()) {
            if (it.next().getPackageName().equals(str)) {
                return true;
            }
        }
        return false;
    }

    private class InitMediaPreprocessorRunnalbe implements Runnable {
        List<NotificationEntry> mEntrys = new ArrayList();

        public InitMediaPreprocessorRunnalbe(List<NotificationEntry> list) {
            this.mEntrys = list;
        }

        public void run() {
            try {
                if (this.mEntrys != null) {
                    MediaTileController.this.mMediaPreprocessor.finish();
                    if (MediaTileController.this.mUserChanged && MediaTileController.this.mMediaTileLayout != null) {
                        boolean unused = MediaTileController.this.mUserChanged = false;
                    }
                }
                MediaTileController.this.mMediaPreprocessor.initialize();
            } catch (Exception e) {
                e.printStackTrace();
            }
            List<NotificationEntry> list = this.mEntrys;
            if (list != null && !list.isEmpty()) {
                for (NotificationEntry next : this.mEntrys) {
                    if (MediaTileController.this.getActiveByKey(next.getKey())) {
                        MediaTileController.this.onNotificationPosted(next.getSbn());
                    }
                }
            }
        }
    }

    /* access modifiers changed from: private */
    public boolean getActiveByKey(String str) {
        Boolean bool = (Boolean) this.mMediaActives.get(str);
        if (bool == null) {
            return true;
        }
        return bool.booleanValue();
    }

    public void recycle() {
        if (DEBUG) {
            Log.d("MediaTileController", "The media notification controller will be recreated and will need to recycle related listens");
        }
        this.mHandler.post(new MediaTileController$$ExternalSyntheticLambda1(this));
        HandlerThread handlerThread = this.mParseNotificationThread;
        if (handlerThread != null) {
            handlerThread.quitSafely();
            this.mParseNotificationThread = null;
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$recycle$0() {
        this.mMediaPreprocessor.finish();
        this.mEntryManager.removeNotificationEntryListener(this.mEntryListener);
        this.mLockscreenUserManager.removeUserChangedListener(this.mUserChangedListener);
    }

    private void dumpModels() {
        if (DEBUG) {
            Log.d("MediaTileController", "+++++++++++++++++ Medias ++++++++++++++++++++");
            Iterator<CliMediaPageModel> it = this.mMediaPageModels.iterator();
            while (it.hasNext()) {
                CliMediaPageModel next = it.next();
                int i = -1;
                MediaController mediaController = next.getMediaController();
                if (!(mediaController == null || mediaController.getPlaybackState() == null)) {
                    i = mediaController.getPlaybackState().getState();
                }
                Log.d("MediaTileController", "mediaPkg: " + next.getPackageName() + "  Active: " + i + "  Song name: " + next.getTrackTitle());
            }
            Log.d("MediaTileController", "+++++++++++++++++++++++++++++++++++++++++++++");
        }
    }

    private void updateMediaTileLayout() {
        ArrayList<CliMediaPageModel> arrayList = this.mMediaPageModels;
        if (arrayList == null || !arrayList.isEmpty()) {
            updateCurrentDisplayMediaModel();
            Iterator<CliMediaPageModel> it = this.mMediaPageModels.iterator();
            while (it.hasNext()) {
                CliMediaPageModel next = it.next();
                if (next.getKey().equals(this.mCurrentDisplayMediaModel.getKey())) {
                    this.mMediaTileLayout.updateMediaTile(next);
                    return;
                }
            }
            return;
        }
        this.mMediaTileLayout.updateForNoPlayer();
        Log.d("MediaTileController", "updateMediaTileLayout: No media player.");
    }

    /* JADX WARNING: Removed duplicated region for block: B:6:0x0021  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void updateCurrentDisplayMediaModel() {
        /*
            r5 = this;
            java.util.ArrayList<com.motorola.systemui.cli.media.CliMediaPageModel> r0 = r5.mMediaPageModels
            int r0 = r0.size()
            r1 = 1
            if (r0 != r1) goto L_0x0015
            java.util.ArrayList<com.motorola.systemui.cli.media.CliMediaPageModel> r0 = r5.mMediaPageModels
            r1 = 0
            java.lang.Object r0 = r0.get(r1)
            com.motorola.systemui.cli.media.CliMediaPageModel r0 = (com.motorola.systemui.cli.media.CliMediaPageModel) r0
            r5.mCurrentDisplayMediaModel = r0
            goto L_0x0075
        L_0x0015:
            java.util.ArrayList<com.motorola.systemui.cli.media.CliMediaPageModel> r0 = r5.mMediaPageModels
            java.util.Iterator r0 = r0.iterator()
        L_0x001b:
            boolean r1 = r0.hasNext()
            if (r1 == 0) goto L_0x0075
            java.lang.Object r1 = r0.next()
            com.motorola.systemui.cli.media.CliMediaPageModel r1 = (com.motorola.systemui.cli.media.CliMediaPageModel) r1
            r2 = -1
            android.media.session.MediaController r3 = r1.getMediaController()
            if (r3 == 0) goto L_0x003c
            android.media.session.PlaybackState r4 = r3.getPlaybackState()
            if (r4 == 0) goto L_0x003c
            android.media.session.PlaybackState r2 = r3.getPlaybackState()
            int r2 = r2.getState()
        L_0x003c:
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r4 = "updateMediaTileLayout: pkg="
            r3.append(r4)
            java.lang.String r4 = r1.getPackageName()
            r3.append(r4)
            java.lang.String r4 = "  state="
            r3.append(r4)
            r3.append(r2)
            java.lang.String r4 = "  MediaActive="
            r3.append(r4)
            boolean r4 = r1.getMediaActive()
            r3.append(r4)
            java.lang.String r3 = r3.toString()
            java.lang.String r4 = "MediaTileController"
            android.util.Log.d(r4, r3)
            boolean r3 = r1.getMediaActive()
            if (r3 != 0) goto L_0x0073
            r3 = 3
            if (r2 != r3) goto L_0x001b
        L_0x0073:
            r5.mCurrentDisplayMediaModel = r1
        L_0x0075:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.motorola.systemui.prc.media.MediaTileController.updateCurrentDisplayMediaModel():void");
    }

    /* access modifiers changed from: private */
    public boolean isForCurrentUser(StatusBarNotification statusBarNotification) {
        return ((NotificationEntryManager.KeyguardEnvironment) Dependency.get(NotificationEntryManager.KeyguardEnvironment.class)).isNotificationForCurrentProfiles(statusBarNotification);
    }
}
