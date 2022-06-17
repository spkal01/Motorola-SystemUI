package com.android.systemui.statusbar.notification.collection.render;

import android.view.View;
import android.view.ViewGroup;
import com.android.systemui.statusbar.notification.stack.SectionHeaderView;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/* compiled from: SectionHeaderController.kt */
public interface SectionHeaderController {
    @Nullable
    SectionHeaderView getHeaderView();

    void reinflateView(@NotNull ViewGroup viewGroup);

    void setOnClearAllClickListener(@NotNull View.OnClickListener onClickListener);
}
