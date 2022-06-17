package com.android.wifitrackerlib;

import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.LinkProperties;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.NetworkScoreManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.hotspot2.OsuProvider;
import android.net.wifi.hotspot2.PasspointConfiguration;
import android.os.Build;
import android.os.Handler;
import android.telephony.SubscriptionManager;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.util.Log;
import android.util.Pair;
import android.util.SparseArray;
import androidx.core.util.Preconditions;
import androidx.lifecycle.Lifecycle;
import com.android.wifitrackerlib.BaseWifiTracker;
import com.android.wifitrackerlib.StandardWifiEntry;
import com.motorola.internal.enterprise.MotoDevicePolicyManager;
import java.time.Clock;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.stream.Collectors;

public class WifiPickerTracker extends BaseWifiTracker {
    private WifiEntry mConnectedWifiEntry;
    private NetworkInfo mCurrentNetworkInfo;
    private DevicePolicyManager mDPM = null;
    private final WifiPickerTrackerCallback mListener;
    private final Object mLock = new Object();
    private MergedCarrierEntry mMergedCarrierEntry;
    private MotoDevicePolicyManager mMotoDPMS = null;
    private final ArrayMap<StandardWifiEntry.StandardWifiEntryKey, List<WifiConfiguration>> mNetworkRequestConfigCache = new ArrayMap<>();
    private NetworkRequestEntry mNetworkRequestEntry;
    private int mNumSavedNetworks;
    private final Map<String, OsuWifiEntry> mOsuWifiEntryCache = new ArrayMap();
    private PackageManager mPackageManager = null;
    private final Map<String, PasspointConfiguration> mPasspointConfigCache = new ArrayMap();
    private final SparseArray<WifiConfiguration> mPasspointWifiConfigCache = new SparseArray<>();
    private final Map<String, PasspointWifiEntry> mPasspointWifiEntryCache = new ArrayMap();
    private final Map<StandardWifiEntry.StandardWifiEntryKey, List<WifiConfiguration>> mStandardWifiConfigCache = new ArrayMap();
    private final List<StandardWifiEntry> mStandardWifiEntryCache = new ArrayList();
    private final Map<StandardWifiEntry.StandardWifiEntryKey, List<WifiConfiguration>> mSuggestedConfigCache = new ArrayMap();
    private final List<StandardWifiEntry> mSuggestedWifiEntryCache = new ArrayList();
    private final List<WifiEntry> mWifiEntries = new ArrayList();

    public interface WifiPickerTrackerCallback extends BaseWifiTracker.BaseWifiTrackerCallback {
        void onNumSavedNetworksChanged();

        void onNumSavedSubscriptionsChanged();

        void onWifiEntriesChanged();
    }

