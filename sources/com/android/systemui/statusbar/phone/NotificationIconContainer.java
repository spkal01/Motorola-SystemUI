package com.android.systemui.statusbar.phone;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Icon;
import android.util.AttributeSet;
import android.util.Property;
import android.view.ContextThemeWrapper;
import android.view.MotionEvent;
import android.view.View;
import androidx.collection.ArrayMap;
import com.android.internal.statusbar.StatusBarIcon;
import com.android.internal.util.ContrastColorUtil;
import com.android.settingslib.Utils;
import com.android.systemui.R$dimen;
import com.android.systemui.statusbar.AlphaOptimizedFrameLayout;
import com.android.systemui.statusbar.StatusBarIconView;
import com.android.systemui.statusbar.notification.stack.AnimationFilter;
import com.android.systemui.statusbar.notification.stack.AnimationProperties;
import com.android.systemui.statusbar.notification.stack.ViewState;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Consumer;

public class NotificationIconContainer extends AlphaOptimizedFrameLayout {
    /* access modifiers changed from: private */
    public static final AnimationProperties ADD_ICON_PROPERTIES = new AnimationProperties() {
        private AnimationFilter mAnimationFilter = new AnimationFilter().animateAlpha();

        public AnimationFilter getAnimationFilter() {
            return this.mAnimationFilter;
        }
    }.setDuration(200).setDelay(50);
    /* access modifiers changed from: private */
    public static final AnimationProperties DOT_ANIMATION_PROPERTIES = new AnimationProperties() {
        private AnimationFilter mAnimationFilter = new AnimationFilter().animateX();

        public AnimationFilter getAnimationFilter() {
            return this.mAnimationFilter;
        }
    }.setDuration(200);
    /* access modifiers changed from: private */
    public static final AnimationProperties ICON_ANIMATION_PROPERTIES = new AnimationProperties() {
        private AnimationFilter mAnimationFilter = new AnimationFilter().animateX().animateY().animateAlpha().animateScale();

        public AnimationFilter getAnimationFilter() {
            return this.mAnimationFilter;
        }
    }.setDuration(100);
    private static int REMOVED_ANIMATION_DURATION = 0;
    /* access modifiers changed from: private */
    public static final AnimationProperties UNISOLATION_PROPERTY = new AnimationProperties() {
        private AnimationFilter mAnimationFilter = new AnimationFilter().animateX();

        public AnimationFilter getAnimationFilter() {
            return this.mAnimationFilter;
        }
    }.setDuration(110);
    /* access modifiers changed from: private */
    public static final AnimationProperties UNISOLATION_PROPERTY_OTHERS = new AnimationProperties() {
        private AnimationFilter mAnimationFilter = new AnimationFilter().animateAlpha();

        public AnimationFilter getAnimationFilter() {
            return this.mAnimationFilter;
        }
    }.setDuration(110);
    /* access modifiers changed from: private */
    public static final AnimationProperties sTempProperties = new AnimationProperties() {
        private AnimationFilter mAnimationFilter = new AnimationFilter();

        public AnimationFilter getAnimationFilter() {
            return this.mAnimationFilter;
        }
    };
    private final int SCROLL_X = 10;
    private int[] mAbsolutePosition = new int[2];
    private int mActualLayoutWidth = Integer.MIN_VALUE;
    private float mActualPaddingEnd = -2.14748365E9f;
    private float mActualPaddingStart = -2.14748365E9f;
    /* access modifiers changed from: private */
    public int mAddAnimationStartIndex = -1;
    private boolean mAnimationsEnabled = true;
    /* access modifiers changed from: private */
    public int mCannedAnimationStartIndex = -1;
    private boolean mChangingViewPositions;
    /* access modifiers changed from: private */
    public final ContrastColorUtil mContrastColorUtil;
    /* access modifiers changed from: private */
    public boolean mDisallowNextAnimation;
    private int mDotPadding;
    private int mDownX;
    private int mDownY;
    private boolean mDozing;
    private IconState mFirstVisibleIconState;
    private int mIconDis;
    private int mIconSize;
    private final HashMap<View, IconState> mIconStates = new HashMap<>();
    private int mIconWidth;
    /* access modifiers changed from: private */
    public boolean mInNotificationIconShelf;
    /* access modifiers changed from: private */
    public boolean mIsCarousel = false;
    private boolean mIsStaticLayout = true;
    /* access modifiers changed from: private */
    public StatusBarIconView mIsolatedIcon;
    /* access modifiers changed from: private */
    public View mIsolatedIconForAnimation;
    private Rect mIsolatedIconLocation;
    private IconState mLastVisibleIconState;
    private int mNumDots;
    private View.OnClickListener mOnChildClickListener;
    private boolean mOnLockScreen;
    private int mOverflowWidth;
    private int mPositionWidth = 0;
    private ArrayMap<String, ArrayList<StatusBarIcon>> mReplacingIcons;
    private StatusBarIconView mSelectedIcon;
    private int mSpeedBumpIndex = -1;
    private int mStaticDotDiameter;
    private int mStaticDotRadius;
    /* access modifiers changed from: private */
    public int mThemedTextColorPrimary;
    private int mVisualIndex = 0;
    private float mVisualOverflowStart;

