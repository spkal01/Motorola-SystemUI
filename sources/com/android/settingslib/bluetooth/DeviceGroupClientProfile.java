package com.android.settingslib.bluetooth;

import android.app.ActivityThread;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothDeviceGroup;
import android.bluetooth.BluetoothGroupCallback;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import java.util.UUID;

public class DeviceGroupClientProfile implements LocalBluetoothProfile {
    /* access modifiers changed from: private */
    public String mCallingPackage;
    /* access modifiers changed from: private */
    public final CachedBluetoothDeviceManager mDeviceManager;
    /* access modifiers changed from: private */
    public final BluetoothGroupCallback mGroupCallback = new BluetoothGroupCallback() {
        public void onNewGroupFound(int i, BluetoothDevice bluetoothDevice, UUID uuid) {
            Log.d("DeviceGroupClientProfile", "onNewGroupFound()");
            CachedBluetoothDevice findDevice = DeviceGroupClientProfile.this.mDeviceManager.findDevice(bluetoothDevice);
            if (findDevice == null) {
                findDevice = DeviceGroupClientProfile.this.mDeviceManager.addDevice(bluetoothDevice);
            }
            DeviceGroupClientProfile.this.mProfileManager.mEventManager.dispatchNewGroupFound(findDevice, i, uuid);
            Log.d("DeviceGroupClientProfile", "Start Group Discovery for Audio capable device");
            DeviceGroupClientProfile.this.mService.startGroupDiscovery(i);
        }

        public void onGroupDiscoveryStatusChanged(int i, int i2, int i3) {
            Log.d("DeviceGroupClientProfile", "onGroupDiscoveryStatusChanged()");
            DeviceGroupClientProfile.this.mProfileManager.mEventManager.dispatchGroupDiscoveryStatusChanged(i, i2, i3);
        }
    };
    /* access modifiers changed from: private */
    public boolean mIsProfileReady;
    /* access modifiers changed from: private */
    public final LocalBluetoothProfileManager mProfileManager;
    /* access modifiers changed from: private */
    public BluetoothDeviceGroup mService;

    public boolean accessProfileEnabled() {
        return false;
    }

    public int getConnectionStatus(BluetoothDevice bluetoothDevice) {
        return 0;
    }

    public int getDrawableResource(BluetoothClass bluetoothClass) {
        return 0;
    }

    public int getProfileId() {
        return 24;
    }

    public boolean setEnabled(BluetoothDevice bluetoothDevice, boolean z) {
        return false;
    }

    public String toString() {
        return "DeviceGroup Client";
    }

    DeviceGroupClientProfile(Context context, CachedBluetoothDeviceManager cachedBluetoothDeviceManager, LocalBluetoothProfileManager localBluetoothProfileManager) {
        this.mDeviceManager = cachedBluetoothDeviceManager;
        this.mProfileManager = localBluetoothProfileManager;
        this.mCallingPackage = ActivityThread.currentOpPackageName();
        BluetoothAdapter.getDefaultAdapter().getProfileProxy(context, new GroupClientServiceListener(), 24);
    }

    private final class GroupClientServiceListener implements BluetoothProfile.ServiceListener {
        private GroupClientServiceListener() {
        }

        public void onServiceConnected(int i, BluetoothProfile bluetoothProfile) {
            BluetoothDeviceGroup unused = DeviceGroupClientProfile.this.mService = (BluetoothDeviceGroup) bluetoothProfile;
            boolean unused2 = DeviceGroupClientProfile.this.mIsProfileReady = true;
            Log.d("DeviceGroupClientProfile", "onServiceConnected: mCallingPackage = " + DeviceGroupClientProfile.this.mCallingPackage);
            if ("com.android.settings".equals(DeviceGroupClientProfile.this.mCallingPackage)) {
                DeviceGroupClientProfile.this.mService.registerGroupClientApp(DeviceGroupClientProfile.this.mGroupCallback, new Handler(Looper.getMainLooper()));
            }
        }

        public void onServiceDisconnected(int i) {
            boolean unused = DeviceGroupClientProfile.this.mIsProfileReady = false;
        }
    }

    /* access modifiers changed from: protected */
    public void finalize() {
        Log.d("DeviceGroupClientProfile", "finalize()");
        if (this.mService != null) {
            try {
                BluetoothAdapter.getDefaultAdapter().closeProfileProxy(24, this.mService);
                this.mService = null;
            } catch (Throwable th) {
                Log.w("DeviceGroupClientProfile", "Error cleaning up BluetoothDeviceGroup proxy Object", th);
            }
        }
    }
}
