package com.android.systemui.statusbar.notification.collection.render;

import com.android.systemui.statusbar.notification.collection.ListEntry;
import com.android.systemui.statusbar.notification.collection.ShadeListBuilder;
import java.util.List;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

/* compiled from: ShadeViewManager.kt */
/* synthetic */ class ShadeViewManager$attach$1 implements ShadeListBuilder.OnRenderListListener {
    final /* synthetic */ ShadeViewManager $tmp0;

    ShadeViewManager$attach$1(ShadeViewManager shadeViewManager) {
        this.$tmp0 = shadeViewManager;
    }

    public final void onRenderList(@NotNull List<? extends ListEntry> list) {
        Intrinsics.checkNotNullParameter(list, "p0");
        this.$tmp0.onNewNotifTree(list);
    }
}
