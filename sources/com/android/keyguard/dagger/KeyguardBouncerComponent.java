package com.android.keyguard.dagger;

import com.android.keyguard.KeyguardHostViewController;
import com.android.keyguard.KeyguardRootViewController;
import com.android.systemui.moto.CliAlertDialog;
import com.android.systemui.moto.DisplayLayoutInflater;

public interface KeyguardBouncerComponent {

    public interface Factory {
        KeyguardBouncerComponent create(DisplayLayoutInflater displayLayoutInflater);
    }

    KeyguardHostViewController getKeyguardHostViewController();

    KeyguardRootViewController getKeyguardRootViewController();

    void inject(CliAlertDialog cliAlertDialog);
}
