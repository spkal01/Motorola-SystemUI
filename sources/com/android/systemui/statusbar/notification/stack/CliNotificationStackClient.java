package com.android.systemui.statusbar.notification.stack;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.os.UserHandle;
import android.util.Log;
import android.view.MotionEvent;
import com.android.systemui.broadcast.BroadcastDispatcher;
import com.android.systemui.settings.CurrentUserTracker;
import com.android.systemui.shared.system.ActivityManagerWrapper;
import com.android.systemui.statusbar.policy.CallbackController;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

public class CliNotificationStackClient extends CurrentUserTracker implements CallbackController<StackProxyListener> {
    private boolean mBound;
    /* access modifiers changed from: private */
    public Handler mClientHandler;
    /* access modifiers changed from: private */
    public int mConnectionBackoffAttempts;
    private final Runnable mConnectionRunnable = new CliNotificationStackClient$$ExternalSyntheticLambda2(this);
    private final Context mContext;
    /* access modifiers changed from: private */
    public int mCurrentBoundedUserId = -1;
    /* access modifiers changed from: private */
    public final Runnable mDeferredConnectionCallback = new CliNotificationStackClient$$ExternalSyntheticLambda1(this);
    /* access modifiers changed from: private */
    public final Handler mHandler;
    private boolean mIsEnabled = true;
    private final BroadcastReceiver mLauncherStateChangedReceiver;
    private final List<StackProxyListener> mProxyCallbacks = new ArrayList();
    private final ServiceConnection mServiceConnection;
    /* access modifiers changed from: private */
    public final IBinder.DeathRecipient mServiceDeathRcpt;
    /* access modifiers changed from: private */
    public Messenger mServiceMessenger = null;
    private final Intent mStackIntent;

    public interface StackProxyListener {
        void onConnectionChanged(boolean z) {
        }

        void onMotionEvent(MotionEvent motionEvent) {
        }

        void onPanelVisibilityChanged(boolean z) {
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0() {
        Log.w("Cli_NotificationStackClient", "Binder supposed established connection but actual connection to service timed out, trying again");
        retryConnectionWithBackoff();
    }

    public void updateEnabledState() {
        this.mIsEnabled = this.mContext.getPackageManager().resolveServiceAsUser(this.mStackIntent, 1048576, ActivityManagerWrapper.getInstance().getCurrentUserId()) != null;
    }

    public CliNotificationStackClient(Context context, BroadcastDispatcher broadcastDispatcher) {
        super(broadcastDispatcher);
        C16451 r6 = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                Log.d("Cli_NotificationStackClient", "LauncherStateChangedReceiver=" + intent);
                CliNotificationStackClient.this.updateEnabledState();
                CliNotificationStackClient.this.startConnectionToCurrentUser();
            }
        };
        this.mLauncherStateChangedReceiver = r6;
        this.mClientHandler = new Handler(Looper.myLooper()) {
            public void handleMessage(Message message) {
                int i = message.what;
                if (i == 100) {
                    int i2 = message.getData().getInt("STACK_STATUS_VALUE");
                    boolean z = true;
                    if (i2 == 0 || i2 == 1) {
                        Log.d("Cli_NotificationStackClient", "message_status=" + i2);
                        CliNotificationStackClient cliNotificationStackClient = CliNotificationStackClient.this;
                        if (i2 != 0) {
                            z = false;
                        }
                        cliNotificationStackClient.notifyPanelVisibilityChanged(z);
                        return;
                    }
                    Log.e("Cli_NotificationStackClient", "Error message_status=" + i2);
                } else if (i == 101) {
                    MotionEvent motionEvent = (MotionEvent) message.getData().getParcelable("STACK_MOTION_EVENT_VALUE");
                    Log.d("Cli_NotificationStackClient", "messge_motion_event=" + motionEvent);
                    CliNotificationStackClient.this.notifyMotionEvent(motionEvent);
                }
            }
        };
        this.mServiceConnection = new ServiceConnection() {
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                Log.d("Cli_NotificationStackClient", "stack service connected mClientHandler=" + CliNotificationStackClient.this.mClientHandler);
                int unused = CliNotificationStackClient.this.mConnectionBackoffAttempts = 0;
                CliNotificationStackClient.this.mHandler.removeCallbacks(CliNotificationStackClient.this.mDeferredConnectionCallback);
                try {
                    iBinder.linkToDeath(CliNotificationStackClient.this.mServiceDeathRcpt, 0);
                    CliNotificationStackClient cliNotificationStackClient = CliNotificationStackClient.this;
                    int unused2 = cliNotificationStackClient.mCurrentBoundedUserId = cliNotificationStackClient.getCurrentUserId();
                    Messenger unused3 = CliNotificationStackClient.this.mServiceMessenger = new Messenger(iBinder);
                    Message obtain = Message.obtain((Handler) null, 100);
                    obtain.replyTo = new Messenger(CliNotificationStackClient.this.mClientHandler);
                    try {
                        CliNotificationStackClient.this.mServiceMessenger.send(obtain);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    CliNotificationStackClient.this.notifyConnectionChanged();
                } catch (RemoteException e2) {
                    Log.e("Cli_NotificationStackClient", "Lost connection to launcher service", e2);
                    CliNotificationStackClient.this.disconnectFromLauncherService();
                    CliNotificationStackClient.this.retryConnectionWithBackoff();
                }
            }

            public void onNullBinding(ComponentName componentName) {
                Log.w("Cli_NotificationStackClient", "Null binding of '" + componentName + "', try reconnecting");
                int unused = CliNotificationStackClient.this.mCurrentBoundedUserId = -1;
                CliNotificationStackClient.this.retryConnectionWithBackoff();
            }

            public void onBindingDied(ComponentName componentName) {
                Log.w("Cli_NotificationStackClient", "Binding died of '" + componentName + "', try reconnecting");
                int unused = CliNotificationStackClient.this.mCurrentBoundedUserId = -1;
                CliNotificationStackClient.this.retryConnectionWithBackoff();
            }

            public void onServiceDisconnected(ComponentName componentName) {
                int unused = CliNotificationStackClient.this.mCurrentBoundedUserId = -1;
            }
        };
        this.mServiceDeathRcpt = new CliNotificationStackClient$$ExternalSyntheticLambda0(this);
        this.mContext = context;
        this.mHandler = new Handler();
        this.mConnectionBackoffAttempts = 0;
        this.mStackIntent = new Intent("com.motorola.intent.action.ACTION_STACK").setPackage("com.motorola.launcher.secondarydisplay");
        IntentFilter intentFilter = new IntentFilter("android.intent.action.PACKAGE_ADDED");
        intentFilter.addDataScheme("package");
        intentFilter.addDataSchemeSpecificPart("com.motorola.launcher.secondarydisplay", 0);
        intentFilter.addAction("android.intent.action.PACKAGE_CHANGED");
        context.registerReceiver(r6, intentFilter);
        startTracking();
        updateEnabledState();
        startConnectionToCurrentUser();
    }

