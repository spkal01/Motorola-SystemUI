package com.android.p011wm.shell;

import android.content.ComponentName;

/* renamed from: com.android.wm.shell.TaskView$$ExternalSyntheticLambda8 */
public final /* synthetic */ class TaskView$$ExternalSyntheticLambda8 implements Runnable {
    public final /* synthetic */ TaskView f$0;
    public final /* synthetic */ int f$1;
    public final /* synthetic */ ComponentName f$2;

    public /* synthetic */ TaskView$$ExternalSyntheticLambda8(TaskView taskView, int i, ComponentName componentName) {
        this.f$0 = taskView;
        this.f$1 = i;
        this.f$2 = componentName;
    }

    public final void run() {
        this.f$0.lambda$onTaskAppeared$4(this.f$1, this.f$2);
    }
}
