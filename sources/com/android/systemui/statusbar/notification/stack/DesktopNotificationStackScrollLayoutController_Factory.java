package com.android.systemui.statusbar.notification.stack;

import android.content.res.Resources;
import android.view.LayoutInflater;
import com.android.internal.statusbar.IStatusBarService;
import com.android.systemui.statusbar.FeatureFlags;
import com.android.systemui.statusbar.NotificationLockscreenUserManager;
import com.android.systemui.statusbar.NotificationRemoteInputManager;
import com.android.systemui.statusbar.notification.DynamicPrivacyController;
import com.android.systemui.statusbar.notification.ForegroundServiceDismissalFeatureController;
import com.android.systemui.statusbar.notification.NotificationEntryManager;
import com.android.systemui.statusbar.notification.collection.NotifCollection;
import com.android.systemui.statusbar.notification.collection.NotifPipeline;
import com.android.systemui.statusbar.notification.collection.legacy.NotificationGroupManagerLegacy;
import com.android.systemui.statusbar.notification.collection.legacy.VisualStabilityManager;
import com.android.systemui.statusbar.notification.collection.render.GroupExpansionManager;
import com.android.systemui.statusbar.notification.collection.render.SectionHeaderController;
import com.android.systemui.statusbar.notification.stack.NotificationSwipeHelper;
import com.android.systemui.statusbar.phone.HeadsUpManagerPhone;
import com.android.systemui.statusbar.phone.StatusBar;
import com.android.systemui.statusbar.policy.ConfigurationController;
import com.android.systemui.statusbar.policy.ZenModeController;
import com.motorola.systemui.desktop.overwrites.statusbar.notification.DesktopHeadsUpController;
import dagger.internal.Factory;
import javax.inject.Provider;

public final class DesktopNotificationStackScrollLayoutController_Factory implements Factory<DesktopNotificationStackScrollLayoutController> {
    private final Provider<Boolean> allowLongPressProvider;
    private final Provider<ConfigurationController> configurationControllerProvider;
    private final Provider<DesktopHeadsUpController> desktopHeadsUpControllerProvider;
    private final Provider<DynamicPrivacyController> dynamicPrivacyControllerProvider;
    private final Provider<FeatureFlags> featureFlagsProvider;
    private final Provider<ForegroundServiceDismissalFeatureController> fgFeatureControllerProvider;
    private final Provider<ForegroundServiceSectionController> fgServicesSectionControllerProvider;
    private final Provider<GroupExpansionManager> groupManagerProvider;
    private final Provider<HeadsUpManagerPhone> headsUpManagerProvider;
    private final Provider<IStatusBarService> iStatusBarServiceProvider;
    private final Provider<LayoutInflater> layoutInflaterProvider;
    private final Provider<NotificationGroupManagerLegacy> legacyGroupManagerProvider;
    private final Provider<NotificationLockscreenUserManager> lockscreenUserManagerProvider;
    private final Provider<NotifCollection> notifCollectionProvider;
    private final Provider<NotifPipeline> notifPipelineProvider;
    private final Provider<NotificationEntryManager> notificationEntryManagerProvider;
    private final Provider<NotificationRoundnessManager> notificationRoundnessManagerProvider;
    private final Provider<NotificationSwipeHelper.Builder> notificationSwipeHelperBuilderProvider;
    private final Provider<NotificationRemoteInputManager> remoteInputManagerProvider;
    private final Provider<Resources> resourcesProvider;
    private final Provider<SectionHeaderController> silentHeaderControllerProvider;
    private final Provider<StatusBar> statusBarProvider;
    private final Provider<VisualStabilityManager> visualStabilityManagerProvider;
    private final Provider<ZenModeController> zenModeControllerProvider;

    public DesktopNotificationStackScrollLayoutController_Factory(Provider<Boolean> provider, Provider<HeadsUpManagerPhone> provider2, Provider<NotificationRoundnessManager> provider3, Provider<DynamicPrivacyController> provider4, Provider<ConfigurationController> provider5, Provider<ZenModeController> provider6, Provider<NotificationLockscreenUserManager> provider7, Provider<Resources> provider8, Provider<NotificationSwipeHelper.Builder> provider9, Provider<StatusBar> provider10, Provider<NotificationGroupManagerLegacy> provider11, Provider<GroupExpansionManager> provider12, Provider<SectionHeaderController> provider13, Provider<FeatureFlags> provider14, Provider<NotifPipeline> provider15, Provider<NotifCollection> provider16, Provider<NotificationEntryManager> provider17, Provider<IStatusBarService> provider18, Provider<ForegroundServiceDismissalFeatureController> provider19, Provider<ForegroundServiceSectionController> provider20, Provider<LayoutInflater> provider21, Provider<NotificationRemoteInputManager> provider22, Provider<DesktopHeadsUpController> provider23, Provider<VisualStabilityManager> provider24) {
        this.allowLongPressProvider = provider;
        this.headsUpManagerProvider = provider2;
        this.notificationRoundnessManagerProvider = provider3;
        this.dynamicPrivacyControllerProvider = provider4;
        this.configurationControllerProvider = provider5;
        this.zenModeControllerProvider = provider6;
        this.lockscreenUserManagerProvider = provider7;
        this.resourcesProvider = provider8;
        this.notificationSwipeHelperBuilderProvider = provider9;
        this.statusBarProvider = provider10;
        this.legacyGroupManagerProvider = provider11;
        this.groupManagerProvider = provider12;
        this.silentHeaderControllerProvider = provider13;
        this.featureFlagsProvider = provider14;
        this.notifPipelineProvider = provider15;
        this.notifCollectionProvider = provider16;
        this.notificationEntryManagerProvider = provider17;
        this.iStatusBarServiceProvider = provider18;
        this.fgFeatureControllerProvider = provider19;
        this.fgServicesSectionControllerProvider = provider20;
        this.layoutInflaterProvider = provider21;
        this.remoteInputManagerProvider = provider22;
        this.desktopHeadsUpControllerProvider = provider23;
        this.visualStabilityManagerProvider = provider24;
    }

