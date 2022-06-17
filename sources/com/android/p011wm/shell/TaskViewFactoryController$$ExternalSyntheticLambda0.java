package com.android.p011wm.shell;

import java.util.function.Consumer;

/* renamed from: com.android.wm.shell.TaskViewFactoryController$$ExternalSyntheticLambda0 */
public final /* synthetic */ class TaskViewFactoryController$$ExternalSyntheticLambda0 implements Runnable {
    public final /* synthetic */ Consumer f$0;
    public final /* synthetic */ TaskView f$1;

    public /* synthetic */ TaskViewFactoryController$$ExternalSyntheticLambda0(Consumer consumer, TaskView taskView) {
        this.f$0 = consumer;
        this.f$1 = taskView;
    }

    public final void run() {
        this.f$0.accept(this.f$1);
    }
}
