package com.android.settingslib.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothDun;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.util.Log;

public final class DunServerProfile implements LocalBluetoothProfile {
    /* access modifiers changed from: private */

    /* renamed from: V */
    public static boolean f58V = true;
    /* access modifiers changed from: private */
    public boolean mIsProfileReady;
    /* access modifiers changed from: private */
    public BluetoothDun mService;

    public boolean accessProfileEnabled() {
        return true;
    }

    public int getDrawableResource(BluetoothClass bluetoothClass) {
        return 17303206;
    }

    public int getProfileId() {
        return 23;
    }

    public boolean setEnabled(BluetoothDevice bluetoothDevice, boolean z) {
        return true;
    }

    public String toString() {
        return "DUN Server";
    }

    private final class DunServiceListener implements BluetoothProfile.ServiceListener {
        private DunServiceListener() {
        }

        public void onServiceConnected(int i, BluetoothProfile bluetoothProfile) {
            if (DunServerProfile.f58V) {
                Log.d("DunServerProfile", "Bluetooth service connected");
            }
            BluetoothDun unused = DunServerProfile.this.mService = (BluetoothDun) bluetoothProfile;
            boolean unused2 = DunServerProfile.this.mIsProfileReady = true;
        }

        public void onServiceDisconnected(int i) {
            if (DunServerProfile.f58V) {
                Log.d("DunServerProfile", "Bluetooth service disconnected");
            }
            boolean unused = DunServerProfile.this.mIsProfileReady = false;
        }
    }

    DunServerProfile(Context context) {
        BluetoothAdapter.getDefaultAdapter().getProfileProxy(context, new DunServiceListener(), 23);
    }

    public int getConnectionStatus(BluetoothDevice bluetoothDevice) {
        BluetoothDun bluetoothDun = this.mService;
        if (bluetoothDun == null) {
            return 0;
        }
        return bluetoothDun.getConnectionState(bluetoothDevice);
    }

    /* access modifiers changed from: protected */
    public void finalize() {
        if (f58V) {
            Log.d("DunServerProfile", "finalize()");
        }
        if (this.mService != null) {
            try {
                BluetoothAdapter.getDefaultAdapter().closeProfileProxy(23, this.mService);
                this.mService = null;
            } catch (Throwable th) {
                Log.w("DunServerProfile", "Error cleaning up DUN proxy", th);
            }
        }
    }
}
