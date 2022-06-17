package com.android.systemui.p006qs.tiles;

import android.bluetooth.BluetoothClass;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import com.android.internal.logging.MetricsLogger;
import com.android.settingslib.Utils;
import com.android.settingslib.bluetooth.CachedBluetoothDevice;
import com.android.settingslib.graph.BluetoothDeviceLayerDrawable;
import com.android.systemui.R$drawable;
import com.android.systemui.R$plurals;
import com.android.systemui.R$string;
import com.android.systemui.moto.MotoFeature;
import com.android.systemui.p006qs.QSDetailItems;
import com.android.systemui.p006qs.QSHost;
import com.android.systemui.p006qs.logging.QSLogger;
import com.android.systemui.p006qs.tileimpl.QSTileImpl;
import com.android.systemui.plugins.ActivityStarter;
import com.android.systemui.plugins.FalsingManager;
import com.android.systemui.plugins.p005qs.DetailAdapter;
import com.android.systemui.plugins.p005qs.QSTile;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.statusbar.policy.BluetoothController;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/* renamed from: com.android.systemui.qs.tiles.BluetoothTile */
public class BluetoothTile extends QSTileImpl<QSTile.BooleanState> {
    /* access modifiers changed from: private */
    public static final Intent BLUETOOTH_SETTINGS = new Intent("android.settings.BLUETOOTH_SETTINGS");
    private final BluetoothController.Callback mCallback;
    /* access modifiers changed from: private */
    public final BluetoothController mController;
    /* access modifiers changed from: private */
    public final BluetoothDetailAdapter mDetailAdapter = ((BluetoothDetailAdapter) createDetailAdapter());

    public int getMetricsCategory() {
        return 113;
    }

    public BluetoothTile(QSHost qSHost, Looper looper, Handler handler, FalsingManager falsingManager, MetricsLogger metricsLogger, StatusBarStateController statusBarStateController, ActivityStarter activityStarter, QSLogger qSLogger, BluetoothController bluetoothController) {
        super(qSHost, looper, handler, falsingManager, metricsLogger, statusBarStateController, activityStarter, qSLogger);
        C12451 r1 = new BluetoothController.Callback() {
            public void onBluetoothStateChange(boolean z) {
                BluetoothTile.this.refreshState();
                if (BluetoothTile.this.isShowingDetail()) {
                    BluetoothTile.this.mDetailAdapter.updateItems();
                    BluetoothTile bluetoothTile = BluetoothTile.this;
                    bluetoothTile.fireToggleStateChanged(bluetoothTile.mDetailAdapter.getToggleState().booleanValue());
                }
            }

            public void onBluetoothDevicesChanged() {
                BluetoothTile.this.refreshState();
                if (BluetoothTile.this.isShowingDetail()) {
                    BluetoothTile.this.mDetailAdapter.updateItems();
                }
            }
        };
        this.mCallback = r1;
        this.mController = bluetoothController;
        bluetoothController.observe(getLifecycle(), r1);
    }

    public DetailAdapter getDetailAdapter() {
        return this.mDetailAdapter;
    }

    public QSTile.BooleanState newTileState() {
        return new QSTile.BooleanState();
    }

    /* access modifiers changed from: protected */
    public void handleClick(View view) {
        Object obj;
        boolean z = ((QSTile.BooleanState) this.mState).value;
        if (z) {
            obj = null;
        } else {
            obj = QSTileImpl.ARG_SHOW_TRANSIENT_ENABLING;
        }
        refreshState(obj);
        this.mController.setBluetoothEnabled(!z);
    }

    public Intent getLongClickIntent() {
        return new Intent("android.settings.BLUETOOTH_SETTINGS");
    }

    /* access modifiers changed from: protected */
    public void handleSecondaryClick(View view) {
        if (!this.mController.canConfigBluetooth()) {
            this.mActivityStarter.postStartActivityDismissingKeyguard(new Intent("android.settings.BLUETOOTH_SETTINGS"), 0);
            return;
        }
        showDetail(true);
        if (!((QSTile.BooleanState) this.mState).value) {
            this.mController.setBluetoothEnabled(true);
        }
    }

