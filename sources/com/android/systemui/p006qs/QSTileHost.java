package com.android.systemui.p006qs;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.UserHandle;
import android.os.UserManager;
import android.provider.Settings;
import android.service.quicksettings.Tile;
import android.text.TextUtils;
import android.util.ArraySet;
import android.util.Log;
import com.android.internal.logging.InstanceId;
import com.android.internal.logging.InstanceIdSequence;
import com.android.internal.logging.UiEventLogger;
import com.android.systemui.Dependency;
import com.android.systemui.Dumpable;
import com.android.systemui.Prefs;
import com.android.systemui.R$string;
import com.android.systemui.broadcast.BroadcastDispatcher;
import com.android.systemui.dump.DumpManager;
import com.android.systemui.moto.MotoFeature;
import com.android.systemui.p006qs.QSHost;
import com.android.systemui.p006qs.external.CustomTile;
import com.android.systemui.p006qs.external.CustomTileStatePersister;
import com.android.systemui.p006qs.external.TileLifecycleManager;
import com.android.systemui.p006qs.external.TileServiceKey;
import com.android.systemui.p006qs.external.TileServices;
import com.android.systemui.p006qs.logging.QSLogger;
import com.android.systemui.plugins.PluginListener;
import com.android.systemui.plugins.p005qs.QSFactory;
import com.android.systemui.plugins.p005qs.QSTile;
import com.android.systemui.plugins.p005qs.QSTileView;
import com.android.systemui.settings.UserTracker;
import com.android.systemui.shared.plugins.PluginManager;
import com.android.systemui.statusbar.phone.AutoTileManager;
import com.android.systemui.statusbar.phone.StatusBar;
import com.android.systemui.statusbar.phone.StatusBarIconController;
import com.android.systemui.statusbar.policy.ConfigurationController;
import com.android.systemui.tuner.TunerService;
import com.android.systemui.util.settings.SecureSettings;
import com.motorola.systemui.p014qs.DynamicTileManager;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import javax.inject.Provider;

/* renamed from: com.android.systemui.qs.QSTileHost */
public class QSTileHost implements QSHost, TunerService.Tunable, PluginListener<QSFactory>, Dumpable {
    /* access modifiers changed from: private */
    public static final boolean DEBUG = (!Build.IS_USER);
    private AutoTileManager mAutoTiles;
    private final BroadcastDispatcher mBroadcastDispatcher;
    private final List<QSHost.Callback> mCallbacks = new ArrayList();
    private ConfigurationController.ConfigurationListener mConfigurationListener;
    /* access modifiers changed from: private */
    public final Context mContext;
    private int mCurrentUser;
    private final CustomTileStatePersister mCustomTileStatePersister;
    private final DumpManager mDumpManager;
    private final StatusBarIconController mIconController;
    private final InstanceIdSequence mInstanceIdSequence;
    private final PluginManager mPluginManager;
    private final QSLogger mQSLogger;
    private final ArrayList<QSFactory> mQsFactories;
    private SecureSettings mSecureSettings;
    private final TileServices mServices;
    private final Optional<StatusBar> mStatusBarOptional;
    protected final ArrayList<String> mTileSpecs = new ArrayList<>();
    private final LinkedHashMap<String, QSTile> mTiles = new LinkedHashMap<>();
    private final TunerService mTunerService;
    private final UiEventLogger mUiEventLogger;
    private Context mUserContext;
    private UserTracker mUserTracker;

    public void warn(String str, Throwable th) {
    }

