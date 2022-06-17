package com.motorola.screenshot.gifmaker.aidl;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

public interface IGifMakerPrivacyDialogCallback extends IInterface {
    void onUserAccepted() throws RemoteException;

    void onUserDeclined() throws RemoteException;

    public static abstract class Stub extends Binder implements IGifMakerPrivacyDialogCallback {
        public static IGifMakerPrivacyDialogCallback asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface queryLocalInterface = iBinder.queryLocalInterface("com.motorola.screenshot.gifmaker.aidl.IGifMakerPrivacyDialogCallback");
            if (queryLocalInterface == null || !(queryLocalInterface instanceof IGifMakerPrivacyDialogCallback)) {
                return new Proxy(iBinder);
            }
            return (IGifMakerPrivacyDialogCallback) queryLocalInterface;
        }

        private static class Proxy implements IGifMakerPrivacyDialogCallback {
            public static IGifMakerPrivacyDialogCallback sDefaultImpl;
            private IBinder mRemote;

            Proxy(IBinder iBinder) {
                this.mRemote = iBinder;
            }

            public IBinder asBinder() {
                return this.mRemote;
            }

            public void onUserAccepted() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.motorola.screenshot.gifmaker.aidl.IGifMakerPrivacyDialogCallback");
                    if (this.mRemote.transact(1, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                        obtain2.recycle();
                        obtain.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().onUserAccepted();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void onUserDeclined() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.motorola.screenshot.gifmaker.aidl.IGifMakerPrivacyDialogCallback");
                    if (this.mRemote.transact(2, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                        obtain2.recycle();
                        obtain.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().onUserDeclined();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }
        }

        public static IGifMakerPrivacyDialogCallback getDefaultImpl() {
            return Proxy.sDefaultImpl;
        }
    }
}
