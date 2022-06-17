package com.android.settingslib.wifi;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkKey;
import android.net.NetworkRequest;
import android.net.NetworkScoreManager;
import android.net.ScoredNetwork;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiNetworkScoreCache;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import com.android.settingslib.R$string;
import com.android.settingslib.Utils;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class WifiStatusTracker {
    /* access modifiers changed from: private */
    public static final SimpleDateFormat SSDF = new SimpleDateFormat("MM-dd HH:mm:ss.SSS");
    public boolean connected;
    public boolean enabled;
    public boolean isCaptivePortal;
    public boolean isCarrierMerged;
    public boolean isDefaultNetwork;
    public boolean isStandard11ax;
    public int level;
    private final WifiNetworkScoreCache.CacheListener mCacheListener;
    /* access modifiers changed from: private */
    public final Runnable mCallback;
    private final ConnectivityManager mConnectivityManager;
    private final Context mContext;
    /* access modifiers changed from: private */
    public Network mDefaultNetwork;
    private final ConnectivityManager.NetworkCallback mDefaultNetworkCallback;
    /* access modifiers changed from: private */
    public NetworkCapabilities mDefaultNetworkCapabilities;
    private final Handler mHandler;
    private final String[] mHistory = new String[32];
    private int mHistoryIndex;
    private final Object mLock = new Object();
    private final ConnectivityManager.NetworkCallback mNetworkCallback;
    private final NetworkRequest mNetworkRequest;
    private final NetworkScoreManager mNetworkScoreManager;
    /* access modifiers changed from: private */
    public final Set<Integer> mNetworks = new HashSet();
    private final boolean mSupportMergedUi;
    private WifiInfo mWifiInfo;
    private final WifiManager mWifiManager;
    private final WifiNetworkScoreCache mWifiNetworkScoreCache;
    public int rssi;
    public String ssid;
    public int state;
    public String statusLabel;
    public int subId;

    public WifiStatusTracker(Context context, WifiManager wifiManager, NetworkScoreManager networkScoreManager, ConnectivityManager connectivityManager, Runnable runnable) {
        Handler handler = new Handler(Looper.getMainLooper());
        this.mHandler = handler;
        this.mCacheListener = new WifiNetworkScoreCache.CacheListener(handler) {
            public void networkCacheUpdated(List<ScoredNetwork> list) {
                WifiStatusTracker.this.updateStatusLabel();
                WifiStatusTracker.this.mCallback.run();
            }
        };
        this.mNetworkRequest = new NetworkRequest.Builder().clearCapabilities().addCapability(15).addTransportType(1).addTransportType(0).build();
        this.mNetworkCallback = new ConnectivityManager.NetworkCallback(1) {
            public void onCapabilitiesChanged(Network network, NetworkCapabilities networkCapabilities) {
                WifiInfo wifiInfo;
                boolean z = false;
                boolean z2 = true;
                if (networkCapabilities.hasTransport(0)) {
                    wifiInfo = Utils.tryGetWifiInfoForVcn(networkCapabilities);
                    if (wifiInfo == null) {
                        z2 = false;
                    }
                    boolean z3 = z2;
                    z2 = false;
                    z = z3;
                } else if (networkCapabilities.hasTransport(1)) {
                    wifiInfo = (WifiInfo) networkCapabilities.getTransportInfo();
                } else {
                    wifiInfo = null;
                    z2 = false;
                }
                if (z || z2) {
                    WifiStatusTracker.this.recordLastWifiNetwork(WifiStatusTracker.SSDF.format(Long.valueOf(System.currentTimeMillis())) + "," + "onCapabilitiesChanged: " + "network=" + network + "," + "networkCapabilities=" + networkCapabilities);
                }
                if (wifiInfo != null && wifiInfo.isPrimary()) {
                    if (!WifiStatusTracker.this.mNetworks.contains(Integer.valueOf(network.getNetId()))) {
                        WifiStatusTracker.this.mNetworks.add(Integer.valueOf(network.getNetId()));
                    }
                    WifiStatusTracker.this.updateWifiInfo(wifiInfo);
                    WifiStatusTracker.this.updateStatusLabel();
                    WifiStatusTracker.this.mCallback.run();
                } else if (WifiStatusTracker.this.mNetworks.contains(Integer.valueOf(network.getNetId()))) {
                    WifiStatusTracker.this.mNetworks.remove(Integer.valueOf(network.getNetId()));
                }
            }

            public void onLost(Network network) {
                WifiStatusTracker.this.recordLastWifiNetwork(WifiStatusTracker.SSDF.format(Long.valueOf(System.currentTimeMillis())) + "," + "onLost: " + "network=" + network);
                if (WifiStatusTracker.this.mNetworks.contains(Integer.valueOf(network.getNetId()))) {
                    WifiStatusTracker.this.mNetworks.remove(Integer.valueOf(network.getNetId()));
                    WifiStatusTracker.this.updateWifiInfo((WifiInfo) null);
                    WifiStatusTracker.this.updateStatusLabel();
                    WifiStatusTracker.this.mCallback.run();
                }
            }
        };
        this.mDefaultNetworkCallback = new ConnectivityManager.NetworkCallback(1) {
            public void onCapabilitiesChanged(Network network, NetworkCapabilities networkCapabilities) {
                Network unused = WifiStatusTracker.this.mDefaultNetwork = network;
                NetworkCapabilities unused2 = WifiStatusTracker.this.mDefaultNetworkCapabilities = networkCapabilities;
                WifiStatusTracker.this.updateStatusLabel();
                WifiStatusTracker.this.mCallback.run();
            }

            public void onLost(Network network) {
                Network unused = WifiStatusTracker.this.mDefaultNetwork = null;
                NetworkCapabilities unused2 = WifiStatusTracker.this.mDefaultNetworkCapabilities = null;
                WifiStatusTracker.this.updateStatusLabel();
                WifiStatusTracker.this.mCallback.run();
            }
        };
        this.mDefaultNetwork = null;
        this.mDefaultNetworkCapabilities = null;
        this.mContext = context;
        this.mWifiManager = wifiManager;
        this.mWifiNetworkScoreCache = new WifiNetworkScoreCache(context);
        this.mNetworkScoreManager = networkScoreManager;
        this.mConnectivityManager = connectivityManager;
        this.mCallback = runnable;
        this.mSupportMergedUi = false;
    }

    public void setListening(boolean z) {
        if (z) {
            this.mNetworkScoreManager.registerNetworkScoreCache(1, this.mWifiNetworkScoreCache, 1);
            this.mWifiNetworkScoreCache.registerListener(this.mCacheListener);
            this.mConnectivityManager.registerNetworkCallback(this.mNetworkRequest, this.mNetworkCallback, this.mHandler);
            this.mConnectivityManager.registerDefaultNetworkCallback(this.mDefaultNetworkCallback, this.mHandler);
            return;
        }
        this.mNetworkScoreManager.unregisterNetworkScoreCache(1, this.mWifiNetworkScoreCache);
        this.mWifiNetworkScoreCache.unregisterListener();
        this.mConnectivityManager.unregisterNetworkCallback(this.mNetworkCallback);
        this.mConnectivityManager.unregisterNetworkCallback(this.mDefaultNetworkCallback);
    }

    /* JADX WARNING: Removed duplicated region for block: B:25:0x0056  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void fetchInitialState() {
        /*
            r4 = this;
            android.net.wifi.WifiManager r0 = r4.mWifiManager
            if (r0 != 0) goto L_0x0005
            return
        L_0x0005:
            java.lang.Object r0 = r4.mLock
            monitor-enter(r0)
            r4.updateWifiState()     // Catch:{ all -> 0x007f }
            android.net.ConnectivityManager r1 = r4.mConnectivityManager     // Catch:{ all -> 0x007f }
            r2 = 1
            android.net.NetworkInfo r1 = r1.getNetworkInfo(r2)     // Catch:{ all -> 0x007f }
            r3 = 0
            if (r1 == 0) goto L_0x001c
            boolean r1 = r1.isConnected()     // Catch:{ all -> 0x007f }
            if (r1 == 0) goto L_0x001c
            goto L_0x001d
        L_0x001c:
            r2 = r3
        L_0x001d:
            r4.connected = r2     // Catch:{ all -> 0x007f }
            r1 = 0
            r4.mWifiInfo = r1     // Catch:{ all -> 0x007f }
            r4.ssid = r1     // Catch:{ all -> 0x007f }
            r4.isStandard11ax = r3     // Catch:{ all -> 0x007f }
            if (r2 == 0) goto L_0x007a
            android.net.wifi.WifiManager r1 = r4.mWifiManager     // Catch:{ all -> 0x007f }
            android.net.wifi.WifiInfo r1 = r1.getConnectionInfo()     // Catch:{ all -> 0x007f }
            r4.mWifiInfo = r1     // Catch:{ all -> 0x007f }
            if (r1 == 0) goto L_0x007a
            boolean r1 = r1.isPasspointAp()     // Catch:{ all -> 0x007f }
            if (r1 != 0) goto L_0x004a
            android.net.wifi.WifiInfo r1 = r4.mWifiInfo     // Catch:{ all -> 0x007f }
            boolean r1 = r1.isOsuAp()     // Catch:{ all -> 0x007f }
            if (r1 == 0) goto L_0x0041
            goto L_0x004a
        L_0x0041:
            android.net.wifi.WifiInfo r1 = r4.mWifiInfo     // Catch:{ all -> 0x007f }
            java.lang.String r1 = r4.getValidSsid(r1)     // Catch:{ all -> 0x007f }
            r4.ssid = r1     // Catch:{ all -> 0x007f }
            goto L_0x0052
        L_0x004a:
            android.net.wifi.WifiInfo r1 = r4.mWifiInfo     // Catch:{ all -> 0x007f }
            java.lang.String r1 = r1.getPasspointProviderFriendlyName()     // Catch:{ all -> 0x007f }
            r4.ssid = r1     // Catch:{ all -> 0x007f }
        L_0x0052:
            boolean r1 = r4.mSupportMergedUi     // Catch:{ all -> 0x007f }
            if (r1 == 0) goto L_0x0066
            android.net.wifi.WifiInfo r1 = r4.mWifiInfo     // Catch:{ all -> 0x007f }
            boolean r1 = r1.isCarrierMerged()     // Catch:{ all -> 0x007f }
            r4.isCarrierMerged = r1     // Catch:{ all -> 0x007f }
            android.net.wifi.WifiInfo r1 = r4.mWifiInfo     // Catch:{ all -> 0x007f }
            int r1 = r1.getSubscriptionId()     // Catch:{ all -> 0x007f }
            r4.subId = r1     // Catch:{ all -> 0x007f }
        L_0x0066:
            android.net.wifi.WifiInfo r1 = r4.mWifiInfo     // Catch:{ all -> 0x007f }
            int r1 = r1.getRssi()     // Catch:{ all -> 0x007f }
            r4.updateRssi(r1)     // Catch:{ all -> 0x007f }
            r4.maybeRequestNetworkScore()     // Catch:{ all -> 0x007f }
            android.net.wifi.WifiInfo r1 = r4.mWifiInfo     // Catch:{ all -> 0x007f }
            boolean r1 = r4.isStandard11ax(r1)     // Catch:{ all -> 0x007f }
            r4.isStandard11ax = r1     // Catch:{ all -> 0x007f }
        L_0x007a:
            r4.updateStatusLabel()     // Catch:{ all -> 0x007f }
            monitor-exit(r0)     // Catch:{ all -> 0x007f }
            return
        L_0x007f:
            r4 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x007f }
            throw r4
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.settingslib.wifi.WifiStatusTracker.fetchInitialState():void");
    }

    public void handleBroadcast(Intent intent) {
        if (this.mWifiManager != null && intent.getAction().equals("android.net.wifi.WIFI_STATE_CHANGED")) {
            updateWifiState();
        }
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Removed duplicated region for block: B:18:0x003b  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void updateWifiInfo(android.net.wifi.WifiInfo r4) {
        /*
            r3 = this;
            java.lang.Object r0 = r3.mLock
            monitor-enter(r0)
            r3.updateWifiState()     // Catch:{ all -> 0x0061 }
            r1 = 0
            if (r4 == 0) goto L_0x000b
            r2 = 1
            goto L_0x000c
        L_0x000b:
            r2 = r1
        L_0x000c:
            r3.connected = r2     // Catch:{ all -> 0x0061 }
            r3.mWifiInfo = r4     // Catch:{ all -> 0x0061 }
            r2 = 0
            r3.ssid = r2     // Catch:{ all -> 0x0061 }
            r3.isStandard11ax = r1     // Catch:{ all -> 0x0061 }
            if (r4 == 0) goto L_0x005f
            boolean r4 = r4.isPasspointAp()     // Catch:{ all -> 0x0061 }
            if (r4 != 0) goto L_0x002f
            android.net.wifi.WifiInfo r4 = r3.mWifiInfo     // Catch:{ all -> 0x0061 }
            boolean r4 = r4.isOsuAp()     // Catch:{ all -> 0x0061 }
            if (r4 == 0) goto L_0x0026
            goto L_0x002f
        L_0x0026:
            android.net.wifi.WifiInfo r4 = r3.mWifiInfo     // Catch:{ all -> 0x0061 }
            java.lang.String r4 = r3.getValidSsid(r4)     // Catch:{ all -> 0x0061 }
            r3.ssid = r4     // Catch:{ all -> 0x0061 }
            goto L_0x0037
        L_0x002f:
            android.net.wifi.WifiInfo r4 = r3.mWifiInfo     // Catch:{ all -> 0x0061 }
            java.lang.String r4 = r4.getPasspointProviderFriendlyName()     // Catch:{ all -> 0x0061 }
            r3.ssid = r4     // Catch:{ all -> 0x0061 }
        L_0x0037:
            boolean r4 = r3.mSupportMergedUi     // Catch:{ all -> 0x0061 }
            if (r4 == 0) goto L_0x004b
            android.net.wifi.WifiInfo r4 = r3.mWifiInfo     // Catch:{ all -> 0x0061 }
            boolean r4 = r4.isCarrierMerged()     // Catch:{ all -> 0x0061 }
            r3.isCarrierMerged = r4     // Catch:{ all -> 0x0061 }
            android.net.wifi.WifiInfo r4 = r3.mWifiInfo     // Catch:{ all -> 0x0061 }
            int r4 = r4.getSubscriptionId()     // Catch:{ all -> 0x0061 }
            r3.subId = r4     // Catch:{ all -> 0x0061 }
        L_0x004b:
            android.net.wifi.WifiInfo r4 = r3.mWifiInfo     // Catch:{ all -> 0x0061 }
            int r4 = r4.getRssi()     // Catch:{ all -> 0x0061 }
            r3.updateRssi(r4)     // Catch:{ all -> 0x0061 }
            r3.maybeRequestNetworkScore()     // Catch:{ all -> 0x0061 }
            android.net.wifi.WifiInfo r4 = r3.mWifiInfo     // Catch:{ all -> 0x0061 }
            boolean r4 = r3.isStandard11ax(r4)     // Catch:{ all -> 0x0061 }
            r3.isStandard11ax = r4     // Catch:{ all -> 0x0061 }
        L_0x005f:
            monitor-exit(r0)     // Catch:{ all -> 0x0061 }
            return
        L_0x0061:
            r3 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x0061 }
            throw r3
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.settingslib.wifi.WifiStatusTracker.updateWifiInfo(android.net.wifi.WifiInfo):void");
    }

    private void updateWifiState() {
        int wifiState = this.mWifiManager.getWifiState();
        this.state = wifiState;
        this.enabled = wifiState == 3;
        this.isCarrierMerged = false;
        this.subId = 0;
    }

    private void updateRssi(int i) {
        this.rssi = i;
        this.level = this.mWifiManager.calculateSignalLevel(i);
    }

    private void maybeRequestNetworkScore() {
        NetworkKey createFromWifiInfo = NetworkKey.createFromWifiInfo(this.mWifiInfo);
        if (this.mWifiNetworkScoreCache.getScoredNetwork(createFromWifiInfo) == null) {
            this.mNetworkScoreManager.requestScores(new NetworkKey[]{createFromWifiInfo});
        }
    }

    /* access modifiers changed from: private */
    public void updateStatusLabel() {
        NetworkCapabilities networkCapabilities;
        String str;
        NetworkCapabilities networkCapabilities2;
        if (this.mWifiManager != null) {
            this.isDefaultNetwork = false;
            NetworkCapabilities networkCapabilities3 = this.mDefaultNetworkCapabilities;
            if (networkCapabilities3 != null) {
                boolean hasTransport = networkCapabilities3.hasTransport(1);
                boolean z = this.mDefaultNetworkCapabilities.hasTransport(0) && Utils.tryGetWifiInfoForVcn(this.mDefaultNetworkCapabilities) != null;
                if (hasTransport || z) {
                    this.isDefaultNetwork = true;
                }
            }
            if (this.isDefaultNetwork) {
                networkCapabilities = this.mDefaultNetworkCapabilities;
            } else {
                networkCapabilities = this.mConnectivityManager.getNetworkCapabilities(this.mWifiManager.getCurrentNetwork());
            }
            this.isCaptivePortal = false;
            if (networkCapabilities != null) {
                if (networkCapabilities.hasCapability(17)) {
                    this.statusLabel = this.mContext.getString(R$string.wifi_status_sign_in_required);
                    this.isCaptivePortal = true;
                    return;
                } else if (networkCapabilities.hasCapability(24)) {
                    this.statusLabel = this.mContext.getString(R$string.wifi_limited_connection);
                    return;
                } else if (!networkCapabilities.hasCapability(16)) {
                    Settings.Global.getString(this.mContext.getContentResolver(), "private_dns_mode");
                    if (networkCapabilities.isPrivateDnsBroken()) {
                        this.statusLabel = this.mContext.getString(R$string.private_dns_broken);
                        return;
                    } else {
                        this.statusLabel = this.mContext.getString(R$string.wifi_status_no_internet);
                        return;
                    }
                } else if (!this.isDefaultNetwork && (networkCapabilities2 = this.mDefaultNetworkCapabilities) != null && networkCapabilities2.hasTransport(0)) {
                    this.statusLabel = this.mContext.getString(R$string.wifi_connected_low_quality);
                    return;
                }
            }
            ScoredNetwork scoredNetwork = this.mWifiNetworkScoreCache.getScoredNetwork(NetworkKey.createFromWifiInfo(this.mWifiInfo));
            if (scoredNetwork == null) {
                str = null;
            } else {
                str = AccessPoint.getSpeedLabel(this.mContext, scoredNetwork, this.rssi);
            }
            this.statusLabel = str;
        }
    }

    public void refreshLocale() {
        updateStatusLabel();
        this.mCallback.run();
    }

    private String getValidSsid(WifiInfo wifiInfo) {
        String ssid2 = wifiInfo.getSSID();
        if (ssid2 == null || "<unknown ssid>".equals(ssid2)) {
            return null;
        }
        return ssid2;
    }

    /* access modifiers changed from: private */
    public void recordLastWifiNetwork(String str) {
        String[] strArr = this.mHistory;
        int i = this.mHistoryIndex;
        strArr[i] = str;
        this.mHistoryIndex = (i + 1) % 32;
    }

    public void dump(PrintWriter printWriter) {
        printWriter.println("  - WiFi Network History ------");
        int i = 0;
        for (int i2 = 0; i2 < 32; i2++) {
            if (this.mHistory[i2] != null) {
                i++;
            }
        }
        int i3 = this.mHistoryIndex + 32;
        while (true) {
            i3--;
            if (i3 >= (this.mHistoryIndex + 32) - i) {
                printWriter.println("  Previous WiFiNetwork(" + ((this.mHistoryIndex + 32) - i3) + "): " + this.mHistory[i3 & 31]);
            } else {
                return;
            }
        }
    }

    private boolean isStandard11ax(WifiInfo wifiInfo) {
        return wifiInfo.getWifiStandard() == 6;
    }
}
