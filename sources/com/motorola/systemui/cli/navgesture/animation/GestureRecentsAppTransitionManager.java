package com.motorola.systemui.cli.navgesture.animation;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.ActivityOptions;
import android.content.Context;
import android.os.CancellationSignal;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import com.android.systemui.R$dimen;
import com.android.systemui.shared.system.ActivityOptionsCompat;
import com.android.systemui.shared.system.RemoteAnimationAdapterCompat;
import com.android.systemui.shared.system.RemoteAnimationTargetCompat;
import com.motorola.systemui.cli.navgesture.AbstractRecentGestureLauncher;
import com.motorola.systemui.cli.navgesture.BaseGestureActivity;
import com.motorola.systemui.cli.navgesture.CommonBasicActivity;
import com.motorola.systemui.cli.navgesture.IRecentsView;
import com.motorola.systemui.cli.navgesture.Interpolators;
import com.motorola.systemui.cli.navgesture.animation.remote.LauncherRemoteAnimationRunner;
import com.motorola.systemui.cli.navgesture.animation.remote.RemoteAnimationProvider;
import com.motorola.systemui.cli.navgesture.animation.remote.WrappedAnimationRunnerImpl;
import com.motorola.systemui.cli.navgesture.animation.remote.WrappedLauncherRemoteAnimationRunner;
import com.motorola.systemui.cli.navgesture.recents.ITaskViewAware;
import com.motorola.systemui.cli.navgesture.states.StateManager;
import com.motorola.systemui.cli.navgesture.util.ClipAnimationHelper;
import com.motorola.systemui.cli.navgesture.util.DeviceProfile;
import com.motorola.systemui.cli.navgesture.util.DeviceProfileProvider;
import com.motorola.systemui.cli.navgesture.util.TaskUtils;
import com.motorola.systemui.cli.navgesture.util.TaskViewUtils;

public class GestureRecentsAppTransitionManager extends AppTransitionManager implements DeviceProfileProvider.DeviceProfileChangeListener {
    private WrappedAnimationRunnerImpl mAppLaunchRunner;
    private final float mClosingWindowTransY;
    private DeviceProfile mDeviceProfile;
    /* access modifiers changed from: private */
    public final AnimatorListenerAdapter mForceInvisibleListener = new AnimatorListenerAdapter() {
        public void onAnimationStart(Animator animator) {
            GestureRecentsAppTransitionManager.this.mLauncher.addForceInvisibleFlag(2);
        }

        public void onAnimationEnd(Animator animator) {
            GestureRecentsAppTransitionManager.this.mLauncher.clearForceInvisibleFlag(2);
        }
    };
    private Handler mHandler;
    /* access modifiers changed from: private */
    public AbstractRecentGestureLauncher mLauncher;
    private RemoteAnimationProvider mRemoteAnimationProvider;

    public int getStateElementAnimationsCount() {
        return 2;
    }

    public GestureRecentsAppTransitionManager(Context context) {
        super(context);
        AbstractRecentGestureLauncher abstractRecentGestureLauncher = (AbstractRecentGestureLauncher) BaseGestureActivity.fromContext(context);
        this.mLauncher = abstractRecentGestureLauncher;
        this.mDeviceProfile = abstractRecentGestureLauncher.getDeviceProfile();
        this.mHandler = new Handler(Looper.getMainLooper());
        this.mClosingWindowTransY = this.mLauncher.getResources().getDimension(R$dimen.closing_window_trans_y);
        DeviceProfileProvider.INSTANCE.lambda$get$0(context).addOnChangeListener(this);
    }

    public void onDeviceProfileChanged(int i, DeviceProfileProvider deviceProfileProvider) {
        this.mDeviceProfile = deviceProfileProvider.getDeviceProfile(this.mLauncher);
    }

    public ActivityOptions getActivityLaunchOptions(CommonBasicActivity commonBasicActivity, View view) {
        if (!canControlRemoteAppTransition(this.mLauncher)) {
            return super.getActivityLaunchOptions(commonBasicActivity, view);
        }
        boolean isLaunchingFromRecents = isLaunchingFromRecents(view, (RemoteAnimationTargetCompat[]) null);
        this.mAppLaunchRunner = new AppLaunchRemoteAnimationRunner(this.mHandler, view);
        WrappedLauncherRemoteAnimationRunner wrappedLauncherRemoteAnimationRunner = new WrappedLauncherRemoteAnimationRunner(this.mAppLaunchRunner, true);
        long j = isLaunchingFromRecents ? 336 : 450;
        return ActivityOptionsCompat.makeRemoteAnimation(new RemoteAnimationAdapterCompat(wrappedLauncherRemoteAnimationRunner, j, (j - 120) - 96));
    }

