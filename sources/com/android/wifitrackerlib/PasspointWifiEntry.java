package com.android.wifitrackerlib;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiNetworkScoreCache;
import android.net.wifi.hotspot2.PasspointConfiguration;
import android.os.Build;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import androidx.core.util.Preconditions;
import com.android.internal.annotations.VisibleForTesting;
import com.android.wifitrackerlib.WifiEntry;
import com.motorola.internal.enterprise.MotoDevicePolicyManager;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

@VisibleForTesting
public class PasspointWifiEntry extends WifiEntry implements WifiEntry.WifiEntryCallback {
    private final Context mContext;
    private final List<ScanResult> mCurrentHomeScanResults = new ArrayList();
    private final List<ScanResult> mCurrentRoamingScanResults = new ArrayList();
    private final String mFqdn;
    private final String mFriendlyName;
    private boolean mIsRoaming = false;
    private final String mKey;
    private int mMeteredOverride = 0;
    private MotoDevicePolicyManager mMotoDPMS = null;
    private OsuWifiEntry mOsuWifiEntry;
    private PasspointConfiguration mPasspointConfig;
    private boolean mShouldAutoOpenCaptivePortal = false;
    protected long mSubscriptionExpirationTimeInMillis;
    private List<Integer> mTargetSecurityTypes = List.of(11, 12);
    private WifiConfiguration mWifiConfig;

    /* access modifiers changed from: protected */
    public String getScanResultDescription() {
        return "";
    }

    PasspointWifiEntry(Context context, Handler handler, PasspointConfiguration passpointConfiguration, WifiManager wifiManager, WifiNetworkScoreCache wifiNetworkScoreCache, boolean z) throws IllegalArgumentException {
        super(handler, wifiManager, wifiNetworkScoreCache, z);
        Preconditions.checkNotNull(passpointConfiguration, "Cannot construct with null PasspointConfiguration!");
        this.mContext = context;
        this.mPasspointConfig = passpointConfiguration;
        this.mKey = uniqueIdToPasspointWifiEntryKey(passpointConfiguration.getUniqueId());
        String fqdn = passpointConfiguration.getHomeSp().getFqdn();
        this.mFqdn = fqdn;
        Preconditions.checkNotNull(fqdn, "Cannot construct with null PasspointConfiguration FQDN!");
        this.mFriendlyName = passpointConfiguration.getHomeSp().getFriendlyName();
        this.mSubscriptionExpirationTimeInMillis = passpointConfiguration.getSubscriptionExpirationTimeMillis();
        this.mMeteredOverride = this.mPasspointConfig.getMeteredOverride();
        this.mMotoDPMS = (MotoDevicePolicyManager) context.getSystemService("mot_device_policy");
    }

    PasspointWifiEntry(Context context, Handler handler, WifiConfiguration wifiConfiguration, WifiManager wifiManager, WifiNetworkScoreCache wifiNetworkScoreCache, boolean z) throws IllegalArgumentException {
        super(handler, wifiManager, wifiNetworkScoreCache, z);
        Preconditions.checkNotNull(wifiConfiguration, "Cannot construct with null WifiConfiguration!");
        if (wifiConfiguration.isPasspoint()) {
            this.mContext = context;
            this.mWifiConfig = wifiConfiguration;
            this.mKey = uniqueIdToPasspointWifiEntryKey(wifiConfiguration.getKey());
            String str = wifiConfiguration.FQDN;
            this.mFqdn = str;
            Preconditions.checkNotNull(str, "Cannot construct with null WifiConfiguration FQDN!");
            this.mFriendlyName = this.mWifiConfig.providerFriendlyName;
            return;
        }
        throw new IllegalArgumentException("Given WifiConfiguration is not for Passpoint!");
    }

    public String getKey() {
        return this.mKey;
    }

    public synchronized int getConnectedState() {
        OsuWifiEntry osuWifiEntry;
        if (!isExpired() || super.getConnectedState() != 0 || (osuWifiEntry = this.mOsuWifiEntry) == null) {
            return super.getConnectedState();
        }
        return osuWifiEntry.getConnectedState();
    }

    public String getTitle() {
        return this.mFriendlyName;
    }

