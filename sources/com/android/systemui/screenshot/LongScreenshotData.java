package com.android.systemui.screenshot;

import com.android.systemui.screenshot.ScreenshotController;
import com.android.systemui.screenshot.ScrollCaptureController;
import java.util.concurrent.atomic.AtomicReference;

public class LongScreenshotData {
    private final AtomicReference<ScrollCaptureController.LongScreenshot> mLongScreenshot = new AtomicReference<>();
    private final AtomicReference<ScreenshotController.TransitionDestination> mTransitionDestinationCallback = new AtomicReference<>();

    public void setLongScreenshot(ScrollCaptureController.LongScreenshot longScreenshot) {
        this.mLongScreenshot.set(longScreenshot);
    }

    public ScrollCaptureController.LongScreenshot takeLongScreenshot() {
        return this.mLongScreenshot.getAndSet((Object) null);
    }

    public void setTransitionDestinationCallback(ScreenshotController.TransitionDestination transitionDestination) {
        this.mTransitionDestinationCallback.set(transitionDestination);
    }

    public ScreenshotController.TransitionDestination takeTransitionDestinationCallback() {
        return this.mTransitionDestinationCallback.getAndSet((Object) null);
    }
}
