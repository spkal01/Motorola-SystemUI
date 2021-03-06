package com.android.systemui.volume;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Handler;
import android.util.Log;
import com.android.systemui.R$bool;
import com.android.systemui.SystemUI;
import com.android.systemui.p006qs.tiles.DndTile;
import java.io.FileDescriptor;
import java.io.PrintWriter;

public class VolumeUI extends SystemUI {
    private static boolean LOGD = Log.isLoggable("VolumeUI", 3);
    private boolean mEnabled;
    private final Handler mHandler = new Handler();
    private VolumeDialogComponent mVolumeComponent;

    public VolumeUI(Context context, VolumeDialogComponent volumeDialogComponent) {
        super(context);
        this.mVolumeComponent = volumeDialogComponent;
    }

    public void start() {
        boolean z = this.mContext.getResources().getBoolean(R$bool.enable_volume_ui);
        boolean z2 = this.mContext.getResources().getBoolean(R$bool.enable_safety_warning);
        boolean z3 = z || z2;
        this.mEnabled = z3;
        if (z3) {
            this.mVolumeComponent.setEnableDialogs(z, z2);
            setDefaultVolumeController();
        }
    }

    /* access modifiers changed from: protected */
    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        if (this.mEnabled) {
            this.mVolumeComponent.onConfigurationChanged(configuration);
        }
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        printWriter.print("mEnabled=");
        printWriter.println(this.mEnabled);
        if (this.mEnabled) {
            this.mVolumeComponent.dump(fileDescriptor, printWriter, strArr);
        }
    }

    private void setDefaultVolumeController() {
        DndTile.setVisible(this.mContext, true);
        if (LOGD) {
            Log.d("VolumeUI", "Registering default volume controller");
        }
        this.mVolumeComponent.register();
    }
}
