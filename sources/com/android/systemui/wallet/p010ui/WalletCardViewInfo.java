package com.android.systemui.wallet.p010ui;

import android.app.PendingIntent;
import android.graphics.drawable.Drawable;

/* renamed from: com.android.systemui.wallet.ui.WalletCardViewInfo */
interface WalletCardViewInfo {
    Drawable getCardDrawable();

    String getCardId();

    CharSequence getContentDescription();

    Drawable getIcon();

    CharSequence getLabel();

    PendingIntent getPendingIntent();

    boolean isUiEquivalent(WalletCardViewInfo walletCardViewInfo) {
        if (walletCardViewInfo == null) {
            return false;
        }
        return getCardId().equals(walletCardViewInfo.getCardId());
    }
}
