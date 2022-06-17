package com.android.systemui.statusbar;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Notification;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.os.Parcelable;
import android.service.notification.StatusBarNotification;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.FloatProperty;
import android.util.Log;
import android.util.Property;
import android.util.TypedValue;
import android.view.ViewDebug;
import android.view.accessibility.AccessibilityEvent;
import android.view.animation.Interpolator;
import android.widget.ImageView;
import androidx.core.graphics.ColorUtils;
import com.android.internal.statusbar.StatusBarIcon;
import com.android.internal.util.ContrastColorUtil;
import com.android.systemui.R$bool;
import com.android.systemui.R$color;
import com.android.systemui.R$dimen;
import com.android.systemui.R$drawable;
import com.android.systemui.R$id;
import com.android.systemui.R$string;
import com.android.systemui.animation.Interpolators;
import com.android.systemui.plugins.DarkIconDispatcher;
import com.android.systemui.statusbar.notification.NotificationIconDozeHelper;
import com.android.systemui.statusbar.notification.NotificationUtils;
import java.text.NumberFormat;
import java.util.Arrays;

public class StatusBarIconView extends AnimatedImageView implements StatusIconDisplayable {
    private static final Property<StatusBarIconView, Float> DOT_APPEAR_AMOUNT = new FloatProperty<StatusBarIconView>("dot_appear_amount") {
        public void setValue(StatusBarIconView statusBarIconView, float f) {
            statusBarIconView.setDotAppearAmount(f);
        }

        public Float get(StatusBarIconView statusBarIconView) {
            return Float.valueOf(statusBarIconView.getDotAppearAmount());
        }
    };
    private static final Property<StatusBarIconView, Float> ICON_APPEAR_AMOUNT = new FloatProperty<StatusBarIconView>("iconAppearAmount") {
        public void setValue(StatusBarIconView statusBarIconView, float f) {
            statusBarIconView.setIconAppearAmount(f);
        }

        public Float get(StatusBarIconView statusBarIconView) {
            return Float.valueOf(statusBarIconView.getIconAppearAmount());
        }
    };
    private int ANIMATION_DURATION_FAST;
    private float CAROUSEL_ICON_SCALE;
    private boolean mAlwaysScaleIcon;
    /* access modifiers changed from: private */
    public int mAnimationStartColor;
    private final boolean mBlocked;
    private int mCachedContrastBackgroundColor;
    private int mCarouselIconColor;
    private int mCarouselIconSelectedColor;
    private final Paint mCirclePaint;
    private int mCirclePaintColor;
    private int mCirclePaintSelectedColor;
    /* access modifiers changed from: private */
    public ValueAnimator mColorAnimator;
    private final ValueAnimator.AnimatorUpdateListener mColorUpdater;
    private int mContrastedDrawableColor;
    private int mCurrentSetColor;
    private int mDecorColor;
    private int mDensity;
    private boolean mDismissed;
    /* access modifiers changed from: private */
    public ObjectAnimator mDotAnimator;
    private float mDotAppearAmount;
    private final Paint mDotPaint;
    private float mDotRadius;
    private float mDozeAmount;
    private final NotificationIconDozeHelper mDozer;
    private int mDrawableColor;
    private boolean mHasTintedIcon;
    private StatusBarIcon mIcon;
    private float mIconAppearAmount;
    /* access modifiers changed from: private */
    public ObjectAnimator mIconAppearAnimator;
    private int mIconColor;
    private float mIconScale;
    private boolean mIncreasedSize;
    private boolean mIsCarousel;
    private boolean mIsInShelf;
    private boolean mIsSelected;
    private Runnable mLayoutRunnable;
    private float[] mMatrix;
    private ColorMatrixColorFilter mMatrixColorFilter;
    private boolean mNightMode;
    private StatusBarNotification mNotification;
    private Drawable mNumberBackground;
    private Paint mNumberPain;
    private String mNumberText;
    private int mNumberX;
    private int mNumberY;
    private Runnable mOnDismissListener;
    private OnVisibilityChangedListener mOnVisibilityChangedListener;
    private boolean mShowsConversation;
    @ViewDebug.ExportedProperty
    private String mSlot;
    private int mStaticDotRadius;
    private int mStatusBarIconDrawingSize;
    private int mStatusBarIconDrawingSizeIncreased;
    private int mStatusBarIconSize;
    private float mSystemIconDefaultScale;
    private float mSystemIconDesiredHeight;
    private float mSystemIconIntrinsicHeight;
    private TintedImageView mTintedImageView;
    private int mVisibleState;

    public interface OnVisibilityChangedListener {
        void onVisibilityChanged(int i);
    }

