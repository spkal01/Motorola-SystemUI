package com.android.systemui.telephony;

import android.telephony.ServiceState;
import android.telephony.TelephonyCallback;
import java.util.ArrayList;
import java.util.List;

class TelephonyCallback extends android.telephony.TelephonyCallback implements TelephonyCallback.ActiveDataSubscriptionIdListener, TelephonyCallback.CallStateListener, TelephonyCallback.ServiceStateListener {
    private final List<TelephonyCallback.ActiveDataSubscriptionIdListener> mActiveDataSubscriptionIdListeners = new ArrayList();
    private final List<TelephonyCallback.CallStateListener> mCallStateListeners = new ArrayList();
    private final List<TelephonyCallback.ServiceStateListener> mServiceStateListeners = new ArrayList();

    TelephonyCallback() {
    }

    /* access modifiers changed from: package-private */
    public boolean hasAnyListeners() {
        return !this.mActiveDataSubscriptionIdListeners.isEmpty() || !this.mCallStateListeners.isEmpty() || !this.mServiceStateListeners.isEmpty();
    }

    public void onActiveDataSubscriptionIdChanged(int i) {
        ArrayList arrayList;
        synchronized (this.mActiveDataSubscriptionIdListeners) {
            arrayList = new ArrayList(this.mActiveDataSubscriptionIdListeners);
        }
        arrayList.forEach(new TelephonyCallback$$ExternalSyntheticLambda0(i));
    }

    /* access modifiers changed from: package-private */
    public void addActiveDataSubscriptionIdListener(TelephonyCallback.ActiveDataSubscriptionIdListener activeDataSubscriptionIdListener) {
        this.mActiveDataSubscriptionIdListeners.add(activeDataSubscriptionIdListener);
    }

    /* access modifiers changed from: package-private */
    public void removeActiveDataSubscriptionIdListener(TelephonyCallback.ActiveDataSubscriptionIdListener activeDataSubscriptionIdListener) {
        this.mActiveDataSubscriptionIdListeners.remove(activeDataSubscriptionIdListener);
    }

    public void onCallStateChanged(int i) {
        ArrayList arrayList;
        synchronized (this.mCallStateListeners) {
            arrayList = new ArrayList(this.mCallStateListeners);
        }
        arrayList.forEach(new TelephonyCallback$$ExternalSyntheticLambda1(i));
    }

    /* access modifiers changed from: package-private */
    public void addCallStateListener(TelephonyCallback.CallStateListener callStateListener) {
        this.mCallStateListeners.add(callStateListener);
    }

    public void onServiceStateChanged(ServiceState serviceState) {
        ArrayList arrayList;
        synchronized (this.mServiceStateListeners) {
            arrayList = new ArrayList(this.mServiceStateListeners);
        }
        arrayList.forEach(new TelephonyCallback$$ExternalSyntheticLambda2(serviceState));
    }

    /* access modifiers changed from: package-private */
    public void addServiceStateListener(TelephonyCallback.ServiceStateListener serviceStateListener) {
        this.mServiceStateListeners.add(serviceStateListener);
    }

    /* access modifiers changed from: package-private */
    public void removeServiceStateListener(TelephonyCallback.ServiceStateListener serviceStateListener) {
        this.mServiceStateListeners.remove(serviceStateListener);
    }
}
