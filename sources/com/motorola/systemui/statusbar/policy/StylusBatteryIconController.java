package com.motorola.systemui.statusbar.policy;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Icon;
import android.os.Handler;
import android.os.UserHandle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.android.internal.statusbar.StatusBarIcon;
import com.android.settingslib.R$color;
import com.android.settingslib.utils.ThreadUtils;
import com.android.systemui.R$array;
import com.android.systemui.R$dimen;
import com.android.systemui.R$layout;
import com.android.systemui.settings.UserTracker;
import com.android.systemui.statusbar.phone.StatusBarIconController;
import com.motorola.stylussdk.ActiveStylusCallback;
import com.motorola.stylussdk.ActiveStylusProxy;
import com.motorola.stylussdk.StylusServiceListener;
import java.util.Arrays;

public class StylusBatteryIconController {
    private final String TAG = "StylusBatteryIconController";
    /* access modifiers changed from: private */
    public final ActiveStylusCallback mActiveStylusCallback = new ActiveStylusCallback() {
        public void onConnectStateChanged(String str, int i, int i2) {
            Log.d("StylusBatteryIconController", "onConnectStateChanged address " + str + " state " + i + " reason: " + i2 + " mStylusAttached: " + StylusBatteryIconController.this.mStylusAttached);
            boolean z = true;
            if (i != 1) {
                z = false;
            }
            if (StylusBatteryIconController.this.mStylusConnected != z) {
                boolean unused = StylusBatteryIconController.this.mStylusConnected = z;
                StylusBatteryIconController.this.updateStylusBatteryIcon();
            }
        }

        public void onAttachStatusChanged(String str, int i) {
            Log.d("StylusBatteryIconController", "onAttachStatusChanged address " + str + " status " + i + " mStylusConnected: " + StylusBatteryIconController.this.mStylusConnected);
            boolean z = true;
            if (i != 1) {
                z = false;
            }
            if (z) {
                StylusBatteryIconController.this.enableStylusAppsIfNeed();
            }
            if (StylusBatteryIconController.this.mStylusAttached != z) {
                boolean unused = StylusBatteryIconController.this.mStylusAttached = z;
                StylusBatteryIconController.this.updateStylusBatteryIcon();
            }
        }

        public void onBatteryLevelChanged(String str, int i) {
            Log.d("StylusBatteryIconController", "onBatteryLevelChanged address " + str + " level " + i);
            if (i == -1) {
                i = 0;
            }
            if (StylusBatteryIconController.this.mStylusBatteryLevel != i) {
                int unused = StylusBatteryIconController.this.mStylusBatteryLevel = i;
                StylusBatteryIconController.this.mDrawable.setBatteryLevel(StylusBatteryIconController.this.mStylusBatteryLevel);
                StylusBatteryIconController.this.updateStylusBatteryIcon();
            }
        }

        public void onChargeStatusChanged(String str, int i) {
            Log.d("StylusBatteryIconController", "onChargeStatusChanged address " + str + " status " + i + " mStylusAttached: " + StylusBatteryIconController.this.mStylusAttached + " mStylusConnected: " + StylusBatteryIconController.this.mStylusConnected);
            boolean z = true;
            if (i != 1) {
                z = false;
            }
            if (StylusBatteryIconController.this.mStylusCharging != z) {
                boolean unused = StylusBatteryIconController.this.mStylusCharging = z;
                StylusBatteryIconController.this.mDrawable.setCharging(StylusBatteryIconController.this.mStylusCharging);
                StylusBatteryIconController.this.updateStylusBatteryIcon();
            }
        }
    };
    /* access modifiers changed from: private */
    public ActiveStylusProxy mActiveStylusProxy = null;
    private String mContentDescription = "stylus battery";
    private final Context mContext;
    /* access modifiers changed from: private */
    public ThemedStylusBatteryDrawable mDrawable;
    private final StatusBarIconController mIconController;
    private int mIconWidth;
    /* access modifiers changed from: private */
    public int mReBindAttempts;
    /* access modifiers changed from: private */
    public boolean mServiceConnected;
    private final String mSlotStylusBattery;
    /* access modifiers changed from: private */
    public boolean mStylusAttached;
    private ImageView mStylusBatteryIconView;
    /* access modifiers changed from: private */
    public int mStylusBatteryLevel;
    /* access modifiers changed from: private */
    public boolean mStylusCharging;
    /* access modifiers changed from: private */
    public boolean mStylusConnected;
    private StylusServiceListener mStylusServiceListener = new StylusServiceListener() {
        public void onServiceConnected(ActiveStylusProxy activeStylusProxy) {
            Log.d("StylusBatteryIconController", "onServiceConnected");
            ActiveStylusProxy unused = StylusBatteryIconController.this.mActiveStylusProxy = activeStylusProxy;
            StylusBatteryIconController.this.mActiveStylusProxy.registerStylusCallback(StylusBatteryIconController.this.mActiveStylusCallback);
            boolean unused2 = StylusBatteryIconController.this.mServiceConnected = true;
            int unused3 = StylusBatteryIconController.this.mReBindAttempts = 0;
            StylusBatteryIconController.this.initStylusState();
            StylusBatteryIconController.this.initIconState();
            StylusBatteryIconController.this.updateStylusBatteryIcon();
        }

        public void onServiceDisconnected() {
            Log.d("StylusBatteryIconController", "onServiceDisconnected");
            ActiveStylusProxy unused = StylusBatteryIconController.this.mActiveStylusProxy = null;
            boolean unused2 = StylusBatteryIconController.this.mServiceConnected = false;
            StylusBatteryIconController.this.updateStylusBatteryIcon();
        }

        public void onBindingDied() {
            Log.d("StylusBatteryIconController", "onBindingDied");
            StylusBatteryIconController.this.retryBindStylusService();
        }

        public void onNullBinding() {
            Log.d("StylusBatteryIconController", "onNullBinding");
            StylusBatteryIconController.this.retryBindStylusService();
        }

        public void onBindFailed(int i) {
            Log.d("StylusBatteryIconController", "onBindFailed, reason: " + i);
            StylusBatteryIconController.this.retryBindStylusService();
        }
    };
    private final UserTracker mUserTracker;

