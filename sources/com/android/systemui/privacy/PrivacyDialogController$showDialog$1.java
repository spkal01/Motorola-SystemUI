package com.android.systemui.privacy;

import android.content.Context;
import android.content.pm.UserInfo;
import android.os.UserHandle;
import android.permission.PermGroupUsage;
import android.util.Log;
import com.android.systemui.privacy.PrivacyDialog;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Executor;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: PrivacyDialogController.kt */
final class PrivacyDialogController$showDialog$1 implements Runnable {
    final /* synthetic */ Context $context;
    final /* synthetic */ PrivacyDialogController this$0;

    PrivacyDialogController$showDialog$1(PrivacyDialogController privacyDialogController, Context context) {
        this.this$0 = privacyDialogController;
        this.$context = context;
    }

    public final void run() {
        PrivacyDialog.PrivacyElement privacyElement;
        T t;
        CharSequence charSequence;
        boolean z;
        boolean z2;
        List<PermGroupUsage> access$permGroupUsage = this.this$0.permGroupUsage();
        List<UserInfo> userProfiles = this.this$0.userTracker.getUserProfiles();
        this.this$0.privacyLogger.logUnfilteredPermGroupUsage(access$permGroupUsage);
        PrivacyDialogController privacyDialogController = this.this$0;
        final ArrayList arrayList = new ArrayList();
        for (PermGroupUsage permGroupUsage : access$permGroupUsage) {
            String permGroupName = permGroupUsage.getPermGroupName();
            Intrinsics.checkNotNullExpressionValue(permGroupName, "it.permGroupName");
            PrivacyType access$filterType = privacyDialogController.filterType(privacyDialogController.permGroupToPrivacyType(permGroupName));
            Iterator<T> it = userProfiles.iterator();
            while (true) {
                privacyElement = null;
                if (!it.hasNext()) {
                    t = null;
                    break;
                }
                t = it.next();
                if (((UserInfo) t).id == UserHandle.getUserId(permGroupUsage.getUid())) {
                    z2 = true;
                    continue;
                } else {
                    z2 = false;
                    continue;
                }
                if (z2) {
                    break;
                }
            }
            UserInfo userInfo = (UserInfo) t;
            if ((userInfo != null || permGroupUsage.isPhoneCall()) && access$filterType != null) {
                if (permGroupUsage.isPhoneCall()) {
                    charSequence = "";
                } else {
                    String packageName = permGroupUsage.getPackageName();
                    Intrinsics.checkNotNullExpressionValue(packageName, "it.packageName");
                    charSequence = privacyDialogController.getLabelForPackage(packageName, permGroupUsage.getUid());
                }
                CharSequence charSequence2 = charSequence;
                String packageName2 = permGroupUsage.getPackageName();
                Intrinsics.checkNotNullExpressionValue(packageName2, "it.packageName");
                int userId = UserHandle.getUserId(permGroupUsage.getUid());
                CharSequence attribution = permGroupUsage.getAttribution();
                long lastAccess = permGroupUsage.getLastAccess();
                boolean isActive = permGroupUsage.isActive();
                if (userInfo == null) {
                    z = false;
                } else {
                    z = userInfo.isManagedProfile();
                }
                privacyElement = new PrivacyDialog.PrivacyElement(access$filterType, packageName2, userId, charSequence2, attribution, lastAccess, isActive, z, permGroupUsage.isPhoneCall());
            }
            if (privacyElement != null) {
                arrayList.add(privacyElement);
            }
        }
        Executor access$getUiExecutor$p = this.this$0.uiExecutor;
        final PrivacyDialogController privacyDialogController2 = this.this$0;
        final Context context = this.$context;
        access$getUiExecutor$p.execute(new Runnable() {
            public final void run() {
                List access$filterAndSelect = privacyDialogController2.filterAndSelect(arrayList);
                if (!access$filterAndSelect.isEmpty()) {
                    PrivacyDialog makeDialog = privacyDialogController2.dialogProvider.makeDialog(context, access$filterAndSelect, new PrivacyDialogController$showDialog$1$1$d$1(privacyDialogController2));
                    makeDialog.setShowForAllUsers(true);
                    makeDialog.addOnDismissListener(privacyDialogController2.onDialogDismissed);
                    makeDialog.show();
                    privacyDialogController2.privacyLogger.logShowDialogContents(access$filterAndSelect);
                    privacyDialogController2.dialog = makeDialog;
                    return;
                }
                Log.w("PrivacyDialogController", "Trying to show empty dialog");
            }
        });
    }
}
