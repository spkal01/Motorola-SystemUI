package com.android.systemui.statusbar;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.MathUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.animation.Interpolator;
import android.view.animation.PathInterpolator;
import com.android.internal.annotations.VisibleForTesting;
import com.android.systemui.R$bool;
import com.android.systemui.R$dimen;
import com.android.systemui.R$id;
import com.android.systemui.R$string;
import com.android.systemui.animation.Interpolators;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.statusbar.notification.row.ActivatableNotificationView;
import com.android.systemui.statusbar.notification.row.ExpandableNotificationRow;
import com.android.systemui.statusbar.notification.row.ExpandableView;
import com.android.systemui.statusbar.notification.stack.AmbientState;
import com.android.systemui.statusbar.notification.stack.AnimationProperties;
import com.android.systemui.statusbar.notification.stack.ExpandableViewState;
import com.android.systemui.statusbar.notification.stack.NotificationStackScrollLayoutController;
import com.android.systemui.statusbar.notification.stack.StackScrollAlgorithm;
import com.android.systemui.statusbar.notification.stack.ViewState;
import com.android.systemui.statusbar.phone.NotificationIconContainer;

public class NotificationShelf extends ActivatableNotificationView implements View.OnLayoutChangeListener, StatusBarStateController.StateListener {
    private static final Interpolator ICON_ALPHA_INTERPOLATOR = new PathInterpolator(0.6f, 0.0f, 0.6f, 0.0f);
    /* access modifiers changed from: private */
    public static final int TAG_CONTINUOUS_CLIPPING = R$id.continuous_clipping_tag;
    private AmbientState mAmbientState;
    /* access modifiers changed from: private */
    public boolean mAnimationsEnabled = true;
    private Rect mClipRect = new Rect();
    private NotificationIconContainer mCollapsedIcons;
    private NotificationShelfController mController;
    private float mCornerAnimationDistance;
    private float mFirstElementRoundness;
    private boolean mHasItemsInStableShelf;
    private boolean mHideBackground;
    private NotificationStackScrollLayoutController mHostLayoutController;
    private int mIndexOfFirstViewInShelf = -1;
    private boolean mInteractive;
    private int mNotGoneIndex;
    private int mPaddingBetweenElements;
    private int mScrollFastThreshold;
    /* access modifiers changed from: private */
    public NotificationIconContainer mShelfIcons;
    /* access modifiers changed from: private */
    public boolean mShowNotificationShelf;
    private int mStatusBarHeight;
    private int mStatusBarState;
    private int[] mTmp = new int[2];

    public boolean hasNoContentHeight() {
        return true;
    }

    public boolean hasOverlappingRendering() {
        return false;
    }

    public boolean needsClippingToShelf() {
        return false;
    }

    public NotificationShelf(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    @VisibleForTesting
    public void onFinishInflate() {
        super.onFinishInflate();
        NotificationIconContainer notificationIconContainer = (NotificationIconContainer) findViewById(R$id.content);
        this.mShelfIcons = notificationIconContainer;
        notificationIconContainer.setClipChildren(false);
        this.mShelfIcons.setClipToPadding(false);
        setClipToActualHeight(false);
        setClipChildren(false);
        setClipToPadding(false);
        this.mShelfIcons.setIsStaticLayout(false);
        setBottomRoundness(1.0f, false);
        setTopRoundness(1.0f, false);
        setFirstInSection(true);
        initDimens();
    }

    public void bind(AmbientState ambientState, NotificationStackScrollLayoutController notificationStackScrollLayoutController) {
        this.mAmbientState = ambientState;
        this.mHostLayoutController = notificationStackScrollLayoutController;
    }

    private void initDimens() {
        Resources resources = getResources();
        this.mStatusBarHeight = resources.getDimensionPixelOffset(R$dimen.status_bar_height);
        this.mPaddingBetweenElements = resources.getDimensionPixelSize(R$dimen.notification_divider_height);
        ViewGroup.LayoutParams layoutParams = getLayoutParams();
        layoutParams.height = resources.getDimensionPixelOffset(R$dimen.notification_shelf_height);
        setLayoutParams(layoutParams);
        int dimensionPixelOffset = resources.getDimensionPixelOffset(R$dimen.shelf_icon_container_padding);
        this.mShelfIcons.setPadding(dimensionPixelOffset, 0, dimensionPixelOffset, 0);
        this.mScrollFastThreshold = resources.getDimensionPixelOffset(R$dimen.scroll_fast_threshold);
        this.mShowNotificationShelf = resources.getBoolean(R$bool.config_showNotificationShelf);
        this.mCornerAnimationDistance = (float) resources.getDimensionPixelSize(R$dimen.notification_corner_animation_distance);
        this.mShelfIcons.setInNotificationIconShelf(true);
        if (!this.mShowNotificationShelf) {
            setVisibility(8);
        }
    }

    /* access modifiers changed from: protected */
    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        initDimens();
    }

    /* access modifiers changed from: protected */
    public View getContentView() {
        return this.mShelfIcons;
    }

    public NotificationIconContainer getShelfIcons() {
        return this.mShelfIcons;
    }

    public ExpandableViewState createExpandableViewState() {
        return new ShelfState();
    }

