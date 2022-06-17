package com.android.systemui.p006qs.tiles;

import android.provider.DeviceConfig;
import java.util.function.Supplier;

/* renamed from: com.android.systemui.qs.tiles.MicrophoneToggleTile$$ExternalSyntheticLambda0 */
public final /* synthetic */ class MicrophoneToggleTile$$ExternalSyntheticLambda0 implements Supplier {
    public static final /* synthetic */ MicrophoneToggleTile$$ExternalSyntheticLambda0 INSTANCE = new MicrophoneToggleTile$$ExternalSyntheticLambda0();

    private /* synthetic */ MicrophoneToggleTile$$ExternalSyntheticLambda0() {
    }

    public final Object get() {
        return Boolean.valueOf(DeviceConfig.getBoolean("privacy", "mic_toggle_enabled", true));
    }
}
