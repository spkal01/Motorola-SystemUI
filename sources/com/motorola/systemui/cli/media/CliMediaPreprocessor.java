package com.motorola.systemui.cli.media;

import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.media.MediaMetadata;
import android.media.session.MediaController;
import android.media.session.MediaSession;
import android.media.session.MediaSessionManager;
import android.media.session.PlaybackState;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.service.notification.StatusBarNotification;
import android.text.TextUtils;
import android.util.Log;
import android.view.InflateException;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RemoteViews;
import androidx.appcompat.R$styleable;
import com.android.systemui.R$dimen;
import com.motorola.systemui.prc.media.IMediaNotification;
import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

public class CliMediaPreprocessor implements MediaSessionManager.OnActiveSessionsChangedListener {
    /* access modifiers changed from: private */
    public static final boolean DEBUG = (!Build.IS_USER);
    /* access modifiers changed from: private */
    public HashSet<MediaController> mActiveControllers = new HashSet<>();
    private final BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            if (CliMediaPreprocessor.this.mIsInitialized && intent != null) {
                String action = intent.getAction();
                String stringExtra = intent.getStringExtra("EXTRA_CLI_MEDIA_VIEW_PAGER_OWN");
                if (CliMediaPreprocessor.DEBUG) {
                    Log.d("CLI-QSMV-CliMediaPreprocessor", "Click the media view controller on " + stringExtra + ". This media preprocessor is belongs to " + CliMediaPreprocessor.this.mCliMediaViewPagerOwn.name());
                }
                if (stringExtra.equals(CliMediaPreprocessor.this.mCliMediaViewPagerOwn.name()) && action != null) {
                    MediaSession.Token token = (MediaSession.Token) intent.getParcelableExtra("EXTRA_TOKEN");
                    if (CliMediaPreprocessor.DEBUG) {
                        Log.d("CLI-QSMV-CliMediaPreprocessor", "Received media intent. action: " + action + ", media session token:" + token.hashCode());
                    }
                    Iterator it = CliMediaPreprocessor.this.mActiveControllers.iterator();
                    while (it.hasNext()) {
                        MediaController mediaController = (MediaController) it.next();
                        if (mediaController.getSessionToken().equals(token)) {
                            if (CliMediaPreprocessor.DEBUG) {
                                Log.d("CLI-QSMV-CliMediaPreprocessor", "Action on controller: " + mediaController.getPackageName());
                            }
                            char c = 65535;
                            switch (action.hashCode()) {
                                case -964315528:
                                    if (action.equals("com.android.systemui.ACTION_MEDIA_NEXT")) {
                                        c = 0;
                                        break;
                                    }
                                    break;
                                case -964249927:
                                    if (action.equals("com.android.systemui.ACTION_MEDIA_PLAY")) {
                                        c = 1;
                                        break;
                                    }
                                    break;
                                case -94792459:
                                    if (action.equals("com.android.systemui.ACTION_PLAY_PAUSE")) {
                                        c = 2;
                                        break;
                                    }
                                    break;
                                case 172714737:
                                    if (action.equals("com.android.systemui.ACTION_MEDIA_PAUSE")) {
                                        c = 3;
                                        break;
                                    }
                                    break;
                                case 1721471996:
                                    if (action.equals("com.android.systemui.ACTION_MEDIA_PREVIOUS")) {
                                        c = 4;
                                        break;
                                    }
                                    break;
                            }
                            switch (c) {
                                case 0:
                                    sendKeyPress(mediaController, 87);
                                    return;
                                case 1:
                                    sendKeyPress(mediaController, R$styleable.AppCompatTheme_windowNoTitle);
                                    return;
                                case 2:
                                    sendKeyPress(mediaController, 85);
                                    return;
                                case 3:
                                    sendKeyPress(mediaController, 127);
                                    return;
                                case 4:
                                    sendKeyPress(mediaController, 88);
                                    return;
                                default:
                                    Log.w("CLI-QSMV-CliMediaPreprocessor", "Invalid action received: " + action);
                                    return;
                            }
                        }
                    }
                    Log.w("CLI-QSMV-CliMediaPreprocessor", "Controller not found for media intent - Ignoring.");
                }
            }
        }

        private void sendKeyPress(MediaController mediaController, int i) {
            mediaController.dispatchMediaButtonEvent(new KeyEvent(0, i));
            mediaController.dispatchMediaButtonEvent(new KeyEvent(1, i));
        }
    };
    /* access modifiers changed from: private */
    public CliMediaViewPagerOwn mCliMediaViewPagerOwn;
    private final Context mContext;
    private HashMap<MediaController, StatusBarNotification> mControllerNotificationMap = new HashMap<>();
    private IMediaNotification mIMediaNotification;
    /* access modifiers changed from: private */
    public boolean mIsInitialized;
    private HashMap<String, MediaController> mLastRemovedControllerMap = new HashMap<>();
    private final MediaSessionManager mMediaManager;

    public CliMediaPreprocessor(Context context, IMediaNotification iMediaNotification, CliMediaViewPagerOwn cliMediaViewPagerOwn) {
        this.mContext = context;
        this.mIMediaNotification = iMediaNotification;
        this.mMediaManager = (MediaSessionManager) context.getSystemService("media_session");
        this.mCliMediaViewPagerOwn = cliMediaViewPagerOwn;
    }

    public void initialize() {
        logd("initialize() this: " + this + "  mIsInitialized: " + this.mIsInitialized);
        if (!this.mIsInitialized) {
            this.mMediaManager.addOnActiveSessionsChangedListener(this, (ComponentName) null);
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction("com.android.systemui.ACTION_MEDIA_PLAY");
            intentFilter.addAction("com.android.systemui.ACTION_PLAY_PAUSE");
            intentFilter.addAction("com.android.systemui.ACTION_MEDIA_PAUSE");
            intentFilter.addAction("com.android.systemui.ACTION_MEDIA_NEXT");
            intentFilter.addAction("com.android.systemui.ACTION_MEDIA_PREVIOUS");
            this.mContext.registerReceiver(this.mBroadcastReceiver, intentFilter, "com.android.systemui.permission.SELF", (Handler) null);
            for (MediaController next : this.mMediaManager.getActiveSessions((ComponentName) null)) {
                logd("initialize: package = " + next.getPackageName());
                if (!(next.getPlaybackState() == null || next.getMetadata() == null || isInActiveControllers(next))) {
                    this.mActiveControllers.add(next);
                    logd("initialize: add controller: " + next.getPackageName());
                    next.registerCallback(new MediaControllerCallback(next));
                }
            }
            logd("After initial, controllers count is " + this.mActiveControllers.size());
            this.mIsInitialized = true;
        }
    }

    private void updateActiveControllers() {
        logd("updateActiveControllers");
        for (MediaController next : this.mMediaManager.getActiveSessions((ComponentName) null)) {
            logd("controller package:  " + next.getPackageName());
            if (!(next.getPlaybackState() == null || next.getMetadata() == null || isInActiveControllers(next))) {
                this.mActiveControllers.add(next);
                logd("updateActiveControllers: add controller: " + next.getPackageName());
                next.registerCallback(new MediaControllerCallback(next));
            }
        }
        logd("After update controllers, controllers count is " + this.mActiveControllers.size());
    }

    private boolean isInActiveControllers(MediaController mediaController) {
        Iterator<MediaController> it = this.mActiveControllers.iterator();
        while (it.hasNext()) {
            String packageName = it.next().getPackageName();
            if (packageName != null && packageName.equals(mediaController.getPackageName())) {
                return true;
            }
        }
        return false;
    }

    public synchronized void finish() {
        if (DEBUG) {
            Log.d("CLI-QSMV-CliMediaPreprocessor", "finish() this: " + this + "  mIsInitialized: " + this.mIsInitialized);
        }
        if (this.mIsInitialized) {
            this.mIsInitialized = false;
            this.mContext.unregisterReceiver(this.mBroadcastReceiver);
            this.mMediaManager.removeOnActiveSessionsChangedListener(this);
            this.mActiveControllers.clear();
            this.mLastRemovedControllerMap.clear();
            this.mControllerNotificationMap.clear();
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:37:0x010e, code lost:
        return null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:84:0x01bb, code lost:
        return r10;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized android.service.notification.StatusBarNotification preprocessNotification(android.service.notification.StatusBarNotification r10, boolean r11) {
        /*
            r9 = this;
            monitor-enter(r9)
            java.lang.String r0 = r10.getPackageName()     // Catch:{ all -> 0x01bc }
            boolean r1 = r9.isMediaStyle(r10)     // Catch:{ all -> 0x01bc }
            android.app.Notification r2 = r10.getNotification()     // Catch:{ all -> 0x01bc }
            boolean r2 = r2.isMediaNotification()     // Catch:{ all -> 0x01bc }
            boolean r3 = DEBUG     // Catch:{ all -> 0x01bc }
            if (r3 == 0) goto L_0x005b
            java.lang.String r3 = "CLI-QSMV-CliMediaPreprocessor"
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ all -> 0x01bc }
            r4.<init>()     // Catch:{ all -> 0x01bc }
            java.lang.String r5 = "preprocessNotification - controllers size: "
            r4.append(r5)     // Catch:{ all -> 0x01bc }
            java.util.HashSet<android.media.session.MediaController> r5 = r9.mActiveControllers     // Catch:{ all -> 0x01bc }
            int r5 = r5.size()     // Catch:{ all -> 0x01bc }
            r4.append(r5)     // Catch:{ all -> 0x01bc }
            java.lang.String r5 = " packageName: "
            r4.append(r5)     // Catch:{ all -> 0x01bc }
            r4.append(r0)     // Catch:{ all -> 0x01bc }
            java.lang.String r5 = " isNotificationRemoval: "
            r4.append(r5)     // Catch:{ all -> 0x01bc }
            r4.append(r11)     // Catch:{ all -> 0x01bc }
            java.lang.String r5 = " isMediaStyle:"
            r4.append(r5)     // Catch:{ all -> 0x01bc }
            r4.append(r1)     // Catch:{ all -> 0x01bc }
            java.lang.String r5 = " isStandardMedia:"
            r4.append(r5)     // Catch:{ all -> 0x01bc }
            r4.append(r2)     // Catch:{ all -> 0x01bc }
            java.lang.String r5 = " initialized: "
            r4.append(r5)     // Catch:{ all -> 0x01bc }
            boolean r5 = r9.mIsInitialized     // Catch:{ all -> 0x01bc }
            r4.append(r5)     // Catch:{ all -> 0x01bc }
            java.lang.String r4 = r4.toString()     // Catch:{ all -> 0x01bc }
            android.util.Log.d(r3, r4)     // Catch:{ all -> 0x01bc }
        L_0x005b:
            if (r2 == 0) goto L_0x0086
            java.util.HashSet<android.media.session.MediaController> r2 = r9.mActiveControllers     // Catch:{ all -> 0x01bc }
            java.util.Iterator r2 = r2.iterator()     // Catch:{ all -> 0x01bc }
        L_0x0063:
            boolean r3 = r2.hasNext()     // Catch:{ all -> 0x01bc }
            if (r3 == 0) goto L_0x0086
            java.lang.Object r3 = r2.next()     // Catch:{ all -> 0x01bc }
            android.media.session.MediaController r3 = (android.media.session.MediaController) r3     // Catch:{ all -> 0x01bc }
            java.lang.String r4 = r3.getPackageName()     // Catch:{ all -> 0x01bc }
            boolean r4 = r4.equals(r0)     // Catch:{ all -> 0x01bc }
            if (r4 == 0) goto L_0x0063
            android.service.notification.StatusBarNotification r10 = r9.processStandardMediaNotification(r10, r3)     // Catch:{ all -> 0x01bc }
            android.app.Notification r11 = r10.getNotification()     // Catch:{ all -> 0x01bc }
            r9.updateMediaActive(r11, r3)     // Catch:{ all -> 0x01bc }
            monitor-exit(r9)
            return r10
        L_0x0086:
            r9.updateActiveControllers()     // Catch:{ all -> 0x01bc }
            boolean r2 = r9.mIsInitialized     // Catch:{ all -> 0x01bc }
            r3 = 0
            if (r2 == 0) goto L_0x018f
            if (r1 != 0) goto L_0x018f
            boolean r2 = DEBUG     // Catch:{ all -> 0x01bc }
            if (r2 == 0) goto L_0x00b0
            java.lang.String r2 = "CLI-QSMV-CliMediaPreprocessor"
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ all -> 0x01bc }
            r4.<init>()     // Catch:{ all -> 0x01bc }
            java.lang.String r5 = "Active Controllers size: "
            r4.append(r5)     // Catch:{ all -> 0x01bc }
            java.util.HashSet<android.media.session.MediaController> r5 = r9.mActiveControllers     // Catch:{ all -> 0x01bc }
            int r5 = r5.size()     // Catch:{ all -> 0x01bc }
            r4.append(r5)     // Catch:{ all -> 0x01bc }
            java.lang.String r4 = r4.toString()     // Catch:{ all -> 0x01bc }
            android.util.Log.d(r2, r4)     // Catch:{ all -> 0x01bc }
        L_0x00b0:
            java.util.HashSet<android.media.session.MediaController> r2 = r9.mActiveControllers     // Catch:{ all -> 0x01bc }
            java.util.Iterator r2 = r2.iterator()     // Catch:{ all -> 0x01bc }
        L_0x00b6:
            boolean r4 = r2.hasNext()     // Catch:{ all -> 0x01bc }
            if (r4 == 0) goto L_0x016d
            java.lang.Object r4 = r2.next()     // Catch:{ all -> 0x01bc }
            android.media.session.MediaController r4 = (android.media.session.MediaController) r4     // Catch:{ all -> 0x01bc }
            boolean r5 = DEBUG     // Catch:{ all -> 0x01bc }
            if (r5 == 0) goto L_0x00e0
            java.lang.String r6 = "CLI-QSMV-CliMediaPreprocessor"
            java.lang.StringBuilder r7 = new java.lang.StringBuilder     // Catch:{ all -> 0x01bc }
            r7.<init>()     // Catch:{ all -> 0x01bc }
            java.lang.String r8 = "controller packageName "
            r7.append(r8)     // Catch:{ all -> 0x01bc }
            java.lang.String r8 = r4.getPackageName()     // Catch:{ all -> 0x01bc }
            r7.append(r8)     // Catch:{ all -> 0x01bc }
            java.lang.String r7 = r7.toString()     // Catch:{ all -> 0x01bc }
            android.util.Log.d(r6, r7)     // Catch:{ all -> 0x01bc }
        L_0x00e0:
            java.lang.String r6 = r4.getPackageName()     // Catch:{ all -> 0x01bc }
            boolean r6 = r6.equals(r0)     // Catch:{ all -> 0x01bc }
            if (r6 == 0) goto L_0x00b6
            if (r11 != 0) goto L_0x010f
            java.util.HashMap<android.media.session.MediaController, android.service.notification.StatusBarNotification> r0 = r9.mControllerNotificationMap     // Catch:{ all -> 0x01bc }
            java.lang.Object r0 = r0.get(r4)     // Catch:{ all -> 0x01bc }
            android.service.notification.StatusBarNotification r0 = (android.service.notification.StatusBarNotification) r0     // Catch:{ all -> 0x01bc }
            if (r0 == 0) goto L_0x010f
            java.lang.String r0 = r0.getKey()     // Catch:{ all -> 0x01bc }
            java.lang.String r1 = r10.getKey()     // Catch:{ all -> 0x01bc }
            boolean r0 = r0.equals(r1)     // Catch:{ all -> 0x01bc }
            if (r0 != 0) goto L_0x010f
            if (r5 == 0) goto L_0x010d
            java.lang.String r10 = "CLI-QSMV-CliMediaPreprocessor"
            java.lang.String r11 = "Preprocess aborted. There is already a media notification for this package"
            android.util.Log.d(r10, r11)     // Catch:{ all -> 0x01bc }
        L_0x010d:
            monitor-exit(r9)
            return r3
        L_0x010f:
            android.app.Notification r0 = r10.getNotification()     // Catch:{ all -> 0x01bc }
            android.widget.RemoteViews r0 = r0.contentView     // Catch:{ all -> 0x01bc }
            if (r0 != 0) goto L_0x0120
            java.lang.String r10 = "CLI-QSMV-CliMediaPreprocessor"
            java.lang.String r11 = "The remoteview is null."
            android.util.Log.w(r10, r11)     // Catch:{ all -> 0x01bc }
            monitor-exit(r9)
            return r3
        L_0x0120:
            java.lang.Class r0 = r0.getClass()     // Catch:{ all -> 0x01bc }
            java.lang.Class<android.widget.RemoteViews> r1 = android.widget.RemoteViews.class
            boolean r0 = r0.equals(r1)     // Catch:{ all -> 0x01bc }
            if (r0 == 0) goto L_0x0164
            android.service.notification.StatusBarNotification r10 = r9.createInternalNotification(r10, r4)     // Catch:{ all -> 0x01bc }
            android.app.Notification r0 = r10.getNotification()     // Catch:{ all -> 0x01bc }
            android.content.Context r1 = r9.mContext     // Catch:{ all -> 0x01bc }
            java.lang.String r1 = r9.processTitle(r0, r1)     // Catch:{ all -> 0x01bc }
            android.os.Bundle r0 = r0.extras     // Catch:{ all -> 0x01bc }
            java.lang.String r0 = r9.processText(r0)     // Catch:{ all -> 0x01bc }
            boolean r1 = android.text.TextUtils.isEmpty(r1)     // Catch:{ all -> 0x01bc }
            if (r1 != 0) goto L_0x014c
            boolean r0 = android.text.TextUtils.isEmpty(r0)     // Catch:{ all -> 0x01bc }
            if (r0 == 0) goto L_0x0157
        L_0x014c:
            java.lang.String r0 = "CLI-QSMV-CliMediaPreprocessor"
            java.lang.String r1 = "Title or content is empty, decide it is not a media notification."
            android.util.Log.w(r0, r1)     // Catch:{ all -> 0x01bc }
            if (r11 != 0) goto L_0x0157
            monitor-exit(r9)
            return r3
        L_0x0157:
            if (r11 == 0) goto L_0x015f
            java.util.HashMap<android.media.session.MediaController, android.service.notification.StatusBarNotification> r11 = r9.mControllerNotificationMap     // Catch:{ all -> 0x01bc }
            r11.remove(r4)     // Catch:{ all -> 0x01bc }
            goto L_0x0164
        L_0x015f:
            java.util.HashMap<android.media.session.MediaController, android.service.notification.StatusBarNotification> r11 = r9.mControllerNotificationMap     // Catch:{ all -> 0x01bc }
            r11.put(r4, r10)     // Catch:{ all -> 0x01bc }
        L_0x0164:
            android.app.Notification r11 = r10.getNotification()     // Catch:{ all -> 0x01bc }
            r9.updateMediaActive(r11, r4)     // Catch:{ all -> 0x01bc }
            monitor-exit(r9)
            return r10
        L_0x016d:
            if (r11 == 0) goto L_0x018f
            java.util.HashMap<java.lang.String, android.media.session.MediaController> r2 = r9.mLastRemovedControllerMap     // Catch:{ all -> 0x01bc }
            java.lang.Object r2 = r2.get(r0)     // Catch:{ all -> 0x01bc }
            android.media.session.MediaController r2 = (android.media.session.MediaController) r2     // Catch:{ all -> 0x01bc }
            if (r2 == 0) goto L_0x018f
            boolean r11 = DEBUG     // Catch:{ all -> 0x01bc }
            if (r11 == 0) goto L_0x0184
            java.lang.String r11 = "CLI-QSMV-CliMediaPreprocessor"
            java.lang.String r0 = "Removed controller found."
            android.util.Log.d(r11, r0)     // Catch:{ all -> 0x01bc }
        L_0x0184:
            java.util.HashMap<android.media.session.MediaController, android.service.notification.StatusBarNotification> r11 = r9.mControllerNotificationMap     // Catch:{ all -> 0x01bc }
            r11.remove(r2)     // Catch:{ all -> 0x01bc }
            android.service.notification.StatusBarNotification r10 = r9.createInternalNotification(r10, r2)     // Catch:{ all -> 0x01bc }
            monitor-exit(r9)
            return r10
        L_0x018f:
            if (r1 == 0) goto L_0x01b6
            if (r11 != 0) goto L_0x01b6
            java.util.HashSet<android.media.session.MediaController> r11 = r9.mActiveControllers     // Catch:{ all -> 0x01bc }
            java.util.Iterator r11 = r11.iterator()     // Catch:{ all -> 0x01bc }
        L_0x0199:
            boolean r2 = r11.hasNext()     // Catch:{ all -> 0x01bc }
            if (r2 == 0) goto L_0x01b6
            java.lang.Object r2 = r11.next()     // Catch:{ all -> 0x01bc }
            android.media.session.MediaController r2 = (android.media.session.MediaController) r2     // Catch:{ all -> 0x01bc }
            java.lang.String r4 = r2.getPackageName()     // Catch:{ all -> 0x01bc }
            boolean r4 = r4.equals(r0)     // Catch:{ all -> 0x01bc }
            if (r4 == 0) goto L_0x0199
            android.app.Notification r11 = r10.getNotification()     // Catch:{ all -> 0x01bc }
            r9.updateMediaActive(r11, r2)     // Catch:{ all -> 0x01bc }
        L_0x01b6:
            if (r1 == 0) goto L_0x01b9
            goto L_0x01ba
        L_0x01b9:
            r10 = r3
        L_0x01ba:
            monitor-exit(r9)
            return r10
        L_0x01bc:
            r10 = move-exception
            monitor-exit(r9)
            throw r10
        */
        throw new UnsupportedOperationException("Method not decompiled: com.motorola.systemui.cli.media.CliMediaPreprocessor.preprocessNotification(android.service.notification.StatusBarNotification, boolean):android.service.notification.StatusBarNotification");
    }

    private StatusBarNotification processStandardMediaNotification(StatusBarNotification statusBarNotification, MediaController mediaController) {
        initBitmapExtra(this.mContext, mediaController.getMetadata(), statusBarNotification.getNotification());
        return statusBarNotification;
    }

    /* JADX WARNING: type inference failed for: r5v3, types: [android.os.Parcelable] */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void initBitmapExtra(android.content.Context r4, android.media.MediaMetadata r5, android.app.Notification r6) {
        /*
            r3 = this;
            android.os.Bundle r0 = r6.extras
            java.lang.String r1 = "android.largeIcon"
            r2 = 0
            r0.putParcelable(r1, r2)
            java.lang.String r1 = "android.largeIcon.big"
            r0.putParcelable(r1, r2)
            java.lang.String r1 = "CLI-QSMV-CliMediaPreprocessor"
            if (r5 == 0) goto L_0x0028
            java.lang.String r2 = "android.media.metadata.ALBUM_ART"
            android.graphics.Bitmap r2 = r5.getBitmap(r2)
            if (r2 != 0) goto L_0x0028
            boolean r2 = DEBUG
            if (r2 == 0) goto L_0x0022
            java.lang.String r2 = "KEY_ALBUM_ART not found, falling back to metadata KEY_ART"
            android.util.Log.d(r1, r2)
        L_0x0022:
            java.lang.String r2 = "android.media.metadata.ART"
            android.graphics.Bitmap r2 = r5.getBitmap(r2)
        L_0x0028:
            if (r2 != 0) goto L_0x003c
            boolean r5 = DEBUG
            if (r5 == 0) goto L_0x0033
            java.lang.String r5 = "KEY_ART not found, falling back to notification EXTRA_PICTURE"
            android.util.Log.d(r1, r5)
        L_0x0033:
            java.lang.String r5 = "android.picture"
            android.os.Parcelable r5 = r0.getParcelable(r5)
            r2 = r5
            android.graphics.Bitmap r2 = (android.graphics.Bitmap) r2
        L_0x003c:
            if (r2 != 0) goto L_0x0042
            android.graphics.Bitmap r2 = r3.getBitmapFromNotificationView(r6, r4)
        L_0x0042:
            if (r2 == 0) goto L_0x0047
            r3.addPictureExtra(r0, r4, r2)
        L_0x0047:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.motorola.systemui.cli.media.CliMediaPreprocessor.initBitmapExtra(android.content.Context, android.media.MediaMetadata, android.app.Notification):void");
    }

    private void addPictureExtra(Bundle bundle, Context context, Bitmap bitmap) {
        Resources resources = context.getResources();
        int dimensionPixelSize = resources.getDimensionPixelSize(R$dimen.cli_notification_max_media_photo_width);
        int dimensionPixelSize2 = resources.getDimensionPixelSize(R$dimen.cli_notification_max_media_photo_height);
        int height = bitmap.getHeight();
        int width = bitmap.getWidth();
        if (height > dimensionPixelSize2 || width > dimensionPixelSize) {
            if (DEBUG) {
                Log.d("CLI-QSMV-CliMediaPreprocessor", "Rescaling bitmap");
            }
            bitmap = new BitmapBuilder(bitmap).scale(dimensionPixelSize, dimensionPixelSize2).build();
        }
        bundle.putParcelable("android.picture", bitmap);
    }

    private Bitmap getBitmapFromNotificationView(Notification notification, Context context) {
        View view;
        try {
            RemoteViews remoteViews = notification.bigContentView;
            if (remoteViews != null) {
                view = remoteViews.apply(context, (ViewGroup) null);
            } else {
                RemoteViews remoteViews2 = notification.contentView;
                if (remoteViews2 != null) {
                    view = remoteViews2.apply(context, (ViewGroup) null);
                }
                view = null;
            }
        } catch (Resources.NotFoundException | InflateException | SecurityException unused) {
            if (DEBUG) {
                Log.d("CLI-QSMV-CliMediaPreprocessor", "getBitmapFromNotificationView: View can't be inflated");
            }
        }
        if (view != null) {
            return getChildImageView(view);
        }
        return null;
    }

    private Bitmap getChildImageView(View view) {
        if (!(view instanceof ViewGroup)) {
            return null;
        }
        ViewGroup viewGroup = (ViewGroup) view;
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            View childAt = viewGroup.getChildAt(i);
            if (childAt instanceof ImageView) {
                return new BitmapBuilder(((ImageView) childAt).getDrawable()).build();
            }
        }
        return null;
    }

    public boolean isMediaStyle(StatusBarNotification statusBarNotification) {
        String string = statusBarNotification.getNotification().extras.getString("android.template");
        return "android.app.Notification$InternalMediaStyle".equals(string) || "android.app.Notification$MediaStyle".equals(string);
    }

    public synchronized void onActiveSessionsChanged(List<MediaController> list) {
        if (DEBUG) {
            StringBuilder sb = new StringBuilder();
            sb.append("onActiveSessionsChanged - controllers size: ");
            sb.append(list != null ? Integer.valueOf(list.size()) : "<null>");
            sb.append(" controllers count: ");
            sb.append(this.mActiveControllers.size());
            sb.append(" initialized: ");
            sb.append(this.mIsInitialized);
            Log.d("CLI-QSMV-CliMediaPreprocessor", sb.toString());
        }
        if (this.mIsInitialized && list != null) {
            for (MediaController next : list) {
                if (DEBUG) {
                    Log.d("CLI-QSMV-CliMediaPreprocessor", "controller package:  " + next.getPackageName());
                }
                if (!(next.getPlaybackState() == null || next.getMetadata() == null || this.mActiveControllers.contains(next))) {
                    this.mActiveControllers.add(next);
                    next.registerCallback(new MediaControllerCallback(next));
                }
            }
            if (DEBUG) {
                Log.d("CLI-QSMV-CliMediaPreprocessor", "New controllers count:" + this.mActiveControllers.size());
            }
            HashSet hashSet = new HashSet(this.mActiveControllers);
            hashSet.removeAll(list);
            this.mActiveControllers.removeAll(hashSet);
            Iterator it = hashSet.iterator();
            while (it.hasNext()) {
                MediaController mediaController = (MediaController) it.next();
                this.mControllerNotificationMap.remove(mediaController);
                this.mLastRemovedControllerMap.put(mediaController.getPackageName(), mediaController);
            }
            Iterator<MediaController> it2 = this.mActiveControllers.iterator();
            while (it2.hasNext()) {
                this.mLastRemovedControllerMap.remove(it2.next().getPackageName());
            }
            if (DEBUG) {
                Log.d("CLI-QSMV-CliMediaPreprocessor", "Final controllers count:" + this.mActiveControllers.size());
            }
        }
    }

    /* access modifiers changed from: private */
    public synchronized boolean refreshNotification(MediaController mediaController) {
        StatusBarNotification statusBarNotification = this.mControllerNotificationMap.get(mediaController);
        if (statusBarNotification != null) {
            this.mIMediaNotification.onNotificationPosted(createInternalNotification(statusBarNotification, mediaController));
            return true;
        }
        return this.mActiveControllers.contains(mediaController);
    }

    private StatusBarNotification createInternalNotification(StatusBarNotification statusBarNotification, MediaController mediaController) {
        MediaController mediaController2 = mediaController;
        int i = 0;
        try {
            try {
                i = ((Integer) StatusBarNotification.class.getDeclaredMethod("getUid", new Class[0]).invoke(statusBarNotification, new Object[0])).intValue();
            } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException unused) {
                Log.e("CLI-QSMV-CliMediaPreprocessor", "Notification uid could not be retrieved. This may cause issues when removing the notification.");
                int i2 = i;
                InternalMediaNotification internalMediaNotification = new InternalMediaNotification(this.mContext, mediaController2, statusBarNotification.getNotification(), this.mCliMediaViewPagerOwn);
                updateMediaActive(internalMediaNotification, mediaController2);
                internalMediaNotification.extras.putParcelable("android.mediaSession", mediaController.getSessionToken());
                return new StatusBarNotification(mediaController.getPackageName(), mediaController.getPackageName(), statusBarNotification.getId(), statusBarNotification.getTag(), i2, 0, 0, internalMediaNotification, statusBarNotification.getUser(), statusBarNotification.getPostTime());
            }
        } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException unused2) {
            StatusBarNotification statusBarNotification2 = statusBarNotification;
            Log.e("CLI-QSMV-CliMediaPreprocessor", "Notification uid could not be retrieved. This may cause issues when removing the notification.");
            int i22 = i;
            InternalMediaNotification internalMediaNotification2 = new InternalMediaNotification(this.mContext, mediaController2, statusBarNotification.getNotification(), this.mCliMediaViewPagerOwn);
            updateMediaActive(internalMediaNotification2, mediaController2);
            internalMediaNotification2.extras.putParcelable("android.mediaSession", mediaController.getSessionToken());
            return new StatusBarNotification(mediaController.getPackageName(), mediaController.getPackageName(), statusBarNotification.getId(), statusBarNotification.getTag(), i22, 0, 0, internalMediaNotification2, statusBarNotification.getUser(), statusBarNotification.getPostTime());
        }
        int i222 = i;
        InternalMediaNotification internalMediaNotification22 = new InternalMediaNotification(this.mContext, mediaController2, statusBarNotification.getNotification(), this.mCliMediaViewPagerOwn);
        updateMediaActive(internalMediaNotification22, mediaController2);
        internalMediaNotification22.extras.putParcelable("android.mediaSession", mediaController.getSessionToken());
        return new StatusBarNotification(mediaController.getPackageName(), mediaController.getPackageName(), statusBarNotification.getId(), statusBarNotification.getTag(), i222, 0, 0, internalMediaNotification22, statusBarNotification.getUser(), statusBarNotification.getPostTime());
    }

    private void updateMediaActive(Notification notification, MediaController mediaController) {
        if (mediaController != null && mediaController.getPlaybackState() != null) {
            Optional.ofNullable(mediaController.getPlaybackState()).ifPresent(new CliMediaPreprocessor$$ExternalSyntheticLambda0(notification));
        }
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$updateMediaActive$0(Notification notification, PlaybackState playbackState) {
        notification.extras.putBoolean("EXTRA_MUSIC_ACTIVE", playbackState.getState() == 3);
    }

    private static final class MediaControllerCallback extends MediaController.Callback {
        private MediaController mController;
        private int mCurrentState;
        private WeakReference<CliMediaPreprocessor> mMediaNotificationPreprocessor;

        private MediaControllerCallback(CliMediaPreprocessor cliMediaPreprocessor, MediaController mediaController) {
            this.mController = mediaController;
            this.mMediaNotificationPreprocessor = new WeakReference<>(cliMediaPreprocessor);
            Optional.ofNullable(mediaController.getPlaybackState()).ifPresent(new C2662xeb2ed343(this));
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$new$0(PlaybackState playbackState) {
            this.mCurrentState = playbackState.getState();
        }

        public void onMetadataChanged(MediaMetadata mediaMetadata) {
            if (CliMediaPreprocessor.DEBUG) {
                Log.d("CLI-QSMV-CliMediaPreprocessor", "onMetadataChanged - package: " + this.mController.getPackageName());
            }
            refreshNotification();
        }

        public void onPlaybackStateChanged(PlaybackState playbackState) {
            if (CliMediaPreprocessor.DEBUG) {
                Log.d("CLI-QSMV-CliMediaPreprocessor", "onPlaybackStateChanged - package: " + this.mController.getPackageName());
            }
            if (playbackState != null && playbackState.getState() != this.mCurrentState) {
                this.mCurrentState = playbackState.getState();
                refreshNotification();
            }
        }

        private void refreshNotification() {
            CliMediaPreprocessor cliMediaPreprocessor = (CliMediaPreprocessor) this.mMediaNotificationPreprocessor.get();
            if (cliMediaPreprocessor != null && !cliMediaPreprocessor.refreshNotification(this.mController)) {
                if (CliMediaPreprocessor.DEBUG) {
                    Log.d("CLI-QSMV-CliMediaPreprocessor", "Controller no longer tracked. Removing callback.");
                }
                this.mController.unregisterCallback(this);
            }
        }
    }

    private String processTitle(Notification notification, Context context) {
        CharSequence charSequence;
        if (notification.extras.containsKey("android.title.big")) {
            charSequence = notification.extras.getCharSequence("android.title.big");
        } else {
            charSequence = notification.extras.containsKey("android.title") ? notification.extras.getCharSequence("android.title") : null;
        }
        if (!TextUtils.isEmpty(charSequence)) {
            return charSequence.toString();
        }
        Log.w("CLI-QSMV-CliMediaPreprocessor", "Unable to get the title.");
        return "";
    }

    private String processText(Bundle bundle) {
        CharSequence charSequence;
        if (bundle.containsKey("android.bigText")) {
            charSequence = bundle.getCharSequence("android.bigText");
        } else {
            charSequence = bundle.containsKey("android.text") ? bundle.getCharSequence("android.text") : null;
        }
        if (!TextUtils.isEmpty(charSequence)) {
            return charSequence.toString();
        }
        Log.i("CLI-QSMV-CliMediaPreprocessor", "Unable to get the text.");
        return "";
    }

    private void logd(String str) {
        if (DEBUG) {
            Log.d("CLI-QSMV-CliMediaPreprocessor", "[" + this.mCliMediaViewPagerOwn + "]: " + str);
        }
    }
}
