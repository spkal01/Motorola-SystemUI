package com.android.systemui.classifier;

import android.net.Uri;
import android.os.Build;
import android.util.IndentingPrintWriter;
import android.util.Log;
import android.view.MotionEvent;
import android.view.accessibility.AccessibilityManager;
import com.android.internal.logging.MetricsLogger;
import com.android.systemui.classifier.FalsingClassifier;
import com.android.systemui.classifier.FalsingDataProvider;
import com.android.systemui.classifier.HistoryTracker;
import com.android.systemui.plugins.FalsingManager;
import com.android.systemui.statusbar.policy.KeyguardStateController;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.StringJoiner;
import java.util.stream.Collectors;

public class BrightLineFalsingManager implements FalsingManager {
    public static final boolean DEBUG = (Log.isLoggable("FalsingManager", 3) || !Build.IS_USER);
    private static final Queue<String> RECENT_INFO_LOG = new ArrayDeque(41);
    /* access modifiers changed from: private */
    public static final Queue<DebugSwipeRecord> RECENT_SWIPES = new ArrayDeque(21);
    private AccessibilityManager mAccessibilityManager;
    private final HistoryTracker.BeliefListener mBeliefListener;
    /* access modifiers changed from: private */
    public final Collection<FalsingClassifier> mClassifiers;
    /* access modifiers changed from: private */
    public final FalsingDataProvider mDataProvider;
    private boolean mDestroyed;
    private final DoubleTapClassifier mDoubleTapClassifier;
    /* access modifiers changed from: private */
    public final List<FalsingManager.FalsingBeliefListener> mFalsingBeliefListeners = new ArrayList();
    private List<FalsingManager.FalsingTapListener> mFalsingTapListeners = new ArrayList();
    private final FalsingDataProvider.GestureFinalizedListener mGestureFinalizedListener;
    /* access modifiers changed from: private */
    public final HistoryTracker mHistoryTracker;
    private int mIsFalseTouchCalls;
    private final KeyguardStateController mKeyguardStateController;
    private final MetricsLogger mMetricsLogger;
    /* access modifiers changed from: private */
    public int mPriorInteractionType;
    /* access modifiers changed from: private */
    public Collection<FalsingClassifier.Result> mPriorResults;
    private final FalsingDataProvider.SessionListener mSessionListener;
    /* access modifiers changed from: private */
    public final SingleTapClassifier mSingleTapClassifier;
    private final boolean mTestHarness;

    public boolean isClassifierEnabled() {
        return true;
    }

    public boolean isReportingEnabled() {
        return false;
    }

    public boolean isUnlockingDisabled() {
        return false;
    }

    public Uri reportRejectedTouch() {
        return null;
    }

    public boolean shouldEnforceBouncer() {
        return false;
    }

