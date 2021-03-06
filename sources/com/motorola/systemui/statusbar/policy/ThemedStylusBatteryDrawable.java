package com.motorola.systemui.statusbar.policy;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.BlendMode;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.PathParser;
import com.android.settingslib.Utils;
import com.android.systemui.R$array;
import com.android.systemui.R$color;
import com.android.systemui.R$string;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/* compiled from: ThemedStylusBatteryDrawable.kt */
public class ThemedStylusBatteryDrawable extends Drawable {
    @NotNull
    public static final Companion Companion = new Companion((DefaultConstructorMarker) null);
    private int backgroundColor = -65281;
    private int batteryLevel;
    private int batteryLevelColorLow;
    private int batteryLevelColorVeryLow;
    @NotNull
    private final Path boltPath = new Path();
    private boolean charging;
    @NotNull
    private int[] colorLevels;
    @NotNull
    private final Context context;
    private int criticalLevel;
    private boolean dualTone;
    @NotNull
    private final Paint dualToneBackgroundFill;
    private int fillColor = -65281;
    @NotNull
    private final Paint fillColorStrokePaint;
    @NotNull
    private final Paint fillColorStrokeProtection;
    @NotNull
    private final Path fillMask = new Path();
    @NotNull
    private final Paint fillPaint;
    @NotNull
    private final RectF fillRect = new RectF();
    private int intrinsicHeight;
    private int intrinsicWidth;
    @NotNull
    private final Function0<Unit> invalidateRunnable = new ThemedStylusBatteryDrawable$invalidateRunnable$1(this);
    private boolean invertFillIcon;
    private int levelColor = -65281;
    @NotNull
    private final Path levelPath = new Path();
    @NotNull
    private final RectF levelRect = new RectF();
    private boolean onlyDrawTintedPart;
    @NotNull
    private final Path perimeterPath = new Path();
    @NotNull
    private final Matrix scaleMatrix = new Matrix();
    @NotNull
    private final Path scaledBolt = new Path();
    @NotNull
    private final Path scaledFill = new Path();
    @NotNull
    private final Path scaledPerimeter = new Path();
    @NotNull
    private final Path unifiedPath = new Path();

    public int getOpacity() {
        return -1;
    }

    public void setAlpha(int i) {
    }

    public ThemedStylusBatteryDrawable(@NotNull Context context2, int i) {
        Intrinsics.checkNotNullParameter(context2, "context");
        this.context = context2;
        this.criticalLevel = context2.getResources().getInteger(17694766);
        Paint paint = new Paint(1);
        paint.setColor(i);
        paint.setAlpha(255);
        paint.setDither(true);
        paint.setStrokeWidth(5.0f);
        paint.setStyle(Paint.Style.STROKE);
        paint.setBlendMode(BlendMode.SRC);
        paint.setStrokeMiter(5.0f);
        paint.setStrokeJoin(Paint.Join.ROUND);
        Unit unit = Unit.INSTANCE;
        this.fillColorStrokePaint = paint;
        Paint paint2 = new Paint(1);
        paint2.setDither(true);
        paint2.setStrokeWidth(5.0f);
        paint2.setStyle(Paint.Style.STROKE);
        paint2.setBlendMode(BlendMode.CLEAR);
        paint2.setStrokeMiter(5.0f);
        paint2.setStrokeJoin(Paint.Join.ROUND);
        this.fillColorStrokeProtection = paint2;
        Paint paint3 = new Paint(1);
        paint3.setColor(i);
        paint3.setAlpha(255);
        paint3.setDither(true);
        paint3.setStrokeWidth(0.0f);
        paint3.setStyle(Paint.Style.FILL_AND_STROKE);
        this.fillPaint = paint3;
        Paint paint4 = new Paint(1);
        paint4.setColor(i);
        paint4.setAlpha(85);
        paint4.setDither(true);
        paint4.setStrokeWidth(0.0f);
        paint4.setStyle(Paint.Style.FILL_AND_STROKE);
        this.dualToneBackgroundFill = paint4;
        float f = context2.getResources().getDisplayMetrics().density;
        this.intrinsicHeight = (int) (15.0f * f);
        this.intrinsicWidth = (int) (f * 9.0f);
        Resources resources = context2.getResources();
        TypedArray obtainTypedArray = resources.obtainTypedArray(R$array.batterymeter_color_levels);
        TypedArray obtainTypedArray2 = resources.obtainTypedArray(R$array.batterymeter_color_values);
        int length = obtainTypedArray.length();
        this.colorLevels = new int[(length * 2)];
        if (length > 0) {
            int i2 = 0;
            while (true) {
                int i3 = i2 + 1;
                int i4 = i2 * 2;
                this.colorLevels[i4] = obtainTypedArray.getInt(i2, 0);
                if (obtainTypedArray2.getType(i2) == 2) {
                    this.colorLevels[i4 + 1] = Utils.getColorAttrDefaultColor(this.context, obtainTypedArray2.getThemeAttributeId(i2, 0));
                } else {
                    this.colorLevels[i4 + 1] = obtainTypedArray2.getColor(i2, 0);
                }
                if (i3 >= length) {
                    break;
                }
                i2 = i3;
            }
        }
        this.batteryLevelColorVeryLow = this.context.getColor(R$color.stylus_battery_level_very_low);
        this.batteryLevelColorLow = this.context.getColor(R$color.stylus_battery_level_low);
        obtainTypedArray.recycle();
        obtainTypedArray2.recycle();
        loadPaths();
    }

