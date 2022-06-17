package com.android.systemui.privacy;

import android.app.ActivityManager;
import android.app.Dialog;
import com.android.systemui.plugins.ActivityStarter;

/* compiled from: PrivacyDialogController.kt */
final class PrivacyDialogController$startActivity$1 implements ActivityStarter.Callback {
    final /* synthetic */ PrivacyDialogController this$0;

    PrivacyDialogController$startActivity$1(PrivacyDialogController privacyDialogController) {
        this.this$0 = privacyDialogController;
    }

    public final void onActivityStarted(int i) {
        if (ActivityManager.isStartResultSuccessful(i)) {
            this.this$0.dismissDialog();
            return;
        }
        Dialog access$getDialog$p = this.this$0.dialog;
        if (access$getDialog$p != null) {
            access$getDialog$p.show();
        }
    }
}
