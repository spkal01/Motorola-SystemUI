package com.motorola.systemui.cli.media;

import android.app.Notification;
import android.media.session.PlaybackState;
import java.util.function.Consumer;

public final /* synthetic */ class CliMediaPreprocessor$$ExternalSyntheticLambda0 implements Consumer {
    public final /* synthetic */ Notification f$0;

    public /* synthetic */ CliMediaPreprocessor$$ExternalSyntheticLambda0(Notification notification) {
        this.f$0 = notification;
    }

    public final void accept(Object obj) {
        CliMediaPreprocessor.lambda$updateMediaActive$0(this.f$0, (PlaybackState) obj);
    }
}
