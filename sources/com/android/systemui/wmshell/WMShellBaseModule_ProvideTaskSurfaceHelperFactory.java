package com.android.systemui.wmshell;

import com.android.p011wm.shell.tasksurfacehelper.TaskSurfaceHelper;
import com.android.p011wm.shell.tasksurfacehelper.TaskSurfaceHelperController;
import dagger.internal.Factory;
import java.util.Optional;
import javax.inject.Provider;

public final class WMShellBaseModule_ProvideTaskSurfaceHelperFactory implements Factory<Optional<TaskSurfaceHelper>> {
    private final Provider<Optional<TaskSurfaceHelperController>> taskSurfaceControllerProvider;

    public WMShellBaseModule_ProvideTaskSurfaceHelperFactory(Provider<Optional<TaskSurfaceHelperController>> provider) {
        this.taskSurfaceControllerProvider = provider;
    }

    public Optional<TaskSurfaceHelper> get() {
        return provideTaskSurfaceHelper(this.taskSurfaceControllerProvider.get());
    }

    public static WMShellBaseModule_ProvideTaskSurfaceHelperFactory create(Provider<Optional<TaskSurfaceHelperController>> provider) {
        return new WMShellBaseModule_ProvideTaskSurfaceHelperFactory(provider);
    }

    /* JADX WARNING: type inference failed for: r0v0, types: [java.util.Optional<com.android.wm.shell.tasksurfacehelper.TaskSurfaceHelperController>, java.util.Optional] */
    /* JADX WARNING: Unknown variable types count: 1 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static java.util.Optional<com.android.p011wm.shell.tasksurfacehelper.TaskSurfaceHelper> provideTaskSurfaceHelper(java.util.Optional<com.android.p011wm.shell.tasksurfacehelper.TaskSurfaceHelperController> r0) {
        /*
            java.util.Optional r0 = com.android.systemui.wmshell.WMShellBaseModule.provideTaskSurfaceHelper(r0)
            java.lang.Object r0 = dagger.internal.Preconditions.checkNotNullFromProvides(r0)
            java.util.Optional r0 = (java.util.Optional) r0
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.wmshell.WMShellBaseModule_ProvideTaskSurfaceHelperFactory.provideTaskSurfaceHelper(java.util.Optional):java.util.Optional");
    }
}
