package com.android.systemui.p006qs;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.provider.Settings;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import com.android.systemui.R$bool;
import com.android.systemui.moto.MotoFeature;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/* renamed from: com.android.systemui.qs.MotoConfirmationStubActivity */
public class MotoConfirmationStubActivity extends Activity {
    public static final boolean DBG = Build.IS_DEBUGGABLE;
    public static final String TAG = "MotoConfirmationStubActivity";
    private static final Map<String, List<String>> myMap = createMap();

    private static Map<String, List<String>> createMap() {
        HashMap hashMap = new HashMap();
        hashMap.put("vzw", Arrays.asList(new String[]{"com.motorola.vzw.phone.extensions.dataenabled.DATAENABLED_UNCHECKED", "com.motorola.vzw.phone.extensions", "com.motorola.vzw.phone.extensions.dataenabled.DataEnabled"}));
        hashMap.put("att", Arrays.asList(new String[]{"com.motorola.att.phone.extensions.dataenabled.DATAENABLED_UNCHECKED", "com.motorola.att.phone.extensions", "com.motorola.att.phone.extensions.dataenabled.DataEnabled"}));
        return hashMap;
    }

    public void onCreate(Bundle bundle) {
        String action;
        super.onCreate(bundle);
        Intent intent = getIntent();
        if (intent != null && (action = intent.getAction()) != null) {
            if (action.equals("com.motorola.vzw.phone.extensions.dataenabled.DATAENABLED_UNCHECKED")) {
                List list = myMap.get(intent.getStringExtra("carrier"));
                if (list == null || list.size() != 3 || !isSystemApp((String) list.get(1)) || isOnCli(this)) {
                    if (DBG) {
                        Log.d(TAG, "Skipping Mobile Data disable dialog.");
                    }
                    setDataEnabled(false);
                    finish();
                    return;
                }
                Intent intent2 = new Intent((String) list.get(0));
                intent2.setClassName((String) list.get(1), (String) list.get(2));
                intent2.putExtra("launched_from_qs", true);
                try {
                    startActivity(intent2);
                } catch (ActivityNotFoundException unused) {
                    setDataEnabled(false);
                }
            }
            finish();
        }
    }

    private void setDataEnabled(boolean z) {
        ((TelephonyManager) getSystemService("phone")).setDataEnabled(SubscriptionManager.getDefaultDataSubscriptionId(), z);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:4:0x000c, code lost:
        r2 = r2.applicationInfo;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private boolean isSystemApp(java.lang.String r3) {
        /*
            r2 = this;
            r0 = 1
            r1 = 0
            android.content.pm.PackageManager r2 = r2.getPackageManager()     // Catch:{ NameNotFoundException -> 0x0016 }
            android.content.pm.PackageInfo r2 = r2.getPackageInfo(r3, r1)     // Catch:{ NameNotFoundException -> 0x0016 }
            if (r2 == 0) goto L_0x0025
            android.content.pm.ApplicationInfo r2 = r2.applicationInfo     // Catch:{ NameNotFoundException -> 0x0016 }
            if (r2 == 0) goto L_0x0025
            int r2 = r2.flags     // Catch:{ NameNotFoundException -> 0x0016 }
            r2 = r2 & r0
            if (r2 == 0) goto L_0x0025
            return r0
        L_0x0016:
            java.lang.String r2 = TAG
            java.lang.Object[] r0 = new java.lang.Object[r0]
            r0[r1] = r3
            java.lang.String r3 = "Couldn't find package [%s]."
            java.lang.String r3 = java.lang.String.format(r3, r0)
            android.util.Log.w(r2, r3)
        L_0x0025:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.p006qs.MotoConfirmationStubActivity.isSystemApp(java.lang.String):boolean");
    }

    public static boolean isDialogEnabled(Context context) {
        return (context.getResources().getBoolean(R$bool.feature_3621_show_data_off_dialog) || context.getResources().getBoolean(R$bool.ftr_5261_att_data_settings)) && Settings.Global.getInt(context.getContentResolver(), "mobile_data", 0) == 1;
    }

    public static void sendDialogIntent(Context context) {
        boolean z = context.getResources().getBoolean(R$bool.feature_3621_show_data_off_dialog);
        Intent intent = new Intent(context, MotoConfirmationStubActivity.class);
        intent.setAction("com.motorola.vzw.phone.extensions.dataenabled.DATAENABLED_UNCHECKED");
        if (z) {
            intent.putExtra("carrier", "vzw");
        } else {
            intent.putExtra("carrier", "att");
        }
        intent.setFlags(268435456);
        context.startActivityAsUser(intent, UserHandle.CURRENT_OR_SELF);
    }

    public static boolean isOnCli(Context context) {
        boolean isSupportCli = MotoFeature.getInstance(context).isSupportCli();
        boolean isLidClosed = MotoFeature.isLidClosed(context);
        if (DBG) {
            Log.d(TAG, String.format("isCliSupported = %b; isLidClosed = %b", new Object[]{Boolean.valueOf(isSupportCli), Boolean.valueOf(isLidClosed)}));
        }
        if (!isSupportCli || !isLidClosed) {
            return false;
        }
        return true;
    }

    public static Intent getIntentResolvedToSystemActivity(Context context, String str) {
        if (context == null || TextUtils.isEmpty(str)) {
            return null;
        }
        Intent intent = new Intent(str);
        List list = (List) context.getPackageManager().queryIntentActivities(intent, 0).stream().filter(MotoConfirmationStubActivity$$ExternalSyntheticLambda0.INSTANCE).collect(Collectors.toList());
        if (list.isEmpty()) {
            if (DBG) {
                Log.d(TAG, "None Activity found for the action " + str);
            }
            return null;
        }
        boolean z = true;
        if (list.size() > 1) {
            if (SystemProperties.getInt("ro.debuggable", 0) != 1) {
                z = false;
            }
            if (z) {
                throw new IllegalArgumentException("Found more than one Activity for the action " + str);
            }
        }
        ActivityInfo activityInfo = ((ResolveInfo) list.get(0)).activityInfo;
        return intent.setClassName(activityInfo.packageName, activityInfo.name);
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$getIntentResolvedToSystemActivity$0(ResolveInfo resolveInfo) {
        return (resolveInfo.activityInfo.applicationInfo.flags & 1) != 0;
    }
}
