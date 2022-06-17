package com.android.wifitrackerlib;

import android.content.Intent;
import android.net.LinkProperties;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import androidx.core.util.Preconditions;

public abstract class NetworkDetailsTracker extends BaseWifiTracker {
    protected NetworkInfo mCurrentNetworkInfo;

    public abstract WifiEntry getWifiEntry();

    /* access modifiers changed from: protected */
    public void handleNetworkStateChangedAction(Intent intent) {
        Preconditions.checkNotNull(intent, "Intent cannot be null!");
        this.mCurrentNetworkInfo = (NetworkInfo) intent.getParcelableExtra("networkInfo");
        getWifiEntry().updateConnectionInfo(this.mWifiManager.getConnectionInfo(), this.mCurrentNetworkInfo);
    }

    /* access modifiers changed from: protected */
    public void handleRssiChangedAction() {
        getWifiEntry().updateConnectionInfo(this.mWifiManager.getConnectionInfo(), this.mCurrentNetworkInfo);
    }

    /* access modifiers changed from: protected */
    public void handleLinkPropertiesChanged(LinkProperties linkProperties) {
        WifiEntry wifiEntry = getWifiEntry();
        if (wifiEntry.getConnectedState() == 2) {
            wifiEntry.updateLinkProperties(linkProperties);
        }
    }

    /* access modifiers changed from: protected */
    public void handleNetworkCapabilitiesChanged(NetworkCapabilities networkCapabilities) {
        WifiEntry wifiEntry = getWifiEntry();
        if (wifiEntry.getConnectedState() == 2) {
            wifiEntry.updateNetworkCapabilities(networkCapabilities);
            wifiEntry.setIsLowQuality(this.mIsWifiValidated && this.mIsCellDefaultRoute);
        }
    }

    /* access modifiers changed from: protected */
    public void handleDefaultRouteChanged() {
        WifiEntry wifiEntry = getWifiEntry();
        if (wifiEntry.getConnectedState() == 2) {
            wifiEntry.setIsDefaultNetwork(this.mIsWifiDefaultRoute);
            wifiEntry.setIsLowQuality(this.mIsWifiValidated && this.mIsCellDefaultRoute);
        }
    }
}