    public QSTileHost(Context context, StatusBarIconController statusBarIconController, QSFactory qSFactory, Handler handler, Looper looper, PluginManager pluginManager, TunerService tunerService, Provider<AutoTileManager> provider, DumpManager dumpManager, BroadcastDispatcher broadcastDispatcher, Optional<StatusBar> optional, QSLogger qSLogger, UiEventLogger uiEventLogger, UserTracker userTracker, SecureSettings secureSettings, CustomTileStatePersister customTileStatePersister) {
        Context context2 = context;
        PluginManager pluginManager2 = pluginManager;
        TunerService tunerService2 = tunerService;
        DumpManager dumpManager2 = dumpManager;
        BroadcastDispatcher broadcastDispatcher2 = broadcastDispatcher;
        UserTracker userTracker2 = userTracker;
        ArrayList<QSFactory> arrayList = new ArrayList<>();
        this.mQsFactories = arrayList;
        this.mConfigurationListener = new ConfigurationController.ConfigurationListener() {
            public void onOverlayChanged() {
                if (!Prefs.getBoolean(QSTileHost.this.mContext, "QsTileCustomizerChanged", false)) {
                    QSTileHost.this.reloadTilesByOverlay();
                } else if (QSTileHost.DEBUG) {
                    Log.i("QSTileHost", "QS_TILE_CUSTOMIZER_CHANGED had changed by user");
                }
            }
        };
        this.mIconController = statusBarIconController;
        this.mContext = context2;
        this.mUserContext = context2;
        this.mTunerService = tunerService2;
        this.mPluginManager = pluginManager2;
        this.mDumpManager = dumpManager2;
        this.mQSLogger = qSLogger;
        this.mUiEventLogger = uiEventLogger;
        this.mBroadcastDispatcher = broadcastDispatcher2;
        this.mInstanceIdSequence = new InstanceIdSequence(1048576);
        Looper looper2 = looper;
        this.mServices = new TileServices(this, looper, broadcastDispatcher2, userTracker2);
        this.mStatusBarOptional = optional;
        QSFactory qSFactory2 = qSFactory;
        arrayList.add(qSFactory);
        pluginManager2.addPluginListener(this, (Class<?>) QSFactory.class, true);
        dumpManager2.registerDumpable("QSTileHost", this);
        this.mUserTracker = userTracker2;
        this.mSecureSettings = secureSettings;
        this.mCustomTileStatePersister = customTileStatePersister;
        ((ConfigurationController) Dependency.get(ConfigurationController.class)).addCallback(this.mConfigurationListener);
        if (isOTAUpgrade()) {
            MotoFeature.getInstance(context);
            if (!MotoFeature.isProductWaveAtleastRefWave("2021.2")) {
                Prefs.putBoolean(context, "QsTileCustomizerChanged", true);
            }
        }
        Handler handler2 = handler;
        handler.post(new QSTileHost$$ExternalSyntheticLambda0(this, tunerService2, provider));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0(TunerService tunerService, Provider provider) {
        DynamicTileManager.getInstance(this).updateDynamicTileIfNeeded();
        tunerService.addTunable(this, "sysui_qs_tiles");
        this.mAutoTiles = (AutoTileManager) provider.get();
    }

    public void reloadTilesByOverlay() {
        List<String> motoSpecs = getMotoSpecs(this.mContext);
        resetAutoTiles(motoSpecs);
        DynamicTileManager.getInstance(this).reloadTiles(motoSpecs);
        if (DEBUG) {
            Log.i("QSTileHost", "reloadTilesByOverlay tiles " + motoSpecs.toString());
        }
        saveTilesToSettings(motoSpecs);
    }

    public boolean isOTAUpgrade() {
        boolean isDeviceUpgrading = this.mContext.getPackageManager().isDeviceUpgrading();
        if (DEBUG) {
            Log.i("QSTileHost", "isOTAUpgrade = " + isDeviceUpgrading);
        }
        return isDeviceUpgrading;
    }

    public StatusBarIconController getIconController() {
        return this.mIconController;
    }

    public InstanceId getNewInstanceId() {
        return this.mInstanceIdSequence.newInstanceId();
    }

    public void onPluginConnected(QSFactory qSFactory, Context context) {
        this.mQsFactories.add(0, qSFactory);
        String value = this.mTunerService.getValue("sysui_qs_tiles");
        onTuningChanged("sysui_qs_tiles", "");
        onTuningChanged("sysui_qs_tiles", value);
    }

    public void onPluginDisconnected(QSFactory qSFactory) {
        this.mQsFactories.remove(qSFactory);
        String value = this.mTunerService.getValue("sysui_qs_tiles");
        onTuningChanged("sysui_qs_tiles", "");
        onTuningChanged("sysui_qs_tiles", value);
    }

    public UiEventLogger getUiEventLogger() {
        return this.mUiEventLogger;
    }

    public void addCallback(QSHost.Callback callback) {
        this.mCallbacks.add(callback);
    }

    public void removeCallback(QSHost.Callback callback) {
        this.mCallbacks.remove(callback);
    }

    public Collection<QSTile> getTiles() {
        return this.mTiles.values();
    }

    public void collapsePanels() {
        this.mStatusBarOptional.ifPresent(QSTileHost$$ExternalSyntheticLambda3.INSTANCE);
    }

    public void forceCollapsePanels() {
        this.mStatusBarOptional.ifPresent(QSTileHost$$ExternalSyntheticLambda4.INSTANCE);
    }

    public void openPanels() {
        this.mStatusBarOptional.ifPresent(QSTileHost$$ExternalSyntheticLambda5.INSTANCE);
    }

    public Context getContext() {
        return this.mContext;
    }

    public Context getUserContext() {
        return this.mUserContext;
    }

    public int getUserId() {
        return this.mCurrentUser;
    }

    public TileServices getTileServices() {
        return this.mServices;
    }

    public int indexOf(String str) {
        return this.mTileSpecs.indexOf(str);
    }

    public void onTuningChanged(String str, String str2) {
        boolean z;
        if ("sysui_qs_tiles".equals(str)) {
            Log.d("QSTileHost", "Recreating tiles");
            if (str2 == null && UserManager.isDeviceInDemoMode(this.mContext)) {
                str2 = this.mContext.getResources().getString(R$string.quick_settings_tiles_retail_mode);
            }
            List<String> loadTileSpecs = loadTileSpecs(this.mContext, str2);
            int userId = this.mUserTracker.getUserId();
            if (userId != this.mCurrentUser) {
                this.mUserContext = this.mUserTracker.getUserContext();
                AutoTileManager autoTileManager = this.mAutoTiles;
                if (autoTileManager != null) {
                    autoTileManager.lambda$changeUser$0(UserHandle.of(userId));
                }
            }
            if (!loadTileSpecs.equals(this.mTileSpecs) || userId != this.mCurrentUser) {
                this.mTiles.entrySet().stream().filter(new QSTileHost$$ExternalSyntheticLambda8(loadTileSpecs)).forEach(new QSTileHost$$ExternalSyntheticLambda1(this));
                LinkedHashMap linkedHashMap = new LinkedHashMap();
                for (String next : loadTileSpecs) {
                    QSTile qSTile = this.mTiles.get(next);
                    if (qSTile == null || (z && ((CustomTile) qSTile).getUser() != userId)) {
                        if (qSTile != null) {
                            qSTile.destroy();
                            Log.d("QSTileHost", "Destroying tile for wrong user: " + next);
                            this.mQSLogger.logTileDestroyed(next, "Tile for wrong user");
                        }
                        Log.d("QSTileHost", "Creating tile: " + next);
                        try {
                            QSTile createTile = createTile(next);
                            if (createTile != null) {
                                createTile.setTileSpec(next);
                                if (createTile.isAvailable()) {
                                    linkedHashMap.put(next, createTile);
                                    this.mQSLogger.logTileAdded(next);
                                } else {
                                    createTile.destroy();
                                    Log.d("QSTileHost", "Destroying not available tile: " + next);
                                    this.mQSLogger.logTileDestroyed(next, "Tile not available");
                                    removeTile(next);
                                }
                            }
                        } catch (Throwable th) {
                            Log.w("QSTileHost", "Error creating tile for spec: " + next, th);
                        }
                    } else if (qSTile.isAvailable()) {
                        if (DEBUG) {
                            Log.d("QSTileHost", "Adding " + qSTile);
                        }
                        qSTile.removeCallbacks();
                        if (!((z = qSTile instanceof CustomTile)) && this.mCurrentUser != userId) {
                            qSTile.userSwitch(userId);
                        }
                        linkedHashMap.put(next, qSTile);
                        this.mQSLogger.logTileAdded(next);
                    } else {
                        qSTile.destroy();
                        Log.d("QSTileHost", "Destroying not available tile: " + next);
                        this.mQSLogger.logTileDestroyed(next, "Tile not available");
                    }
                }
                this.mCurrentUser = userId;
                ArrayList arrayList = new ArrayList(this.mTileSpecs);
                this.mTileSpecs.clear();
                this.mTileSpecs.addAll(loadTileSpecs);
                this.mTiles.clear();
                this.mTiles.putAll(linkedHashMap);
                if (!linkedHashMap.isEmpty() || loadTileSpecs.isEmpty()) {
                    for (int i = 0; i < this.mCallbacks.size(); i++) {
                        this.mCallbacks.get(i).onTilesChanged();
                    }
                    return;
                }
                Log.d("QSTileHost", "No valid tiles on tuning changed. Setting to default.");
                changeTiles(arrayList, loadTileSpecs(this.mContext, ""));
            }
        }
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$onTuningChanged$2(List list, Map.Entry entry) {
        return !list.contains(entry.getKey());
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onTuningChanged$3(Map.Entry entry) {
        Log.d("QSTileHost", "Destroying tile: " + ((String) entry.getKey()));
        this.mQSLogger.logTileDestroyed((String) entry.getKey(), "Tile removed");
        ((QSTile) entry.getValue()).destroy();
    }

    public void removeTile(String str) {
        changeTileSpecs(new QSTileHost$$ExternalSyntheticLambda6(str));
    }

    public void unmarkTileAsAutoAdded(String str) {
        AutoTileManager autoTileManager = this.mAutoTiles;
        if (autoTileManager != null) {
            autoTileManager.unmarkTileAsAutoAdded(str);
        }
    }

    public void replaceTile(String str, String str2) {
        if (DEBUG) {
            Log.d("QSTileHost", "replaceTile spec " + str + " newSpec " + str2);
        }
        List<String> loadTileSpecs = loadTileSpecs(this.mContext, Settings.Secure.getStringForUser(this.mContext.getContentResolver(), "sysui_qs_tiles", ActivityManager.getCurrentUser()));
        if (loadTileSpecs.contains(str)) {
            int indexOf = loadTileSpecs.indexOf(str);
            loadTileSpecs.remove(str);
            loadTileSpecs.add(indexOf, str2);
            Settings.Secure.putStringForUser(this.mContext.getContentResolver(), "sysui_qs_tiles", TextUtils.join(",", loadTileSpecs), ActivityManager.getCurrentUser());
            return;
        }
        Log.d("QSTileHost", "setting tileSpecs doesn't contain spec: " + str);
    }

    public void addTile(String str) {
        addTile(str, -1);
    }

    public void addTile(String str, int i) {
        changeTileSpecs(new QSTileHost$$ExternalSyntheticLambda7(str, i));
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$addTile$5(String str, int i, List list) {
        if (list.contains(str)) {
            return false;
        }
        int size = list.size();
        if (i == -1 || i >= size) {
            list.add(str);
            return true;
        }
        list.add(i, str);
        return true;
    }

    /* access modifiers changed from: package-private */
    public void saveTilesToSettings(List<String> list) {
        this.mSecureSettings.putStringForUser("sysui_qs_tiles", TextUtils.join(",", list), (String) null, false, this.mCurrentUser, true);
    }

    private void changeTileSpecs(Predicate<List<String>> predicate) {
        List<String> loadTileSpecs = loadTileSpecs(this.mContext, this.mSecureSettings.getStringForUser("sysui_qs_tiles", this.mCurrentUser));
        if (predicate.test(loadTileSpecs)) {
            saveTilesToSettings(loadTileSpecs);
        }
    }

    public void addTile(ComponentName componentName) {
        addTile(componentName, false);
    }

    public void addTile(ComponentName componentName, boolean z) {
        String spec = CustomTile.toSpec(componentName);
        if (!this.mTileSpecs.contains(spec)) {
            ArrayList arrayList = new ArrayList(this.mTileSpecs);
            if (z) {
                arrayList.add(spec);
            } else {
                arrayList.add(0, spec);
            }
            changeTiles(this.mTileSpecs, arrayList);
        }
    }

    public void removeTile(ComponentName componentName) {
        ArrayList arrayList = new ArrayList(this.mTileSpecs);
        arrayList.remove(CustomTile.toSpec(componentName));
        changeTiles(this.mTileSpecs, arrayList);
    }

    public void changeTiles(List<String> list, List<String> list2) {
        ArrayList arrayList = new ArrayList(list);
        int size = arrayList.size();
        for (int i = 0; i < size; i++) {
            String str = (String) arrayList.get(i);
            if (str.startsWith("custom(") && !list2.contains(str)) {
                ComponentName componentFromSpec = CustomTile.getComponentFromSpec(str);
                TileLifecycleManager tileLifecycleManager = new TileLifecycleManager(new Handler(), this.mContext, this.mServices, new Tile(), new Intent().setComponent(componentFromSpec), new UserHandle(this.mCurrentUser), this.mBroadcastDispatcher);
                tileLifecycleManager.onStopListening();
                tileLifecycleManager.onTileRemoved();
                this.mCustomTileStatePersister.removeState(new TileServiceKey(componentFromSpec, this.mCurrentUser));
                TileLifecycleManager.setTileAdded(this.mContext, componentFromSpec, false);
                tileLifecycleManager.flushMessagesAndUnbind();
            }
        }
        if (DEBUG) {
            Log.d("QSTileHost", "saveCurrentTiles " + list2);
        }
        saveTilesToSettings(list2);
    }

    public QSTile createTile(String str) {
        for (int i = 0; i < this.mQsFactories.size(); i++) {
            QSTile createTile = this.mQsFactories.get(i).createTile(str);
            if (createTile != null) {
                return createTile;
            }
        }
        return null;
    }

    public QSTileView createTileView(Context context, QSTile qSTile, boolean z) {
        for (int i = 0; i < this.mQsFactories.size(); i++) {
            QSTileView createTileView = this.mQsFactories.get(i).createTileView(context, qSTile, z);
            if (createTileView != null) {
                return createTileView;
            }
        }
        throw new RuntimeException("Default factory didn't create view for " + qSTile.getTileSpec());
    }

    protected static List<String> loadTileSpecs(Context context, String str) {
        Resources resources = context.getResources();
        if (TextUtils.isEmpty(str)) {
            str = resources.getString(R$string.quick_settings_tiles);
            if (DEBUG) {
                Log.d("QSTileHost", "Loaded tile specs from config: " + str);
            }
        } else if (DEBUG) {
            Log.d("QSTileHost", "Loaded tile specs from setting: " + str);
        }
        ArrayList arrayList = new ArrayList();
        changeDefaultStringToDefaultSpecs(context, arrayList, str);
        if (DEBUG) {
            Log.d("QSTileHost", "tiles: " + arrayList.toString());
        }
        return arrayList;
    }

    public static void changeDefaultStringToDefaultSpecs(Context context, ArrayList<String> arrayList, String str) {
        ArraySet arraySet = new ArraySet();
        boolean z = false;
        for (String trim : str.split(",")) {
            String trim2 = trim.trim();
            if (!trim2.isEmpty()) {
                if (trim2.equals("default")) {
                    if (!z) {
                        for (String next : getDefaultSpecs(context)) {
                            if (!arraySet.contains(next)) {
                                arrayList.add(next);
                                arraySet.add(next);
                            }
                        }
                        z = true;
                    }
                } else if (!arraySet.contains(trim2)) {
                    arrayList.add(trim2);
                    arraySet.add(trim2);
                }
            }
        }
        if (MotoFeature.getInstance(context).isCustomPanelView()) {
            if (arrayList.contains("internet")) {
                arrayList.remove("internet");
            }
            List<String> prcFixedTiles = getPrcFixedTiles(context);
            for (int i = 0; i < prcFixedTiles.size(); i++) {
                String str2 = prcFixedTiles.get(i);
                if (arrayList.contains(str2)) {
                    arrayList.remove(str2);
                }
                arrayList.add(i, str2);
            }
        }
    }

    public static List<String> getPrcFixedTiles(Context context) {
        ArrayList arrayList = new ArrayList();
        arrayList.addAll(Arrays.asList(context.getResources().getString(R$string.zz_moto_prc_fixed_tiles).split(",")));
        return arrayList;
    }

    public static List<String> getMotoSpecs(Context context) {
        ArrayList arrayList = new ArrayList();
        changeDefaultStringToDefaultSpecs(context, arrayList, context.getResources().getString(R$string.quick_settings_tiles));
        return arrayList;
    }

    public static List<String> getDefaultSpecs(Context context) {
        ArrayList arrayList = new ArrayList();
        arrayList.addAll(Arrays.asList(context.getResources().getString(R$string.quick_settings_tiles_default).split(",")));
        if (Build.IS_DEBUGGABLE) {
            arrayList.add("dbg:mem");
        }
        return arrayList;
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        printWriter.println("QSTileHost:");
        this.mTiles.values().stream().filter(QSTileHost$$ExternalSyntheticLambda9.INSTANCE).forEach(new QSTileHost$$ExternalSyntheticLambda2(fileDescriptor, printWriter, strArr));
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$dump$6(QSTile qSTile) {
        return qSTile instanceof Dumpable;
    }

    public void resetAutoTiles(List<String> list) {
        AutoTileManager autoTileManager = this.mAutoTiles;
        if (autoTileManager != null) {
            autoTileManager.resetAutoTiles(list);
        }
    }
}
