package com.motorola.keyguard.paks;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.IWindowManager;
import android.view.WindowManagerGlobal;
import com.android.internal.widget.LockPatternUtils;
import com.motorola.keyguard.paks.IPAKSLockable;

public class PAKSLockService extends Service {
    private LockPatternUtils mLockPatternUtils;
    private IWindowManager mWindowManager;

    public void onCreate() {
        Log.d("PAKSLockService", "onCreate");
        super.onCreate();
        this.mLockPatternUtils = new LockPatternUtils(this);
        this.mWindowManager = WindowManagerGlobal.getWindowManagerService();
    }

    public IBinder onBind(Intent intent) {
        Log.d("PAKSLockService", "onBind");
        return new IPAKSLockable.Stub() {
            public void lock(String str) throws RemoteException {
                Log.d("PAKSLockService", "IPAKSLockable::lock");
                PAKSLockService.this.ensureCallerAuthorization();
                if (PAKSLockService.this.isPAKSPermanentlyLocked()) {
                    Log.e("PAKSLockService", "Permanently locked. Ignoring lock call.");
                    return;
                }
                PAKSLockService.this.setLockMessage(str);
                PAKSLockService.this.lockDevice(false);
            }

            public void unlock() throws RemoteException {
                Log.d("PAKSLockService", "IPAKSLockable::unlock");
                PAKSLockService.this.ensureCallerAuthorization();
                if (PAKSLockService.this.isPAKSPermanentlyLocked()) {
                    Log.e("PAKSLockService", "Permanently locked. Ignoring unlock call.");
                    return;
                }
                PAKSLockService.this.clearLockMessage();
                PAKSLockService.this.unlockDevice();
            }

            public void permanentLock(String str) throws RemoteException {
                Log.d("PAKSLockService", "IPAKSLockable::permanentLock");
                PAKSLockService.this.ensureCallerAuthorization();
                if (PAKSLockService.this.isPAKSPermanentlyLocked()) {
                    Log.e("PAKSLockService", "Permanently locked. Ignoring permanentLock call.");
                    return;
                }
                PAKSLockService.this.setLockMessage(str);
                PAKSLockService.this.lockDevice(true);
            }
        };
    }

    /* access modifiers changed from: private */
    public void setLockMessage(String str) {
        this.mLockPatternUtils.setDeviceOwnerInfo(str);
    }

    /* access modifiers changed from: private */
    public void clearLockMessage() {
        this.mLockPatternUtils.setDeviceOwnerInfo((String) null);
    }

    /* access modifiers changed from: private */
    public void lockDevice(boolean z) throws RemoteException {
        this.mLockPatternUtils.setPAKSLockState(z ? 2 : 1);
        this.mLockPatternUtils.requireStrongAuth(2, -1);
        this.mWindowManager.lockNow((Bundle) null);
        if (z) {
            disableService();
        }
    }

    /* access modifiers changed from: private */
    public void unlockDevice() throws RemoteException {
        this.mLockPatternUtils.setPAKSLockState(0);
        this.mWindowManager.lockNow((Bundle) null);
    }

    private void disableService() {
        getPackageManager().setComponentEnabledSetting(ComponentName.unflattenFromString("com.android.systemui/com.motorola.keyguard.paks.PAKSLockService"), 2, 1);
    }

    /* access modifiers changed from: private */
    public boolean isPAKSPermanentlyLocked() {
        return this.mLockPatternUtils.getPAKSLockState() == 2;
    }

    /* access modifiers changed from: private */
    public void ensureCallerAuthorization() {
        String retrieveCallingPackage = retrieveCallingPackage();
        if (!"com.motorola.paks".equals(retrieveCallingPackage)) {
            throw new SecurityException("Unauthorized caller: " + retrieveCallingPackage);
        }
    }

    private String retrieveCallingPackage() {
        return getPackageManager().getNameForUid(Binder.getCallingUid());
    }
}
