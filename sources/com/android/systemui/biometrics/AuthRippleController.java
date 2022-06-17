package com.android.systemui.biometrics;

import android.content.Context;
import android.graphics.PointF;
import android.hardware.biometrics.BiometricSourceType;
import com.android.keyguard.KeyguardUpdateMonitor;
import com.android.keyguard.KeyguardUpdateMonitorCallback;
import com.android.settingslib.Utils;
import com.android.systemui.biometrics.AuthController;
import com.android.systemui.statusbar.CircleReveal;
import com.android.systemui.statusbar.LightRevealEffect;
import com.android.systemui.statusbar.LightRevealScrim;
import com.android.systemui.statusbar.NotificationShadeWindowController;
import com.android.systemui.statusbar.commandline.Command;
import com.android.systemui.statusbar.commandline.CommandRegistry;
import com.android.systemui.statusbar.phone.BiometricUnlockController;
import com.android.systemui.statusbar.phone.KeyguardBypassController;
import com.android.systemui.statusbar.phone.StatusBar;
import com.android.systemui.statusbar.policy.ConfigurationController;
import com.android.systemui.util.ViewController;
import java.io.PrintWriter;
import java.util.List;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/* compiled from: AuthRippleController.kt */
public final class AuthRippleController extends ViewController<AuthRippleView> {
    @NotNull
    private final AuthController authController;
    @NotNull
    private final AuthController.Callback authControllerCallback = new AuthRippleController$authControllerCallback$1(this);
    @NotNull
    private final BiometricUnlockController biometricUnlockController;
    @NotNull
    private final KeyguardBypassController bypassController;
    @Nullable
    private LightRevealEffect circleReveal;
    @NotNull
    private final CommandRegistry commandRegistry;
    @NotNull
    private final ConfigurationController.ConfigurationListener configurationChangedListener = new AuthRippleController$configurationChangedListener$1(this);
    @NotNull
    private final ConfigurationController configurationController;
    /* access modifiers changed from: private */
    @Nullable
    public PointF faceSensorLocation;
    @Nullable
    private PointF fingerprintSensorLocation;
    @NotNull
    private final KeyguardUpdateMonitor keyguardUpdateMonitor;
    @NotNull
    private final KeyguardUpdateMonitorCallback keyguardUpdateMonitorCallback = new AuthRippleController$keyguardUpdateMonitorCallback$1(this);
    /* access modifiers changed from: private */
    @NotNull
    public final NotificationShadeWindowController notificationShadeWindowController;
    @NotNull
    private final StatusBar statusBar;
    @NotNull
    private final Context sysuiContext;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public AuthRippleController(@NotNull StatusBar statusBar2, @NotNull Context context, @NotNull AuthController authController2, @NotNull ConfigurationController configurationController2, @NotNull KeyguardUpdateMonitor keyguardUpdateMonitor2, @NotNull CommandRegistry commandRegistry2, @NotNull NotificationShadeWindowController notificationShadeWindowController2, @NotNull KeyguardBypassController keyguardBypassController, @NotNull BiometricUnlockController biometricUnlockController2, @Nullable AuthRippleView authRippleView) {
        super(authRippleView);
        Intrinsics.checkNotNullParameter(statusBar2, "statusBar");
        Intrinsics.checkNotNullParameter(context, "sysuiContext");
        Intrinsics.checkNotNullParameter(authController2, "authController");
        Intrinsics.checkNotNullParameter(configurationController2, "configurationController");
        Intrinsics.checkNotNullParameter(keyguardUpdateMonitor2, "keyguardUpdateMonitor");
        Intrinsics.checkNotNullParameter(commandRegistry2, "commandRegistry");
        Intrinsics.checkNotNullParameter(notificationShadeWindowController2, "notificationShadeWindowController");
        Intrinsics.checkNotNullParameter(keyguardBypassController, "bypassController");
        Intrinsics.checkNotNullParameter(biometricUnlockController2, "biometricUnlockController");
        this.statusBar = statusBar2;
        this.sysuiContext = context;
        this.authController = authController2;
        this.configurationController = configurationController2;
        this.keyguardUpdateMonitor = keyguardUpdateMonitor2;
        this.commandRegistry = commandRegistry2;
        this.notificationShadeWindowController = notificationShadeWindowController2;
        this.bypassController = keyguardBypassController;
        this.biometricUnlockController = biometricUnlockController2;
    }

    @Nullable
    public final PointF getFingerprintSensorLocation() {
        return this.fingerprintSensorLocation;
    }

    public void onViewAttached() {
        updateRippleColor();
        updateSensorLocation();
        this.authController.addCallback(this.authControllerCallback);
        this.configurationController.addCallback(this.configurationChangedListener);
        this.keyguardUpdateMonitor.registerCallback(this.keyguardUpdateMonitorCallback);
        this.commandRegistry.registerCommand("auth-ripple", new AuthRippleController$onViewAttached$1(this));
    }

    public void onViewDetached() {
        this.authController.removeCallback(this.authControllerCallback);
        this.keyguardUpdateMonitor.removeCallback(this.keyguardUpdateMonitorCallback);
        this.configurationController.removeCallback(this.configurationChangedListener);
        this.commandRegistry.unregisterCommand("auth-ripple");
        this.notificationShadeWindowController.setForcePluginOpen(false, this);
    }

