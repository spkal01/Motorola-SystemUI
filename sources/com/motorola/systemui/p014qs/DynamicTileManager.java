package com.motorola.systemui.p014qs;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.UserHandle;
import android.text.TextUtils;
import android.util.Log;
import com.android.systemui.Dependency;
import com.android.systemui.R$bool;
import com.android.systemui.R$integer;
import com.android.systemui.p006qs.QSTileHost;
import com.android.systemui.p006qs.external.CustomTile;
import com.android.systemui.p006qs.external.TileLifecycleManager;
import com.android.systemui.p006qs.tileimpl.QSTileImpl;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/* renamed from: com.motorola.systemui.qs.DynamicTileManager */
public class DynamicTileManager {
    /* access modifiers changed from: private */
    public static final boolean DEBUG = (!Build.IS_USER);
    private static DynamicTileManager sInstance;
    /* access modifiers changed from: private */
    public Context mContext;
    /* access modifiers changed from: private */
    public final ArrayList<DynamicTile> mDynamicTiles = new ArrayList<>();
    private Handler mHandler;
    private BroadcastReceiver mPackageReceiver;
    /* access modifiers changed from: private */
    public QSTileHost mQSTileHost;

    /* renamed from: com.motorola.systemui.qs.DynamicTileManager$Prefs */
    private static final class Prefs {
        public static boolean getBoolean(Context context, String str, boolean z) {
            return get(context).getBoolean(str + ActivityManager.getCurrentUser(), z);
        }

        public static void putBoolean(Context context, String str, boolean z) {
            get(context).edit().putBoolean(str + ActivityManager.getCurrentUser(), z).apply();
        }

        private static SharedPreferences get(Context context) {
            return context.getSharedPreferences(context.getPackageName(), 0);
        }
    }

    /* renamed from: com.motorola.systemui.qs.DynamicTileManager$DynamicTile */
    private abstract class DynamicTile {
        protected String mTileSpec;

        public QSTileImpl createSystemTile() {
            return null;
        }

        public abstract String getClassName();

        public abstract String getPackageName();

        /* access modifiers changed from: protected */
        public int getPositionResId() {
            return 0;
        }

        /* access modifiers changed from: protected */
        public abstract String getPreferenceKey();

        /* access modifiers changed from: protected */
        public boolean isFeatureEnabled() {
            return true;
        }

        public boolean isSystemTile() {
            return false;
        }

        public boolean isTileAvailable() {
            return true;
        }

        public boolean needsInstantLoad() {
            return true;
        }

        public boolean needsPackageEvent() {
            return false;
        }

        public boolean needsStock() {
            return false;
        }

        public void updateTileIfNeeded() {
        }

        private DynamicTile() {
        }

        public String getTileSpec() {
            if (this.mTileSpec == null) {
                this.mTileSpec = CustomTile.toSpec(new ComponentName(getPackageName(), getClassName()));
            }
            return this.mTileSpec;
        }

        public ComponentName getComponentName() {
            if (isCustomTile()) {
                return CustomTile.getComponentFromSpec(getTileSpec());
            }
            return null;
        }

        /* access modifiers changed from: protected */
        public int getPosition(int i) {
            return DynamicTileManager.this.mContext.getResources().getInteger(i);
        }

        public int getPosition() {
            return getPosition((List<String>) null);
        }

        /* access modifiers changed from: protected */
        public int getPosition(List<String> list) {
            if (getPositionResId() != 0) {
                return getPosition(getPositionResId());
            }
            return 2147483646;
        }

        public boolean isAdded() {
            return Prefs.getBoolean(DynamicTileManager.this.mContext, getPreferenceKey(), false);
        }

        public void setAdded(boolean z) {
            Prefs.putBoolean(DynamicTileManager.this.mContext, getPreferenceKey(), z);
        }

        public void load() {
            if (needsInstantLoad() && isAvailable()) {
                addTile();
            }
        }

        public void reload(List<String> list) {
            setAdded(false);
            if (needsInstantLoad() && isAvailable()) {
                addTile(list);
            }
        }

        /* access modifiers changed from: protected */
        public void addTile() {
            String tileSpec = getTileSpec();
            if (DynamicTileManager.this.mQSTileHost.indexOf(tileSpec) < 0) {
                int position = getPosition();
                if (position == 2147483646) {
                    DynamicTileManager.this.mQSTileHost.addTile(tileSpec);
                    if (DynamicTileManager.DEBUG) {
                        Log.d("DynamicTileManager", tileSpec + " tile appended");
                    }
                } else {
                    DynamicTileManager.this.mQSTileHost.addTile(tileSpec, position);
                    if (DynamicTileManager.DEBUG) {
                        Log.d("DynamicTileManager", tileSpec + " tile added at: " + position);
                    }
                }
            }
            setAdded(true);
        }

