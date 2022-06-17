package com.android.systemui.shared.system.smartspace;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

public interface ISmartspaceCallback extends IInterface {
    SmartspaceState getSmartspaceState() throws RemoteException;

    void setVisibility(int i) throws RemoteException;

    public static abstract class Stub extends Binder implements ISmartspaceCallback {
        public static ISmartspaceCallback asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface queryLocalInterface = iBinder.queryLocalInterface("com.android.systemui.shared.system.smartspace.ISmartspaceCallback");
            if (queryLocalInterface == null || !(queryLocalInterface instanceof ISmartspaceCallback)) {
                return new Proxy(iBinder);
            }
            return (ISmartspaceCallback) queryLocalInterface;
        }

        private static class Proxy implements ISmartspaceCallback {
            public static ISmartspaceCallback sDefaultImpl;
            private IBinder mRemote;

            Proxy(IBinder iBinder) {
                this.mRemote = iBinder;
            }

            public IBinder asBinder() {
                return this.mRemote;
            }

            public SmartspaceState getSmartspaceState() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.android.systemui.shared.system.smartspace.ISmartspaceCallback");
                    if (!this.mRemote.transact(1, obtain, obtain2, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().getSmartspaceState();
                    }
                    obtain2.readException();
                    SmartspaceState createFromParcel = obtain2.readInt() != 0 ? SmartspaceState.CREATOR.createFromParcel(obtain2) : null;
                    obtain2.recycle();
                    obtain.recycle();
                    return createFromParcel;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void setVisibility(int i) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.android.systemui.shared.system.smartspace.ISmartspaceCallback");
                    obtain.writeInt(i);
                    if (this.mRemote.transact(3, obtain, (Parcel) null, 1) || Stub.getDefaultImpl() == null) {
                        obtain.recycle();
                    } else {
                        Stub.getDefaultImpl().setVisibility(i);
                    }
                } finally {
                    obtain.recycle();
                }
            }
        }

        public static ISmartspaceCallback getDefaultImpl() {
            return Proxy.sDefaultImpl;
        }
    }
}
