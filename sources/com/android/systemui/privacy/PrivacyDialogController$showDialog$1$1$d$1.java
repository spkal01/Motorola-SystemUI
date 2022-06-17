package com.android.systemui.privacy;

import kotlin.Unit;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.FunctionReferenceImpl;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

/* compiled from: PrivacyDialogController.kt */
/* synthetic */ class PrivacyDialogController$showDialog$1$1$d$1 extends FunctionReferenceImpl implements Function2<String, Integer, Unit> {
    PrivacyDialogController$showDialog$1$1$d$1(PrivacyDialogController privacyDialogController) {
        super(2, privacyDialogController, PrivacyDialogController.class, "startActivity", "startActivity(Ljava/lang/String;I)V", 0);
    }

    public /* bridge */ /* synthetic */ Object invoke(Object obj, Object obj2) {
        invoke((String) obj, ((Number) obj2).intValue());
        return Unit.INSTANCE;
    }

    public final void invoke(@NotNull String str, int i) {
        Intrinsics.checkNotNullParameter(str, "p0");
        ((PrivacyDialogController) this.receiver).startActivity(str, i);
    }
}
