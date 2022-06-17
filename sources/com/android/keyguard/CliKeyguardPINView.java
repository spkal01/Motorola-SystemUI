package com.android.keyguard;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import com.android.systemui.FontSizeUtils;
import com.android.systemui.R$dimen;
import com.android.systemui.R$id;
import com.android.systemui.moto.MotoFeature;

public class CliKeyguardPINView extends KeyguardPINView {
    private static final int[] NUMBER_KEY_IDS = {R$id.key0, R$id.key1, R$id.key2, R$id.key3, R$id.key4, R$id.key5, R$id.key6, R$id.key7, R$id.key8, R$id.key9};

    /* access modifiers changed from: protected */
    public boolean needsTranslationAnimation() {
        return false;
    }

    public CliKeyguardPINView(Context context) {
        this(context, (AttributeSet) null);
    }

    public CliKeyguardPINView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        super.onFinishInflate();
        setViews(new View[][]{new View[]{findViewById(R$id.row0), null, null}, new View[]{findViewById(R$id.key1), findViewById(R$id.key2), findViewById(R$id.key3)}, new View[]{findViewById(R$id.key4), findViewById(R$id.key5), findViewById(R$id.key6)}, new View[]{findViewById(R$id.key7), findViewById(R$id.key8), findViewById(R$id.key9)}, new View[]{findViewById(R$id.keyguard_selector_fade_container), findViewById(R$id.key0), findViewById(R$id.key_enter)}});
        for (int findViewById : NUMBER_KEY_IDS) {
            NumPadKey numPadKey = (NumPadKey) findViewById(findViewById);
            numPadKey.setLayoutHorizontally(true);
            FontSizeUtils.updateFontSize(numPadKey, R$id.digit_text, R$dimen.cli_number_pad_digit_font_size);
            FontSizeUtils.updateFontSize(numPadKey, R$id.klondike_text, R$dimen.cli_number_pad_klondike_font_size);
        }
        KeyguardMessageArea keyguardMessageArea = (KeyguardMessageArea) findViewById(R$id.keyguard_message_area);
        keyguardMessageArea.setTextColor(-1);
        FontSizeUtils.updateFontSize(keyguardMessageArea, R$dimen.cli_message_area_font_size);
        ((EmergencyButton) findViewById(R$id.emergency_call_button)).setSelected(true);
    }

    public void startAppearAnimation() {
        super.startAppearAnimation();
        animate().alpha(1.0f).setDuration(220);
    }

    public boolean startDisappearAnimation(Runnable runnable) {
        boolean startDisappearAnimation = super.startDisappearAnimation(runnable);
        animate().alpha(0.0f).setDuration(220);
        return startDisappearAnimation;
    }

    public boolean needDisappearAnim() {
        return MotoFeature.isLidClosed(this.mContext);
    }
}
