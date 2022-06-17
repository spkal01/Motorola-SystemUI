package com.android.settingslib.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.BluetoothVcp;
import android.content.Context;
import android.util.Log;

public class VcpProfile implements LocalBluetoothProfile {
    private final BluetoothAdapter mBluetoothAdapter;
    private Context mContext;
    private final CachedBluetoothDeviceManager mDeviceManager;
    /* access modifiers changed from: private */
    public boolean mIsProfileReady;
    /* access modifiers changed from: private */
    public final LocalBluetoothProfileManager mProfileManager;
    /* access modifiers changed from: private */
    public BluetoothVcp mService;

    public boolean accessProfileEnabled() {
        return false;
    }

    public int getDrawableResource(BluetoothClass bluetoothClass) {
        return 0;
    }

    public int getProfileId() {
        return 26;
    }

    public boolean setEnabled(BluetoothDevice bluetoothDevice, boolean z) {
        return false;
    }

    public String toString() {
        return "VCP";
    }

    private final class VcpServiceListener implements BluetoothProfile.ServiceListener {
        private VcpServiceListener() {
        }

        public void onServiceConnected(int i, BluetoothProfile bluetoothProfile) {
            BluetoothVcp unused = VcpProfile.this.mService = (BluetoothVcp) bluetoothProfile;
            Log.w("VcpProfile", "Bluetooth service Connected");
            boolean unused2 = VcpProfile.this.mIsProfileReady = true;
            VcpProfile.this.mProfileManager.callServiceConnectedListeners();
        }

        public void onServiceDisconnected(int i) {
            Log.w("VcpProfile", "Bluetooth service Disconnected");
            boolean unused = VcpProfile.this.mIsProfileReady = false;
        }
    }

    VcpProfile(Context context, CachedBluetoothDeviceManager cachedBluetoothDeviceManager, LocalBluetoothProfileManager localBluetoothProfileManager) {
        this.mContext = context;
        this.mDeviceManager = cachedBluetoothDeviceManager;
        this.mProfileManager = localBluetoothProfileManager;
        BluetoothAdapter defaultAdapter = BluetoothAdapter.getDefaultAdapter();
        this.mBluetoothAdapter = defaultAdapter;
        defaultAdapter.getProfileProxy(context, new VcpServiceListener(), 26);
    }

    public int getConnectionStatus(BluetoothDevice bluetoothDevice) {
        BluetoothVcp bluetoothVcp = this.mService;
        if (bluetoothVcp == null) {
            return 0;
        }
        return bluetoothVcp.getConnectionState(bluetoothDevice);
    }

    /* access modifiers changed from: protected */
    public void finalize() {
        Log.d("VcpProfile", "finalize()");
        if (this.mService != null) {
            try {
                BluetoothAdapter.getDefaultAdapter().closeProfileProxy(26, this.mService);
                this.mService = null;
            } catch (Throwable th) {
                Log.w("VcpProfile", "Error cleaning up Vcp proxy", th);
            }
        }
    }
}
