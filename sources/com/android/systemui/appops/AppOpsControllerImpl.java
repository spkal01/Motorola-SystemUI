package com.android.systemui.appops;

import android.app.AppOpsManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.AudioRecordingConfiguration;
import android.os.Handler;
import android.os.Looper;
import android.os.UserHandle;
import android.permission.PermissionManager;
import android.util.ArraySet;
import android.util.SparseArray;
import androidx.constraintlayout.widget.R$styleable;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.annotations.VisibleForTesting;
import com.android.systemui.Dumpable;
import com.android.systemui.appops.AppOpsController;
import com.android.systemui.broadcast.BroadcastDispatcher;
import com.android.systemui.dump.DumpManager;
import com.android.systemui.statusbar.policy.IndividualSensorPrivacyController;
import com.android.systemui.util.Assert;
import com.android.systemui.util.time.SystemClock;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class AppOpsControllerImpl extends BroadcastReceiver implements AppOpsController, AppOpsManager.OnOpActiveChangedListener, AppOpsManager.OnOpNotedListener, IndividualSensorPrivacyController.Callback, Dumpable {
    protected static final int[] OPS = {42, 26, R$styleable.Constraint_layout_goneMarginRight, 24, 27, 100, 0, 1};
    /* access modifiers changed from: private */
    @GuardedBy({"mActiveItems"})
    public final List<AppOpItem> mActiveItems = new ArrayList();
    private final AppOpsManager mAppOps;
    private final AudioManager mAudioManager;
    private AudioManager.AudioRecordingCallback mAudioRecordingCallback = new AudioManager.AudioRecordingCallback() {
        public void onRecordingConfigChanged(List<AudioRecordingConfiguration> list) {
            synchronized (AppOpsControllerImpl.this.mActiveItems) {
                AppOpsControllerImpl.this.mRecordingsByUid.clear();
                int size = list.size();
                for (int i = 0; i < size; i++) {
                    AudioRecordingConfiguration audioRecordingConfiguration = list.get(i);
                    ArrayList arrayList = (ArrayList) AppOpsControllerImpl.this.mRecordingsByUid.get(audioRecordingConfiguration.getClientUid());
                    if (arrayList == null) {
                        arrayList = new ArrayList();
                        AppOpsControllerImpl.this.mRecordingsByUid.put(audioRecordingConfiguration.getClientUid(), arrayList);
                    }
                    arrayList.add(audioRecordingConfiguration);
                }
            }
            AppOpsControllerImpl.this.updateSensorDisabledStatus();
        }
    };
    private C0823H mBGHandler;
    private final List<AppOpsController.Callback> mCallbacks = new ArrayList();
    private final SparseArray<Set<AppOpsController.Callback>> mCallbacksByCode = new SparseArray<>();
    private boolean mCameraDisabled;
    private final SystemClock mClock;
    private final Context mContext;
    private final BroadcastDispatcher mDispatcher;
    private boolean mListening;
    private boolean mMicMuted;
    @GuardedBy({"mNotedItems"})
    private final List<AppOpItem> mNotedItems = new ArrayList();
    /* access modifiers changed from: private */
    @GuardedBy({"mActiveItems"})
    public final SparseArray<ArrayList<AudioRecordingConfiguration>> mRecordingsByUid = new SparseArray<>();
    private final IndividualSensorPrivacyController mSensorPrivacyController;

    private boolean isOpCamera(int i) {
        return i == 26 || i == 101;
    }

    private boolean isOpMicrophone(int i) {
        return i == 27 || i == 100;
    }

    public AppOpsControllerImpl(Context context, Looper looper, DumpManager dumpManager, AudioManager audioManager, IndividualSensorPrivacyController individualSensorPrivacyController, BroadcastDispatcher broadcastDispatcher, SystemClock systemClock) {
        this.mDispatcher = broadcastDispatcher;
        this.mAppOps = (AppOpsManager) context.getSystemService("appops");
        this.mBGHandler = new C0823H(looper);
        boolean z = false;
        for (int put : OPS) {
            this.mCallbacksByCode.put(put, new ArraySet());
        }
        this.mAudioManager = audioManager;
        this.mSensorPrivacyController = individualSensorPrivacyController;
        this.mMicMuted = (audioManager.isMicrophoneMute() || individualSensorPrivacyController.isSensorBlocked(1)) ? true : z;
        this.mCameraDisabled = individualSensorPrivacyController.isSensorBlocked(2);
        this.mContext = context;
        this.mClock = systemClock;
        dumpManager.registerDumpable("AppOpsControllerImpl", this);
    }

    /* access modifiers changed from: protected */
    @VisibleForTesting
    public void setBGHandler(C0823H h) {
        this.mBGHandler = h;
    }

    /* access modifiers changed from: protected */
    @VisibleForTesting
    public void setListening(boolean z) {
        this.mListening = z;
        if (z) {
            AppOpsManager appOpsManager = this.mAppOps;
            int[] iArr = OPS;
            appOpsManager.startWatchingActive(iArr, this);
            this.mAppOps.startWatchingNoted(iArr, this);
            this.mAudioManager.registerAudioRecordingCallback(this.mAudioRecordingCallback, this.mBGHandler);
            this.mSensorPrivacyController.addCallback(this);
            boolean z2 = true;
            if (!this.mAudioManager.isMicrophoneMute() && !this.mSensorPrivacyController.isSensorBlocked(1)) {
                z2 = false;
            }
            this.mMicMuted = z2;
            this.mCameraDisabled = this.mSensorPrivacyController.isSensorBlocked(2);
            this.mBGHandler.post(new AppOpsControllerImpl$$ExternalSyntheticLambda0(this));
            this.mDispatcher.registerReceiverWithHandler(this, new IntentFilter("android.media.action.MICROPHONE_MUTE_CHANGED"), this.mBGHandler);
            return;
        }
        this.mAppOps.stopWatchingActive(this);
        this.mAppOps.stopWatchingNoted(this);
        this.mAudioManager.unregisterAudioRecordingCallback(this.mAudioRecordingCallback);
        this.mSensorPrivacyController.removeCallback(this);
        this.mBGHandler.removeCallbacksAndMessages((Object) null);
        this.mDispatcher.unregisterReceiver(this);
        synchronized (this.mActiveItems) {
            this.mActiveItems.clear();
            this.mRecordingsByUid.clear();
        }
        synchronized (this.mNotedItems) {
            this.mNotedItems.clear();
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$setListening$0() {
        this.mAudioRecordingCallback.onRecordingConfigChanged(this.mAudioManager.getActiveRecordingConfigurations());
    }

    public void addCallback(int[] iArr, AppOpsController.Callback callback) {
        int length = iArr.length;
        boolean z = false;
        for (int i = 0; i < length; i++) {
            if (this.mCallbacksByCode.contains(iArr[i])) {
                this.mCallbacksByCode.get(iArr[i]).add(callback);
                z = true;
            }
        }
        if (z) {
            this.mCallbacks.add(callback);
        }
        if (!this.mCallbacks.isEmpty()) {
            setListening(true);
        }
    }

    public void removeCallback(int[] iArr, AppOpsController.Callback callback) {
        int length = iArr.length;
        for (int i = 0; i < length; i++) {
            if (this.mCallbacksByCode.contains(iArr[i])) {
                this.mCallbacksByCode.get(iArr[i]).remove(callback);
            }
        }
        this.mCallbacks.remove(callback);
        if (this.mCallbacks.isEmpty()) {
            setListening(false);
        }
    }

    private AppOpItem getAppOpItemLocked(List<AppOpItem> list, int i, int i2, String str) {
        int size = list.size();
        for (int i3 = 0; i3 < size; i3++) {
            AppOpItem appOpItem = list.get(i3);
            if (appOpItem.getCode() == i && appOpItem.getUid() == i2 && appOpItem.getPackageName().equals(str)) {
                return appOpItem;
            }
        }
        return null;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:17:0x0045, code lost:
        return r2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:24:0x0052, code lost:
        return false;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private boolean updateActives(int r11, int r12, java.lang.String r13, boolean r14) {
        /*
            r10 = this;
            java.util.List<com.android.systemui.appops.AppOpItem> r0 = r10.mActiveItems
            monitor-enter(r0)
            java.util.List<com.android.systemui.appops.AppOpItem> r1 = r10.mActiveItems     // Catch:{ all -> 0x0053 }
            com.android.systemui.appops.AppOpItem r1 = r10.getAppOpItemLocked(r1, r11, r12, r13)     // Catch:{ all -> 0x0053 }
            r2 = 1
            r3 = 0
            if (r1 != 0) goto L_0x0046
            if (r14 == 0) goto L_0x0046
            com.android.systemui.appops.AppOpItem r14 = new com.android.systemui.appops.AppOpItem     // Catch:{ all -> 0x0053 }
            com.android.systemui.util.time.SystemClock r1 = r10.mClock     // Catch:{ all -> 0x0053 }
            long r8 = r1.elapsedRealtime()     // Catch:{ all -> 0x0053 }
            r4 = r14
            r5 = r11
            r6 = r12
            r7 = r13
            r4.<init>(r5, r6, r7, r8)     // Catch:{ all -> 0x0053 }
            boolean r13 = r10.isOpMicrophone(r11)     // Catch:{ all -> 0x0053 }
            if (r13 == 0) goto L_0x002c
            boolean r11 = r10.isAnyRecordingPausedLocked(r12)     // Catch:{ all -> 0x0053 }
            r14.setDisabled(r11)     // Catch:{ all -> 0x0053 }
            goto L_0x0037
        L_0x002c:
            boolean r11 = r10.isOpCamera(r11)     // Catch:{ all -> 0x0053 }
            if (r11 == 0) goto L_0x0037
            boolean r11 = r10.mCameraDisabled     // Catch:{ all -> 0x0053 }
            r14.setDisabled(r11)     // Catch:{ all -> 0x0053 }
        L_0x0037:
            java.util.List<com.android.systemui.appops.AppOpItem> r10 = r10.mActiveItems     // Catch:{ all -> 0x0053 }
            r10.add(r14)     // Catch:{ all -> 0x0053 }
            boolean r10 = r14.isDisabled()     // Catch:{ all -> 0x0053 }
            if (r10 != 0) goto L_0x0043
            goto L_0x0044
        L_0x0043:
            r2 = r3
        L_0x0044:
            monitor-exit(r0)     // Catch:{ all -> 0x0053 }
            return r2
        L_0x0046:
            if (r1 == 0) goto L_0x0051
            if (r14 != 0) goto L_0x0051
            java.util.List<com.android.systemui.appops.AppOpItem> r10 = r10.mActiveItems     // Catch:{ all -> 0x0053 }
            r10.remove(r1)     // Catch:{ all -> 0x0053 }
            monitor-exit(r0)     // Catch:{ all -> 0x0053 }
            return r2
        L_0x0051:
            monitor-exit(r0)     // Catch:{ all -> 0x0053 }
            return r3
        L_0x0053:
            r10 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x0053 }
            throw r10
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.appops.AppOpsControllerImpl.updateActives(int, int, java.lang.String, boolean):boolean");
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Code restructure failed: missing block: B:10:0x0015, code lost:
        monitor-enter(r1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:13:0x001d, code lost:
        if (getAppOpItemLocked(r3.mActiveItems, r4, r5, r6) == null) goto L_0x0021;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:14:0x001f, code lost:
        r0 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:15:0x0021, code lost:
        r0 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:16:0x0022, code lost:
        monitor-exit(r1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:17:0x0023, code lost:
        if (r0 != false) goto L_?;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:18:0x0025, code lost:
        notifySuscribersWorker(r4, r5, r6, false);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:33:?, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:34:?, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:9:0x0013, code lost:
        r1 = r3.mActiveItems;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void removeNoted(int r4, int r5, java.lang.String r6) {
        /*
            r3 = this;
            java.util.List<com.android.systemui.appops.AppOpItem> r0 = r3.mNotedItems
            monitor-enter(r0)
            java.util.List<com.android.systemui.appops.AppOpItem> r1 = r3.mNotedItems     // Catch:{ all -> 0x002c }
            com.android.systemui.appops.AppOpItem r1 = r3.getAppOpItemLocked(r1, r4, r5, r6)     // Catch:{ all -> 0x002c }
            if (r1 != 0) goto L_0x000d
            monitor-exit(r0)     // Catch:{ all -> 0x002c }
            return
        L_0x000d:
            java.util.List<com.android.systemui.appops.AppOpItem> r2 = r3.mNotedItems     // Catch:{ all -> 0x002c }
            r2.remove(r1)     // Catch:{ all -> 0x002c }
            monitor-exit(r0)     // Catch:{ all -> 0x002c }
            java.util.List<com.android.systemui.appops.AppOpItem> r1 = r3.mActiveItems
            monitor-enter(r1)
            java.util.List<com.android.systemui.appops.AppOpItem> r0 = r3.mActiveItems     // Catch:{ all -> 0x0029 }
            com.android.systemui.appops.AppOpItem r0 = r3.getAppOpItemLocked(r0, r4, r5, r6)     // Catch:{ all -> 0x0029 }
            r2 = 0
            if (r0 == 0) goto L_0x0021
            r0 = 1
            goto L_0x0022
        L_0x0021:
            r0 = r2
        L_0x0022:
            monitor-exit(r1)     // Catch:{ all -> 0x0029 }
            if (r0 != 0) goto L_0x0028
            r3.lambda$notifySuscribers$1(r4, r5, r6, r2)
        L_0x0028:
            return
        L_0x0029:
            r3 = move-exception
            monitor-exit(r1)     // Catch:{ all -> 0x0029 }
            throw r3
        L_0x002c:
            r3 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x002c }
            throw r3
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.appops.AppOpsControllerImpl.removeNoted(int, int, java.lang.String):void");
    }

    private boolean addNoted(int i, int i2, String str) {
        AppOpItem appOpItemLocked;
        boolean z;
        synchronized (this.mNotedItems) {
            appOpItemLocked = getAppOpItemLocked(this.mNotedItems, i, i2, str);
            if (appOpItemLocked == null) {
                appOpItemLocked = new AppOpItem(i, i2, str, this.mClock.elapsedRealtime());
                this.mNotedItems.add(appOpItemLocked);
                z = true;
            } else {
                z = false;
            }
        }
        this.mBGHandler.removeCallbacksAndMessages(appOpItemLocked);
        this.mBGHandler.scheduleRemoval(appOpItemLocked, 5000);
        return z;
    }

    private boolean isUserVisible(String str) {
        return PermissionManager.shouldShowPackageForIndicatorCached(this.mContext, str);
    }

    public List<AppOpItem> getActiveAppOps() {
        return getActiveAppOps(false);
    }

    public List<AppOpItem> getActiveAppOps(boolean z) {
        return getActiveAppOpsForUser(-1, z);
    }

    public List<AppOpItem> getActiveAppOpsForUser(int i, boolean z) {
        int i2;
        Assert.isNotMainThread();
        ArrayList arrayList = new ArrayList();
        synchronized (this.mActiveItems) {
            int size = this.mActiveItems.size();
            for (int i3 = 0; i3 < size; i3++) {
                AppOpItem appOpItem = this.mActiveItems.get(i3);
                if ((i == -1 || UserHandle.getUserId(appOpItem.getUid()) == i) && isUserVisible(appOpItem.getPackageName()) && (z || !appOpItem.isDisabled())) {
                    arrayList.add(appOpItem);
                }
            }
        }
        synchronized (this.mNotedItems) {
            int size2 = this.mNotedItems.size();
            for (i2 = 0; i2 < size2; i2++) {
                AppOpItem appOpItem2 = this.mNotedItems.get(i2);
                if ((i == -1 || UserHandle.getUserId(appOpItem2.getUid()) == i) && isUserVisible(appOpItem2.getPackageName())) {
                    arrayList.add(appOpItem2);
                }
            }
        }
        return arrayList;
    }

    private void notifySuscribers(int i, int i2, String str, boolean z) {
        this.mBGHandler.post(new AppOpsControllerImpl$$ExternalSyntheticLambda1(this, i, i2, str, z));
    }

    public void onOpActiveChanged(String str, int i, String str2, boolean z) {
        onOpActiveChanged(str, i, str2, (String) null, z, 0, -1);
    }

    public void onOpActiveChanged(String str, int i, String str2, String str3, boolean z, int i2, int i3) {
        boolean z2;
        int strOpToOp = AppOpsManager.strOpToOp(str);
        if ((i3 == -1 || i2 == 0 || (i2 & 1) != 0 || (i2 & 8) != 0) && updateActives(strOpToOp, i, str2, z)) {
            synchronized (this.mNotedItems) {
                z2 = getAppOpItemLocked(this.mNotedItems, strOpToOp, i, str2) != null;
            }
            if (!z2) {
                notifySuscribers(strOpToOp, i, str2, z);
            }
        }
    }

    public void onOpNoted(int i, int i2, String str, String str2, int i3, int i4) {
        boolean z;
        if (i4 == 0 && addNoted(i, i2, str)) {
            synchronized (this.mActiveItems) {
                z = getAppOpItemLocked(this.mActiveItems, i, i2, str) != null;
            }
            if (!z) {
                notifySuscribers(i, i2, str, true);
            }
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: notifySuscribersWorker */
    public void lambda$notifySuscribers$1(int i, int i2, String str, boolean z) {
        if (this.mCallbacksByCode.contains(i) && isUserVisible(str)) {
            for (AppOpsController.Callback onActiveStateChanged : this.mCallbacksByCode.get(i)) {
                onActiveStateChanged.onActiveStateChanged(i, i2, str, z);
            }
        }
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        printWriter.println("AppOpsController state:");
        printWriter.println("  Listening: " + this.mListening);
        printWriter.println("  Active Items:");
        for (int i = 0; i < this.mActiveItems.size(); i++) {
            printWriter.print("    ");
            printWriter.println(this.mActiveItems.get(i).toString());
        }
        printWriter.println("  Noted Items:");
        for (int i2 = 0; i2 < this.mNotedItems.size(); i2++) {
            printWriter.print("    ");
            printWriter.println(this.mNotedItems.get(i2).toString());
        }
    }

    private boolean isAnyRecordingPausedLocked(int i) {
        if (this.mMicMuted) {
            return true;
        }
        List list = this.mRecordingsByUid.get(i);
        if (list == null) {
            return false;
        }
        int size = list.size();
        for (int i2 = 0; i2 < size; i2++) {
            if (((AudioRecordingConfiguration) list.get(i2)).isClientSilenced()) {
                return true;
            }
        }
        return false;
    }

    /* access modifiers changed from: private */
    public void updateSensorDisabledStatus() {
        boolean z;
        synchronized (this.mActiveItems) {
            int size = this.mActiveItems.size();
            for (int i = 0; i < size; i++) {
                AppOpItem appOpItem = this.mActiveItems.get(i);
                if (isOpMicrophone(appOpItem.getCode())) {
                    z = isAnyRecordingPausedLocked(appOpItem.getUid());
                } else {
                    z = isOpCamera(appOpItem.getCode()) ? this.mCameraDisabled : false;
                }
                if (appOpItem.isDisabled() != z) {
                    appOpItem.setDisabled(z);
                    notifySuscribers(appOpItem.getCode(), appOpItem.getUid(), appOpItem.getPackageName(), !appOpItem.isDisabled());
                }
            }
        }
    }

    public void onReceive(Context context, Intent intent) {
        boolean z = true;
        if (!this.mAudioManager.isMicrophoneMute() && !this.mSensorPrivacyController.isSensorBlocked(1)) {
            z = false;
        }
        this.mMicMuted = z;
        updateSensorDisabledStatus();
    }

    public void onSensorBlockedChanged(int i, boolean z) {
        this.mBGHandler.post(new AppOpsControllerImpl$$ExternalSyntheticLambda2(this, i, z));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onSensorBlockedChanged$2(int i, boolean z) {
        if (i == 2) {
            this.mCameraDisabled = z;
        } else {
            boolean z2 = true;
            if (i == 1) {
                if (!this.mAudioManager.isMicrophoneMute() && !z) {
                    z2 = false;
                }
                this.mMicMuted = z2;
            }
        }
        updateSensorDisabledStatus();
    }

    public boolean isMicMuted() {
        return this.mMicMuted;
    }

    /* renamed from: com.android.systemui.appops.AppOpsControllerImpl$H */
    protected class C0823H extends Handler {
        C0823H(Looper looper) {
            super(looper);
        }

        public void scheduleRemoval(final AppOpItem appOpItem, long j) {
            removeCallbacksAndMessages(appOpItem);
            postDelayed(new Runnable() {
                public void run() {
                    AppOpsControllerImpl.this.removeNoted(appOpItem.getCode(), appOpItem.getUid(), appOpItem.getPackageName());
                }
            }, appOpItem, j);
        }
    }
}