    public CharSequence getTileLabel() {
        return this.mContext.getString(R$string.quick_settings_bluetooth_label);
    }

    /* access modifiers changed from: protected */
    public void handleUpdateState(QSTile.BooleanState booleanState, Object obj) {
        boolean z = obj == QSTileImpl.ARG_SHOW_TRANSIENT_ENABLING;
        boolean z2 = z || this.mController.isBluetoothEnabled();
        boolean isBluetoothConnected = this.mController.isBluetoothConnected();
        boolean isBluetoothConnecting = this.mController.isBluetoothConnecting();
        booleanState.isTransient = z || isBluetoothConnecting || this.mController.getBluetoothState() == 11;
        booleanState.dualTarget = true;
        booleanState.value = z2;
        if (booleanState.slash == null) {
            booleanState.slash = new QSTile.SlashState();
        }
        booleanState.slash.isSlashed = !z2;
        Context context = this.mContext;
        int i = R$string.quick_settings_bluetooth_label;
        booleanState.label = context.getString(i);
        booleanState.secondaryLabel = TextUtils.emptyIfNull(getSecondaryLabel(z2, isBluetoothConnecting, isBluetoothConnected, booleanState.isTransient));
        booleanState.contentDescription = this.mContext.getString(R$string.accessibility_quick_settings_bluetooth);
        booleanState.stateDescription = "";
        if (z2) {
            if (isBluetoothConnected) {
                booleanState.icon = new BluetoothConnectedTileIcon();
                if (!TextUtils.isEmpty(this.mController.getConnectedDeviceName())) {
                    booleanState.label = this.mController.getConnectedDeviceName();
                    if (TextUtils.isEmpty(booleanState.secondaryLabel) && !booleanState.label.equals(this.mContext.getString(i))) {
                        booleanState.secondaryLabel = " ";
                    }
                }
                booleanState.stateDescription = this.mContext.getString(R$string.accessibility_bluetooth_name, new Object[]{booleanState.label}) + ", " + booleanState.secondaryLabel;
            } else if (booleanState.isTransient) {
                booleanState.icon = QSTileImpl.ResourceIcon.get(17303199);
                booleanState.stateDescription = booleanState.secondaryLabel;
            } else {
                booleanState.icon = QSTileImpl.ResourceIcon.get(17303696);
                booleanState.stateDescription = this.mContext.getString(R$string.accessibility_not_connected);
            }
            booleanState.state = 2;
        } else {
            booleanState.icon = QSTileImpl.ResourceIcon.get(17303696);
            booleanState.state = 1;
        }
        booleanState.dualLabelContentDescription = this.mContext.getResources().getString(R$string.accessibility_quick_settings_open_settings, new Object[]{getTileLabel()});
        booleanState.expandedAccessibilityClassName = Switch.class.getName();
    }

