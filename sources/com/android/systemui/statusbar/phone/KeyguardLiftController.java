package com.android.systemui.statusbar.phone;

import android.hardware.Sensor;
import android.hardware.TriggerEventListener;
import com.android.keyguard.KeyguardUpdateMonitor;
import com.android.keyguard.KeyguardUpdateMonitorCallback;
import com.android.systemui.Dumpable;
import com.android.systemui.dump.DumpManager;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.util.sensors.AsyncSensorManager;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

/* compiled from: KeyguardLiftController.kt */
public final class KeyguardLiftController extends KeyguardUpdateMonitorCallback implements StatusBarStateController.StateListener, Dumpable {
    @NotNull
    private final AsyncSensorManager asyncSensorManager;
    private boolean bouncerVisible;
    /* access modifiers changed from: private */
    public boolean isListening;
    /* access modifiers changed from: private */
    @NotNull
    public final KeyguardUpdateMonitor keyguardUpdateMonitor;
    @NotNull
    private final TriggerEventListener listener = new KeyguardLiftController$listener$1(this);
    private final Sensor pickupSensor;
    @NotNull
    private final StatusBarStateController statusBarStateController;

    public KeyguardLiftController(@NotNull StatusBarStateController statusBarStateController2, @NotNull AsyncSensorManager asyncSensorManager2, @NotNull KeyguardUpdateMonitor keyguardUpdateMonitor2, @NotNull DumpManager dumpManager) {
        Intrinsics.checkNotNullParameter(statusBarStateController2, "statusBarStateController");
        Intrinsics.checkNotNullParameter(asyncSensorManager2, "asyncSensorManager");
        Intrinsics.checkNotNullParameter(keyguardUpdateMonitor2, "keyguardUpdateMonitor");
        Intrinsics.checkNotNullParameter(dumpManager, "dumpManager");
        this.statusBarStateController = statusBarStateController2;
        this.asyncSensorManager = asyncSensorManager2;
        this.keyguardUpdateMonitor = keyguardUpdateMonitor2;
        this.pickupSensor = asyncSensorManager2.getDefaultSensor(25);
        String name = KeyguardLiftController.class.getName();
        Intrinsics.checkNotNullExpressionValue(name, "javaClass.name");
        dumpManager.registerDumpable(name, this);
        statusBarStateController2.addCallback(this);
        keyguardUpdateMonitor2.registerCallback(this);
        updateListeningState();
    }

    public void onDozingChanged(boolean z) {
        updateListeningState();
    }

    public void onKeyguardBouncerChanged(boolean z) {
        this.bouncerVisible = z;
        updateListeningState();
    }

    public void onKeyguardVisibilityChanged(boolean z) {
        updateListeningState();
    }

    public void dump(@NotNull FileDescriptor fileDescriptor, @NotNull PrintWriter printWriter, @NotNull String[] strArr) {
        Intrinsics.checkNotNullParameter(fileDescriptor, "fd");
        Intrinsics.checkNotNullParameter(printWriter, "pw");
        Intrinsics.checkNotNullParameter(strArr, "args");
        printWriter.println("KeyguardLiftController:");
        printWriter.println(Intrinsics.stringPlus("  pickupSensor: ", this.pickupSensor));
        printWriter.println(Intrinsics.stringPlus("  isListening: ", Boolean.valueOf(this.isListening)));
        printWriter.println(Intrinsics.stringPlus("  bouncerVisible: ", Boolean.valueOf(this.bouncerVisible)));
    }

    /* access modifiers changed from: private */
    public final void updateListeningState() {
        if (this.pickupSensor != null) {
            boolean z = true;
            boolean z2 = this.keyguardUpdateMonitor.isKeyguardVisible() && !this.statusBarStateController.isDozing();
            boolean isFaceAuthEnabledForUser = this.keyguardUpdateMonitor.isFaceAuthEnabledForUser(KeyguardUpdateMonitor.getCurrentUser());
            if ((!z2 && !this.bouncerVisible) || !isFaceAuthEnabledForUser) {
                z = false;
            }
            if (z != this.isListening) {
                this.isListening = z;
                if (z) {
                    this.asyncSensorManager.requestTriggerSensor(this.listener, this.pickupSensor);
                } else {
                    this.asyncSensorManager.cancelTriggerSensor(this.listener, this.pickupSensor);
                }
            }
        }
    }
}
