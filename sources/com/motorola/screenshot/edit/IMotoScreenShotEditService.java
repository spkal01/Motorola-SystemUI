package com.motorola.screenshot.edit;

import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

public interface IMotoScreenShotEditService extends IInterface {
    void onExitScreenShot(int i) throws RemoteException;

    void onImageAdded(int i, Uri uri) throws RemoteException;

    void onLongScreenShotEnd(int i) throws RemoteException;

    int onLongScreenShotStart() throws RemoteException;

    void setMaxImageHeight(int i, int i2) throws RemoteException;

    void setScreenshotPackageName(String str) throws RemoteException;

    void startEditActivity(int i, int i2) throws RemoteException;

    public static abstract class Stub extends Binder implements IMotoScreenShotEditService {
        public static IMotoScreenShotEditService asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface queryLocalInterface = iBinder.queryLocalInterface("com.motorola.screenshot.edit.IMotoScreenShotEditService");
            if (queryLocalInterface == null || !(queryLocalInterface instanceof IMotoScreenShotEditService)) {
                return new Proxy(iBinder);
            }
            return (IMotoScreenShotEditService) queryLocalInterface;
        }

        private static class Proxy implements IMotoScreenShotEditService {
            public static IMotoScreenShotEditService sDefaultImpl;
            private IBinder mRemote;

            Proxy(IBinder iBinder) {
                this.mRemote = iBinder;
            }

            public IBinder asBinder() {
                return this.mRemote;
            }

            public int onLongScreenShotStart() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.motorola.screenshot.edit.IMotoScreenShotEditService");
                    if (!this.mRemote.transact(1, obtain, obtain2, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().onLongScreenShotStart();
                    }
                    obtain2.readException();
                    int readInt = obtain2.readInt();
                    obtain2.recycle();
                    obtain.recycle();
                    return readInt;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void onImageAdded(int i, Uri uri) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.motorola.screenshot.edit.IMotoScreenShotEditService");
                    obtain.writeInt(i);
                    if (uri != null) {
                        obtain.writeInt(1);
                        uri.writeToParcel(obtain, 0);
                    } else {
                        obtain.writeInt(0);
                    }
                    if (this.mRemote.transact(2, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                        obtain2.recycle();
                        obtain.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().onImageAdded(i, uri);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void onLongScreenShotEnd(int i) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.motorola.screenshot.edit.IMotoScreenShotEditService");
                    obtain.writeInt(i);
                    if (this.mRemote.transact(3, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                        obtain2.recycle();
                        obtain.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().onLongScreenShotEnd(i);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void onExitScreenShot(int i) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.motorola.screenshot.edit.IMotoScreenShotEditService");
                    obtain.writeInt(i);
                    if (this.mRemote.transact(4, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                        obtain2.recycle();
                        obtain.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().onExitScreenShot(i);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void setMaxImageHeight(int i, int i2) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.motorola.screenshot.edit.IMotoScreenShotEditService");
                    obtain.writeInt(i);
                    obtain.writeInt(i2);
                    if (this.mRemote.transact(5, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                        obtain2.recycle();
                        obtain.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().setMaxImageHeight(i, i2);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void startEditActivity(int i, int i2) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.motorola.screenshot.edit.IMotoScreenShotEditService");
                    obtain.writeInt(i);
                    obtain.writeInt(i2);
                    if (this.mRemote.transact(6, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                        obtain2.recycle();
                        obtain.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().startEditActivity(i, i2);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void setScreenshotPackageName(String str) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.motorola.screenshot.edit.IMotoScreenShotEditService");
                    obtain.writeString(str);
                    if (this.mRemote.transact(7, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                        obtain2.recycle();
                        obtain.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().setScreenshotPackageName(str);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }
        }

        public static IMotoScreenShotEditService getDefaultImpl() {
            return Proxy.sDefaultImpl;
        }
    }
}
