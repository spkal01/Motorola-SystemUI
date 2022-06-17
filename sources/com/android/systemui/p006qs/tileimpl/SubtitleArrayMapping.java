package com.android.systemui.p006qs.tileimpl;

import com.android.systemui.R$array;
import java.util.Map;
import kotlin.TuplesKt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/* renamed from: com.android.systemui.qs.tileimpl.SubtitleArrayMapping */
/* compiled from: QSTileViewImpl.kt */
public final class SubtitleArrayMapping {
    @NotNull
    public static final SubtitleArrayMapping INSTANCE = new SubtitleArrayMapping();
    @NotNull
    private static final Map<String, Integer> subtitleIdsMap = MapsKt__MapsKt.mapOf(TuplesKt.m104to("internet", Integer.valueOf(R$array.tile_states_internet)), TuplesKt.m104to("wifi", Integer.valueOf(R$array.tile_states_wifi)), TuplesKt.m104to("cell", Integer.valueOf(R$array.tile_states_cell)), TuplesKt.m104to("battery", Integer.valueOf(R$array.tile_states_battery)), TuplesKt.m104to("dnd", Integer.valueOf(R$array.tile_states_dnd)), TuplesKt.m104to("flashlight", Integer.valueOf(R$array.tile_states_flashlight)), TuplesKt.m104to("rotation", Integer.valueOf(R$array.tile_states_rotation)), TuplesKt.m104to("bt", Integer.valueOf(R$array.tile_states_bt)), TuplesKt.m104to("airplane", Integer.valueOf(R$array.tile_states_airplane)), TuplesKt.m104to("location", Integer.valueOf(R$array.tile_states_location)), TuplesKt.m104to("hotspot", Integer.valueOf(R$array.tile_states_hotspot)), TuplesKt.m104to("inversion", Integer.valueOf(R$array.tile_states_inversion)), TuplesKt.m104to("saver", Integer.valueOf(R$array.tile_states_saver)), TuplesKt.m104to("dark", Integer.valueOf(R$array.tile_states_dark)), TuplesKt.m104to("work", Integer.valueOf(R$array.tile_states_work)), TuplesKt.m104to("cast", Integer.valueOf(R$array.tile_states_cast)), TuplesKt.m104to("night", Integer.valueOf(R$array.tile_states_night)), TuplesKt.m104to("screenrecord", Integer.valueOf(R$array.tile_states_screenrecord)), TuplesKt.m104to("reverse", Integer.valueOf(R$array.tile_states_reverse)), TuplesKt.m104to("reduce_brightness", Integer.valueOf(R$array.tile_states_reduce_brightness)), TuplesKt.m104to("cameratoggle", Integer.valueOf(R$array.tile_states_cameratoggle)), TuplesKt.m104to("mictoggle", Integer.valueOf(R$array.tile_states_mictoggle)), TuplesKt.m104to("controls", Integer.valueOf(R$array.tile_states_controls)), TuplesKt.m104to("wallet", Integer.valueOf(R$array.tile_states_wallet)), TuplesKt.m104to("alarm", Integer.valueOf(R$array.tile_states_alarm)));

    private SubtitleArrayMapping() {
    }

    public final int getSubtitleId(@Nullable String str) {
        return subtitleIdsMap.getOrDefault(str, Integer.valueOf(R$array.tile_states_default)).intValue();
    }
}
