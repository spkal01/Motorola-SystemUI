package com.android.systemui.privacy;

import android.content.IntentFilter;
import android.os.UserHandle;
import androidx.constraintlayout.widget.R$styleable;
import com.android.internal.annotations.VisibleForTesting;
import com.android.systemui.Dumpable;
import com.android.systemui.appops.AppOpItem;
import com.android.systemui.appops.AppOpsController;
import com.android.systemui.dump.DumpManager;
import com.android.systemui.privacy.logging.PrivacyLogger;
import com.android.systemui.settings.UserTracker;
import com.android.systemui.util.DeviceConfigProxy;
import com.android.systemui.util.concurrency.DelayableExecutor;
import com.android.systemui.util.time.SystemClock;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Executor;
import kotlin.Unit;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/* compiled from: PrivacyItemController.kt */
public final class PrivacyItemController implements Dumpable {
    @NotNull
    public static final Companion Companion;
    /* access modifiers changed from: private */
    @NotNull
    public static final int[] OPS;
    /* access modifiers changed from: private */
    @NotNull
    public static final int[] OPS_LOCATION = {0, 1};
    /* access modifiers changed from: private */
    @NotNull
    public static final int[] OPS_MIC_CAMERA = {26, R$styleable.Constraint_layout_goneMarginRight, 27, 100};
    @NotNull
    private static final IntentFilter intentFilter;
    private boolean allIndicatorsAvailable;
    @NotNull
    private final AppOpsController appOpsController;
    @NotNull
    private final DelayableExecutor bgExecutor;
    /* access modifiers changed from: private */
    @NotNull
    public final List<WeakReference<Callback>> callbacks = new ArrayList();
    @NotNull

    /* renamed from: cb */
    private final PrivacyItemController$cb$1 f112cb;
    /* access modifiers changed from: private */
    @NotNull
    public List<Integer> currentUserIds = CollectionsKt__CollectionsKt.emptyList();
    @NotNull
    private final DeviceConfigProxy deviceConfigProxy;
    @NotNull
    private final PrivacyItemController$devicePropertiesChangedListener$1 devicePropertiesChangedListener;
    @Nullable
    private Runnable holdingRunnableCanceler;
    /* access modifiers changed from: private */
    @NotNull
    public final MyExecutor internalUiExecutor;
    private boolean listening;
    private boolean locationAvailable;
    /* access modifiers changed from: private */
    @NotNull
    public final PrivacyLogger logger;
    /* access modifiers changed from: private */
    public boolean micCameraAvailable;
    /* access modifiers changed from: private */
    @NotNull
    public final Runnable notifyChanges;
    @NotNull
    private List<PrivacyItem> privacyList = CollectionsKt__CollectionsKt.emptyList();
    @NotNull
    private final SystemClock systemClock;
    /* access modifiers changed from: private */
    @NotNull
    public final Runnable updateListAndNotifyChanges;
    /* access modifiers changed from: private */
    @NotNull
    public final UserTracker userTracker;
    @NotNull
    private UserTracker.Callback userTrackerCallback;

    /* compiled from: PrivacyItemController.kt */
    public interface Callback {
        void onFlagLocationChanged(boolean z) {
        }

        void onFlagMicCameraChanged(boolean z) {
        }

        void onPrivacyItemsChanged(@NotNull List<PrivacyItem> list);
    }

    @VisibleForTesting
    /* renamed from: getPrivacyList$frameworks__base__packages__SystemUI__android_common__SystemUI_core$annotations */
    public static /* synthetic */ void m45xaaa48fd6() {
    }

    @VisibleForTesting
    /* renamed from: getUserTrackerCallback$frameworks__base__packages__SystemUI__android_common__SystemUI_core$annotations */
    public static /* synthetic */ void m46x9bde9e2() {
    }

