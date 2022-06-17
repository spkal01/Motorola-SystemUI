package com.android.systemui.statusbar.phone.ongoingcall;

import android.app.IActivityManager;
import android.app.IUidObserver;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import com.android.systemui.R$id;
import com.android.systemui.plugins.ActivityStarter;
import com.android.systemui.statusbar.FeatureFlags;
import com.android.systemui.statusbar.notification.collection.notifcollection.CommonNotifCollection;
import com.android.systemui.statusbar.policy.CallbackController;
import com.android.systemui.util.time.SystemClock;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import kotlin.Unit;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/* compiled from: OngoingCallController.kt */
public final class OngoingCallController implements CallbackController<OngoingCallListener> {
    /* access modifiers changed from: private */
    @NotNull
    public final ActivityStarter activityStarter;
    /* access modifiers changed from: private */
    @Nullable
    public CallNotificationInfo callNotificationInfo;
    @Nullable
    private View chipView;
    @NotNull
    private final FeatureFlags featureFlags;
    @NotNull
    private final IActivityManager iActivityManager;
    /* access modifiers changed from: private */
    public boolean isCallAppVisible = true;
    /* access modifiers changed from: private */
    @NotNull
    public final OngoingCallLogger logger;
    /* access modifiers changed from: private */
    @NotNull
    public final List<OngoingCallListener> mListeners = new ArrayList();
    /* access modifiers changed from: private */
    @NotNull
    public final Executor mainExecutor;
    @NotNull
    private final CommonNotifCollection notifCollection;
    @NotNull
    private final OngoingCallController$notifListener$1 notifListener = new OngoingCallController$notifListener$1(this);
    @NotNull
    private final SystemClock systemClock;
    @Nullable
    private IUidObserver.Stub uidObserver;

    /* access modifiers changed from: private */
    public final boolean isProcessVisibleToUser(int i) {
        return i <= 2;
    }

    public OngoingCallController(@NotNull CommonNotifCollection commonNotifCollection, @NotNull FeatureFlags featureFlags2, @NotNull SystemClock systemClock2, @NotNull ActivityStarter activityStarter2, @NotNull Executor executor, @NotNull IActivityManager iActivityManager2, @NotNull OngoingCallLogger ongoingCallLogger) {
        Intrinsics.checkNotNullParameter(commonNotifCollection, "notifCollection");
        Intrinsics.checkNotNullParameter(featureFlags2, "featureFlags");
        Intrinsics.checkNotNullParameter(systemClock2, "systemClock");
        Intrinsics.checkNotNullParameter(activityStarter2, "activityStarter");
        Intrinsics.checkNotNullParameter(executor, "mainExecutor");
        Intrinsics.checkNotNullParameter(iActivityManager2, "iActivityManager");
        Intrinsics.checkNotNullParameter(ongoingCallLogger, "logger");
        this.notifCollection = commonNotifCollection;
        this.featureFlags = featureFlags2;
        this.systemClock = systemClock2;
        this.activityStarter = activityStarter2;
        this.mainExecutor = executor;
        this.iActivityManager = iActivityManager2;
        this.logger = ongoingCallLogger;
    }

    public final void init() {
        if (this.featureFlags.isOngoingCallStatusBarChipEnabled()) {
            this.notifCollection.addCollectionListener(this.notifListener);
        }
    }

    public final void setChipView(@NotNull View view) {
        Intrinsics.checkNotNullParameter(view, "chipView");
        tearDownChipView();
        this.chipView = view;
        if (hasOngoingCall()) {
            updateChip();
        }
    }

    public final void notifyChipVisibilityChanged(boolean z) {
        this.logger.logChipVisibilityChanged(z);
    }

    public final boolean hasOngoingCall() {
        CallNotificationInfo callNotificationInfo2 = this.callNotificationInfo;
        return Intrinsics.areEqual((Object) callNotificationInfo2 == null ? null : Boolean.valueOf(callNotificationInfo2.isOngoing()), (Object) Boolean.TRUE) && !this.isCallAppVisible;
    }

    public void addCallback(@NotNull OngoingCallListener ongoingCallListener) {
        Intrinsics.checkNotNullParameter(ongoingCallListener, "listener");
        synchronized (this.mListeners) {
            if (!this.mListeners.contains(ongoingCallListener)) {
                this.mListeners.add(ongoingCallListener);
            }
            Unit unit = Unit.INSTANCE;
        }
    }

    public void removeCallback(@NotNull OngoingCallListener ongoingCallListener) {
        Intrinsics.checkNotNullParameter(ongoingCallListener, "listener");
        synchronized (this.mListeners) {
            this.mListeners.remove(ongoingCallListener);
        }
    }

