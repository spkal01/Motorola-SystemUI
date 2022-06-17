package com.android.systemui.doze;

import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

public interface IMotoDisplay extends IInterface {
    void hide() throws RemoteException;

    void notifyEvent(Bundle bundle) throws RemoteException;

    void screenChange(boolean z) throws RemoteException;

    void show(IMotoDisplayCallback iMotoDisplayCallback, int i, boolean z, Bundle bundle) throws RemoteException;

    public static abstract class Stub extends Binder implements IMotoDisplay {
        public static IMotoDisplay asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface queryLocalInterface = iBinder.queryLocalInterface("com.android.systemui.doze.IMotoDisplay");
            if (queryLocalInterface == null || !(queryLocalInterface instanceof IMotoDisplay)) {
                return new Proxy(iBinder);
            }
            return (IMotoDisplay) queryLocalInterface;
        }

        private static class Proxy implements IMotoDisplay {
            public static IMotoDisplay sDefaultImpl;
            private IBinder mRemote;

            Proxy(IBinder iBinder) {
                this.mRemote = iBinder;
            }

            public IBinder asBinder() {
                return this.mRemote;
            }

            public void show(IMotoDisplayCallback iMotoDisplayCallback, int i, boolean z, Bundle bundle) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.android.systemui.doze.IMotoDisplay");
                    obtain.writeStrongBinder(iMotoDisplayCallback != null ? iMotoDisplayCallback.asBinder() : null);
                    obtain.writeInt(i);
                    obtain.writeInt(z ? 1 : 0);
                    if (bundle != null) {
                        obtain.writeInt(1);
                        bundle.writeToParcel(obtain, 0);
                    } else {
                        obtain.writeInt(0);
                    }
                    if (this.mRemote.transact(1, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                        obtain2.recycle();
                        obtain.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().show(iMotoDisplayCallback, i, z, bundle);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void hide() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.android.systemui.doze.IMotoDisplay");
                    if (this.mRemote.transact(2, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                        obtain2.recycle();
                        obtain.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().hide();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void screenChange(boolean z) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.android.systemui.doze.IMotoDisplay");
                    obtain.writeInt(z ? 1 : 0);
                    if (this.mRemote.transact(3, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                        obtain2.recycle();
                        obtain.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().screenChange(z);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void notifyEvent(Bundle bundle) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.android.systemui.doze.IMotoDisplay");
                    if (bundle != null) {
                        obtain.writeInt(1);
                        bundle.writeToParcel(obtain, 0);
                    } else {
                        obtain.writeInt(0);
                    }
                    if (this.mRemote.transact(4, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                        obtain2.recycle();
                        obtain.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().notifyEvent(bundle);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }
        }

        public static IMotoDisplay getDefaultImpl() {
            return Proxy.sDefaultImpl;
        }
    }
}
