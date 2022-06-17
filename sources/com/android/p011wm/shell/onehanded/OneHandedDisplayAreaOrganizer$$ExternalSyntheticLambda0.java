package com.android.p011wm.shell.onehanded;

import android.view.SurfaceControl;
import android.window.WindowContainerToken;
import java.util.function.BiConsumer;

/* renamed from: com.android.wm.shell.onehanded.OneHandedDisplayAreaOrganizer$$ExternalSyntheticLambda0 */
public final /* synthetic */ class OneHandedDisplayAreaOrganizer$$ExternalSyntheticLambda0 implements BiConsumer {
    public final /* synthetic */ OneHandedDisplayAreaOrganizer f$0;
    public final /* synthetic */ float f$1;
    public final /* synthetic */ int f$2;
    public final /* synthetic */ int f$3;

    public /* synthetic */ OneHandedDisplayAreaOrganizer$$ExternalSyntheticLambda0(OneHandedDisplayAreaOrganizer oneHandedDisplayAreaOrganizer, float f, int i, int i2) {
        this.f$0 = oneHandedDisplayAreaOrganizer;
        this.f$1 = f;
        this.f$2 = i;
        this.f$3 = i2;
    }

    public final void accept(Object obj, Object obj2) {
        this.f$0.lambda$scheduleOffset$0(this.f$1, this.f$2, this.f$3, (WindowContainerToken) obj, (SurfaceControl) obj2);
    }
}
