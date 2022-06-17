package com.android.systemui.globalactions;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.util.Log;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.android.settingslib.Utils;
import com.android.systemui.R$attr;
import com.android.systemui.R$bool;
import com.android.systemui.R$color;
import com.android.systemui.R$dimen;
import com.android.systemui.R$drawable;
import com.android.systemui.R$style;
import com.android.systemui.moto.MotoFeature;
import com.android.systemui.plugins.GlobalActions;
import com.android.systemui.scrim.ScrimDrawable;
import com.android.systemui.statusbar.BlurUtils;
import com.android.systemui.statusbar.CommandQueue;
import com.android.systemui.statusbar.policy.DeviceProvisionedController;
import com.android.systemui.statusbar.policy.KeyguardStateController;
import com.motorola.systemui.folio.FolioSensorManager;
import dagger.Lazy;

public class GlobalActionsImpl implements GlobalActions, CommandQueue.Callbacks {
    private static final boolean DEBUG = (!Build.IS_USER);
    private final BlurUtils mBlurUtils;
    private FolioSensorManager.Callback mCallback;
    private final CommandQueue mCommandQueue;
    private Context mContext;
    private final DeviceProvisionedController mDeviceProvisionedController;
    private boolean mDisabled;
    private GlobalActionsDialogLite mFolioGlobalActionDialog;
    private final Lazy<GlobalActionsDialogFolio> mFolioGlobalActionsDialogLazy;
    private FolioSensorManager mFolioSensorManager;
    private GlobalActionsDialogLite mGlobalActionsDialog;
    private final Lazy<GlobalActionsDialogLite> mGlobalActionsDialogLazy;
    private boolean mIsFolioClose = false;
    private final KeyguardStateController mKeyguardStateController;

