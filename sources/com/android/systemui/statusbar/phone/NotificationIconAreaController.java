package com.android.systemui.statusbar.phone;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import androidx.collection.ArrayMap;
import com.android.internal.statusbar.StatusBarIcon;
import com.android.internal.util.ContrastColorUtil;
import com.android.p011wm.shell.bubbles.Bubbles;
import com.android.settingslib.Utils;
import com.android.systemui.R$attr;
import com.android.systemui.R$dimen;
import com.android.systemui.R$id;
import com.android.systemui.R$layout;
import com.android.systemui.animation.Interpolators;
import com.android.systemui.demomode.DemoMode;
import com.android.systemui.demomode.DemoModeController;
import com.android.systemui.doze.MotoDisplayManager;
import com.android.systemui.moto.MotoFeature;
import com.android.systemui.plugins.DarkIconDispatcher;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.statusbar.CrossFadeHelper;
import com.android.systemui.statusbar.NotificationListener;
import com.android.systemui.statusbar.NotificationMediaManager;
import com.android.systemui.statusbar.NotificationShelfController;
import com.android.systemui.statusbar.StatusBarIconView;
import com.android.systemui.statusbar.notification.AnimatableProperty;
import com.android.systemui.statusbar.notification.NotificationUtils;
import com.android.systemui.statusbar.notification.NotificationWakeUpCoordinator;
import com.android.systemui.statusbar.notification.PropertyAnimator;
import com.android.systemui.statusbar.notification.collection.ListEntry;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import com.android.systemui.statusbar.notification.stack.AnimationProperties;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

public class NotificationIconAreaController implements DarkIconDispatcher.DarkReceiver, StatusBarStateController.StateListener, NotificationWakeUpCoordinator.WakeUpListener, DemoMode {
    private boolean mAnimationsEnabled;
    private int mAodIconAppearTranslation;
    private int mAodIconTint;
    private NotificationIconContainer mAodIcons;
    private boolean mAodIconsVisible;
    private final Optional<Bubbles> mBubblesOptional;
    private final KeyguardBypassController mBypassController;
    private NotificationIconContainer mCenteredIcon;
    protected View mCenteredIconArea;
    private int mCenteredIconTint = -1;
    private StatusBarIconView mCenteredIconView;
    private Context mContext;
    private final ContrastColorUtil mContrastColorUtil;
    private final DemoModeController mDemoModeController;
    private final DozeParameters mDozeParameters;
    private int mIconHPadding;
    private int mIconSize;
    private int mIconTint = -1;
    private final NotificationMediaManager mMediaManager;
    private List<ListEntry> mNotificationEntries = List.of();
    protected View mNotificationIconArea;
    private NotificationIconContainer mNotificationIcons;
    final NotificationListener.NotificationSettingsListener mSettingsListener;
    private NotificationIconContainer mShelfIcons;
    /* access modifiers changed from: private */
    public boolean mShowLowPriority = true;
    private final StatusBarStateController mStatusBarStateController;
    private final StatusBarWindowController mStatusBarWindowController;
    private final Rect mTintArea = new Rect();
    private final UnlockedScreenOffAnimationController mUnlockedScreenOffAnimationController;
    private final Runnable mUpdateStatusBarIcons = new NotificationIconAreaController$$ExternalSyntheticLambda0(this);
    private final NotificationWakeUpCoordinator mWakeUpCoordinator;

