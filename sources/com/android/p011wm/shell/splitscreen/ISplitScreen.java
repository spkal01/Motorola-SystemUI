package com.android.p011wm.shell.splitscreen;

import android.app.PendingIntent;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IInterface;
import android.os.RemoteException;
import android.os.UserHandle;
import android.window.IRemoteTransition;

/* renamed from: com.android.wm.shell.splitscreen.ISplitScreen */
public interface ISplitScreen extends IInterface {
    void exitSplitScreen() throws RemoteException;

    void exitSplitScreenOnHide(boolean z) throws RemoteException;

    void registerSplitScreenListener(ISplitScreenListener iSplitScreenListener) throws RemoteException;

    void removeFromSideStage(int i) throws RemoteException;

    void setSideStageVisibility(boolean z) throws RemoteException;

    void startIntent(PendingIntent pendingIntent, Intent intent, int i, int i2, Bundle bundle) throws RemoteException;

    void startShortcut(String str, String str2, int i, int i2, Bundle bundle, UserHandle userHandle) throws RemoteException;

    void startTask(int i, int i2, int i3, Bundle bundle) throws RemoteException;

    void startTasks(int i, Bundle bundle, int i2, Bundle bundle2, int i3, IRemoteTransition iRemoteTransition) throws RemoteException;

    void unregisterSplitScreenListener(ISplitScreenListener iSplitScreenListener) throws RemoteException;

    /* renamed from: com.android.wm.shell.splitscreen.ISplitScreen$Stub */
    public static abstract class Stub extends Binder implements ISplitScreen {
        public IBinder asBinder() {
            return this;
        }

