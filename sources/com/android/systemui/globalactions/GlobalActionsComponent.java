package com.android.systemui.globalactions;

import android.content.Context;
import android.os.RemoteException;
import android.os.ServiceManager;
import com.android.internal.statusbar.IStatusBarService;
import com.android.systemui.SystemUI;
import com.android.systemui.moto.MotoFeature;
import com.android.systemui.plugins.GlobalActions;
import com.android.systemui.statusbar.CommandQueue;
import com.android.systemui.statusbar.phone.StatusBarKeyguardViewManager;
import com.android.systemui.statusbar.policy.ExtensionController;
import java.util.Objects;
import javax.inject.Provider;

public class GlobalActionsComponent extends SystemUI implements CommandQueue.Callbacks, GlobalActions.GlobalActionsManager {
    private IStatusBarService mBarService;
    private GlobalActions mCliGlobalActions;
    private final CommandQueue mCommandQueue;
    private ExtensionController.Extension<GlobalActions> mExtension;
    private final ExtensionController mExtensionController;
    private final Provider<GlobalActions> mGlobalActionsProvider;
    private GlobalActions mPlugin;
    private StatusBarKeyguardViewManager mStatusBarKeyguardViewManager;

    public GlobalActionsComponent(Context context, CommandQueue commandQueue, ExtensionController extensionController, Provider<GlobalActions> provider, StatusBarKeyguardViewManager statusBarKeyguardViewManager) {
        super(context);
        this.mCommandQueue = commandQueue;
        this.mExtensionController = extensionController;
        this.mGlobalActionsProvider = provider;
        this.mStatusBarKeyguardViewManager = statusBarKeyguardViewManager;
    }

    public void start() {
        Class<GlobalActions> cls = GlobalActions.class;
        this.mBarService = IStatusBarService.Stub.asInterface(ServiceManager.getService("statusbar"));
        ExtensionController.ExtensionBuilder<GlobalActions> withPlugin = this.mExtensionController.newExtension(cls).withPlugin(cls);
        Provider<GlobalActions> provider = this.mGlobalActionsProvider;
        Objects.requireNonNull(provider);
        this.mExtension = withPlugin.withDefault(new GlobalActionsComponent$$ExternalSyntheticLambda1(provider)).withCallback(new GlobalActionsComponent$$ExternalSyntheticLambda0(this)).build();
        if (MotoFeature.getInstance(this.mContext).isSupportCli()) {
            ExtensionController.ExtensionBuilder<GlobalActions> withPlugin2 = this.mExtensionController.newExtension(cls).withPlugin(cls);
            Provider<GlobalActions> provider2 = this.mGlobalActionsProvider;
            Objects.requireNonNull(provider2);
            GlobalActions globalActions = withPlugin2.withDefault(new GlobalActionsComponent$$ExternalSyntheticLambda1(provider2)).withCallback(new GlobalActionsComponent$$ExternalSyntheticLambda0(this)).build().get();
            this.mCliGlobalActions = globalActions;
            globalActions.updateForCli();
        }
        this.mPlugin = this.mExtension.get();
        this.mCommandQueue.addCallback((CommandQueue.Callbacks) this);
    }

    /* access modifiers changed from: private */
    public void onExtensionCallback(GlobalActions globalActions) {
        GlobalActions globalActions2 = this.mPlugin;
        if (globalActions2 != null) {
            globalActions2.destroy();
        }
        this.mPlugin = globalActions;
    }

    public void handleShowShutdownUi(boolean z, String str) {
        GlobalActions globalActions;
        if (MotoFeature.getInstance(this.mContext).isSupportCli() && MotoFeature.isLidClosed(this.mContext) && (globalActions = this.mCliGlobalActions) != null) {
            globalActions.showShutdownUi(z, str);
        }
        this.mExtension.get().showShutdownUi(z, str);
    }

    public void handleShowGlobalActionsMenu() {
        this.mStatusBarKeyguardViewManager.setGlobalActionsVisible(true);
        if (!MotoFeature.getInstance(this.mContext).isSupportCli() || !MotoFeature.isLidClosed(this.mContext)) {
            this.mExtension.get().showGlobalActions(this);
            return;
        }
        GlobalActions globalActions = this.mCliGlobalActions;
        if (globalActions != null) {
            globalActions.showGlobalActions(this);
        }
    }

    public void onGlobalActionsShown() {
        try {
            this.mBarService.onGlobalActionsShown();
        } catch (RemoteException unused) {
        }
    }

    public void onGlobalActionsHidden() {
        try {
            this.mStatusBarKeyguardViewManager.setGlobalActionsVisible(false);
            this.mBarService.onGlobalActionsHidden();
        } catch (RemoteException unused) {
        }
    }

    public void shutdown() {
        try {
            this.mBarService.shutdown();
        } catch (RemoteException unused) {
        }
    }

    public void reboot(boolean z) {
        try {
            this.mBarService.reboot(z);
        } catch (RemoteException unused) {
        }
    }
}