    public GlobalActionsImpl(Context context, CommandQueue commandQueue, Lazy<GlobalActionsDialogLite> lazy, BlurUtils blurUtils, KeyguardStateController keyguardStateController, DeviceProvisionedController deviceProvisionedController, Lazy<GlobalActionsDialogFolio> lazy2) {
        this.mContext = context;
        this.mGlobalActionsDialogLazy = lazy;
        this.mKeyguardStateController = keyguardStateController;
        this.mDeviceProvisionedController = deviceProvisionedController;
        this.mCommandQueue = commandQueue;
        this.mBlurUtils = blurUtils;
        commandQueue.addCallback((CommandQueue.Callbacks) this);
        this.mFolioGlobalActionsDialogLazy = lazy2;
        if (context.getResources().getBoolean(R$bool.zz_moto_folio_product)) {
            FolioSensorManager instance = FolioSensorManager.getInstance(this.mContext);
            this.mFolioSensorManager = instance;
            GlobalActionsImpl$$ExternalSyntheticLambda1 globalActionsImpl$$ExternalSyntheticLambda1 = new GlobalActionsImpl$$ExternalSyntheticLambda1(this);
            this.mCallback = globalActionsImpl$$ExternalSyntheticLambda1;
            instance.addSensorChangeListener(globalActionsImpl$$ExternalSyntheticLambda1);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0(boolean z) {
        if (this.mIsFolioClose != z) {
            if (DEBUG) {
                Log.d("FolioGlobalAction", "onSensorChanged: " + z);
            }
            this.mIsFolioClose = z;
            handlerFolioStateChange();
        }
    }

    public void updateForCli() {
        Context context = this.mContext;
        if (context != null && !MotoFeature.isCliContext(context)) {
            this.mContext = MotoFeature.getCliContext(this.mContext);
        }
    }

    public void destroy() {
        FolioSensorManager.Callback callback;
        this.mCommandQueue.removeCallback((CommandQueue.Callbacks) this);
        GlobalActionsDialogLite globalActionsDialogLite = this.mGlobalActionsDialog;
        if (globalActionsDialogLite != null) {
            globalActionsDialogLite.destroy();
            this.mGlobalActionsDialog = null;
        }
        FolioSensorManager folioSensorManager = this.mFolioSensorManager;
        if (!(folioSensorManager == null || (callback = this.mCallback) == null)) {
            folioSensorManager.removeSensorChangeListener(callback);
        }
        GlobalActionsDialogLite globalActionsDialogLite2 = this.mFolioGlobalActionDialog;
        if (globalActionsDialogLite2 != null) {
            globalActionsDialogLite2.destroy();
            this.mFolioGlobalActionDialog = null;
        }
    }

    public void showGlobalActions(GlobalActions.GlobalActionsManager globalActionsManager) {
        if (!this.mDisabled) {
            this.mGlobalActionsDialog = this.mGlobalActionsDialogLazy.get();
            Context context = this.mContext;
            if (context != null && MotoFeature.isCliContext(context)) {
                this.mGlobalActionsDialog.updateForCli();
            }
            boolean z = DEBUG;
            if (z) {
                Log.d("FolioGlobalAction", "showGlobalActions: close = " + this.mIsFolioClose);
            }
            if (this.mIsFolioClose) {
                if (z) {
                    Log.d("FolioGlobalAction", "show folio global actions");
                }
                this.mGlobalActionsDialog.dismissDialog();
                GlobalActionsDialogLite globalActionsDialogLite = this.mFolioGlobalActionsDialogLazy.get();
                this.mFolioGlobalActionDialog = globalActionsDialogLite;
                globalActionsDialogLite.showOrHideDialog(this.mKeyguardStateController.isShowing(), this.mDeviceProvisionedController.isDeviceProvisioned());
                return;
            }
            this.mGlobalActionsDialog.showOrHideDialog(this.mKeyguardStateController.isShowing(), this.mDeviceProvisionedController.isDeviceProvisioned());
        }
    }

    private void handlerFolioStateChange() {
        GlobalActionsDialogLite globalActionsDialogLite = this.mFolioGlobalActionDialog;
        if (globalActionsDialogLite != null && this.mGlobalActionsDialog != null) {
            if (!this.mIsFolioClose && globalActionsDialogLite.isShowing() && !this.mGlobalActionsDialog.isShowing()) {
                this.mGlobalActionsDialog.showOrHideDialog(this.mKeyguardStateController.isShowing(), this.mDeviceProvisionedController.isDeviceProvisioned());
            }
            this.mFolioGlobalActionDialog.dismissDialog();
        }
    }

    public void showShutdownUi(boolean z, String str) {
        int i;
        if (MotoFeature.isCliContext(this.mContext)) {
            this.mContext.setTheme(R$style.Theme_SystemUI);
        }
        ScrimDrawable scrimDrawable = new ScrimDrawable();
        Dialog dialog = new Dialog(this.mContext, R$style.Theme_SystemUI_Dialog_GlobalActions);
        dialog.setOnShowListener(new GlobalActionsImpl$$ExternalSyntheticLambda0(this, scrimDrawable, dialog));
        Window window = dialog.getWindow();
        window.requestFeature(1);
        window.getAttributes().systemUiVisibility |= 1792;
        window.getDecorView();
        window.getAttributes().width = -1;
        window.getAttributes().height = -1;
        window.getAttributes().layoutInDisplayCutoutMode = 3;
        window.setType(2020);
        window.getAttributes().setFitInsetsTypes(0);
        window.clearFlags(2);
        window.addFlags(17629472);
        if (MotoFeature.isCliContext(this.mContext)) {
            window.setBackgroundDrawableResource(R$drawable.zz_moto_global_action_bg);
        } else {
            window.setBackgroundDrawable(scrimDrawable);
        }
        window.setWindowAnimations(R$style.Animation_ShutdownUi);
        dialog.setContentView(17367309);
        dialog.setCancelable(false);
        if (this.mBlurUtils.supportsBlursOnWindows()) {
            i = Utils.getColorAttrDefaultColor(this.mContext, R$attr.wallpaperTextColor);
        } else {
            i = this.mContext.getResources().getColor(R$color.global_actions_shutdown_ui_text);
        }
        ((ProgressBar) dialog.findViewById(16908301)).getIndeterminateDrawable().setTint(i);
        TextView textView = (TextView) dialog.findViewById(16908308);
        TextView textView2 = (TextView) dialog.findViewById(16908309);
        textView.setTextColor(i);
        textView2.setTextColor(i);
        textView2.setText(getRebootMessage(z, str));
        String reasonMessage = getReasonMessage(str);
        if (reasonMessage != null) {
            textView.setVisibility(0);
            textView.setText(reasonMessage);
        }
        dialog.show();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$showShutdownUi$1(ScrimDrawable scrimDrawable, Dialog dialog, DialogInterface dialogInterface) {
        if (this.mBlurUtils.supportsBlursOnWindows()) {
            scrimDrawable.setAlpha(255);
            this.mBlurUtils.applyBlur(dialog.getWindow().getDecorView().getViewRootImpl(), this.mBlurUtils.blurRadiusOfRatio(1.0f), true);
            return;
        }
        scrimDrawable.setAlpha((int) (this.mContext.getResources().getFloat(R$dimen.shutdown_scrim_behind_alpha) * 255.0f));
    }

    private int getRebootMessage(boolean z, String str) {
        if (str != null && str.startsWith("recovery-update")) {
            return 17041302;
        }
        if ((str == null || !str.equals("recovery")) && !z) {
            return 17041447;
        }
        return 17041298;
    }

    private String getReasonMessage(String str) {
        if (str != null && str.startsWith("recovery-update")) {
            return this.mContext.getString(17041303);
        }
        if (str == null || !str.equals("recovery")) {
            return null;
        }
        return this.mContext.getString(17041299);
    }

    public void disable(int i, int i2, int i3, boolean z) {
        GlobalActionsDialogLite globalActionsDialogLite;
        GlobalActionsDialogLite globalActionsDialogLite2;
        boolean z2 = (i3 & 8) != 0;
        if (i == this.mContext.getDisplayId() && z2 != this.mDisabled) {
            this.mDisabled = z2;
            if (z2 && (globalActionsDialogLite2 = this.mGlobalActionsDialog) != null) {
                globalActionsDialogLite2.dismissDialog();
            }
            if (z2 && (globalActionsDialogLite = this.mFolioGlobalActionDialog) != null) {
                globalActionsDialogLite.dismissDialog();
            }
        }
    }
}
