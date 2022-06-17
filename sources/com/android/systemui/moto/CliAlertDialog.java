package com.android.systemui.moto;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Handler;
import android.os.UserHandle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;
import com.android.keyguard.EmergencyButton;
import com.android.keyguard.EmergencyButtonController;
import com.android.systemui.R$id;
import com.android.systemui.R$layout;
import com.android.systemui.R$style;
import com.android.systemui.statusbar.phone.SystemUIDialog;

public class CliAlertDialog extends SystemUIDialog {
    /* access modifiers changed from: private */
    public static final boolean DEBUG = Build.IS_DEBUGGABLE;
    private ImageButton mButtonDismiss;
    private Button mButtonNegative;
    private Button mButtonNeutral;
    private Button mButtonPositive;
    private CheckBox mCheckBox;
    private View mContentView;
    /* access modifiers changed from: private */
    public Context mContext;
    private EmergencyButton mEmergencyButton;
    private EmergencyButtonController mEmergencyButtonController;
    public EmergencyButtonController.Factory mEmergencyButtonControllerFactory;
    private boolean mEmergencyEnabled;
    private FlipReceiver mFlipReceiver;
    private Handler mHandler;
    private View mLayoutEmergency;
    private final Runnable mRunnableDismiss;
    private int mStyle;
    private TextView mTextMessage;
    private TextView mTextNotifyPrimary;
    private TextView mTextNotifySecondary;
    private TextView mTextTitle;

    /* access modifiers changed from: protected */
    public boolean needsDefaultDismissListener() {
        return false;
    }

    private class FlipReceiver extends BroadcastReceiver {
        private FlipReceiver() {
        }

        public void onReceive(Context context, Intent intent) {
            int lidState = getLidState(intent);
            if (CliAlertDialog.DEBUG) {
                Log.d("CliAlertDialog", "Received " + intent + ", lidState: " + lidState);
            }
            if (lidState == 1) {
                CliAlertDialog.this.lambda$new$0();
            }
        }

        public void register() {
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction("com.motorola.hardware.action.ACTION_LID_STATE_CHANGE");
            CliAlertDialog.this.mContext.registerReceiverAsUser(this, UserHandle.CURRENT, intentFilter, (String) null, (Handler) null);
        }

        public void unregister() {
            CliAlertDialog.this.mContext.unregisterReceiver(this);
        }

        private int getLidState(Intent intent) {
            if (intent != null) {
                return intent.getIntExtra("com.motorola.hardware.extra.LID_STATE", -1);
            }
            return -1;
        }
    }

    public static class Builder extends AlertDialog.Builder {
        private CliAlertDialog cliAlertDialog;

        public Builder(Context context) {
            this(context, false);
        }

        public Builder(Context context, boolean z) {
            super(context);
            this.cliAlertDialog = new CliAlertDialog(context, 0, z);
        }

        public Builder setTitle(int i) {
            this.cliAlertDialog.setTitle(i);
            return this;
        }

        public Builder setTitle(CharSequence charSequence) {
            this.cliAlertDialog.setTitle(charSequence);
            return this;
        }

        public Builder setMessage(int i) {
            this.cliAlertDialog.setMessage(i);
            return this;
        }

        public Builder setMessage(CharSequence charSequence) {
            this.cliAlertDialog.setMessage(charSequence);
            return this;
        }

        public Builder setPositiveButton(int i, DialogInterface.OnClickListener onClickListener) {
            this.cliAlertDialog.setPositiveButton(i, onClickListener);
            return this;
        }

        public Builder setNegativeButton(int i, DialogInterface.OnClickListener onClickListener) {
            this.cliAlertDialog.setNegativeButton(i, onClickListener);
            return this;
        }

        public Builder setNeutralButton(int i, DialogInterface.OnClickListener onClickListener) {
            this.cliAlertDialog.setNeutralButton(i, onClickListener);
            return this;
        }

        public Builder setOnDismissListener(DialogInterface.OnDismissListener onDismissListener) {
            this.cliAlertDialog.setOnDismissListener(onDismissListener);
            return this;
        }

