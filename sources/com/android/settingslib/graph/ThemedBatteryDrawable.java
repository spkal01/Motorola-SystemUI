package com.android.settingslib.graph;

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
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.util.PathParser;
import com.android.settingslib.R$array;
import com.android.settingslib.R$color;
import com.android.settingslib.R$string;
import com.android.settingslib.Utils;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/* compiled from: ThemedBatteryDrawable.kt */
public class ThemedBatteryDrawable extends Drawable {
    @NotNull
    public static final Companion Companion = new Companion((DefaultConstructorMarker) null);
    @NotNull
    private final Path adaptiveChargingBatteryPath;
    private int backgroundColor = -65281;
    private int batteryLevel;
    private int batteryPercentColor;
    private int batteryPercentDarkColor;
    private int batteryPercentLightColor;
    @NotNull
    private final Path boltPath = new Path();
    private boolean charging;
    private boolean chargingDisabledByAdaptive;
    @NotNull
    private int[] colorLevels;
    @NotNull
    private final Context context;
    private int criticalLevel;
    private boolean dualTone;
    @NotNull
    private final Paint dualToneBackgroundFill;
    @NotNull
    private final Paint errorPaint;
    @NotNull
    private final Path errorPerimeterPath = new Path();
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
    private final Function0<Unit> invalidateRunnable;
    private boolean invertFillIcon;
    private int levelColor = -65281;
    @NotNull
    private final Path levelPath = new Path();
    @NotNull
    private final RectF levelRect = new RectF();
    @NotNull
    private final Rect padding = new Rect();
    @NotNull
    private final Path perimeterPath = new Path();
    @NotNull
    private final Path plusPath = new Path();
    private boolean powerSaveEnabled;
    @NotNull
    private final Matrix scaleMatrix = new Matrix();
    @NotNull
    private final Path scaledAdaptiveChargingBattery;
    @NotNull
    private final Path scaledBolt = new Path();
    @NotNull
    private final Path scaledErrorPerimeter = new Path();
    @NotNull
    private final Path scaledFill = new Path();
    @NotNull
    private final Path scaledPerimeter = new Path();
    @NotNull
    private final Path scaledPlus = new Path();
    @NotNull
    private final Path scaledShield;
    @NotNull
    private final Path shieldPath;
    private boolean showPercent;
    private float textHeight;
    @NotNull
    private final Paint textPaint;
    @NotNull
    private Path textPath = new Path();
    @NotNull
    private final Path unifiedPath = new Path();

    public int getOpacity() {
        return -1;
    }

    public void setAlpha(int i) {
    }

    public ThemedBatteryDrawable(@NotNull Context context2, int i) {
        Intrinsics.checkNotNullParameter(context2, "context");
        this.context = context2;
        Paint paint = new Paint(1);
        paint.setTypeface(Typeface.create("sans-serif-condensed", 1));
        paint.setTextAlign(Paint.Align.CENTER);
        Unit unit = Unit.INSTANCE;
        this.textPaint = paint;
        this.batteryPercentDarkColor = -65281;
        this.batteryPercentLightColor = -65281;
        this.batteryPercentColor = -65281;
        this.adaptiveChargingBatteryPath = new Path();
        this.scaledAdaptiveChargingBattery = new Path();
        this.shieldPath = new Path();
        this.scaledShield = new Path();
        this.invalidateRunnable = new ThemedBatteryDrawable$invalidateRunnable$1(this);
        this.criticalLevel = context2.getResources().getInteger(17694766);
        Paint paint2 = new Paint(1);
        paint2.setColor(i);
        paint2.setAlpha(255);
        paint2.setDither(true);
        paint2.setStrokeWidth(5.0f);
        paint2.setStyle(Paint.Style.STROKE);
        paint2.setBlendMode(BlendMode.SRC);
        paint2.setStrokeMiter(5.0f);
        paint2.setStrokeJoin(Paint.Join.ROUND);
        this.fillColorStrokePaint = paint2;
        Paint paint3 = new Paint(1);
        paint3.setDither(true);
        paint3.setStrokeWidth(5.0f);
        paint3.setStyle(Paint.Style.STROKE);
        paint3.setBlendMode(BlendMode.CLEAR);
        paint3.setStrokeMiter(5.0f);
        paint3.setStrokeJoin(Paint.Join.ROUND);
        this.fillColorStrokeProtection = paint3;
        Paint paint4 = new Paint(1);
        paint4.setColor(i);
        paint4.setAlpha(255);
        paint4.setDither(true);
        paint4.setStrokeWidth(0.0f);
        paint4.setStyle(Paint.Style.FILL_AND_STROKE);
        this.fillPaint = paint4;
        Paint paint5 = new Paint(1);
        paint5.setColor(Utils.getColorStateListDefaultColor(context2, R$color.batterymeter_plus_color));
        paint5.setAlpha(255);
        paint5.setDither(true);
        paint5.setStrokeWidth(0.0f);
        paint5.setStyle(Paint.Style.FILL_AND_STROKE);
        paint5.setBlendMode(BlendMode.SRC);
        this.errorPaint = paint5;
        Paint paint6 = new Paint(1);
        paint6.setColor(i);
        paint6.setAlpha(85);
        paint6.setDither(true);
        paint6.setStrokeWidth(0.0f);
        paint6.setStyle(Paint.Style.FILL_AND_STROKE);
        this.dualToneBackgroundFill = paint6;
        float f = context2.getResources().getDisplayMetrics().density;
        this.intrinsicHeight = (int) (20.0f * f);
        this.intrinsicWidth = (int) (f * 12.0f);
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
        obtainTypedArray.recycle();
        obtainTypedArray2.recycle();
        this.batteryPercentDarkColor = this.context.getColor(R$color.dark_battery_percent_color);
        this.batteryPercentLightColor = this.context.getColor(R$color.light_battery_percent_color);
        loadPaths();
    }

