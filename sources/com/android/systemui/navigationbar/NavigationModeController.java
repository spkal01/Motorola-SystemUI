package com.android.systemui.navigationbar;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.om.IOverlayManager;
import android.content.om.OverlayInfo;
import android.content.pm.PackageManager;
import android.content.res.ApkAssets;
import android.database.ContentObserver;
import android.os.Handler;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.UserHandle;
import android.provider.Settings;
import android.util.Log;
import com.android.systemui.Dumpable;
import com.android.systemui.shared.system.ActivityManagerWrapper;
import com.android.systemui.statusbar.policy.ConfigurationController;
import com.android.systemui.statusbar.policy.DeviceProvisionedController;
import com.motorola.android.provider.MotorolaSettings;
import com.motorola.systemui.desktop.util.DesktopDisplayContext;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.concurrent.Executor;

public class NavigationModeController implements Dumpable {
    /* access modifiers changed from: private */
    public static final String TAG = "NavigationModeController";
    private final Context mContext;
    /* access modifiers changed from: private */
    public Context mCurrentUserContext;
    private final DeviceProvisionedController.DeviceProvisionedListener mDeviceProvisionedCallback;
    private ContentObserver mHideGestureObserver;
    private ArrayList<ModeChangedListener> mListeners = new ArrayList<>();
    private final IOverlayManager mOverlayManager;
    private BroadcastReceiver mReceiver;
    private boolean mSupportCustomNav = false;
    private boolean mTrackpadIconShow = false;
    private final Executor mUiBgExecutor;

    public interface ModeChangedListener {
        void onNavigationModeChanged(int i);
    }

