package com.android.systemui.wmshell;

import com.android.p011wm.shell.ShellTaskOrganizer;
import com.android.p011wm.shell.TaskViewFactoryController;
import com.android.p011wm.shell.common.ShellExecutor;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import javax.inject.Provider;

public final class WMShellBaseModule_ProvideTaskViewFactoryControllerFactory implements Factory<TaskViewFactoryController> {
    private final Provider<ShellExecutor> mainExecutorProvider;
    private final Provider<ShellTaskOrganizer> shellTaskOrganizerProvider;

    public WMShellBaseModule_ProvideTaskViewFactoryControllerFactory(Provider<ShellTaskOrganizer> provider, Provider<ShellExecutor> provider2) {
        this.shellTaskOrganizerProvider = provider;
        this.mainExecutorProvider = provider2;
    }

    public TaskViewFactoryController get() {
        return provideTaskViewFactoryController(this.shellTaskOrganizerProvider.get(), this.mainExecutorProvider.get());
    }

    public static WMShellBaseModule_ProvideTaskViewFactoryControllerFactory create(Provider<ShellTaskOrganizer> provider, Provider<ShellExecutor> provider2) {
        return new WMShellBaseModule_ProvideTaskViewFactoryControllerFactory(provider, provider2);
    }

    public static TaskViewFactoryController provideTaskViewFactoryController(ShellTaskOrganizer shellTaskOrganizer, ShellExecutor shellExecutor) {
        return (TaskViewFactoryController) Preconditions.checkNotNullFromProvides(WMShellBaseModule.provideTaskViewFactoryController(shellTaskOrganizer, shellExecutor));
    }
}
