package com.android.systemui.p006qs.tiles;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import androidx.lifecycle.LifecycleOwner;
import com.android.internal.logging.MetricsLogger;
import com.android.systemui.R$drawable;
import com.android.systemui.R$string;
import com.android.systemui.animation.ActivityLaunchAnimator;
import com.android.systemui.controls.dagger.ControlsComponent;
import com.android.systemui.controls.management.ControlsListingController;
import com.android.systemui.controls.p004ui.ControlsActivity;
import com.android.systemui.p006qs.QSHost;
import com.android.systemui.p006qs.logging.QSLogger;
import com.android.systemui.p006qs.tileimpl.QSTileImpl;
import com.android.systemui.plugins.ActivityStarter;
import com.android.systemui.plugins.FalsingManager;
import com.android.systemui.plugins.p005qs.QSTile;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.statusbar.policy.KeyguardStateController;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/* renamed from: com.android.systemui.qs.tiles.DeviceControlsTile */
/* compiled from: DeviceControlsTile.kt */
public final class DeviceControlsTile extends QSTileImpl<QSTile.State> {
    @NotNull
    private final ControlsComponent controlsComponent;
    /* access modifiers changed from: private */
    @NotNull
    public AtomicBoolean hasControlsApps = new AtomicBoolean(false);
    private final QSTile.Icon icon = QSTileImpl.ResourceIcon.get(R$drawable.controls_icon);
    /* access modifiers changed from: private */
    @NotNull
    public final KeyguardStateController keyguardStateController;
    /* access modifiers changed from: private */
    @NotNull
    public final DeviceControlsTile$listingCallback$1 listingCallback = new DeviceControlsTile$listingCallback$1(this);

    @Nullable
    public Intent getLongClickIntent() {
        return null;
    }

    public int getMetricsCategory() {
        return 0;
    }

    /* access modifiers changed from: protected */
    public void handleLongClick(@Nullable View view) {
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public DeviceControlsTile(@NotNull QSHost qSHost, @NotNull Looper looper, @NotNull Handler handler, @NotNull FalsingManager falsingManager, @NotNull MetricsLogger metricsLogger, @NotNull StatusBarStateController statusBarStateController, @NotNull ActivityStarter activityStarter, @NotNull QSLogger qSLogger, @NotNull ControlsComponent controlsComponent2, @NotNull KeyguardStateController keyguardStateController2) {
        super(qSHost, looper, handler, falsingManager, metricsLogger, statusBarStateController, activityStarter, qSLogger);
        Intrinsics.checkNotNullParameter(qSHost, "host");
        Intrinsics.checkNotNullParameter(looper, "backgroundLooper");
        Intrinsics.checkNotNullParameter(handler, "mainHandler");
        Intrinsics.checkNotNullParameter(falsingManager, "falsingManager");
        Intrinsics.checkNotNullParameter(metricsLogger, "metricsLogger");
        Intrinsics.checkNotNullParameter(statusBarStateController, "statusBarStateController");
        Intrinsics.checkNotNullParameter(activityStarter, "activityStarter");
        Intrinsics.checkNotNullParameter(qSLogger, "qsLogger");
        Intrinsics.checkNotNullParameter(controlsComponent2, "controlsComponent");
        Intrinsics.checkNotNullParameter(keyguardStateController2, "keyguardStateController");
        this.controlsComponent = controlsComponent2;
        this.keyguardStateController = keyguardStateController2;
        controlsComponent2.getControlsListingController().ifPresent(new Consumer<ControlsListingController>(this) {
            final /* synthetic */ DeviceControlsTile this$0;

            {
                this.this$0 = r1;
            }

            public final void accept(@NotNull ControlsListingController controlsListingController) {
                Intrinsics.checkNotNullParameter(controlsListingController, "it");
                DeviceControlsTile deviceControlsTile = this.this$0;
                controlsListingController.observe((LifecycleOwner) deviceControlsTile, deviceControlsTile.listingCallback);
            }
        });
    }

    public boolean isAvailable() {
        return this.controlsComponent.getControlsController().isPresent();
    }

    @NotNull
    public QSTile.State newTileState() {
        QSTile.State state = new QSTile.State();
        state.state = 0;
        state.handlesLongClick = false;
        return state;
    }

    /* access modifiers changed from: protected */
    public void handleClick(@Nullable View view) {
        ActivityLaunchAnimator.Controller controller;
        if (getState().state != 0) {
            Intent intent = new Intent();
            intent.setComponent(new ComponentName(this.mContext, ControlsActivity.class));
            intent.addFlags(335544320);
            intent.putExtra("extra_animate", true);
            if (view == null) {
                controller = null;
            } else {
                controller = ActivityLaunchAnimator.Controller.Companion.fromView(view, 32);
            }
            this.mUiHandler.post(new DeviceControlsTile$handleClick$1(this, intent, controller));
        }
    }

    /* access modifiers changed from: protected */
    public void handleUpdateState(@NotNull QSTile.State state, @Nullable Object obj) {
        Intrinsics.checkNotNullParameter(state, "state");
        CharSequence tileLabel = getTileLabel();
        state.label = tileLabel;
        state.contentDescription = tileLabel;
        state.icon = this.icon;
        if (!this.controlsComponent.isEnabled() || !this.hasControlsApps.get()) {
            state.state = 0;
            return;
        }
        if (this.controlsComponent.getVisibility() == ControlsComponent.Visibility.AVAILABLE) {
            state.state = 2;
            state.secondaryLabel = this.controlsComponent.getControlsController().get().getPreferredStructure().getStructure();
        } else {
            state.state = 1;
            state.secondaryLabel = this.mContext.getText(R$string.controls_tile_locked);
        }
        state.stateDescription = state.secondaryLabel;
    }

    @NotNull
    public CharSequence getTileLabel() {
        CharSequence text = this.mContext.getText(R$string.quick_controls_title);
        Intrinsics.checkNotNullExpressionValue(text, "mContext.getText(R.string.quick_controls_title)");
        return text;
    }
}
