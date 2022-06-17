package com.android.settingslib.net;

import android.app.usage.NetworkStats;
import android.app.usage.NetworkStatsManager;
import android.content.Context;
import android.net.INetworkStatsService;
import android.net.NetworkPolicy;
import android.net.NetworkPolicyManager;
import android.net.NetworkTemplate;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.text.format.DateUtils;
import android.util.Log;
import android.util.Range;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.util.ArrayUtils;
import com.motorola.android.content.MSimContext;
import java.time.ZonedDateTime;
import java.util.Formatter;
import java.util.Iterator;
import java.util.Locale;

public class DataUsageController {
    private static final boolean DEBUG = Log.isLoggable("DataUsageController", 3);
    private static final StringBuilder PERIOD_BUILDER;
    private static final Formatter PERIOD_FORMATTER;
    private Callback mCallback;
    private final Context mContext;
    private NetworkNameProvider mNetworkController;
    private final NetworkStatsManager mNetworkStatsManager;
    private final NetworkPolicyManager mPolicyManager;
    private final INetworkStatsService mStatsService = INetworkStatsService.Stub.asInterface(ServiceManager.getService("netstats"));
    private int mSubscriptionId;

    public interface Callback {
        void onMobileDataEnabled(boolean z);
    }

    public static class DataUsageInfo {
        public String carrier;
        public long cycleEnd;
        public long cycleStart;
        public long limitLevel;
        public String period;
        public long startDate;
        public long usageLevel;
        public long warningLevel;
    }

    public interface NetworkNameProvider {
        String getMobileDataNetworkName();
    }

    static {
        StringBuilder sb = new StringBuilder(50);
        PERIOD_BUILDER = sb;
        PERIOD_FORMATTER = new Formatter(sb, Locale.getDefault());
    }

    public DataUsageController(Context context) {
        this.mContext = context;
        this.mPolicyManager = NetworkPolicyManager.from(context);
        this.mNetworkStatsManager = (NetworkStatsManager) context.getSystemService(NetworkStatsManager.class);
        this.mSubscriptionId = -1;
    }

    public void setNetworkController(NetworkNameProvider networkNameProvider) {
        this.mNetworkController = networkNameProvider;
    }

    public long getDefaultWarningLevel() {
        return ((long) this.mContext.getResources().getInteger(17694997)) * 1048576;
    }

    public void setCallback(Callback callback) {
        this.mCallback = callback;
    }

    private DataUsageInfo warn(String str) {
        Log.w("DataUsageController", "Failed to get data usage, " + str);
        return null;
    }

    public DataUsageInfo getDataUsageInfo() {
        return getDataUsageInfo(DataUsageUtils.getMobileTemplate(this.mContext, this.mSubscriptionId));
    }

    public DataUsageInfo getDataUsageInfo(NetworkTemplate networkTemplate) {
        return getDataUsageInfo(networkTemplate, (NetworkPolicy) null);
    }

    public DataUsageInfo getDataUsageInfo(NetworkTemplate networkTemplate, NetworkPolicy networkPolicy) {
        long j;
        if (networkPolicy == null) {
            networkPolicy = findNetworkPolicy(networkTemplate);
        }
        long currentTimeMillis = System.currentTimeMillis();
        Iterator cycleIterator = networkPolicy != null ? networkPolicy.cycleIterator() : null;
        if (cycleIterator == null || !cycleIterator.hasNext()) {
            j = currentTimeMillis - 2419200000L;
        } else {
            Range range = (Range) cycleIterator.next();
            long epochMilli = ((ZonedDateTime) range.getLower()).toInstant().toEpochMilli();
            currentTimeMillis = ((ZonedDateTime) range.getUpper()).toInstant().toEpochMilli();
            j = epochMilli;
        }
        long usageLevel = getUsageLevel(networkTemplate, j, currentTimeMillis);
        long j2 = 0;
        if (usageLevel < 0) {
            return warn("no entry data");
        }
        DataUsageInfo dataUsageInfo = new DataUsageInfo();
        dataUsageInfo.startDate = j;
        dataUsageInfo.usageLevel = usageLevel;
        dataUsageInfo.period = formatDateRange(j, currentTimeMillis);
        dataUsageInfo.cycleStart = j;
        dataUsageInfo.cycleEnd = currentTimeMillis;
        if (networkPolicy != null) {
            long j3 = networkPolicy.limitBytes;
            if (j3 <= 0) {
                j3 = 0;
            }
            dataUsageInfo.limitLevel = j3;
            long j4 = networkPolicy.warningBytes;
            if (j4 > 0) {
                j2 = j4;
            }
            dataUsageInfo.warningLevel = j2;
        } else {
            dataUsageInfo.warningLevel = getDefaultWarningLevel();
        }
        NetworkNameProvider networkNameProvider = this.mNetworkController;
        if (networkNameProvider != null) {
            dataUsageInfo.carrier = networkNameProvider.getMobileDataNetworkName();
        }
        return dataUsageInfo;
    }

