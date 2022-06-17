package com.android.systemui.p006qs.tileimpl;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Animatable2;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import com.android.settingslib.Utils;
import com.android.systemui.R$attr;
import com.android.systemui.R$color;
import com.android.systemui.R$dimen;
import com.android.systemui.R$id;
import com.android.systemui.moto.MotoFeature;
import com.android.systemui.p006qs.AlphaControlledSignalTileView;
import com.android.systemui.plugins.p005qs.QSIconView;
import com.android.systemui.plugins.p005qs.QSTile;
import java.util.Objects;
import java.util.function.Supplier;

/* renamed from: com.android.systemui.qs.tileimpl.QSIconViewImpl */
public class QSIconViewImpl extends QSIconView {
    private boolean mAnimationEnabled = true;
    protected final View mIcon;
    protected int mIconSizePx;
    private boolean mIsNightMode;
    private QSTile.Icon mLastIcon;
    public QSTile.State mQSTileState;
    private int mState = -1;
    private int mTint;

    /* access modifiers changed from: protected */
    public int getIconMeasureMode() {
        return 1073741824;
    }

    public QSIconViewImpl(Context context) {
        super(context);
        boolean z = true;
        Resources resources = context.getResources();
        if (MotoFeature.getInstance(context).isCustomPanelView()) {
            this.mIconSizePx = resources.getDimensionPixelSize(R$dimen.zz_moto_qs_prc_tile_icon_size);
        } else {
            this.mIconSizePx = resources.getDimensionPixelSize(R$dimen.qs_icon_size);
        }
        View createIcon = createIcon();
        this.mIcon = createIcon;
        addView(createIcon);
        this.mIsNightMode = (resources.getConfiguration().uiMode & 48) != 32 ? false : z;
    }

    /* access modifiers changed from: protected */
    public void onConfigurationChanged(Configuration configuration) {
        QSTile.State state;
        super.onConfigurationChanged(configuration);
        if (MotoFeature.getInstance(getContext()).isCustomPanelView()) {
            this.mIconSizePx = getContext().getResources().getDimensionPixelSize(R$dimen.zz_moto_qs_prc_tile_icon_size);
        } else {
            this.mIconSizePx = getContext().getResources().getDimensionPixelSize(R$dimen.qs_icon_size);
        }
        boolean z = (configuration.uiMode & 48) == 32;
        if (this.mIsNightMode != z) {
            this.mIsNightMode = z;
            View view = this.mIcon;
            if ((view instanceof ImageView) && (state = this.mQSTileState) != null) {
                setIcon((ImageView) view, state, true, true);
            }
        }
    }

    public void disableAnimation() {
        this.mAnimationEnabled = false;
    }

    public View getIconView() {
        return this.mIcon;
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        int size = View.MeasureSpec.getSize(i);
        this.mIcon.measure(View.MeasureSpec.makeMeasureSpec(size, getIconMeasureMode()), exactly(this.mIconSizePx));
        setMeasuredDimension(size, this.mIcon.getMeasuredHeight());
    }

    public String toString() {
        StringBuilder sb = new StringBuilder(getClass().getSimpleName());
        sb.append('[');
        sb.append("state=" + this.mState);
        sb.append(", tint=" + this.mTint);
        if (this.mLastIcon != null) {
            sb.append(", lastIcon=" + this.mLastIcon.toString());
        }
        sb.append("]");
        return sb.toString();
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        layout(this.mIcon, (getMeasuredWidth() - this.mIcon.getMeasuredWidth()) / 2, 0);
    }

    public void setIcon(QSTile.State state, boolean z) {
        setIcon((ImageView) this.mIcon, state, z);
    }

    /* access modifiers changed from: protected */
    public void updateIcon(final ImageView imageView, QSTile.State state, boolean z) {
        Drawable drawable;
        Supplier<QSTile.Icon> supplier = state.iconSupplier;
        QSTile.Icon icon = supplier != null ? supplier.get() : state.icon;
        int i = R$id.qs_icon_tag;
        if (!Objects.equals(icon, imageView.getTag(i)) || !Objects.equals(state.slash, imageView.getTag(R$id.qs_slash_tag))) {
            boolean z2 = z && shouldAnimate(imageView);
            this.mLastIcon = icon;
            if (icon != null) {
                drawable = z2 ? icon.getDrawable(this.mContext) : icon.getInvisibleDrawable(this.mContext);
            } else {
                drawable = null;
            }
            int padding = icon != null ? icon.getPadding() : 0;
            if (drawable != null) {
                drawable.setAutoMirrored(false);
                drawable.setLayoutDirection(getLayoutDirection());
            }
            if (imageView instanceof SlashImageView) {
                SlashImageView slashImageView = (SlashImageView) imageView;
                slashImageView.setAnimationEnabled(z2);
                slashImageView.setState((QSTile.SlashState) null, drawable);
            } else {
                imageView.setImageDrawable(drawable);
            }
            imageView.setTag(i, icon);
            imageView.setTag(R$id.qs_slash_tag, state.slash);
            imageView.setPadding(0, padding, 0, padding);
            if (drawable instanceof Animatable2) {
                final Animatable2 animatable2 = (Animatable2) drawable;
                animatable2.start();
                if (state.isTransient) {
                    animatable2.registerAnimationCallback(new Animatable2.AnimationCallback() {
                        public void onAnimationEnd(Drawable drawable) {
                            if (imageView.isShown()) {
                                animatable2.start();
                            }
                        }
                    });
                }
            } else if (drawable instanceof Animatable) {
                ((Animatable) drawable).start();
            }
        }
    }

