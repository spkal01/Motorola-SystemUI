package com.android.systemui.statusbar;

import android.app.ITransientNotificationCallback;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.hardware.biometrics.IBiometricSysuiReceiver;
import android.hardware.biometrics.PromptInfo;
import android.hardware.display.DisplayManager;
import android.hardware.fingerprint.IUdfpsHbmListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.ParcelFileDescriptor;
import android.os.UserHandle;
import android.util.Pair;
import android.util.SparseArray;
import com.android.internal.os.SomeArgs;
import com.android.internal.statusbar.IStatusBar;
import com.android.internal.statusbar.StatusBarIcon;
import com.android.internal.util.GcUtils;
import com.android.internal.view.AppearanceRegion;
import com.android.systemui.moto.MotoFeature;
import com.android.systemui.statusbar.commandline.CommandRegistry;
import com.android.systemui.statusbar.phone.StatusBar;
import com.android.systemui.statusbar.policy.CallbackController;
import com.android.systemui.tracing.ProtoTracer;
import com.motorola.internal.app.MotoDesktopManager;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;

public class CommandQueue extends IStatusBar.Stub implements CallbackController<Callbacks>, DisplayManager.DisplayListener {
    /* access modifiers changed from: private */
    public static final String TAG = CommandQueue.class.getSimpleName();
    /* access modifiers changed from: private */
    public ArrayList<Callbacks> mCallbacks = new ArrayList<>();
    private Context mContext;
    private SparseArray<Pair<Integer, Integer>> mDisplayDisabled = new SparseArray<>();
    /* access modifiers changed from: private */
    public Handler mHandler = new C1426H(Looper.getMainLooper());
    private int mLastKeyCode;
    private int mLastUpdatedImeDisplayId = -1;
    private final Object mLock = new Object();
    private ProtoTracer mProtoTracer;
    /* access modifiers changed from: private */
    public final CommandRegistry mRegistry;
    private long mStylusDownTime = -1;

    public interface Callbacks {
        void abortTransient(int i, int[] iArr) {
        }

        void addDesktopIcon(String str, int i, StatusBarIcon statusBarIcon, PendingIntent pendingIntent) {
        }

        void addQsTile(ComponentName componentName) {
        }

        void animateCollapsePanels(int i, boolean z) {
        }

        void animateExpandNotificationsPanel() {
        }

        void animateExpandSettingsPanel(String str) {
        }

        void appTransitionCancelled(int i) {
        }

        void appTransitionFinished(int i) {
        }

        void appTransitionPending(int i, boolean z) {
        }

        void appTransitionStarting(int i, long j, long j2, boolean z) {
        }

        void cancelPreloadRecentApps() {
        }

        void clickTile(ComponentName componentName) {
        }

        void disable(int i, int i2, int i3, boolean z) {
        }

        void dismissInattentiveSleepWarning(boolean z) {
        }

        void dismissKeyboardShortcutsMenu() {
        }

        void dismissKeyboardShortcutsMenuForDisplay(int i) {
        }

        void handleShowGlobalActionsMenu() {
        }

        void handleShowShutdownUi(boolean z, String str) {
        }

        void handleSystemKey(int i) {
        }

        void handleWindowManagerLoggingCommand(String[] strArr, ParcelFileDescriptor parcelFileDescriptor) {
        }

        void hideAuthenticationDialog() {
        }

        void hideRecentApps(boolean z, boolean z2) {
        }

        void hideToast(String str, IBinder iBinder) {
        }

        void onBiometricAuthenticated() {
        }

        void onBiometricError(int i, int i2, int i3) {
        }

        void onBiometricHelp(int i, String str) {
        }

        void onCameraLaunchGestureDetected(int i) {
        }

        void onCameraLaunchGestureDetectedForAutoQuickCapture(int i, boolean z) {
        }

        void onDisplayReady(int i) {
        }

        void onDisplayRemoved(int i) {
        }

        void onEmergencyActionLaunchGestureDetected() {
        }

        void onRecentsAnimationStateChanged(boolean z) {
        }

        void onRotationProposal(int i, boolean z) {
        }

        void onStylusButtonEvent(int i) {
        }

        void onSystemBarAttributesChanged(int i, int i2, AppearanceRegion[] appearanceRegionArr, boolean z, int i3, boolean z2) {
        }

        void onTracingStateChanged(boolean z) {
        }

        void preloadRecentApps() {
        }

        void remQsTile(ComponentName componentName) {
        }

        void removeDesktopIcon(String str, int i) {
        }

        void removeIcon(String str) {
        }

        void requestWindowMagnificationConnection(boolean z) {
        }

        void resetTaskBarAudoHideTimeout(int i) {
        }

        void setHeadsUpVisibleForCli(boolean z) {
        }

        void setIcon(String str, StatusBarIcon statusBarIcon) {
        }

        void setImeWindowStatus(int i, IBinder iBinder, int i2, int i3, boolean z) {
        }

        void setNavigationBarLumaSamplingEnabled(int i, boolean z) {
        }

        void setTopAppHidesStatusBar(boolean z) {
        }

        void setUdfpsHbmListener(IUdfpsHbmListener iUdfpsHbmListener) {
        }

        void setWindowState(int i, int i2, int i3) {
        }

        void showAssistDisclosure() {
        }

        void showAuthenticationDialog(PromptInfo promptInfo, IBiometricSysuiReceiver iBiometricSysuiReceiver, int[] iArr, boolean z, boolean z2, int i, String str, long j, int i2) {
        }

        void showInattentiveSleepWarning() {
        }

        void showPictureInPictureMenu() {
        }

        void showPinningEnterExitToast(boolean z) {
        }

        void showPinningEscapeToast() {
        }

        void showRecentApps(boolean z) {
        }

        void showScreenPinningRequest(int i) {
        }

        void showToast(int i, String str, IBinder iBinder, CharSequence charSequence, IBinder iBinder2, int i2, ITransientNotificationCallback iTransientNotificationCallback) {
        }

        void showToastForDisplay(int i, String str, IBinder iBinder, CharSequence charSequence, IBinder iBinder2, int i2, ITransientNotificationCallback iTransientNotificationCallback, int i3) {
        }

        void showTransient(int i, int[] iArr) {
        }

        void showWirelessChargingAnimation(int i) {
        }

        void startAssist(Bundle bundle) {
        }

        void suppressAmbientDisplay(boolean z) {
        }

        void toggleKeyboardShortcutsMenu(int i) {
        }

        void toggleKeyboardShortcutsMenuForDisplay(int i, int i2) {
        }

        void togglePanel() {
        }

        void toggleRecentApps() {
        }

        void toggleSplitScreen() {
        }
    }

    public void onDisplayAdded(int i) {
    }

    public void onDisplayChanged(int i) {
    }

    public CommandQueue(Context context, ProtoTracer protoTracer, CommandRegistry commandRegistry) {
        this.mProtoTracer = protoTracer;
        this.mRegistry = commandRegistry;
        this.mContext = context;
        ((DisplayManager) context.getSystemService(DisplayManager.class)).registerDisplayListener(this, this.mHandler);
        setDisabled(0, 0, 0);
    }

    public void onDisplayRemoved(int i) {
        synchronized (this.mLock) {
            this.mDisplayDisabled.remove(i);
        }
        for (int size = this.mCallbacks.size() - 1; size >= 0; size--) {
            this.mCallbacks.get(size).onDisplayRemoved(i);
        }
    }

    public boolean panelsEnabled() {
        int disabled1 = getDisabled1(0);
        int disabled2 = getDisabled2(0);
        if ((disabled1 & 65536) == 0 && (disabled2 & 4) == 0 && !StatusBar.ONLY_CORE_APPS) {
            return true;
        }
        return false;
    }

    public void addCallback(Callbacks callbacks) {
        this.mCallbacks.add(callbacks);
        for (int i = 0; i < this.mDisplayDisabled.size(); i++) {
            int keyAt = this.mDisplayDisabled.keyAt(i);
            if (!MotoFeature.getInstance(this.mContext).isSupportCli() || ((!MotoFeature.isLidClosed(this.mContext) || keyAt == 1) && (MotoFeature.isLidClosed(this.mContext) || keyAt == 0))) {
                callbacks.disable(keyAt, getDisabled1(keyAt), getDisabled2(keyAt), false);
            }
        }
    }

    public void removeCallback(Callbacks callbacks) {
        this.mCallbacks.remove(callbacks);
    }

    public void setIcon(String str, StatusBarIcon statusBarIcon) {
        synchronized (this.mLock) {
            this.mHandler.obtainMessage(65536, 1, 0, new Pair(str, statusBarIcon)).sendToTarget();
        }
    }

    public void removeIcon(String str) {
        synchronized (this.mLock) {
            this.mHandler.obtainMessage(65536, 2, 0, str).sendToTarget();
        }
    }

    public void disable(int i, int i2, int i3, boolean z) {
        synchronized (this.mLock) {
            setDisabled(i, i2, i3);
            int i4 = 131072 | (65535 & i);
            this.mHandler.removeMessages(i4);
            SomeArgs obtain = SomeArgs.obtain();
            obtain.argi1 = i;
            obtain.argi2 = i2;
            obtain.argi3 = i3;
            obtain.argi4 = z ? 1 : 0;
            Message obtainMessage = this.mHandler.obtainMessage(i4, obtain);
            if (Looper.myLooper() == this.mHandler.getLooper()) {
                this.mHandler.handleMessage(obtainMessage);
                obtainMessage.recycle();
            } else {
                obtainMessage.sendToTarget();
            }
        }
    }

    public void disable(int i, int i2, int i3) {
        disable(i, i2, i3, true);
    }

    public void recomputeDisableFlags(int i, boolean z) {
        synchronized (this.mLock) {
            disable(i, getDisabled1(i), getDisabled2(i), z);
        }
    }

    private void setDisabled(int i, int i2, int i3) {
        this.mDisplayDisabled.put(i, new Pair(Integer.valueOf(i2), Integer.valueOf(i3)));
    }

    private int getDisabled1(int i) {
        return ((Integer) getDisabled(i).first).intValue();
    }

    private int getDisabled2(int i) {
        return ((Integer) getDisabled(i).second).intValue();
    }

    private Pair<Integer, Integer> getDisabled(int i) {
        Pair<Integer, Integer> pair = this.mDisplayDisabled.get(i);
        if (pair != null) {
            return pair;
        }
        Pair<Integer, Integer> pair2 = new Pair<>(0, 0);
        this.mDisplayDisabled.put(i, pair2);
        return pair2;
    }

    public void animateExpandNotificationsPanel() {
        synchronized (this.mLock) {
            this.mHandler.removeMessages(196608);
            this.mHandler.sendEmptyMessage(196608);
        }
    }

    public void animateCollapsePanels() {
        synchronized (this.mLock) {
            this.mHandler.removeMessages(262144);
            this.mHandler.obtainMessage(262144, 0, 0).sendToTarget();
        }
    }

    public void animateCollapsePanels(int i, boolean z) {
        synchronized (this.mLock) {
            this.mHandler.removeMessages(262144);
            this.mHandler.obtainMessage(262144, i, z ? 1 : 0).sendToTarget();
        }
    }

    public void togglePanel() {
        synchronized (this.mLock) {
            this.mHandler.removeMessages(2293760);
            this.mHandler.obtainMessage(2293760, 0, 0).sendToTarget();
        }
    }

    public void animateExpandSettingsPanel(String str) {
        synchronized (this.mLock) {
            this.mHandler.removeMessages(327680);
            this.mHandler.obtainMessage(327680, str).sendToTarget();
        }
    }

    public void setImeWindowStatus(int i, IBinder iBinder, int i2, int i3, boolean z, boolean z2) {
        synchronized (this.mLock) {
            this.mHandler.removeMessages(524288);
            SomeArgs obtain = SomeArgs.obtain();
            obtain.argi1 = i;
            obtain.argi2 = i2;
            obtain.argi3 = i3;
            int i4 = 1;
            obtain.argi4 = z ? 1 : 0;
            if (!z2) {
                i4 = 0;
            }
            obtain.argi5 = i4;
            obtain.arg1 = iBinder;
            this.mHandler.obtainMessage(524288, obtain).sendToTarget();
        }
    }

    public void showRecentApps(boolean z) {
        synchronized (this.mLock) {
            this.mHandler.removeMessages(851968);
            this.mHandler.obtainMessage(851968, z ? 1 : 0, 0, (Object) null).sendToTarget();
        }
    }

    public void hideRecentApps(boolean z, boolean z2) {
        synchronized (this.mLock) {
            this.mHandler.removeMessages(917504);
            this.mHandler.obtainMessage(917504, z ? 1 : 0, z2 ? 1 : 0, (Object) null).sendToTarget();
        }
    }

    public void toggleSplitScreen() {
        synchronized (this.mLock) {
            this.mHandler.removeMessages(1966080);
            this.mHandler.obtainMessage(1966080, 0, 0, (Object) null).sendToTarget();
        }
    }

    public void toggleRecentApps() {
        synchronized (this.mLock) {
            this.mHandler.removeMessages(589824);
            Message obtainMessage = this.mHandler.obtainMessage(589824, 0, 0, (Object) null);
            obtainMessage.setAsynchronous(true);
            obtainMessage.sendToTarget();
        }
    }

    public void preloadRecentApps() {
        synchronized (this.mLock) {
            this.mHandler.removeMessages(655360);
            this.mHandler.obtainMessage(655360, 0, 0, (Object) null).sendToTarget();
        }
    }

    public void cancelPreloadRecentApps() {
        synchronized (this.mLock) {
            this.mHandler.removeMessages(720896);
            this.mHandler.obtainMessage(720896, 0, 0, (Object) null).sendToTarget();
        }
    }

    public void dismissKeyboardShortcutsMenu() {
        synchronized (this.mLock) {
            this.mHandler.removeMessages(2097152);
            this.mHandler.obtainMessage(2097152).sendToTarget();
        }
    }

    public void toggleKeyboardShortcutsMenu(int i) {
        synchronized (this.mLock) {
            this.mHandler.removeMessages(1638400);
            this.mHandler.obtainMessage(1638400, i, 0).sendToTarget();
        }
    }

    public void dismissKeyboardShortcutsMenuForDisplay(int i) {
        synchronized (this.mLock) {
            if (MotoDesktopManager.isDesktopSupported()) {
                if (i != 0) {
                    this.mHandler.removeMessages(4325376);
                    this.mHandler.obtainMessage(4325376, i, 0).sendToTarget();
                }
            }
            dismissKeyboardShortcutsMenu();
        }
    }

    public void toggleKeyboardShortcutsMenuForDisplay(int i, int i2) {
        synchronized (this.mLock) {
            if (MotoDesktopManager.isDesktopSupported()) {
                if (i2 != 0) {
                    this.mHandler.removeMessages(4390912);
                    this.mHandler.obtainMessage(4390912, i, i2).sendToTarget();
                }
            }
            toggleKeyboardShortcutsMenu(i);
        }
    }

    public void showPictureInPictureMenu() {
        synchronized (this.mLock) {
            this.mHandler.removeMessages(1703936);
            this.mHandler.obtainMessage(1703936).sendToTarget();
        }
    }

    public void setWindowState(int i, int i2, int i3) {
        synchronized (this.mLock) {
            this.mHandler.obtainMessage(786432, i, i2, Integer.valueOf(i3)).sendToTarget();
        }
    }

    public void showScreenPinningRequest(int i) {
        synchronized (this.mLock) {
            this.mHandler.obtainMessage(1179648, i, 0, (Object) null).sendToTarget();
        }
    }

