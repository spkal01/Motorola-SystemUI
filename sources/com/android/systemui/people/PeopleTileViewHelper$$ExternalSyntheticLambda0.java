package com.android.systemui.people;

import android.graphics.ImageDecoder;

public final /* synthetic */ class PeopleTileViewHelper$$ExternalSyntheticLambda0 implements ImageDecoder.OnHeaderDecodedListener {
    public final /* synthetic */ PeopleTileViewHelper f$0;

    public /* synthetic */ PeopleTileViewHelper$$ExternalSyntheticLambda0(PeopleTileViewHelper peopleTileViewHelper) {
        this.f$0 = peopleTileViewHelper;
    }

    public final void onHeaderDecoded(ImageDecoder imageDecoder, ImageDecoder.ImageInfo imageInfo, ImageDecoder.Source source) {
        this.f$0.lambda$resolveImage$5(imageDecoder, imageInfo, source);
    }
}