    public BrightLineFalsingManager(FalsingDataProvider falsingDataProvider, MetricsLogger metricsLogger, Set<FalsingClassifier> set, SingleTapClassifier singleTapClassifier, DoubleTapClassifier doubleTapClassifier, HistoryTracker historyTracker, KeyguardStateController keyguardStateController, AccessibilityManager accessibilityManager, boolean z) {
        C08781 r0 = new FalsingDataProvider.SessionListener() {
            public void onSessionEnded() {
                BrightLineFalsingManager.this.mClassifiers.forEach(BrightLineFalsingManager$1$$ExternalSyntheticLambda0.INSTANCE);
            }

            public void onSessionStarted() {
                BrightLineFalsingManager.this.mClassifiers.forEach(BrightLineFalsingManager$1$$ExternalSyntheticLambda1.INSTANCE);
            }
        };
        this.mSessionListener = r0;
        C08792 r1 = new HistoryTracker.BeliefListener() {
            public void onBeliefChanged(double d) {
                BrightLineFalsingManager.logInfo(String.format("{belief=%s confidence=%s}", new Object[]{Double.valueOf(BrightLineFalsingManager.this.mHistoryTracker.falseBelief()), Double.valueOf(BrightLineFalsingManager.this.mHistoryTracker.falseConfidence())}));
                if (d > 0.9d) {
                    BrightLineFalsingManager.this.mFalsingBeliefListeners.forEach(BrightLineFalsingManager$2$$ExternalSyntheticLambda0.INSTANCE);
                    BrightLineFalsingManager.logInfo("Triggering False Event (Threshold: 0.9)");
                }
            }
        };
        this.mBeliefListener = r1;
        C08803 r2 = new FalsingDataProvider.GestureFinalizedListener() {
            public void onGestureFinalized(long j) {
                if (BrightLineFalsingManager.this.mPriorResults != null) {
                    boolean anyMatch = BrightLineFalsingManager.this.mPriorResults.stream().anyMatch(BrightLineFalsingManager$3$$ExternalSyntheticLambda2.INSTANCE);
                    BrightLineFalsingManager.this.mPriorResults.forEach(BrightLineFalsingManager$3$$ExternalSyntheticLambda0.INSTANCE);
                    if (Build.IS_ENG || Build.IS_USERDEBUG) {
                        BrightLineFalsingManager.RECENT_SWIPES.add(new DebugSwipeRecord(anyMatch, BrightLineFalsingManager.this.mPriorInteractionType, (List) BrightLineFalsingManager.this.mDataProvider.getRecentMotionEvents().stream().map(BrightLineFalsingManager$3$$ExternalSyntheticLambda1.INSTANCE).collect(Collectors.toList())));
                        while (BrightLineFalsingManager.RECENT_SWIPES.size() > 40) {
                            BrightLineFalsingManager.RECENT_SWIPES.remove();
                        }
                    }
                    BrightLineFalsingManager.this.mHistoryTracker.addResults(BrightLineFalsingManager.this.mPriorResults, j);
                    Collection unused = BrightLineFalsingManager.this.mPriorResults = null;
                    int unused2 = BrightLineFalsingManager.this.mPriorInteractionType = 7;
                    return;
                }
                BrightLineFalsingManager.this.mHistoryTracker.addResults(Collections.singleton(FalsingClassifier.Result.falsed(BrightLineFalsingManager.this.mSingleTapClassifier.isTap(BrightLineFalsingManager.this.mDataProvider.getRecentMotionEvents(), 0.0d).isFalse() ? 0.7d : 0.8d, C08803.class.getSimpleName(), "unclassified")), j);
            }

            /* access modifiers changed from: private */
            public static /* synthetic */ void lambda$onGestureFinalized$0(FalsingClassifier.Result result) {
                String reason;
                if (result.isFalse() && (reason = result.getReason()) != null) {
                    BrightLineFalsingManager.logInfo(reason);
                }
            }

            /* access modifiers changed from: private */
            public static /* synthetic */ XYDt lambda$onGestureFinalized$1(MotionEvent motionEvent) {
                return new XYDt((int) motionEvent.getX(), (int) motionEvent.getY(), (int) (motionEvent.getEventTime() - motionEvent.getDownTime()));
            }
        };
        this.mGestureFinalizedListener = r2;
        this.mPriorInteractionType = 7;
        this.mDataProvider = falsingDataProvider;
        this.mMetricsLogger = metricsLogger;
        this.mClassifiers = set;
        this.mSingleTapClassifier = singleTapClassifier;
        this.mDoubleTapClassifier = doubleTapClassifier;
        this.mHistoryTracker = historyTracker;
        this.mKeyguardStateController = keyguardStateController;
        this.mAccessibilityManager = accessibilityManager;
        this.mTestHarness = z;
        falsingDataProvider.addSessionListener(r0);
        falsingDataProvider.addGestureCompleteListener(r2);
        historyTracker.addBeliefListener(r1);
    }

    public boolean isFalseTouch(int i) {
        checkDestroyed();
        this.mPriorInteractionType = i;
        if (skipFalsing(i)) {
            this.mPriorResults = getPassedResult(1.0d);
            logDebug("Skipped falsing");
            return false;
        }
        boolean[] zArr = {false};
        this.mPriorResults = (Collection) this.mClassifiers.stream().map(new BrightLineFalsingManager$$ExternalSyntheticLambda3(this, i, zArr)).collect(Collectors.toList());
        logDebug("False Gesture: " + zArr[0]);
        return zArr[0];
    }

