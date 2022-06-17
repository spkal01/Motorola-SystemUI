package com.android.wifitrackerlib;

import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiNetworkScoreCache;
import android.os.Handler;
import com.android.internal.annotations.VisibleForTesting;
import com.android.wifitrackerlib.StandardWifiEntry;
import com.android.wifitrackerlib.WifiEntry;

@VisibleForTesting
public class NetworkRequestEntry extends StandardWifiEntry {
    public boolean canSetAutoJoinEnabled() {
        return false;
    }

    public boolean canSetMeteredChoice() {
        return false;
    }

    public void connect(WifiEntry.ConnectCallback connectCallback) {
    }

    public int getMeteredChoice() {
        return 0;
    }

    public WifiConfiguration getWifiConfiguration() {
        return null;
    }

    public boolean isAutoJoinEnabled() {
        return true;
    }

    public boolean isMetered() {
        return false;
    }

    public boolean isSaved() {
        return false;
    }

    public boolean isSubscription() {
        return false;
    }

    public boolean isSuggestion() {
        return false;
    }

    NetworkRequestEntry(Context context, Handler handler, StandardWifiEntry.StandardWifiEntryKey standardWifiEntryKey, WifiManager wifiManager, WifiNetworkScoreCache wifiNetworkScoreCache, boolean z) throws IllegalArgumentException {
        super(context, handler, standardWifiEntryKey, wifiManager, wifiNetworkScoreCache, z);
    }
}
