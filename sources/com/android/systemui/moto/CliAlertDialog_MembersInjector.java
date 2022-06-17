package com.android.systemui.moto;

import com.android.keyguard.EmergencyButtonController;

public final class CliAlertDialog_MembersInjector {
    public static void injectMEmergencyButtonControllerFactory(CliAlertDialog cliAlertDialog, EmergencyButtonController.Factory factory) {
        cliAlertDialog.mEmergencyButtonControllerFactory = factory;
    }
}
