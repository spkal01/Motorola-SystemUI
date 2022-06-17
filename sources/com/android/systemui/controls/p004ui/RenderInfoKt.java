package com.android.systemui.controls.p004ui;

import com.android.systemui.R$color;
import com.android.systemui.R$drawable;
import java.util.Map;
import kotlin.Pair;
import kotlin.TuplesKt;
import org.jetbrains.annotations.NotNull;

/* renamed from: com.android.systemui.controls.ui.RenderInfoKt */
/* compiled from: RenderInfo.kt */
public final class RenderInfoKt {
    /* access modifiers changed from: private */
    @NotNull
    public static final Map<Integer, Pair<Integer, Integer>> deviceColorMap = MapsKt__MapWithDefaultKt.withDefault(MapsKt__MapsKt.mapOf(TuplesKt.m104to(49001, new Pair(Integer.valueOf(R$color.control_default_foreground), Integer.valueOf(R$color.control_default_background))), TuplesKt.m104to(49002, new Pair(Integer.valueOf(R$color.thermo_heat_foreground), Integer.valueOf(R$color.control_enabled_thermo_heat_background))), TuplesKt.m104to(49003, new Pair(Integer.valueOf(R$color.thermo_cool_foreground), Integer.valueOf(R$color.control_enabled_thermo_cool_background))), TuplesKt.m104to(13, new Pair(Integer.valueOf(R$color.light_foreground), Integer.valueOf(R$color.control_enabled_light_background))), TuplesKt.m104to(50, new Pair(Integer.valueOf(R$color.camera_foreground), Integer.valueOf(R$color.control_enabled_default_background)))), RenderInfoKt$deviceColorMap$1.INSTANCE);
    /* access modifiers changed from: private */
    @NotNull
    public static final Map<Integer, Integer> deviceIconMap;

    static {
        int i = R$drawable.ic_device_thermostat_off;
        int i2 = R$drawable.ic_device_thermostat;
        int i3 = R$drawable.ic_device_air_freshener;
        int i4 = R$drawable.ic_device_kettle;
        int i5 = R$drawable.ic_device_washer;
        int i6 = R$drawable.ic_device_blinds;
        int i7 = R$drawable.ic_device_drawer;
        int i8 = R$drawable.ic_device_pergola;
        int i9 = R$drawable.ic_device_window;
        deviceIconMap = MapsKt__MapWithDefaultKt.withDefault(MapsKt__MapsKt.mapOf(TuplesKt.m104to(49001, Integer.valueOf(i)), TuplesKt.m104to(49002, Integer.valueOf(i2)), TuplesKt.m104to(49003, Integer.valueOf(i2)), TuplesKt.m104to(49004, Integer.valueOf(i2)), TuplesKt.m104to(49005, Integer.valueOf(i)), TuplesKt.m104to(49, Integer.valueOf(i2)), TuplesKt.m104to(13, Integer.valueOf(R$drawable.ic_device_light)), TuplesKt.m104to(50, Integer.valueOf(R$drawable.ic_device_camera)), TuplesKt.m104to(45, Integer.valueOf(R$drawable.ic_device_lock)), TuplesKt.m104to(21, Integer.valueOf(R$drawable.ic_device_switch)), TuplesKt.m104to(15, Integer.valueOf(R$drawable.ic_device_outlet)), TuplesKt.m104to(32, Integer.valueOf(R$drawable.ic_device_vacuum)), TuplesKt.m104to(26, Integer.valueOf(R$drawable.ic_device_mop)), TuplesKt.m104to(3, Integer.valueOf(i3)), TuplesKt.m104to(4, Integer.valueOf(R$drawable.ic_device_air_purifier)), TuplesKt.m104to(8, Integer.valueOf(R$drawable.ic_device_fan)), TuplesKt.m104to(10, Integer.valueOf(R$drawable.ic_device_hood)), TuplesKt.m104to(12, Integer.valueOf(i4)), TuplesKt.m104to(14, Integer.valueOf(R$drawable.ic_device_microwave)), TuplesKt.m104to(17, Integer.valueOf(R$drawable.ic_device_remote_control)), TuplesKt.m104to(18, Integer.valueOf(R$drawable.ic_device_set_top)), TuplesKt.m104to(20, Integer.valueOf(R$drawable.ic_device_styler)), TuplesKt.m104to(22, Integer.valueOf(R$drawable.ic_device_tv)), TuplesKt.m104to(23, Integer.valueOf(R$drawable.ic_device_water_heater)), TuplesKt.m104to(24, Integer.valueOf(R$drawable.ic_device_dishwasher)), TuplesKt.m104to(28, Integer.valueOf(R$drawable.ic_device_multicooker)), TuplesKt.m104to(30, Integer.valueOf(R$drawable.ic_device_sprinkler)), TuplesKt.m104to(31, Integer.valueOf(i5)), TuplesKt.m104to(34, Integer.valueOf(i6)), TuplesKt.m104to(38, Integer.valueOf(i7)), TuplesKt.m104to(39, Integer.valueOf(R$drawable.ic_device_garage)), TuplesKt.m104to(40, Integer.valueOf(R$drawable.ic_device_gate)), TuplesKt.m104to(41, Integer.valueOf(i8)), TuplesKt.m104to(43, Integer.valueOf(i9)), TuplesKt.m104to(44, Integer.valueOf(R$drawable.ic_device_valve)), TuplesKt.m104to(46, Integer.valueOf(R$drawable.ic_device_security_system)), TuplesKt.m104to(48, Integer.valueOf(R$drawable.ic_device_refrigerator)), TuplesKt.m104to(51, Integer.valueOf(R$drawable.ic_device_doorbell)), TuplesKt.m104to(52, -1), TuplesKt.m104to(1, Integer.valueOf(i2)), TuplesKt.m104to(2, Integer.valueOf(i2)), TuplesKt.m104to(5, Integer.valueOf(i4)), TuplesKt.m104to(6, Integer.valueOf(i3)), TuplesKt.m104to(16, Integer.valueOf(i2)), TuplesKt.m104to(19, Integer.valueOf(R$drawable.ic_device_cooking)), TuplesKt.m104to(7, Integer.valueOf(R$drawable.ic_device_display)), TuplesKt.m104to(25, Integer.valueOf(i5)), TuplesKt.m104to(27, Integer.valueOf(R$drawable.ic_device_outdoor_garden)), TuplesKt.m104to(29, Integer.valueOf(R$drawable.ic_device_water)), TuplesKt.m104to(33, Integer.valueOf(i8)), TuplesKt.m104to(35, Integer.valueOf(i7)), TuplesKt.m104to(36, Integer.valueOf(i6)), TuplesKt.m104to(37, Integer.valueOf(R$drawable.ic_device_door)), TuplesKt.m104to(42, Integer.valueOf(i9)), TuplesKt.m104to(47, Integer.valueOf(i2)), TuplesKt.m104to(-1000, Integer.valueOf(R$drawable.ic_error_outline))), RenderInfoKt$deviceIconMap$1.INSTANCE);
    }
}
