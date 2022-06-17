package com.android.wifitrackerlib;

import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.hotspot2.PasspointConfiguration;
import android.os.Handler;
import android.util.Log;
import android.util.Pair;
import androidx.core.util.Preconditions;
import com.android.wifitrackerlib.BaseWifiTracker;
import com.android.wifitrackerlib.StandardWifiEntry;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.stream.Collectors;

public class SavedNetworkTracker extends BaseWifiTracker {
    private final SavedNetworkTrackerCallback mListener;
    private final Object mLock;
    private final Map<String, PasspointWifiEntry> mPasspointWifiEntryCache;
    private final List<WifiEntry> mSavedWifiEntries;
    private final List<StandardWifiEntry> mStandardWifiEntryCache;
    private final List<WifiEntry> mSubscriptionWifiEntries;

    public interface SavedNetworkTrackerCallback extends BaseWifiTracker.BaseWifiTrackerCallback {
        void onSavedWifiEntriesChanged();

        void onSubscriptionWifiEntriesChanged();
    }

    /* access modifiers changed from: protected */
    public void handleOnStart() {
        updateStandardWifiEntryConfigs(this.mWifiManager.getConfiguredNetworks());
        updatePasspointWifiEntryConfigs(this.mWifiManager.getPasspointConfigurations());
        conditionallyUpdateScanResults(true);
        updateSavedWifiEntries();
        updateSubscriptionWifiEntries();
    }

    /* access modifiers changed from: protected */
    public void handleWifiStateChangedAction() {
        conditionallyUpdateScanResults(true);
        updateSavedWifiEntries();
        updateSubscriptionWifiEntries();
    }

    /* access modifiers changed from: protected */
    public void handleScanResultsAvailableAction(Intent intent) {
        Preconditions.checkNotNull(intent, "Intent cannot be null!");
        conditionallyUpdateScanResults(intent.getBooleanExtra("resultsUpdated", true));
        updateSavedWifiEntries();
        updateSubscriptionWifiEntries();
    }

    /* access modifiers changed from: protected */
    public void handleConfiguredNetworksChangedAction(Intent intent) {
        Preconditions.checkNotNull(intent, "Intent cannot be null!");
        updateStandardWifiEntryConfigs(this.mWifiManager.getConfiguredNetworks());
        updatePasspointWifiEntryConfigs(this.mWifiManager.getPasspointConfigurations());
        updateSavedWifiEntries();
        updateSubscriptionWifiEntries();
    }

    /* access modifiers changed from: protected */
    public void handleNetworkScoreCacheUpdated() {
        for (StandardWifiEntry onScoreCacheUpdated : this.mStandardWifiEntryCache) {
            onScoreCacheUpdated.onScoreCacheUpdated();
        }
        for (PasspointWifiEntry onScoreCacheUpdated2 : this.mPasspointWifiEntryCache.values()) {
            onScoreCacheUpdated2.onScoreCacheUpdated();
        }
    }

    private void updateSavedWifiEntries() {
        synchronized (this.mLock) {
            this.mSavedWifiEntries.clear();
            this.mSavedWifiEntries.addAll(this.mStandardWifiEntryCache);
            Collections.sort(this.mSavedWifiEntries);
            if (BaseWifiTracker.isVerboseLoggingEnabled()) {
                Log.v("SavedNetworkTracker", "Updated SavedWifiEntries: " + Arrays.toString(this.mSavedWifiEntries.toArray()));
            }
        }
        notifyOnSavedWifiEntriesChanged();
    }

    private void updateSubscriptionWifiEntries() {
        synchronized (this.mLock) {
            this.mSubscriptionWifiEntries.clear();
            this.mSubscriptionWifiEntries.addAll(this.mPasspointWifiEntryCache.values());
            Collections.sort(this.mSubscriptionWifiEntries);
            if (BaseWifiTracker.isVerboseLoggingEnabled()) {
                Log.v("SavedNetworkTracker", "Updated SubscriptionWifiEntries: " + Arrays.toString(this.mSubscriptionWifiEntries.toArray()));
            }
        }
        notifyOnSubscriptionWifiEntriesChanged();
    }

    private void updateStandardWifiEntryScans(List<ScanResult> list) {
        Preconditions.checkNotNull(list, "Scan Result list should not be null!");
        this.mStandardWifiEntryCache.forEach(new SavedNetworkTracker$$ExternalSyntheticLambda2((Map) list.stream().collect(Collectors.groupingBy(SavedNetworkTracker$$ExternalSyntheticLambda4.INSTANCE))));
    }

