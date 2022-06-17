package com.android.systemui.screenshot;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.os.UserHandle;
import android.util.Log;
import com.android.internal.util.ScreenshotHelper;

public final class ScreenshotInputService extends Service {
    private static final boolean DEBUG = (!Build.IS_USER);
    /* access modifiers changed from: private */
    public boolean mBound = false;
    private Handler mHandler = new Handler() {
        public void handleMessage(Message message) {
            int i;
            final Messenger messenger = message.replyTo;
            C13701 r1 = new Runnable() {
                public void run() {
                    try {
                        messenger.send(Message.obtain((Handler) null, 1));
                    } catch (RemoteException unused) {
                    }
                }
            };
            Object obj = message.obj;
            int i2 = 0;
            if (obj == null || !(obj instanceof Bundle)) {
                i = 0;
            } else {
                Bundle bundle = (Bundle) obj;
                int i3 = bundle.getInt("extraType", 0);
                i = bundle.getInt("extraDisplayId", 0);
                i2 = i3;
            }
            ScreenshotHelper.ScreenshotRequest screenshotRequest = new ScreenshotHelper.ScreenshotRequest(5, true, true);
            screenshotRequest.setExtraType(i2);
            screenshotRequest.setExtraDisplayId(i);
            Message message2 = new Message();
            message2.copyFrom(message);
            message2.obj = screenshotRequest;
            ScreenshotInputService.this.takeScreenShot(r1, message2);
        }
    };
    /* access modifiers changed from: private */
    public ServiceConnection mScreenshotConnection = null;
    /* access modifiers changed from: private */
    public Messenger mTakeScreenshotServiceMessenger = null;

    public IBinder onBind(Intent intent) {
        return new Messenger(this.mHandler).getBinder();
    }

    public boolean onUnbind(Intent intent) {
        unbindTakeScreenshotService();
        if (!DEBUG) {
            return true;
        }
        Log.i("ScreenshotInputService", "onUnbind");
        return true;
    }

    public void onDestroy() {
        super.onDestroy();
        unbindTakeScreenshotService();
        if (DEBUG) {
            Log.i("ScreenshotInputService", "onDestroy");
        }
    }

    /* access modifiers changed from: private */
    public void sendTakeScreenShotRequest(final Runnable runnable, Message message) {
        message.replyTo = new Messenger(new Handler() {
            public void handleMessage(Message message) {
                runnable.run();
            }
        });
        try {
            this.mTakeScreenshotServiceMessenger.send(message);
        } catch (RemoteException e) {
            e.printStackTrace();
            Log.w("ScreenshotInputService", "Failed sending message to TakeScreenshotService");
            runnable.run();
        }
    }

    /* access modifiers changed from: private */
    public void takeScreenShot(final Runnable runnable, final Message message) {
        if (DEBUG) {
            Log.i("ScreenshotInputService", "takeScreenShot mBound: " + this.mBound + "; mScreenshotConnection: " + this.mScreenshotConnection);
        }
        if (this.mBound && this.mScreenshotConnection == null) {
            Log.i("ScreenshotInputService", "There is in bounding status! will not ignore this request.");
            runnable.run();
        } else if (this.mScreenshotConnection != null) {
            sendTakeScreenShotRequest(runnable, message);
        } else {
            ComponentName componentName = new ComponentName(this, TakeScreenshotService.class.getName());
            Intent intent = new Intent();
            intent.setComponent(componentName);
            if (bindServiceAsUser(intent, new ServiceConnection() {
                public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                    Messenger unused = ScreenshotInputService.this.mTakeScreenshotServiceMessenger = new Messenger(iBinder);
                    ServiceConnection unused2 = ScreenshotInputService.this.mScreenshotConnection = this;
                    ScreenshotInputService.this.sendTakeScreenShotRequest(runnable, message);
                }

                public void onServiceDisconnected(ComponentName componentName) {
                    if (ScreenshotInputService.this.mScreenshotConnection == null) {
                        runnable.run();
                    }
                    boolean unused = ScreenshotInputService.this.mBound = false;
                    ServiceConnection unused2 = ScreenshotInputService.this.mScreenshotConnection = null;
                }
            }, 33554433, UserHandle.CURRENT)) {
                this.mBound = true;
                return;
            }
            runnable.run();
            Log.w("ScreenshotInputService", "Failed to bind service TakeScreenshotService");
        }
    }

    private void unbindTakeScreenshotService() {
        ServiceConnection serviceConnection;
        if (this.mBound && (serviceConnection = this.mScreenshotConnection) != null) {
            unbindService(serviceConnection);
            this.mScreenshotConnection = null;
            this.mBound = false;
        }
    }
}
