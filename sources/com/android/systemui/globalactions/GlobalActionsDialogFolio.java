package com.android.systemui.globalactions;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.IActivityManager;
import android.app.admin.DevicePolicyManager;
import android.app.trust.TrustManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.content.pm.UserInfo;
import android.content.res.Resources;
import android.media.AudioManager;
import android.os.Build;
import android.os.Handler;
import android.os.PowerManager;
import android.os.SystemClock;
import android.os.UserManager;
import android.os.Vibrator;
import android.service.dreams.IDreamManager;
import android.telecom.TelecomManager;
import android.util.Log;
import android.view.IWindowManager;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import com.android.internal.logging.MetricsLogger;
import com.android.internal.logging.UiEventLogger;
import com.android.internal.statusbar.IStatusBarService;
import com.android.internal.widget.LockPatternUtils;
import com.android.systemui.R$array;
import com.android.systemui.R$color;
import com.android.systemui.R$layout;
import com.android.systemui.R$style;
import com.android.systemui.animation.Interpolators;
import com.android.systemui.broadcast.BroadcastDispatcher;
import com.android.systemui.colorextraction.SysuiColorExtractor;
import com.android.systemui.globalactions.GlobalActionsDialogLite;
import com.android.systemui.model.SysUiState;
import com.android.systemui.navigationbar.NavigationBarView;
import com.android.systemui.plugins.GlobalActions;
import com.android.systemui.statusbar.NotificationShadeWindowController;
import com.android.systemui.statusbar.phone.StatusBar;
import com.android.systemui.statusbar.policy.ConfigurationController;
import com.android.systemui.statusbar.policy.KeyguardStateController;
import com.android.systemui.telephony.TelephonyListenerManager;
import com.android.systemui.util.RingerModeTracker;
import com.android.systemui.util.settings.GlobalSettings;
import com.android.systemui.util.settings.SecureSettings;
import java.util.concurrent.Executor;

public class GlobalActionsDialogFolio extends GlobalActionsDialogLite {
    /* access modifiers changed from: private */
    public static final boolean DEBUG = (!Build.IS_USER);
    private final NotificationShadeWindowController mNotificationShadeWindowController;
    private final IStatusBarService mStatusBarService;
    private final SysUiState mSysUiState;
    private final SysuiColorExtractor mSysuiColorExtractor;
    /* access modifiers changed from: private */
    public final GlobalActions.GlobalActionsManager mWindowManagerFuncs;

    /* access modifiers changed from: protected */
    public int getMaxShownPowerItems() {
        return 4;
    }

    /* access modifiers changed from: package-private */
    public boolean shouldDisplayLockdown(UserInfo userInfo) {
        return true;
    }

