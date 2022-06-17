package com.android.systemui.screenrecord;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import com.android.systemui.R$dimen;
import com.android.systemui.R$drawable;
import java.util.ArrayList;
import java.util.List;

public class DoodleView extends View {
    private Path mCurrentPath;
    private float mLastX;
    private float mLastY;
    private Paint mPaint = new Paint();
    private int mPaintColor;
    private List<PathData> mPathDataList = new ArrayList();
    private ImageView mUndoView;

    public DoodleView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        int dimensionPixelSize = context.getResources().getDimensionPixelSize(R$dimen.screenrecord_color_pen_stroke);
        this.mPaintColor = 1660944383;
        this.mPaint.setStyle(Paint.Style.STROKE);
        this.mPaint.setStrokeWidth((float) dimensionPixelSize);
        this.mPaint.setAntiAlias(true);
        this.mPaint.setStrokeCap(Paint.Cap.ROUND);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:6:0x000d, code lost:
        if (r0 != 3) goto L_0x0083;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean onTouchEvent(android.view.MotionEvent r7) {
        /*
            r6 = this;
            int r0 = r7.getAction()
            r1 = 1
            if (r0 == 0) goto L_0x0054
            if (r0 == r1) goto L_0x003f
            r2 = 2
            if (r0 == r2) goto L_0x0010
            r7 = 3
            if (r0 == r7) goto L_0x003f
            goto L_0x0083
        L_0x0010:
            android.graphics.Path r0 = r6.mCurrentPath
            float r2 = r7.getX()
            float r3 = r6.mLastX
            float r2 = r2 + r3
            r3 = 1073741824(0x40000000, float:2.0)
            float r2 = r2 / r3
            float r4 = r7.getY()
            float r5 = r6.mLastY
            float r4 = r4 + r5
            float r4 = r4 / r3
            float r3 = r7.getX()
            float r5 = r7.getY()
            r0.quadTo(r2, r4, r3, r5)
            float r0 = r7.getX()
            r6.mLastX = r0
            float r7 = r7.getY()
            r6.mLastY = r7
            r6.invalidate()
            goto L_0x0083
        L_0x003f:
            r7 = 0
            r6.mCurrentPath = r7
            java.util.List<com.android.systemui.screenrecord.DoodleView$PathData> r7 = r6.mPathDataList
            int r7 = r7.size()
            if (r7 != r1) goto L_0x0083
            android.widget.ImageView r6 = r6.mUndoView
            if (r6 == 0) goto L_0x0083
            int r7 = com.android.systemui.R$drawable.zz_moto_recording_undo
            r6.setImageResource(r7)
            goto L_0x0083
        L_0x0054:
            android.graphics.Path r0 = new android.graphics.Path
            r0.<init>()
            r6.mCurrentPath = r0
            java.util.List<com.android.systemui.screenrecord.DoodleView$PathData> r2 = r6.mPathDataList
            com.android.systemui.screenrecord.DoodleView$PathData r3 = new com.android.systemui.screenrecord.DoodleView$PathData
            int r4 = r6.mPaintColor
            r3.<init>(r0, r4)
            r2.add(r3)
            android.graphics.Path r0 = r6.mCurrentPath
            float r2 = r7.getX()
            float r3 = r7.getY()
            r0.moveTo(r2, r3)
            float r0 = r7.getX()
            r6.mLastX = r0
            float r7 = r7.getY()
            r6.mLastY = r7
            r6.invalidate()
        L_0x0083:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.screenrecord.DoodleView.onTouchEvent(android.view.MotionEvent):boolean");
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        for (PathData next : this.mPathDataList) {
            this.mPaint.setColor(next.getPaintColor());
            canvas.drawPath(next.getPath(), this.mPaint);
        }
    }

    public void undo() {
        ImageView imageView;
        if (this.mPathDataList.size() > 0) {
            List<PathData> list = this.mPathDataList;
            list.remove(list.size() - 1);
            if (this.mPathDataList.size() == 0 && (imageView = this.mUndoView) != null) {
                imageView.setImageResource(R$drawable.zz_moto_recording_nothing_undo);
            }
            invalidate();
        }
    }

    public boolean hasDrawingPath() {
        return this.mPathDataList.size() > 0;
    }

    public void setArrawView(ImageView imageView) {
        this.mUndoView = imageView;
    }

    public void setPaintColor(int i) {
        this.mPaintColor = i;
    }

    private static class PathData {
        private final int mPaintColor;
        private final Path mPath;

        public PathData(Path path, int i) {
            this.mPath = path;
            this.mPaintColor = i;
        }

        public Path getPath() {
            return this.mPath;
        }

        public int getPaintColor() {
            return this.mPaintColor;
        }
    }
}
