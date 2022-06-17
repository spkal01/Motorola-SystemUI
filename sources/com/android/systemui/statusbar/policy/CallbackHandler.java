package com.android.systemui.statusbar.policy;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.telephony.ServiceState;
import android.telephony.SubscriptionInfo;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.os.SomeArgs;
import com.android.systemui.moto.NetworkStateTracker;
import com.android.systemui.statusbar.policy.NetworkController;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CallbackHandler extends Handler implements NetworkController.EmergencyListener, NetworkController.SignalCallback {
    private static final SimpleDateFormat SSDF = new SimpleDateFormat("MM-dd HH:mm:ss.SSS");
    private ArrayList<NetworkStateTracker.PanelCarrierLabelListener> mCarrierListeners = new ArrayList<>();
    private final ArrayList<NetworkStateTracker.DefaultDataSubscriptionListener> mDefaultDataSubListeners = new ArrayList<>();
    private final ArrayList<NetworkController.EmergencyListener> mEmergencyListeners = new ArrayList<>();
    private final ArrayList<NetworkStateTracker.EriSoundListener> mEriSoundListeners = new ArrayList<>();
    private final String[] mHistory = new String[64];
    private int mHistoryIndex;
    private String mLastCallback;
    private ArrayList<NetworkStateTracker.ShortFormLabelListener> mShortFormLabelListeners = new ArrayList<>();
    private final ArrayList<NetworkController.SignalCallback> mSignalCallbacks = new ArrayList<>();

    @VisibleForTesting
    CallbackHandler(Looper looper) {
        super(looper);
    }

    public void handleMessage(Message message) {
        switch (message.what) {
            case 0:
                Iterator<NetworkController.EmergencyListener> it = this.mEmergencyListeners.iterator();
                while (it.hasNext()) {
                    it.next().setEmergencyCallsOnly(message.arg1 != 0);
                }
                return;
            case 1:
                Iterator<NetworkController.SignalCallback> it2 = this.mSignalCallbacks.iterator();
                while (it2.hasNext()) {
                    it2.next().setSubs((List) message.obj);
                }
                return;
            case 2:
                Iterator<NetworkController.SignalCallback> it3 = this.mSignalCallbacks.iterator();
                while (it3.hasNext()) {
                    it3.next().setNoSims(message.arg1 != 0, message.arg2 != 0);
                }
                return;
            case 3:
                Iterator<NetworkController.SignalCallback> it4 = this.mSignalCallbacks.iterator();
                while (it4.hasNext()) {
                    it4.next().setEthernetIndicators((NetworkController.IconState) message.obj);
                }
                return;
            case 4:
                Iterator<NetworkController.SignalCallback> it5 = this.mSignalCallbacks.iterator();
                while (it5.hasNext()) {
                    it5.next().setIsAirplaneMode((NetworkController.IconState) message.obj);
                }
                return;
            case 5:
                Iterator<NetworkController.SignalCallback> it6 = this.mSignalCallbacks.iterator();
                while (it6.hasNext()) {
                    it6.next().setMobileDataEnabled(message.arg1 != 0);
                }
                return;
            case 6:
                if (message.arg1 != 0) {
                    this.mEmergencyListeners.add((NetworkController.EmergencyListener) message.obj);
                    return;
                } else {
                    this.mEmergencyListeners.remove((NetworkController.EmergencyListener) message.obj);
                    return;
                }
            case 7:
                if (message.arg1 != 0) {
                    this.mSignalCallbacks.add((NetworkController.SignalCallback) message.obj);
                    return;
                } else {
                    this.mSignalCallbacks.remove((NetworkController.SignalCallback) message.obj);
                    return;
                }
            case 8:
                if (message.arg1 != 0) {
                    this.mCarrierListeners.add((NetworkStateTracker.PanelCarrierLabelListener) message.obj);
                    return;
                } else {
                    this.mCarrierListeners.remove((NetworkStateTracker.PanelCarrierLabelListener) message.obj);
                    return;
                }
            case 9:
                Iterator<NetworkStateTracker.PanelCarrierLabelListener> it7 = this.mCarrierListeners.iterator();
                while (it7.hasNext()) {
                    it7.next().updateLabel();
                }
                return;
            case 10:
                if (message.arg1 != 0) {
                    this.mShortFormLabelListeners.add((NetworkStateTracker.ShortFormLabelListener) message.obj);
                    return;
                } else {
                    this.mShortFormLabelListeners.remove((NetworkStateTracker.ShortFormLabelListener) message.obj);
                    return;
                }
            case 11:
                Iterator<NetworkStateTracker.ShortFormLabelListener> it8 = this.mShortFormLabelListeners.iterator();
                while (it8.hasNext()) {
                    it8.next().updateLabel();
                }
                return;
            case 12:
                if (message.arg1 != 0) {
                    this.mDefaultDataSubListeners.add((NetworkStateTracker.DefaultDataSubscriptionListener) message.obj);
                    return;
                } else {
                    this.mDefaultDataSubListeners.remove((NetworkStateTracker.DefaultDataSubscriptionListener) message.obj);
                    return;
                }
            case 13:
                Iterator<NetworkStateTracker.DefaultDataSubscriptionListener> it9 = this.mDefaultDataSubListeners.iterator();
                while (it9.hasNext()) {
                    it9.next().updateDefaultDataSubscription();
                }
                return;
            case 14:
                if (message.arg1 != 0) {
                    this.mEriSoundListeners.add((NetworkStateTracker.EriSoundListener) message.obj);
                    return;
                } else {
                    this.mEriSoundListeners.remove((NetworkStateTracker.EriSoundListener) message.obj);
                    return;
                }
            case 15:
                SomeArgs someArgs = (SomeArgs) message.obj;
                ServiceState serviceState = (ServiceState) someArgs.arg1;
                ServiceState serviceState2 = (ServiceState) someArgs.arg2;
                Iterator<NetworkStateTracker.EriSoundListener> it10 = this.mEriSoundListeners.iterator();
                while (it10.hasNext()) {
                    it10.next().updateEri(serviceState, serviceState2);
                }
                return;
            case 16:
                Iterator<NetworkStateTracker.EriSoundListener> it11 = this.mEriSoundListeners.iterator();
                while (it11.hasNext()) {
                    it11.next().playEriSoundAfterBoot();
                }
                return;
            default:
                return;
        }
    }

    public void setWifiIndicators(NetworkController.WifiIndicators wifiIndicators) {
        recordLastCallback(SSDF.format(Long.valueOf(System.currentTimeMillis())) + "," + wifiIndicators);
        post(new CallbackHandler$$ExternalSyntheticLambda2(this, wifiIndicators));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$setWifiIndicators$0(NetworkController.WifiIndicators wifiIndicators) {
        Iterator<NetworkController.SignalCallback> it = this.mSignalCallbacks.iterator();
        while (it.hasNext()) {
            it.next().setWifiIndicators(wifiIndicators);
        }
    }

    public void setMobileDataIndicators(NetworkController.MobileDataIndicators mobileDataIndicators) {
        recordLastCallback(SSDF.format(Long.valueOf(System.currentTimeMillis())) + "," + mobileDataIndicators);
        post(new CallbackHandler$$ExternalSyntheticLambda1(this, mobileDataIndicators));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$setMobileDataIndicators$1(NetworkController.MobileDataIndicators mobileDataIndicators) {
        Iterator<NetworkController.SignalCallback> it = this.mSignalCallbacks.iterator();
        while (it.hasNext()) {
            it.next().setMobileDataIndicators(mobileDataIndicators);
        }
    }

    public void setConnectivityStatus(boolean z, boolean z2, boolean z3) {
        String str = "setConnectivityStatus: " + "noDefaultNetwork=" + z + "," + "noValidatedNetwork=" + z2 + "," + "noNetworksAvailable=" + z3;
        if (!str.equals(this.mLastCallback)) {
            this.mLastCallback = str;
            recordLastCallback(SSDF.format(Long.valueOf(System.currentTimeMillis())) + "," + str + ",");
        }
        post(new CallbackHandler$$ExternalSyntheticLambda3(this, z, z2, z3));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$setConnectivityStatus$2(boolean z, boolean z2, boolean z3) {
        Iterator<NetworkController.SignalCallback> it = this.mSignalCallbacks.iterator();
        while (it.hasNext()) {
            it.next().setConnectivityStatus(z, z2, z3);
        }
    }

    public void setCallIndicator(NetworkController.IconState iconState, int i) {
        String str = "setCallIndicator: " + "statusIcon=" + iconState + "," + "subId=" + i;
        if (!str.equals(this.mLastCallback)) {
            this.mLastCallback = str;
            recordLastCallback(SSDF.format(Long.valueOf(System.currentTimeMillis())) + "," + str + ",");
        }
        post(new CallbackHandler$$ExternalSyntheticLambda0(this, iconState, i));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$setCallIndicator$3(NetworkController.IconState iconState, int i) {
        Iterator<NetworkController.SignalCallback> it = this.mSignalCallbacks.iterator();
        while (it.hasNext()) {
            it.next().setCallIndicator(iconState, i);
        }
    }

    public void setSubs(List<SubscriptionInfo> list) {
        StringBuilder sb = new StringBuilder();
        sb.append("setSubs: ");
        sb.append("subs=");
        sb.append(list == null ? "" : list.toString());
        String sb2 = sb.toString();
        if (!sb2.equals(this.mLastCallback)) {
            this.mLastCallback = sb2;
            recordLastCallback(SSDF.format(Long.valueOf(System.currentTimeMillis())) + "," + sb2 + ",");
        }
        obtainMessage(1, list).sendToTarget();
    }

    public void setNoSims(boolean z, boolean z2) {
        obtainMessage(2, z ? 1 : 0, z2 ? 1 : 0).sendToTarget();
    }

    public void setMobileDataEnabled(boolean z) {
        obtainMessage(5, z ? 1 : 0, 0).sendToTarget();
    }

    public void setEmergencyCallsOnly(boolean z) {
        obtainMessage(0, z ? 1 : 0, 0).sendToTarget();
    }

    public void setEthernetIndicators(NetworkController.IconState iconState) {
        recordLastCallback(SSDF.format(Long.valueOf(System.currentTimeMillis())) + "," + "setEthernetIndicators: " + "icon=" + iconState);
        obtainMessage(3, iconState).sendToTarget();
    }

    public void setIsAirplaneMode(NetworkController.IconState iconState) {
        String str = "setIsAirplaneMode: " + "icon=" + iconState;
        if (!str.equals(this.mLastCallback)) {
            this.mLastCallback = str;
            recordLastCallback(SSDF.format(Long.valueOf(System.currentTimeMillis())) + "," + str + ",");
        }
        obtainMessage(4, iconState).sendToTarget();
    }

    public void setListening(NetworkController.SignalCallback signalCallback, boolean z) {
        obtainMessage(7, z ? 1 : 0, 0, signalCallback).sendToTarget();
    }

    /* access modifiers changed from: protected */
    public void recordLastCallback(String str) {
        String[] strArr = this.mHistory;
        int i = this.mHistoryIndex;
        strArr[i] = str;
        this.mHistoryIndex = (i + 1) % 64;
    }

    public void setCarrierLabelListening(NetworkStateTracker.PanelCarrierLabelListener panelCarrierLabelListener, boolean z) {
        obtainMessage(8, z ? 1 : 0, 0, panelCarrierLabelListener).sendToTarget();
    }

    public void updatePanelCarrierLabel() {
        obtainMessage(9).sendToTarget();
    }

    public void setOperaterNameListening(NetworkStateTracker.ShortFormLabelListener shortFormLabelListener, boolean z) {
        obtainMessage(10, z ? 1 : 0, 0, shortFormLabelListener).sendToTarget();
    }

    public void updateShortFormLabel() {
        obtainMessage(11).sendToTarget();
    }

    public void setEriSoundListening(NetworkStateTracker.EriSoundListener eriSoundListener, boolean z) {
        obtainMessage(14, z ? 1 : 0, 0, eriSoundListener).sendToTarget();
    }

    public void updateEri(ServiceState serviceState, ServiceState serviceState2) {
        SomeArgs obtain = SomeArgs.obtain();
        obtain.arg1 = serviceState;
        obtain.arg2 = serviceState2;
        obtainMessage(15, obtain).sendToTarget();
    }

    public void playEriSoundAfterBoot() {
        obtainMessage(16).sendToTarget();
    }

    public void dump(PrintWriter printWriter) {
        printWriter.println("  - CallbackHandler -----");
        int i = 0;
        for (int i2 = 0; i2 < 64; i2++) {
            if (this.mHistory[i2] != null) {
                i++;
            }
        }
        int i3 = this.mHistoryIndex + 64;
        while (true) {
            i3--;
            if (i3 >= (this.mHistoryIndex + 64) - i) {
                printWriter.println("  Previous Callback(" + ((this.mHistoryIndex + 64) - i3) + "): " + this.mHistory[i3 & 63]);
            } else {
                return;
            }
        }
    }
}
