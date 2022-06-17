package com.motorola.systemui.desktop.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import com.android.systemui.R$dimen;

public class DesktopQSPanelLayout extends FrameLayout {
    private int mOutlineRadius;
    private int mRealHeight = 0;

    public DesktopQSPanelLayout(Context context) {
        super(context);
    }

    public DesktopQSPanelLayout(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public DesktopQSPanelLayout(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    public DesktopQSPanelLayout(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
    }

    public void onFinishInflate() {
        super.onFinishInflate();
        setVerticalScrollBarEnabled(false);
        setWillNotDraw(false);
        this.mOutlineRadius = getResources().getDimensionPixelSize(R$dimen.desktop_qs_notification_panel_radius);
    }

    public void setRealHeight(int i) {
        this.mRealHeight = i;
    }

    public void draw(Canvas canvas) {
        int i = this.mRealHeight;
        Path path = new Path();
        RectF rectF = new RectF(0.0f, (float) getScrollY(), (float) getWidth(), ((float) i) + ((float) getScrollY()));
        int i2 = this.mOutlineRadius;
        path.addRoundRect(rectF, (float) i2, (float) i2, Path.Direction.CW);
        canvas.save();
        canvas.clipPath(path);
        super.draw(canvas);
        canvas.restore();
    }
}