    /* access modifiers changed from: protected */
    public boolean shouldShowAction(GlobalActionsDialogLite.Action action) {
        return true;
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public GlobalActionsDialogFolio(Context context, GlobalActions.GlobalActionsManager globalActionsManager, AudioManager audioManager, IDreamManager iDreamManager, DevicePolicyManager devicePolicyManager, LockPatternUtils lockPatternUtils, BroadcastDispatcher broadcastDispatcher, TelephonyListenerManager telephonyListenerManager, GlobalSettings globalSettings, SecureSettings secureSettings, Vibrator vibrator, Resources resources, ConfigurationController configurationController, KeyguardStateController keyguardStateController, UserManager userManager, TrustManager trustManager, IActivityManager iActivityManager, TelecomManager telecomManager, MetricsLogger metricsLogger, SysuiColorExtractor sysuiColorExtractor, IStatusBarService iStatusBarService, NotificationShadeWindowController notificationShadeWindowController, IWindowManager iWindowManager, Executor executor, UiEventLogger uiEventLogger, RingerModeTracker ringerModeTracker, SysUiState sysUiState, Handler handler, PackageManager packageManager, StatusBar statusBar) {
        super(context, globalActionsManager, audioManager, iDreamManager, devicePolicyManager, lockPatternUtils, broadcastDispatcher, telephonyListenerManager, globalSettings, secureSettings, vibrator, resources, configurationController, keyguardStateController, userManager, trustManager, iActivityManager, telecomManager, metricsLogger, sysuiColorExtractor, iStatusBarService, notificationShadeWindowController, iWindowManager, executor, uiEventLogger, (GlobalActionsInfoProvider) null, ringerModeTracker, sysUiState, handler, packageManager, statusBar);
        this.mWindowManagerFuncs = globalActionsManager;
        this.mSysuiColorExtractor = sysuiColorExtractor;
        this.mStatusBarService = iStatusBarService;
        this.mNotificationShadeWindowController = notificationShadeWindowController;
        this.mSysUiState = sysUiState;
    }

    /* access modifiers changed from: protected */
    public int getGridItemLayoutResource() {
        return R$layout.zz_moto_global_actions_item_folio;
    }

    /* access modifiers changed from: protected */
    public String[] getDefaultActions() {
        return this.mResources.getStringArray(R$array.zz_moto_config_globalActionsListFolio);
    }

    /* access modifiers changed from: protected */
    public int getEmergencyTextColor(Context context) {
        return context.getResources().getColor(R$color.global_actions_lite_emergency_icon);
    }

    /* access modifiers changed from: protected */
    public void createActionItems() {
        super.createActionItems();
        this.mItems.add(new FolioShutDownAction());
        this.mItems.add(new FolioRestartAction());
        this.mItems.add(new FolioLockDownAction());
    }

    /* access modifiers changed from: protected */
    public GlobalActionsDialogLite.ActionsDialogLite createDialog() {
        initDialogItems();
        ActionDialogFolio actionDialogFolio = new ActionDialogFolio(getContext(), this.mAdapter, this.mOverflowAdapter, this.mSysuiColorExtractor, this.mStatusBarService, this.mNotificationShadeWindowController, this.mSysUiState, new GlobalActionsDialogFolio$$ExternalSyntheticLambda0(this), isKeyguardShowing(), this.mPowerAdapter, getEventLogger(), getStatusBar());
        actionDialogFolio.setOnDismissListener(this);
        actionDialogFolio.setOnShowListener(this);
        return actionDialogFolio;
    }

    public void onShow(DialogInterface dialogInterface) {
        super.onShow(dialogInterface);
        NavigationBarView navigationBarView = getStatusBar().getNavigationBarView();
        if (navigationBarView != null) {
            navigationBarView.setVisibility(4);
        }
    }

    public void onDismiss(DialogInterface dialogInterface) {
        super.onDismiss(dialogInterface);
        NavigationBarView navigationBarView = getStatusBar().getNavigationBarView();
        if (navigationBarView != null) {
            navigationBarView.setVisibility(0);
        }
    }

    final class FolioRestartAction extends GlobalActionsDialogLite.SinglePressAction {
        public boolean showBeforeProvisioning() {
            return true;
        }

        public boolean showDuringKeyguard() {
            return true;
        }

        FolioRestartAction() {
            super(17303702, 17040359);
        }

        public void onPress() {
            if (GlobalActionsDialogFolio.DEBUG) {
                Log.d("GlobalActionsFolio", "press folio restart");
            }
            GlobalActionsDialogFolio.this.mWindowManagerFuncs.reboot(false);
        }
    }

    final class FolioShutDownAction extends GlobalActionsDialogLite.SinglePressAction {
        public boolean showBeforeProvisioning() {
            return true;
        }

        public boolean showDuringKeyguard() {
            return true;
        }

        FolioShutDownAction() {
            super(17301552, 17040357);
        }

        public void onPress() {
            if (GlobalActionsDialogFolio.DEBUG) {
                Log.d("GlobalActionsFolio", "press folio shutdown");
            }
            GlobalActionsDialogFolio.this.mWindowManagerFuncs.shutdown();
        }
    }

    class FolioLockDownAction extends GlobalActionsDialogLite.LockDownAction {
        FolioLockDownAction() {
            super();
        }

        public void onPress() {
            try {
                if (GlobalActionsDialogFolio.DEBUG) {
                    Log.d("GlobalActionsFolio", "press folio lock down");
                }
                ((PowerManager) GlobalActionsDialogFolio.this.getContext().getSystemService(PowerManager.class)).goToSleep(SystemClock.uptimeMillis());
            } catch (Exception e) {
                if (GlobalActionsDialogFolio.DEBUG) {
                    Log.d("GlobalActionsFolio", "lock down error", e);
                }
            }
        }
    }

    static class ActionDialogFolio extends GlobalActionsDialogLite.ActionsDialogLite {
        ActionDialogFolio(Context context, GlobalActionsDialogLite.MyAdapter myAdapter, GlobalActionsDialogLite.MyOverflowAdapter myOverflowAdapter, SysuiColorExtractor sysuiColorExtractor, IStatusBarService iStatusBarService, NotificationShadeWindowController notificationShadeWindowController, SysUiState sysUiState, Runnable runnable, boolean z, GlobalActionsDialogLite.MyPowerOptionsAdapter myPowerOptionsAdapter, UiEventLogger uiEventLogger, StatusBar statusBar) {
            super(context, R$style.Theme_SystemUI_Dialog_GlobalActions, myAdapter, myOverflowAdapter, sysuiColorExtractor, iStatusBarService, notificationShadeWindowController, sysUiState, runnable, z, myPowerOptionsAdapter, uiEventLogger, (GlobalActionsInfoProvider) null, statusBar);
            Window window = getWindow();
            window.setType(2951);
            window.getAttributes().systemUiVisibility |= 1792;
            window.setLayout(-1, -1);
            window.clearFlags(2);
            window.addFlags(17563936);
            WindowInsetsController insetsController = window.getInsetsController();
            if (insetsController != null) {
                insetsController.hide(WindowInsets.Type.navigationBars() | WindowInsets.Type.statusBars());
            }
            setTitle("GlobalActionsFolio");
            initializeLayout();
        }

        /* access modifiers changed from: protected */
        public void initializeLayout() {
            super.initializeLayout();
            getWindow().setBackgroundDrawable(this.mBackgroundDrawable);
        }

        /* access modifiers changed from: protected */
        public void showDialog() {
            this.mShowing = true;
            this.mNotificationShadeWindowController.setRequestTopUi(true, "GlobalActionsFolio");
            this.mSysUiState.setFlag(32768, true).commitUpdate(this.mContext.getDisplayId());
            ViewGroup viewGroup = (ViewGroup) this.mGlobalActionsLayout.getRootView();
            viewGroup.setOnApplyWindowInsetsListener(new C0944x7904191e(viewGroup));
            this.mBackgroundDrawable.setAlpha(0);
            ObjectAnimator ofFloat = ObjectAnimator.ofFloat(this.mContainer, "alpha", new float[]{0.0f, 1.0f});
            ofFloat.setInterpolator(Interpolators.LINEAR_OUT_SLOW_IN);
            ofFloat.setDuration(183);
            ofFloat.addUpdateListener(new C0943x7904191d(this));
            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.play(ofFloat);
            animatorSet.start();
            setCancelable(false);
            setCanceledOnTouchOutside(false);
            this.mTapBlankToDismiss = false;
            ((PowerManager) getContext().getSystemService(PowerManager.class)).wakeUp(SystemClock.uptimeMillis(), "show folio global action");
            if (this.mContainer != null) {
                int i = this.mContext.getResources().getDisplayMetrics().widthPixels;
                int i2 = this.mContext.getResources().getDisplayMetrics().heightPixels;
                this.mContainer.setRotation(90.0f);
                ViewGroup.LayoutParams layoutParams = this.mContainer.getLayoutParams();
                layoutParams.width = i2;
                layoutParams.height = i;
                this.mContainer.setLayoutParams(layoutParams);
            }
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$showDialog$1(ValueAnimator valueAnimator) {
            this.mBackgroundDrawable.setAlpha((int) (valueAnimator.getAnimatedFraction() * this.mScrimAlpha * 255.0f));
        }

        /* access modifiers changed from: protected */
        public int getLayoutResource() {
            return R$layout.zz_moto_global_actions_dialog_folio;
        }
    }
}
