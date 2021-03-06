package com.android.keyguard.dagger;

import com.android.systemui.statusbar.phone.UserAvatarView;
import com.android.systemui.statusbar.policy.KeyguardQsUserSwitchController;

public interface KeyguardQsUserSwitchComponent {

    public interface Factory {
        KeyguardQsUserSwitchComponent build(UserAvatarView userAvatarView);
    }

    KeyguardQsUserSwitchController getKeyguardQsUserSwitchController();
}
