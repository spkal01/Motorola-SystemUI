package com.android.systemui.doze;

import android.app.AlarmManager;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Handler;
import android.os.SystemClock;
import android.text.format.Formatter;
import android.util.Log;
import com.android.internal.annotations.VisibleForTesting;
import com.android.keyguard.KeyguardUpdateMonitor;
import com.android.keyguard.KeyguardUpdateMonitorCallback;
import com.android.systemui.doze.DozeHost;
import com.android.systemui.doze.DozeMachine;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.statusbar.phone.DozeParameters;
import com.android.systemui.statusbar.policy.ConfigurationController;
import com.android.systemui.tuner.TunerService;
import com.android.systemui.util.AlarmTimeout;
import com.android.systemui.util.wakelock.WakeLock;
import dagger.Lazy;
import java.util.Calendar;
import java.util.Objects;

public class DozeUi implements DozeMachine.Part, TunerService.Tunable, ConfigurationController.ConfigurationListener {
    private final boolean mCanAnimateTransition;
    private final ConfigurationController mConfigurationController;
    private final Context mContext;
    private final DozeLog mDozeLog;
    private final DozeParameters mDozeParameters;
    private final Handler mHandler;
    private final DozeHost mHost;
    /* access modifiers changed from: private */
    public boolean mKeyguardShowing;
    private final KeyguardUpdateMonitorCallback mKeyguardVisibilityCallback;
    private long mLastTimeTickElapsed = 0;
    /* access modifiers changed from: private */
    public DozeMachine mMachine;
    private final Lazy<StatusBarStateController> mStatusBarStateController;
    private final AlarmTimeout mTimeTicker;
    private final TunerService mTunerService;
    private final WakeLock mWakeLock;

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$onTimeTick$0() {
    }

    public DozeUi(Context context, AlarmManager alarmManager, WakeLock wakeLock, DozeHost dozeHost, Handler handler, DozeParameters dozeParameters, KeyguardUpdateMonitor keyguardUpdateMonitor, DozeLog dozeLog, TunerService tunerService, Lazy<StatusBarStateController> lazy, ConfigurationController configurationController) {
        C09251 r0 = new KeyguardUpdateMonitorCallback() {
            public void onTimeChanged() {
            }

            public void onKeyguardVisibilityChanged(boolean z) {
                boolean unused = DozeUi.this.mKeyguardShowing = z;
                DozeUi.this.updateAnimateScreenOff();
            }

            public void onShadeExpandedChanged(boolean z) {
                DozeUi.this.updateAnimateScreenOff();
            }
        };
        this.mKeyguardVisibilityCallback = r0;
        this.mContext = context;
        this.mWakeLock = wakeLock;
        this.mHost = dozeHost;
        this.mHandler = handler;
        this.mCanAnimateTransition = !dozeParameters.getDisplayNeedsBlanking();
        this.mDozeParameters = dozeParameters;
        this.mTimeTicker = new AlarmTimeout(alarmManager, new DozeUi$$ExternalSyntheticLambda0(this), "doze_time_tick", handler);
        keyguardUpdateMonitor.registerCallback(r0);
        this.mDozeLog = dozeLog;
        this.mTunerService = tunerService;
        this.mStatusBarStateController = lazy;
        tunerService.addTunable(this, "doze_always_on");
        this.mConfigurationController = configurationController;
        configurationController.addCallback(this);
    }

    public void destroy() {
        this.mTunerService.removeTunable(this);
        this.mConfigurationController.removeCallback(this);
    }

    public void setDozeMachine(DozeMachine dozeMachine) {
        this.mMachine = dozeMachine;
    }

    /* access modifiers changed from: private */
    public void updateAnimateScreenOff() {
        if (this.mCanAnimateTransition) {
            boolean z = true;
            boolean z2 = this.mDozeParameters.getAlwaysOn() && (this.mKeyguardShowing || this.mDozeParameters.shouldControlUnlockedScreenOff()) && !this.mHost.isPowerSaveActive();
            this.mDozeParameters.setControlScreenOffAnimation(z2 && MotoDisplayManager.isAospAD());
            DozeHost dozeHost = this.mHost;
            if (!z2 || !MotoDisplayManager.isAospAD()) {
                z = false;
            }
            dozeHost.setAnimateScreenOff(z);
        }
    }

    private void pulseWhileDozing(final int i) {
        this.mHost.pulseWhileDozing(new DozeHost.PulseCallback() {
            public void onPulseStarted() {
                DozeMachine.State state;
                try {
                    DozeMachine access$200 = DozeUi.this.mMachine;
                    if (i == 8) {
                        state = DozeMachine.State.DOZE_PULSING_BRIGHT;
                    } else {
                        state = DozeMachine.State.DOZE_PULSING;
                    }
                    access$200.requestState(state);
                } catch (IllegalStateException unused) {
                }
            }

            public void onPulseFinished() {
                DozeUi.this.mMachine.requestState(DozeMachine.State.DOZE_PULSE_DONE);
            }
        }, i);
    }

