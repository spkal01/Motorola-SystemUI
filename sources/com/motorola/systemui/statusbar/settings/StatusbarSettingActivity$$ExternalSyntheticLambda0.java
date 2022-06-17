package com.motorola.systemui.statusbar.settings;

import com.android.systemui.fragments.FragmentService;
import java.util.function.Consumer;

public final /* synthetic */ class StatusbarSettingActivity$$ExternalSyntheticLambda0 implements Consumer {
    public static final /* synthetic */ StatusbarSettingActivity$$ExternalSyntheticLambda0 INSTANCE = new StatusbarSettingActivity$$ExternalSyntheticLambda0();

    private /* synthetic */ StatusbarSettingActivity$$ExternalSyntheticLambda0() {
    }

    public final void accept(Object obj) {
        ((FragmentService) obj).destroyAll();
    }
}
