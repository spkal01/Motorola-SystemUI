package com.android.systemui.controls.p004ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.systemui.R$id;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/* renamed from: com.android.systemui.controls.ui.ItemAdapter */
/* compiled from: ControlsUiControllerImpl.kt */
final class ItemAdapter extends ArrayAdapter<SelectionItem> {
    private final LayoutInflater layoutInflater = LayoutInflater.from(getContext());
    @NotNull
    private final Context parentContext;
    private final int resource;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public ItemAdapter(@NotNull Context context, int i) {
        super(context, i);
        Intrinsics.checkNotNullParameter(context, "parentContext");
        this.parentContext = context;
        this.resource = i;
    }

    @NotNull
    public View getView(int i, @Nullable View view, @NotNull ViewGroup viewGroup) {
        Intrinsics.checkNotNullParameter(viewGroup, "parent");
        SelectionItem selectionItem = (SelectionItem) getItem(i);
        if (view == null) {
            view = this.layoutInflater.inflate(this.resource, viewGroup, false);
        }
        ((TextView) view.requireViewById(R$id.controls_spinner_item)).setText(selectionItem.getTitle());
        ((ImageView) view.requireViewById(R$id.app_icon)).setImageDrawable(selectionItem.getIcon());
        Intrinsics.checkNotNullExpressionValue(view, "view");
        return view;
    }
}
