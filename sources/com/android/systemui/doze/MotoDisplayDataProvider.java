package com.android.systemui.doze;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import com.android.keyguard.KeyguardUpdateMonitor;
import com.android.systemui.Dependency;

public class MotoDisplayDataProvider {
    private static final boolean DEBUG = Build.IS_DEBUGGABLE;
    private Context mContext;
    KeyguardUpdateMonitor mKeyguardUpdateMonitor = ((KeyguardUpdateMonitor) Dependency.get(KeyguardUpdateMonitor.class));

    public MotoDisplayDataProvider(Context context) {
        this.mContext = context;
    }

    public Bundle getExtraDataForShow() {
        Bundle bundle = new Bundle();
        int currentUser = KeyguardUpdateMonitor.getCurrentUser();
        bundle.putBoolean("isUserInLockdown_ACTION", this.mKeyguardUpdateMonitor.isUserInLockdown(currentUser));
        bundle.putInt("isUserInLockdown_userId", currentUser);
        return bundle;
    }

    public Bundle queryDataInternal(Bundle bundle) {
        if (bundle == null) {
            return null;
        }
        String string = bundle.getString("ACTION");
        if (TextUtils.isEmpty(string)) {
            return null;
        }
        try {
            if (string.equals("isUserInLockdown_ACTION")) {
                boolean isUserInLockdown = this.mKeyguardUpdateMonitor.isUserInLockdown(bundle.getInt("isUserInLockdown_userId", -10000));
                Bundle bundle2 = new Bundle();
                bundle2.putBoolean("RESULT", isUserInLockdown);
                return bundle2;
            }
        } catch (Throwable th) {
            Log.e("MotoDisplayDataProvider", "queryDataInternal<request>" + bundle, th);
        }
        return null;
    }
}
