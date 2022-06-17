package com.android.p011wm.shell.bubbles;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.drawable.AdaptiveIconDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.InsetDrawable;
import android.os.UserHandle;
import android.util.PathParser;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import com.android.launcher3.icons.IconNormalizer;
import com.android.p011wm.shell.C2219R;
import com.android.p011wm.shell.bubbles.BadgedImageView;
import java.util.Objects;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/* renamed from: com.android.wm.shell.bubbles.BubbleOverflow */
/* compiled from: BubbleOverflow.kt */
public final class BubbleOverflow implements BubbleViewProvider {
    @NotNull
    public static final Companion Companion = new Companion((DefaultConstructorMarker) null);
    private Bitmap bitmap;
    @NotNull
    private final Context context;
    private int dotColor;
    private Path dotPath;
    @Nullable
    private BubbleExpandedView expandedView = null;
    @NotNull
    private final LayoutInflater inflater;
    @Nullable
    private BadgedImageView overflowBtn = null;
    private int overflowIconInset;
    @NotNull
    private final BubblePositioner positioner;
    private boolean showDot;

    @Nullable
    public Bitmap getAppBadge() {
        return null;
    }

    @NotNull
    public String getKey() {
        return "Overflow";
    }

    public void setTaskViewVisibility(boolean z) {
    }

    public BubbleOverflow(@NotNull Context context2, @NotNull BubblePositioner bubblePositioner) {
        Intrinsics.checkNotNullParameter(context2, "context");
        Intrinsics.checkNotNullParameter(bubblePositioner, "positioner");
        this.context = context2;
        this.positioner = bubblePositioner;
        LayoutInflater from = LayoutInflater.from(context2);
        Intrinsics.checkNotNullExpressionValue(from, "from(context)");
        this.inflater = from;
        updateResources();
    }

    public final void initialize(@NotNull BubbleController bubbleController) {
        Intrinsics.checkNotNullParameter(bubbleController, "controller");
        BubbleExpandedView expandedView2 = getExpandedView();
        if (expandedView2 != null) {
            expandedView2.initialize(bubbleController, bubbleController.getStackView(), true);
        }
    }

    public final void cleanUpExpandedState() {
        BubbleExpandedView bubbleExpandedView = this.expandedView;
        if (bubbleExpandedView != null) {
            bubbleExpandedView.cleanUpExpandedState();
        }
        this.expandedView = null;
    }

    public final void update() {
        updateResources();
        BubbleExpandedView expandedView2 = getExpandedView();
        if (expandedView2 != null) {
            expandedView2.applyThemeAttrs();
        }
        BadgedImageView iconView = getIconView();
        if (iconView != null) {
            iconView.setImageResource(C2219R.C2221drawable.bubble_ic_overflow_button);
        }
        updateBtnTheme();
    }

    public final void updateResources() {
        this.overflowIconInset = this.context.getResources().getDimensionPixelSize(C2219R.dimen.bubble_overflow_icon_inset);
        BadgedImageView badgedImageView = this.overflowBtn;
        if (badgedImageView != null) {
            badgedImageView.setLayoutParams(new FrameLayout.LayoutParams(this.positioner.getBubbleSize(), this.positioner.getBubbleSize()));
        }
        BubbleExpandedView bubbleExpandedView = this.expandedView;
        if (bubbleExpandedView != null) {
            bubbleExpandedView.updateDimensions();
        }
    }

    private final void updateBtnTheme() {
        Drawable drawable;
        Resources resources = this.context.getResources();
        TypedValue typedValue = new TypedValue();
        this.context.getTheme().resolveAttribute(17956901, typedValue, true);
        int color = resources.getColor(typedValue.resourceId, (Resources.Theme) null);
        this.dotColor = color;
        int color2 = resources.getColor(17170499);
        BadgedImageView badgedImageView = this.overflowBtn;
        if (!(badgedImageView == null || (drawable = badgedImageView.getDrawable()) == null)) {
            drawable.setTint(color2);
        }
        BubbleIconFactory bubbleIconFactory = new BubbleIconFactory(this.context);
        BadgedImageView badgedImageView2 = this.overflowBtn;
        Bitmap bitmap2 = bubbleIconFactory.createBadgedIconBitmap(new AdaptiveIconDrawable(new ColorDrawable(color), new InsetDrawable(badgedImageView2 == null ? null : badgedImageView2.getDrawable(), this.overflowIconInset)), (UserHandle) null, true).icon;
        Intrinsics.checkNotNullExpressionValue(bitmap2, "iconFactory.createBadgedIconBitmap(AdaptiveIconDrawable(\n                ColorDrawable(colorAccent), fg),\n            null /* user */, true /* shrinkNonAdaptiveIcons */).icon");
        this.bitmap = bitmap2;
        Path createPathFromPathData = PathParser.createPathFromPathData(resources.getString(17039967));
        Intrinsics.checkNotNullExpressionValue(createPathFromPathData, "createPathFromPathData(\n            res.getString(com.android.internal.R.string.config_icon_mask))");
        this.dotPath = createPathFromPathData;
        IconNormalizer normalizer = bubbleIconFactory.getNormalizer();
        BadgedImageView iconView = getIconView();
        Intrinsics.checkNotNull(iconView);
        float scale = normalizer.getScale(iconView.getDrawable(), (RectF) null, (Path) null, (boolean[]) null);
        Matrix matrix = new Matrix();
        matrix.setScale(scale, scale, 50.0f, 50.0f);
        Path path = this.dotPath;
        if (path != null) {
            path.transform(matrix);
            BadgedImageView badgedImageView3 = this.overflowBtn;
            if (badgedImageView3 != null) {
                badgedImageView3.setRenderedBubble(this);
            }
            BadgedImageView badgedImageView4 = this.overflowBtn;
            if (badgedImageView4 != null) {
                badgedImageView4.removeDotSuppressionFlag(BadgedImageView.SuppressionFlag.FLYOUT_VISIBLE);
                return;
            }
            return;
        }
        Intrinsics.throwUninitializedPropertyAccessException("dotPath");
        throw null;
    }

