package com.android.p011wm.shell.bubbles;

import android.graphics.Rect;
import android.view.View;
import android.widget.Button;
import com.android.p011wm.shell.C2219R;
import com.android.p011wm.shell.animation.Interpolators;

/* renamed from: com.android.wm.shell.bubbles.ManageEducationView$show$1 */
/* compiled from: ManageEducationView.kt */
final class ManageEducationView$show$1 implements Runnable {
    final /* synthetic */ BubbleExpandedView $expandedView;
    final /* synthetic */ Rect $rect;
    final /* synthetic */ ManageEducationView this$0;

    ManageEducationView$show$1(BubbleExpandedView bubbleExpandedView, Rect rect, ManageEducationView manageEducationView) {
        this.$expandedView = bubbleExpandedView;
        this.$rect = rect;
        this.this$0 = manageEducationView;
    }

    public final void run() {
        this.$expandedView.getManageButtonBoundsOnScreen(this.$rect);
        Button access$getManageButton = this.this$0.getManageButton();
        final BubbleExpandedView bubbleExpandedView = this.$expandedView;
        final ManageEducationView manageEducationView = this.this$0;
        access$getManageButton.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                bubbleExpandedView.findViewById(C2219R.C2222id.settings_button).performClick();
                manageEducationView.hide(true);
            }
        });
        Button access$getGotItButton = this.this$0.getGotItButton();
        final ManageEducationView manageEducationView2 = this.this$0;
        access$getGotItButton.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                manageEducationView2.hide(true);
            }
        });
        final ManageEducationView manageEducationView3 = this.this$0;
        manageEducationView3.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                manageEducationView3.hide(true);
            }
        });
        View access$getManageView = this.this$0.getManageView();
        Rect rect = this.$rect;
        ManageEducationView manageEducationView4 = this.this$0;
        access$getManageView.setTranslationX(0.0f);
        access$getManageView.setTranslationY((float) ((rect.top - manageEducationView4.getManageView().getHeight()) + access$getManageView.getResources().getDimensionPixelSize(C2219R.dimen.bubbles_manage_education_top_inset)));
        this.this$0.bringToFront();
        this.this$0.animate().setDuration(this.this$0.ANIMATE_DURATION).setInterpolator(Interpolators.FAST_OUT_SLOW_IN).alpha(1.0f);
    }
}
