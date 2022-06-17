package com.android.systemui.doze;

public final /* synthetic */ class DozeTriggers$$ExternalSyntheticLambda3 implements Runnable {
    public final /* synthetic */ DozeTriggers f$0;
    public final /* synthetic */ float f$1;
    public final /* synthetic */ float f$2;
    public final /* synthetic */ float[] f$3;

    public /* synthetic */ DozeTriggers$$ExternalSyntheticLambda3(DozeTriggers dozeTriggers, float f, float f2, float[] fArr) {
        this.f$0 = dozeTriggers;
        this.f$1 = f;
        this.f$2 = f2;
        this.f$3 = fArr;
    }

    public final void run() {
        this.f$0.lambda$onSensor$1(this.f$1, this.f$2, this.f$3);
    }
}
