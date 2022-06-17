package com.android.p011wm.shell;

import android.content.Context;
import java.util.concurrent.Executor;
import java.util.function.Consumer;

/* renamed from: com.android.wm.shell.TaskViewFactory */
public interface TaskViewFactory {
    void create(Context context, Executor executor, Consumer<TaskView> consumer);
}
