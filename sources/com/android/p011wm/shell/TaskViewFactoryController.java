package com.android.p011wm.shell;

import android.content.Context;
import com.android.p011wm.shell.common.ShellExecutor;
import java.util.concurrent.Executor;
import java.util.function.Consumer;

/* renamed from: com.android.wm.shell.TaskViewFactoryController */
public class TaskViewFactoryController {
    private final TaskViewFactory mImpl = new TaskViewFactoryImpl();
    /* access modifiers changed from: private */
    public final ShellExecutor mShellExecutor;
    private final ShellTaskOrganizer mTaskOrganizer;

    public TaskViewFactoryController(ShellTaskOrganizer shellTaskOrganizer, ShellExecutor shellExecutor) {
        this.mTaskOrganizer = shellTaskOrganizer;
        this.mShellExecutor = shellExecutor;
    }

    public TaskViewFactory asTaskViewFactory() {
        return this.mImpl;
    }

    public void create(Context context, Executor executor, Consumer<TaskView> consumer) {
        executor.execute(new TaskViewFactoryController$$ExternalSyntheticLambda0(consumer, new TaskView(context, this.mTaskOrganizer)));
    }

    /* renamed from: com.android.wm.shell.TaskViewFactoryController$TaskViewFactoryImpl */
    private class TaskViewFactoryImpl implements TaskViewFactory {
        private TaskViewFactoryImpl() {
        }

        public void create(Context context, Executor executor, Consumer<TaskView> consumer) {
            TaskViewFactoryController.this.mShellExecutor.execute(new C2229xccf6ffa7(this, context, executor, consumer));
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$create$0(Context context, Executor executor, Consumer consumer) {
            TaskViewFactoryController.this.create(context, executor, consumer);
        }
    }
}
