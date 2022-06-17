package com.android.systemui.wallet.p010ui;

import android.view.View;
import com.android.systemui.wallet.p010ui.WalletCardCarousel;

/* renamed from: com.android.systemui.wallet.ui.WalletCardCarousel$WalletCardCarouselAdapter$$ExternalSyntheticLambda0 */
public final /* synthetic */ class C2190x7bd3973a implements View.OnClickListener {
    public final /* synthetic */ WalletCardCarousel.WalletCardCarouselAdapter f$0;
    public final /* synthetic */ int f$1;
    public final /* synthetic */ WalletCardViewInfo f$2;

    public /* synthetic */ C2190x7bd3973a(WalletCardCarousel.WalletCardCarouselAdapter walletCardCarouselAdapter, int i, WalletCardViewInfo walletCardViewInfo) {
        this.f$0 = walletCardCarouselAdapter;
        this.f$1 = i;
        this.f$2 = walletCardViewInfo;
    }

    public final void onClick(View view) {
        this.f$0.lambda$onBindViewHolder$0(this.f$1, this.f$2, view);
    }
}
