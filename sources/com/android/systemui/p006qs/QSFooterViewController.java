package com.android.systemui.p006qs;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.UserManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.android.internal.logging.MetricsLogger;
import com.android.internal.logging.UiEventLogger;
import com.android.keyguard.KeyguardUpdateMonitor;
import com.android.systemui.R$id;
import com.android.systemui.R$string;
import com.android.systemui.animation.ActivityLaunchAnimator;
import com.android.systemui.globalactions.GlobalActionsDialogLite;
import com.android.systemui.plugins.ActivityStarter;
import com.android.systemui.plugins.FalsingManager;
import com.android.systemui.settings.UserTracker;
import com.android.systemui.statusbar.phone.MultiUserSwitchController;
import com.android.systemui.statusbar.phone.SettingsButton;
import com.android.systemui.statusbar.policy.DeviceProvisionedController;
import com.android.systemui.statusbar.policy.UserInfoController;
import com.android.systemui.tuner.TunerService;
import com.android.systemui.util.ViewController;

/* renamed from: com.android.systemui.qs.QSFooterViewController */
public class QSFooterViewController extends ViewController<QSFooterView> implements QSFooter {
    /* access modifiers changed from: private */
    public final ActivityStarter mActivityStarter;
    private final TextView mBuildText;
    /* access modifiers changed from: private */
    public final DeviceProvisionedController mDeviceProvisionedController;
    private final View mEdit;
    /* access modifiers changed from: private */
    public boolean mExpanded;
    /* access modifiers changed from: private */
    public final FalsingManager mFalsingManager;
    /* access modifiers changed from: private */
    public final GlobalActionsDialogLite mGlobalActionsDialog;
    private boolean mListening;
    /* access modifiers changed from: private */
    public final MetricsLogger mMetricsLogger;
    private final MultiUserSwitchController mMultiUserSwitchController;
    private final UserInfoController.OnUserInfoChangedListener mOnUserInfoChangedListener = new UserInfoController.OnUserInfoChangedListener() {
        public void onUserInfoChanged(String str, Drawable drawable, String str2) {
            ((QSFooterView) QSFooterViewController.this.mView).onUserInfoChanged(drawable, QSFooterViewController.this.mUserManager.isGuestUser(KeyguardUpdateMonitor.getCurrentUser()));
        }
    };
    private final PageIndicator mPageIndicator;
    /* access modifiers changed from: private */
    public final View mPowerMenuLite;
    private final QSPanelController mQsPanelController;
    private final QuickQSPanelController mQuickQSPanelController;
    /* access modifiers changed from: private */
    public final SettingsButton mSettingsButton;
    private final View mSettingsButtonContainer;
    private final View.OnClickListener mSettingsOnClickListener = new View.OnClickListener() {
        /* access modifiers changed from: private */
        public static /* synthetic */ void lambda$onClick$0() {
        }

        public void onClick(View view) {
            if (QSFooterViewController.this.mExpanded && !QSFooterViewController.this.mFalsingManager.isFalseTap(1)) {
                if (view == QSFooterViewController.this.mSettingsButton) {
                    if (!QSFooterViewController.this.mDeviceProvisionedController.isCurrentUserSetup()) {
                        QSFooterViewController.this.mActivityStarter.postQSRunnableDismissingKeyguard(QSFooterViewController$2$$ExternalSyntheticLambda2.INSTANCE);
                        return;
                    }
                    QSFooterViewController.this.mMetricsLogger.action(QSFooterViewController.this.mExpanded ? 406 : 490);
                    if (QSFooterViewController.this.mSettingsButton.isTunerClick()) {
                        QSFooterViewController.this.mActivityStarter.postQSRunnableDismissingKeyguard(new QSFooterViewController$2$$ExternalSyntheticLambda0(this));
                    } else {
                        QSFooterViewController.this.startSettingsActivity();
                    }
                } else if (view == QSFooterViewController.this.mPowerMenuLite) {
                    QSFooterViewController.this.mUiEventLogger.log(GlobalActionsDialogLite.GlobalActionsEvent.GA_OPEN_QS);
                    QSFooterViewController.this.mGlobalActionsDialog.showOrHideDialog(false, true);
                }
            }
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$onClick$2() {
            if (QSFooterViewController.this.isTunerEnabled()) {
                QSFooterViewController.this.mTunerService.showResetRequest(new QSFooterViewController$2$$ExternalSyntheticLambda1(this));
            } else {
                Toast.makeText(QSFooterViewController.this.getContext(), R$string.tuner_toast, 1).show();
                QSFooterViewController.this.mTunerService.setTunerEnabled(true);
            }
            QSFooterViewController.this.startSettingsActivity();
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$onClick$1() {
            QSFooterViewController.this.startSettingsActivity();
        }
    };
    private final boolean mShowPMLiteButton;
    /* access modifiers changed from: private */
    public final TunerService mTunerService;
    /* access modifiers changed from: private */
    public final UiEventLogger mUiEventLogger;
    private final UserInfoController mUserInfoController;
    /* access modifiers changed from: private */
    public final UserManager mUserManager;
    private final UserTracker mUserTracker;

    QSFooterViewController(QSFooterView qSFooterView, UserManager userManager, UserInfoController userInfoController, ActivityStarter activityStarter, DeviceProvisionedController deviceProvisionedController, UserTracker userTracker, QSPanelController qSPanelController, MultiUserSwitchController multiUserSwitchController, QuickQSPanelController quickQSPanelController, TunerService tunerService, MetricsLogger metricsLogger, FalsingManager falsingManager, boolean z, GlobalActionsDialogLite globalActionsDialogLite, UiEventLogger uiEventLogger) {
        super(qSFooterView);
        this.mUserManager = userManager;
        this.mUserInfoController = userInfoController;
        this.mActivityStarter = activityStarter;
        this.mDeviceProvisionedController = deviceProvisionedController;
        this.mUserTracker = userTracker;
        this.mQsPanelController = qSPanelController;
        this.mQuickQSPanelController = quickQSPanelController;
        this.mTunerService = tunerService;
        this.mMetricsLogger = metricsLogger;
        this.mFalsingManager = falsingManager;
        this.mMultiUserSwitchController = multiUserSwitchController;
        this.mSettingsButton = (SettingsButton) ((QSFooterView) this.mView).findViewById(R$id.settings_button);
        this.mSettingsButtonContainer = ((QSFooterView) this.mView).findViewById(R$id.settings_button_container);
        this.mBuildText = (TextView) ((QSFooterView) this.mView).findViewById(R$id.build);
        this.mEdit = ((QSFooterView) this.mView).findViewById(16908291);
        this.mPageIndicator = (PageIndicator) ((QSFooterView) this.mView).findViewById(R$id.footer_page_indicator);
        this.mPowerMenuLite = ((QSFooterView) this.mView).findViewById(R$id.pm_lite);
        this.mShowPMLiteButton = z;
        this.mGlobalActionsDialog = globalActionsDialogLite;
        this.mUiEventLogger = uiEventLogger;
    }

    /* access modifiers changed from: protected */
    public void onInit() {
        super.onInit();
        this.mMultiUserSwitchController.init();
    }

    /* access modifiers changed from: protected */
    public void onViewAttached() {
        if (this.mShowPMLiteButton) {
            this.mPowerMenuLite.setVisibility(0);
            this.mPowerMenuLite.setOnClickListener(this.mSettingsOnClickListener);
        } else {
            this.mPowerMenuLite.setVisibility(8);
        }
        ((QSFooterView) this.mView).addOnLayoutChangeListener(new QSFooterViewController$$ExternalSyntheticLambda1(this));
        this.mSettingsButton.setOnClickListener(this.mSettingsOnClickListener);
        this.mBuildText.setOnLongClickListener(new QSFooterViewController$$ExternalSyntheticLambda2(this));
        this.mEdit.setOnClickListener(new QSFooterViewController$$ExternalSyntheticLambda0(this));
        this.mQsPanelController.setFooterPageIndicator(this.mPageIndicator);
        ((QSFooterView) this.mView).updateEverything(isTunerEnabled(), this.mMultiUserSwitchController.isMultiUserEnabled());
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onViewAttached$0(View view, int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8) {
        ((QSFooterView) this.mView).updateAnimator(i3 - i, this.mQuickQSPanelController.getNumQuickTiles());
    }

    /* access modifiers changed from: private */
    public /* synthetic */ boolean lambda$onViewAttached$1(View view) {
        CharSequence text = this.mBuildText.getText();
        if (TextUtils.isEmpty(text)) {
            return false;
        }
        ((ClipboardManager) this.mUserTracker.getUserContext().getSystemService(ClipboardManager.class)).setPrimaryClip(ClipData.newPlainText(getResources().getString(R$string.build_number_clip_data_label), text));
        Toast.makeText(getContext(), R$string.build_number_copy_toast, 0).show();
        return true;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onViewAttached$3(View view) {
        if (!this.mFalsingManager.isFalseTap(1)) {
            this.mActivityStarter.postQSRunnableDismissingKeyguard(new QSFooterViewController$$ExternalSyntheticLambda3(this, view));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onViewAttached$2(View view) {
        this.mQsPanelController.showEdit(view);
    }

    /* access modifiers changed from: protected */
    public void onViewDetached() {
        setListening(false);
    }

    public void setVisibility(int i) {
        ((QSFooterView) this.mView).setVisibility(i);
    }

    public void setExpanded(boolean z) {
        this.mExpanded = z;
        ((QSFooterView) this.mView).setExpanded(z, isTunerEnabled(), this.mMultiUserSwitchController.isMultiUserEnabled());
    }

    public void setExpansion(float f) {
        ((QSFooterView) this.mView).setExpansion(f);
    }

    public void setListening(boolean z) {
        if (this.mListening != z) {
            this.mListening = z;
            if (z) {
                this.mUserInfoController.addCallback(this.mOnUserInfoChangedListener);
            } else {
                this.mUserInfoController.removeCallback(this.mOnUserInfoChangedListener);
            }
        }
    }

    public void setKeyguardShowing(boolean z) {
        ((QSFooterView) this.mView).setKeyguardShowing();
    }

    public void setExpandClickListener(View.OnClickListener onClickListener) {
        ((QSFooterView) this.mView).setExpandClickListener(onClickListener);
    }

    public void disable(int i, int i2, boolean z) {
        ((QSFooterView) this.mView).disable(i2, isTunerEnabled(), this.mMultiUserSwitchController.isMultiUserEnabled());
    }

    /* access modifiers changed from: private */
    public void startSettingsActivity() {
        View view = this.mSettingsButtonContainer;
        this.mActivityStarter.startActivity(new Intent("android.settings.SETTINGS"), true, view != null ? ActivityLaunchAnimator.Controller.fromView(view, 33) : null);
    }

    /* access modifiers changed from: private */
    public boolean isTunerEnabled() {
        return this.mTunerService.isTunerEnabled();
    }
}
