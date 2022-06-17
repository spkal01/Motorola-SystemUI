package com.android.systemui.moto;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.os.Build;
import android.telephony.CellSignalStrengthLte;
import android.telephony.SignalStrength;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.text.TextUtils;
import android.util.Log;
import com.android.systemui.Dependency;
import com.android.systemui.R$bool;
import com.android.systemui.R$integer;
import com.android.systemui.R$string;
import com.android.systemui.statusbar.policy.ConfigurationController;
import java.util.List;

public class NotifyLowSignalStrength {
    static final boolean DEBUG = (!Build.IS_USER);
    static NotifyLowSignalStrength sNotifyLowSignalStrength;
    int mCarrierDbmValue = 0;
    private int mCarrierDbmValueSlot1 = 0;
    private ConfigurationController.ConfigurationListener mConfigurationListener = new ConfigurationController.ConfigurationListener() {
        public void onOverlayChanged() {
            if (NotifyLowSignalStrength.DEBUG) {
                Log.i("NotifyLowSS", "onOverlayChanged");
            }
            NotifyLowSignalStrength notifyLowSignalStrength = NotifyLowSignalStrength.this;
            Context context = notifyLowSignalStrength.mContext;
            notifyLowSignalStrength.updateSource(context, 0, NotifyLowSignalStrength.getSubIdFromSlotId(context, 0));
            NotifyLowSignalStrength notifyLowSignalStrength2 = NotifyLowSignalStrength.this;
            Context context2 = notifyLowSignalStrength2.mContext;
            notifyLowSignalStrength2.updateSource(context2, 1, NotifyLowSignalStrength.getSubIdFromSlotId(context2, 1));
        }
    };
    Context mContext;
    Intent mLowSignalStrengthIntent = null;
    private Intent mLowSignalStrengthIntentSlot1 = null;
    int mPreviousDbmValue = 0;
    private int mPreviousDbmValueSlot1 = 0;
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            NotifyLowSignalStrength notifyLowSignalStrength = NotifyLowSignalStrength.this;
            notifyLowSignalStrength.debugLog("NotifyLowSS", "received broadcast " + action);
            if ("android.intent.action.SIM_STATE_CHANGED".equals(action)) {
                int intExtra = intent.getIntExtra("android.telephony.extra.SUBSCRIPTION_INDEX", -1);
                NotifyLowSignalStrength.this.updateSource(context, intent.getIntExtra("android.telephony.extra.SLOT_INDEX", -1), intExtra);
            }
        }
    };

    private NotifyLowSignalStrength(Context context) {
        init(context);
    }

    public static NotifyLowSignalStrength getInstance(Context context) {
        if (sNotifyLowSignalStrength == null) {
            sNotifyLowSignalStrength = new NotifyLowSignalStrength(context);
        }
        return sNotifyLowSignalStrength;
    }

    /* access modifiers changed from: private */
    public void updateSource(Context context, int i, int i2) {
        if (i == 0 || i == -1) {
            boolean z = getResources(context, i2).getBoolean(R$bool.config_notify_signal_strength_dropped_below_threshold);
            this.mCarrierDbmValue = getResources(context, i2).getInteger(R$integer.config_low_signal_threshold_val);
            String string = getResources(context, i2).getString(R$string.config_signal_strength_dropped_below_threshold_handler_package);
            if (z && this.mCarrierDbmValue != 0 && !TextUtils.isEmpty(string)) {
                Intent intent = new Intent("com.motorola.android.intent.action.SIGNAL_STRENGTH_DROPPED_BELOW_THRESHOLD");
                this.mLowSignalStrengthIntent = intent;
                intent.setPackage(string);
            }
            debugLog("NotifyLowSS", "isLowSSFeatureEnabled: " + z + ", mCarrierDbmValue: " + this.mCarrierDbmValue + ", mTargetPackage :" + string);
        } else if (i == 1) {
            boolean z2 = getResources(context, i2).getBoolean(R$bool.config_notify_signal_strength_dropped_below_threshold);
            this.mCarrierDbmValueSlot1 = getResources(context, i2).getInteger(R$integer.config_low_signal_threshold_val);
            String string2 = getResources(context, i2).getString(R$string.config_signal_strength_dropped_below_threshold_handler_package);
            if (z2 && this.mCarrierDbmValueSlot1 != 0 && !TextUtils.isEmpty(string2)) {
                Intent intent2 = new Intent("com.motorola.android.intent.action.SIGNAL_STRENGTH_DROPPED_BELOW_THRESHOLD");
                this.mLowSignalStrengthIntentSlot1 = intent2;
                intent2.setPackage(string2);
            }
            debugLog("NotifyLowSS", "isLowSSFeatureEnabledSlot1: " + z2 + ", mCarrierDbmValueSlot1: " + this.mCarrierDbmValueSlot1 + ", mTargetPkgSlot1: " + string2);
        }
    }

    private void init(Context context) {
        this.mContext = context;
        updateSource(context, 0, getSubIdFromSlotId(context, 0));
        updateSource(context, 1, getSubIdFromSlotId(context, 1));
        this.mContext.registerReceiver(this.mReceiver, new IntentFilter("android.intent.action.SIM_STATE_CHANGED"));
        ((ConfigurationController) Dependency.get(ConfigurationController.class)).addCallback(this.mConfigurationListener);
    }

    private boolean isLte(SignalStrength signalStrength) {
        List<CellSignalStrengthLte> cellSignalStrengths = signalStrength.getCellSignalStrengths(CellSignalStrengthLte.class);
        return cellSignalStrengths != null && cellSignalStrengths.size() > 0;
    }

    public void notifyLowSignalStrengthIfNeeded(SignalStrength signalStrength, int i, int i2) {
        if (signalStrength != null) {
            if (i2 == 0 && this.mLowSignalStrengthIntent != null) {
                debugLog("NotifyLowSS", "current dbmLevel :" + signalStrength.getDbm() + ", prev dbmLevel :" + this.mPreviousDbmValue + ", isLte: " + isLte(signalStrength) + ", Slot: " + i2);
                if (isLte(signalStrength)) {
                    int dbm = signalStrength.getDbm();
                    int i3 = this.mPreviousDbmValue;
                    int i4 = this.mCarrierDbmValue;
                    if (i3 >= i4 && dbm < i4) {
                        dumpLog(signalStrength, i2);
                        Log.i("NotifyLowSS", "notifying for low dbm : " + dbm);
                        this.mLowSignalStrengthIntent.putExtra("android.telephony.extra.SUBSCRIPTION_INDEX", i);
                        this.mContext.sendBroadcast(this.mLowSignalStrengthIntent);
                    }
                    this.mPreviousDbmValue = dbm;
                }
            }
            if (i2 == 1 && this.mLowSignalStrengthIntentSlot1 != null) {
                debugLog("NotifyLowSS", "current dbmLevel :" + signalStrength.getDbm() + ", prev dbmLevel :" + this.mPreviousDbmValueSlot1 + ", isLte: " + isLte(signalStrength) + ", Slot: " + i2);
                if (isLte(signalStrength)) {
                    int dbm2 = signalStrength.getDbm();
                    int i5 = this.mPreviousDbmValueSlot1;
                    int i6 = this.mCarrierDbmValueSlot1;
                    if (i5 >= i6 && dbm2 < i6) {
                        dumpLog(signalStrength, i2);
                        Log.i("NotifyLowSS", "notifying for low dbm : " + dbm2);
                        this.mLowSignalStrengthIntentSlot1.putExtra("android.telephony.extra.SUBSCRIPTION_INDEX", i);
                        this.mContext.sendBroadcast(this.mLowSignalStrengthIntentSlot1);
                    }
                    this.mPreviousDbmValueSlot1 = dbm2;
                }
            }
        }
    }

    /* access modifiers changed from: private */
    public void debugLog(String str, String str2) {
        if (DEBUG) {
            Log.d(str, str2);
        }
    }

    private void dumpLog(SignalStrength signalStrength, int i) {
        int i2;
        if (DEBUG) {
            Log.d("NotifyLowSS", "signalstrength current value :" + signalStrength.toString());
            StringBuilder sb = new StringBuilder();
            sb.append("mCarrierDbmValue :");
            if (i == 0) {
                i2 = this.mCarrierDbmValue;
            } else {
                i2 = this.mCarrierDbmValueSlot1;
            }
            sb.append(i2);
            Log.d("NotifyLowSS", sb.toString());
            StringBuilder sb2 = new StringBuilder();
            sb2.append("check for signal strength.., mPrevSS :");
            sb2.append(i == 0 ? this.mPreviousDbmValue : this.mPreviousDbmValueSlot1);
            Log.d("NotifyLowSS", sb2.toString());
            String str = null;
            if (i == 0) {
                StringBuilder sb3 = new StringBuilder();
                sb3.append("Intent details: ");
                Intent intent = this.mLowSignalStrengthIntent;
                if (intent != null) {
                    str = intent.toString();
                }
                sb3.append(str);
                Log.d("NotifyLowSS", sb3.toString());
            } else if (i == 1) {
                StringBuilder sb4 = new StringBuilder();
                sb4.append("Intent details: ");
                Intent intent2 = this.mLowSignalStrengthIntentSlot1;
                if (intent2 != null) {
                    str = intent2.toString();
                }
                sb4.append(str);
                Log.d("NotifyLowSS", sb4.toString());
            }
        }
    }

    /* access modifiers changed from: private */
    public static int getSubIdFromSlotId(Context context, int i) {
        SubscriptionInfo activeSubscriptionInfoForSimSlotIndex;
        if (i == -1 || (activeSubscriptionInfoForSimSlotIndex = ((SubscriptionManager) context.getSystemService(SubscriptionManager.class)).getActiveSubscriptionInfoForSimSlotIndex(i)) == null) {
            return -1;
        }
        return activeSubscriptionInfoForSimSlotIndex.getSubscriptionId();
    }

    public static Resources getResources(Context context, int i) {
        Resources resourcesForSubId = i != -1 ? SubscriptionManager.getResourcesForSubId(context, i) : null;
        return resourcesForSubId == null ? context.getResources() : resourcesForSubId;
    }
}
