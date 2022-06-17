package com.android.systemui.statusbar.phone;

import android.app.ActivityManager;
import android.app.StatusBarManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.UserInfo;
import android.os.UserHandle;
import android.os.UserManager;
import com.android.systemui.broadcast.BroadcastDispatcher;
import com.android.systemui.statusbar.phone.ManagedProfileController;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executor;

public class ManagedProfileControllerImpl implements ManagedProfileController {
    private final BroadcastDispatcher mBroadcastDispatcher;
    /* access modifiers changed from: private */
    public final CopyOnWriteArrayList<ManagedProfileController.Callback> mCallbacks = new CopyOnWriteArrayList<>();
    private final Context mContext;
    private int mCurrentUser;
    private boolean mListening;
    private final LinkedList<UserInfo> mProfiles;
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            ManagedProfileControllerImpl.this.reloadManagedProfiles();
            Iterator it = ManagedProfileControllerImpl.this.mCallbacks.iterator();
            while (it.hasNext()) {
                ((ManagedProfileController.Callback) it.next()).onManagedProfileChanged();
            }
        }
    };
    private final UserManager mUserManager;

    public ManagedProfileControllerImpl(Context context, BroadcastDispatcher broadcastDispatcher) {
        this.mContext = context;
        this.mUserManager = UserManager.get(context);
        this.mBroadcastDispatcher = broadcastDispatcher;
        this.mProfiles = new LinkedList<>();
    }

    public void addCallback(ManagedProfileController.Callback callback) {
        this.mCallbacks.add(callback);
        if (this.mCallbacks.size() == 1) {
            setListening(true);
        }
        callback.onManagedProfileChanged();
    }

    public void removeCallback(ManagedProfileController.Callback callback) {
        if (this.mCallbacks.remove(callback) && this.mCallbacks.size() == 0) {
            setListening(false);
        }
    }

    public void setWorkModeEnabled(boolean z) {
        synchronized (this.mProfiles) {
            Iterator it = this.mProfiles.iterator();
            while (it.hasNext()) {
                if (!this.mUserManager.requestQuietModeEnabled(!z, UserHandle.of(((UserInfo) it.next()).id))) {
                    ((StatusBarManager) this.mContext.getSystemService("statusbar")).collapsePanels();
                }
            }
        }
    }

    /* access modifiers changed from: private */
    public void reloadManagedProfiles() {
        synchronized (this.mProfiles) {
            int currentUser = ActivityManager.getCurrentUser();
            this.mProfiles.clear();
            for (UserInfo userInfo : this.mUserManager.getEnabledProfiles(currentUser)) {
                if (userInfo.isManagedProfile()) {
                    this.mProfiles.add(userInfo);
                }
            }
            if (this.mProfiles.size() == 0 && currentUser == this.mCurrentUser) {
                Iterator<ManagedProfileController.Callback> it = this.mCallbacks.iterator();
                while (it.hasNext()) {
                    it.next().onManagedProfileRemoved();
                }
            }
            this.mCurrentUser = currentUser;
        }
    }

    public boolean hasActiveProfile() {
        boolean z;
        if (!this.mListening) {
            reloadManagedProfiles();
        }
        synchronized (this.mProfiles) {
            z = this.mProfiles.size() > 0;
        }
        return z;
    }

    public boolean isWorkModeEnabled() {
        if (!this.mListening) {
            reloadManagedProfiles();
        }
        synchronized (this.mProfiles) {
            Iterator it = this.mProfiles.iterator();
            while (it.hasNext()) {
                if (((UserInfo) it.next()).isQuietModeEnabled()) {
                    return false;
                }
            }
            return true;
        }
    }

    private void setListening(boolean z) {
        this.mListening = z;
        if (z) {
            reloadManagedProfiles();
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction("android.intent.action.USER_SWITCHED");
            intentFilter.addAction("android.intent.action.MANAGED_PROFILE_ADDED");
            intentFilter.addAction("android.intent.action.MANAGED_PROFILE_REMOVED");
            intentFilter.addAction("android.intent.action.MANAGED_PROFILE_AVAILABLE");
            intentFilter.addAction("android.intent.action.MANAGED_PROFILE_UNAVAILABLE");
            this.mBroadcastDispatcher.registerReceiver(this.mReceiver, intentFilter, (Executor) null, UserHandle.ALL);
            return;
        }
        this.mBroadcastDispatcher.unregisterReceiver(this.mReceiver);
    }
}