    public boolean hasOverlappingRendering() {
        return false;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0(ValueAnimator valueAnimator) {
        setColorInternal(NotificationUtils.interpolateColors(this.mAnimationStartColor, this.mIconColor, valueAnimator.getAnimatedFraction()));
    }

    public StatusBarIconView(Context context, String str, StatusBarNotification statusBarNotification) {
        this(context, str, statusBarNotification, false);
    }

    public StatusBarIconView(Context context, String str, StatusBarNotification statusBarNotification, boolean z) {
        super(context);
        this.mSystemIconDesiredHeight = 15.0f;
        this.mSystemIconIntrinsicHeight = 17.0f;
        this.mSystemIconDefaultScale = 15.0f / 17.0f;
        this.ANIMATION_DURATION_FAST = 100;
        boolean z2 = true;
        this.mStatusBarIconDrawingSizeIncreased = 1;
        this.mStatusBarIconDrawingSize = 1;
        this.mStatusBarIconSize = 1;
        this.mIconScale = 1.0f;
        this.mDotPaint = new Paint(1);
        this.mVisibleState = 0;
        this.mIconAppearAmount = 1.0f;
        this.mCurrentSetColor = 0;
        this.mAnimationStartColor = 0;
        this.mColorUpdater = new StatusBarIconView$$ExternalSyntheticLambda0(this);
        this.mCachedContrastBackgroundColor = 0;
        this.mHasTintedIcon = false;
        this.mIsCarousel = false;
        this.mCirclePaint = new Paint(1);
        this.mIsSelected = false;
        this.CAROUSEL_ICON_SCALE = 0.55f;
        this.mDozer = new NotificationIconDozeHelper(context);
        this.mBlocked = z;
        this.mSlot = str;
        Paint paint = new Paint();
        this.mNumberPain = paint;
        paint.setTextAlign(Paint.Align.CENTER);
        this.mNumberPain.setColor(context.getColor(R$drawable.notification_number_text_color));
        this.mNumberPain.setAntiAlias(true);
        setNotification(statusBarNotification);
        setScaleType(ImageView.ScaleType.CENTER);
        this.mDensity = context.getResources().getDisplayMetrics().densityDpi;
        this.mNightMode = (context.getResources().getConfiguration().uiMode & 48) != 32 ? false : z2;
        initializeDecorColor();
        reloadDimens();
        maybeUpdateIconScaleDimens();
    }

    public StatusBarIconView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mSystemIconDesiredHeight = 15.0f;
        this.mSystemIconIntrinsicHeight = 17.0f;
        this.mSystemIconDefaultScale = 15.0f / 17.0f;
        this.ANIMATION_DURATION_FAST = 100;
        this.mStatusBarIconDrawingSizeIncreased = 1;
        this.mStatusBarIconDrawingSize = 1;
        this.mStatusBarIconSize = 1;
        this.mIconScale = 1.0f;
        this.mDotPaint = new Paint(1);
        this.mVisibleState = 0;
        this.mIconAppearAmount = 1.0f;
        this.mCurrentSetColor = 0;
        this.mAnimationStartColor = 0;
        this.mColorUpdater = new StatusBarIconView$$ExternalSyntheticLambda0(this);
        this.mCachedContrastBackgroundColor = 0;
        this.mHasTintedIcon = false;
        this.mIsCarousel = false;
        this.mCirclePaint = new Paint(1);
        this.mIsSelected = false;
        this.CAROUSEL_ICON_SCALE = 0.55f;
        this.mDozer = new NotificationIconDozeHelper(context);
        this.mBlocked = false;
        this.mAlwaysScaleIcon = true;
        reloadDimens();
        maybeUpdateIconScaleDimens();
        this.mDensity = context.getResources().getDisplayMetrics().densityDpi;
    }

    private void maybeUpdateIconScaleDimens() {
        if (this.mNotification != null || this.mAlwaysScaleIcon) {
            updateIconScaleForNotifications();
        } else {
            updateIconScaleForSystemIcons();
        }
    }

    private void updateIconScaleForNotifications() {
        this.mIconScale = ((float) (this.mIncreasedSize ? this.mStatusBarIconDrawingSizeIncreased : this.mStatusBarIconDrawingSize)) / ((float) this.mStatusBarIconSize);
        if (this.mIsCarousel) {
            this.mIconScale = this.CAROUSEL_ICON_SCALE;
            setScaleType(ImageView.ScaleType.FIT_XY);
        }
        updatePivot();
    }

    private void updateIconScaleForSystemIcons() {
        float iconHeight = getIconHeight();
        if (iconHeight != 0.0f) {
            this.mIconScale = this.mSystemIconDesiredHeight / iconHeight;
        } else {
            this.mIconScale = this.mSystemIconDefaultScale;
        }
    }

    private float getIconHeight() {
        if (getDrawable() != null) {
            return (float) getDrawable().getIntrinsicHeight();
        }
        return this.mSystemIconIntrinsicHeight;
    }

    public float getIconScaleIncreased() {
        return ((float) this.mStatusBarIconDrawingSizeIncreased) / ((float) this.mStatusBarIconDrawingSize);
    }

    public float getIconScale() {
        return this.mIconScale;
    }

