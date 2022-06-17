package com.android.systemui.p006qs;

import com.android.internal.logging.UiEventLogger;

/* renamed from: com.android.systemui.qs.QSDndEvent */
/* compiled from: QSEvents.kt */
public enum QSDndEvent implements UiEventLogger.UiEventEnum {
    QS_DND_CONDITION_SELECT(420),
    QS_DND_TIME_UP(422),
    QS_DND_TIME_DOWN(423);
    
    private final int _id;

    private QSDndEvent(int i) {
        this._id = i;
    }

    public int getId() {
        return this._id;
    }
}
