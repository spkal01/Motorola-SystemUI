package com.android.p011wm.shell.bubbles;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.android.internal.util.ContrastColorUtil;
import com.android.p011wm.shell.C2219R;
import kotlin.Lazy;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

/* renamed from: com.android.wm.shell.bubbles.ManageEducationView */
/* compiled from: ManageEducationView.kt */
public final class ManageEducationView extends LinearLayout {
    /* access modifiers changed from: private */
    public final long ANIMATE_DURATION = 200;
    private final long ANIMATE_DURATION_SHORT = 40;
    @NotNull
    private final String TAG = "Bubbles";
    @NotNull
    private final Lazy descTextView$delegate = LazyKt__LazyJVMKt.lazy(new ManageEducationView$descTextView$2(this));
    @NotNull
    private final Lazy gotItButton$delegate = LazyKt__LazyJVMKt.lazy(new ManageEducationView$gotItButton$2(this));
    /* access modifiers changed from: private */
    public boolean isHiding;
    @NotNull
    private final Lazy manageButton$delegate = LazyKt__LazyJVMKt.lazy(new ManageEducationView$manageButton$2(this));
    @NotNull
    private final Lazy manageView$delegate = LazyKt__LazyJVMKt.lazy(new ManageEducationView$manageView$2(this));
    @NotNull
    private final Lazy titleTextView$delegate = LazyKt__LazyJVMKt.lazy(new ManageEducationView$titleTextView$2(this));

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public ManageEducationView(@NotNull Context context) {
        super(context);
        Intrinsics.checkNotNullParameter(context, "context");
        LayoutInflater.from(context).inflate(C2219R.layout.bubbles_manage_button_education, this);
        setVisibility(8);
        setElevation((float) getResources().getDimensionPixelSize(C2219R.dimen.bubble_elevation));
        setLayoutDirection(3);
    }

    /* access modifiers changed from: private */
    public final View getManageView() {
        return (View) this.manageView$delegate.getValue();
    }

    /* access modifiers changed from: private */
    public final Button getManageButton() {
        return (Button) this.manageButton$delegate.getValue();
    }

    /* access modifiers changed from: private */
    public final Button getGotItButton() {
        return (Button) this.gotItButton$delegate.getValue();
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
        View manageView = getManageView();
        if (getResources().getConfiguration().getLayoutDirection() == 1) {
            i = C2219R.C2221drawable.bubble_stack_user_education_bg_rtl;
        } else {
            i = C2219R.C2221drawable.bubble_stack_user_education_bg;
        }
        manageView.setBackgroundResource(i);
    }

    public final void show(@NotNull BubbleExpandedView bubbleExpandedView, @NotNull Rect rect) {
        Intrinsics.checkNotNullParameter(bubbleExpandedView, "expandedView");
        Intrinsics.checkNotNullParameter(rect, "rect");
        if (getVisibility() != 0) {
            setAlpha(0.0f);
            setVisibility(0);
            post(new ManageEducationView$show$1(bubbleExpandedView, rect, this));
            setShouldShow(false);
        }
    }

    public final void hide(boolean z) {
        if (getVisibility() == 0 && !this.isHiding) {
            animate().withStartAction(new ManageEducationView$hide$1(this)).alpha(0.0f).setDuration(z ? this.ANIMATE_DURATION_SHORT : this.ANIMATE_DURATION).withEndAction(new ManageEducationView$hide$2(this));
        }
    }

    private final void setShouldShow(boolean z) {
        getContext().getSharedPreferences(getContext().getPackageName(), 0).edit().putBoolean("HasSeenBubblesManageOnboarding", !z).apply();
    }
}
