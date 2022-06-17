package com.motorola.systemui.desktop.widget;

import android.content.Context;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.drawable.ShapeDrawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.android.systemui.R$color;
import com.android.systemui.R$dimen;
import com.android.systemui.R$id;
import com.android.systemui.recents.TriangleShape;
import com.android.systemui.shared.system.QuickStepContract;

public class DesktopNavGuideView extends LinearLayout {
    private View mArrowView;
    private ShapeDrawable mBottomArrowDrawable;
    private TriangleDirection mDirection = TriangleDirection.BOTTOM;
    protected boolean mIsOpen;
    private final WindowManager.LayoutParams mLayoutParams;
    private ShapeDrawable mLeftArrowDrawable;
    private View mLeftArrowView;
    private ShapeDrawable mRightArrowDrawable;
    private WindowManager mWindowManager;

    private enum TriangleDirection {
        TOP,
        LEFT,
        RIGHT,
        BOTTOM
    }

    public DesktopNavGuideView(Context context, int i) {
        super(context, (AttributeSet) null, 0);
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        this.mLayoutParams = layoutParams;
        this.mWindowManager = (WindowManager) context.getSystemService("window");
        layoutParams.setTitle("DesktopNavGuideView");
        layoutParams.packageName = context.getOpPackageName();
        layoutParams.type = 2038;
        layoutParams.width = -2;
        layoutParams.height = -2;
        layoutParams.format = -3;
        layoutParams.flags = 8650760;
        layoutParams.privateFlags = 16;
        layoutParams.gravity = 8388659;
        layoutParams.setFitInsetsTypes(0);
        init(context, i);
    }

    public void close(boolean z, boolean z2) {
        if (this.mIsOpen) {
            if (z) {
                this.mWindowManager.removeView(this);
            } else {
                this.mWindowManager.removeViewImmediate(this);
            }
            this.mIsOpen = false;
        }
    }

    private void init(Context context, int i) {
        LinearLayout.inflate(context, i, this);
        setOrientation(1);
        findViewById(R$id.close).setOnClickListener(DesktopNavGuideView$$ExternalSyntheticLambda0.INSTANCE);
        this.mArrowView = findViewById(R$id.arrow);
        this.mLeftArrowView = findViewById(R$id.left_arrow);
        this.mBottomArrowDrawable = getArrowShapeDrawable(TriangleDirection.BOTTOM);
        this.mRightArrowDrawable = getArrowShapeDrawable(TriangleDirection.RIGHT);
        this.mLeftArrowDrawable = getArrowShapeDrawable(TriangleDirection.LEFT);
    }

    public DesktopNavGuideView show(View view) {
        return show("", view);
    }

    public DesktopNavGuideView show(String str, View view) {
        if (this.mIsOpen) {
            updateLayoutParams(view);
            return this;
        }
        if (!TextUtils.isEmpty(str)) {
            ((TextView) findViewById(R$id.text)).setText(str);
        }
        updateLayoutParams(view);
        try {
            this.mWindowManager.addView(this, this.mLayoutParams);
            this.mIsOpen = true;
        } catch (WindowManager.InvalidDisplayException e) {
            e.printStackTrace();
        }
        return this;
    }