    private long getUsageLevel(NetworkTemplate networkTemplate, long j, long j2) {
        try {
            NetworkStats.Bucket querySummaryForDevice = this.mNetworkStatsManager.querySummaryForDevice(networkTemplate, j, j2);
            if (querySummaryForDevice != null) {
                return querySummaryForDevice.getRxBytes() + querySummaryForDevice.getTxBytes();
            }
            Log.w("DataUsageController", "Failed to get data usage, no entry data");
            return -1;
        } catch (RemoteException unused) {
            Log.w("DataUsageController", "Failed to get data usage, remote call failed");
            return -1;
        }
    }

    private NetworkPolicy findNetworkPolicy(NetworkTemplate networkTemplate) {
        NetworkPolicy[] networkPolicies;
        NetworkPolicyManager networkPolicyManager = this.mPolicyManager;
        if (networkPolicyManager == null || networkTemplate == null || (networkPolicies = networkPolicyManager.getNetworkPolicies()) == null) {
            return null;
        }
        for (NetworkPolicy networkPolicy : networkPolicies) {
            if (networkPolicy != null && networkTemplate.equals(networkPolicy.template)) {
                return networkPolicy;
            }
        }
        return null;
    }

    @VisibleForTesting
    public TelephonyManager getTelephonyManager() {
        int i = this.mSubscriptionId;
        if (!SubscriptionManager.isValidSubscriptionId(i)) {
            i = SubscriptionManager.getDefaultDataSubscriptionId();
        }
        if (!SubscriptionManager.isValidSubscriptionId(i)) {
            int[] activeSubscriptionIdList = SubscriptionManager.from(this.mContext).getActiveSubscriptionIdList();
            if (!ArrayUtils.isEmpty(activeSubscriptionIdList)) {
                i = activeSubscriptionIdList[0];
            }
        }
        return ((TelephonyManager) this.mContext.getSystemService(TelephonyManager.class)).createForSubscriptionId(i);
    }

    public void setMobileDataEnabled(boolean z) {
        Log.d("DataUsageController", "setMobileDataEnabled: enabled=" + z);
        getTelephonyManager().setDataEnabled(z);
        Callback callback = this.mCallback;
        if (callback != null) {
            callback.onMobileDataEnabled(z);
        }
    }

    public boolean isMobileDataSupported() {
        return getTelephonyManager().isDataCapable() && getTelephonyManager().getSimState() == 5;
    }

    public boolean isMobileDataSupported(int i) {
        return getTelephonyManager().isDataCapable() && getTelephonyManager().getSimState(SubscriptionManager.getSlotIndex(i)) == 5;
    }

    public boolean isMobileDataEnabled() {
        return getTelephonyManager().isDataEnabled();
    }

    public boolean isMobileDataEnabled(int i) {
        return getTelephonyManager().getDataEnabled(i);
    }

    private String formatDateRange(long j, long j2) {
        String formatter;
        StringBuilder sb = PERIOD_BUILDER;
        synchronized (sb) {
            sb.setLength(0);
            formatter = DateUtils.formatDateRange(this.mContext, PERIOD_FORMATTER, j, j2, 65552, (String) null).toString();
        }
        return formatter;
    }

    public static boolean isDisableMobileDataSupported(Context context, int i) {
        if (TelephonyManager.from(context).getPhoneCount() == 1) {
            return true ^ new MSimContext(context).getResources(i).getBoolean(17891606);
        }
        return true;
    }
}
