package com.android.systemui.volume;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.media.VolumePolicy;
import android.os.Bundle;
import com.android.settingslib.applications.InterestingConfigChanges;
import com.android.systemui.Dependency;
import com.android.systemui.demomode.DemoMode;
import com.android.systemui.demomode.DemoModeController;
import com.android.systemui.keyguard.KeyguardViewMediator;
import com.android.systemui.moto.MotoFeature;
import com.android.systemui.p006qs.tiles.DndTile;
import com.android.systemui.plugins.ActivityStarter;
import com.android.systemui.plugins.PluginDependencyProvider;
import com.android.systemui.plugins.VolumeDialog;
import com.android.systemui.plugins.VolumeDialogController;
import com.android.systemui.statusbar.policy.ExtensionController;
import com.android.systemui.tuner.TunerService;
import com.android.systemui.volume.VolumeDialogControllerImpl;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class VolumeDialogComponent implements VolumeComponent, TunerService.Tunable, VolumeDialogControllerImpl.UserActivityListener {
    private VolumeDialog mCliDialog;
    private final InterestingConfigChanges mConfigChanges = new InterestingConfigChanges(-1073741308);
    protected final Context mContext;
    private final VolumeDialogControllerImpl mController;
    private VolumeDialog mDialog;
    private final KeyguardViewMediator mKeyguardViewMediator;
    private final VolumeDialog.Callback mVolumeDialogCallback;
    private VolumePolicy mVolumePolicy = new VolumePolicy(false, false, false, 400);

    public void dispatchDemoCommand(String str, Bundle bundle) {
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
    }

    public VolumeDialogComponent(Context context, KeyguardViewMediator keyguardViewMediator, VolumeDialogControllerImpl volumeDialogControllerImpl, DemoModeController demoModeController) {
        Class<VolumeDialog> cls = VolumeDialog.class;
        C21351 r1 = new VolumeDialog.Callback() {
            public void onZenSettingsClicked() {
                VolumeDialogComponent.this.startSettings(ZenModePanel.ZEN_SETTINGS);
            }

            public void onZenPrioritySettingsClicked() {
                VolumeDialogComponent.this.startSettings(ZenModePanel.ZEN_PRIORITY_SETTINGS);
            }
        };
        this.mVolumeDialogCallback = r1;
        this.mContext = context;
        this.mKeyguardViewMediator = keyguardViewMediator;
        this.mController = volumeDialogControllerImpl;
        volumeDialogControllerImpl.setUserActivityListener(this);
        ((PluginDependencyProvider) Dependency.get(PluginDependencyProvider.class)).allowPluginDependency(VolumeDialogController.class);
        ((ExtensionController) Dependency.get(ExtensionController.class)).newExtension(cls).withPlugin(cls).withDefault(new VolumeDialogComponent$$ExternalSyntheticLambda1(this)).withCallback(new VolumeDialogComponent$$ExternalSyntheticLambda0(this)).build();
        if (MotoFeature.getInstance(context).isSupportCli()) {
            VolumeDialog volumeDialog = this.mCliDialog;
            if (volumeDialog != null) {
                volumeDialog.destroy();
            }
            VolumeDialogImpl volumeDialogImpl = new VolumeDialogImpl(MotoFeature.getCliContext(context));
            volumeDialogImpl.setStreamImportant(1, false);
            volumeDialogImpl.setAutomute(true);
            volumeDialogImpl.setSilentMode(false);
            this.mCliDialog = volumeDialogImpl;
            volumeDialogImpl.init(2020, r1);
        }
        applyConfiguration();
        ((TunerService) Dependency.get(TunerService.class)).addTunable(this, "sysui_volume_down_silent", "sysui_volume_up_silent", "sysui_do_not_disturb");
        demoModeController.addCallback((DemoMode) this);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0(VolumeDialog volumeDialog) {
        VolumeDialog volumeDialog2 = this.mDialog;
        if (volumeDialog2 != null) {
            volumeDialog2.destroy();
        }
        this.mDialog = volumeDialog;
        volumeDialog.init(2020, this.mVolumeDialogCallback);
    }

    /* access modifiers changed from: protected */
    public VolumeDialog createDefault() {
        VolumeDialogImpl volumeDialogImpl = new VolumeDialogImpl(this.mContext);
        volumeDialogImpl.setStreamImportant(1, false);
        volumeDialogImpl.setAutomute(true);
        volumeDialogImpl.setSilentMode(false);
        return volumeDialogImpl;
    }

    public void onTuningChanged(String str, String str2) {
        VolumePolicy volumePolicy = this.mVolumePolicy;
        boolean z = volumePolicy.volumeDownToEnterSilent;
        boolean z2 = volumePolicy.volumeUpToExitSilent;
        boolean z3 = volumePolicy.doNotDisturbWhenSilent;
        if ("sysui_volume_down_silent".equals(str)) {
            z = TunerService.parseIntegerSwitch(str2, false);
        } else if ("sysui_volume_up_silent".equals(str)) {
            z2 = TunerService.parseIntegerSwitch(str2, false);
        } else if ("sysui_do_not_disturb".equals(str)) {
            z3 = TunerService.parseIntegerSwitch(str2, false);
        }
        setVolumePolicy(z, z2, z3, this.mVolumePolicy.vibrateToSilentDebounce);
    }

    private void setVolumePolicy(boolean z, boolean z2, boolean z3, int i) {
        VolumePolicy volumePolicy = new VolumePolicy(z, z2, z3, i);
        this.mVolumePolicy = volumePolicy;
        this.mController.setVolumePolicy(volumePolicy);
    }

    /* access modifiers changed from: package-private */
    public void setEnableDialogs(boolean z, boolean z2) {
        this.mController.setEnableDialogs(z, z2);
    }

    public void onUserActivity() {
        this.mKeyguardViewMediator.userActivity();
    }

    private void applyConfiguration() {
        this.mController.setVolumePolicy(this.mVolumePolicy);
        this.mController.showDndTile(true);
    }

    public void onConfigurationChanged(Configuration configuration) {
        if (this.mConfigChanges.applyNewConfig(this.mContext.getResources())) {
            this.mController.mCallbacks.onConfigurationChanged();
        }
    }

    public void dismissNow() {
        this.mController.dismiss();
    }

    public List<String> demoCommands() {
        ArrayList arrayList = new ArrayList();
        arrayList.add("volume");
        return arrayList;
    }

    public void register() {
        this.mController.register();
        DndTile.setCombinedIcon(this.mContext, true);
    }

    /* access modifiers changed from: private */
    public void startSettings(Intent intent) {
        ((ActivityStarter) Dependency.get(ActivityStarter.class)).startActivity(intent, true, true);
    }
}
