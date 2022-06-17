package com.android.systemui.statusbar.phone;

import android.content.Context;
import android.os.Bundle;
import android.service.notification.StatusBarNotification;
import android.text.TextUtils;
import android.util.ArraySet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.android.internal.statusbar.StatusBarIcon;
import com.android.systemui.Dependency;
import com.android.systemui.R$array;
import com.android.systemui.R$dimen;
import com.android.systemui.demomode.DemoModeCommandReceiver;
import com.android.systemui.plugins.DarkIconDispatcher;
import com.android.systemui.statusbar.FeatureFlags;
import com.android.systemui.statusbar.StatusBarIconView;
import com.android.systemui.statusbar.StatusBarMobileView;
import com.android.systemui.statusbar.StatusBarMobileViewDual;
import com.android.systemui.statusbar.StatusBarWifiView;
import com.android.systemui.statusbar.StatusIconDisplayable;
import com.android.systemui.statusbar.phone.StatusBarSignalPolicy;
import java.util.ArrayList;
import java.util.List;

public interface StatusBarIconController {
    void addIconGroup(IconManager iconManager);

    void removeAllIconsForSlot(String str);

    void removeIcon(String str, int i);

    void removeIconGroup(IconManager iconManager);

    void setCallStrengthIcons(String str, List<StatusBarSignalPolicy.CallIndicatorIconState> list);

    void setExternalIcon(String str);

    void setIcon(String str, int i, CharSequence charSequence);

    void setIcon(String str, StatusBarIcon statusBarIcon);

    void setIconAccessibilityLiveRegion(String str, int i);

    void setIconVisibility(String str, boolean z);

    void setMobileIcons(String str, List<StatusBarSignalPolicy.MobileIconState> list);

    void setNoCallingIcons(String str, List<StatusBarSignalPolicy.CallIndicatorIconState> list);

    void setSignalIcon(String str, StatusBarSignalPolicy.WifiIconState wifiIconState);

    static ArraySet<String> getIconHideList(Context context, String str) {
        String[] strArr;
        ArraySet<String> arraySet = new ArraySet<>();
        if (str == null) {
            strArr = context.getResources().getStringArray(R$array.config_statusBarIconsToExclude);
        } else {
            strArr = str.split(",");
        }
        for (String str2 : strArr) {
            if (!TextUtils.isEmpty(str2)) {
                arraySet.add(str2);
            }
        }
        return arraySet;
    }

    public static class DarkIconManager extends IconManager {
        private final DarkIconDispatcher mDarkIconDispatcher = ((DarkIconDispatcher) Dependency.get(DarkIconDispatcher.class));
        private int mIconHPadding = this.mContext.getResources().getDimensionPixelSize(R$dimen.status_bar_icon_padding);

        public DarkIconManager(LinearLayout linearLayout, FeatureFlags featureFlags) {
            super(linearLayout, featureFlags);
        }

        /* access modifiers changed from: protected */
        public void onIconAdded(int i, String str, boolean z, StatusBarIconHolder statusBarIconHolder) {
            this.mDarkIconDispatcher.addDarkReceiver((DarkIconDispatcher.DarkReceiver) addHolder(i, str, z, statusBarIconHolder));
        }

        /* access modifiers changed from: protected */
        public LinearLayout.LayoutParams onCreateLayoutParams() {
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(-2, this.mIconSize);
            int i = this.mIconHPadding;
            layoutParams.setMargins(i, 0, i, 0);
            return layoutParams;
        }

        /* access modifiers changed from: protected */
        public void destroy() {
            for (int i = 0; i < this.mGroup.getChildCount(); i++) {
                this.mDarkIconDispatcher.removeDarkReceiver((DarkIconDispatcher.DarkReceiver) this.mGroup.getChildAt(i));
            }
            this.mGroup.removeAllViews();
        }

        /* access modifiers changed from: protected */
        public void onRemoveIcon(int i) {
            this.mDarkIconDispatcher.removeDarkReceiver((DarkIconDispatcher.DarkReceiver) this.mGroup.getChildAt(i));
            super.onRemoveIcon(i);
        }

        public void onSetIcon(int i, StatusBarIcon statusBarIcon) {
            super.onSetIcon(i, statusBarIcon);
            this.mDarkIconDispatcher.applyDark((DarkIconDispatcher.DarkReceiver) this.mGroup.getChildAt(i));
        }

        /* access modifiers changed from: protected */
        public DemoStatusIcons createDemoStatusIcons() {
            DemoStatusIcons createDemoStatusIcons = super.createDemoStatusIcons();
            this.mDarkIconDispatcher.addDarkReceiver((DarkIconDispatcher.DarkReceiver) createDemoStatusIcons);
            return createDemoStatusIcons;
        }

        /* access modifiers changed from: protected */
        public void exitDemoMode() {
            this.mDarkIconDispatcher.removeDarkReceiver((DarkIconDispatcher.DarkReceiver) this.mDemoStatusIcons);
            super.exitDemoMode();
        }