    /* access modifiers changed from: protected */
    public void updateContextualWifiEntryScans(List<ScanResult> list) {
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public WifiPickerTracker(Lifecycle lifecycle, Context context, WifiManager wifiManager, ConnectivityManager connectivityManager, NetworkScoreManager networkScoreManager, Handler handler, Handler handler2, Clock clock, long j, long j2, WifiPickerTrackerCallback wifiPickerTrackerCallback) {
        super(lifecycle, context, wifiManager, connectivityManager, networkScoreManager, handler, handler2, clock, j, j2, wifiPickerTrackerCallback, "WifiPickerTracker");
        this.mListener = wifiPickerTrackerCallback;
        this.mMotoDPMS = (MotoDevicePolicyManager) this.mContext.getSystemService("mot_device_policy");
        this.mDPM = (DevicePolicyManager) this.mContext.getSystemService("device_policy");
        this.mPackageManager = context.getPackageManager();
    }

    public WifiEntry getConnectedWifiEntry() {
        return this.mConnectedWifiEntry;
    }

    public List<WifiEntry> getWifiEntries() {
        ArrayList arrayList;
        DevicePolicyManager devicePolicyManager;
        if (this.mPackageManager.hasSystemFeature("android.software.device_admin") && (devicePolicyManager = this.mDPM) != null && devicePolicyManager.getDeviceOwnerComponentOnAnyUser() != null) {
            return applyAdminRestrictions();
        }
        synchronized (this.mLock) {
            arrayList = new ArrayList(this.mWifiEntries);
        }
        return arrayList;
    }

    private List<WifiEntry> applyAdminRestrictions() {
        ArrayList arrayList;
        List<String> blockedSsids;
        List allowedSsids;
        MotoDevicePolicyManager motoDevicePolicyManager = this.mMotoDPMS;
        if (motoDevicePolicyManager != null && motoDevicePolicyManager.hasUserRestriction("disallow_wifi_unsecure_networks")) {
            synchronized (this.mLock) {
                this.mWifiEntries.removeIf(new WifiPickerTracker$$ExternalSyntheticLambda16(this));
                if (Build.IS_DEBUGGABLE) {
                    Log.d("WifiPickerTracker", "MotoDevicePolicyManager.DISALLOW_UNSECURE_NETWORKS is active - WifiEntries: " + this.mWifiEntries.toString());
                }
            }
        }
        MotoDevicePolicyManager motoDevicePolicyManager2 = this.mMotoDPMS;
        if (motoDevicePolicyManager2 != null && motoDevicePolicyManager2.hasUserRestriction("no_wifi_connection_by_ssid")) {
            if (Build.IS_DEBUGGABLE) {
                Log.d("WifiPickerTracker", "MotoDevicePolicyManager - Filter SSIDs by Allow or by Block list...");
            }
            int wifiRestrictionState = this.mMotoDPMS.getWifiRestrictionState();
            if (wifiRestrictionState == 1 && (allowedSsids = this.mMotoDPMS.getAllowedSsids()) != null) {
                synchronized (this.mLock) {
                    this.mWifiEntries.removeIf(new WifiPickerTracker$$ExternalSyntheticLambda19(allowedSsids));
                    if (Build.IS_DEBUGGABLE) {
                        Log.d("WifiPickerTracker", "MotoDevicePolicyManager.WIFI_RESTRICTION_ALLOWLIST_ACTIVATED is active - WifiEntries: " + this.mWifiEntries.toString());
                    }
                }
            }
            if (wifiRestrictionState == 2 && (blockedSsids = this.mMotoDPMS.getBlockedSsids()) != null) {
                synchronized (this.mLock) {
                    for (String wifiPickerTracker$$ExternalSyntheticLambda18 : blockedSsids) {
                        this.mWifiEntries.removeIf(new WifiPickerTracker$$ExternalSyntheticLambda18(this, wifiPickerTracker$$ExternalSyntheticLambda18));
                    }
                    if (Build.IS_DEBUGGABLE) {
                        Log.d("WifiPickerTracker", "MotoDevicePolicyManager.WIFI_RESTRICTION_BLOCKLIST_ACTIVATED is active - WifiEntries: " + this.mWifiEntries.toString());
                    }
                }
            }
        }
        synchronized (this.mLock) {
            arrayList = new ArrayList(this.mWifiEntries);
        }
        return arrayList;
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$applyAdminRestrictions$1(List list, WifiEntry wifiEntry) {
        return !list.contains(wifiEntry.getSsid());
    }

    /* access modifiers changed from: private */
    /* renamed from: predicateUnsecureNetworks */
    public boolean lambda$applyAdminRestrictions$0(WifiEntry wifiEntry) {
        boolean z = wifiEntry.getSecurity() == 0;
        if (Build.IS_DEBUGGABLE) {
            Log.d("WifiPickerTracker", "Is ssid: " + wifiEntry.getSsid() + " unsecure? " + z);
        }
        return z;
    }

    /* access modifiers changed from: private */
    /* renamed from: predicateBlocked */
    public boolean lambda$applyAdminRestrictions$2(WifiEntry wifiEntry, String str) {
        if (Build.IS_DEBUGGABLE) {
            StringBuilder sb = new StringBuilder();
            sb.append("MotoDevicePolicyManager: scanned ssid: ");
            sb.append(wifiEntry.getSsid());
            sb.append(" - blocked ssid: ");
            sb.append(str);
            sb.append(" Check if it is blocked... - ");
            sb.append(!str.equals(wifiEntry.getSsid()));
            Log.d("WifiPickerTracker", sb.toString());
        }
        return str.equals(wifiEntry.getSsid());
    }

    /* access modifiers changed from: protected */
    public void handleOnStart() {
        updateWifiConfigurations(this.mWifiManager.getPrivilegedConfiguredNetworks());
        updatePasspointConfigurations(this.mWifiManager.getPasspointConfigurations());
        this.mScanResultUpdater.update(this.mWifiManager.getScanResults());
        conditionallyUpdateScanResults(true);
        WifiInfo connectionInfo = this.mWifiManager.getConnectionInfo();
        Network currentNetwork = this.mWifiManager.getCurrentNetwork();
        NetworkInfo networkInfo = this.mConnectivityManager.getNetworkInfo(currentNetwork);
        this.mCurrentNetworkInfo = networkInfo;
        updateConnectionInfo(connectionInfo, networkInfo);
        notifyOnNumSavedNetworksChanged();
        notifyOnNumSavedSubscriptionsChanged();
        handleDefaultSubscriptionChanged(SubscriptionManager.getDefaultDataSubscriptionId());
        updateWifiEntries();
        handleNetworkCapabilitiesChanged(this.mConnectivityManager.getNetworkCapabilities(currentNetwork));
        handleLinkPropertiesChanged(this.mConnectivityManager.getLinkProperties(currentNetwork));
        handleDefaultRouteChanged();
    }

    /* access modifiers changed from: protected */
    public void handleWifiStateChangedAction() {
        conditionallyUpdateScanResults(true);
        if (this.mWifiManager.getWifiState() == 1) {
            updateConnectionInfo((WifiInfo) null, (NetworkInfo) null);
        }
        updateWifiEntries();
    }

    /* access modifiers changed from: protected */
    public void handleScanResultsAvailableAction(Intent intent) {
        Preconditions.checkNotNull(intent, "Intent cannot be null!");
        conditionallyUpdateScanResults(intent.getBooleanExtra("resultsUpdated", true));
        updateWifiEntries();
    }

    /* access modifiers changed from: protected */
    public void handleConfiguredNetworksChangedAction(Intent intent) {
        Preconditions.checkNotNull(intent, "Intent cannot be null!");
        processConfiguredNetworksChanged();
    }

    /* access modifiers changed from: protected */
    public void processConfiguredNetworksChanged() {
        updateWifiConfigurations(this.mWifiManager.getPrivilegedConfiguredNetworks());
        updatePasspointConfigurations(this.mWifiManager.getPasspointConfigurations());
        List<ScanResult> scanResults = this.mScanResultUpdater.getScanResults();
        updateStandardWifiEntryScans(scanResults);
        updateNetworkRequestEntryScans(scanResults);
        updatePasspointWifiEntryScans(scanResults);
        updateOsuWifiEntryScans(scanResults);
        notifyOnNumSavedNetworksChanged();
        notifyOnNumSavedSubscriptionsChanged();
        updateWifiEntries();
    }

    /* access modifiers changed from: protected */
    public void handleNetworkStateChangedAction(Intent intent) {
        Preconditions.checkNotNull(intent, "Intent cannot be null!");
        this.mCurrentNetworkInfo = (NetworkInfo) intent.getParcelableExtra("networkInfo");
        updateConnectionInfo(this.mWifiManager.getConnectionInfo(), this.mCurrentNetworkInfo);
        updateWifiEntries();
    }

    /* access modifiers changed from: protected */
    public void handleRssiChangedAction() {
        WifiInfo connectionInfo = this.mWifiManager.getConnectionInfo();
        WifiEntry wifiEntry = this.mConnectedWifiEntry;
        if (wifiEntry != null) {
            wifiEntry.updateConnectionInfo(connectionInfo, this.mCurrentNetworkInfo);
        }
        MergedCarrierEntry mergedCarrierEntry = this.mMergedCarrierEntry;
        if (mergedCarrierEntry != null) {
            mergedCarrierEntry.updateConnectionInfo(connectionInfo, this.mCurrentNetworkInfo);
        }
    }

    /* access modifiers changed from: protected */
    public void handleLinkPropertiesChanged(LinkProperties linkProperties) {
        WifiEntry wifiEntry = this.mConnectedWifiEntry;
        if (wifiEntry != null && wifiEntry.getConnectedState() == 2) {
            this.mConnectedWifiEntry.updateLinkProperties(linkProperties);
        }
        MergedCarrierEntry mergedCarrierEntry = this.mMergedCarrierEntry;
        if (mergedCarrierEntry != null) {
            mergedCarrierEntry.updateLinkProperties(linkProperties);
        }
    }

    /* access modifiers changed from: protected */
    public void handleNetworkCapabilitiesChanged(NetworkCapabilities networkCapabilities) {
        WifiEntry wifiEntry = this.mConnectedWifiEntry;
        if (wifiEntry != null && wifiEntry.getConnectedState() == 2) {
            this.mConnectedWifiEntry.updateNetworkCapabilities(networkCapabilities);
            this.mConnectedWifiEntry.setIsLowQuality(this.mIsWifiValidated && this.mIsCellDefaultRoute);
        }
        MergedCarrierEntry mergedCarrierEntry = this.mMergedCarrierEntry;
        if (mergedCarrierEntry != null) {
            mergedCarrierEntry.updateNetworkCapabilities(networkCapabilities);
        }
    }

    /* access modifiers changed from: protected */
    public void handleDefaultRouteChanged() {
        WifiEntry wifiEntry = this.mConnectedWifiEntry;
        if (wifiEntry != null) {
            wifiEntry.setIsDefaultNetwork(this.mIsWifiDefaultRoute);
            this.mConnectedWifiEntry.setIsLowQuality(this.mIsWifiValidated && this.mIsCellDefaultRoute);
        }
        MergedCarrierEntry mergedCarrierEntry = this.mMergedCarrierEntry;
        if (mergedCarrierEntry != null) {
            if (mergedCarrierEntry.getConnectedState() == 2) {
                this.mMergedCarrierEntry.setIsDefaultNetwork(this.mIsWifiDefaultRoute);
            }
            this.mMergedCarrierEntry.updateIsCellDefaultRoute(this.mIsCellDefaultRoute);
        }
    }

    /* access modifiers changed from: protected */
    public void handleNetworkScoreCacheUpdated() {
        for (StandardWifiEntry onScoreCacheUpdated : this.mStandardWifiEntryCache) {
            onScoreCacheUpdated.onScoreCacheUpdated();
        }
        for (StandardWifiEntry onScoreCacheUpdated2 : this.mSuggestedWifiEntryCache) {
            onScoreCacheUpdated2.onScoreCacheUpdated();
        }
        for (PasspointWifiEntry onScoreCacheUpdated3 : this.mPasspointWifiEntryCache.values()) {
            onScoreCacheUpdated3.onScoreCacheUpdated();
        }
    }

    /* access modifiers changed from: protected */
    public void handleDefaultSubscriptionChanged(int i) {
        updateMergedCarrierEntry(i);
    }

    /* access modifiers changed from: protected */
    public void updateWifiEntries() {
        NetworkRequestEntry networkRequestEntry;
        synchronized (this.mLock) {
            WifiEntry wifiEntry = (WifiEntry) this.mStandardWifiEntryCache.stream().filter(WifiPickerTracker$$ExternalSyntheticLambda30.INSTANCE).findAny().orElse((Object) null);
            this.mConnectedWifiEntry = wifiEntry;
            if (wifiEntry == null) {
                this.mConnectedWifiEntry = (WifiEntry) this.mSuggestedWifiEntryCache.stream().filter(WifiPickerTracker$$ExternalSyntheticLambda28.INSTANCE).findAny().orElse((Object) null);
            }
            if (this.mConnectedWifiEntry == null) {
                this.mConnectedWifiEntry = this.mPasspointWifiEntryCache.values().stream().filter(WifiPickerTracker$$ExternalSyntheticLambda26.INSTANCE).findAny().orElse((Object) null);
            }
            if (!(this.mConnectedWifiEntry != null || (networkRequestEntry = this.mNetworkRequestEntry) == null || networkRequestEntry.getConnectedState() == 0)) {
                this.mConnectedWifiEntry = this.mNetworkRequestEntry;
            }
            WifiEntry wifiEntry2 = this.mConnectedWifiEntry;
            if (wifiEntry2 != null) {
                wifiEntry2.setIsDefaultNetwork(this.mIsWifiDefaultRoute);
            }
            this.mWifiEntries.clear();
            Set set = (Set) this.mSuggestedWifiEntryCache.stream().filter(new WifiPickerTracker$$ExternalSyntheticLambda15(this)).map(WifiPickerTracker$$ExternalSyntheticLambda10.INSTANCE).collect(Collectors.toSet());
            for (StandardWifiEntry next : this.mStandardWifiEntryCache) {
                if (next != this.mConnectedWifiEntry) {
                    if (next.isSaved() || !set.contains(next.getStandardWifiEntryKey().getScanResultKey())) {
                        this.mWifiEntries.add(next);
                    }
                }
            }
            this.mWifiEntries.addAll((Collection) this.mSuggestedWifiEntryCache.stream().filter(WifiPickerTracker$$ExternalSyntheticLambda27.INSTANCE).collect(Collectors.toList()));
            this.mWifiEntries.addAll((Collection) this.mPasspointWifiEntryCache.values().stream().filter(WifiPickerTracker$$ExternalSyntheticLambda25.INSTANCE).collect(Collectors.toList()));
            this.mWifiEntries.addAll((Collection) this.mOsuWifiEntryCache.values().stream().filter(WifiPickerTracker$$ExternalSyntheticLambda24.INSTANCE).collect(Collectors.toList()));
            this.mWifiEntries.addAll((Collection) getContextualWifiEntries().stream().filter(WifiPickerTracker$$ExternalSyntheticLambda32.INSTANCE).collect(Collectors.toList()));
            Collections.sort(this.mWifiEntries);
            if (BaseWifiTracker.isVerboseLoggingEnabled()) {
                Log.v("WifiPickerTracker", "Connected WifiEntry: " + this.mConnectedWifiEntry);
                Log.v("WifiPickerTracker", "Updated WifiEntries: " + Arrays.toString(this.mWifiEntries.toArray()));
            }
        }
        notifyOnWifiEntriesChanged();
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$updateWifiEntries$3(StandardWifiEntry standardWifiEntry) {
        int connectedState = standardWifiEntry.getConnectedState();
        return connectedState == 2 || connectedState == 1;
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$updateWifiEntries$4(StandardWifiEntry standardWifiEntry) {
        int connectedState = standardWifiEntry.getConnectedState();
        return connectedState == 2 || connectedState == 1;
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$updateWifiEntries$5(PasspointWifiEntry passpointWifiEntry) {
        int connectedState = passpointWifiEntry.getConnectedState();
        return connectedState == 2 || connectedState == 1;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ boolean lambda$updateWifiEntries$6(StandardWifiEntry standardWifiEntry) {
        return standardWifiEntry.isUserShareable() || standardWifiEntry == this.mConnectedWifiEntry;
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$updateWifiEntries$8(StandardWifiEntry standardWifiEntry) {
        return standardWifiEntry.getConnectedState() == 0 && standardWifiEntry.isUserShareable();
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$updateWifiEntries$9(PasspointWifiEntry passpointWifiEntry) {
        return passpointWifiEntry.getConnectedState() == 0;
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$updateWifiEntries$10(OsuWifiEntry osuWifiEntry) {
        return osuWifiEntry.getConnectedState() == 0 && !osuWifiEntry.isAlreadyProvisioned();
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$updateWifiEntries$11(WifiEntry wifiEntry) {
        return wifiEntry.getConnectedState() == 0;
    }

    private void updateMergedCarrierEntry(int i) {
        if (i != -1) {
            MergedCarrierEntry mergedCarrierEntry = this.mMergedCarrierEntry;
            if (mergedCarrierEntry == null || i != mergedCarrierEntry.getSubscriptionId()) {
                MergedCarrierEntry mergedCarrierEntry2 = new MergedCarrierEntry(this.mWorkerHandler, this.mWifiManager, this.mWifiNetworkScoreCache, false, this.mContext, i);
                this.mMergedCarrierEntry = mergedCarrierEntry2;
                mergedCarrierEntry2.updateConnectionInfo(this.mWifiManager.getConnectionInfo(), this.mCurrentNetworkInfo);
            } else {
                return;
            }
        } else if (this.mMergedCarrierEntry != null) {
            this.mMergedCarrierEntry = null;
        } else {
            return;
        }
        notifyOnWifiEntriesChanged();
    }

    /* access modifiers changed from: protected */
    public List<WifiEntry> getContextualWifiEntries() {
        return Collections.emptyList();
    }

    private void updateStandardWifiEntryScans(List<ScanResult> list) {
        Preconditions.checkNotNull(list, "Scan Result list should not be null!");
        Map map = (Map) list.stream().filter(WifiPickerTracker$$ExternalSyntheticLambda22.INSTANCE).collect(Collectors.groupingBy(SavedNetworkTracker$$ExternalSyntheticLambda4.INSTANCE));
        ArraySet<StandardWifiEntry.ScanResultKey> arraySet = new ArraySet<>(map.keySet());
        this.mStandardWifiEntryCache.forEach(new WifiPickerTracker$$ExternalSyntheticLambda5(arraySet, map));
        for (StandardWifiEntry.ScanResultKey scanResultKey : arraySet) {
            StandardWifiEntry.StandardWifiEntryKey standardWifiEntryKey = new StandardWifiEntry.StandardWifiEntryKey(scanResultKey, true);
            this.mStandardWifiEntryCache.add(new StandardWifiEntry(this.mContext, this.mMainHandler, standardWifiEntryKey, this.mStandardWifiConfigCache.get(standardWifiEntryKey), (List) map.get(scanResultKey), this.mWifiManager, this.mWifiNetworkScoreCache, false));
        }
        this.mStandardWifiEntryCache.removeIf(WifiPickerTracker$$ExternalSyntheticLambda31.INSTANCE);
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$updateStandardWifiEntryScans$12(ScanResult scanResult) {
        return !TextUtils.isEmpty(scanResult.SSID);
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$updateStandardWifiEntryScans$13(Set set, Map map, StandardWifiEntry standardWifiEntry) {
        StandardWifiEntry.ScanResultKey scanResultKey = standardWifiEntry.getStandardWifiEntryKey().getScanResultKey();
        set.remove(scanResultKey);
        standardWifiEntry.updateScanResultInfo((List) map.get(scanResultKey));
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$updateStandardWifiEntryScans$14(StandardWifiEntry standardWifiEntry) {
        return standardWifiEntry.getLevel() == -1;
    }

    private void updateSuggestedWifiEntryScans(List<ScanResult> list) {
        Preconditions.checkNotNull(list, "Scan Result list should not be null!");
        Map map = (Map) list.stream().filter(WifiPickerTracker$$ExternalSyntheticLambda21.INSTANCE).collect(Collectors.groupingBy(SavedNetworkTracker$$ExternalSyntheticLambda4.INSTANCE));
        ArraySet arraySet = new ArraySet();
        this.mSuggestedWifiEntryCache.forEach(new WifiPickerTracker$$ExternalSyntheticLambda6(arraySet, map));
        Set set = (Set) this.mWifiManager.getWifiConfigForMatchedNetworkSuggestionsSharedWithUser(list).stream().map(SavedNetworkTracker$$ExternalSyntheticLambda5.INSTANCE).collect(Collectors.toSet());
        for (StandardWifiEntry.StandardWifiEntryKey next : this.mSuggestedConfigCache.keySet()) {
            StandardWifiEntry.ScanResultKey scanResultKey = next.getScanResultKey();
            if (!arraySet.contains(next) && map.containsKey(scanResultKey)) {
                StandardWifiEntry standardWifiEntry = new StandardWifiEntry(this.mContext, this.mMainHandler, next, this.mSuggestedConfigCache.get(next), (List) map.get(scanResultKey), this.mWifiManager, this.mWifiNetworkScoreCache, false);
                standardWifiEntry.setUserShareable(set.contains(next));
                this.mSuggestedWifiEntryCache.add(standardWifiEntry);
            }
        }
        this.mSuggestedWifiEntryCache.removeIf(WifiPickerTracker$$ExternalSyntheticLambda29.INSTANCE);
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$updateSuggestedWifiEntryScans$15(ScanResult scanResult) {
        return !TextUtils.isEmpty(scanResult.SSID);
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$updateSuggestedWifiEntryScans$16(Set set, Map map, StandardWifiEntry standardWifiEntry) {
        StandardWifiEntry.StandardWifiEntryKey standardWifiEntryKey = standardWifiEntry.getStandardWifiEntryKey();
        set.add(standardWifiEntryKey);
        standardWifiEntry.updateScanResultInfo((List) map.get(standardWifiEntryKey.getScanResultKey()));
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$updateSuggestedWifiEntryScans$17(StandardWifiEntry standardWifiEntry) {
        return standardWifiEntry.getLevel() == -1;
    }

    private void updatePasspointWifiEntryScans(List<ScanResult> list) {
        List<ScanResult> list2 = list;
        Preconditions.checkNotNull(list2, "Scan Result list should not be null!");
        TreeSet treeSet = new TreeSet();
        for (Pair pair : this.mWifiManager.getAllMatchingWifiConfigs(list2)) {
            WifiConfiguration wifiConfiguration = (WifiConfiguration) pair.first;
            List list3 = (List) ((Map) pair.second).get(0);
            List list4 = (List) ((Map) pair.second).get(1);
            String uniqueIdToPasspointWifiEntryKey = PasspointWifiEntry.uniqueIdToPasspointWifiEntryKey(wifiConfiguration.getKey());
            treeSet.add(uniqueIdToPasspointWifiEntryKey);
            if (!this.mPasspointWifiEntryCache.containsKey(uniqueIdToPasspointWifiEntryKey)) {
                if (wifiConfiguration.fromWifiNetworkSuggestion) {
                    this.mPasspointWifiEntryCache.put(uniqueIdToPasspointWifiEntryKey, new PasspointWifiEntry(this.mContext, this.mMainHandler, wifiConfiguration, this.mWifiManager, this.mWifiNetworkScoreCache, false));
                } else if (this.mPasspointConfigCache.containsKey(uniqueIdToPasspointWifiEntryKey)) {
                    Map<String, PasspointWifiEntry> map = this.mPasspointWifiEntryCache;
                    Context context = this.mContext;
                    Handler handler = this.mMainHandler;
                    Handler handler2 = handler;
                    map.put(uniqueIdToPasspointWifiEntryKey, new PasspointWifiEntry(context, handler2, this.mPasspointConfigCache.get(uniqueIdToPasspointWifiEntryKey), this.mWifiManager, this.mWifiNetworkScoreCache, false));
                }
            }
            this.mPasspointWifiEntryCache.get(uniqueIdToPasspointWifiEntryKey).updateScanResultInfo(wifiConfiguration, list3, list4);
        }
        this.mPasspointWifiEntryCache.entrySet().removeIf(new WifiPickerTracker$$ExternalSyntheticLambda20(treeSet));
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$updatePasspointWifiEntryScans$18(Set set, Map.Entry entry) {
        return ((PasspointWifiEntry) entry.getValue()).getLevel() == -1 || (!set.contains(entry.getKey()) && ((PasspointWifiEntry) entry.getValue()).getConnectedState() == 0);
    }

    private void updateOsuWifiEntryScans(List<ScanResult> list) {
        Preconditions.checkNotNull(list, "Scan Result list should not be null!");
        Map matchingOsuProviders = this.mWifiManager.getMatchingOsuProviders(list);
        Map matchingPasspointConfigsForOsuProviders = this.mWifiManager.getMatchingPasspointConfigsForOsuProviders(matchingOsuProviders.keySet());
        for (OsuWifiEntry next : this.mOsuWifiEntryCache.values()) {
            next.updateScanResultInfo((List) matchingOsuProviders.remove(next.getOsuProvider()));
        }
        for (OsuProvider osuProvider : matchingOsuProviders.keySet()) {
            OsuWifiEntry osuWifiEntry = new OsuWifiEntry(this.mContext, this.mMainHandler, osuProvider, this.mWifiManager, this.mWifiNetworkScoreCache, false);
            osuWifiEntry.updateScanResultInfo((List) matchingOsuProviders.get(osuProvider));
            this.mOsuWifiEntryCache.put(OsuWifiEntry.osuProviderToOsuWifiEntryKey(osuProvider), osuWifiEntry);
        }
        this.mOsuWifiEntryCache.values().forEach(new WifiPickerTracker$$ExternalSyntheticLambda4(this, matchingPasspointConfigsForOsuProviders));
        this.mOsuWifiEntryCache.entrySet().removeIf(WifiPickerTracker$$ExternalSyntheticLambda33.INSTANCE);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$updateOsuWifiEntryScans$19(Map map, OsuWifiEntry osuWifiEntry) {
        PasspointConfiguration passpointConfiguration = (PasspointConfiguration) map.get(osuWifiEntry.getOsuProvider());
        if (passpointConfiguration == null) {
            osuWifiEntry.setAlreadyProvisioned(false);
            return;
        }
        osuWifiEntry.setAlreadyProvisioned(true);
        PasspointWifiEntry passpointWifiEntry = this.mPasspointWifiEntryCache.get(PasspointWifiEntry.uniqueIdToPasspointWifiEntryKey(passpointConfiguration.getUniqueId()));
        if (passpointWifiEntry != null) {
            passpointWifiEntry.setOsuWifiEntry(osuWifiEntry);
        }
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$updateOsuWifiEntryScans$20(Map.Entry entry) {
        return ((OsuWifiEntry) entry.getValue()).getLevel() == -1;
    }

    private void updateNetworkRequestEntryScans(List<ScanResult> list) {
        Preconditions.checkNotNull(list, "Scan Result list should not be null!");
        NetworkRequestEntry networkRequestEntry = this.mNetworkRequestEntry;
        if (networkRequestEntry != null) {
            StandardWifiEntry.ScanResultKey scanResultKey = networkRequestEntry.getStandardWifiEntryKey().getScanResultKey();
            this.mNetworkRequestEntry.updateScanResultInfo((List) list.stream().filter(new WifiPickerTracker$$ExternalSyntheticLambda13(scanResultKey)).collect(Collectors.toList()));
        }
    }

    private void conditionallyUpdateScanResults(boolean z) {
        if (this.mWifiManager.getWifiState() == 1) {
            updateStandardWifiEntryScans(Collections.emptyList());
            updateSuggestedWifiEntryScans(Collections.emptyList());
            updatePasspointWifiEntryScans(Collections.emptyList());
            updateOsuWifiEntryScans(Collections.emptyList());
            updateNetworkRequestEntryScans(Collections.emptyList());
            updateContextualWifiEntryScans(Collections.emptyList());
            return;
        }
        long j = this.mMaxScanAgeMillis;
        if (z) {
            this.mScanResultUpdater.update(this.mWifiManager.getScanResults());
        } else {
            j += this.mScanIntervalMillis;
        }
        List<ScanResult> scanResults = this.mScanResultUpdater.getScanResults(j);
        updateStandardWifiEntryScans(scanResults);
        updateSuggestedWifiEntryScans(scanResults);
        updatePasspointWifiEntryScans(scanResults);
        updateOsuWifiEntryScans(scanResults);
        updateNetworkRequestEntryScans(scanResults);
        updateContextualWifiEntryScans(scanResults);
    }

    private void updateWifiConfigurations(List<WifiConfiguration> list) {
        Preconditions.checkNotNull(list, "Config list should not be null!");
        this.mStandardWifiConfigCache.clear();
        this.mSuggestedConfigCache.clear();
        this.mNetworkRequestConfigCache.clear();
        new ArrayList();
        for (WifiConfiguration next : list) {
            if (!next.carrierMerged) {
                StandardWifiEntry.StandardWifiEntryKey standardWifiEntryKey = new StandardWifiEntry.StandardWifiEntryKey(next, true);
                if (next.isPasspoint()) {
                    this.mPasspointWifiConfigCache.put(next.networkId, next);
                } else if (next.fromWifiNetworkSuggestion) {
                    if (!this.mSuggestedConfigCache.containsKey(standardWifiEntryKey)) {
                        this.mSuggestedConfigCache.put(standardWifiEntryKey, new ArrayList());
                    }
                    this.mSuggestedConfigCache.get(standardWifiEntryKey).add(next);
                } else if (next.fromWifiNetworkSpecifier) {
                    if (!this.mNetworkRequestConfigCache.containsKey(standardWifiEntryKey)) {
                        this.mNetworkRequestConfigCache.put(standardWifiEntryKey, new ArrayList());
                    }
                    this.mNetworkRequestConfigCache.get(standardWifiEntryKey).add(next);
                } else {
                    if (!this.mStandardWifiConfigCache.containsKey(standardWifiEntryKey)) {
                        this.mStandardWifiConfigCache.put(standardWifiEntryKey, new ArrayList());
                    }
                    this.mStandardWifiConfigCache.get(standardWifiEntryKey).add(next);
                }
            }
        }
        this.mNumSavedNetworks = (int) this.mStandardWifiConfigCache.values().stream().flatMap(WifiPickerTracker$$ExternalSyntheticLambda11.INSTANCE).filter(WifiPickerTracker$$ExternalSyntheticLambda23.INSTANCE).map(WifiPickerTracker$$ExternalSyntheticLambda7.INSTANCE).distinct().count();
        this.mStandardWifiEntryCache.forEach(new WifiPickerTracker$$ExternalSyntheticLambda3(this));
        this.mSuggestedWifiEntryCache.removeIf(new WifiPickerTracker$$ExternalSyntheticLambda14(this));
        NetworkRequestEntry networkRequestEntry = this.mNetworkRequestEntry;
        if (networkRequestEntry != null) {
            networkRequestEntry.updateConfig(this.mNetworkRequestConfigCache.get(networkRequestEntry.getStandardWifiEntryKey()));
        }
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$updateWifiConfigurations$22(WifiConfiguration wifiConfiguration) {
        return !wifiConfiguration.isEphemeral();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$updateWifiConfigurations$24(StandardWifiEntry standardWifiEntry) {
        standardWifiEntry.updateConfig(this.mStandardWifiConfigCache.get(standardWifiEntry.getStandardWifiEntryKey()));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ boolean lambda$updateWifiConfigurations$25(StandardWifiEntry standardWifiEntry) {
        standardWifiEntry.updateConfig(this.mSuggestedConfigCache.get(standardWifiEntry.getStandardWifiEntryKey()));
        return !standardWifiEntry.isSuggestion();
    }

    private void updatePasspointConfigurations(List<PasspointConfiguration> list) {
        Preconditions.checkNotNull(list, "Config list should not be null!");
        this.mPasspointConfigCache.clear();
        this.mPasspointConfigCache.putAll((Map) list.stream().collect(Collectors.toMap(WifiPickerTracker$$ExternalSyntheticLambda9.INSTANCE, Function.identity())));
        this.mPasspointWifiEntryCache.entrySet().removeIf(new WifiPickerTracker$$ExternalSyntheticLambda17(this));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ boolean lambda$updatePasspointConfigurations$27(Map.Entry entry) {
        PasspointWifiEntry passpointWifiEntry = (PasspointWifiEntry) entry.getValue();
        passpointWifiEntry.updatePasspointConfig(this.mPasspointConfigCache.get(passpointWifiEntry.getKey()));
        return !passpointWifiEntry.isSubscription() && !passpointWifiEntry.isSuggestion();
    }

    private void updateConnectionInfo(WifiInfo wifiInfo, NetworkInfo networkInfo) {
        for (StandardWifiEntry updateConnectionInfo : this.mStandardWifiEntryCache) {
            updateConnectionInfo.updateConnectionInfo(wifiInfo, networkInfo);
        }
        for (StandardWifiEntry updateConnectionInfo2 : this.mSuggestedWifiEntryCache) {
            updateConnectionInfo2.updateConnectionInfo(wifiInfo, networkInfo);
        }
        for (PasspointWifiEntry updateConnectionInfo3 : this.mPasspointWifiEntryCache.values()) {
            updateConnectionInfo3.updateConnectionInfo(wifiInfo, networkInfo);
        }
        for (OsuWifiEntry updateConnectionInfo4 : this.mOsuWifiEntryCache.values()) {
            updateConnectionInfo4.updateConnectionInfo(wifiInfo, networkInfo);
        }
        NetworkRequestEntry networkRequestEntry = this.mNetworkRequestEntry;
        if (networkRequestEntry != null) {
            networkRequestEntry.updateConnectionInfo(wifiInfo, networkInfo);
        }
        updateNetworkRequestEntryConnectionInfo(wifiInfo, networkInfo);
        MergedCarrierEntry mergedCarrierEntry = this.mMergedCarrierEntry;
        if (mergedCarrierEntry != null) {
            mergedCarrierEntry.updateConnectionInfo(wifiInfo, networkInfo);
        }
        conditionallyCreateConnectedStandardWifiEntry(wifiInfo, networkInfo);
        conditionallyCreateConnectedSuggestedWifiEntry(wifiInfo, networkInfo);
        conditionallyCreateConnectedPasspointWifiEntry(wifiInfo, networkInfo);
    }

    private void updateNetworkRequestEntryConnectionInfo(WifiInfo wifiInfo, NetworkInfo networkInfo) {
        ArrayList arrayList = new ArrayList();
        if (wifiInfo != null) {
            int i = 0;
            while (true) {
                if (i >= this.mNetworkRequestConfigCache.size()) {
                    break;
                }
                List valueAt = this.mNetworkRequestConfigCache.valueAt(i);
                if (!valueAt.isEmpty() && ((WifiConfiguration) valueAt.get(0)).networkId == wifiInfo.getNetworkId()) {
                    arrayList.addAll(valueAt);
                    break;
                }
                i++;
            }
        }
        if (arrayList.isEmpty()) {
            this.mNetworkRequestEntry = null;
            return;
        }
        StandardWifiEntry.StandardWifiEntryKey standardWifiEntryKey = new StandardWifiEntry.StandardWifiEntryKey((WifiConfiguration) arrayList.get(0));
        NetworkRequestEntry networkRequestEntry = this.mNetworkRequestEntry;
        if (networkRequestEntry == null || !networkRequestEntry.getStandardWifiEntryKey().equals(standardWifiEntryKey)) {
            NetworkRequestEntry networkRequestEntry2 = new NetworkRequestEntry(this.mContext, this.mMainHandler, standardWifiEntryKey, this.mWifiManager, this.mWifiNetworkScoreCache, false);
            this.mNetworkRequestEntry = networkRequestEntry2;
            networkRequestEntry2.updateConfig(arrayList);
        }
        this.mNetworkRequestEntry.updateConnectionInfo(wifiInfo, networkInfo);
    }

    private void conditionallyCreateConnectedStandardWifiEntry(WifiInfo wifiInfo, NetworkInfo networkInfo) {
        if (wifiInfo != null && !wifiInfo.isPasspointAp() && !wifiInfo.isOsuAp()) {
            int networkId = wifiInfo.getNetworkId();
            for (List next : this.mStandardWifiConfigCache.values()) {
                if (next.stream().map(WifiPickerTracker$$ExternalSyntheticLambda8.INSTANCE).filter(new WifiPickerTracker$$ExternalSyntheticLambda12(networkId)).count() != 0) {
                    StandardWifiEntry.StandardWifiEntryKey standardWifiEntryKey = new StandardWifiEntry.StandardWifiEntryKey((WifiConfiguration) next.get(0), true);
                    for (StandardWifiEntry standardWifiEntryKey2 : this.mStandardWifiEntryCache) {
                        if (standardWifiEntryKey.equals(standardWifiEntryKey2.getStandardWifiEntryKey())) {
                            return;
                        }
                    }
                    StandardWifiEntry standardWifiEntry = new StandardWifiEntry(this.mContext, this.mMainHandler, standardWifiEntryKey, next, (List<ScanResult>) null, this.mWifiManager, this.mWifiNetworkScoreCache, false);
                    standardWifiEntry.updateConnectionInfo(wifiInfo, networkInfo);
                    this.mStandardWifiEntryCache.add(standardWifiEntry);
                    return;
                }
            }
        }
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$conditionallyCreateConnectedStandardWifiEntry$29(int i, Integer num) {
        return num.intValue() == i;
    }

    private void conditionallyCreateConnectedSuggestedWifiEntry(WifiInfo wifiInfo, NetworkInfo networkInfo) {
        if (wifiInfo != null && !wifiInfo.isPasspointAp() && !wifiInfo.isOsuAp()) {
            int networkId = wifiInfo.getNetworkId();
            for (List next : this.mSuggestedConfigCache.values()) {
                if (!next.isEmpty() && ((WifiConfiguration) next.get(0)).networkId == networkId) {
                    StandardWifiEntry.StandardWifiEntryKey standardWifiEntryKey = new StandardWifiEntry.StandardWifiEntryKey((WifiConfiguration) next.get(0), true);
                    for (StandardWifiEntry standardWifiEntryKey2 : this.mSuggestedWifiEntryCache) {
                        if (standardWifiEntryKey.equals(standardWifiEntryKey2.getStandardWifiEntryKey())) {
                            return;
                        }
                    }
                    StandardWifiEntry standardWifiEntry = new StandardWifiEntry(this.mContext, this.mMainHandler, standardWifiEntryKey, next, (List<ScanResult>) null, this.mWifiManager, this.mWifiNetworkScoreCache, false);
                    standardWifiEntry.updateConnectionInfo(wifiInfo, networkInfo);
                    this.mSuggestedWifiEntryCache.add(standardWifiEntry);
                    return;
                }
            }
        }
    }

    private void conditionallyCreateConnectedPasspointWifiEntry(WifiInfo wifiInfo, NetworkInfo networkInfo) {
        WifiConfiguration wifiConfiguration;
        PasspointWifiEntry passpointWifiEntry;
        if (wifiInfo != null && wifiInfo.isPasspointAp() && (wifiConfiguration = this.mPasspointWifiConfigCache.get(wifiInfo.getNetworkId())) != null) {
            if (!this.mPasspointWifiEntryCache.containsKey(PasspointWifiEntry.uniqueIdToPasspointWifiEntryKey(wifiConfiguration.getKey()))) {
                PasspointConfiguration passpointConfiguration = this.mPasspointConfigCache.get(PasspointWifiEntry.uniqueIdToPasspointWifiEntryKey(wifiConfiguration.getKey()));
                if (passpointConfiguration != null) {
                    passpointWifiEntry = new PasspointWifiEntry(this.mContext, this.mMainHandler, passpointConfiguration, this.mWifiManager, this.mWifiNetworkScoreCache, false);
                } else {
                    passpointWifiEntry = new PasspointWifiEntry(this.mContext, this.mMainHandler, wifiConfiguration, this.mWifiManager, this.mWifiNetworkScoreCache, false);
                }
                passpointWifiEntry.updateConnectionInfo(wifiInfo, networkInfo);
                this.mPasspointWifiEntryCache.put(passpointWifiEntry.getKey(), passpointWifiEntry);
            }
        }
    }

    private void notifyOnWifiEntriesChanged() {
        WifiPickerTrackerCallback wifiPickerTrackerCallback = this.mListener;
        if (wifiPickerTrackerCallback != null) {
            Handler handler = this.mMainHandler;
            Objects.requireNonNull(wifiPickerTrackerCallback);
            handler.post(new WifiPickerTracker$$ExternalSyntheticLambda2(wifiPickerTrackerCallback));
        }
    }

    private void notifyOnNumSavedNetworksChanged() {
        WifiPickerTrackerCallback wifiPickerTrackerCallback = this.mListener;
        if (wifiPickerTrackerCallback != null) {
            Handler handler = this.mMainHandler;
            Objects.requireNonNull(wifiPickerTrackerCallback);
            handler.post(new WifiPickerTracker$$ExternalSyntheticLambda0(wifiPickerTrackerCallback));
        }
    }

    private void notifyOnNumSavedSubscriptionsChanged() {
        WifiPickerTrackerCallback wifiPickerTrackerCallback = this.mListener;
        if (wifiPickerTrackerCallback != null) {
            Handler handler = this.mMainHandler;
            Objects.requireNonNull(wifiPickerTrackerCallback);
            handler.post(new WifiPickerTracker$$ExternalSyntheticLambda1(wifiPickerTrackerCallback));
        }
    }
}
