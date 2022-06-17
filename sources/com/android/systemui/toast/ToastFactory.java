package com.android.systemui.toast;

import android.content.Context;
import android.view.LayoutInflater;
import com.android.systemui.Dumpable;
import com.android.systemui.dump.DumpManager;
import com.android.systemui.plugins.PluginListener;
import com.android.systemui.plugins.ToastPlugin;
import com.android.systemui.shared.plugins.PluginManager;
import java.io.FileDescriptor;
import java.io.PrintWriter;

public class ToastFactory implements Dumpable {
    private final LayoutInflater mLayoutInflater;
    /* access modifiers changed from: private */
    public ToastPlugin mPlugin;

    public ToastFactory(LayoutInflater layoutInflater, PluginManager pluginManager, DumpManager dumpManager) {
        this.mLayoutInflater = layoutInflater;
        dumpManager.registerDumpable("ToastFactory", this);
        pluginManager.addPluginListener(new PluginListener<ToastPlugin>() {
            public void onPluginConnected(ToastPlugin toastPlugin, Context context) {
                ToastPlugin unused = ToastFactory.this.mPlugin = toastPlugin;
            }

            public void onPluginDisconnected(ToastPlugin toastPlugin) {
                if (toastPlugin.equals(ToastFactory.this.mPlugin)) {
                    ToastPlugin unused = ToastFactory.this.mPlugin = null;
                }
            }
        }, (Class<?>) ToastPlugin.class, false);
    }

    public SystemUIToast createToast(Context context, CharSequence charSequence, String str, int i, int i2) {
        if (isPluginAvailable()) {
            CharSequence charSequence2 = charSequence;
            String str2 = str;
            int i3 = i;
            return new SystemUIToast(this.mLayoutInflater, context, charSequence, this.mPlugin.createToast(charSequence, str, i), str, i, i2);
        }
        return new SystemUIToast(this.mLayoutInflater, context, charSequence, str, i, i2);
    }

    public SystemUIToast createToastForDisplay(Context context, CharSequence charSequence, String str, int i, int i2) {
        LayoutInflater from = LayoutInflater.from(context);
        if (!isPluginAvailable()) {
            return new SystemUIToast(from, context, charSequence, str, i, i2);
        }
        return new SystemUIToast(from, context, charSequence, this.mPlugin.createToast(charSequence, str, i), str, i, i2);
    }

    private boolean isPluginAvailable() {
        return this.mPlugin != null;
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        printWriter.println("ToastFactory:");
        printWriter.println("    mAttachedPlugin=" + this.mPlugin);
    }
}
