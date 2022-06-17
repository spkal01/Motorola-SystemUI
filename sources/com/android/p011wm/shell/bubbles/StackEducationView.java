package com.android.p011wm.shell.bubbles;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.PointF;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.android.internal.util.ContrastColorUtil;
import com.android.p011wm.shell.C2219R;
import kotlin.Lazy;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

/* renamed from: com.android.wm.shell.bubbles.StackEducationView */
/* compiled from: StackEducationView.kt */
public final class StackEducationView extends LinearLayout {
    /* access modifiers changed from: private */
    public final long ANIMATE_DURATION = 200;
    private final long ANIMATE_DURATION_SHORT = 40;
    @NotNull
    private final String TAG = "Bubbles";
    @NotNull
    private final Lazy descTextView$delegate = LazyKt__LazyJVMKt.lazy(new StackEducationView$descTextView$2(this));
    private boolean isHiding;
    @NotNull
    private final Lazy titleTextView$delegate = LazyKt__LazyJVMKt.lazy(new StackEducationView$titleTextView$2(this));
    @NotNull
    private final Lazy view$delegate = LazyKt__LazyJVMKt.lazy(new StackEducationView$view$2(this));

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public StackEducationView(@NotNull Context context) {
        super(context);
        Intrinsics.checkNotNullParameter(context, "context");
        LayoutInflater.from(context).inflate(C2219R.layout.bubble_stack_user_education, this);
        setVisibility(8);
        setElevation((float) getResources().getDimensionPixelSize(C2219R.dimen.bubble_elevation));
        setLayoutDirection(3);
    }

    /* access modifiers changed from: private */
    public final View getView() {
        return (View) this.view$delegate.getValue();
    }

    private final TextView getTitleTextView() {
        return (TextView) this.titleTextView$delegate.getValue();
    }

    private final TextView getDescTextView() {
        return (TextView) this.descTextView$delegate.getValue();
    }

    public void setLayoutDirection(int i) {
        super.setLayoutDirection(i);
        setDrawableDirection();
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        super.onFinishInflate();
        setLayoutDirection(getResources().getConfiguration().getLayoutDirection());
        setTextColor();
    }

    private final void setTextColor() {
        TypedArray obtainStyledAttributes = this.mContext.obtainStyledAttributes(new int[]{16843829, 16842809});
        int color = obtainStyledAttributes.getColor(0, -16777216);
        int color2 = obtainStyledAttributes.getColor(1, -1);
        obtainStyledAttributes.recycle();
        int ensureTextContrast = ContrastColorUtil.ensureTextContrast(color2, color, true);
        getTitleTextView().setTextColor(ensureTextContrast);
        getDescTextView().setTextColor(ensureTextContrast);
    }

    private final void setDrawableDirection() {
        int i;
        View view = getView();
        if (getResources().getConfiguration().getLayoutDirection() == 0) {
            i = C2219R.C2221drawable.bubble_stack_user_education_bg;
        } else {
            i = C2219R.C2221drawable.bubble_stack_user_education_bg_rtl;
        }
        view.setBackgroundResource(i);
    }

    public final boolean show(@NotNull PointF pointF) {
        Intrinsics.checkNotNullParameter(pointF, "stackPosition");
        if (getVisibility() == 0) {
            return false;
        }
        setAlpha(0.0f);
        setVisibility(0);
        post(new StackEducationView$show$1(this, pointF));
        setShouldShow(false);
        return true;
    }

    public final void hide(boolean z) {
        if (getVisibility() == 0 && !this.isHiding) {
            animate().alpha(0.0f).setDuration(z ? this.ANIMATE_DURATION_SHORT : this.ANIMATE_DURATION).withEndAction(new StackEducationView$hide$1(this));
        }
    }

    private final void setShouldShow(boolean z) {
        getContext().getSharedPreferences(getContext().getPackageName(), 0).edit().putBoolean("HasSeenBubblesOnboarding", !z).apply();
    }
}