    public PrivacyItemController(@NotNull AppOpsController appOpsController2, @NotNull DelayableExecutor delayableExecutor, @NotNull DelayableExecutor delayableExecutor2, @NotNull DeviceConfigProxy deviceConfigProxy2, @NotNull UserTracker userTracker2, @NotNull PrivacyLogger privacyLogger, @NotNull SystemClock systemClock2, @NotNull DumpManager dumpManager) {
        Intrinsics.checkNotNullParameter(appOpsController2, "appOpsController");
        Intrinsics.checkNotNullParameter(delayableExecutor, "uiExecutor");
        Intrinsics.checkNotNullParameter(delayableExecutor2, "bgExecutor");
        Intrinsics.checkNotNullParameter(deviceConfigProxy2, "deviceConfigProxy");
        Intrinsics.checkNotNullParameter(userTracker2, "userTracker");
        Intrinsics.checkNotNullParameter(privacyLogger, "logger");
        Intrinsics.checkNotNullParameter(systemClock2, "systemClock");
        Intrinsics.checkNotNullParameter(dumpManager, "dumpManager");
        this.appOpsController = appOpsController2;
        this.bgExecutor = delayableExecutor2;
        this.deviceConfigProxy = deviceConfigProxy2;
        this.userTracker = userTracker2;
        this.logger = privacyLogger;
        this.systemClock = systemClock2;
        this.internalUiExecutor = new MyExecutor(this, delayableExecutor);
        this.notifyChanges = new PrivacyItemController$notifyChanges$1(this);
        this.updateListAndNotifyChanges = new PrivacyItemController$updateListAndNotifyChanges$1(this, delayableExecutor);
        this.micCameraAvailable = isMicCameraEnabled();
        boolean isLocationEnabled = isLocationEnabled();
        this.locationAvailable = isLocationEnabled;
        this.allIndicatorsAvailable = this.micCameraAvailable && isLocationEnabled;
        PrivacyItemController$devicePropertiesChangedListener$1 privacyItemController$devicePropertiesChangedListener$1 = new PrivacyItemController$devicePropertiesChangedListener$1(this);
        this.devicePropertiesChangedListener = privacyItemController$devicePropertiesChangedListener$1;
        this.f112cb = new PrivacyItemController$cb$1(this);
        this.userTrackerCallback = new PrivacyItemController$userTrackerCallback$1(this);
        deviceConfigProxy2.addOnPropertiesChangedListener("privacy", delayableExecutor, privacyItemController$devicePropertiesChangedListener$1);
        dumpManager.registerDumpable("PrivacyItemController", this);
    }

    @VisibleForTesting
    /* compiled from: PrivacyItemController.kt */
    public static final class Companion {
        public /* synthetic */ Companion(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }

        @VisibleForTesting
        public static /* synthetic */ void getTIME_TO_HOLD_INDICATORS$annotations() {
        }

        private Companion() {
        }

        @NotNull
        public final int[] getOPS_MIC_CAMERA() {
            return PrivacyItemController.OPS_MIC_CAMERA;
        }

        @NotNull
        public final int[] getOPS_LOCATION() {
            return PrivacyItemController.OPS_LOCATION;
        }

        @NotNull
        public final int[] getOPS() {
            return PrivacyItemController.OPS;
        }
    }

    static {
        Companion companion = new Companion((DefaultConstructorMarker) null);
        Companion = companion;
        OPS = ArraysKt___ArraysJvmKt.plus(companion.getOPS_MIC_CAMERA(), companion.getOPS_LOCATION());
        IntentFilter intentFilter2 = new IntentFilter();
        intentFilter2.addAction("android.intent.action.USER_SWITCHED");
        intentFilter2.addAction("android.intent.action.MANAGED_PROFILE_AVAILABLE");
        intentFilter2.addAction("android.intent.action.MANAGED_PROFILE_UNAVAILABLE");
        intentFilter = intentFilter2;
    }

    @NotNull
    /* renamed from: getPrivacyList$frameworks__base__packages__SystemUI__android_common__SystemUI_core */
    public final synchronized List<PrivacyItem> mo15937xca4da456() {
        return CollectionsKt___CollectionsKt.toList(this.privacyList);
    }

    private final boolean isMicCameraEnabled() {
        return this.deviceConfigProxy.getBoolean("privacy", "camera_mic_icons_enabled", true);
    }

    private final boolean isLocationEnabled() {
        return this.deviceConfigProxy.getBoolean("privacy", "location_indicators_enabled", false);
    }

    public final boolean getMicCameraAvailable() {
        return this.micCameraAvailable;
    }

    public final boolean getLocationAvailable() {
        return this.locationAvailable;
    }

    public final void setLocationAvailable(boolean z) {
        this.locationAvailable = z;
    }

    public final boolean getAllIndicatorsAvailable() {
        return this.allIndicatorsAvailable;
    }