        public Stub() {
            attachInterface(this, "com.android.wm.shell.splitscreen.ISplitScreen");
        }

        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v0, resolved type: java.lang.Object} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v3, resolved type: android.os.Bundle} */
        /* JADX WARNING: Multi-variable type inference failed */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public boolean onTransact(int r11, android.os.Parcel r12, android.os.Parcel r13, int r14) throws android.os.RemoteException {
            /*
                r10 = this;
                r7 = 1
                java.lang.String r3 = "com.android.wm.shell.splitscreen.ISplitScreen"
                r4 = 1598968902(0x5f4e5446, float:1.4867585E19)
                if (r11 == r4) goto L_0x013f
                r4 = 0
                r5 = 0
                switch(r11) {
                    case 2: goto L_0x0130;
                    case 3: goto L_0x0121;
                    case 4: goto L_0x0113;
                    case 5: goto L_0x0108;
                    case 6: goto L_0x0101;
                    case 7: goto L_0x00f3;
                    case 8: goto L_0x00d1;
                    case 9: goto L_0x0093;
                    case 10: goto L_0x0051;
                    case 11: goto L_0x0012;
                    default: goto L_0x000d;
                }
            L_0x000d:
                boolean r0 = super.onTransact(r11, r12, r13, r14)
                return r0
            L_0x0012:
                r12.enforceInterface(r3)
                int r1 = r12.readInt()
                int r3 = r12.readInt()
                if (r3 == 0) goto L_0x0028
                android.os.Parcelable$Creator r3 = android.os.Bundle.CREATOR
                java.lang.Object r3 = r3.createFromParcel(r12)
                android.os.Bundle r3 = (android.os.Bundle) r3
                goto L_0x0029
            L_0x0028:
                r3 = r5
            L_0x0029:
                int r4 = r12.readInt()
                int r6 = r12.readInt()
                if (r6 == 0) goto L_0x003b
                android.os.Parcelable$Creator r5 = android.os.Bundle.CREATOR
                java.lang.Object r5 = r5.createFromParcel(r12)
                android.os.Bundle r5 = (android.os.Bundle) r5
            L_0x003b:
                int r6 = r12.readInt()
                android.os.IBinder r2 = r12.readStrongBinder()
                android.window.IRemoteTransition r8 = android.window.IRemoteTransition.Stub.asInterface(r2)
                r0 = r10
                r2 = r3
                r3 = r4
                r4 = r5
                r5 = r6
                r6 = r8
                r0.startTasks(r1, r2, r3, r4, r5, r6)
                return r7
            L_0x0051:
                r12.enforceInterface(r3)
                int r1 = r12.readInt()
                if (r1 == 0) goto L_0x0063
                android.os.Parcelable$Creator r1 = android.app.PendingIntent.CREATOR
                java.lang.Object r1 = r1.createFromParcel(r12)
                android.app.PendingIntent r1 = (android.app.PendingIntent) r1
                goto L_0x0064
            L_0x0063:
                r1 = r5
            L_0x0064:
                int r3 = r12.readInt()
                if (r3 == 0) goto L_0x0073
                android.os.Parcelable$Creator r3 = android.content.Intent.CREATOR
                java.lang.Object r3 = r3.createFromParcel(r12)
                android.content.Intent r3 = (android.content.Intent) r3
                goto L_0x0074
            L_0x0073:
                r3 = r5
            L_0x0074:
                int r4 = r12.readInt()
                int r6 = r12.readInt()
                int r8 = r12.readInt()
                if (r8 == 0) goto L_0x008b
                android.os.Parcelable$Creator r5 = android.os.Bundle.CREATOR
                java.lang.Object r2 = r5.createFromParcel(r12)
                android.os.Bundle r2 = (android.os.Bundle) r2
                r5 = r2
            L_0x008b:
                r0 = r10
                r2 = r3
                r3 = r4
                r4 = r6
                r0.startIntent(r1, r2, r3, r4, r5)
                return r7
            L_0x0093:
                r12.enforceInterface(r3)
                java.lang.String r1 = r12.readString()
                java.lang.String r3 = r12.readString()
                int r4 = r12.readInt()
                int r6 = r12.readInt()
                int r8 = r12.readInt()
                if (r8 == 0) goto L_0x00b5
                android.os.Parcelable$Creator r8 = android.os.Bundle.CREATOR
                java.lang.Object r8 = r8.createFromParcel(r12)
                android.os.Bundle r8 = (android.os.Bundle) r8
                goto L_0x00b6
            L_0x00b5:
                r8 = r5
            L_0x00b6:
                int r9 = r12.readInt()
                if (r9 == 0) goto L_0x00c6
                android.os.Parcelable$Creator r5 = android.os.UserHandle.CREATOR
                java.lang.Object r2 = r5.createFromParcel(r12)
                android.os.UserHandle r2 = (android.os.UserHandle) r2
                r9 = r2
                goto L_0x00c7
            L_0x00c6:
                r9 = r5
            L_0x00c7:
                r0 = r10
                r2 = r3
                r3 = r4
                r4 = r6
                r5 = r8
                r6 = r9
                r0.startShortcut(r1, r2, r3, r4, r5, r6)
                return r7
            L_0x00d1:
                r12.enforceInterface(r3)
                int r1 = r12.readInt()
                int r3 = r12.readInt()
                int r4 = r12.readInt()
                int r6 = r12.readInt()
                if (r6 == 0) goto L_0x00ef
                android.os.Parcelable$Creator r5 = android.os.Bundle.CREATOR
                java.lang.Object r2 = r5.createFromParcel(r12)
                r5 = r2
                android.os.Bundle r5 = (android.os.Bundle) r5
            L_0x00ef:
                r10.startTask(r1, r3, r4, r5)
                return r7
            L_0x00f3:
                r12.enforceInterface(r3)
                int r1 = r12.readInt()
                if (r1 == 0) goto L_0x00fd
                r4 = r7
            L_0x00fd:
                r10.exitSplitScreenOnHide(r4)
                return r7
            L_0x0101:
                r12.enforceInterface(r3)
                r10.exitSplitScreen()
                return r7
            L_0x0108:
                r12.enforceInterface(r3)
                int r1 = r12.readInt()
                r10.removeFromSideStage(r1)
                return r7
            L_0x0113:
                r12.enforceInterface(r3)
                int r1 = r12.readInt()
                if (r1 == 0) goto L_0x011d
                r4 = r7
            L_0x011d:
                r10.setSideStageVisibility(r4)
                return r7
            L_0x0121:
                r12.enforceInterface(r3)
                android.os.IBinder r1 = r12.readStrongBinder()
                com.android.wm.shell.splitscreen.ISplitScreenListener r1 = com.android.p011wm.shell.splitscreen.ISplitScreenListener.Stub.asInterface(r1)
                r10.unregisterSplitScreenListener(r1)
                return r7
            L_0x0130:
                r12.enforceInterface(r3)
                android.os.IBinder r1 = r12.readStrongBinder()
                com.android.wm.shell.splitscreen.ISplitScreenListener r1 = com.android.p011wm.shell.splitscreen.ISplitScreenListener.Stub.asInterface(r1)
                r10.registerSplitScreenListener(r1)
                return r7
            L_0x013f:
                r13.writeString(r3)
                return r7
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.p011wm.shell.splitscreen.ISplitScreen.Stub.onTransact(int, android.os.Parcel, android.os.Parcel, int):boolean");
        }
    }
}
