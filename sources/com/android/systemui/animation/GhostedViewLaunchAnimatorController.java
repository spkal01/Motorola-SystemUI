package com.android.systemui.animation;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.InsetDrawable;
import android.graphics.drawable.LayerDrawable;
import android.util.Log;
import android.view.GhostView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroupOverlay;
import android.widget.FrameLayout;
import com.android.internal.jank.InteractionJankMonitor;
import com.android.systemui.animation.ActivityLaunchAnimator;
import java.util.Objects;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/* compiled from: GhostedViewLaunchAnimatorController.kt */
public class GhostedViewLaunchAnimatorController implements ActivityLaunchAnimator.Controller {
    @NotNull
    public static final Companion Companion = new Companion((DefaultConstructorMarker) null);
    @Nullable
    private WrappedDrawable backgroundDrawable;
    @Nullable
    private FrameLayout backgroundView;
    @Nullable
    private final Integer cujType;
    @Nullable
    private GhostView ghostView;
    @NotNull
    private final Matrix ghostViewMatrix;
    @NotNull
    private final View ghostedView;
    @NotNull
    private final float[] initialGhostViewMatrixValues;
    @NotNull
    private ViewGroup launchContainer;
    private int startBackgroundAlpha;

    public GhostedViewLaunchAnimatorController(@NotNull View view, @Nullable Integer num) {
        Intrinsics.checkNotNullParameter(view, "ghostedView");
        this.ghostedView = view;
        this.cujType = num;
        View rootView = view.getRootView();
        Objects.requireNonNull(rootView, "null cannot be cast to non-null type android.view.ViewGroup");
        this.launchContainer = (ViewGroup) rootView;
        float[] fArr = new float[9];
        for (int i = 0; i < 9; i++) {
            fArr[i] = 0.0f;
        }
        this.initialGhostViewMatrixValues = fArr;
        this.ghostViewMatrix = new Matrix();
        this.startBackgroundAlpha = 255;
    }

    public void onIntentStarted(boolean z) {
        ActivityLaunchAnimator.Controller.DefaultImpls.onIntentStarted(this, z);
    }

    public void onLaunchAnimationCancelled() {
        ActivityLaunchAnimator.Controller.DefaultImpls.onLaunchAnimationCancelled(this);
    }

    @NotNull
    public ViewGroup getLaunchContainer() {
        return this.launchContainer;
    }

    public void setLaunchContainer(@NotNull ViewGroup viewGroup) {
        Intrinsics.checkNotNullParameter(viewGroup, "<set-?>");
        this.launchContainer = viewGroup;
    }

    private final ViewGroupOverlay getLaunchContainerOverlay() {
        ViewGroupOverlay overlay = getLaunchContainer().getOverlay();
        Intrinsics.checkNotNullExpressionValue(overlay, "launchContainer.overlay");
        return overlay;
    }

    /* access modifiers changed from: protected */
    @Nullable
    public Drawable getBackground() {
        return this.ghostedView.getBackground();
    }

    /* access modifiers changed from: protected */
    public void setBackgroundCornerRadius(@NotNull Drawable drawable, float f, float f2) {
        Intrinsics.checkNotNullParameter(drawable, "background");
        WrappedDrawable wrappedDrawable = this.backgroundDrawable;
        if (wrappedDrawable != null) {
            wrappedDrawable.setBackgroundRadius(f, f2);
        }
    }

    /* access modifiers changed from: protected */
    public float getCurrentTopCornerRadius() {
        GradientDrawable access$findGradientDrawable;
        Drawable background = getBackground();
        if (background == null || (access$findGradientDrawable = Companion.findGradientDrawable(background)) == null) {
            return 0.0f;
        }
        float[] cornerRadii = access$findGradientDrawable.getCornerRadii();
        Float valueOf = cornerRadii == null ? null : Float.valueOf(cornerRadii[0]);
        return valueOf == null ? access$findGradientDrawable.getCornerRadius() : valueOf.floatValue();
    }

    /* access modifiers changed from: protected */
    public float getCurrentBottomCornerRadius() {
        GradientDrawable access$findGradientDrawable;
        Drawable background = getBackground();
        if (background == null || (access$findGradientDrawable = Companion.findGradientDrawable(background)) == null) {
            return 0.0f;
        }
        float[] cornerRadii = access$findGradientDrawable.getCornerRadii();
        Float valueOf = cornerRadii == null ? null : Float.valueOf(cornerRadii[4]);
        return valueOf == null ? access$findGradientDrawable.getCornerRadius() : valueOf.floatValue();
    }

    @NotNull
    public ActivityLaunchAnimator.State createAnimatorState() {
        int[] locationOnScreen = this.ghostedView.getLocationOnScreen();
        return new ActivityLaunchAnimator.State(locationOnScreen[1], this.ghostedView.getHeight() + locationOnScreen[1], locationOnScreen[0], locationOnScreen[0] + this.ghostedView.getWidth(), getCurrentTopCornerRadius(), getCurrentBottomCornerRadius());
    }

