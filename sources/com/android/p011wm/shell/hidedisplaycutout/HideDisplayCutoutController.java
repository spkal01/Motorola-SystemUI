package com.android.p011wm.shell.hidedisplaycutout;

import android.content.Context;
import android.content.res.Configuration;
import android.os.SystemProperties;
import com.android.p011wm.shell.common.DisplayController;
import com.android.p011wm.shell.common.ShellExecutor;
import java.io.PrintWriter;

/* renamed from: com.android.wm.shell.hidedisplaycutout.HideDisplayCutoutController */
public class HideDisplayCutoutController {
    private final Context mContext;
    boolean mEnabled;
    private final HideDisplayCutoutImpl mImpl = new HideDisplayCutoutImpl();
    /* access modifiers changed from: private */
    public final ShellExecutor mMainExecutor;
    private final HideDisplayCutoutOrganizer mOrganizer;

    public static HideDisplayCutoutController create(Context context, DisplayController displayController, ShellExecutor shellExecutor) {
        if (!SystemProperties.getBoolean("ro.support_hide_display_cutout", false)) {
            return null;
        }
        return new HideDisplayCutoutController(context, new HideDisplayCutoutOrganizer(context, displayController, shellExecutor), shellExecutor);
    }

    HideDisplayCutoutController(Context context, HideDisplayCutoutOrganizer hideDisplayCutoutOrganizer, ShellExecutor shellExecutor) {
        this.mContext = context;
        this.mOrganizer = hideDisplayCutoutOrganizer;
        this.mMainExecutor = shellExecutor;
        updateStatus();
    }

    public HideDisplayCutout asHideDisplayCutout() {
        return this.mImpl;
    }

    /* access modifiers changed from: package-private */
    public void updateStatus() {
        boolean z = this.mContext.getResources().getBoolean(17891630);
        if (z != this.mEnabled) {
            this.mEnabled = z;
            if (z) {
                this.mOrganizer.enableHideDisplayCutout();
            } else {
                this.mOrganizer.disableHideDisplayCutout();
            }
        }
    }

    /* access modifiers changed from: private */
    public void onConfigurationChanged(Configuration configuration) {
        updateStatus();
    }

    public void dump(PrintWriter printWriter) {
        printWriter.print("HideDisplayCutoutController");
        printWriter.println(" states: ");
        printWriter.print("  ");
        printWriter.print("mEnabled=");
        printWriter.println(this.mEnabled);
        this.mOrganizer.dump(printWriter);
    }

    /* renamed from: com.android.wm.shell.hidedisplaycutout.HideDisplayCutoutController$HideDisplayCutoutImpl */
    private class HideDisplayCutoutImpl implements HideDisplayCutout {
        private HideDisplayCutoutImpl() {
        }

        public void onConfigurationChanged(Configuration configuration) {
            HideDisplayCutoutController.this.mMainExecutor.execute(new C2313x58c373ff(this, configuration));
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$onConfigurationChanged$0(Configuration configuration) {
            HideDisplayCutoutController.this.onConfigurationChanged(configuration);
        }
    }
}
