package com.android.p011wm.shell.common.magnetictarget;

import com.android.p011wm.shell.common.magnetictarget.MagnetizedObject;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function5;
import kotlin.jvm.internal.FunctionReferenceImpl;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/* renamed from: com.android.wm.shell.common.magnetictarget.MagnetizedObject$animateStuckToTarget$1 */
/* compiled from: MagnetizedObject.kt */
/* synthetic */ class MagnetizedObject$animateStuckToTarget$1 extends FunctionReferenceImpl implements Function5<MagnetizedObject.MagneticTarget, Float, Float, Boolean, Function0<? extends Unit>, Unit> {
    MagnetizedObject$animateStuckToTarget$1(MagnetizedObject<T> magnetizedObject) {
        super(5, magnetizedObject, MagnetizedObject.class, "animateStuckToTargetInternal", "animateStuckToTargetInternal(Lcom/android/wm/shell/common/magnetictarget/MagnetizedObject$MagneticTarget;FFZLkotlin/jvm/functions/Function0;)V", 0);
    }

    public /* bridge */ /* synthetic */ Object invoke(Object obj, Object obj2, Object obj3, Object obj4, Object obj5) {
        invoke((MagnetizedObject.MagneticTarget) obj, ((Number) obj2).floatValue(), ((Number) obj3).floatValue(), ((Boolean) obj4).booleanValue(), (Function0<Unit>) (Function0) obj5);
        return Unit.INSTANCE;
    }

    public final void invoke(@NotNull MagnetizedObject.MagneticTarget magneticTarget, float f, float f2, boolean z, @Nullable Function0<Unit> function0) {
        Intrinsics.checkNotNullParameter(magneticTarget, "p0");
        ((MagnetizedObject) this.receiver).animateStuckToTargetInternal(magneticTarget, f, f2, z, function0);
    }
}
