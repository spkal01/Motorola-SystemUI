package com.android.systemui.controls.p004ui;

import android.content.DialogInterface;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;

/* renamed from: com.android.systemui.controls.ui.ChallengeDialogs$createConfirmationDialog$builder$1$2 */
/* compiled from: ChallengeDialogs.kt */
final class ChallengeDialogs$createConfirmationDialog$builder$1$2 implements DialogInterface.OnClickListener {
    final /* synthetic */ Function0<Unit> $onCancel;

    ChallengeDialogs$createConfirmationDialog$builder$1$2(Function0<Unit> function0) {
        this.$onCancel = function0;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        this.$onCancel.invoke();
        dialogInterface.cancel();
    }
}