    public final void setAllIndicatorsAvailable(boolean z) {
        this.allIndicatorsAvailable = z;
    }

    private final void unregisterListener() {
        this.userTracker.removeCallback(this.userTrackerCallback);
    }

    private final void registerReceiver() {
        this.userTracker.addCallback(this.userTrackerCallback, this.bgExecutor);
    }

    /* access modifiers changed from: private */
    public final void update(boolean z) {
        this.bgExecutor.execute(new PrivacyItemController$update$1(z, this));
    }

    /* access modifiers changed from: private */
    public final void setListeningState() {
        boolean z = (!this.callbacks.isEmpty()) & (this.micCameraAvailable || this.locationAvailable);
        if (this.listening != z) {
            this.listening = z;
            if (z) {
                this.appOpsController.addCallback(Companion.getOPS(), this.f112cb);
                registerReceiver();
                update(true);
                return;
            }
            this.appOpsController.removeCallback(Companion.getOPS(), this.f112cb);
            unregisterListener();
            update(false);
        }
    }

    private final void addCallback(WeakReference<Callback> weakReference) {
        this.callbacks.add(weakReference);
        if ((!this.callbacks.isEmpty()) && !this.listening) {
            this.internalUiExecutor.updateListeningState();
        } else if (this.listening) {
            this.internalUiExecutor.execute(new NotifyChangesToCallback((Callback) weakReference.get(), mo15937xca4da456()));
        }
    }

    private final void removeCallback(WeakReference<Callback> weakReference) {
        this.callbacks.removeIf(new PrivacyItemController$removeCallback$1(weakReference));
        if (this.callbacks.isEmpty()) {
            this.internalUiExecutor.updateListeningState();
        }
    }

    public final void addCallback(@NotNull Callback callback) {
        Intrinsics.checkNotNullParameter(callback, "callback");
        addCallback((WeakReference<Callback>) new WeakReference(callback));
    }

    public final void removeCallback(@NotNull Callback callback) {
        Intrinsics.checkNotNullParameter(callback, "callback");
        removeCallback((WeakReference<Callback>) new WeakReference(callback));
    }

