package com.android.systemui.wallet.p010ui;

import android.service.quickaccesswallet.GetWalletCardsError;

/* renamed from: com.android.systemui.wallet.ui.WalletScreenController$$ExternalSyntheticLambda2 */
public final /* synthetic */ class WalletScreenController$$ExternalSyntheticLambda2 implements Runnable {
    public final /* synthetic */ WalletScreenController f$0;
    public final /* synthetic */ GetWalletCardsError f$1;

    public /* synthetic */ WalletScreenController$$ExternalSyntheticLambda2(WalletScreenController walletScreenController, GetWalletCardsError getWalletCardsError) {
        this.f$0 = walletScreenController;
        this.f$1 = getWalletCardsError;
    }

    public final void run() {
        this.f$0.lambda$onWalletCardRetrievalError$1(this.f$1);
    }
}
