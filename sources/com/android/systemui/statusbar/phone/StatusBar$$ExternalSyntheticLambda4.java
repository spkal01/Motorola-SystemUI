package com.android.systemui.statusbar.phone;

import android.app.Fragment;
import com.android.systemui.fragments.FragmentHostManager;

public final /* synthetic */ class StatusBar$$ExternalSyntheticLambda4 implements FragmentHostManager.FragmentListener {
    public final /* synthetic */ StatusBar f$0;

    public /* synthetic */ StatusBar$$ExternalSyntheticLambda4(StatusBar statusBar) {
        this.f$0 = statusBar;
    }

    public final void onFragmentViewCreated(String str, Fragment fragment) {
        this.f$0.lambda$makeStatusBarView$4(str, fragment);
    }
}
