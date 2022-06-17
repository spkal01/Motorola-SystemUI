package com.android.systemui.controls;

import android.view.animation.DecelerateInterpolator;

/* compiled from: TooltipManager.kt */
final class TooltipManager$show$1 implements Runnable {

    /* renamed from: $x */
    final /* synthetic */ int f80$x;

    /* renamed from: $y */
    final /* synthetic */ int f81$y;
    final /* synthetic */ TooltipManager this$0;

    TooltipManager$show$1(TooltipManager tooltipManager, int i, int i2) {
        this.this$0 = tooltipManager;
        this.f80$x = i;
        this.f81$y = i2;
    }

    public final void run() {
        int[] iArr = new int[2];
        this.this$0.getLayout().getLocationOnScreen(iArr);
        boolean z = false;
        this.this$0.getLayout().setTranslationX((float) ((this.f80$x - iArr[0]) - (this.this$0.getLayout().getWidth() / 2)));
        this.this$0.getLayout().setTranslationY(((float) (this.f81$y - iArr[1])) - ((float) (!this.this$0.below ? this.this$0.getLayout().getHeight() : 0)));
        if (this.this$0.getLayout().getAlpha() == 0.0f) {
            z = true;
        }
        if (z) {
            this.this$0.getLayout().animate().alpha(1.0f).withLayer().setStartDelay(500).setDuration(300).setInterpolator(new DecelerateInterpolator()).start();
        }
    }
}