    public void onUserSwitched(int i) {
        this.mConnectionBackoffAttempts = 0;
        internalConnectToCurrentUser();
    }

    public void cleanupAfterDeath() {
        startConnectionToCurrentUser();
    }

    public void startConnectionToCurrentUser() {
        if (this.mHandler.getLooper() != Looper.myLooper()) {
            this.mHandler.post(this.mConnectionRunnable);
        } else {
            internalConnectToCurrentUser();
        }
    }

    /* access modifiers changed from: private */
    public void internalConnectToCurrentUser() {
        disconnectFromLauncherService();
        if (!isEnabled()) {
            Log.v("Cli_NotificationStackClient", "Cannot attempt connection, is enabled " + isEnabled());
            return;
        }
        this.mHandler.removeCallbacks(this.mConnectionRunnable);
        try {
            this.mBound = this.mContext.bindServiceAsUser(new Intent("com.motorola.intent.action.ACTION_STACK").setPackage("com.motorola.launcher.secondarydisplay"), this.mServiceConnection, 33554433, UserHandle.of(getCurrentUserId()));
        } catch (SecurityException e) {
            Log.e("Cli_NotificationStackClient", "Unable to bind because of security error", e);
        }
        if (this.mBound) {
            this.mHandler.postDelayed(this.mDeferredConnectionCallback, 5000);
        } else {
            retryConnectionWithBackoff();
        }
    }

    /* access modifiers changed from: private */
    public void retryConnectionWithBackoff() {
        if (!this.mHandler.hasCallbacks(this.mConnectionRunnable)) {
            long min = (long) Math.min(Math.scalb(1000.0f, this.mConnectionBackoffAttempts), 600000.0f);
            this.mHandler.postDelayed(this.mConnectionRunnable, min);
            this.mConnectionBackoffAttempts++;
            Log.w("Cli_NotificationStackClient", "Failed to connect on attempt " + this.mConnectionBackoffAttempts + " will try again in " + min + "ms");
        }
    }

    public void addCallback(StackProxyListener stackProxyListener) {
        this.mProxyCallbacks.add(stackProxyListener);
    }

    public void removeCallback(StackProxyListener stackProxyListener) {
        this.mProxyCallbacks.remove(stackProxyListener);
    }

    public boolean isEnabled() {
        return this.mIsEnabled;
    }

    /* access modifiers changed from: private */
    public void disconnectFromLauncherService() {
        if (this.mBound) {
            this.mContext.unbindService(this.mServiceConnection);
            this.mBound = false;
        }
        Messenger messenger = this.mServiceMessenger;
        if (messenger != null) {
            try {
                messenger.getBinder().unlinkToDeath(this.mServiceDeathRcpt, 0);
            } catch (NoSuchElementException unused) {
                Log.w("Cli_NotificationStackClient", "Unable to unlink mServiceDeathRcpt");
            }
            this.mServiceMessenger = null;
            notifyConnectionChanged();
        }
    }

    /* access modifiers changed from: private */
    public void notifyConnectionChanged() {
        for (int size = this.mProxyCallbacks.size() - 1; size >= 0; size--) {
            this.mProxyCallbacks.get(size).onConnectionChanged(this.mServiceMessenger != null);
        }
    }

    /* access modifiers changed from: private */
    public void notifyPanelVisibilityChanged(boolean z) {
        for (int size = this.mProxyCallbacks.size() - 1; size >= 0; size--) {
            this.mProxyCallbacks.get(size).onPanelVisibilityChanged(z);
        }
    }

    /* access modifiers changed from: private */
    public void notifyMotionEvent(MotionEvent motionEvent) {
        for (int size = this.mProxyCallbacks.size() - 1; size >= 0; size--) {
            this.mProxyCallbacks.get(size).onMotionEvent(motionEvent);
        }
    }
}
