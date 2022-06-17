package com.android.systemui.assist;

import android.content.ComponentName;
import android.content.Context;
import android.metrics.LogMaker;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.SystemClock;
import com.android.internal.app.AssistUtils;
import com.android.internal.app.IVoiceInteractionSessionListener;
import com.android.internal.app.IVoiceInteractionSessionShowCallback;
import com.android.internal.logging.MetricsLogger;
import com.android.keyguard.KeyguardUpdateMonitor;
import com.android.systemui.DejankUtils;
import com.android.systemui.assist.p003ui.DefaultUiController;
import com.android.systemui.model.SysUiState;
import com.android.systemui.moto.MotoFeature;
import com.android.systemui.recents.OverviewProxyService;
import com.android.systemui.statusbar.CommandQueue;
import com.android.systemui.statusbar.policy.ConfigurationController;
import com.android.systemui.statusbar.policy.DeviceProvisionedController;
import dagger.Lazy;

public class AssistManager {
    private final AssistDisclosure mAssistDisclosure;
    protected final AssistLogger mAssistLogger;
    protected final AssistUtils mAssistUtils;
    private final CommandQueue mCommandQueue;
    protected final Context mContext;
    private final DeviceProvisionedController mDeviceProvisionedController;
    /* access modifiers changed from: private */
    public final AssistOrbController mOrbController;
    private final PhoneStateMonitor mPhoneStateMonitor;
    private IVoiceInteractionSessionShowCallback mShowCallback = new IVoiceInteractionSessionShowCallback.Stub() {
        public void onFailed() throws RemoteException {
            AssistManager.this.mOrbController.postHide();
        }

        public void onShown() throws RemoteException {
            AssistManager.this.mOrbController.postHide();
        }
    };
    protected final Lazy<SysUiState> mSysUiState;
    private final UiController mUiController;

    public interface UiController {
        void onGestureCompletion(float f);

        void onInvocationProgress(int i, float f);
    }

    /* access modifiers changed from: protected */
    public boolean shouldShowOrb() {
        return false;
    }

    /* access modifiers changed from: protected */
    public final int toLoggingSubType(int i, int i2) {
        return (i << 1) | 0 | (i2 << 4);
    }

    public AssistManager(DeviceProvisionedController deviceProvisionedController, Context context, AssistUtils assistUtils, CommandQueue commandQueue, PhoneStateMonitor phoneStateMonitor, OverviewProxyService overviewProxyService, ConfigurationController configurationController, Lazy<SysUiState> lazy, DefaultUiController defaultUiController, AssistLogger assistLogger) {
        this.mContext = context;
        this.mDeviceProvisionedController = deviceProvisionedController;
        this.mCommandQueue = commandQueue;
        this.mAssistUtils = assistUtils;
        this.mAssistDisclosure = new AssistDisclosure(context, new Handler());
        this.mPhoneStateMonitor = phoneStateMonitor;
        this.mAssistLogger = assistLogger;
        this.mOrbController = new AssistOrbController(configurationController, context);
        registerVoiceInteractionSessionListener();
        this.mUiController = defaultUiController;
        this.mSysUiState = lazy;
        overviewProxyService.addCallback((OverviewProxyService.OverviewProxyListener) new OverviewProxyService.OverviewProxyListener() {
            public void onAssistantProgress(float f) {
                AssistManager.this.onInvocationProgress(1, f);
            }

            public void onAssistantGestureCompletion(float f) {
                AssistManager.this.onGestureCompletion(f);
            }
        });
    }

    /* access modifiers changed from: protected */
    public void registerVoiceInteractionSessionListener() {
        this.mAssistUtils.registerVoiceInteractionSessionListener(new IVoiceInteractionSessionListener.Stub() {
            public void onVoiceSessionShown() throws RemoteException {
                AssistManager.this.mAssistLogger.reportAssistantSessionEvent(AssistantSessionEvent.ASSISTANT_SESSION_UPDATE);
            }

            public void onVoiceSessionHidden() throws RemoteException {
                AssistManager.this.mAssistLogger.reportAssistantSessionEvent(AssistantSessionEvent.ASSISTANT_SESSION_CLOSE);
            }

            public void onSetUiHints(Bundle bundle) {
                if ("set_assist_gesture_constrained".equals(bundle.getString("action"))) {
                    AssistManager.this.mSysUiState.get().setFlag(8192, bundle.getBoolean("should_constrain", false)).commitUpdate(0);
                }
            }
        });
    }

    public void startAssist(Bundle bundle) {
        ComponentName assistInfo = getAssistInfo();
        if (assistInfo != null) {
            boolean equals = assistInfo.equals(getVoiceInteractorComponentName());
            if ((!equals || (!isVoiceSessionRunning() && shouldShowOrb())) && !MotoFeature.isPrcProduct()) {
                this.mOrbController.showOrb(assistInfo, equals);
                this.mOrbController.postHideDelayed(equals ? 2500 : 1000);
            }
            if (bundle == null) {
                bundle = new Bundle();
            }
            int i = bundle.getInt("invocation_type", 0);
            int phoneState = this.mPhoneStateMonitor.getPhoneState();
            bundle.putInt("invocation_phone_state", phoneState);
            bundle.putLong("invocation_time_ms", SystemClock.elapsedRealtime());
            this.mAssistLogger.reportAssistantInvocationEventFromLegacy(i, true, assistInfo, Integer.valueOf(phoneState));
            logStartAssistLegacy(i, phoneState);
            startAssistInternal(bundle, assistInfo, equals);
        }
    }

    public void onInvocationProgress(int i, float f) {
        this.mUiController.onInvocationProgress(i, f);
    }

