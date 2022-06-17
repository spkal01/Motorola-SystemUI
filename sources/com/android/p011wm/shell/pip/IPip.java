package com.android.p011wm.shell.pip;

import android.app.PictureInPictureParams;
import android.content.ComponentName;
import android.content.pm.ActivityInfo;
import android.graphics.Rect;
import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.RemoteException;
import android.view.SurfaceControl;

/* renamed from: com.android.wm.shell.pip.IPip */
public interface IPip extends IInterface {
    void setPinnedStackAnimationListener(IPipAnimationListener iPipAnimationListener) throws RemoteException;

    void setShelfHeight(boolean z, int i) throws RemoteException;

    Rect startSwipePipToHome(ComponentName componentName, ActivityInfo activityInfo, PictureInPictureParams pictureInPictureParams, int i, int i2) throws RemoteException;

    void stopSwipePipToHome(ComponentName componentName, Rect rect, SurfaceControl surfaceControl) throws RemoteException;

    /* renamed from: com.android.wm.shell.pip.IPip$Stub */
    public static abstract class Stub extends Binder implements IPip {
        public IBinder asBinder() {
            return this;
        }

        public Stub() {
            attachInterface(this, "com.android.wm.shell.pip.IPip");
        }

        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v0, resolved type: android.view.SurfaceControl} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v1, resolved type: android.view.SurfaceControl} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v0, resolved type: android.app.PictureInPictureParams} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v4, resolved type: android.view.SurfaceControl} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v1, resolved type: java.lang.Object} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v5, resolved type: android.view.SurfaceControl} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v7, resolved type: android.view.SurfaceControl} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v8, resolved type: android.view.SurfaceControl} */
        /* JADX WARNING: type inference failed for: r4v3, types: [android.app.PictureInPictureParams] */
        /* JADX WARNING: Multi-variable type inference failed */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public boolean onTransact(int r12, android.os.Parcel r13, android.os.Parcel r14, int r15) throws android.os.RemoteException {
            /*
                r11 = this;
                r0 = 1
                java.lang.String r1 = "com.android.wm.shell.pip.IPip"
                r2 = 1598968902(0x5f4e5446, float:1.4867585E19)
                if (r12 == r2) goto L_0x00c4
                r2 = 2
                r3 = 0
                r4 = 0
                if (r12 == r2) goto L_0x0072
                r2 = 3
                if (r12 == r2) goto L_0x003c
                r2 = 4
                if (r12 == r2) goto L_0x002d
                r2 = 5
                if (r12 == r2) goto L_0x001b
                boolean r11 = super.onTransact(r12, r13, r14, r15)
                return r11
            L_0x001b:
                r13.enforceInterface(r1)
                int r12 = r13.readInt()
                if (r12 == 0) goto L_0x0025
                r3 = r0
            L_0x0025:
                int r12 = r13.readInt()
                r11.setShelfHeight(r3, r12)
                return r0
            L_0x002d:
                r13.enforceInterface(r1)
                android.os.IBinder r12 = r13.readStrongBinder()
                com.android.wm.shell.pip.IPipAnimationListener r12 = com.android.p011wm.shell.pip.IPipAnimationListener.Stub.asInterface(r12)
                r11.setPinnedStackAnimationListener(r12)
                return r0
            L_0x003c:
                r13.enforceInterface(r1)
                int r12 = r13.readInt()
                if (r12 == 0) goto L_0x004e
                android.os.Parcelable$Creator r12 = android.content.ComponentName.CREATOR
                java.lang.Object r12 = r12.createFromParcel(r13)
                android.content.ComponentName r12 = (android.content.ComponentName) r12
                goto L_0x004f
            L_0x004e:
                r12 = r4
            L_0x004f:
                int r14 = r13.readInt()
                if (r14 == 0) goto L_0x005e
                android.os.Parcelable$Creator r14 = android.graphics.Rect.CREATOR
                java.lang.Object r14 = r14.createFromParcel(r13)
                android.graphics.Rect r14 = (android.graphics.Rect) r14
                goto L_0x005f
            L_0x005e:
                r14 = r4
            L_0x005f:
                int r15 = r13.readInt()
                if (r15 == 0) goto L_0x006e
                android.os.Parcelable$Creator r15 = android.view.SurfaceControl.CREATOR
                java.lang.Object r13 = r15.createFromParcel(r13)
                r4 = r13
                android.view.SurfaceControl r4 = (android.view.SurfaceControl) r4
            L_0x006e:
                r11.stopSwipePipToHome(r12, r14, r4)
                return r0
            L_0x0072:
                r13.enforceInterface(r1)
                int r12 = r13.readInt()
                if (r12 == 0) goto L_0x0085
                android.os.Parcelable$Creator r12 = android.content.ComponentName.CREATOR
                java.lang.Object r12 = r12.createFromParcel(r13)
                android.content.ComponentName r12 = (android.content.ComponentName) r12
                r6 = r12
                goto L_0x0086
            L_0x0085:
                r6 = r4
            L_0x0086:
                int r12 = r13.readInt()
                if (r12 == 0) goto L_0x0096
                android.os.Parcelable$Creator r12 = android.content.pm.ActivityInfo.CREATOR
                java.lang.Object r12 = r12.createFromParcel(r13)
                android.content.pm.ActivityInfo r12 = (android.content.pm.ActivityInfo) r12
                r7 = r12
                goto L_0x0097
            L_0x0096:
                r7 = r4
            L_0x0097:
                int r12 = r13.readInt()
                if (r12 == 0) goto L_0x00a6
                android.os.Parcelable$Creator r12 = android.app.PictureInPictureParams.CREATOR
                java.lang.Object r12 = r12.createFromParcel(r13)
                r4 = r12
                android.app.PictureInPictureParams r4 = (android.app.PictureInPictureParams) r4
            L_0x00a6:
                r8 = r4
                int r9 = r13.readInt()
                int r10 = r13.readInt()
                r5 = r11
                android.graphics.Rect r11 = r5.startSwipePipToHome(r6, r7, r8, r9, r10)
                r14.writeNoException()
                if (r11 == 0) goto L_0x00c0
                r14.writeInt(r0)
                r11.writeToParcel(r14, r0)
                goto L_0x00c3
            L_0x00c0:
                r14.writeInt(r3)
            L_0x00c3:
                return r0
            L_0x00c4:
                r14.writeString(r1)
                return r0
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.p011wm.shell.pip.IPip.Stub.onTransact(int, android.os.Parcel, android.os.Parcel, int):boolean");
        }
    }
}
