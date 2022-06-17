package com.android.systemui.statusbar.notification.init;

import android.service.notification.StatusBarNotification;
import com.android.p011wm.shell.bubbles.Bubbles;
import com.android.systemui.people.widget.PeopleSpaceWidgetManager;
import com.android.systemui.plugins.statusbar.NotificationSwipeActionHelper;
import com.android.systemui.statusbar.FeatureFlags;
import com.android.systemui.statusbar.NotificationListener;
import com.android.systemui.statusbar.NotificationPresenter;
import com.android.systemui.statusbar.notification.AnimatedImageNotificationManager;
import com.android.systemui.statusbar.notification.NotificationActivityStarter;
import com.android.systemui.statusbar.notification.NotificationClicker;
import com.android.systemui.statusbar.notification.NotificationEntryManager;
import com.android.systemui.statusbar.notification.NotificationListController;
import com.android.systemui.statusbar.notification.collection.NotifPipeline;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import com.android.systemui.statusbar.notification.collection.NotificationRankingManager;
import com.android.systemui.statusbar.notification.collection.TargetSdkResolver;
import com.android.systemui.statusbar.notification.collection.inflation.NotificationRowBinderImpl;
import com.android.systemui.statusbar.notification.collection.init.NotifPipelineInitializer;
import com.android.systemui.statusbar.notification.collection.legacy.NotificationGroupManagerLegacy;
import com.android.systemui.statusbar.notification.interruption.HeadsUpController;
import com.android.systemui.statusbar.notification.interruption.HeadsUpViewBinder;
import com.android.systemui.statusbar.notification.row.NotifBindPipelineInitializer;
import com.android.systemui.statusbar.notification.stack.NotificationListContainer;
import com.android.systemui.statusbar.phone.NotificationGroupAlertTransferHelper;
import com.android.systemui.statusbar.phone.StatusBar;
import com.android.systemui.statusbar.policy.DeviceProvisionedController;
import com.android.systemui.statusbar.policy.HeadsUpManager;
import com.android.systemui.statusbar.policy.RemoteInputUriController;
import dagger.Lazy;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.Optional;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

/* compiled from: NotificationsControllerImpl.kt */
public final class NotificationsControllerImpl implements NotificationsController {
    @NotNull
    private final AnimatedImageNotificationManager animatedImageNotificationManager;
    @NotNull
    private final NotificationClicker.Builder clickerBuilder;
    @NotNull
    private final DeviceProvisionedController deviceProvisionedController;
    @NotNull
    private final NotificationEntryManager entryManager;
    @NotNull
    private final FeatureFlags featureFlags;
    @NotNull
    private final NotificationGroupAlertTransferHelper groupAlertTransferHelper;
    @NotNull
    private final Lazy<NotificationGroupManagerLegacy> groupManagerLegacy;
    @NotNull
    private final HeadsUpController headsUpController;
    @NotNull
    private final HeadsUpManager headsUpManager;
    @NotNull
    private final HeadsUpViewBinder headsUpViewBinder;
    @NotNull
    private final NotificationRankingManager legacyRanker;
    @NotNull
    private final Lazy<NotifPipelineInitializer> newNotifPipeline;
    @NotNull
    private final NotifBindPipelineInitializer notifBindPipelineInitializer;
    @NotNull
    private final Lazy<NotifPipeline> notifPipeline;
    @NotNull
    private final NotificationListener notificationListener;
    @NotNull
    private final NotificationRowBinderImpl notificationRowBinder;
    @NotNull
    private final PeopleSpaceWidgetManager peopleSpaceWidgetManager;
    @NotNull
    private final RemoteInputUriController remoteInputUriController;
    @NotNull
    private final TargetSdkResolver targetSdkResolver;

