package com.android.systemui.statusbar.phone;

import com.android.systemui.util.AlarmTimeout;

public final /* synthetic */ class ScrimController$$ExternalSyntheticLambda7 implements Runnable {
    public final /* synthetic */ AlarmTimeout f$0;

    public /* synthetic */ ScrimController$$ExternalSyntheticLambda7(AlarmTimeout alarmTimeout) {
        this.f$0 = alarmTimeout;
    }

    public final void run() {
        this.f$0.cancel();
    }
}
