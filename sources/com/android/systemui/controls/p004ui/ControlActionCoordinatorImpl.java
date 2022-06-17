package com.android.systemui.controls.p004ui;

import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.service.controls.Control;
import com.android.internal.annotations.VisibleForTesting;
import com.android.p011wm.shell.TaskViewFactory;
import com.android.systemui.broadcast.BroadcastDispatcher;
import com.android.systemui.controls.ControlsMetricsLogger;
import com.android.systemui.globalactions.GlobalActionsComponent;
import com.android.systemui.plugins.ActivityStarter;
import com.android.systemui.statusbar.policy.KeyguardStateController;
import com.android.systemui.util.concurrency.DelayableExecutor;
import dagger.Lazy;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/* renamed from: com.android.systemui.controls.ui.ControlActionCoordinatorImpl */
/* compiled from: ControlActionCoordinatorImpl.kt */
public final class ControlActionCoordinatorImpl implements ControlActionCoordinator {
    @NotNull
    public static final Companion Companion = new Companion((DefaultConstructorMarker) null);
    /* access modifiers changed from: private */
    @NotNull
    public Set<String> actionsInProgress = new LinkedHashSet();
    public Context activityContext;
    @NotNull
    private final ActivityStarter activityStarter;
    @NotNull
    private final DelayableExecutor bgExecutor;
    @NotNull
    private final BroadcastDispatcher broadcastDispatcher;
    /* access modifiers changed from: private */
    @NotNull
    public final Context context;
    @NotNull
    private final ControlsMetricsLogger controlsMetricsLogger;
    /* access modifiers changed from: private */
    @Nullable
    public Dialog dialog;
    @NotNull
    private final GlobalActionsComponent globalActionsComponent;
    @NotNull
    private final KeyguardStateController keyguardStateController;
    @NotNull
    private final Lazy<ControlsUiController> lazyUiController;
    /* access modifiers changed from: private */
    @Nullable
    public Action pendingAction;
    /* access modifiers changed from: private */
    @NotNull
    public final Optional<TaskViewFactory> taskViewFactory;
    /* access modifiers changed from: private */
    @NotNull
    public final DelayableExecutor uiExecutor;
    /* access modifiers changed from: private */
    @NotNull
    public final Vibrator vibrator;

    public ControlActionCoordinatorImpl(@NotNull Context context2, @NotNull DelayableExecutor delayableExecutor, @NotNull DelayableExecutor delayableExecutor2, @NotNull ActivityStarter activityStarter2, @NotNull KeyguardStateController keyguardStateController2, @NotNull GlobalActionsComponent globalActionsComponent2, @NotNull Optional<TaskViewFactory> optional, @NotNull BroadcastDispatcher broadcastDispatcher2, @NotNull Lazy<ControlsUiController> lazy, @NotNull ControlsMetricsLogger controlsMetricsLogger2) {
        Intrinsics.checkNotNullParameter(context2, "context");
        Intrinsics.checkNotNullParameter(delayableExecutor, "bgExecutor");
        Intrinsics.checkNotNullParameter(delayableExecutor2, "uiExecutor");
        Intrinsics.checkNotNullParameter(activityStarter2, "activityStarter");
        Intrinsics.checkNotNullParameter(keyguardStateController2, "keyguardStateController");
        Intrinsics.checkNotNullParameter(globalActionsComponent2, "globalActionsComponent");
        Intrinsics.checkNotNullParameter(optional, "taskViewFactory");
        Intrinsics.checkNotNullParameter(broadcastDispatcher2, "broadcastDispatcher");
        Intrinsics.checkNotNullParameter(lazy, "lazyUiController");
        Intrinsics.checkNotNullParameter(controlsMetricsLogger2, "controlsMetricsLogger");
        this.context = context2;
        this.bgExecutor = delayableExecutor;
        this.uiExecutor = delayableExecutor2;
        this.activityStarter = activityStarter2;
        this.keyguardStateController = keyguardStateController2;
        this.globalActionsComponent = globalActionsComponent2;
        this.taskViewFactory = optional;
        this.broadcastDispatcher = broadcastDispatcher2;
        this.lazyUiController = lazy;
        this.controlsMetricsLogger = controlsMetricsLogger2;
        Object systemService = context2.getSystemService("vibrator");
        Objects.requireNonNull(systemService, "null cannot be cast to non-null type android.os.Vibrator");
        this.vibrator = (Vibrator) systemService;
    }

    private final boolean isLocked() {
        return !this.keyguardStateController.isUnlocked();
    }

    @NotNull
    public Context getActivityContext() {
        Context context2 = this.activityContext;
        if (context2 != null) {
            return context2;
        }
        Intrinsics.throwUninitializedPropertyAccessException("activityContext");
        throw null;
    }

    public void setActivityContext(@NotNull Context context2) {
        Intrinsics.checkNotNullParameter(context2, "<set-?>");
        this.activityContext = context2;
    }

    /* renamed from: com.android.systemui.controls.ui.ControlActionCoordinatorImpl$Companion */
    /* compiled from: ControlActionCoordinatorImpl.kt */
    public static final class Companion {
        public /* synthetic */ Companion(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }

        private Companion() {
        }
    }

    public void closeDialogs() {
        Dialog dialog2 = this.dialog;
        if (dialog2 != null) {
            dialog2.dismiss();
        }
        this.dialog = null;
    }

