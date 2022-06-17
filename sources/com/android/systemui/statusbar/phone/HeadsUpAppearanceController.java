package com.android.systemui.statusbar.phone;

import android.graphics.Rect;
import android.os.Build;
import android.util.Log;
import android.view.View;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.widget.ViewClippingUtil;
import com.android.systemui.Dependency;
import com.android.systemui.R$id;
import com.android.systemui.moto.CarrierLabelUpdateMonitor;
import com.android.systemui.moto.MotoFeature;
import com.android.systemui.plugins.DarkIconDispatcher;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.statusbar.CommandQueue;
import com.android.systemui.statusbar.CrossFadeHelper;
import com.android.systemui.statusbar.HeadsUpStatusBarView;
import com.android.systemui.statusbar.notification.NotificationWakeUpCoordinator;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import com.android.systemui.statusbar.notification.row.ExpandableNotificationRow;
import com.android.systemui.statusbar.notification.stack.NotificationStackScrollLayoutController;
import com.android.systemui.statusbar.policy.KeyguardStateController;
import com.android.systemui.statusbar.policy.OnHeadsUpChangedListener;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class HeadsUpAppearanceController implements OnHeadsUpChangedListener, DarkIconDispatcher.DarkReceiver, NotificationWakeUpCoordinator.WakeUpListener {
    static final boolean DEBUG = (Build.IS_DEBUGGABLE || Log.isLoggable("HeadsUpAppearanceController", 3));
    private boolean mAnimationsEnabled;
    @VisibleForTesting
    float mAppearFraction;
    private final KeyguardBypassController mBypassController;
    private CarrierLabelUpdateMonitor mCarrierLabelUpdateMonitor;
    private final View mCenteredIconView;
    private final View mClockView;
    private final CommandQueue mCommandQueue;
    private final DarkIconDispatcher mDarkIconDispatcher;
    @VisibleForTesting
    float mExpandedHeight;
    private final HeadsUpManagerPhone mHeadsUpManager;
    /* access modifiers changed from: private */
    public final HeadsUpStatusBarView mHeadsUpStatusBarView;
    @VisibleForTesting
    boolean mIsExpanded;
    private KeyguardStateController mKeyguardStateController;
    private final NotificationIconAreaController mNotificationIconAreaController;
    private final NotificationPanelViewController mNotificationPanelViewController;
    private final View mOnsTextView;
    private final View mOperatorNameView;
    private final ViewClippingUtil.ClippingParameters mParentClippingParams;
    private final BiConsumer<Float, Float> mSetExpandedHeight;
    private final Consumer<ExpandableNotificationRow> mSetTrackingHeadsUp;
    private boolean mShown;
    /* access modifiers changed from: private */
    public final NotificationStackScrollLayoutController mStackScrollerController;
    private final StatusBarStateController mStatusBarStateController;
    private ExpandableNotificationRow mTrackedChild;
    private final NotificationWakeUpCoordinator mWakeUpCoordinator;

    /* JADX WARNING: Illegal instructions before constructor call */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public HeadsUpAppearanceController(com.android.systemui.statusbar.phone.NotificationIconAreaController r19, com.android.systemui.statusbar.phone.HeadsUpManagerPhone r20, com.android.systemui.statusbar.notification.stack.NotificationStackScrollLayoutController r21, com.android.systemui.statusbar.SysuiStatusBarStateController r22, com.android.systemui.statusbar.phone.KeyguardBypassController r23, com.android.systemui.statusbar.policy.KeyguardStateController r24, com.android.systemui.statusbar.notification.NotificationWakeUpCoordinator r25, com.android.systemui.statusbar.CommandQueue r26, com.android.systemui.statusbar.phone.NotificationPanelViewController r27, android.view.View r28, com.android.systemui.moto.CarrierLabelUpdateMonitor r29) {
        /*
            r18 = this;
            r0 = r28
            int r1 = com.android.systemui.R$id.heads_up_status_bar_view
            android.view.View r1 = r0.findViewById(r1)
            r12 = r1
            com.android.systemui.statusbar.HeadsUpStatusBarView r12 = (com.android.systemui.statusbar.HeadsUpStatusBarView) r12
            int r1 = com.android.systemui.R$id.status_bar_left_side
            android.view.View r1 = r0.findViewById(r1)
            int r2 = com.android.systemui.R$id.clock
            android.view.View r13 = r1.findViewById(r2)
            int r1 = com.android.systemui.R$id.operator_name_frame
            android.view.View r14 = r0.findViewById(r1)
            int r1 = com.android.systemui.R$id.centered_icon_area
            android.view.View r15 = r0.findViewById(r1)
            int r1 = com.android.systemui.R$id.onsText_att
            android.view.View r16 = r0.findViewById(r1)
            r2 = r18
            r3 = r19
            r4 = r20
            r5 = r22
            r6 = r23
            r7 = r25
            r8 = r24
            r9 = r26
            r10 = r21
            r11 = r27
            r17 = r29
            r2.<init>(r3, r4, r5, r6, r7, r8, r9, r10, r11, r12, r13, r14, r15, r16, r17)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.statusbar.phone.HeadsUpAppearanceController.<init>(com.android.systemui.statusbar.phone.NotificationIconAreaController, com.android.systemui.statusbar.phone.HeadsUpManagerPhone, com.android.systemui.statusbar.notification.stack.NotificationStackScrollLayoutController, com.android.systemui.statusbar.SysuiStatusBarStateController, com.android.systemui.statusbar.phone.KeyguardBypassController, com.android.systemui.statusbar.policy.KeyguardStateController, com.android.systemui.statusbar.notification.NotificationWakeUpCoordinator, com.android.systemui.statusbar.CommandQueue, com.android.systemui.statusbar.phone.NotificationPanelViewController, android.view.View, com.android.systemui.moto.CarrierLabelUpdateMonitor):void");
    }

    @VisibleForTesting
    public HeadsUpAppearanceController(NotificationIconAreaController notificationIconAreaController, HeadsUpManagerPhone headsUpManagerPhone, StatusBarStateController statusBarStateController, KeyguardBypassController keyguardBypassController, NotificationWakeUpCoordinator notificationWakeUpCoordinator, KeyguardStateController keyguardStateController, CommandQueue commandQueue, NotificationStackScrollLayoutController notificationStackScrollLayoutController, NotificationPanelViewController notificationPanelViewController, HeadsUpStatusBarView headsUpStatusBarView, View view, View view2, View view3) {
        this(notificationIconAreaController, headsUpManagerPhone, statusBarStateController, keyguardBypassController, notificationWakeUpCoordinator, keyguardStateController, commandQueue, notificationStackScrollLayoutController, notificationPanelViewController, headsUpStatusBarView, view, view2, view3, (View) null, (CarrierLabelUpdateMonitor) null);
    }

    @VisibleForTesting
    public HeadsUpAppearanceController(NotificationIconAreaController notificationIconAreaController, HeadsUpManagerPhone headsUpManagerPhone, StatusBarStateController statusBarStateController, KeyguardBypassController keyguardBypassController, NotificationWakeUpCoordinator notificationWakeUpCoordinator, KeyguardStateController keyguardStateController, CommandQueue commandQueue, NotificationStackScrollLayoutController notificationStackScrollLayoutController, NotificationPanelViewController notificationPanelViewController, HeadsUpStatusBarView headsUpStatusBarView, View view, View view2, View view3, View view4, CarrierLabelUpdateMonitor carrierLabelUpdateMonitor) {
        NotificationStackScrollLayoutController notificationStackScrollLayoutController2 = notificationStackScrollLayoutController;
        NotificationPanelViewController notificationPanelViewController2 = notificationPanelViewController;
        HeadsUpStatusBarView headsUpStatusBarView2 = headsUpStatusBarView;
        HeadsUpAppearanceController$$ExternalSyntheticLambda5 headsUpAppearanceController$$ExternalSyntheticLambda5 = new HeadsUpAppearanceController$$ExternalSyntheticLambda5(this);
        this.mSetTrackingHeadsUp = headsUpAppearanceController$$ExternalSyntheticLambda5;
        HeadsUpAppearanceController$$ExternalSyntheticLambda3 headsUpAppearanceController$$ExternalSyntheticLambda3 = new HeadsUpAppearanceController$$ExternalSyntheticLambda3(this);
        this.mSetExpandedHeight = headsUpAppearanceController$$ExternalSyntheticLambda3;
        this.mParentClippingParams = new ViewClippingUtil.ClippingParameters() {
            public boolean shouldFinish(View view) {
                return view.getId() == R$id.status_bar;
            }
        };
        this.mAnimationsEnabled = true;
        this.mNotificationIconAreaController = notificationIconAreaController;
        this.mHeadsUpManager = headsUpManagerPhone;
        headsUpManagerPhone.addListener(this);
        this.mHeadsUpStatusBarView = headsUpStatusBarView2;
        this.mCenteredIconView = view3;
        headsUpStatusBarView2.setOnDrawingRectChangedListener(new HeadsUpAppearanceController$$ExternalSyntheticLambda1(this));
        this.mStackScrollerController = notificationStackScrollLayoutController2;
        this.mNotificationPanelViewController = notificationPanelViewController2;
        notificationPanelViewController2.addTrackingHeadsUpListener(headsUpAppearanceController$$ExternalSyntheticLambda5);
        notificationPanelViewController2.setHeadsUpAppearanceController(this);
        notificationStackScrollLayoutController2.addOnExpandedHeightChangedListener(headsUpAppearanceController$$ExternalSyntheticLambda3);
        notificationStackScrollLayoutController2.setHeadsUpAppearanceController(this);
        this.mClockView = view;
        this.mOperatorNameView = view2;
        this.mCarrierLabelUpdateMonitor = carrierLabelUpdateMonitor;
        this.mOnsTextView = view4;
        DarkIconDispatcher darkIconDispatcher = (DarkIconDispatcher) Dependency.get(DarkIconDispatcher.class);
        this.mDarkIconDispatcher = darkIconDispatcher;
        darkIconDispatcher.addDarkReceiver((DarkIconDispatcher.DarkReceiver) this);
        headsUpStatusBarView2.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            public void onLayoutChange(View view, int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8) {
                if (HeadsUpAppearanceController.this.shouldBeVisible()) {
                    HeadsUpAppearanceController.this.updateTopEntry();
                    HeadsUpAppearanceController.this.mStackScrollerController.requestLayout();
                }
                HeadsUpAppearanceController.this.mHeadsUpStatusBarView.removeOnLayoutChangeListener(this);
            }
        });
        this.mBypassController = keyguardBypassController;
        this.mStatusBarStateController = statusBarStateController;
        this.mWakeUpCoordinator = notificationWakeUpCoordinator;
        notificationWakeUpCoordinator.addListener(this);
        this.mCommandQueue = commandQueue;
        this.mKeyguardStateController = keyguardStateController;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0() {
        updateIsolatedIconLocation(true);
    }

    public void destroy() {
        this.mHeadsUpManager.removeListener(this);
        this.mHeadsUpStatusBarView.setOnDrawingRectChangedListener((Runnable) null);
        this.mWakeUpCoordinator.removeListener(this);
        this.mNotificationPanelViewController.removeTrackingHeadsUpListener(this.mSetTrackingHeadsUp);
        this.mNotificationPanelViewController.setVerticalTranslationListener((Runnable) null);
        this.mNotificationPanelViewController.setHeadsUpAppearanceController((HeadsUpAppearanceController) null);
        this.mStackScrollerController.removeOnExpandedHeightChangedListener(this.mSetExpandedHeight);
        this.mDarkIconDispatcher.removeDarkReceiver((DarkIconDispatcher.DarkReceiver) this);
    }

    private void updateIsolatedIconLocation(boolean z) {
        this.mNotificationIconAreaController.setIsolatedIconLocation(this.mHeadsUpStatusBarView.getIconDrawingRect(), z);
    }

    public void onHeadsUpPinned(NotificationEntry notificationEntry) {
        updateTopEntry();
        lambda$updateHeadsUpHeaders$3(notificationEntry);
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Removed duplicated region for block: B:15:0x0038  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void updateTopEntry() {
        /*
            r5 = this;
            boolean r0 = r5.shouldBeVisible()
            r1 = 0
            if (r0 == 0) goto L_0x000e
            com.android.systemui.statusbar.phone.HeadsUpManagerPhone r0 = r5.mHeadsUpManager
            com.android.systemui.statusbar.notification.collection.NotificationEntry r0 = r0.getTopEntry()
            goto L_0x000f
        L_0x000e:
            r0 = r1
        L_0x000f:
            com.android.systemui.statusbar.HeadsUpStatusBarView r2 = r5.mHeadsUpStatusBarView
            com.android.systemui.statusbar.notification.collection.NotificationEntry r2 = r2.getShowingEntry()
            com.android.systemui.statusbar.HeadsUpStatusBarView r3 = r5.mHeadsUpStatusBarView
            r3.setEntry(r0)
            if (r0 == r2) goto L_0x0043
            r3 = 1
            r4 = 0
            if (r0 != 0) goto L_0x0027
            r5.setShown(r4)
            boolean r2 = r5.mIsExpanded
        L_0x0025:
            r2 = r2 ^ r3
            goto L_0x0030
        L_0x0027:
            if (r2 != 0) goto L_0x002f
            r5.setShown(r3)
            boolean r2 = r5.mIsExpanded
            goto L_0x0025
        L_0x002f:
            r2 = r4
        L_0x0030:
            r5.updateIsolatedIconLocation(r4)
            com.android.systemui.statusbar.phone.NotificationIconAreaController r5 = r5.mNotificationIconAreaController
            if (r0 != 0) goto L_0x0038
            goto L_0x0040
        L_0x0038:
            com.android.systemui.statusbar.notification.icon.IconPack r0 = r0.getIcons()
            com.android.systemui.statusbar.StatusBarIconView r1 = r0.getStatusBarIcon()
        L_0x0040:
            r5.showIconIsolated(r1, r2)
        L_0x0043:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.statusbar.phone.HeadsUpAppearanceController.updateTopEntry():void");
    }

    private void setShown(boolean z) {
        CarrierLabelUpdateMonitor carrierLabelUpdateMonitor;
        if (DEBUG) {
            Log.i("CarrierLabel", "HeadsUp setShown isShown = " + z);
        }
        if (this.mShown != z) {
            this.mShown = z;
            if (z) {
                updateParentClipping(false);
                this.mHeadsUpStatusBarView.setVisibility(0);
                show(this.mHeadsUpStatusBarView);
                if (!MotoFeature.getInstance(this.mHeadsUpStatusBarView.getContext()).showRightSideClock()) {
                    hide(this.mClockView, 4);
                }
                if (this.mCenteredIconView.getVisibility() != 8) {
                    hide(this.mCenteredIconView, 4);
                }
                View view = this.mOperatorNameView;
                if (view != null) {
                    hide(view, 4);
                }
                if (this.mOnsTextView != null && (carrierLabelUpdateMonitor = this.mCarrierLabelUpdateMonitor) != null && carrierLabelUpdateMonitor.shouldShowShortFormLabel() && this.mCarrierLabelUpdateMonitor.isOnsShown()) {
                    this.mCarrierLabelUpdateMonitor.setOnsShown(false);
                }
            } else {
                if (!MotoFeature.getInstance(this.mHeadsUpStatusBarView.getContext()).showRightSideClock()) {
                    show(this.mClockView);
                }
                if (this.mCenteredIconView.getVisibility() != 8) {
                    show(this.mCenteredIconView);
                }
                View view2 = this.mOperatorNameView;
                if (view2 != null) {
                    show(view2);
                }
                hide(this.mHeadsUpStatusBarView, 8, new HeadsUpAppearanceController$$ExternalSyntheticLambda2(this));
            }
            if (this.mStatusBarStateController.getState() != 0) {
                this.mCommandQueue.recomputeDisableFlags(this.mHeadsUpStatusBarView.getContext().getDisplayId(), false);
            }
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$setShown$1() {
        updateParentClipping(true);
    }

    private void updateParentClipping(boolean z) {
        ViewClippingUtil.setClippingDeactivated(this.mHeadsUpStatusBarView, !z, this.mParentClippingParams);
    }

    private void hide(View view, int i) {
        hide(view, i, (Runnable) null);
    }

    private void hide(View view, int i, Runnable runnable) {
        if (this.mAnimationsEnabled) {
            CrossFadeHelper.fadeOut(view, 110, 0, new HeadsUpAppearanceController$$ExternalSyntheticLambda0(view, i, runnable));
            return;
        }
        view.setVisibility(i);
        if (runnable != null) {
            runnable.run();
        }
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$hide$2(View view, int i, Runnable runnable) {
        view.setVisibility(i);
        if (runnable != null) {
            runnable.run();
        }
    }

    private void show(View view) {
        if (this.mAnimationsEnabled) {
            CrossFadeHelper.fadeIn(view, 110, 100);
        } else {
            view.setVisibility(0);
        }
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public void setAnimationsEnabled(boolean z) {
        this.mAnimationsEnabled = z;
    }

    @VisibleForTesting
    public boolean isShown() {
        return this.mShown;
    }

    public boolean shouldBeVisible() {
        boolean z = !this.mWakeUpCoordinator.getNotificationsFullyHidden();
        boolean z2 = !this.mIsExpanded && z;
        if (this.mBypassController.getBypassEnabled() && ((this.mStatusBarStateController.getState() == 1 || this.mKeyguardStateController.isKeyguardGoingAway()) && z)) {
            z2 = true;
        }
        if (!z2 || !this.mHeadsUpManager.hasPinnedHeadsUp()) {
            return false;
        }
        return true;
    }

    public void onHeadsUpUnPinned(NotificationEntry notificationEntry) {
        updateTopEntry();
        lambda$updateHeadsUpHeaders$3(notificationEntry);
    }

    public void setAppearFraction(float f, float f2) {
        boolean z = true;
        boolean z2 = Math.abs(f - this.mExpandedHeight) > 0.001f;
        this.mExpandedHeight = f;
        this.mAppearFraction = f2;
        if (f <= 0.0f) {
            z = false;
        }
        if (z2) {
            updateHeadsUpHeaders();
        }
        if (z != this.mIsExpanded) {
            this.mIsExpanded = z;
            updateTopEntry();
        }
    }

    public void setTrackingHeadsUp(ExpandableNotificationRow expandableNotificationRow) {
        ExpandableNotificationRow expandableNotificationRow2 = this.mTrackedChild;
        this.mTrackedChild = expandableNotificationRow;
        if (expandableNotificationRow2 != null) {
            lambda$updateHeadsUpHeaders$3(expandableNotificationRow2.getEntry());
        }
    }

    private void updateHeadsUpHeaders() {
        this.mHeadsUpManager.getAllEntries().forEach(new HeadsUpAppearanceController$$ExternalSyntheticLambda4(this));
    }

    /* renamed from: updateHeader */
    public void lambda$updateHeadsUpHeaders$3(NotificationEntry notificationEntry) {
        float f;
        ExpandableNotificationRow row = notificationEntry.getRow();
        if (row.isPinned() || row.isHeadsUpAnimatingAway() || row == this.mTrackedChild || row.showingPulsing()) {
            f = this.mAppearFraction;
        } else {
            f = 1.0f;
        }
        row.setHeaderVisibleAmount(f);
    }

    public void onDarkChanged(Rect rect, float f, int i) {
        this.mHeadsUpStatusBarView.onDarkChanged(rect, f, i);
    }

    public void onStateChanged() {
        updateTopEntry();
    }

    /* access modifiers changed from: package-private */
    public void readFrom(HeadsUpAppearanceController headsUpAppearanceController) {
        if (headsUpAppearanceController != null) {
            this.mTrackedChild = headsUpAppearanceController.mTrackedChild;
            this.mExpandedHeight = headsUpAppearanceController.mExpandedHeight;
            this.mIsExpanded = headsUpAppearanceController.mIsExpanded;
            this.mAppearFraction = headsUpAppearanceController.mAppearFraction;
        }
    }

    public void onFullyHiddenChanged(boolean z) {
        updateTopEntry();
    }
}