    public StylusBatteryIconController(Context context, StatusBarIconController statusBarIconController, UserTracker userTracker) {
        this.mContext = context;
        this.mIconController = statusBarIconController;
        String string = context.getResources().getString(17041489);
        this.mSlotStylusBattery = string;
        statusBarIconController.setIconVisibility(string, false);
        ActiveStylusProxy.getActiveStylusProxy(context, this.mStylusServiceListener);
        this.mUserTracker = userTracker;
    }

    /* access modifiers changed from: private */
    public void initIconState() {
        Log.d("StylusBatteryIconController", "initIconState");
        ThemedStylusBatteryDrawable themedStylusBatteryDrawable = new ThemedStylusBatteryDrawable(this.mContext, this.mContext.getColor(R$color.meter_background_color));
        this.mDrawable = themedStylusBatteryDrawable;
        themedStylusBatteryDrawable.setBatteryLevel(this.mStylusBatteryLevel);
        this.mDrawable.setCharging(this.mStylusCharging);
        ImageView imageView = (ImageView) LayoutInflater.from(this.mContext).inflate(R$layout.stylus_battery_view, (ViewGroup) null);
        this.mStylusBatteryIconView = imageView;
        imageView.setImageDrawable(this.mDrawable);
        this.mIconWidth = this.mContext.getResources().getDimensionPixelSize(R$dimen.stylus_battery_icon_size);
    }

    /* access modifiers changed from: private */
    public void initStylusState() {
        boolean z = false;
        this.mStylusAttached = this.mActiveStylusProxy.getAttachStatus() == 1;
        this.mStylusConnected = this.mActiveStylusProxy.getConnectState() == 1;
        int batteryLevel = this.mActiveStylusProxy.getBatteryLevel();
        this.mStylusBatteryLevel = batteryLevel;
        if (batteryLevel == -1) {
            this.mStylusBatteryLevel = 0;
        }
        if (this.mActiveStylusProxy.getChargingStatus() == 1) {
            z = true;
        }
        this.mStylusCharging = z;
        Log.d("StylusBatteryIconController", "initStylusState mStylusAttached: " + this.mStylusAttached + " mStylusConnected: " + this.mStylusConnected + " mStylusBatteryLevel: " + this.mStylusBatteryLevel + " mStylusCharging: " + this.mStylusCharging);
        if (this.mStylusAttached) {
            enableStylusAppsIfNeed();
        }
    }

