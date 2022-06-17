package com.android.systemui.shared.system;

import android.os.RemoteException;
import android.util.Log;
import android.view.IRecentsAnimationController;
import android.window.TaskSnapshot;
import com.android.systemui.shared.recents.model.ThumbnailData;

public class RecentsAnimationControllerCompat {
    private static final String TAG = "RecentsAnimationControllerCompat";
    private IRecentsAnimationController mAnimationController;

    public RecentsAnimationControllerCompat() {
    }

    public RecentsAnimationControllerCompat(IRecentsAnimationController iRecentsAnimationController) {
        this.mAnimationController = iRecentsAnimationController;
    }

    public ThumbnailData screenshotTask(int i) {
        try {
            TaskSnapshot screenshotTask = this.mAnimationController.screenshotTask(i);
            return screenshotTask != null ? new ThumbnailData(screenshotTask) : new ThumbnailData();
        } catch (RemoteException e) {
            Log.e(TAG, "Failed to screenshot task", e);
            return new ThumbnailData();
        }
    }

    public void setInputConsumerEnabled(boolean z) {
        try {
            this.mAnimationController.setInputConsumerEnabled(z);
        } catch (RemoteException e) {
            Log.e(TAG, "Failed to set input consumer enabled state", e);
        }
    }

    public void setAnimationTargetsBehindSystemBars(boolean z) {
        try {
            this.mAnimationController.setAnimationTargetsBehindSystemBars(z);
        } catch (RemoteException e) {
            Log.e(TAG, "Failed to set whether animation targets are behind system bars", e);
        }
    }

    public void hideCurrentInputMethod() {
        try {
            this.mAnimationController.hideCurrentInputMethod();
        } catch (RemoteException e) {
            Log.e(TAG, "Failed to set hide input method", e);
        }
    }

    public void finish(boolean z, boolean z2) {
        try {
            this.mAnimationController.finish(z, z2);
        } catch (RemoteException e) {
            Log.e(TAG, "Failed to finish recents animation", e);
        }
    }

    public void setDeferCancelUntilNextTransition(boolean z, boolean z2) {
        try {
            this.mAnimationController.setDeferCancelUntilNextTransition(z, z2);
        } catch (RemoteException e) {
            Log.e(TAG, "Failed to set deferred cancel with screenshot", e);
        }
    }

    public void cleanupScreenshot() {
        try {
            this.mAnimationController.cleanupScreenshot();
        } catch (RemoteException e) {
            Log.e(TAG, "Failed to clean up screenshot of recents animation", e);
        }
    }

    public boolean removeTask(int i) {
        try {
            return this.mAnimationController.removeTask(i);
        } catch (RemoteException e) {
            Log.e(TAG, "Failed to remove remote animation target", e);
            return false;
        }
    }
}
