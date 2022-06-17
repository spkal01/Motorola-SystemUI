package com.android.systemui.biometrics;

import com.android.systemui.biometrics.AuthRippleController;
import com.android.systemui.statusbar.commandline.Command;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.Lambda;
import org.jetbrains.annotations.NotNull;

/* compiled from: AuthRippleController.kt */
final class AuthRippleController$onViewAttached$1 extends Lambda implements Function0<Command> {
    final /* synthetic */ AuthRippleController this$0;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    AuthRippleController$onViewAttached$1(AuthRippleController authRippleController) {
        super(0);
        this.this$0 = authRippleController;
    }

    @NotNull
    public final Command invoke() {
        return new AuthRippleController.AuthRippleCommand(this.this$0);
    }
}