    private void updateLayoutParams(View view) {
        if (this.mLayoutParams != null && view != null) {
            int[] iArr = new int[2];
            view.getLocationOnScreen(iArr);
            int i = iArr[0];
            int i2 = iArr[1];
            int currentRotation = getCurrentRotation();
            this.mDirection = getTriangleDirection();
            updateArrowView();
            TriangleDirection triangleDirection = this.mDirection;
            TriangleDirection triangleDirection2 = TriangleDirection.RIGHT;
            setOrientation(triangleDirection == triangleDirection2 ? 0 : 1);
            int dimensionPixelSize = getResources().getDimensionPixelSize(17105535);
            int dimensionPixelSize2 = getResources().getDimensionPixelSize(17105367);
            requestLayout();
            int makeMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, 0);
            measure(makeMeasureSpec, makeMeasureSpec);
            int measuredHeight = getMeasuredHeight();
            int measuredWidth = getMeasuredWidth();
            int dimensionPixelSize3 = getContext().getResources().getDimensionPixelSize(R$dimen.trackpad_guide_tip_arrow_width);
            TriangleDirection triangleDirection3 = this.mDirection;
            TriangleDirection triangleDirection4 = TriangleDirection.BOTTOM;
            if (triangleDirection3 == triangleDirection4) {
                int width = i + (view.getWidth() / 2);
                if (currentRotation == 0 || currentRotation == 3) {
                    this.mLayoutParams.x = width - dimensionPixelSize3;
                } else if (currentRotation == 1) {
                    this.mLayoutParams.x = (width - dimensionPixelSize3) - dimensionPixelSize;
                }
            } else if (triangleDirection3 != triangleDirection2) {
                this.mLayoutParams.x = i;
            } else if (currentRotation == 1) {
                this.mLayoutParams.x = (i - measuredWidth) - dimensionPixelSize2;
            } else {
                this.mLayoutParams.x = i + dimensionPixelSize2;
            }
            TriangleDirection triangleDirection5 = this.mDirection;
            if (triangleDirection5 == triangleDirection4) {
                if (currentRotation == 0) {
                    this.mLayoutParams.y = (i2 - measuredHeight) - dimensionPixelSize;
                } else if (currentRotation == 1 || currentRotation == 3) {
                    this.mLayoutParams.y = i2 - measuredHeight;
                }
            } else if (triangleDirection5 == triangleDirection2) {
                int height = i2 + (view.getHeight() / 2);
                if (currentRotation == 1) {
                    this.mLayoutParams.y = (height - measuredHeight) + dimensionPixelSize3;
                } else if (currentRotation == 3) {
                    this.mLayoutParams.y = height - dimensionPixelSize3;
                }
            } else {
                this.mLayoutParams.y = i2;
            }
        }
    }

    public void updateLocation(View view) {
        if (this.mIsOpen) {
            updateLayoutParams(view);
            this.mWindowManager.updateViewLayout(this, this.mLayoutParams);
        }
    }

    private boolean isGesturalMode() {
        return QuickStepContract.isGesturalMode(getContext().getResources().getInteger(17694885));
    }

    private int getCurrentRotation() {
        return getContext().getDisplay().getRotation();
    }

    private TriangleDirection getTriangleDirection() {
        TriangleDirection triangleDirection = TriangleDirection.BOTTOM;
        return (isGesturalMode() || getCurrentRotation() == 0) ? triangleDirection : TriangleDirection.RIGHT;
    }

    private ShapeDrawable getArrowShapeDrawable(TriangleDirection triangleDirection) {
        ShapeDrawable shapeDrawable;
        View view = this.mArrowView;
        if (view == null) {
            return null;
        }
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        TriangleDirection triangleDirection2 = TriangleDirection.TOP;
        boolean z = true;
        if (triangleDirection == triangleDirection2 || triangleDirection == TriangleDirection.BOTTOM) {
            float f = (float) layoutParams.width;
            float f2 = (float) layoutParams.height;
            if (triangleDirection != triangleDirection2) {
                z = false;
            }
            shapeDrawable = new ShapeDrawable(TriangleShape.create(f, f2, z));
        } else {
            float f3 = (float) layoutParams.width;
            float f4 = (float) layoutParams.height;
            if (triangleDirection != TriangleDirection.LEFT) {
                z = false;
            }
            shapeDrawable = new ShapeDrawable(TriangleShape.createHorizontal(f3, f4, z));
        }
        Paint paint = shapeDrawable.getPaint();
        paint.setColor(getContext().getResources().getColor(R$color.trackpad_guide_background));
        paint.setPathEffect(new CornerPathEffect(getContext().getResources().getDimension(R$dimen.arrow_toast_corner_radius)));
        return shapeDrawable;
    }

    /* JADX WARNING: Removed duplicated region for block: B:35:0x0080  */
    /* JADX WARNING: Removed duplicated region for block: B:36:0x0086  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void updateArrowView() {
        /*
            r11 = this;
            android.view.View r0 = r11.mArrowView
            if (r0 == 0) goto L_0x009f
            android.view.View r0 = r11.mLeftArrowView
            if (r0 != 0) goto L_0x000a
            goto L_0x009f
        L_0x000a:
            android.content.Context r0 = r11.getContext()
            android.content.res.Resources r0 = r0.getResources()
            int r1 = com.android.systemui.R$dimen.trackpad_guide_tip_arrow_width
            int r0 = r0.getDimensionPixelSize(r1)
            int r1 = r0 / 2
            r2 = -4
            android.widget.LinearLayout$LayoutParams r3 = new android.widget.LinearLayout$LayoutParams
            r3.<init>(r0, r0)
            com.motorola.systemui.desktop.widget.DesktopNavGuideView$TriangleDirection r0 = r11.mDirection
            com.motorola.systemui.desktop.widget.DesktopNavGuideView$TriangleDirection r4 = com.motorola.systemui.desktop.widget.DesktopNavGuideView.TriangleDirection.RIGHT
            r5 = 1
            r6 = 0
            if (r0 != r4) goto L_0x0031
            int r0 = r11.getCurrentRotation()
            r7 = 3
            if (r0 != r7) goto L_0x0031
            r0 = r5
            goto L_0x0032
        L_0x0031:
            r0 = r6
        L_0x0032:
            if (r0 == 0) goto L_0x0037
            android.view.View r7 = r11.mLeftArrowView
            goto L_0x0039
        L_0x0037:
            android.view.View r7 = r11.mArrowView
        L_0x0039:
            android.view.View r8 = r11.mArrowView
            r9 = 8
            if (r0 == 0) goto L_0x0041
            r10 = r9
            goto L_0x0042
        L_0x0041:
            r10 = r6
        L_0x0042:
            r8.setVisibility(r10)
            android.view.View r8 = r11.mLeftArrowView
            if (r0 == 0) goto L_0x004a
            goto L_0x004b
        L_0x004a:
            r6 = r9
        L_0x004b:
            r8.setVisibility(r6)
            com.motorola.systemui.desktop.widget.DesktopNavGuideView$TriangleDirection r0 = r11.mDirection
            com.motorola.systemui.desktop.widget.DesktopNavGuideView$TriangleDirection r6 = com.motorola.systemui.desktop.widget.DesktopNavGuideView.TriangleDirection.BOTTOM
            r8 = 80
            r9 = 48
            r10 = 8388611(0x800003, float:1.1754948E-38)
            if (r0 != r6) goto L_0x0061
            android.graphics.drawable.ShapeDrawable r11 = r11.mBottomArrowDrawable
            r7.setBackground(r11)
            goto L_0x007b
        L_0x0061:
            if (r0 != r4) goto L_0x007b
            int r0 = r11.getCurrentRotation()
            if (r0 != r5) goto L_0x006c
            android.graphics.drawable.ShapeDrawable r0 = r11.mRightArrowDrawable
            goto L_0x006e
        L_0x006c:
            android.graphics.drawable.ShapeDrawable r0 = r11.mLeftArrowDrawable
        L_0x006e:
            r7.setBackground(r0)
            int r11 = r11.getCurrentRotation()
            if (r11 != r5) goto L_0x0079
            r11 = r8
            goto L_0x007c
        L_0x0079:
            r11 = r9
            goto L_0x007c
        L_0x007b:
            r11 = r10
        L_0x007c:
            r3.gravity = r11
            if (r11 != r10) goto L_0x0086
            r3.setMarginStart(r1)
            r3.topMargin = r2
            goto L_0x009c
        L_0x0086:
            if (r11 != r9) goto L_0x008d
            r3.topMargin = r1
            r3.rightMargin = r2
            goto L_0x009c
        L_0x008d:
            r0 = 8388613(0x800005, float:1.175495E-38)
            if (r11 != r0) goto L_0x0096
            r3.setMarginEnd(r1)
            goto L_0x009c
        L_0x0096:
            if (r11 != r8) goto L_0x009c
            r3.bottomMargin = r1
            r3.leftMargin = r2
        L_0x009c:
            r7.setLayoutParams(r3)
        L_0x009f:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.motorola.systemui.desktop.widget.DesktopNavGuideView.updateArrowView():void");
    }
}