    public NotificationIconAreaController(Context context, StatusBarStateController statusBarStateController, NotificationWakeUpCoordinator notificationWakeUpCoordinator, KeyguardBypassController keyguardBypassController, NotificationMediaManager notificationMediaManager, NotificationListener notificationListener, DozeParameters dozeParameters, Optional<Bubbles> optional, DemoModeController demoModeController, DarkIconDispatcher darkIconDispatcher, StatusBarWindowController statusBarWindowController, UnlockedScreenOffAnimationController unlockedScreenOffAnimationController) {
        C18371 r0 = new NotificationListener.NotificationSettingsListener() {
            public void onStatusBarIconsBehaviorChanged(boolean z) {
                boolean unused = NotificationIconAreaController.this.mShowLowPriority = !z;
                NotificationIconAreaController.this.updateStatusBarIcons();
            }
        };
        this.mSettingsListener = r0;
        this.mContrastColorUtil = ContrastColorUtil.getInstance(context);
        this.mContext = context;
        this.mStatusBarStateController = statusBarStateController;
        statusBarStateController.addCallback(this);
        this.mMediaManager = notificationMediaManager;
        this.mDozeParameters = dozeParameters;
        this.mWakeUpCoordinator = notificationWakeUpCoordinator;
        notificationWakeUpCoordinator.addListener(this);
        this.mBypassController = keyguardBypassController;
        this.mBubblesOptional = optional;
        this.mDemoModeController = demoModeController;
        demoModeController.addCallback((DemoMode) this);
        this.mStatusBarWindowController = statusBarWindowController;
        this.mUnlockedScreenOffAnimationController = unlockedScreenOffAnimationController;
        notificationListener.addNotificationSettingsListener(r0);
        initializeNotificationAreaViews(context);
        reloadAodColor();
        darkIconDispatcher.addDarkReceiver((DarkIconDispatcher.DarkReceiver) this);
    }

    /* access modifiers changed from: protected */
    public View inflateIconArea(LayoutInflater layoutInflater) {
        return layoutInflater.inflate(R$layout.notification_icon_area, (ViewGroup) null);
    }

    /* access modifiers changed from: protected */
    public void initializeNotificationAreaViews(Context context) {
        reloadDimens(context);
        LayoutInflater from = LayoutInflater.from(context);
        View inflateIconArea = inflateIconArea(from);
        this.mNotificationIconArea = inflateIconArea;
        this.mNotificationIcons = (NotificationIconContainer) inflateIconArea.findViewById(R$id.notificationIcons);
        View inflate = from.inflate(R$layout.center_icon_area, (ViewGroup) null);
        this.mCenteredIconArea = inflate;
        this.mCenteredIcon = (NotificationIconContainer) inflate.findViewById(R$id.centeredIcon);
    }

    public void setupAodIcons(NotificationIconContainer notificationIconContainer) {
        NotificationIconContainer notificationIconContainer2 = this.mAodIcons;
        boolean z = (notificationIconContainer2 == null || notificationIconContainer == notificationIconContainer2) ? false : true;
        if (z) {
            notificationIconContainer2.setAnimationsEnabled(false);
            this.mAodIcons.removeAllViews();
        }
        this.mAodIcons = notificationIconContainer;
        notificationIconContainer.setOnLockScreen(true);
        updateAodIconsVisibility(false, z);
        updateAnimations();
        if (z) {
            updateAodNotificationIcons();
        }
        updateIconLayoutParams(this.mContext);
    }

    public void updatePosition(int i, AnimationProperties animationProperties, boolean z) {
        NotificationIconContainer notificationIconContainer = this.mAodIcons;
        if (notificationIconContainer != null) {
            PropertyAnimator.setProperty(notificationIconContainer, AnimatableProperty.TRANSLATION_X, (float) i, animationProperties, z);
        }
    }

    public void setupShelf(NotificationShelfController notificationShelfController) {
        this.mShelfIcons = notificationShelfController.getShelfIcons();
        notificationShelfController.setCollapsedIcons(this.mNotificationIcons);
    }

    public void onDensityOrFontScaleChanged(Context context) {
        updateIconLayoutParams(context);
    }

    private void updateIconLayoutParams(Context context) {
        reloadDimens(context);
        FrameLayout.LayoutParams generateIconLayoutParams = generateIconLayoutParams();
        for (int i = 0; i < this.mNotificationIcons.getChildCount(); i++) {
            this.mNotificationIcons.getChildAt(i).setLayoutParams(generateIconLayoutParams);
        }
        for (int i2 = 0; i2 < this.mCenteredIcon.getChildCount(); i2++) {
            this.mCenteredIcon.getChildAt(i2).setLayoutParams(generateIconLayoutParams);
        }
        if (this.mShelfIcons != null) {
            for (int i3 = 0; i3 < this.mShelfIcons.getChildCount(); i3++) {
                this.mShelfIcons.getChildAt(i3).setLayoutParams(generateIconLayoutParams);
            }
        }
        if (this.mAodIcons != null) {
            for (int i4 = 0; i4 < this.mAodIcons.getChildCount(); i4++) {
                this.mAodIcons.getChildAt(i4).setLayoutParams(generateIconLayoutParams);
            }
        }
    }

