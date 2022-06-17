package com.android.systemui.shortcut;

import com.android.p011wm.shell.legacysplitscreen.LegacySplitScreen;
import java.util.function.Consumer;

public final /* synthetic */ class ShortcutKeyDispatcher$$ExternalSyntheticLambda0 implements Consumer {
    public final /* synthetic */ ShortcutKeyDispatcher f$0;
    public final /* synthetic */ long f$1;

    public /* synthetic */ ShortcutKeyDispatcher$$ExternalSyntheticLambda0(ShortcutKeyDispatcher shortcutKeyDispatcher, long j) {
        this.f$0 = shortcutKeyDispatcher;
        this.f$1 = j;
    }

    public final void accept(Object obj) {
        this.f$0.lambda$handleDockKey$0(this.f$1, (LegacySplitScreen) obj);
    }
}
