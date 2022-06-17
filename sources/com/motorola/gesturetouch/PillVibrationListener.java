package com.motorola.gesturetouch;

import android.content.Context;
import android.os.VibrationEffect;
import android.os.Vibrator;

class PillVibrationListener {
    private Context mContext;
    private EdgeTouchPillController mEdgeTouchPillController;

    public PillVibrationListener(Context context, EdgeTouchPillController edgeTouchPillController) {
        this.mContext = context;
        this.mEdgeTouchPillController = edgeTouchPillController;
    }

    public void excuteAction(int i) {
        vibrate();
    }

    public void vibrate() {
        ((Vibrator) this.mContext.getSystemService("vibrator")).vibrate(VibrationEffect.createPredefined(0));
    }

    public void onUserSwitch(Context context) {
        this.mContext = context;
    }
}