    private FrameLayout.LayoutParams generateIconLayoutParams() {
        return new FrameLayout.LayoutParams(this.mIconSize + (this.mIconHPadding * 2), this.mStatusBarWindowController.getStatusBarHeight());
    }

    private void reloadDimens(Context context) {
        Resources resources = context.getResources();
        this.mIconSize = resources.getDimensionPixelSize(17105536);
        this.mIconHPadding = resources.getDimensionPixelSize(R$dimen.status_bar_icon_padding);
        this.mAodIconAppearTranslation = resources.getDimensionPixelSize(R$dimen.shelf_appear_translation);
    }

    public View getNotificationInnerAreaView() {
        return this.mNotificationIconArea;
    }

    public View getCenteredNotificationAreaView() {
        return this.mCenteredIconArea;
    }

    public void onDarkChanged(Rect rect, float f, int i) {
        if (rect == null) {
            this.mTintArea.setEmpty();
        } else {
            this.mTintArea.set(rect);
        }
        if (DarkIconDispatcher.isInArea(rect, this.mNotificationIconArea)) {
            this.mIconTint = i;
        }
        if (DarkIconDispatcher.isInArea(rect, this.mCenteredIconArea)) {
            this.mCenteredIconTint = i;
        }
        applyNotificationIconsTint();
    }

    /* access modifiers changed from: protected */
    public boolean shouldShowNotificationIcon(NotificationEntry notificationEntry, boolean z, boolean z2, boolean z3, boolean z4, boolean z5, boolean z6, boolean z7, boolean z8) {
        boolean z9 = (this.mCenteredIconView == null || notificationEntry.getIcons().getCenteredIcon() == null || !Objects.equals(notificationEntry.getIcons().getCenteredIcon(), this.mCenteredIconView)) ? false : true;
        if (z8) {
            return z9;
        }
        if (z6 && z9 && !notificationEntry.isRowHeadsUp()) {
            return false;
        }
        if (notificationEntry.getRanking().isAmbient() && !z) {
            return false;
        }
        if (z5 && notificationEntry.getKey().equals(this.mMediaManager.getMediaNotificationKey())) {
            return false;
        }
        if ((!z2 && notificationEntry.getImportance() < 3) || !notificationEntry.isTopLevelChild() || notificationEntry.getRow().getVisibility() == 8) {
            return false;
        }
        if (notificationEntry.isRowDismissed() && z3) {
            return false;
        }
        if (z4 && notificationEntry.isLastMessageFromReply()) {
            return false;
        }
        if (!z && notificationEntry.shouldSuppressStatusBar()) {
            return false;
        }
        if (!z7 || !notificationEntry.showingPulsing() || (this.mWakeUpCoordinator.getNotificationsFullyHidden() && notificationEntry.isPulseSuppressed())) {
            return !this.mBubblesOptional.isPresent() || !this.mBubblesOptional.get().isBubbleExpanded(notificationEntry.getKey());
        }
        return false;
    }

    public void updateNotificationIcons(List<ListEntry> list) {
        this.mNotificationEntries = list;
        updateNotificationIcons();
    }

    private void updateNotificationIcons() {
        updateStatusBarIcons();
        updateShelfIcons();
        updateCenterIcon();
        updateAodNotificationIcons();
        applyNotificationIconsTint();
    }

    private void updateShelfIcons() {
        updateIconsForLayout(NotificationIconAreaController$$ExternalSyntheticLambda5.INSTANCE, this.mShelfIcons, true, true, false, false, false, false, false, false);
    }

    public void updateStatusBarIcons() {
        updateIconsForLayout(NotificationIconAreaController$$ExternalSyntheticLambda6.INSTANCE, this.mNotificationIcons, false, this.mShowLowPriority, true, true, false, true, false, false);
    }

    private void updateCenterIcon() {
        updateIconsForLayout(NotificationIconAreaController$$ExternalSyntheticLambda7.INSTANCE, this.mCenteredIcon, false, true, false, false, false, false, false, true);
    }

    public void updateAodNotificationIcons() {
        NotificationIconContainer notificationIconContainer = this.mAodIcons;
        if (notificationIconContainer != null) {
            updateIconsForLayout(NotificationIconAreaController$$ExternalSyntheticLambda4.INSTANCE, notificationIconContainer, false, true, true, true, true, true, this.mBypassController.getBypassEnabled(), false);
        }
    }

