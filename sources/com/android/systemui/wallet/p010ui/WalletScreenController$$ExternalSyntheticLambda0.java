package com.android.systemui.wallet.p010ui;

import android.content.Intent;
import android.view.View;

/* renamed from: com.android.systemui.wallet.ui.WalletScreenController$$ExternalSyntheticLambda0 */
public final /* synthetic */ class WalletScreenController$$ExternalSyntheticLambda0 implements View.OnClickListener {
    public final /* synthetic */ WalletScreenController f$0;
    public final /* synthetic */ Intent f$1;

    public /* synthetic */ WalletScreenController$$ExternalSyntheticLambda0(WalletScreenController walletScreenController, Intent intent) {
        this.f$0 = walletScreenController;
        this.f$1 = intent;
    }

    public final void onClick(View view) {
        this.f$0.lambda$showEmptyStateView$2(this.f$1, view);
    }
}