    public void appTransitionPending(int i) {
        appTransitionPending(i, false);
    }

    public void appTransitionPending(int i, boolean z) {
        synchronized (this.mLock) {
            this.mHandler.obtainMessage(1245184, i, z ? 1 : 0).sendToTarget();
        }
    }

    public void appTransitionCancelled(int i) {
        synchronized (this.mLock) {
            this.mHandler.obtainMessage(1310720, i, 0).sendToTarget();
        }
    }

    public void appTransitionStarting(int i, long j, long j2) {
        appTransitionStarting(i, j, j2, false);
    }

    public void appTransitionStarting(int i, long j, long j2, boolean z) {
        synchronized (this.mLock) {
            SomeArgs obtain = SomeArgs.obtain();
            obtain.argi1 = i;
            obtain.argi2 = z ? 1 : 0;
            obtain.arg1 = Long.valueOf(j);
            obtain.arg2 = Long.valueOf(j2);
            this.mHandler.obtainMessage(1376256, obtain).sendToTarget();
        }
    }

    public void appTransitionFinished(int i) {
        synchronized (this.mLock) {
            this.mHandler.obtainMessage(2031616, i, 0).sendToTarget();
        }
    }

    public void showAssistDisclosure() {
        synchronized (this.mLock) {
            this.mHandler.removeMessages(1441792);
            this.mHandler.obtainMessage(1441792).sendToTarget();
        }
    }

    public void startAssist(Bundle bundle) {
        synchronized (this.mLock) {
            this.mHandler.removeMessages(1507328);
            this.mHandler.obtainMessage(1507328, bundle).sendToTarget();
        }
    }

    public void onCameraLaunchGestureDetected(int i) {
        synchronized (this.mLock) {
            this.mHandler.removeMessages(1572864);
            this.mHandler.obtainMessage(1572864, i, 0).sendToTarget();
        }
    }

    public void onEmergencyActionLaunchGestureDetected() {
        synchronized (this.mLock) {
            this.mHandler.removeMessages(3801088);
            this.mHandler.obtainMessage(3801088).sendToTarget();
        }
    }

    public void addQsTile(ComponentName componentName) {
        synchronized (this.mLock) {
            this.mHandler.obtainMessage(1769472, componentName).sendToTarget();
        }
    }

    public void remQsTile(ComponentName componentName) {
        synchronized (this.mLock) {
            this.mHandler.obtainMessage(1835008, componentName).sendToTarget();
        }
    }

    public void clickQsTile(ComponentName componentName) {
        synchronized (this.mLock) {
            this.mHandler.obtainMessage(1900544, componentName).sendToTarget();
        }
    }

    public void handleSystemKey(int i) {
        synchronized (this.mLock) {
            this.mHandler.obtainMessage(2162688, i, 0).sendToTarget();
        }
    }

    public void showPinningEnterExitToast(boolean z) {
        synchronized (this.mLock) {
            this.mHandler.obtainMessage(2949120, Boolean.valueOf(z)).sendToTarget();
        }
    }

    public void showPinningEscapeToast() {
        synchronized (this.mLock) {
            this.mHandler.obtainMessage(3014656).sendToTarget();
        }
    }

    public void showGlobalActionsMenu() {
        synchronized (this.mLock) {
            Intent intent = new Intent("com.android.systemui.ACTION_GLOBAL_ACTIONS_SHOW");
            intent.setPackage(this.mContext.getPackageName());
            this.mContext.sendBroadcastAsUser(intent, UserHandle.CURRENT);
            this.mHandler.removeMessages(2228224);
            this.mHandler.obtainMessage(2228224).sendToTarget();
        }
    }

    public void setTopAppHidesStatusBar(boolean z) {
        this.mHandler.removeMessages(2424832);
        this.mHandler.obtainMessage(2424832, z ? 1 : 0, 0).sendToTarget();
    }

    public void showShutdownUi(boolean z, String str) {
        synchronized (this.mLock) {
            this.mHandler.removeMessages(2359296);
            this.mHandler.obtainMessage(2359296, z ? 1 : 0, 0, str).sendToTarget();
        }
    }

    public void showWirelessChargingAnimation(int i) {
        this.mHandler.removeMessages(2883584);
        this.mHandler.obtainMessage(2883584, i, 0).sendToTarget();
    }

    public void onProposedRotationChanged(int i, boolean z) {
        synchronized (this.mLock) {
            this.mHandler.removeMessages(2490368);
            this.mHandler.obtainMessage(2490368, i, z ? 1 : 0, (Object) null).sendToTarget();
        }
    }

    public void showAuthenticationDialog(PromptInfo promptInfo, IBiometricSysuiReceiver iBiometricSysuiReceiver, int[] iArr, boolean z, boolean z2, int i, String str, long j, int i2) {
        synchronized (this.mLock) {
            SomeArgs obtain = SomeArgs.obtain();
            obtain.arg1 = promptInfo;
            obtain.arg2 = iBiometricSysuiReceiver;
            obtain.arg3 = iArr;
            obtain.arg4 = Boolean.valueOf(z);
            obtain.arg5 = Boolean.valueOf(z2);
            obtain.argi1 = i;
            obtain.arg6 = str;
            obtain.arg7 = Long.valueOf(j);
            obtain.argi2 = i2;
            this.mHandler.obtainMessage(2555904, obtain).sendToTarget();
        }
    }

    public void showToast(int i, String str, IBinder iBinder, CharSequence charSequence, IBinder iBinder2, int i2, ITransientNotificationCallback iTransientNotificationCallback) {
        synchronized (this.mLock) {
            SomeArgs obtain = SomeArgs.obtain();
            obtain.arg1 = str;
            obtain.arg2 = iBinder;
            obtain.arg3 = charSequence;
            obtain.arg4 = iBinder2;
            obtain.arg5 = iTransientNotificationCallback;
            obtain.argi1 = i;
            obtain.argi2 = i2;
            this.mHandler.obtainMessage(3407872, obtain).sendToTarget();
        }
    }

    public void hideToast(String str, IBinder iBinder) {
        synchronized (this.mLock) {
            SomeArgs obtain = SomeArgs.obtain();
            obtain.arg1 = str;
            obtain.arg2 = iBinder;
            this.mHandler.obtainMessage(3473408, obtain).sendToTarget();
        }
    }

    public void onBiometricAuthenticated() {
        synchronized (this.mLock) {
            this.mHandler.obtainMessage(2621440).sendToTarget();
        }
    }

    public void onBiometricHelp(int i, String str) {
        synchronized (this.mLock) {
            SomeArgs obtain = SomeArgs.obtain();
            obtain.argi1 = i;
            obtain.arg1 = str;
            this.mHandler.obtainMessage(2686976, obtain).sendToTarget();
        }
    }

    public void onBiometricError(int i, int i2, int i3) {
        synchronized (this.mLock) {
            SomeArgs obtain = SomeArgs.obtain();
            obtain.argi1 = i;
            obtain.argi2 = i2;
            obtain.argi3 = i3;
            this.mHandler.obtainMessage(2752512, obtain).sendToTarget();
        }
    }

    public void hideAuthenticationDialog() {
        synchronized (this.mLock) {
            this.mHandler.obtainMessage(2818048).sendToTarget();
        }
    }

    public void setUdfpsHbmListener(IUdfpsHbmListener iUdfpsHbmListener) {
        synchronized (this.mLock) {
            this.mHandler.obtainMessage(3932160, iUdfpsHbmListener).sendToTarget();
        }
    }

    public void onDisplayReady(int i) {
        synchronized (this.mLock) {
            this.mHandler.obtainMessage(458752, i, 0).sendToTarget();
        }
    }

    public void onRecentsAnimationStateChanged(boolean z) {
        synchronized (this.mLock) {
            this.mHandler.obtainMessage(3080192, z ? 1 : 0, 0).sendToTarget();
        }
    }

    public void showInattentiveSleepWarning() {
        synchronized (this.mLock) {
            this.mHandler.obtainMessage(3276800).sendToTarget();
        }
    }

    public void dismissInattentiveSleepWarning(boolean z) {
        synchronized (this.mLock) {
            this.mHandler.obtainMessage(3342336, Boolean.valueOf(z)).sendToTarget();
        }
    }