    /* access modifiers changed from: package-private */
    public boolean shouldShouldLowPriorityIcons() {
        return this.mShowLowPriority;
    }

    private void updateIconsForLayout(Function<NotificationEntry, StatusBarIconView> function, NotificationIconContainer notificationIconContainer, boolean z, boolean z2, boolean z3, boolean z4, boolean z5, boolean z6, boolean z7, boolean z8) {
        NotificationIconContainer notificationIconContainer2 = notificationIconContainer;
        ArrayList arrayList = new ArrayList(this.mNotificationEntries.size());
        for (int i = 0; i < this.mNotificationEntries.size(); i++) {
            NotificationEntry representativeEntry = this.mNotificationEntries.get(i).getRepresentativeEntry();
            if (representativeEntry == null || representativeEntry.getRow() == null || !shouldShowNotificationIcon(representativeEntry, z, z2, z3, z4, z5, z6, z7, z8)) {
                Function<NotificationEntry, StatusBarIconView> function2 = function;
            } else {
                StatusBarIconView apply = function.apply(representativeEntry);
                if (apply != null) {
                    arrayList.add(apply);
                }
            }
        }
        ArrayMap arrayMap = new ArrayMap();
        ArrayList arrayList2 = new ArrayList();
        for (int i2 = 0; i2 < notificationIconContainer.getChildCount(); i2++) {
            View childAt = notificationIconContainer2.getChildAt(i2);
            if ((childAt instanceof StatusBarIconView) && !arrayList.contains(childAt)) {
                StatusBarIconView statusBarIconView = (StatusBarIconView) childAt;
                String groupKey = statusBarIconView.getNotification().getGroupKey();
                int i3 = 0;
                boolean z9 = false;
                while (true) {
                    if (i3 >= arrayList.size()) {
                        break;
                    }
                    StatusBarIconView statusBarIconView2 = (StatusBarIconView) arrayList.get(i3);
                    if (statusBarIconView2.getSourceIcon().sameAs(statusBarIconView.getSourceIcon()) && statusBarIconView2.getNotification().getGroupKey().equals(groupKey)) {
                        if (z9) {
                            z9 = false;
                            break;
                        }
                        z9 = true;
                    }
                    i3++;
                }
                if (z9) {
                    ArrayList arrayList3 = (ArrayList) arrayMap.get(groupKey);
                    if (arrayList3 == null) {
                        arrayList3 = new ArrayList();
                        arrayMap.put(groupKey, arrayList3);
                    }
                    arrayList3.add(statusBarIconView.getStatusBarIcon());
                }
                arrayList2.add(statusBarIconView);
            }
        }
        ArrayList arrayList4 = new ArrayList();
        for (String str : arrayMap.keySet()) {
            if (((ArrayList) arrayMap.get(str)).size() != 1) {
                arrayList4.add(str);
            }
        }
        arrayMap.removeAll(arrayList4);
        notificationIconContainer2.setReplacingIcons(arrayMap);
        int size = arrayList2.size();
        for (int i4 = 0; i4 < size; i4++) {
            notificationIconContainer2.removeView((View) arrayList2.get(i4));
        }
        FrameLayout.LayoutParams generateIconLayoutParams = generateIconLayoutParams();
        for (int i5 = 0; i5 < arrayList.size(); i5++) {
            StatusBarIconView statusBarIconView3 = (StatusBarIconView) arrayList.get(i5);
            notificationIconContainer2.removeTransientView(statusBarIconView3);
            if (statusBarIconView3.getParent() == null) {
                if (z3) {
                    statusBarIconView3.setOnDismissListener(this.mUpdateStatusBarIcons);
                }
                notificationIconContainer2.addView(statusBarIconView3, i5, generateIconLayoutParams);
            }
        }
        notificationIconContainer2.setChangingViewPositions(true);
        int childCount = notificationIconContainer.getChildCount();
        for (int i6 = 0; i6 < childCount; i6++) {
            View childAt2 = notificationIconContainer2.getChildAt(i6);
            StatusBarIconView statusBarIconView4 = (StatusBarIconView) arrayList.get(i6);
            if (childAt2 != statusBarIconView4) {
                notificationIconContainer2.removeView(statusBarIconView4);
                notificationIconContainer2.addView(statusBarIconView4, i6);
            }
        }
        notificationIconContainer2.setChangingViewPositions(false);
        notificationIconContainer2.setReplacingIcons((ArrayMap<String, ArrayList<StatusBarIcon>>) null);
    }