    public final void setCharging(boolean z) {
        this.charging = z;
        postInvalidate();
    }

    public void draw(@NotNull Canvas canvas) {
        float f;
        Intrinsics.checkNotNullParameter(canvas, "c");
        if (!this.onlyDrawTintedPart || haveTintedPart()) {
            canvas.saveLayer((RectF) null, (Paint) null);
            this.unifiedPath.reset();
            this.levelPath.reset();
            this.levelRect.set(this.fillRect);
            int i = this.batteryLevel;
            float f2 = ((float) i) / 100.0f;
            if (i >= 95) {
                f = this.fillRect.top;
            } else {
                RectF rectF = this.fillRect;
                f = (rectF.height() * (((float) 1) - f2)) + rectF.top;
            }
            this.levelRect.top = (float) Math.floor((double) f);
            this.levelPath.addRect(this.levelRect, Path.Direction.CCW);
            this.unifiedPath.addPath(this.scaledPerimeter);
            if (!this.dualTone) {
                this.unifiedPath.op(this.levelPath, Path.Op.UNION);
            }
            this.fillPaint.setColor(this.levelColor);
            if (this.charging) {
                this.unifiedPath.op(this.scaledBolt, Path.Op.DIFFERENCE);
                if (!this.invertFillIcon) {
                    canvas.drawPath(this.scaledBolt, this.fillPaint);
                }
            }
            if (this.dualTone) {
                canvas.drawPath(this.unifiedPath, this.dualToneBackgroundFill);
                canvas.save();
                canvas.clipRect(0.0f, ((float) getBounds().bottom) - (((float) getBounds().height()) * f2), (float) getBounds().right, (float) getBounds().bottom);
                canvas.drawPath(this.unifiedPath, this.fillPaint);
                canvas.restore();
            } else {
                if (!this.onlyDrawTintedPart) {
                    this.fillPaint.setColor(this.fillColor);
                    canvas.drawPath(this.unifiedPath, this.fillPaint);
                }
                this.fillPaint.setColor(this.levelColor);
                int i2 = this.batteryLevel;
                if (i2 <= 30 && !this.charging && this.onlyDrawTintedPart) {
                    this.fillPaint.setColor(i2 <= 15 ? this.batteryLevelColorVeryLow : this.batteryLevelColorLow);
                    canvas.save();
                    canvas.clipPath(this.scaledFill);
                    canvas.drawPath(this.levelPath, this.fillPaint);
                    canvas.restore();
                }
            }
            if (this.charging) {
                canvas.clipOutPath(this.scaledBolt);
                if (this.invertFillIcon) {
                    canvas.drawPath(this.scaledBolt, this.fillColorStrokePaint);
                } else {
                    canvas.drawPath(this.scaledBolt, this.fillColorStrokeProtection);
                }
            }
            canvas.restore();
        }
    }

