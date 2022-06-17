package com.android.keyguard.dagger;

import android.view.ViewGroup;
import com.android.keyguard.KeyguardHostView;
import com.android.keyguard.KeyguardSecurityContainer;
import com.android.keyguard.KeyguardSecurityViewFlipper;
import com.android.systemui.R$id;
import com.android.systemui.R$layout;
import com.android.systemui.moto.DisplayLayoutInflater;

public interface KeyguardBouncerModule {
    static ViewGroup providesRootView(DisplayLayoutInflater displayLayoutInflater) {
        return (ViewGroup) displayLayoutInflater.getLayoutInflater().inflate(R$layout.keyguard_bouncer, (ViewGroup) null);
    }

    static KeyguardHostView providesKeyguardHostView(ViewGroup viewGroup) {
        return (KeyguardHostView) viewGroup.findViewById(R$id.keyguard_host_view);
    }

    static KeyguardSecurityContainer providesKeyguardSecurityContainer(KeyguardHostView keyguardHostView) {
        return (KeyguardSecurityContainer) keyguardHostView.findViewById(R$id.keyguard_security_container);
    }

    static KeyguardSecurityViewFlipper providesKeyguardSecurityViewFlipper(KeyguardSecurityContainer keyguardSecurityContainer) {
        return (KeyguardSecurityViewFlipper) keyguardSecurityContainer.findViewById(R$id.view_flipper);
    }
}