    /* access modifiers changed from: protected */
    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        int i = configuration.densityDpi;
        if (i != this.mDensity) {
            this.mDensity = i;
            reloadDimens();
            updateDrawable();
            maybeUpdateIconScaleDimens();
        }
        boolean z = (configuration.uiMode & 48) == 32;
        if (z != this.mNightMode) {
            this.mNightMode = z;
            initializeDecorColor();
        }
    }

    private void reloadDimens() {
        boolean z = this.mDotRadius == ((float) this.mStaticDotRadius);
        Resources resources = getResources();
        if (this.mIsCarousel) {
            this.mStaticDotRadius = getResources().getDimensionPixelSize(R$dimen.cli_overflow_dot_radius);
        } else {
            this.mStaticDotRadius = resources.getDimensionPixelSize(R$dimen.overflow_dot_radius);
        }
        this.mStatusBarIconSize = resources.getDimensionPixelSize(R$dimen.status_bar_icon_size);
        this.mStatusBarIconDrawingSizeIncreased = resources.getDimensionPixelSize(R$dimen.status_bar_icon_drawing_size_dark);
        this.mStatusBarIconDrawingSize = resources.getDimensionPixelSize(R$dimen.status_bar_icon_drawing_size);
        if (z) {
            this.mDotRadius = (float) this.mStaticDotRadius;
        }
        this.mSystemIconDesiredHeight = resources.getDimension(17105539);
        float dimension = resources.getDimension(17105538);
        this.mSystemIconIntrinsicHeight = dimension;
        this.mSystemIconDefaultScale = this.mSystemIconDesiredHeight / dimension;
    }

    public void setNotification(StatusBarNotification statusBarNotification) {
        this.mNotification = statusBarNotification;
        if (statusBarNotification != null) {
            setContentDescription(statusBarNotification.getNotification());
        }
        maybeUpdateIconScaleDimens();
    }

    public boolean equalIcons(Icon icon, Icon icon2) {
        if (icon == icon2) {
            return true;
        }
        if (icon.getType() != icon2.getType()) {
            return false;
        }
        int type = icon.getType();
        if (type != 2) {
            if (type == 4 || type == 6) {
                return icon.getUriString().equals(icon2.getUriString());
            }
            return false;
        } else if (!icon.getResPackage().equals(icon2.getResPackage()) || icon.getResId() != icon2.getResId()) {
            return false;
        } else {
            return true;
        }
    }

    public boolean set(StatusBarIcon statusBarIcon) {
        StatusBarIcon statusBarIcon2 = this.mIcon;
        int i = 0;
        boolean z = statusBarIcon2 != null && equalIcons(statusBarIcon2.icon, statusBarIcon.icon);
        boolean z2 = z && this.mIcon.iconLevel == statusBarIcon.iconLevel;
        StatusBarIcon statusBarIcon3 = this.mIcon;
        boolean z3 = statusBarIcon3 != null && statusBarIcon3.visible == statusBarIcon.visible;
        boolean z4 = statusBarIcon3 != null && statusBarIcon3.number == statusBarIcon.number;
        this.mIcon = statusBarIcon.clone();
        setContentDescription(statusBarIcon.contentDescription);
        if (!z) {
            if (!updateDrawable(false)) {
                return false;
            }
            setTag(R$id.icon_is_grayscale, (Object) null);
            maybeUpdateIconScaleDimens();
        }
        if (!z2) {
            setImageLevel(statusBarIcon.iconLevel);
        }
        if (!z4) {
            if (statusBarIcon.number <= 0 || !getContext().getResources().getBoolean(R$bool.config_statusBarShowNumber)) {
                this.mNumberBackground = null;
                this.mNumberText = null;
            } else {
                if (this.mNumberBackground == null) {
                    this.mNumberBackground = getContext().getResources().getDrawable(R$drawable.ic_notification_overlay);
                }
                placeNumber();
            }
            invalidate();
        }
        if (!z3) {
            if (!statusBarIcon.visible || this.mBlocked) {
                i = 8;
            }
            setVisibility(i);
        }
        return true;
    }

    public void updateDrawable() {
        updateDrawable(true);
    }

    /* JADX WARNING: Removed duplicated region for block: B:21:0x007a  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private boolean updateDrawable(boolean r7) {
        /*
            r6 = this;
            java.lang.String r0 = "StatusBarIconView"
            com.android.internal.statusbar.StatusBarIcon r1 = r6.mIcon
            r2 = 0
            if (r1 != 0) goto L_0x0008
            return r2
        L_0x0008:
            android.graphics.drawable.Drawable r1 = r6.getIcon(r1)     // Catch:{ OutOfMemoryError -> 0x00b4 }
            if (r1 != 0) goto L_0x0031
            java.lang.StringBuilder r7 = new java.lang.StringBuilder
            r7.<init>()
            java.lang.String r1 = "No icon for slot "
            r7.append(r1)
            java.lang.String r1 = r6.mSlot
            r7.append(r1)
            java.lang.String r1 = "; "
            r7.append(r1)
            com.android.internal.statusbar.StatusBarIcon r6 = r6.mIcon
            android.graphics.drawable.Icon r6 = r6.icon
            r7.append(r6)
            java.lang.String r6 = r7.toString()
            android.util.Log.w(r0, r6)
            return r2
        L_0x0031:
            boolean r3 = r1 instanceof android.graphics.drawable.BitmapDrawable
            java.lang.String r4 = "Drawable is too large ("
            if (r3 == 0) goto L_0x0069
            r3 = r1
            android.graphics.drawable.BitmapDrawable r3 = (android.graphics.drawable.BitmapDrawable) r3
            android.graphics.Bitmap r5 = r3.getBitmap()
            if (r5 == 0) goto L_0x0069
            android.graphics.Bitmap r3 = r3.getBitmap()
            int r3 = r3.getByteCount()
            r5 = 104857600(0x6400000, float:3.6111186E-35)
            if (r3 <= r5) goto L_0x0078
            java.lang.StringBuilder r7 = new java.lang.StringBuilder
            r7.<init>()
            r7.append(r4)
            r7.append(r3)
            java.lang.String r1 = " bytes) "
            r7.append(r1)
            com.android.internal.statusbar.StatusBarIcon r6 = r6.mIcon
            r7.append(r6)
            java.lang.String r6 = r7.toString()
            android.util.Log.w(r0, r6)
            return r2
        L_0x0069:
            int r3 = r1.getIntrinsicWidth()
            r5 = 5000(0x1388, float:7.006E-42)
            if (r3 > r5) goto L_0x0086
            int r3 = r1.getIntrinsicHeight()
            if (r3 <= r5) goto L_0x0078
            goto L_0x0086
        L_0x0078:
            if (r7 == 0) goto L_0x007e
            r7 = 0
            r6.setImageDrawable(r7)
        L_0x007e:
            r6.setImageDrawable(r1)
            r6.updateTintedDrawable()
            r6 = 1
            return r6
        L_0x0086:
            java.lang.StringBuilder r7 = new java.lang.StringBuilder
            r7.<init>()
            r7.append(r4)
            int r3 = r1.getIntrinsicWidth()
            r7.append(r3)
            java.lang.String r3 = "x"
            r7.append(r3)
            int r1 = r1.getIntrinsicHeight()
            r7.append(r1)
            java.lang.String r1 = ") "
            r7.append(r1)
            com.android.internal.statusbar.StatusBarIcon r6 = r6.mIcon
            r7.append(r6)
            java.lang.String r6 = r7.toString()
            android.util.Log.w(r0, r6)
            return r2
        L_0x00b4:
            java.lang.StringBuilder r7 = new java.lang.StringBuilder
            r7.<init>()
            java.lang.String r1 = "OOM while inflating "
            r7.append(r1)
            com.android.internal.statusbar.StatusBarIcon r1 = r6.mIcon
            android.graphics.drawable.Icon r1 = r1.icon
            r7.append(r1)
            java.lang.String r1 = " for slot "
            r7.append(r1)
            java.lang.String r6 = r6.mSlot
            r7.append(r6)
            java.lang.String r6 = r7.toString()
            android.util.Log.w(r0, r6)
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.statusbar.StatusBarIconView.updateDrawable(boolean):boolean");
    }

    public Icon getSourceIcon() {
        return this.mIcon.icon;
    }

    /* access modifiers changed from: package-private */
    public Drawable getIcon(StatusBarIcon statusBarIcon) {
        return getIcon(statusBarIcon, false);
    }

    private Drawable getIcon(StatusBarIcon statusBarIcon, boolean z) {
        Context context = getContext();
        StatusBarNotification statusBarNotification = this.mNotification;
        if (statusBarNotification != null) {
            context = statusBarNotification.getPackageContext(getContext());
        }
        Context context2 = getContext();
        if (context == null) {
            context = getContext();
        }
        return getIcon(context2, context, statusBarIcon, z);
    }

    private static Drawable getIcon(Context context, Context context2, StatusBarIcon statusBarIcon, boolean z) {
        int identifier = statusBarIcon.user.getIdentifier();
        if (identifier == -1) {
            identifier = 0;
        }
        Icon tintedIcon = z ? statusBarIcon.getTintedIcon() : statusBarIcon.icon;
        if (tintedIcon == null) {
            return null;
        }
        Drawable loadDrawableAsUser = tintedIcon.loadDrawableAsUser(context2, identifier);
        TypedValue typedValue = new TypedValue();
        context.getResources().getValue(R$dimen.status_bar_icon_scale_factor, typedValue, true);
        float f = typedValue.getFloat();
        if (f == 1.0f) {
            return loadDrawableAsUser;
        }
        return new ScalingDrawableWrapper(loadDrawableAsUser, f);
    }

    public StatusBarIcon getStatusBarIcon() {
        return this.mIcon;
    }

    public void onInitializeAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        super.onInitializeAccessibilityEvent(accessibilityEvent);
        StatusBarNotification statusBarNotification = this.mNotification;
        if (statusBarNotification != null) {
            accessibilityEvent.setParcelableData(statusBarNotification.getNotification());
        }
    }

    /* access modifiers changed from: protected */
    public void onSizeChanged(int i, int i2, int i3, int i4) {
        super.onSizeChanged(i, i2, i3, i4);
        if (this.mNumberBackground != null) {
            placeNumber();
        }
    }

    public void onRtlPropertiesChanged(int i) {
        super.onRtlPropertiesChanged(i);
        updateDrawable();
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Removed duplicated region for block: B:33:0x00db  */
    /* JADX WARNING: Removed duplicated region for block: B:34:0x00ef  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onDraw(android.graphics.Canvas r7) {
        /*
            r6 = this;
            boolean r0 = r6.mIsCarousel
            r1 = 0
            if (r0 == 0) goto L_0x0028
            float r0 = r6.mIconAppearAmount
            int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r0 <= 0) goto L_0x0028
            int r0 = r6.getWidth()
            int r0 = r0 / 2
            float r0 = (float) r0
            int r2 = r6.getHeight()
            int r2 = r2 / 2
            float r2 = (float) r2
            int r3 = r6.getWidth()
            int r3 = r3 / 2
            float r3 = (float) r3
            float r4 = r6.mIconAppearAmount
            float r3 = r3 * r4
            android.graphics.Paint r4 = r6.mCirclePaint
            r7.drawCircle(r0, r2, r3, r4)
        L_0x0028:
            float r0 = r6.mIconAppearAmount
            int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r0 <= 0) goto L_0x0089
            r7.save()
            float r0 = r6.mIconScale
            float r2 = r6.mIconAppearAmount
            float r3 = r0 * r2
            float r0 = r0 * r2
            int r2 = r6.getWidth()
            int r2 = r2 / 2
            float r2 = (float) r2
            int r4 = r6.getHeight()
            int r4 = r4 / 2
            float r4 = (float) r4
            r7.scale(r3, r0, r2, r4)
            super.onDraw(r7)
            boolean r0 = r6.mHasTintedIcon
            if (r0 == 0) goto L_0x0086
            com.android.systemui.statusbar.StatusBarIconView$TintedImageView r0 = r6.mTintedImageView
            if (r0 == 0) goto L_0x0086
            int r0 = r6.getWidth()
            int r2 = r6.getHeight()
            com.android.systemui.statusbar.StatusBarIconView$TintedImageView r3 = r6.mTintedImageView
            int r3 = r3.getMeasuredWidth()
            if (r3 != r0) goto L_0x006c
            com.android.systemui.statusbar.StatusBarIconView$TintedImageView r3 = r6.mTintedImageView
            int r3 = r3.getMeasuredHeight()
            if (r3 == r2) goto L_0x0081
        L_0x006c:
            r3 = 1073741824(0x40000000, float:2.0)
            int r4 = android.view.View.MeasureSpec.makeMeasureSpec(r0, r3)
            int r3 = android.view.View.MeasureSpec.makeMeasureSpec(r2, r3)
            com.android.systemui.statusbar.StatusBarIconView$TintedImageView r5 = r6.mTintedImageView
            r5.measure(r4, r3)
            com.android.systemui.statusbar.StatusBarIconView$TintedImageView r3 = r6.mTintedImageView
            r4 = 0
            r3.layout(r4, r4, r0, r2)
        L_0x0081:
            com.android.systemui.statusbar.StatusBarIconView$TintedImageView r0 = r6.mTintedImageView
            r0.onDraw(r7)
        L_0x0086:
            r7.restore()
        L_0x0089:
            android.graphics.drawable.Drawable r0 = r6.mNumberBackground
            if (r0 == 0) goto L_0x009d
            r0.draw(r7)
            java.lang.String r0 = r6.mNumberText
            int r2 = r6.mNumberX
            float r2 = (float) r2
            int r3 = r6.mNumberY
            float r3 = (float) r3
            android.graphics.Paint r4 = r6.mNumberPain
            r7.drawText(r0, r2, r3, r4)
        L_0x009d:
            float r0 = r6.mDotAppearAmount
            int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r0 == 0) goto L_0x010e
            int r0 = r6.mDecorColor
            int r0 = android.graphics.Color.alpha(r0)
            float r0 = (float) r0
            r1 = 1132396544(0x437f0000, float:255.0)
            float r0 = r0 / r1
            float r2 = r6.mDotAppearAmount
            r3 = 1065353216(0x3f800000, float:1.0)
            int r4 = (r2 > r3 ? 1 : (r2 == r3 ? 0 : -1))
            if (r4 > 0) goto L_0x00b9
            float r3 = r6.mDotRadius
        L_0x00b7:
            float r3 = r3 * r2
            goto L_0x00d0
        L_0x00b9:
            float r2 = r2 - r3
            float r3 = r3 - r2
            float r0 = r0 * r3
            boolean r3 = r6.mIsCarousel
            if (r3 == 0) goto L_0x00c3
            float r3 = r6.mDotRadius
            goto L_0x00b7
        L_0x00c3:
            float r3 = r6.mDotRadius
            int r4 = r6.getWidth()
            int r4 = r4 / 4
            float r4 = (float) r4
            float r3 = com.android.systemui.statusbar.notification.NotificationUtils.interpolate(r3, r4, r2)
        L_0x00d0:
            android.graphics.Paint r2 = r6.mDotPaint
            float r0 = r0 * r1
            int r0 = (int) r0
            r2.setAlpha(r0)
            boolean r0 = r6.mIsCarousel
            if (r0 == 0) goto L_0x00ef
            int r0 = r6.getWidth()
            int r0 = r0 / 2
            float r0 = (float) r0
            int r1 = r6.getHeight()
            int r1 = r1 / 2
            float r1 = (float) r1
            android.graphics.Paint r6 = r6.mDotPaint
            r7.drawCircle(r0, r1, r3, r6)
            goto L_0x010e
        L_0x00ef:
            int r0 = r6.getWidth()
            int r1 = r6.mStatusBarIconSize
            if (r0 >= r1) goto L_0x00ff
            int r0 = r6.getWidth()
            int r0 = r0 / 2
            float r0 = (float) r0
            goto L_0x0102
        L_0x00ff:
            int r1 = r1 / 2
            float r0 = (float) r1
        L_0x0102:
            int r1 = r6.getHeight()
            int r1 = r1 / 2
            float r1 = (float) r1
            android.graphics.Paint r6 = r6.mDotPaint
            r7.drawCircle(r0, r1, r3, r6)
        L_0x010e:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.statusbar.StatusBarIconView.onDraw(android.graphics.Canvas):void");
    }

    /* access modifiers changed from: protected */
    public void debug(int i) {
        super.debug(i);
        Log.d("View", ImageView.debugIndent(i) + "slot=" + this.mSlot);
        Log.d("View", ImageView.debugIndent(i) + "icon=" + this.mIcon);
    }

    /* access modifiers changed from: package-private */
    public void placeNumber() {
        String str;
        if (this.mIcon.number > getContext().getResources().getInteger(17694723)) {
            str = getContext().getResources().getString(17039383);
        } else {
            str = NumberFormat.getIntegerInstance().format((long) this.mIcon.number);
        }
        this.mNumberText = str;
        int width = getWidth();
        int height = getHeight();
        Rect rect = new Rect();
        this.mNumberPain.getTextBounds(str, 0, str.length(), rect);
        int i = rect.right - rect.left;
        int i2 = rect.bottom - rect.top;
        this.mNumberBackground.getPadding(rect);
        int i3 = rect.left + i + rect.right;
        if (i3 < this.mNumberBackground.getMinimumWidth()) {
            i3 = this.mNumberBackground.getMinimumWidth();
        }
        int i4 = rect.right;
        this.mNumberX = (width - i4) - (((i3 - i4) - rect.left) / 2);
        int i5 = rect.top + i2 + rect.bottom;
        if (i5 < this.mNumberBackground.getMinimumWidth()) {
            i5 = this.mNumberBackground.getMinimumWidth();
        }
        int i6 = rect.bottom;
        this.mNumberY = (height - i6) - ((((i5 - rect.top) - i2) - i6) / 2);
        this.mNumberBackground.setBounds(width - i3, height - i5, width, height);
    }

    private void setContentDescription(Notification notification) {
        if (notification != null) {
            String contentDescForNotification = contentDescForNotification(this.mContext, notification);
            if (!TextUtils.isEmpty(contentDescForNotification)) {
                setContentDescription(contentDescForNotification);
            }
        }
    }

    public String toString() {
        return "StatusBarIconView(slot=" + this.mSlot + " icon=" + this.mIcon + " notification=" + this.mNotification + ")";
    }

    public StatusBarNotification getNotification() {
        return this.mNotification;
    }

    public String getSlot() {
        return this.mSlot;
    }

    public static String contentDescForNotification(Context context, Notification notification) {
        CharSequence charSequence;
        CharSequence charSequence2 = "";
        try {
            charSequence = Notification.Builder.recoverBuilder(context, notification).loadHeaderAppName();
        } catch (RuntimeException e) {
            Log.e("StatusBarIconView", "Unable to recover builder", e);
            Parcelable parcelable = notification.extras.getParcelable("android.appInfo");
            charSequence = parcelable instanceof ApplicationInfo ? String.valueOf(((ApplicationInfo) parcelable).loadLabel(context.getPackageManager())) : charSequence2;
        }
        CharSequence charSequence3 = notification.extras.getCharSequence("android.title");
        CharSequence charSequence4 = notification.extras.getCharSequence("android.text");
        CharSequence charSequence5 = notification.tickerText;
        if (TextUtils.equals(charSequence3, charSequence)) {
            charSequence3 = charSequence4;
        }
        if (!TextUtils.isEmpty(charSequence3)) {
            charSequence2 = charSequence3;
        } else if (!TextUtils.isEmpty(charSequence5)) {
            charSequence2 = charSequence5;
        }
        return context.getString(R$string.accessibility_desc_notification_icon, new Object[]{charSequence, charSequence2});
    }

    public void setDecorColor(int i) {
        this.mDecorColor = i;
        updateDecorColor();
    }

    private void initializeDecorColor() {
        if (this.mNotification == null) {
            return;
        }
        if (this.mIsCarousel) {
            setDecorColor(getContext().getColor(R$color.zz_moto_carousel_dot));
        } else {
            setDecorColor(getContext().getColor(this.mNightMode ? 17170987 : 17170988));
        }
    }

    private void updateDecorColor() {
        int interpolateColors = NotificationUtils.interpolateColors(this.mDecorColor, -1, this.mDozeAmount);
        if (this.mDotPaint.getColor() != interpolateColors) {
            this.mDotPaint.setColor(interpolateColors);
            if (this.mDotAppearAmount != 0.0f) {
                invalidate();
            }
        }
    }

    public void setStaticDrawableColor(int i) {
        this.mDrawableColor = i;
        setColorInternal(i);
        updateContrastedStaticColor();
        this.mIconColor = i;
        this.mDozer.setColor(i);
    }

    private void setColorInternal(int i) {
        this.mCurrentSetColor = i;
        updateIconColor();
    }

    private void updateIconColor() {
        if (this.mShowsConversation) {
            setColorFilter((ColorFilter) null);
        } else if (this.mCurrentSetColor != 0) {
            if (this.mMatrixColorFilter == null) {
                this.mMatrix = new float[20];
                this.mMatrixColorFilter = new ColorMatrixColorFilter(this.mMatrix);
            }
            updateTintMatrix(this.mMatrix, NotificationUtils.interpolateColors(this.mCurrentSetColor, -1, this.mDozeAmount), this.mDozeAmount * 0.67f);
            this.mMatrixColorFilter.setColorMatrixArray(this.mMatrix);
            setColorFilter((ColorFilter) null);
            setColorFilter(this.mMatrixColorFilter);
        } else {
            this.mDozer.updateGrayscale(this, this.mDozeAmount);
        }
    }

    private static void updateTintMatrix(float[] fArr, int i, float f) {
        Arrays.fill(fArr, 0.0f);
        fArr[4] = (float) Color.red(i);
        fArr[9] = (float) Color.green(i);
        fArr[14] = (float) Color.blue(i);
        fArr[18] = (((float) Color.alpha(i)) / 255.0f) + f;
    }

    public void setIconColor(int i, boolean z) {
        if (this.mIconColor != i) {
            this.mIconColor = i;
            ValueAnimator valueAnimator = this.mColorAnimator;
            if (valueAnimator != null) {
                valueAnimator.cancel();
            }
            int i2 = this.mCurrentSetColor;
            if (i2 != i) {
                if (!z || i2 == 0) {
                    setColorInternal(i);
                    return;
                }
                this.mAnimationStartColor = i2;
                ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
                this.mColorAnimator = ofFloat;
                ofFloat.setInterpolator(Interpolators.FAST_OUT_SLOW_IN);
                this.mColorAnimator.setDuration((long) this.ANIMATION_DURATION_FAST);
                this.mColorAnimator.addUpdateListener(this.mColorUpdater);
                this.mColorAnimator.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animator) {
                        ValueAnimator unused = StatusBarIconView.this.mColorAnimator = null;
                        int unused2 = StatusBarIconView.this.mAnimationStartColor = 0;
                    }
                });
                this.mColorAnimator.start();
            }
        }
    }

    public int getStaticDrawableColor() {
        return this.mDrawableColor;
    }

    /* access modifiers changed from: package-private */
    public int getContrastedStaticDrawableColor(int i) {
        if (this.mCachedContrastBackgroundColor != i) {
            this.mCachedContrastBackgroundColor = i;
            updateContrastedStaticColor();
        }
        return this.mContrastedDrawableColor;
    }

    private void updateContrastedStaticColor() {
        if (Color.alpha(this.mCachedContrastBackgroundColor) != 255) {
            this.mContrastedDrawableColor = this.mDrawableColor;
            return;
        }
        int i = this.mDrawableColor;
        if (!ContrastColorUtil.satisfiesTextContrast(this.mCachedContrastBackgroundColor, i)) {
            float[] fArr = new float[3];
            ColorUtils.colorToHSL(this.mDrawableColor, fArr);
            if (fArr[1] < 0.2f) {
                i = 0;
            }
            i = ContrastColorUtil.resolveContrastColor(this.mContext, i, this.mCachedContrastBackgroundColor, !ContrastColorUtil.isColorLight(this.mCachedContrastBackgroundColor));
        }
        this.mContrastedDrawableColor = i;
    }

    public void setVisibleState(int i) {
        setVisibleState(i, true, (Runnable) null);
    }

    public void setVisibleState(int i, boolean z) {
        setVisibleState(i, z, (Runnable) null);
    }

    public void setVisibleState(int i, boolean z, Runnable runnable) {
        setVisibleState(i, z, runnable, 0);
    }

    public void setVisibleState(int i, boolean z, Runnable runnable, long j) {
        float f;
        Interpolator interpolator;
        boolean z2;
        int i2 = i;
        final Runnable runnable2 = runnable;
        int i3 = this.mVisibleState;
        boolean z3 = false;
        float f2 = 1.0f;
        if (i2 != i3) {
            this.mVisibleState = i2;
            ObjectAnimator objectAnimator = this.mIconAppearAnimator;
            if (objectAnimator != null) {
                objectAnimator.cancel();
            }
            ObjectAnimator objectAnimator2 = this.mDotAnimator;
            if (objectAnimator2 != null) {
                objectAnimator2.cancel();
            }
            if (z) {
                Interpolator interpolator2 = Interpolators.FAST_OUT_LINEAR_IN;
                if (i2 == 0) {
                    interpolator = Interpolators.LINEAR_OUT_SLOW_IN;
                    f = 1.0f;
                } else {
                    f = 0.0f;
                    interpolator = interpolator2;
                }
                float iconAppearAmount = getIconAppearAmount();
                if (f != iconAppearAmount) {
                    ObjectAnimator ofFloat = ObjectAnimator.ofFloat(this, ICON_APPEAR_AMOUNT, new float[]{iconAppearAmount, f});
                    this.mIconAppearAnimator = ofFloat;
                    ofFloat.setInterpolator(interpolator);
                    this.mIconAppearAnimator.setDuration(j == 0 ? (long) this.ANIMATION_DURATION_FAST : j);
                    this.mIconAppearAnimator.addListener(new AnimatorListenerAdapter() {
                        public void onAnimationEnd(Animator animator) {
                            ObjectAnimator unused = StatusBarIconView.this.mIconAppearAnimator = null;
                            StatusBarIconView.this.runRunnable(runnable2);
                        }
                    });
                    this.mIconAppearAnimator.start();
                    z2 = true;
                } else {
                    z2 = false;
                }
                float f3 = i2 == 0 ? 2.0f : 0.0f;
                if (i2 == 1) {
                    interpolator2 = Interpolators.LINEAR_OUT_SLOW_IN;
                } else {
                    f2 = f3;
                }
                float dotAppearAmount = getDotAppearAmount();
                if (f2 != dotAppearAmount) {
                    ObjectAnimator ofFloat2 = ObjectAnimator.ofFloat(this, DOT_APPEAR_AMOUNT, new float[]{dotAppearAmount, f2});
                    this.mDotAnimator = ofFloat2;
                    ofFloat2.setInterpolator(interpolator2);
                    this.mDotAnimator.setDuration(j == 0 ? (long) this.ANIMATION_DURATION_FAST : j);
                    final boolean z4 = !z2;
                    this.mDotAnimator.addListener(new AnimatorListenerAdapter() {
                        public void onAnimationEnd(Animator animator) {
                            ObjectAnimator unused = StatusBarIconView.this.mDotAnimator = null;
                            if (z4) {
                                StatusBarIconView.this.runRunnable(runnable2);
                            }
                        }
                    });
                    this.mDotAnimator.start();
                    z3 = true;
                } else {
                    z3 = z2;
                }
            } else {
                setIconAppearAmount(i2 == 0 ? 1.0f : 0.0f);
                if (i2 != 1) {
                    f2 = i2 == 0 ? 2.0f : 0.0f;
                }
                setDotAppearAmount(f2);
            }
        } else if (this.mIsCarousel && i3 == 0 && this.mIconAppearAmount != 1.0f) {
            setIconAppearAmount(1.0f);
        }
        if (!z3) {
            runRunnable(runnable2);
        }
    }

    /* access modifiers changed from: private */
    public void runRunnable(Runnable runnable) {
        if (runnable != null) {
            runnable.run();
        }
    }

    public void setIconAppearAmount(float f) {
        if (this.mIconAppearAmount != f) {
            this.mIconAppearAmount = f;
            invalidate();
        }
    }

    public float getIconAppearAmount() {
        return this.mIconAppearAmount;
    }

    public int getVisibleState() {
        return this.mVisibleState;
    }

    public void setDotAppearAmount(float f) {
        if (this.mDotAppearAmount != f) {
            this.mDotAppearAmount = f;
            invalidate();
        }
    }

    public void setVisibility(int i) {
        super.setVisibility(i);
        OnVisibilityChangedListener onVisibilityChangedListener = this.mOnVisibilityChangedListener;
        if (onVisibilityChangedListener != null) {
            onVisibilityChangedListener.onVisibilityChanged(i);
        }
    }

    public float getDotAppearAmount() {
        return this.mDotAppearAmount;
    }

    public void setDozing(boolean z, boolean z2, long j) {
        this.mDozer.setDozing(new StatusBarIconView$$ExternalSyntheticLambda1(this), z, z2, j, this);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$setDozing$1(Float f) {
        this.mDozeAmount = f.floatValue();
        updateDecorColor();
        updateIconColor();
        updateAllowAnimation();
    }

    private void updateAllowAnimation() {
        float f = this.mDozeAmount;
        if (f == 0.0f || f == 1.0f) {
            setAllowAnimation(f == 0.0f);
        }
    }

    public void getDrawingRect(Rect rect) {
        super.getDrawingRect(rect);
        float translationX = getTranslationX();
        float translationY = getTranslationY();
        rect.left = (int) (((float) rect.left) + translationX);
        rect.right = (int) (((float) rect.right) + translationX);
        rect.top = (int) (((float) rect.top) + translationY);
        rect.bottom = (int) (((float) rect.bottom) + translationY);
    }

    public void setIsInShelf(boolean z) {
        this.mIsInShelf = z;
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        super.onLayout(z, i, i2, i3, i4);
        Runnable runnable = this.mLayoutRunnable;
        if (runnable != null) {
            runnable.run();
            this.mLayoutRunnable = null;
        }
        updatePivot();
    }

    private void updatePivot() {
        if (isLayoutRtl()) {
            setPivotX(((this.mIconScale + 1.0f) / 2.0f) * ((float) getWidth()));
        } else {
            setPivotX(((1.0f - this.mIconScale) / 2.0f) * ((float) getWidth()));
        }
        setPivotY((((float) getHeight()) - (this.mIconScale * ((float) getWidth()))) / 2.0f);
    }

    public void executeOnLayout(Runnable runnable) {
        this.mLayoutRunnable = runnable;
    }

    public void setDismissed() {
        this.mDismissed = true;
        Runnable runnable = this.mOnDismissListener;
        if (runnable != null) {
            runnable.run();
        }
    }

    public void setOnDismissListener(Runnable runnable) {
        this.mOnDismissListener = runnable;
    }

    public void onDarkChanged(Rect rect, float f, int i) {
        int tint = DarkIconDispatcher.getTint(rect, this, i);
        setImageTintList(ColorStateList.valueOf(tint));
        setDecorColor(tint);
    }

    public boolean isIconVisible() {
        StatusBarIcon statusBarIcon = this.mIcon;
        return statusBarIcon != null && statusBarIcon.visible;
    }

    public boolean isIconBlocked() {
        return this.mBlocked;
    }

    public void setIncreasedSize(boolean z) {
        this.mIncreasedSize = z;
        maybeUpdateIconScaleDimens();
    }

    public void setShowsConversation(boolean z) {
        if (this.mShowsConversation != z) {
            this.mShowsConversation = z;
            updateIconColor();
        }
    }

    public boolean showsConversation() {
        return this.mShowsConversation;
    }

    public void setIsCarousel(boolean z) {
        this.mIsCarousel = z;
        if (z) {
            this.mDotRadius = (float) getResources().getDimensionPixelSize(R$dimen.cli_overflow_dot_radius);
            setDecorColor(getContext().getColor(R$color.zz_moto_carousel_dot));
            this.mCarouselIconColor = getContext().getColor(R$color.zz_moto_carousel_icon);
            this.mCarouselIconSelectedColor = getContext().getColor(R$color.zz_moto_carousel_icon_selected);
            this.mCirclePaintColor = getContext().getColor(R$color.zz_moto_carousel_icon_bg);
            this.mCirclePaintSelectedColor = getContext().getColor(R$color.zz_moto_carousel_icon_bg_selected);
            setStaticDrawableColor(this.mCarouselIconColor);
            this.mCirclePaint.setColor(this.mCirclePaintColor);
            this.ANIMATION_DURATION_FAST = 500;
        }
    }

    public void setCarouselIconSelectedColor(int i) {
        if (i != -1) {
            this.mCarouselIconSelectedColor = i;
        }
    }

    public void updateColorForced(int i, int i2) {
        this.mCirclePaint.setColor(i);
        this.mDrawableColor = i2;
        setIconColor(i2, false);
    }

    public void setSelected(boolean z) {
        if (this.mIsSelected != z) {
            if (z) {
                this.mCirclePaint.setColor(this.mCirclePaintSelectedColor);
                int i = this.mCarouselIconSelectedColor;
                this.mDrawableColor = i;
                setIconColor(i, true);
            } else {
                this.mCirclePaint.setColor(this.mCirclePaintColor);
                int i2 = this.mCarouselIconColor;
                this.mDrawableColor = i2;
                setIconColor(i2, false);
            }
            this.mIsSelected = z;
        }
    }

    public void setVisibleState(int i, float f) {
        this.mVisibleState = i;
        if (this.mIconAppearAmount != f) {
            if (i == 2) {
                this.mIconAppearAmount = 0.0f;
                this.mDotAppearAmount = 0.0f;
            } else {
                this.mIconAppearAmount = f;
                this.mDotAppearAmount = 0.0f;
            }
            invalidate();
        }
    }

    private boolean updateTintedDrawable() {
        StatusBarIcon statusBarIcon = this.mIcon;
        if (statusBarIcon == null) {
            return false;
        }
        if (statusBarIcon.getTintedIcon() != null) {
            this.mHasTintedIcon = true;
            if (this.mTintedImageView == null) {
                this.mTintedImageView = new TintedImageView(getContext());
            }
            try {
                Drawable icon = getIcon(this.mIcon, true);
                if (icon == null) {
                    Log.w("StatusBarIconView", "No tinted icon for slot " + this.mSlot + "; " + this.mIcon.getTintedIcon());
                    return false;
                }
                if (icon instanceof BitmapDrawable) {
                    BitmapDrawable bitmapDrawable = (BitmapDrawable) icon;
                    if (bitmapDrawable.getBitmap() != null) {
                        int byteCount = bitmapDrawable.getBitmap().getByteCount();
                        if (byteCount > 104857600) {
                            Log.w("StatusBarIconView", "tinted Drawable is too large (" + byteCount + " bytes) " + this.mIcon);
                            return false;
                        }
                        this.mTintedImageView.setImageDrawable(icon);
                        return true;
                    }
                }
                if (icon.getIntrinsicWidth() > 5000 || icon.getIntrinsicHeight() > 5000) {
                    Log.w("StatusBarIconView", "tinted Drawable is too large (" + icon.getIntrinsicWidth() + "x" + icon.getIntrinsicHeight() + ") " + this.mIcon);
                    return false;
                }
                this.mTintedImageView.setImageDrawable(icon);
                return true;
            } catch (OutOfMemoryError unused) {
                Log.w("StatusBarIconView", "OOM while inflating tinted " + this.mIcon.getTintedIcon() + " for slot " + this.mSlot);
                return false;
            }
        } else {
            this.mHasTintedIcon = false;
            return true;
        }
    }

    private class TintedImageView extends ImageView {
        public TintedImageView(Context context) {
            super(context);
        }

        public void onDraw(Canvas canvas) {
            super.onDraw(canvas);
        }
    }
}
