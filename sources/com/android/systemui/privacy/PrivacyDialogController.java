package com.android.systemui.privacy;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.UserHandle;
import android.permission.PermGroupUsage;
import android.permission.PermissionManager;
import android.util.Log;
import com.android.systemui.appops.AppOpsController;
import com.android.systemui.plugins.ActivityStarter;
import com.android.systemui.privacy.PrivacyDialog;
import com.android.systemui.privacy.logging.PrivacyLogger;
import com.android.systemui.settings.UserTracker;
import com.android.systemui.statusbar.policy.KeyguardStateController;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.concurrent.Executor;
import kotlin.Unit;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/* compiled from: PrivacyDialogController.kt */
public final class PrivacyDialogController {
    @NotNull
    public static final Companion Companion = new Companion((DefaultConstructorMarker) null);
    @NotNull
    private final ActivityStarter activityStarter;
    @NotNull
    private final AppOpsController appOpsController;
    @NotNull
    private final Executor backgroundExecutor;
    /* access modifiers changed from: private */
    @Nullable
    public Dialog dialog;
    /* access modifiers changed from: private */
    @NotNull
    public final DialogProvider dialogProvider;
    @NotNull
    private final KeyguardStateController keyguardStateController;
    /* access modifiers changed from: private */
    @NotNull
    public final PrivacyDialogController$onDialogDismissed$1 onDialogDismissed;
    @NotNull
    private final PackageManager packageManager;
    @NotNull
    private final PermissionManager permissionManager;
    @NotNull
    private final PrivacyItemController privacyItemController;
    /* access modifiers changed from: private */
    @NotNull
    public final PrivacyLogger privacyLogger;
    /* access modifiers changed from: private */
    @NotNull
    public final Executor uiExecutor;
    /* access modifiers changed from: private */
    @NotNull
    public final UserTracker userTracker;

    /* compiled from: PrivacyDialogController.kt */
    public interface DialogProvider {
        @NotNull
        PrivacyDialog makeDialog(@NotNull Context context, @NotNull List<PrivacyDialog.PrivacyElement> list, @NotNull Function2<? super String, ? super Integer, Unit> function2);
    }

    public PrivacyDialogController(@NotNull PermissionManager permissionManager2, @NotNull PackageManager packageManager2, @NotNull PrivacyItemController privacyItemController2, @NotNull UserTracker userTracker2, @NotNull ActivityStarter activityStarter2, @NotNull Executor executor, @NotNull Executor executor2, @NotNull PrivacyLogger privacyLogger2, @NotNull KeyguardStateController keyguardStateController2, @NotNull AppOpsController appOpsController2, @NotNull DialogProvider dialogProvider2) {
        Intrinsics.checkNotNullParameter(permissionManager2, "permissionManager");
        Intrinsics.checkNotNullParameter(packageManager2, "packageManager");
        Intrinsics.checkNotNullParameter(privacyItemController2, "privacyItemController");
        Intrinsics.checkNotNullParameter(userTracker2, "userTracker");
        Intrinsics.checkNotNullParameter(activityStarter2, "activityStarter");
        Intrinsics.checkNotNullParameter(executor, "backgroundExecutor");
        Intrinsics.checkNotNullParameter(executor2, "uiExecutor");
        Intrinsics.checkNotNullParameter(privacyLogger2, "privacyLogger");
        Intrinsics.checkNotNullParameter(keyguardStateController2, "keyguardStateController");
        Intrinsics.checkNotNullParameter(appOpsController2, "appOpsController");
        Intrinsics.checkNotNullParameter(dialogProvider2, "dialogProvider");
        this.permissionManager = permissionManager2;
        this.packageManager = packageManager2;
        this.privacyItemController = privacyItemController2;
        this.userTracker = userTracker2;
        this.activityStarter = activityStarter2;
        this.backgroundExecutor = executor;
        this.uiExecutor = executor2;
        this.privacyLogger = privacyLogger2;
        this.keyguardStateController = keyguardStateController2;
        this.appOpsController = appOpsController2;
        this.dialogProvider = dialogProvider2;
        this.onDialogDismissed = new PrivacyDialogController$onDialogDismissed$1(this);
    }

