package com.android.p011wm.shell;

import android.os.RemoteException;
import com.android.p011wm.shell.common.ShellExecutor;
import com.android.p011wm.shell.pip.PinnedStackListenerForwarder;

/* renamed from: com.android.wm.shell.WindowManagerShellWrapper */
public class WindowManagerShellWrapper {
    private final PinnedStackListenerForwarder mPinnedStackListenerForwarder;

    public WindowManagerShellWrapper(ShellExecutor shellExecutor) {
        this.mPinnedStackListenerForwarder = new PinnedStackListenerForwarder(shellExecutor);
    }

    public void addPinnedStackListener(PinnedStackListenerForwarder.PinnedTaskListener pinnedTaskListener) throws RemoteException {
        this.mPinnedStackListenerForwarder.addListener(pinnedTaskListener);
        this.mPinnedStackListenerForwarder.register(0);
    }
}