    public synchronized String getSummary(boolean z) {
        StringJoiner stringJoiner;
        String str;
        stringJoiner = new StringJoiner(this.mContext.getString(R$string.wifitrackerlib_summary_separator));
        if (isExpired()) {
            OsuWifiEntry osuWifiEntry = this.mOsuWifiEntry;
            if (osuWifiEntry != null) {
                stringJoiner.add(osuWifiEntry.getSummary(z));
            } else {
                stringJoiner.add(this.mContext.getString(R$string.wifitrackerlib_wifi_passpoint_expired));
            }
        } else {
            int connectedState = getConnectedState();
            if (connectedState == 0) {
                str = Utils.getDisconnectedDescription(this.mContext, this.mWifiConfig, this.mForSavedNetworksPage, z);
            } else if (connectedState == 1) {
                str = Utils.getConnectingDescription(this.mContext, this.mNetworkInfo);
            } else if (connectedState != 2) {
                Log.e("PasspointWifiEntry", "getConnectedState() returned unknown state: " + connectedState);
                str = null;
            } else {
                str = Utils.getConnectedDescription(this.mContext, this.mWifiConfig, this.mNetworkCapabilities, (String) null, this.mIsDefaultNetwork, this.mIsLowQuality);
            }
            if (!TextUtils.isEmpty(str)) {
                stringJoiner.add(str);
            }
        }
        String speedDescription = Utils.getSpeedDescription(this.mContext, this);
        if (!TextUtils.isEmpty(speedDescription)) {
            stringJoiner.add(speedDescription);
        }
        String autoConnectDescription = Utils.getAutoConnectDescription(this.mContext, this);
        if (!TextUtils.isEmpty(autoConnectDescription)) {
            stringJoiner.add(autoConnectDescription);
        }
        String meteredDescription = Utils.getMeteredDescription(this.mContext, this);
        if (!TextUtils.isEmpty(meteredDescription)) {
            stringJoiner.add(meteredDescription);
        }
        if (!z) {
            String verboseLoggingDescription = Utils.getVerboseLoggingDescription(this);
            if (!TextUtils.isEmpty(verboseLoggingDescription)) {
                stringJoiner.add(verboseLoggingDescription);
            }
        }
        return stringJoiner.toString();
    }

