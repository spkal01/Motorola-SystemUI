package com.motorola.systemui.cli.navgesture;

import android.content.Context;
import android.graphics.Rect;
import android.view.MotionEvent;
import com.android.systemui.shared.recents.model.ThumbnailData;
import com.motorola.systemui.cli.navgesture.BaseGestureActivity;
import com.motorola.systemui.cli.navgesture.animation.AnimatorPlaybackController;
import com.motorola.systemui.cli.navgesture.animation.RecentsAnimationDeviceState;
import com.motorola.systemui.cli.navgesture.animation.remote.RemoteAnimationTargetSet;
import com.motorola.systemui.cli.navgesture.util.DeviceProfile;
import com.motorola.systemui.cli.navgesture.util.LayoutCalculator;
import java.util.function.Consumer;
import java.util.function.Predicate;

public interface ActivityControlHelper<T extends BaseGestureActivity> {

    public interface AnimationFactory {
        void adjustActivityControllerInterpolators() {
        }

        void createActivityController(long j);

        void onRemoteAnimationReceived(RemoteAnimationTargetSet remoteAnimationTargetSet) {
        }

        void onTransitionCancelled() {
        }

        void setRecentsAttachedToAppWindow(boolean z, boolean z2) {
        }
    }

    ActivityInitListener<T> createActivityInitListener(Predicate<Boolean> predicate);

    T getCreatedActivity();

    IRecentsView getVisibleRecentsView() {
        return null;
    }

    void onLaunchTaskFailed() {
    }

    void onLaunchTaskSuccess() {
    }

    void onSwipeUpToRecentsComplete() {
    }

    void onTransitionCancelled(boolean z) {
    }

    AnimationFactory prepareRecentsUI(boolean z, boolean z2, Consumer<AnimatorPlaybackController> consumer);

    boolean switchToRecentsIfVisible(Runnable runnable) {
        return false;
    }

    int getSwipeUpDestinationAndLength(DeviceProfile deviceProfile, Context context, Rect rect) {
        return LayoutCalculator.INSTANCE.lambda$get$0(context).getSwipeUpDestinationAndLength(deviceProfile, context, rect);
    }

    boolean isResumed() {
        BaseGestureActivity createdActivity = getCreatedActivity();
        return createdActivity != null && createdActivity.hasBeenResumed();
    }

    boolean deferStartingActivity(RecentsAnimationDeviceState recentsAnimationDeviceState, MotionEvent motionEvent) {
        return recentsAnimationDeviceState.isInDeferredGestureRegion(motionEvent);
    }

    void switchRunningTaskViewToScreenshot(ThumbnailData thumbnailData, Runnable runnable) {
        runnable.run();
    }
}
