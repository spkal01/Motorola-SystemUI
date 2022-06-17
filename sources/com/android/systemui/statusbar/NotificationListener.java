package com.android.systemui.statusbar;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.ShortcutInfo;
import android.os.Handler;
import android.os.RemoteException;
import android.os.UserHandle;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;
import com.android.systemui.statusbar.phone.NotificationListenerWithPlugins;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@SuppressLint({"OverrideAbstract"})
public class NotificationListener extends NotificationListenerWithPlugins {
    private final Context mContext;
    private final Handler mMainHandler;
    private final List<NotificationHandler> mNotificationHandlers = new ArrayList();
    private final NotificationManager mNotificationManager;
    private final ArrayList<NotificationSettingsListener> mSettingsListeners = new ArrayList<>();

    public interface NotificationHandler {
        void onNotificationChannelModified(String str, UserHandle userHandle, NotificationChannel notificationChannel, int i) {
        }

        void onNotificationPosted(StatusBarNotification statusBarNotification, NotificationListenerService.RankingMap rankingMap);

        void onNotificationRankingUpdate(NotificationListenerService.RankingMap rankingMap);

        void onNotificationRemoved(StatusBarNotification statusBarNotification, NotificationListenerService.RankingMap rankingMap, int i);

        void onNotificationsInitialized();
    }

    public interface NotificationSettingsListener {
        void onStatusBarIconsBehaviorChanged(boolean z) {
        }
    }

    public NotificationListener(Context context, NotificationManager notificationManager, Handler handler) {
        this.mContext = context;
        this.mNotificationManager = notificationManager;
        this.mMainHandler = handler;
    }

    public void addNotificationHandler(NotificationHandler notificationHandler) {
        if (!this.mNotificationHandlers.contains(notificationHandler)) {
            this.mNotificationHandlers.add(notificationHandler);
            return;
        }
        throw new IllegalArgumentException("Listener is already added");
    }

    public void addNotificationSettingsListener(NotificationSettingsListener notificationSettingsListener) {
        this.mSettingsListeners.add(notificationSettingsListener);
    }

