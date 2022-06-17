package com.motorola.taskbar;

import android.app.PendingIntent;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import com.android.internal.statusbar.StatusBarIcon;

public interface ITaskBarService extends IInterface {
    void addDesktopIcon(String str, int i, StatusBarIcon statusBarIcon, PendingIntent pendingIntent) throws RemoteException;

    void onDisplayReady(int i) throws RemoteException;

    void onDisplayRemoved(int i) throws RemoteException;

    void onMobileStateChanged(Bundle bundle) throws RemoteException;

    void onNavIconClicked() throws RemoteException;

    void onOverviewShown() throws RemoteException;

    void onSystemUIReady() throws RemoteException;

    void onTaskbarWindowStateChanged(int i, int i2) throws RemoteException;

    void onUnreadNotificationCountChanged(int i, int i2) throws RemoteException;

    void removeDesktopIcon(String str, int i) throws RemoteException;

    void setTaskBarImeSwitchButtonVisible(int i, boolean z) throws RemoteException;

    void setTaskBarProxy(ITaskBarProxy iTaskBarProxy) throws RemoteException;

    void setTaskBarTransitionMode(int i, int i2) throws RemoteException;

    void setTaskBarViewVisibility(int i, int i2) throws RemoteException;

    void updateImeVisible(int i, boolean z) throws RemoteException;

    public static abstract class Stub extends Binder implements ITaskBarService {
        public static ITaskBarService asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface queryLocalInterface = iBinder.queryLocalInterface("com.motorola.taskbar.ITaskBarService");
            if (queryLocalInterface == null || !(queryLocalInterface instanceof ITaskBarService)) {
                return new Proxy(iBinder);
            }
            return (ITaskBarService) queryLocalInterface;
        }

        private static class Proxy implements ITaskBarService {
            public static ITaskBarService sDefaultImpl;
            private IBinder mRemote;

            Proxy(IBinder iBinder) {
                this.mRemote = iBinder;
            }

            public IBinder asBinder() {
                return this.mRemote;
            }

            public void setTaskBarProxy(ITaskBarProxy iTaskBarProxy) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.motorola.taskbar.ITaskBarService");
                    obtain.writeStrongBinder(iTaskBarProxy != null ? iTaskBarProxy.asBinder() : null);
                    if (this.mRemote.transact(1, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                        obtain2.recycle();
                        obtain.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().setTaskBarProxy(iTaskBarProxy);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void onSystemUIReady() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.motorola.taskbar.ITaskBarService");
                    if (this.mRemote.transact(2, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                        obtain2.recycle();
                        obtain.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().onSystemUIReady();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void onDisplayReady(int i) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.motorola.taskbar.ITaskBarService");
                    obtain.writeInt(i);
                    if (this.mRemote.transact(3, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                        obtain2.recycle();
                        obtain.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().onDisplayReady(i);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void onDisplayRemoved(int i) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.motorola.taskbar.ITaskBarService");
                    obtain.writeInt(i);
                    if (this.mRemote.transact(4, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                        obtain2.recycle();
                        obtain.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().onDisplayRemoved(i);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void setTaskBarTransitionMode(int i, int i2) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.motorola.taskbar.ITaskBarService");
                    obtain.writeInt(i);
                    obtain.writeInt(i2);
                    if (this.mRemote.transact(5, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                        obtain2.recycle();
                        obtain.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().setTaskBarTransitionMode(i, i2);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void setTaskBarViewVisibility(int i, int i2) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.motorola.taskbar.ITaskBarService");
                    obtain.writeInt(i);
                    obtain.writeInt(i2);
                    if (this.mRemote.transact(6, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                        obtain2.recycle();
                        obtain.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().setTaskBarViewVisibility(i, i2);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void setTaskBarImeSwitchButtonVisible(int i, boolean z) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.motorola.taskbar.ITaskBarService");
                    obtain.writeInt(i);
                    obtain.writeInt(z ? 1 : 0);
                    if (this.mRemote.transact(7, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                        obtain2.recycle();
                        obtain.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().setTaskBarImeSwitchButtonVisible(i, z);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void onMobileStateChanged(Bundle bundle) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.motorola.taskbar.ITaskBarService");
                    if (bundle != null) {
                        obtain.writeInt(1);
                        bundle.writeToParcel(obtain, 0);
                    } else {
                        obtain.writeInt(0);
                    }
                    if (this.mRemote.transact(8, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                        obtain2.recycle();
                        obtain.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().onMobileStateChanged(bundle);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void updateImeVisible(int i, boolean z) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.motorola.taskbar.ITaskBarService");
                    obtain.writeInt(i);
                    obtain.writeInt(z ? 1 : 0);
                    if (this.mRemote.transact(9, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                        obtain2.recycle();
                        obtain.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().updateImeVisible(i, z);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void addDesktopIcon(String str, int i, StatusBarIcon statusBarIcon, PendingIntent pendingIntent) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.motorola.taskbar.ITaskBarService");
                    obtain.writeString(str);
                    obtain.writeInt(i);
                    if (statusBarIcon != null) {
                        obtain.writeInt(1);
                        statusBarIcon.writeToParcel(obtain, 0);
                    } else {
                        obtain.writeInt(0);
                    }
                    if (pendingIntent != null) {
                        obtain.writeInt(1);
                        pendingIntent.writeToParcel(obtain, 0);
                    } else {
                        obtain.writeInt(0);
                    }
                    if (this.mRemote.transact(10, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                        obtain2.recycle();
                        obtain.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().addDesktopIcon(str, i, statusBarIcon, pendingIntent);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void removeDesktopIcon(String str, int i) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.motorola.taskbar.ITaskBarService");
                    obtain.writeString(str);
                    obtain.writeInt(i);
                    if (this.mRemote.transact(11, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                        obtain2.recycle();
                        obtain.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().removeDesktopIcon(str, i);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void onTaskbarWindowStateChanged(int i, int i2) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.motorola.taskbar.ITaskBarService");
                    obtain.writeInt(i);
                    obtain.writeInt(i2);
                    if (this.mRemote.transact(12, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                        obtain2.recycle();
                        obtain.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().onTaskbarWindowStateChanged(i, i2);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void onNavIconClicked() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.motorola.taskbar.ITaskBarService");
                    if (this.mRemote.transact(14, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                        obtain2.recycle();
                        obtain.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().onNavIconClicked();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void onUnreadNotificationCountChanged(int i, int i2) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.motorola.taskbar.ITaskBarService");
                    obtain.writeInt(i);
                    obtain.writeInt(i2);
                    if (this.mRemote.transact(15, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                        obtain2.recycle();
                        obtain.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().onUnreadNotificationCountChanged(i, i2);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void onOverviewShown() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.motorola.taskbar.ITaskBarService");
                    if (this.mRemote.transact(16, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                        obtain2.recycle();
                        obtain.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().onOverviewShown();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }
        }

        public static ITaskBarService getDefaultImpl() {
            return Proxy.sDefaultImpl;
        }
    }
}
