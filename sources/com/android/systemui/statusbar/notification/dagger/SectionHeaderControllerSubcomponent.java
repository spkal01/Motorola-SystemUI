package com.android.systemui.statusbar.notification.dagger;

import com.android.systemui.statusbar.notification.collection.render.NodeController;
import com.android.systemui.statusbar.notification.collection.render.SectionHeaderController;
import org.jetbrains.annotations.NotNull;

/* compiled from: NotificationSectionHeadersModule.kt */
public interface SectionHeaderControllerSubcomponent {

    /* compiled from: NotificationSectionHeadersModule.kt */
    public interface Builder {
        @NotNull
        SectionHeaderControllerSubcomponent build();

        @NotNull
        Builder clickIntentAction(@NotNull String str);

        @NotNull
        Builder headerText(int i);

        @NotNull
        Builder nodeLabel(@NotNull String str);
    }

    @NotNull
    SectionHeaderController getHeaderController();

    @NotNull
    NodeController getNodeController();
}
