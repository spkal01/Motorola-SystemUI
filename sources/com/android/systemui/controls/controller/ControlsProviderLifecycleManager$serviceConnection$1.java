package com.android.systemui.controls.controller;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.service.controls.IControlsProvider;
import android.util.Log;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/* compiled from: ControlsProviderLifecycleManager.kt */
public final class ControlsProviderLifecycleManager$serviceConnection$1 implements ServiceConnection {
    final /* synthetic */ ControlsProviderLifecycleManager this$0;

    ControlsProviderLifecycleManager$serviceConnection$1(ControlsProviderLifecycleManager controlsProviderLifecycleManager) {
        this.this$0 = controlsProviderLifecycleManager;
    }

    public void onServiceConnected(@NotNull ComponentName componentName, @NotNull IBinder iBinder) {
        Intrinsics.checkNotNullParameter(componentName, "name");
        Intrinsics.checkNotNullParameter(iBinder, "service");
        Log.d(this.this$0.TAG, Intrinsics.stringPlus("onServiceConnected ", componentName));
        this.this$0.bindTryCount = 0;
        ControlsProviderLifecycleManager controlsProviderLifecycleManager = this.this$0;
        IControlsProvider asInterface = IControlsProvider.Stub.asInterface(iBinder);
        Intrinsics.checkNotNullExpressionValue(asInterface, "asInterface(service)");
        controlsProviderLifecycleManager.wrapper = new ServiceWrapper(asInterface);
        try {
            iBinder.linkToDeath(this.this$0, 0);
        } catch (RemoteException unused) {
        }
        this.this$0.handlePendingServiceMethods();
    }

    public void onServiceDisconnected(@Nullable ComponentName componentName) {
        Log.d(this.this$0.TAG, Intrinsics.stringPlus("onServiceDisconnected ", componentName));
        this.this$0.wrapper = null;
        this.this$0.bindService(false);
    }
}