    public NotificationsControllerImpl(@NotNull FeatureFlags featureFlags2, @NotNull NotificationListener notificationListener2, @NotNull NotificationEntryManager notificationEntryManager, @NotNull NotificationRankingManager notificationRankingManager, @NotNull Lazy<NotifPipeline> lazy, @NotNull TargetSdkResolver targetSdkResolver2, @NotNull Lazy<NotifPipelineInitializer> lazy2, @NotNull NotifBindPipelineInitializer notifBindPipelineInitializer2, @NotNull DeviceProvisionedController deviceProvisionedController2, @NotNull NotificationRowBinderImpl notificationRowBinderImpl, @NotNull RemoteInputUriController remoteInputUriController2, @NotNull Lazy<NotificationGroupManagerLegacy> lazy3, @NotNull NotificationGroupAlertTransferHelper notificationGroupAlertTransferHelper, @NotNull HeadsUpManager headsUpManager2, @NotNull HeadsUpController headsUpController2, @NotNull HeadsUpViewBinder headsUpViewBinder2, @NotNull NotificationClicker.Builder builder, @NotNull AnimatedImageNotificationManager animatedImageNotificationManager2, @NotNull PeopleSpaceWidgetManager peopleSpaceWidgetManager2) {
        FeatureFlags featureFlags3 = featureFlags2;
        NotificationListener notificationListener3 = notificationListener2;
        NotificationEntryManager notificationEntryManager2 = notificationEntryManager;
        NotificationRankingManager notificationRankingManager2 = notificationRankingManager;
        Lazy<NotifPipeline> lazy4 = lazy;
        TargetSdkResolver targetSdkResolver3 = targetSdkResolver2;
        Lazy<NotifPipelineInitializer> lazy5 = lazy2;
        NotifBindPipelineInitializer notifBindPipelineInitializer3 = notifBindPipelineInitializer2;
        DeviceProvisionedController deviceProvisionedController3 = deviceProvisionedController2;
        NotificationRowBinderImpl notificationRowBinderImpl2 = notificationRowBinderImpl;
        RemoteInputUriController remoteInputUriController3 = remoteInputUriController2;
        Lazy<NotificationGroupManagerLegacy> lazy6 = lazy3;
        NotificationGroupAlertTransferHelper notificationGroupAlertTransferHelper2 = notificationGroupAlertTransferHelper;
        HeadsUpManager headsUpManager3 = headsUpManager2;
        HeadsUpViewBinder headsUpViewBinder3 = headsUpViewBinder2;
        Intrinsics.checkNotNullParameter(featureFlags3, "featureFlags");
        Intrinsics.checkNotNullParameter(notificationListener3, "notificationListener");
        Intrinsics.checkNotNullParameter(notificationEntryManager2, "entryManager");
        Intrinsics.checkNotNullParameter(notificationRankingManager2, "legacyRanker");
        Intrinsics.checkNotNullParameter(lazy4, "notifPipeline");
        Intrinsics.checkNotNullParameter(targetSdkResolver3, "targetSdkResolver");
        Intrinsics.checkNotNullParameter(lazy5, "newNotifPipeline");
        Intrinsics.checkNotNullParameter(notifBindPipelineInitializer3, "notifBindPipelineInitializer");
        Intrinsics.checkNotNullParameter(deviceProvisionedController3, "deviceProvisionedController");
        Intrinsics.checkNotNullParameter(notificationRowBinderImpl2, "notificationRowBinder");
        Intrinsics.checkNotNullParameter(remoteInputUriController3, "remoteInputUriController");
        Intrinsics.checkNotNullParameter(lazy6, "groupManagerLegacy");
        Intrinsics.checkNotNullParameter(notificationGroupAlertTransferHelper2, "groupAlertTransferHelper");
        Intrinsics.checkNotNullParameter(headsUpManager3, "headsUpManager");
        Intrinsics.checkNotNullParameter(headsUpController2, "headsUpController");
        Intrinsics.checkNotNullParameter(headsUpViewBinder2, "headsUpViewBinder");
        Intrinsics.checkNotNullParameter(builder, "clickerBuilder");
        Intrinsics.checkNotNullParameter(animatedImageNotificationManager2, "animatedImageNotificationManager");
        Intrinsics.checkNotNullParameter(peopleSpaceWidgetManager2, "peopleSpaceWidgetManager");
        this.featureFlags = featureFlags3;
        this.notificationListener = notificationListener3;
        this.entryManager = notificationEntryManager2;
        this.legacyRanker = notificationRankingManager2;
        this.notifPipeline = lazy4;
        this.targetSdkResolver = targetSdkResolver3;
        this.newNotifPipeline = lazy5;
        this.notifBindPipelineInitializer = notifBindPipelineInitializer3;
        this.deviceProvisionedController = deviceProvisionedController3;
        this.notificationRowBinder = notificationRowBinderImpl2;
        this.remoteInputUriController = remoteInputUriController3;
        this.groupManagerLegacy = lazy6;
        this.groupAlertTransferHelper = notificationGroupAlertTransferHelper2;
        this.headsUpManager = headsUpManager3;
        this.headsUpController = headsUpController2;
        this.headsUpViewBinder = headsUpViewBinder2;
        this.clickerBuilder = builder;
        this.animatedImageNotificationManager = animatedImageNotificationManager2;
        this.peopleSpaceWidgetManager = peopleSpaceWidgetManager2;
    }