    /* renamed from: com.android.systemui.doze.DozeUi$3 */
    static /* synthetic */ class C09273 {
        static final /* synthetic */ int[] $SwitchMap$com$android$systemui$doze$DozeMachine$State;

        /* JADX WARNING: Can't wrap try/catch for region: R(22:0|1|2|3|4|5|6|7|8|9|10|11|12|13|14|15|16|17|18|19|20|(3:21|22|24)) */
        /* JADX WARNING: Can't wrap try/catch for region: R(24:0|1|2|3|4|5|6|7|8|9|10|11|12|13|14|15|16|17|18|19|20|21|22|24) */
        /* JADX WARNING: Failed to process nested try/catch */
        /* JADX WARNING: Missing exception handler attribute for start block: B:11:0x003e */
        /* JADX WARNING: Missing exception handler attribute for start block: B:13:0x0049 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:15:0x0054 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:17:0x0060 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:19:0x006c */
        /* JADX WARNING: Missing exception handler attribute for start block: B:21:0x0078 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:3:0x0012 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:5:0x001d */
        /* JADX WARNING: Missing exception handler attribute for start block: B:7:0x0028 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:9:0x0033 */
        static {
            /*
                com.android.systemui.doze.DozeMachine$State[] r0 = com.android.systemui.doze.DozeMachine.State.values()
                int r0 = r0.length
                int[] r0 = new int[r0]
                $SwitchMap$com$android$systemui$doze$DozeMachine$State = r0
                com.android.systemui.doze.DozeMachine$State r1 = com.android.systemui.doze.DozeMachine.State.DOZE_AOD     // Catch:{ NoSuchFieldError -> 0x0012 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0012 }
                r2 = 1
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0012 }
            L_0x0012:
                int[] r0 = $SwitchMap$com$android$systemui$doze$DozeMachine$State     // Catch:{ NoSuchFieldError -> 0x001d }
                com.android.systemui.doze.DozeMachine$State r1 = com.android.systemui.doze.DozeMachine.State.DOZE_AOD_DOCKED     // Catch:{ NoSuchFieldError -> 0x001d }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x001d }
                r2 = 2
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x001d }
            L_0x001d:
                int[] r0 = $SwitchMap$com$android$systemui$doze$DozeMachine$State     // Catch:{ NoSuchFieldError -> 0x0028 }
                com.android.systemui.doze.DozeMachine$State r1 = com.android.systemui.doze.DozeMachine.State.DOZE_AOD_PAUSING     // Catch:{ NoSuchFieldError -> 0x0028 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0028 }
                r2 = 3
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0028 }
            L_0x0028:
                int[] r0 = $SwitchMap$com$android$systemui$doze$DozeMachine$State     // Catch:{ NoSuchFieldError -> 0x0033 }
                com.android.systemui.doze.DozeMachine$State r1 = com.android.systemui.doze.DozeMachine.State.DOZE     // Catch:{ NoSuchFieldError -> 0x0033 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0033 }
                r2 = 4
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0033 }
            L_0x0033:
                int[] r0 = $SwitchMap$com$android$systemui$doze$DozeMachine$State     // Catch:{ NoSuchFieldError -> 0x003e }
                com.android.systemui.doze.DozeMachine$State r1 = com.android.systemui.doze.DozeMachine.State.DOZE_AOD_PAUSED     // Catch:{ NoSuchFieldError -> 0x003e }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x003e }
                r2 = 5
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x003e }
            L_0x003e:
                int[] r0 = $SwitchMap$com$android$systemui$doze$DozeMachine$State     // Catch:{ NoSuchFieldError -> 0x0049 }
                com.android.systemui.doze.DozeMachine$State r1 = com.android.systemui.doze.DozeMachine.State.DOZE_REQUEST_PULSE     // Catch:{ NoSuchFieldError -> 0x0049 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0049 }
                r2 = 6
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0049 }
            L_0x0049:
                int[] r0 = $SwitchMap$com$android$systemui$doze$DozeMachine$State     // Catch:{ NoSuchFieldError -> 0x0054 }
                com.android.systemui.doze.DozeMachine$State r1 = com.android.systemui.doze.DozeMachine.State.INITIALIZED     // Catch:{ NoSuchFieldError -> 0x0054 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0054 }
                r2 = 7
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0054 }
            L_0x0054:
                int[] r0 = $SwitchMap$com$android$systemui$doze$DozeMachine$State     // Catch:{ NoSuchFieldError -> 0x0060 }
                com.android.systemui.doze.DozeMachine$State r1 = com.android.systemui.doze.DozeMachine.State.FINISH     // Catch:{ NoSuchFieldError -> 0x0060 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0060 }
                r2 = 8
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0060 }
            L_0x0060:
                int[] r0 = $SwitchMap$com$android$systemui$doze$DozeMachine$State     // Catch:{ NoSuchFieldError -> 0x006c }
                com.android.systemui.doze.DozeMachine$State r1 = com.android.systemui.doze.DozeMachine.State.DOZE_PULSING     // Catch:{ NoSuchFieldError -> 0x006c }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x006c }
                r2 = 9
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x006c }
            L_0x006c:
                int[] r0 = $SwitchMap$com$android$systemui$doze$DozeMachine$State     // Catch:{ NoSuchFieldError -> 0x0078 }
                com.android.systemui.doze.DozeMachine$State r1 = com.android.systemui.doze.DozeMachine.State.DOZE_PULSING_BRIGHT     // Catch:{ NoSuchFieldError -> 0x0078 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0078 }
                r2 = 10
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0078 }
            L_0x0078:
                int[] r0 = $SwitchMap$com$android$systemui$doze$DozeMachine$State     // Catch:{ NoSuchFieldError -> 0x0084 }
                com.android.systemui.doze.DozeMachine$State r1 = com.android.systemui.doze.DozeMachine.State.DOZE_PULSE_DONE     // Catch:{ NoSuchFieldError -> 0x0084 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0084 }
                r2 = 11
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0084 }
            L_0x0084:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.doze.DozeUi.C09273.<clinit>():void");
        }
    }

    public void transitionTo(DozeMachine.State state, DozeMachine.State state2) {
        switch (C09273.$SwitchMap$com$android$systemui$doze$DozeMachine$State[state2.ordinal()]) {
            case 1:
            case 2:
                if (state == DozeMachine.State.DOZE_AOD_PAUSED || state == DozeMachine.State.DOZE) {
                    this.mHost.dozeTimeTick();
                    Handler handler = this.mHandler;
                    WakeLock wakeLock = this.mWakeLock;
                    DozeHost dozeHost = this.mHost;
                    Objects.requireNonNull(dozeHost);
                    handler.postDelayed(wakeLock.wrap(new DozeUi$$ExternalSyntheticLambda1(dozeHost)), 500);
                }
                scheduleTimeTick();
                break;
            case 3:
                scheduleTimeTick();
                break;
            case 4:
            case 5:
                unscheduleTimeTick();
                break;
            case 6:
                scheduleTimeTick();
                pulseWhileDozing(this.mMachine.getPulseReason());
                break;
            case 7:
                this.mHost.startDozing();
                break;
            case 8:
                this.mHost.stopDozing();
                unscheduleTimeTick();
                break;
        }
        updateAnimateWakeup(state2);
    }

    private void updateAnimateWakeup(DozeMachine.State state) {
        switch (C09273.$SwitchMap$com$android$systemui$doze$DozeMachine$State[state.ordinal()]) {
            case 6:
            case 9:
            case 10:
            case 11:
                this.mHost.setAnimateWakeup(MotoDisplayManager.isAospAD());
                return;
            case 8:
                return;
            default:
                DozeHost dozeHost = this.mHost;
                boolean z = false;
                if (MotoDisplayManager.isAospAD() && this.mCanAnimateTransition && this.mDozeParameters.getAlwaysOn()) {
                    z = true;
                }
                dozeHost.setAnimateWakeup(z);
                return;
        }
    }

    private void scheduleTimeTick() {
        if (!this.mTimeTicker.isScheduled()) {
            long currentTimeMillis = System.currentTimeMillis();
            long roundToNextMinute = roundToNextMinute(currentTimeMillis) - System.currentTimeMillis();
            if (this.mTimeTicker.schedule(roundToNextMinute, 1)) {
                this.mDozeLog.traceTimeTickScheduled(currentTimeMillis, roundToNextMinute + currentTimeMillis);
            }
            this.mLastTimeTickElapsed = SystemClock.elapsedRealtime();
        }
    }

    private void unscheduleTimeTick() {
        if (this.mTimeTicker.isScheduled()) {
            verifyLastTimeTick();
            this.mTimeTicker.cancel();
        }
    }

    private void verifyLastTimeTick() {
        long elapsedRealtime = SystemClock.elapsedRealtime() - this.mLastTimeTickElapsed;
        if (elapsedRealtime > 90000) {
            String formatShortElapsedTime = Formatter.formatShortElapsedTime(this.mContext, elapsedRealtime);
            this.mDozeLog.traceMissedTick(formatShortElapsedTime);
            Log.e("DozeMachine", "Missed AOD time tick by " + formatShortElapsedTime);
        }
    }

    private long roundToNextMinute(long j) {
        Calendar instance = Calendar.getInstance();
        instance.setTimeInMillis(j);
        instance.set(14, 0);
        instance.set(13, 0);
        instance.add(12, 1);
        return instance.getTimeInMillis();
    }

    /* access modifiers changed from: private */
    public void onTimeTick() {
        verifyLastTimeTick();
        this.mHost.dozeTimeTick();
        this.mHandler.post(this.mWakeLock.wrap(DozeUi$$ExternalSyntheticLambda2.INSTANCE));
        scheduleTimeTick();
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public KeyguardUpdateMonitorCallback getKeyguardCallback() {
        return this.mKeyguardVisibilityCallback;
    }

    public void onTuningChanged(String str, String str2) {
        if (str.equals("doze_always_on")) {
            updateAnimateScreenOff();
        }
    }

    public void onConfigChanged(Configuration configuration) {
        updateAnimateScreenOff();
    }
}
