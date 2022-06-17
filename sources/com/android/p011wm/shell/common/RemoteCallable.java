package com.android.p011wm.shell.common;

import android.content.Context;

/* renamed from: com.android.wm.shell.common.RemoteCallable */
public interface RemoteCallable<T> {
    Context getContext();

    ShellExecutor getRemoteCallExecutor();
}
