package com.motorola.systemui.cli.media;

import android.media.session.PlaybackState;
import com.motorola.systemui.cli.media.CliMediaPreprocessor;
import java.util.function.Consumer;

/* renamed from: com.motorola.systemui.cli.media.CliMediaPreprocessor$MediaControllerCallback$$ExternalSyntheticLambda0 */
public final /* synthetic */ class C2662xeb2ed343 implements Consumer {
    public final /* synthetic */ CliMediaPreprocessor.MediaControllerCallback f$0;

    public /* synthetic */ C2662xeb2ed343(CliMediaPreprocessor.MediaControllerCallback mediaControllerCallback) {
        this.f$0 = mediaControllerCallback;
    }

    public final void accept(Object obj) {
        this.f$0.lambda$new$0((PlaybackState) obj);
    }
}
