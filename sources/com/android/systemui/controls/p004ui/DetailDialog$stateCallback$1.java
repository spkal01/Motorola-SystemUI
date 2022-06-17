package com.android.systemui.controls.p004ui;

import android.app.ActivityOptions;
import android.content.ComponentName;
import android.content.Context;
import android.view.ViewGroup;
import com.android.p011wm.shell.TaskView;
import com.android.systemui.R$id;
import org.jetbrains.annotations.Nullable;

/* renamed from: com.android.systemui.controls.ui.DetailDialog$stateCallback$1 */
/* compiled from: DetailDialog.kt */
public final class DetailDialog$stateCallback$1 implements TaskView.Listener {
    final /* synthetic */ DetailDialog this$0;

    DetailDialog$stateCallback$1(DetailDialog detailDialog) {
        this.this$0 = detailDialog;
    }

    public void onInitialized() {
        ActivityOptions activityOptions;
        Context activityContext = this.this$0.getActivityContext();
        if (activityContext == null) {
            activityOptions = null;
        } else {
            activityOptions = ActivityOptions.makeCustomAnimation(activityContext, 0, 0);
        }
        if (activityOptions == null) {
            activityOptions = ActivityOptions.makeBasic();
        }
        this.this$0.getTaskView().startActivity(this.this$0.getPendingIntent(), this.this$0.fillInIntent, activityOptions, this.this$0.getTaskViewBounds());
    }

    public void onTaskRemovalStarted(int i) {
        this.this$0.setDetailTaskId(-1);
        this.this$0.dismiss();
    }

    public void onTaskCreated(int i, @Nullable ComponentName componentName) {
        this.this$0.setDetailTaskId(i);
        ((ViewGroup) this.this$0.requireViewById(R$id.controls_activity_view)).setAlpha(1.0f);
    }

    public void onReleased() {
        this.this$0.removeDetailTask();
    }

    public void onBackPressedOnTaskRoot(int i) {
        this.this$0.dismiss();
    }
}
