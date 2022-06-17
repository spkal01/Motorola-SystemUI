package com.motorola.android.systemui.safemode;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.PowerManager;
import com.android.internal.app.AlertActivity;
import com.android.internal.app.AlertController;
import com.android.systemui.R$string;

public final class SafeModeRebootActivity extends AlertActivity implements DialogInterface.OnClickListener {
    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        SafeModeRebootActivity.super.onCreate(bundle);
        AlertController.AlertParams alertParams = this.mAlertParams;
        alertParams.mTitle = getString(R$string.safemode_reboot_dialog_title);
        alertParams.mMessage = getString(R$string.safemode_reboot_dialog_message);
        alertParams.mPositiveButtonText = getString(R$string.safemode_reboot_dialog_positive_button_text);
        alertParams.mNegativeButtonText = getString(17039360);
        alertParams.mPositiveButtonListener = this;
        alertParams.mNegativeButtonListener = this;
        setupAlert();
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        if (i == -1) {
            rebootPhoneToNormal();
        }
    }

    private void rebootPhoneToNormal() {
        ((PowerManager) getSystemService("power")).reboot((String) null);
    }
}