        public void reapplyDark() {
            for (int i = 0; i < this.mGroup.getChildCount(); i++) {
                this.mDarkIconDispatcher.applyDark((DarkIconDispatcher.DarkReceiver) this.mGroup.getChildAt(i));
            }
        }
    }

    public static class TintedIconManager extends IconManager {
        private int mColor;

        public TintedIconManager(ViewGroup viewGroup, FeatureFlags featureFlags) {
            super(viewGroup, featureFlags);
        }

        /* access modifiers changed from: protected */
        public void onIconAdded(int i, String str, boolean z, StatusBarIconHolder statusBarIconHolder) {
            StatusIconDisplayable addHolder = addHolder(i, str, z, statusBarIconHolder);
            addHolder.setStaticDrawableColor(this.mColor);
            addHolder.setDecorColor(this.mColor);
        }

        public void setTint(int i) {
            this.mColor = i;
            for (int i2 = 0; i2 < this.mGroup.getChildCount(); i2++) {
                View childAt = this.mGroup.getChildAt(i2);
                if (childAt instanceof StatusIconDisplayable) {
                    StatusIconDisplayable statusIconDisplayable = (StatusIconDisplayable) childAt;
                    statusIconDisplayable.setStaticDrawableColor(this.mColor);
                    statusIconDisplayable.setDecorColor(this.mColor);
                }
            }
        }

        /* access modifiers changed from: protected */
        public DemoStatusIcons createDemoStatusIcons() {
            DemoStatusIcons createDemoStatusIcons = super.createDemoStatusIcons();
            createDemoStatusIcons.setColor(this.mColor);
            return createDemoStatusIcons;
        }
    }

    public static class IconManager implements DemoModeCommandReceiver {
        protected ArrayList<String> mBlockList = new ArrayList<>();
        protected final Context mContext;
        protected DemoStatusIcons mDemoStatusIcons;
        protected boolean mDemoable = true;
        private final FeatureFlags mFeatureFlags;
        protected final ViewGroup mGroup;
        protected final int mIconSize;
        private boolean mIsInDemoMode;
        protected boolean mShouldLog = false;

        public IconManager(ViewGroup viewGroup, FeatureFlags featureFlags) {
            this.mFeatureFlags = featureFlags;
            this.mGroup = viewGroup;
            Context context = viewGroup.getContext();
            this.mContext = context;
            this.mIconSize = context.getResources().getDimensionPixelSize(17105536);
        }

        public boolean isDemoable() {
            return this.mDemoable;
        }

        public void setBlockList(List<String> list) {
            this.mBlockList.clear();
            if (list != null && !list.isEmpty()) {
                this.mBlockList.addAll(list);
            }
        }

        public void setShouldLog(boolean z) {
            this.mShouldLog = z;
        }

        public boolean shouldLog() {
            return this.mShouldLog;
        }

        /* access modifiers changed from: protected */
        public void onIconAdded(int i, String str, boolean z, StatusBarIconHolder statusBarIconHolder) {
            addHolder(i, str, z, statusBarIconHolder);
        }

        /* access modifiers changed from: protected */
        public StatusIconDisplayable addHolder(int i, String str, boolean z, StatusBarIconHolder statusBarIconHolder) {
            if (this.mBlockList.contains(str)) {
                z = true;
            }
            int type = statusBarIconHolder.getType();
            if (type == 0) {
                return addIcon(i, str, z, statusBarIconHolder.getIcon());
            }
            if (type == 1) {
                return addSignalIcon(i, str, statusBarIconHolder.getWifiState());
            }
            if (type != 2) {
                return null;
            }
            return addMobileIcon(i, str, statusBarIconHolder.getMobileState());
        }

        /* access modifiers changed from: protected */
        public StatusBarIconView addIcon(int i, String str, boolean z, StatusBarIcon statusBarIcon) {
            StatusBarIconView onCreateStatusBarIconView = onCreateStatusBarIconView(str, z);
            onCreateStatusBarIconView.set(statusBarIcon);
            this.mGroup.addView(onCreateStatusBarIconView, i, onCreateLayoutParams());
            return onCreateStatusBarIconView;
        }

        /* access modifiers changed from: protected */
        public StatusBarWifiView addSignalIcon(int i, String str, StatusBarSignalPolicy.WifiIconState wifiIconState) {
            StatusBarWifiView onCreateStatusBarWifiView = onCreateStatusBarWifiView(str);
            onCreateStatusBarWifiView.applyWifiState(wifiIconState);
            this.mGroup.addView(onCreateStatusBarWifiView, i, onCreateLayoutParams());
            if (this.mIsInDemoMode) {
                this.mDemoStatusIcons.addDemoWifiView(wifiIconState);
            }
            return onCreateStatusBarWifiView;
        }

        /* access modifiers changed from: protected */
        public StatusBarMobileView addMobileIcon(int i, String str, StatusBarSignalPolicy.MobileIconState mobileIconState) {
            StatusBarMobileView onCreateStatusBarMobileView = onCreateStatusBarMobileView(str, mobileIconState.mIsDual);
            onCreateStatusBarMobileView.applyMobileState(mobileIconState);
            this.mGroup.addView(onCreateStatusBarMobileView, i, onCreateLayoutParams());
            if (this.mIsInDemoMode) {
                this.mDemoStatusIcons.addMobileView(mobileIconState);
            }
            return onCreateStatusBarMobileView;
        }