    private final int batteryColorForLevel(int i) {
        if (this.charging) {
            return this.fillColor;
        }
        return getColorForLevel(i);
    }

    private final int getColorForLevel(int i) {
        int i2 = 0;
        int i3 = 0;
        while (true) {
            int[] iArr = this.colorLevels;
            if (i2 >= iArr.length) {
                return i3;
            }
            int i4 = iArr[i2];
            int i5 = iArr[i2 + 1];
            if (i <= i4) {
                return i2 == iArr.length + -2 ? this.fillColor : i5;
            }
            i2 += 2;
            i3 = i5;
        }
    }

    public void setColorFilter(@Nullable ColorFilter colorFilter) {
        this.fillPaint.setColorFilter(colorFilter);
        this.fillColorStrokePaint.setColorFilter(colorFilter);
        this.dualToneBackgroundFill.setColorFilter(colorFilter);
    }

    public int getIntrinsicHeight() {
        return this.intrinsicHeight;
    }

    public int getIntrinsicWidth() {
        return this.intrinsicWidth;
    }

    public void setBatteryLevel(int i) {
        this.invertFillIcon = i >= 67 ? true : i <= 33 ? false : this.invertFillIcon;
        this.batteryLevel = i;
        this.levelColor = batteryColorForLevel(i);
        invalidateSelf();
    }

    /* access modifiers changed from: protected */
    public void onBoundsChange(@Nullable Rect rect) {
        super.onBoundsChange(rect);
        updateSize();
    }

    private final void postInvalidate() {
        unscheduleSelf(new ThemedStylusBatteryDrawable$sam$java_lang_Runnable$0(this.invalidateRunnable));
        scheduleSelf(new ThemedStylusBatteryDrawable$sam$java_lang_Runnable$0(this.invalidateRunnable), 0);
    }

    private final void updateSize() {
        Rect bounds = getBounds();
        if (bounds.isEmpty()) {
            this.scaleMatrix.setScale(1.0f, 1.0f);
        } else {
            this.scaleMatrix.setScale(((float) bounds.right) / 9.0f, ((float) bounds.bottom) / 15.0f);
        }
        this.perimeterPath.transform(this.scaleMatrix, this.scaledPerimeter);
        this.fillMask.transform(this.scaleMatrix, this.scaledFill);
        this.scaledFill.computeBounds(this.fillRect, true);
        this.boltPath.transform(this.scaleMatrix, this.scaledBolt);
        float max = Math.max((((float) bounds.right) / 9.0f) * 3.0f, 6.0f);
        this.fillColorStrokePaint.setStrokeWidth(max);
        this.fillColorStrokeProtection.setStrokeWidth(max);
    }

    private final void loadPaths() {
        this.perimeterPath.set(PathParser.createPathFromPathData(this.context.getResources().getString(R$string.config_stylusBatteryPerimeterPath)));
        this.perimeterPath.computeBounds(new RectF(), true);
        this.fillMask.set(PathParser.createPathFromPathData(this.context.getResources().getString(R$string.config_stylusBatteryFillMask)));
        this.fillMask.computeBounds(this.fillRect, true);
        this.boltPath.set(PathParser.createPathFromPathData(this.context.getResources().getString(R$string.config_stylusBatteryBoltPath)));
        this.dualTone = this.context.getResources().getBoolean(17891394);
    }

    public final void setOnlyDrawTintedPart(boolean z) {
        this.onlyDrawTintedPart = z;
    }

    public final boolean haveTintedPart() {
        return !this.charging && this.batteryLevel <= 30;
    }

    /* compiled from: ThemedStylusBatteryDrawable.kt */
    public static final class Companion {
        public /* synthetic */ Companion(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }

        private Companion() {
        }
    }
}
