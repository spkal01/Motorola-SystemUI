package com.motorola.systemui.cli.navgesture.util;

import android.content.Context;
import android.content.res.Resources;
import android.view.MotionEvent;
import com.android.systemui.R$dimen;

public class MotionPauseDetector {
    private boolean mDisallowPause;
    private final TimeoutRecorder mForcePauseTimeout;
    private boolean mHasEverBeenPaused;
    private boolean mIsPaused;
    private final boolean mMakePauseHarderToTrigger;
    private OnMotionPauseListener mOnMotionPauseListener;
    private Float mPreviousVelocity = null;
    private long mSlowStartTime;
    private final float mSpeedFast;
    private final float mSpeedSlow;
    private final float mSpeedSomewhatFast;
    private final float mSpeedVerySlow;
    private final VelocityProvider mVelocityProvider;

    public interface OnMotionPauseListener {
        void onMotionPauseChanged(boolean z);
    }

    protected interface VelocityProvider {
        Float addMotionEvent(MotionEvent motionEvent, int i);

        void clear();
    }

    public MotionPauseDetector(Context context, boolean z, int i) {
        Resources resources = context.getResources();
        this.mSpeedVerySlow = resources.getDimension(R$dimen.motion_pause_detector_speed_very_slow);
        this.mSpeedSlow = resources.getDimension(R$dimen.motion_pause_detector_speed_slow);
        this.mSpeedSomewhatFast = resources.getDimension(R$dimen.motion_pause_detector_speed_somewhat_fast);
        this.mSpeedFast = resources.getDimension(R$dimen.motion_pause_detector_speed_fast);
        TimeoutRecorder timeoutRecorder = new TimeoutRecorder();
        this.mForcePauseTimeout = timeoutRecorder;
        timeoutRecorder.setOnTimeoutListener(new MotionPauseDetector$$ExternalSyntheticLambda0(this));
        this.mMakePauseHarderToTrigger = z;
        this.mVelocityProvider = new LinearVelocityProvider(i);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0(TimeoutRecorder timeoutRecorder) {
        updatePaused(true);
    }

    public void setOnMotionPauseListener(OnMotionPauseListener onMotionPauseListener) {
        this.mOnMotionPauseListener = onMotionPauseListener;
    }

    public void setDisallowPause(boolean z) {
        this.mDisallowPause = z;
        updatePaused(this.mIsPaused);
    }

    public void addPosition(MotionEvent motionEvent) {
        addPosition(motionEvent, 0);
    }

    public void addPosition(MotionEvent motionEvent, int i) {
        this.mForcePauseTimeout.setTimeout(this.mMakePauseHarderToTrigger ? 400 : 300);
        Float addMotionEvent = this.mVelocityProvider.addMotionEvent(motionEvent, i);
        if (!(addMotionEvent == null || this.mPreviousVelocity == null)) {
            checkMotionPaused(addMotionEvent.floatValue(), this.mPreviousVelocity.floatValue(), motionEvent.getEventTime());
        }
        this.mPreviousVelocity = addMotionEvent;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:43:0x0073, code lost:
        if ((r8 - r5.mSlowStartTime) >= 400) goto L_0x001a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:5:0x0016, code lost:
        if (r1 >= r6) goto L_0x0019;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void checkMotionPaused(float r6, float r7, long r8) {
        /*
            r5 = this;
            float r0 = java.lang.Math.abs(r6)
            float r1 = java.lang.Math.abs(r7)
            boolean r2 = r5.mIsPaused
            r3 = 1
            r4 = 0
            if (r2 == 0) goto L_0x001d
            float r6 = r5.mSpeedFast
            int r7 = (r0 > r6 ? 1 : (r0 == r6 ? 0 : -1))
            if (r7 < 0) goto L_0x001a
            int r6 = (r1 > r6 ? 1 : (r1 == r6 ? 0 : -1))
            if (r6 >= 0) goto L_0x0019
            goto L_0x001a
        L_0x0019:
            r3 = r4
        L_0x001a:
            r4 = r3
            goto L_0x007a
        L_0x001d:
            r2 = 0
            int r6 = (r6 > r2 ? 1 : (r6 == r2 ? 0 : -1))
            if (r6 >= 0) goto L_0x0024
            r6 = r3
            goto L_0x0025
        L_0x0024:
            r6 = r4
        L_0x0025:
            int r7 = (r7 > r2 ? 1 : (r7 == r2 ? 0 : -1))
            if (r7 >= 0) goto L_0x002b
            r7 = r3
            goto L_0x002c
        L_0x002b:
            r7 = r4
        L_0x002c:
            if (r6 == r7) goto L_0x002f
            goto L_0x007a
        L_0x002f:
            float r6 = r5.mSpeedVerySlow
            int r7 = (r0 > r6 ? 1 : (r0 == r6 ? 0 : -1))
            if (r7 >= 0) goto L_0x003b
            int r6 = (r1 > r6 ? 1 : (r1 == r6 ? 0 : -1))
            if (r6 >= 0) goto L_0x003b
            r6 = r3
            goto L_0x003c
        L_0x003b:
            r6 = r4
        L_0x003c:
            if (r6 != 0) goto L_0x0058
            boolean r7 = r5.mHasEverBeenPaused
            if (r7 != 0) goto L_0x0058
            r6 = 1058642330(0x3f19999a, float:0.6)
            float r1 = r1 * r6
            int r6 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r6 >= 0) goto L_0x004c
            r6 = r3
            goto L_0x004d
        L_0x004c:
            r6 = r4
        L_0x004d:
            if (r6 == 0) goto L_0x0057
            float r6 = r5.mSpeedSomewhatFast
            int r6 = (r0 > r6 ? 1 : (r0 == r6 ? 0 : -1))
            if (r6 >= 0) goto L_0x0057
            r6 = r3
            goto L_0x0058
        L_0x0057:
            r6 = r4
        L_0x0058:
            boolean r7 = r5.mMakePauseHarderToTrigger
            if (r7 == 0) goto L_0x0079
            float r6 = r5.mSpeedSlow
            int r6 = (r0 > r6 ? 1 : (r0 == r6 ? 0 : -1))
            r0 = 0
            if (r6 >= 0) goto L_0x0076
            long r6 = r5.mSlowStartTime
            int r6 = (r6 > r0 ? 1 : (r6 == r0 ? 0 : -1))
            if (r6 != 0) goto L_0x006c
            r5.mSlowStartTime = r8
        L_0x006c:
            long r6 = r5.mSlowStartTime
            long r8 = r8 - r6
            r6 = 400(0x190, double:1.976E-321)
            int r6 = (r8 > r6 ? 1 : (r8 == r6 ? 0 : -1))
            if (r6 < 0) goto L_0x0019
            goto L_0x001a
        L_0x0076:
            r5.mSlowStartTime = r0
            goto L_0x007a
        L_0x0079:
            r4 = r6
        L_0x007a:
            r5.updatePaused(r4)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.motorola.systemui.cli.navgesture.util.MotionPauseDetector.checkMotionPaused(float, float, long):void");
    }

    private void updatePaused(boolean z) {
        if (this.mDisallowPause) {
            z = false;
        }
        if (this.mIsPaused != z) {
            this.mIsPaused = z;
            if (z) {
                this.mHasEverBeenPaused = true;
            }
            OnMotionPauseListener onMotionPauseListener = this.mOnMotionPauseListener;
            if (onMotionPauseListener != null) {
                onMotionPauseListener.onMotionPauseChanged(z);
            }
        }
    }

    public void clear() {
        this.mVelocityProvider.clear();
        this.mPreviousVelocity = null;
        setOnMotionPauseListener((OnMotionPauseListener) null);
        this.mHasEverBeenPaused = false;
        this.mIsPaused = false;
        this.mSlowStartTime = 0;
        this.mForcePauseTimeout.cancelTimeout();
    }

    private static class LinearVelocityProvider implements VelocityProvider {
        private final int mAxis;
        private Float mPreviousPosition = null;
        private Long mPreviousTime = null;

        LinearVelocityProvider(int i) {
            this.mAxis = i;
        }

        public Float addMotionEvent(MotionEvent motionEvent, int i) {
            Float f;
            long eventTime = motionEvent.getEventTime();
            float axisValue = motionEvent.getAxisValue(this.mAxis, i);
            Long l = this.mPreviousTime;
            if (l == null || this.mPreviousPosition == null) {
                f = null;
            } else {
                f = Float.valueOf((axisValue - this.mPreviousPosition.floatValue()) / ((float) Math.max(1, eventTime - l.longValue())));
            }
            this.mPreviousTime = Long.valueOf(eventTime);
            this.mPreviousPosition = Float.valueOf(axisValue);
            return f;
        }

        public void clear() {
            this.mPreviousTime = null;
            this.mPreviousPosition = null;
        }
    }
}
