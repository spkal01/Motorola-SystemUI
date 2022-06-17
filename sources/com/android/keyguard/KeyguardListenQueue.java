package com.android.keyguard;

import java.io.PrintWriter;
import java.text.DateFormat;
import java.util.Iterator;
import java.util.List;
import kotlin.NoWhenBranchMatchedException;
import kotlin.collections.ArrayDeque;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

/* compiled from: KeyguardListenQueue.kt */
public final class KeyguardListenQueue {
    @NotNull
    private final ArrayDeque<KeyguardFaceListenModel> faceQueue;
    @NotNull
    private final ArrayDeque<KeyguardFingerprintListenModel> fingerprintQueue;
    private final int sizePerModality;

    public KeyguardListenQueue() {
        this(0, 1, (DefaultConstructorMarker) null);
    }

    public final void print(@NotNull PrintWriter printWriter) {
        Intrinsics.checkNotNullParameter(printWriter, "writer");
        print$default(this, printWriter, (DateFormat) null, 2, (Object) null);
    }

    public KeyguardListenQueue(int i) {
        this.sizePerModality = i;
        this.faceQueue = new ArrayDeque<>();
        this.fingerprintQueue = new ArrayDeque<>();
    }

    /* JADX INFO: this call moved to the top of the method (can break code semantics) */
    public /* synthetic */ KeyguardListenQueue(int i, int i2, DefaultConstructorMarker defaultConstructorMarker) {
        this((i2 & 1) != 0 ? 20 : i);
    }

    @NotNull
    public final List<KeyguardListenModel> getModels() {
        return CollectionsKt___CollectionsKt.plus(this.faceQueue, this.fingerprintQueue);
    }

    public final void add(@NotNull KeyguardListenModel keyguardListenModel) {
        ArrayDeque arrayDeque;
        Intrinsics.checkNotNullParameter(keyguardListenModel, "model");
        if (keyguardListenModel instanceof KeyguardFaceListenModel) {
            arrayDeque = this.faceQueue;
            arrayDeque.add(keyguardListenModel);
        } else if (keyguardListenModel instanceof KeyguardFingerprintListenModel) {
            arrayDeque = this.fingerprintQueue;
            arrayDeque.add(keyguardListenModel);
        } else {
            throw new NoWhenBranchMatchedException();
        }
        if (arrayDeque.size() > this.sizePerModality) {
            arrayDeque.removeFirstOrNull();
        }
    }

    public static /* synthetic */ void print$default(KeyguardListenQueue keyguardListenQueue, PrintWriter printWriter, DateFormat dateFormat, int i, Object obj) {
        if ((i & 2) != 0) {
            dateFormat = KeyguardListenQueueKt.DEFAULT_FORMATTING;
        }
        keyguardListenQueue.print(printWriter, dateFormat);
    }

    public final void print(@NotNull PrintWriter printWriter, @NotNull DateFormat dateFormat) {
        Intrinsics.checkNotNullParameter(printWriter, "writer");
        Intrinsics.checkNotNullParameter(dateFormat, "dateFormat");
        KeyguardListenQueue$print$stringify$1 keyguardListenQueue$print$stringify$1 = new KeyguardListenQueue$print$stringify$1(dateFormat);
        printWriter.println("  Face listen results (last " + this.faceQueue.size() + " calls):");
        Iterator<KeyguardFaceListenModel> it = this.faceQueue.iterator();
        while (it.hasNext()) {
            printWriter.println((String) keyguardListenQueue$print$stringify$1.invoke(it.next()));
        }
        printWriter.println("  Fingerprint listen results (last " + this.fingerprintQueue.size() + " calls):");
        Iterator<KeyguardFingerprintListenModel> it2 = this.fingerprintQueue.iterator();
        while (it2.hasNext()) {
            printWriter.println((String) keyguardListenQueue$print$stringify$1.invoke(it2.next()));
        }
    }
}