    private String getSecondaryLabel(boolean z, boolean z2, boolean z3, boolean z4) {
        if (z2) {
            return this.mContext.getString(R$string.quick_settings_connecting);
        }
        if (z4) {
            return this.mContext.getString(R$string.quick_settings_bluetooth_secondary_label_transient);
        }
        List<CachedBluetoothDevice> connectedDevices = this.mController.getConnectedDevices();
        if (!z || !z3 || connectedDevices.isEmpty()) {
            return null;
        }
        if (connectedDevices.size() > 1) {
            return this.mContext.getResources().getQuantityString(R$plurals.quick_settings_hotspot_secondary_label_num_devices, connectedDevices.size(), new Object[]{Integer.valueOf(connectedDevices.size())});
        }
        CachedBluetoothDevice cachedBluetoothDevice = connectedDevices.get(0);
        int batteryLevel = cachedBluetoothDevice.getBatteryLevel();
        if (batteryLevel > -1) {
            return this.mContext.getString(R$string.quick_settings_bluetooth_secondary_label_battery_level, new Object[]{Utils.formatPercentage(batteryLevel)});
        }
        BluetoothClass btClass = cachedBluetoothDevice.getBtClass();
        if (btClass == null) {
            return null;
        }
        if (cachedBluetoothDevice.isHearingAidDevice()) {
            return this.mContext.getString(R$string.quick_settings_bluetooth_secondary_label_hearing_aids);
        }
        if (btClass.doesClassMatch(1)) {
            return this.mContext.getString(R$string.quick_settings_bluetooth_secondary_label_audio);
        }
        if (btClass.doesClassMatch(0)) {
            return this.mContext.getString(R$string.quick_settings_bluetooth_secondary_label_headset);
        }
        if (btClass.doesClassMatch(3)) {
            return this.mContext.getString(R$string.quick_settings_bluetooth_secondary_label_input);
        }
        return null;
    }

    /* access modifiers changed from: protected */
    public String composeChangeAnnouncement() {
        if (((QSTile.BooleanState) this.mState).value) {
            return this.mContext.getString(R$string.accessibility_quick_settings_bluetooth_changed_on);
        }
        return this.mContext.getString(R$string.accessibility_quick_settings_bluetooth_changed_off);
    }

    public boolean isAvailable() {
        return this.mController.isBluetoothSupported();
    }

    /* access modifiers changed from: protected */
    public DetailAdapter createDetailAdapter() {
        return new BluetoothDetailAdapter();
    }

    /* renamed from: com.android.systemui.qs.tiles.BluetoothTile$BluetoothBatteryTileIcon */
    private class BluetoothBatteryTileIcon extends QSTile.Icon {
        private int mBatteryLevel;
        private float mIconScale;

        BluetoothBatteryTileIcon(int i, float f) {
            this.mBatteryLevel = i;
            this.mIconScale = f;
        }

        public Drawable getDrawable(Context context) {
            return BluetoothDeviceLayerDrawable.createLayerDrawable(context, R$drawable.ic_bluetooth_connected, this.mBatteryLevel, this.mIconScale);
        }
    }

    /* renamed from: com.android.systemui.qs.tiles.BluetoothTile$BluetoothConnectedTileIcon */
    private class BluetoothConnectedTileIcon extends QSTile.Icon {
        BluetoothConnectedTileIcon() {
        }

        public Drawable getDrawable(Context context) {
            return context.getDrawable(R$drawable.ic_bluetooth_connected);
        }
    }

    /* renamed from: com.android.systemui.qs.tiles.BluetoothTile$BluetoothDetailAdapter */
    protected class BluetoothDetailAdapter implements DetailAdapter, QSDetailItems.Callback {
        private QSDetailItems mItems;

        public int getMetricsCategory() {
            return 150;
        }

        protected BluetoothDetailAdapter() {
        }

        public CharSequence getTitle() {
            if (!MotoFeature.getInstance(BluetoothTile.this.mContext).isSupportCli() || !MotoFeature.isLidClosed(BluetoothTile.this.mContext)) {
                return BluetoothTile.this.mContext.getString(R$string.quick_settings_bluetooth_label);
            }
            return BluetoothTile.this.mContext.getString(R$string.cli_qs_detail_bluetooth_label);
        }

        public Boolean getToggleState() {
            return Boolean.valueOf(((QSTile.BooleanState) BluetoothTile.this.mState).value);
        }

        public boolean getToggleEnabled() {
            return BluetoothTile.this.mController.getBluetoothState() == 10 || BluetoothTile.this.mController.getBluetoothState() == 12;
        }

        public Intent getSettingsIntent() {
            return BluetoothTile.BLUETOOTH_SETTINGS;
        }

        public void setToggleState(boolean z) {
            MetricsLogger.action(BluetoothTile.this.mContext, 154, z);
            BluetoothTile.this.mController.setBluetoothEnabled(z);
        }