        /* access modifiers changed from: protected */
        public void addTile(List<String> list) {
            String tileSpec = getTileSpec();
            if (list != null && list.indexOf(tileSpec) < 0) {
                int position = getPosition(list);
                if (position == 2147483646) {
                    list.add(tileSpec);
                    if (DynamicTileManager.DEBUG) {
                        Log.d("DynamicTileManager", tileSpec + " tile appended");
                    }
                } else {
                    int min = Math.min(position, list.size());
                    list.add(min, tileSpec);
                    if (DynamicTileManager.DEBUG) {
                        Log.d("DynamicTileManager", tileSpec + " tile added at: " + min);
                    }
                }
            }
            setAdded(true);
        }

        /* access modifiers changed from: protected */
        public void removeTile() {
            setAdded(false);
            if (DynamicTileManager.DEBUG) {
                Log.d("DynamicTileManager", getTileSpec() + " tile removed");
            }
        }

        /* access modifiers changed from: protected */
        public boolean isAvailable() {
            boolean z = false;
            if (!isFeatureEnabled()) {
                return false;
            }
            if (isCustomTile()) {
                try {
                    PackageManager packageManager = DynamicTileManager.this.mContext.getPackageManager();
                    ApplicationInfo applicationInfo = packageManager.getApplicationInfo(getPackageName(), 0);
                    if (!(applicationInfo == null || !applicationInfo.enabled || packageManager.getServiceInfo(getComponentName(), 786944) == null)) {
                        z = true;
                    }
                } catch (Exception unused) {
                }
            }
            if (DynamicTileManager.DEBUG) {
                Log.d("DynamicTileManager", getTileSpec() + " tile available: " + z);
            }
            return z;
        }

        /* access modifiers changed from: protected */
        public boolean hasSystemFeature(String str) {
            return DynamicTileManager.this.mContext.getPackageManager().hasSystemFeature(str);
        }

        public void onPackageEvent(String str, String str2, boolean z) {
            if (TextUtils.equals(str2, getPackageName())) {
                str.hashCode();
                char c = 65535;
                switch (str.hashCode()) {
                    case 172491798:
                        if (str.equals("android.intent.action.PACKAGE_CHANGED")) {
                            c = 0;
                            break;
                        }
                        break;
                    case 525384130:
                        if (str.equals("android.intent.action.PACKAGE_REMOVED")) {
                            c = 1;
                            break;
                        }
                        break;
                    case 1544582882:
                        if (str.equals("android.intent.action.PACKAGE_ADDED")) {
                            c = 2;
                            break;
                        }
                        break;
                }
                switch (c) {
                    case 0:
                        if (isAvailable()) {
                            addTile();
                            return;
                        } else {
                            removeTile();
                            return;
                        }
                    case 1:
                        if (!z || !isAvailable()) {
                            removeTile();
                            return;
                        }
                        return;
                    case 2:
                        if (!isAdded() && isAvailable()) {
                            addTile();
                            return;
                        }
                        return;
                    default:
                        return;
                }
            }
        }

        public boolean isCustomTile() {
            String tileSpec = getTileSpec();
            return tileSpec != null && tileSpec.startsWith("custom(");
        }
    }

    /* renamed from: com.motorola.systemui.qs.DynamicTileManager$MobileDesktopTile */
    private final class MobileDesktopTile extends DynamicTile {
        public String getClassName() {
            return "com.motorola.mobiledesktop.core.desktop.tile.MobileDesktopTile";
        }

        public String getPackageName() {
            return "com.motorola.mobiledesktop.core";
        }

        /* access modifiers changed from: protected */
        public String getPreferenceKey() {
            return "QsMobileDesktopAdded";
        }

        public boolean needsPackageEvent() {
            return true;
        }

        private MobileDesktopTile() {
            super();
        }

        /* access modifiers changed from: protected */
        public int getPositionResId() {
            return R$integer.mobiledesktop_tile_position;
        }

        /* access modifiers changed from: protected */
        public boolean isFeatureEnabled() {
            try {
                ApplicationInfo applicationInfo = DynamicTileManager.this.mContext.getPackageManager().getApplicationInfo("com.motorola.mobiledesktop.core", 0);
                if (applicationInfo != null && applicationInfo.enabled) {
                    return true;
                }
                Log.d("DynamicTileManager", "isFeatureEnabled false: mobiledesktop not installed!");
                return false;
            } catch (Exception unused) {
            }
        }
    }

