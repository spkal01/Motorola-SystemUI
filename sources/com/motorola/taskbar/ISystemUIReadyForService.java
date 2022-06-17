package com.motorola.taskbar;

import android.content.ComponentName;
import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import android.service.quicksettings.Tile;

public interface ISystemUIReadyForService extends IInterface {
    int getUnreadNotificationCount() throws RemoteException;

    void onTileChanged(ComponentName componentName) throws RemoteException;

    void onTileDialogHidden(ComponentName componentName) throws RemoteException;

    void requestQSNPanel(int i) throws RemoteException;

    void setTaskBarServiceCallback(ISystemUIReadyForServiceCallback iSystemUIReadyForServiceCallback) throws RemoteException;

    void updateQsTile(ComponentName componentName, Tile tile) throws RemoteException;

    public static abstract class Stub extends Binder implements ISystemUIReadyForService {
        public IBinder asBinder() {
            return this;
        }

        public Stub() {
            attachInterface(this, "com.motorola.taskbar.ISystemUIReadyForService");
        }

        public static ISystemUIReadyForService asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface queryLocalInterface = iBinder.queryLocalInterface("com.motorola.taskbar.ISystemUIReadyForService");
            if (queryLocalInterface == null || !(queryLocalInterface instanceof ISystemUIReadyForService)) {
                return new Proxy(iBinder);
            }
            return (ISystemUIReadyForService) queryLocalInterface;
        }

        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v2, resolved type: android.content.ComponentName} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v5, resolved type: android.service.quicksettings.Tile} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v8, resolved type: android.content.ComponentName} */
        /* JADX WARNING: type inference failed for: r2v1 */
        /* JADX WARNING: type inference failed for: r2v11 */
        /* JADX WARNING: type inference failed for: r2v12 */
        /* JADX WARNING: type inference failed for: r2v13 */
        /* JADX WARNING: Multi-variable type inference failed */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public boolean onTransact(int r4, android.os.Parcel r5, android.os.Parcel r6, int r7) throws android.os.RemoteException {
            /*
                r3 = this;
                r0 = 1
                java.lang.String r1 = "com.motorola.taskbar.ISystemUIReadyForService"
                r2 = 1598968902(0x5f4e5446, float:1.4867585E19)
                if (r4 == r2) goto L_0x009a
                r2 = 0
                switch(r4) {
                    case 1: goto L_0x0088;
                    case 2: goto L_0x007a;
                    case 3: goto L_0x006c;
                    case 4: goto L_0x0053;
                    case 5: goto L_0x002a;
                    case 6: goto L_0x0011;
                    default: goto L_0x000c;
                }
            L_0x000c:
                boolean r3 = super.onTransact(r4, r5, r6, r7)
                return r3
            L_0x0011:
                r5.enforceInterface(r1)
                int r4 = r5.readInt()
                if (r4 == 0) goto L_0x0023
                android.os.Parcelable$Creator r4 = android.content.ComponentName.CREATOR
                java.lang.Object r4 = r4.createFromParcel(r5)
                r2 = r4
                android.content.ComponentName r2 = (android.content.ComponentName) r2
            L_0x0023:
                r3.onTileDialogHidden(r2)
                r6.writeNoException()
                return r0
            L_0x002a:
                r5.enforceInterface(r1)
                int r4 = r5.readInt()
                if (r4 == 0) goto L_0x003c
                android.os.Parcelable$Creator r4 = android.content.ComponentName.CREATOR
                java.lang.Object r4 = r4.createFromParcel(r5)
                android.content.ComponentName r4 = (android.content.ComponentName) r4
                goto L_0x003d
            L_0x003c:
                r4 = r2
            L_0x003d:
                int r7 = r5.readInt()
                if (r7 == 0) goto L_0x004c
                android.os.Parcelable$Creator r7 = android.service.quicksettings.Tile.CREATOR
                java.lang.Object r5 = r7.createFromParcel(r5)
                r2 = r5
                android.service.quicksettings.Tile r2 = (android.service.quicksettings.Tile) r2
            L_0x004c:
                r3.updateQsTile(r4, r2)
                r6.writeNoException()
                return r0
            L_0x0053:
                r5.enforceInterface(r1)
                int r4 = r5.readInt()
                if (r4 == 0) goto L_0x0065
                android.os.Parcelable$Creator r4 = android.content.ComponentName.CREATOR
                java.lang.Object r4 = r4.createFromParcel(r5)
                r2 = r4
                android.content.ComponentName r2 = (android.content.ComponentName) r2
            L_0x0065:
                r3.onTileChanged(r2)
                r6.writeNoException()
                return r0
            L_0x006c:
                r5.enforceInterface(r1)
                int r4 = r5.readInt()
                r3.requestQSNPanel(r4)
                r6.writeNoException()
                return r0
            L_0x007a:
                r5.enforceInterface(r1)
                int r3 = r3.getUnreadNotificationCount()
                r6.writeNoException()
                r6.writeInt(r3)
                return r0
            L_0x0088:
                r5.enforceInterface(r1)
                android.os.IBinder r4 = r5.readStrongBinder()
                com.motorola.taskbar.ISystemUIReadyForServiceCallback r4 = com.motorola.taskbar.ISystemUIReadyForServiceCallback.Stub.asInterface(r4)
                r3.setTaskBarServiceCallback(r4)
                r6.writeNoException()
                return r0
            L_0x009a:
                r6.writeString(r1)
                return r0
            */
            throw new UnsupportedOperationException("Method not decompiled: com.motorola.taskbar.ISystemUIReadyForService.Stub.onTransact(int, android.os.Parcel, android.os.Parcel, int):boolean");
        }

        private static class Proxy implements ISystemUIReadyForService {
            public static ISystemUIReadyForService sDefaultImpl;
            private IBinder mRemote;

            Proxy(IBinder iBinder) {
                this.mRemote = iBinder;
            }

            public IBinder asBinder() {
                return this.mRemote;
            }

            public void setTaskBarServiceCallback(ISystemUIReadyForServiceCallback iSystemUIReadyForServiceCallback) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.motorola.taskbar.ISystemUIReadyForService");
                    obtain.writeStrongBinder(iSystemUIReadyForServiceCallback != null ? iSystemUIReadyForServiceCallback.asBinder() : null);
                    if (this.mRemote.transact(1, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                        obtain2.recycle();
                        obtain.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().setTaskBarServiceCallback(iSystemUIReadyForServiceCallback);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public int getUnreadNotificationCount() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.motorola.taskbar.ISystemUIReadyForService");
                    if (!this.mRemote.transact(2, obtain, obtain2, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().getUnreadNotificationCount();
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

            public void requestQSNPanel(int i) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.motorola.taskbar.ISystemUIReadyForService");
                    obtain.writeInt(i);
                    if (this.mRemote.transact(3, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                        obtain2.recycle();
                        obtain.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().requestQSNPanel(i);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void onTileChanged(ComponentName componentName) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.motorola.taskbar.ISystemUIReadyForService");
                    if (componentName != null) {
                        obtain.writeInt(1);
                        componentName.writeToParcel(obtain, 0);
                    } else {
                        obtain.writeInt(0);
                    }
                    if (this.mRemote.transact(4, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                        obtain2.recycle();
                        obtain.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().onTileChanged(componentName);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void updateQsTile(ComponentName componentName, Tile tile) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.motorola.taskbar.ISystemUIReadyForService");
                    if (componentName != null) {
                        obtain.writeInt(1);
                        componentName.writeToParcel(obtain, 0);
                    } else {
                        obtain.writeInt(0);
                    }
                    if (tile != null) {
                        obtain.writeInt(1);
                        tile.writeToParcel(obtain, 0);
                    } else {
                        obtain.writeInt(0);
                    }
                    if (this.mRemote.transact(5, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                        obtain2.recycle();
                        obtain.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().updateQsTile(componentName, tile);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void onTileDialogHidden(ComponentName componentName) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.motorola.taskbar.ISystemUIReadyForService");
                    if (componentName != null) {
                        obtain.writeInt(1);
                        componentName.writeToParcel(obtain, 0);
                    } else {
                        obtain.writeInt(0);
                    }
                    if (this.mRemote.transact(6, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                        obtain2.recycle();
                        obtain.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().onTileDialogHidden(componentName);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }
        }

        public static ISystemUIReadyForService getDefaultImpl() {
            return Proxy.sDefaultImpl;
        }
    }
}