    public void toggle(@NotNull ControlViewHolder controlViewHolder, @NotNull String str, boolean z) {
        Intrinsics.checkNotNullParameter(controlViewHolder, "cvh");
        Intrinsics.checkNotNullParameter(str, "templateId");
        this.controlsMetricsLogger.touch(controlViewHolder, isLocked());
        bouncerOrRun(createAction(controlViewHolder.getCws().getCi().getControlId(), new ControlActionCoordinatorImpl$toggle$1(controlViewHolder, str, z), true));
    }

    public void touch(@NotNull ControlViewHolder controlViewHolder, @NotNull String str, @NotNull Control control) {
        Intrinsics.checkNotNullParameter(controlViewHolder, "cvh");
        Intrinsics.checkNotNullParameter(str, "templateId");
        Intrinsics.checkNotNullParameter(control, "control");
        this.controlsMetricsLogger.touch(controlViewHolder, isLocked());
        bouncerOrRun(createAction(controlViewHolder.getCws().getCi().getControlId(), new ControlActionCoordinatorImpl$touch$1(controlViewHolder, this, control, str), controlViewHolder.usePanel()));
    }

    public void drag(boolean z) {
        if (z) {
            vibrate(Vibrations.INSTANCE.getRangeEdgeEffect());
        } else {
            vibrate(Vibrations.INSTANCE.getRangeMiddleEffect());
        }
    }

    public void setValue(@NotNull ControlViewHolder controlViewHolder, @NotNull String str, float f) {
        Intrinsics.checkNotNullParameter(controlViewHolder, "cvh");
        Intrinsics.checkNotNullParameter(str, "templateId");
        this.controlsMetricsLogger.drag(controlViewHolder, isLocked());
        bouncerOrRun(createAction(controlViewHolder.getCws().getCi().getControlId(), new ControlActionCoordinatorImpl$setValue$1(controlViewHolder, str, f), false));
    }

    public void longPress(@NotNull ControlViewHolder controlViewHolder) {
        Intrinsics.checkNotNullParameter(controlViewHolder, "cvh");
        this.controlsMetricsLogger.longPress(controlViewHolder, isLocked());
        bouncerOrRun(createAction(controlViewHolder.getCws().getCi().getControlId(), new ControlActionCoordinatorImpl$longPress$1(controlViewHolder, this), false));
    }

    public void runPendingAction(@NotNull String str) {
        Intrinsics.checkNotNullParameter(str, "controlId");
        if (!isLocked()) {
            Action action = this.pendingAction;
            if (Intrinsics.areEqual((Object) action == null ? null : action.getControlId(), (Object) str)) {
                Action action2 = this.pendingAction;
                if (action2 != null) {
                    action2.invoke();
                }
                this.pendingAction = null;
            }
        }
    }

    public void enableActionOnTouch(@NotNull String str) {
        Intrinsics.checkNotNullParameter(str, "controlId");
        this.actionsInProgress.remove(str);
    }

    /* access modifiers changed from: private */
    public final boolean shouldRunAction(String str) {
        if (!this.actionsInProgress.add(str)) {
            return false;
        }
        this.uiExecutor.executeDelayed(new ControlActionCoordinatorImpl$shouldRunAction$1(this, str), 3000);
        return true;
    }

    @VisibleForTesting
    public final void bouncerOrRun(@NotNull Action action) {
        Intrinsics.checkNotNullParameter(action, "action");
        if (this.keyguardStateController.isShowing()) {
            if (isLocked()) {
                this.context.sendBroadcast(new Intent("android.intent.action.CLOSE_SYSTEM_DIALOGS"));
                this.pendingAction = action;
            }
            this.activityStarter.dismissKeyguardThenExecute(new ControlActionCoordinatorImpl$bouncerOrRun$1(action), new ControlActionCoordinatorImpl$bouncerOrRun$2(this), true);
            return;
        }
        action.invoke();
    }

    private final void vibrate(VibrationEffect vibrationEffect) {
        this.bgExecutor.execute(new ControlActionCoordinatorImpl$vibrate$1(this, vibrationEffect));
    }

    /* access modifiers changed from: private */
    public final void showDetail(ControlViewHolder controlViewHolder, PendingIntent pendingIntent) {
        this.bgExecutor.execute(new ControlActionCoordinatorImpl$showDetail$1(this, pendingIntent, controlViewHolder));
    }

    @NotNull
    @VisibleForTesting
    public final Action createAction(@NotNull String str, @NotNull Function0<Unit> function0, boolean z) {
        Intrinsics.checkNotNullParameter(str, "controlId");
        Intrinsics.checkNotNullParameter(function0, "f");
        return new Action(this, str, function0, z);
    }

    /* renamed from: com.android.systemui.controls.ui.ControlActionCoordinatorImpl$Action */
    /* compiled from: ControlActionCoordinatorImpl.kt */
    public final class Action {
        private final boolean blockable;
        @NotNull
        private final String controlId;
        @NotNull

        /* renamed from: f */
        private final Function0<Unit> f85f;
        final /* synthetic */ ControlActionCoordinatorImpl this$0;

        public Action(@NotNull ControlActionCoordinatorImpl controlActionCoordinatorImpl, @NotNull String str, Function0<Unit> function0, boolean z) {
            Intrinsics.checkNotNullParameter(controlActionCoordinatorImpl, "this$0");
            Intrinsics.checkNotNullParameter(str, "controlId");
            Intrinsics.checkNotNullParameter(function0, "f");
            this.this$0 = controlActionCoordinatorImpl;
            this.controlId = str;
            this.f85f = function0;
            this.blockable = z;
        }

        @NotNull
        public final String getControlId() {
            return this.controlId;
        }

        public final void invoke() {
            if (!this.blockable || this.this$0.shouldRunAction(this.controlId)) {
                this.f85f.invoke();
            }
        }
    }
}