    public void onGestureCompletion(float f) {
        this.mUiController.onGestureCompletion(f);
    }

    public void hideAssist() {
        this.mAssistUtils.hideCurrentSession();
    }

    private void startAssistInternal(Bundle bundle, ComponentName componentName, boolean z) {
        if (z) {
            startVoiceInteractor(bundle);
        } else {
            startAssistActivity(bundle, componentName);
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:9:0x002e, code lost:
        r0 = r0.getAssistIntent(r2);
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void startAssistActivity(android.os.Bundle r6, android.content.ComponentName r7) {
        /*
            r5 = this;
            com.android.systemui.statusbar.policy.DeviceProvisionedController r0 = r5.mDeviceProvisionedController
            boolean r0 = r0.isDeviceProvisioned()
            if (r0 != 0) goto L_0x0009
            return
        L_0x0009:
            com.android.systemui.statusbar.CommandQueue r0 = r5.mCommandQueue
            r1 = 3
            r2 = 0
            r0.animateCollapsePanels(r1, r2)
            android.content.Context r0 = r5.mContext
            android.content.ContentResolver r0 = r0.getContentResolver()
            r1 = -2
            java.lang.String r3 = "assist_structure_enabled"
            r4 = 1
            int r0 = android.provider.Settings.Secure.getIntForUser(r0, r3, r4, r1)
            if (r0 == 0) goto L_0x0021
            r2 = r4
        L_0x0021:
            android.content.Context r0 = r5.mContext
            java.lang.String r1 = "search"
            java.lang.Object r0 = r0.getSystemService(r1)
            android.app.SearchManager r0 = (android.app.SearchManager) r0
            if (r0 != 0) goto L_0x002e
            return
        L_0x002e:
            android.content.Intent r0 = r0.getAssistIntent(r2)
            if (r0 != 0) goto L_0x0035
            return
        L_0x0035:
            r0.setComponent(r7)
            r0.putExtras(r6)
            if (r2 == 0) goto L_0x0048
            android.content.Context r6 = r5.mContext
            boolean r6 = com.android.internal.app.AssistUtils.isDisclosureEnabled(r6)
            if (r6 == 0) goto L_0x0048
            r5.showDisclosure()
        L_0x0048:
            android.content.Context r6 = r5.mContext     // Catch:{ ActivityNotFoundException -> 0x0060 }
            int r7 = com.android.systemui.R$anim.search_launch_enter     // Catch:{ ActivityNotFoundException -> 0x0060 }
            int r1 = com.android.systemui.R$anim.search_launch_exit     // Catch:{ ActivityNotFoundException -> 0x0060 }
            android.app.ActivityOptions r6 = android.app.ActivityOptions.makeCustomAnimation(r6, r7, r1)     // Catch:{ ActivityNotFoundException -> 0x0060 }
            r7 = 268435456(0x10000000, float:2.5243549E-29)
            r0.addFlags(r7)     // Catch:{ ActivityNotFoundException -> 0x0060 }
            com.android.systemui.assist.AssistManager$4 r7 = new com.android.systemui.assist.AssistManager$4     // Catch:{ ActivityNotFoundException -> 0x0060 }
            r7.<init>(r0, r6)     // Catch:{ ActivityNotFoundException -> 0x0060 }
            android.os.AsyncTask.execute(r7)     // Catch:{ ActivityNotFoundException -> 0x0060 }
            goto L_0x007a
        L_0x0060:
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            java.lang.String r6 = "Activity not found for "
            r5.append(r6)
            java.lang.String r6 = r0.getAction()
            r5.append(r6)
            java.lang.String r5 = r5.toString()
            java.lang.String r6 = "AssistManager"
            android.util.Log.w(r6, r5)
        L_0x007a:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.assist.AssistManager.startAssistActivity(android.os.Bundle, android.content.ComponentName):void");
    }

    private void startVoiceInteractor(Bundle bundle) {
        this.mAssistUtils.showSessionForActiveService(bundle, 4, this.mShowCallback, (IBinder) null);
    }

    public void launchVoiceAssistFromKeyguard() {
        this.mAssistUtils.launchVoiceAssistFromKeyguard();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ Boolean lambda$canVoiceAssistBeLaunchedFromKeyguard$0() {
        return Boolean.valueOf(this.mAssistUtils.activeServiceSupportsLaunchFromKeyguard());
    }

    public boolean canVoiceAssistBeLaunchedFromKeyguard() {
        return ((Boolean) DejankUtils.whitelistIpcs(new AssistManager$$ExternalSyntheticLambda0(this))).booleanValue();
    }

    public ComponentName getVoiceInteractorComponentName() {
        return this.mAssistUtils.getActiveServiceComponentName();
    }

    private boolean isVoiceSessionRunning() {
        return this.mAssistUtils.isSessionRunning();
    }

    public ComponentName getAssistInfoForUser(int i) {
        return this.mAssistUtils.getAssistComponentForUser(i);
    }

    private ComponentName getAssistInfo() {
        return getAssistInfoForUser(KeyguardUpdateMonitor.getCurrentUser());
    }

    public void showDisclosure() {
        this.mAssistDisclosure.postShow();
    }

    public void onLockscreenShown() {
        AsyncTask.execute(new Runnable() {
            public void run() {
                AssistManager.this.mAssistUtils.onLockscreenShown();
            }
        });
    }

    public int toLoggingSubType(int i) {
        return toLoggingSubType(i, this.mPhoneStateMonitor.getPhoneState());
    }

    /* access modifiers changed from: protected */
    public void logStartAssistLegacy(int i, int i2) {
        MetricsLogger.action(new LogMaker(1716).setType(1).setSubtype(toLoggingSubType(i, i2)));
    }
}
