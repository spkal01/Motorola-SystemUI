package com.android.systemui.usb;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.hardware.usb.UsbManager;
import android.hardware.usb.UsbPort;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;
import com.android.systemui.R$id;
import com.android.systemui.R$layout;
import com.android.systemui.R$string;

public class UsbContaminantActivity extends Activity implements View.OnClickListener {
    private TextView mEnableUsb;
    private TextView mGotIt;
    private TextView mLearnMore;
    private TextView mMessage;
    private TextView mTitle;
    private UsbPort mUsbPort;

    public void onCreate(Bundle bundle) {
        Window window = getWindow();
        window.addSystemFlags(524288);
        window.setType(2008);
        requestWindowFeature(1);
        super.onCreate(bundle);
        setContentView(R$layout.zz_moto_contaminant_dialog);
        this.mUsbPort = getIntent().getParcelableExtra("port").getUsbPort((UsbManager) getSystemService(UsbManager.class));
        this.mLearnMore = (TextView) findViewById(R$id.learnMore);
        this.mEnableUsb = (TextView) findViewById(R$id.enableUsb);
        this.mGotIt = (TextView) findViewById(R$id.gotIt);
        this.mTitle = (TextView) findViewById(R$id.title);
        this.mMessage = (TextView) findViewById(R$id.message);
        this.mTitle.setText(getString(R$string.zz_moto_usb_contaminant_title));
        this.mMessage.setText(getString(R$string.zz_moto_usb_contaminant_message));
        this.mEnableUsb.setText(getString(R$string.usb_disable_contaminant_detection));
        this.mGotIt.setText(getString(R$string.got_it));
        this.mLearnMore.setText(getString(R$string.learn_more));
        this.mEnableUsb.setOnClickListener(this);
        this.mGotIt.setOnClickListener(this);
        this.mLearnMore.setOnClickListener(this);
    }

    public void onWindowAttributesChanged(WindowManager.LayoutParams layoutParams) {
        super.onWindowAttributesChanged(layoutParams);
    }

    public void onClick(View view) {
        if (view == this.mEnableUsb) {
            try {
                this.mUsbPort.enableContaminantDetection(false);
                Toast.makeText(this, R$string.usb_port_enabled, 0).show();
            } catch (Exception e) {
                Log.e("UsbContaminantActivity", "Unable to notify Usb service", e);
            }
        } else if (view == this.mLearnMore) {
            Intent intent = new Intent();
            intent.setClassName("com.motorola.help", "com.motorola.feedback.devicehelp.ui.MotoHelpWebView");
            intent.putExtra("topic", "help_url_usb_contaminant_detected");
            try {
                startActivity(intent);
            } catch (ActivityNotFoundException unused) {
                Log.e("UsbContaminantActivity", "startActivity failed for " + intent);
            }
        }
        finish();
    }
}
