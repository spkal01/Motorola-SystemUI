package com.android.systemui.keyguard;

import android.database.ContentObserver;
import android.os.Handler;

/* compiled from: FaceAuthScreenBrightnessController.kt */
public final class FaceAuthScreenBrightnessController$attach$1 extends ContentObserver {
    final /* synthetic */ FaceAuthScreenBrightnessController this$0;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    FaceAuthScreenBrightnessController$attach$1(FaceAuthScreenBrightnessController faceAuthScreenBrightnessController, Handler handler) {
        super(handler);
        this.this$0 = faceAuthScreenBrightnessController;
    }

    public void onChange(boolean z) {
        FaceAuthScreenBrightnessController faceAuthScreenBrightnessController = this.this$0;
        faceAuthScreenBrightnessController.userDefinedBrightness = faceAuthScreenBrightnessController.systemSettings.getFloat("screen_brightness_float");
    }
}
