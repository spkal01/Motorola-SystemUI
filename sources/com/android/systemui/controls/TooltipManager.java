package com.android.systemui.controls;

import android.content.Context;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.drawable.ShapeDrawable;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.android.systemui.Prefs;
import com.android.systemui.R$dimen;
import com.android.systemui.R$id;
import com.android.systemui.R$layout;
import com.android.systemui.recents.TriangleShape;
import java.util.Objects;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

/* compiled from: TooltipManager.kt */
public final class TooltipManager {
    @NotNull
    public static final Companion Companion = new Companion((DefaultConstructorMarker) null);
    private final View arrowView;
    /* access modifiers changed from: private */
    public final boolean below;
    private final View dismissView;
    @NotNull
    private final ViewGroup layout;
    private final int maxTimesShown;
    /* access modifiers changed from: private */
    @NotNull
    public final String preferenceName;
    @NotNull
    private final Function1<Integer, Unit> preferenceStorer;
    private int shown;
    private final TextView textView;

    public TooltipManager(@NotNull Context context, @NotNull String str, int i, boolean z) {
        Intrinsics.checkNotNullParameter(context, "context");
        Intrinsics.checkNotNullParameter(str, "preferenceName");
        this.preferenceName = str;
        this.maxTimesShown = i;
        this.below = z;
        this.shown = Prefs.getInt(context, str, 0);
        View inflate = LayoutInflater.from(context).inflate(R$layout.controls_onboarding, (ViewGroup) null);
        Objects.requireNonNull(inflate, "null cannot be cast to non-null type android.view.ViewGroup");
        ViewGroup viewGroup = (ViewGroup) inflate;
        this.layout = viewGroup;
        this.preferenceStorer = new TooltipManager$preferenceStorer$1(context, this);
        viewGroup.setAlpha(0.0f);
        this.textView = (TextView) viewGroup.requireViewById(R$id.onboarding_text);
        View requireViewById = viewGroup.requireViewById(R$id.dismiss);
        requireViewById.setOnClickListener(new TooltipManager$dismissView$1$1(this));
        Unit unit = Unit.INSTANCE;
        this.dismissView = requireViewById;
        View requireViewById2 = viewGroup.requireViewById(R$id.arrow);
        TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(16843829, typedValue, true);
        int color = context.getResources().getColor(typedValue.resourceId, context.getTheme());
        int dimensionPixelSize = context.getResources().getDimensionPixelSize(R$dimen.recents_onboarding_toast_arrow_corner_radius);
        ViewGroup.LayoutParams layoutParams = requireViewById2.getLayoutParams();
        ShapeDrawable shapeDrawable = new ShapeDrawable(TriangleShape.create((float) layoutParams.width, (float) layoutParams.height, z));
        Paint paint = shapeDrawable.getPaint();
        paint.setColor(color);
        paint.setPathEffect(new CornerPathEffect((float) dimensionPixelSize));
        requireViewById2.setBackground(shapeDrawable);
        this.arrowView = requireViewById2;
        if (!z) {
            viewGroup.removeView(requireViewById2);
            viewGroup.addView(requireViewById2);
            ViewGroup.LayoutParams layoutParams2 = requireViewById2.getLayoutParams();
            Objects.requireNonNull(layoutParams2, "null cannot be cast to non-null type android.view.ViewGroup.MarginLayoutParams");
            ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) layoutParams2;
            marginLayoutParams.bottomMargin = marginLayoutParams.topMargin;
            marginLayoutParams.topMargin = 0;
        }
    }

    /* JADX INFO: this call moved to the top of the method (can break code semantics) */
    public /* synthetic */ TooltipManager(Context context, String str, int i, boolean z, int i2, DefaultConstructorMarker defaultConstructorMarker) {
        this(context, str, (i2 & 4) != 0 ? 2 : i, (i2 & 8) != 0 ? true : z);
    }

    /* compiled from: TooltipManager.kt */
    public static final class Companion {
        public /* synthetic */ Companion(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }

        private Companion() {
        }
    }

    @NotNull
    public final ViewGroup getLayout() {
        return this.layout;
    }

    public final void show(int i, int i2, int i3) {
        if (shouldShow()) {
            this.textView.setText(i);
            int i4 = this.shown + 1;
            this.shown = i4;
            this.preferenceStorer.invoke(Integer.valueOf(i4));
            this.layout.post(new TooltipManager$show$1(this, i2, i3));
        }
    }

    public final void hide(boolean z) {
        if (!(this.layout.getAlpha() == 0.0f)) {
            this.layout.post(new TooltipManager$hide$1(z, this));
        }
    }

    private final boolean shouldShow() {
        return this.shown < this.maxTimesShown;
    }
}
