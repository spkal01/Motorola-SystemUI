package com.android.p011wm.shell.common.magnetictarget;

import com.android.p011wm.shell.common.magnetictarget.MagnetizedObject;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.Lambda;

/* renamed from: com.android.wm.shell.common.magnetictarget.MagnetizedObject$maybeConsumeMotionEvent$1 */
/* compiled from: MagnetizedObject.kt */
final class MagnetizedObject$maybeConsumeMotionEvent$1 extends Lambda implements Function0<Unit> {
    final /* synthetic */ MagnetizedObject.MagneticTarget $flungToTarget;
    final /* synthetic */ MagnetizedObject<T> this$0;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    MagnetizedObject$maybeConsumeMotionEvent$1(MagnetizedObject<T> magnetizedObject, MagnetizedObject.MagneticTarget magneticTarget) {
        super(0);
        this.this$0 = magnetizedObject;
        this.$flungToTarget = magneticTarget;
    }

    public final void invoke() {
        this.this$0.getMagnetListener().onReleasedInTarget(this.$flungToTarget);
        this.this$0.targetObjectIsStuckTo = null;
        this.this$0.vibrateIfEnabled(5);
    }
}
