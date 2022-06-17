package com.android.systemui.dump;

import dagger.internal.Factory;
import javax.inject.Provider;

public final class SystemUIAuxiliaryDumpService_Factory implements Factory<SystemUIAuxiliaryDumpService> {
    private final Provider<DumpHandler> dumpHandlerProvider;

    public SystemUIAuxiliaryDumpService_Factory(Provider<DumpHandler> provider) {
        this.dumpHandlerProvider = provider;
    }

    public SystemUIAuxiliaryDumpService get() {
        return newInstance(this.dumpHandlerProvider.get());
    }

    public static SystemUIAuxiliaryDumpService_Factory create(Provider<DumpHandler> provider) {
        return new SystemUIAuxiliaryDumpService_Factory(provider);
    }

    public static SystemUIAuxiliaryDumpService newInstance(DumpHandler dumpHandler) {
        return new SystemUIAuxiliaryDumpService(dumpHandler);
    }
}
