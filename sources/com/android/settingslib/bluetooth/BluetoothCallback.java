package com.android.settingslib.bluetooth;

import android.bluetooth.BluetoothCodecStatus;
import java.util.UUID;

public interface BluetoothCallback {
    void onA2dpCodecConfigChanged(CachedBluetoothDevice cachedBluetoothDevice, BluetoothCodecStatus bluetoothCodecStatus) {
    }

    void onAclConnectionStateChanged(CachedBluetoothDevice cachedBluetoothDevice, int i) {
    }

    void onActiveDeviceChanged(CachedBluetoothDevice cachedBluetoothDevice, int i) {
    }

    void onAudioModeChanged() {
    }

    void onBluetoothStateChanged(int i) {
    }

    void onBroadcastKeyGenerated() {
    }

    void onBroadcastStateChanged(int i) {
    }

    void onConnectionStateChanged(CachedBluetoothDevice cachedBluetoothDevice, int i) {
    }

    void onDeviceAdded(CachedBluetoothDevice cachedBluetoothDevice) {
    }

    void onDeviceBondStateChanged(CachedBluetoothDevice cachedBluetoothDevice, int i) {
    }

    void onDeviceDeleted(CachedBluetoothDevice cachedBluetoothDevice) {
    }

    void onGroupDiscoveryStatusChanged(int i, int i2, int i3) {
    }

    void onNewGroupFound(CachedBluetoothDevice cachedBluetoothDevice, int i, UUID uuid) {
    }

    void onProfileConnectionStateChanged(CachedBluetoothDevice cachedBluetoothDevice, int i, int i2) {
    }

    void onScanningStateChanged(boolean z) {
    }
}
