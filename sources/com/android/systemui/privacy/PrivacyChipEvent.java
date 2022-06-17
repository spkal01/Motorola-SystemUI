package com.android.systemui.privacy;

import com.android.internal.logging.UiEventLogger;

/* compiled from: PrivacyChipEvent.kt */
public enum PrivacyChipEvent implements UiEventLogger.UiEventEnum {
    ONGOING_INDICATORS_CHIP_VIEW(601),
    ONGOING_INDICATORS_CHIP_CLICK(602);
    
    private final int _id;

    private PrivacyChipEvent(int i) {
        this._id = i;
    }

    public int getId() {
        return this._id;
    }
}
