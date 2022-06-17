package com.android.keyguard.dagger;

import com.android.systemui.statusbar.phone.KeyguardStatusBarView;
import com.android.systemui.statusbar.phone.KeyguardStatusBarViewController;

public interface KeyguardStatusBarViewComponent {

    public interface Factory {
        KeyguardStatusBarViewComponent build(KeyguardStatusBarView keyguardStatusBarView);
    }

    KeyguardStatusBarViewController getKeyguardStatusBarViewController();
}
