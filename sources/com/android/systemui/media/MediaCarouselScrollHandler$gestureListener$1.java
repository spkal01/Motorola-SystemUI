package com.android.systemui.media;

import android.view.GestureDetector;
import android.view.MotionEvent;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.Nullable;

/* compiled from: MediaCarouselScrollHandler.kt */
public final class MediaCarouselScrollHandler$gestureListener$1 extends GestureDetector.SimpleOnGestureListener {
    final /* synthetic */ MediaCarouselScrollHandler this$0;

    MediaCarouselScrollHandler$gestureListener$1(MediaCarouselScrollHandler mediaCarouselScrollHandler) {
        this.this$0 = mediaCarouselScrollHandler;
    }

    public boolean onFling(@Nullable MotionEvent motionEvent, @Nullable MotionEvent motionEvent2, float f, float f2) {
        return this.this$0.onFling(f, f2);
    }

    public boolean onScroll(@Nullable MotionEvent motionEvent, @Nullable MotionEvent motionEvent2, float f, float f2) {
        MediaCarouselScrollHandler mediaCarouselScrollHandler = this.this$0;
        Intrinsics.checkNotNull(motionEvent);
        Intrinsics.checkNotNull(motionEvent2);
        return mediaCarouselScrollHandler.onScroll(motionEvent, motionEvent2, f);
    }

    public boolean onDown(@Nullable MotionEvent motionEvent) {
        if (!this.this$0.getFalsingProtectionNeeded()) {
            return false;
        }
        this.this$0.falsingCollector.onNotificationStartDismissing();
        return false;
    }
}
