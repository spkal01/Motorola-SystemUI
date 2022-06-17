package com.android.systemui.wallet.dagger;

import android.content.Context;
import android.service.quickaccesswallet.QuickAccessWalletClient;

public abstract class WalletModule {
    public static QuickAccessWalletClient provideQuickAccessWalletClient(Context context) {
        return QuickAccessWalletClient.create(context);
    }
}
