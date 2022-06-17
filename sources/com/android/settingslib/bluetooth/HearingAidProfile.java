package com.android.settingslib.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothHearingAid;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.util.Log;
import java.util.ArrayList;
import java.util.List;

public class HearingAidProfile implements LocalBluetoothProfile {
    /* access modifiers changed from: private */

    /* renamed from: V */
    public static boolean f59V = true;
    private final BluetoothAdapter mBluetoothAdapter;
    private Context mContext;
    /* access modifiers changed from: private */
    public final CachedBluetoothDeviceManager mDeviceManager;
    /* access modifiers changed from: private */
    public boolean mIsProfileReady;
    /* access modifiers changed from: private */
    public final LocalBluetoothProfileManager mProfileManager;
    /* access modifiers changed from: private */
    public BluetoothHearingAid mService;

    public boolean accessProfileEnabled() {
        return false;
    }

    public int getDrawableResource(BluetoothClass bluetoothClass) {
        return 17303203;
    }

    public int getProfileId() {
        return 21;
    }

    public String toString() {
        return "HearingAid";
    }

    private final class HearingAidServiceListener implements BluetoothProfile.ServiceListener {
        private HearingAidServiceListener() {
        }

        public void onServiceConnected(int i, BluetoothProfile bluetoothProfile) {
            BluetoothHearingAid unused = HearingAidProfile.this.mService = (BluetoothHearingAid) bluetoothProfile;
            List<BluetoothDevice> connectedDevices = HearingAidProfile.this.mService.getConnectedDevices();
            while (!connectedDevices.isEmpty()) {
                BluetoothDevice remove = connectedDevices.remove(0);
                CachedBluetoothDevice findDevice = HearingAidProfile.this.mDeviceManager.findDevice(remove);
                if (findDevice == null) {
                    if (HearingAidProfile.f59V) {
                        Log.d("HearingAidProfile", "HearingAidProfile found new device: " + remove);
                    }
                    findDevice = HearingAidProfile.this.mDeviceManager.addDevice(remove);
                }
                findDevice.onProfileStateChanged(HearingAidProfile.this, 2);
                findDevice.refresh();
            }
            HearingAidProfile.this.mDeviceManager.updateHearingAidsDevices();
            boolean unused2 = HearingAidProfile.this.mIsProfileReady = true;
            HearingAidProfile.this.mProfileManager.callServiceConnectedListeners();
        }

        public void onServiceDisconnected(int i) {
            boolean unused = HearingAidProfile.this.mIsProfileReady = false;
        }
    }

    HearingAidProfile(Context context, CachedBluetoothDeviceManager cachedBluetoothDeviceManager, LocalBluetoothProfileManager localBluetoothProfileManager) {
        this.mContext = context;
        this.mDeviceManager = cachedBluetoothDeviceManager;
        this.mProfileManager = localBluetoothProfileManager;
        BluetoothAdapter defaultAdapter = BluetoothAdapter.getDefaultAdapter();
        this.mBluetoothAdapter = defaultAdapter;
        defaultAdapter.getProfileProxy(context, new HearingAidServiceListener(), 21);
    }

    public int getConnectionStatus(BluetoothDevice bluetoothDevice) {
        BluetoothHearingAid bluetoothHearingAid = this.mService;
        if (bluetoothHearingAid == null) {
            return 0;
        }
        return bluetoothHearingAid.getConnectionState(bluetoothDevice);
    }

    public List<BluetoothDevice> getActiveDevices() {
        BluetoothHearingAid bluetoothHearingAid = this.mService;
        if (bluetoothHearingAid == null) {
            return new ArrayList();
        }
        return bluetoothHearingAid.getActiveDevices();
    }

    public boolean setEnabled(BluetoothDevice bluetoothDevice, boolean z) {
        BluetoothHearingAid bluetoothHearingAid = this.mService;
        if (bluetoothHearingAid == null || bluetoothDevice == null) {
            return false;
        }
        if (!z) {
            return bluetoothHearingAid.setConnectionPolicy(bluetoothDevice, 0);
        }
        if (bluetoothHearingAid.getConnectionPolicy(bluetoothDevice) < 100) {
            return this.mService.setConnectionPolicy(bluetoothDevice, 100);
        }
        return false;
    }

    public long getHiSyncId(BluetoothDevice bluetoothDevice) {
        BluetoothHearingAid bluetoothHearingAid = this.mService;
        if (bluetoothHearingAid == null || bluetoothDevice == null) {
            return 0;
        }
        return bluetoothHearingAid.getHiSyncId(bluetoothDevice);
    }

    /* access modifiers changed from: protected */
    public void finalize() {
        Log.d("HearingAidProfile", "finalize()");
        if (this.mService != null) {
            try {
                BluetoothAdapter.getDefaultAdapter().closeProfileProxy(21, this.mService);
                this.mService = null;
            } catch (Throwable th) {
                Log.w("HearingAidProfile", "Error cleaning up Hearing Aid proxy", th);
            }
        }
    }
}