    /* access modifiers changed from: private */
    public final void showRipple(BiometricSourceType biometricSourceType) {
        PointF pointF;
        if (this.keyguardUpdateMonitor.isKeyguardVisible() && !this.keyguardUpdateMonitor.userNeedsStrongAuth()) {
            if (biometricSourceType == BiometricSourceType.FINGERPRINT && (pointF = this.fingerprintSensorLocation) != null) {
                Intrinsics.checkNotNull(pointF);
                ((AuthRippleView) this.mView).setSensorLocation(pointF);
                showRipple();
            } else if (biometricSourceType == BiometricSourceType.FACE && this.faceSensorLocation != null && this.bypassController.canBypass()) {
                PointF pointF2 = this.faceSensorLocation;
                Intrinsics.checkNotNull(pointF2);
                ((AuthRippleView) this.mView).setSensorLocation(pointF2);
                showRipple();
            }
        }
    }

    /* access modifiers changed from: private */
    public final void showRipple() {
        boolean z = true;
        this.notificationShadeWindowController.setForcePluginOpen(true, this);
        int mode = this.biometricUnlockController.getMode();
        if (this.circleReveal == null || !(mode == 1 || mode == 2 || mode == 6)) {
            z = false;
        }
        LightRevealScrim lightRevealScrim = this.statusBar.getLightRevealScrim();
        if (z && lightRevealScrim != null) {
            LightRevealEffect lightRevealEffect = this.circleReveal;
            Intrinsics.checkNotNull(lightRevealEffect);
            lightRevealScrim.setRevealEffect(lightRevealEffect);
        }
        AuthRippleView authRippleView = (AuthRippleView) this.mView;
        AuthRippleController$showRipple$1 authRippleController$showRipple$1 = new AuthRippleController$showRipple$1(this);
        if (!z) {
            lightRevealScrim = null;
        }
        authRippleView.startRipple(authRippleController$showRipple$1, lightRevealScrim);
    }

    public final void updateSensorLocation() {
        this.fingerprintSensorLocation = this.authController.getFingerprintSensorLocation();
        this.faceSensorLocation = this.authController.getFaceAuthSensorLocation();
        PointF pointF = this.fingerprintSensorLocation;
        if (pointF != null) {
            float f = pointF.x;
            this.circleReveal = new CircleReveal(f, pointF.y, 0.0f, Math.max(Math.max(f, this.statusBar.getDisplayWidth() - pointF.x), Math.max(pointF.y, this.statusBar.getDisplayHeight() - pointF.y)));
        }
    }

    /* access modifiers changed from: private */
    public final void updateRippleColor() {
        ((AuthRippleView) this.mView).setColor(Utils.getColorAttr(this.sysuiContext, 16843829).getDefaultColor());
    }

    /* compiled from: AuthRippleController.kt */
    public final class AuthRippleCommand implements Command {
        final /* synthetic */ AuthRippleController this$0;

        public AuthRippleCommand(AuthRippleController authRippleController) {
            Intrinsics.checkNotNullParameter(authRippleController, "this$0");
            this.this$0 = authRippleController;
        }

        public void execute(@NotNull PrintWriter printWriter, @NotNull List<String> list) {
            Intrinsics.checkNotNullParameter(printWriter, "pw");
            Intrinsics.checkNotNullParameter(list, "args");
            if (list.isEmpty()) {
                invalidCommand(printWriter);
                return;
            }
            String str = list.get(0);
            int hashCode = str.hashCode();
            if (hashCode != -1375934236) {
                if (hashCode != -1349088399) {
                    if (hashCode == 3135069 && str.equals("face")) {
                        printWriter.println(Intrinsics.stringPlus("face ripple sensorLocation=", this.this$0.faceSensorLocation));
                        this.this$0.showRipple(BiometricSourceType.FACE);
                        return;
                    }
                } else if (str.equals("custom")) {
                    if (list.size() != 3 || StringsKt__StringNumberConversionsJVMKt.toFloatOrNull(list.get(1)) == null || StringsKt__StringNumberConversionsJVMKt.toFloatOrNull(list.get(2)) == null) {
                        invalidCommand(printWriter);
                        return;
                    }
                    printWriter.println("custom ripple sensorLocation=" + Float.parseFloat(list.get(1)) + ", " + Float.parseFloat(list.get(2)));
                    ((AuthRippleView) this.this$0.mView).setSensorLocation(new PointF(Float.parseFloat(list.get(1)), Float.parseFloat(list.get(2))));
                    this.this$0.showRipple();
                    return;
                }
            } else if (str.equals("fingerprint")) {
                printWriter.println(Intrinsics.stringPlus("fingerprint ripple sensorLocation=", this.this$0.getFingerprintSensorLocation()));
                this.this$0.showRipple(BiometricSourceType.FINGERPRINT);
                return;
            }
            invalidCommand(printWriter);
        }

        public void help(@NotNull PrintWriter printWriter) {
            Intrinsics.checkNotNullParameter(printWriter, "pw");
            printWriter.println("Usage: adb shell cmd statusbar auth-ripple <command>");
            printWriter.println("Available commands:");
            printWriter.println("  fingerprint");
            printWriter.println("  face");
            printWriter.println("  custom <x-location: int> <y-location: int>");
        }

        public final void invalidCommand(@NotNull PrintWriter printWriter) {
            Intrinsics.checkNotNullParameter(printWriter, "pw");
            printWriter.println("invalid command");
            help(printWriter);
        }
    }
}
