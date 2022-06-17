package com.android.systemui.screenrecord;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import com.android.systemui.R$string;

public class ScreenRecordDialog extends Activity implements DialogInterface.OnClickListener, DialogInterface.OnDismissListener {
    public ScreenRecordDialog(RecordingController recordingController) {
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        if (!RecordingUtils.isRecordMemEnough()) {
            Toast.makeText(this, R$string.screenrecord_memory_lower, 1).show();
            finish();
        }
        RecordingSettings.sCameraStatus = getIntent().getBooleanExtra("extra_showCamera", false);
        Log.d("Recording_Dialog", "onCreate RecordingSettings.sCameraStatus=" + RecordingSettings.sCameraStatus);
        AlertDialog create = new AlertDialog.Builder(this).setTitle(R$string.screenrecord_start_label).setMessage(R$string.screenrecord_description).setNegativeButton(R$string.cancel, this).setPositiveButton(R$string.screenrecord_start, this).setOnDismissListener(this).create();
        create.getWindow().addSystemFlags(524288);
        create.show();
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        if (i == -1) {
            requestScreenCapture();
        }
        finish();
    }

    public void onDismiss(DialogInterface dialogInterface) {
        finish();
    }

    private void requestScreenCapture() {
        RecordingSettings.setScreenRecordingStatus(this, 1);
        new ScreenRecordPanel(this).show();
    }
}