        private StatusBarIconView onCreateStatusBarIconView(String str, boolean z) {
            return new StatusBarIconView(this.mContext, str, (StatusBarNotification) null, z);
        }

        private StatusBarWifiView onCreateStatusBarWifiView(String str) {
            return StatusBarWifiView.fromContext(this.mContext, str);
        }

        private StatusBarMobileView onCreateStatusBarMobileView(String str, boolean z) {
            if (!z) {
                return StatusBarMobileView.fromContext(this.mContext, str, this.mFeatureFlags.isCombinedStatusBarSignalIconsEnabled());
            }
            StatusBarMobileView fromContext = StatusBarMobileViewDual.fromContext(this.mContext);
            fromContext.setSlot(str);
            return fromContext;
        }

        /* access modifiers changed from: protected */
        public LinearLayout.LayoutParams onCreateLayoutParams() {
            return new LinearLayout.LayoutParams(-2, this.mIconSize);
        }

        /* access modifiers changed from: protected */
        public void destroy() {
            this.mGroup.removeAllViews();
        }

        public void clearAllViews() {
            this.mGroup.removeAllViews();
            if (this.mIsInDemoMode) {
                this.mDemoStatusIcons.removeAllViews();
            }
        }

        /* access modifiers changed from: protected */
        public void onIconExternal(int i, int i2) {
            ImageView imageView = (ImageView) this.mGroup.getChildAt(i);
            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            imageView.setAdjustViewBounds(true);
            setHeightAndCenter(imageView, i2);
        }

        private void setHeightAndCenter(ImageView imageView, int i) {
            ViewGroup.LayoutParams layoutParams = imageView.getLayoutParams();
            layoutParams.height = i;
            if (layoutParams instanceof LinearLayout.LayoutParams) {
                ((LinearLayout.LayoutParams) layoutParams).gravity = 16;
            }
            imageView.setLayoutParams(layoutParams);
        }

        /* access modifiers changed from: protected */
        public void onRemoveIcon(int i) {
            if (this.mIsInDemoMode) {
                this.mDemoStatusIcons.onRemoveIcon((StatusIconDisplayable) this.mGroup.getChildAt(i));
            }
            this.mGroup.removeViewAt(i);
        }

        public void onSetIcon(int i, StatusBarIcon statusBarIcon) {
            ((StatusBarIconView) this.mGroup.getChildAt(i)).set(statusBarIcon);
        }

        public void onSetIconHolder(int i, StatusBarIconHolder statusBarIconHolder) {
            int type = statusBarIconHolder.getType();
            if (type == 0) {
                onSetIcon(i, statusBarIconHolder.getIcon());
            } else if (type == 1) {
                onSetSignalIcon(i, statusBarIconHolder.getWifiState());
            } else if (type == 2) {
                onSetMobileIcon(i, statusBarIconHolder.getMobileState());
            }
        }

        public void onSetSignalIcon(int i, StatusBarSignalPolicy.WifiIconState wifiIconState) {
            StatusBarWifiView statusBarWifiView = (StatusBarWifiView) this.mGroup.getChildAt(i);
            if (statusBarWifiView != null) {
                statusBarWifiView.applyWifiState(wifiIconState);
            }
            if (this.mIsInDemoMode) {
                this.mDemoStatusIcons.updateWifiState(wifiIconState);
            }
        }

        public void onSetMobileIcon(int i, StatusBarSignalPolicy.MobileIconState mobileIconState) {
            StatusBarMobileView statusBarMobileView = (StatusBarMobileView) this.mGroup.getChildAt(i);
            if (statusBarMobileView != null) {
                statusBarMobileView.applyMobileState(mobileIconState);
            }
            if (this.mIsInDemoMode) {
                this.mDemoStatusIcons.updateMobileState(mobileIconState);
            }
        }

        public void dispatchDemoCommand(String str, Bundle bundle) {
            if (this.mDemoable) {
                this.mDemoStatusIcons.dispatchDemoCommand(str, bundle);
            }
        }

        public void onDemoModeStarted() {
            this.mIsInDemoMode = true;
            if (this.mDemoStatusIcons == null) {
                this.mDemoStatusIcons = createDemoStatusIcons();
            }
            this.mDemoStatusIcons.onDemoModeStarted();
        }

        public void onDemoModeFinished() {
            DemoStatusIcons demoStatusIcons = this.mDemoStatusIcons;
            if (demoStatusIcons != null) {
                demoStatusIcons.onDemoModeFinished();
                exitDemoMode();
                this.mIsInDemoMode = false;
            }
        }

        /* access modifiers changed from: protected */
        public void exitDemoMode() {
            this.mDemoStatusIcons.remove();
            this.mDemoStatusIcons = null;
        }

        /* access modifiers changed from: protected */
        public DemoStatusIcons createDemoStatusIcons() {
            return new DemoStatusIcons((LinearLayout) this.mGroup, this.mIconSize, this.mFeatureFlags);
        }
    }
}
