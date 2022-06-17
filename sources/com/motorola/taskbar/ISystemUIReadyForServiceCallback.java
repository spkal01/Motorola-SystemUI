package com.motorola.taskbar;

import android.content.ComponentName;
import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

public interface ISystemUIReadyForServiceCallback extends IInterface {
    void onTileClick(ComponentName componentName, IBinder iBinder) throws RemoteException;

    void onTileStartListening(ComponentName componentName) throws RemoteException;

    void onTileStopListening(ComponentName componentName) throws RemoteException;

    void onUnreadNotificationCountChanged(int i) throws RemoteException;

    void setTileBindRequested(ComponentName componentName, boolean z) throws RemoteException;

    public static abstract class Stub extends Binder implements ISystemUIReadyForServiceCallback {
        public IBinder asBinder() {
            return this;
        }

        public Stub() {
            attachInterface(this, "com.motorola.taskbar.ISystemUIReadyForServiceCallback");
        }

        public static ISystemUIReadyForServiceCallback asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface queryLocalInterface = iBinder.queryLocalInterface("com.motorola.taskbar.ISystemUIReadyForServiceCallback");
            if (queryLocalInterface == null || !(queryLocalInterface instanceof ISystemUIReadyForServiceCallback)) {
                return new Proxy(iBinder);
            }
            return (ISystemUIReadyForServiceCallback) queryLocalInterface;
        }

        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v4, resolved type: java.lang.Object} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v2, resolved type: android.content.ComponentName} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v7, resolved type: java.lang.Object} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v5, resolved type: android.content.ComponentName} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v11, resolved type: java.lang.Object} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v8, resolved type: android.content.ComponentName} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v18, resolved type: java.lang.Object} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v11, resolved type: android.content.ComponentName} */
        /* JADX WARNING: Multi-variable type inference failed */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public boolean onTransact(int r5, android.os.Parcel r6, android.os.Parcel r7, int r8) throws android.os.RemoteException {
            /*
                r4 = this;
                r0 = 1
                java.lang.String r1 = "com.motorola.taskbar.ISystemUIReadyForServiceCallback"
                r2 = 1598968902(0x5f4e5446, float:1.4867585E19)
                if (r5 == r2) goto L_0x009b
                if (r5 == r0) goto L_0x008d
                r2 = 2
                r3 = 0
                if (r5 == r2) goto L_0x0074
                r2 = 3
                if (r5 == r2) goto L_0x005b
                r2 = 4
                if (r5 == r2) goto L_0x003e
                r2 = 5
                if (r5 == r2) goto L_0x001c
                boolean r4 = super.onTransact(r5, r6, r7, r8)
                return r4
            L_0x001c:
                r6.enforceInterface(r1)
                int r5 = r6.readInt()
                if (r5 == 0) goto L_0x002e
                android.os.Parcelable$Creator r5 = android.content.ComponentName.CREATOR
                java.lang.Object r5 = r5.createFromParcel(r6)
                r3 = r5
                android.content.ComponentName r3 = (android.content.ComponentName) r3
            L_0x002e:
                int r5 = r6.readInt()
                if (r5 == 0) goto L_0x0036
                r5 = r0
                goto L_0x0037
            L_0x0036:
                r5 = 0
            L_0x0037:
                r4.setTileBindRequested(r3, r5)
                r7.writeNoException()
                return r0
            L_0x003e:
                r6.enforceInterface(r1)
                int r5 = r6.readInt()
                if (r5 == 0) goto L_0x0050
                android.os.Parcelable$Creator r5 = android.content.ComponentName.CREATOR
                java.lang.Object r5 = r5.createFromParcel(r6)
                r3 = r5
                android.content.ComponentName r3 = (android.content.ComponentName) r3
            L_0x0050:
                android.os.IBinder r5 = r6.readStrongBinder()
                r4.onTileClick(r3, r5)
                r7.writeNoException()
                return r0
            L_0x005b:
                r6.enforceInterface(r1)
                int r5 = r6.readInt()
                if (r5 == 0) goto L_0x006d
                android.os.Parcelable$Creator r5 = android.content.ComponentName.CREATOR
                java.lang.Object r5 = r5.createFromParcel(r6)
                r3 = r5
                android.content.ComponentName r3 = (android.content.ComponentName) r3
            L_0x006d:
                r4.onTileStopListening(r3)
                r7.writeNoException()
                return r0
            L_0x0074:
                r6.enforceInterface(r1)
                int r5 = r6.readInt()
                if (r5 == 0) goto L_0x0086
                android.os.Parcelable$Creator r5 = android.content.ComponentName.CREATOR
                java.lang.Object r5 = r5.createFromParcel(r6)
                r3 = r5
                android.content.ComponentName r3 = (android.content.ComponentName) r3
            L_0x0086:
                r4.onTileStartListening(r3)
                r7.writeNoException()
                return r0
            L_0x008d:
                r6.enforceInterface(r1)
                int r5 = r6.readInt()
                r4.onUnreadNotificationCountChanged(r5)
                r7.writeNoException()
                return r0
            L_0x009b:
                r7.writeString(r1)
                return r0
            */
            throw new UnsupportedOperationException("Method not decompiled: com.motorola.taskbar.ISystemUIReadyForServiceCallback.Stub.onTransact(int, android.os.Parcel, android.os.Parcel, int):boolean");
        }

        private static class Proxy implements ISystemUIReadyForServiceCallback {
            public static ISystemUIReadyForServiceCallback sDefaultImpl;
            private IBinder mRemote;

            Proxy(IBinder iBinder) {
                this.mRemote = iBinder;
            }

            public IBinder asBinder() {
                return this.mRemote;
            }

            public void onUnreadNotificationCountChanged(int i) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.motorola.taskbar.ISystemUIReadyForServiceCallback");
                    obtain.writeInt(i);
                    if (this.mRemote.transact(1, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                        obtain2.recycle();
                        obtain.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().onUnreadNotificationCountChanged(i);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void onTileStartListening(ComponentName componentName) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.motorola.taskbar.ISystemUIReadyForServiceCallback");
                    if (componentName != null) {
                        obtain.writeInt(1);
                        componentName.writeToParcel(obtain, 0);
                    } else {
                        obtain.writeInt(0);
                    }
                    if (this.mRemote.transact(2, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                        obtain2.recycle();
                        obtain.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().onTileStartListening(componentName);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void onTileStopListening(ComponentName componentName) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.motorola.taskbar.ISystemUIReadyForServiceCallback");
                    if (componentName != null) {
                        obtain.writeInt(1);
                        componentName.writeToParcel(obtain, 0);
                    } else {
                        obtain.writeInt(0);
                    }
                    if (this.mRemote.transact(3, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                        obtain2.recycle();
                        obtain.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().onTileStopListening(componentName);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void onTileClick(ComponentName componentName, IBinder iBinder) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.motorola.taskbar.ISystemUIReadyForServiceCallback");
                    if (componentName != null) {
                        obtain.writeInt(1);
                        componentName.writeToParcel(obtain, 0);
                    } else {
                        obtain.writeInt(0);
                    }
                    obtain.writeStrongBinder(iBinder);
                    if (this.mRemote.transact(4, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                        obtain2.recycle();
                        obtain.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().onTileClick(componentName, iBinder);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void setTileBindRequested(ComponentName componentName, boolean z) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.motorola.taskbar.ISystemUIReadyForServiceCallback");
                    int i = 1;
                    if (componentName != null) {
                        obtain.writeInt(1);
                        componentName.writeToParcel(obtain, 0);
                    } else {
                        obtain.writeInt(0);
                    }
                    if (!z) {
                        i = 0;
                    }
                    obtain.writeInt(i);
                    if (this.mRemote.transact(5, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                        obtain2.recycle();
                        obtain.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().setTileBindRequested(componentName, z);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }
        }

        public static ISystemUIReadyForServiceCallback getDefaultImpl() {
            return Proxy.sDefaultImpl;
        }
    }
}