    public final void setChargingDisabledByAdaptive(boolean z) {
        this.chargingDisabledByAdaptive = z;
        postInvalidate();
    }

    public final void setCharging(boolean z) {
        this.charging = z;
        this.levelColor = batteryColorForLevel(this.batteryLevel);
        postInvalidate();
    }

    public final boolean getPowerSaveEnabled() {
        return this.powerSaveEnabled;
    }

    public final void setPowerSaveEnabled(boolean z) {
        this.powerSaveEnabled = z;
        this.levelColor = batteryColorForLevel(this.batteryLevel);
        postInvalidate();
    }

    public void draw(@NotNull Canvas canvas) {
        float f;
        Canvas canvas2 = canvas;
        Intrinsics.checkNotNullParameter(canvas2, "c");
        canvas2.saveLayer((RectF) null, (Paint) null);
        this.unifiedPath.reset();
        this.levelPath.reset();
        this.levelRect.set(this.fillRect);
        int i = this.batteryLevel;
        float f2 = ((float) i) / 100.0f;
        boolean z = true;
        if (i >= 95) {
            f = this.fillRect.top;
        } else {
            RectF rectF = this.fillRect;
            f = (rectF.height() * (((float) 1) - f2)) + rectF.top;
        }
        this.levelRect.top = (float) Math.floor((double) f);
        this.levelPath.addRect(this.levelRect, Path.Direction.CCW);
        if (this.chargingDisabledByAdaptive) {
            this.unifiedPath.addPath(this.scaledAdaptiveChargingBattery);
            this.fillPaint.setColor(this.fillColor);
            canvas2.drawPath(this.unifiedPath, this.fillPaint);
            canvas2.drawPath(this.scaledShield, this.fillPaint);
            canvas.restore();
            return;
        }
        this.unifiedPath.addPath(this.scaledPerimeter);
        if (!this.dualTone) {
            this.unifiedPath.op(this.levelPath, Path.Op.UNION);
        }
        this.fillPaint.setColor(this.levelColor);
        if (!this.charging && !this.powerSaveEnabled && this.showPercent) {
            this.textPaint.setColor(getBatteryPercentColor());
            this.textPaint.setTextSize(this.fillRect.height() * (this.batteryLevel == 100 ? 0.38f : 0.5f));
            this.textHeight = -this.textPaint.getFontMetrics().ascent;
            String valueOf = String.valueOf(this.batteryLevel);
            RectF rectF2 = this.fillRect;
            float width = (this.fillRect.width() * 0.5f) + rectF2.left;
            float height = ((rectF2.height() + this.textHeight) * 0.47f) + this.fillRect.top;
            if (this.levelRect.top <= height) {
                z = false;
            }
            this.textPath.reset();
            this.textPaint.getTextPath(valueOf, 0, valueOf.length(), width, height, this.textPath);
            if (!z) {
                this.unifiedPath.op(this.textPath, Path.Op.DIFFERENCE);
                canvas.save();
                canvas2.clipRect(0.0f, 0.0f, (float) getBounds().right, this.levelRect.top);
                this.textPaint.setColor(this.levelColor);
                canvas2.drawPath(this.textPath, this.textPaint);
                canvas2.clipRect(0.0f, this.levelRect.top, (float) getBounds().right, (float) getBounds().bottom);
                this.textPaint.setColor(getBatteryPercentColor());
                canvas2.drawPath(this.textPath, this.textPaint);
                canvas.restore();
            } else {
                this.textPaint.setColor(getColorForLevel(this.batteryLevel));
                canvas2.drawPath(this.textPath, this.textPaint);
            }
        }
        if (this.charging) {
            this.unifiedPath.op(this.scaledBolt, Path.Op.DIFFERENCE);
            if (!this.invertFillIcon) {
                canvas2.drawPath(this.scaledBolt, this.fillPaint);
            }
        }
        if (this.dualTone) {
            canvas2.drawPath(this.unifiedPath, this.dualToneBackgroundFill);
            canvas.save();
            canvas2.clipRect(0.0f, ((float) getBounds().bottom) - (((float) getBounds().height()) * f2), (float) getBounds().right, (float) getBounds().bottom);
            canvas2.drawPath(this.unifiedPath, this.fillPaint);
            canvas.restore();
        } else {
            this.fillPaint.setColor(this.fillColor);
            canvas2.drawPath(this.unifiedPath, this.fillPaint);
            this.fillPaint.setColor(this.levelColor);
            if (this.batteryLevel <= 15 && !this.charging) {
                canvas.save();
                canvas2.clipPath(this.scaledFill);
                canvas2.drawPath(this.levelPath, this.fillPaint);
                canvas.restore();
            }
        }
        if (this.charging) {
            canvas2.clipOutPath(this.scaledBolt);
            if (this.invertFillIcon) {
                canvas2.drawPath(this.scaledBolt, this.fillColorStrokePaint);
            } else {
                canvas2.drawPath(this.scaledBolt, this.fillColorStrokeProtection);
            }
        } else if (this.powerSaveEnabled) {
            canvas2.drawPath(this.scaledErrorPerimeter, this.errorPaint);
            canvas2.drawPath(this.scaledPlus, this.errorPaint);
        }
        canvas.restore();
    }

