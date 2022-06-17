package com.android.systemui.statusbar.phone;

import android.os.Handler;
import android.util.Log;
import com.android.internal.annotations.VisibleForTesting;
import com.android.systemui.Dependency;
import com.android.systemui.doze.DozeHost;
import com.android.systemui.doze.DozeLog;
import com.android.systemui.doze.MotoDisplayManager;
import com.android.systemui.moto.MotoFeature;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.statusbar.phone.ScrimController;

public class DozeScrimController implements StatusBarStateController.StateListener {
    /* access modifiers changed from: private */
    public static final boolean DEBUG = Log.isLoggable("DozeScrimController", 3);
    /* access modifiers changed from: private */
    public long mDarkenTransitionDuration;
    private final DozeLog mDozeLog;
    /* access modifiers changed from: private */
    public final DozeParameters mDozeParameters;
    /* access modifiers changed from: private */
    public boolean mDozing;
    /* access modifiers changed from: private */
    public boolean mFullyPulsing;
    /* access modifiers changed from: private */
    public final Handler mHandler = new Handler();
    private DozeHost.PulseCallback mPulseCallback;
    /* access modifiers changed from: private */
    public final Runnable mPulseFinished = new DozeScrimController$$ExternalSyntheticLambda0(this);
    /* access modifiers changed from: private */
    public final Runnable mPulseOut = new Runnable() {
        public void run() {
            boolean unused = DozeScrimController.this.mFullyPulsing = false;
            DozeScrimController.this.mHandler.removeCallbacks(DozeScrimController.this.mPulseOut);
            DozeScrimController.this.mHandler.removeCallbacks(DozeScrimController.this.mPulseOutExtended);
            if (DozeScrimController.DEBUG) {
                Log.d("DozeScrimController", "Pulse out delay, mDozing=" + DozeScrimController.this.mDozing);
            }
            if (DozeScrimController.this.mDozing) {
                ((MotoDisplayManager) Dependency.get(MotoDisplayManager.class)).setDarkenBrightnessForCli();
                DozeScrimController.this.mHandler.postDelayed(DozeScrimController.this.mPulseFinished, DozeScrimController.this.mDarkenTransitionDuration);
            }
        }
    };
    /* access modifiers changed from: private */
    public final Runnable mPulseOutExtended = new Runnable() {
        public void run() {
            DozeScrimController.this.mHandler.removeCallbacks(DozeScrimController.this.mPulseOut);
            DozeScrimController.this.mPulseOut.run();
        }
    };
    private final Runnable mPulseOutNow = new Runnable() {
        public void run() {
            boolean unused = DozeScrimController.this.mFullyPulsing = false;
            DozeScrimController.this.mHandler.removeCallbacks(DozeScrimController.this.mPulseOut);
            DozeScrimController.this.mHandler.removeCallbacks(DozeScrimController.this.mPulseOutExtended);
            if (DozeScrimController.DEBUG) {
                Log.d("DozeScrimController", "Pulse out now, mDozing=" + DozeScrimController.this.mDozing);
            }
            if (DozeScrimController.this.mDozing) {
                DozeScrimController.this.lambda$new$0();
            }
        }
    };
    /* access modifiers changed from: private */
    public int mPulseReason;
    private final ScrimController.Callback mScrimCallback = new ScrimController.Callback() {
        public void onDisplayBlanked() {
            if (DozeScrimController.DEBUG) {
                Log.d("DozeScrimController", "Pulse in, mDozing=" + DozeScrimController.this.mDozing + " mPulseReason=" + DozeLog.reasonToString(DozeScrimController.this.mPulseReason));
            }
            if (DozeScrimController.this.mDozing) {
                DozeScrimController.this.pulseStarted();
            }
        }

        public void onFinished() {
            if (DozeScrimController.DEBUG) {
                Log.d("DozeScrimController", "Pulse in finished, mDozing=" + DozeScrimController.this.mDozing);
            }
            if (DozeScrimController.this.mDozing) {
                if (DozeScrimController.this.mPulseReason != 12 || !((MotoDisplayManager) Dependency.get(MotoDisplayManager.class)).isCliAndLidClose()) {
                    long unused = DozeScrimController.this.mDarkenTransitionDuration = 0;
                } else {
                    long unused2 = DozeScrimController.this.mDarkenTransitionDuration = 2000;
                }
                if (!(!MotoDisplayManager.isAospAD() || DozeScrimController.this.mPulseReason == 1 || DozeScrimController.this.mPulseReason == 6) || DozeScrimController.this.mPulseReason == 12) {
                    DozeScrimController.this.mHandler.postDelayed(DozeScrimController.this.mPulseOut, ((long) DozeScrimController.this.mDozeParameters.getPulseVisibleDuration()) - DozeScrimController.this.mDarkenTransitionDuration);
                    DozeScrimController.this.mHandler.postDelayed(DozeScrimController.this.mPulseOutExtended, ((long) DozeScrimController.this.mDozeParameters.getPulseVisibleDurationExtended()) - DozeScrimController.this.mDarkenTransitionDuration);
                }
                boolean unused3 = DozeScrimController.this.mFullyPulsing = true;
            }
        }

        public void onCancelled() {
            DozeScrimController.this.lambda$new$0();
        }
    };

