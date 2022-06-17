package com.android.systemui.p006qs.tileimpl;

import android.content.Context;
import android.os.Build;
import android.util.Log;
import com.android.systemui.moto.MotoFeature;
import com.android.systemui.p006qs.QSHost;
import com.android.systemui.p006qs.QSTileHost;
import com.android.systemui.p006qs.external.CustomTile;
import com.android.systemui.p006qs.tiles.AirplaneModeTile;
import com.android.systemui.p006qs.tiles.AlarmTile;
import com.android.systemui.p006qs.tiles.BatterySaverTile;
import com.android.systemui.p006qs.tiles.BluetoothTile;
import com.android.systemui.p006qs.tiles.CameraToggleTile;
import com.android.systemui.p006qs.tiles.CastTile;
import com.android.systemui.p006qs.tiles.CellularTile;
import com.android.systemui.p006qs.tiles.ColorInversionTile;
import com.android.systemui.p006qs.tiles.DataSaverTile;
import com.android.systemui.p006qs.tiles.DeviceControlsTile;
import com.android.systemui.p006qs.tiles.DndTile;
import com.android.systemui.p006qs.tiles.FlashlightTile;
import com.android.systemui.p006qs.tiles.HotspotTile;
import com.android.systemui.p006qs.tiles.InternetTile;
import com.android.systemui.p006qs.tiles.LocationTile;
import com.android.systemui.p006qs.tiles.MicrophoneToggleTile;
import com.android.systemui.p006qs.tiles.Moto5GTile;
import com.android.systemui.p006qs.tiles.NfcTile;
import com.android.systemui.p006qs.tiles.NightDisplayTile;
import com.android.systemui.p006qs.tiles.QuickAccessWalletTile;
import com.android.systemui.p006qs.tiles.ReduceBrightColorsTile;
import com.android.systemui.p006qs.tiles.RotationLockTile;
import com.android.systemui.p006qs.tiles.ScreenRecordTile;
import com.android.systemui.p006qs.tiles.ScreenshotTile;
import com.android.systemui.p006qs.tiles.UiModeNightTile;
import com.android.systemui.p006qs.tiles.UserTile;
import com.android.systemui.p006qs.tiles.WifiTile;
import com.android.systemui.p006qs.tiles.WorkModeTile;
import com.android.systemui.plugins.p005qs.QSFactory;
import com.android.systemui.plugins.p005qs.QSIconView;
import com.android.systemui.plugins.p005qs.QSTile;
import com.android.systemui.plugins.p005qs.QSTileView;
import com.android.systemui.util.leak.GarbageMonitor;
import com.motorola.systemui.p014qs.DynamicTileManager;
import dagger.Lazy;
import javax.inject.Provider;

/* renamed from: com.android.systemui.qs.tileimpl.QSFactoryImpl */
public class QSFactoryImpl implements QSFactory {
    private final Provider<AirplaneModeTile> mAirplaneModeTileProvider;
    private final Provider<AlarmTile> mAlarmTileProvider;
    private final Provider<BatterySaverTile> mBatterySaverTileProvider;
    private final Provider<BluetoothTile> mBluetoothTileProvider;
    private final Provider<CameraToggleTile> mCameraToggleTileProvider;
    private final Provider<CastTile> mCastTileProvider;
    private final Provider<CellularTile> mCellularTileProvider;
    private final Provider<ColorInversionTile> mColorInversionTileProvider;
    private final Provider<CustomTile.Builder> mCustomTileBuilderProvider;
    private final Provider<DataSaverTile> mDataSaverTileProvider;
    private final Provider<DeviceControlsTile> mDeviceControlsTileProvider;
    private final Provider<DndTile> mDndTileProvider;
    private final Provider<FlashlightTile> mFlashlightTileProvider;
    private final Provider<HotspotTile> mHotspotTileProvider;
    private final Provider<InternetTile> mInternetTileProvider;
    private final Provider<LocationTile> mLocationTileProvider;
    private final Provider<GarbageMonitor.MemoryTile> mMemoryTileProvider;
    private final Provider<MicrophoneToggleTile> mMicrophoneToggleTileProvider;
    private final Provider<Moto5GTile> mMoto5GTileProvider;
    private final Provider<NfcTile> mNfcTileProvider;
    private final Provider<NightDisplayTile> mNightDisplayTileProvider;
    private final Lazy<QSHost> mQsHostLazy;
    private final Provider<QuickAccessWalletTile> mQuickAccessWalletTileProvider;
    private final Provider<ReduceBrightColorsTile> mReduceBrightColorsTileProvider;
    private final Provider<RotationLockTile> mRotationLockTileProvider;
    private final Provider<ScreenRecordTile> mScreenRecordTileProvider;
    private final Provider<ScreenshotTile> mScreenshotTileProvider;
    private final Provider<UiModeNightTile> mUiModeNightTileProvider;
    private final Provider<UserTile> mUserTileProvider;
    private final Provider<WifiTile> mWifiTileProvider;
    private final Provider<WorkModeTile> mWorkModeTileProvider;

