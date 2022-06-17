package com.android.systemui.wmshell;

import com.android.p011wm.shell.common.TransactionPool;
import dagger.internal.Factory;
import dagger.internal.Preconditions;

public final class WMShellBaseModule_ProvideTransactionPoolFactory implements Factory<TransactionPool> {

    private static final class InstanceHolder {
        /* access modifiers changed from: private */
        public static final WMShellBaseModule_ProvideTransactionPoolFactory INSTANCE = new WMShellBaseModule_ProvideTransactionPoolFactory();
    }

    public TransactionPool get() {
        return provideTransactionPool();
    }

    public static WMShellBaseModule_ProvideTransactionPoolFactory create() {
        return InstanceHolder.INSTANCE;
    }

    public static TransactionPool provideTransactionPool() {
        return (TransactionPool) Preconditions.checkNotNullFromProvides(WMShellBaseModule.provideTransactionPool());
    }
}