    public final void setVisible(int i) {
        BadgedImageView badgedImageView = this.overflowBtn;
        if (badgedImageView != null) {
            badgedImageView.setVisibility(i);
        }
    }

    public final void setShowDot(boolean z) {
        this.showDot = z;
        BadgedImageView badgedImageView = this.overflowBtn;
        if (badgedImageView != null) {
            badgedImageView.updateDotVisibility(true);
        }
    }

    @Nullable
    public BubbleExpandedView getExpandedView() {
        if (this.expandedView == null) {
            View inflate = this.inflater.inflate(C2219R.layout.bubble_expanded_view, (ViewGroup) null, false);
            Objects.requireNonNull(inflate, "null cannot be cast to non-null type com.android.wm.shell.bubbles.BubbleExpandedView");
            BubbleExpandedView bubbleExpandedView = (BubbleExpandedView) inflate;
            this.expandedView = bubbleExpandedView;
            bubbleExpandedView.applyThemeAttrs();
            updateResources();
        }
        return this.expandedView;
    }

    public int getDotColor() {
        return this.dotColor;
    }

    @NotNull
    public Bitmap getBubbleIcon() {
        Bitmap bitmap2 = this.bitmap;
        if (bitmap2 != null) {
            return bitmap2;
        }
        Intrinsics.throwUninitializedPropertyAccessException("bitmap");
        throw null;
    }

    public boolean showDot() {
        return this.showDot;
    }

    @Nullable
    public Path getDotPath() {
        Path path = this.dotPath;
        if (path != null) {
            return path;
        }
        Intrinsics.throwUninitializedPropertyAccessException("dotPath");
        throw null;
    }

    public void setExpandedContentAlpha(float f) {
        BubbleExpandedView bubbleExpandedView = this.expandedView;
        if (bubbleExpandedView != null) {
            bubbleExpandedView.setAlpha(f);
        }
    }

    @Nullable
    public BadgedImageView getIconView() {
        if (this.overflowBtn == null) {
            View inflate = this.inflater.inflate(C2219R.layout.bubble_overflow_button, (ViewGroup) null, false);
            Objects.requireNonNull(inflate, "null cannot be cast to non-null type com.android.wm.shell.bubbles.BadgedImageView");
            BadgedImageView badgedImageView = (BadgedImageView) inflate;
            this.overflowBtn = badgedImageView;
            badgedImageView.initialize(this.positioner);
            BadgedImageView badgedImageView2 = this.overflowBtn;
            if (badgedImageView2 != null) {
                badgedImageView2.setContentDescription(this.context.getResources().getString(C2219R.string.bubble_overflow_button_content_description));
            }
            int bubbleSize = this.positioner.getBubbleSize();
            BadgedImageView badgedImageView3 = this.overflowBtn;
            if (badgedImageView3 != null) {
                badgedImageView3.setLayoutParams(new FrameLayout.LayoutParams(bubbleSize, bubbleSize));
            }
            updateBtnTheme();
        }
        return this.overflowBtn;
    }

    public int getTaskId() {
        BubbleExpandedView bubbleExpandedView = this.expandedView;
        if (bubbleExpandedView == null) {
            return -1;
        }
        Intrinsics.checkNotNull(bubbleExpandedView);
        return bubbleExpandedView.getTaskId();
    }

    /* renamed from: com.android.wm.shell.bubbles.BubbleOverflow$Companion */
    /* compiled from: BubbleOverflow.kt */
    public static final class Companion {
        public /* synthetic */ Companion(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }

        private Companion() {
        }
    }
}