    private void updatePasspointWifiEntryScans(List<ScanResult> list) {
        Preconditions.checkNotNull(list, "Scan Result list should not be null!");
        TreeSet treeSet = new TreeSet();
        for (Pair pair : this.mWifiManager.getAllMatchingWifiConfigs(list)) {
            WifiConfiguration wifiConfiguration = (WifiConfiguration) pair.first;
            String uniqueIdToPasspointWifiEntryKey = PasspointWifiEntry.uniqueIdToPasspointWifiEntryKey(wifiConfiguration.getKey());
            treeSet.add(uniqueIdToPasspointWifiEntryKey);
            if (this.mPasspointWifiEntryCache.containsKey(uniqueIdToPasspointWifiEntryKey)) {
                this.mPasspointWifiEntryCache.get(uniqueIdToPasspointWifiEntryKey).updateScanResultInfo(wifiConfiguration, (List) ((Map) pair.second).get(0), (List) ((Map) pair.second).get(1));
            }
        }
        for (PasspointWifiEntry next : this.mPasspointWifiEntryCache.values()) {
            if (!treeSet.contains(next.getKey())) {
                next.updateScanResultInfo((WifiConfiguration) null, (List<ScanResult>) null, (List<ScanResult>) null);
            }
        }
    }

    private void conditionallyUpdateScanResults(boolean z) {
        if (this.mWifiManager.getWifiState() == 1) {
            updateStandardWifiEntryScans(Collections.emptyList());
            updatePasspointWifiEntryScans(Collections.emptyList());
            return;
        }
        long j = this.mMaxScanAgeMillis;
        if (z) {
            this.mScanResultUpdater.update(this.mWifiManager.getScanResults());
        } else {
            j += this.mScanIntervalMillis;
        }
        updateStandardWifiEntryScans(this.mScanResultUpdater.getScanResults(j));
        updatePasspointWifiEntryScans(this.mScanResultUpdater.getScanResults(j));
    }

    private void updateStandardWifiEntryConfigs(List<WifiConfiguration> list) {
        Preconditions.checkNotNull(list, "Config list should not be null!");
        Map map = (Map) list.stream().filter(SavedNetworkTracker$$ExternalSyntheticLambda8.INSTANCE).collect(Collectors.groupingBy(SavedNetworkTracker$$ExternalSyntheticLambda5.INSTANCE));
        this.mStandardWifiEntryCache.removeIf(new SavedNetworkTracker$$ExternalSyntheticLambda6(map));
        for (StandardWifiEntry.StandardWifiEntryKey standardWifiEntryKey : map.keySet()) {
            this.mStandardWifiEntryCache.add(new StandardWifiEntry(this.mContext, this.mMainHandler, standardWifiEntryKey, (List) map.get(standardWifiEntryKey), (List<ScanResult>) null, this.mWifiManager, this.mWifiNetworkScoreCache, true));
        }
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$updateStandardWifiEntryConfigs$1(WifiConfiguration wifiConfiguration) {
        return !wifiConfiguration.carrierMerged;
    }

    private void updatePasspointWifiEntryConfigs(List<PasspointConfiguration> list) {
        Preconditions.checkNotNull(list, "Config list should not be null!");
        Map map = (Map) list.stream().collect(Collectors.toMap(SavedNetworkTracker$$ExternalSyntheticLambda3.INSTANCE, Function.identity()));
        this.mPasspointWifiEntryCache.entrySet().removeIf(new SavedNetworkTracker$$ExternalSyntheticLambda7(map));
        for (String str : map.keySet()) {
            this.mPasspointWifiEntryCache.put(str, new PasspointWifiEntry(this.mContext, this.mMainHandler, (PasspointConfiguration) map.get(str), this.mWifiManager, this.mWifiNetworkScoreCache, true));
        }
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$updatePasspointWifiEntryConfigs$4(Map map, Map.Entry entry) {
        PasspointWifiEntry passpointWifiEntry = (PasspointWifiEntry) entry.getValue();
        PasspointConfiguration passpointConfiguration = (PasspointConfiguration) map.remove(passpointWifiEntry.getKey());
        if (passpointConfiguration == null) {
            return true;
        }
        passpointWifiEntry.updatePasspointConfig(passpointConfiguration);
        return false;
    }

    private void notifyOnSavedWifiEntriesChanged() {
        SavedNetworkTrackerCallback savedNetworkTrackerCallback = this.mListener;
        if (savedNetworkTrackerCallback != null) {
            Handler handler = this.mMainHandler;
            Objects.requireNonNull(savedNetworkTrackerCallback);
            handler.post(new SavedNetworkTracker$$ExternalSyntheticLambda0(savedNetworkTrackerCallback));
        }
    }

    private void notifyOnSubscriptionWifiEntriesChanged() {
        SavedNetworkTrackerCallback savedNetworkTrackerCallback = this.mListener;
        if (savedNetworkTrackerCallback != null) {
            Handler handler = this.mMainHandler;
            Objects.requireNonNull(savedNetworkTrackerCallback);
            handler.post(new SavedNetworkTracker$$ExternalSyntheticLambda1(savedNetworkTrackerCallback));
        }
    }
}