    public void updateState(StackScrollAlgorithm.StackScrollAlgorithmState stackScrollAlgorithmState, AmbientState ambientState) {
        ExpandableView lastVisibleBackgroundChild = ambientState.getLastVisibleBackgroundChild();
        ShelfState shelfState = (ShelfState) getViewState();
        boolean z = false;
        if (!this.mShowNotificationShelf || lastVisibleBackgroundChild == null) {
            shelfState.hidden = true;
            shelfState.location = 64;
            boolean unused = shelfState.hasItemsInStableShelf = false;
            return;
        }
        ExpandableViewState viewState = lastVisibleBackgroundChild.getViewState();
        shelfState.copyFrom(viewState);
        shelfState.height = getIntrinsicHeight();
        shelfState.zTranslation = (float) ambientState.getBaseZHeight();
        shelfState.clipTopAmount = 0;
        if (!ambientState.isExpansionChanging() || ambientState.isOnKeyguard()) {
            shelfState.alpha = 1.0f - ambientState.getHideAmount();
        } else {
            shelfState.alpha = Interpolators.getNotificationScrimAlpha(ambientState.getExpansionFraction(), true);
        }
        shelfState.belowSpeedBump = this.mHostLayoutController.getSpeedBumpIndex() == 0;
        shelfState.hideSensitive = false;
        shelfState.xTranslation = getTranslationX();
        boolean unused2 = shelfState.hasItemsInStableShelf = viewState.inShelf;
        ExpandableView unused3 = shelfState.firstViewInShelf = stackScrollAlgorithmState.firstViewInShelf;
        int i = this.mNotGoneIndex;
        if (i != -1) {
            shelfState.notGoneIndex = Math.min(shelfState.notGoneIndex, i);
        }
        if (!this.mAmbientState.isShadeExpanded() || this.mAmbientState.isQsCustomizerShowing() || stackScrollAlgorithmState.firstViewInShelf == null) {
            z = true;
        }
        shelfState.hidden = z;
        int indexOf = stackScrollAlgorithmState.visibleChildren.indexOf(stackScrollAlgorithmState.firstViewInShelf);
        if (this.mAmbientState.isExpansionChanging() && stackScrollAlgorithmState.firstViewInShelf != null && indexOf > 0 && stackScrollAlgorithmState.visibleChildren.get(indexOf - 1).getViewState().hidden) {
            shelfState.hidden = true;
        }
        shelfState.yTranslation = (ambientState.getStackY() + ambientState.getStackHeight()) - ((float) shelfState.height);
    }