        public CliAlertDialog create() {
            return this.cliAlertDialog;
        }
    }

    public CliAlertDialog(Context context, int i) {
        this(context, i, false);
    }

    public CliAlertDialog(Context context, int i, boolean z) {
        super(MotoFeature.getCliContext(context), R$style.CliAlertDialog);
        this.mStyle = 0;
        this.mRunnableDismiss = new CliAlertDialog$$ExternalSyntheticLambda6(this);
        this.mContext = getContext();
        this.mStyle = i;
        View inflate = getLayoutInflater().inflate(R$layout.cli_alert_dialog, (ViewGroup) null);
        this.mContentView = inflate;
        if (i == 1) {
            inflate.findViewById(R$id.alert_layout).setVisibility(8);
            this.mContentView.findViewById(R$id.notify_layout).setVisibility(0);
            this.mContentView.setOnClickListener(new CliAlertDialog$$ExternalSyntheticLambda0(this));
            this.mLayoutEmergency = this.mContentView.findViewById(R$id.emergency_layout);
            ImageButton imageButton = (ImageButton) this.mContentView.findViewById(R$id.dismiss_button);
            this.mButtonDismiss = imageButton;
            imageButton.setOnClickListener(new CliAlertDialog$$ExternalSyntheticLambda2(this));
            EmergencyButton emergencyButton = (EmergencyButton) this.mLayoutEmergency.findViewById(R$id.emergency_button);
            this.mEmergencyButton = emergencyButton;
            emergencyButton.setSelected(true);
            this.mEmergencyButton.setOnClickListener(new CliAlertDialog$$ExternalSyntheticLambda1(this));
            this.mTextNotifyPrimary = (TextView) this.mContentView.findViewById(R$id.notify_primary);
            this.mTextNotifySecondary = (TextView) this.mContentView.findViewById(R$id.notify_secondary);
        } else {
            inflate.findViewById(R$id.notify_layout).setVisibility(8);
            this.mContentView.findViewById(R$id.alert_layout).setVisibility(0);
            this.mTextTitle = (TextView) this.mContentView.findViewById(R$id.title);
            this.mTextMessage = (TextView) this.mContentView.findViewById(R$id.message);
            this.mButtonPositive = (Button) this.mContentView.findViewById(R$id.positive_button);
            this.mButtonNegative = (Button) this.mContentView.findViewById(R$id.negative_button);
            this.mButtonNeutral = (Button) this.mContentView.findViewById(R$id.neutral_button);
            this.mCheckBox = (CheckBox) this.mContentView.findViewById(R$id.check_box);
        }
        if (z) {
            SystemUIDialog.registerDismissListener(this);
            FlipReceiver flipReceiver = new FlipReceiver();
            this.mFlipReceiver = flipReceiver;
            flipReceiver.register();
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$1(View view) {
        if (!this.mEmergencyEnabled) {
            lambda$new$0();
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$2(View view) {
        lambda$new$0();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$3(View view) {
        lambda$new$0();
        EmergencyButtonController.Factory factory = this.mEmergencyButtonControllerFactory;
        if (factory != null) {
            if (this.mEmergencyButtonController == null) {
                this.mEmergencyButtonController = factory.create(this.mEmergencyButton);
            }
            this.mEmergencyButtonController.takeEmergencyCallAction();
        }
    }

    public void show() {
        super.show();
        setContentView(this.mContentView);
        if (this.mStyle == 1 && !this.mEmergencyEnabled) {
            startDismissRunnable();
        }
    }

    /* renamed from: dismiss */
    public void lambda$new$0() {
        super.dismiss();
        cancelDismissRunnable();
        FlipReceiver flipReceiver = this.mFlipReceiver;
        if (flipReceiver != null) {
            flipReceiver.unregister();
            this.mFlipReceiver = null;
        }
    }

    public void setTitle(int i) {
        setTitle((CharSequence) this.mContext.getString(i));
    }

    public void setTitle(CharSequence charSequence) {
        TextView textView = this.mTextTitle;
        if (textView != null) {
            textView.setText(charSequence);
            this.mTextTitle.setVisibility(!TextUtils.isEmpty(charSequence) ? 0 : 8);
        }
    }

    public void setMessage(int i) {
        setMessage((CharSequence) this.mContext.getString(i));
    }

    public void setMessage(CharSequence charSequence) {
        if (this.mStyle == 1) {
            setMessage(charSequence, (CharSequence) null);
            return;
        }
        TextView textView = this.mTextMessage;
        if (textView != null) {
            textView.setText(charSequence);
        }
    }

    public void setMessage(CharSequence charSequence, CharSequence charSequence2) {
        TextView textView = this.mTextNotifyPrimary;
        int i = 0;
        if (textView != null) {
            textView.setText(charSequence);
            this.mTextNotifyPrimary.setVisibility(!TextUtils.isEmpty(charSequence) ? 0 : 8);
        }
        TextView textView2 = this.mTextNotifySecondary;
        if (textView2 != null) {
            textView2.setText(charSequence2);
            TextView textView3 = this.mTextNotifySecondary;
            if (TextUtils.isEmpty(charSequence2)) {
                i = 8;
            }
            textView3.setVisibility(i);
        }
    }

    public void setPositiveButton(int i, DialogInterface.OnClickListener onClickListener) {
        Button button = this.mButtonPositive;
        if (button != null) {
            button.setText(this.mContext.getString(i));
            this.mButtonPositive.setVisibility(0);
            this.mButtonPositive.setOnClickListener(new CliAlertDialog$$ExternalSyntheticLambda4(this, onClickListener));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$setPositiveButton$4(DialogInterface.OnClickListener onClickListener, View view) {
        if (onClickListener != null) {
            onClickListener.onClick(this, -1);
        }
        lambda$new$0();
    }

    public void setNegativeButton(int i, DialogInterface.OnClickListener onClickListener) {
        Button button = this.mButtonNegative;
        if (button != null) {
            button.setText(this.mContext.getString(i));
            this.mButtonNegative.setVisibility(0);
            this.mButtonNegative.setOnClickListener(new CliAlertDialog$$ExternalSyntheticLambda5(this, onClickListener));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$setNegativeButton$5(DialogInterface.OnClickListener onClickListener, View view) {
        if (onClickListener != null) {
            onClickListener.onClick(this, -2);
        }
        lambda$new$0();
    }

    public void setNeutralButton(int i, DialogInterface.OnClickListener onClickListener) {
        Button button = this.mButtonNeutral;
        if (button != null) {
            button.setText(this.mContext.getString(i));
            this.mButtonNeutral.setVisibility(0);
            this.mButtonNeutral.setOnClickListener(new CliAlertDialog$$ExternalSyntheticLambda3(this, onClickListener));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$setNeutralButton$6(DialogInterface.OnClickListener onClickListener, View view) {
        if (onClickListener != null) {
            onClickListener.onClick(this, -3);
        }
        lambda$new$0();
    }

    public void setEmergencyEnabled(boolean z) {
        this.mEmergencyEnabled = z;
        View view = this.mLayoutEmergency;
        int i = 0;
        if (view != null) {
            view.setVisibility(z ? 0 : 8);
        }
        ImageButton imageButton = this.mButtonDismiss;
        if (imageButton != null) {
            if (!z) {
                i = 8;
            }
            imageButton.setVisibility(i);
        }
    }

    public static CliAlertDialog createNotifyDialog(Context context) {
        return new CliAlertDialog(context, 1);
    }

    public static boolean canShowOnCliDisplay(Context context) {
        return MotoFeature.getInstance(context).isSupportCli() && MotoFeature.isLidClosed(context);
    }

    private void startDismissRunnable() {
        if (this.mHandler == null) {
            this.mHandler = new Handler();
        }
        this.mHandler.removeCallbacks(this.mRunnableDismiss);
        this.mHandler.postDelayed(this.mRunnableDismiss, 3000);
    }

    private void cancelDismissRunnable() {
        Handler handler = this.mHandler;
        if (handler != null) {
            handler.removeCallbacks(this.mRunnableDismiss);
        }
    }
}
