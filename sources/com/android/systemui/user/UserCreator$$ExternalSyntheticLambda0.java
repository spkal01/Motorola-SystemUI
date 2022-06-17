package com.android.systemui.user;

import android.app.Dialog;
import android.graphics.drawable.Drawable;
import java.util.function.Consumer;

public final /* synthetic */ class UserCreator$$ExternalSyntheticLambda0 implements Runnable {
    public final /* synthetic */ UserCreator f$0;
    public final /* synthetic */ String f$1;
    public final /* synthetic */ Dialog f$2;
    public final /* synthetic */ Runnable f$3;
    public final /* synthetic */ Drawable f$4;
    public final /* synthetic */ Consumer f$5;

    public /* synthetic */ UserCreator$$ExternalSyntheticLambda0(UserCreator userCreator, String str, Dialog dialog, Runnable runnable, Drawable drawable, Consumer consumer) {
        this.f$0 = userCreator;
        this.f$1 = str;
        this.f$2 = dialog;
        this.f$3 = runnable;
        this.f$4 = drawable;
        this.f$5 = consumer;
    }

    public final void run() {
        this.f$0.lambda$createUser$0(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5);
    }
}
