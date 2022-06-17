package com.android.systemui.statusbar.policy;

import android.content.Context;
import android.content.pm.UserInfo;
import android.content.res.Resources;
import android.database.DataSetObserver;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityNodeInfo;
import com.android.keyguard.KeyguardConstants;
import com.android.keyguard.KeyguardVisibilityHelper;
import com.android.settingslib.drawable.CircleFramedDrawable;
import com.android.systemui.R$color;
import com.android.systemui.R$dimen;
import com.android.systemui.R$drawable;
import com.android.systemui.R$string;
import com.android.systemui.keyguard.ScreenLifecycle;
import com.android.systemui.p006qs.tiles.UserDetailView;
import com.android.systemui.plugins.FalsingManager;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.statusbar.SysuiStatusBarStateController;
import com.android.systemui.statusbar.notification.AnimatableProperty;
import com.android.systemui.statusbar.notification.PropertyAnimator;
import com.android.systemui.statusbar.notification.stack.AnimationProperties;
import com.android.systemui.statusbar.phone.DozeParameters;
import com.android.systemui.statusbar.phone.NotificationPanelViewController;
import com.android.systemui.statusbar.phone.UnlockedScreenOffAnimationController;
import com.android.systemui.statusbar.phone.UserAvatarView;
import com.android.systemui.statusbar.policy.ConfigurationController;
import com.android.systemui.statusbar.policy.UserSwitcherController;
import com.android.systemui.util.ViewController;
import javax.inject.Provider;

public class KeyguardQsUserSwitchController extends ViewController<UserAvatarView> {
    private static final AnimationProperties ANIMATION_PROPERTIES = new AnimationProperties().setDuration(360);
    /* access modifiers changed from: private */
    public static final boolean DEBUG = KeyguardConstants.DEBUG;
    private UserSwitcherController.BaseUserAdapter mAdapter;
    /* access modifiers changed from: private */
    public int mBarState;
    private final ConfigurationController mConfigurationController;
    private ConfigurationController.ConfigurationListener mConfigurationListener = new ConfigurationController.ConfigurationListener() {
        public void onUiModeChanged() {
            KeyguardQsUserSwitchController.this.updateView(true);
        }
    };
    /* access modifiers changed from: private */
    public final Context mContext;
    UserSwitcherController.UserRecord mCurrentUser;
    public final DataSetObserver mDataSetObserver = new DataSetObserver() {
        public void onChanged() {
            KeyguardQsUserSwitchController.this.updateView(false);
        }
    };
    private final FalsingManager mFalsingManager;
    /* access modifiers changed from: private */
    public final KeyguardStateController mKeyguardStateController;
    private final KeyguardVisibilityHelper mKeyguardVisibilityHelper;
    /* access modifiers changed from: private */
    public NotificationPanelViewController mNotificationPanelViewController;
    private Resources mResources;
    private final ScreenLifecycle mScreenLifecycle;
    protected final SysuiStatusBarStateController mStatusBarStateController;
    private final StatusBarStateController.StateListener mStatusBarStateListener = new StatusBarStateController.StateListener() {
        public void onStateChanged(int i) {
            if (KeyguardQsUserSwitchController.DEBUG) {
                Log.d("KeyguardQsUserSwitchController", String.format("onStateChanged: newState=%d", new Object[]{Integer.valueOf(i)}));
            }
            boolean goingToFullShade = KeyguardQsUserSwitchController.this.mStatusBarStateController.goingToFullShade();
            boolean isKeyguardFadingAway = KeyguardQsUserSwitchController.this.mKeyguardStateController.isKeyguardFadingAway();
            int access$200 = KeyguardQsUserSwitchController.this.mBarState;
            int unused = KeyguardQsUserSwitchController.this.mBarState = i;
            KeyguardQsUserSwitchController.this.setKeyguardQsUserSwitchVisibility(i, isKeyguardFadingAway, goingToFullShade, access$200);
        }
    };
    private final KeyguardUserDetailAdapter mUserDetailAdapter;
    private final UserSwitcherController mUserSwitcherController;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public KeyguardQsUserSwitchController(UserAvatarView userAvatarView, Context context, Resources resources, ScreenLifecycle screenLifecycle, UserSwitcherController userSwitcherController, KeyguardStateController keyguardStateController, FalsingManager falsingManager, ConfigurationController configurationController, SysuiStatusBarStateController sysuiStatusBarStateController, DozeParameters dozeParameters, Provider<UserDetailView.Adapter> provider, UnlockedScreenOffAnimationController unlockedScreenOffAnimationController) {
        super(userAvatarView);
        Context context2 = context;
        if (DEBUG) {
            Log.d("KeyguardQsUserSwitchController", "New KeyguardQsUserSwitchController");
        }
        this.mContext = context2;
        this.mResources = resources;
        this.mScreenLifecycle = screenLifecycle;
        this.mUserSwitcherController = userSwitcherController;
        KeyguardStateController keyguardStateController2 = keyguardStateController;
        this.mKeyguardStateController = keyguardStateController2;
        this.mFalsingManager = falsingManager;
        this.mConfigurationController = configurationController;
        this.mStatusBarStateController = sysuiStatusBarStateController;
        this.mKeyguardVisibilityHelper = new KeyguardVisibilityHelper(this.mView, keyguardStateController2, dozeParameters, unlockedScreenOffAnimationController, false);
        this.mUserDetailAdapter = new KeyguardUserDetailAdapter(context, provider);
    }