    public QSFactoryImpl(Lazy<QSHost> lazy, Provider<CustomTile.Builder> provider, Provider<WifiTile> provider2, Provider<InternetTile> provider3, Provider<BluetoothTile> provider4, Provider<CellularTile> provider5, Provider<DndTile> provider6, Provider<ColorInversionTile> provider7, Provider<AirplaneModeTile> provider8, Provider<WorkModeTile> provider9, Provider<RotationLockTile> provider10, Provider<FlashlightTile> provider11, Provider<LocationTile> provider12, Provider<CastTile> provider13, Provider<HotspotTile> provider14, Provider<UserTile> provider15, Provider<BatterySaverTile> provider16, Provider<DataSaverTile> provider17, Provider<NightDisplayTile> provider18, Provider<NfcTile> provider19, Provider<GarbageMonitor.MemoryTile> provider20, Provider<UiModeNightTile> provider21, Provider<ScreenRecordTile> provider22, Provider<ReduceBrightColorsTile> provider23, Provider<CameraToggleTile> provider24, Provider<MicrophoneToggleTile> provider25, Provider<DeviceControlsTile> provider26, Provider<AlarmTile> provider27, Provider<QuickAccessWalletTile> provider28, Provider<Moto5GTile> provider29, Provider<ScreenshotTile> provider30) {
        this.mQsHostLazy = lazy;
        this.mCustomTileBuilderProvider = provider;
        this.mWifiTileProvider = provider2;
        this.mInternetTileProvider = provider3;
        this.mBluetoothTileProvider = provider4;
        this.mCellularTileProvider = provider5;
        this.mDndTileProvider = provider6;
        this.mColorInversionTileProvider = provider7;
        this.mAirplaneModeTileProvider = provider8;
        this.mWorkModeTileProvider = provider9;
        this.mRotationLockTileProvider = provider10;
        this.mFlashlightTileProvider = provider11;
        this.mLocationTileProvider = provider12;
        this.mCastTileProvider = provider13;
        this.mHotspotTileProvider = provider14;
        this.mUserTileProvider = provider15;
        this.mBatterySaverTileProvider = provider16;
        this.mDataSaverTileProvider = provider17;
        this.mNightDisplayTileProvider = provider18;
        this.mNfcTileProvider = provider19;
        this.mMemoryTileProvider = provider20;
        this.mUiModeNightTileProvider = provider21;
        this.mScreenRecordTileProvider = provider22;
        this.mReduceBrightColorsTileProvider = provider23;
        this.mCameraToggleTileProvider = provider24;
        this.mMicrophoneToggleTileProvider = provider25;
        this.mDeviceControlsTileProvider = provider26;
        this.mAlarmTileProvider = provider27;
        this.mQuickAccessWalletTileProvider = provider28;
        this.mMoto5GTileProvider = provider29;
        this.mScreenshotTileProvider = provider30;
    }

    public QSTile createTile(String str) {
        QSTileImpl createTileInternal = createTileInternal(str);
        if (createTileInternal != null) {
            createTileInternal.initialize();
            createTileInternal.postStale();
        }
        return createTileInternal;
    }