    /* access modifiers changed from: private */
    public void updateStylusBatteryIcon() {
        ThreadUtils.postOnMainThread(new StylusBatteryIconController$$ExternalSyntheticLambda1(this));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$updateStylusBatteryIcon$0() {
        updateStylusBatteryIcon(this.mStylusAttached || this.mStylusConnected);
    }

    private void updateStylusBatteryIcon(boolean z) {
        if (z) {
            this.mIconController.setIcon(this.mSlotStylusBattery, getIcon());
            this.mIconController.setIconVisibility(this.mSlotStylusBattery, true);
            return;
        }
        this.mIconController.setIcon(this.mSlotStylusBattery, (StatusBarIcon) null);
    }

    /* access modifiers changed from: private */
    public void enableStylusAppsIfNeed() {
        try {
            Context userContext = this.mUserTracker.getUserContext();
            Log.d("StylusBatteryIconController", "enableStylusAppsIfNeed: user = " + userContext.getUserId());
            PackageManager packageManager = userContext.getPackageManager();
            String[] stringArray = userContext.getResources().getStringArray(R$array.stylus_apps);
            Log.d("StylusBatteryIconController", "enableStylusApp: apps = " + Arrays.toString(stringArray));
            if (stringArray == null) {
                return;
            }
            if (stringArray.length != 0) {
                for (String str : stringArray) {
                    if (packageManager.getApplicationEnabledSetting(str) != 1) {
                        Log.d("StylusBatteryIconController", "enableStylusApp: " + str);
                        packageManager.setApplicationEnabledSetting(str, 1, 0);
                    }
                }
            }
        } catch (Exception e) {
            Log.d("StylusBatteryIconController", "enableStylusApp: enable app failed." + e);
        }
    }

    private Bitmap getIconBitmap(boolean z) {
        this.mDrawable.setOnlyDrawTintedPart(z);
        ImageView imageView = this.mStylusBatteryIconView;
        if (imageView == null) {
            return null;
        }
        imageView.measure(View.MeasureSpec.makeMeasureSpec(0, 0), View.MeasureSpec.makeMeasureSpec(0, 0));
        ImageView imageView2 = this.mStylusBatteryIconView;
        imageView2.layout(0, 0, this.mIconWidth, imageView2.getMeasuredHeight());
        this.mStylusBatteryIconView.setDrawingCacheEnabled(true);
        Bitmap createBitmap = Bitmap.createBitmap(this.mStylusBatteryIconView.getDrawingCache());
        this.mStylusBatteryIconView.destroyDrawingCache();
        return createBitmap;
    }

    private StatusBarIcon getIcon() {
        Bitmap iconBitmap = getIconBitmap(false);
        if (iconBitmap != null) {
            StatusBarIcon statusBarIcon = new StatusBarIcon(UserHandle.SYSTEM, this.mContext.getPackageName(), Icon.createWithBitmap(iconBitmap), 0, 0, this.mContentDescription);
            if (this.mDrawable.haveTintedPart()) {
                statusBarIcon.setTintedIcon(Icon.createWithBitmap(getIconBitmap(true)));
            }
            return statusBarIcon;
        }
        Log.d("StylusBatteryIconController", "battery icon is null!");
        return null;
    }

    /* access modifiers changed from: private */
    public void retryBindStylusService() {
        Log.d("StylusBatteryIconController", "retryBindStylusService:  mReBindAttempts= " + this.mReBindAttempts);
        int i = this.mReBindAttempts;
        this.mReBindAttempts = i + 1;
        if (i < 5) {
            new Handler().postDelayed(new StylusBatteryIconController$$ExternalSyntheticLambda0(this), 10000);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$retryBindStylusService$1() {
        ActiveStylusProxy.getActiveStylusProxy(this.mContext, this.mStylusServiceListener);
    }
}