    private boolean shouldAnimate(ImageView imageView) {
        return this.mAnimationEnabled && imageView.isShown() && imageView.getDrawable() != null;
    }

    /* access modifiers changed from: protected */
    public void setIcon(ImageView imageView, QSTile.State state, boolean z) {
        this.mQSTileState = state;
        setIcon(imageView, state, z, false);
    }

    /* access modifiers changed from: protected */
    public void setIcon(final ImageView imageView, final QSTile.State state, final boolean z, boolean z2) {
        if (state.disabledByPolicy) {
            imageView.setColorFilter(getContext().getColor(R$color.qs_tile_disabled_color));
        } else {
            imageView.clearColorFilter();
        }
        int i = state.state;
        if (i != this.mState || z2) {
            int color = getColor(i);
            this.mState = state.state;
            if (this.mTint == 0 || !z || !shouldAnimate(imageView)) {
                if (imageView instanceof AlphaControlledSignalTileView.AlphaControlledSlashImageView) {
                    ((AlphaControlledSignalTileView.AlphaControlledSlashImageView) imageView).setFinalImageTintList(ColorStateList.valueOf(color));
                } else {
                    setTint(imageView, color);
                }
                this.mTint = color;
            } else {
                animateGrayScale(this.mTint, color, imageView, new QSIconViewImpl$$ExternalSyntheticLambda1(this, imageView));
                this.mTint = color;
            }
        }
        post(new Runnable() {
            public void run() {
                QSIconViewImpl.this.updateIcon(imageView, state, z);
            }
        });
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$setIcon$0(ImageView imageView) {
        if (imageView instanceof AlphaControlledSignalTileView.AlphaControlledSlashImageView) {
            ((AlphaControlledSignalTileView.AlphaControlledSlashImageView) imageView).setFinalImageTintList(ColorStateList.valueOf(this.mTint));
        } else {
            setTint(imageView, this.mTint);
        }
    }

    /* access modifiers changed from: protected */
    public int getColor(int i) {
        return getIconColorForState(getContext(), i);
    }

    private void animateGrayScale(int i, int i2, ImageView imageView, final Runnable runnable) {
        if (imageView instanceof AlphaControlledSignalTileView.AlphaControlledSlashImageView) {
            ((AlphaControlledSignalTileView.AlphaControlledSlashImageView) imageView).setFinalImageTintList(ColorStateList.valueOf(i2));
        }
        if (!this.mAnimationEnabled || !ValueAnimator.areAnimatorsEnabled()) {
            setTint(imageView, i2);
            runnable.run();
            return;
        }
        float red = (float) Color.red(i);
        float red2 = (float) Color.red(i2);
        ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
        ofFloat.setDuration(350);
        ofFloat.addUpdateListener(new QSIconViewImpl$$ExternalSyntheticLambda0((float) Color.alpha(i), (float) Color.alpha(i2), red, red2, imageView));
        ofFloat.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animator) {
                runnable.run();
            }
        });
        ofFloat.start();
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$animateGrayScale$1(float f, float f2, float f3, float f4, ImageView imageView, ValueAnimator valueAnimator) {
        float animatedFraction = valueAnimator.getAnimatedFraction();
        int i = (int) (f3 + ((f4 - f3) * animatedFraction));
        setTint(imageView, Color.argb((int) (f + ((f2 - f) * animatedFraction)), i, i, i));
    }

    public static void setTint(ImageView imageView, int i) {
        imageView.setImageTintList(ColorStateList.valueOf(i));
    }

    /* access modifiers changed from: protected */
    public View createIcon() {
        SlashImageView slashImageView = new SlashImageView(this.mContext);
        slashImageView.setId(16908294);
        slashImageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        return slashImageView;
    }

    /* access modifiers changed from: protected */
    public final int exactly(int i) {
        return View.MeasureSpec.makeMeasureSpec(i, 1073741824);
    }

    /* access modifiers changed from: protected */
    public final void layout(View view, int i, int i2) {
        view.layout(i, i2, view.getMeasuredWidth() + i, view.getMeasuredHeight() + i2);
    }

    public static int getIconColorForState(Context context, int i) {
        if (i != 0) {
            if (i != 1) {
                if (i != 2) {
                    Log.e("QSIconView", "Invalid state " + i);
                    return 0;
                } else if (!MotoFeature.getInstance(context).isCustomPanelView()) {
                    return Utils.getColorAttrDefaultColor(context, R$attr.prcQSTileIconActiveColor);
                } else {
                    return Utils.getColorAttrDefaultColor(context, 16842809);
                }
            } else if (MotoFeature.getInstance(context).isCustomPanelView()) {
                return Utils.getColorAttrDefaultColor(context, R$attr.prcQSTileIconInactiveColor);
            } else {
                return Utils.getColorAttrDefaultColor(context, 16842806);
            }
        } else if (MotoFeature.getInstance(context).isCustomPanelView()) {
            return Utils.getColorAttrDefaultColor(context, R$attr.prcQSTileIconUnavailableColor);
        } else {
            return Utils.applyAlpha(0.3f, Utils.getColorAttrDefaultColor(context, 16842806));
        }
    }
}
