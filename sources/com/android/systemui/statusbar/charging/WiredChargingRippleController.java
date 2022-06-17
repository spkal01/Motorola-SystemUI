package com.android.systemui.statusbar.charging;

import android.content.Context;
import android.graphics.PointF;
import android.os.SystemProperties;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.logging.UiEventLogger;
import com.android.settingslib.Utils;
import com.android.systemui.R$dimen;
import com.android.systemui.statusbar.FeatureFlags;
import com.android.systemui.statusbar.commandline.Command;
import com.android.systemui.statusbar.commandline.CommandRegistry;
import com.android.systemui.statusbar.policy.BatteryController;
import com.android.systemui.statusbar.policy.ConfigurationController;
import com.android.systemui.util.leak.RotationUtils;
import com.android.systemui.util.time.SystemClock;
import java.io.PrintWriter;
import java.util.List;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/* compiled from: WiredChargingRippleController.kt */
public final class WiredChargingRippleController {
    /* access modifiers changed from: private */
    @NotNull
    public final Context context;
    private int debounceLevel;
    @Nullable
    private Long lastTriggerTime;
    /* access modifiers changed from: private */
    public float normalizedPortPosX;
    /* access modifiers changed from: private */
    public float normalizedPortPosY;
    /* access modifiers changed from: private */
    @Nullable
    public Boolean pluggedIn;
    /* access modifiers changed from: private */
    public final boolean rippleEnabled;
    @NotNull
    private ChargingRippleView rippleView;
    @NotNull
    private final SystemClock systemClock;
    @NotNull
    private final UiEventLogger uiEventLogger;
    @NotNull
    private final WindowManager.LayoutParams windowLayoutParams;
    /* access modifiers changed from: private */
    @NotNull
    public final WindowManager windowManager;

    @VisibleForTesting
    public static /* synthetic */ void getRippleView$annotations() {
    }

    public WiredChargingRippleController(@NotNull CommandRegistry commandRegistry, @NotNull BatteryController batteryController, @NotNull ConfigurationController configurationController, @NotNull FeatureFlags featureFlags, @NotNull Context context2, @NotNull WindowManager windowManager2, @NotNull SystemClock systemClock2, @NotNull UiEventLogger uiEventLogger2) {
        Intrinsics.checkNotNullParameter(commandRegistry, "commandRegistry");
        Intrinsics.checkNotNullParameter(batteryController, "batteryController");
        Intrinsics.checkNotNullParameter(configurationController, "configurationController");
        Intrinsics.checkNotNullParameter(featureFlags, "featureFlags");
        Intrinsics.checkNotNullParameter(context2, "context");
        Intrinsics.checkNotNullParameter(windowManager2, "windowManager");
        Intrinsics.checkNotNullParameter(systemClock2, "systemClock");
        Intrinsics.checkNotNullParameter(uiEventLogger2, "uiEventLogger");
        this.context = context2;
        this.windowManager = windowManager2;
        this.systemClock = systemClock2;
        this.uiEventLogger = uiEventLogger2;
        this.rippleEnabled = featureFlags.isChargingRippleEnabled() && !SystemProperties.getBoolean("persist.debug.suppress-charging-ripple", false);
        this.normalizedPortPosX = context2.getResources().getFloat(R$dimen.physical_charger_port_location_normalized_x);
        this.normalizedPortPosY = context2.getResources().getFloat(R$dimen.physical_charger_port_location_normalized_y);
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.width = -1;
        layoutParams.height = -1;
        layoutParams.layoutInDisplayCutoutMode = 3;
        layoutParams.format = -3;
        layoutParams.type = 2006;
        layoutParams.setFitInsetsTypes(0);
        layoutParams.setTitle("Wired Charging Animation");
        layoutParams.flags = 24;
        layoutParams.setTrustedOverlay();
        Unit unit = Unit.INSTANCE;
        this.windowLayoutParams = layoutParams;
        this.rippleView = new ChargingRippleView(context2, (AttributeSet) null);
        this.pluggedIn = Boolean.valueOf(batteryController.isPluggedIn());
        batteryController.addCallback(new WiredChargingRippleController$batteryStateChangeCallback$1(this, batteryController));
        configurationController.addCallback(new WiredChargingRippleController$configurationChangedListener$1(this));
        commandRegistry.registerCommand("charging-ripple", new Function0<Command>(this) {
            final /* synthetic */ WiredChargingRippleController this$0;

            {
                this.this$0 = r1;
            }

            @NotNull
            public final Command invoke() {
                return new ChargingRippleCommand(this.this$0);
            }
        });
        updateRippleColor();
    }

