package com.android.systemui.wmshell;

import com.android.p011wm.shell.FullscreenTaskListener;
import com.android.p011wm.shell.common.SyncTransactionQueue;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import javax.inject.Provider;

public final class WMShellBaseModule_ProvideFullscreenTaskListenerFactory implements Factory<FullscreenTaskListener> {
    private final Provider<SyncTransactionQueue> syncQueueProvider;

    public WMShellBaseModule_ProvideFullscreenTaskListenerFactory(Provider<SyncTransactionQueue> provider) {
        this.syncQueueProvider = provider;
    }

    public FullscreenTaskListener get() {
        return provideFullscreenTaskListener(this.syncQueueProvider.get());
    }

    public static WMShellBaseModule_ProvideFullscreenTaskListenerFactory create(Provider<SyncTransactionQueue> provider) {
        return new WMShellBaseModule_ProvideFullscreenTaskListenerFactory(provider);
    }

    public static FullscreenTaskListener provideFullscreenTaskListener(SyncTransactionQueue syncTransactionQueue) {
        return (FullscreenTaskListener) Preconditions.checkNotNullFromProvides(WMShellBaseModule.provideFullscreenTaskListener(syncTransactionQueue));
    }
}
