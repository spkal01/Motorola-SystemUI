package com.android.systemui.statusbar.notification.row;

import com.android.systemui.statusbar.notification.row.ChannelEditorDialog;
import dagger.internal.Factory;

public final class ChannelEditorDialog_Builder_Factory implements Factory<ChannelEditorDialog.Builder> {

    private static final class InstanceHolder {
        /* access modifiers changed from: private */
        public static final ChannelEditorDialog_Builder_Factory INSTANCE = new ChannelEditorDialog_Builder_Factory();
    }

    public ChannelEditorDialog.Builder get() {
        return newInstance();
    }

    public static ChannelEditorDialog_Builder_Factory create() {
        return InstanceHolder.INSTANCE;
    }

    public static ChannelEditorDialog.Builder newInstance() {
        return new ChannelEditorDialog.Builder();
    }
}