    @NotNull
    public final ChargingRippleView getRippleView() {
        return this.rippleView;
    }

    /* renamed from: startRippleWithDebounce$frameworks__base__packages__SystemUI__android_common__SystemUI_core */
    public final void mo18941x242f6c47() {
        long elapsedRealtime = this.systemClock.elapsedRealtime();
        Long l = this.lastTriggerTime;
        if (l != null) {
            Intrinsics.checkNotNull(l);
            if (((double) (elapsedRealtime - l.longValue())) <= ((double) 2000) * Math.pow(2.0d, (double) this.debounceLevel)) {
                this.debounceLevel = Math.min(3, this.debounceLevel + 1);
                this.lastTriggerTime = Long.valueOf(elapsedRealtime);
            }
        }
        startRipple();
        this.debounceLevel = 0;
        this.lastTriggerTime = Long.valueOf(elapsedRealtime);
    }

    public final void startRipple() {
        if (this.rippleEnabled && !this.rippleView.getRippleInProgress() && this.rippleView.getParent() == null) {
            this.windowLayoutParams.packageName = this.context.getOpPackageName();
            this.rippleView.addOnAttachStateChangeListener(new WiredChargingRippleController$startRipple$1(this));
            this.windowManager.addView(this.rippleView, this.windowLayoutParams);
            this.uiEventLogger.log(WiredChargingRippleEvent.CHARGING_RIPPLE_PLAYED);
        }
    }

    /* access modifiers changed from: private */
    public final void layoutRipple() {
        PointF pointF;
        DisplayMetrics displayMetrics = new DisplayMetrics();
        this.context.getDisplay().getRealMetrics(displayMetrics);
        int i = displayMetrics.widthPixels;
        int i2 = displayMetrics.heightPixels;
        this.rippleView.setRadius((float) Integer.max(i, i2));
        ChargingRippleView chargingRippleView = this.rippleView;
        int rotation = RotationUtils.getRotation(this.context);
        if (rotation == 1) {
            pointF = new PointF(((float) i) * this.normalizedPortPosY, ((float) i2) * (((float) 1) - this.normalizedPortPosX));
        } else if (rotation == 2) {
            float f = (float) 1;
            pointF = new PointF(((float) i) * (f - this.normalizedPortPosX), ((float) i2) * (f - this.normalizedPortPosY));
        } else if (rotation != 3) {
            pointF = new PointF(((float) i) * this.normalizedPortPosX, ((float) i2) * this.normalizedPortPosY);
        } else {
            pointF = new PointF(((float) i) * (((float) 1) - this.normalizedPortPosY), ((float) i2) * this.normalizedPortPosX);
        }
        chargingRippleView.setOrigin(pointF);
    }

    /* access modifiers changed from: private */
    public final void updateRippleColor() {
        this.rippleView.setColor(Utils.getColorAttr(this.context, 16843829).getDefaultColor());
    }

    /* compiled from: WiredChargingRippleController.kt */
    public final class ChargingRippleCommand implements Command {
        final /* synthetic */ WiredChargingRippleController this$0;

        public ChargingRippleCommand(WiredChargingRippleController wiredChargingRippleController) {
            Intrinsics.checkNotNullParameter(wiredChargingRippleController, "this$0");
            this.this$0 = wiredChargingRippleController;
        }

        public void execute(@NotNull PrintWriter printWriter, @NotNull List<String> list) {
            Intrinsics.checkNotNullParameter(printWriter, "pw");
            Intrinsics.checkNotNullParameter(list, "args");
            this.this$0.startRipple();
        }
    }

    /* compiled from: WiredChargingRippleController.kt */
    public enum WiredChargingRippleEvent implements UiEventLogger.UiEventEnum {
        CHARGING_RIPPLE_PLAYED(829);
        
        private final int _id;

        private WiredChargingRippleEvent(int i) {
            this._id = i;
        }

        public int getId() {
            return this._id;
        }
    }
}
