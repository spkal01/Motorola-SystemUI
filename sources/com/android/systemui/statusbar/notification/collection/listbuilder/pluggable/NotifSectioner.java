package com.android.systemui.statusbar.notification.collection.listbuilder.pluggable;

import com.android.systemui.statusbar.notification.collection.ListEntry;
import com.android.systemui.statusbar.notification.collection.render.NodeController;

public abstract class NotifSectioner extends Pluggable<NotifSectioner> {
    public NodeController getHeaderNodeController() {
        return null;
    }

    public abstract boolean isInSection(ListEntry listEntry);

    protected NotifSectioner(String str) {
        super(str);
    }
}
