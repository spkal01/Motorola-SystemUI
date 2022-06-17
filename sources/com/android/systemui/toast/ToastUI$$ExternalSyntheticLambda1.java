package com.android.systemui.toast;

import android.app.ITransientNotificationCallback;
import android.os.IBinder;

public final /* synthetic */ class ToastUI$$ExternalSyntheticLambda1 implements Runnable {
    public final /* synthetic */ ToastUI f$0;
    public final /* synthetic */ int f$1;
    public final /* synthetic */ CharSequence f$2;
    public final /* synthetic */ String f$3;
    public final /* synthetic */ ITransientNotificationCallback f$4;
    public final /* synthetic */ IBinder f$5;
    public final /* synthetic */ IBinder f$6;
    public final /* synthetic */ int f$7;

    public /* synthetic */ ToastUI$$ExternalSyntheticLambda1(ToastUI toastUI, int i, CharSequence charSequence, String str, ITransientNotificationCallback iTransientNotificationCallback, IBinder iBinder, IBinder iBinder2, int i2) {
        this.f$0 = toastUI;
        this.f$1 = i;
        this.f$2 = charSequence;
        this.f$3 = str;
        this.f$4 = iTransientNotificationCallback;
        this.f$5 = iBinder;
        this.f$6 = iBinder2;
        this.f$7 = i2;
    }

    public final void run() {
        this.f$0.lambda$showToast$0(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7);
    }
}
