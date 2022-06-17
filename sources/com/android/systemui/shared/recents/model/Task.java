package com.android.systemui.shared.recents.model;

import android.app.ActivityManager;
import android.app.TaskInfo;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.ViewDebug;
import java.util.Objects;

public class Task {
    @ViewDebug.ExportedProperty(category = "recents")
    public int colorBackground;
    @ViewDebug.ExportedProperty(category = "recents")
    public int colorPrimary;
    @ViewDebug.ExportedProperty(category = "recents")
    public boolean isDockable;
    @ViewDebug.ExportedProperty(category = "recents")
    public boolean isLocked;
    @ViewDebug.ExportedProperty(deepExport = true, prefix = "key_")
    public TaskKey key;
    public ActivityManager.RecentTaskInfo.PersistedTaskSnapshotData lastSnapshotData = new ActivityManager.RecentTaskInfo.PersistedTaskSnapshotData();
    public ActivityManager.TaskDescription taskDescription;
    public ThumbnailData thumbnail;
    @ViewDebug.ExportedProperty(category = "recents")
    @Deprecated
    public String title;
    @ViewDebug.ExportedProperty(category = "recents")
    public ComponentName topActivity;

    public static class TaskKey implements Parcelable {
        public static final Parcelable.Creator<TaskKey> CREATOR = new Parcelable.Creator<TaskKey>() {
            public TaskKey createFromParcel(Parcel parcel) {
                return TaskKey.readFromParcel(parcel);
            }

            public TaskKey[] newArray(int i) {
                return new TaskKey[i];
            }
        };
        @ViewDebug.ExportedProperty(category = "recents")
        public final Intent baseIntent;
        @ViewDebug.ExportedProperty(category = "recents")
        public final int displayId;
        @ViewDebug.ExportedProperty(category = "recents")

        /* renamed from: id */
        public final int f124id;
        @ViewDebug.ExportedProperty(category = "recents")
        public long lastActiveTime;
        private int mHashCode;
        public final ComponentName sourceComponent;
        @ViewDebug.ExportedProperty(category = "recents")
        public final int userId;
        @ViewDebug.ExportedProperty(category = "recents")
        public int windowingMode;

        public int describeContents() {
            return 0;
        }

        public TaskKey(TaskInfo taskInfo) {
            ComponentName componentName = taskInfo.origActivity;
            componentName = componentName == null ? taskInfo.realActivity : componentName;
            this.f124id = taskInfo.taskId;
            this.windowingMode = taskInfo.configuration.windowConfiguration.getWindowingMode();
            this.baseIntent = taskInfo.baseIntent;
            this.sourceComponent = componentName;
            this.userId = taskInfo.userId;
            this.lastActiveTime = taskInfo.lastActiveTime;
            this.displayId = taskInfo.displayId;
            updateHashCode();
        }

        public TaskKey(int i, int i2, Intent intent, ComponentName componentName, int i3, long j) {
            this.f124id = i;
            this.windowingMode = i2;
            this.baseIntent = intent;
            this.sourceComponent = componentName;
            this.userId = i3;
            this.lastActiveTime = j;
            this.displayId = 0;
            updateHashCode();
        }

        public TaskKey(int i, int i2, Intent intent, ComponentName componentName, int i3, long j, int i4) {
            this.f124id = i;
            this.windowingMode = i2;
            this.baseIntent = intent;
            this.sourceComponent = componentName;
            this.userId = i3;
            this.lastActiveTime = j;
            this.displayId = i4;
            updateHashCode();
        }

        public ComponentName getComponent() {
            return this.baseIntent.getComponent();
        }

        public boolean equals(Object obj) {
            if (!(obj instanceof TaskKey)) {
                return false;
            }
            TaskKey taskKey = (TaskKey) obj;
            if (this.f124id == taskKey.f124id && this.windowingMode == taskKey.windowingMode && this.userId == taskKey.userId) {
                return true;
            }
            return false;
        }

        public int hashCode() {
            return this.mHashCode;
        }

        public String toString() {
            return "id=" + this.f124id + " windowingMode=" + this.windowingMode + " user=" + this.userId + " lastActiveTime=" + this.lastActiveTime;
        }

        private void updateHashCode() {
            this.mHashCode = Objects.hash(new Object[]{Integer.valueOf(this.f124id), Integer.valueOf(this.windowingMode), Integer.valueOf(this.userId)});
        }

        public final void writeToParcel(Parcel parcel, int i) {
            parcel.writeInt(this.f124id);
            parcel.writeInt(this.windowingMode);
            parcel.writeTypedObject(this.baseIntent, i);
            parcel.writeInt(this.userId);
            parcel.writeLong(this.lastActiveTime);
            parcel.writeInt(this.displayId);
            parcel.writeTypedObject(this.sourceComponent, i);
        }

        /* access modifiers changed from: private */
        public static TaskKey readFromParcel(Parcel parcel) {
            return new TaskKey(parcel.readInt(), parcel.readInt(), (Intent) parcel.readTypedObject(Intent.CREATOR), (ComponentName) parcel.readTypedObject(ComponentName.CREATOR), parcel.readInt(), parcel.readLong(), parcel.readInt());
        }
    }

    public Task() {
    }

    public static Task from(TaskKey taskKey, TaskInfo taskInfo, boolean z) {
        ActivityManager.TaskDescription taskDescription2 = taskInfo.taskDescription;
        int i = 0;
        int primaryColor = taskDescription2 != null ? taskDescription2.getPrimaryColor() : 0;
        if (taskDescription2 != null) {
            i = taskDescription2.getBackgroundColor();
        }
        return new Task(taskKey, primaryColor, i, taskInfo.supportsSplitScreenMultiWindow, z, taskDescription2, taskInfo.topActivity);
    }

    public Task(TaskKey taskKey) {
        this.key = taskKey;
        this.taskDescription = new ActivityManager.TaskDescription();
    }

    @Deprecated
    public Task(TaskKey taskKey, int i, int i2, boolean z, boolean z2, ActivityManager.TaskDescription taskDescription2, ComponentName componentName) {
        this.key = taskKey;
        this.colorPrimary = i;
        this.colorBackground = i2;
        this.taskDescription = taskDescription2;
        this.isDockable = z;
        this.isLocked = z2;
        this.topActivity = componentName;
    }

    public boolean equals(Object obj) {
        return this.key.equals(((Task) obj).key);
    }

    public String toString() {
        return "[" + this.key.toString() + "] " + this.title;
    }
}
