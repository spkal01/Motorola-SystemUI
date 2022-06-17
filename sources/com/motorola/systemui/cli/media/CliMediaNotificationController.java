package com.motorola.systemui.cli.media;

import android.content.Context;
import android.media.AudioManager;
import android.os.Build;
import android.os.Handler;
import android.os.ServiceManager;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;
import com.android.internal.statusbar.IStatusBarService;
import com.android.internal.statusbar.NotificationVisibility;
import com.android.systemui.Dependency;
import com.android.systemui.keyguard.ScreenLifecycle;
import com.android.systemui.media.MediaCarouselController;
import com.android.systemui.moto.MotoFeature;
import com.android.systemui.statusbar.NotificationLockscreenUserManager;
import com.android.systemui.statusbar.notification.NotificationEntryListener;
import com.android.systemui.statusbar.notification.NotificationEntryManager;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import com.motorola.systemui.cli.media.CliMediaViewForQS;
import com.motorola.systemui.prc.media.IMediaNotification;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CliMediaNotificationController implements CliMediaViewForQS.MediaViewCallback, MediaCarouselController.RemovedCallback, IMediaNotification {
    /* access modifiers changed from: private */
    public static final boolean DEBUG = (!Build.IS_USER);
    /* access modifiers changed from: private */
    public AudioManager mAudioManager;
    final ScreenLifecycle.Observer mCliScreenObserver;
    /* access modifiers changed from: private */
    public CliMediaViewPager mCliViewPager;
    private Context mContext;
    private NotificationEntryListener mEntryListener = new NotificationEntryListener() {
        public void onNotificationAdded(NotificationEntry notificationEntry) {
            Log.e("CLI-QSMV-CliMediaNotificationController", "onNotificationAdded key = " + notificationEntry.getKey());
            if (CliMediaNotificationController.this.isForCurrentUser(notificationEntry.getSbn())) {
                CliMediaNotificationController.this.mMediaActives.put(notificationEntry.getKey(), Boolean.TRUE);
                CliMediaNotificationController.this.onNotificationPosted(notificationEntry.getSbn());
            }
        }

        public void onEntryReinflated(NotificationEntry notificationEntry) {
            Log.e("CLI-QSMV-CliMediaNotificationController", "onEntryReinflated");
            if (CliMediaNotificationController.this.isForCurrentUser(notificationEntry.getSbn()) && CliMediaNotificationController.this.getActiveByKey(notificationEntry.getKey())) {
                CliMediaNotificationController.this.onNotificationUpdated(notificationEntry.getSbn());
            }
        }

        public void onEntryRemoved(NotificationEntry notificationEntry, NotificationVisibility notificationVisibility, boolean z, int i) {
            Log.e("CLI-QSMV-CliMediaNotificationController", "onEntryRemoved");
            if (CliMediaNotificationController.this.isForCurrentUser(notificationEntry.getSbn())) {
                CliMediaNotificationController.this.mMediaActives.remove(notificationEntry.getKey());
                CliMediaNotificationController.this.onNotificationRemoved(notificationEntry.getSbn());
            }
        }

        public void onNotificationRankingUpdated(NotificationListenerService.RankingMap rankingMap) {
            CliMediaNotificationController cliMediaNotificationController = CliMediaNotificationController.this;
            cliMediaNotificationController.updateCliLidState(cliMediaNotificationController.mLidClosed);
        }
    };
    /* access modifiers changed from: private */
    public NotificationEntryManager mEntryManager;
    private Handler mHandler = new Handler();
    private InitializeMediaPreprocessorRunnable mInitMediaPreprocessorRunnalbe;
    /* access modifiers changed from: private */
    public boolean mLidClosed = false;
    private NotificationLockscreenUserManager mLockscreenUserManager;
    /* access modifiers changed from: private */
    public Map mMediaActives = new HashMap();
    private MediaCarouselController mMediaCarouselController;
    /* access modifiers changed from: private */
    public CliMediaPreprocessor mMediaPreprocessor;
    private ScreenLifecycle mScreenLifecycle;
    private final IStatusBarService mStatusBarService;
    /* access modifiers changed from: private */
    public boolean mUserChanged;
    private NotificationLockscreenUserManager.UserChangedListener mUserChangedListener = new NotificationLockscreenUserManager.UserChangedListener() {
        public void onUserChanged(int i) {
            boolean unused = CliMediaNotificationController.this.mUserChanged = true;
            CliMediaNotificationController cliMediaNotificationController = CliMediaNotificationController.this;
            cliMediaNotificationController.onRetrieveNotifications(cliMediaNotificationController.mEntryManager.getActiveNotificationsForCurrentUser());
        }
    };

    public CliMediaNotificationController(Context context, CliMediaViewPagerOwn cliMediaViewPagerOwn) {
        C26583 r0 = new ScreenLifecycle.Observer() {
            public void onScreenTurnedOff() {
                if (CliMediaNotificationController.DEBUG && CliMediaNotificationController.this.mLidClosed) {
                    Log.d("CLI-QSMV-CliMediaNotificationController", "onScreenTurnedOff mLidClosed = " + CliMediaNotificationController.this.mLidClosed);
                }
                CliMediaNotificationController cliMediaNotificationController = CliMediaNotificationController.this;
                cliMediaNotificationController.updateCliLidState(cliMediaNotificationController.mLidClosed);
            }

            public void onLidOpen() {
                boolean unused = CliMediaNotificationController.this.mLidClosed = false;
            }

            public void onLidClosed() {
                boolean unused = CliMediaNotificationController.this.mLidClosed = true;
                CliMediaNotificationController cliMediaNotificationController = CliMediaNotificationController.this;
                cliMediaNotificationController.onRetrieveNotifications(cliMediaNotificationController.mEntryManager.getActiveNotificationsForCurrentUser());
                CliMediaNotificationController cliMediaNotificationController2 = CliMediaNotificationController.this;
                cliMediaNotificationController2.updateCliLidState(cliMediaNotificationController2.mLidClosed);
            }
        };
        this.mCliScreenObserver = r0;
        Log.d("CLI-QSMV-CliMediaNotificationController", "CliMediaNotificationController init. " + this);
        Context applicationContext = context.getApplicationContext();
        this.mStatusBarService = IStatusBarService.Stub.asInterface(ServiceManager.getService("statusbar"));
        this.mContext = applicationContext;
        this.mContext = MotoFeature.getCliContext(context);
        this.mMediaPreprocessor = new CliMediaPreprocessor(context, new CliMediaNotificationController$$ExternalSyntheticLambda0(this), cliMediaViewPagerOwn);
        this.mAudioManager = new AudioManager(this.mContext);
        this.mLidClosed = MotoFeature.getInstance(this.mContext).isLidClosed();
        if (this.mEntryManager == null) {
            this.mEntryManager = (NotificationEntryManager) Dependency.get(NotificationEntryManager.class);
        }
        this.mEntryManager.addNotificationEntryListener(this.mEntryListener);
        ScreenLifecycle screenLifecycle = (ScreenLifecycle) Dependency.get(ScreenLifecycle.class);
        this.mScreenLifecycle = screenLifecycle;
        screenLifecycle.addObserver(r0);
        NotificationLockscreenUserManager notificationLockscreenUserManager = (NotificationLockscreenUserManager) Dependency.get(NotificationLockscreenUserManager.class);
        this.mLockscreenUserManager = notificationLockscreenUserManager;
        notificationLockscreenUserManager.addUserChangedListener(this.mUserChangedListener);
        if (cliMediaViewPagerOwn != CliMediaViewPagerOwn.PRC_TILE && cliMediaViewPagerOwn != CliMediaViewPagerOwn.PRC_EXPANDED) {
            MediaCarouselController mediaCarouselController = (MediaCarouselController) Dependency.get(MediaCarouselController.class);
            this.mMediaCarouselController = mediaCarouselController;
            if (mediaCarouselController != null) {
                mediaCarouselController.addRemovedCallback(this);
            }
        }
    }

    /* access modifiers changed from: private */
    public boolean isForCurrentUser(StatusBarNotification statusBarNotification) {
        return ((NotificationEntryManager.KeyguardEnvironment) Dependency.get(NotificationEntryManager.KeyguardEnvironment.class)).isNotificationForCurrentProfiles(statusBarNotification);
    }

    public void recycle() {
        if (DEBUG) {
            Log.d("CLI-QSMV-CliMediaNotificationController", "The media notification controller will be recreated and will need to recycle related listens");
        }
        this.mMediaPreprocessor.finish();
        this.mEntryManager.removeNotificationEntryListener(this.mEntryListener);
        this.mScreenLifecycle.removeObserver(this.mCliScreenObserver);
        this.mLockscreenUserManager.removeUserChangedListener(this.mUserChangedListener);
    }

    public void setCliViewPager(CliMediaViewPager cliMediaViewPager) {
        this.mCliViewPager = cliMediaViewPager;
        cliMediaViewPager.setMediaViewCallback(this);
        onRetrieveNotifications(this.mEntryManager.getActiveNotificationsForCurrentUser());
        if (DEBUG) {
            Log.d("CLI-QSMV-CliMediaNotificationController", "init cli view pager.");
        }
    }

    public void updateCliLidState(final boolean z) {
        this.mHandler.post(new Runnable() {
            public void run() {
                if (z) {
                    boolean isMusicActive = CliMediaNotificationController.this.mAudioManager.isMusicActive();
                    if (CliMediaNotificationController.DEBUG) {
                        Log.d("CLI-QSMV-CliMediaNotificationController", "updateCliLidState lid closed isMediaActive = " + isMusicActive);
                    }
                    if (CliMediaNotificationController.this.mCliViewPager != null) {
                        CliMediaNotificationController.this.mCliViewPager.updatePagePanel(isMusicActive);
                    }
                }
            }
        });
    }

    public void onRetrieveNotifications(List<NotificationEntry> list) {
        InitializeMediaPreprocessorRunnable initializeMediaPreprocessorRunnable = this.mInitMediaPreprocessorRunnalbe;
        if (initializeMediaPreprocessorRunnable != null) {
            this.mHandler.removeCallbacks(initializeMediaPreprocessorRunnable);
        }
        InitializeMediaPreprocessorRunnable initializeMediaPreprocessorRunnable2 = new InitializeMediaPreprocessorRunnable(list);
        this.mInitMediaPreprocessorRunnalbe = initializeMediaPreprocessorRunnable2;
        this.mHandler.post(initializeMediaPreprocessorRunnable2);
    }

    public void onNotificationPosted(StatusBarNotification statusBarNotification) {
        StatusBarNotification preprocessNotification = this.mMediaPreprocessor.preprocessNotification(statusBarNotification, false);
        if (preprocessNotification != null && this.mCliViewPager != null) {
            if (DEBUG) {
                Log.d("CLI-QSMV-CliMediaNotificationController", "onNotificationPosted media notification = " + statusBarNotification.getNotification());
            }
            this.mCliViewPager.addMediaNotification(preprocessNotification);
        }
    }

    public void onNotificationUpdated(StatusBarNotification statusBarNotification) {
        StatusBarNotification preprocessNotification = this.mMediaPreprocessor.preprocessNotification(statusBarNotification, false);
        if (preprocessNotification != null && getActiveByKey(statusBarNotification.getKey()) && this.mCliViewPager != null) {
            if (DEBUG) {
                Log.d("CLI-QSMV-CliMediaNotificationController", "onNotificationUpdated media notification = " + statusBarNotification.getNotification() + "; key = " + statusBarNotification.getKey());
            }
            this.mCliViewPager.updateMediaNotification(preprocessNotification);
        }
    }

    public void onNotificationRemoved(StatusBarNotification statusBarNotification) {
        StatusBarNotification preprocessNotification = this.mMediaPreprocessor.preprocessNotification(statusBarNotification, true);
        if (preprocessNotification != null && this.mCliViewPager != null) {
            if (DEBUG) {
                Log.d("CLI-QSMV-CliMediaNotificationController", "onNotificationRemoved media notification = " + statusBarNotification.getNotification() + "; key = " + statusBarNotification.getKey());
            }
            this.mCliViewPager.removeMediaNotification(preprocessNotification);
        }
    }

    private class InitializeMediaPreprocessorRunnable implements Runnable {
        List<NotificationEntry> mEntrys;

        public InitializeMediaPreprocessorRunnable(List<NotificationEntry> list) {
            this.mEntrys = list;
        }

        public void run() {
            if (CliMediaNotificationController.DEBUG) {
                Log.d("CLI-QSMV-CliMediaNotificationController", "mInitializeMediaPreprocessorRunnable - run");
            }
            try {
                if (this.mEntrys != null) {
                    CliMediaNotificationController.this.mMediaPreprocessor.finish();
                    if (CliMediaNotificationController.this.mUserChanged && CliMediaNotificationController.this.mCliViewPager != null) {
                        CliMediaNotificationController.this.mCliViewPager.removeAllMedias();
                        CliMediaNotificationController.this.mCliViewPager.restoreQSHeightWhenUserChanged();
                        boolean unused = CliMediaNotificationController.this.mUserChanged = false;
                    }
                }
                CliMediaNotificationController.this.mMediaPreprocessor.initialize();
            } catch (Exception e) {
                e.printStackTrace();
            }
            List<NotificationEntry> list = this.mEntrys;
            if (list != null && list.size() != 0) {
                for (NotificationEntry next : this.mEntrys) {
                    if (CliMediaNotificationController.this.getActiveByKey(next.getKey())) {
                        CliMediaNotificationController.this.onNotificationPosted(next.getSbn());
                    }
                }
            }
        }
    }

    private void suppressNotification(NotificationEntry notificationEntry) {
        boolean z = DEBUG;
        if (z) {
            Log.d("CLI-QSMV-CliMediaNotificationController", "supressing notification with entry: " + notificationEntry);
        }
        if (notificationEntry != null && notificationEntry.getSbn() != null) {
            this.mCliViewPager.removeMediaNotification(notificationEntry.getSbn());
        } else if (z) {
            Log.d("CLI-QSMV-CliMediaNotificationController", "failed to supressNotification.");
        }
    }

    public void onMediaCloseClicked(String str, String str2) {
        NotificationEntry activeNotificationUnfiltered = this.mEntryManager.getActiveNotificationUnfiltered(str);
        Log.d("CLI-QSMV-CliMediaNotificationController", "onMediaCloseClicked key = " + str);
        suppressNotification(activeNotificationUnfiltered);
        MediaCarouselController mediaCarouselController = this.mMediaCarouselController;
        if (mediaCarouselController != null) {
            mediaCarouselController.removeMainMediaPlayer(str);
        }
    }

    public void onMediaRemoved(String str) {
        NotificationEntry activeNotificationUnfiltered;
        if (str != null && (activeNotificationUnfiltered = this.mEntryManager.getActiveNotificationUnfiltered(str)) != null) {
            Log.d("CLI-QSMV-CliMediaNotificationController", "onMediaRemoved key = " + str + "; entry = " + activeNotificationUnfiltered);
            suppressNotification(activeNotificationUnfiltered);
        }
    }

    public void syncMediaActiveState(String str, boolean z) {
        Boolean bool = (Boolean) this.mMediaActives.get(str);
        if (bool == null || bool.booleanValue() != z) {
            this.mMediaActives.put(str, Boolean.valueOf(z));
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
}
