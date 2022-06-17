package com.android.p011wm.shell.bubbles;

/* renamed from: com.android.wm.shell.bubbles.BadgedImageView$$ExternalSyntheticLambda2 */
public final /* synthetic */ class BadgedImageView$$ExternalSyntheticLambda2 implements Runnable {
    public final /* synthetic */ BadgedImageView f$0;
    public final /* synthetic */ boolean f$1;
    public final /* synthetic */ Runnable f$2;

    public /* synthetic */ BadgedImageView$$ExternalSyntheticLambda2(BadgedImageView badgedImageView, boolean z, Runnable runnable) {
        this.f$0 = badgedImageView;
        this.f$1 = z;
        this.f$2 = runnable;
    }

    public final void run() {
        this.f$0.lambda$animateDotScale$2(this.f$1, this.f$2);
    }
}
