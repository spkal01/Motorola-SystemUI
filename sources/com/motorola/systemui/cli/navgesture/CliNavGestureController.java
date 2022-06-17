package com.motorola.systemui.cli.navgesture;

import android.app.trust.TrustManager;
import android.content.ContentResolver;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import com.android.systemui.Dependency;
import com.android.systemui.moto.MotoFeature;
import com.android.systemui.statusbar.CommandQueue;
import com.android.systemui.statusbar.phone.CliStatusBarWindowController;
import com.android.systemui.statusbar.phone.StatusBar;
import com.android.systemui.statusbar.phone.StatusBarWindowCallback;
import dagger.Lazy;
import java.util.Optional;

public class CliNavGestureController implements CommandQueue.Callbacks {
    private CliStatusBarWindowController mCliStatusBarWindowController;
    private CommandQueue mCommandQueue;
    private Context mContext;
    private Handler mHandler;
    boolean mKeyguardOcclude;
    boolean mKeyguardShowing;
    private MultiUserCliNavGestures mMultiUserNavGestures;
    boolean mPanelVisible;
    private final Lazy<StatusBar> mStatusBarLazy;
    private StatusBarWindowCallback mStatusBarViewStateCallback = new StatusBarWindowCallback() {
        public void onStateChanged(boolean z, boolean z2, boolean z3) {
        }

        public void onViewStateChanged(boolean z) {
            CliNavGestureController cliNavGestureController = CliNavGestureController.this;
            if (cliNavGestureController.mPanelVisible != z) {
                cliNavGestureController.mPanelVisible = z;
                cliNavGestureController.setSystemUiFlag(4, z);
            }
        }

        public void onStateChangedForCli(boolean z, boolean z2, boolean z3, boolean z4, boolean z5) {
            CliNavGestureController cliNavGestureController = CliNavGestureController.this;
            if (cliNavGestureController.mKeyguardShowing != z) {
                cliNavGestureController.updateKeyguardShowing(z);
            }
            CliNavGestureController cliNavGestureController2 = CliNavGestureController.this;
            cliNavGestureController2.mKeyguardOcclude = z2;
            cliNavGestureController2.mKeyguardShowing = z;
            cliNavGestureController2.setSystemUiFlag(512, z2 && z);
            CliNavGestureController.this.setSystemUiFlag(64, z);
        }
    };
    private TrustManager mTrustManager;
    private int mUserId = 0;

    public CliNavGestureController(Context context, Optional<Lazy<StatusBar>> optional) {
        this.mContext = MotoFeature.getCliContext(context);
        this.mHandler = new Handler(Looper.getMainLooper());
        CommandQueue commandQueue = (CommandQueue) Dependency.get(CommandQueue.class);
        this.mCommandQueue = commandQueue;
        commandQueue.addCallback((CommandQueue.Callbacks) this);
        this.mStatusBarLazy = optional.orElse((Object) null);
        CliStatusBarWindowController cliStatusBarWindowController = (CliStatusBarWindowController) Dependency.get(CliStatusBarWindowController.class);
        this.mCliStatusBarWindowController = cliStatusBarWindowController;
        cliStatusBarWindowController.registerCallback(this.mStatusBarViewStateCallback);
        this.mTrustManager = (TrustManager) context.getSystemService("trust");
        this.mMultiUserNavGestures = (MultiUserCliNavGestures) Dependency.get(MultiUserCliNavGestures.class);
    }

    private boolean isUserSetup() {
        ContentResolver contentResolver = this.mContext.getContentResolver();
        if (Settings.Global.getInt(contentResolver, "device_provisioned", 0) == 0 || Settings.Secure.getInt(contentResolver, "user_setup_complete", 0) == 0) {
            return false;
        }
        return true;
    }

    public void toggleRecentApps() {
        if (isUserSetup()) {
            onOverviewToggle();
        }
    }

    public void preloadOverView() {
        this.mMultiUserNavGestures.preloadOverView();
    }

    /* access modifiers changed from: private */
    public void updateKeyguardShowing(boolean z) {
        Log.d("CliNavGestureController", "updateKeyguardShowing keyguardShowing = " + z);
        if (!z) {
            preloadOverView();
        }
    }

    public void setSystemUiFlag(int i, boolean z) {
        this.mMultiUserNavGestures.setSystemUiFlag(i, z);
    }

    public void onOverviewToggle() {
        if (MotoFeature.getInstance(this.mContext).isSupportCli() && MotoFeature.isLidClosed(this.mContext)) {
            CliNavGestureController$$ExternalSyntheticLambda0 cliNavGestureController$$ExternalSyntheticLambda0 = new CliNavGestureController$$ExternalSyntheticLambda0(this);
            Lazy<StatusBar> lazy = this.mStatusBarLazy;
            if (lazy == null || !lazy.get().isKeyguardShowing()) {
                cliNavGestureController$$ExternalSyntheticLambda0.run();
            } else {
                this.mStatusBarLazy.get().executeRunnableDismissingKeyguard(new CliNavGestureController$$ExternalSyntheticLambda1(this, cliNavGestureController$$ExternalSyntheticLambda0), (Runnable) null, true, false, true);
            }
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onOverviewToggle$0() {
        this.mMultiUserNavGestures.toggleRecents();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onOverviewToggle$1(Runnable runnable) {
        this.mTrustManager.reportKeyguardShowingChanged();
        this.mHandler.post(runnable);
    }
}
