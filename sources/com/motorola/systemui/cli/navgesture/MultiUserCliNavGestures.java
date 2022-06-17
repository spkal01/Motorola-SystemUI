package com.motorola.systemui.cli.navgesture;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.RemoteException;
import android.os.UserHandle;
import android.os.UserManager;
import android.util.Log;
import com.android.systemui.SystemUI;
import com.android.systemui.shared.system.ActivityManagerWrapper;
import com.motorola.systemui.cli.navgesture.ICliRecentsSystemUserCallbacks;
import java.util.ArrayList;
import java.util.Iterator;

public class MultiUserCliNavGestures extends SystemUI {
    /* access modifiers changed from: private */
    public Handler mHandler;
    CliNavGestureImpl mImpl;
    private final ArrayList<Runnable> mOnConnectRunnables = new ArrayList<>();
    private CliNavGestureSystemUser mSystemToUserCallbacks;
    UserManager mUm;
    /* access modifiers changed from: private */
    public ICliRecentsSystemUserCallbacks mUserToSystemCallbacks;
    /* access modifiers changed from: private */
    public final IBinder.DeathRecipient mUserToSystemCallbacksDeathRcpt = new IBinder.DeathRecipient() {
        public void binderDied() {
            ICliRecentsSystemUserCallbacks unused = MultiUserCliNavGestures.this.mUserToSystemCallbacks = null;
            MultiUserCliNavGestures.this.mHandler.postDelayed(new Runnable() {
                public void run() {
                    MultiUserCliNavGestures.this.registerWithSystemUser();
                }
            }, 5000);
        }
    };
    private final ServiceConnection mUserToSystemServiceConnection = new ServiceConnection() {
        public void onServiceDisconnected(ComponentName componentName) {
        }

        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            if (iBinder != null) {
                ICliRecentsSystemUserCallbacks unused = MultiUserCliNavGestures.this.mUserToSystemCallbacks = ICliRecentsSystemUserCallbacks.Stub.asInterface(iBinder);
                try {
                    iBinder.linkToDeath(MultiUserCliNavGestures.this.mUserToSystemCallbacksDeathRcpt, 0);
                } catch (RemoteException e) {
                    Log.e("MultiUserCliNavGestures", "Lost connection to (System) SystemUI", e);
                }
                MultiUserCliNavGestures.this.runAndFlushOnConnectRunnables();
            }
            MultiUserCliNavGestures.this.mContext.unbindService(this);
        }
    };

    public boolean isSystemUser(int i) {
        return i == 0;
    }

    public MultiUserCliNavGestures(Context context) {
        super(context);
    }

    public int getProcessUser() {
        UserManager userManager = this.mUm;
        if (userManager == null) {
            return 0;
        }
        return userManager.getUserHandle();
    }

    public void start() {
        this.mUm = UserManager.get(this.mContext);
        this.mImpl = new CliNavGestureImpl(this.mContext);
        this.mHandler = new Handler(Looper.getMainLooper());
        int processUser = getProcessUser();
        ActivityManagerWrapper.getInstance().getCurrentUserId();
        if (isSystemUser(processUser)) {
            this.mSystemToUserCallbacks = new CliNavGestureSystemUser(this.mContext, this.mImpl);
        } else {
            registerWithSystemUser();
        }
    }

    public void toggleRecents() {
        int currentUserId = ActivityManagerWrapper.getInstance().getCurrentUserId();
        if (isSystemUser(currentUserId)) {
            this.mImpl.onOverviewToggle();
            return;
        }
        CliNavGestureSystemUser cliNavGestureSystemUser = this.mSystemToUserCallbacks;
        if (cliNavGestureSystemUser != null) {
            ICliRecentsNotSystemUserCallbacks nonSystemUserNavGestureForUser = cliNavGestureSystemUser.getNonSystemUserNavGestureForUser(currentUserId);
            if (nonSystemUserNavGestureForUser != null) {
                try {
                    nonSystemUserNavGestureForUser.onOverviewToggle();
                } catch (RemoteException e) {
                    Log.e("MultiUserCliNavGestures", "Callback failed", e);
                }
            } else {
                Log.e("MultiUserCliNavGestures", "toggleRecents No SystemUserNavGesturecallbacks found for user: " + currentUserId);
            }
        }
    }

    public void preloadOverView() {
        int currentUserId = ActivityManagerWrapper.getInstance().getCurrentUserId();
        if (isSystemUser(currentUserId)) {
            this.mImpl.preloadOverView();
            return;
        }
        CliNavGestureSystemUser cliNavGestureSystemUser = this.mSystemToUserCallbacks;
        if (cliNavGestureSystemUser != null) {
            ICliRecentsNotSystemUserCallbacks nonSystemUserNavGestureForUser = cliNavGestureSystemUser.getNonSystemUserNavGestureForUser(currentUserId);
            if (nonSystemUserNavGestureForUser != null) {
                try {
                    nonSystemUserNavGestureForUser.preloadOverView();
                } catch (RemoteException e) {
                    Log.e("MultiUserCliNavGestures", "Callback failed", e);
                }
            } else {
                Log.e("MultiUserCliNavGestures", "preloadOverView No SystemUserNavGesturecallbacks found for user: " + currentUserId);
            }
        } else {
            this.mImpl.preloadOverView();
        }
    }

    public void setSystemUiFlag(int i, boolean z) {
        ICliRecentsNotSystemUserCallbacks nonSystemUserNavGestureForUser;
        int currentUserId = ActivityManagerWrapper.getInstance().getCurrentUserId();
        if (isSystemUser(currentUserId)) {
            CliNavGestureImpl cliNavGestureImpl = this.mImpl;
            if (cliNavGestureImpl != null) {
                cliNavGestureImpl.setSystemUiFlag(i, z);
                return;
            }
            return;
        }
        CliNavGestureSystemUser cliNavGestureSystemUser = this.mSystemToUserCallbacks;
        if (cliNavGestureSystemUser != null && (nonSystemUserNavGestureForUser = cliNavGestureSystemUser.getNonSystemUserNavGestureForUser(currentUserId)) != null) {
            try {
                nonSystemUserNavGestureForUser.setSystemUiFlag(i, z);
            } catch (RemoteException e) {
                Log.e("MultiUserCliNavGestures", "Callback failed", e);
            }
        }
    }

    public IBinder getSystemUserCallbacks() {
        return this.mSystemToUserCallbacks;
    }

    /* access modifiers changed from: private */
    public void registerWithSystemUser() {
        final int processUser = getProcessUser();
        postToSystemUser(new Runnable() {
            public void run() {
                try {
                    MultiUserCliNavGestures.this.mUserToSystemCallbacks.registerNonSystemUserCallbacks(new CliNavGestureNotSystemUser(MultiUserCliNavGestures.this.mImpl), processUser);
                } catch (RemoteException e) {
                    Log.e("MultiUserCliNavGestures", "Failed to register", e);
                }
            }
        });
    }

    private void postToSystemUser(Runnable runnable) {
        this.mOnConnectRunnables.add(runnable);
        if (this.mUserToSystemCallbacks == null) {
            Intent intent = new Intent();
            intent.setClass(this.mContext, CliNavGestureSystemUserService.class);
            boolean bindServiceAsUser = this.mContext.bindServiceAsUser(intent, this.mUserToSystemServiceConnection, 1, UserHandle.SYSTEM);
            Log.d("MultiUserCliNavGestures", "postToSystemUser bound = " + bindServiceAsUser);
            if (!bindServiceAsUser) {
                this.mHandler.postDelayed(new Runnable() {
                    public void run() {
                        MultiUserCliNavGestures.this.registerWithSystemUser();
                    }
                }, 5000);
                return;
            }
            return;
        }
        runAndFlushOnConnectRunnables();
    }

    /* access modifiers changed from: private */
    public void runAndFlushOnConnectRunnables() {
        Iterator<Runnable> it = this.mOnConnectRunnables.iterator();
        while (it.hasNext()) {
            it.next().run();
        }
        this.mOnConnectRunnables.clear();
    }
}
