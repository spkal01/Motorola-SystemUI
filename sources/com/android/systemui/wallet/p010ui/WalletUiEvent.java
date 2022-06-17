package com.android.systemui.wallet.p010ui;

import com.android.internal.logging.UiEventLogger;

/* renamed from: com.android.systemui.wallet.ui.WalletUiEvent */
public enum WalletUiEvent implements UiEventLogger.UiEventEnum {
    QAW_SHOW_ALL(860),
    QAW_UNLOCK_FROM_CARD_CLICK(861),
    QAW_CHANGE_CARD(863),
    QAW_IMPRESSION(864),
    QAW_CLICK_CARD(865),
    QAW_UNLOCK_FROM_UNLOCK_BUTTON(866),
    QAW_UNLOCK_FROM_SHOW_ALL_BUTTON(867);
    
    private final int mId;

    private WalletUiEvent(int i) {
        this.mId = i;
    }

    public int getId() {
        return this.mId;
    }
}
