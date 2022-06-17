package com.android.systemui.statusbar.notification.row;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import com.android.systemui.R$dimen;

public class CliMaxHeightHeadsUpView extends CliHeadsUpView {
    private int mMaxHeight;
    private int mTopMargin;

    public CliMaxHeightHeadsUpView(Context context) {
        this(context, (AttributeSet) null);
    }

    public CliMaxHeightHeadsUpView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mMaxHeight = 0;
        this.mTopMargin = 0;
    }

    public void setMaxHeight(int i) {
        this.mMaxHeight = i;
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        super.onFinishInflate();
        this.mTopMargin = getResources().getDimensionPixelSize(R$dimen.cli_headup_notification_top_pad);
        setElevation((float) getResources().getDimensionPixelSize(R$dimen.fake_shadow_size));
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        boolean z;
        int size = View.MeasureSpec.getSize(i2);
        int childCount = getChildCount();
        int i3 = 0;
        for (int i4 = 0; i4 < childCount; i4++) {
            View childAt = getChildAt(i4);
            int i5 = childAt.getLayoutParams().height;
            if (i5 >= 0) {
                size = Math.min(this.mMaxHeight, i5);
                z = true;
            } else {
                z = false;
            }
            measureChildWithMargins(childAt, i, 0, View.MeasureSpec.makeMeasureSpec(size, z ? 1073741824 : Integer.MIN_VALUE), 0);
            i3 = Math.max(i3, childAt.getMeasuredHeight());
        }
        setMeasuredDimension(View.MeasureSpec.getSize(i), Math.min(i3, View.MeasureSpec.getSize(i2)) - calculateCliBottomMargin());
    }

    /* JADX WARNING: Code restructure failed: missing block: B:12:0x0031, code lost:
        r0 = ((android.view.ViewGroup.MarginLayoutParams) r0.getLayoutParams()).topMargin;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private int calculateCliBottomMargin() {
        /*
            r3 = this;
            com.android.systemui.statusbar.notification.row.NotificationContentView r0 = r3.mCliPrivateLayout
            android.view.View r0 = r0.getHeadsUpChild()
            if (r0 != 0) goto L_0x000e
            com.android.systemui.statusbar.notification.row.NotificationContentView r0 = r3.mCliPrivateLayout
            android.view.View r0 = r0.getContractedChild()
        L_0x000e:
            r1 = 0
            if (r0 == 0) goto L_0x003f
            r2 = 16909234(0x10203b2, float:2.387988E-38)
            android.view.View r2 = r0.findViewById(r2)
            if (r2 == 0) goto L_0x0028
            android.view.ViewGroup$LayoutParams r2 = r2.getLayoutParams()
            android.view.ViewGroup$MarginLayoutParams r2 = (android.view.ViewGroup.MarginLayoutParams) r2
            int r2 = r2.topMargin
            if (r2 <= 0) goto L_0x0028
            int r1 = r3.mTopMargin
            int r1 = r2 - r1
        L_0x0028:
            r2 = 16909239(0x10203b7, float:2.3879894E-38)
            android.view.View r0 = r0.findViewById(r2)
            if (r0 == 0) goto L_0x003f
            android.view.ViewGroup$LayoutParams r0 = r0.getLayoutParams()
            android.view.ViewGroup$MarginLayoutParams r0 = (android.view.ViewGroup.MarginLayoutParams) r0
            int r0 = r0.topMargin
            if (r0 <= 0) goto L_0x003f
            int r3 = r3.mTopMargin
            int r1 = r0 - r3
        L_0x003f:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.statusbar.notification.row.CliMaxHeightHeadsUpView.calculateCliBottomMargin():int");
    }
}
