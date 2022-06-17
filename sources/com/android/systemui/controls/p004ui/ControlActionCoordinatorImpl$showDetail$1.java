package com.android.systemui.controls.p004ui;

import android.app.PendingIntent;
import android.content.Context;
import android.content.pm.ResolveInfo;
import com.android.p011wm.shell.TaskView;
import com.android.p011wm.shell.TaskViewFactory;
import com.android.systemui.util.concurrency.DelayableExecutor;
import java.util.List;
import java.util.function.Consumer;
import kotlin.Unit;
import kotlin.jvm.internal.Intrinsics;

/* renamed from: com.android.systemui.controls.ui.ControlActionCoordinatorImpl$showDetail$1 */
/* compiled from: ControlActionCoordinatorImpl.kt */
final class ControlActionCoordinatorImpl$showDetail$1 implements Runnable {
    final /* synthetic */ ControlViewHolder $cvh;
    final /* synthetic */ PendingIntent $pendingIntent;
    final /* synthetic */ ControlActionCoordinatorImpl this$0;

    ControlActionCoordinatorImpl$showDetail$1(ControlActionCoordinatorImpl controlActionCoordinatorImpl, PendingIntent pendingIntent, ControlViewHolder controlViewHolder) {
        this.this$0 = controlActionCoordinatorImpl;
        this.$pendingIntent = pendingIntent;
        this.$cvh = controlViewHolder;
    }

    public final void run() {
        final List<ResolveInfo> queryIntentActivities = this.this$0.context.getPackageManager().queryIntentActivities(this.$pendingIntent.getIntent(), 65536);
        Intrinsics.checkNotNullExpressionValue(queryIntentActivities, "context.packageManager.queryIntentActivities(\n                pendingIntent.getIntent(),\n                PackageManager.MATCH_DEFAULT_ONLY\n            )");
        DelayableExecutor access$getUiExecutor$p = this.this$0.uiExecutor;
        final ControlActionCoordinatorImpl controlActionCoordinatorImpl = this.this$0;
        final ControlViewHolder controlViewHolder = this.$cvh;
        final PendingIntent pendingIntent = this.$pendingIntent;
        access$getUiExecutor$p.execute(new Runnable() {
            public final void run() {
                if (!(!queryIntentActivities.isEmpty()) || !controlActionCoordinatorImpl.taskViewFactory.isPresent()) {
                    controlViewHolder.setErrorStatus();
                    return;
                }
                Context access$getContext$p = controlActionCoordinatorImpl.context;
                DelayableExecutor access$getUiExecutor$p = controlActionCoordinatorImpl.uiExecutor;
                final ControlActionCoordinatorImpl controlActionCoordinatorImpl = controlActionCoordinatorImpl;
                final PendingIntent pendingIntent = pendingIntent;
                final ControlViewHolder controlViewHolder = controlViewHolder;
                ((TaskViewFactory) controlActionCoordinatorImpl.taskViewFactory.get()).create(access$getContext$p, access$getUiExecutor$p, new Consumer<TaskView>() {
                    public final void accept(TaskView taskView) {
                        ControlActionCoordinatorImpl controlActionCoordinatorImpl = controlActionCoordinatorImpl;
                        Context activityContext = controlActionCoordinatorImpl.getActivityContext();
                        Intrinsics.checkNotNullExpressionValue(taskView, "it");
                        DetailDialog detailDialog = new DetailDialog(activityContext, taskView, pendingIntent, controlViewHolder);
                        detailDialog.setOnDismissListener(new ControlActionCoordinatorImpl$showDetail$1$1$1$1$1(controlActionCoordinatorImpl));
                        detailDialog.show();
                        Unit unit = Unit.INSTANCE;
                        controlActionCoordinatorImpl.dialog = detailDialog;
                    }
                });
            }
        });
    }
}
