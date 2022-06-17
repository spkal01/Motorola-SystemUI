package com.android.systemui.media.dialog;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.media.session.MediaSessionManager;
import com.android.internal.logging.UiEventLogger;
import com.android.settingslib.bluetooth.LocalBluetoothManager;
import com.android.systemui.plugins.ActivityStarter;
import com.android.systemui.statusbar.notification.NotificationEntryManager;
import com.android.systemui.statusbar.phone.ShadeController;
import com.motorola.internal.app.MotoDesktopManager;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/* compiled from: MediaOutputDialogFactory.kt */
public final class MediaOutputDialogFactory {
    @NotNull
    public static final Companion Companion = new Companion((DefaultConstructorMarker) null);
    /* access modifiers changed from: private */
    @Nullable
    public static MediaOutputDialog mediaOutputDialog;
    /* access modifiers changed from: private */
    @Nullable
    public static PRCMediaPanelWithOutput prcMediaWithOutput;
    @NotNull
    private final String DESKTOP_MEDIA_ACTION = "com.motorola.mobiledesktop.action.MEDIA_OUTPUT";
    @NotNull
    private final Context context;
    @Nullable
    private final LocalBluetoothManager lbm;
    @NotNull
    private final MediaSessionManager mediaSessionManager;
    @NotNull
    private final NotificationEntryManager notificationEntryManager;
    @NotNull
    private final ShadeController shadeController;
    @NotNull
    private final ActivityStarter starter;
    @NotNull
    private final UiEventLogger uiEventLogger;

    public MediaOutputDialogFactory(@NotNull Context context2, @NotNull MediaSessionManager mediaSessionManager2, @Nullable LocalBluetoothManager localBluetoothManager, @NotNull ShadeController shadeController2, @NotNull ActivityStarter activityStarter, @NotNull NotificationEntryManager notificationEntryManager2, @NotNull UiEventLogger uiEventLogger2) {
        Intrinsics.checkNotNullParameter(context2, "context");
        Intrinsics.checkNotNullParameter(mediaSessionManager2, "mediaSessionManager");
        Intrinsics.checkNotNullParameter(shadeController2, "shadeController");
        Intrinsics.checkNotNullParameter(activityStarter, "starter");
        Intrinsics.checkNotNullParameter(notificationEntryManager2, "notificationEntryManager");
        Intrinsics.checkNotNullParameter(uiEventLogger2, "uiEventLogger");
        this.context = context2;
        this.mediaSessionManager = mediaSessionManager2;
        this.lbm = localBluetoothManager;
        this.shadeController = shadeController2;
        this.starter = activityStarter;
        this.notificationEntryManager = notificationEntryManager2;
        this.uiEventLogger = uiEventLogger2;
    }

    /* compiled from: MediaOutputDialogFactory.kt */
    public static final class Companion {
        public /* synthetic */ Companion(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }

        private Companion() {
        }

        @Nullable
        public final MediaOutputDialog getMediaOutputDialog() {
            return MediaOutputDialogFactory.mediaOutputDialog;
        }

        public final void setMediaOutputDialog(@Nullable MediaOutputDialog mediaOutputDialog) {
            MediaOutputDialogFactory.mediaOutputDialog = mediaOutputDialog;
        }

        @Nullable
        public final PRCMediaPanelWithOutput getPrcMediaWithOutput() {
            return MediaOutputDialogFactory.prcMediaWithOutput;
        }

        public final void setPrcMediaWithOutput(@Nullable PRCMediaPanelWithOutput pRCMediaPanelWithOutput) {
            MediaOutputDialogFactory.prcMediaWithOutput = pRCMediaPanelWithOutput;
        }
    }

    public final void create(@NotNull String str, boolean z) {
        Intrinsics.checkNotNullParameter(str, "packageName");
        Companion companion = Companion;
        MediaOutputDialog mediaOutputDialog2 = companion.getMediaOutputDialog();
        if (mediaOutputDialog2 != null) {
            mediaOutputDialog2.dismiss();
        }
        if (!MotoDesktopManager.isDesktopConnected(this.context) || !jumpToDesktopMediaRouteWhenDesktopConnected(this.context)) {
            companion.setMediaOutputDialog(new MediaOutputDialog(this.context, z, new MediaOutputController(this.context, str, z, this.mediaSessionManager, this.lbm, this.shadeController, this.starter, this.notificationEntryManager, this.uiEventLogger), this.uiEventLogger));
        }
    }

    public final void dismiss() {
        Companion companion = Companion;
        MediaOutputDialog mediaOutputDialog2 = companion.getMediaOutputDialog();
        if (mediaOutputDialog2 != null) {
            mediaOutputDialog2.dismiss();
        }
        companion.setMediaOutputDialog((MediaOutputDialog) null);
    }

    private final boolean jumpToDesktopMediaRouteWhenDesktopConnected(Context context2) {
        try {
            context2.startActivity(new Intent().setAction(this.DESKTOP_MEDIA_ACTION).setPackage("com.motorola.mobiledesktop.core").setFlags(268435456));
            return true;
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    public final void showPrcMediaViewPagerWithOutput(@NotNull String str, boolean z) {
        Intrinsics.checkNotNullParameter(str, "packageName");
        Companion companion = Companion;
        PRCMediaPanelWithOutput prcMediaWithOutput2 = companion.getPrcMediaWithOutput();
        if (prcMediaWithOutput2 != null) {
            prcMediaWithOutput2.dismiss();
        }
        companion.setPrcMediaWithOutput(new PRCMediaPanelWithOutput(this.context, z, new MediaOutputController(this.context, str, z, this.mediaSessionManager, this.lbm, this.shadeController, this.starter, this.notificationEntryManager, this.uiEventLogger)));
    }
}
