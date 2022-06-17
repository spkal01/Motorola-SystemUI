package com.motorola.systemui.cli.navgesture.recents;

import android.content.Context;
import android.content.res.Resources;
import android.os.Handler;
import android.os.Looper;
import com.android.systemui.R$integer;
import com.android.systemui.shared.recents.model.Task;
import com.android.systemui.shared.recents.model.ThumbnailData;
import com.android.systemui.shared.system.ActivityManagerWrapper;
import com.motorola.systemui.cli.navgesture.executors.AppExecutors;
import com.motorola.systemui.cli.navgesture.util.HandlerRunnable;
import com.motorola.systemui.cli.navgesture.util.Utilities;
import java.util.ArrayList;
import java.util.function.Consumer;

public class TaskThumbnailCache {
    private final Handler mBackgroundHandler;
    /* access modifiers changed from: private */
    public final TaskKeyLruCache<ThumbnailData> mCache;
    private final int mCacheSize;
    private final HighResLoadingState mHighResLoadingState;

    public static class HighResLoadingState {
        private ArrayList<HighResLoadingStateChangedCallback> mCallbacks;
        private boolean mFlingingFast;
        private boolean mForceHighResThumbnails;
        private boolean mHighResLoadingEnabled;
        /* access modifiers changed from: private */
        public boolean mVisible;

        public interface HighResLoadingStateChangedCallback {
            void onHighResLoadingStateChanged(boolean z);
        }

        private HighResLoadingState(Context context) {
            this.mCallbacks = new ArrayList<>();
            this.mForceHighResThumbnails = !TaskThumbnailCache.supportsLowResThumbnails();
        }

        public void addCallback(HighResLoadingStateChangedCallback highResLoadingStateChangedCallback) {
            this.mCallbacks.add(highResLoadingStateChangedCallback);
        }

        public void removeCallback(HighResLoadingStateChangedCallback highResLoadingStateChangedCallback) {
            this.mCallbacks.remove(highResLoadingStateChangedCallback);
        }

        public void setVisible(boolean z) {
            this.mVisible = z;
            updateState();
        }

        public boolean isEnabled() {
            return this.mHighResLoadingEnabled;
        }

        private void updateState() {
            boolean z = this.mHighResLoadingEnabled;
            boolean z2 = this.mForceHighResThumbnails || (this.mVisible && !this.mFlingingFast);
            this.mHighResLoadingEnabled = z2;
            if (z != z2) {
                for (int size = this.mCallbacks.size() - 1; size >= 0; size--) {
                    this.mCallbacks.get(size).onHighResLoadingStateChanged(this.mHighResLoadingEnabled);
                }
            }
        }
    }

    public TaskThumbnailCache(Context context, Looper looper) {
        this.mBackgroundHandler = new Handler(looper);
        this.mHighResLoadingState = new HighResLoadingState(context);
        int integer = context.getResources().getInteger(R$integer.recentsThumbnailCacheSize);
        this.mCacheSize = integer;
        this.mCache = new TaskKeyLruCache<>(integer);
    }

    public void updateThumbnailInCache(Task task) {
        if (task.thumbnail == null) {
            updateThumbnailInBackground(task.key, true, new TaskThumbnailCache$$ExternalSyntheticLambda0(task));
        }
    }

    public void updateTaskSnapShot(int i, ThumbnailData thumbnailData) {
        this.mCache.updateIfAlreadyInCache(i, thumbnailData);
    }

    public ThumbnailLoadRequest updateThumbnailInBackground(Task task, Consumer<ThumbnailData> consumer) {
        boolean z = !this.mHighResLoadingState.isEnabled();
        ThumbnailData thumbnailData = task.thumbnail;
        if (thumbnailData == null || (thumbnailData.reducedResolution && !z)) {
            return updateThumbnailInBackground(task.key, !this.mHighResLoadingState.isEnabled(), new TaskThumbnailCache$$ExternalSyntheticLambda1(task, consumer));
        }
        consumer.accept(thumbnailData);
        return null;
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$updateThumbnailInBackground$1(Task task, Consumer consumer, ThumbnailData thumbnailData) {
        task.thumbnail = thumbnailData;
        consumer.accept(thumbnailData);
    }

    private ThumbnailLoadRequest updateThumbnailInBackground(Task.TaskKey taskKey, boolean z, Consumer<ThumbnailData> consumer) {
        ThumbnailData andInvalidateIfModified = this.mCache.getAndInvalidateIfModified(taskKey);
        if (andInvalidateIfModified == null || (andInvalidateIfModified.reducedResolution && !z)) {
            final Task.TaskKey taskKey2 = taskKey;
            final boolean z2 = z;
            final Consumer<ThumbnailData> consumer2 = consumer;
            C27201 r0 = new ThumbnailLoadRequest(this.mBackgroundHandler, z) {
                public void run() {
                    ThumbnailData taskThumbnail = ActivityManagerWrapper.getInstance().getTaskThumbnail(taskKey2.f124id, z2);
                    if (!isCanceled()) {
                        AppExecutors.m97ui().execute(new TaskThumbnailCache$1$$ExternalSyntheticLambda0(this, taskKey2, taskThumbnail, consumer2));
                    }
                }

                /* access modifiers changed from: private */
                public /* synthetic */ void lambda$run$0(Task.TaskKey taskKey, ThumbnailData thumbnailData, Consumer consumer) {
                    TaskThumbnailCache.this.mCache.put(taskKey, thumbnailData);
                    consumer.accept(thumbnailData);
                    onEnd();
                }
            };
            Utilities.postAsyncCallback(this.mBackgroundHandler, r0);
            return r0;
        }
        consumer.accept(andInvalidateIfModified);
        return null;
    }

    public void clear() {
        this.mCache.evictAll();
    }

    public void remove(Task.TaskKey taskKey) {
        this.mCache.remove(taskKey);
    }

    public int getCacheSize() {
        return this.mCacheSize;
    }

    public HighResLoadingState getHighResLoadingState() {
        return this.mHighResLoadingState;
    }

    public boolean isPreloadingEnabled() {
        return this.mHighResLoadingState.mVisible;
    }

    public static abstract class ThumbnailLoadRequest extends HandlerRunnable {
        public final boolean mLowResolution;

        ThumbnailLoadRequest(Handler handler, boolean z) {
            super(handler, (Runnable) null);
            this.mLowResolution = z;
        }
    }

    /* access modifiers changed from: private */
    public static boolean supportsLowResThumbnails() {
        Resources system = Resources.getSystem();
        int identifier = system.getIdentifier("config_lowResTaskSnapshotScale", "dimen", "android");
        if (identifier == 0 || 0.0f < system.getFloat(identifier)) {
            return true;
        }
        return false;
    }
}
