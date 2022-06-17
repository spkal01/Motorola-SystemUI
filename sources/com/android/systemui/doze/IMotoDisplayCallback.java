package com.android.systemui.doze;

import android.app.PendingIntent;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IInterface;
import android.os.RemoteException;

public interface IMotoDisplayCallback extends IInterface {
    void hideUdfpsFromExternal() throws RemoteException;

    Bundle queryData(Bundle bundle) throws RemoteException;

    void requestHide() throws RemoteException;

    void requestScreenOff() throws RemoteException;

    void requestUnlock(IBinder iBinder, boolean z, boolean z2) throws RemoteException;

    void setScreenBrightness(int i) throws RemoteException;

    void showUdfpsFromExternal() throws RemoteException;

    void triggerNotificationClickAndRequestUnlock(String str, PendingIntent pendingIntent, Intent intent) throws RemoteException;

    void triggerUdfpsStartAuth(Bundle bundle) throws RemoteException;

    void triggerVirtualSensor() throws RemoteException;

    public static abstract class Stub extends Binder implements IMotoDisplayCallback {
        public IBinder asBinder() {
            return this;
        }

        public Stub() {
            attachInterface(this, "com.android.systemui.doze.IMotoDisplayCallback");
        }

        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v1, resolved type: android.os.Bundle} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v7, resolved type: android.os.Bundle} */
        /* JADX WARNING: type inference failed for: r3v0 */
        /* JADX WARNING: type inference failed for: r3v4, types: [android.content.Intent] */
        /* JADX WARNING: type inference failed for: r3v10 */
        /* JADX WARNING: type inference failed for: r3v11 */
        /* JADX WARNING: type inference failed for: r3v12 */
        /* JADX WARNING: Multi-variable type inference failed */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public boolean onTransact(int r5, android.os.Parcel r6, android.os.Parcel r7, int r8) throws android.os.RemoteException {
            /*
                r4 = this;
                r0 = 1
                java.lang.String r1 = "com.android.systemui.doze.IMotoDisplayCallback"
                r2 = 1598968902(0x5f4e5446, float:1.4867585E19)
                if (r5 == r2) goto L_0x00dc
                r2 = 0
                r3 = 0
                switch(r5) {
                    case 1: goto L_0x00d2;
                    case 2: goto L_0x00b4;
                    case 3: goto L_0x00aa;
                    case 4: goto L_0x00a0;
                    case 5: goto L_0x0092;
                    case 6: goto L_0x006c;
                    case 7: goto L_0x003f;
                    case 8: goto L_0x0035;
                    case 9: goto L_0x002b;
                    case 10: goto L_0x0012;
                    default: goto L_0x000d;
                }
            L_0x000d:
                boolean r4 = super.onTransact(r5, r6, r7, r8)
                return r4
            L_0x0012:
                r6.enforceInterface(r1)
                int r5 = r6.readInt()
                if (r5 == 0) goto L_0x0024
                android.os.Parcelable$Creator r5 = android.os.Bundle.CREATOR
                java.lang.Object r5 = r5.createFromParcel(r6)
                r3 = r5
                android.os.Bundle r3 = (android.os.Bundle) r3
            L_0x0024:
                r4.triggerUdfpsStartAuth(r3)
                r7.writeNoException()
                return r0
            L_0x002b:
                r6.enforceInterface(r1)
                r4.hideUdfpsFromExternal()
                r7.writeNoException()
                return r0
            L_0x0035:
                r6.enforceInterface(r1)
                r4.showUdfpsFromExternal()
                r7.writeNoException()
                return r0
            L_0x003f:
                r6.enforceInterface(r1)
                java.lang.String r5 = r6.readString()
                int r8 = r6.readInt()
                if (r8 == 0) goto L_0x0055
                android.os.Parcelable$Creator r8 = android.app.PendingIntent.CREATOR
                java.lang.Object r8 = r8.createFromParcel(r6)
                android.app.PendingIntent r8 = (android.app.PendingIntent) r8
                goto L_0x0056
            L_0x0055:
                r8 = r3
            L_0x0056:
                int r1 = r6.readInt()
                if (r1 == 0) goto L_0x0065
                android.os.Parcelable$Creator r1 = android.content.Intent.CREATOR
                java.lang.Object r6 = r1.createFromParcel(r6)
                r3 = r6
                android.content.Intent r3 = (android.content.Intent) r3
            L_0x0065:
                r4.triggerNotificationClickAndRequestUnlock(r5, r8, r3)
                r7.writeNoException()
                return r0
            L_0x006c:
                r6.enforceInterface(r1)
                int r5 = r6.readInt()
                if (r5 == 0) goto L_0x007e
                android.os.Parcelable$Creator r5 = android.os.Bundle.CREATOR
                java.lang.Object r5 = r5.createFromParcel(r6)
                r3 = r5
                android.os.Bundle r3 = (android.os.Bundle) r3
            L_0x007e:
                android.os.Bundle r4 = r4.queryData(r3)
                r7.writeNoException()
                if (r4 == 0) goto L_0x008e
                r7.writeInt(r0)
                r4.writeToParcel(r7, r0)
                goto L_0x0091
            L_0x008e:
                r7.writeInt(r2)
            L_0x0091:
                return r0
            L_0x0092:
                r6.enforceInterface(r1)
                int r5 = r6.readInt()
                r4.setScreenBrightness(r5)
                r7.writeNoException()
                return r0
            L_0x00a0:
                r6.enforceInterface(r1)
                r4.triggerVirtualSensor()
                r7.writeNoException()
                return r0
            L_0x00aa:
                r6.enforceInterface(r1)
                r4.requestScreenOff()
                r7.writeNoException()
                return r0
            L_0x00b4:
                r6.enforceInterface(r1)
                android.os.IBinder r5 = r6.readStrongBinder()
                int r8 = r6.readInt()
                if (r8 == 0) goto L_0x00c3
                r8 = r0
                goto L_0x00c4
            L_0x00c3:
                r8 = r2
            L_0x00c4:
                int r6 = r6.readInt()
                if (r6 == 0) goto L_0x00cb
                r2 = r0
            L_0x00cb:
                r4.requestUnlock(r5, r8, r2)
                r7.writeNoException()
                return r0
            L_0x00d2:
                r6.enforceInterface(r1)
                r4.requestHide()
                r7.writeNoException()
                return r0
            L_0x00dc:
                r7.writeString(r1)
                return r0
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.doze.IMotoDisplayCallback.Stub.onTransact(int, android.os.Parcel, android.os.Parcel, int):boolean");
        }
    }
}
