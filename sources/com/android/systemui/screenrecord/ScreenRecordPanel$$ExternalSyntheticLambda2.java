package com.android.systemui.screenrecord;

import android.widget.RadioGroup;

public final /* synthetic */ class ScreenRecordPanel$$ExternalSyntheticLambda2 implements RadioGroup.OnCheckedChangeListener {
    public final /* synthetic */ ScreenRecordPanel f$0;

    public /* synthetic */ ScreenRecordPanel$$ExternalSyntheticLambda2(ScreenRecordPanel screenRecordPanel) {
        this.f$0 = screenRecordPanel;
    }

    public final void onCheckedChanged(RadioGroup radioGroup, int i) {
        this.f$0.lambda$createRecordingBarWindow$1(radioGroup, i);
    }
}
