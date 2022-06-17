package com.android.systemui.wmshell;

import android.graphics.drawable.Drawable;
import com.android.p011wm.shell.pip.Pip;
import com.android.systemui.statusbar.policy.UserInfoController;

public final /* synthetic */ class WMShell$$ExternalSyntheticLambda1 implements UserInfoController.OnUserInfoChangedListener {
    public final /* synthetic */ Pip f$0;

    public /* synthetic */ WMShell$$ExternalSyntheticLambda1(Pip pip) {
        this.f$0 = pip;
    }

    public final void onUserInfoChanged(String str, Drawable drawable, String str2) {
        this.f$0.registerSessionListenerForCurrentUser();
    }
}
