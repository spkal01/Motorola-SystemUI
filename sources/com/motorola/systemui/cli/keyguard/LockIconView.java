package com.motorola.systemui.cli.keyguard;

import android.content.Context;
import android.content.res.Configuration;
import android.util.AttributeSet;
import android.widget.TextView;
import com.android.keyguard.KeyguardUpdateMonitor;
import com.android.systemui.Dependency;
import com.android.systemui.R$string;
import com.android.systemui.statusbar.policy.KeyguardStateController;
import java.util.Locale;

public class LockIconView extends TextView implements KeyguardStateController.Callback {
    private KeyguardStateController mKeyguardMonitor;
    private Locale mLocale;
    private KeyguardUpdateMonitor mUpdateMonitor;

    public LockIconView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, -1);
    }

    public LockIconView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mUpdateMonitor = (KeyguardUpdateMonitor) Dependency.get(KeyguardUpdateMonitor.class);
        this.mKeyguardMonitor = (KeyguardStateController) Dependency.get(KeyguardStateController.class);
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.mKeyguardMonitor.addCallback(this);
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.mKeyguardMonitor.removeCallback(this);
    }

    public void onKeyguardShowingChanged() {
        if (!this.mKeyguardMonitor.isMethodSecure() || !this.mKeyguardMonitor.isShowing() || this.mUpdateMonitor.getUserCanSkipBouncer(KeyguardUpdateMonitor.getCurrentUser())) {
            setVisibility(8);
        } else {
            setVisibility(0);
        }
    }

    /* access modifiers changed from: protected */
    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        Locale locale = getContext().getResources().getConfiguration().locale;
        if (!locale.equals(this.mLocale)) {
            this.mLocale = locale;
            setText(R$string.cli_locked_label);
        }
    }
}
