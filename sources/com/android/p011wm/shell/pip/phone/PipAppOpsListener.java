package com.android.p011wm.shell.pip.phone;

import android.app.AppOpsManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.util.Pair;
import com.android.p011wm.shell.common.ShellExecutor;
import com.android.p011wm.shell.pip.PipUtils;

/* renamed from: com.android.wm.shell.pip.phone.PipAppOpsListener */
public class PipAppOpsListener {
    private AppOpsManager.OnOpChangedListener mAppOpsChangedListener = new AppOpsManager.OnOpChangedListener() {
        public void onOpChanged(String str, String str2) {
            try {
                Pair<ComponentName, Integer> topPipActivity = PipUtils.getTopPipActivity(PipAppOpsListener.this.mContext);
                if (topPipActivity.first != null) {
                    ApplicationInfo applicationInfoAsUser = PipAppOpsListener.this.mContext.getPackageManager().getApplicationInfoAsUser(str2, 0, ((Integer) topPipActivity.second).intValue());
                    if (applicationInfoAsUser.packageName.equals(((ComponentName) topPipActivity.first).getPackageName()) && PipAppOpsListener.this.mAppOpsManager.checkOpNoThrow(67, applicationInfoAsUser.uid, str2) != 0) {
                        PipAppOpsListener.this.mMainExecutor.execute(new PipAppOpsListener$1$$ExternalSyntheticLambda0(this));
                    }
                }
            } catch (PackageManager.NameNotFoundException unused) {
                PipAppOpsListener.this.unregisterAppOpsListener();
            }
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$onOpChanged$0() {
            PipAppOpsListener.this.mCallback.dismissPip();
        }
    };
    /* access modifiers changed from: private */
    public AppOpsManager mAppOpsManager;
    /* access modifiers changed from: private */
    public Callback mCallback;
    /* access modifiers changed from: private */
    public Context mContext;
    /* access modifiers changed from: private */
    public ShellExecutor mMainExecutor;

    /* renamed from: com.android.wm.shell.pip.phone.PipAppOpsListener$Callback */
    public interface Callback {
        void dismissPip();
    }

    public PipAppOpsListener(Context context, Callback callback, ShellExecutor shellExecutor) {
        this.mContext = context;
        this.mMainExecutor = shellExecutor;
        this.mAppOpsManager = (AppOpsManager) context.getSystemService("appops");
        this.mCallback = callback;
    }

    public void onActivityPinned(String str) {
        registerAppOpsListener(str);
    }

    public void onActivityUnpinned() {
        unregisterAppOpsListener();
    }

    private void registerAppOpsListener(String str) {
        this.mAppOpsManager.startWatchingMode(67, str, this.mAppOpsChangedListener);
    }

    /* access modifiers changed from: private */
    public void unregisterAppOpsListener() {
        this.mAppOpsManager.stopWatchingMode(this.mAppOpsChangedListener);
    }
}