    public NotificationIconContainer(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        initDimens();
        setWillNotDraw(true);
        this.mContrastColorUtil = ContrastColorUtil.getInstance(context);
    }

    private void initDimens() {
        this.mDotPadding = getResources().getDimensionPixelSize(R$dimen.overflow_icon_dot_padding);
        int dimensionPixelSize = getResources().getDimensionPixelSize(R$dimen.overflow_dot_radius);
        this.mStaticDotRadius = dimensionPixelSize;
        this.mStaticDotDiameter = dimensionPixelSize * 2;
        this.mThemedTextColorPrimary = Utils.getColorAttr(new ContextThemeWrapper(getContext(), 16974563), 16842806).getDefaultColor();
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Paint paint = new Paint();
        paint.setColor(-65536);
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawRect(getActualPaddingStart(), 0.0f, getLayoutEnd(), (float) getHeight(), paint);
    }

    /* access modifiers changed from: protected */
    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        initDimens();
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        float height = ((float) getHeight()) / 2.0f;
        this.mIconSize = 0;
        for (int i5 = 0; i5 < getChildCount(); i5++) {
            View childAt = getChildAt(i5);
            int measuredWidth = childAt.getMeasuredWidth();
            int measuredHeight = childAt.getMeasuredHeight();
            int i6 = (int) (height - (((float) measuredHeight) / 2.0f));
            childAt.layout(0, i6, measuredWidth, measuredHeight + i6);
            if (i5 == 0) {
                setIconSize(childAt.getWidth());
            }
        }
        getLocationOnScreen(this.mAbsolutePosition);
        if (this.mIsStaticLayout) {
            updateState();
        }
    }

    private void setIconSize(int i) {
        this.mIconSize = i;
        this.mOverflowWidth = i + ((this.mStaticDotDiameter + this.mDotPadding) * 0);
    }

    private void updateState() {
        resetViewStates();
        calculateIconTranslations();
        applyIconStates();
    }

    public void applyIconStates() {
        for (int i = 0; i < getChildCount(); i++) {
            View childAt = getChildAt(i);
            ViewState viewState = this.mIconStates.get(childAt);
            if (viewState != null) {
                viewState.applyToView(childAt);
            }
        }
        this.mAddAnimationStartIndex = -1;
        this.mCannedAnimationStartIndex = -1;
        this.mDisallowNextAnimation = false;
        this.mIsolatedIconForAnimation = null;
    }

    public void onViewAdded(View view) {
        super.onViewAdded(view);
        boolean isReplacingIcon = isReplacingIcon(view);
        if (!this.mChangingViewPositions) {
            IconState iconState = new IconState(view);
            if (isReplacingIcon) {
                iconState.justAdded = false;
                boolean unused = iconState.justReplaced = true;
            }
            this.mIconStates.put(view, iconState);
        }
        int indexOfChild = indexOfChild(view);
        if (indexOfChild < getChildCount() - 1 && !isReplacingIcon && this.mIconStates.get(getChildAt(indexOfChild + 1)).iconAppearAmount > 0.0f) {
            int i = this.mAddAnimationStartIndex;
            if (i < 0) {
                this.mAddAnimationStartIndex = indexOfChild;
            } else {
                this.mAddAnimationStartIndex = Math.min(i, indexOfChild);
            }
        }
        if (view instanceof StatusBarIconView) {
            ((StatusBarIconView) view).setDozing(this.mDozing, false, 0);
        }
        if (this.mIsCarousel) {
            view.setOnClickListener(new NotificationIconContainer$$ExternalSyntheticLambda0(this, view));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onViewAdded$0(View view, View view2) {
        int indexOfChild;
        int i;
        if (this.mOnChildClickListener != null && (indexOfChild = indexOfChild(view)) >= (i = this.mVisualIndex) && indexOfChild < i + 4) {
            this.mOnChildClickListener.onClick(view2);
            updateSelectedIcon((StatusBarIconView) view2);
        }
    }

    private boolean isReplacingIcon(View view) {
        if (this.mReplacingIcons == null || !(view instanceof StatusBarIconView)) {
            return false;
        }
        StatusBarIconView statusBarIconView = (StatusBarIconView) view;
        Icon sourceIcon = statusBarIconView.getSourceIcon();
        ArrayList arrayList = this.mReplacingIcons.get(statusBarIconView.getNotification().getGroupKey());
        if (arrayList == null || !sourceIcon.sameAs(((StatusBarIcon) arrayList.get(0)).icon)) {
            return false;
        }
        return true;
    }

    public void onViewRemoved(View view) {
        long j;
        super.onViewRemoved(view);
        boolean z = false;
        if (this.mIsCarousel && this.mVisualIndex + 4 > getChildCount()) {
            int childCount = getChildCount() - 4;
            this.mVisualIndex = childCount;
            if (childCount < 0) {
                this.mVisualIndex = 0;
            }
        }
        if (view instanceof StatusBarIconView) {
            boolean isReplacingIcon = isReplacingIcon(view);
            StatusBarIconView statusBarIconView = (StatusBarIconView) view;
            if (areAnimationsEnabled(statusBarIconView) && statusBarIconView.getVisibleState() != 2 && view.getVisibility() == 0 && isReplacingIcon) {
                int findFirstViewIndexAfter = findFirstViewIndexAfter(statusBarIconView.getTranslationX());
                int i = this.mAddAnimationStartIndex;
                if (i < 0) {
                    this.mAddAnimationStartIndex = findFirstViewIndexAfter;
                } else {
                    this.mAddAnimationStartIndex = Math.min(i, findFirstViewIndexAfter);
                }
            }
            if (!this.mChangingViewPositions) {
                this.mIconStates.remove(view);
                if (areAnimationsEnabled(statusBarIconView) && !isReplacingIcon) {
                    addTransientView(statusBarIconView, 0);
                    if (view == this.mIsolatedIcon) {
                        z = true;
                    }
                    NotificationIconContainer$$ExternalSyntheticLambda1 notificationIconContainer$$ExternalSyntheticLambda1 = new NotificationIconContainer$$ExternalSyntheticLambda1(this, statusBarIconView);
                    if (z) {
                        j = 110;
                    } else {
                        j = (long) REMOVED_ANIMATION_DURATION;
                    }
                    statusBarIconView.setVisibleState(2, true, notificationIconContainer$$ExternalSyntheticLambda1, j);
                }
            }
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onViewRemoved$1(StatusBarIconView statusBarIconView) {
        removeTransientView(statusBarIconView);
    }

    public boolean hasMaxNumDot() {
        return this.mNumDots >= 1;
    }

    /* access modifiers changed from: private */
    public boolean areAnimationsEnabled(StatusBarIconView statusBarIconView) {
        return this.mAnimationsEnabled || statusBarIconView == this.mIsolatedIcon;
    }

    private int findFirstViewIndexAfter(float f) {
        for (int i = 0; i < getChildCount(); i++) {
            if (getChildAt(i).getTranslationX() > f) {
                return i;
            }
        }
        return getChildCount();
    }

    public void resetViewStates() {
        for (int i = 0; i < getChildCount(); i++) {
            View childAt = getChildAt(i);
            ViewState viewState = this.mIconStates.get(childAt);
            viewState.initFrom(childAt);
            StatusBarIconView statusBarIconView = this.mIsolatedIcon;
            viewState.alpha = (statusBarIconView == null || childAt == statusBarIconView) ? 1.0f : 0.0f;
            viewState.hidden = false;
        }
    }

    public void calculateIconTranslations() {
        int i;
        IconState iconState;
        float f;
        if (this.mIsCarousel) {
            calculateIconTranslationsForCli();
            return;
        }
        float actualPaddingStart = getActualPaddingStart();
        int childCount = getChildCount();
        if (this.mOnLockScreen) {
            i = 5;
        } else {
            i = this.mIsStaticLayout ? 4 : childCount;
        }
        float layoutEnd = getLayoutEnd();
        float maxOverflowStart = getMaxOverflowStart();
        float f2 = 0.0f;
        this.mVisualOverflowStart = 0.0f;
        this.mFirstVisibleIconState = null;
        int i2 = -1;
        int i3 = 0;
        while (i3 < childCount) {
            View childAt = getChildAt(i3);
            IconState iconState2 = this.mIconStates.get(childAt);
            float f3 = iconState2.iconAppearAmount;
            if (f3 == 1.0f) {
                iconState2.xTranslation = actualPaddingStart;
            }
            if (this.mFirstVisibleIconState == null) {
                this.mFirstVisibleIconState = iconState2;
            }
            int i4 = this.mSpeedBumpIndex;
            boolean z = (i4 != -1 && i3 >= i4 && f3 > f2) || i3 >= i;
            boolean z2 = i3 == childCount + -1;
            float iconScaleIncreased = (!this.mOnLockScreen || !(childAt instanceof StatusBarIconView)) ? 1.0f : ((StatusBarIconView) childAt).getIconScaleIncreased();
            iconState2.visibleState = iconState2.hidden ? 2 : 0;
            if (z2) {
                f = layoutEnd - ((float) this.mIconSize);
            } else {
                f = maxOverflowStart - ((float) (this.mIconSize / 2));
            }
            boolean z3 = actualPaddingStart > f;
            if (i2 == -1 && (z || z3)) {
                float f4 = layoutEnd - ((float) this.mOverflowWidth);
                this.mVisualOverflowStart = f4;
                if (z || this.mIsStaticLayout) {
                    this.mVisualOverflowStart = Math.min(actualPaddingStart, f4);
                }
                i2 = i3;
            }
            actualPaddingStart += iconState2.iconAppearAmount * ((float) childAt.getWidth()) * iconScaleIncreased;
            i3++;
            f2 = 0.0f;
        }
        this.mNumDots = 0;
        if (i2 != -1) {
            float f5 = this.mVisualOverflowStart;
            while (i2 < childCount) {
                IconState iconState3 = this.mIconStates.get(getChildAt(i2));
                int i5 = this.mStaticDotDiameter + this.mDotPadding;
                iconState3.xTranslation = f5;
                int i6 = this.mNumDots;
                if (i6 < 1) {
                    if (i6 != 0 || iconState3.iconAppearAmount >= 0.8f) {
                        iconState3.visibleState = 1;
                        this.mNumDots = i6 + 1;
                    } else {
                        iconState3.visibleState = 0;
                    }
                    if (this.mNumDots == 1) {
                        i5 *= 1;
                    }
                    f5 += ((float) i5) * iconState3.iconAppearAmount;
                    this.mLastVisibleIconState = iconState3;
                } else {
                    iconState3.visibleState = 2;
                }
                i2++;
            }
        } else if (childCount > 0) {
            this.mLastVisibleIconState = this.mIconStates.get(getChildAt(childCount - 1));
            this.mFirstVisibleIconState = this.mIconStates.get(getChildAt(0));
        }
        if (isLayoutRtl()) {
            for (int i7 = 0; i7 < childCount; i7++) {
                View childAt2 = getChildAt(i7);
                IconState iconState4 = this.mIconStates.get(childAt2);
                iconState4.xTranslation = (((float) getWidth()) - iconState4.xTranslation) - ((float) childAt2.getWidth());
            }
        }
        StatusBarIconView statusBarIconView = this.mIsolatedIcon;
        if (statusBarIconView != null && (iconState = this.mIconStates.get(statusBarIconView)) != null) {
            iconState.xTranslation = ((float) (this.mIsolatedIconLocation.left - this.mAbsolutePosition[0])) - (((1.0f - this.mIsolatedIcon.getIconScale()) * ((float) this.mIsolatedIcon.getWidth())) / 2.0f);
            iconState.visibleState = 0;
        }
    }

    private float getLayoutEnd() {
        return ((float) getActualWidth()) - getActualPaddingEnd();
    }

    private float getActualPaddingEnd() {
        float f = this.mActualPaddingEnd;
        return f == -2.14748365E9f ? (float) getPaddingEnd() : f;
    }

    public float getActualPaddingStart() {
        float f = this.mActualPaddingStart;
        return f == -2.14748365E9f ? (float) getPaddingStart() : f;
    }

    public void setIsStaticLayout(boolean z) {
        this.mIsStaticLayout = z;
    }

    public int getActualWidth() {
        int i = this.mActualLayoutWidth;
        return i == Integer.MIN_VALUE ? getWidth() : i;
    }

    private float getMaxOverflowStart() {
        return getLayoutEnd() - ((float) this.mOverflowWidth);
    }

    public void setChangingViewPositions(boolean z) {
        this.mChangingViewPositions = z;
    }

    public void setDozing(boolean z, boolean z2, long j) {
        this.mDozing = z;
        this.mDisallowNextAnimation |= !z2;
        for (int i = 0; i < getChildCount(); i++) {
            View childAt = getChildAt(i);
            if (childAt instanceof StatusBarIconView) {
                ((StatusBarIconView) childAt).setDozing(z, z2, j);
            }
        }
    }

    public IconState getIconState(StatusBarIconView statusBarIconView) {
        return this.mIconStates.get(statusBarIconView);
    }

    public void setSpeedBumpIndex(int i) {
        this.mSpeedBumpIndex = i;
    }

    public void setAnimationsEnabled(boolean z) {
        if (!z && this.mAnimationsEnabled) {
            for (int i = 0; i < getChildCount(); i++) {
                View childAt = getChildAt(i);
                ViewState viewState = this.mIconStates.get(childAt);
                if (viewState != null) {
                    viewState.cancelAnimations(childAt);
                    viewState.applyToView(childAt);
                }
            }
        }
        this.mAnimationsEnabled = z;
    }

    public void setReplacingIcons(ArrayMap<String, ArrayList<StatusBarIcon>> arrayMap) {
        this.mReplacingIcons = arrayMap;
    }

    public void showIconIsolated(StatusBarIconView statusBarIconView, boolean z) {
        if (z) {
            this.mIsolatedIconForAnimation = statusBarIconView != null ? statusBarIconView : this.mIsolatedIcon;
        }
        this.mIsolatedIcon = statusBarIconView;
        updateState();
    }

    public void setIsolatedIconLocation(Rect rect, boolean z) {
        this.mIsolatedIconLocation = rect;
        if (z) {
            updateState();
        }
    }

    public void setOnLockScreen(boolean z) {
        this.mOnLockScreen = z;
    }

    public void setInNotificationIconShelf(boolean z) {
        this.mInNotificationIconShelf = z;
    }

    public class IconState extends ViewState {
        public float clampedAppearAmount = 1.0f;
        public float iconAppearAmount = 1.0f;
        public int iconColor = 0;
        public boolean justAdded = true;
        /* access modifiers changed from: private */
        public boolean justReplaced;
        private final Consumer<Property> mCannedAnimationEndListener;
        private final View mView;
        public boolean needsCannedAnimation;
        public boolean noAnimations;
        public int visibleState;

        public IconState(View view) {
            this.mView = view;
            this.mCannedAnimationEndListener = new NotificationIconContainer$IconState$$ExternalSyntheticLambda0(this);
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$new$0(Property property) {
            if (property == View.TRANSLATION_Y && this.iconAppearAmount == 0.0f && this.mView.getVisibility() == 0) {
                this.mView.setVisibility(4);
            }
        }

        /* JADX WARNING: Removed duplicated region for block: B:49:0x009c  */
        /* JADX WARNING: Removed duplicated region for block: B:69:0x0149  */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void applyToView(android.view.View r14) {
            /*
                r13 = this;
                boolean r0 = r14 instanceof com.android.systemui.statusbar.StatusBarIconView
                r1 = 0
                if (r0 == 0) goto L_0x01ef
                r0 = r14
                com.android.systemui.statusbar.StatusBarIconView r0 = (com.android.systemui.statusbar.StatusBarIconView) r0
                int r2 = r13.visibleState
                r3 = 2
                r4 = 1
                if (r2 != r3) goto L_0x0014
                int r2 = r0.getVisibleState()
                if (r2 == r4) goto L_0x001e
            L_0x0014:
                int r2 = r13.visibleState
                if (r2 != r4) goto L_0x0020
                int r2 = r0.getVisibleState()
                if (r2 != r3) goto L_0x0020
            L_0x001e:
                r2 = r4
                goto L_0x0021
            L_0x0020:
                r2 = r1
            L_0x0021:
                com.android.systemui.statusbar.phone.NotificationIconContainer r5 = com.android.systemui.statusbar.phone.NotificationIconContainer.this
                boolean r5 = r5.areAnimationsEnabled(r0)
                if (r5 == 0) goto L_0x0039
                com.android.systemui.statusbar.phone.NotificationIconContainer r5 = com.android.systemui.statusbar.phone.NotificationIconContainer.this
                boolean r5 = r5.mDisallowNextAnimation
                if (r5 != 0) goto L_0x0039
                boolean r5 = r13.noAnimations
                if (r5 != 0) goto L_0x0039
                if (r2 != 0) goto L_0x0039
                r2 = r4
                goto L_0x003a
            L_0x0039:
                r2 = r1
            L_0x003a:
                r5 = 0
                if (r2 == 0) goto L_0x0190
                boolean r6 = r13.justAdded
                if (r6 != 0) goto L_0x0053
                boolean r6 = r13.justReplaced
                if (r6 == 0) goto L_0x0046
                goto L_0x0053
            L_0x0046:
                int r6 = r13.visibleState
                int r7 = r0.getVisibleState()
                if (r6 == r7) goto L_0x006d
                com.android.systemui.statusbar.notification.stack.AnimationProperties r6 = com.android.systemui.statusbar.phone.NotificationIconContainer.DOT_ANIMATION_PROPERTIES
                goto L_0x006b
            L_0x0053:
                super.applyToView(r0)
                boolean r6 = r13.justAdded
                if (r6 == 0) goto L_0x006d
                float r6 = r13.iconAppearAmount
                r7 = 0
                int r6 = (r6 > r7 ? 1 : (r6 == r7 ? 0 : -1))
                if (r6 == 0) goto L_0x006d
                r0.setAlpha(r7)
                r0.setVisibleState((int) r3, (boolean) r1)
                com.android.systemui.statusbar.notification.stack.AnimationProperties r6 = com.android.systemui.statusbar.phone.NotificationIconContainer.ADD_ICON_PROPERTIES
            L_0x006b:
                r7 = r4
                goto L_0x006f
            L_0x006d:
                r7 = r1
                r6 = r5
            L_0x006f:
                if (r7 != 0) goto L_0x0096
                com.android.systemui.statusbar.phone.NotificationIconContainer r8 = com.android.systemui.statusbar.phone.NotificationIconContainer.this
                int r8 = r8.mAddAnimationStartIndex
                if (r8 < 0) goto L_0x0096
                com.android.systemui.statusbar.phone.NotificationIconContainer r8 = com.android.systemui.statusbar.phone.NotificationIconContainer.this
                int r8 = r8.indexOfChild(r14)
                com.android.systemui.statusbar.phone.NotificationIconContainer r9 = com.android.systemui.statusbar.phone.NotificationIconContainer.this
                int r9 = r9.mAddAnimationStartIndex
                if (r8 < r9) goto L_0x0096
                int r8 = r0.getVisibleState()
                if (r8 != r3) goto L_0x0091
                int r8 = r13.visibleState
                if (r8 == r3) goto L_0x0096
            L_0x0091:
                com.android.systemui.statusbar.notification.stack.AnimationProperties r6 = com.android.systemui.statusbar.phone.NotificationIconContainer.DOT_ANIMATION_PROPERTIES
                r7 = r4
            L_0x0096:
                boolean r8 = r13.needsCannedAnimation
                r9 = 100
                if (r8 == 0) goto L_0x0102
                com.android.systemui.statusbar.notification.stack.AnimationProperties r7 = com.android.systemui.statusbar.phone.NotificationIconContainer.sTempProperties
                com.android.systemui.statusbar.notification.stack.AnimationFilter r7 = r7.getAnimationFilter()
                r7.reset()
                com.android.systemui.statusbar.notification.stack.AnimationProperties r8 = com.android.systemui.statusbar.phone.NotificationIconContainer.ICON_ANIMATION_PROPERTIES
                com.android.systemui.statusbar.notification.stack.AnimationFilter r8 = r8.getAnimationFilter()
                r7.combineFilter(r8)
                com.android.systemui.statusbar.notification.stack.AnimationProperties r8 = com.android.systemui.statusbar.phone.NotificationIconContainer.sTempProperties
                r8.resetCustomInterpolators()
                com.android.systemui.statusbar.notification.stack.AnimationProperties r8 = com.android.systemui.statusbar.phone.NotificationIconContainer.sTempProperties
                com.android.systemui.statusbar.notification.stack.AnimationProperties r11 = com.android.systemui.statusbar.phone.NotificationIconContainer.ICON_ANIMATION_PROPERTIES
                r8.combineCustomInterpolators(r11)
                boolean r8 = r0.showsConversation()
                if (r8 == 0) goto L_0x00cd
                android.view.animation.Interpolator r8 = com.android.systemui.animation.Interpolators.ICON_OVERSHOT_LESS
                goto L_0x00cf
            L_0x00cd:
                android.view.animation.Interpolator r8 = com.android.systemui.animation.Interpolators.ICON_OVERSHOT
            L_0x00cf:
                com.android.systemui.statusbar.notification.stack.AnimationProperties r11 = com.android.systemui.statusbar.phone.NotificationIconContainer.sTempProperties
                android.util.Property r12 = android.view.View.TRANSLATION_Y
                r11.setCustomInterpolator(r12, r8)
                com.android.systemui.statusbar.notification.stack.AnimationProperties r8 = com.android.systemui.statusbar.phone.NotificationIconContainer.sTempProperties
                java.util.function.Consumer<android.util.Property> r11 = r13.mCannedAnimationEndListener
                r8.setAnimationEndAction(r11)
                if (r6 == 0) goto L_0x00f1
                com.android.systemui.statusbar.notification.stack.AnimationFilter r8 = r6.getAnimationFilter()
                r7.combineFilter(r8)
                com.android.systemui.statusbar.notification.stack.AnimationProperties r7 = com.android.systemui.statusbar.phone.NotificationIconContainer.sTempProperties
                r7.combineCustomInterpolators(r6)
            L_0x00f1:
                com.android.systemui.statusbar.notification.stack.AnimationProperties r6 = com.android.systemui.statusbar.phone.NotificationIconContainer.sTempProperties
                r6.setDuration(r9)
                com.android.systemui.statusbar.phone.NotificationIconContainer r7 = com.android.systemui.statusbar.phone.NotificationIconContainer.this
                int r8 = r7.indexOfChild(r14)
                int unused = r7.mCannedAnimationStartIndex = r8
                r7 = r4
            L_0x0102:
                if (r7 != 0) goto L_0x0141
                com.android.systemui.statusbar.phone.NotificationIconContainer r8 = com.android.systemui.statusbar.phone.NotificationIconContainer.this
                int r8 = r8.mCannedAnimationStartIndex
                if (r8 < 0) goto L_0x0141
                com.android.systemui.statusbar.phone.NotificationIconContainer r8 = com.android.systemui.statusbar.phone.NotificationIconContainer.this
                int r8 = r8.indexOfChild(r14)
                com.android.systemui.statusbar.phone.NotificationIconContainer r11 = com.android.systemui.statusbar.phone.NotificationIconContainer.this
                int r11 = r11.mCannedAnimationStartIndex
                if (r8 <= r11) goto L_0x0141
                int r8 = r0.getVisibleState()
                if (r8 != r3) goto L_0x0124
                int r8 = r13.visibleState
                if (r8 == r3) goto L_0x0141
            L_0x0124:
                com.android.systemui.statusbar.notification.stack.AnimationProperties r3 = com.android.systemui.statusbar.phone.NotificationIconContainer.sTempProperties
                com.android.systemui.statusbar.notification.stack.AnimationFilter r3 = r3.getAnimationFilter()
                r3.reset()
                r3.animateX()
                com.android.systemui.statusbar.notification.stack.AnimationProperties r3 = com.android.systemui.statusbar.phone.NotificationIconContainer.sTempProperties
                r3.resetCustomInterpolators()
                com.android.systemui.statusbar.notification.stack.AnimationProperties r6 = com.android.systemui.statusbar.phone.NotificationIconContainer.sTempProperties
                r6.setDuration(r9)
                r7 = r4
            L_0x0141:
                com.android.systemui.statusbar.phone.NotificationIconContainer r3 = com.android.systemui.statusbar.phone.NotificationIconContainer.this
                android.view.View r3 = r3.mIsolatedIconForAnimation
                if (r3 == 0) goto L_0x0178
                com.android.systemui.statusbar.phone.NotificationIconContainer r3 = com.android.systemui.statusbar.phone.NotificationIconContainer.this
                android.view.View r3 = r3.mIsolatedIconForAnimation
                r6 = 0
                if (r14 != r3) goto L_0x0165
                com.android.systemui.statusbar.notification.stack.AnimationProperties r3 = com.android.systemui.statusbar.phone.NotificationIconContainer.UNISOLATION_PROPERTY
                com.android.systemui.statusbar.phone.NotificationIconContainer r8 = com.android.systemui.statusbar.phone.NotificationIconContainer.this
                com.android.systemui.statusbar.StatusBarIconView r8 = r8.mIsolatedIcon
                if (r8 == 0) goto L_0x0160
                goto L_0x0161
            L_0x0160:
                r9 = r6
            L_0x0161:
                r3.setDelay(r9)
                goto L_0x0176
            L_0x0165:
                com.android.systemui.statusbar.notification.stack.AnimationProperties r3 = com.android.systemui.statusbar.phone.NotificationIconContainer.UNISOLATION_PROPERTY_OTHERS
                com.android.systemui.statusbar.phone.NotificationIconContainer r8 = com.android.systemui.statusbar.phone.NotificationIconContainer.this
                com.android.systemui.statusbar.StatusBarIconView r8 = r8.mIsolatedIcon
                if (r8 != 0) goto L_0x0172
                goto L_0x0173
            L_0x0172:
                r9 = r6
            L_0x0173:
                r3.setDelay(r9)
            L_0x0176:
                r6 = r3
                r7 = r4
            L_0x0178:
                com.android.systemui.statusbar.phone.NotificationIconContainer r3 = com.android.systemui.statusbar.phone.NotificationIconContainer.this
                boolean r3 = r3.mIsCarousel
                if (r3 == 0) goto L_0x0192
                if (r7 != 0) goto L_0x0192
                com.android.systemui.statusbar.phone.NotificationIconContainer r3 = com.android.systemui.statusbar.phone.NotificationIconContainer.this
                int r3 = r3.getTransientViewCount()
                if (r3 <= 0) goto L_0x0192
                com.android.systemui.statusbar.notification.stack.AnimationProperties r6 = com.android.systemui.statusbar.phone.NotificationIconContainer.DOT_ANIMATION_PROPERTIES
                r7 = r4
                goto L_0x0192
            L_0x0190:
                r7 = r1
                r6 = r5
            L_0x0192:
                int r3 = r13.visibleState
                r0.setVisibleState((int) r3, (boolean) r2)
                boolean r3 = com.android.systemui.moto.MotoFeature.isPrcProduct()
                if (r3 == 0) goto L_0x01b5
                com.android.systemui.statusbar.phone.NotificationIconContainer r3 = com.android.systemui.statusbar.phone.NotificationIconContainer.this
                com.android.internal.util.ContrastColorUtil r3 = r3.mContrastColorUtil
                boolean r3 = com.android.systemui.statusbar.notification.NotificationUtils.isGrayscale(r0, r3)
                if (r3 != 0) goto L_0x01b5
                com.android.systemui.statusbar.phone.NotificationIconContainer r3 = com.android.systemui.statusbar.phone.NotificationIconContainer.this
                boolean r3 = r3.mInNotificationIconShelf
                if (r3 == 0) goto L_0x01b5
                r0.setIconColor(r1, r1)
                goto L_0x01d2
            L_0x01b5:
                com.android.systemui.statusbar.phone.NotificationIconContainer r3 = com.android.systemui.statusbar.phone.NotificationIconContainer.this
                boolean r3 = r3.mInNotificationIconShelf
                if (r3 == 0) goto L_0x01c4
                com.android.systemui.statusbar.phone.NotificationIconContainer r3 = com.android.systemui.statusbar.phone.NotificationIconContainer.this
                int r3 = r3.mThemedTextColorPrimary
                goto L_0x01c6
            L_0x01c4:
                int r3 = r13.iconColor
            L_0x01c6:
                boolean r8 = r13.needsCannedAnimation
                if (r8 == 0) goto L_0x01ce
                if (r2 == 0) goto L_0x01ce
                r2 = r4
                goto L_0x01cf
            L_0x01ce:
                r2 = r1
            L_0x01cf:
                r0.setIconColor(r3, r2)
            L_0x01d2:
                if (r7 == 0) goto L_0x01d8
                r13.animateTo(r0, r6)
                goto L_0x01db
            L_0x01d8:
                super.applyToView(r14)
            L_0x01db:
                com.android.systemui.statusbar.notification.stack.AnimationProperties r14 = com.android.systemui.statusbar.phone.NotificationIconContainer.sTempProperties
                r14.setAnimationEndAction(r5)
                float r14 = r13.iconAppearAmount
                r2 = 1065353216(0x3f800000, float:1.0)
                int r14 = (r14 > r2 ? 1 : (r14 == r2 ? 0 : -1))
                if (r14 != 0) goto L_0x01eb
                goto L_0x01ec
            L_0x01eb:
                r4 = r1
            L_0x01ec:
                r0.setIsInShelf(r4)
            L_0x01ef:
                r13.justAdded = r1
                r13.justReplaced = r1
                r13.needsCannedAnimation = r1
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.statusbar.phone.NotificationIconContainer.IconState.applyToView(android.view.View):void");
        }

        public void initFrom(View view) {
            super.initFrom(view);
            if (view instanceof StatusBarIconView) {
                this.iconColor = ((StatusBarIconView) view).getStaticDrawableColor();
            }
        }
    }

    public void setIsCarousel(boolean z) {
        this.mIsCarousel = z;
        this.mIconDis = getResources().getDimensionPixelSize(R$dimen.cli_overflow_icon_padding);
        int dimensionPixelSize = getResources().getDimensionPixelSize(R$dimen.cli_status_bar_icon_size);
        this.mIconWidth = dimensionPixelSize;
        this.mPositionWidth = dimensionPixelSize + this.mIconDis;
        REMOVED_ANIMATION_DURATION = 200;
    }

    private void updateVisualIndex(int i, boolean z) {
        this.mVisualIndex = i;
        this.mAddAnimationStartIndex = i;
        if (z) {
            updateState();
        }
    }

    private void updateTransitionX(int i) {
        int i2;
        int childCount = getChildCount();
        if (childCount > 4) {
            float f = 0.0f;
            for (int i3 = 0; i3 < childCount; i3++) {
                View childAt = getChildAt(i3);
                float width = (float) ((((i3 - this.mVisualIndex) + 1) * (childAt.getWidth() + this.mIconDis)) + i);
                childAt.setTranslationX(width);
                if (width < 0.0f || width > ((float) (getWidth() - childAt.getWidth()))) {
                    i2 = 2;
                } else {
                    float f2 = ((float) this.mPositionWidth) - width;
                    float width2 = ((float) (getWidth() - childAt.getWidth())) - width;
                    if (f2 > 0.0f) {
                        int i4 = this.mPositionWidth;
                        if (f2 < ((float) i4)) {
                            f = width / ((float) i4);
                            i2 = 0;
                        }
                    }
                    int i5 = this.mPositionWidth;
                    f = (width2 >= ((float) i5) || width2 <= 0.0f) ? 1.0f : width2 / ((float) i5);
                    i2 = 0;
                }
                ((StatusBarIconView) childAt).setVisibleState(i2, f);
            }
        }
    }

    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        boolean handleTouchEvent;
        if (!this.mIsCarousel || !(handleTouchEvent = handleTouchEvent(motionEvent))) {
            return super.onInterceptTouchEvent(motionEvent);
        }
        return handleTouchEvent;
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (!this.mIsCarousel) {
            return super.onTouchEvent(motionEvent);
        }
        handleTouchEvent(motionEvent);
        return true;
    }

    private boolean handleTouchEvent(MotionEvent motionEvent) {
        int rawX = (int) motionEvent.getRawX();
        int rawY = (int) motionEvent.getRawY();
        if (motionEvent.getActionMasked() == 0) {
            this.mDownX = rawX;
            this.mDownY = rawY;
        } else if (motionEvent.getActionMasked() == 2) {
            int i = rawX - this.mDownX;
            if (Math.abs(i) > 10) {
                updateTransitionX(i);
                return true;
            }
        } else if (motionEvent.getActionMasked() == 1 || motionEvent.getActionMasked() == 3) {
            updateVisualIndex(findVisualIndex(rawX - this.mDownX < 0), true);
        }
        return false;
    }

    private int findVisualIndex(boolean z) {
        int childCount = getChildCount();
        int i = -1;
        for (int i2 = 0; i2 < childCount; i2++) {
            if (Math.abs(getChildAt(i2).getTranslationX() - ((float) this.mPositionWidth)) < ((float) (this.mPositionWidth / 2))) {
                i = i2;
            }
        }
        if (i == -1) {
            i = z ? childCount - 4 : 0;
        }
        int i3 = childCount - 4;
        if (i > i3) {
            i = i3;
        }
        if (i < 0) {
            return 0;
        }
        return i;
    }

    public void setOnChildClickListener(View.OnClickListener onClickListener) {
        this.mOnChildClickListener = onClickListener;
    }

    public void updateSelectedIcon(StatusBarIconView statusBarIconView) {
        StatusBarIconView statusBarIconView2 = this.mSelectedIcon;
        if (statusBarIconView2 != statusBarIconView) {
            if (statusBarIconView != null) {
                statusBarIconView.setSelected(true);
            }
            StatusBarIconView statusBarIconView3 = this.mSelectedIcon;
            if (statusBarIconView3 != null) {
                statusBarIconView3.setSelected(false);
            }
            this.mSelectedIcon = statusBarIconView;
        } else if (statusBarIconView2 != null) {
            statusBarIconView2.setSelected(false);
            this.mSelectedIcon = null;
        }
    }

    public void calculateIconTranslationsForCli() {
        float f;
        float width;
        int i = this.mVisualIndex + 4;
        int childCount = getChildCount();
        if (childCount <= 4) {
            int i2 = 0;
            for (int i3 = 0; i3 < childCount; i3++) {
                View childAt = getChildAt(i3);
                i2 = (int) (((float) i2) + (this.mIconStates.get(childAt).iconAppearAmount * ((float) childAt.getWidth())));
            }
            f = (float) (((getWidth() - i2) - (this.mIconDis * (childCount - 1))) / 2);
        } else {
            f = 0.0f;
        }
        for (int i4 = 0; i4 < childCount; i4++) {
            View childAt2 = getChildAt(i4);
            IconState iconState = this.mIconStates.get(childAt2);
            int i5 = this.mVisualIndex;
            if (i4 == i5 - 1) {
                iconState.visibleState = 1;
            } else if (i4 < i5 || i4 >= i) {
                if (i4 == i) {
                    iconState.visibleState = 1;
                } else {
                    iconState.visibleState = 2;
                }
            } else if (iconState.visibleState == 0) {
                ((StatusBarIconView) childAt2).setIconAppearAmount(1.0f);
            } else {
                iconState.visibleState = 0;
            }
            if (childCount <= 4) {
                iconState.xTranslation = f;
                width = f + ((float) (childAt2.getWidth() + this.mIconDis));
            } else {
                width = (float) (((i4 - this.mVisualIndex) + 1) * (childAt2.getWidth() + this.mIconDis));
                iconState.xTranslation = width;
            }
        }
    }
}