    public void onLaunchAnimationStart(boolean z) {
        int i;
        Matrix matrix;
        if (!(this.ghostedView.getParent() instanceof ViewGroup)) {
            Log.w("GhostedViewLaunchAnimatorController", "Skipping animation as ghostedView is not attached to a ViewGroup");
            return;
        }
        this.backgroundView = new FrameLayout(getLaunchContainer().getContext());
        getLaunchContainerOverlay().add(this.backgroundView);
        Drawable background = getBackground();
        if (background == null) {
            i = 255;
        } else {
            i = background.getAlpha();
        }
        this.startBackgroundAlpha = i;
        WrappedDrawable wrappedDrawable = new WrappedDrawable(background);
        this.backgroundDrawable = wrappedDrawable;
        FrameLayout frameLayout = this.backgroundView;
        if (frameLayout != null) {
            frameLayout.setBackground(wrappedDrawable);
        }
        GhostView addGhost = GhostView.addGhost(this.ghostedView, getLaunchContainer());
        this.ghostView = addGhost;
        if (addGhost == null) {
            matrix = null;
        } else {
            matrix = addGhost.getAnimationMatrix();
        }
        if (matrix == null) {
            matrix = Matrix.IDENTITY_MATRIX;
        }
        matrix.getValues(this.initialGhostViewMatrixValues);
        Integer num = this.cujType;
        if (num != null) {
            InteractionJankMonitor.getInstance().begin(this.ghostedView, num.intValue());
        }
    }

    public void onLaunchAnimationProgress(@NotNull ActivityLaunchAnimator.State state, float f, float f2) {
        Intrinsics.checkNotNullParameter(state, "state");
        GhostView ghostView2 = this.ghostView;
        if (ghostView2 != null) {
            FrameLayout frameLayout = this.backgroundView;
            Intrinsics.checkNotNull(frameLayout);
            if (state.getVisible()) {
                float min = Math.min(state.getWidthRatio(), state.getHeightRatio());
                this.ghostViewMatrix.setValues(this.initialGhostViewMatrixValues);
                this.ghostViewMatrix.postScale(min, min, state.getStartCenterX(), state.getStartCenterY());
                this.ghostViewMatrix.postTranslate(((float) (state.getLeftChange() + state.getRightChange())) / 2.0f, ((float) (state.getTopChange() + state.getBottomChange())) / 2.0f);
                ghostView2.setAnimationMatrix(this.ghostViewMatrix);
                frameLayout.setTop(state.getTop());
                frameLayout.setBottom(state.getBottom());
                frameLayout.setLeft(state.getLeft());
                frameLayout.setRight(state.getRight());
                WrappedDrawable wrappedDrawable = this.backgroundDrawable;
                Intrinsics.checkNotNull(wrappedDrawable);
                Drawable wrapped = wrappedDrawable.getWrapped();
                if (wrapped != null) {
                    setBackgroundCornerRadius(wrapped, state.getTopCornerRadius(), state.getBottomCornerRadius());
                }
            } else if (ghostView2.getVisibility() == 0) {
                ghostView2.setVisibility(4);
                this.ghostedView.setVisibility(4);
                frameLayout.setVisibility(4);
            }
        }
    }

    public void onLaunchAnimationEnd(boolean z) {
        if (this.ghostView != null) {
            Integer num = this.cujType;
            if (num != null) {
                InteractionJankMonitor.getInstance().end(num.intValue());
            }
            WrappedDrawable wrappedDrawable = this.backgroundDrawable;
            Drawable wrapped = wrappedDrawable == null ? null : wrappedDrawable.getWrapped();
            if (wrapped != null) {
                wrapped.setAlpha(this.startBackgroundAlpha);
            }
            GhostView.removeGhost(this.ghostedView);
            getLaunchContainerOverlay().remove(this.backgroundView);
            this.ghostedView.setVisibility(0);
            this.ghostedView.invalidate();
        }
    }

    /* compiled from: GhostedViewLaunchAnimatorController.kt */
    public static final class Companion {
        public /* synthetic */ Companion(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }

        private Companion() {
        }

        /* access modifiers changed from: private */
        public final GradientDrawable findGradientDrawable(Drawable drawable) {
            if (drawable instanceof GradientDrawable) {
                return (GradientDrawable) drawable;
            }
            if (drawable instanceof InsetDrawable) {
                Drawable drawable2 = ((InsetDrawable) drawable).getDrawable();
                if (drawable2 == null) {
                    return null;
                }
                return GhostedViewLaunchAnimatorController.Companion.findGradientDrawable(drawable2);
            }
            if (drawable instanceof LayerDrawable) {
                int i = 0;
                LayerDrawable layerDrawable = (LayerDrawable) drawable;
                int numberOfLayers = layerDrawable.getNumberOfLayers();
                if (numberOfLayers > 0) {
                    while (true) {
                        int i2 = i + 1;
                        Drawable drawable3 = layerDrawable.getDrawable(i);
                        if (drawable3 instanceof GradientDrawable) {
                            return (GradientDrawable) drawable3;
                        }
                        if (i2 >= numberOfLayers) {
                            break;
                        }
                        i = i2;
                    }
                }
            }
            return null;
        }
    }