    /* access modifiers changed from: private */
    public /* synthetic */ FalsingClassifier.Result lambda$isFalseTouch$0(int i, boolean[] zArr, FalsingClassifier falsingClassifier) {
        FalsingClassifier.Result classifyGesture = falsingClassifier.classifyGesture(i, this.mHistoryTracker.falseBelief(), this.mHistoryTracker.falseConfidence());
        zArr[0] = zArr[0] | classifyGesture.isFalse();
        return classifyGesture;
    }

    public boolean isSimpleTap() {
        checkDestroyed();
        FalsingClassifier.Result isTap = this.mSingleTapClassifier.isTap(this.mDataProvider.getRecentMotionEvents(), 0.0d);
        this.mPriorResults = Collections.singleton(isTap);
        return !isTap.isFalse();
    }

    private void checkDestroyed() {
        if (this.mDestroyed) {
            Log.wtf("FalsingManager", "Tried to use FalsingManager after being destroyed!");
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:17:0x0048  */
    /* JADX WARNING: Removed duplicated region for block: B:18:0x004f  */
    /* JADX WARNING: Removed duplicated region for block: B:21:0x0065  */
    /* JADX WARNING: Removed duplicated region for block: B:35:0x00bf  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean isFalseTap(int r12) {
        /*
            r11 = this;
            r11.checkDestroyed()
            r0 = 7
            boolean r0 = r11.skipFalsing(r0)
            r1 = 4607182418800017408(0x3ff0000000000000, double:1.0)
            r3 = 0
            if (r0 == 0) goto L_0x0019
            java.util.Collection r12 = getPassedResult(r1)
            r11.mPriorResults = r12
            java.lang.String r11 = "Skipped falsing"
            logDebug(r11)
            return r3
        L_0x0019:
            r4 = 4591870180066957722(0x3fb999999999999a, double:0.1)
            r0 = 1
            r6 = 0
            if (r12 == 0) goto L_0x002b
            if (r12 == r0) goto L_0x0039
            r8 = 2
            if (r12 == r8) goto L_0x0033
            r8 = 3
            if (r12 == r8) goto L_0x002d
        L_0x002b:
            r8 = r6
            goto L_0x003a
        L_0x002d:
            r8 = 4603579539098121011(0x3fe3333333333333, double:0.6)
            goto L_0x003a
        L_0x0033:
            r8 = 4599075939470750515(0x3fd3333333333333, double:0.3)
            goto L_0x003a
        L_0x0039:
            r8 = r4
        L_0x003a:
            com.android.systemui.classifier.SingleTapClassifier r12 = r11.mSingleTapClassifier
            com.android.systemui.classifier.FalsingDataProvider r10 = r11.mDataProvider
            java.util.List r10 = r10.getRecentMotionEvents()
            boolean r10 = r10.isEmpty()
            if (r10 == 0) goto L_0x004f
            com.android.systemui.classifier.FalsingDataProvider r10 = r11.mDataProvider
            java.util.List r10 = r10.getPriorMotionEvents()
            goto L_0x0055
        L_0x004f:
            com.android.systemui.classifier.FalsingDataProvider r10 = r11.mDataProvider
            java.util.List r10 = r10.getRecentMotionEvents()
        L_0x0055:
            com.android.systemui.classifier.FalsingClassifier$Result r12 = r12.isTap(r10, r8)
            java.util.Set r8 = java.util.Collections.singleton(r12)
            r11.mPriorResults = r8
            boolean r8 = r12.isFalse()
            if (r8 != 0) goto L_0x00bf
            com.android.systemui.classifier.FalsingDataProvider r12 = r11.mDataProvider
            boolean r12 = r12.isJustUnlockedWithFace()
            if (r12 == 0) goto L_0x0079
            java.util.Collection r12 = getPassedResult(r1)
            r11.mPriorResults = r12
            java.lang.String r11 = "False Single Tap: false (face detected)"
            logDebug(r11)
            return r3
        L_0x0079:
            boolean r12 = r11.isFalseDoubleTap()
            if (r12 != 0) goto L_0x0085
            java.lang.String r11 = "False Single Tap: false (double tapped)"
            logDebug(r11)
            return r3
        L_0x0085:
            com.android.systemui.classifier.HistoryTracker r12 = r11.mHistoryTracker
            double r1 = r12.falseBelief()
            r8 = 4604480259023595110(0x3fe6666666666666, double:0.7)
            int r12 = (r1 > r8 ? 1 : (r1 == r8 ? 0 : -1))
            if (r12 <= 0) goto L_0x00b3
            java.lang.Class<com.android.systemui.classifier.BrightLineFalsingManager> r12 = com.android.systemui.classifier.BrightLineFalsingManager.class
            java.lang.String r12 = r12.getSimpleName()
            java.lang.String r1 = "bad history"
            com.android.systemui.classifier.FalsingClassifier$Result r12 = com.android.systemui.classifier.FalsingClassifier.Result.falsed(r6, r12, r1)
            java.util.Set r12 = java.util.Collections.singleton(r12)
            r11.mPriorResults = r12
            java.lang.String r12 = "False Single Tap: true (bad history)"
            logDebug(r12)
            java.util.List<com.android.systemui.plugins.FalsingManager$FalsingTapListener> r11 = r11.mFalsingTapListeners
            com.android.systemui.classifier.BrightLineFalsingManager$$ExternalSyntheticLambda2 r12 = com.android.systemui.classifier.BrightLineFalsingManager$$ExternalSyntheticLambda2.INSTANCE
            r11.forEach(r12)
            return r0
        L_0x00b3:
            java.util.Collection r12 = getPassedResult(r4)
            r11.mPriorResults = r12
            java.lang.String r11 = "False Single Tap: false (default)"
            logDebug(r11)
            return r3
        L_0x00bf:
            java.lang.StringBuilder r11 = new java.lang.StringBuilder
            r11.<init>()
            java.lang.String r0 = "False Single Tap: "
            r11.append(r0)
            boolean r0 = r12.isFalse()
            r11.append(r0)
            java.lang.String r0 = " (simple)"
            r11.append(r0)
            java.lang.String r11 = r11.toString()
            logDebug(r11)
            boolean r11 = r12.isFalse()
            return r11
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.classifier.BrightLineFalsingManager.isFalseTap(int):boolean");
    }

    public boolean isFalseDoubleTap() {
        checkDestroyed();
        if (skipFalsing(7)) {
            this.mPriorResults = getPassedResult(1.0d);
            logDebug("Skipped falsing");
            return false;
        }
        FalsingClassifier.Result classifyGesture = this.mDoubleTapClassifier.classifyGesture(7, this.mHistoryTracker.falseBelief(), this.mHistoryTracker.falseConfidence());
        this.mPriorResults = Collections.singleton(classifyGesture);
        logDebug("False Double Tap: " + classifyGesture.isFalse());
        return classifyGesture.isFalse();
    }

    private boolean skipFalsing(int i) {
        return i == 16 || !this.mKeyguardStateController.isShowing() || this.mTestHarness || this.mDataProvider.isJustUnlockedWithFace() || this.mDataProvider.isDocked() || this.mAccessibilityManager.isEnabled();
    }

    public void onProximityEvent(FalsingManager.ProximityEvent proximityEvent) {
        this.mClassifiers.forEach(new BrightLineFalsingManager$$ExternalSyntheticLambda0(proximityEvent));
    }

    public void onSuccessfulUnlock() {
        int i = this.mIsFalseTouchCalls;
        if (i != 0) {
            this.mMetricsLogger.histogram("falsing_success_after_attempts", i);
            this.mIsFalseTouchCalls = 0;
        }
    }

    public void addFalsingBeliefListener(FalsingManager.FalsingBeliefListener falsingBeliefListener) {
        this.mFalsingBeliefListeners.add(falsingBeliefListener);
    }

    public void removeFalsingBeliefListener(FalsingManager.FalsingBeliefListener falsingBeliefListener) {
        this.mFalsingBeliefListeners.remove(falsingBeliefListener);
    }

    public void addTapListener(FalsingManager.FalsingTapListener falsingTapListener) {
        this.mFalsingTapListeners.add(falsingTapListener);
    }

    public void removeTapListener(FalsingManager.FalsingTapListener falsingTapListener) {
        this.mFalsingTapListeners.remove(falsingTapListener);
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        IndentingPrintWriter indentingPrintWriter = new IndentingPrintWriter(printWriter, "  ");
        indentingPrintWriter.println("BRIGHTLINE FALSING MANAGER");
        indentingPrintWriter.print("classifierEnabled=");
        indentingPrintWriter.println(isClassifierEnabled() ? 1 : 0);
        indentingPrintWriter.print("mJustUnlockedWithFace=");
        indentingPrintWriter.println(this.mDataProvider.isJustUnlockedWithFace() ? 1 : 0);
        indentingPrintWriter.print("isDocked=");
        indentingPrintWriter.println(this.mDataProvider.isDocked() ? 1 : 0);
        indentingPrintWriter.print("width=");
        indentingPrintWriter.println(this.mDataProvider.getWidthPixels());
        indentingPrintWriter.print("height=");
        indentingPrintWriter.println(this.mDataProvider.getHeightPixels());
        indentingPrintWriter.println();
        Queue<DebugSwipeRecord> queue = RECENT_SWIPES;
        if (queue.size() != 0) {
            indentingPrintWriter.println("Recent swipes:");
            indentingPrintWriter.increaseIndent();
            for (DebugSwipeRecord string : queue) {
                indentingPrintWriter.println(string.getString());
                indentingPrintWriter.println();
            }
            indentingPrintWriter.decreaseIndent();
        } else {
            indentingPrintWriter.println("No recent swipes");
        }
        indentingPrintWriter.println();
        indentingPrintWriter.println("Recent falsing info:");
        indentingPrintWriter.increaseIndent();
        for (String println : RECENT_INFO_LOG) {
            indentingPrintWriter.println(println);
        }
        indentingPrintWriter.println();
    }

    public void cleanupInternal() {
        this.mDestroyed = true;
        this.mDataProvider.removeSessionListener(this.mSessionListener);
        this.mDataProvider.removeGestureCompleteListener(this.mGestureFinalizedListener);
        this.mClassifiers.forEach(BrightLineFalsingManager$$ExternalSyntheticLambda1.INSTANCE);
        this.mFalsingBeliefListeners.clear();
        this.mHistoryTracker.removeBeliefListener(this.mBeliefListener);
    }

    private static Collection<FalsingClassifier.Result> getPassedResult(double d) {
        return Collections.singleton(FalsingClassifier.Result.passed(d));
    }

    static void logDebug(String str) {
        logDebug(str, (Throwable) null);
    }

    static void logDebug(String str, Throwable th) {
        if (DEBUG) {
            Log.d("FalsingManager", str, th);
        }
    }

    static void logInfo(String str) {
        Log.i("FalsingManager", str);
        RECENT_INFO_LOG.add(str);
        while (true) {
            Queue<String> queue = RECENT_INFO_LOG;
            if (queue.size() > 40) {
                queue.remove();
            } else {
                return;
            }
        }
    }

    private static class DebugSwipeRecord {
        private final int mInteractionType;
        private final boolean mIsFalse;
        private final List<XYDt> mRecentMotionEvents;

        DebugSwipeRecord(boolean z, int i, List<XYDt> list) {
            this.mIsFalse = z;
            this.mInteractionType = i;
            this.mRecentMotionEvents = list;
        }

        /* access modifiers changed from: package-private */
        public String getString() {
            StringJoiner stringJoiner = new StringJoiner(",");
            stringJoiner.add(Integer.toString(1)).add(this.mIsFalse ? "1" : "0").add(Integer.toString(this.mInteractionType));
            for (XYDt xYDt : this.mRecentMotionEvents) {
                stringJoiner.add(xYDt.toString());
            }
            return stringJoiner.toString();
        }
    }

    private static class XYDt {
        private final int mDT;

        /* renamed from: mX */
        private final int f78mX;

        /* renamed from: mY */
        private final int f79mY;

        XYDt(int i, int i2, int i3) {
            this.f78mX = i;
            this.f79mY = i2;
            this.mDT = i3;
        }

        public String toString() {
            return this.f78mX + "," + this.f79mY + "," + this.mDT;
        }
    }
}
