package com.android.systemui.statusbar.phone;

import android.content.DialogInterface;

public final /* synthetic */ class KeyguardBouncerDelegate$$ExternalSyntheticLambda0 implements DialogInterface.OnDismissListener {
    public final /* synthetic */ KeyguardBouncerDelegate f$0;

    public /* synthetic */ KeyguardBouncerDelegate$$ExternalSyntheticLambda0(KeyguardBouncerDelegate keyguardBouncerDelegate) {
        this.f$0 = keyguardBouncerDelegate;
    }

    public final void onDismiss(DialogInterface dialogInterface) {
        this.f$0.lambda$showCliUnlockAlert$1(dialogInterface);
    }
}
