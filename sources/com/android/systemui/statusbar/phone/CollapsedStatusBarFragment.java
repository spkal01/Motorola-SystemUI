package com.android.systemui.statusbar.phone;

import android.animation.ValueAnimator;
import android.app.Fragment;
import android.content.res.Configuration;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.LinearLayout;
import com.android.systemui.R$bool;
import com.android.systemui.R$id;
import com.android.systemui.R$layout;
import com.android.systemui.animation.Interpolators;
import com.android.systemui.moto.MotoFeature;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.statusbar.CommandQueue;
import com.android.systemui.statusbar.FeatureFlags;
import com.android.systemui.statusbar.events.SystemStatusAnimationCallback;
import com.android.systemui.statusbar.events.SystemStatusAnimationScheduler;
import com.android.systemui.statusbar.phone.StatusBarIconController;
import com.android.systemui.statusbar.phone.ongoingcall.OngoingCallController;
import com.android.systemui.statusbar.phone.ongoingcall.OngoingCallListener;
import com.android.systemui.statusbar.policy.Clock;
import com.android.systemui.statusbar.policy.EncryptionHelper;
import com.android.systemui.statusbar.policy.KeyguardStateController;
import com.android.systemui.statusbar.policy.NetworkController;
import com.motorola.android.provider.MotorolaSettings;
import java.util.ArrayList;
import java.util.List;
import org.jetbrains.annotations.NotNull;

public class CollapsedStatusBarFragment extends Fragment implements CommandQueue.Callbacks, StatusBarStateController.StateListener, SystemStatusAnimationCallback {
    private final SystemStatusAnimationScheduler mAnimationScheduler;
    private List<String> mBlockedIcons = new ArrayList();
    private View mCenteredIconArea;
    private View mClockView;
    /* access modifiers changed from: private */
    public final CommandQueue mCommandQueue;
    private ContentObserver mContentObserver;
    private StatusBarIconController.DarkIconManager mDarkIconManager;
    /* access modifiers changed from: private */
    public boolean mDisableNotificationIconsMoto;
    /* access modifiers changed from: private */
    public int mDisabled1;
    /* access modifiers changed from: private */
    public int mDisabled2;
    private final FeatureFlags mFeatureFlags;
    private Handler mHandler = new Handler(Looper.getMainLooper());
    private final KeyguardStateController mKeyguardStateController;
    private final StatusBarLocationPublisher mLocationPublisher;
    private final NetworkController mNetworkController;
    private final NotificationIconAreaController mNotificationIconAreaController;
    private View mNotificationIconAreaInner;
    private View mOngoingCallChip;
    private final OngoingCallController mOngoingCallController;
    private final OngoingCallListener mOngoingCallListener = new OngoingCallListener() {
        public void onOngoingCallStateChanged(boolean z) {
            CollapsedStatusBarFragment collapsedStatusBarFragment = CollapsedStatusBarFragment.this;
            collapsedStatusBarFragment.disable(collapsedStatusBarFragment.getContext().getDisplayId(), CollapsedStatusBarFragment.this.mDisabled1, CollapsedStatusBarFragment.this.mDisabled2, z);
        }
    };
    private View mOperatorNameFrame;
    private NetworkController.SignalCallback mSignalCallback = new NetworkController.SignalCallback() {
        public void setIsAirplaneMode(NetworkController.IconState iconState) {
            CollapsedStatusBarFragment.this.mCommandQueue.recomputeDisableFlags(CollapsedStatusBarFragment.this.getContext().getDisplayId(), true);
        }
    };
    private PhoneStatusBarView mStatusBar;
    private final StatusBar mStatusBarComponent;
    private final StatusBarIconController mStatusBarIconController;
    private View.OnLayoutChangeListener mStatusBarLayoutListener = new CollapsedStatusBarFragment$$ExternalSyntheticLambda0(this);
    private final StatusBarStateController mStatusBarStateController;
    private LinearLayout mSystemIconArea;

    public void onStateChanged(int i) {
    }