    public void onStateChanged(int i) {
    }

    public DozeScrimController(DozeParameters dozeParameters, DozeLog dozeLog) {
        this.mDozeParameters = dozeParameters;
        ((StatusBarStateController) Dependency.get(StatusBarStateController.class)).addCallback(this);
        this.mDozeLog = dozeLog;
    }

    @VisibleForTesting
    public void setDozing(boolean z) {
        if (this.mDozing != z) {
            this.mDozing = z;
            if (!z) {
                cancelPulsing();
            }
        }
    }

    public void pulse(DozeHost.PulseCallback pulseCallback, int i) {
        if (pulseCallback == null) {
            throw new IllegalArgumentException("callback must not be null");
        } else if (!this.mDozing || this.mPulseCallback != null) {
            if (DEBUG) {
                StringBuilder sb = new StringBuilder();
                sb.append("Pulse supressed. Dozing: ");
                sb.append(this.mDozeParameters);
                sb.append(" had callback? ");
                sb.append(this.mPulseCallback != null);
                Log.d("DozeScrimController", sb.toString());
            }
            pulseCallback.onPulseFinished();
        } else {
            this.mPulseCallback = pulseCallback;
            this.mPulseReason = i;
        }
    }

    public void pulseOutNow() {
        if (this.mPulseCallback != null && this.mFullyPulsing) {
            this.mPulseOutNow.run();
        }
    }

    public boolean isPulsing() {
        return this.mPulseCallback != null;
    }

    public void extendPulse() {
        this.mHandler.removeCallbacks(this.mPulseOut);
    }

    public void cancelPendingPulseTimeout() {
        if (!MotoFeature.getExistedInstance().isSupportCli()) {
            this.mHandler.removeCallbacks(this.mPulseOut);
            this.mHandler.removeCallbacks(this.mPulseOutExtended);
        }
    }

    public void resetCliPulse(int i) {
        if (this.mPulseReason == 12 && isPulsing()) {
            this.mHandler.removeCallbacks(this.mPulseOut);
            this.mHandler.removeCallbacks(this.mPulseOutExtended);
            this.mHandler.removeCallbacks(this.mPulseFinished);
            ((MotoDisplayManager) Dependency.get(MotoDisplayManager.class)).resetCliDozeBrightness();
            this.mHandler.postDelayed(this.mPulseOutExtended, ((long) i) - this.mDarkenTransitionDuration);
        }
    }

    private void cancelPulsing() {
        if (this.mPulseCallback != null) {
            if (DEBUG) {
                Log.d("DozeScrimController", "Cancel pulsing");
            }
            this.mFullyPulsing = false;
            this.mHandler.removeCallbacks(this.mPulseOut);
            this.mHandler.removeCallbacks(this.mPulseOutExtended);
            lambda$new$0();
        }
    }

    /* access modifiers changed from: private */
    public void pulseStarted() {
        this.mDozeLog.tracePulseStart(this.mPulseReason);
        DozeHost.PulseCallback pulseCallback = this.mPulseCallback;
        if (pulseCallback != null) {
            pulseCallback.onPulseStarted();
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: pulseFinished */
    public void lambda$new$0() {
        this.mDozeLog.tracePulseFinish();
        DozeHost.PulseCallback pulseCallback = this.mPulseCallback;
        if (pulseCallback != null) {
            pulseCallback.onPulseFinished();
            this.mPulseCallback = null;
        }
    }

    public ScrimController.Callback getScrimCallback() {
        return this.mScrimCallback;
    }

    public void onDozingChanged(boolean z) {
        setDozing(z);
    }
}