    /* renamed from: com.motorola.systemui.qs.DynamicTileManager$AlexaTile */
    private final class AlexaTile extends DynamicTile {
        public String getClassName() {
            return "com.amazon.alexa.handsfree.settings.quicksettings.AlexaQuickSettingService";
        }

        public String getPackageName() {
            return "com.motorola.motoalexa";
        }

        /* access modifiers changed from: protected */
        public String getPreferenceKey() {
            return "QsAlexaAdded";
        }

        public boolean needsPackageEvent() {
            return true;
        }

        private AlexaTile() {
            super();
        }

        /* access modifiers changed from: protected */
        public int getPositionResId() {
            return R$integer.alexa_tile_position;
        }
    }

    /* renamed from: com.motorola.systemui.qs.DynamicTileManager$PowerSharingTile */
    private final class PowerSharingTile extends DynamicTile {
        public String getClassName() {
            return "com.motorola.coresettingsext.charging.wireless.PSWirelessTitleService";
        }

        public String getPackageName() {
            return "com.motorola.coresettingsext";
        }

        /* access modifiers changed from: protected */
        public String getPreferenceKey() {
            return "QsPowerSharingAdded";
        }

        private PowerSharingTile() {
            super();
        }

        /* access modifiers changed from: protected */
        public boolean isFeatureEnabled() {
            return hasSystemFeature("com.motorola.hardware.wireless_power_share");
        }
    }

    /* renamed from: com.motorola.systemui.qs.DynamicTileManager$AlexaHandsFreeTile */
    private final class AlexaHandsFreeTile extends DynamicTile {
        public String getClassName() {
            return "com.amazon.alexa.handsfree.settings.quicksettings.AlexaQuickSettingService";
        }

        public String getPackageName() {
            return "com.amazon.dee.app";
        }

        /* access modifiers changed from: protected */
        public String getPreferenceKey() {
            return "QsAlexaAdded";
        }

        public boolean needsPackageEvent() {
            return true;
        }

        private AlexaHandsFreeTile() {
            super();
        }

        /* access modifiers changed from: protected */
        public int getPositionResId() {
            return R$integer.alexa_tile_position;
        }

        /* access modifiers changed from: protected */
        public boolean isFeatureEnabled() {
            try {
                ApplicationInfo applicationInfo = DynamicTileManager.this.mContext.getPackageManager().getApplicationInfo("com.quicinc.voice.activation", 0);
                if (applicationInfo != null && applicationInfo.enabled) {
                    return true;
                }
                Log.d("DynamicTileManager", "isFeatureEnabled false: com.quicinc.voice.activation not installed!");
                return false;
            } catch (Exception unused) {
            }
        }
    }

    /* renamed from: com.motorola.systemui.qs.DynamicTileManager$DolbyTile */
    private final class DolbyTile extends DynamicTile {
        /* access modifiers changed from: protected */
        public String getPreferenceKey() {
            return "QsDolbyAdded";
        }

        private DolbyTile() {
            super();
        }

        public String getPackageName() {
            return hasSystemFeature("com.motorola.software.dolbyui") ? "com.motorola.dolby.dolbyui" : "com.dolby.dax2appUI";
        }

        public String getClassName() {
            return hasSystemFeature("com.motorola.software.dolbyui") ? ".AudioEffectTileService" : "com.motorola.dlbafx.AudioEffectTileService";
        }

        /* access modifiers changed from: protected */
        public int getPositionResId() {
            return R$integer.motoaudio_tile_position;
        }

        /* access modifiers changed from: protected */
        public boolean isFeatureEnabled() {
            return hasSystemFeature("com.dolby.dax2appUI") || hasSystemFeature("com.motorola.software.dolbyui");
        }

        public void updateTileIfNeeded() {
            ComponentName unflattenFromString = ComponentName.unflattenFromString("com.dolby.dax2appUI/com.motorola.dlbafx.AudioEffectTileService");
            ComponentName unflattenFromString2 = ComponentName.unflattenFromString("com.motorola.dolby.dolbyui/.AudioEffectTileService");
            if (TileLifecycleManager.isTileAdded(DynamicTileManager.this.mContext, unflattenFromString)) {
                if (getPackageName() != unflattenFromString.getPackageName() && hasSystemFeature(getPackageName()) && DynamicTileManager.DEBUG) {
                    Log.d("DynamicTileManager", "Dolby package changed, need to replace old Dolby tile.");
                }
                DynamicTileManager.this.mQSTileHost.replaceTile(CustomTile.toSpec(unflattenFromString), CustomTile.toSpec(unflattenFromString2));
                TileLifecycleManager.setTileAdded(DynamicTileManager.this.mContext, unflattenFromString, false);
            }
        }
    }

