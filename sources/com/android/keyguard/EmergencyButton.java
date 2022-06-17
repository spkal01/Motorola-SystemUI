package com.android.keyguard;

import android.app.ActivityTaskManager;
import android.content.Context;
import android.os.RemoteException;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.Button;
import com.android.internal.util.EmergencyAffordanceManager;
import com.android.internal.widget.LockPatternUtils;
import com.android.settingslib.Utils;
import com.android.systemui.R$drawable;

public class EmergencyButton extends Button {
    private int mDownX;
    private int mDownY;
    private final EmergencyAffordanceManager mEmergencyAffordanceManager;
    private final boolean mEnableEmergencyCallWhileSimLocked;
    private boolean mIsInCall;
    private LockPatternUtils mLockPatternUtils;
    private boolean mLongPressWasDragged;

    public EmergencyButton(Context context) {
        this(context, (AttributeSet) null);
    }

    public EmergencyButton(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mEnableEmergencyCallWhileSimLocked = this.mContext.getResources().getBoolean(17891594);
        this.mEmergencyAffordanceManager = new EmergencyAffordanceManager(context);
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        super.onFinishInflate();
        this.mLockPatternUtils = new LockPatternUtils(this.mContext);
        if (this.mEmergencyAffordanceManager.needsEmergencyAffordance()) {
            setOnLongClickListener(new EmergencyButton$$ExternalSyntheticLambda0(this));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ boolean lambda$onFinishInflate$0(View view) {
        if (this.mLongPressWasDragged || this.mIsInCall || !this.mEmergencyAffordanceManager.needsEmergencyAffordance()) {
            return false;
        }
        try {
            ActivityTaskManager.getService().stopSystemLockTaskMode();
        } catch (RemoteException unused) {
            Log.w("EmergencyButton", "Failed to stop app pinning");
        } catch (Throwable unused2) {
        }
        this.mEmergencyAffordanceManager.performEmergencyCall();
        return true;
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        int x = (int) motionEvent.getX();
        int y = (int) motionEvent.getY();
        if (motionEvent.getActionMasked() == 0) {
            this.mDownX = x;
            this.mDownY = y;
            this.mLongPressWasDragged = false;
        } else {
            int abs = Math.abs(x - this.mDownX);
            int abs2 = Math.abs(y - this.mDownY);
            int scaledTouchSlop = ViewConfiguration.get(this.mContext).getScaledTouchSlop();
            if (Math.abs(abs2) > scaledTouchSlop || Math.abs(abs) > scaledTouchSlop) {
                this.mLongPressWasDragged = true;
            }
        }
        return super.onTouchEvent(motionEvent);
    }

    public void reloadColors() {
        setTextColor(Utils.getColorAttrDefaultColor(getContext(), 16842809));
        setBackground(getContext().getDrawable(R$drawable.kg_emergency_button_background));
    }

    public boolean performLongClick() {
        return super.performLongClick();
    }

    /* access modifiers changed from: package-private */
    public void updateEmergencyCallButton(boolean z, boolean z2, boolean z3) {
        boolean z4;
        this.mIsInCall = z;
        if (!z2) {
            z4 = false;
        } else if (z) {
            z4 = true;
        } else {
            z4 = z3 ? this.mEnableEmergencyCallWhileSimLocked : this.mLockPatternUtils.isSecure(KeyguardUpdateMonitor.getCurrentUser());
        }
        if (z4) {
            setVisibility(0);
            setText(z ? 17040582 : 17040555);
            return;
        }
        setVisibility(8);
    }
}
