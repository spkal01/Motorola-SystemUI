package com.motorola.systemui.cli.navgesture.animation;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.Region;
import android.os.Process;
import android.os.UserManager;
import android.text.TextUtils;
import android.view.MotionEvent;
import com.android.systemui.R$array;
import com.android.systemui.shared.system.ActivityManagerWrapper;
import com.android.systemui.shared.system.SystemGestureExclusionListenerCompat;
import com.android.systemui.shared.system.TaskStackChangeListener;
import com.motorola.systemui.cli.navgesture.SysUINavigationMode;
import com.motorola.systemui.cli.navgesture.display.DisplayInfoChangeListener;
import com.motorola.systemui.cli.navgesture.display.SecondaryDisplay;
import com.motorola.systemui.cli.navgesture.util.NavBarPosition;
import com.motorola.systemui.cli.navgesture.util.Utilities;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public class RecentsAnimationDeviceState implements SysUINavigationMode.NavigationModeChangeListener, DisplayInfoChangeListener {
    private final Context mContext;
    private final Region mDeferredGestureRegion;
    private final int mDisplayId;
    private final SystemGestureExclusionListenerCompat mExclusionListener;
    /* access modifiers changed from: private */
    public Region mExclusionRegion;
    private int mFlagsToClear;
    private int mFlagsToSet;
    private final TaskStackChangeListener mFrozenTaskListener;
    private final List<ComponentName> mGestureBlockedActivities;
    /* access modifiers changed from: private */
    public boolean mIsUserUnlocked;
    private int mMode;
    private NavBarPosition mNavBarPosition;
    private final ArrayList<Runnable> mOnDestroyActions = new ArrayList<>();
    private Runnable mOnDestroyFrozenTaskRunnable;
    private final SecondaryDisplay mSecondaryDisplay;
    private final RectF mSwipeTouchRegion;
    private final SysUINavigationMode mSysUiNavMode;
    private int mSystemUiStateFlags;
    /* access modifiers changed from: private */
    public boolean mTaskListFrozen;
    private final ArrayList<Runnable> mUserUnlockedActions;
    private final BroadcastReceiver mUserUnlockedReceiver;

    public RecentsAnimationDeviceState(Context context) {
        String[] strArr;
        this.mMode = 0;
        this.mDeferredGestureRegion = new Region();
        this.mUserUnlockedActions = new ArrayList<>();
        C27061 r1 = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                if ("android.intent.action.USER_UNLOCKED".equals(intent.getAction())) {
                    boolean unused = RecentsAnimationDeviceState.this.mIsUserUnlocked = true;
                    RecentsAnimationDeviceState.this.notifyUserUnlocked();
                }
            }
        };
        this.mUserUnlockedReceiver = r1;
        this.mFrozenTaskListener = new TaskStackChangeListener() {
            public void onRecentTaskListFrozenChanged(boolean z) {
                boolean unused = RecentsAnimationDeviceState.this.mTaskListFrozen = z;
            }
        };
        this.mSwipeTouchRegion = new RectF();
        this.mFlagsToSet = 0;
        this.mFlagsToClear = 0;
        this.mContext = context;
        SysUINavigationMode instance = SysUINavigationMode.getInstance(context);
        this.mSysUiNavMode = instance;
        SecondaryDisplay secondaryDisplay = SecondaryDisplay.INSTANCE.lambda$get$0(context);
        this.mSecondaryDisplay = secondaryDisplay;
        int displayId = secondaryDisplay.getDisplayId();
        this.mDisplayId = displayId;
        secondaryDisplay.addChangeListener(this);
        runOnDestroy(new RecentsAnimationDeviceState$$ExternalSyntheticLambda4(this));
        boolean isUserUnlocked = ((UserManager) context.getSystemService(UserManager.class)).isUserUnlocked(Process.myUserHandle());
        this.mIsUserUnlocked = isUserUnlocked;
        if (!isUserUnlocked) {
            context.registerReceiver(r1, new IntentFilter("android.intent.action.USER_UNLOCKED"));
        }
        runOnDestroy(new RecentsAnimationDeviceState$$ExternalSyntheticLambda3(this));
        C27083 r12 = new SystemGestureExclusionListenerCompat(displayId) {
            public void onExclusionChanged(Region region) {
                Region unused = RecentsAnimationDeviceState.this.mExclusionRegion = region;
            }
        };
        this.mExclusionListener = r12;
        Objects.requireNonNull(r12);
        runOnDestroy(new RecentsAnimationDeviceState$$ExternalSyntheticLambda0(r12));
        onNavigationModeChanged(instance.addModeChangeListener(this));
        runOnDestroy(new RecentsAnimationDeviceState$$ExternalSyntheticLambda1(this));
        try {
            strArr = context.getResources().getStringArray(R$array.gesture_blocking_components);
        } catch (Resources.NotFoundException unused) {
            strArr = new String[0];
        }
        this.mGestureBlockedActivities = new ArrayList(strArr.length);
        for (String str : strArr) {
            if (!TextUtils.isEmpty(str)) {
                this.mGestureBlockedActivities.add(ComponentName.unflattenFromString(str));
            }
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0() {
        this.mSecondaryDisplay.removeChangeListener(this);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$1() {
        Utilities.unregisterReceiverSafely(this.mContext, this.mUserUnlockedReceiver);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$2() {
        this.mSysUiNavMode.removeModeChangeListener(this);
    }

    private void setupOrientationSwipeHandler() {
        ActivityManagerWrapper.getInstance().registerTaskStackListener(this.mFrozenTaskListener);
        RecentsAnimationDeviceState$$ExternalSyntheticLambda2 recentsAnimationDeviceState$$ExternalSyntheticLambda2 = new RecentsAnimationDeviceState$$ExternalSyntheticLambda2(this);
        this.mOnDestroyFrozenTaskRunnable = recentsAnimationDeviceState$$ExternalSyntheticLambda2;
        runOnDestroy(recentsAnimationDeviceState$$ExternalSyntheticLambda2);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$setupOrientationSwipeHandler$3() {
        ActivityManagerWrapper.getInstance().unregisterTaskStackListener(this.mFrozenTaskListener);
    }

    private void destroyOrientationSwipeHandlerCallback() {
        ActivityManagerWrapper.getInstance().unregisterTaskStackListener(this.mFrozenTaskListener);
        this.mOnDestroyActions.remove(this.mOnDestroyFrozenTaskRunnable);
    }

    private void runOnDestroy(Runnable runnable) {
        this.mOnDestroyActions.add(runnable);
    }

    public void addNavigationModeChangedCallback(SysUINavigationMode.NavigationModeChangeListener navigationModeChangeListener) {
        int addModeChangeListener = this.mSysUiNavMode.addModeChangeListener(navigationModeChangeListener);
        this.mMode = addModeChangeListener;
        navigationModeChangeListener.onNavigationModeChanged(addModeChangeListener);
        runOnDestroy(new RecentsAnimationDeviceState$$ExternalSyntheticLambda5(this, navigationModeChangeListener));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$addNavigationModeChangedCallback$4(SysUINavigationMode.NavigationModeChangeListener navigationModeChangeListener) {
        this.mSysUiNavMode.removeModeChangeListener(navigationModeChangeListener);
    }

    public void onNavigationModeChanged(int i) {
        onDisplayInfoChanged(this.mSecondaryDisplay);
        if (i == 2) {
            this.mExclusionListener.register();
        } else {
            this.mExclusionListener.unregister();
        }
        this.mNavBarPosition = new NavBarPosition(i, this.mSecondaryDisplay.getRotation());
        int i2 = this.mMode;
        if (i2 != 2 && i == 2) {
            setupOrientationSwipeHandler();
        } else if (i2 == 2 && i != 2) {
            destroyOrientationSwipeHandlerCallback();
        }
        this.mMode = i;
    }

    public void onDisplayInfoChanged(SecondaryDisplay secondaryDisplay) {
        int i;
        if (secondaryDisplay.getDisplayId() == this.mDisplayId && (i = this.mMode) == 2) {
            this.mNavBarPosition = new NavBarPosition(i, this.mSecondaryDisplay.getRotation());
            updateGestureTouchRegions();
        }
    }

    public NavBarPosition getNavBarPosition() {
        return this.mNavBarPosition;
    }

    public boolean isFullyGesturalNavMode() {
        return this.mMode == 2;
    }

    public boolean isButtonNavMode() {
        return this.mMode == 0;
    }

    public int getDisplayId() {
        return this.mDisplayId;
    }

    public void runOnUserUnlocked(Runnable runnable) {
        if (this.mIsUserUnlocked) {
            runnable.run();
        } else {
            this.mUserUnlockedActions.add(runnable);
        }
    }

    public boolean isUserUnlocked() {
        return this.mIsUserUnlocked;
    }

    /* access modifiers changed from: private */
    public void notifyUserUnlocked() {
        Iterator<Runnable> it = this.mUserUnlockedActions.iterator();
        while (it.hasNext()) {
            it.next().run();
        }
        this.mUserUnlockedActions.clear();
        Utilities.unregisterReceiverSafely(this.mContext, this.mUserUnlockedReceiver);
    }

    public boolean isGestureBlockedActivity(ActivityManager.RunningTaskInfo runningTaskInfo) {
        return runningTaskInfo != null && this.mGestureBlockedActivities.contains(runningTaskInfo.topActivity);
    }

    public void setFlags(int i, boolean z) {
        if (z) {
            this.mFlagsToSet = i | this.mFlagsToSet;
        } else {
            this.mFlagsToClear = i | this.mFlagsToClear;
        }
        updateFlags();
        this.mFlagsToSet = 0;
        this.mFlagsToClear = 0;
    }

    private void updateFlags() {
        this.mSystemUiStateFlags = (this.mSystemUiStateFlags | this.mFlagsToSet) & (~this.mFlagsToClear);
    }

    public int getSystemUiStateFlags() {
        return this.mSystemUiStateFlags;
    }

    public boolean canStartSystemGesture() {
        int i = this.mSystemUiStateFlags;
        if (((i & 2) == 0 || this.mTaskListFrozen) && (i & 4) == 0 && (i & 2048) == 0) {
            return (i & 256) == 0 || (i & 128) == 0;
        }
        return false;
    }

    public boolean isKeyguardShowingOccluded() {
        return (this.mSystemUiStateFlags & 512) != 0;
    }

    public boolean isKeyguardShowing() {
        return (this.mSystemUiStateFlags & 64) != 0;
    }

    public boolean isScreenPinningActive() {
        return (this.mSystemUiStateFlags & 1) != 0;
    }

    public void updateGestureTouchRegions() {
        if (this.mMode != 0) {
            SecondaryDisplay secondaryDisplay = SecondaryDisplay.INSTANCE.lambda$get$0(this.mContext);
            Point displaySize = secondaryDisplay.getDisplaySize();
            Point navBarSize = secondaryDisplay.navBarSize();
            this.mSwipeTouchRegion.set(0.0f, 0.0f, (float) displaySize.x, (float) displaySize.y);
            if (this.mMode == 2) {
                RectF rectF = this.mSwipeTouchRegion;
                rectF.top = rectF.bottom - ((float) navBarSize.y);
                return;
            }
            int rotation = secondaryDisplay.getRotation();
            if (rotation == 1) {
                RectF rectF2 = this.mSwipeTouchRegion;
                rectF2.left = rectF2.right - ((float) navBarSize.x);
            } else if (rotation != 3) {
                RectF rectF3 = this.mSwipeTouchRegion;
                rectF3.top = rectF3.bottom - ((float) navBarSize.y);
            } else {
                RectF rectF4 = this.mSwipeTouchRegion;
                rectF4.right = rectF4.left + ((float) navBarSize.x);
            }
        }
    }

    public boolean isInSwipeUpTouchRegion(MotionEvent motionEvent) {
        return this.mSwipeTouchRegion.contains(motionEvent.getX(), motionEvent.getY());
    }

    public boolean isInSwipeUpTouchRegion(MotionEvent motionEvent, int i) {
        return this.mSwipeTouchRegion.contains(motionEvent.getX(i), motionEvent.getY(i));
    }

    public boolean isInDeferredGestureRegion(MotionEvent motionEvent) {
        return this.mDeferredGestureRegion.contains((int) motionEvent.getX(), (int) motionEvent.getY());
    }

    public boolean isInExclusionRegion(MotionEvent motionEvent) {
        Region region = this.mExclusionRegion;
        return this.mMode == 2 && region != null && region.contains((int) motionEvent.getX(), (int) motionEvent.getY());
    }
}