    public DesktopNotificationStackScrollLayoutController get() {
        return newInstance(this.allowLongPressProvider.get().booleanValue(), this.headsUpManagerProvider.get(), this.notificationRoundnessManagerProvider.get(), this.dynamicPrivacyControllerProvider.get(), this.configurationControllerProvider.get(), this.zenModeControllerProvider.get(), this.lockscreenUserManagerProvider.get(), this.resourcesProvider.get(), this.notificationSwipeHelperBuilderProvider.get(), this.statusBarProvider.get(), this.legacyGroupManagerProvider.get(), this.groupManagerProvider.get(), this.silentHeaderControllerProvider.get(), this.featureFlagsProvider.get(), this.notifPipelineProvider.get(), this.notifCollectionProvider.get(), this.notificationEntryManagerProvider.get(), this.iStatusBarServiceProvider.get(), this.fgFeatureControllerProvider.get(), this.fgServicesSectionControllerProvider.get(), this.layoutInflaterProvider.get(), this.remoteInputManagerProvider.get(), this.desktopHeadsUpControllerProvider.get(), this.visualStabilityManagerProvider.get());
    }

    public static DesktopNotificationStackScrollLayoutController_Factory create(Provider<Boolean> provider, Provider<HeadsUpManagerPhone> provider2, Provider<NotificationRoundnessManager> provider3, Provider<DynamicPrivacyController> provider4, Provider<ConfigurationController> provider5, Provider<ZenModeController> provider6, Provider<NotificationLockscreenUserManager> provider7, Provider<Resources> provider8, Provider<NotificationSwipeHelper.Builder> provider9, Provider<StatusBar> provider10, Provider<NotificationGroupManagerLegacy> provider11, Provider<GroupExpansionManager> provider12, Provider<SectionHeaderController> provider13, Provider<FeatureFlags> provider14, Provider<NotifPipeline> provider15, Provider<NotifCollection> provider16, Provider<NotificationEntryManager> provider17, Provider<IStatusBarService> provider18, Provider<ForegroundServiceDismissalFeatureController> provider19, Provider<ForegroundServiceSectionController> provider20, Provider<LayoutInflater> provider21, Provider<NotificationRemoteInputManager> provider22, Provider<DesktopHeadsUpController> provider23, Provider<VisualStabilityManager> provider24) {
        return new DesktopNotificationStackScrollLayoutController_Factory(provider, provider2, provider3, provider4, provider5, provider6, provider7, provider8, provider9, provider10, provider11, provider12, provider13, provider14, provider15, provider16, provider17, provider18, provider19, provider20, provider21, provider22, provider23, provider24);
    }

    public static DesktopNotificationStackScrollLayoutController newInstance(boolean z, HeadsUpManagerPhone headsUpManagerPhone, NotificationRoundnessManager notificationRoundnessManager, DynamicPrivacyController dynamicPrivacyController, ConfigurationController configurationController, ZenModeController zenModeController, NotificationLockscreenUserManager notificationLockscreenUserManager, Resources resources, Object obj, StatusBar statusBar, NotificationGroupManagerLegacy notificationGroupManagerLegacy, GroupExpansionManager groupExpansionManager, SectionHeaderController sectionHeaderController, FeatureFlags featureFlags, NotifPipeline notifPipeline, NotifCollection notifCollection, NotificationEntryManager notificationEntryManager, IStatusBarService iStatusBarService, ForegroundServiceDismissalFeatureController foregroundServiceDismissalFeatureController, ForegroundServiceSectionController foregroundServiceSectionController, LayoutInflater layoutInflater, NotificationRemoteInputManager notificationRemoteInputManager, DesktopHeadsUpController desktopHeadsUpController, VisualStabilityManager visualStabilityManager) {
        return new DesktopNotificationStackScrollLayoutController(z, headsUpManagerPhone, notificationRoundnessManager, dynamicPrivacyController, configurationController, zenModeController, notificationLockscreenUserManager, resources, (NotificationSwipeHelper.Builder) obj, statusBar, notificationGroupManagerLegacy, groupExpansionManager, sectionHeaderController, featureFlags, notifPipeline, notifCollection, notificationEntryManager, iStatusBarService, foregroundServiceDismissalFeatureController, foregroundServiceSectionController, layoutInflater, notificationRemoteInputManager, desktopHeadsUpController, visualStabilityManager);
    }
}
