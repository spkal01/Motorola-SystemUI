package com.android.keyguard;

import android.view.MotionEvent;
import com.android.systemui.Gefingerpoken;
import java.util.function.Predicate;

public final /* synthetic */ class KeyguardSecurityContainer$$ExternalSyntheticLambda1 implements Predicate {
    public final /* synthetic */ MotionEvent f$0;

    public /* synthetic */ KeyguardSecurityContainer$$ExternalSyntheticLambda1(MotionEvent motionEvent) {
        this.f$0 = motionEvent;
    }

    public final boolean test(Object obj) {
        return ((Gefingerpoken) obj).onInterceptTouchEvent(this.f$0);
    }
}