    /* access modifiers changed from: private */
    public final void updatePrivacyList() {
        Runnable runnable = this.holdingRunnableCanceler;
        if (runnable != null) {
            runnable.run();
            Unit unit = Unit.INSTANCE;
            this.holdingRunnableCanceler = null;
        }
        if (!this.listening) {
            this.privacyList = CollectionsKt__CollectionsKt.emptyList();
            return;
        }
        List<AppOpItem> activeAppOps = this.appOpsController.getActiveAppOps(true);
        Intrinsics.checkNotNullExpressionValue(activeAppOps, "appOpsController.getActiveAppOps(true)");
        ArrayList<AppOpItem> arrayList = new ArrayList<>();
        for (T next : activeAppOps) {
            AppOpItem appOpItem = (AppOpItem) next;
            if (this.currentUserIds.contains(Integer.valueOf(UserHandle.getUserId(appOpItem.getUid()))) || appOpItem.getCode() == 100 || appOpItem.getCode() == 101) {
                arrayList.add(next);
            }
        }
        ArrayList arrayList2 = new ArrayList();
        for (AppOpItem appOpItem2 : arrayList) {
            Intrinsics.checkNotNullExpressionValue(appOpItem2, "it");
            PrivacyItem privacyItem = toPrivacyItem(appOpItem2);
            if (privacyItem != null) {
                arrayList2.add(privacyItem);
            }
        }
        this.privacyList = processNewList(CollectionsKt___CollectionsKt.distinct(arrayList2));
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v3, resolved type: com.android.systemui.privacy.PrivacyItem} */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private final java.util.List<com.android.systemui.privacy.PrivacyItem> processNewList(java.util.List<com.android.systemui.privacy.PrivacyItem> r13) {
        /*
            r12 = this;
            com.android.systemui.privacy.logging.PrivacyLogger r0 = r12.logger
            r0.logRetrievedPrivacyItemsList(r13)
            com.android.systemui.util.time.SystemClock r0 = r12.systemClock
            long r0 = r0.elapsedRealtime()
            r2 = 5000(0x1388, double:2.4703E-320)
            long r0 = r0 - r2
            java.util.List r2 = r12.mo15937xca4da456()
            java.util.ArrayList r3 = new java.util.ArrayList
            r3.<init>()
            java.util.Iterator r2 = r2.iterator()
        L_0x001b:
            boolean r4 = r2.hasNext()
            r5 = 1
            if (r4 == 0) goto L_0x003f
            java.lang.Object r4 = r2.next()
            r6 = r4
            com.android.systemui.privacy.PrivacyItem r6 = (com.android.systemui.privacy.PrivacyItem) r6
            long r7 = r6.getTimeStampElapsed()
            int r7 = (r7 > r0 ? 1 : (r7 == r0 ? 0 : -1))
            if (r7 <= 0) goto L_0x0038
            boolean r6 = r12.isIn(r6, r13)
            if (r6 != 0) goto L_0x0038
            goto L_0x0039
        L_0x0038:
            r5 = 0
        L_0x0039:
            if (r5 == 0) goto L_0x001b
            r3.add(r4)
            goto L_0x001b
        L_0x003f:
            boolean r2 = r3.isEmpty()
            r2 = r2 ^ r5
            if (r2 == 0) goto L_0x009b
            com.android.systemui.privacy.logging.PrivacyLogger r2 = r12.logger
            r2.logPrivacyItemsToHold(r3)
            java.util.Iterator r2 = r3.iterator()
            boolean r4 = r2.hasNext()
            if (r4 != 0) goto L_0x0057
            r2 = 0
            goto L_0x0082
        L_0x0057:
            java.lang.Object r4 = r2.next()
            boolean r6 = r2.hasNext()
            if (r6 != 0) goto L_0x0063
        L_0x0061:
            r2 = r4
            goto L_0x0082
        L_0x0063:
            r6 = r4
            com.android.systemui.privacy.PrivacyItem r6 = (com.android.systemui.privacy.PrivacyItem) r6
            long r6 = r6.getTimeStampElapsed()
        L_0x006a:
            java.lang.Object r8 = r2.next()
            r9 = r8
            com.android.systemui.privacy.PrivacyItem r9 = (com.android.systemui.privacy.PrivacyItem) r9
            long r9 = r9.getTimeStampElapsed()
            int r11 = (r6 > r9 ? 1 : (r6 == r9 ? 0 : -1))
            if (r11 <= 0) goto L_0x007b
            r4 = r8
            r6 = r9
        L_0x007b:
            boolean r8 = r2.hasNext()
            if (r8 != 0) goto L_0x006a
            goto L_0x0061
        L_0x0082:
            com.android.systemui.privacy.PrivacyItem r2 = (com.android.systemui.privacy.PrivacyItem) r2
            kotlin.jvm.internal.Intrinsics.checkNotNull(r2)
            long r6 = r2.getTimeStampElapsed()
            long r6 = r6 - r0
            com.android.systemui.privacy.logging.PrivacyLogger r0 = r12.logger
            r0.logPrivacyItemsUpdateScheduled(r6)
            com.android.systemui.util.concurrency.DelayableExecutor r0 = r12.bgExecutor
            java.lang.Runnable r1 = r12.updateListAndNotifyChanges
            java.lang.Runnable r0 = r0.executeDelayed(r1, r6)
            r12.holdingRunnableCanceler = r0
        L_0x009b:
            java.util.ArrayList r12 = new java.util.ArrayList
            r12.<init>()
            java.util.Iterator r13 = r13.iterator()
        L_0x00a4:
            boolean r0 = r13.hasNext()
            if (r0 == 0) goto L_0x00bc
            java.lang.Object r0 = r13.next()
            r1 = r0
            com.android.systemui.privacy.PrivacyItem r1 = (com.android.systemui.privacy.PrivacyItem) r1
            boolean r1 = r1.getPaused()
            r1 = r1 ^ r5
            if (r1 == 0) goto L_0x00a4
            r12.add(r0)
            goto L_0x00a4
        L_0x00bc:
            java.util.List r12 = kotlin.collections.CollectionsKt___CollectionsKt.plus(r12, r3)
            return r12
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.privacy.PrivacyItemController.processNewList(java.util.List):java.util.List");
    }

    private final PrivacyItem toPrivacyItem(AppOpItem appOpItem) {
        PrivacyType privacyType;
        int code = appOpItem.getCode();
        if (code == 0 || code == 1) {
            privacyType = PrivacyType.TYPE_LOCATION;
        } else {
            if (code != 26) {
                if (code == 27 || code == 100) {
                    privacyType = PrivacyType.TYPE_MICROPHONE;
                } else if (code != 101) {
                    return null;
                }
            }
            privacyType = PrivacyType.TYPE_CAMERA;
        }
        PrivacyType privacyType2 = privacyType;
        if (privacyType2 == PrivacyType.TYPE_LOCATION && !this.locationAvailable) {
            return null;
        }
        String packageName = appOpItem.getPackageName();
        Intrinsics.checkNotNullExpressionValue(packageName, "appOpItem.packageName");
        return new PrivacyItem(privacyType2, new PrivacyApplication(packageName, appOpItem.getUid()), appOpItem.getTimeStartedElapsed(), appOpItem.isDisabled());
    }

    /* compiled from: PrivacyItemController.kt */
    private static final class NotifyChangesToCallback implements Runnable {
        @Nullable
        private final Callback callback;
        @NotNull
        private final List<PrivacyItem> list;

        public NotifyChangesToCallback(@Nullable Callback callback2, @NotNull List<PrivacyItem> list2) {
            Intrinsics.checkNotNullParameter(list2, "list");
            this.callback = callback2;
            this.list = list2;
        }

        public void run() {
            Callback callback2 = this.callback;
            if (callback2 != null) {
                callback2.onPrivacyItemsChanged(this.list);
            }
        }
    }

    public void dump(@NotNull FileDescriptor fileDescriptor, @NotNull PrintWriter printWriter, @NotNull String[] strArr) {
        Intrinsics.checkNotNullParameter(fileDescriptor, "fd");
        Intrinsics.checkNotNullParameter(printWriter, "pw");
        Intrinsics.checkNotNullParameter(strArr, "args");
        printWriter.println("PrivacyItemController state:");
        printWriter.println(Intrinsics.stringPlus("  Listening: ", Boolean.valueOf(this.listening)));
        printWriter.println(Intrinsics.stringPlus("  Current user ids: ", this.currentUserIds));
        printWriter.println("  Privacy Items:");
        for (PrivacyItem privacyItem : mo15937xca4da456()) {
            printWriter.print("    ");
            printWriter.println(privacyItem.toString());
        }
        printWriter.println("  Callbacks:");
        for (WeakReference weakReference : this.callbacks) {
            Callback callback = (Callback) weakReference.get();
            if (callback != null) {
                printWriter.print("    ");
                printWriter.println(callback.toString());
            }
        }
    }

    /* compiled from: PrivacyItemController.kt */
    private final class MyExecutor implements Executor {
        @NotNull
        private final DelayableExecutor delegate;
        @Nullable
        private Runnable listeningCanceller;
        final /* synthetic */ PrivacyItemController this$0;

        public MyExecutor(@NotNull PrivacyItemController privacyItemController, DelayableExecutor delayableExecutor) {
            Intrinsics.checkNotNullParameter(privacyItemController, "this$0");
            Intrinsics.checkNotNullParameter(delayableExecutor, "delegate");
            this.this$0 = privacyItemController;
            this.delegate = delayableExecutor;
        }

        public void execute(@NotNull Runnable runnable) {
            Intrinsics.checkNotNullParameter(runnable, "command");
            this.delegate.execute(runnable);
        }

        public final void updateListeningState() {
            Runnable runnable = this.listeningCanceller;
            if (runnable != null) {
                runnable.run();
            }
            this.listeningCanceller = this.delegate.executeDelayed(new PrivacyItemController$MyExecutor$updateListeningState$1(this.this$0), 0);
        }
    }

    private final boolean isIn(PrivacyItem privacyItem, List<PrivacyItem> list) {
        boolean z;
        if (!(list instanceof Collection) || !list.isEmpty()) {
            for (PrivacyItem privacyItem2 : list) {
                if (privacyItem2.getPrivacyType() == privacyItem.getPrivacyType() && Intrinsics.areEqual((Object) privacyItem2.getApplication(), (Object) privacyItem.getApplication()) && privacyItem2.getTimeStampElapsed() == privacyItem.getTimeStampElapsed()) {
                    z = true;
                    continue;
                } else {
                    z = false;
                    continue;
                }
                if (z) {
                    return true;
                }
            }
        }
        return false;
    }
}
