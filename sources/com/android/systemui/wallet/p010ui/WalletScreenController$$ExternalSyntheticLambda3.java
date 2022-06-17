package com.android.systemui.wallet.p010ui;

import android.service.quickaccesswallet.GetWalletCardsResponse;
import java.util.List;

/* renamed from: com.android.systemui.wallet.ui.WalletScreenController$$ExternalSyntheticLambda3 */
public final /* synthetic */ class WalletScreenController$$ExternalSyntheticLambda3 implements Runnable {
    public final /* synthetic */ WalletScreenController f$0;
    public final /* synthetic */ List f$1;
    public final /* synthetic */ GetWalletCardsResponse f$2;

    public /* synthetic */ WalletScreenController$$ExternalSyntheticLambda3(WalletScreenController walletScreenController, List list, GetWalletCardsResponse getWalletCardsResponse) {
        this.f$0 = walletScreenController;
        this.f$1 = list;
        this.f$2 = getWalletCardsResponse;
    }

    public final void run() {
        this.f$0.lambda$onWalletCardsRetrieved$0(this.f$1, this.f$2);
    }
}