    /* renamed from: com.motorola.systemui.qs.DynamicTileManager$SystemUpdateTile */
    private final class SystemUpdateTile extends DynamicTile {
        public String getClassName() {
            return ".env.SystemUpdateQSTile";
        }

        public String getPackageName() {
            return "com.motorola.ccc.ota";
        }

        /* access modifiers changed from: protected */
        public String getPreferenceKey() {
            return "QsSystemUpdateAdded";
        }

        private SystemUpdateTile() {
            super();
        }

        /* access modifiers changed from: protected */
        public int getPositionResId() {
            return R$integer.su_tile_position;
        }
    }

    /* renamed from: com.motorola.systemui.qs.DynamicTileManager$SelfieCameraTile */
    private final class SelfieCameraTile extends DynamicTile {
        public String getClassName() {
            return "com.motorola.camera.service.SelfieCameraTileService";
        }

        public String getPackageName() {
            return "com.motorola.camera2";
        }

        /* access modifiers changed from: protected */
        public String getPreferenceKey() {
            return "QsSelfieCameraAdded";
        }

        private SelfieCameraTile() {
            super();
        }

        /* access modifiers changed from: protected */
        public boolean isFeatureEnabled() {
            return DynamicTileManager.this.mContext.getResources().getBoolean(R$bool.config_selfie_camera_in_tile);
        }

        /* access modifiers changed from: protected */
        public int getPositionResId() {
            return R$integer.selfie_camera_tile_position;
        }
    }

    /* renamed from: com.motorola.systemui.qs.DynamicTileManager$GIFMakerTile */
    private final class GIFMakerTile extends DynamicTile {
        public String getClassName() {
            return "com.motorola.gifmaker.ui.GIFMakerTileService";
        }

        public String getPackageName() {
            return "com.motorola.screenshoteditor";
        }

        /* access modifiers changed from: protected */
        public String getPreferenceKey() {
            return "QsGIFMakerTileAdded";
        }

        public boolean needsPackageEvent() {
            return true;
        }

        private GIFMakerTile() {
            super();
        }

        /* access modifiers changed from: protected */
        public boolean isFeatureEnabled() {
            return hasSystemFeature("com.motorola.screenshoteditor") && hasSystemFeature("com.motorola.gifmaker");
        }

        public int getPosition() {
            int indexOf = DynamicTileManager.this.mQSTileHost.indexOf("screenrecord");
            if (indexOf >= 0) {
                return indexOf + 1;
            }
            return getPosition((List<String>) null);
        }

        public boolean isTileAvailable() {
            return isFeatureEnabled();
        }
    }

    /* renamed from: com.motorola.systemui.qs.DynamicTileManager$FeedbackTile */
    private final class FeedbackTile extends DynamicTile {
        public String getClassName() {
            return "com.motorola.feedback.quicksettings.FeedBackTileService";
        }

        public String getPackageName() {
            return "com.motorola.help";
        }

        /* access modifiers changed from: protected */
        public String getPreferenceKey() {
            return "QsFeedbackAdded";
        }

        private FeedbackTile() {
            super();
        }

        /* access modifiers changed from: protected */
        public boolean isFeatureEnabled() {
            return DynamicTileManager.this.mContext.getResources().getBoolean(R$bool.config_feedback_in_tile);
        }
    }

    private DynamicTileManager(QSTileHost qSTileHost) {
        this.mQSTileHost = qSTileHost;
        this.mContext = qSTileHost.getContext();
        this.mHandler = new Handler((Looper) Dependency.get(Dependency.BG_LOOPER));
        setupTileList();
    }

    private void setupTileList() {
        addDynamicTile(new AlexaTile());
        addDynamicTile(new DolbyTile());
        addDynamicTile(new SystemUpdateTile());
        addDynamicTile(new SelfieCameraTile());
        addDynamicTile(new PowerSharingTile());
        addDynamicTile(new AlexaHandsFreeTile());
        addDynamicTile(new GIFMakerTile());
        addDynamicTile(new MobileDesktopTile());
        addDynamicTile(new FeedbackTile());
    }

    private void addDynamicTile(DynamicTile dynamicTile) {
        int position = dynamicTile.getPosition();
        for (int i = 0; i < this.mDynamicTiles.size(); i++) {
            if (this.mDynamicTiles.get(i).getPosition() > position) {
                this.mDynamicTiles.add(i, dynamicTile);
                return;
            }
        }
        this.mDynamicTiles.add(dynamicTile);
    }

