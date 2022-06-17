package com.android.systemui.statusbar.policy;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.android.internal.widget.LockPatternUtils;
import com.android.keyguard.KeyguardUpdateMonitor;
import com.android.systemui.ActivityIntentHelper;
import com.android.systemui.R$integer;
import com.android.systemui.statusbar.phone.KeyguardPreviewContainer;
import java.util.List;

public class PreviewInflater {
    private static int mNumberofCameras;
    private final ActivityIntentHelper mActivityIntentHelper;
    private Context mContext;
    private LockPatternUtils mLockPatternUtils;

    public PreviewInflater(Context context, LockPatternUtils lockPatternUtils, ActivityIntentHelper activityIntentHelper) {
        mNumberofCameras = context.getResources().getInteger(R$integer.config_default_max_camera_count);
        this.mContext = context;
        this.mLockPatternUtils = lockPatternUtils;
        this.mActivityIntentHelper = activityIntentHelper;
    }

    public View inflatePreview(Intent intent) {
        return inflatePreview(getWidgetInfo(intent));
    }

    public View inflatePreviewFromService(ComponentName componentName) {
        return inflatePreview(getWidgetInfoFromService(componentName));
    }

    private KeyguardPreviewContainer inflatePreview(WidgetInfo widgetInfo) {
        View inflateWidgetView;
        if (widgetInfo == null || (inflateWidgetView = inflateWidgetView(widgetInfo)) == null) {
            return null;
        }
        KeyguardPreviewContainer keyguardPreviewContainer = new KeyguardPreviewContainer(this.mContext, (AttributeSet) null);
        keyguardPreviewContainer.addView(inflateWidgetView);
        return keyguardPreviewContainer;
    }

    private View inflateWidgetView(WidgetInfo widgetInfo) {
        try {
            Context createPackageContext = this.mContext.createPackageContext(widgetInfo.contextPackage, 4);
            return ((LayoutInflater) createPackageContext.getSystemService("layout_inflater")).cloneInContext(createPackageContext).inflate(widgetInfo.layoutId, (ViewGroup) null, false);
        } catch (PackageManager.NameNotFoundException | RuntimeException e) {
            Log.w("PreviewInflater", "Error creating widget view", e);
            return null;
        }
    }

    private WidgetInfo getWidgetInfoFromService(ComponentName componentName) {
        if (componentName == null) {
            return null;
        }
        try {
            return getWidgetInfoFromMetaData(componentName.getPackageName(), this.mContext.getPackageManager().getServiceInfo(componentName, 128).metaData);
        } catch (PackageManager.NameNotFoundException e) {
            Log.w("PreviewInflater", "Failed to load preview; " + componentName.flattenToShortString() + " not found", e);
            return null;
        }
    }

    private WidgetInfo getWidgetInfoFromMetaData(String str, Bundle bundle) {
        if (bundle == null) {
            return null;
        }
        int i = 0;
        if (mNumberofCameras <= 1) {
            i = bundle.getInt("com.android.keyguard.layout_one_or_less_camera");
        }
        if (i == 0) {
            i = bundle.getInt("com.android.keyguard.layout");
        }
        if (i == 0) {
            return null;
        }
        WidgetInfo widgetInfo = new WidgetInfo();
        widgetInfo.contextPackage = str;
        widgetInfo.layoutId = i;
        return widgetInfo;
    }

    private WidgetInfo getWidgetInfo(Intent intent) {
        PackageManager packageManager = this.mContext.getPackageManager();
        List<ResolveInfo> queryIntentActivitiesAsUser = packageManager.queryIntentActivitiesAsUser(intent, 852096, KeyguardUpdateMonitor.getCurrentUser());
        if (queryIntentActivitiesAsUser.size() == 0) {
            return null;
        }
        ResolveInfo resolveActivityAsUser = packageManager.resolveActivityAsUser(intent, 852096, KeyguardUpdateMonitor.getCurrentUser());
        if (this.mActivityIntentHelper.wouldLaunchResolverActivity(resolveActivityAsUser, queryIntentActivitiesAsUser) || resolveActivityAsUser == null || resolveActivityAsUser.activityInfo == null) {
            return null;
        }
        if (queryIntentActivitiesAsUser.size() > 1) {
            String action = intent.getAction();
            if ("android.media.action.STILL_IMAGE_CAMERA_SECURE".equals(action) || "android.media.action.STILL_IMAGE_CAMERA".equals(action)) {
                for (ResolveInfo resolveInfo : queryIntentActivitiesAsUser) {
                    if (resolveInfo.activityInfo.packageName.equals(this.mContext.getResources().getString(17039984))) {
                        Log.i("PreviewInflater", "Multi cameras, load moto camera preview layout.");
                        ActivityInfo activityInfo = resolveInfo.activityInfo;
                        return getWidgetInfoFromMetaData(activityInfo.packageName, activityInfo.metaData);
                    }
                }
            }
        }
        ActivityInfo activityInfo2 = resolveActivityAsUser.activityInfo;
        return getWidgetInfoFromMetaData(activityInfo2.packageName, activityInfo2.metaData);
    }

    private static class WidgetInfo {
        String contextPackage;
        int layoutId;

        private WidgetInfo() {
        }
    }
}