    private QSTileImpl createTileInternal(String str) {
        str.hashCode();
        char c = 65535;
        switch (str.hashCode()) {
            case -2016941037:
                if (str.equals("inversion")) {
                    c = 0;
                    break;
                }
                break;
            case -1183073498:
                if (str.equals("flashlight")) {
                    c = 1;
                    break;
                }
                break;
            case -805491779:
                if (str.equals("screenrecord")) {
                    c = 2;
                    break;
                }
                break;
            case -795192327:
                if (str.equals("wallet")) {
                    c = 3;
                    break;
                }
                break;
            case -677011630:
                if (str.equals("airplane")) {
                    c = 4;
                    break;
                }
                break;
            case -566933834:
                if (str.equals("controls")) {
                    c = 5;
                    break;
                }
                break;
            case -416447130:
                if (str.equals("screenshot")) {
                    c = 6;
                    break;
                }
                break;
            case -343519030:
                if (str.equals("reduce_brightness")) {
                    c = 7;
                    break;
                }
                break;
            case -331239923:
                if (str.equals("battery")) {
                    c = 8;
                    break;
                }
                break;
            case -40300674:
                if (str.equals("rotation")) {
                    c = 9;
                    break;
                }
                break;
            case -37334949:
                if (str.equals("mictoggle")) {
                    c = 10;
                    break;
                }
                break;
            case 1714:
                if (str.equals("5G")) {
                    c = 11;
                    break;
                }
                break;
            case 3154:
                if (str.equals("bt")) {
                    c = 12;
                    break;
                }
                break;
            case 99610:
                if (str.equals("dnd")) {
                    c = 13;
                    break;
                }
                break;
            case 108971:
                if (str.equals("nfc")) {
                    c = 14;
                    break;
                }
                break;
            case 3046207:
                if (str.equals("cast")) {
                    c = 15;
                    break;
                }
                break;
            case 3049826:
                if (str.equals("cell")) {
                    c = 16;
                    break;
                }
                break;
            case 3075958:
                if (str.equals("dark")) {
                    c = 17;
                    break;
                }
                break;
            case 3599307:
                if (str.equals("user")) {
                    c = 18;
                    break;
                }
                break;
            case 3649301:
                if (str.equals("wifi")) {
                    c = 19;
                    break;
                }
                break;
            case 3655441:
                if (str.equals("work")) {
                    c = 20;
                    break;
                }
                break;
            case 6344377:
                if (str.equals("cameratoggle")) {
                    c = 21;
                    break;
                }
                break;
            case 92895825:
                if (str.equals("alarm")) {
                    c = 22;
                    break;
                }
                break;
            case 104817688:
                if (str.equals("night")) {
                    c = 23;
                    break;
                }
                break;
            case 109211285:
                if (str.equals("saver")) {
                    c = 24;
                    break;
                }
                break;
            case 570410817:
                if (str.equals("internet")) {
                    c = 25;
                    break;
                }
                break;
            case 1099603663:
                if (str.equals("hotspot")) {
                    c = 26;
                    break;
                }
                break;
            case 1901043637:
                if (str.equals("location")) {
                    c = 27;
                    break;
                }
                break;
        }
        switch (c) {
            case 0:
                return this.mColorInversionTileProvider.get();
            case 1:
                return this.mFlashlightTileProvider.get();
            case 2:
                return this.mScreenRecordTileProvider.get();
            case 3:
                return this.mQuickAccessWalletTileProvider.get();
            case 4:
                return this.mAirplaneModeTileProvider.get();
            case 5:
                return this.mDeviceControlsTileProvider.get();
            case 6:
                return this.mScreenshotTileProvider.get();
            case 7:
                return this.mReduceBrightColorsTileProvider.get();
            case 8:
                return this.mBatterySaverTileProvider.get();
            case 9:
                return this.mRotationLockTileProvider.get();
            case 10:
                return this.mMicrophoneToggleTileProvider.get();
            case 11:
                return this.mMoto5GTileProvider.get();
            case 12:
                return this.mBluetoothTileProvider.get();
            case 13:
                return this.mDndTileProvider.get();
            case 14:
                return this.mNfcTileProvider.get();
            case 15:
                return this.mCastTileProvider.get();
            case 16:
                return this.mCellularTileProvider.get();
            case 17:
                return this.mUiModeNightTileProvider.get();
            case 18:
                return this.mUserTileProvider.get();
            case 19:
                return this.mWifiTileProvider.get();
            case 20:
                return this.mWorkModeTileProvider.get();
            case 21:
                return this.mCameraToggleTileProvider.get();
            case 22:
                return this.mAlarmTileProvider.get();
            case 23:
                return this.mNightDisplayTileProvider.get();
            case 24:
                return this.mDataSaverTileProvider.get();
            case 25:
                return this.mInternetTileProvider.get();
            case 26:
                return this.mHotspotTileProvider.get();
            case 27:
                return this.mLocationTileProvider.get();
            default:
                QSTileImpl createSystemTile = DynamicTileManager.getInstance((QSTileHost) this.mQsHostLazy.get()).createSystemTile(str);
                if (createSystemTile != null) {
                    return createSystemTile;
                }
                if (str.startsWith("custom(")) {
                    return CustomTile.create(this.mCustomTileBuilderProvider.get(), str, this.mQsHostLazy.get().getUserContext());
                }
                if (Build.IS_DEBUGGABLE && str.equals("dbg:mem")) {
                    return this.mMemoryTileProvider.get();
                }
                Log.w("QSFactory", "No stock tile spec: " + str);
                return null;
        }
    }

    public QSTileView createTileView(Context context, QSTile qSTile, boolean z) {
        QSIconView createTileView = qSTile.createTileView(context);
        if (!MotoFeature.getInstance(context).isCustomPanelView()) {
            return new QSTileViewImpl(context, createTileView, z);
        }
        if (z) {
            return new QSTileViewPrcFixedImpl(context, createTileView, z);
        }
        return new QSTileViewPrcImpl(context, createTileView, z);
    }
}
