package com.android.keyguard;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import com.android.systemui.R$attr;
import com.android.systemui.R$dimen;
import com.android.systemui.R$id;
import com.android.systemui.R$layout;
import com.motorola.android.provider.MotorolaSettings;

public class PresentationNumPadKey extends NumPadKey {
    private float mFontScale;
    private View.OnClickListener mListener;
    /* access modifiers changed from: private */
    public PresentationKgPINView mPresentationKgPINView;

    public PresentationNumPadKey(Context context) {
        this(context, (AttributeSet) null);
    }

    public PresentationNumPadKey(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, R$attr.numPadKeyStyle);
    }

    public PresentationNumPadKey(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i, R$layout.kg_pt_keyguard_num_pad_key);
        this.mListener = new View.OnClickListener() {
            public void onClick(View view) {
                View findViewById;
                PresentationNumPadKey presentationNumPadKey = PresentationNumPadKey.this;
                if (presentationNumPadKey.mTextView == null && presentationNumPadKey.mTextViewResId > 0 && (findViewById = presentationNumPadKey.getRootView().findViewById(PresentationNumPadKey.this.mTextViewResId)) != null && (findViewById instanceof PasswordTextView)) {
                    PresentationNumPadKey.this.mTextView = (PasswordTextView) findViewById;
                }
                PasswordTextView passwordTextView = PresentationNumPadKey.this.mTextView;
                if (passwordTextView != null && passwordTextView.isEnabled()) {
                    PresentationNumPadKey presentationNumPadKey2 = PresentationNumPadKey.this;
                    presentationNumPadKey2.mTextView.append(Character.forDigit(presentationNumPadKey2.mDigit, 10));
                    if (PresentationNumPadKey.this.mPresentationKgPINView == null) {
                        PresentationNumPadKey presentationNumPadKey3 = PresentationNumPadKey.this;
                        PresentationKgPINView unused = presentationNumPadKey3.mPresentationKgPINView = (PresentationKgPINView) presentationNumPadKey3.getRootView().findViewById(R$id.keyguard_pin_view);
                    }
                    PresentationNumPadKey.this.mPresentationKgPINView.updateBottomMessage(false);
                }
                PresentationNumPadKey.this.userActivity();
            }
        };
        this.mFontScale = MotorolaSettings.Global.getFloat(this.mContext.getContentResolver(), "desktop_font_size_scale", 1.0f);
        this.mKlondikeText.setVisibility(4);
        setOnClickListener(this.mListener);
        float caculateMultiple = PtDisplayFontUtils.caculateMultiple(this.mFontScale, context.getDisplay());
        this.mDigitText.setTextSize(0, ((((float) context.getResources().getDimensionPixelSize(R$dimen.pt_kg_numPadKey_text_size)) * caculateMultiple) * ((float) PtDisplayFontUtils.getScreenHeight(context.getDisplay()))) / 1080.0f);
    }
}
