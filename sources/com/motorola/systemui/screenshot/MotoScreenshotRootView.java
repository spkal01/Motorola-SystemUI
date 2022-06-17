package com.motorola.systemui.screenshot;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.WindowInsets;
import android.widget.RelativeLayout;

public class MotoScreenshotRootView extends RelativeLayout {
    private boolean mIsLandscape;
    private OnInterceptTouchEventListener mOnInterceptTouchEventListener;

    public interface OnInterceptTouchEventListener {
        boolean onInterceptTouchEvent(MotionEvent motionEvent);
    }

    public MotoScreenshotRootView(Context context) {
        this(context, (AttributeSet) null);
    }

    public MotoScreenshotRootView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public MotoScreenshotRootView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mIsLandscape = false;
    }

    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        OnInterceptTouchEventListener onInterceptTouchEventListener = this.mOnInterceptTouchEventListener;
        if (onInterceptTouchEventListener != null) {
            return onInterceptTouchEventListener.onInterceptTouchEvent(motionEvent) || super.onInterceptTouchEvent(motionEvent);
        }
        return super.onInterceptTouchEvent(motionEvent);
    }

    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (this.mIsLandscape) {
            getWindowInsetsController().hide(WindowInsets.Type.navigationBars());
        }
    }

    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    public void setOnInterceptTouchEventListener(OnInterceptTouchEventListener onInterceptTouchEventListener) {
        this.mOnInterceptTouchEventListener = onInterceptTouchEventListener;
    }

    public void setIsLandscape(boolean z) {
        this.mIsLandscape = z;
        if (z) {
            setImmersivePolicy();
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:11:0x003a  */
    /* JADX WARNING: Removed duplicated region for block: B:21:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void setImmersivePolicy() {
        /*
            r7 = this;
            java.lang.String r0 = "MotoScreenshotRootView"
            r1 = 0
            r2 = -2
            android.content.Context r3 = r7.mContext     // Catch:{ all -> 0x0019 }
            android.content.ContentResolver r3 = r3.getContentResolver()     // Catch:{ all -> 0x0019 }
            java.lang.String r4 = "immersive_mode_confirmations"
            java.lang.String r3 = android.provider.Settings.Secure.getStringForUser(r3, r4, r2)     // Catch:{ all -> 0x0019 }
            java.lang.String r4 = "confirmed"
            boolean r3 = r4.equals(r3)     // Catch:{ all -> 0x0017 }
            goto L_0x0038
        L_0x0017:
            r4 = move-exception
            goto L_0x001b
        L_0x0019:
            r4 = move-exception
            r3 = r1
        L_0x001b:
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            java.lang.String r6 = "Error loading confirmations, value="
            r5.append(r6)
            r5.append(r3)
            java.lang.String r3 = "; throwable = "
            r5.append(r3)
            r5.append(r4)
            java.lang.String r3 = r5.toString()
            android.util.Log.w(r0, r3)
            r3 = 0
        L_0x0038:
            if (r3 != 0) goto L_0x00b5
            android.content.Context r3 = r7.mContext
            android.content.ContentResolver r3 = r3.getContentResolver()
            java.lang.String r4 = "policy_control"
            java.lang.String r3 = android.provider.Settings.Global.getStringForUser(r3, r4, r2)
            boolean r5 = android.text.TextUtils.isEmpty(r3)
            if (r5 == 0) goto L_0x0064
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r5 = "immersive.preconfirms="
            r1.append(r5)
            android.content.Context r5 = r7.mContext
            java.lang.String r5 = r5.getPackageName()
            r1.append(r5)
            java.lang.String r1 = r1.toString()
            goto L_0x008a
        L_0x0064:
            android.content.Context r5 = r7.mContext
            java.lang.String r5 = r5.getPackageName()
            boolean r5 = r3.contains(r5)
            if (r5 != 0) goto L_0x008a
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r1.append(r3)
            java.lang.String r5 = ":immersive.preconfirms="
            r1.append(r5)
            android.content.Context r5 = r7.mContext
            java.lang.String r5 = r5.getPackageName()
            r1.append(r5)
            java.lang.String r1 = r1.toString()
        L_0x008a:
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            java.lang.String r6 = "setImmersivePolicy Last policy value = "
            r5.append(r6)
            r5.append(r3)
            java.lang.String r3 = "; mPolicyValue = "
            r5.append(r3)
            r5.append(r1)
            java.lang.String r3 = r5.toString()
            android.util.Log.i(r0, r3)
            boolean r0 = android.text.TextUtils.isEmpty(r1)
            if (r0 != 0) goto L_0x00b5
            android.content.Context r7 = r7.mContext
            android.content.ContentResolver r7 = r7.getContentResolver()
            android.provider.Settings.Global.putStringForUser(r7, r4, r1, r2)
        L_0x00b5:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.motorola.systemui.screenshot.MotoScreenshotRootView.setImmersivePolicy():void");
    }
}