    /* JADX WARNING: Code restructure failed: missing block: B:13:0x001c, code lost:
        return r0 != null ? android.net.wifi.WifiInfo.sanitizeSsid(r0.SSID) : null;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized java.lang.String getSsid() {
        /*
            r1 = this;
            monitor-enter(r1)
            android.net.wifi.WifiInfo r0 = r1.mWifiInfo     // Catch:{ all -> 0x001d }
            if (r0 == 0) goto L_0x000f
            java.lang.String r0 = r0.getSSID()     // Catch:{ all -> 0x001d }
            java.lang.String r0 = android.net.wifi.WifiInfo.sanitizeSsid(r0)     // Catch:{ all -> 0x001d }
            monitor-exit(r1)
            return r0
        L_0x000f:
            android.net.wifi.WifiConfiguration r0 = r1.mWifiConfig     // Catch:{ all -> 0x001d }
            if (r0 == 0) goto L_0x001a
            java.lang.String r0 = r0.SSID     // Catch:{ all -> 0x001d }
            java.lang.String r0 = android.net.wifi.WifiInfo.sanitizeSsid(r0)     // Catch:{ all -> 0x001d }
            goto L_0x001b
        L_0x001a:
            r0 = 0
        L_0x001b:
            monitor-exit(r1)
            return r0
        L_0x001d:
            r0 = move-exception
            monitor-exit(r1)
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.wifitrackerlib.PasspointWifiEntry.getSsid():java.lang.String");
    }

    public synchronized List<Integer> getSecurityTypes() {
        return new ArrayList(this.mTargetSecurityTypes);
    }

    public synchronized boolean isMetered() {
        boolean z;
        WifiConfiguration wifiConfiguration;
        z = true;
        if (getMeteredChoice() != 1 && ((wifiConfiguration = this.mWifiConfig) == null || !wifiConfiguration.meteredHint)) {
            z = false;
        }
        return z;
    }

    public synchronized boolean isSuggestion() {
        WifiConfiguration wifiConfiguration;
        wifiConfiguration = this.mWifiConfig;
        return wifiConfiguration != null && wifiConfiguration.fromWifiNetworkSuggestion;
    }

    public synchronized boolean isSubscription() {
        return this.mPasspointConfig != null;
    }

    private boolean cannotConnectWithAdminRestrictions() {
        List allowedSsids;
        MotoDevicePolicyManager motoDevicePolicyManager = this.mMotoDPMS;
        if (motoDevicePolicyManager == null || !motoDevicePolicyManager.hasUserRestriction("no_wifi_connection_by_ssid")) {
            return false;
        }
        int wifiRestrictionState = this.mMotoDPMS.getWifiRestrictionState();
        if (wifiRestrictionState == 2) {
            List blockedSsids = this.mMotoDPMS.getBlockedSsids();
            if (blockedSsids == null || !blockedSsids.contains(getSsid())) {
                return false;
            }
            if (Build.IS_DEBUGGABLE) {
                Log.d("PasspointWifiEntry:", getSsid() + ": Can't connect to blocked SSID");
            }
            return true;
        } else if (wifiRestrictionState != 1 || (allowedSsids = this.mMotoDPMS.getAllowedSsids()) == null || allowedSsids.contains(getSsid())) {
            return false;
        } else {
            if (Build.IS_DEBUGGABLE) {
                Log.d("PasspointWifiEntry:", getSsid() + ": SSID is not in allowed list");
            }
            return true;
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:8:0x0026, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized void connect(com.android.wifitrackerlib.WifiEntry.ConnectCallback r3) {
        /*
            r2 = this;
            monitor-enter(r2)
            boolean r0 = r2.cannotConnectWithAdminRestrictions()     // Catch:{ all -> 0x005b }
            if (r0 == 0) goto L_0x0027
            boolean r3 = android.os.Build.IS_DEBUGGABLE     // Catch:{ all -> 0x005b }
            if (r3 == 0) goto L_0x0025
            java.lang.String r3 = "PasspointWifiEntry:"
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ all -> 0x005b }
            r0.<init>()     // Catch:{ all -> 0x005b }
            java.lang.String r1 = "Can't connect to Passpoint network: "
            r0.append(r1)     // Catch:{ all -> 0x005b }
            java.lang.String r1 = r2.getSsid()     // Catch:{ all -> 0x005b }
            r0.append(r1)     // Catch:{ all -> 0x005b }
            java.lang.String r0 = r0.toString()     // Catch:{ all -> 0x005b }
            android.util.Log.d(r3, r0)     // Catch:{ all -> 0x005b }
        L_0x0025:
            monitor-exit(r2)
            return
        L_0x0027:
            boolean r0 = r2.isExpired()     // Catch:{ all -> 0x005b }
            if (r0 == 0) goto L_0x0036
            com.android.wifitrackerlib.OsuWifiEntry r0 = r2.mOsuWifiEntry     // Catch:{ all -> 0x005b }
            if (r0 == 0) goto L_0x0036
            r0.connect(r3)     // Catch:{ all -> 0x005b }
            monitor-exit(r2)
            return
        L_0x0036:
            r0 = 1
            r2.mShouldAutoOpenCaptivePortal = r0     // Catch:{ all -> 0x005b }
            r2.mConnectCallback = r3     // Catch:{ all -> 0x005b }
            android.net.wifi.WifiConfiguration r3 = r2.mWifiConfig     // Catch:{ all -> 0x005b }
            if (r3 != 0) goto L_0x0048
            com.android.wifitrackerlib.WifiEntry$ConnectActionListener r3 = new com.android.wifitrackerlib.WifiEntry$ConnectActionListener     // Catch:{ all -> 0x005b }
            r3.<init>()     // Catch:{ all -> 0x005b }
            r0 = 0
            r3.onFailure(r0)     // Catch:{ all -> 0x005b }
        L_0x0048:
            android.net.wifi.WifiManager r3 = r2.mWifiManager     // Catch:{ all -> 0x005b }
            r3.stopRestrictingAutoJoinToSubscriptionId()     // Catch:{ all -> 0x005b }
            android.net.wifi.WifiManager r3 = r2.mWifiManager     // Catch:{ all -> 0x005b }
            android.net.wifi.WifiConfiguration r0 = r2.mWifiConfig     // Catch:{ all -> 0x005b }
            com.android.wifitrackerlib.WifiEntry$ConnectActionListener r1 = new com.android.wifitrackerlib.WifiEntry$ConnectActionListener     // Catch:{ all -> 0x005b }
            r1.<init>()     // Catch:{ all -> 0x005b }
            r3.connect(r0, r1)     // Catch:{ all -> 0x005b }
            monitor-exit(r2)
            return
        L_0x005b:
            r3 = move-exception
            monitor-exit(r2)
            throw r3
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.wifitrackerlib.PasspointWifiEntry.connect(com.android.wifitrackerlib.WifiEntry$ConnectCallback):void");
    }

    public synchronized int getMeteredChoice() {
        int i = this.mMeteredOverride;
        if (i == 1) {
            return 1;
        }
        if (i == 2) {
            return 2;
        }
        return 0;
    }

    public synchronized boolean canSetMeteredChoice() {
        return !isSuggestion() && this.mPasspointConfig != null;
    }

    public synchronized boolean isAutoJoinEnabled() {
        PasspointConfiguration passpointConfiguration = this.mPasspointConfig;
        if (passpointConfiguration != null) {
            return passpointConfiguration.isAutojoinEnabled();
        }
        WifiConfiguration wifiConfiguration = this.mWifiConfig;
        if (wifiConfiguration == null) {
            return false;
        }
        return wifiConfiguration.allowAutojoin;
    }

    public synchronized boolean canSetAutoJoinEnabled() {
        return (this.mPasspointConfig == null && this.mWifiConfig == null) ? false : true;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:13:0x0018, code lost:
        return r1;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized boolean isExpired() {
        /*
            r6 = this;
            monitor-enter(r6)
            long r0 = r6.mSubscriptionExpirationTimeInMillis     // Catch:{ all -> 0x0019 }
            r2 = 0
            int r0 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            r1 = 0
            if (r0 > 0) goto L_0x000c
            monitor-exit(r6)
            return r1
        L_0x000c:
            long r2 = java.lang.System.currentTimeMillis()     // Catch:{ all -> 0x0019 }
            long r4 = r6.mSubscriptionExpirationTimeInMillis     // Catch:{ all -> 0x0019 }
            int r0 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1))
            if (r0 < 0) goto L_0x0017
            r1 = 1
        L_0x0017:
            monitor-exit(r6)
            return r1
        L_0x0019:
            r0 = move-exception
            monitor-exit(r6)
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.wifitrackerlib.PasspointWifiEntry.isExpired():boolean");
    }

    /* access modifiers changed from: package-private */
    public synchronized void updatePasspointConfig(PasspointConfiguration passpointConfiguration) {
        this.mPasspointConfig = passpointConfiguration;
        if (passpointConfiguration != null) {
            this.mSubscriptionExpirationTimeInMillis = passpointConfiguration.getSubscriptionExpirationTimeMillis();
            this.mMeteredOverride = passpointConfiguration.getMeteredOverride();
        }
        notifyOnUpdated();
    }