    public void requestWindowMagnificationConnection(boolean z) {
        synchronized (this.mLock) {
            this.mHandler.obtainMessage(3670016, Boolean.valueOf(z)).sendToTarget();
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:22:0x00af, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onStylusKeyEvent(int r8, boolean r9) {
        /*
            r7 = this;
            java.lang.Object r0 = r7.mLock
            monitor-enter(r0)
            r1 = 10086(0x2766, float:1.4133E-41)
            if (r8 == r1) goto L_0x000b
            r1 = 10087(0x2767, float:1.4135E-41)
            if (r8 != r1) goto L_0x00ae
        L_0x000b:
            long r1 = java.lang.System.currentTimeMillis()     // Catch:{ all -> 0x00b0 }
            if (r9 == 0) goto L_0x002d
            java.lang.String r9 = TAG     // Catch:{ all -> 0x00b0 }
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ all -> 0x00b0 }
            r3.<init>()     // Catch:{ all -> 0x00b0 }
            java.lang.String r4 = "Stylus button press: "
            r3.append(r4)     // Catch:{ all -> 0x00b0 }
            r3.append(r1)     // Catch:{ all -> 0x00b0 }
            java.lang.String r3 = r3.toString()     // Catch:{ all -> 0x00b0 }
            android.util.Log.d(r9, r3)     // Catch:{ all -> 0x00b0 }
            r7.mStylusDownTime = r1     // Catch:{ all -> 0x00b0 }
            r7.mLastKeyCode = r8     // Catch:{ all -> 0x00b0 }
            goto L_0x00ae
        L_0x002d:
            long r3 = r7.mStylusDownTime     // Catch:{ all -> 0x00b0 }
            long r3 = r1 - r3
            r5 = 1500(0x5dc, double:7.41E-321)
            int r9 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1))
            if (r9 > 0) goto L_0x0068
            int r9 = r7.mLastKeyCode     // Catch:{ all -> 0x00b0 }
            if (r8 != r9) goto L_0x0068
            java.lang.String r9 = TAG     // Catch:{ all -> 0x00b0 }
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ all -> 0x00b0 }
            r1.<init>()     // Catch:{ all -> 0x00b0 }
            java.lang.String r2 = "Trigger stylus click event: keycode = "
            r1.append(r2)     // Catch:{ all -> 0x00b0 }
            r1.append(r8)     // Catch:{ all -> 0x00b0 }
            java.lang.String r1 = r1.toString()     // Catch:{ all -> 0x00b0 }
            android.util.Log.d(r9, r1)     // Catch:{ all -> 0x00b0 }
            r1 = -1
            r7.mStylusDownTime = r1     // Catch:{ all -> 0x00b0 }
            android.os.Handler r9 = r7.mHandler     // Catch:{ all -> 0x00b0 }
            r1 = 4456448(0x440000, float:6.244814E-39)
            r9.removeMessages(r1)     // Catch:{ all -> 0x00b0 }
            android.os.Handler r7 = r7.mHandler     // Catch:{ all -> 0x00b0 }
            android.os.Message r7 = r7.obtainMessage(r1)     // Catch:{ all -> 0x00b0 }
            r7.arg1 = r8     // Catch:{ all -> 0x00b0 }
            r7.sendToTarget()     // Catch:{ all -> 0x00b0 }
            goto L_0x00ae
        L_0x0068:
            int r9 = r7.mLastKeyCode     // Catch:{ all -> 0x00b0 }
            if (r9 == r8) goto L_0x008e
            java.lang.String r9 = TAG     // Catch:{ all -> 0x00b0 }
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ all -> 0x00b0 }
            r1.<init>()     // Catch:{ all -> 0x00b0 }
            java.lang.String r2 = "Keycode not match, keycode = "
            r1.append(r2)     // Catch:{ all -> 0x00b0 }
            r1.append(r8)     // Catch:{ all -> 0x00b0 }
            java.lang.String r8 = ", lastKeycode = "
            r1.append(r8)     // Catch:{ all -> 0x00b0 }
            int r7 = r7.mLastKeyCode     // Catch:{ all -> 0x00b0 }
            r1.append(r7)     // Catch:{ all -> 0x00b0 }
            java.lang.String r7 = r1.toString()     // Catch:{ all -> 0x00b0 }
            android.util.Log.d(r9, r7)     // Catch:{ all -> 0x00b0 }
            monitor-exit(r0)     // Catch:{ all -> 0x00b0 }
            return
        L_0x008e:
            java.lang.String r8 = TAG     // Catch:{ all -> 0x00b0 }
            java.lang.StringBuilder r9 = new java.lang.StringBuilder     // Catch:{ all -> 0x00b0 }
            r9.<init>()     // Catch:{ all -> 0x00b0 }
            java.lang.String r3 = "Stylus press timeout, downTime = "
            r9.append(r3)     // Catch:{ all -> 0x00b0 }
            long r3 = r7.mStylusDownTime     // Catch:{ all -> 0x00b0 }
            r9.append(r3)     // Catch:{ all -> 0x00b0 }
            java.lang.String r7 = ", upTime = "
            r9.append(r7)     // Catch:{ all -> 0x00b0 }
            r9.append(r1)     // Catch:{ all -> 0x00b0 }
            java.lang.String r7 = r9.toString()     // Catch:{ all -> 0x00b0 }
            android.util.Log.d(r8, r7)     // Catch:{ all -> 0x00b0 }
        L_0x00ae:
            monitor-exit(r0)     // Catch:{ all -> 0x00b0 }
            return
        L_0x00b0:
            r7 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x00b0 }
            throw r7
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.statusbar.CommandQueue.onStylusKeyEvent(int, boolean):void");
    }

    public void onCameraLaunchGestureDetectedForAutoQuickCapture(int i, boolean z) {
        synchronized (this.mLock) {
            this.mHandler.removeMessages(1572864);
            SomeArgs obtain = SomeArgs.obtain();
            obtain.arg1 = Integer.valueOf(i);
            obtain.arg2 = Integer.valueOf(z ? 1 : 0);
            this.mHandler.obtainMessage(1572864, obtain).sendToTarget();
        }
    }

    public void setHeadsUpVisibleForCli(boolean z) {
        synchronized (this.mLock) {
            this.mHandler.removeMessages(4259840);
            this.mHandler.obtainMessage(4259840, z ? 1 : 0, 0).sendToTarget();
        }
    }

    /* access modifiers changed from: private */
    public void handleShowImeButton(int i, IBinder iBinder, int i2, int i3, boolean z, boolean z2) {
        int i4;
        if (i != -1) {
            if (!(z2 || (i4 = this.mLastUpdatedImeDisplayId) == i || i4 == -1)) {
                sendImeInvisibleStatusForPrevNavBar();
            }
            for (int i5 = 0; i5 < this.mCallbacks.size(); i5++) {
                this.mCallbacks.get(i5).setImeWindowStatus(i, iBinder, i2, i3, z);
            }
            this.mLastUpdatedImeDisplayId = i;
        }
    }

    private void sendImeInvisibleStatusForPrevNavBar() {
        for (int i = 0; i < this.mCallbacks.size(); i++) {
            this.mCallbacks.get(i).setImeWindowStatus(this.mLastUpdatedImeDisplayId, (IBinder) null, 4, 0, false);
        }
    }

    public void onSystemBarAttributesChanged(int i, int i2, AppearanceRegion[] appearanceRegionArr, boolean z, int i3, boolean z2) {
        synchronized (this.mLock) {
            SomeArgs obtain = SomeArgs.obtain();
            obtain.argi1 = i;
            obtain.argi2 = i2;
            int i4 = 1;
            obtain.argi3 = z ? 1 : 0;
            obtain.arg1 = appearanceRegionArr;
            obtain.argi4 = i3;
            if (!z2) {
                i4 = 0;
            }
            obtain.argi5 = i4;
            this.mHandler.obtainMessage(393216, obtain).sendToTarget();
        }
    }

    public void showTransient(int i, int[] iArr) {
        synchronized (this.mLock) {
            this.mHandler.obtainMessage(3145728, i, 0, iArr).sendToTarget();
        }
    }

    public void abortTransient(int i, int[] iArr) {
        synchronized (this.mLock) {
            this.mHandler.obtainMessage(3211264, i, 0, iArr).sendToTarget();
        }
    }

    public void startTracing() {
        synchronized (this.mLock) {
            ProtoTracer protoTracer = this.mProtoTracer;
            if (protoTracer != null) {
                protoTracer.start();
            }
            this.mHandler.obtainMessage(3538944, Boolean.TRUE).sendToTarget();
        }
    }

    public void stopTracing() {
        synchronized (this.mLock) {
            ProtoTracer protoTracer = this.mProtoTracer;
            if (protoTracer != null) {
                protoTracer.stop();
            }
            this.mHandler.obtainMessage(3538944, Boolean.FALSE).sendToTarget();
        }
    }

    public void handleWindowManagerLoggingCommand(String[] strArr, ParcelFileDescriptor parcelFileDescriptor) {
        synchronized (this.mLock) {
            SomeArgs obtain = SomeArgs.obtain();
            obtain.arg1 = strArr;
            obtain.arg2 = parcelFileDescriptor;
            this.mHandler.obtainMessage(3735552, obtain).sendToTarget();
        }
    }

    public void suppressAmbientDisplay(boolean z) {
        synchronized (this.mLock) {
            this.mHandler.obtainMessage(3604480, Boolean.valueOf(z)).sendToTarget();
        }
    }

    public void setNavigationBarLumaSamplingEnabled(int i, boolean z) {
        synchronized (this.mLock) {
            this.mHandler.obtainMessage(3866624, i, z ? 1 : 0).sendToTarget();
        }
    }

    public void passThroughShellCommand(String[] strArr, ParcelFileDescriptor parcelFileDescriptor) {
        final PrintWriter printWriter = new PrintWriter(new FileOutputStream(parcelFileDescriptor.getFileDescriptor()));
        final String[] strArr2 = strArr;
        final ParcelFileDescriptor parcelFileDescriptor2 = parcelFileDescriptor;
        new Thread("Sysui.passThroughShellCommand") {
            public void run() {
                try {
                    if (CommandQueue.this.mRegistry == null) {
                    } else {
                        CommandQueue.this.mRegistry.onShellCommand(printWriter, strArr2);
                        printWriter.flush();
                        try {
                            parcelFileDescriptor2.close();
                        } catch (Exception unused) {
                        }
                    }
                } finally {
                    printWriter.flush();
                    try {
                        parcelFileDescriptor2.close();
                    } catch (Exception unused2) {
                    }
                }
            }
        }.start();
    }

    public void runGcForTest() {
        GcUtils.runGcAndFinalizersSync();
    }

    public void addDesktopIcon(String str, int i, StatusBarIcon statusBarIcon, PendingIntent pendingIntent) {
        synchronized (this.mLock) {
            SomeArgs obtain = SomeArgs.obtain();
            obtain.arg1 = str;
            obtain.arg2 = statusBarIcon;
            obtain.arg3 = pendingIntent;
            obtain.argi1 = i;
            this.mHandler.obtainMessage(3997696, obtain).sendToTarget();
        }
    }

    public void removeDesktopIcon(String str, int i) {
        synchronized (this.mLock) {
            this.mHandler.obtainMessage(4063232, i, 0, str).sendToTarget();
        }
    }

    public void showToastForDisplay(int i, String str, IBinder iBinder, CharSequence charSequence, IBinder iBinder2, int i2, ITransientNotificationCallback iTransientNotificationCallback, int i3) {
        synchronized (this.mLock) {
            SomeArgs obtain = SomeArgs.obtain();
            obtain.arg1 = str;
            obtain.arg2 = iBinder;
            obtain.arg3 = charSequence;
            obtain.arg4 = iBinder2;
            obtain.arg5 = iTransientNotificationCallback;
            obtain.argi1 = i;
            obtain.argi2 = i2;
            obtain.argi3 = i3;
            this.mHandler.obtainMessage(4128768, obtain).sendToTarget();
        }
    }

    public void resetTaskBarAudoHideTimeout(int i) {
        synchronized (this.mLock) {
            this.mHandler.obtainMessage(4194304, Integer.valueOf(i)).sendToTarget();
        }
    }

    /* renamed from: com.android.systemui.statusbar.CommandQueue$H */
    private final class C1426H extends Handler {
        private C1426H(Looper looper) {
            super(looper);
        }

        /* JADX WARNING: Code restructure failed: missing block: B:10:0x005e, code lost:
            ((com.android.systemui.statusbar.CommandQueue.Callbacks) com.android.systemui.statusbar.CommandQueue.access$200(r0.this$0).get(r3)).dismissKeyboardShortcutsMenuForDisplay(r1.arg1);
            r3 = r3 + 1;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:113:0x03a5, code lost:
            if (r3 >= com.android.systemui.statusbar.CommandQueue.access$200(r0.this$0).size()) goto L_0x0a67;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:114:0x03a7, code lost:
            ((com.android.systemui.statusbar.CommandQueue.Callbacks) com.android.systemui.statusbar.CommandQueue.access$200(r0.this$0).get(r3)).showPinningEscapeToast();
            r3 = r3 + 1;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:116:0x03c3, code lost:
            if (r3 >= com.android.systemui.statusbar.CommandQueue.access$200(r0.this$0).size()) goto L_0x0a67;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:117:0x03c5, code lost:
            ((com.android.systemui.statusbar.CommandQueue.Callbacks) com.android.systemui.statusbar.CommandQueue.access$200(r0.this$0).get(r3)).showPinningEnterExitToast(((java.lang.Boolean) r1.obj).booleanValue());
            r3 = r3 + 1;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:119:0x03e9, code lost:
            if (r3 >= com.android.systemui.statusbar.CommandQueue.access$200(r0.this$0).size()) goto L_0x0a67;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:120:0x03eb, code lost:
            ((com.android.systemui.statusbar.CommandQueue.Callbacks) com.android.systemui.statusbar.CommandQueue.access$200(r0.this$0).get(r3)).showWirelessChargingAnimation(r1.arg1);
            r3 = r3 + 1;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:122:0x0409, code lost:
            if (r3 >= com.android.systemui.statusbar.CommandQueue.access$200(r0.this$0).size()) goto L_0x0a67;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:123:0x040b, code lost:
            ((com.android.systemui.statusbar.CommandQueue.Callbacks) com.android.systemui.statusbar.CommandQueue.access$200(r0.this$0).get(r3)).hideAuthenticationDialog();
            r3 = r3 + 1;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:135:0x0481, code lost:
            if (r3 >= com.android.systemui.statusbar.CommandQueue.access$200(r0.this$0).size()) goto L_0x0a67;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:136:0x0483, code lost:
            ((com.android.systemui.statusbar.CommandQueue.Callbacks) com.android.systemui.statusbar.CommandQueue.access$200(r0.this$0).get(r3)).onBiometricAuthenticated();
            r3 = r3 + 1;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:167:0x0590, code lost:
            if (r3 >= com.android.systemui.statusbar.CommandQueue.access$200(r0.this$0).size()) goto L_0x0a67;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:168:0x0592, code lost:
            ((com.android.systemui.statusbar.CommandQueue.Callbacks) com.android.systemui.statusbar.CommandQueue.access$200(r0.this$0).get(r3)).togglePanel();
            r3 = r3 + 1;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:170:0x05ae, code lost:
            if (r3 >= com.android.systemui.statusbar.CommandQueue.access$200(r0.this$0).size()) goto L_0x0a67;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:171:0x05b0, code lost:
            ((com.android.systemui.statusbar.CommandQueue.Callbacks) com.android.systemui.statusbar.CommandQueue.access$200(r0.this$0).get(r3)).handleShowGlobalActionsMenu();
            r3 = r3 + 1;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:173:0x05cc, code lost:
            if (r3 >= com.android.systemui.statusbar.CommandQueue.access$200(r0.this$0).size()) goto L_0x0a67;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:174:0x05ce, code lost:
            ((com.android.systemui.statusbar.CommandQueue.Callbacks) com.android.systemui.statusbar.CommandQueue.access$200(r0.this$0).get(r3)).handleSystemKey(r1.arg1);
            r3 = r3 + 1;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:176:0x05ec, code lost:
            if (r3 >= com.android.systemui.statusbar.CommandQueue.access$200(r0.this$0).size()) goto L_0x0a67;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:177:0x05ee, code lost:
            ((com.android.systemui.statusbar.CommandQueue.Callbacks) com.android.systemui.statusbar.CommandQueue.access$200(r0.this$0).get(r3)).dismissKeyboardShortcutsMenu();
            r3 = r3 + 1;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:179:0x060a, code lost:
            if (r3 >= com.android.systemui.statusbar.CommandQueue.access$200(r0.this$0).size()) goto L_0x0a67;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:180:0x060c, code lost:
            ((com.android.systemui.statusbar.CommandQueue.Callbacks) com.android.systemui.statusbar.CommandQueue.access$200(r0.this$0).get(r3)).appTransitionFinished(r1.arg1);
            r3 = r3 + 1;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:182:0x062a, code lost:
            if (r3 >= com.android.systemui.statusbar.CommandQueue.access$200(r0.this$0).size()) goto L_0x0a67;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:183:0x062c, code lost:
            ((com.android.systemui.statusbar.CommandQueue.Callbacks) com.android.systemui.statusbar.CommandQueue.access$200(r0.this$0).get(r3)).toggleSplitScreen();
            r3 = r3 + 1;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:185:0x0648, code lost:
            if (r3 >= com.android.systemui.statusbar.CommandQueue.access$200(r0.this$0).size()) goto L_0x0a67;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:186:0x064a, code lost:
            ((com.android.systemui.statusbar.CommandQueue.Callbacks) com.android.systemui.statusbar.CommandQueue.access$200(r0.this$0).get(r3)).clickTile((android.content.ComponentName) r1.obj);
            r3 = r3 + 1;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:188:0x066a, code lost:
            if (r3 >= com.android.systemui.statusbar.CommandQueue.access$200(r0.this$0).size()) goto L_0x0a67;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:189:0x066c, code lost:
            ((com.android.systemui.statusbar.CommandQueue.Callbacks) com.android.systemui.statusbar.CommandQueue.access$200(r0.this$0).get(r3)).remQsTile((android.content.ComponentName) r1.obj);
            r3 = r3 + 1;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:191:0x068c, code lost:
            if (r3 >= com.android.systemui.statusbar.CommandQueue.access$200(r0.this$0).size()) goto L_0x0a67;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:192:0x068e, code lost:
            ((com.android.systemui.statusbar.CommandQueue.Callbacks) com.android.systemui.statusbar.CommandQueue.access$200(r0.this$0).get(r3)).addQsTile((android.content.ComponentName) r1.obj);
            r3 = r3 + 1;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:194:0x06ae, code lost:
            if (r3 >= com.android.systemui.statusbar.CommandQueue.access$200(r0.this$0).size()) goto L_0x0a67;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:195:0x06b0, code lost:
            ((com.android.systemui.statusbar.CommandQueue.Callbacks) com.android.systemui.statusbar.CommandQueue.access$200(r0.this$0).get(r3)).showPictureInPictureMenu();
            r3 = r3 + 1;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:197:0x06cc, code lost:
            if (r3 >= com.android.systemui.statusbar.CommandQueue.access$200(r0.this$0).size()) goto L_0x0a67;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:198:0x06ce, code lost:
            ((com.android.systemui.statusbar.CommandQueue.Callbacks) com.android.systemui.statusbar.CommandQueue.access$200(r0.this$0).get(r3)).toggleKeyboardShortcutsMenu(r1.arg1);
            r3 = r3 + 1;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:211:0x0743, code lost:
            if (r3 >= com.android.systemui.statusbar.CommandQueue.access$200(r0.this$0).size()) goto L_0x0a67;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:212:0x0745, code lost:
            ((com.android.systemui.statusbar.CommandQueue.Callbacks) com.android.systemui.statusbar.CommandQueue.access$200(r0.this$0).get(r3)).startAssist((android.os.Bundle) r1.obj);
            r3 = r3 + 1;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:214:0x0765, code lost:
            if (r3 >= com.android.systemui.statusbar.CommandQueue.access$200(r0.this$0).size()) goto L_0x0a67;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:215:0x0767, code lost:
            ((com.android.systemui.statusbar.CommandQueue.Callbacks) com.android.systemui.statusbar.CommandQueue.access$200(r0.this$0).get(r3)).showAssistDisclosure();
            r3 = r3 + 1;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:225:0x07c0, code lost:
            if (r3 >= com.android.systemui.statusbar.CommandQueue.access$200(r0.this$0).size()) goto L_0x0a67;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:226:0x07c2, code lost:
            ((com.android.systemui.statusbar.CommandQueue.Callbacks) com.android.systemui.statusbar.CommandQueue.access$200(r0.this$0).get(r3)).appTransitionCancelled(r1.arg1);
            r3 = r3 + 1;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:236:0x0808, code lost:
            if (r3 >= com.android.systemui.statusbar.CommandQueue.access$200(r0.this$0).size()) goto L_0x0a67;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:237:0x080a, code lost:
            ((com.android.systemui.statusbar.CommandQueue.Callbacks) com.android.systemui.statusbar.CommandQueue.access$200(r0.this$0).get(r3)).showScreenPinningRequest(r1.arg1);
            r3 = r3 + 1;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:259:0x087b, code lost:
            if (r3 >= com.android.systemui.statusbar.CommandQueue.access$200(r0.this$0).size()) goto L_0x0a67;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:260:0x087d, code lost:
            ((com.android.systemui.statusbar.CommandQueue.Callbacks) com.android.systemui.statusbar.CommandQueue.access$200(r0.this$0).get(r3)).setWindowState(r1.arg1, r1.arg2, ((java.lang.Integer) r1.obj).intValue());
            r3 = r3 + 1;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:262:0x08a5, code lost:
            if (r3 >= com.android.systemui.statusbar.CommandQueue.access$200(r0.this$0).size()) goto L_0x0a67;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:263:0x08a7, code lost:
            ((com.android.systemui.statusbar.CommandQueue.Callbacks) com.android.systemui.statusbar.CommandQueue.access$200(r0.this$0).get(r3)).cancelPreloadRecentApps();
            r3 = r3 + 1;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:265:0x08c3, code lost:
            if (r3 >= com.android.systemui.statusbar.CommandQueue.access$200(r0.this$0).size()) goto L_0x0a67;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:266:0x08c5, code lost:
            ((com.android.systemui.statusbar.CommandQueue.Callbacks) com.android.systemui.statusbar.CommandQueue.access$200(r0.this$0).get(r3)).preloadRecentApps();
            r3 = r3 + 1;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:268:0x08e1, code lost:
            if (r3 >= com.android.systemui.statusbar.CommandQueue.access$200(r0.this$0).size()) goto L_0x0a67;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:269:0x08e3, code lost:
            ((com.android.systemui.statusbar.CommandQueue.Callbacks) com.android.systemui.statusbar.CommandQueue.access$200(r0.this$0).get(r3)).toggleRecentApps();
            r3 = r3 + 1;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:280:0x0923, code lost:
            if (r3 >= com.android.systemui.statusbar.CommandQueue.access$200(r0.this$0).size()) goto L_0x0a67;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:281:0x0925, code lost:
            ((com.android.systemui.statusbar.CommandQueue.Callbacks) com.android.systemui.statusbar.CommandQueue.access$200(r0.this$0).get(r3)).onDisplayReady(r1.arg1);
            r3 = r3 + 1;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:28:0x0115, code lost:
            if (r3 >= com.android.systemui.statusbar.CommandQueue.access$200(r0.this$0).size()) goto L_0x0a67;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:296:0x0985, code lost:
            if (r3 >= com.android.systemui.statusbar.CommandQueue.access$200(r0.this$0).size()) goto L_0x0a67;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:297:0x0987, code lost:
            ((com.android.systemui.statusbar.CommandQueue.Callbacks) com.android.systemui.statusbar.CommandQueue.access$200(r0.this$0).get(r3)).animateExpandSettingsPanel((java.lang.String) r1.obj);
            r3 = r3 + 1;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:29:0x0117, code lost:
            ((com.android.systemui.statusbar.CommandQueue.Callbacks) com.android.systemui.statusbar.CommandQueue.access$200(r0.this$0).get(r3)).removeDesktopIcon((java.lang.String) r1.obj, r1.arg1);
            r3 = r3 + 1;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:307:0x09cf, code lost:
            if (r3 >= com.android.systemui.statusbar.CommandQueue.access$200(r0.this$0).size()) goto L_0x0a67;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:308:0x09d1, code lost:
            ((com.android.systemui.statusbar.CommandQueue.Callbacks) com.android.systemui.statusbar.CommandQueue.access$200(r0.this$0).get(r3)).animateExpandNotificationsPanel();
            r3 = r3 + 1;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:35:0x0169, code lost:
            if (r3 >= com.android.systemui.statusbar.CommandQueue.access$200(r0.this$0).size()) goto L_0x0a67;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:36:0x016b, code lost:
            ((com.android.systemui.statusbar.CommandQueue.Callbacks) com.android.systemui.statusbar.CommandQueue.access$200(r0.this$0).get(r3)).setUdfpsHbmListener((android.hardware.fingerprint.IUdfpsHbmListener) r1.obj);
            r3 = r3 + 1;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:3:0x001a, code lost:
            if (r3 >= com.android.systemui.statusbar.CommandQueue.access$200(r0.this$0).size()) goto L_0x0a67;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:429:?, code lost:
            return;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:430:?, code lost:
            return;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:431:?, code lost:
            return;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:434:?, code lost:
            return;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:436:?, code lost:
            return;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:438:?, code lost:
            return;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:439:?, code lost:
            return;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:441:?, code lost:
            return;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:444:?, code lost:
            return;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:445:?, code lost:
            return;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:449:?, code lost:
            return;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:450:?, code lost:
            return;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:451:?, code lost:
            return;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:452:?, code lost:
            return;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:453:?, code lost:
            return;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:457:?, code lost:
            return;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:458:?, code lost:
            return;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:459:?, code lost:
            return;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:460:?, code lost:
            return;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:461:?, code lost:
            return;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:462:?, code lost:
            return;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:463:?, code lost:
            return;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:464:?, code lost:
            return;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:465:?, code lost:
            return;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:466:?, code lost:
            return;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:467:?, code lost:
            return;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:46:0x01b3, code lost:
            if (r3 >= com.android.systemui.statusbar.CommandQueue.access$200(r0.this$0).size()) goto L_0x0a67;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:470:?, code lost:
            return;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:471:?, code lost:
            return;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:473:?, code lost:
            return;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:475:?, code lost:
            return;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:478:?, code lost:
            return;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:479:?, code lost:
            return;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:47:0x01b5, code lost:
            ((com.android.systemui.statusbar.CommandQueue.Callbacks) com.android.systemui.statusbar.CommandQueue.access$200(r0.this$0).get(r3)).onEmergencyActionLaunchGestureDetected();
            r3 = r3 + 1;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:480:?, code lost:
            return;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:481:?, code lost:
            return;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:482:?, code lost:
            return;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:483:?, code lost:
            return;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:485:?, code lost:
            return;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:4:0x001c, code lost:
            ((com.android.systemui.statusbar.CommandQueue.Callbacks) com.android.systemui.statusbar.CommandQueue.access$200(r0.this$0).get(r3)).onStylusButtonEvent(r1.arg1);
            r3 = r3 + 1;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:6:0x003a, code lost:
            if (r3 >= com.android.systemui.statusbar.CommandQueue.access$200(r0.this$0).size()) goto L_0x0a67;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:73:0x021e, code lost:
            if (r3 >= com.android.systemui.statusbar.CommandQueue.access$200(r0.this$0).size()) goto L_0x0a67;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:74:0x0220, code lost:
            ((com.android.systemui.statusbar.CommandQueue.Callbacks) com.android.systemui.statusbar.CommandQueue.access$200(r0.this$0).get(r3)).requestWindowMagnificationConnection(((java.lang.Boolean) r1.obj).booleanValue());
            r3 = r3 + 1;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:7:0x003c, code lost:
            ((com.android.systemui.statusbar.CommandQueue.Callbacks) com.android.systemui.statusbar.CommandQueue.access$200(r0.this$0).get(r3)).toggleKeyboardShortcutsMenuForDisplay(r1.arg1, r1.arg2);
            r3 = r3 + 1;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:80:0x0266, code lost:
            if (r3 >= com.android.systemui.statusbar.CommandQueue.access$200(r0.this$0).size()) goto L_0x0a67;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:81:0x0268, code lost:
            ((com.android.systemui.statusbar.CommandQueue.Callbacks) com.android.systemui.statusbar.CommandQueue.access$200(r0.this$0).get(r3)).onTracingStateChanged(((java.lang.Boolean) r1.obj).booleanValue());
            r3 = r3 + 1;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:91:0x02f3, code lost:
            if (r3 >= com.android.systemui.statusbar.CommandQueue.access$200(r0.this$0).size()) goto L_0x0a67;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:92:0x02f5, code lost:
            ((com.android.systemui.statusbar.CommandQueue.Callbacks) com.android.systemui.statusbar.CommandQueue.access$200(r0.this$0).get(r3)).dismissInattentiveSleepWarning(((java.lang.Boolean) r1.obj).booleanValue());
            r3 = r3 + 1;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:94:0x0319, code lost:
            if (r3 >= com.android.systemui.statusbar.CommandQueue.access$200(r0.this$0).size()) goto L_0x0a67;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:95:0x031b, code lost:
            ((com.android.systemui.statusbar.CommandQueue.Callbacks) com.android.systemui.statusbar.CommandQueue.access$200(r0.this$0).get(r3)).showInattentiveSleepWarning();
            r3 = r3 + 1;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:9:0x005c, code lost:
            if (r3 >= com.android.systemui.statusbar.CommandQueue.access$200(r0.this$0).size()) goto L_0x0a67;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void handleMessage(android.os.Message r19) {
            /*
                r18 = this;
                r0 = r18
                r1 = r19
                int r2 = r1.what
                r3 = -65536(0xffffffffffff0000, float:NaN)
                r2 = r2 & r3
                r3 = 0
                r4 = 1
                switch(r2) {
                    case 65536: goto L_0x0a13;
                    case 131072: goto L_0x09e3;
                    case 196608: goto L_0x09c5;
                    case 262144: goto L_0x099d;
                    case 327680: goto L_0x097b;
                    case 393216: goto L_0x0939;
                    case 458752: goto L_0x0919;
                    case 524288: goto L_0x08f5;
                    case 589824: goto L_0x08d7;
                    case 655360: goto L_0x08b9;
                    case 720896: goto L_0x089b;
                    case 786432: goto L_0x0871;
                    case 851968: goto L_0x084b;
                    case 917504: goto L_0x081e;
                    case 1179648: goto L_0x07fe;
                    case 1245184: goto L_0x07d6;
                    case 1310720: goto L_0x07b6;
                    case 1376256: goto L_0x0779;
                    case 1441792: goto L_0x075b;
                    case 1507328: goto L_0x0739;
                    case 1572864: goto L_0x06e2;
                    case 1638400: goto L_0x06c2;
                    case 1703936: goto L_0x06a4;
                    case 1769472: goto L_0x0682;
                    case 1835008: goto L_0x0660;
                    case 1900544: goto L_0x063e;
                    case 1966080: goto L_0x0620;
                    case 2031616: goto L_0x0600;
                    case 2097152: goto L_0x05e2;
                    case 2162688: goto L_0x05c2;
                    case 2228224: goto L_0x05a4;
                    case 2293760: goto L_0x0586;
                    case 2359296: goto L_0x055c;
                    case 2424832: goto L_0x0536;
                    case 2490368: goto L_0x050e;
                    case 2555904: goto L_0x0495;
                    case 2621440: goto L_0x0477;
                    case 2686976: goto L_0x044a;
                    case 2752512: goto L_0x041d;
                    case 2818048: goto L_0x03ff;
                    case 2883584: goto L_0x03df;
                    case 2949120: goto L_0x03b9;
                    case 3014656: goto L_0x039b;
                    case 3080192: goto L_0x0375;
                    case 3145728: goto L_0x0351;
                    case 3211264: goto L_0x032d;
                    case 3276800: goto L_0x030f;
                    case 3342336: goto L_0x02e9;
                    case 3407872: goto L_0x02a8;
                    case 3473408: goto L_0x0282;
                    case 3538944: goto L_0x025c;
                    case 3604480: goto L_0x023a;
                    case 3670016: goto L_0x0214;
                    case 3735552: goto L_0x01c7;
                    case 3801088: goto L_0x01a9;
                    case 3866624: goto L_0x0181;
                    case 3932160: goto L_0x015f;
                    case 3997696: goto L_0x012f;
                    case 4063232: goto L_0x010b;
                    case 4128768: goto L_0x00bf;
                    case 4194304: goto L_0x0072;
                    case 4259840: goto L_0x0099;
                    case 4325376: goto L_0x0052;
                    case 4390912: goto L_0x0030;
                    case 4456448: goto L_0x0010;
                    default: goto L_0x000e;
                }
            L_0x000e:
                goto L_0x0a67
            L_0x0010:
                com.android.systemui.statusbar.CommandQueue r2 = com.android.systemui.statusbar.CommandQueue.this
                java.util.ArrayList r2 = r2.mCallbacks
                int r2 = r2.size()
                if (r3 >= r2) goto L_0x0a67
                com.android.systemui.statusbar.CommandQueue r2 = com.android.systemui.statusbar.CommandQueue.this
                java.util.ArrayList r2 = r2.mCallbacks
                java.lang.Object r2 = r2.get(r3)
                com.android.systemui.statusbar.CommandQueue$Callbacks r2 = (com.android.systemui.statusbar.CommandQueue.Callbacks) r2
                int r4 = r1.arg1
                r2.onStylusButtonEvent(r4)
                int r3 = r3 + 1
                goto L_0x0010
            L_0x0030:
                com.android.systemui.statusbar.CommandQueue r2 = com.android.systemui.statusbar.CommandQueue.this
                java.util.ArrayList r2 = r2.mCallbacks
                int r2 = r2.size()
                if (r3 >= r2) goto L_0x0a67
                com.android.systemui.statusbar.CommandQueue r2 = com.android.systemui.statusbar.CommandQueue.this
                java.util.ArrayList r2 = r2.mCallbacks
                java.lang.Object r2 = r2.get(r3)
                com.android.systemui.statusbar.CommandQueue$Callbacks r2 = (com.android.systemui.statusbar.CommandQueue.Callbacks) r2
                int r4 = r1.arg1
                int r5 = r1.arg2
                r2.toggleKeyboardShortcutsMenuForDisplay(r4, r5)
                int r3 = r3 + 1
                goto L_0x0030
            L_0x0052:
                com.android.systemui.statusbar.CommandQueue r2 = com.android.systemui.statusbar.CommandQueue.this
                java.util.ArrayList r2 = r2.mCallbacks
                int r2 = r2.size()
                if (r3 >= r2) goto L_0x0a67
                com.android.systemui.statusbar.CommandQueue r2 = com.android.systemui.statusbar.CommandQueue.this
                java.util.ArrayList r2 = r2.mCallbacks
                java.lang.Object r2 = r2.get(r3)
                com.android.systemui.statusbar.CommandQueue$Callbacks r2 = (com.android.systemui.statusbar.CommandQueue.Callbacks) r2
                int r4 = r1.arg1
                r2.dismissKeyboardShortcutsMenuForDisplay(r4)
                int r3 = r3 + 1
                goto L_0x0052
            L_0x0072:
                r2 = r3
            L_0x0073:
                com.android.systemui.statusbar.CommandQueue r5 = com.android.systemui.statusbar.CommandQueue.this
                java.util.ArrayList r5 = r5.mCallbacks
                int r5 = r5.size()
                if (r2 >= r5) goto L_0x0099
                com.android.systemui.statusbar.CommandQueue r5 = com.android.systemui.statusbar.CommandQueue.this
                java.util.ArrayList r5 = r5.mCallbacks
                java.lang.Object r5 = r5.get(r2)
                com.android.systemui.statusbar.CommandQueue$Callbacks r5 = (com.android.systemui.statusbar.CommandQueue.Callbacks) r5
                java.lang.Object r6 = r1.obj
                java.lang.Integer r6 = (java.lang.Integer) r6
                int r6 = r6.intValue()
                r5.resetTaskBarAudoHideTimeout(r6)
                int r2 = r2 + 1
                goto L_0x0073
            L_0x0099:
                r2 = r3
            L_0x009a:
                com.android.systemui.statusbar.CommandQueue r5 = com.android.systemui.statusbar.CommandQueue.this
                java.util.ArrayList r5 = r5.mCallbacks
                int r5 = r5.size()
                if (r2 >= r5) goto L_0x0a67
                com.android.systemui.statusbar.CommandQueue r5 = com.android.systemui.statusbar.CommandQueue.this
                java.util.ArrayList r5 = r5.mCallbacks
                java.lang.Object r5 = r5.get(r2)
                com.android.systemui.statusbar.CommandQueue$Callbacks r5 = (com.android.systemui.statusbar.CommandQueue.Callbacks) r5
                int r6 = r1.arg1
                if (r6 != r4) goto L_0x00b8
                r6 = r4
                goto L_0x00b9
            L_0x00b8:
                r6 = r3
            L_0x00b9:
                r5.setHeadsUpVisibleForCli(r6)
                int r2 = r2 + 1
                goto L_0x009a
            L_0x00bf:
                java.lang.Object r1 = r1.obj
                com.android.internal.os.SomeArgs r1 = (com.android.internal.os.SomeArgs) r1
                java.lang.Object r2 = r1.arg1
                java.lang.String r2 = (java.lang.String) r2
                java.lang.Object r3 = r1.arg2
                r12 = r3
                android.os.IBinder r12 = (android.os.IBinder) r12
                java.lang.Object r3 = r1.arg3
                r13 = r3
                java.lang.CharSequence r13 = (java.lang.CharSequence) r13
                java.lang.Object r3 = r1.arg4
                r14 = r3
                android.os.IBinder r14 = (android.os.IBinder) r14
                java.lang.Object r3 = r1.arg5
                r15 = r3
                android.app.ITransientNotificationCallback r15 = (android.app.ITransientNotificationCallback) r15
                int r11 = r1.argi1
                int r10 = r1.argi2
                int r1 = r1.argi3
                com.android.systemui.statusbar.CommandQueue r0 = com.android.systemui.statusbar.CommandQueue.this
                java.util.ArrayList r0 = r0.mCallbacks
                java.util.Iterator r0 = r0.iterator()
            L_0x00eb:
                boolean r3 = r0.hasNext()
                if (r3 == 0) goto L_0x0a67
                java.lang.Object r3 = r0.next()
                com.android.systemui.statusbar.CommandQueue$Callbacks r3 = (com.android.systemui.statusbar.CommandQueue.Callbacks) r3
                r4 = r11
                r5 = r2
                r6 = r12
                r7 = r13
                r8 = r14
                r9 = r10
                r16 = r10
                r10 = r15
                r17 = r11
                r11 = r1
                r3.showToastForDisplay(r4, r5, r6, r7, r8, r9, r10, r11)
                r10 = r16
                r11 = r17
                goto L_0x00eb
            L_0x010b:
                com.android.systemui.statusbar.CommandQueue r2 = com.android.systemui.statusbar.CommandQueue.this
                java.util.ArrayList r2 = r2.mCallbacks
                int r2 = r2.size()
                if (r3 >= r2) goto L_0x0a67
                com.android.systemui.statusbar.CommandQueue r2 = com.android.systemui.statusbar.CommandQueue.this
                java.util.ArrayList r2 = r2.mCallbacks
                java.lang.Object r2 = r2.get(r3)
                com.android.systemui.statusbar.CommandQueue$Callbacks r2 = (com.android.systemui.statusbar.CommandQueue.Callbacks) r2
                java.lang.Object r4 = r1.obj
                java.lang.String r4 = (java.lang.String) r4
                int r5 = r1.arg1
                r2.removeDesktopIcon(r4, r5)
                int r3 = r3 + 1
                goto L_0x010b
            L_0x012f:
                java.lang.Object r1 = r1.obj
                com.android.internal.os.SomeArgs r1 = (com.android.internal.os.SomeArgs) r1
                java.lang.Object r2 = r1.arg1
                java.lang.String r2 = (java.lang.String) r2
                java.lang.Object r4 = r1.arg2
                com.android.internal.statusbar.StatusBarIcon r4 = (com.android.internal.statusbar.StatusBarIcon) r4
                java.lang.Object r5 = r1.arg3
                android.app.PendingIntent r5 = (android.app.PendingIntent) r5
                int r1 = r1.argi1
            L_0x0141:
                com.android.systemui.statusbar.CommandQueue r6 = com.android.systemui.statusbar.CommandQueue.this
                java.util.ArrayList r6 = r6.mCallbacks
                int r6 = r6.size()
                if (r3 >= r6) goto L_0x0a67
                com.android.systemui.statusbar.CommandQueue r6 = com.android.systemui.statusbar.CommandQueue.this
                java.util.ArrayList r6 = r6.mCallbacks
                java.lang.Object r6 = r6.get(r3)
                com.android.systemui.statusbar.CommandQueue$Callbacks r6 = (com.android.systemui.statusbar.CommandQueue.Callbacks) r6
                r6.addDesktopIcon(r2, r1, r4, r5)
                int r3 = r3 + 1
                goto L_0x0141
            L_0x015f:
                com.android.systemui.statusbar.CommandQueue r2 = com.android.systemui.statusbar.CommandQueue.this
                java.util.ArrayList r2 = r2.mCallbacks
                int r2 = r2.size()
                if (r3 >= r2) goto L_0x0a67
                com.android.systemui.statusbar.CommandQueue r2 = com.android.systemui.statusbar.CommandQueue.this
                java.util.ArrayList r2 = r2.mCallbacks
                java.lang.Object r2 = r2.get(r3)
                com.android.systemui.statusbar.CommandQueue$Callbacks r2 = (com.android.systemui.statusbar.CommandQueue.Callbacks) r2
                java.lang.Object r4 = r1.obj
                android.hardware.fingerprint.IUdfpsHbmListener r4 = (android.hardware.fingerprint.IUdfpsHbmListener) r4
                r2.setUdfpsHbmListener(r4)
                int r3 = r3 + 1
                goto L_0x015f
            L_0x0181:
                r2 = r3
            L_0x0182:
                com.android.systemui.statusbar.CommandQueue r5 = com.android.systemui.statusbar.CommandQueue.this
                java.util.ArrayList r5 = r5.mCallbacks
                int r5 = r5.size()
                if (r2 >= r5) goto L_0x0a67
                com.android.systemui.statusbar.CommandQueue r5 = com.android.systemui.statusbar.CommandQueue.this
                java.util.ArrayList r5 = r5.mCallbacks
                java.lang.Object r5 = r5.get(r2)
                com.android.systemui.statusbar.CommandQueue$Callbacks r5 = (com.android.systemui.statusbar.CommandQueue.Callbacks) r5
                int r6 = r1.arg1
                int r7 = r1.arg2
                if (r7 == 0) goto L_0x01a2
                r7 = r4
                goto L_0x01a3
            L_0x01a2:
                r7 = r3
            L_0x01a3:
                r5.setNavigationBarLumaSamplingEnabled(r6, r7)
                int r2 = r2 + 1
                goto L_0x0182
            L_0x01a9:
                com.android.systemui.statusbar.CommandQueue r1 = com.android.systemui.statusbar.CommandQueue.this
                java.util.ArrayList r1 = r1.mCallbacks
                int r1 = r1.size()
                if (r3 >= r1) goto L_0x0a67
                com.android.systemui.statusbar.CommandQueue r1 = com.android.systemui.statusbar.CommandQueue.this
                java.util.ArrayList r1 = r1.mCallbacks
                java.lang.Object r1 = r1.get(r3)
                com.android.systemui.statusbar.CommandQueue$Callbacks r1 = (com.android.systemui.statusbar.CommandQueue.Callbacks) r1
                r1.onEmergencyActionLaunchGestureDetected()
                int r3 = r3 + 1
                goto L_0x01a9
            L_0x01c7:
                java.lang.Object r1 = r1.obj
                com.android.internal.os.SomeArgs r1 = (com.android.internal.os.SomeArgs) r1
                java.lang.Object r2 = r1.arg2     // Catch:{ IOException -> 0x0205 }
                android.os.ParcelFileDescriptor r2 = (android.os.ParcelFileDescriptor) r2     // Catch:{ IOException -> 0x0205 }
            L_0x01cf:
                com.android.systemui.statusbar.CommandQueue r4 = com.android.systemui.statusbar.CommandQueue.this     // Catch:{ all -> 0x01f7 }
                java.util.ArrayList r4 = r4.mCallbacks     // Catch:{ all -> 0x01f7 }
                int r4 = r4.size()     // Catch:{ all -> 0x01f7 }
                if (r3 >= r4) goto L_0x01f1
                com.android.systemui.statusbar.CommandQueue r4 = com.android.systemui.statusbar.CommandQueue.this     // Catch:{ all -> 0x01f7 }
                java.util.ArrayList r4 = r4.mCallbacks     // Catch:{ all -> 0x01f7 }
                java.lang.Object r4 = r4.get(r3)     // Catch:{ all -> 0x01f7 }
                com.android.systemui.statusbar.CommandQueue$Callbacks r4 = (com.android.systemui.statusbar.CommandQueue.Callbacks) r4     // Catch:{ all -> 0x01f7 }
                java.lang.Object r5 = r1.arg1     // Catch:{ all -> 0x01f7 }
                java.lang.String[] r5 = (java.lang.String[]) r5     // Catch:{ all -> 0x01f7 }
                r4.handleWindowManagerLoggingCommand(r5, r2)     // Catch:{ all -> 0x01f7 }
                int r3 = r3 + 1
                goto L_0x01cf
            L_0x01f1:
                if (r2 == 0) goto L_0x020f
                r2.close()     // Catch:{ IOException -> 0x0205 }
                goto L_0x020f
            L_0x01f7:
                r0 = move-exception
                r3 = r0
                if (r2 == 0) goto L_0x0204
                r2.close()     // Catch:{ all -> 0x01ff }
                goto L_0x0204
            L_0x01ff:
                r0 = move-exception
                r2 = r0
                r3.addSuppressed(r2)     // Catch:{ IOException -> 0x0205 }
            L_0x0204:
                throw r3     // Catch:{ IOException -> 0x0205 }
            L_0x0205:
                r0 = move-exception
                java.lang.String r2 = com.android.systemui.statusbar.CommandQueue.TAG
                java.lang.String r3 = "Failed to handle logging command"
                android.util.Log.e(r2, r3, r0)
            L_0x020f:
                r1.recycle()
                goto L_0x0a67
            L_0x0214:
                com.android.systemui.statusbar.CommandQueue r2 = com.android.systemui.statusbar.CommandQueue.this
                java.util.ArrayList r2 = r2.mCallbacks
                int r2 = r2.size()
                if (r3 >= r2) goto L_0x0a67
                com.android.systemui.statusbar.CommandQueue r2 = com.android.systemui.statusbar.CommandQueue.this
                java.util.ArrayList r2 = r2.mCallbacks
                java.lang.Object r2 = r2.get(r3)
                com.android.systemui.statusbar.CommandQueue$Callbacks r2 = (com.android.systemui.statusbar.CommandQueue.Callbacks) r2
                java.lang.Object r4 = r1.obj
                java.lang.Boolean r4 = (java.lang.Boolean) r4
                boolean r4 = r4.booleanValue()
                r2.requestWindowMagnificationConnection(r4)
                int r3 = r3 + 1
                goto L_0x0214
            L_0x023a:
                com.android.systemui.statusbar.CommandQueue r0 = com.android.systemui.statusbar.CommandQueue.this
                java.util.ArrayList r0 = r0.mCallbacks
                java.util.Iterator r0 = r0.iterator()
            L_0x0244:
                boolean r2 = r0.hasNext()
                if (r2 == 0) goto L_0x0a67
                java.lang.Object r2 = r0.next()
                com.android.systemui.statusbar.CommandQueue$Callbacks r2 = (com.android.systemui.statusbar.CommandQueue.Callbacks) r2
                java.lang.Object r3 = r1.obj
                java.lang.Boolean r3 = (java.lang.Boolean) r3
                boolean r3 = r3.booleanValue()
                r2.suppressAmbientDisplay(r3)
                goto L_0x0244
            L_0x025c:
                com.android.systemui.statusbar.CommandQueue r2 = com.android.systemui.statusbar.CommandQueue.this
                java.util.ArrayList r2 = r2.mCallbacks
                int r2 = r2.size()
                if (r3 >= r2) goto L_0x0a67
                com.android.systemui.statusbar.CommandQueue r2 = com.android.systemui.statusbar.CommandQueue.this
                java.util.ArrayList r2 = r2.mCallbacks
                java.lang.Object r2 = r2.get(r3)
                com.android.systemui.statusbar.CommandQueue$Callbacks r2 = (com.android.systemui.statusbar.CommandQueue.Callbacks) r2
                java.lang.Object r4 = r1.obj
                java.lang.Boolean r4 = (java.lang.Boolean) r4
                boolean r4 = r4.booleanValue()
                r2.onTracingStateChanged(r4)
                int r3 = r3 + 1
                goto L_0x025c
            L_0x0282:
                java.lang.Object r1 = r1.obj
                com.android.internal.os.SomeArgs r1 = (com.android.internal.os.SomeArgs) r1
                java.lang.Object r2 = r1.arg1
                java.lang.String r2 = (java.lang.String) r2
                java.lang.Object r1 = r1.arg2
                android.os.IBinder r1 = (android.os.IBinder) r1
                com.android.systemui.statusbar.CommandQueue r0 = com.android.systemui.statusbar.CommandQueue.this
                java.util.ArrayList r0 = r0.mCallbacks
                java.util.Iterator r0 = r0.iterator()
            L_0x0298:
                boolean r3 = r0.hasNext()
                if (r3 == 0) goto L_0x0a67
                java.lang.Object r3 = r0.next()
                com.android.systemui.statusbar.CommandQueue$Callbacks r3 = (com.android.systemui.statusbar.CommandQueue.Callbacks) r3
                r3.hideToast(r2, r1)
                goto L_0x0298
            L_0x02a8:
                java.lang.Object r1 = r1.obj
                com.android.internal.os.SomeArgs r1 = (com.android.internal.os.SomeArgs) r1
                java.lang.Object r2 = r1.arg1
                java.lang.String r2 = (java.lang.String) r2
                java.lang.Object r3 = r1.arg2
                r11 = r3
                android.os.IBinder r11 = (android.os.IBinder) r11
                java.lang.Object r3 = r1.arg3
                r12 = r3
                java.lang.CharSequence r12 = (java.lang.CharSequence) r12
                java.lang.Object r3 = r1.arg4
                r13 = r3
                android.os.IBinder r13 = (android.os.IBinder) r13
                java.lang.Object r3 = r1.arg5
                r14 = r3
                android.app.ITransientNotificationCallback r14 = (android.app.ITransientNotificationCallback) r14
                int r15 = r1.argi1
                int r1 = r1.argi2
                com.android.systemui.statusbar.CommandQueue r0 = com.android.systemui.statusbar.CommandQueue.this
                java.util.ArrayList r0 = r0.mCallbacks
                java.util.Iterator r0 = r0.iterator()
            L_0x02d2:
                boolean r3 = r0.hasNext()
                if (r3 == 0) goto L_0x0a67
                java.lang.Object r3 = r0.next()
                com.android.systemui.statusbar.CommandQueue$Callbacks r3 = (com.android.systemui.statusbar.CommandQueue.Callbacks) r3
                r4 = r15
                r5 = r2
                r6 = r11
                r7 = r12
                r8 = r13
                r9 = r1
                r10 = r14
                r3.showToast(r4, r5, r6, r7, r8, r9, r10)
                goto L_0x02d2
            L_0x02e9:
                com.android.systemui.statusbar.CommandQueue r2 = com.android.systemui.statusbar.CommandQueue.this
                java.util.ArrayList r2 = r2.mCallbacks
                int r2 = r2.size()
                if (r3 >= r2) goto L_0x0a67
                com.android.systemui.statusbar.CommandQueue r2 = com.android.systemui.statusbar.CommandQueue.this
                java.util.ArrayList r2 = r2.mCallbacks
                java.lang.Object r2 = r2.get(r3)
                com.android.systemui.statusbar.CommandQueue$Callbacks r2 = (com.android.systemui.statusbar.CommandQueue.Callbacks) r2
                java.lang.Object r4 = r1.obj
                java.lang.Boolean r4 = (java.lang.Boolean) r4
                boolean r4 = r4.booleanValue()
                r2.dismissInattentiveSleepWarning(r4)
                int r3 = r3 + 1
                goto L_0x02e9
            L_0x030f:
                com.android.systemui.statusbar.CommandQueue r1 = com.android.systemui.statusbar.CommandQueue.this
                java.util.ArrayList r1 = r1.mCallbacks
                int r1 = r1.size()
                if (r3 >= r1) goto L_0x0a67
                com.android.systemui.statusbar.CommandQueue r1 = com.android.systemui.statusbar.CommandQueue.this
                java.util.ArrayList r1 = r1.mCallbacks
                java.lang.Object r1 = r1.get(r3)
                com.android.systemui.statusbar.CommandQueue$Callbacks r1 = (com.android.systemui.statusbar.CommandQueue.Callbacks) r1
                r1.showInattentiveSleepWarning()
                int r3 = r3 + 1
                goto L_0x030f
            L_0x032d:
                int r2 = r1.arg1
                java.lang.Object r1 = r1.obj
                int[] r1 = (int[]) r1
            L_0x0333:
                com.android.systemui.statusbar.CommandQueue r4 = com.android.systemui.statusbar.CommandQueue.this
                java.util.ArrayList r4 = r4.mCallbacks
                int r4 = r4.size()
                if (r3 >= r4) goto L_0x0a67
                com.android.systemui.statusbar.CommandQueue r4 = com.android.systemui.statusbar.CommandQueue.this
                java.util.ArrayList r4 = r4.mCallbacks
                java.lang.Object r4 = r4.get(r3)
                com.android.systemui.statusbar.CommandQueue$Callbacks r4 = (com.android.systemui.statusbar.CommandQueue.Callbacks) r4
                r4.abortTransient(r2, r1)
                int r3 = r3 + 1
                goto L_0x0333
            L_0x0351:
                int r2 = r1.arg1
                java.lang.Object r1 = r1.obj
                int[] r1 = (int[]) r1
            L_0x0357:
                com.android.systemui.statusbar.CommandQueue r4 = com.android.systemui.statusbar.CommandQueue.this
                java.util.ArrayList r4 = r4.mCallbacks
                int r4 = r4.size()
                if (r3 >= r4) goto L_0x0a67
                com.android.systemui.statusbar.CommandQueue r4 = com.android.systemui.statusbar.CommandQueue.this
                java.util.ArrayList r4 = r4.mCallbacks
                java.lang.Object r4 = r4.get(r3)
                com.android.systemui.statusbar.CommandQueue$Callbacks r4 = (com.android.systemui.statusbar.CommandQueue.Callbacks) r4
                r4.showTransient(r2, r1)
                int r3 = r3 + 1
                goto L_0x0357
            L_0x0375:
                r2 = r3
            L_0x0376:
                com.android.systemui.statusbar.CommandQueue r5 = com.android.systemui.statusbar.CommandQueue.this
                java.util.ArrayList r5 = r5.mCallbacks
                int r5 = r5.size()
                if (r2 >= r5) goto L_0x0a67
                com.android.systemui.statusbar.CommandQueue r5 = com.android.systemui.statusbar.CommandQueue.this
                java.util.ArrayList r5 = r5.mCallbacks
                java.lang.Object r5 = r5.get(r2)
                com.android.systemui.statusbar.CommandQueue$Callbacks r5 = (com.android.systemui.statusbar.CommandQueue.Callbacks) r5
                int r6 = r1.arg1
                if (r6 <= 0) goto L_0x0394
                r6 = r4
                goto L_0x0395
            L_0x0394:
                r6 = r3
            L_0x0395:
                r5.onRecentsAnimationStateChanged(r6)
                int r2 = r2 + 1
                goto L_0x0376
            L_0x039b:
                com.android.systemui.statusbar.CommandQueue r1 = com.android.systemui.statusbar.CommandQueue.this
                java.util.ArrayList r1 = r1.mCallbacks
                int r1 = r1.size()
                if (r3 >= r1) goto L_0x0a67
                com.android.systemui.statusbar.CommandQueue r1 = com.android.systemui.statusbar.CommandQueue.this
                java.util.ArrayList r1 = r1.mCallbacks
                java.lang.Object r1 = r1.get(r3)
                com.android.systemui.statusbar.CommandQueue$Callbacks r1 = (com.android.systemui.statusbar.CommandQueue.Callbacks) r1
                r1.showPinningEscapeToast()
                int r3 = r3 + 1
                goto L_0x039b
            L_0x03b9:
                com.android.systemui.statusbar.CommandQueue r2 = com.android.systemui.statusbar.CommandQueue.this
                java.util.ArrayList r2 = r2.mCallbacks
                int r2 = r2.size()
                if (r3 >= r2) goto L_0x0a67
                com.android.systemui.statusbar.CommandQueue r2 = com.android.systemui.statusbar.CommandQueue.this
                java.util.ArrayList r2 = r2.mCallbacks
                java.lang.Object r2 = r2.get(r3)
                com.android.systemui.statusbar.CommandQueue$Callbacks r2 = (com.android.systemui.statusbar.CommandQueue.Callbacks) r2
                java.lang.Object r4 = r1.obj
                java.lang.Boolean r4 = (java.lang.Boolean) r4
                boolean r4 = r4.booleanValue()
                r2.showPinningEnterExitToast(r4)
                int r3 = r3 + 1
                goto L_0x03b9
            L_0x03df:
                com.android.systemui.statusbar.CommandQueue r2 = com.android.systemui.statusbar.CommandQueue.this
                java.util.ArrayList r2 = r2.mCallbacks
                int r2 = r2.size()
                if (r3 >= r2) goto L_0x0a67
                com.android.systemui.statusbar.CommandQueue r2 = com.android.systemui.statusbar.CommandQueue.this
                java.util.ArrayList r2 = r2.mCallbacks
                java.lang.Object r2 = r2.get(r3)
                com.android.systemui.statusbar.CommandQueue$Callbacks r2 = (com.android.systemui.statusbar.CommandQueue.Callbacks) r2
                int r4 = r1.arg1
                r2.showWirelessChargingAnimation(r4)
                int r3 = r3 + 1
                goto L_0x03df
            L_0x03ff:
                com.android.systemui.statusbar.CommandQueue r1 = com.android.systemui.statusbar.CommandQueue.this
                java.util.ArrayList r1 = r1.mCallbacks
                int r1 = r1.size()
                if (r3 >= r1) goto L_0x0a67
                com.android.systemui.statusbar.CommandQueue r1 = com.android.systemui.statusbar.CommandQueue.this
                java.util.ArrayList r1 = r1.mCallbacks
                java.lang.Object r1 = r1.get(r3)
                com.android.systemui.statusbar.CommandQueue$Callbacks r1 = (com.android.systemui.statusbar.CommandQueue.Callbacks) r1
                r1.hideAuthenticationDialog()
                int r3 = r3 + 1
                goto L_0x03ff
            L_0x041d:
                java.lang.Object r1 = r1.obj
                com.android.internal.os.SomeArgs r1 = (com.android.internal.os.SomeArgs) r1
            L_0x0421:
                com.android.systemui.statusbar.CommandQueue r2 = com.android.systemui.statusbar.CommandQueue.this
                java.util.ArrayList r2 = r2.mCallbacks
                int r2 = r2.size()
                if (r3 >= r2) goto L_0x0445
                com.android.systemui.statusbar.CommandQueue r2 = com.android.systemui.statusbar.CommandQueue.this
                java.util.ArrayList r2 = r2.mCallbacks
                java.lang.Object r2 = r2.get(r3)
                com.android.systemui.statusbar.CommandQueue$Callbacks r2 = (com.android.systemui.statusbar.CommandQueue.Callbacks) r2
                int r4 = r1.argi1
                int r5 = r1.argi2
                int r6 = r1.argi3
                r2.onBiometricError(r4, r5, r6)
                int r3 = r3 + 1
                goto L_0x0421
            L_0x0445:
                r1.recycle()
                goto L_0x0a67
            L_0x044a:
                java.lang.Object r1 = r1.obj
                com.android.internal.os.SomeArgs r1 = (com.android.internal.os.SomeArgs) r1
            L_0x044e:
                com.android.systemui.statusbar.CommandQueue r2 = com.android.systemui.statusbar.CommandQueue.this
                java.util.ArrayList r2 = r2.mCallbacks
                int r2 = r2.size()
                if (r3 >= r2) goto L_0x0472
                com.android.systemui.statusbar.CommandQueue r2 = com.android.systemui.statusbar.CommandQueue.this
                java.util.ArrayList r2 = r2.mCallbacks
                java.lang.Object r2 = r2.get(r3)
                com.android.systemui.statusbar.CommandQueue$Callbacks r2 = (com.android.systemui.statusbar.CommandQueue.Callbacks) r2
                int r4 = r1.argi1
                java.lang.Object r5 = r1.arg1
                java.lang.String r5 = (java.lang.String) r5
                r2.onBiometricHelp(r4, r5)
                int r3 = r3 + 1
                goto L_0x044e
            L_0x0472:
                r1.recycle()
                goto L_0x0a67
            L_0x0477:
                com.android.systemui.statusbar.CommandQueue r1 = com.android.systemui.statusbar.CommandQueue.this
                java.util.ArrayList r1 = r1.mCallbacks
                int r1 = r1.size()
                if (r3 >= r1) goto L_0x0a67
                com.android.systemui.statusbar.CommandQueue r1 = com.android.systemui.statusbar.CommandQueue.this
                java.util.ArrayList r1 = r1.mCallbacks
                java.lang.Object r1 = r1.get(r3)
                com.android.systemui.statusbar.CommandQueue$Callbacks r1 = (com.android.systemui.statusbar.CommandQueue.Callbacks) r1
                r1.onBiometricAuthenticated()
                int r3 = r3 + 1
                goto L_0x0477
            L_0x0495:
                com.android.systemui.statusbar.CommandQueue r2 = com.android.systemui.statusbar.CommandQueue.this
                android.os.Handler r2 = r2.mHandler
                r4 = 2752512(0x2a0000, float:3.857091E-39)
                r2.removeMessages(r4)
                com.android.systemui.statusbar.CommandQueue r2 = com.android.systemui.statusbar.CommandQueue.this
                android.os.Handler r2 = r2.mHandler
                r4 = 2686976(0x290000, float:3.765255E-39)
                r2.removeMessages(r4)
                com.android.systemui.statusbar.CommandQueue r2 = com.android.systemui.statusbar.CommandQueue.this
                android.os.Handler r2 = r2.mHandler
                r4 = 2621440(0x280000, float:3.67342E-39)
                r2.removeMessages(r4)
                java.lang.Object r1 = r1.obj
                com.android.internal.os.SomeArgs r1 = (com.android.internal.os.SomeArgs) r1
            L_0x04ba:
                com.android.systemui.statusbar.CommandQueue r2 = com.android.systemui.statusbar.CommandQueue.this
                java.util.ArrayList r2 = r2.mCallbacks
                int r2 = r2.size()
                if (r3 >= r2) goto L_0x0509
                com.android.systemui.statusbar.CommandQueue r2 = com.android.systemui.statusbar.CommandQueue.this
                java.util.ArrayList r2 = r2.mCallbacks
                java.lang.Object r2 = r2.get(r3)
                r4 = r2
                com.android.systemui.statusbar.CommandQueue$Callbacks r4 = (com.android.systemui.statusbar.CommandQueue.Callbacks) r4
                java.lang.Object r2 = r1.arg1
                r5 = r2
                android.hardware.biometrics.PromptInfo r5 = (android.hardware.biometrics.PromptInfo) r5
                java.lang.Object r2 = r1.arg2
                r6 = r2
                android.hardware.biometrics.IBiometricSysuiReceiver r6 = (android.hardware.biometrics.IBiometricSysuiReceiver) r6
                java.lang.Object r2 = r1.arg3
                r7 = r2
                int[] r7 = (int[]) r7
                java.lang.Object r2 = r1.arg4
                java.lang.Boolean r2 = (java.lang.Boolean) r2
                boolean r8 = r2.booleanValue()
                java.lang.Object r2 = r1.arg5
                java.lang.Boolean r2 = (java.lang.Boolean) r2
                boolean r9 = r2.booleanValue()
                int r10 = r1.argi1
                java.lang.Object r2 = r1.arg6
                r11 = r2
                java.lang.String r11 = (java.lang.String) r11
                java.lang.Object r2 = r1.arg7
                java.lang.Long r2 = (java.lang.Long) r2
                long r12 = r2.longValue()
                int r14 = r1.argi2
                r4.showAuthenticationDialog(r5, r6, r7, r8, r9, r10, r11, r12, r14)
                int r3 = r3 + 1
                goto L_0x04ba
            L_0x0509:
                r1.recycle()
                goto L_0x0a67
            L_0x050e:
                r2 = r3
            L_0x050f:
                com.android.systemui.statusbar.CommandQueue r5 = com.android.systemui.statusbar.CommandQueue.this
                java.util.ArrayList r5 = r5.mCallbacks
                int r5 = r5.size()
                if (r2 >= r5) goto L_0x0a67
                com.android.systemui.statusbar.CommandQueue r5 = com.android.systemui.statusbar.CommandQueue.this
                java.util.ArrayList r5 = r5.mCallbacks
                java.lang.Object r5 = r5.get(r2)
                com.android.systemui.statusbar.CommandQueue$Callbacks r5 = (com.android.systemui.statusbar.CommandQueue.Callbacks) r5
                int r6 = r1.arg1
                int r7 = r1.arg2
                if (r7 == 0) goto L_0x052f
                r7 = r4
                goto L_0x0530
            L_0x052f:
                r7 = r3
            L_0x0530:
                r5.onRotationProposal(r6, r7)
                int r2 = r2 + 1
                goto L_0x050f
            L_0x0536:
                r2 = r3
            L_0x0537:
                com.android.systemui.statusbar.CommandQueue r5 = com.android.systemui.statusbar.CommandQueue.this
                java.util.ArrayList r5 = r5.mCallbacks
                int r5 = r5.size()
                if (r2 >= r5) goto L_0x0a67
                com.android.systemui.statusbar.CommandQueue r5 = com.android.systemui.statusbar.CommandQueue.this
                java.util.ArrayList r5 = r5.mCallbacks
                java.lang.Object r5 = r5.get(r2)
                com.android.systemui.statusbar.CommandQueue$Callbacks r5 = (com.android.systemui.statusbar.CommandQueue.Callbacks) r5
                int r6 = r1.arg1
                if (r6 == 0) goto L_0x0555
                r6 = r4
                goto L_0x0556
            L_0x0555:
                r6 = r3
            L_0x0556:
                r5.setTopAppHidesStatusBar(r6)
                int r2 = r2 + 1
                goto L_0x0537
            L_0x055c:
                r2 = r3
            L_0x055d:
                com.android.systemui.statusbar.CommandQueue r5 = com.android.systemui.statusbar.CommandQueue.this
                java.util.ArrayList r5 = r5.mCallbacks
                int r5 = r5.size()
                if (r2 >= r5) goto L_0x0a67
                com.android.systemui.statusbar.CommandQueue r5 = com.android.systemui.statusbar.CommandQueue.this
                java.util.ArrayList r5 = r5.mCallbacks
                java.lang.Object r5 = r5.get(r2)
                com.android.systemui.statusbar.CommandQueue$Callbacks r5 = (com.android.systemui.statusbar.CommandQueue.Callbacks) r5
                int r6 = r1.arg1
                if (r6 == 0) goto L_0x057b
                r6 = r4
                goto L_0x057c
            L_0x057b:
                r6 = r3
            L_0x057c:
                java.lang.Object r7 = r1.obj
                java.lang.String r7 = (java.lang.String) r7
                r5.handleShowShutdownUi(r6, r7)
                int r2 = r2 + 1
                goto L_0x055d
            L_0x0586:
                com.android.systemui.statusbar.CommandQueue r1 = com.android.systemui.statusbar.CommandQueue.this
                java.util.ArrayList r1 = r1.mCallbacks
                int r1 = r1.size()
                if (r3 >= r1) goto L_0x0a67
                com.android.systemui.statusbar.CommandQueue r1 = com.android.systemui.statusbar.CommandQueue.this
                java.util.ArrayList r1 = r1.mCallbacks
                java.lang.Object r1 = r1.get(r3)
                com.android.systemui.statusbar.CommandQueue$Callbacks r1 = (com.android.systemui.statusbar.CommandQueue.Callbacks) r1
                r1.togglePanel()
                int r3 = r3 + 1
                goto L_0x0586
            L_0x05a4:
                com.android.systemui.statusbar.CommandQueue r1 = com.android.systemui.statusbar.CommandQueue.this
                java.util.ArrayList r1 = r1.mCallbacks
                int r1 = r1.size()
                if (r3 >= r1) goto L_0x0a67
                com.android.systemui.statusbar.CommandQueue r1 = com.android.systemui.statusbar.CommandQueue.this
                java.util.ArrayList r1 = r1.mCallbacks
                java.lang.Object r1 = r1.get(r3)
                com.android.systemui.statusbar.CommandQueue$Callbacks r1 = (com.android.systemui.statusbar.CommandQueue.Callbacks) r1
                r1.handleShowGlobalActionsMenu()
                int r3 = r3 + 1
                goto L_0x05a4
            L_0x05c2:
                com.android.systemui.statusbar.CommandQueue r2 = com.android.systemui.statusbar.CommandQueue.this
                java.util.ArrayList r2 = r2.mCallbacks
                int r2 = r2.size()
                if (r3 >= r2) goto L_0x0a67
                com.android.systemui.statusbar.CommandQueue r2 = com.android.systemui.statusbar.CommandQueue.this
                java.util.ArrayList r2 = r2.mCallbacks
                java.lang.Object r2 = r2.get(r3)
                com.android.systemui.statusbar.CommandQueue$Callbacks r2 = (com.android.systemui.statusbar.CommandQueue.Callbacks) r2
                int r4 = r1.arg1
                r2.handleSystemKey(r4)
                int r3 = r3 + 1
                goto L_0x05c2
            L_0x05e2:
                com.android.systemui.statusbar.CommandQueue r1 = com.android.systemui.statusbar.CommandQueue.this
                java.util.ArrayList r1 = r1.mCallbacks
                int r1 = r1.size()
                if (r3 >= r1) goto L_0x0a67
                com.android.systemui.statusbar.CommandQueue r1 = com.android.systemui.statusbar.CommandQueue.this
                java.util.ArrayList r1 = r1.mCallbacks
                java.lang.Object r1 = r1.get(r3)
                com.android.systemui.statusbar.CommandQueue$Callbacks r1 = (com.android.systemui.statusbar.CommandQueue.Callbacks) r1
                r1.dismissKeyboardShortcutsMenu()
                int r3 = r3 + 1
                goto L_0x05e2
            L_0x0600:
                com.android.systemui.statusbar.CommandQueue r2 = com.android.systemui.statusbar.CommandQueue.this
                java.util.ArrayList r2 = r2.mCallbacks
                int r2 = r2.size()
                if (r3 >= r2) goto L_0x0a67
                com.android.systemui.statusbar.CommandQueue r2 = com.android.systemui.statusbar.CommandQueue.this
                java.util.ArrayList r2 = r2.mCallbacks
                java.lang.Object r2 = r2.get(r3)
                com.android.systemui.statusbar.CommandQueue$Callbacks r2 = (com.android.systemui.statusbar.CommandQueue.Callbacks) r2
                int r4 = r1.arg1
                r2.appTransitionFinished(r4)
                int r3 = r3 + 1
                goto L_0x0600
            L_0x0620:
                com.android.systemui.statusbar.CommandQueue r1 = com.android.systemui.statusbar.CommandQueue.this
                java.util.ArrayList r1 = r1.mCallbacks
                int r1 = r1.size()
                if (r3 >= r1) goto L_0x0a67
                com.android.systemui.statusbar.CommandQueue r1 = com.android.systemui.statusbar.CommandQueue.this
                java.util.ArrayList r1 = r1.mCallbacks
                java.lang.Object r1 = r1.get(r3)
                com.android.systemui.statusbar.CommandQueue$Callbacks r1 = (com.android.systemui.statusbar.CommandQueue.Callbacks) r1
                r1.toggleSplitScreen()
                int r3 = r3 + 1
                goto L_0x0620
            L_0x063e:
                com.android.systemui.statusbar.CommandQueue r2 = com.android.systemui.statusbar.CommandQueue.this
                java.util.ArrayList r2 = r2.mCallbacks
                int r2 = r2.size()
                if (r3 >= r2) goto L_0x0a67
                com.android.systemui.statusbar.CommandQueue r2 = com.android.systemui.statusbar.CommandQueue.this
                java.util.ArrayList r2 = r2.mCallbacks
                java.lang.Object r2 = r2.get(r3)
                com.android.systemui.statusbar.CommandQueue$Callbacks r2 = (com.android.systemui.statusbar.CommandQueue.Callbacks) r2
                java.lang.Object r4 = r1.obj
                android.content.ComponentName r4 = (android.content.ComponentName) r4
                r2.clickTile(r4)
                int r3 = r3 + 1
                goto L_0x063e
            L_0x0660:
                com.android.systemui.statusbar.CommandQueue r2 = com.android.systemui.statusbar.CommandQueue.this
                java.util.ArrayList r2 = r2.mCallbacks
                int r2 = r2.size()
                if (r3 >= r2) goto L_0x0a67
                com.android.systemui.statusbar.CommandQueue r2 = com.android.systemui.statusbar.CommandQueue.this
                java.util.ArrayList r2 = r2.mCallbacks
                java.lang.Object r2 = r2.get(r3)
                com.android.systemui.statusbar.CommandQueue$Callbacks r2 = (com.android.systemui.statusbar.CommandQueue.Callbacks) r2
                java.lang.Object r4 = r1.obj
                android.content.ComponentName r4 = (android.content.ComponentName) r4
                r2.remQsTile(r4)
                int r3 = r3 + 1
                goto L_0x0660
            L_0x0682:
                com.android.systemui.statusbar.CommandQueue r2 = com.android.systemui.statusbar.CommandQueue.this
                java.util.ArrayList r2 = r2.mCallbacks
                int r2 = r2.size()
                if (r3 >= r2) goto L_0x0a67
                com.android.systemui.statusbar.CommandQueue r2 = com.android.systemui.statusbar.CommandQueue.this
                java.util.ArrayList r2 = r2.mCallbacks
                java.lang.Object r2 = r2.get(r3)
                com.android.systemui.statusbar.CommandQueue$Callbacks r2 = (com.android.systemui.statusbar.CommandQueue.Callbacks) r2
                java.lang.Object r4 = r1.obj
                android.content.ComponentName r4 = (android.content.ComponentName) r4
                r2.addQsTile(r4)
                int r3 = r3 + 1
                goto L_0x0682
            L_0x06a4:
                com.android.systemui.statusbar.CommandQueue r1 = com.android.systemui.statusbar.CommandQueue.this
                java.util.ArrayList r1 = r1.mCallbacks
                int r1 = r1.size()
                if (r3 >= r1) goto L_0x0a67
                com.android.systemui.statusbar.CommandQueue r1 = com.android.systemui.statusbar.CommandQueue.this
                java.util.ArrayList r1 = r1.mCallbacks
                java.lang.Object r1 = r1.get(r3)
                com.android.systemui.statusbar.CommandQueue$Callbacks r1 = (com.android.systemui.statusbar.CommandQueue.Callbacks) r1
                r1.showPictureInPictureMenu()
                int r3 = r3 + 1
                goto L_0x06a4
            L_0x06c2:
                com.android.systemui.statusbar.CommandQueue r2 = com.android.systemui.statusbar.CommandQueue.this
                java.util.ArrayList r2 = r2.mCallbacks
                int r2 = r2.size()
                if (r3 >= r2) goto L_0x0a67
                com.android.systemui.statusbar.CommandQueue r2 = com.android.systemui.statusbar.CommandQueue.this
                java.util.ArrayList r2 = r2.mCallbacks
                java.lang.Object r2 = r2.get(r3)
                com.android.systemui.statusbar.CommandQueue$Callbacks r2 = (com.android.systemui.statusbar.CommandQueue.Callbacks) r2
                int r4 = r1.arg1
                r2.toggleKeyboardShortcutsMenu(r4)
                int r3 = r3 + 1
                goto L_0x06c2
            L_0x06e2:
                java.lang.Object r2 = r1.obj
                if (r2 == 0) goto L_0x0719
                com.android.internal.os.SomeArgs r2 = (com.android.internal.os.SomeArgs) r2
                java.lang.Object r1 = r2.arg1
                java.lang.Integer r1 = (java.lang.Integer) r1
                int r1 = r1.intValue()
                java.lang.Object r2 = r2.arg2
                java.lang.Integer r2 = (java.lang.Integer) r2
                int r2 = r2.intValue()
                if (r2 != 0) goto L_0x06fb
                r4 = r3
            L_0x06fb:
                com.android.systemui.statusbar.CommandQueue r2 = com.android.systemui.statusbar.CommandQueue.this
                java.util.ArrayList r2 = r2.mCallbacks
                int r2 = r2.size()
                if (r3 >= r2) goto L_0x0a67
                com.android.systemui.statusbar.CommandQueue r2 = com.android.systemui.statusbar.CommandQueue.this
                java.util.ArrayList r2 = r2.mCallbacks
                java.lang.Object r2 = r2.get(r3)
                com.android.systemui.statusbar.CommandQueue$Callbacks r2 = (com.android.systemui.statusbar.CommandQueue.Callbacks) r2
                r2.onCameraLaunchGestureDetectedForAutoQuickCapture(r1, r4)
                int r3 = r3 + 1
                goto L_0x06fb
            L_0x0719:
                com.android.systemui.statusbar.CommandQueue r2 = com.android.systemui.statusbar.CommandQueue.this
                java.util.ArrayList r2 = r2.mCallbacks
                int r2 = r2.size()
                if (r3 >= r2) goto L_0x0a67
                com.android.systemui.statusbar.CommandQueue r2 = com.android.systemui.statusbar.CommandQueue.this
                java.util.ArrayList r2 = r2.mCallbacks
                java.lang.Object r2 = r2.get(r3)
                com.android.systemui.statusbar.CommandQueue$Callbacks r2 = (com.android.systemui.statusbar.CommandQueue.Callbacks) r2
                int r4 = r1.arg1
                r2.onCameraLaunchGestureDetected(r4)
                int r3 = r3 + 1
                goto L_0x0719
            L_0x0739:
                com.android.systemui.statusbar.CommandQueue r2 = com.android.systemui.statusbar.CommandQueue.this
                java.util.ArrayList r2 = r2.mCallbacks
                int r2 = r2.size()
                if (r3 >= r2) goto L_0x0a67
                com.android.systemui.statusbar.CommandQueue r2 = com.android.systemui.statusbar.CommandQueue.this
                java.util.ArrayList r2 = r2.mCallbacks
                java.lang.Object r2 = r2.get(r3)
                com.android.systemui.statusbar.CommandQueue$Callbacks r2 = (com.android.systemui.statusbar.CommandQueue.Callbacks) r2
                java.lang.Object r4 = r1.obj
                android.os.Bundle r4 = (android.os.Bundle) r4
                r2.startAssist(r4)
                int r3 = r3 + 1
                goto L_0x0739
            L_0x075b:
                com.android.systemui.statusbar.CommandQueue r1 = com.android.systemui.statusbar.CommandQueue.this
                java.util.ArrayList r1 = r1.mCallbacks
                int r1 = r1.size()
                if (r3 >= r1) goto L_0x0a67
                com.android.systemui.statusbar.CommandQueue r1 = com.android.systemui.statusbar.CommandQueue.this
                java.util.ArrayList r1 = r1.mCallbacks
                java.lang.Object r1 = r1.get(r3)
                com.android.systemui.statusbar.CommandQueue$Callbacks r1 = (com.android.systemui.statusbar.CommandQueue.Callbacks) r1
                r1.showAssistDisclosure()
                int r3 = r3 + 1
                goto L_0x075b
            L_0x0779:
                java.lang.Object r1 = r1.obj
                com.android.internal.os.SomeArgs r1 = (com.android.internal.os.SomeArgs) r1
                r2 = r3
            L_0x077e:
                com.android.systemui.statusbar.CommandQueue r5 = com.android.systemui.statusbar.CommandQueue.this
                java.util.ArrayList r5 = r5.mCallbacks
                int r5 = r5.size()
                if (r2 >= r5) goto L_0x0a67
                com.android.systemui.statusbar.CommandQueue r5 = com.android.systemui.statusbar.CommandQueue.this
                java.util.ArrayList r5 = r5.mCallbacks
                java.lang.Object r5 = r5.get(r2)
                r6 = r5
                com.android.systemui.statusbar.CommandQueue$Callbacks r6 = (com.android.systemui.statusbar.CommandQueue.Callbacks) r6
                int r7 = r1.argi1
                java.lang.Object r5 = r1.arg1
                java.lang.Long r5 = (java.lang.Long) r5
                long r8 = r5.longValue()
                java.lang.Object r5 = r1.arg2
                java.lang.Long r5 = (java.lang.Long) r5
                long r10 = r5.longValue()
                int r5 = r1.argi2
                if (r5 == 0) goto L_0x07af
                r12 = r4
                goto L_0x07b0
            L_0x07af:
                r12 = r3
            L_0x07b0:
                r6.appTransitionStarting(r7, r8, r10, r12)
                int r2 = r2 + 1
                goto L_0x077e
            L_0x07b6:
                com.android.systemui.statusbar.CommandQueue r2 = com.android.systemui.statusbar.CommandQueue.this
                java.util.ArrayList r2 = r2.mCallbacks
                int r2 = r2.size()
                if (r3 >= r2) goto L_0x0a67
                com.android.systemui.statusbar.CommandQueue r2 = com.android.systemui.statusbar.CommandQueue.this
                java.util.ArrayList r2 = r2.mCallbacks
                java.lang.Object r2 = r2.get(r3)
                com.android.systemui.statusbar.CommandQueue$Callbacks r2 = (com.android.systemui.statusbar.CommandQueue.Callbacks) r2
                int r4 = r1.arg1
                r2.appTransitionCancelled(r4)
                int r3 = r3 + 1
                goto L_0x07b6
            L_0x07d6:
                r2 = r3
            L_0x07d7:
                com.android.systemui.statusbar.CommandQueue r5 = com.android.systemui.statusbar.CommandQueue.this
                java.util.ArrayList r5 = r5.mCallbacks
                int r5 = r5.size()
                if (r2 >= r5) goto L_0x0a67
                com.android.systemui.statusbar.CommandQueue r5 = com.android.systemui.statusbar.CommandQueue.this
                java.util.ArrayList r5 = r5.mCallbacks
                java.lang.Object r5 = r5.get(r2)
                com.android.systemui.statusbar.CommandQueue$Callbacks r5 = (com.android.systemui.statusbar.CommandQueue.Callbacks) r5
                int r6 = r1.arg1
                int r7 = r1.arg2
                if (r7 == 0) goto L_0x07f7
                r7 = r4
                goto L_0x07f8
            L_0x07f7:
                r7 = r3
            L_0x07f8:
                r5.appTransitionPending(r6, r7)
                int r2 = r2 + 1
                goto L_0x07d7
            L_0x07fe:
                com.android.systemui.statusbar.CommandQueue r2 = com.android.systemui.statusbar.CommandQueue.this
                java.util.ArrayList r2 = r2.mCallbacks
                int r2 = r2.size()
                if (r3 >= r2) goto L_0x0a67
                com.android.systemui.statusbar.CommandQueue r2 = com.android.systemui.statusbar.CommandQueue.this
                java.util.ArrayList r2 = r2.mCallbacks
                java.lang.Object r2 = r2.get(r3)
                com.android.systemui.statusbar.CommandQueue$Callbacks r2 = (com.android.systemui.statusbar.CommandQueue.Callbacks) r2
                int r4 = r1.arg1
                r2.showScreenPinningRequest(r4)
                int r3 = r3 + 1
                goto L_0x07fe
            L_0x081e:
                r2 = r3
            L_0x081f:
                com.android.systemui.statusbar.CommandQueue r5 = com.android.systemui.statusbar.CommandQueue.this
                java.util.ArrayList r5 = r5.mCallbacks
                int r5 = r5.size()
                if (r2 >= r5) goto L_0x0a67
                com.android.systemui.statusbar.CommandQueue r5 = com.android.systemui.statusbar.CommandQueue.this
                java.util.ArrayList r5 = r5.mCallbacks
                java.lang.Object r5 = r5.get(r2)
                com.android.systemui.statusbar.CommandQueue$Callbacks r5 = (com.android.systemui.statusbar.CommandQueue.Callbacks) r5
                int r6 = r1.arg1
                if (r6 == 0) goto L_0x083d
                r6 = r4
                goto L_0x083e
            L_0x083d:
                r6 = r3
            L_0x083e:
                int r7 = r1.arg2
                if (r7 == 0) goto L_0x0844
                r7 = r4
                goto L_0x0845
            L_0x0844:
                r7 = r3
            L_0x0845:
                r5.hideRecentApps(r6, r7)
                int r2 = r2 + 1
                goto L_0x081f
            L_0x084b:
                r2 = r3
            L_0x084c:
                com.android.systemui.statusbar.CommandQueue r5 = com.android.systemui.statusbar.CommandQueue.this
                java.util.ArrayList r5 = r5.mCallbacks
                int r5 = r5.size()
                if (r2 >= r5) goto L_0x0a67
                com.android.systemui.statusbar.CommandQueue r5 = com.android.systemui.statusbar.CommandQueue.this
                java.util.ArrayList r5 = r5.mCallbacks
                java.lang.Object r5 = r5.get(r2)
                com.android.systemui.statusbar.CommandQueue$Callbacks r5 = (com.android.systemui.statusbar.CommandQueue.Callbacks) r5
                int r6 = r1.arg1
                if (r6 == 0) goto L_0x086a
                r6 = r4
                goto L_0x086b
            L_0x086a:
                r6 = r3
            L_0x086b:
                r5.showRecentApps(r6)
                int r2 = r2 + 1
                goto L_0x084c
            L_0x0871:
                com.android.systemui.statusbar.CommandQueue r2 = com.android.systemui.statusbar.CommandQueue.this
                java.util.ArrayList r2 = r2.mCallbacks
                int r2 = r2.size()
                if (r3 >= r2) goto L_0x0a67
                com.android.systemui.statusbar.CommandQueue r2 = com.android.systemui.statusbar.CommandQueue.this
                java.util.ArrayList r2 = r2.mCallbacks
                java.lang.Object r2 = r2.get(r3)
                com.android.systemui.statusbar.CommandQueue$Callbacks r2 = (com.android.systemui.statusbar.CommandQueue.Callbacks) r2
                int r4 = r1.arg1
                int r5 = r1.arg2
                java.lang.Object r6 = r1.obj
                java.lang.Integer r6 = (java.lang.Integer) r6
                int r6 = r6.intValue()
                r2.setWindowState(r4, r5, r6)
                int r3 = r3 + 1
                goto L_0x0871
            L_0x089b:
                com.android.systemui.statusbar.CommandQueue r1 = com.android.systemui.statusbar.CommandQueue.this
                java.util.ArrayList r1 = r1.mCallbacks
                int r1 = r1.size()
                if (r3 >= r1) goto L_0x0a67
                com.android.systemui.statusbar.CommandQueue r1 = com.android.systemui.statusbar.CommandQueue.this
                java.util.ArrayList r1 = r1.mCallbacks
                java.lang.Object r1 = r1.get(r3)
                com.android.systemui.statusbar.CommandQueue$Callbacks r1 = (com.android.systemui.statusbar.CommandQueue.Callbacks) r1
                r1.cancelPreloadRecentApps()
                int r3 = r3 + 1
                goto L_0x089b
            L_0x08b9:
                com.android.systemui.statusbar.CommandQueue r1 = com.android.systemui.statusbar.CommandQueue.this
                java.util.ArrayList r1 = r1.mCallbacks
                int r1 = r1.size()
                if (r3 >= r1) goto L_0x0a67
                com.android.systemui.statusbar.CommandQueue r1 = com.android.systemui.statusbar.CommandQueue.this
                java.util.ArrayList r1 = r1.mCallbacks
                java.lang.Object r1 = r1.get(r3)
                com.android.systemui.statusbar.CommandQueue$Callbacks r1 = (com.android.systemui.statusbar.CommandQueue.Callbacks) r1
                r1.preloadRecentApps()
                int r3 = r3 + 1
                goto L_0x08b9
            L_0x08d7:
                com.android.systemui.statusbar.CommandQueue r1 = com.android.systemui.statusbar.CommandQueue.this
                java.util.ArrayList r1 = r1.mCallbacks
                int r1 = r1.size()
                if (r3 >= r1) goto L_0x0a67
                com.android.systemui.statusbar.CommandQueue r1 = com.android.systemui.statusbar.CommandQueue.this
                java.util.ArrayList r1 = r1.mCallbacks
                java.lang.Object r1 = r1.get(r3)
                com.android.systemui.statusbar.CommandQueue$Callbacks r1 = (com.android.systemui.statusbar.CommandQueue.Callbacks) r1
                r1.toggleRecentApps()
                int r3 = r3 + 1
                goto L_0x08d7
            L_0x08f5:
                java.lang.Object r1 = r1.obj
                com.android.internal.os.SomeArgs r1 = (com.android.internal.os.SomeArgs) r1
                com.android.systemui.statusbar.CommandQueue r5 = com.android.systemui.statusbar.CommandQueue.this
                int r6 = r1.argi1
                java.lang.Object r0 = r1.arg1
                r7 = r0
                android.os.IBinder r7 = (android.os.IBinder) r7
                int r8 = r1.argi2
                int r9 = r1.argi3
                int r0 = r1.argi4
                if (r0 == 0) goto L_0x090c
                r10 = r4
                goto L_0x090d
            L_0x090c:
                r10 = r3
            L_0x090d:
                int r0 = r1.argi5
                if (r0 == 0) goto L_0x0913
                r11 = r4
                goto L_0x0914
            L_0x0913:
                r11 = r3
            L_0x0914:
                r5.handleShowImeButton(r6, r7, r8, r9, r10, r11)
                goto L_0x0a67
            L_0x0919:
                com.android.systemui.statusbar.CommandQueue r2 = com.android.systemui.statusbar.CommandQueue.this
                java.util.ArrayList r2 = r2.mCallbacks
                int r2 = r2.size()
                if (r3 >= r2) goto L_0x0a67
                com.android.systemui.statusbar.CommandQueue r2 = com.android.systemui.statusbar.CommandQueue.this
                java.util.ArrayList r2 = r2.mCallbacks
                java.lang.Object r2 = r2.get(r3)
                com.android.systemui.statusbar.CommandQueue$Callbacks r2 = (com.android.systemui.statusbar.CommandQueue.Callbacks) r2
                int r4 = r1.arg1
                r2.onDisplayReady(r4)
                int r3 = r3 + 1
                goto L_0x0919
            L_0x0939:
                java.lang.Object r1 = r1.obj
                com.android.internal.os.SomeArgs r1 = (com.android.internal.os.SomeArgs) r1
                r2 = r3
            L_0x093e:
                com.android.systemui.statusbar.CommandQueue r5 = com.android.systemui.statusbar.CommandQueue.this
                java.util.ArrayList r5 = r5.mCallbacks
                int r5 = r5.size()
                if (r2 >= r5) goto L_0x0976
                com.android.systemui.statusbar.CommandQueue r5 = com.android.systemui.statusbar.CommandQueue.this
                java.util.ArrayList r5 = r5.mCallbacks
                java.lang.Object r5 = r5.get(r2)
                r6 = r5
                com.android.systemui.statusbar.CommandQueue$Callbacks r6 = (com.android.systemui.statusbar.CommandQueue.Callbacks) r6
                int r7 = r1.argi1
                int r8 = r1.argi2
                java.lang.Object r5 = r1.arg1
                r9 = r5
                com.android.internal.view.AppearanceRegion[] r9 = (com.android.internal.view.AppearanceRegion[]) r9
                int r5 = r1.argi3
                if (r5 != r4) goto L_0x0966
                r10 = r4
                goto L_0x0967
            L_0x0966:
                r10 = r3
            L_0x0967:
                int r11 = r1.argi4
                int r5 = r1.argi5
                if (r5 != r4) goto L_0x096f
                r12 = r4
                goto L_0x0970
            L_0x096f:
                r12 = r3
            L_0x0970:
                r6.onSystemBarAttributesChanged(r7, r8, r9, r10, r11, r12)
                int r2 = r2 + 1
                goto L_0x093e
            L_0x0976:
                r1.recycle()
                goto L_0x0a67
            L_0x097b:
                com.android.systemui.statusbar.CommandQueue r2 = com.android.systemui.statusbar.CommandQueue.this
                java.util.ArrayList r2 = r2.mCallbacks
                int r2 = r2.size()
                if (r3 >= r2) goto L_0x0a67
                com.android.systemui.statusbar.CommandQueue r2 = com.android.systemui.statusbar.CommandQueue.this
                java.util.ArrayList r2 = r2.mCallbacks
                java.lang.Object r2 = r2.get(r3)
                com.android.systemui.statusbar.CommandQueue$Callbacks r2 = (com.android.systemui.statusbar.CommandQueue.Callbacks) r2
                java.lang.Object r4 = r1.obj
                java.lang.String r4 = (java.lang.String) r4
                r2.animateExpandSettingsPanel(r4)
                int r3 = r3 + 1
                goto L_0x097b
            L_0x099d:
                r2 = r3
            L_0x099e:
                com.android.systemui.statusbar.CommandQueue r5 = com.android.systemui.statusbar.CommandQueue.this
                java.util.ArrayList r5 = r5.mCallbacks
                int r5 = r5.size()
                if (r2 >= r5) goto L_0x0a67
                com.android.systemui.statusbar.CommandQueue r5 = com.android.systemui.statusbar.CommandQueue.this
                java.util.ArrayList r5 = r5.mCallbacks
                java.lang.Object r5 = r5.get(r2)
                com.android.systemui.statusbar.CommandQueue$Callbacks r5 = (com.android.systemui.statusbar.CommandQueue.Callbacks) r5
                int r6 = r1.arg1
                int r7 = r1.arg2
                if (r7 == 0) goto L_0x09be
                r7 = r4
                goto L_0x09bf
            L_0x09be:
                r7 = r3
            L_0x09bf:
                r5.animateCollapsePanels(r6, r7)
                int r2 = r2 + 1
                goto L_0x099e
            L_0x09c5:
                com.android.systemui.statusbar.CommandQueue r1 = com.android.systemui.statusbar.CommandQueue.this
                java.util.ArrayList r1 = r1.mCallbacks
                int r1 = r1.size()
                if (r3 >= r1) goto L_0x0a67
                com.android.systemui.statusbar.CommandQueue r1 = com.android.systemui.statusbar.CommandQueue.this
                java.util.ArrayList r1 = r1.mCallbacks
                java.lang.Object r1 = r1.get(r3)
                com.android.systemui.statusbar.CommandQueue$Callbacks r1 = (com.android.systemui.statusbar.CommandQueue.Callbacks) r1
                r1.animateExpandNotificationsPanel()
                int r3 = r3 + 1
                goto L_0x09c5
            L_0x09e3:
                java.lang.Object r1 = r1.obj
                com.android.internal.os.SomeArgs r1 = (com.android.internal.os.SomeArgs) r1
                r2 = r3
            L_0x09e8:
                com.android.systemui.statusbar.CommandQueue r5 = com.android.systemui.statusbar.CommandQueue.this
                java.util.ArrayList r5 = r5.mCallbacks
                int r5 = r5.size()
                if (r2 >= r5) goto L_0x0a67
                com.android.systemui.statusbar.CommandQueue r5 = com.android.systemui.statusbar.CommandQueue.this
                java.util.ArrayList r5 = r5.mCallbacks
                java.lang.Object r5 = r5.get(r2)
                com.android.systemui.statusbar.CommandQueue$Callbacks r5 = (com.android.systemui.statusbar.CommandQueue.Callbacks) r5
                int r6 = r1.argi1
                int r7 = r1.argi2
                int r8 = r1.argi3
                int r9 = r1.argi4
                if (r9 == 0) goto L_0x0a0c
                r9 = r4
                goto L_0x0a0d
            L_0x0a0c:
                r9 = r3
            L_0x0a0d:
                r5.disable(r6, r7, r8, r9)
                int r2 = r2 + 1
                goto L_0x09e8
            L_0x0a13:
                int r2 = r1.arg1
                if (r2 == r4) goto L_0x0a3d
                r4 = 2
                if (r2 == r4) goto L_0x0a1b
                goto L_0x0a67
            L_0x0a1b:
                com.android.systemui.statusbar.CommandQueue r2 = com.android.systemui.statusbar.CommandQueue.this
                java.util.ArrayList r2 = r2.mCallbacks
                int r2 = r2.size()
                if (r3 >= r2) goto L_0x0a67
                com.android.systemui.statusbar.CommandQueue r2 = com.android.systemui.statusbar.CommandQueue.this
                java.util.ArrayList r2 = r2.mCallbacks
                java.lang.Object r2 = r2.get(r3)
                com.android.systemui.statusbar.CommandQueue$Callbacks r2 = (com.android.systemui.statusbar.CommandQueue.Callbacks) r2
                java.lang.Object r4 = r1.obj
                java.lang.String r4 = (java.lang.String) r4
                r2.removeIcon(r4)
                int r3 = r3 + 1
                goto L_0x0a1b
            L_0x0a3d:
                java.lang.Object r1 = r1.obj
                android.util.Pair r1 = (android.util.Pair) r1
            L_0x0a41:
                com.android.systemui.statusbar.CommandQueue r2 = com.android.systemui.statusbar.CommandQueue.this
                java.util.ArrayList r2 = r2.mCallbacks
                int r2 = r2.size()
                if (r3 >= r2) goto L_0x0a67
                com.android.systemui.statusbar.CommandQueue r2 = com.android.systemui.statusbar.CommandQueue.this
                java.util.ArrayList r2 = r2.mCallbacks
                java.lang.Object r2 = r2.get(r3)
                com.android.systemui.statusbar.CommandQueue$Callbacks r2 = (com.android.systemui.statusbar.CommandQueue.Callbacks) r2
                java.lang.Object r4 = r1.first
                java.lang.String r4 = (java.lang.String) r4
                java.lang.Object r5 = r1.second
                com.android.internal.statusbar.StatusBarIcon r5 = (com.android.internal.statusbar.StatusBarIcon) r5
                r2.setIcon(r4, r5)
                int r3 = r3 + 1
                goto L_0x0a41
            L_0x0a67:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.statusbar.CommandQueue.C1426H.handleMessage(android.os.Message):void");
        }
    }
}
