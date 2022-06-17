package com.android.systemui.tuner;

import java.util.function.Consumer;

public final /* synthetic */ class ShortcutPicker$$ExternalSyntheticLambda2 implements Consumer {
    public final /* synthetic */ String f$0;

    public /* synthetic */ ShortcutPicker$$ExternalSyntheticLambda2(String str) {
        this.f$0 = str;
    }

    public final void accept(Object obj) {
        ((SelectablePreference) obj).setChecked(this.f$0.equals(((SelectablePreference) obj).toString()));
    }
}
