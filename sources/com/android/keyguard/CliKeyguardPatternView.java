package com.android.keyguard;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import com.android.internal.widget.LockPatternView;
import com.android.settingslib.animation.AppearAnimationUtils;
import com.android.settingslib.animation.DisappearAnimationUtils;
import com.android.systemui.FontSizeUtils;
import com.android.systemui.R$dimen;
import com.android.systemui.R$id;
import com.android.systemui.moto.MotoFeature;

public class CliKeyguardPatternView extends KeyguardPatternView {
    private View mButtonsRow;

    /* access modifiers changed from: protected */
    public boolean needsEmergencyAreaAnimation() {
        return true;
    }

    /* access modifiers changed from: protected */
    public boolean needsTranslationAnimation() {
        return false;
    }

    public CliKeyguardPatternView(Context context) {
        this(context, (AttributeSet) null);
    }

    public CliKeyguardPatternView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        super.onFinishInflate();
        KeyguardMessageArea keyguardMessageArea = (KeyguardMessageArea) findViewById(R$id.keyguard_message_area);
        keyguardMessageArea.setTextColor(-1);
        FontSizeUtils.updateFontSize(keyguardMessageArea, R$dimen.cli_message_area_font_size);
        LockPatternView findViewById = findViewById(R$id.lockPatternView);
        findViewById.setPathWidth(getSize(R$dimen.cli_pattern_path_width));
        findViewById.setDotSize(getSize(R$dimen.cli_pattern_dot_size_default), getSize(R$dimen.cli_pattern_dot_size_activated));
        ((EmergencyButton) findViewById(R$id.emergency_call_button)).setSelected(true);
        this.mButtonsRow = findViewById(R$id.buttons_row);
    }

    public void startAppearAnimation() {
        AppearAnimationUtils appearAnimationUtils = getAppearAnimationUtils();
        if (appearAnimationUtils != null) {
            appearAnimationUtils.createAnimation(this.mButtonsRow, 0, 220, appearAnimationUtils.getStartTranslation(), true, appearAnimationUtils.getInterpolator(), (Runnable) null);
        }
        super.startAppearAnimation();
        animate().alpha(1.0f).setDuration(220);
    }

    public boolean startDisappearAnimation(Runnable runnable) {
        DisappearAnimationUtils disappearAnimationUtils = getDisappearAnimationUtils();
        if (disappearAnimationUtils != null) {
            disappearAnimationUtils.createAnimation(this.mButtonsRow, 0, 220, -disappearAnimationUtils.getStartTranslation(), false, disappearAnimationUtils.getInterpolator(), (Runnable) null);
        }
        boolean startDisappearAnimation = super.startDisappearAnimation(runnable);
        animate().alpha(0.0f).setDuration(220);
        return startDisappearAnimation;
    }

    public boolean needDisappearAnim() {
        return MotoFeature.isLidClosed(this.mContext);
    }

    private int getSize(int i) {
        return getResources().getDimensionPixelSize(i);
    }
}
