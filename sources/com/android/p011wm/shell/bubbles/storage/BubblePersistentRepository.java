package com.android.p011wm.shell.bubbles.storage;

import android.content.Context;
import android.util.AtomicFile;
import android.util.Log;
import android.util.SparseArray;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import kotlin.Unit;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

/* renamed from: com.android.wm.shell.bubbles.storage.BubblePersistentRepository */
/* compiled from: BubblePersistentRepository.kt */
public final class BubblePersistentRepository {
    @NotNull
    private final AtomicFile bubbleFile;

    public BubblePersistentRepository(@NotNull Context context) {
        Intrinsics.checkNotNullParameter(context, "context");
        this.bubbleFile = new AtomicFile(new File(context.getFilesDir(), "overflow_bubbles.xml"), "overflow-bubbles");
    }

    public final boolean persistsToDisk(@NotNull SparseArray<List<BubbleEntity>> sparseArray) {
        Intrinsics.checkNotNullParameter(sparseArray, "bubbles");
        synchronized (this.bubbleFile) {
            try {
                FileOutputStream startWrite = this.bubbleFile.startWrite();
                Intrinsics.checkNotNullExpressionValue(startWrite, "{ bubbleFile.startWrite() }");
                try {
                    BubbleXmlHelperKt.writeXml(startWrite, sparseArray);
                    this.bubbleFile.finishWrite(startWrite);
                } catch (Exception e) {
                    Log.e("BubblePersistentRepository", "Failed to save bubble file, restoring backup", e);
                    this.bubbleFile.failWrite(startWrite);
                    Unit unit = Unit.INSTANCE;
                    return false;
                }
            } catch (IOException e2) {
                Log.e("BubblePersistentRepository", "Failed to save bubble file", e2);
                return false;
            }
        }
        return true;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:20:0x0024, code lost:
        r2 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:22:?, code lost:
        kotlin.p015io.CloseableKt.closeFinally(r3, r1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:23:0x0028, code lost:
        throw r2;
     */
    @org.jetbrains.annotations.NotNull
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public final android.util.SparseArray<java.util.List<com.android.p011wm.shell.bubbles.storage.BubbleEntity>> readFromDisk() {
        /*
            r3 = this;
            android.util.AtomicFile r0 = r3.bubbleFile
            monitor-enter(r0)
            android.util.AtomicFile r1 = r3.bubbleFile     // Catch:{ all -> 0x0038 }
            boolean r1 = r1.exists()     // Catch:{ all -> 0x0038 }
            if (r1 != 0) goto L_0x0012
            android.util.SparseArray r3 = new android.util.SparseArray     // Catch:{ all -> 0x0038 }
            r3.<init>()     // Catch:{ all -> 0x0038 }
            monitor-exit(r0)
            return r3
        L_0x0012:
            android.util.AtomicFile r3 = r3.bubbleFile     // Catch:{ all -> 0x0029 }
            java.io.FileInputStream r3 = r3.openRead()     // Catch:{ all -> 0x0029 }
            r1 = 0
            android.util.SparseArray r2 = com.android.p011wm.shell.bubbles.storage.BubbleXmlHelperKt.readXml(r3)     // Catch:{ all -> 0x0022 }
            kotlin.p015io.CloseableKt.closeFinally(r3, r1)     // Catch:{ all -> 0x0029 }
            monitor-exit(r0)
            return r2
        L_0x0022:
            r1 = move-exception
            throw r1     // Catch:{ all -> 0x0024 }
        L_0x0024:
            r2 = move-exception
            kotlin.p015io.CloseableKt.closeFinally(r3, r1)     // Catch:{ all -> 0x0029 }
            throw r2     // Catch:{ all -> 0x0029 }
        L_0x0029:
            r3 = move-exception
            java.lang.String r1 = "BubblePersistentRepository"
            java.lang.String r2 = "Failed to open bubble file"
            android.util.Log.e(r1, r2, r3)     // Catch:{ all -> 0x0038 }
            android.util.SparseArray r3 = new android.util.SparseArray     // Catch:{ all -> 0x0038 }
            r3.<init>()     // Catch:{ all -> 0x0038 }
            monitor-exit(r0)
            return r3
        L_0x0038:
            r3 = move-exception
            monitor-exit(r0)
            throw r3
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.p011wm.shell.bubbles.storage.BubblePersistentRepository.readFromDisk():android.util.SparseArray");
    }
}
