package com.motorola.systemui.cli.navgesture.view;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import com.android.systemui.shared.recents.model.Task;
import com.android.systemui.shared.recents.model.ThumbnailData;
import com.motorola.systemui.cli.navgesture.ActivityContext;
import com.motorola.systemui.cli.navgesture.util.DeviceProfile;
import com.motorola.systemui.cli.navgesture.util.TaskCornerRadius;
import com.motorola.systemui.cli.navgesture.util.Utilities;
import com.motorola.systemui.cli.navgesture.view.TaskView;

public class TaskThumbnailView extends View {
    private static final ColorMatrix COLOR_MATRIX = new ColorMatrix();
    private final ActivityContext mActivity;
    private final Paint mBackgroundPaint;
    protected BitmapShader mBitmapShader;
    private float mClipBottom;
    private RectF mClippedInsets;
    private float mDimAlpha;
    private float mDimAlphaMultiplier;
    private final Paint mDimmingPaintAfterClearing;
    private TaskView.FullscreenDrawParams mFullscreenParams;
    private final Matrix mMatrix;
    private final Paint mPaint;
    private Task mTask;
    private ThumbnailData mThumbnailData;

    public TaskThumbnailView(Context context) {
        this(context, (AttributeSet) null);
    }

    public TaskThumbnailView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public TaskThumbnailView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        Paint paint = new Paint(1);
        this.mPaint = paint;
        Paint paint2 = new Paint(1);
        this.mBackgroundPaint = paint2;
        Paint paint3 = new Paint();
        this.mDimmingPaintAfterClearing = paint3;
        this.mMatrix = new Matrix();
        this.mClipBottom = -1.0f;
        this.mClippedInsets = new RectF();
        this.mDimAlpha = 1.0f;
        this.mDimAlphaMultiplier = 1.0f;
        paint.setFilterBitmap(true);
        paint2.setColor(-1);
        paint3.setColor(-16777216);
        this.mActivity = ActivityContext.lookupContext(context);
        this.mFullscreenParams = new TaskView.FullscreenDrawParams(TaskCornerRadius.get(context));
    }

    public void bind(Task task) {
        this.mTask = task;
        int i = -16777216;
        if (task != null) {
            i = -16777216 | task.colorBackground;
        }
        this.mPaint.setColor(i);
        this.mBackgroundPaint.setColor(i);
    }

    public void setThumbnail(Task task, ThumbnailData thumbnailData) {
        Bitmap bitmap;
        this.mTask = task;
        if (thumbnailData == null || (bitmap = thumbnailData.thumbnail) == null) {
            this.mBitmapShader = null;
            this.mThumbnailData = null;
            this.mPaint.setShader((Shader) null);
        } else {
            bitmap.prepareToDraw();
            Shader.TileMode tileMode = Shader.TileMode.CLAMP;
            BitmapShader bitmapShader = new BitmapShader(bitmap, tileMode, tileMode);
            this.mBitmapShader = bitmapShader;
            this.mPaint.setShader(bitmapShader);
            this.mThumbnailData = thumbnailData;
            updateThumbnailMatrix();
        }
        updateThumbnailPaintFilter();
    }

    public void setDimAlphaMultipler(float f) {
        this.mDimAlphaMultiplier = f;
        setDimAlpha(this.mDimAlpha);
    }

    public void setDimAlpha(float f) {
        this.mDimAlpha = f;
        updateThumbnailPaintFilter();
    }

    public ThumbnailData getThumbnailData() {
        return this.mThumbnailData;
    }

    public int getSysUiStatusNavFlags() {
        ThumbnailData thumbnailData = this.mThumbnailData;
        if (thumbnailData == null) {
            return 0;
        }
        int i = thumbnailData.appearance;
        return ((i & 16) != 0 ? 1 : 2) | 0 | ((i & 8) != 0 ? 4 : 8);
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        RectF rectF = this.mFullscreenParams.mCurrentDrawnInsets;
        canvas.save();
        canvas.translate(rectF.left, rectF.top);
        float f = this.mFullscreenParams.mScale;
        canvas.scale(f, f);
        drawOnCanvas(canvas, -rectF.left, -rectF.top, ((float) getMeasuredWidth()) + rectF.right, ((float) getMeasuredHeight()) + rectF.bottom, this.mFullscreenParams.mCurrentDrawnCornerRadius);
        canvas.restore();
    }

    public RectF getInsetsToDrawInFullscreen() {
        return this.mClippedInsets;
    }

    public void setFullscreenParams(TaskView.FullscreenDrawParams fullscreenDrawParams) {
        this.mFullscreenParams = fullscreenDrawParams;
        invalidate();
    }

    public void drawOnCanvas(Canvas canvas, float f, float f2, float f3, float f4, float f5) {
        Task task = this.mTask;
        boolean z = task == null || task.isLocked || this.mBitmapShader == null || this.mThumbnailData == null;
        if (z || this.mClipBottom > 0.0f || this.mThumbnailData.isTranslucent) {
            canvas.drawRoundRect(f, f2, f3, f4, f5, f5, this.mBackgroundPaint);
            if (z) {
                return;
            }
        }
        if (this.mClipBottom > 0.0f) {
            canvas.save();
            Canvas canvas2 = canvas;
            float f6 = f3;
            canvas.clipRect(f, f2, f6, this.mClipBottom);
            canvas.drawRoundRect(f, f2, f6, f4, f5, f5, this.mPaint);
            canvas.restore();
            return;
        }
        Canvas canvas3 = canvas;
        canvas.drawRoundRect(f, f2, f3, f4, f5, f5, this.mPaint);
    }

    private void updateThumbnailPaintFilter() {
        int i = (int) ((1.0f - (this.mDimAlpha * this.mDimAlphaMultiplier)) * 255.0f);
        ColorFilter colorFilter = getColorFilter(i);
        this.mBackgroundPaint.setColorFilter(colorFilter);
        this.mDimmingPaintAfterClearing.setAlpha(255 - i);
        if (this.mBitmapShader != null) {
            this.mPaint.setColorFilter(colorFilter);
        } else {
            this.mPaint.setColorFilter((ColorFilter) null);
            this.mPaint.setColor(Color.argb(255, i, i, i));
            this.mBackgroundPaint.setColorFilter((ColorFilter) null);
        }
        invalidate();
    }

    private void updateThumbnailMatrix() {
        ThumbnailData thumbnailData;
        float f;
        int i;
        float f2;
        this.mClipBottom = -1.0f;
        if (!(this.mBitmapShader == null || (thumbnailData = this.mThumbnailData) == null)) {
            float f3 = thumbnailData.scale;
            Rect rect = thumbnailData.insets;
            float width = ((float) thumbnailData.thumbnail.getWidth()) - (((float) (rect.left + rect.right)) * f3);
            float height = ((float) this.mThumbnailData.thumbnail.getHeight()) - (((float) (rect.top + rect.bottom)) * f3);
            DeviceProfile deviceProfile = this.mActivity.getDeviceProfile();
            boolean z = false;
            if (getMeasuredWidth() == 0) {
                Log.d("chentq2", "updateThumbnailMatrix getMeasuredWidth is 0");
                f = 0.0f;
            } else {
                Configuration configuration = getContext().getResources().getConfiguration();
                int i2 = configuration.orientation;
                ThumbnailData thumbnailData2 = this.mThumbnailData;
                if (i2 != thumbnailData2.orientation && thumbnailData2.windowingMode == 1) {
                    z = true;
                }
                if (z) {
                    f = ((float) getMeasuredWidth()) / height;
                } else {
                    f = ((float) getMeasuredWidth()) / width;
                }
                Log.d("chentq2", "updateThumbnailMatrix configuration = " + configuration.toString() + "; isRotated = " + z + "; thumbnailScale = " + f);
            }
            Log.d("chentq2", "updateThumbnailMatrix isRotated = " + z + "; profile.isLandscape = " + deviceProfile.isLandscape);
            if (z) {
                int i3 = deviceProfile.isLandscape ? -1 : 1;
                this.mMatrix.setRotate((float) (i3 * 90));
                this.mClippedInsets.offsetTo(((float) (i3 == 1 ? rect.bottom : rect.top)) * f3, ((float) (i3 == 1 ? rect.left : rect.right)) * f3);
                if (i3 == -1) {
                    this.mClippedInsets.offset(0.0f, (width * f) - ((float) getMeasuredHeight()));
                }
                Matrix matrix = this.mMatrix;
                RectF rectF = this.mClippedInsets;
                matrix.postTranslate(-rectF.left, -rectF.top);
                if (i3 == 1) {
                    this.mMatrix.postTranslate((float) this.mThumbnailData.thumbnail.getHeight(), 0.0f);
                } else {
                    this.mMatrix.postTranslate(0.0f, (float) this.mThumbnailData.thumbnail.getWidth());
                }
            } else {
                this.mClippedInsets.offsetTo(((float) rect.left) * f3, ((float) rect.top) * f3);
                Matrix matrix2 = this.mMatrix;
                RectF rectF2 = this.mClippedInsets;
                matrix2.setTranslate(-rectF2.left, -rectF2.top);
            }
            if (z) {
                f2 = ((float) this.mThumbnailData.thumbnail.getHeight()) * f;
                i = this.mThumbnailData.thumbnail.getWidth();
            } else {
                f2 = ((float) this.mThumbnailData.thumbnail.getWidth()) * f;
                i = this.mThumbnailData.thumbnail.getHeight();
            }
            RectF rectF3 = this.mClippedInsets;
            float f4 = rectF3.left * f;
            rectF3.left = f4;
            rectF3.top *= f;
            rectF3.right = (f2 - f4) - ((float) getMeasuredWidth());
            RectF rectF4 = this.mClippedInsets;
            rectF4.bottom = ((((float) i) * f) - rectF4.top) - ((float) getMeasuredHeight());
            this.mMatrix.postScale(f, f);
            this.mBitmapShader.setLocalMatrix(this.mMatrix);
            if (!z) {
                width = height;
            }
            float max = Math.max(width * f, 0.0f);
            if (Math.round(max) < getMeasuredHeight()) {
                this.mClipBottom = max;
            }
            this.mPaint.setShader(this.mBitmapShader);
        }
        invalidate();
    }

    /* access modifiers changed from: protected */
    public void onSizeChanged(int i, int i2, int i3, int i4) {
        super.onSizeChanged(i, i2, i3, i4);
        updateThumbnailMatrix();
    }

    private static ColorFilter getColorFilter(int i) {
        int boundToRange = Utilities.boundToRange(i, 0, 255);
        if (boundToRange == 255) {
            return null;
        }
        float f = ((float) boundToRange) / 255.0f;
        ColorMatrix colorMatrix = COLOR_MATRIX;
        colorMatrix.setScale(f, f, f, 1.0f);
        float[] array = colorMatrix.getArray();
        float f2 = (float) (255 - boundToRange);
        array[4] = f2;
        array[9] = f2;
        array[14] = f2;
        return new ColorMatrixColorFilter(colorMatrix);
    }
}
