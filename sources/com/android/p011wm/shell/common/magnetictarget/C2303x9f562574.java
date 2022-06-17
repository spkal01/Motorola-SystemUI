package com.android.p011wm.shell.common.magnetictarget;

import android.content.Context;
import android.database.ContentObserver;
import android.os.Handler;
import android.provider.Settings;
import com.android.p011wm.shell.common.magnetictarget.MagnetizedObject;

/* renamed from: com.android.wm.shell.common.magnetictarget.MagnetizedObject$Companion$initHapticSettingObserver$hapticSettingObserver$1 */
/* compiled from: MagnetizedObject.kt */
public final class C2303x9f562574 extends ContentObserver {
    final /* synthetic */ Context $context;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    C2303x9f562574(Context context, Handler handler) {
        super(handler);
        this.$context = context;
    }

    public void onChange(boolean z) {
        MagnetizedObject.Companion companion = MagnetizedObject.Companion;
        boolean z2 = false;
        if (Settings.System.getIntForUser(this.$context.getContentResolver(), "haptic_feedback_enabled", 0, -2) != 0) {
            z2 = true;
        }
        MagnetizedObject.systemHapticsEnabled = z2;
    }
}
