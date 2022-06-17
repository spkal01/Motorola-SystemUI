package com.android.keyguard;

public interface PtKDMCallback {
    int getFailedCount();

    boolean onceLockout();

    void setFailedCount(int i);

    void setOnceLockout(boolean z);
}
