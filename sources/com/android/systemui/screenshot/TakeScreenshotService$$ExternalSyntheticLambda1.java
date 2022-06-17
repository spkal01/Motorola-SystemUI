package com.android.systemui.screenshot;

import android.net.Uri;
import android.os.Messenger;
import java.util.function.Consumer;

public final /* synthetic */ class TakeScreenshotService$$ExternalSyntheticLambda1 implements Consumer {
    public final /* synthetic */ Messenger f$0;

    public /* synthetic */ TakeScreenshotService$$ExternalSyntheticLambda1(Messenger messenger) {
        this.f$0 = messenger;
    }

    public final void accept(Object obj) {
        TakeScreenshotService.reportUri(this.f$0, (Uri) obj);
    }
}
