package com.android.systemui.statusbar.policy;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.database.DataSetObserver;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import com.android.keyguard.KeyguardConstants;
import com.android.keyguard.KeyguardUpdateMonitor;
import com.android.keyguard.KeyguardUpdateMonitorCallback;
import com.android.keyguard.KeyguardVisibilityHelper;
import com.android.settingslib.drawable.CircleFramedDrawable;
import com.android.systemui.R$color;
import com.android.systemui.R$dimen;
import com.android.systemui.R$drawable;
import com.android.systemui.R$id;
import com.android.systemui.R$layout;
import com.android.systemui.animation.Interpolators;
import com.android.systemui.keyguard.ScreenLifecycle;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.statusbar.SysuiStatusBarStateController;
import com.android.systemui.statusbar.notification.AnimatableProperty;
import com.android.systemui.statusbar.notification.PropertyAnimator;
import com.android.systemui.statusbar.notification.stack.AnimationProperties;
import com.android.systemui.statusbar.phone.DozeParameters;
import com.android.systemui.statusbar.phone.UnlockedScreenOffAnimationController;
import com.android.systemui.statusbar.policy.UserSwitcherController;
import com.android.systemui.util.ViewController;
import java.util.ArrayList;

public class KeyguardUserSwitcherController extends ViewController<KeyguardUserSwitcherView> {
    private static final AnimationProperties ANIMATION_PROPERTIES = new AnimationProperties().setDuration(360);
    /* access modifiers changed from: private */
    public static final boolean DEBUG = KeyguardConstants.DEBUG;
    private final KeyguardUserAdapter mAdapter;
    private final KeyguardUserSwitcherScrim mBackground;
    /* access modifiers changed from: private */
    public int mBarState;
    /* access modifiers changed from: private */
    public ObjectAnimator mBgAnimator;
    private final Context mContext;
    private int mCurrentUserId = -10000;
    private float mDarkAmount;
    public final DataSetObserver mDataSetObserver = new DataSetObserver() {
        public void onChanged() {
            KeyguardUserSwitcherController.this.refreshUserList();
        }
    };
    private final KeyguardUpdateMonitorCallback mInfoCallback = new KeyguardUpdateMonitorCallback() {
        public void onKeyguardVisibilityChanged(boolean z) {
            if (KeyguardUserSwitcherController.DEBUG) {
                Log.d("KeyguardUserSwitcherController", String.format("onKeyguardVisibilityChanged %b", new Object[]{Boolean.valueOf(z)}));
            }
            if (!z) {
                KeyguardUserSwitcherController.this.closeSwitcherIfOpenAndNotSimple(false);
            }
        }

        public void onUserSwitching(int i) {
            KeyguardUserSwitcherController.this.closeSwitcherIfOpenAndNotSimple(false);
        }
    };
    /* access modifiers changed from: private */
    public final KeyguardStateController mKeyguardStateController;
    private final KeyguardUpdateMonitor mKeyguardUpdateMonitor;
    private final KeyguardVisibilityHelper mKeyguardVisibilityHelper;
    private KeyguardUserSwitcherListView mListView;
    private final ScreenLifecycle mScreenLifecycle;
    private final ScreenLifecycle.Observer mScreenObserver = new ScreenLifecycle.Observer() {
        public void onScreenTurnedOff() {
            if (KeyguardUserSwitcherController.DEBUG) {
                Log.d("KeyguardUserSwitcherController", "onScreenTurnedOff");
            }
            KeyguardUserSwitcherController.this.closeSwitcherIfOpenAndNotSimple(false);
        }
    };
    protected final SysuiStatusBarStateController mStatusBarStateController;
    private final StatusBarStateController.StateListener mStatusBarStateListener = new StatusBarStateController.StateListener() {
        public void onStateChanged(int i) {
            if (KeyguardUserSwitcherController.DEBUG) {
                Log.d("KeyguardUserSwitcherController", String.format("onStateChanged: newState=%d", new Object[]{Integer.valueOf(i)}));
            }
            boolean goingToFullShade = KeyguardUserSwitcherController.this.mStatusBarStateController.goingToFullShade();
            boolean isKeyguardFadingAway = KeyguardUserSwitcherController.this.mKeyguardStateController.isKeyguardFadingAway();
            int access$200 = KeyguardUserSwitcherController.this.mBarState;
            int unused = KeyguardUserSwitcherController.this.mBarState = i;
            if (KeyguardUserSwitcherController.this.mStatusBarStateController.goingToFullShade() || KeyguardUserSwitcherController.this.mKeyguardStateController.isKeyguardFadingAway()) {
                KeyguardUserSwitcherController.this.closeSwitcherIfOpenAndNotSimple(true);
            }
            KeyguardUserSwitcherController.this.setKeyguardUserSwitcherVisibility(i, isKeyguardFadingAway, goingToFullShade, access$200);
        }

        public void onDozeAmountChanged(float f, float f2) {
            if (KeyguardUserSwitcherController.DEBUG) {
                Log.d("KeyguardUserSwitcherController", String.format("onDozeAmountChanged: linearAmount=%f amount=%f", new Object[]{Float.valueOf(f), Float.valueOf(f2)}));
            }
            KeyguardUserSwitcherController.this.setDarkAmount(f2);
        }
    };
    private final UserSwitcherController mUserSwitcherController;
    private boolean mUserSwitcherOpen;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public KeyguardUserSwitcherController(KeyguardUserSwitcherView keyguardUserSwitcherView, Context context, Resources resources, LayoutInflater layoutInflater, ScreenLifecycle screenLifecycle, UserSwitcherController userSwitcherController, KeyguardStateController keyguardStateController, SysuiStatusBarStateController sysuiStatusBarStateController, KeyguardUpdateMonitor keyguardUpdateMonitor, DozeParameters dozeParameters, UnlockedScreenOffAnimationController unlockedScreenOffAnimationController) {
        super(keyguardUserSwitcherView);
        Context context2 = context;
        if (DEBUG) {
            Log.d("KeyguardUserSwitcherController", "New KeyguardUserSwitcherController");
        }
        this.mContext = context2;
        this.mScreenLifecycle = screenLifecycle;
        UserSwitcherController userSwitcherController2 = userSwitcherController;
        this.mUserSwitcherController = userSwitcherController2;
        this.mKeyguardStateController = keyguardStateController;
        this.mStatusBarStateController = sysuiStatusBarStateController;
        this.mKeyguardUpdateMonitor = keyguardUpdateMonitor;
        this.mAdapter = new KeyguardUserAdapter(context, resources, layoutInflater, userSwitcherController2, this);
        this.mKeyguardVisibilityHelper = new KeyguardVisibilityHelper(this.mView, keyguardStateController, dozeParameters, unlockedScreenOffAnimationController, false);
        this.mBackground = new KeyguardUserSwitcherScrim(context);
    }

