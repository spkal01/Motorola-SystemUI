package com.android.systemui.toast;

import android.app.ITransientNotificationCallback;
import android.os.IBinder;

public final /* synthetic */ class ToastUI$$ExternalSyntheticLambda0 implements Runnable {
    public final /* synthetic */ ToastUI f$0;
    public final /* synthetic */ int f$1;
    public final /* synthetic */ int f$2;
    public final /* synthetic */ CharSequence f$3;
    public final /* synthetic */ String f$4;
    public final /* synthetic */ ITransientNotificationCallback f$5;
    public final /* synthetic */ IBinder f$6;
    public final /* synthetic */ IBinder f$7;
    public final /* synthetic */ int f$8;

    public /* synthetic */ ToastUI$$ExternalSyntheticLambda0(ToastUI toastUI, int i, int i2, CharSequence charSequence, String str, ITransientNotificationCallback iTransientNotificationCallback, IBinder iBinder, IBinder iBinder2, int i3) {
        this.f$0 = toastUI;
        this.f$1 = i;
        this.f$2 = i2;
        this.f$3 = charSequence;
        this.f$4 = str;
        this.f$5 = iTransientNotificationCallback;
        this.f$6 = iBinder;
        this.f$7 = iBinder2;
        this.f$8 = i3;
    }

    public final void run() {
        this.f$0.lambda$showToastForDisplay$1(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, this.f$8);
    }
}