    private void registerPackageReceiver(IntentFilter intentFilter) {
        if (this.mPackageReceiver == null) {
            this.mPackageReceiver = new BroadcastReceiver() {
                public void onReceive(Context context, Intent intent) {
                    if (intent != null && intent.getData() != null) {
                        String action = intent.getAction();
                        String encodedSchemeSpecificPart = intent.getData().getEncodedSchemeSpecificPart();
                        boolean z = intent.getExtras() != null && intent.getExtras().getBoolean("android.intent.extra.REPLACING");
                        if (DynamicTileManager.DEBUG) {
                            Log.d("DynamicTileManager", "Received broadcast: " + action + " for: " + encodedSchemeSpecificPart + " replacing: " + z);
                        }
                        DynamicTileManager.this.mDynamicTiles.stream().filter(DynamicTileManager$1$$ExternalSyntheticLambda1.INSTANCE).forEach(new DynamicTileManager$1$$ExternalSyntheticLambda0(action, encodedSchemeSpecificPart, z));
                    } else if (DynamicTileManager.DEBUG) {
                        Log.w("DynamicTileManager", "Invalid intent or data for package broadcast");
                    }
                }
            };
            intentFilter.addDataScheme("package");
            intentFilter.addAction("android.intent.action.PACKAGE_ADDED");
            intentFilter.addAction("android.intent.action.PACKAGE_CHANGED");
            intentFilter.addAction("android.intent.action.PACKAGE_REMOVED");
            this.mContext.registerReceiverAsUser(this.mPackageReceiver, new UserHandle(ActivityManager.getCurrentUser()), intentFilter, (String) null, this.mHandler);
        }
    }

    public static DynamicTileManager getInstance(QSTileHost qSTileHost) {
        if (sInstance == null) {
            sInstance = new DynamicTileManager(qSTileHost);
        }
        return sInstance;
    }

    public void loadTiles() {
        if (DEBUG) {
            Log.d("DynamicTileManager", "Loading dynamic tiles");
        }
        IntentFilter intentFilter = new IntentFilter();
        this.mDynamicTiles.forEach(new DynamicTileManager$$ExternalSyntheticLambda0(intentFilter));
        if (intentFilter.countDataSchemeSpecificParts() > 0) {
            registerPackageReceiver(intentFilter);
        }
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$loadTiles$0(IntentFilter intentFilter, DynamicTile dynamicTile) {
        if (!dynamicTile.isAdded()) {
            dynamicTile.load();
        }
        if (dynamicTile.needsPackageEvent()) {
            intentFilter.addDataSchemeSpecificPart(dynamicTile.getPackageName(), 0);
        }
    }

    public void reloadTiles(List<String> list) {
        if (DEBUG) {
            Log.d("DynamicTileManager", "Reloading dynamic tiles. defaultTiles: " + list);
        }
        this.mDynamicTiles.forEach(new DynamicTileManager$$ExternalSyntheticLambda2(list));
    }

    public void addStockTiles(ArrayList<String> arrayList) {
        if (arrayList != null) {
            this.mDynamicTiles.stream().filter(DynamicTileManager$$ExternalSyntheticLambda3.INSTANCE).forEach(new DynamicTileManager$$ExternalSyntheticLambda1(arrayList));
        }
    }

    public boolean isTileAvailable(String str) {
        Iterator<DynamicTile> it = this.mDynamicTiles.iterator();
        while (it.hasNext()) {
            DynamicTile next = it.next();
            if (TextUtils.equals(str, next.getTileSpec())) {
                if (next.isTileAvailable()) {
                    return true;
                }
                if (!DEBUG) {
                    return false;
                }
                Log.d("DynamicTileManager", "DynamicTile not available: " + str);
                return false;
            }
        }
        return true;
    }

    public QSTileImpl createSystemTile(String str) {
        Iterator<DynamicTile> it = this.mDynamicTiles.iterator();
        while (it.hasNext()) {
            DynamicTile next = it.next();
            if (next.isSystemTile() && TextUtils.equals(str, next.getTileSpec())) {
                if (DEBUG) {
                    Log.d("DynamicTileManager", "Creating system tile. tileSpec: " + str);
                }
                return next.createSystemTile();
            }
        }
        return null;
    }

    public void updateDynamicTileIfNeeded() {
        Iterator<DynamicTile> it = this.mDynamicTiles.iterator();
        while (it.hasNext()) {
            it.next().updateTileIfNeeded();
        }
    }
}
