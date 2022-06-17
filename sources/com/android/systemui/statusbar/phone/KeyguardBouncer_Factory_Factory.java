package com.android.systemui.statusbar.phone;

import android.content.Context;
import android.os.Handler;
import com.android.internal.widget.LockPatternUtils;
import com.android.keyguard.KeyguardSecurityModel;
import com.android.keyguard.KeyguardUpdateMonitor;
import com.android.keyguard.ViewMediatorCallback;
import com.android.keyguard.dagger.KeyguardBouncerComponent;
import com.android.systemui.classifier.FalsingCollector;
import com.android.systemui.keyguard.DismissCallbackRegistry;
import com.android.systemui.statusbar.phone.KeyguardBouncer;
import com.android.systemui.statusbar.policy.KeyguardStateController;
import dagger.internal.Factory;
import javax.inject.Provider;

public final class KeyguardBouncer_Factory_Factory implements Factory<KeyguardBouncer.Factory> {
    private final Provider<ViewMediatorCallback> callbackProvider;
    private final Provider<Context> contextProvider;
    private final Provider<DismissCallbackRegistry> dismissCallbackRegistryProvider;
    private final Provider<FalsingCollector> falsingCollectorProvider;
    private final Provider<Handler> handlerProvider;
    private final Provider<KeyguardBouncerComponent.Factory> keyguardBouncerComponentFactoryProvider;
    private final Provider<KeyguardBypassController> keyguardBypassControllerProvider;
    private final Provider<KeyguardSecurityModel> keyguardSecurityModelProvider;
    private final Provider<KeyguardStateController> keyguardStateControllerProvider;
    private final Provider<KeyguardUpdateMonitor> keyguardUpdateMonitorProvider;
    private final Provider<LockPatternUtils> lockPatternUtilsProvider;

    public KeyguardBouncer_Factory_Factory(Provider<Context> provider, Provider<ViewMediatorCallback> provider2, Provider<DismissCallbackRegistry> provider3, Provider<FalsingCollector> provider4, Provider<KeyguardStateController> provider5, Provider<KeyguardUpdateMonitor> provider6, Provider<KeyguardBypassController> provider7, Provider<Handler> provider8, Provider<KeyguardSecurityModel> provider9, Provider<LockPatternUtils> provider10, Provider<KeyguardBouncerComponent.Factory> provider11) {
        this.contextProvider = provider;
        this.callbackProvider = provider2;
        this.dismissCallbackRegistryProvider = provider3;
        this.falsingCollectorProvider = provider4;
        this.keyguardStateControllerProvider = provider5;
        this.keyguardUpdateMonitorProvider = provider6;
        this.keyguardBypassControllerProvider = provider7;
        this.handlerProvider = provider8;
        this.keyguardSecurityModelProvider = provider9;
        this.lockPatternUtilsProvider = provider10;
        this.keyguardBouncerComponentFactoryProvider = provider11;
    }

    public KeyguardBouncer.Factory get() {
        return newInstance(this.contextProvider.get(), this.callbackProvider.get(), this.dismissCallbackRegistryProvider.get(), this.falsingCollectorProvider.get(), this.keyguardStateControllerProvider.get(), this.keyguardUpdateMonitorProvider.get(), this.keyguardBypassControllerProvider.get(), this.handlerProvider.get(), this.keyguardSecurityModelProvider.get(), this.lockPatternUtilsProvider.get(), this.keyguardBouncerComponentFactoryProvider.get());
    }

    public static KeyguardBouncer_Factory_Factory create(Provider<Context> provider, Provider<ViewMediatorCallback> provider2, Provider<DismissCallbackRegistry> provider3, Provider<FalsingCollector> provider4, Provider<KeyguardStateController> provider5, Provider<KeyguardUpdateMonitor> provider6, Provider<KeyguardBypassController> provider7, Provider<Handler> provider8, Provider<KeyguardSecurityModel> provider9, Provider<LockPatternUtils> provider10, Provider<KeyguardBouncerComponent.Factory> provider11) {
        return new KeyguardBouncer_Factory_Factory(provider, provider2, provider3, provider4, provider5, provider6, provider7, provider8, provider9, provider10, provider11);
    }

    public static KeyguardBouncer.Factory newInstance(Context context, ViewMediatorCallback viewMediatorCallback, DismissCallbackRegistry dismissCallbackRegistry, FalsingCollector falsingCollector, KeyguardStateController keyguardStateController, KeyguardUpdateMonitor keyguardUpdateMonitor, KeyguardBypassController keyguardBypassController, Handler handler, KeyguardSecurityModel keyguardSecurityModel, LockPatternUtils lockPatternUtils, KeyguardBouncerComponent.Factory factory) {
        return new KeyguardBouncer.Factory(context, viewMediatorCallback, dismissCallbackRegistry, falsingCollector, keyguardStateController, keyguardUpdateMonitor, keyguardBypassController, handler, keyguardSecurityModel, lockPatternUtils, factory);
    }
}
