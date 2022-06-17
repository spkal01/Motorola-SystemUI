package com.android.p011wm.shell.tasksurfacehelper;

import com.android.p011wm.shell.ShellTaskOrganizer;
import com.android.p011wm.shell.common.ShellExecutor;

/* renamed from: com.android.wm.shell.tasksurfacehelper.TaskSurfaceHelperController */
public class TaskSurfaceHelperController {
    private final TaskSurfaceHelperImpl mImpl = new TaskSurfaceHelperImpl();
    private final ShellExecutor mMainExecutor;
    private final ShellTaskOrganizer mTaskOrganizer;

    public TaskSurfaceHelperController(ShellTaskOrganizer shellTaskOrganizer, ShellExecutor shellExecutor) {
        this.mTaskOrganizer = shellTaskOrganizer;
        this.mMainExecutor = shellExecutor;
    }

    public TaskSurfaceHelper asTaskSurfaceHelper() {
        return this.mImpl;
    }

    /* renamed from: com.android.wm.shell.tasksurfacehelper.TaskSurfaceHelperController$TaskSurfaceHelperImpl */
    private class TaskSurfaceHelperImpl implements TaskSurfaceHelper {
        private TaskSurfaceHelperImpl() {
        }
    }
}
