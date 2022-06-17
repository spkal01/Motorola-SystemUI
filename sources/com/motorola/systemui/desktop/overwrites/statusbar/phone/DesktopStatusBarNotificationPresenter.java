package com.motorola.systemui.desktop.overwrites.statusbar.phone;

import android.view.View;
import com.android.systemui.Dependency;
import com.android.systemui.statusbar.NotificationLockscreenUserManager;
import com.android.systemui.statusbar.NotificationPresenter;
import com.android.systemui.statusbar.NotificationRemoteInputManager;
import com.android.systemui.statusbar.NotificationViewHierarchyManager;
import com.android.systemui.statusbar.notification.AboveShelfObserver;
import com.android.systemui.statusbar.notification.NotificationEntryManager;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import com.android.systemui.statusbar.notification.collection.inflation.NotificationRowBinderImpl;
import com.android.systemui.statusbar.notification.row.ActivatableNotificationView;
import com.android.systemui.statusbar.notification.row.ExpandableNotificationRow;
import com.android.systemui.statusbar.notification.stack.DesktopNotificationStackScrollLayoutController;
import com.android.systemui.statusbar.policy.ConfigurationController;
import com.motorola.systemui.desktop.DesktopSystemUIFactory;
import java.util.List;

public class DesktopStatusBarNotificationPresenter implements NotificationPresenter, NotificationRowBinderImpl.BindRowCallback, ConfigurationController.ConfigurationListener {
    private final AboveShelfObserver mAboveShelfObserver;
    private final int mDisplayId;
    protected NotificationEntryManager mEntryManager;
    protected NotificationLockscreenUserManager mLockscreenUserManager;
    private DesktopNotificationStackScrollLayoutController mNotificationStackScrollLayoutController;
    protected NotificationViewHierarchyManager mViewHierarchyManager;

    /* access modifiers changed from: private */
    public boolean canDismissLockScreen() {
        return true;
    }

    public boolean isCollapsing() {
        return false;
    }

    public boolean isDeviceInVrMode() {
        return false;
    }

    public boolean isPresenterFullyCollapsed() {
        return false;
    }

    public void onActivated(ActivatableNotificationView activatableNotificationView) {
    }

    public void onActivationReset(ActivatableNotificationView activatableNotificationView) {
    }

    public void onExpandClicked(NotificationEntry notificationEntry, View view, boolean z) {
    }

    public void updateMediaMetaData(boolean z, boolean z2) {
    }

    public DesktopStatusBarNotificationPresenter(int i, DesktopNotificationStackScrollLayoutController desktopNotificationStackScrollLayoutController) {
        this.mDisplayId = i;
        this.mAboveShelfObserver = new AboveShelfObserver(desktopNotificationStackScrollLayoutController.getView());
        DesktopSystemUIFactory.getDesktopFactory().getSysUIComponent().inject(this);
        this.mNotificationStackScrollLayoutController = desktopNotificationStackScrollLayoutController;
        this.mViewHierarchyManager.setUpWithPresenter(this, desktopNotificationStackScrollLayoutController.getNotificationListContainer());
        this.mEntryManager.setUpWithPresenter(this);
        this.mLockscreenUserManager.setUpWithPresenter(this);
        NotificationRemoteInputManager notificationRemoteInputManager = (NotificationRemoteInputManager) Dependency.get(NotificationRemoteInputManager.class);
        notificationRemoteInputManager.setUpWithCallback((NotificationRemoteInputManager.Callback) Dependency.get(NotificationRemoteInputManager.Callback.class), this.mNotificationStackScrollLayoutController.createDelegate());
        this.mEntryManager.addNotificationLifetimeExtenders(notificationRemoteInputManager.getLifetimeExtenders());
        ((ConfigurationController) Dependency.get(ConfigurationController.class)).addCallback(this);
    }

    public void updateNotificationViews(String str) {
        this.mViewHierarchyManager.updateNotificationViews();
        this.mNotificationStackScrollLayoutController.updateSectionBoundaries(str);
        this.mNotificationStackScrollLayoutController.updateFooter();
    }

    public void onUpdateRowStates() {
        this.mNotificationStackScrollLayoutController.onUpdateRowStates();
    }

    public void onBindRow(ExpandableNotificationRow expandableNotificationRow) {
        expandableNotificationRow.setAboveShelfChangedListener(this.mAboveShelfObserver);
        expandableNotificationRow.setSecureStateProvider(new DesktopStatusBarNotificationPresenter$$ExternalSyntheticLambda0(this));
    }

    public void onUiModeChanged() {
        updateNotificationOnUiModeChanged();
    }

    private void updateNotificationOnUiModeChanged() {
        List<NotificationEntry> activeNotificationsForCurrentUser = this.mEntryManager.getActiveNotificationsForCurrentUser();
        for (int i = 0; i < activeNotificationsForCurrentUser.size(); i++) {
            ExpandableNotificationRow row = activeNotificationsForCurrentUser.get(i).getRow();
            if (row != null) {
                row.onUiModeChanged();
            }
        }
    }

    public void onOverlayChanged() {
        updateNotificationsOnDensityOrFontScaleChanged();
    }

    public void onDensityOrFontScaleChanged() {
        updateNotificationsOnDensityOrFontScaleChanged();
    }

    private void updateNotificationsOnDensityOrFontScaleChanged() {
        List<NotificationEntry> activeNotificationsForCurrentUser = this.mEntryManager.getActiveNotificationsForCurrentUser();
        for (int i = 0; i < activeNotificationsForCurrentUser.size(); i++) {
            activeNotificationsForCurrentUser.get(i).onDensityOrFontScaleChanged();
        }
    }
}
