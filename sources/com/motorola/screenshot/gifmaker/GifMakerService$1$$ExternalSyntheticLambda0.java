package com.motorola.screenshot.gifmaker;

import com.motorola.screenshot.gifmaker.GifMakerService;
import com.motorola.screenshot.gifmaker.aidl.IGifMakerPrivacyDialogCallback;

public final /* synthetic */ class GifMakerService$1$$ExternalSyntheticLambda0 implements Runnable {
    public final /* synthetic */ GifMakerService.C26431 f$0;
    public final /* synthetic */ IGifMakerPrivacyDialogCallback f$1;

    public /* synthetic */ GifMakerService$1$$ExternalSyntheticLambda0(GifMakerService.C26431 r1, IGifMakerPrivacyDialogCallback iGifMakerPrivacyDialogCallback) {
        this.f$0 = r1;
        this.f$1 = iGifMakerPrivacyDialogCallback;
    }

    public final void run() {
        this.f$0.lambda$showPrivacyDialog$0(this.f$1);
    }
}
