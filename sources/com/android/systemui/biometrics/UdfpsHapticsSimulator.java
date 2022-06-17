package com.android.systemui.biometrics;

import android.media.AudioAttributes;
import android.os.VibrationEffect;
import android.os.Vibrator;
import com.android.keyguard.KeyguardUpdateMonitor;
import com.android.systemui.statusbar.commandline.Command;
import com.android.systemui.statusbar.commandline.CommandRegistry;
import java.io.PrintWriter;
import java.util.List;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/* compiled from: UdfpsHapticsSimulator.kt */
public final class UdfpsHapticsSimulator implements Command {
    @NotNull
    private final KeyguardUpdateMonitor keyguardUpdateMonitor;
    private final AudioAttributes sonificationEffects = new AudioAttributes.Builder().setContentType(4).setUsage(13).build();
    @Nullable
    private UdfpsController udfpsController;
    @Nullable
    private final Vibrator vibrator;

    public UdfpsHapticsSimulator(@NotNull CommandRegistry commandRegistry, @Nullable Vibrator vibrator2, @NotNull KeyguardUpdateMonitor keyguardUpdateMonitor2) {
        Intrinsics.checkNotNullParameter(commandRegistry, "commandRegistry");
        Intrinsics.checkNotNullParameter(keyguardUpdateMonitor2, "keyguardUpdateMonitor");
        this.vibrator = vibrator2;
        this.keyguardUpdateMonitor = keyguardUpdateMonitor2;
        commandRegistry.registerCommand("udfps-haptic", new Function0<Command>(this) {
            final /* synthetic */ UdfpsHapticsSimulator this$0;

            {
                this.this$0 = r1;
            }

            @NotNull
            public final Command invoke() {
                return this.this$0;
            }
        });
    }

    public final void setUdfpsController(@Nullable UdfpsController udfpsController2) {
        this.udfpsController = udfpsController2;
    }

    public void execute(@NotNull PrintWriter printWriter, @NotNull List<String> list) {
        Intrinsics.checkNotNullParameter(printWriter, "pw");
        Intrinsics.checkNotNullParameter(list, "args");
        if (list.isEmpty()) {
            invalidCommand(printWriter);
            return;
        }
        String str = list.get(0);
        switch (str.hashCode()) {
            case -1867169789:
                if (str.equals("success")) {
                    Vibrator vibrator2 = this.vibrator;
                    if (vibrator2 != null) {
                        vibrator2.vibrate(VibrationEffect.get(0), this.sonificationEffects);
                        return;
                    }
                    return;
                }
                break;
            case -1731151282:
                if (str.equals("acquired")) {
                    this.keyguardUpdateMonitor.playAcquiredHaptic();
                    return;
                }
                break;
            case 96784904:
                if (str.equals("error")) {
                    Vibrator vibrator3 = this.vibrator;
                    if (vibrator3 != null) {
                        vibrator3.vibrate(VibrationEffect.get(1), this.sonificationEffects);
                        return;
                    }
                    return;
                }
                break;
            case 109757538:
                if (str.equals("start")) {
                    UdfpsController udfpsController2 = this.udfpsController;
                    if (udfpsController2 != null) {
                        udfpsController2.playStartHaptic();
                        return;
                    }
                    return;
                }
                break;
        }
        invalidCommand(printWriter);
    }

    public void help(@NotNull PrintWriter printWriter) {
        Intrinsics.checkNotNullParameter(printWriter, "pw");
        printWriter.println("Usage: adb shell cmd statusbar udfps-haptic <haptic>");
        printWriter.println("Available commands:");
        printWriter.println("  start");
        printWriter.println("  acquired");
        printWriter.println("  success, always plays CLICK haptic");
        printWriter.println("  error, always plays DOUBLE_CLICK haptic");
    }

    public final void invalidCommand(@NotNull PrintWriter printWriter) {
        Intrinsics.checkNotNullParameter(printWriter, "pw");
        printWriter.println("invalid command");
        help(printWriter);
    }
}