    /* access modifiers changed from: protected */
    public void onInit() {
        super.onInit();
        if (DEBUG) {
            Log.d("KeyguardQsUserSwitchController", "onInit");
        }
        this.mAdapter = new UserSwitcherController.BaseUserAdapter(this.mUserSwitcherController) {
            public View getView(int i, View view, ViewGroup viewGroup) {
                return null;
            }
        };
        ((UserAvatarView) this.mView).setOnClickListener(new KeyguardQsUserSwitchController$$ExternalSyntheticLambda0(this));
        ((UserAvatarView) this.mView).setAccessibilityDelegate(new View.AccessibilityDelegate() {
            public void onInitializeAccessibilityNodeInfo(View view, AccessibilityNodeInfo accessibilityNodeInfo) {
                super.onInitializeAccessibilityNodeInfo(view, accessibilityNodeInfo);
                accessibilityNodeInfo.addAction(new AccessibilityNodeInfo.AccessibilityAction(16, KeyguardQsUserSwitchController.this.mContext.getString(R$string.accessibility_quick_settings_choose_user_action)));
            }
        });
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onInit$0(View view) {
        if (!this.mFalsingManager.isFalseTap(1) && !isListAnimating()) {
            openQsUserPanel();
        }
    }

    /* access modifiers changed from: protected */
    public void onViewAttached() {
        if (DEBUG) {
            Log.d("KeyguardQsUserSwitchController", "onViewAttached");
        }
        this.mAdapter.registerDataSetObserver(this.mDataSetObserver);
        this.mDataSetObserver.onChanged();
        this.mStatusBarStateController.addCallback(this.mStatusBarStateListener);
        this.mConfigurationController.addCallback(this.mConfigurationListener);
        updateView(true);
    }

    /* access modifiers changed from: protected */
    public void onViewDetached() {
        if (DEBUG) {
            Log.d("KeyguardQsUserSwitchController", "onViewDetached");
        }
        this.mAdapter.unregisterDataSetObserver(this.mDataSetObserver);
        this.mStatusBarStateController.removeCallback(this.mStatusBarStateListener);
        this.mConfigurationController.removeCallback(this.mConfigurationListener);
    }

    private boolean updateCurrentUser() {
        UserSwitcherController.UserRecord userRecord = this.mCurrentUser;
        this.mCurrentUser = null;
        for (int i = 0; i < this.mAdapter.getCount(); i++) {
            UserSwitcherController.UserRecord item = this.mAdapter.getItem(i);
            if (item.isCurrent) {
                this.mCurrentUser = item;
                return !item.equals(userRecord);
            }
        }
        if (this.mCurrentUser != null || userRecord == null) {
            return false;
        }
        return true;
    }

    /* access modifiers changed from: private */
    public void updateView(boolean z) {
        String str;
        UserInfo userInfo;
        if (updateCurrentUser() || z) {
            UserSwitcherController.UserRecord userRecord = this.mCurrentUser;
            if (userRecord == null || (userInfo = userRecord.info) == null || TextUtils.isEmpty(userInfo.name)) {
                str = this.mContext.getString(R$string.accessibility_multi_user_switch_switcher);
            } else {
                str = this.mContext.getString(R$string.accessibility_quick_settings_user, new Object[]{this.mCurrentUser.info.name});
            }
            if (!TextUtils.equals(((UserAvatarView) this.mView).getContentDescription(), str)) {
                ((UserAvatarView) this.mView).setContentDescription(str);
            }
            UserSwitcherController.UserRecord userRecord2 = this.mCurrentUser;
            ((UserAvatarView) this.mView).setDrawableWithBadge(getCurrentUserIcon().mutate(), userRecord2 != null ? userRecord2.resolveId() : -10000);
        }
    }

    /* access modifiers changed from: package-private */
    public Drawable getCurrentUserIcon() {
        Drawable drawable;
        Drawable drawable2;
        UserSwitcherController.UserRecord userRecord = this.mCurrentUser;
        if (userRecord == null || userRecord.picture == null) {
            if (userRecord == null || !userRecord.isGuest) {
                drawable2 = this.mContext.getDrawable(R$drawable.ic_avatar_user);
            } else {
                drawable2 = this.mContext.getDrawable(R$drawable.ic_avatar_guest_user);
            }
            drawable = drawable2;
            drawable.setTint(this.mResources.getColor(R$color.kg_user_switcher_avatar_icon_color, this.mContext.getTheme()));
        } else {
            drawable = new CircleFramedDrawable(this.mCurrentUser.picture, (int) this.mResources.getDimension(R$dimen.kg_framed_avatar_size));
        }
        return new LayerDrawable(new Drawable[]{this.mContext.getDrawable(R$drawable.kg_bg_avatar), drawable});
    }

    public int getUserIconHeight() {
        return ((UserAvatarView) this.mView).getHeight();
    }

    public void setKeyguardQsUserSwitchVisibility(int i, boolean z, boolean z2, int i2) {
        this.mKeyguardVisibilityHelper.setViewVisibility(i, z, z2, i2);
    }

    public void updatePosition(int i, int i2, boolean z) {
        AnimationProperties animationProperties = ANIMATION_PROPERTIES;
        PropertyAnimator.setProperty((UserAvatarView) this.mView, AnimatableProperty.f131Y, (float) i2, animationProperties, z);
        PropertyAnimator.setProperty((UserAvatarView) this.mView, AnimatableProperty.TRANSLATION_X, (float) (-Math.abs(i)), animationProperties, z);
    }

    public void setAlpha(float f) {
        if (!this.mKeyguardVisibilityHelper.isVisibilityAnimating()) {
            ((UserAvatarView) this.mView).setAlpha(f);
        }
    }

    private boolean isListAnimating() {
        return this.mKeyguardVisibilityHelper.isVisibilityAnimating();
    }

    private void openQsUserPanel() {
        this.mNotificationPanelViewController.expandWithQsDetail(this.mUserDetailAdapter);
    }

    public void setNotificationPanelViewController(NotificationPanelViewController notificationPanelViewController) {
        this.mNotificationPanelViewController = notificationPanelViewController;
    }

    class KeyguardUserDetailAdapter extends UserSwitcherController.UserDetailAdapter {
        public boolean shouldAnimate() {
            return false;
        }

        KeyguardUserDetailAdapter(Context context, Provider<UserDetailView.Adapter> provider) {
            super(context, provider);
        }

        public int getDoneText() {
            return R$string.quick_settings_close_user_panel;
        }

        public boolean onDoneButtonClicked() {
            if (KeyguardQsUserSwitchController.this.mNotificationPanelViewController == null) {
                return false;
            }
            KeyguardQsUserSwitchController.this.mNotificationPanelViewController.animateCloseQs(true);
            return true;
        }
    }
}
