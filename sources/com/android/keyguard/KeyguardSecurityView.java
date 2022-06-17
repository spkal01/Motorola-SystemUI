package com.android.keyguard;

public interface KeyguardSecurityView {
    boolean needsInput();

    void onStartingToHide() {
    }
}
