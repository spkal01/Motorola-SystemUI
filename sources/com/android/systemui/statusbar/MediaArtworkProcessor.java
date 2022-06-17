package com.android.systemui.statusbar;

import android.graphics.Bitmap;
import android.graphics.Point;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/* compiled from: MediaArtworkProcessor.kt */
public final class MediaArtworkProcessor {
    @Nullable
    private Bitmap mArtworkCache;
    @NotNull
    private final Point mTmpSize = new Point();

    /* JADX WARNING: Removed duplicated region for block: B:54:0x00e4  */
    /* JADX WARNING: Removed duplicated region for block: B:56:0x00ea  */
    /* JADX WARNING: Removed duplicated region for block: B:59:0x00f3  */
    /* JADX WARNING: Removed duplicated region for block: B:64:0x00fc  */
    /* JADX WARNING: Removed duplicated region for block: B:66:0x0102  */
    /* JADX WARNING: Removed duplicated region for block: B:69:0x010b  */
    @org.jetbrains.annotations.Nullable
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public final android.graphics.Bitmap processArtwork(@org.jetbrains.annotations.NotNull android.content.Context r8, @org.jetbrains.annotations.NotNull android.graphics.Bitmap r9) {
        /*
            r7 = this;
            java.lang.String r0 = "context"
            kotlin.jvm.internal.Intrinsics.checkNotNullParameter(r8, r0)
            java.lang.String r0 = "artwork"
            kotlin.jvm.internal.Intrinsics.checkNotNullParameter(r9, r0)
            android.graphics.Bitmap r0 = r7.mArtworkCache
            if (r0 == 0) goto L_0x000f
            return r0
        L_0x000f:
            android.renderscript.RenderScript r0 = android.renderscript.RenderScript.create(r8)
            android.renderscript.Element r1 = android.renderscript.Element.U8_4(r0)
            android.renderscript.ScriptIntrinsicBlur r1 = android.renderscript.ScriptIntrinsicBlur.create(r0, r1)
            r2 = 0
            android.view.Display r8 = r8.getDisplay()     // Catch:{ IllegalArgumentException -> 0x00d6, all -> 0x00d2 }
            if (r8 != 0) goto L_0x0023
            goto L_0x0028
        L_0x0023:
            android.graphics.Point r3 = r7.mTmpSize     // Catch:{ IllegalArgumentException -> 0x00d6, all -> 0x00d2 }
            r8.getSize(r3)     // Catch:{ IllegalArgumentException -> 0x00d6, all -> 0x00d2 }
        L_0x0028:
            android.graphics.Rect r8 = new android.graphics.Rect     // Catch:{ IllegalArgumentException -> 0x00d6, all -> 0x00d2 }
            int r3 = r9.getWidth()     // Catch:{ IllegalArgumentException -> 0x00d6, all -> 0x00d2 }
            int r4 = r9.getHeight()     // Catch:{ IllegalArgumentException -> 0x00d6, all -> 0x00d2 }
            r5 = 0
            r8.<init>(r5, r5, r3, r4)     // Catch:{ IllegalArgumentException -> 0x00d6, all -> 0x00d2 }
            android.graphics.Point r7 = r7.mTmpSize     // Catch:{ IllegalArgumentException -> 0x00d6, all -> 0x00d2 }
            int r3 = r7.x     // Catch:{ IllegalArgumentException -> 0x00d6, all -> 0x00d2 }
            int r3 = r3 / 6
            int r7 = r7.y     // Catch:{ IllegalArgumentException -> 0x00d6, all -> 0x00d2 }
            int r7 = r7 / 6
            int r7 = java.lang.Math.max(r3, r7)     // Catch:{ IllegalArgumentException -> 0x00d6, all -> 0x00d2 }
            android.util.MathUtils.fitRect(r8, r7)     // Catch:{ IllegalArgumentException -> 0x00d6, all -> 0x00d2 }
            int r7 = r8.width()     // Catch:{ IllegalArgumentException -> 0x00d6, all -> 0x00d2 }
            int r8 = r8.height()     // Catch:{ IllegalArgumentException -> 0x00d6, all -> 0x00d2 }
            r3 = 1
            android.graphics.Bitmap r7 = android.graphics.Bitmap.createScaledBitmap(r9, r7, r8, r3)     // Catch:{ IllegalArgumentException -> 0x00d6, all -> 0x00d2 }
            android.graphics.Bitmap$Config r8 = r7.getConfig()     // Catch:{ IllegalArgumentException -> 0x00cb, all -> 0x00c5 }
            android.graphics.Bitmap$Config r3 = android.graphics.Bitmap.Config.ARGB_8888     // Catch:{ IllegalArgumentException -> 0x00cb, all -> 0x00c5 }
            if (r8 == r3) goto L_0x006d
            android.graphics.Bitmap r8 = r7.copy(r3, r5)     // Catch:{ IllegalArgumentException -> 0x00cb, all -> 0x00c5 }
            r7.recycle()     // Catch:{ IllegalArgumentException -> 0x0069, all -> 0x0065 }
            r7 = r8
            goto L_0x006d
        L_0x0065:
            r7 = move-exception
            r0 = r2
            goto L_0x00f9
        L_0x0069:
            r7 = move-exception
            r0 = r2
            goto L_0x00d9
        L_0x006d:
            int r8 = r7.getWidth()     // Catch:{ IllegalArgumentException -> 0x00cb, all -> 0x00c5 }
            int r3 = r7.getHeight()     // Catch:{ IllegalArgumentException -> 0x00cb, all -> 0x00c5 }
            android.graphics.Bitmap$Config r4 = android.graphics.Bitmap.Config.ARGB_8888     // Catch:{ IllegalArgumentException -> 0x00cb, all -> 0x00c5 }
            android.graphics.Bitmap r8 = android.graphics.Bitmap.createBitmap(r8, r3, r4)     // Catch:{ IllegalArgumentException -> 0x00cb, all -> 0x00c5 }
            android.renderscript.Allocation$MipmapControl r3 = android.renderscript.Allocation.MipmapControl.MIPMAP_NONE     // Catch:{ IllegalArgumentException -> 0x00cb, all -> 0x00c5 }
            r4 = 2
            android.renderscript.Allocation r3 = android.renderscript.Allocation.createFromBitmap(r0, r7, r3, r4)     // Catch:{ IllegalArgumentException -> 0x00cb, all -> 0x00c5 }
            android.renderscript.Allocation r0 = android.renderscript.Allocation.createFromBitmap(r0, r8)     // Catch:{ IllegalArgumentException -> 0x00c2, all -> 0x00be }
            r4 = 1103626240(0x41c80000, float:25.0)
            r1.setRadius(r4)     // Catch:{ IllegalArgumentException -> 0x00bc, all -> 0x00ba }
            r1.setInput(r3)     // Catch:{ IllegalArgumentException -> 0x00bc, all -> 0x00ba }
            r1.forEach(r0)     // Catch:{ IllegalArgumentException -> 0x00bc, all -> 0x00ba }
            r0.copyTo(r8)     // Catch:{ IllegalArgumentException -> 0x00bc, all -> 0x00ba }
            androidx.palette.graphics.Palette$Swatch r9 = com.android.systemui.statusbar.notification.MediaNotificationProcessor.findBackgroundSwatch((android.graphics.Bitmap) r9)     // Catch:{ IllegalArgumentException -> 0x00bc, all -> 0x00ba }
            android.graphics.Canvas r4 = new android.graphics.Canvas     // Catch:{ IllegalArgumentException -> 0x00bc, all -> 0x00ba }
            r4.<init>(r8)     // Catch:{ IllegalArgumentException -> 0x00bc, all -> 0x00ba }
            int r9 = r9.getRgb()     // Catch:{ IllegalArgumentException -> 0x00bc, all -> 0x00ba }
            r5 = 178(0xb2, float:2.5E-43)
            int r9 = com.android.internal.graphics.ColorUtils.setAlphaComponent(r9, r5)     // Catch:{ IllegalArgumentException -> 0x00bc, all -> 0x00ba }
            r4.drawColor(r9)     // Catch:{ IllegalArgumentException -> 0x00bc, all -> 0x00ba }
            if (r3 != 0) goto L_0x00ad
            goto L_0x00b0
        L_0x00ad:
            r3.destroy()
        L_0x00b0:
            r0.destroy()
            r1.destroy()
            r7.recycle()
            return r8
        L_0x00ba:
            r8 = move-exception
            goto L_0x00c0
        L_0x00bc:
            r8 = move-exception
            goto L_0x00ce
        L_0x00be:
            r8 = move-exception
            r0 = r2
        L_0x00c0:
            r2 = r3
            goto L_0x00c7
        L_0x00c2:
            r8 = move-exception
            r0 = r2
            goto L_0x00ce
        L_0x00c5:
            r8 = move-exception
            r0 = r2
        L_0x00c7:
            r6 = r8
            r8 = r7
            r7 = r6
            goto L_0x00f9
        L_0x00cb:
            r8 = move-exception
            r0 = r2
            r3 = r0
        L_0x00ce:
            r6 = r8
            r8 = r7
            r7 = r6
            goto L_0x00da
        L_0x00d2:
            r7 = move-exception
            r8 = r2
            r0 = r8
            goto L_0x00f9
        L_0x00d6:
            r7 = move-exception
            r8 = r2
            r0 = r8
        L_0x00d9:
            r3 = r0
        L_0x00da:
            java.lang.String r9 = "MediaArtworkProcessor"
            java.lang.String r4 = "error while processing artwork"
            android.util.Log.e(r9, r4, r7)     // Catch:{ all -> 0x00f7 }
            if (r3 != 0) goto L_0x00e4
            goto L_0x00e7
        L_0x00e4:
            r3.destroy()
        L_0x00e7:
            if (r0 != 0) goto L_0x00ea
            goto L_0x00ed
        L_0x00ea:
            r0.destroy()
        L_0x00ed:
            r1.destroy()
            if (r8 != 0) goto L_0x00f3
            goto L_0x00f6
        L_0x00f3:
            r8.recycle()
        L_0x00f6:
            return r2
        L_0x00f7:
            r7 = move-exception
            r2 = r3
        L_0x00f9:
            if (r2 != 0) goto L_0x00fc
            goto L_0x00ff
        L_0x00fc:
            r2.destroy()
        L_0x00ff:
            if (r0 != 0) goto L_0x0102
            goto L_0x0105
        L_0x0102:
            r0.destroy()
        L_0x0105:
            r1.destroy()
            if (r8 != 0) goto L_0x010b
            goto L_0x010e
        L_0x010b:
            r8.recycle()
        L_0x010e:
            throw r7
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.statusbar.MediaArtworkProcessor.processArtwork(android.content.Context, android.graphics.Bitmap):android.graphics.Bitmap");
    }

    public final void clearCache() {
        Bitmap bitmap = this.mArtworkCache;
        if (bitmap != null) {
            bitmap.recycle();
        }
        this.mArtworkCache = null;
    }
}
