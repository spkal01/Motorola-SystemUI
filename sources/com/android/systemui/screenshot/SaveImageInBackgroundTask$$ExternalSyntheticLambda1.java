package com.android.systemui.screenshot;

import android.content.Context;
import android.content.res.Resources;
import android.net.Uri;
import java.util.function.Supplier;

public final /* synthetic */ class SaveImageInBackgroundTask$$ExternalSyntheticLambda1 implements Supplier {
    public final /* synthetic */ SaveImageInBackgroundTask f$0;
    public final /* synthetic */ Context f$1;
    public final /* synthetic */ Uri f$2;
    public final /* synthetic */ Resources f$3;

    public /* synthetic */ SaveImageInBackgroundTask$$ExternalSyntheticLambda1(SaveImageInBackgroundTask saveImageInBackgroundTask, Context context, Uri uri, Resources resources) {
        this.f$0 = saveImageInBackgroundTask;
        this.f$1 = context;
        this.f$2 = uri;
        this.f$3 = resources;
    }

    public final Object get() {
        return this.f$0.lambda$createEditAction$1(this.f$1, this.f$2, this.f$3);
    }
}