    /* access modifiers changed from: protected */
    public void onInit() {
        super.onInit();
        if (DEBUG) {
            Log.d("KeyguardUserSwitcherController", "onInit");
        }
        this.mListView = (KeyguardUserSwitcherListView) ((KeyguardUserSwitcherView) this.mView).findViewById(R$id.keyguard_user_switcher_list);
        ((KeyguardUserSwitcherView) this.mView).setOnTouchListener(new KeyguardUserSwitcherController$$ExternalSyntheticLambda0(this));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ boolean lambda$onInit$0(View view, MotionEvent motionEvent) {
        if (!isListAnimating()) {
            return closeSwitcherIfOpenAndNotSimple(true);
        }
        return false;
    }

    /* access modifiers changed from: protected */
    public void onViewAttached() {
        if (DEBUG) {
            Log.d("KeyguardUserSwitcherController", "onViewAttached");
        }
        this.mAdapter.registerDataSetObserver(this.mDataSetObserver);
        this.mAdapter.notifyDataSetChanged();
        this.mKeyguardUpdateMonitor.registerCallback(this.mInfoCallback);
        this.mStatusBarStateController.addCallback(this.mStatusBarStateListener);
        this.mScreenLifecycle.addObserver(this.mScreenObserver);
        if (isSimpleUserSwitcher()) {
            setUserSwitcherOpened(true, true);
            return;
        }
        ((KeyguardUserSwitcherView) this.mView).addOnLayoutChangeListener(this.mBackground);
        ((KeyguardUserSwitcherView) this.mView).setBackground(this.mBackground);
        this.mBackground.setAlpha(0);
    }

    /* access modifiers changed from: protected */
    public void onViewDetached() {
        if (DEBUG) {
            Log.d("KeyguardUserSwitcherController", "onViewDetached");
        }
        closeSwitcherIfOpenAndNotSimple(false);
        this.mAdapter.unregisterDataSetObserver(this.mDataSetObserver);
        this.mKeyguardUpdateMonitor.removeCallback(this.mInfoCallback);
        this.mStatusBarStateController.removeCallback(this.mStatusBarStateListener);
        this.mScreenLifecycle.removeObserver(this.mScreenObserver);
        ((KeyguardUserSwitcherView) this.mView).removeOnLayoutChangeListener(this.mBackground);
        ((KeyguardUserSwitcherView) this.mView).setBackground((Drawable) null);
        this.mBackground.setAlpha(0);
    }

    public boolean isSimpleUserSwitcher() {
        return this.mUserSwitcherController.isSimpleUserSwitcher();
    }

    public boolean closeSwitcherIfOpenAndNotSimple(boolean z) {
        if (!isUserSwitcherOpen() || isSimpleUserSwitcher()) {
            return false;
        }
        setUserSwitcherOpened(false, z);
        return true;
    }

    /* access modifiers changed from: package-private */
    public void refreshUserList() {
        int childCount = this.mListView.getChildCount();
        int count = this.mAdapter.getCount();
        int max = Math.max(childCount, count);
        if (DEBUG) {
            Log.d("KeyguardUserSwitcherController", String.format("refreshUserList childCount=%d adapterCount=%d", new Object[]{Integer.valueOf(childCount), Integer.valueOf(count)}));
        }
        boolean z = false;
        for (int i = 0; i < max; i++) {
            if (i < count) {
                View view = null;
                if (i < childCount) {
                    view = this.mListView.getChildAt(i);
                }
                KeyguardUserDetailItemView keyguardUserDetailItemView = (KeyguardUserDetailItemView) this.mAdapter.getView(i, view, this.mListView);
                UserSwitcherController.UserRecord userRecord = (UserSwitcherController.UserRecord) keyguardUserDetailItemView.getTag();
                if (userRecord.isCurrent) {
                    if (i != 0) {
                        Log.w("KeyguardUserSwitcherController", "Current user is not the first view in the list");
                    }
                    this.mCurrentUserId = userRecord.info.id;
                    keyguardUserDetailItemView.updateVisibilities(true, this.mUserSwitcherOpen, false);
                    z = true;
                } else {
                    keyguardUserDetailItemView.updateVisibilities(this.mUserSwitcherOpen, true, false);
                }
                keyguardUserDetailItemView.setDarkAmount(this.mDarkAmount);
                if (view == null) {
                    this.mListView.addView(keyguardUserDetailItemView);
                } else if (view != keyguardUserDetailItemView) {
                    this.mListView.replaceView(keyguardUserDetailItemView, i);
                }
            } else {
                this.mListView.removeLastView();
            }
        }
        if (!z) {
            Log.w("KeyguardUserSwitcherController", "Current user is not listed");
            this.mCurrentUserId = -10000;
        }
    }

    public void setKeyguardUserSwitcherVisibility(int i, boolean z, boolean z2, int i2) {
        this.mKeyguardVisibilityHelper.setViewVisibility(i, z, z2, i2);
    }

    public void updatePosition(int i, int i2, boolean z) {
        AnimationProperties animationProperties = ANIMATION_PROPERTIES;
        PropertyAnimator.setProperty(this.mListView, AnimatableProperty.f131Y, (float) i2, animationProperties, z);
        PropertyAnimator.setProperty(this.mListView, AnimatableProperty.TRANSLATION_X, (float) (-Math.abs(i)), animationProperties, z);
        Rect rect = new Rect();
        this.mListView.getDrawingRect(rect);
        ((KeyguardUserSwitcherView) this.mView).offsetDescendantRectToMyCoords(this.mListView, rect);
        this.mBackground.setGradientCenter((int) (this.mListView.getTranslationX() + ((float) rect.left) + ((float) (rect.width() / 2))), (int) (this.mListView.getTranslationY() + ((float) rect.top) + ((float) (rect.height() / 2))));
    }

    public void setAlpha(float f) {
        if (!this.mKeyguardVisibilityHelper.isVisibilityAnimating()) {
            ((KeyguardUserSwitcherView) this.mView).setAlpha(f);
        }
    }

    /* access modifiers changed from: private */
    public void setDarkAmount(float f) {
        boolean z = f == 1.0f;
        if (f != this.mDarkAmount) {
            this.mDarkAmount = f;
            this.mListView.setDarkAmount(f);
            if (z) {
                closeSwitcherIfOpenAndNotSimple(false);
            }
        }
    }

    /* access modifiers changed from: private */
    public boolean isListAnimating() {
        return this.mKeyguardVisibilityHelper.isVisibilityAnimating() || this.mListView.isAnimating();
    }

    /* access modifiers changed from: private */
    public void setUserSwitcherOpened(boolean z, boolean z2) {
        if (DEBUG) {
            Log.d("KeyguardUserSwitcherController", String.format("setUserSwitcherOpened: %b -> %b (animate=%b)", new Object[]{Boolean.valueOf(this.mUserSwitcherOpen), Boolean.valueOf(z), Boolean.valueOf(z2)}));
        }
        this.mUserSwitcherOpen = z;
        updateVisibilities(z2);
    }

    private void updateVisibilities(boolean z) {
        if (DEBUG) {
            Log.d("KeyguardUserSwitcherController", String.format("updateVisibilities: animate=%b", new Object[]{Boolean.valueOf(z)}));
        }
        ObjectAnimator objectAnimator = this.mBgAnimator;
        if (objectAnimator != null) {
            objectAnimator.cancel();
        }
        if (this.mUserSwitcherOpen) {
            ObjectAnimator ofInt = ObjectAnimator.ofInt(this.mBackground, "alpha", new int[]{0, 255});
            this.mBgAnimator = ofInt;
            ofInt.setDuration(400);
            this.mBgAnimator.setInterpolator(Interpolators.ALPHA_IN);
            this.mBgAnimator.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    ObjectAnimator unused = KeyguardUserSwitcherController.this.mBgAnimator = null;
                }
            });
            this.mBgAnimator.start();
        } else {
            ObjectAnimator ofInt2 = ObjectAnimator.ofInt(this.mBackground, "alpha", new int[]{255, 0});
            this.mBgAnimator = ofInt2;
            ofInt2.setDuration(400);
            this.mBgAnimator.setInterpolator(Interpolators.ALPHA_OUT);
            this.mBgAnimator.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    ObjectAnimator unused = KeyguardUserSwitcherController.this.mBgAnimator = null;
                }
            });
            this.mBgAnimator.start();
        }
        this.mListView.updateVisibilities(this.mUserSwitcherOpen, z);
    }

    /* access modifiers changed from: private */
    public boolean isUserSwitcherOpen() {
        return this.mUserSwitcherOpen;
    }

    static class KeyguardUserAdapter extends UserSwitcherController.BaseUserAdapter implements View.OnClickListener {
        private final Context mContext;
        private View mCurrentUserView;
        private KeyguardUserSwitcherController mKeyguardUserSwitcherController;
        private final LayoutInflater mLayoutInflater;
        private final Resources mResources;
        private ArrayList<UserSwitcherController.UserRecord> mUsersOrdered = new ArrayList<>();

        KeyguardUserAdapter(Context context, Resources resources, LayoutInflater layoutInflater, UserSwitcherController userSwitcherController, KeyguardUserSwitcherController keyguardUserSwitcherController) {
            super(userSwitcherController);
            this.mContext = context;
            this.mResources = resources;
            this.mLayoutInflater = layoutInflater;
            this.mKeyguardUserSwitcherController = keyguardUserSwitcherController;
        }

        public void notifyDataSetChanged() {
            refreshUserOrder();
            super.notifyDataSetChanged();
        }

        /* access modifiers changed from: package-private */
        public void refreshUserOrder() {
            ArrayList<UserSwitcherController.UserRecord> users = super.getUsers();
            this.mUsersOrdered = new ArrayList<>(users.size());
            for (int i = 0; i < users.size(); i++) {
                UserSwitcherController.UserRecord userRecord = users.get(i);
                if (userRecord.isCurrent) {
                    this.mUsersOrdered.add(0, userRecord);
                } else {
                    this.mUsersOrdered.add(userRecord);
                }
            }
        }

        /* access modifiers changed from: protected */
        public ArrayList<UserSwitcherController.UserRecord> getUsers() {
            return this.mUsersOrdered;
        }

        public View getView(int i, View view, ViewGroup viewGroup) {
            return createUserDetailItemView(view, viewGroup, getItem(i));
        }

        /* access modifiers changed from: package-private */
        public KeyguardUserDetailItemView convertOrInflate(View view, ViewGroup viewGroup) {
            if (!(view instanceof KeyguardUserDetailItemView) || !(view.getTag() instanceof UserSwitcherController.UserRecord)) {
                view = this.mLayoutInflater.inflate(R$layout.keyguard_user_switcher_item, viewGroup, false);
            }
            return (KeyguardUserDetailItemView) view;
        }

        /* access modifiers changed from: package-private */
        public KeyguardUserDetailItemView createUserDetailItemView(View view, ViewGroup viewGroup, UserSwitcherController.UserRecord userRecord) {
            KeyguardUserDetailItemView convertOrInflate = convertOrInflate(view, viewGroup);
            convertOrInflate.setOnClickListener(this);
            String name = getName(this.mContext, userRecord);
            if (userRecord.picture == null) {
                convertOrInflate.bind(name, getDrawable(userRecord).mutate(), userRecord.resolveId());
            } else {
                CircleFramedDrawable circleFramedDrawable = new CircleFramedDrawable(userRecord.picture, (int) this.mResources.getDimension(R$dimen.kg_framed_avatar_size));
                circleFramedDrawable.setColorFilter(userRecord.isSwitchToEnabled ? null : UserSwitcherController.BaseUserAdapter.getDisabledUserAvatarColorFilter());
                convertOrInflate.bind(name, circleFramedDrawable, userRecord.info.id);
            }
            convertOrInflate.setActivated(userRecord.isCurrent);
            convertOrInflate.setDisabledByAdmin(userRecord.isDisabledByAdmin);
            convertOrInflate.setEnabled(userRecord.isSwitchToEnabled);
            convertOrInflate.setAlpha(convertOrInflate.isEnabled() ? 1.0f : 0.38f);
            if (userRecord.isCurrent) {
                this.mCurrentUserView = convertOrInflate;
            }
            convertOrInflate.setTag(userRecord);
            return convertOrInflate;
        }

        private Drawable getDrawable(UserSwitcherController.UserRecord userRecord) {
            Drawable drawable;
            int i;
            if (!userRecord.isCurrent || !userRecord.isGuest) {
                drawable = UserSwitcherController.BaseUserAdapter.getIconDrawable(this.mContext, userRecord);
            } else {
                drawable = this.mContext.getDrawable(R$drawable.ic_avatar_guest_user);
            }
            if (userRecord.isSwitchToEnabled) {
                i = R$color.kg_user_switcher_avatar_icon_color;
            } else {
                i = R$color.kg_user_switcher_restricted_avatar_icon_color;
            }
            drawable.setTint(this.mResources.getColor(i, this.mContext.getTheme()));
            return new LayerDrawable(new Drawable[]{this.mContext.getDrawable(R$drawable.kg_bg_avatar), drawable});
        }

        public void onClick(View view) {
            UserSwitcherController.UserRecord userRecord = (UserSwitcherController.UserRecord) view.getTag();
            if (!this.mKeyguardUserSwitcherController.isListAnimating()) {
                if (!this.mKeyguardUserSwitcherController.isUserSwitcherOpen()) {
                    this.mKeyguardUserSwitcherController.setUserSwitcherOpened(true, true);
                } else if (!userRecord.isCurrent || userRecord.isGuest) {
                    onUserListItemClicked(userRecord);
                } else {
                    this.mKeyguardUserSwitcherController.closeSwitcherIfOpenAndNotSimple(true);
                }
            }
        }
    }
}