        public View createDetailView(Context context, View view, ViewGroup viewGroup) {
            QSDetailItems convertOrInflate = QSDetailItems.convertOrInflate(context, view, viewGroup);
            this.mItems = convertOrInflate;
            convertOrInflate.setTagSuffix("Bluetooth");
            this.mItems.setCallback(this);
            updateItems();
            setItemsVisible(((QSTile.BooleanState) BluetoothTile.this.mState).value);
            return this.mItems;
        }

        public void setItemsVisible(boolean z) {
            QSDetailItems qSDetailItems = this.mItems;
            if (qSDetailItems != null) {
                qSDetailItems.setItemsVisible(z);
            }
        }

        /* access modifiers changed from: private */
        public void updateItems() {
            if (this.mItems != null) {
                if (BluetoothTile.this.mController.isBluetoothEnabled()) {
                    this.mItems.setEmptyState(R$drawable.ic_qs_bluetooth_detail_empty, R$string.quick_settings_bluetooth_detail_empty_text);
                } else {
                    this.mItems.setEmptyState(R$drawable.ic_qs_bluetooth_detail_empty, R$string.bt_is_off);
                }
                ArrayList arrayList = new ArrayList();
                Collection<CachedBluetoothDevice> devices = BluetoothTile.this.mController.getDevices();
                if (devices != null) {
                    int i = 0;
                    int i2 = 0;
                    for (CachedBluetoothDevice next : devices) {
                        if (BluetoothTile.this.mController.getBondState(next) != 10) {
                            QSDetailItems.Item item = new QSDetailItems.Item();
                            item.iconResId = 17303696;
                            item.line1 = next.getName();
                            item.tag = next;
                            int maxConnectionState = next.getMaxConnectionState();
                            if (maxConnectionState == 2) {
                                item.iconResId = R$drawable.ic_bluetooth_connected;
                                int batteryLevel = next.getBatteryLevel();
                                if (batteryLevel > -1) {
                                    item.icon = new BluetoothBatteryTileIcon(batteryLevel, 1.0f);
                                    item.line2 = BluetoothTile.this.mContext.getString(R$string.quick_settings_connected_battery_level, new Object[]{Utils.formatPercentage(batteryLevel)});
                                } else {
                                    item.line2 = BluetoothTile.this.mContext.getString(R$string.quick_settings_connected);
                                }
                                item.canDisconnect = true;
                                arrayList.add(i, item);
                                i++;
                            } else if (maxConnectionState == 1) {
                                item.iconResId = R$drawable.ic_qs_bluetooth_connecting;
                                item.line2 = BluetoothTile.this.mContext.getString(R$string.quick_settings_connecting);
                                arrayList.add(i, item);
                            } else {
                                arrayList.add(item);
                            }
                            i2++;
                            if (i2 == 20) {
                                break;
                            }
                        }
                    }
                }
                this.mItems.setItems((QSDetailItems.Item[]) arrayList.toArray(new QSDetailItems.Item[arrayList.size()]));
            }
        }

        public void onDetailItemClick(QSDetailItems.Item item) {
            Object obj;
            CachedBluetoothDevice cachedBluetoothDevice;
            if (item != null && (obj = item.tag) != null && (cachedBluetoothDevice = (CachedBluetoothDevice) obj) != null && cachedBluetoothDevice.getMaxConnectionState() == 0) {
                BluetoothTile.this.mController.connect(cachedBluetoothDevice);
            }
        }

        public void onDetailItemDisconnect(QSDetailItems.Item item) {
            Object obj;
            CachedBluetoothDevice cachedBluetoothDevice;
            if (item != null && (obj = item.tag) != null && (cachedBluetoothDevice = (CachedBluetoothDevice) obj) != null) {
                BluetoothTile.this.mController.disconnect(cachedBluetoothDevice);
            }
        }
    }
}