    /* access modifiers changed from: private */
    public final void updateChip() {
        OngoingCallChronometer ongoingCallChronometer;
        View view;
        CallNotificationInfo callNotificationInfo2 = this.callNotificationInfo;
        if (callNotificationInfo2 != null) {
            View view2 = this.chipView;
            if (view2 == null) {
                ongoingCallChronometer = null;
            } else {
                ongoingCallChronometer = getTimeView(view2);
            }
            if (view2 == null) {
                view = null;
            } else {
                view = view2.findViewById(R$id.ongoing_call_chip_background);
            }
            if (view2 == null || ongoingCallChronometer == null || view == null) {
                this.callNotificationInfo = null;
                if (OngoingCallControllerKt.DEBUG) {
                    Log.w("OngoingCallController", "Ongoing call chip view could not be found; Not displaying chip in status bar");
                    return;
                }
                return;
            }
            if (callNotificationInfo2.hasValidStartTime()) {
                ongoingCallChronometer.setShouldHideText(false);
                ongoingCallChronometer.setBase((callNotificationInfo2.getCallStartTime() - this.systemClock.currentTimeMillis()) + this.systemClock.elapsedRealtime());
                ongoingCallChronometer.start();
            } else {
                ongoingCallChronometer.setShouldHideText(true);
                ongoingCallChronometer.stop();
            }
            Intent intent = callNotificationInfo2.getIntent();
            if (intent != null) {
                view2.setOnClickListener(new OngoingCallController$updateChip$1$1(this, intent, view));
            }
            setUpUidObserver(callNotificationInfo2);
            for (OngoingCallListener onOngoingCallStateChanged : this.mListeners) {
                onOngoingCallStateChanged.onOngoingCallStateChanged(true);
            }
        }
    }

    private final void setUpUidObserver(CallNotificationInfo callNotificationInfo2) {
        this.isCallAppVisible = isProcessVisibleToUser(this.iActivityManager.getUidProcessState(callNotificationInfo2.getUid(), (String) null));
        IUidObserver.Stub stub = this.uidObserver;
        if (stub != null) {
            this.iActivityManager.unregisterUidObserver(stub);
        }
        OngoingCallController$setUpUidObserver$1 ongoingCallController$setUpUidObserver$1 = new OngoingCallController$setUpUidObserver$1(callNotificationInfo2, this);
        this.uidObserver = ongoingCallController$setUpUidObserver$1;
        this.iActivityManager.registerUidObserver(ongoingCallController$setUpUidObserver$1, 1, -1, (String) null);
    }

    /* access modifiers changed from: private */
    public final void removeChip() {
        this.callNotificationInfo = null;
        tearDownChipView();
        for (OngoingCallListener onOngoingCallStateChanged : this.mListeners) {
            onOngoingCallStateChanged.onOngoingCallStateChanged(true);
        }
        IUidObserver.Stub stub = this.uidObserver;
        if (stub != null) {
            this.iActivityManager.unregisterUidObserver(stub);
        }
    }

    @Nullable
    public final Unit tearDownChipView() {
        OngoingCallChronometer timeView;
        View view = this.chipView;
        if (view == null || (timeView = getTimeView(view)) == null) {
            return null;
        }
        timeView.stop();
        return Unit.INSTANCE;
    }

    private final OngoingCallChronometer getTimeView(View view) {
        return (OngoingCallChronometer) view.findViewById(R$id.ongoing_call_chip_time);
    }

    /* compiled from: OngoingCallController.kt */
    private static final class CallNotificationInfo {
        private final long callStartTime;
        @Nullable
        private final Intent intent;
        private final boolean isOngoing;
        @NotNull
        private final String key;
        private final int uid;

        public boolean equals(@Nullable Object obj) {
            if (this == obj) {
                return true;
            }
            if (!(obj instanceof CallNotificationInfo)) {
                return false;
            }
            CallNotificationInfo callNotificationInfo = (CallNotificationInfo) obj;
            return Intrinsics.areEqual((Object) this.key, (Object) callNotificationInfo.key) && this.callStartTime == callNotificationInfo.callStartTime && Intrinsics.areEqual((Object) this.intent, (Object) callNotificationInfo.intent) && this.uid == callNotificationInfo.uid && this.isOngoing == callNotificationInfo.isOngoing;
        }

        public int hashCode() {
            int hashCode = ((this.key.hashCode() * 31) + Long.hashCode(this.callStartTime)) * 31;
            Intent intent2 = this.intent;
            int hashCode2 = (((hashCode + (intent2 == null ? 0 : intent2.hashCode())) * 31) + Integer.hashCode(this.uid)) * 31;
            boolean z = this.isOngoing;
            if (z) {
                z = true;
            }
            return hashCode2 + (z ? 1 : 0);
        }

        @NotNull
        public String toString() {
            return "CallNotificationInfo(key=" + this.key + ", callStartTime=" + this.callStartTime + ", intent=" + this.intent + ", uid=" + this.uid + ", isOngoing=" + this.isOngoing + ')';
        }

        public CallNotificationInfo(@NotNull String str, long j, @Nullable Intent intent2, int i, boolean z) {
            Intrinsics.checkNotNullParameter(str, "key");
            this.key = str;
            this.callStartTime = j;
            this.intent = intent2;
            this.uid = i;
            this.isOngoing = z;
        }

        @NotNull
        public final String getKey() {
            return this.key;
        }

        public final long getCallStartTime() {
            return this.callStartTime;
        }

        @Nullable
        public final Intent getIntent() {
            return this.intent;
        }

        public final int getUid() {
            return this.uid;
        }

        public final boolean isOngoing() {
            return this.isOngoing;
        }

        public final boolean hasValidStartTime() {
            return this.callStartTime > 0;
        }
    }
}
