package com.android.systemui.statusbar.notification;

import android.view.View;
import android.view.ViewGroup;
import com.android.internal.jank.InteractionJankMonitor;
import com.android.systemui.animation.ActivityLaunchAnimator;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import com.android.systemui.statusbar.notification.row.ExpandableNotificationRow;
import com.android.systemui.statusbar.notification.stack.NotificationListContainer;
import com.android.systemui.statusbar.phone.HeadsUpManagerPhone;
import com.android.systemui.statusbar.phone.NotificationShadeWindowViewController;
import com.android.systemui.statusbar.policy.HeadsUpUtil;
import java.util.Objects;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

/* compiled from: NotificationLaunchAnimatorController.kt */
public final class NotificationLaunchAnimatorController implements ActivityLaunchAnimator.Controller {
    @NotNull
    public static final Companion Companion = new Companion((DefaultConstructorMarker) null);
    @NotNull
    private final HeadsUpManagerPhone headsUpManager;
    @NotNull
    private final ExpandableNotificationRow notification;
    @NotNull
    private final NotificationEntry notificationEntry;
    private final String notificationKey;
    @NotNull
    private final NotificationListContainer notificationListContainer;
    @NotNull
    private final NotificationShadeWindowViewController notificationShadeWindowViewController;

    public void setLaunchContainer(@NotNull ViewGroup viewGroup) {
        Intrinsics.checkNotNullParameter(viewGroup, "ignored");
    }

    public NotificationLaunchAnimatorController(@NotNull NotificationShadeWindowViewController notificationShadeWindowViewController2, @NotNull NotificationListContainer notificationListContainer2, @NotNull HeadsUpManagerPhone headsUpManagerPhone, @NotNull ExpandableNotificationRow expandableNotificationRow) {
        Intrinsics.checkNotNullParameter(notificationShadeWindowViewController2, "notificationShadeWindowViewController");
        Intrinsics.checkNotNullParameter(notificationListContainer2, "notificationListContainer");
        Intrinsics.checkNotNullParameter(headsUpManagerPhone, "headsUpManager");
        Intrinsics.checkNotNullParameter(expandableNotificationRow, "notification");
        this.notificationShadeWindowViewController = notificationShadeWindowViewController2;
        this.notificationListContainer = notificationListContainer2;
        this.headsUpManager = headsUpManagerPhone;
        this.notification = expandableNotificationRow;
        NotificationEntry entry = expandableNotificationRow.getEntry();
        Intrinsics.checkNotNullExpressionValue(entry, "notification.entry");
        this.notificationEntry = entry;
        this.notificationKey = entry.getSbn().getKey();
    }

    /* compiled from: NotificationLaunchAnimatorController.kt */
    public static final class Companion {
        public /* synthetic */ Companion(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }

        private Companion() {
        }
    }

    @NotNull
    public ViewGroup getLaunchContainer() {
        View rootView = this.notification.getRootView();
        Objects.requireNonNull(rootView, "null cannot be cast to non-null type android.view.ViewGroup");
        return (ViewGroup) rootView;
    }

    @NotNull
    public ActivityLaunchAnimator.State createAnimatorState() {
        float f;
        int max = Math.max(0, this.notification.getActualHeight() - this.notification.getClipBottomAmount());
        int[] locationOnScreen = this.notification.getLocationOnScreen();
        int topClippingStartLocation = this.notificationListContainer.getTopClippingStartLocation();
        int max2 = Math.max(topClippingStartLocation - locationOnScreen[1], 0);
        int i = locationOnScreen[1] + max2;
        if (max2 > 0) {
            f = 0.0f;
        } else {
            f = this.notification.getCurrentBackgroundRadiusTop();
        }
        ExpandAnimationParameters expandAnimationParameters = new ExpandAnimationParameters(i, locationOnScreen[1] + max, locationOnScreen[0], locationOnScreen[0] + this.notification.getWidth(), f, this.notification.getCurrentBackgroundRadiusBottom());
        expandAnimationParameters.setStartTranslationZ(this.notification.getTranslationZ());
        expandAnimationParameters.setStartNotificationTop(this.notification.getTranslationY());
        expandAnimationParameters.setStartRoundedTopClipping(max2);
        expandAnimationParameters.setStartClipTopAmount(this.notification.getClipTopAmount());
        if (this.notification.isChildInGroup()) {
            expandAnimationParameters.setStartNotificationTop(expandAnimationParameters.getStartNotificationTop() + this.notification.getNotificationParent().getTranslationY());
            expandAnimationParameters.setParentStartRoundedTopClipping(Math.max(topClippingStartLocation - this.notification.getNotificationParent().getLocationOnScreen()[1], 0));
            int clipTopAmount = this.notification.getNotificationParent().getClipTopAmount();
            expandAnimationParameters.setParentStartClipTopAmount(clipTopAmount);
            if (clipTopAmount != 0) {
                float translationY = ((float) clipTopAmount) - this.notification.getTranslationY();
                if (translationY > 0.0f) {
                    expandAnimationParameters.setStartClipTopAmount((int) Math.ceil((double) translationY));
                }
            }
        }
        return expandAnimationParameters;
    }

    public void onIntentStarted(boolean z) {
        this.notificationShadeWindowViewController.setExpandAnimationRunning(z);
        this.notificationEntry.setExpandAnimationRunning(z);
        if (!z) {
            removeHun(true);
        }
    }

    private final void removeHun(boolean z) {
        if (this.headsUpManager.isAlerting(this.notificationKey)) {
            HeadsUpUtil.setNeedsHeadsUpDisappearAnimationAfterClick(this.notification, z);
            this.headsUpManager.removeNotification(this.notificationKey, true, z);
        }
    }

    public void onLaunchAnimationCancelled() {
        this.notificationShadeWindowViewController.setExpandAnimationRunning(false);
        this.notificationEntry.setExpandAnimationRunning(false);
        removeHun(true);
    }

    public void onLaunchAnimationStart(boolean z) {
        this.notification.setExpandAnimationRunning(true);
        this.notificationListContainer.setExpandingNotification(this.notification);
        InteractionJankMonitor.getInstance().begin(this.notification, 16);
    }

    public void onLaunchAnimationEnd(boolean z) {
        InteractionJankMonitor.getInstance().end(16);
        this.notification.setExpandAnimationRunning(false);
        this.notificationShadeWindowViewController.setExpandAnimationRunning(false);
        this.notificationEntry.setExpandAnimationRunning(false);
        this.notificationListContainer.setExpandingNotification((ExpandableNotificationRow) null);
        applyParams((ExpandAnimationParameters) null);
        removeHun(false);
    }

    private final void applyParams(ExpandAnimationParameters expandAnimationParameters) {
        this.notification.applyExpandAnimationParams(expandAnimationParameters);
        this.notificationListContainer.applyExpandAnimationParams(expandAnimationParameters);
    }

    public void onLaunchAnimationProgress(@NotNull ActivityLaunchAnimator.State state, float f, float f2) {
        Intrinsics.checkNotNullParameter(state, "state");
        ExpandAnimationParameters expandAnimationParameters = (ExpandAnimationParameters) state;
        expandAnimationParameters.setProgress(f);
        expandAnimationParameters.setLinearProgress(f2);
        applyParams(expandAnimationParameters);
    }
}