    /* access modifiers changed from: package-private */
    public synchronized void updateScanResultInfo(WifiConfiguration wifiConfiguration, List<ScanResult> list, List<ScanResult> list2) throws IllegalArgumentException {
        this.mIsRoaming = false;
        this.mWifiConfig = wifiConfiguration;
        this.mCurrentHomeScanResults.clear();
        this.mCurrentRoamingScanResults.clear();
        if (list != null) {
            this.mCurrentHomeScanResults.addAll(list);
        }
        if (list2 != null) {
            this.mCurrentRoamingScanResults.addAll(list2);
        }
        int i = -1;
        if (this.mWifiConfig != null) {
            ArrayList arrayList = new ArrayList();
            if (list != null && !list.isEmpty()) {
                arrayList.addAll(list);
            } else if (list2 != null && !list2.isEmpty()) {
                arrayList.addAll(list2);
                this.mIsRoaming = true;
            }
            ScanResult bestScanResultByLevel = Utils.getBestScanResultByLevel(arrayList);
            if (bestScanResultByLevel != null) {
                WifiConfiguration wifiConfiguration2 = this.mWifiConfig;
                wifiConfiguration2.SSID = "\"" + bestScanResultByLevel.SSID + "\"";
            }
            if (getConnectedState() == 0) {
                if (bestScanResultByLevel != null) {
                    i = this.mWifiManager.calculateSignalLevel(bestScanResultByLevel.level);
                }
                this.mLevel = i;
                this.mSpeed = Utils.getAverageSpeedFromScanResults(this.mScoreCache, arrayList);
            }
        } else {
            this.mLevel = -1;
        }
        notifyOnUpdated();
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Code restructure failed: missing block: B:10:0x0019, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized void updateSecurityTypes() {
        /*
            r2 = this;
            monitor-enter(r2)
            android.net.wifi.WifiInfo r0 = r2.mWifiInfo     // Catch:{ all -> 0x001a }
            if (r0 == 0) goto L_0x0018
            int r0 = r0.getCurrentSecurityType()     // Catch:{ all -> 0x001a }
            r1 = -1
            if (r0 == r1) goto L_0x0018
            java.lang.Integer r0 = java.lang.Integer.valueOf(r0)     // Catch:{ all -> 0x001a }
            java.util.List r0 = java.util.Collections.singletonList(r0)     // Catch:{ all -> 0x001a }
            r2.mTargetSecurityTypes = r0     // Catch:{ all -> 0x001a }
            monitor-exit(r2)
            return
        L_0x0018:
            monitor-exit(r2)
            return
        L_0x001a:
            r0 = move-exception
            monitor-exit(r2)
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.wifitrackerlib.PasspointWifiEntry.updateSecurityTypes():void");
    }

    /* access modifiers changed from: package-private */
    public synchronized void onScoreCacheUpdated() {
        WifiInfo wifiInfo = this.mWifiInfo;
        if (wifiInfo != null) {
            this.mSpeed = Utils.getSpeedFromWifiInfo(this.mScoreCache, wifiInfo);
        } else if (!this.mCurrentHomeScanResults.isEmpty()) {
            this.mSpeed = Utils.getAverageSpeedFromScanResults(this.mScoreCache, this.mCurrentHomeScanResults);
        } else {
            this.mSpeed = Utils.getAverageSpeedFromScanResults(this.mScoreCache, this.mCurrentRoamingScanResults);
        }
        notifyOnUpdated();
    }

    /* access modifiers changed from: protected */
    public boolean connectionInfoMatches(WifiInfo wifiInfo, NetworkInfo networkInfo) {
        if (!wifiInfo.isPasspointAp()) {
            return false;
        }
        return TextUtils.equals(wifiInfo.getPasspointFqdn(), this.mFqdn);
    }

    /* access modifiers changed from: package-private */
    public synchronized void updateNetworkCapabilities(NetworkCapabilities networkCapabilities) {
        super.updateNetworkCapabilities(networkCapabilities);
        if (canSignIn() && this.mShouldAutoOpenCaptivePortal) {
            this.mShouldAutoOpenCaptivePortal = false;
            signIn((WifiEntry.SignInCallback) null);
        }
    }

    /* access modifiers changed from: package-private */
    public static String uniqueIdToPasspointWifiEntryKey(String str) {
        Preconditions.checkNotNull(str, "Cannot create key with null unique id!");
        return "PasspointWifiEntry:" + str;
    }

    /* access modifiers changed from: package-private */
    public synchronized String getNetworkSelectionDescription() {
        return Utils.getNetworkSelectionDescription(this.mWifiConfig);
    }

    /* access modifiers changed from: package-private */
    public synchronized void setOsuWifiEntry(OsuWifiEntry osuWifiEntry) {
        this.mOsuWifiEntry = osuWifiEntry;
        if (osuWifiEntry != null) {
            osuWifiEntry.setListener(this);
        }
    }

    public void onUpdated() {
        notifyOnUpdated();
    }

    public synchronized boolean canSignIn() {
        NetworkCapabilities networkCapabilities;
        networkCapabilities = this.mNetworkCapabilities;
        return networkCapabilities != null && networkCapabilities.hasCapability(17);
    }

    public void signIn(WifiEntry.SignInCallback signInCallback) {
        if (canSignIn()) {
            ((ConnectivityManager) this.mContext.getSystemService("connectivity")).startCaptivePortalApp(this.mWifiManager.getCurrentNetwork());
        }
    }
}