    /* JADX WARNING: Removed duplicated region for block: B:64:0x0158  */
    /* JADX WARNING: Removed duplicated region for block: B:68:0x0164  */
    /* JADX WARNING: Removed duplicated region for block: B:77:0x018b  */
    /* JADX WARNING: Removed duplicated region for block: B:78:0x019a  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void updateAppearance() {
        /*
            r29 = this;
            r6 = r29
            boolean r0 = r6.mShowNotificationShelf
            if (r0 != 0) goto L_0x0007
            return
        L_0x0007:
            com.android.systemui.statusbar.phone.NotificationIconContainer r0 = r6.mShelfIcons
            r0.resetViewStates()
            float r7 = r29.getTranslationY()
            com.android.systemui.statusbar.notification.stack.AmbientState r0 = r6.mAmbientState
            com.android.systemui.statusbar.notification.row.ExpandableView r8 = r0.getLastVisibleBackgroundChild()
            r9 = -1
            r6.mNotGoneIndex = r9
            boolean r0 = r6.mHideBackground
            if (r0 == 0) goto L_0x002b
            com.android.systemui.statusbar.notification.stack.ExpandableViewState r0 = r29.getViewState()
            com.android.systemui.statusbar.NotificationShelf$ShelfState r0 = (com.android.systemui.statusbar.NotificationShelf.ShelfState) r0
            boolean r0 = r0.hasItemsInStableShelf
            if (r0 != 0) goto L_0x002b
            r12 = 1
            goto L_0x002c
        L_0x002b:
            r12 = 0
        L_0x002c:
            com.android.systemui.statusbar.notification.stack.AmbientState r0 = r6.mAmbientState
            float r0 = r0.getCurrentScrollVelocity()
            int r1 = r6.mScrollFastThreshold
            float r1 = (float) r1
            int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r0 > 0) goto L_0x0055
            com.android.systemui.statusbar.notification.stack.AmbientState r0 = r6.mAmbientState
            boolean r0 = r0.isExpansionChanging()
            if (r0 == 0) goto L_0x0053
            com.android.systemui.statusbar.notification.stack.AmbientState r0 = r6.mAmbientState
            float r0 = r0.getExpandingVelocity()
            float r0 = java.lang.Math.abs(r0)
            int r1 = r6.mScrollFastThreshold
            float r1 = (float) r1
            int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r0 <= 0) goto L_0x0053
            goto L_0x0055
        L_0x0053:
            r13 = 0
            goto L_0x0056
        L_0x0055:
            r13 = 1
        L_0x0056:
            com.android.systemui.statusbar.notification.stack.AmbientState r0 = r6.mAmbientState
            boolean r0 = r0.isExpansionChanging()
            if (r0 == 0) goto L_0x0068
            com.android.systemui.statusbar.notification.stack.AmbientState r0 = r6.mAmbientState
            boolean r0 = r0.isPanelTracking()
            if (r0 != 0) goto L_0x0068
            r14 = 1
            goto L_0x0069
        L_0x0068:
            r14 = 0
        L_0x0069:
            com.android.systemui.statusbar.notification.stack.AmbientState r0 = r6.mAmbientState
            int r15 = r0.getBaseZHeight()
            r0 = 0
            r1 = 0
            r2 = 0
            r3 = 0
            r4 = 0
            r10 = 0
            r11 = 0
            r16 = 0
            r17 = 0
            r18 = 0
        L_0x007c:
            com.android.systemui.statusbar.notification.stack.NotificationStackScrollLayoutController r5 = r6.mHostLayoutController
            int r5 = r5.getChildCount()
            r20 = 1065353216(0x3f800000, float:1.0)
            r9 = 8
            if (r4 >= r5) goto L_0x01e8
            com.android.systemui.statusbar.notification.stack.NotificationStackScrollLayoutController r5 = r6.mHostLayoutController
            com.android.systemui.statusbar.notification.row.ExpandableView r5 = r5.getChildAt(r4)
            boolean r21 = r5.needsClippingToShelf()
            if (r21 == 0) goto L_0x01bf
            r21 = r0
            int r0 = r5.getVisibility()
            if (r0 != r9) goto L_0x00b2
            r24 = r1
            r25 = r2
            r27 = r4
            r23 = r8
            r19 = r13
            r4 = r17
            r8 = r21
            r0 = 0
            r13 = 0
            r17 = r11
            r21 = r15
            goto L_0x01d2
        L_0x00b2:
            float r0 = com.android.systemui.statusbar.notification.stack.ViewState.getFinalTranslationZ(r5)
            float r9 = (float) r15
            int r0 = (r0 > r9 ? 1 : (r0 == r9 ? 0 : -1))
            if (r0 > 0) goto L_0x00c4
            boolean r0 = r5.isPinned()
            if (r0 == 0) goto L_0x00c2
            goto L_0x00c4
        L_0x00c2:
            r9 = 0
            goto L_0x00c5
        L_0x00c4:
            r9 = 1
        L_0x00c5:
            if (r5 != r8) goto L_0x00ca
            r22 = 1
            goto L_0x00cc
        L_0x00ca:
            r22 = 0
        L_0x00cc:
            float r0 = r5.getTranslationY()
            r23 = r8
            r8 = r21
            r21 = r15
            r15 = r0
            r0 = r29
            r24 = r1
            r1 = r4
            r25 = r2
            r2 = r5
            r26 = r11
            r11 = r3
            r3 = r13
            r27 = r4
            r4 = r14
            r28 = r5
            r19 = r13
            r13 = 0
            r5 = r22
            float r0 = r0.updateShelfTransformation(r1, r2, r3, r4, r5)
            if (r22 == 0) goto L_0x00f9
            boolean r1 = r28.isInShelf()
            if (r1 == 0) goto L_0x0104
        L_0x00f9:
            if (r9 != 0) goto L_0x0104
            if (r12 == 0) goto L_0x00fe
            goto L_0x0104
        L_0x00fe:
            int r1 = r6.mPaddingBetweenElements
            float r1 = (float) r1
            float r1 = r7 - r1
            goto L_0x010a
        L_0x0104:
            int r1 = r29.getIntrinsicHeight()
            float r1 = (float) r1
            float r1 = r1 + r7
        L_0x010a:
            r2 = r28
            int r1 = r6.updateNotificationClipHeight(r2, r1, r8)
            int r3 = java.lang.Math.max(r1, r11)
            boolean r1 = r2 instanceof com.android.systemui.statusbar.notification.row.ExpandableNotificationRow
            if (r1 == 0) goto L_0x01a6
            r5 = r2
            com.android.systemui.statusbar.notification.row.ExpandableNotificationRow r5 = (com.android.systemui.statusbar.notification.row.ExpandableNotificationRow) r5
            float r16 = r16 + r0
            int r1 = r5.getBackgroundColorWithoutTint()
            int r4 = (r15 > r7 ? 1 : (r15 == r7 ? 0 : -1))
            if (r4 < 0) goto L_0x013e
            int r4 = r6.mNotGoneIndex
            r11 = -1
            if (r4 != r11) goto L_0x0139
            r6.mNotGoneIndex = r8
            r6.setTintColor(r10)
            r4 = r17
            r10 = r26
            r6.setOverrideTintColor(r10, r4)
            r17 = r10
            goto L_0x014a
        L_0x0139:
            r4 = r17
            r17 = r26
            goto L_0x0143
        L_0x013e:
            r4 = r17
            r17 = r26
            r11 = -1
        L_0x0143:
            int r13 = r6.mNotGoneIndex
            if (r13 != r11) goto L_0x014a
            r17 = r0
            goto L_0x014e
        L_0x014a:
            r10 = r17
            r17 = r4
        L_0x014e:
            if (r22 == 0) goto L_0x0164
            com.android.systemui.statusbar.NotificationShelfController r4 = r6.mController
            boolean r4 = r4.canModifyColorOfNotifications()
            if (r4 == 0) goto L_0x0164
            if (r18 != 0) goto L_0x015c
            r4 = r1
            goto L_0x015e
        L_0x015c:
            r4 = r18
        L_0x015e:
            r5.setOverrideTintColor(r4, r0)
            r0 = 0
            r13 = 0
            goto L_0x016a
        L_0x0164:
            r0 = 0
            r13 = 0
            r5.setOverrideTintColor(r13, r0)
            r4 = r1
        L_0x016a:
            if (r8 != 0) goto L_0x016e
            if (r9 != 0) goto L_0x0171
        L_0x016e:
            r5.setAboveShelf(r13)
        L_0x0171:
            if (r8 != 0) goto L_0x019a
            com.android.systemui.statusbar.notification.collection.NotificationEntry r9 = r5.getEntry()
            com.android.systemui.statusbar.notification.icon.IconPack r9 = r9.getIcons()
            com.android.systemui.statusbar.StatusBarIconView r9 = r9.getShelfIcon()
            com.android.systemui.statusbar.phone.NotificationIconContainer$IconState r9 = r6.getIconState(r9)
            if (r9 == 0) goto L_0x019a
            float r9 = r9.clampedAppearAmount
            int r9 = (r9 > r20 ? 1 : (r9 == r20 ? 0 : -1))
            if (r9 != 0) goto L_0x019a
            float r9 = r2.getTranslationY()
            float r11 = r29.getTranslationY()
            float r9 = r9 - r11
            int r9 = (int) r9
            float r5 = r5.getCurrentTopRoundness()
            goto L_0x019e
        L_0x019a:
            r9 = r24
            r5 = r25
        L_0x019e:
            int r8 = r8 + 1
            r18 = r4
            r11 = r10
            r10 = r1
            r1 = r9
            goto L_0x01b4
        L_0x01a6:
            r0 = r13
            r4 = r17
            r17 = r26
            r13 = 0
            r11 = r17
            r1 = r24
            r5 = r25
            r17 = r4
        L_0x01b4:
            boolean r4 = r2 instanceof com.android.systemui.statusbar.notification.row.ActivatableNotificationView
            if (r4 == 0) goto L_0x01bd
            com.android.systemui.statusbar.notification.row.ActivatableNotificationView r2 = (com.android.systemui.statusbar.notification.row.ActivatableNotificationView) r2
            r6.updateCornerRoundnessOnScroll(r2, r15, r7)
        L_0x01bd:
            r2 = r5
            goto L_0x01dc
        L_0x01bf:
            r24 = r1
            r25 = r2
            r27 = r4
            r23 = r8
            r19 = r13
            r21 = r15
            r4 = r17
            r13 = 0
            r8 = r0
            r17 = r11
            r0 = 0
        L_0x01d2:
            r11 = r3
            r3 = r11
            r11 = r17
            r1 = r24
            r2 = r25
            r17 = r4
        L_0x01dc:
            int r4 = r27 + 1
            r0 = r8
            r13 = r19
            r15 = r21
            r8 = r23
            r9 = -1
            goto L_0x007c
        L_0x01e8:
            r8 = r0
            r24 = r1
            r25 = r2
            r11 = r3
            r13 = 0
            r29.clipTransientViews()
            r6.setClipTopAmount(r11)
            com.android.systemui.statusbar.notification.stack.ExpandableViewState r0 = r29.getViewState()
            boolean r0 = r0.hidden
            if (r0 != 0) goto L_0x020e
            int r0 = r29.getIntrinsicHeight()
            if (r11 >= r0) goto L_0x020e
            boolean r0 = r6.mShowNotificationShelf
            if (r0 == 0) goto L_0x020e
            int r0 = (r16 > r20 ? 1 : (r16 == r20 ? 0 : -1))
            if (r0 >= 0) goto L_0x020c
            goto L_0x020e
        L_0x020c:
            r10 = r13
            goto L_0x020f
        L_0x020e:
            r10 = 1
        L_0x020f:
            if (r10 == 0) goto L_0x0213
            r0 = 4
            goto L_0x0214
        L_0x0213:
            r0 = r13
        L_0x0214:
            r6.setVisibility(r0)
            r1 = r24
            r6.setBackgroundTop(r1)
            r2 = r25
            r6.setFirstElementRoundness(r2)
            com.android.systemui.statusbar.phone.NotificationIconContainer r0 = r6.mShelfIcons
            com.android.systemui.statusbar.notification.stack.NotificationStackScrollLayoutController r1 = r6.mHostLayoutController
            int r1 = r1.getSpeedBumpIndex()
            r0.setSpeedBumpIndex(r1)
            com.android.systemui.statusbar.phone.NotificationIconContainer r0 = r6.mShelfIcons
            r0.calculateIconTranslations()
            com.android.systemui.statusbar.phone.NotificationIconContainer r0 = r6.mShelfIcons
            r0.applyIconStates()
            r11 = r13
        L_0x0237:
            com.android.systemui.statusbar.notification.stack.NotificationStackScrollLayoutController r0 = r6.mHostLayoutController
            int r0 = r0.getChildCount()
            if (r11 >= r0) goto L_0x0258
            com.android.systemui.statusbar.notification.stack.NotificationStackScrollLayoutController r0 = r6.mHostLayoutController
            com.android.systemui.statusbar.notification.row.ExpandableView r0 = r0.getChildAt(r11)
            boolean r1 = r0 instanceof com.android.systemui.statusbar.notification.row.ExpandableNotificationRow
            if (r1 == 0) goto L_0x0255
            int r1 = r0.getVisibility()
            if (r1 != r9) goto L_0x0250
            goto L_0x0255
        L_0x0250:
            com.android.systemui.statusbar.notification.row.ExpandableNotificationRow r0 = (com.android.systemui.statusbar.notification.row.ExpandableNotificationRow) r0
            r6.updateContinuousClipping(r0)
        L_0x0255:
            int r11 = r11 + 1
            goto L_0x0237
        L_0x0258:
            r6.setHideBackground(r10)
            int r0 = r6.mNotGoneIndex
            r1 = -1
            if (r0 != r1) goto L_0x0262
            r6.mNotGoneIndex = r8
        L_0x0262:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.statusbar.NotificationShelf.updateAppearance():void");
    }

    private void updateCornerRoundnessOnScroll(ActivatableNotificationView activatableNotificationView, float f, float f2) {
        boolean z = true;
        boolean z2 = !this.mAmbientState.isOnKeyguard() && !this.mAmbientState.isShadeExpanded() && (activatableNotificationView instanceof ExpandableNotificationRow) && ((ExpandableNotificationRow) activatableNotificationView).isHeadsUp();
        boolean z3 = this.mAmbientState.isShadeExpanded() && activatableNotificationView == this.mAmbientState.getTrackedHeadsUpRow();
        if (f >= f2 || this.mHostLayoutController.isViewAffectedBySwipe(activatableNotificationView) || z2 || z3 || activatableNotificationView.isAboveShelf() || this.mAmbientState.isPulsing() || this.mAmbientState.isDozing()) {
            z = false;
        }
        if (z) {
            float dimension = getResources().getDimension(R$dimen.notification_corner_radius_small) / getResources().getDimension(R$dimen.notification_corner_radius);
            float actualHeight = ((float) activatableNotificationView.getActualHeight()) + f;
            float expansionFraction = this.mCornerAnimationDistance * this.mAmbientState.getExpansionFraction();
            float f3 = f2 - expansionFraction;
            float f4 = 1.0f;
            if (actualHeight >= f3) {
                float saturate = MathUtils.saturate((actualHeight - f3) / expansionFraction);
                if (activatableNotificationView.isLastInSection()) {
                    saturate = 1.0f;
                }
                activatableNotificationView.setBottomRoundness(saturate, false);
            } else if (actualHeight < f3) {
                activatableNotificationView.setBottomRoundness(activatableNotificationView.isLastInSection() ? 1.0f : dimension, false);
            }
            if (f >= f3) {
                float saturate2 = MathUtils.saturate((f - f3) / expansionFraction);
                if (!activatableNotificationView.isFirstInSection()) {
                    f4 = saturate2;
                }
                activatableNotificationView.setTopRoundness(f4, false);
            } else if (f < f3) {
                if (activatableNotificationView.isFirstInSection()) {
                    dimension = 1.0f;
                }
                activatableNotificationView.setTopRoundness(dimension, false);
            }
        }
    }

    private void clipTransientViews() {
        for (int i = 0; i < this.mHostLayoutController.getTransientViewCount(); i++) {
            View transientView = this.mHostLayoutController.getTransientView(i);
            if (transientView instanceof ExpandableView) {
                updateNotificationClipHeight((ExpandableView) transientView, getTranslationY(), -1);
            }
        }
    }

    private void setFirstElementRoundness(float f) {
        if (this.mFirstElementRoundness != f) {
            this.mFirstElementRoundness = f;
        }
    }

    /* access modifiers changed from: private */
    public void updateIconClipAmount(ExpandableNotificationRow expandableNotificationRow) {
        float translationY = expandableNotificationRow.getTranslationY();
        if (getClipTopAmount() != 0) {
            translationY = Math.max(translationY, getTranslationY() + ((float) getClipTopAmount()));
        }
        StatusBarIconView shelfIcon = expandableNotificationRow.getEntry().getIcons().getShelfIcon();
        float translationY2 = getTranslationY() + ((float) shelfIcon.getTop()) + shelfIcon.getTranslationY();
        if (translationY2 >= translationY || this.mAmbientState.isFullyHidden()) {
            shelfIcon.setClipBounds((Rect) null);
            return;
        }
        int i = (int) (translationY - translationY2);
        shelfIcon.setClipBounds(new Rect(0, i, shelfIcon.getWidth(), Math.max(i, shelfIcon.getHeight())));
    }