    public NavigationModeController(Context context, DeviceProvisionedController deviceProvisionedController, ConfigurationController configurationController, Executor executor) {
        C10831 r1 = new DeviceProvisionedController.DeviceProvisionedListener() {
            public void onUserSwitched() {
                String access$000 = NavigationModeController.TAG;
                Log.d(access$000, "onUserSwitched: " + ActivityManagerWrapper.getInstance().getCurrentUserId());
                NavigationModeController.this.updateCurrentInteractionMode(true);
            }
        };
        this.mDeviceProvisionedCallback = r1;
        this.mReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                Log.d(NavigationModeController.TAG, "ACTION_OVERLAY_CHANGED");
                NavigationModeController.this.updateCurrentInteractionMode(true);
            }
        };
        this.mHideGestureObserver = new ContentObserver((Handler) null) {
            public void onChange(boolean z) {
                NavigationModeController navigationModeController = NavigationModeController.this;
                if (navigationModeController.getCurrentInteractionMode(navigationModeController.mCurrentUserContext) == 2) {
                    NavigationModeController.this.updateCurrentInteractionMode(true);
                }
            }
        };
        this.mContext = context;
        this.mCurrentUserContext = context;
        this.mSupportCustomNav = context.getResources().getBoolean(17891596);
        this.mOverlayManager = IOverlayManager.Stub.asInterface(ServiceManager.getService("overlay"));
        this.mUiBgExecutor = executor;
        deviceProvisionedController.addCallback(r1);
        IntentFilter intentFilter = new IntentFilter("android.intent.action.OVERLAY_CHANGED");
        intentFilter.addDataScheme("package");
        intentFilter.addDataSchemeSpecificPart("android", 0);
        context.registerReceiverAsUser(this.mReceiver, UserHandle.ALL, intentFilter, (String) null, (Handler) null);
        configurationController.addCallback(new ConfigurationController.ConfigurationListener() {
            public void onOverlayChanged() {
                Log.d(NavigationModeController.TAG, "onOverlayChanged");
                NavigationModeController.this.updateCurrentInteractionMode(true);
            }
        });
        updateCurrentInteractionMode(false);
        context.getContentResolver().registerContentObserver(MotorolaSettings.Secure.getUriFor("hide_gesture_pill"), false, this.mHideGestureObserver, -1);
    }

    public void updateCurrentInteractionMode(boolean z) {
        if (!(this.mContext instanceof DesktopDisplayContext)) {
            Context currentUserContext = getCurrentUserContext();
            this.mCurrentUserContext = currentUserContext;
            int currentInteractionMode = getCurrentInteractionMode(currentUserContext);
            if (this.mSupportCustomNav && currentInteractionMode == 2) {
                switchToDefaultGestureNavOverlayIfNecessary();
            }
            this.mUiBgExecutor.execute(new NavigationModeController$$ExternalSyntheticLambda0(this, currentInteractionMode));
            String str = TAG;
            Log.d(str, "updateCurrentInteractionMode: mode=" + currentInteractionMode);
            dumpAssetPaths(this.mCurrentUserContext);
            if (z) {
                for (int i = 0; i < this.mListeners.size(); i++) {
                    this.mListeners.get(i).onNavigationModeChanged(currentInteractionMode);
                }
            }
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$updateCurrentInteractionMode$0(int i) {
        Settings.Secure.putString(this.mCurrentUserContext.getContentResolver(), "navigation_mode", String.valueOf(i));
    }

    /* access modifiers changed from: package-private */
    public boolean isGestureHided() {
        if (MotorolaSettings.Secure.getIntForUser(this.mContext.getContentResolver(), "hide_gesture_pill", 0, -2) == 1) {
            return true;
        }
        return false;
    }

    private void switchToDefaultGestureNavOverlayIfNecessary() {
        int userId = this.mCurrentUserContext.getUserId();
        try {
            String str = isGestureHided() && !this.mTrackpadIconShow ? "com.android.internal.systemui.navbar.hidegestural" : "com.android.internal.systemui.navbar.gestural";
            OverlayInfo overlayInfo = this.mOverlayManager.getOverlayInfo(str, userId);
            if (overlayInfo != null && !overlayInfo.isEnabled()) {
                this.mOverlayManager.setEnabledExclusiveInCategory(str, userId);
            }
        } catch (RemoteException | IllegalStateException | SecurityException unused) {
            String str2 = TAG;
            Log.e(str2, "Failed to switch to default gesture nav overlay for user " + userId);
        }
    }

    public int addListener(ModeChangedListener modeChangedListener) {
        this.mListeners.add(modeChangedListener);
        return getCurrentInteractionMode(this.mCurrentUserContext);
    }

    public void removeListener(ModeChangedListener modeChangedListener) {
        this.mListeners.remove(modeChangedListener);
    }

    /* access modifiers changed from: private */
    public int getCurrentInteractionMode(Context context) {
        int integer = context.getResources().getInteger(17694885);
        String str = TAG;
        Log.d(str, "getCurrentInteractionMode: mode=" + integer + " contextUser=" + context.getUserId());
        return integer;
    }

    public Context getCurrentUserContext() {
        int currentUserId = ActivityManagerWrapper.getInstance().getCurrentUserId();
        String str = TAG;
        Log.d(str, "getCurrentUserContext: contextUser=" + this.mContext.getUserId() + " currentUser=" + currentUserId);
        if (this.mContext.getUserId() == currentUserId) {
            return this.mContext;
        }
        try {
            Context context = this.mContext;
            return context.createPackageContextAsUser(context.getPackageName(), 0, UserHandle.of(currentUserId));
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "Failed to create package context", e);
            return null;
        }
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        String str;
        printWriter.println("NavigationModeController:");
        printWriter.println("  mode=" + getCurrentInteractionMode(this.mCurrentUserContext));
        try {
            str = String.join(", ", this.mOverlayManager.getDefaultOverlayPackages());
        } catch (RemoteException unused) {
            str = "failed_to_fetch";
        }
        printWriter.println("  defaultOverlays=" + str);
        dumpAssetPaths(this.mCurrentUserContext);
    }

    private void dumpAssetPaths(Context context) {
        String str = TAG;
        Log.d(str, "  contextUser=" + this.mCurrentUserContext.getUserId());
        Log.d(str, "  assetPaths=");
        for (ApkAssets apkAssets : context.getResources().getAssets().getApkAssets()) {
            Log.d(TAG, "    " + apkAssets.getDebugName());
        }
    }

    public void requestNavGestureOverlay(boolean z) {
        if (this.mTrackpadIconShow != z) {
            this.mTrackpadIconShow = z;
            updateCurrentInteractionMode(true);
        }
    }

    public boolean isTrackpadIconShow() {
        return this.mTrackpadIconShow;
    }
}
