package com.motorola.systemui.cli.navgesture.animation.remote;

import android.graphics.Rect;
import android.os.Handler;
import android.util.ArraySet;
import com.android.systemui.shared.recents.model.ThumbnailData;
import com.android.systemui.shared.system.RecentsAnimationControllerCompat;
import com.android.systemui.shared.system.RemoteAnimationTargetCompat;
import com.motorola.systemui.cli.navgesture.executors.AppExecutors;
import com.motorola.systemui.cli.navgesture.util.Utilities;
import java.util.Objects;
import java.util.Set;

public class RecentsAnimationCallbacks implements com.android.systemui.shared.system.RecentsAnimationListener {
    private final boolean mAllowMinimizeSplitScreen;
    private boolean mCancelled;
    private RecentsAnimationTargetSetController mController;
    private final Set<RecentsAnimationListener> mListeners = new ArraySet();

    public interface RecentsAnimationListener {
        void onRecentsAnimationCanceled(ThumbnailData thumbnailData) {
        }

        void onRecentsAnimationFinished(RecentsAnimationTargetSetController recentsAnimationTargetSetController) {
        }

        void onRecentsAnimationStart(RecentsAnimationTargetSetController recentsAnimationTargetSetController, RecentsAnimationTargetSet recentsAnimationTargetSet) {
        }

        void onTaskAppeared(RemoteAnimationTargetCompat remoteAnimationTargetCompat) {
        }
    }

    public RecentsAnimationCallbacks(boolean z) {
        this.mAllowMinimizeSplitScreen = z;
    }

    public void addListener(RecentsAnimationListener recentsAnimationListener) {
        this.mListeners.add(recentsAnimationListener);
    }

    public void removeListener(RecentsAnimationListener recentsAnimationListener) {
        this.mListeners.remove(recentsAnimationListener);
    }

    public void removeAllListeners() {
        this.mListeners.clear();
    }

    public void notifyAnimationCanceled() {
        this.mCancelled = true;
        onAnimationCanceled((ThumbnailData) null);
    }

    public final void onAnimationStart(RecentsAnimationControllerCompat recentsAnimationControllerCompat, RemoteAnimationTargetCompat[] remoteAnimationTargetCompatArr, RemoteAnimationTargetCompat[] remoteAnimationTargetCompatArr2, Rect rect, Rect rect2) {
        RecentsAnimationTargetSet recentsAnimationTargetSet = new RecentsAnimationTargetSet(remoteAnimationTargetCompatArr, remoteAnimationTargetCompatArr2, rect, rect2);
        this.mController = new RecentsAnimationTargetSetController(recentsAnimationControllerCompat, this.mAllowMinimizeSplitScreen, new RecentsAnimationCallbacks$$ExternalSyntheticLambda5(this));
        if (this.mCancelled) {
            Handler handler = AppExecutors.m97ui().getHandler();
            RecentsAnimationTargetSetController recentsAnimationTargetSetController = this.mController;
            Objects.requireNonNull(recentsAnimationTargetSetController);
            Utilities.postAsyncCallback(handler, new RecentsAnimationCallbacks$$ExternalSyntheticLambda4(recentsAnimationTargetSetController));
            return;
        }
        Utilities.postAsyncCallback(AppExecutors.m97ui().getHandler(), new RecentsAnimationCallbacks$$ExternalSyntheticLambda2(this, recentsAnimationTargetSet));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onAnimationStart$0(RecentsAnimationTargetSet recentsAnimationTargetSet) {
        for (RecentsAnimationListener onRecentsAnimationStart : getListeners()) {
            onRecentsAnimationStart.onRecentsAnimationStart(this.mController, recentsAnimationTargetSet);
        }
    }

    public final void onAnimationCanceled(ThumbnailData thumbnailData) {
        Utilities.postAsyncCallback(AppExecutors.m97ui().getHandler(), new RecentsAnimationCallbacks$$ExternalSyntheticLambda0(this, thumbnailData));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onAnimationCanceled$1(ThumbnailData thumbnailData) {
        for (RecentsAnimationListener onRecentsAnimationCanceled : getListeners()) {
            onRecentsAnimationCanceled.onRecentsAnimationCanceled(thumbnailData);
        }
    }

    public void onTaskAppeared(RemoteAnimationTargetCompat remoteAnimationTargetCompat) {
        Utilities.postAsyncCallback(AppExecutors.m97ui().getHandler(), new RecentsAnimationCallbacks$$ExternalSyntheticLambda1(this, remoteAnimationTargetCompat));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onTaskAppeared$2(RemoteAnimationTargetCompat remoteAnimationTargetCompat) {
        for (RecentsAnimationListener onTaskAppeared : getListeners()) {
            onTaskAppeared.onTaskAppeared(remoteAnimationTargetCompat);
        }
    }

    /* access modifiers changed from: private */
    public void onAnimationFinished(RecentsAnimationTargetSetController recentsAnimationTargetSetController) {
        Utilities.postAsyncCallback(AppExecutors.m97ui().getHandler(), new RecentsAnimationCallbacks$$ExternalSyntheticLambda3(this, recentsAnimationTargetSetController));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onAnimationFinished$3(RecentsAnimationTargetSetController recentsAnimationTargetSetController) {
        for (RecentsAnimationListener onRecentsAnimationFinished : getListeners()) {
            onRecentsAnimationFinished.onRecentsAnimationFinished(recentsAnimationTargetSetController);
        }
    }

    private RecentsAnimationListener[] getListeners() {
        return (RecentsAnimationListener[]) this.mListeners.toArray(new RecentsAnimationListener[0]);
    }
}
