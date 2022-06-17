package com.motorola.systemui.desktop.overwrites.statusbar.phone;

import com.android.systemui.statusbar.NotificationLockscreenUserManager;
import com.android.systemui.statusbar.NotificationViewHierarchyManager;
import com.android.systemui.statusbar.notification.NotificationEntryManager;

public final class DesktopStatusBarNotificationPresenter_MembersInjector {
    public static void injectMViewHierarchyManager(DesktopStatusBarNotificationPresenter desktopStatusBarNotificationPresenter, NotificationViewHierarchyManager notificationViewHierarchyManager) {
        desktopStatusBarNotificationPresenter.mViewHierarchyManager = notificationViewHierarchyManager;
    }

    public static void injectMEntryManager(DesktopStatusBarNotificationPresenter desktopStatusBarNotificationPresenter, NotificationEntryManager notificationEntryManager) {
        desktopStatusBarNotificationPresenter.mEntryManager = notificationEntryManager;
    }

    public static void injectMLockscreenUserManager(DesktopStatusBarNotificationPresenter desktopStatusBarNotificationPresenter, NotificationLockscreenUserManager notificationLockscreenUserManager) {
        desktopStatusBarNotificationPresenter.mLockscreenUserManager = notificationLockscreenUserManager;
    }
}
