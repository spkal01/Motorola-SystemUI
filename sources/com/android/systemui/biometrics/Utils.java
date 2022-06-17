package com.android.systemui.biometrics;

import android.content.Context;
import android.hardware.biometrics.PromptInfo;
import android.hardware.biometrics.SensorPropertiesInternal;
import android.os.Build;
import android.os.UserManager;
import android.util.Log;
import android.util.MathUtils;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import com.android.internal.widget.LockPatternUtils;
import java.util.List;

public class Utils {
    static float dpToPixels(Context context, float f) {
        return f * (((float) context.getResources().getDisplayMetrics().densityDpi) / 160.0f);
    }

    static void notifyAccessibilityContentChanged(AccessibilityManager accessibilityManager, ViewGroup viewGroup) {
        if (accessibilityManager.isEnabled()) {
            AccessibilityEvent obtain = AccessibilityEvent.obtain();
            obtain.setEventType(2048);
            obtain.setContentChangeTypes(1);
            viewGroup.sendAccessibilityEventUnchecked(obtain);
            viewGroup.notifySubtreeAccessibilityStateChanged(viewGroup, viewGroup, 1);
        }
    }

    static boolean isDeviceCredentialAllowed(PromptInfo promptInfo) {
        return (promptInfo.getAuthenticators() & 32768) != 0;
    }

    static boolean isBiometricAllowed(PromptInfo promptInfo) {
        return (promptInfo.getAuthenticators() & 255) != 0;
    }

    static int getCredentialType(Context context, int i) {
        int keyguardStoredPasswordQuality = new LockPatternUtils(context).getKeyguardStoredPasswordQuality(i);
        if (keyguardStoredPasswordQuality != 65536) {
            return (keyguardStoredPasswordQuality == 131072 || keyguardStoredPasswordQuality == 196608) ? 1 : 3;
        }
        return 2;
    }

    static boolean isManagedProfile(Context context, int i) {
        return ((UserManager) context.getSystemService(UserManager.class)).isManagedProfile(i);
    }

    static boolean containsSensorId(List<? extends SensorPropertiesInternal> list, int i) {
        if (list == null) {
            return false;
        }
        for (SensorPropertiesInternal sensorPropertiesInternal : list) {
            if (sensorPropertiesInternal.sensorId == i) {
                return true;
            }
        }
        return false;
    }

    static boolean isSystem(Context context, String str) {
        if (!(context.checkCallingOrSelfPermission("android.permission.USE_BIOMETRIC_INTERNAL") == 0) || !"android".equals(str)) {
            return false;
        }
        return true;
    }

    static float convertNits2Alpha(int i, float f, float f2, boolean z, boolean z2) {
        float f3 = 0.9f;
        float f4 = 0.0f;
        if (z) {
            f3 = 1.0f;
        } else if (!z2) {
            if (f < 0.0f) {
                f3 = MathUtils.constrain(((float) (255 - i)) / 255.0f, 0.0f, 0.9f);
            } else {
                float f5 = f2 - f;
                f3 = MathUtils.constrain(MathUtils.exp(0.004f * f5) * 0.0706f, 0.0f, 0.99f);
                f4 = f5;
            }
        }
        if (!Build.IS_USER) {
            Log.d("UdfpsView", "new brightness change brightness=" + i + " nits=" + f + " deltaNits=" + f4 + " disable_masking=" + false + " generate alpha=" + f3);
        }
        return f3;
    }
}
