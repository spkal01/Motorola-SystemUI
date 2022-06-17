package com.android.systemui.p006qs;

import com.android.systemui.Dumpable;
import com.android.systemui.plugins.p005qs.QSTile;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.function.Consumer;

/* renamed from: com.android.systemui.qs.QSTileHost$$ExternalSyntheticLambda2 */
public final /* synthetic */ class QSTileHost$$ExternalSyntheticLambda2 implements Consumer {
    public final /* synthetic */ FileDescriptor f$0;
    public final /* synthetic */ PrintWriter f$1;
    public final /* synthetic */ String[] f$2;

    public /* synthetic */ QSTileHost$$ExternalSyntheticLambda2(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        this.f$0 = fileDescriptor;
        this.f$1 = printWriter;
        this.f$2 = strArr;
    }

    public final void accept(Object obj) {
        ((Dumpable) ((QSTile) obj)).dump(this.f$0, this.f$1, this.f$2);
    }
}
