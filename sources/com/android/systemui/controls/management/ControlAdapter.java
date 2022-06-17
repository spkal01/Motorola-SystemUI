package com.android.systemui.controls.management;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.systemui.R$drawable;
import com.android.systemui.R$layout;
import com.android.systemui.controls.ControlInterface;
import java.util.List;
import java.util.Objects;
import kotlin.NoWhenBranchMatchedException;
import kotlin.Unit;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/* compiled from: ControlAdapter.kt */
public final class ControlAdapter extends RecyclerView.Adapter<Holder> {
    @NotNull
    public static final Companion Companion = new Companion((DefaultConstructorMarker) null);
    private final float elevation;
    /* access modifiers changed from: private */
    @Nullable
    public ControlsModel model;
    @NotNull
    private final GridLayoutManager.SpanSizeLookup spanSizeLookup = new ControlAdapter$spanSizeLookup$1(this);

    public ControlAdapter(float f) {
        this.elevation = f;
    }

    /* compiled from: ControlAdapter.kt */
    public static final class Companion {
        public /* synthetic */ Companion(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }

        private Companion() {
        }
    }

    @NotNull
    public final GridLayoutManager.SpanSizeLookup getSpanSizeLookup() {
        return this.spanSizeLookup;
    }

    @NotNull
    public Holder onCreateViewHolder(@NotNull ViewGroup viewGroup, int i) {
        Intrinsics.checkNotNullParameter(viewGroup, "parent");
        LayoutInflater from = LayoutInflater.from(viewGroup.getContext());
        if (i == 0) {
            View inflate = from.inflate(R$layout.controls_zone_header, viewGroup, false);
            Intrinsics.checkNotNullExpressionValue(inflate, "layoutInflater.inflate(R.layout.controls_zone_header, parent, false)");
            return new ZoneHolder(inflate);
        } else if (i == 1) {
            View inflate2 = from.inflate(R$layout.controls_base_item, viewGroup, false);
            ViewGroup.LayoutParams layoutParams = inflate2.getLayoutParams();
            Objects.requireNonNull(layoutParams, "null cannot be cast to non-null type android.view.ViewGroup.MarginLayoutParams");
            ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) layoutParams;
            marginLayoutParams.width = -1;
            marginLayoutParams.topMargin = 0;
            marginLayoutParams.bottomMargin = 0;
            marginLayoutParams.leftMargin = 0;
            marginLayoutParams.rightMargin = 0;
            inflate2.setElevation(this.elevation);
            inflate2.setBackground(viewGroup.getContext().getDrawable(R$drawable.control_background_ripple));
            Unit unit = Unit.INSTANCE;
            Intrinsics.checkNotNullExpressionValue(inflate2, "layoutInflater.inflate(R.layout.controls_base_item, parent, false).apply {\n                        (layoutParams as ViewGroup.MarginLayoutParams).apply {\n                            width = ViewGroup.LayoutParams.MATCH_PARENT\n                            // Reset margins as they will be set through the decoration\n                            topMargin = 0\n                            bottomMargin = 0\n                            leftMargin = 0\n                            rightMargin = 0\n                        }\n                        elevation = this@ControlAdapter.elevation\n                        background = parent.context.getDrawable(\n                                R.drawable.control_background_ripple)\n                    }");
            ControlsModel controlsModel = this.model;
            return new ControlHolder(inflate2, controlsModel == null ? null : controlsModel.getMoveHelper(), new ControlAdapter$onCreateViewHolder$2(this));
        } else if (i == 2) {
            View inflate3 = from.inflate(R$layout.controls_horizontal_divider_with_empty, viewGroup, false);
            Intrinsics.checkNotNullExpressionValue(inflate3, "layoutInflater.inflate(\n                        R.layout.controls_horizontal_divider_with_empty, parent, false)");
            return new DividerHolder(inflate3);
        } else {
            throw new IllegalStateException(Intrinsics.stringPlus("Wrong viewType: ", Integer.valueOf(i)));
        }
    }

    public final void changeModel(@NotNull ControlsModel controlsModel) {
        Intrinsics.checkNotNullParameter(controlsModel, "model");
        this.model = controlsModel;
        notifyDataSetChanged();
    }

    public int getItemCount() {
        ControlsModel controlsModel = this.model;
        List<ElementWrapper> elements = controlsModel == null ? null : controlsModel.getElements();
        if (elements == null) {
            return 0;
        }
        return elements.size();
    }

    public void onBindViewHolder(@NotNull Holder holder, int i) {
        Intrinsics.checkNotNullParameter(holder, "holder");
        ControlsModel controlsModel = this.model;
        if (controlsModel != null) {
            holder.bindData(controlsModel.getElements().get(i));
        }
    }

    public void onBindViewHolder(@NotNull Holder holder, int i, @NotNull List<Object> list) {
        Intrinsics.checkNotNullParameter(holder, "holder");
        Intrinsics.checkNotNullParameter(list, "payloads");
        if (list.isEmpty()) {
            super.onBindViewHolder(holder, i, list);
            return;
        }
        ControlsModel controlsModel = this.model;
        if (controlsModel != null) {
            ElementWrapper elementWrapper = controlsModel.getElements().get(i);
            if (elementWrapper instanceof ControlInterface) {
                holder.updateFavorite(((ControlInterface) elementWrapper).getFavorite());
            }
        }
    }

    public int getItemViewType(int i) {
        ControlsModel controlsModel = this.model;
        if (controlsModel != null) {
            ElementWrapper elementWrapper = controlsModel.getElements().get(i);
            if (elementWrapper instanceof ZoneNameWrapper) {
                return 0;
            }
            if ((elementWrapper instanceof ControlStatusWrapper) || (elementWrapper instanceof ControlInfoWrapper)) {
                return 1;
            }
            if (elementWrapper instanceof DividerWrapper) {
                return 2;
            }
            throw new NoWhenBranchMatchedException();
        }
        throw new IllegalStateException("Getting item type for null model");
    }
}
