package com.android.systemui.statusbar.phone;

import android.app.NotificationChannel;
import android.content.ComponentName;
import android.content.Context;
import android.os.RemoteException;
import android.os.UserHandle;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import com.android.systemui.Dependency;
import com.android.systemui.plugins.NotificationListenerController;
import com.android.systemui.plugins.PluginListener;
import com.android.systemui.shared.plugins.PluginManager;
import java.util.ArrayList;
import java.util.Iterator;

public class NotificationListenerWithPlugins extends NotificationListenerService implements PluginListener<NotificationListenerController> {
    private boolean mConnected;
    private ArrayList<NotificationListenerController> mPlugins = new ArrayList<>();

    public void registerAsSystemService(Context context, ComponentName componentName, int i) throws RemoteException {
        super.registerAsSystemService(context, componentName, i);
        ((PluginManager) Dependency.get(PluginManager.class)).addPluginListener(this, NotificationListenerController.class);
    }

    public void unregisterAsSystemService() throws RemoteException {
        super.unregisterAsSystemService();
        ((PluginManager) Dependency.get(PluginManager.class)).removePluginListener(this);
    }

    public StatusBarNotification[] getActiveNotifications() {
        StatusBarNotification[] activeNotifications = super.getActiveNotifications();
        Iterator<NotificationListenerController> it = this.mPlugins.iterator();
        while (it.hasNext()) {
            activeNotifications = it.next().getActiveNotifications(activeNotifications);
        }
        return activeNotifications;
    }

    public NotificationListenerService.RankingMap getCurrentRanking() {
        NotificationListenerService.RankingMap currentRanking = super.getCurrentRanking();
        Iterator<NotificationListenerController> it = this.mPlugins.iterator();
        while (it.hasNext()) {
            currentRanking = it.next().getCurrentRanking(currentRanking);
        }
        return currentRanking;
    }

    public void onPluginConnected() {
        this.mConnected = true;
        this.mPlugins.forEach(new NotificationListenerWithPlugins$$ExternalSyntheticLambda0(this));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onPluginConnected$0(NotificationListenerController notificationListenerController) {
        notificationListenerController.onListenerConnected(getProvider());
    }

    public boolean onPluginNotificationPosted(StatusBarNotification statusBarNotification, NotificationListenerService.RankingMap rankingMap) {
        Iterator<NotificationListenerController> it = this.mPlugins.iterator();
        while (it.hasNext()) {
            if (it.next().onNotificationPosted(statusBarNotification, rankingMap)) {
                return true;
            }
        }
        return false;
    }

    public boolean onPluginNotificationRemoved(StatusBarNotification statusBarNotification, NotificationListenerService.RankingMap rankingMap) {
        Iterator<NotificationListenerController> it = this.mPlugins.iterator();
        while (it.hasNext()) {
            if (it.next().onNotificationRemoved(statusBarNotification, rankingMap)) {
                return true;
            }
        }
        return false;
    }

    public boolean onPluginNotificationChannelModified(String str, UserHandle userHandle, NotificationChannel notificationChannel, int i) {
        Iterator<NotificationListenerController> it = this.mPlugins.iterator();
        while (it.hasNext()) {
            if (it.next().onNotificationChannelModified(str, userHandle, notificationChannel, i)) {
                return true;
            }
        }
        return false;
    }

    public NotificationListenerService.RankingMap onPluginRankingUpdate(NotificationListenerService.RankingMap rankingMap) {
        return getCurrentRanking();
    }

    public void onPluginConnected(NotificationListenerController notificationListenerController, Context context) {
        this.mPlugins.add(notificationListenerController);
        if (this.mConnected) {
            notificationListenerController.onListenerConnected(getProvider());
        }
    }

    public void onPluginDisconnected(NotificationListenerController notificationListenerController) {
        this.mPlugins.remove(notificationListenerController);
    }

    private NotificationListenerController.NotificationProvider getProvider() {
        return new NotificationListenerController.NotificationProvider() {
            public StatusBarNotification[] getActiveNotifications() {
                return NotificationListenerWithPlugins.super.getActiveNotifications();
            }

            public NotificationListenerService.RankingMap getRankingMap() {
                return NotificationListenerWithPlugins.super.getCurrentRanking();
            }

            public void addNotification(StatusBarNotification statusBarNotification) {
                NotificationListenerWithPlugins.this.onNotificationPosted(statusBarNotification, getRankingMap());
            }

            public void removeNotification(StatusBarNotification statusBarNotification) {
                NotificationListenerWithPlugins.this.onNotificationRemoved(statusBarNotification, getRankingMap());
            }

            public void updateRanking() {
                NotificationListenerWithPlugins.this.onNotificationRankingUpdate(getRankingMap());
            }
        };
    }
}