    private void updateContinuousClipping(final ExpandableNotificationRow expandableNotificationRow) {
        final StatusBarIconView shelfIcon = expandableNotificationRow.getEntry().getIcons().getShelfIcon();
        boolean z = true;
        boolean z2 = ViewState.isAnimatingY(shelfIcon) && !this.mAmbientState.isDozing();
        int i = TAG_CONTINUOUS_CLIPPING;
        if (shelfIcon.getTag(i) == null) {
            z = false;
        }
        if (z2 && !z) {
            final ViewTreeObserver viewTreeObserver = shelfIcon.getViewTreeObserver();
            final C14791 r2 = new ViewTreeObserver.OnPreDrawListener() {
                public boolean onPreDraw() {
                    if (!ViewState.isAnimatingY(shelfIcon)) {
                        if (viewTreeObserver.isAlive()) {
                            viewTreeObserver.removeOnPreDrawListener(this);
                        }
                        shelfIcon.setTag(NotificationShelf.TAG_CONTINUOUS_CLIPPING, (Object) null);
                        return true;
                    }
                    NotificationShelf.this.updateIconClipAmount(expandableNotificationRow);
                    return true;
                }
            };
            viewTreeObserver.addOnPreDrawListener(r2);
            shelfIcon.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
                public void onViewAttachedToWindow(View view) {
                }

                public void onViewDetachedFromWindow(View view) {
                    if (view == shelfIcon) {
                        if (viewTreeObserver.isAlive()) {
                            viewTreeObserver.removeOnPreDrawListener(r2);
                        }
                        shelfIcon.setTag(NotificationShelf.TAG_CONTINUOUS_CLIPPING, (Object) null);
                    }
                }
            });
            shelfIcon.setTag(i, r2);
        }
    }

    private int updateNotificationClipHeight(ExpandableView expandableView, float f, int i) {
        float translationY = expandableView.getTranslationY() + ((float) expandableView.getActualHeight());
        boolean z = true;
        boolean z2 = (expandableView.isPinned() || expandableView.isHeadsUpAnimatingAway()) && !this.mAmbientState.isDozingAndNotPulsing(expandableView);
        if (!this.mAmbientState.isPulseExpanding()) {
            z = expandableView.showingPulsing();
        } else if (i != 0) {
            z = false;
        }
        if (translationY <= f || z || (!this.mAmbientState.isShadeExpanded() && z2)) {
            expandableView.setClipBottomAmount(0);
        } else {
            int i2 = (int) (translationY - f);
            if (z2) {
                i2 = Math.min(expandableView.getIntrinsicHeight() - expandableView.getCollapsedHeight(), i2);
            }
            expandableView.setClipBottomAmount(i2);
        }
        if (z) {
            return (int) (translationY - getTranslationY());
        }
        return 0;
    }

    public void setFakeShadowIntensity(float f, float f2, int i, int i2) {
        if (!this.mHasItemsInStableShelf) {
            f = 0.0f;
        }
        super.setFakeShadowIntensity(f, f2, i, i2);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:10:0x005a, code lost:
        if (r12 >= r1) goto L_0x005c;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private float updateShelfTransformation(int r12, com.android.systemui.statusbar.notification.row.ExpandableView r13, boolean r14, boolean r15, boolean r16) {
        /*
            r11 = this;
            r0 = r11
            float r1 = r13.getTranslationY()
            int r2 = r13.getActualHeight()
            int r3 = r0.mPaddingBetweenElements
            int r2 = r2 + r3
            r3 = r13
            float r4 = r11.calculateIconTransformationStart(r13)
            float r5 = (float) r2
            float r5 = r5 + r1
            float r5 = r5 - r4
            int r6 = r11.getIntrinsicHeight()
            float r6 = (float) r6
            float r5 = java.lang.Math.min(r5, r6)
            if (r16 == 0) goto L_0x003a
            int r6 = r13.getMinHeight()
            int r7 = r11.getIntrinsicHeight()
            int r6 = r6 - r7
            int r2 = java.lang.Math.min(r2, r6)
            int r6 = r13.getMinHeight()
            int r7 = r11.getIntrinsicHeight()
            int r6 = r6 - r7
            float r6 = (float) r6
            float r5 = java.lang.Math.min(r5, r6)
        L_0x003a:
            float r2 = (float) r2
            float r6 = r1 + r2
            float r7 = r11.getTranslationY()
            com.android.systemui.statusbar.notification.stack.AmbientState r8 = r0.mAmbientState
            boolean r8 = r8.isExpansionChanging()
            r9 = 0
            r10 = 1065353216(0x3f800000, float:1.0)
            if (r8 == 0) goto L_0x005f
            com.android.systemui.statusbar.notification.stack.AmbientState r8 = r0.mAmbientState
            boolean r8 = r8.isOnKeyguard()
            if (r8 != 0) goto L_0x005f
            int r1 = r0.mIndexOfFirstViewInShelf
            r2 = -1
            if (r1 == r2) goto L_0x00a3
            r2 = r12
            if (r2 < r1) goto L_0x00a3
        L_0x005c:
            r2 = r10
            r9 = r2
            goto L_0x00a4
        L_0x005f:
            int r6 = (r6 > r7 ? 1 : (r6 == r7 ? 0 : -1))
            if (r6 < 0) goto L_0x00a3
            com.android.systemui.statusbar.notification.stack.AmbientState r6 = r0.mAmbientState
            boolean r6 = r6.isUnlockHintRunning()
            if (r6 == 0) goto L_0x0071
            boolean r6 = r13.isInShelf()
            if (r6 == 0) goto L_0x00a3
        L_0x0071:
            com.android.systemui.statusbar.notification.stack.AmbientState r6 = r0.mAmbientState
            boolean r6 = r6.isShadeExpanded()
            if (r6 != 0) goto L_0x0085
            boolean r6 = r13.isPinned()
            if (r6 != 0) goto L_0x00a3
            boolean r6 = r13.isHeadsUpAnimatingAway()
            if (r6 != 0) goto L_0x00a3
        L_0x0085:
            int r6 = (r1 > r7 ? 1 : (r1 == r7 ? 0 : -1))
            if (r6 >= 0) goto L_0x005c
            float r6 = r7 - r1
            float r2 = r6 / r2
            float r2 = java.lang.Math.min(r10, r2)
            float r2 = r10 - r2
            if (r16 == 0) goto L_0x0098
            float r4 = r4 - r1
            float r6 = r6 / r4
            goto L_0x009b
        L_0x0098:
            float r7 = r7 - r4
            float r6 = r7 / r5
        L_0x009b:
            float r1 = android.util.MathUtils.constrain(r6, r9, r10)
            float r10 = r10 - r1
            r9 = r2
            r2 = r10
            goto L_0x00a4
        L_0x00a3:
            r2 = r9
        L_0x00a4:
            r0 = r11
            r1 = r13
            r3 = r14
            r4 = r15
            r5 = r16
            r0.updateIconPositioning(r1, r2, r3, r4, r5)
            return r9
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.statusbar.NotificationShelf.updateShelfTransformation(int, com.android.systemui.statusbar.notification.row.ExpandableView, boolean, boolean, boolean):float");
    }

    private float calculateIconTransformationStart(ExpandableView expandableView) {
        View shelfTransformationTarget = expandableView.getShelfTransformationTarget();
        if (shelfTransformationTarget == null) {
            return expandableView.getTranslationY();
        }
        return (expandableView.getTranslationY() + ((float) expandableView.getRelativeTopPadding(shelfTransformationTarget))) - ((float) expandableView.getShelfIcon().getTop());
    }

    private void updateIconPositioning(ExpandableView expandableView, float f, boolean z, boolean z2, boolean z3) {
        StatusBarIconView shelfIcon = expandableView.getShelfIcon();
        NotificationIconContainer.IconState iconState = getIconState(shelfIcon);
        if (iconState != null) {
            boolean z4 = false;
            float f2 = (f > 0.5f ? 1 : (f == 0.5f ? 0 : -1)) > 0 || isTargetClipped(expandableView) ? 1.0f : 0.0f;
            if (f == f2) {
                iconState.noAnimations = (z || z2) && !z3;
            }
            if (!z3 && (z || (z2 && !ViewState.isAnimatingY(shelfIcon)))) {
                iconState.cancelAnimations(shelfIcon);
                iconState.noAnimations = true;
            }
            if (!this.mAmbientState.isHiddenAtAll() || expandableView.isInShelf()) {
                if (iconState.clampedAppearAmount != f2) {
                    z4 = true;
                }
                iconState.needsCannedAnimation = z4;
            } else {
                f = this.mAmbientState.isFullyHidden() ? 1.0f : 0.0f;
            }
            iconState.clampedAppearAmount = f2;
            setIconTransformationAmount(expandableView, f);
        }
    }

    private boolean isTargetClipped(ExpandableView expandableView) {
        View shelfTransformationTarget = expandableView.getShelfTransformationTarget();
        if (shelfTransformationTarget != null && expandableView.getTranslationY() + expandableView.getContentTranslation() + ((float) expandableView.getRelativeTopPadding(shelfTransformationTarget)) + ((float) shelfTransformationTarget.getHeight()) >= getTranslationY() - ((float) this.mPaddingBetweenElements)) {
            return true;
        }
        return false;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:3:0x0005, code lost:
        r8 = (com.android.systemui.statusbar.notification.row.ExpandableNotificationRow) r8;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void setIconTransformationAmount(com.android.systemui.statusbar.notification.row.ExpandableView r8, float r9) {
        /*
            r7 = this;
            boolean r0 = r8 instanceof com.android.systemui.statusbar.notification.row.ExpandableNotificationRow
            if (r0 != 0) goto L_0x0005
            return
        L_0x0005:
            com.android.systemui.statusbar.notification.row.ExpandableNotificationRow r8 = (com.android.systemui.statusbar.notification.row.ExpandableNotificationRow) r8
            com.android.systemui.statusbar.StatusBarIconView r1 = r8.getShelfIcon()
            com.android.systemui.statusbar.phone.NotificationIconContainer$IconState r2 = r7.getIconState(r1)
            if (r2 != 0) goto L_0x0012
            return
        L_0x0012:
            android.view.animation.Interpolator r3 = ICON_ALPHA_INTERPOLATOR
            float r3 = r3.getInterpolation(r9)
            r2.alpha = r3
            boolean r3 = r8.isDrawingAppearAnimation()
            r4 = 1
            r5 = 0
            if (r3 == 0) goto L_0x002a
            boolean r3 = r8.isInShelf()
            if (r3 != 0) goto L_0x002a
            r3 = r4
            goto L_0x002b
        L_0x002a:
            r3 = r5
        L_0x002b:
            r6 = 0
            if (r3 != 0) goto L_0x0066
            if (r0 == 0) goto L_0x003e
            boolean r0 = r8.isLowPriority()
            if (r0 == 0) goto L_0x003e
            com.android.systemui.statusbar.phone.NotificationIconContainer r0 = r7.mShelfIcons
            boolean r0 = r0.hasMaxNumDot()
            if (r0 != 0) goto L_0x0066
        L_0x003e:
            int r0 = (r9 > r6 ? 1 : (r9 == r6 ? 0 : -1))
            if (r0 != 0) goto L_0x0048
            boolean r0 = r2.isAnimating(r1)
            if (r0 == 0) goto L_0x0066
        L_0x0048:
            boolean r0 = r8.isAboveShelf()
            if (r0 != 0) goto L_0x0066
            boolean r0 = r8.showingPulsing()
            if (r0 != 0) goto L_0x0066
            float r0 = r8.getTranslationZ()
            com.android.systemui.statusbar.notification.stack.AmbientState r3 = r7.mAmbientState
            int r3 = r3.getBaseZHeight()
            float r3 = (float) r3
            int r0 = (r0 > r3 ? 1 : (r0 == r3 ? 0 : -1))
            if (r0 <= 0) goto L_0x0064
            goto L_0x0066
        L_0x0064:
            r0 = r5
            goto L_0x0067
        L_0x0066:
            r0 = r4
        L_0x0067:
            r2.hidden = r0
            if (r0 == 0) goto L_0x006c
            r9 = r6
        L_0x006c:
            r2.iconAppearAmount = r9
            com.android.systemui.statusbar.phone.NotificationIconContainer r9 = r7.mShelfIcons
            float r9 = r9.getActualPaddingStart()
            r2.xTranslation = r9
            boolean r9 = r8.isInShelf()
            if (r9 == 0) goto L_0x0083
            boolean r9 = r8.isTransformingIntoShelf()
            if (r9 != 0) goto L_0x0083
            goto L_0x0084
        L_0x0083:
            r4 = r5
        L_0x0084:
            if (r4 == 0) goto L_0x008e
            r9 = 1065353216(0x3f800000, float:1.0)
            r2.iconAppearAmount = r9
            r2.alpha = r9
            r2.hidden = r5
        L_0x008e:
            int r7 = r7.getBackgroundColorWithoutTint()
            int r7 = r1.getContrastedStaticDrawableColor(r7)
            boolean r9 = r8.isShowingIcon()
            if (r9 == 0) goto L_0x00a8
            if (r7 == 0) goto L_0x00a8
            int r8 = r8.getOriginalIconColor()
            float r9 = r2.iconAppearAmount
            int r7 = com.android.systemui.statusbar.notification.NotificationUtils.interpolateColors(r8, r7, r9)
        L_0x00a8:
            r2.iconColor = r7
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.statusbar.NotificationShelf.setIconTransformationAmount(com.android.systemui.statusbar.notification.row.ExpandableView, float):void");
    }

    private NotificationIconContainer.IconState getIconState(StatusBarIconView statusBarIconView) {
        return this.mShelfIcons.getIconState(statusBarIconView);
    }

    private void setHideBackground(boolean z) {
        if (this.mHideBackground != z) {
            this.mHideBackground = z;
            updateOutline();
        }
    }

    /* access modifiers changed from: protected */
    public boolean needsOutline() {
        return !this.mHideBackground && super.needsOutline();
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        super.onLayout(z, i, i2, i3, i4);
        updateRelativeOffset();
        int i5 = getResources().getDisplayMetrics().heightPixels;
        this.mClipRect.set(0, -i5, getWidth(), i5);
        this.mShelfIcons.setClipBounds(this.mClipRect);
    }

    private void updateRelativeOffset() {
        this.mCollapsedIcons.getLocationOnScreen(this.mTmp);
        getLocationOnScreen(this.mTmp);
    }

    public int getNotGoneIndex() {
        return this.mNotGoneIndex;
    }

    /* access modifiers changed from: private */
    public void setHasItemsInStableShelf(boolean z) {
        if (this.mHasItemsInStableShelf != z) {
            this.mHasItemsInStableShelf = z;
            updateInteractiveness();
        }
    }

    public void setCollapsedIcons(NotificationIconContainer notificationIconContainer) {
        this.mCollapsedIcons = notificationIconContainer;
        notificationIconContainer.addOnLayoutChangeListener(this);
    }

    public void onStateChanged(int i) {
        this.mStatusBarState = i;
        updateInteractiveness();
    }

    private void updateInteractiveness() {
        int i = 1;
        boolean z = this.mStatusBarState == 1 && this.mHasItemsInStableShelf;
        this.mInteractive = z;
        setClickable(z);
        setFocusable(this.mInteractive);
        if (!this.mInteractive) {
            i = 4;
        }
        setImportantForAccessibility(i);
    }

    public void setAnimationsEnabled(boolean z) {
        this.mAnimationsEnabled = z;
        if (!z) {
            this.mShelfIcons.setAnimationsEnabled(false);
        }
    }

    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
        super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
        if (this.mInteractive) {
            accessibilityNodeInfo.addAction(AccessibilityNodeInfo.AccessibilityAction.ACTION_EXPAND);
            accessibilityNodeInfo.addAction(new AccessibilityNodeInfo.AccessibilityAction(16, getContext().getString(R$string.accessibility_overflow_action)));
        }
    }

    public void onLayoutChange(View view, int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8) {
        updateRelativeOffset();
    }

    public void setController(NotificationShelfController notificationShelfController) {
        this.mController = notificationShelfController;
    }

    public void setIndexOfFirstViewInShelf(ExpandableView expandableView) {
        this.mIndexOfFirstViewInShelf = this.mHostLayoutController.indexOfChild(expandableView);
    }

    private class ShelfState extends ExpandableViewState {
        /* access modifiers changed from: private */
        public ExpandableView firstViewInShelf;
        /* access modifiers changed from: private */
        public boolean hasItemsInStableShelf;

        private ShelfState() {
        }

        public void applyToView(View view) {
            if (NotificationShelf.this.mShowNotificationShelf) {
                super.applyToView(view);
                NotificationShelf.this.setIndexOfFirstViewInShelf(this.firstViewInShelf);
                NotificationShelf.this.updateAppearance();
                NotificationShelf.this.setHasItemsInStableShelf(this.hasItemsInStableShelf);
                NotificationShelf.this.mShelfIcons.setAnimationsEnabled(NotificationShelf.this.mAnimationsEnabled);
            }
        }

        public void animateTo(View view, AnimationProperties animationProperties) {
            if (NotificationShelf.this.mShowNotificationShelf) {
                super.animateTo(view, animationProperties);
                NotificationShelf.this.setIndexOfFirstViewInShelf(this.firstViewInShelf);
                NotificationShelf.this.updateAppearance();
                NotificationShelf.this.setHasItemsInStableShelf(this.hasItemsInStableShelf);
                NotificationShelf.this.mShelfIcons.setAnimationsEnabled(NotificationShelf.this.mAnimationsEnabled);
            }
        }
    }
}
