package com.android.systemui.statusbar.phone;

import android.content.Context;
import android.content.res.Resources;
import android.hardware.display.ColorDisplayManager;
import android.hardware.display.NightDisplayListener;
import android.os.Handler;
import android.os.UserHandle;
import android.util.Log;
import com.android.internal.annotations.VisibleForTesting;
import com.android.systemui.R$array;
import com.android.systemui.p006qs.AutoAddTracker;
import com.android.systemui.p006qs.QSTileHost;
import com.android.systemui.p006qs.ReduceBrightColorsController;
import com.android.systemui.p006qs.SecureSetting;
import com.android.systemui.p006qs.external.CustomTile;
import com.android.systemui.statusbar.phone.AudioFxController;
import com.android.systemui.statusbar.phone.ManagedProfileController;
import com.android.systemui.statusbar.policy.CastController;
import com.android.systemui.statusbar.policy.DataSaverController;
import com.android.systemui.statusbar.policy.DeviceControlsController;
import com.android.systemui.statusbar.policy.HotspotController;
import com.android.systemui.statusbar.policy.WalletController;
import com.android.systemui.util.UserAwareController;
import com.android.systemui.util.settings.SecureSettings;
import com.motorola.systemui.p014qs.DynamicTileManager;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public class AutoTileManager implements UserAwareController {
    /* access modifiers changed from: private */
    public final AudioFxController.Callback mAudioFxCallback = new AudioFxController.Callback() {
        public void onAudioFxChanged() {
            if (!AutoTileManager.this.mAutoTracker.isAdded("custom(com.motorola.audiofx/com.motorola.motoaudio.tileservice.AudioEffectTileService)") && AutoTileManager.this.mAudioFxController.isAudioFxEnabled()) {
                AutoTileManager.this.mHost.addTile("custom(com.motorola.audiofx/com.motorola.motoaudio.tileservice.AudioEffectTileService)");
                AutoTileManager.this.mAutoTracker.setTileAdded("custom(com.motorola.audiofx/com.motorola.motoaudio.tileservice.AudioEffectTileService)");
                AutoTileManager.this.mHandler.post(new AutoTileManager$1$$ExternalSyntheticLambda0(this));
            }
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$onAudioFxChanged$0() {
            AutoTileManager.this.mAudioFxController.removeCallback(AutoTileManager.this.mAudioFxCallback);
        }
    };
    /* access modifiers changed from: private */
    public final AudioFxController mAudioFxController;
    private final ArrayList<AutoAddSetting> mAutoAddSettingList = new ArrayList<>();
    protected final AutoAddTracker mAutoTracker;
    @VisibleForTesting
    final CastController.Callback mCastCallback = new CastController.Callback() {
        public void onCastDevicesChanged() {
            if (!AutoTileManager.this.mAutoTracker.isAdded("cast")) {
                boolean z = false;
                Iterator<CastController.CastDevice> it = AutoTileManager.this.mCastController.getCastDevices().iterator();
                while (true) {
                    if (it.hasNext()) {
                        int i = it.next().state;
                        if (i != 2) {
                            if (i == 1) {
                                break;
                            }
                        } else {
                            break;
                        }
                    } else {
                        break;
                    }
                }
                z = true;
                if (z) {
                    AutoTileManager.this.mHost.addTile("cast");
                    AutoTileManager.this.mAutoTracker.setTileAdded("cast");
                    AutoTileManager.this.mHandler.post(new AutoTileManager$8$$ExternalSyntheticLambda0(this));
                }
            }
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$onCastDevicesChanged$0() {
            AutoTileManager.this.mCastController.removeCallback(AutoTileManager.this.mCastCallback);
        }
    };
    /* access modifiers changed from: private */
    public final CastController mCastController;
    protected final Context mContext;
    private UserHandle mCurrentUser;
    /* access modifiers changed from: private */
    public final DataSaverController mDataSaverController;
    /* access modifiers changed from: private */
    public final DataSaverController.Listener mDataSaverListener = new DataSaverController.Listener() {
        public void onDataSaverChanged(boolean z) {
            if (!AutoTileManager.this.mAutoTracker.isAdded("saver") && z) {
                AutoTileManager.this.mHost.addTile("saver");
                AutoTileManager.this.mAutoTracker.setTileAdded("saver");
                AutoTileManager.this.mHandler.post(new AutoTileManager$3$$ExternalSyntheticLambda0(this));
            }
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$onDataSaverChanged$0() {
            AutoTileManager.this.mDataSaverController.removeCallback(AutoTileManager.this.mDataSaverListener);
        }
    };
    private final DeviceControlsController.Callback mDeviceControlsCallback = new DeviceControlsController.Callback() {
        public void onControlsUpdate(Integer num) {
            if (!AutoTileManager.this.mAutoTracker.isAdded("controls")) {
                if (num != null) {
                    AutoTileManager.this.mHost.addTile("controls", num.intValue());
                }
                AutoTileManager.this.mAutoTracker.setTileAdded("controls");
                AutoTileManager.this.mHandler.post(new AutoTileManager$5$$ExternalSyntheticLambda0(this));
            }
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$onControlsUpdate$0() {
            AutoTileManager.this.mDeviceControlsController.removeCallback();
        }
    };
    /* access modifiers changed from: private */
    public final DeviceControlsController mDeviceControlsController;
    protected final Handler mHandler;
    protected final QSTileHost mHost;
    /* access modifiers changed from: private */
    public final HotspotController.Callback mHotspotCallback = new HotspotController.Callback() {
        public void onHotspotChanged(boolean z, int i) {
            if (!AutoTileManager.this.mAutoTracker.isAdded("hotspot") && z) {
                AutoTileManager.this.mHost.addTile("hotspot");
                AutoTileManager.this.mAutoTracker.setTileAdded("hotspot");
                AutoTileManager.this.mHandler.post(new AutoTileManager$4$$ExternalSyntheticLambda0(this));
            }
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$onHotspotChanged$0() {
            AutoTileManager.this.mHotspotController.removeCallback(AutoTileManager.this.mHotspotCallback);
        }
    };
    /* access modifiers changed from: private */
    public final HotspotController mHotspotController;
    private boolean mInitialized;
    private final boolean mIsReduceBrightColorsAvailable;
    /* access modifiers changed from: private */
    public final ManagedProfileController mManagedProfileController;
    @VisibleForTesting
    final NightDisplayListener.Callback mNightDisplayCallback = new NightDisplayListener.Callback() {
        public void onActivated(boolean z) {
            if (z) {
                addNightTile();
            }
        }

        public void onAutoModeChanged(int i) {
            if (i == 1 || i == 2) {
                addNightTile();
            }
        }

        private void addNightTile() {
            if (!AutoTileManager.this.mAutoTracker.isAdded("night")) {
                AutoTileManager.this.mHost.addTile("night");
                AutoTileManager.this.mAutoTracker.setTileAdded("night");
                AutoTileManager.this.mHandler.post(new AutoTileManager$6$$ExternalSyntheticLambda0(this));
            }
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$addNightTile$0() {
            AutoTileManager.this.mNightDisplayListener.setCallback((NightDisplayListener.Callback) null);
        }
    };
    /* access modifiers changed from: private */
    public NightDisplayListener mNightDisplayListener;
    private final ManagedProfileController.Callback mProfileCallback = new ManagedProfileController.Callback() {
        public void onManagedProfileRemoved() {
        }

        public void onManagedProfileChanged() {
            if (!AutoTileManager.this.mAutoTracker.isAdded("work") && AutoTileManager.this.mManagedProfileController.hasActiveProfile()) {
                AutoTileManager.this.mHost.addTile("work");
                AutoTileManager.this.mAutoTracker.setTileAdded("work");
            }
        }
    };
    @VisibleForTesting
    final ReduceBrightColorsController.Listener mReduceBrightColorsCallback = new ReduceBrightColorsController.Listener() {
        public void onActivated(boolean z) {
            if (z) {
                addReduceBrightColorsTile();
            }
        }

        private void addReduceBrightColorsTile() {
            if (!AutoTileManager.this.mAutoTracker.isAdded("reduce_brightness")) {
                AutoTileManager.this.mHost.addTile("reduce_brightness");
                AutoTileManager.this.mAutoTracker.setTileAdded("reduce_brightness");
                AutoTileManager.this.mHandler.post(new AutoTileManager$7$$ExternalSyntheticLambda0(this));
            }
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$addReduceBrightColorsTile$0() {
            AutoTileManager.this.mReduceBrightColorsController.removeCallback((ReduceBrightColorsController.Listener) this);
        }
    };
    /* access modifiers changed from: private */
    public final ReduceBrightColorsController mReduceBrightColorsController;
    protected final SecureSettings mSecureSettings;
    private final WalletController mWalletController;

    public AutoTileManager(Context context, AutoAddTracker.Builder builder, QSTileHost qSTileHost, Handler handler, SecureSettings secureSettings, HotspotController hotspotController, DataSaverController dataSaverController, ManagedProfileController managedProfileController, NightDisplayListener nightDisplayListener, CastController castController, ReduceBrightColorsController reduceBrightColorsController, DeviceControlsController deviceControlsController, WalletController walletController, boolean z, AudioFxController audioFxController) {
        this.mContext = context;
        this.mHost = qSTileHost;
        this.mSecureSettings = secureSettings;
        UserHandle user = qSTileHost.getUserContext().getUser();
        this.mCurrentUser = user;
        AutoAddTracker.Builder builder2 = builder;
        this.mAutoTracker = builder.setUserId(user.getIdentifier()).build();
        this.mHandler = handler;
        this.mHotspotController = hotspotController;
        this.mDataSaverController = dataSaverController;
        this.mManagedProfileController = managedProfileController;
        this.mNightDisplayListener = nightDisplayListener;
        this.mCastController = castController;
        this.mReduceBrightColorsController = reduceBrightColorsController;
        this.mIsReduceBrightColorsAvailable = z;
        this.mDeviceControlsController = deviceControlsController;
        this.mWalletController = walletController;
        this.mAudioFxController = audioFxController;
    }

    public void init() {
        if (this.mInitialized) {
            Log.w("AutoTileManager", "Trying to re-initialize");
            return;
        }
        this.mAutoTracker.initialize();
        populateSettingsList();
        startControllersAndSettingsListeners();
        this.mInitialized = true;
    }

    /* access modifiers changed from: protected */
    public void startControllersAndSettingsListeners() {
        if (!this.mAutoTracker.isAdded("hotspot")) {
            this.mHotspotController.addCallback(this.mHotspotCallback);
        }
        if (!this.mAutoTracker.isAdded("saver")) {
            this.mDataSaverController.addCallback(this.mDataSaverListener);
        }
        if (!this.mAutoTracker.isAdded("work")) {
            this.mManagedProfileController.addCallback(this.mProfileCallback);
        }
        if (!this.mAutoTracker.isAdded("night") && ColorDisplayManager.isNightDisplayAvailable(this.mContext)) {
            this.mNightDisplayListener.setCallback(this.mNightDisplayCallback);
        }
        if (!this.mAutoTracker.isAdded("cast")) {
            this.mCastController.addCallback(this.mCastCallback);
        }
        if (!this.mAutoTracker.isAdded("reduce_brightness") && this.mIsReduceBrightColorsAvailable) {
            this.mReduceBrightColorsController.addCallback(this.mReduceBrightColorsCallback);
        }
        if (!this.mAutoTracker.isAdded("controls")) {
            this.mDeviceControlsController.setCallback(this.mDeviceControlsCallback);
        }
        if (!this.mAutoTracker.isAdded("wallet")) {
            initWalletController();
        }
        if (!this.mAutoTracker.isAdded("custom(com.motorola.audiofx/com.motorola.motoaudio.tileservice.AudioEffectTileService)") && this.mAudioFxController.isAudioFxAvailable()) {
            this.mAudioFxController.addCallback(this.mAudioFxCallback);
        }
        int size = this.mAutoAddSettingList.size();
        for (int i = 0; i < size; i++) {
            if (!this.mAutoTracker.isAdded(this.mAutoAddSettingList.get(i).mSpec)) {
                this.mAutoAddSettingList.get(i).setListening(true);
            }
        }
        DynamicTileManager.getInstance(this.mHost).loadTiles();
    }

    /* access modifiers changed from: protected */
    public void stopListening() {
        this.mHotspotController.removeCallback(this.mHotspotCallback);
        this.mDataSaverController.removeCallback(this.mDataSaverListener);
        this.mManagedProfileController.removeCallback(this.mProfileCallback);
        if (ColorDisplayManager.isNightDisplayAvailable(this.mContext)) {
            this.mNightDisplayListener.setCallback((NightDisplayListener.Callback) null);
        }
        if (this.mIsReduceBrightColorsAvailable) {
            this.mReduceBrightColorsController.removeCallback(this.mReduceBrightColorsCallback);
        }
        this.mCastController.removeCallback(this.mCastCallback);
        this.mDeviceControlsController.removeCallback();
        this.mAudioFxController.removeCallback(this.mAudioFxCallback);
        int size = this.mAutoAddSettingList.size();
        for (int i = 0; i < size; i++) {
            this.mAutoAddSettingList.get(i).setListening(false);
        }
    }

    private void populateSettingsList() {
        try {
            for (String str : this.mContext.getResources().getStringArray(R$array.config_quickSettingsAutoAdd)) {
                String[] split = str.split(":");
                if (split.length == 2) {
                    this.mAutoAddSettingList.add(new AutoAddSetting(this.mSecureSettings, this.mHandler, split[0], this.mCurrentUser.getIdentifier(), split[1]));
                } else {
                    Log.w("AutoTileManager", "Malformed item in array: " + str);
                }
            }
        } catch (Resources.NotFoundException unused) {
            Log.w("AutoTileManager", "Missing config resource");
        }
    }

    /* renamed from: changeUser */
    public void lambda$changeUser$0(UserHandle userHandle) {
        if (!this.mInitialized) {
            throw new IllegalStateException("AutoTileManager not initialized");
        } else if (!Thread.currentThread().equals(this.mHandler.getLooper().getThread())) {
            this.mHandler.post(new AutoTileManager$$ExternalSyntheticLambda0(this, userHandle));
        } else if (userHandle.getIdentifier() != this.mCurrentUser.getIdentifier()) {
            stopListening();
            this.mCurrentUser = userHandle;
            this.mNightDisplayListener = new NightDisplayListener(this.mContext);
            int size = this.mAutoAddSettingList.size();
            for (int i = 0; i < size; i++) {
                this.mAutoAddSettingList.get(i).setUserId(userHandle.getIdentifier());
            }
            this.mAutoTracker.changeUser(userHandle);
            startControllersAndSettingsListeners();
        }
    }

    public void unmarkTileAsAutoAdded(String str) {
        this.mAutoTracker.setTileRemoved(str);
    }

    private void initWalletController() {
        Integer walletPosition;
        if (!this.mAutoTracker.isAdded("wallet") && (walletPosition = this.mWalletController.getWalletPosition()) != null) {
            this.mHost.addTile("wallet", walletPosition.intValue());
            this.mAutoTracker.setTileAdded("wallet");
        }
    }

    /* access modifiers changed from: protected */
    @VisibleForTesting
    public SecureSetting getSecureSettingForKey(String str) {
        Iterator<AutoAddSetting> it = this.mAutoAddSettingList.iterator();
        while (it.hasNext()) {
            SecureSetting next = it.next();
            if (Objects.equals(str, next.getKey())) {
                return next;
            }
        }
        return null;
    }

    private class AutoAddSetting extends SecureSetting {
        /* access modifiers changed from: private */
        public final String mSpec;

        AutoAddSetting(SecureSettings secureSettings, Handler handler, String str, int i, String str2) {
            super(secureSettings, handler, str, i);
            this.mSpec = str2;
        }

        /* access modifiers changed from: protected */
        public void handleValueChanged(int i, boolean z) {
            if (AutoTileManager.this.mAutoTracker.isAdded(this.mSpec)) {
                AutoTileManager.this.mHandler.post(new AutoTileManager$AutoAddSetting$$ExternalSyntheticLambda0(this));
            } else if (i != 0) {
                if (this.mSpec.startsWith("custom(")) {
                    AutoTileManager.this.mHost.addTile(CustomTile.getComponentFromSpec(this.mSpec), true);
                } else {
                    AutoTileManager.this.mHost.addTile(this.mSpec);
                }
                AutoTileManager.this.mAutoTracker.setTileAdded(this.mSpec);
                AutoTileManager.this.mHandler.post(new AutoTileManager$AutoAddSetting$$ExternalSyntheticLambda1(this));
            }
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$handleValueChanged$0() {
            setListening(false);
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$handleValueChanged$1() {
            setListening(false);
        }
    }

    public void resetAutoTiles(List<String> list) {
        this.mAutoTracker.setTileRemoved("hotspot");
        this.mAutoTracker.setTileRemoved("saver");
        this.mAutoTracker.setTileRemoved("inversion");
        this.mAutoTracker.setTileRemoved("work");
        this.mAutoTracker.setTileRemoved("night");
        this.mAutoTracker.setTileRemoved("custom(com.motorola.audiofx/com.motorola.motoaudio.tileservice.AudioEffectTileService)");
        this.mHotspotController.addCallback(this.mHotspotCallback);
        this.mDataSaverController.addCallback(this.mDataSaverListener);
        this.mManagedProfileController.addCallback(this.mProfileCallback);
        this.mCastController.addCallback(this.mCastCallback);
        if (ColorDisplayManager.isNightDisplayAvailable(this.mContext)) {
            this.mNightDisplayListener.setCallback(this.mNightDisplayCallback);
        }
        if (this.mAudioFxController.isAudioFxAvailable()) {
            this.mAudioFxController.addCallback(this.mAudioFxCallback);
        }
        list.addAll(this.mAutoTracker.getAdded());
    }
}