    public CollapsedStatusBarFragment(OngoingCallController ongoingCallController, SystemStatusAnimationScheduler systemStatusAnimationScheduler, StatusBarLocationPublisher statusBarLocationPublisher, NotificationIconAreaController notificationIconAreaController, FeatureFlags featureFlags, StatusBarIconController statusBarIconController, KeyguardStateController keyguardStateController, NetworkController networkController, StatusBarStateController statusBarStateController, StatusBar statusBar, CommandQueue commandQueue) {
        this.mOngoingCallController = ongoingCallController;
        this.mAnimationScheduler = systemStatusAnimationScheduler;
        this.mLocationPublisher = statusBarLocationPublisher;
        this.mNotificationIconAreaController = notificationIconAreaController;
        this.mFeatureFlags = featureFlags;
        this.mStatusBarIconController = statusBarIconController;
        this.mKeyguardStateController = keyguardStateController;
        this.mNetworkController = networkController;
        this.mStatusBarStateController = statusBarStateController;
        this.mStatusBarComponent = statusBar;
        this.mCommandQueue = commandQueue;
    }

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        return layoutInflater.inflate(R$layout.status_bar, viewGroup, false);
    }

    public void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
        PhoneStatusBarView phoneStatusBarView = (PhoneStatusBarView) view;
        this.mStatusBar = phoneStatusBarView;
        View findViewById = phoneStatusBarView.findViewById(R$id.status_bar_contents);
        findViewById.addOnLayoutChangeListener(this.mStatusBarLayoutListener);
        updateStatusBarLocation(findViewById.getLeft(), findViewById.getRight());
        if (bundle != null && bundle.containsKey("panel_state")) {
            this.mStatusBar.restoreHierarchyState(bundle.getSparseParcelableArray("panel_state"));
        }
        StatusBarIconController.DarkIconManager darkIconManager = new StatusBarIconController.DarkIconManager((LinearLayout) view.findViewById(R$id.statusIcons), this.mFeatureFlags);
        this.mDarkIconManager = darkIconManager;
        darkIconManager.setShouldLog(true);
        this.mBlockedIcons.add(getString(17041494));
        this.mDarkIconManager.setBlockList(this.mBlockedIcons);
        this.mStatusBarIconController.addIconGroup(this.mDarkIconManager);
        this.mSystemIconArea = (LinearLayout) this.mStatusBar.findViewById(R$id.system_icon_area);
        if (MotoFeature.getInstance(getContext()).showRightSideClock()) {
            this.mClockView = this.mStatusBar.findViewById(R$id.right_clock);
            ((Clock) this.mStatusBar.findViewById(R$id.clock)).setClockVisibilityByMoto(false);
        } else {
            this.mClockView = this.mStatusBar.findViewById(R$id.clock);
            ((Clock) this.mStatusBar.findViewById(R$id.right_clock)).setClockVisibilityByMoto(false);
        }
        this.mOngoingCallChip = this.mStatusBar.findViewById(R$id.ongoing_call_chip);
        showSystemIconArea(false);
        showClock(false);
        initEmergencyCryptkeeperText();
        initOperatorName();
        initNotificationIconArea();
        this.mAnimationScheduler.addCallback((SystemStatusAnimationCallback) this);
        if (MotoFeature.isPrcProduct()) {
            registerShowNotificatoinIconsObserver();
        }
    }

    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        SparseArray sparseArray = new SparseArray();
        this.mStatusBar.saveHierarchyState(sparseArray);
        bundle.putSparseParcelableArray("panel_state", sparseArray);
    }

    public void onResume() {
        super.onResume();
        this.mCommandQueue.addCallback((CommandQueue.Callbacks) this);
        this.mStatusBarStateController.addCallback(this);
        initOngoingCallChip();
    }

    public void onPause() {
        super.onPause();
        this.mCommandQueue.removeCallback((CommandQueue.Callbacks) this);
        this.mStatusBarStateController.removeCallback(this);
        this.mOngoingCallController.removeCallback(this.mOngoingCallListener);
    }

    public void onDestroyView() {
        super.onDestroyView();
        this.mStatusBarIconController.removeIconGroup(this.mDarkIconManager);
        this.mAnimationScheduler.removeCallback((SystemStatusAnimationCallback) this);
        if (this.mNetworkController.hasEmergencyCryptKeeperText()) {
            this.mNetworkController.removeCallback(this.mSignalCallback);
        }
        if (this.mContentObserver != null) {
            getContext().getContentResolver().unregisterContentObserver(this.mContentObserver);
            this.mContentObserver = null;
        }
    }

    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        this.mHandler.postDelayed(new CollapsedStatusBarFragment$$ExternalSyntheticLambda2(this), 400);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onConfigurationChanged$0() {
        StatusBarIconController.DarkIconManager darkIconManager = this.mDarkIconManager;
        if (darkIconManager != null) {
            darkIconManager.reapplyDark();
        }
    }

    public void initNotificationIconArea() {
        ViewGroup viewGroup = (ViewGroup) this.mStatusBar.findViewById(R$id.notification_icon_area);
        View notificationInnerAreaView = this.mNotificationIconAreaController.getNotificationInnerAreaView();
        this.mNotificationIconAreaInner = notificationInnerAreaView;
        if (notificationInnerAreaView.getParent() != null) {
            ((ViewGroup) this.mNotificationIconAreaInner.getParent()).removeView(this.mNotificationIconAreaInner);
        }
        viewGroup.addView(this.mNotificationIconAreaInner);
        ViewGroup viewGroup2 = (ViewGroup) this.mStatusBar.findViewById(R$id.centered_icon_area);
        View centeredNotificationAreaView = this.mNotificationIconAreaController.getCenteredNotificationAreaView();
        this.mCenteredIconArea = centeredNotificationAreaView;
        if (centeredNotificationAreaView.getParent() != null) {
            ((ViewGroup) this.mCenteredIconArea.getParent()).removeView(this.mCenteredIconArea);
        }
        viewGroup2.addView(this.mCenteredIconArea);
        updateNotificationIconAreaAndCallChip(this.mDisabled1, false);
    }

    public void disable(int i, int i2, int i3, boolean z) {
        if (i == getContext().getDisplayId()) {
            int adjustDisableFlags = adjustDisableFlags(i2);
            int i4 = this.mDisabled1 ^ adjustDisableFlags;
            int i5 = this.mDisabled2 ^ i3;
            this.mDisabled1 = adjustDisableFlags;
            this.mDisabled2 = i3;
            if (!((i4 & 1048576) == 0 && (i5 & 2) == 0)) {
                if ((adjustDisableFlags & 1048576) == 0 && (i3 & 2) == 0) {
                    showSystemIconArea(z);
                    showOperatorName(z);
                } else {
                    hideSystemIconArea(z);
                    hideOperatorName(z);
                }
            }
            if (!((67108864 & i4) == 0 && (131072 & i4) == 0)) {
                updateNotificationIconAreaAndCallChip(adjustDisableFlags, z);
            }
            if ((i4 & 8388608) != 0 || this.mClockView.getVisibility() != clockHiddenMode()) {
                if ((adjustDisableFlags & 8388608) != 0) {
                    hideClock(z);
                } else {
                    showClock(z);
                }
            }
        }
    }

    /* access modifiers changed from: protected */
    public int adjustDisableFlags(int i) {
        boolean headsUpShouldBeVisible = this.mStatusBarComponent.headsUpShouldBeVisible();
        if (headsUpShouldBeVisible && shouldHideClock()) {
            i |= 8388608;
        }
        if (!this.mKeyguardStateController.isLaunchTransitionFadingAway() && !this.mKeyguardStateController.isKeyguardFadingAway() && shouldHideNotificationIcons() && (this.mStatusBarStateController.getState() != 1 || !headsUpShouldBeVisible)) {
            i = i | 131072 | 1048576 | 8388608;
        }
        NetworkController networkController = this.mNetworkController;
        if (networkController != null && EncryptionHelper.IS_DATA_ENCRYPTED) {
            if (networkController.hasEmergencyCryptKeeperText()) {
                i |= 131072;
            }
            if (!this.mNetworkController.isRadioOn()) {
                i |= 1048576;
            }
        }
        if (this.mStatusBarStateController.isDozing() && this.mStatusBarComponent.getPanelController().hasCustomClock()) {
            i |= 9437184;
        }
        return this.mOngoingCallController.hasOngoingCall() ? -67108865 & i : 67108864 | i;
    }

    /* access modifiers changed from: private */
    public void updateNotificationIconAreaAndCallChip(int i, boolean z) {
        boolean z2 = false;
        boolean z3 = (131072 & i) != 0 || this.mDisableNotificationIconsMoto;
        boolean z4 = (i & 67108864) == 0;
        if (z3 || z4) {
            hideNotificationIconArea(z);
        } else {
            showNotificationIconArea(z);
        }
        if (z4 && !z3) {
            z2 = true;
        }
        if (z2) {
            showOngoingCallChip(z);
        } else {
            hideOngoingCallChip(z);
        }
        this.mOngoingCallController.notifyChipVisibilityChanged(z2);
    }

    private boolean shouldHideClock() {
        return !MotoFeature.getInstance(getContext()).showRightSideClock();
    }

    private boolean shouldHideNotificationIcons() {
        if ((this.mStatusBar.isClosed() || !this.mStatusBarComponent.hideStatusBarIconsWhenExpanded()) && !this.mStatusBarComponent.hideStatusBarIconsForBouncer()) {
            return false;
        }
        return true;
    }

    private void hideSystemIconArea(boolean z) {
        animateHide(this.mSystemIconArea, z);
    }

    private void showSystemIconArea(boolean z) {
        int animationState = this.mAnimationScheduler.getAnimationState();
        if (animationState == 0 || animationState == 4) {
            animateShow(this.mSystemIconArea, z);
        }
    }

    private void hideClock(boolean z) {
        animateHiddenState(this.mClockView, clockHiddenMode(), z);
    }

    private void showClock(boolean z) {
        animateShow(this.mClockView, z);
    }

    public void hideOngoingCallChip(boolean z) {
        animateHiddenState(this.mOngoingCallChip, 8, z);
    }

    public void showOngoingCallChip(boolean z) {
        animateShow(this.mOngoingCallChip, z);
    }

    private int clockHiddenMode() {
        return (this.mStatusBar.isClosed() || this.mKeyguardStateController.isShowing() || this.mStatusBarStateController.isDozing()) ? 8 : 4;
    }

    public void hideNotificationIconArea(boolean z) {
        animateHide(this.mNotificationIconAreaInner, z);
        animateHide(this.mCenteredIconArea, z);
    }

    public void showNotificationIconArea(boolean z) {
        animateShow(this.mNotificationIconAreaInner, z);
        animateShow(this.mCenteredIconArea, z);
    }

    public void hideOperatorName(boolean z) {
        View view = this.mOperatorNameFrame;
        if (view != null) {
            animateHide(view, z);
        }
    }

    public void showOperatorName(boolean z) {
        View view = this.mOperatorNameFrame;
        if (view != null) {
            animateShow(view, z);
        }
    }

    private void animateHiddenState(View view, int i, boolean z) {
        view.animate().cancel();
        if (!z) {
            view.setAlpha(0.0f);
            view.setVisibility(i);
            return;
        }
        view.animate().alpha(0.0f).setDuration(160).setStartDelay(0).setInterpolator(Interpolators.ALPHA_OUT).withEndAction(new CollapsedStatusBarFragment$$ExternalSyntheticLambda1(view, i));
    }

    private void animateHide(View view, boolean z) {
        animateHiddenState(view, 4, z);
    }

    private void animateShow(View view, boolean z) {
        view.animate().cancel();
        view.setVisibility(0);
        if (!z) {
            view.setAlpha(1.0f);
            return;
        }
        view.animate().alpha(1.0f).setDuration(320).setInterpolator(Interpolators.ALPHA_IN).setStartDelay(50).withEndAction((Runnable) null);
        if (this.mKeyguardStateController.isKeyguardFadingAway()) {
            view.animate().setDuration(this.mKeyguardStateController.getKeyguardFadingAwayDuration()).setInterpolator(Interpolators.LINEAR_OUT_SLOW_IN).setStartDelay(this.mKeyguardStateController.getKeyguardFadingAwayDelay()).start();
        }
    }

    private void initEmergencyCryptkeeperText() {
        View findViewById = this.mStatusBar.findViewById(R$id.emergency_cryptkeeper_text);
        if (this.mNetworkController.hasEmergencyCryptKeeperText()) {
            if (findViewById != null) {
                ((ViewStub) findViewById).inflate();
            }
            this.mNetworkController.addCallback(this.mSignalCallback);
        } else if (findViewById != null) {
            ((ViewGroup) findViewById.getParent()).removeView(findViewById);
        }
    }

    private void initOperatorName() {
        if (getResources().getBoolean(R$bool.config_showOperatorNameInStatusBar)) {
            this.mOperatorNameFrame = ((ViewStub) this.mStatusBar.findViewById(R$id.operator_name)).inflate();
        }
    }

    private void initOngoingCallChip() {
        this.mOngoingCallController.addCallback(this.mOngoingCallListener);
        this.mOngoingCallController.setChipView(this.mOngoingCallChip);
    }

    public void onDozingChanged(boolean z) {
        disable(getContext().getDisplayId(), this.mDisabled1, this.mDisabled2, false);
    }

    public void onSystemChromeAnimationStart() {
        if (this.mAnimationScheduler.getAnimationState() == 3 && !isSystemIconAreaDisabled()) {
            this.mSystemIconArea.setVisibility(0);
            this.mSystemIconArea.setAlpha(0.0f);
        }
    }

    public void onSystemChromeAnimationEnd() {
        if (this.mAnimationScheduler.getAnimationState() == 1) {
            this.mSystemIconArea.setVisibility(4);
            this.mSystemIconArea.setAlpha(0.0f);
        } else if (!isSystemIconAreaDisabled()) {
            this.mSystemIconArea.setAlpha(1.0f);
            this.mSystemIconArea.setVisibility(0);
        }
    }

    public void onSystemChromeAnimationUpdate(@NotNull ValueAnimator valueAnimator) {
        this.mSystemIconArea.setAlpha(((Float) valueAnimator.getAnimatedValue()).floatValue());
    }

    private boolean isSystemIconAreaDisabled() {
        return ((this.mDisabled1 & 1048576) == 0 && (this.mDisabled2 & 2) == 0) ? false : true;
    }

    private void updateStatusBarLocation(int i, int i2) {
        this.mLocationPublisher.updateStatusBarMargin(i - this.mStatusBar.getLeft(), this.mStatusBar.getRight() - i2);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$2(View view, int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8) {
        if (i != i5 || i3 != i7) {
            updateStatusBarLocation(i, i3);
        }
    }

    private void registerShowNotificatoinIconsObserver() {
        boolean z = true;
        if (MotorolaSettings.Global.getInt(getContext().getContentResolver(), "show_notification_icons", 1) != 0) {
            z = false;
        }
        this.mDisableNotificationIconsMoto = z;
        Uri uriFor = MotorolaSettings.Global.getUriFor("show_notification_icons");
        this.mContentObserver = new ContentObserver(new Handler()) {
            public void onChange(boolean z) {
                boolean z2 = true;
                if (MotorolaSettings.Global.getInt(CollapsedStatusBarFragment.this.getContext().getContentResolver(), "show_notification_icons", 1) != 0) {
                    z2 = false;
                }
                if (CollapsedStatusBarFragment.this.mDisableNotificationIconsMoto != z2) {
                    boolean unused = CollapsedStatusBarFragment.this.mDisableNotificationIconsMoto = z2;
                    CollapsedStatusBarFragment collapsedStatusBarFragment = CollapsedStatusBarFragment.this;
                    collapsedStatusBarFragment.updateNotificationIconAreaAndCallChip(collapsedStatusBarFragment.mDisabled1, false);
                }
            }
        };
        getContext().getContentResolver().registerContentObserver(uriFor, false, this.mContentObserver);
    }
}
