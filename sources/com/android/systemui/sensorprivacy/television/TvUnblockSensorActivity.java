package com.android.systemui.sensorprivacy.television;

import android.hardware.SensorPrivacyManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.systemui.R$id;
import com.android.systemui.R$string;
import com.android.systemui.p008tv.TvBottomSheetActivity;
import com.android.systemui.statusbar.policy.IndividualSensorPrivacyController;

public class TvUnblockSensorActivity extends TvBottomSheetActivity {
    private static final String TAG = "TvUnblockSensorActivity";
    private int mSensor = -1;
    private IndividualSensorPrivacyController.Callback mSensorPrivacyCallback;
    private final IndividualSensorPrivacyController mSensorPrivacyController;

    public TvUnblockSensorActivity(IndividualSensorPrivacyController individualSensorPrivacyController) {
        this.mSensorPrivacyController = individualSensorPrivacyController;
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        if (getIntent().getBooleanExtra(SensorPrivacyManager.EXTRA_ALL_SENSORS, false)) {
            this.mSensor = Integer.MAX_VALUE;
        } else {
            this.mSensor = getIntent().getIntExtra(SensorPrivacyManager.EXTRA_SENSOR, -1);
        }
        if (this.mSensor == -1) {
            Log.v(TAG, "Invalid extras");
            finish();
            return;
        }
        this.mSensorPrivacyCallback = new TvUnblockSensorActivity$$ExternalSyntheticLambda2(this);
        initUI();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onCreate$0(int i, boolean z) {
        int i2 = this.mSensor;
        if (i2 == Integer.MAX_VALUE) {
            if (!this.mSensorPrivacyController.isSensorBlocked(2) && !this.mSensorPrivacyController.isSensorBlocked(1)) {
                finish();
            }
        } else if (i2 == i && !z) {
            finish();
        }
    }

    private void initUI() {
        TextView textView = (TextView) findViewById(R$id.bottom_sheet_title);
        TextView textView2 = (TextView) findViewById(R$id.bottom_sheet_body);
        ImageView imageView = (ImageView) findViewById(R$id.bottom_sheet_icon);
        ImageView imageView2 = (ImageView) findViewById(R$id.bottom_sheet_second_icon);
        Button button = (Button) findViewById(R$id.bottom_sheet_positive_button);
        Button button2 = (Button) findViewById(R$id.bottom_sheet_negative_button);
        int i = this.mSensor;
        if (i == 1) {
            textView.setText(R$string.sensor_privacy_start_use_mic_dialog_title);
            textView2.setText(R$string.sensor_privacy_start_use_mic_dialog_content);
            imageView.setImageResource(17304027);
            imageView2.setVisibility(8);
        } else if (i != 2) {
            textView.setText(R$string.sensor_privacy_start_use_mic_camera_dialog_title);
            textView2.setText(R$string.sensor_privacy_start_use_mic_camera_dialog_content);
            imageView.setImageResource(17304022);
            imageView2.setImageResource(17304027);
        } else {
            textView.setText(R$string.sensor_privacy_start_use_camera_dialog_title);
            textView2.setText(R$string.sensor_privacy_start_use_camera_dialog_content);
            imageView.setImageResource(17304022);
            imageView2.setVisibility(8);
        }
        button.setText(17041410);
        button.setOnClickListener(new TvUnblockSensorActivity$$ExternalSyntheticLambda0(this));
        button2.setText(17039360);
        button2.setOnClickListener(new TvUnblockSensorActivity$$ExternalSyntheticLambda1(this));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$initUI$1(View view) {
        int i = this.mSensor;
        if (i == Integer.MAX_VALUE) {
            this.mSensorPrivacyController.setSensorBlocked(5, 2, false);
            this.mSensorPrivacyController.setSensorBlocked(5, 1, false);
            return;
        }
        this.mSensorPrivacyController.setSensorBlocked(5, i, false);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$initUI$2(View view) {
        finish();
    }

    public void onResume() {
        super.onResume();
        this.mSensorPrivacyController.addCallback(this.mSensorPrivacyCallback);
    }

    public void onPause() {
        this.mSensorPrivacyController.removeCallback(this.mSensorPrivacyCallback);
        super.onPause();
    }
}
