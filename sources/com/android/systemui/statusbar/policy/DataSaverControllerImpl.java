package com.android.systemui.statusbar.policy;

import android.content.Context;
import android.net.INetworkPolicyListener;
import android.net.NetworkPolicyManager;
import android.os.Handler;
import android.os.Looper;
import android.os.RemoteException;
import com.android.systemui.Dependency;
import com.android.systemui.statusbar.policy.AllBgDataRestrictionMonitor;
import com.android.systemui.statusbar.policy.DataSaverController;
import com.android.systemui.statusbar.policy.NetworkController;
import java.util.ArrayList;

public class DataSaverControllerImpl implements DataSaverController, AllBgDataRestrictionMonitor.Listener {
    /* access modifiers changed from: private */
    public final AllBgDataRestrictionMonitor mAllBgDataRestrictionMonitor;
    /* access modifiers changed from: private */
    public final Handler mHandler = new Handler(Looper.getMainLooper());
    private final ArrayList<DataSaverController.Listener> mListeners = new ArrayList<>();
    private final INetworkPolicyListener mPolicyListener = new NetworkPolicyManager.Listener() {
        public void onRestrictBackgroundChanged(final boolean z) {
            DataSaverControllerImpl.this.mHandler.post(new Runnable() {
                public void run() {
                    DataSaverControllerImpl dataSaverControllerImpl = DataSaverControllerImpl.this;
                    dataSaverControllerImpl.handleRestrictBackgroundChanged(dataSaverControllerImpl.mAllBgDataRestrictionMonitor.isRestricted() ? false : z);
                }
            });
        }
    };
    private final NetworkPolicyManager mPolicyManager;
    private final DataSignalCallback mSignalCallback = new DataSignalCallback();

    public DataSaverControllerImpl(Context context) {
        this.mPolicyManager = NetworkPolicyManager.from(context);
        AllBgDataRestrictionMonitor allBgDataRestrictionMonitor = new AllBgDataRestrictionMonitor(context);
        this.mAllBgDataRestrictionMonitor = allBgDataRestrictionMonitor;
        allBgDataRestrictionMonitor.registerListener(this);
    }

    /* access modifiers changed from: private */
    public void handleRestrictBackgroundChanged(boolean z) {
        synchronized (this.mListeners) {
            for (int i = 0; i < this.mListeners.size(); i++) {
                this.mListeners.get(i).onDataSaverChanged(z);
            }
        }
    }

    public void addCallback(DataSaverController.Listener listener) {
        synchronized (this.mListeners) {
            this.mListeners.add(listener);
            if (this.mListeners.size() == 1) {
                this.mPolicyManager.registerListener(this.mPolicyListener);
                ((NetworkController) Dependency.get(NetworkController.class)).addCallback(this.mSignalCallback);
            }
        }
        listener.onDataSaverChanged(isDataSaverEnabled());
    }

    public void removeCallback(DataSaverController.Listener listener) {
        synchronized (this.mListeners) {
            this.mListeners.remove(listener);
            if (this.mListeners.size() == 0) {
                this.mPolicyManager.unregisterListener(this.mPolicyListener);
                ((NetworkController) Dependency.get(NetworkController.class)).removeCallback(this.mSignalCallback);
            }
        }
    }

    public boolean isDataSaverEnabled() {
        return this.mPolicyManager.getRestrictBackground();
    }

    public void setDataSaverEnabled(boolean z) {
        this.mPolicyManager.setRestrictBackground(z);
        try {
            this.mPolicyListener.onRestrictBackgroundChanged(z);
        } catch (RemoteException unused) {
        }
    }

    public void onAllBgDataRestrictionChanged(boolean z) {
        try {
            this.mPolicyListener.onRestrictBackgroundChanged(isDataSaverEnabled());
        } catch (RemoteException unused) {
        }
    }

    public boolean dataSaverUnavailable() {
        return this.mAllBgDataRestrictionMonitor.isRestricted();
    }

    private final class DataSignalCallback implements NetworkController.SignalCallback {
        private DataSignalCallback() {
        }
    }
}