    private void applyNotificationIconsTint() {
        for (int i = 0; i < this.mNotificationIcons.getChildCount(); i++) {
            StatusBarIconView statusBarIconView = (StatusBarIconView) this.mNotificationIcons.getChildAt(i);
            if (statusBarIconView.getWidth() != 0) {
                updateTintForIcon(statusBarIconView, this.mIconTint);
            } else {
                statusBarIconView.executeOnLayout(new NotificationIconAreaController$$ExternalSyntheticLambda3(this, statusBarIconView));
            }
        }
        for (int i2 = 0; i2 < this.mCenteredIcon.getChildCount(); i2++) {
            StatusBarIconView statusBarIconView2 = (StatusBarIconView) this.mCenteredIcon.getChildAt(i2);
            if (statusBarIconView2.getWidth() != 0) {
                updateTintForIcon(statusBarIconView2, this.mCenteredIconTint);
            } else {
                statusBarIconView2.executeOnLayout(new NotificationIconAreaController$$ExternalSyntheticLambda2(this, statusBarIconView2));
            }
        }
        updateAodIconColors();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$applyNotificationIconsTint$4(StatusBarIconView statusBarIconView) {
        updateTintForIcon(statusBarIconView, this.mIconTint);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$applyNotificationIconsTint$5(StatusBarIconView statusBarIconView) {
        updateTintForIcon(statusBarIconView, this.mCenteredIconTint);
    }

    private void updateTintForIcon(StatusBarIconView statusBarIconView, int i) {
        int i2 = 0;
        if ((!Boolean.TRUE.equals(statusBarIconView.getTag(R$id.icon_is_pre_L)) && !MotoFeature.isPrcProduct()) || NotificationUtils.isGrayscale(statusBarIconView, this.mContrastColorUtil)) {
            i2 = DarkIconDispatcher.getTint(this.mTintArea, statusBarIconView, i);
        }
        statusBarIconView.setStaticDrawableColor(i2);
        statusBarIconView.setDecorColor(DarkIconDispatcher.getTint(this.mTintArea, statusBarIconView, i));
    }

    public void showIconIsolated(StatusBarIconView statusBarIconView, boolean z) {
        this.mNotificationIcons.showIconIsolated(statusBarIconView, z);
    }

    public void setIsolatedIconLocation(Rect rect, boolean z) {
        this.mNotificationIcons.setIsolatedIconLocation(rect, z);
    }

    public void onDozingChanged(boolean z) {
        if (this.mAodIcons != null) {
            this.mAodIcons.setDozing(z, this.mDozeParameters.getAlwaysOn() && !this.mDozeParameters.getDisplayNeedsBlanking(), 0);
        }
    }

    public void setAnimationsEnabled(boolean z) {
        this.mAnimationsEnabled = z;
        updateAnimations();
    }

    public void onStateChanged(int i) {
        updateAodIconsVisibility(false, false);
        updateAnimations();
    }

    private void updateAnimations() {
        boolean z = true;
        boolean z2 = this.mStatusBarStateController.getState() == 0;
        NotificationIconContainer notificationIconContainer = this.mAodIcons;
        if (notificationIconContainer != null) {
            notificationIconContainer.setAnimationsEnabled(this.mAnimationsEnabled && !z2);
        }
        this.mCenteredIcon.setAnimationsEnabled(this.mAnimationsEnabled && z2);
        NotificationIconContainer notificationIconContainer2 = this.mNotificationIcons;
        if (!this.mAnimationsEnabled || !z2) {
            z = false;
        }
        notificationIconContainer2.setAnimationsEnabled(z);
    }

    public void onThemeChanged() {
        reloadAodColor();
        updateAodIconColors();
    }

    public int getHeight() {
        NotificationIconContainer notificationIconContainer = this.mAodIcons;
        if (notificationIconContainer == null) {
            return 0;
        }
        return notificationIconContainer.getHeight();
    }

    public void appearAodIcons() {
        if (this.mAodIcons != null) {
            if (this.mDozeParameters.shouldControlScreenOff()) {
                this.mAodIcons.setTranslationY((float) (-this.mAodIconAppearTranslation));
                this.mAodIcons.setAlpha(0.0f);
                animateInAodIconTranslation();
                this.mAodIcons.animate().alpha(1.0f).setInterpolator(Interpolators.LINEAR).setDuration(200).start();
                return;
            }
            this.mAodIcons.setAlpha(1.0f);
            this.mAodIcons.setTranslationY(0.0f);
        }
    }

    private void animateInAodIconTranslation() {
        this.mAodIcons.animate().setInterpolator(Interpolators.DECELERATE_QUINT).translationY(0.0f).setDuration(200).start();
    }

    private void reloadAodColor() {
        this.mAodIconTint = Utils.getColorAttrDefaultColor(this.mContext, R$attr.wallpaperTextColor);
    }

    private void updateAodIconColors() {
        if (this.mAodIcons != null) {
            for (int i = 0; i < this.mAodIcons.getChildCount(); i++) {
                StatusBarIconView statusBarIconView = (StatusBarIconView) this.mAodIcons.getChildAt(i);
                if (statusBarIconView.getWidth() != 0) {
                    updateTintForIcon(statusBarIconView, this.mAodIconTint);
                } else {
                    statusBarIconView.executeOnLayout(new NotificationIconAreaController$$ExternalSyntheticLambda1(this, statusBarIconView));
                }
            }
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$updateAodIconColors$6(StatusBarIconView statusBarIconView) {
        updateTintForIcon(statusBarIconView, this.mAodIconTint);
    }

    public void onFullyHiddenChanged(boolean z) {
        boolean z2 = true;
        if (!this.mBypassController.getBypassEnabled()) {
            if (!this.mDozeParameters.getAlwaysOn() || this.mDozeParameters.getDisplayNeedsBlanking()) {
                z2 = false;
            }
            z2 &= z;
        }
        updateAodIconsVisibility(z2, false);
        updateAodNotificationIcons();
    }

    public void onPulseExpansionChanged(boolean z) {
        if (z) {
            updateAodIconsVisibility(true, false);
        }
    }

    private void updateAodIconsVisibility(boolean z, boolean z2) {
        if (this.mAodIcons != null) {
            boolean z3 = true;
            int i = 0;
            boolean z4 = (this.mBypassController.getBypassEnabled() || this.mWakeUpCoordinator.getNotificationsFullyHidden()) && MotoDisplayManager.isAospAD();
            if (this.mStatusBarStateController.getState() != 1 && !this.mUnlockedScreenOffAnimationController.isScreenOffAnimationPlaying()) {
                z4 = false;
            }
            if (z4 && this.mWakeUpCoordinator.isPulseExpanding() && !this.mBypassController.getBypassEnabled()) {
                z4 = false;
            }
            if (this.mAodIconsVisible != z4 || z2) {
                this.mAodIconsVisible = z4;
                this.mAodIcons.animate().cancel();
                if (z) {
                    if (this.mAodIcons.getVisibility() == 0) {
                        z3 = false;
                    }
                    if (!this.mAodIconsVisible) {
                        animateInAodIconTranslation();
                        CrossFadeHelper.fadeOut(this.mAodIcons);
                    } else if (z3) {
                        this.mAodIcons.setVisibility(0);
                        this.mAodIcons.setAlpha(1.0f);
                        appearAodIcons();
                    } else {
                        animateInAodIconTranslation();
                        CrossFadeHelper.fadeIn(this.mAodIcons);
                    }
                } else {
                    this.mAodIcons.setAlpha(1.0f);
                    this.mAodIcons.setTranslationY(0.0f);
                    NotificationIconContainer notificationIconContainer = this.mAodIcons;
                    if (!z4) {
                        i = 4;
                    }
                    notificationIconContainer.setVisibility(i);
                }
            }
        }
    }

    public List<String> demoCommands() {
        ArrayList arrayList = new ArrayList();
        arrayList.add("notifications");
        return arrayList;
    }

    public void dispatchDemoCommand(String str, Bundle bundle) {
        if (this.mNotificationIconArea != null) {
            this.mNotificationIconArea.setVisibility("false".equals(bundle.getString("visible")) ? 4 : 0);
        }
    }

    public void onDemoModeFinished() {
        View view = this.mNotificationIconArea;
        if (view != null) {
            view.setVisibility(0);
        }
    }
}