    public void setRemoteAnimationProvider(RemoteAnimationProvider remoteAnimationProvider, CancellationSignal cancellationSignal) {
        this.mRemoteAnimationProvider = remoteAnimationProvider;
        cancellationSignal.setOnCancelListener(new GestureRecentsAppTransitionManager$$ExternalSyntheticLambda0(this, remoteAnimationProvider));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$setRemoteAnimationProvider$0(RemoteAnimationProvider remoteAnimationProvider) {
        if (remoteAnimationProvider == this.mRemoteAnimationProvider) {
            this.mRemoteAnimationProvider = null;
        }
    }

    /* access modifiers changed from: private */
    public boolean isLaunchingFromRecents(View view, RemoteAnimationTargetCompat[] remoteAnimationTargetCompatArr) {
        return this.mLauncher.getStateManager().getState().overview() && TaskViewUtils.findTaskViewToLaunch(this.mLauncher, view, remoteAnimationTargetCompatArr) != null;
    }

    /* access modifiers changed from: private */
    public boolean composeRecentsLaunchAnimator(AnimatorSet animatorSet, View view, RemoteAnimationTargetCompat[] remoteAnimationTargetCompatArr, RemoteAnimationTargetCompat[] remoteAnimationTargetCompatArr2, boolean z) {
        Animator.AnimatorListener animatorListener;
        ValueAnimator valueAnimator;
        IRecentsView overviewPanel = this.mLauncher.getOverviewPanel();
        boolean z2 = !z;
        ITaskViewAware findTaskViewToLaunch = TaskViewUtils.findTaskViewToLaunch(this.mLauncher, view, remoteAnimationTargetCompatArr);
        if (findTaskViewToLaunch == null) {
            return false;
        }
        animatorSet.play(TaskViewUtils.getRecentsWindowAnimator(findTaskViewToLaunch, overviewPanel, z2, remoteAnimationTargetCompatArr, remoteAnimationTargetCompatArr2, new ClipAnimationHelper(this.mLauncher)).setDuration(336));
        AnimatorSet animatorSet2 = null;
        if (z) {
            valueAnimator = ValueAnimator.ofInt(new int[]{0, 1});
            valueAnimator.setInterpolator(Interpolators.TOUCH_RESPONSE_INTERPOLATOR);
            valueAnimator.setDuration(336);
            animatorListener = new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    GestureRecentsAppTransitionManager.this.mLauncher.getStateManager().moveToRestState();
                    GestureRecentsAppTransitionManager.this.mLauncher.getStateManager().reapplyState();
                }
            };
        } else {
            AnimatorPlaybackController createAnimationToNewWorkspace = this.mLauncher.getStateManager().createAnimationToNewWorkspace(StateManager.NORMAL, 336);
            createAnimationToNewWorkspace.dispatchOnStart();
            AnimatorSet target = createAnimationToNewWorkspace.getTarget();
            ValueAnimator duration = createAnimationToNewWorkspace.getAnimationPlayer().setDuration(336);
            animatorListener = new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    GestureRecentsAppTransitionManager.this.mLauncher.getStateManager().goToState(StateManager.NORMAL, false);
                }
            };
            AnimatorSet animatorSet3 = target;
            valueAnimator = duration;
            animatorSet2 = animatorSet3;
        }
        animatorSet.play(valueAnimator);
        this.mLauncher.getStateManager().setCurrentAnimation(animatorSet, animatorSet2);
        animatorSet.addListener(animatorListener);
        return true;
    }

    public static boolean canControlRemoteAppTransition(Context context) {
        return hasControlRemoteAppTransitionPermission(context);
    }

    private static boolean hasControlRemoteAppTransitionPermission(Context context) {
        return context.checkSelfPermission("android.permission.CONTROL_REMOTE_APP_TRANSITION_ANIMATIONS") == 0;
    }

    public void dispose() {
        super.dispose();
        this.mAppLaunchRunner = null;
    }

    private class AppLaunchRemoteAnimationRunner implements WrappedAnimationRunnerImpl {
        private final Handler mHandler;

        /* renamed from: mV */
        private final View f193mV;

        public AppLaunchRemoteAnimationRunner(Handler handler, View view) {
            this.mHandler = handler;
            this.f193mV = view;
        }

        public Handler getHandler() {
            return this.mHandler;
        }

        public void onCreateAnimation(RemoteAnimationTargetCompat[] remoteAnimationTargetCompatArr, RemoteAnimationTargetCompat[] remoteAnimationTargetCompatArr2, LauncherRemoteAnimationRunner.AnimationResult animationResult) {
            AnimatorSet animatorSet = new AnimatorSet();
            boolean activityIsATargetWithMode = TaskUtils.activityIsATargetWithMode(GestureRecentsAppTransitionManager.this.mLauncher, remoteAnimationTargetCompatArr, 1);
            if (GestureRecentsAppTransitionManager.this.isLaunchingFromRecents(this.f193mV, remoteAnimationTargetCompatArr)) {
                boolean unused = GestureRecentsAppTransitionManager.this.composeRecentsLaunchAnimator(animatorSet, this.f193mV, remoteAnimationTargetCompatArr, remoteAnimationTargetCompatArr2, activityIsATargetWithMode);
            } else {
                Log.w("GestureRecents", "onCreateAnimation !isLaunchingFromRecents");
                animatorSet = null;
            }
            if (activityIsATargetWithMode) {
                animatorSet.addListener(GestureRecentsAppTransitionManager.this.mForceInvisibleListener);
            }
            animationResult.setAnimation(animatorSet);
        }
    }

    public Animator createStateElementAnimation(int i, float... fArr) {
        if (i == 0) {
            return ObjectAnimator.ofFloat(this.mLauncher.getOverviewPanel(), IRecentsView.CONTENT_ALPHA, fArr);
        }
        if (i != 1) {
            return super.createStateElementAnimation(i, fArr);
        }
        return ValueAnimator.ofFloat(fArr);
    }
}