    /* compiled from: GhostedViewLaunchAnimatorController.kt */
    private static final class WrappedDrawable extends Drawable {
        @NotNull
        private float[] cornerRadii;
        private int currentAlpha = 255;
        @NotNull
        private Rect previousBounds = new Rect();
        @NotNull
        private float[] previousCornerRadii;
        @Nullable
        private final Drawable wrapped;

        public WrappedDrawable(@Nullable Drawable drawable) {
            this.wrapped = drawable;
            float[] fArr = new float[8];
            for (int i = 0; i < 8; i++) {
                fArr[i] = -1.0f;
            }
            this.cornerRadii = fArr;
            this.previousCornerRadii = new float[8];
        }

        @Nullable
        public final Drawable getWrapped() {
            return this.wrapped;
        }

        public void draw(@NotNull Canvas canvas) {
            Intrinsics.checkNotNullParameter(canvas, "canvas");
            Drawable drawable = this.wrapped;
            if (drawable != null) {
                drawable.copyBounds(this.previousBounds);
                drawable.setAlpha(this.currentAlpha);
                drawable.setBounds(getBounds());
                applyBackgroundRadii();
                drawable.draw(canvas);
                drawable.setAlpha(0);
                drawable.setBounds(this.previousBounds);
                restoreBackgroundRadii();
            }
        }

        public void setAlpha(int i) {
            if (i != this.currentAlpha) {
                this.currentAlpha = i;
                invalidateSelf();
            }
        }

        public int getAlpha() {
            return this.currentAlpha;
        }

        public int getOpacity() {
            Drawable drawable = this.wrapped;
            if (drawable == null) {
                return -2;
            }
            int alpha = drawable.getAlpha();
            drawable.setAlpha(this.currentAlpha);
            int opacity = drawable.getOpacity();
            drawable.setAlpha(alpha);
            return opacity;
        }

        public void setColorFilter(@Nullable ColorFilter colorFilter) {
            Drawable drawable = this.wrapped;
            if (drawable != null) {
                drawable.setColorFilter(colorFilter);
            }
        }

        public final void setBackgroundRadius(float f, float f2) {
            updateRadii(this.cornerRadii, f, f2);
            invalidateSelf();
        }

        private final void updateRadii(float[] fArr, float f, float f2) {
            fArr[0] = f;
            fArr[1] = f;
            fArr[2] = f;
            fArr[3] = f;
            fArr[4] = f2;
            fArr[5] = f2;
            fArr[6] = f2;
            fArr[7] = f2;
        }

        private final void applyBackgroundRadii() {
            Drawable drawable;
            if (this.cornerRadii[0] >= 0.0f && (drawable = this.wrapped) != null) {
                savePreviousBackgroundRadii(drawable);
                applyBackgroundRadii(this.wrapped, this.cornerRadii);
            }
        }

        private final void savePreviousBackgroundRadii(Drawable drawable) {
            GradientDrawable access$findGradientDrawable = GhostedViewLaunchAnimatorController.Companion.findGradientDrawable(drawable);
            if (access$findGradientDrawable != null) {
                float[] cornerRadii2 = access$findGradientDrawable.getCornerRadii();
                if (cornerRadii2 != null) {
                    float[] unused = ArraysKt___ArraysJvmKt.copyInto$default(cornerRadii2, this.previousCornerRadii, 0, 0, 0, 14, (Object) null);
                    return;
                }
                float cornerRadius = access$findGradientDrawable.getCornerRadius();
                updateRadii(this.previousCornerRadii, cornerRadius, cornerRadius);
            }
        }

        private final void applyBackgroundRadii(Drawable drawable, float[] fArr) {
            if (drawable instanceof GradientDrawable) {
                ((GradientDrawable) drawable).setCornerRadii(fArr);
            } else if (drawable instanceof InsetDrawable) {
                Drawable drawable2 = ((InsetDrawable) drawable).getDrawable();
                if (drawable2 != null) {
                    applyBackgroundRadii(drawable2, fArr);
                }
            } else if (drawable instanceof LayerDrawable) {
                int i = 0;
                LayerDrawable layerDrawable = (LayerDrawable) drawable;
                int numberOfLayers = layerDrawable.getNumberOfLayers();
                if (numberOfLayers > 0) {
                    while (true) {
                        int i2 = i + 1;
                        Drawable drawable3 = layerDrawable.getDrawable(i);
                        GradientDrawable gradientDrawable = drawable3 instanceof GradientDrawable ? (GradientDrawable) drawable3 : null;
                        if (gradientDrawable != null) {
                            gradientDrawable.setCornerRadii(fArr);
                        }
                        if (i2 < numberOfLayers) {
                            i = i2;
                        } else {
                            return;
                        }
                    }
                }
            }
        }

        private final void restoreBackgroundRadii() {
            Drawable drawable;
            if (this.cornerRadii[0] >= 0.0f && (drawable = this.wrapped) != null) {
                applyBackgroundRadii(drawable, this.previousCornerRadii);
            }
        }
    }
}