    /* JADX WARNING: Illegal instructions before constructor call */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public PrivacyDialogController(@org.jetbrains.annotations.NotNull android.permission.PermissionManager r14, @org.jetbrains.annotations.NotNull android.content.pm.PackageManager r15, @org.jetbrains.annotations.NotNull com.android.systemui.privacy.PrivacyItemController r16, @org.jetbrains.annotations.NotNull com.android.systemui.settings.UserTracker r17, @org.jetbrains.annotations.NotNull com.android.systemui.plugins.ActivityStarter r18, @org.jetbrains.annotations.NotNull java.util.concurrent.Executor r19, @org.jetbrains.annotations.NotNull java.util.concurrent.Executor r20, @org.jetbrains.annotations.NotNull com.android.systemui.privacy.logging.PrivacyLogger r21, @org.jetbrains.annotations.NotNull com.android.systemui.statusbar.policy.KeyguardStateController r22, @org.jetbrains.annotations.NotNull com.android.systemui.appops.AppOpsController r23) {
        /*
            r13 = this;
            java.lang.String r0 = "permissionManager"
            r2 = r14
            kotlin.jvm.internal.Intrinsics.checkNotNullParameter(r14, r0)
            java.lang.String r0 = "packageManager"
            r3 = r15
            kotlin.jvm.internal.Intrinsics.checkNotNullParameter(r15, r0)
            java.lang.String r0 = "privacyItemController"
            r4 = r16
            kotlin.jvm.internal.Intrinsics.checkNotNullParameter(r4, r0)
            java.lang.String r0 = "userTracker"
            r5 = r17
            kotlin.jvm.internal.Intrinsics.checkNotNullParameter(r5, r0)
            java.lang.String r0 = "activityStarter"
            r6 = r18
            kotlin.jvm.internal.Intrinsics.checkNotNullParameter(r6, r0)
            java.lang.String r0 = "backgroundExecutor"
            r7 = r19
            kotlin.jvm.internal.Intrinsics.checkNotNullParameter(r7, r0)
            java.lang.String r0 = "uiExecutor"
            r8 = r20
            kotlin.jvm.internal.Intrinsics.checkNotNullParameter(r8, r0)
            java.lang.String r0 = "privacyLogger"
            r9 = r21
            kotlin.jvm.internal.Intrinsics.checkNotNullParameter(r9, r0)
            java.lang.String r0 = "keyguardStateController"
            r10 = r22
            kotlin.jvm.internal.Intrinsics.checkNotNullParameter(r10, r0)
            java.lang.String r0 = "appOpsController"
            r11 = r23
            kotlin.jvm.internal.Intrinsics.checkNotNullParameter(r11, r0)
            com.android.systemui.privacy.PrivacyDialogControllerKt$defaultDialogProvider$1 r12 = com.android.systemui.privacy.PrivacyDialogControllerKt.access$getDefaultDialogProvider$p()
            r1 = r13
            r1.<init>(r2, r3, r4, r5, r6, r7, r8, r9, r10, r11, r12)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.privacy.PrivacyDialogController.<init>(android.permission.PermissionManager, android.content.pm.PackageManager, com.android.systemui.privacy.PrivacyItemController, com.android.systemui.settings.UserTracker, com.android.systemui.plugins.ActivityStarter, java.util.concurrent.Executor, java.util.concurrent.Executor, com.android.systemui.privacy.logging.PrivacyLogger, com.android.systemui.statusbar.policy.KeyguardStateController, com.android.systemui.appops.AppOpsController):void");
    }

    /* compiled from: PrivacyDialogController.kt */
    public static final class Companion {
        public /* synthetic */ Companion(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }

        private Companion() {
        }
    }

    /* access modifiers changed from: private */
    public final void startActivity(String str, int i) {
        Dialog dialog2;
        Intent intent = new Intent("android.intent.action.MANAGE_APP_PERMISSIONS");
        intent.putExtra("android.intent.extra.PACKAGE_NAME", str);
        intent.putExtra("android.intent.extra.USER", UserHandle.of(i));
        this.privacyLogger.logStartSettingsActivityFromDialog(str, i);
        if (!this.keyguardStateController.isUnlocked() && (dialog2 = this.dialog) != null) {
            dialog2.hide();
        }
        this.activityStarter.startActivity(intent, true, (ActivityStarter.Callback) new PrivacyDialogController$startActivity$1(this));
    }

    /* access modifiers changed from: private */
    public final List<PermGroupUsage> permGroupUsage() {
        List<PermGroupUsage> indicatorAppOpUsageData = this.permissionManager.getIndicatorAppOpUsageData(this.appOpsController.isMicMuted());
        Intrinsics.checkNotNullExpressionValue(indicatorAppOpUsageData, "permissionManager.getIndicatorAppOpUsageData(appOpsController.isMicMuted)");
        return indicatorAppOpUsageData;
    }

    public final void showDialog(@NotNull Context context) {
        Intrinsics.checkNotNullParameter(context, "context");
        dismissDialog();
        this.backgroundExecutor.execute(new PrivacyDialogController$showDialog$1(this, context));
    }

    public final void dismissDialog() {
        Dialog dialog2 = this.dialog;
        if (dialog2 != null) {
            dialog2.dismiss();
        }
    }

    /* access modifiers changed from: private */
    public final CharSequence getLabelForPackage(String str, int i) {
        try {
            CharSequence loadLabel = this.packageManager.getApplicationInfoAsUser(str, 0, UserHandle.getUserId(i)).loadLabel(this.packageManager);
            Intrinsics.checkNotNullExpressionValue(loadLabel, "{\n            packageManager\n                    .getApplicationInfoAsUser(packageName, 0, UserHandle.getUserId(uid))\n                    .loadLabel(packageManager)\n        }");
            return loadLabel;
        } catch (PackageManager.NameNotFoundException unused) {
            Log.w("PrivacyDialogController", Intrinsics.stringPlus("Label not found for: ", str));
            return str;
        }
    }

    /* access modifiers changed from: private */
    public final PrivacyType permGroupToPrivacyType(String str) {
        int hashCode = str.hashCode();
        if (hashCode != -1140935117) {
            if (hashCode != 828638019) {
                if (hashCode == 1581272376 && str.equals("android.permission-group.MICROPHONE")) {
                    return PrivacyType.TYPE_MICROPHONE;
                }
            } else if (str.equals("android.permission-group.LOCATION")) {
                return PrivacyType.TYPE_LOCATION;
            }
        } else if (str.equals("android.permission-group.CAMERA")) {
            return PrivacyType.TYPE_CAMERA;
        }
        return null;
    }

    /* access modifiers changed from: private */
    public final PrivacyType filterType(PrivacyType privacyType) {
        if (privacyType == null) {
            return null;
        }
        if ((!(privacyType == PrivacyType.TYPE_CAMERA || privacyType == PrivacyType.TYPE_MICROPHONE) || !this.privacyItemController.getMicCameraAvailable()) && (privacyType != PrivacyType.TYPE_LOCATION || !this.privacyItemController.getLocationAvailable())) {
            privacyType = null;
        }
        return privacyType;
    }

    /* access modifiers changed from: private */
    public final List<PrivacyDialog.PrivacyElement> filterAndSelect(List<PrivacyDialog.PrivacyElement> list) {
        List list2;
        Object obj;
        LinkedHashMap linkedHashMap = new LinkedHashMap();
        for (T next : list) {
            PrivacyType type = ((PrivacyDialog.PrivacyElement) next).getType();
            Object obj2 = linkedHashMap.get(type);
            if (obj2 == null) {
                obj2 = new ArrayList();
                linkedHashMap.put(type, obj2);
            }
            ((List) obj2).add(next);
        }
        SortedMap sortedMap = MapsKt__MapsJVMKt.toSortedMap(linkedHashMap);
        ArrayList arrayList = new ArrayList();
        for (Map.Entry value : sortedMap.entrySet()) {
            List list3 = (List) value.getValue();
            Intrinsics.checkNotNullExpressionValue(list3, "elements");
            ArrayList arrayList2 = new ArrayList();
            for (Object next2 : list3) {
                if (((PrivacyDialog.PrivacyElement) next2).getActive()) {
                    arrayList2.add(next2);
                }
            }
            if (!arrayList2.isEmpty()) {
                list2 = CollectionsKt___CollectionsKt.sortedWith(arrayList2, new C1143xfdcce2a3());
            } else {
                Iterator it = list3.iterator();
                if (!it.hasNext()) {
                    obj = null;
                } else {
                    obj = it.next();
                    if (it.hasNext()) {
                        long lastActiveTimestamp = ((PrivacyDialog.PrivacyElement) obj).getLastActiveTimestamp();
                        do {
                            Object next3 = it.next();
                            long lastActiveTimestamp2 = ((PrivacyDialog.PrivacyElement) next3).getLastActiveTimestamp();
                            if (lastActiveTimestamp < lastActiveTimestamp2) {
                                obj = next3;
                                lastActiveTimestamp = lastActiveTimestamp2;
                            }
                        } while (it.hasNext());
                    }
                }
                PrivacyDialog.PrivacyElement privacyElement = (PrivacyDialog.PrivacyElement) obj;
                if (privacyElement == null) {
                    list2 = null;
                } else {
                    list2 = CollectionsKt__CollectionsJVMKt.listOf(privacyElement);
                }
                if (list2 == null) {
                    list2 = CollectionsKt__CollectionsKt.emptyList();
                }
            }
            boolean unused = CollectionsKt__MutableCollectionsKt.addAll(arrayList, list2);
        }
        return arrayList;
    }
}