    public void onListenerConnected() {
        onPluginConnected();
        StatusBarNotification[] activeNotifications = getActiveNotifications();
        if (activeNotifications == null) {
            Log.w("NotificationListener", "onListenerConnected unable to get active notifications.");
            return;
        }
        this.mMainHandler.post(new NotificationListener$$ExternalSyntheticLambda4(this, activeNotifications, getCurrentRanking()));
        onSilentStatusBarIconsVisibilityChanged(this.mNotificationManager.shouldHideSilentStatusBarIcons());
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onListenerConnected$0(StatusBarNotification[] statusBarNotificationArr, NotificationListenerService.RankingMap rankingMap) {
        ArrayList arrayList = new ArrayList();
        for (StatusBarNotification key : statusBarNotificationArr) {
            arrayList.add(getRankingOrTemporaryStandIn(rankingMap, key.getKey()));
        }
        NotificationListenerService.RankingMap rankingMap2 = new NotificationListenerService.RankingMap((NotificationListenerService.Ranking[]) arrayList.toArray(new NotificationListenerService.Ranking[0]));
        for (StatusBarNotification statusBarNotification : statusBarNotificationArr) {
            for (NotificationHandler onNotificationPosted : this.mNotificationHandlers) {
                onNotificationPosted.onNotificationPosted(statusBarNotification, rankingMap2);
            }
        }
        for (NotificationHandler onNotificationsInitialized : this.mNotificationHandlers) {
            onNotificationsInitialized.onNotificationsInitialized();
        }
    }

    public void onNotificationPosted(StatusBarNotification statusBarNotification, NotificationListenerService.RankingMap rankingMap) {
        if (statusBarNotification != null && !onPluginNotificationPosted(statusBarNotification, rankingMap)) {
            this.mMainHandler.post(new NotificationListener$$ExternalSyntheticLambda1(this, statusBarNotification, rankingMap));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onNotificationPosted$1(StatusBarNotification statusBarNotification, NotificationListenerService.RankingMap rankingMap) {
        RemoteInputController.processForRemoteInput(statusBarNotification.getNotification(), this.mContext);
        for (NotificationHandler onNotificationPosted : this.mNotificationHandlers) {
            onNotificationPosted.onNotificationPosted(statusBarNotification, rankingMap);
        }
    }

    public void onNotificationRemoved(StatusBarNotification statusBarNotification, NotificationListenerService.RankingMap rankingMap, int i) {
        if (statusBarNotification != null && !onPluginNotificationRemoved(statusBarNotification, rankingMap)) {
            this.mMainHandler.post(new NotificationListener$$ExternalSyntheticLambda2(this, statusBarNotification, rankingMap, i));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onNotificationRemoved$2(StatusBarNotification statusBarNotification, NotificationListenerService.RankingMap rankingMap, int i) {
        for (NotificationHandler onNotificationRemoved : this.mNotificationHandlers) {
            onNotificationRemoved.onNotificationRemoved(statusBarNotification, rankingMap, i);
        }
    }

    public void onNotificationRemoved(StatusBarNotification statusBarNotification, NotificationListenerService.RankingMap rankingMap) {
        onNotificationRemoved(statusBarNotification, rankingMap, 0);
    }

    public void onNotificationRankingUpdate(NotificationListenerService.RankingMap rankingMap) {
        if (rankingMap != null) {
            this.mMainHandler.post(new NotificationListener$$ExternalSyntheticLambda0(this, onPluginRankingUpdate(rankingMap)));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onNotificationRankingUpdate$3(NotificationListenerService.RankingMap rankingMap) {
        for (NotificationHandler onNotificationRankingUpdate : this.mNotificationHandlers) {
            onNotificationRankingUpdate.onNotificationRankingUpdate(rankingMap);
        }
    }

    public void onNotificationChannelModified(String str, UserHandle userHandle, NotificationChannel notificationChannel, int i) {
        if (!onPluginNotificationChannelModified(str, userHandle, notificationChannel, i)) {
            this.mMainHandler.post(new NotificationListener$$ExternalSyntheticLambda3(this, str, userHandle, notificationChannel, i));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onNotificationChannelModified$4(String str, UserHandle userHandle, NotificationChannel notificationChannel, int i) {
        for (NotificationHandler onNotificationChannelModified : this.mNotificationHandlers) {
            onNotificationChannelModified.onNotificationChannelModified(str, userHandle, notificationChannel, i);
        }
    }

    public void onSilentStatusBarIconsVisibilityChanged(boolean z) {
        Iterator<NotificationSettingsListener> it = this.mSettingsListeners.iterator();
        while (it.hasNext()) {
            it.next().onStatusBarIconsBehaviorChanged(z);
        }
    }

    public void registerAsSystemService() {
        try {
            registerAsSystemService(this.mContext, new ComponentName(this.mContext.getPackageName(), NotificationListener.class.getCanonicalName()), -1);
        } catch (RemoteException e) {
            Log.e("NotificationListener", "Unable to register notification listener", e);
        }
    }

    private static NotificationListenerService.Ranking getRankingOrTemporaryStandIn(NotificationListenerService.RankingMap rankingMap, String str) {
        NotificationListenerService.Ranking ranking = new NotificationListenerService.Ranking();
        if (rankingMap.getRanking(str, ranking)) {
            return ranking;
        }
        ArrayList arrayList = r0;
        ArrayList arrayList2 = new ArrayList();
        ArrayList arrayList3 = r0;
        ArrayList arrayList4 = new ArrayList();
        ArrayList arrayList5 = r0;
        ArrayList arrayList6 = new ArrayList();
        ArrayList arrayList7 = r0;
        ArrayList arrayList8 = new ArrayList();
        NotificationListenerService.Ranking ranking2 = ranking;
        ranking.populate(str, 0, false, 0, 0, 0, (CharSequence) null, (String) null, (NotificationChannel) null, arrayList, arrayList3, false, 0, false, 0, false, arrayList5, arrayList7, false, false, false, (ShortcutInfo) null, 0, false);
        return ranking2;
    }
}
