package com.android.systemui.wmshell;

import android.content.Context;
import com.android.p011wm.shell.common.DisplayController;
import com.android.p011wm.shell.draganddrop.DragAndDropController;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import javax.inject.Provider;

public final class WMShellBaseModule_ProvideDragAndDropControllerFactory implements Factory<DragAndDropController> {
    private final Provider<Context> contextProvider;
    private final Provider<DisplayController> displayControllerProvider;

    public WMShellBaseModule_ProvideDragAndDropControllerFactory(Provider<Context> provider, Provider<DisplayController> provider2) {
        this.contextProvider = provider;
        this.displayControllerProvider = provider2;
    }

    public DragAndDropController get() {
        return provideDragAndDropController(this.contextProvider.get(), this.displayControllerProvider.get());
    }

    public static WMShellBaseModule_ProvideDragAndDropControllerFactory create(Provider<Context> provider, Provider<DisplayController> provider2) {
        return new WMShellBaseModule_ProvideDragAndDropControllerFactory(provider, provider2);
    }

    public static DragAndDropController provideDragAndDropController(Context context, DisplayController displayController) {
        return (DragAndDropController) Preconditions.checkNotNullFromProvides(WMShellBaseModule.provideDragAndDropController(context, displayController));
    }
}
