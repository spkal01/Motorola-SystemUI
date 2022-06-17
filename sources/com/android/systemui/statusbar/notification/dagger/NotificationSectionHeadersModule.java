package com.android.systemui.statusbar.notification.dagger;

import com.android.systemui.R$string;
import com.android.systemui.statusbar.notification.collection.render.NodeController;
import com.android.systemui.statusbar.notification.collection.render.SectionHeaderController;
import com.android.systemui.statusbar.notification.dagger.SectionHeaderControllerSubcomponent;
import javax.inject.Provider;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

/* compiled from: NotificationSectionHeadersModule.kt */
public final class NotificationSectionHeadersModule {
    @NotNull
    public static final NotificationSectionHeadersModule INSTANCE = new NotificationSectionHeadersModule();

    private NotificationSectionHeadersModule() {
    }

    @NotNull
    public static final SectionHeaderControllerSubcomponent providesIncomingHeaderSubcomponent(@NotNull Provider<SectionHeaderControllerSubcomponent.Builder> provider) {
        Intrinsics.checkNotNullParameter(provider, "builder");
        return provider.get().nodeLabel("incoming header").headerText(R$string.notification_section_header_incoming).clickIntentAction("android.settings.NOTIFICATION_SETTINGS").build();
    }

    @NotNull
    public static final SectionHeaderControllerSubcomponent providesAlertingHeaderSubcomponent(@NotNull Provider<SectionHeaderControllerSubcomponent.Builder> provider) {
        Intrinsics.checkNotNullParameter(provider, "builder");
        return provider.get().nodeLabel("alerting header").headerText(R$string.notification_section_header_alerting).clickIntentAction("android.settings.NOTIFICATION_SETTINGS").build();
    }

    @NotNull
    public static final SectionHeaderControllerSubcomponent providesPeopleHeaderSubcomponent(@NotNull Provider<SectionHeaderControllerSubcomponent.Builder> provider) {
        Intrinsics.checkNotNullParameter(provider, "builder");
        return provider.get().nodeLabel("people header").headerText(R$string.notification_section_header_conversations).clickIntentAction("android.settings.CONVERSATION_SETTINGS").build();
    }

    @NotNull
    public static final SectionHeaderControllerSubcomponent providesSilentHeaderSubcomponent(@NotNull Provider<SectionHeaderControllerSubcomponent.Builder> provider) {
        Intrinsics.checkNotNullParameter(provider, "builder");
        return provider.get().nodeLabel("silent header").headerText(R$string.notification_section_header_gentle).clickIntentAction("android.settings.NOTIFICATION_SETTINGS").build();
    }

    @NotNull
    public static final NodeController providesSilentHeaderNodeController(@NotNull SectionHeaderControllerSubcomponent sectionHeaderControllerSubcomponent) {
        Intrinsics.checkNotNullParameter(sectionHeaderControllerSubcomponent, "subcomponent");
        return sectionHeaderControllerSubcomponent.getNodeController();
    }

    @NotNull
    public static final SectionHeaderController providesSilentHeaderController(@NotNull SectionHeaderControllerSubcomponent sectionHeaderControllerSubcomponent) {
        Intrinsics.checkNotNullParameter(sectionHeaderControllerSubcomponent, "subcomponent");
        return sectionHeaderControllerSubcomponent.getHeaderController();
    }

    @NotNull
    public static final NodeController providesAlertingHeaderNodeController(@NotNull SectionHeaderControllerSubcomponent sectionHeaderControllerSubcomponent) {
        Intrinsics.checkNotNullParameter(sectionHeaderControllerSubcomponent, "subcomponent");
        return sectionHeaderControllerSubcomponent.getNodeController();
    }

    @NotNull
    public static final SectionHeaderController providesAlertingHeaderController(@NotNull SectionHeaderControllerSubcomponent sectionHeaderControllerSubcomponent) {
        Intrinsics.checkNotNullParameter(sectionHeaderControllerSubcomponent, "subcomponent");
        return sectionHeaderControllerSubcomponent.getHeaderController();
    }

    @NotNull
    public static final NodeController providesPeopleHeaderNodeController(@NotNull SectionHeaderControllerSubcomponent sectionHeaderControllerSubcomponent) {
        Intrinsics.checkNotNullParameter(sectionHeaderControllerSubcomponent, "subcomponent");
        return sectionHeaderControllerSubcomponent.getNodeController();
    }

    @NotNull
    public static final SectionHeaderController providesPeopleHeaderController(@NotNull SectionHeaderControllerSubcomponent sectionHeaderControllerSubcomponent) {
        Intrinsics.checkNotNullParameter(sectionHeaderControllerSubcomponent, "subcomponent");
        return sectionHeaderControllerSubcomponent.getHeaderController();
    }

    @NotNull
    public static final NodeController providesIncomingHeaderNodeController(@NotNull SectionHeaderControllerSubcomponent sectionHeaderControllerSubcomponent) {
        Intrinsics.checkNotNullParameter(sectionHeaderControllerSubcomponent, "subcomponent");
        return sectionHeaderControllerSubcomponent.getNodeController();
    }

    @NotNull
    public static final SectionHeaderController providesIncomingHeaderController(@NotNull SectionHeaderControllerSubcomponent sectionHeaderControllerSubcomponent) {
        Intrinsics.checkNotNullParameter(sectionHeaderControllerSubcomponent, "subcomponent");
        return sectionHeaderControllerSubcomponent.getHeaderController();
    }
}