    private final int batteryColorForLevel(int i) {
        if (this.charging || this.powerSaveEnabled) {
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

    public final void setColors(int i, int i2, int i3) {
        if (!this.dualTone) {
            i = i3;
        }
        this.fillColor = i;
        this.fillPaint.setColor(i);
        this.fillColorStrokePaint.setColor(this.fillColor);
        this.backgroundColor = i2;
        this.dualToneBackgroundFill.setColor(i2);
        this.levelColor = batteryColorForLevel(this.batteryLevel);
        invalidateSelf();
    }

    private final void postInvalidate() {
        unscheduleSelf(new ThemedBatteryDrawable$sam$java_lang_Runnable$0(this.invalidateRunnable));
        scheduleSelf(new ThemedBatteryDrawable$sam$java_lang_Runnable$0(this.invalidateRunnable), 0);
    }

    private final void updateSize() {
        Rect bounds = getBounds();
        if (bounds.isEmpty()) {
            this.scaleMatrix.setScale(1.0f, 1.0f);
        } else {
            this.scaleMatrix.setScale(((float) bounds.right) / 12.0f, ((float) bounds.bottom) / 20.0f);
        }
        this.perimeterPath.transform(this.scaleMatrix, this.scaledPerimeter);
        this.errorPerimeterPath.transform(this.scaleMatrix, this.scaledErrorPerimeter);
        this.fillMask.transform(this.scaleMatrix, this.scaledFill);
        this.scaledFill.computeBounds(this.fillRect, true);
        this.boltPath.transform(this.scaleMatrix, this.scaledBolt);
        this.plusPath.transform(this.scaleMatrix, this.scaledPlus);
        this.adaptiveChargingBatteryPath.transform(this.scaleMatrix, this.scaledAdaptiveChargingBattery);
        this.shieldPath.transform(this.scaleMatrix, this.scaledShield);
        float max = Math.max((((float) bounds.right) / 12.0f) * 3.0f, 6.0f);
        this.fillColorStrokePaint.setStrokeWidth(max);
        this.fillColorStrokeProtection.setStrokeWidth(max);
    }

    private final void loadPaths() {
        this.perimeterPath.set(PathParser.createPathFromPathData(this.context.getResources().getString(17039875)));
        this.perimeterPath.computeBounds(new RectF(), true);
        this.errorPerimeterPath.set(PathParser.createPathFromPathData(this.context.getResources().getString(17039873)));
        this.errorPerimeterPath.computeBounds(new RectF(), true);
        this.fillMask.set(PathParser.createPathFromPathData(this.context.getResources().getString(17039874)));
        this.fillMask.computeBounds(this.fillRect, true);
        this.boltPath.set(PathParser.createPathFromPathData(this.context.getResources().getString(17039872)));
        this.plusPath.set(PathParser.createPathFromPathData(this.context.getResources().getString(17039876)));
        this.adaptiveChargingBatteryPath.set(PathParser.createPathFromPathData(this.context.getResources().getString(R$string.config_adaptiveChargingBatteryPath)));
        this.shieldPath.set(PathParser.createPathFromPathData(this.context.getResources().getString(R$string.config_adaptiveChargingShieldPath)));
        this.dualTone = this.context.getResources().getBoolean(17891394);
    }

    public final boolean getShowPercent() {
        return this.showPercent;
    }

    public final void setShowPercent(boolean z) {
        this.showPercent = z;
        postInvalidate();
    }

    public final void setColors(float f, int i, int i2, int i3) {
        int i4;
        if (f > 0.5f) {
            i4 = this.batteryPercentLightColor;
        } else {
            i4 = this.batteryPercentDarkColor;
        }
        this.batteryPercentColor = i4;
        setColors(i, i2, i3);
    }

    public final int getBatteryPercentColor() {
        return this.batteryPercentColor;
    }

    /* compiled from: ThemedBatteryDrawable.kt */
    public static final class Companion {
        public /* synthetic */ Companion(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }

        private Companion() {
        }
    }
}