    public void initialize(@NotNull StatusBar statusBar, @NotNull Optional<Bubbles> optional, @NotNull NotificationPresenter notificationPresenter, @NotNull NotificationListContainer notificationListContainer, @NotNull NotificationActivityStarter notificationActivityStarter, @NotNull NotificationRowBinderImpl.BindRowCallback bindRowCallback) {
        Intrinsics.checkNotNullParameter(statusBar, "statusBar");
        Intrinsics.checkNotNullParameter(optional, "bubblesOptional");
        Intrinsics.checkNotNullParameter(notificationPresenter, "presenter");
        Intrinsics.checkNotNullParameter(notificationListContainer, "listContainer");
        Intrinsics.checkNotNullParameter(notificationActivityStarter, "notificationActivityStarter");
        Intrinsics.checkNotNullParameter(bindRowCallback, "bindRowCallback");
        this.notificationListener.registerAsSystemService();
        new NotificationListController(this.entryManager, notificationListContainer, this.deviceProvisionedController).bind();
        this.notificationRowBinder.setNotificationClicker(this.clickerBuilder.build(Optional.of(statusBar), optional, notificationActivityStarter));
        this.notificationRowBinder.setUpWithPresenter(notificationPresenter, notificationListContainer, bindRowCallback);
        this.headsUpViewBinder.setPresenter(notificationPresenter);
        this.notifBindPipelineInitializer.initialize();
        this.animatedImageNotificationManager.bind();
        if (this.featureFlags.isNewNotifPipelineEnabled()) {
            this.newNotifPipeline.get().initialize(this.notificationListener, this.notificationRowBinder, notificationListContainer);
        }
        if (this.featureFlags.isNewNotifPipelineRenderingEnabled()) {
            TargetSdkResolver targetSdkResolver2 = this.targetSdkResolver;
            NotifPipeline notifPipeline2 = this.notifPipeline.get();
            Intrinsics.checkNotNullExpressionValue(notifPipeline2, "notifPipeline.get()");
            targetSdkResolver2.initialize(notifPipeline2);
        } else {
            this.targetSdkResolver.initialize(this.entryManager);
            this.remoteInputUriController.attach(this.entryManager);
            this.groupAlertTransferHelper.bind(this.entryManager, this.groupManagerLegacy.get());
            this.headsUpManager.addListener(this.groupManagerLegacy.get());
            this.headsUpManager.addListener(this.groupAlertTransferHelper);
            this.headsUpController.attach(this.entryManager, this.headsUpManager);
            this.groupManagerLegacy.get().setHeadsUpManager(this.headsUpManager);
            this.groupAlertTransferHelper.setHeadsUpManager(this.headsUpManager);
            this.entryManager.setRanker(this.legacyRanker);
            this.entryManager.attach(this.notificationListener);
        }
        this.peopleSpaceWidgetManager.attach(this.notificationListener);
    }

    public void dump(@NotNull FileDescriptor fileDescriptor, @NotNull PrintWriter printWriter, @NotNull String[] strArr, boolean z) {
        Intrinsics.checkNotNullParameter(fileDescriptor, "fd");
        Intrinsics.checkNotNullParameter(printWriter, "pw");
        Intrinsics.checkNotNullParameter(strArr, "args");
        if (z) {
            this.entryManager.dump(printWriter, "  ");
        }
    }

    public void requestNotificationUpdate(@NotNull String str) {
        Intrinsics.checkNotNullParameter(str, "reason");
        this.entryManager.updateNotifications(str);
    }

    public void resetUserExpandedStates() {
        for (NotificationEntry resetUserExpansion : this.entryManager.getVisibleNotifications()) {
            resetUserExpansion.resetUserExpansion();
        }
    }

    public void setNotificationSnoozed(@NotNull StatusBarNotification statusBarNotification, @NotNull NotificationSwipeActionHelper.SnoozeOption snoozeOption) {
        Intrinsics.checkNotNullParameter(statusBarNotification, "sbn");
        Intrinsics.checkNotNullParameter(snoozeOption, "snoozeOption");
        if (snoozeOption.getSnoozeCriterion() != null) {
            this.notificationListener.snoozeNotification(statusBarNotification.getKey(), snoozeOption.getSnoozeCriterion().getId());
        } else {
            this.notificationListener.snoozeNotification(statusBarNotification.getKey(), ((long) (snoozeOption.getMinutesToSnoozeFor() * 60)) * 1000);
        }
    }

    public int getActiveNotificationsCount() {
        return this.entryManager.getActiveNotificationsCount();
    }
}
