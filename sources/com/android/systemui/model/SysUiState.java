package com.android.systemui.model;

import android.util.Log;
import com.android.systemui.Dumpable;
import com.android.systemui.shared.system.QuickStepContract;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class SysUiState implements Dumpable {
    private static final String TAG = "SysUiState";
    private final List<SysUiStateCallback> mCallbacks = new ArrayList();
    private int mCliFlags;
    private int mFlags;
    private int mFlagsToClear = 0;
    private int mFlagsToSet = 0;

    public interface SysUiStateCallback {
        void onSystemUiStateChanged(int i);

        void onSystemUiStateChanged(int i, int i2) {
        }
    }

    public void addCallback(SysUiStateCallback sysUiStateCallback) {
        this.mCallbacks.add(sysUiStateCallback);
        sysUiStateCallback.onSystemUiStateChanged(this.mFlags);
    }

    public void removeCallback(SysUiStateCallback sysUiStateCallback) {
        this.mCallbacks.remove(sysUiStateCallback);
    }

    public int getFlags() {
        return this.mFlags;
    }

    public SysUiState setFlag(int i, boolean z) {
        if (z) {
            this.mFlagsToSet = i | this.mFlagsToSet;
        } else {
            this.mFlagsToClear = i | this.mFlagsToClear;
        }
        return this;
    }

    public void commitUpdate(int i) {
        updateFlags(i);
        this.mFlagsToSet = 0;
        this.mFlagsToClear = 0;
    }

    private void updateFlags(int i) {
        if (i == 0 || i == 1) {
            notifyAndSetSystemUiStateChanged(((i == 0 ? this.mFlags : this.mCliFlags) | this.mFlagsToSet) & (~this.mFlagsToClear), i == 0 ? this.mFlags : this.mCliFlags, i);
            return;
        }
        String str = TAG;
        Log.w(str, "Ignoring flag update for display: " + i, new Throwable());
    }

    private void notifyAndSetSystemUiStateChanged(int i, int i2, int i3) {
        if (i == i2) {
            return;
        }
        if (i3 == 0) {
            this.mCallbacks.forEach(new SysUiState$$ExternalSyntheticLambda0(i));
            this.mFlags = i;
            return;
        }
        this.mCallbacks.forEach(new SysUiState$$ExternalSyntheticLambda1(i, i3));
        this.mCliFlags = i;
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        printWriter.println("SysUiState state:");
        printWriter.print("  mSysUiStateFlags=");
        printWriter.println(this.mFlags);
        printWriter.println("    " + QuickStepContract.getSystemUiStateString(this.mFlags));
        printWriter.print("    backGestureDisabled=");
        printWriter.println(QuickStepContract.isBackGestureDisabled(this.mFlags));
        printWriter.print("    assistantGestureDisabled=");
        printWriter.println(QuickStepContract.isAssistantGestureDisabled(this.mFlags));
    }
}
