package com.android.settingslib.widget;

import android.content.Context;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.URLSpan;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;
import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;
import com.android.settingslib.utils.applications.AppUtils;

public class FooterPreference extends Preference {
    private CharSequence mContentDescription;
    private CharSequence mLearnMoreContentDescription;
    View.OnClickListener mLearnMoreListener;

    public FooterPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet, R$attr.footerPreferenceStyle);
        init();
    }

    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        TextView textView = (TextView) preferenceViewHolder.itemView.findViewById(16908310);
        textView.setMovementMethod(new LinkMovementMethod());
        textView.setClickable(false);
        textView.setLongClickable(false);
        if (!TextUtils.isEmpty(this.mContentDescription)) {
            textView.setContentDescription(this.mContentDescription);
        }
        TextView textView2 = (TextView) preferenceViewHolder.itemView.findViewById(R$id.settingslib_learn_more);
        if (AppUtils.IS_PRC_PRODUCT) {
            textView2.setVisibility(8);
        } else if (textView2 == null || this.mLearnMoreListener == null) {
            textView2.setVisibility(8);
        } else {
            textView2.setVisibility(0);
            SpannableString spannableString = new SpannableString(textView2.getText());
            spannableString.setSpan(new FooterLearnMoreSpan(this.mLearnMoreListener), 0, spannableString.length(), 0);
            textView2.setText(spannableString);
            if (!TextUtils.isEmpty(this.mLearnMoreContentDescription)) {
                textView2.setContentDescription(this.mLearnMoreContentDescription);
            }
        }
    }

    public void setSummary(CharSequence charSequence) {
        setTitle(charSequence);
    }

    public void setSummary(int i) {
        setTitle(i);
    }

    public CharSequence getSummary() {
        return getTitle();
    }

    /* access modifiers changed from: package-private */
    public CharSequence getContentDescription() {
        return this.mContentDescription;
    }

    /* access modifiers changed from: package-private */
    public CharSequence getLearnMoreContentDescription() {
        return this.mLearnMoreContentDescription;
    }

    private void init() {
        setLayoutResource(R$layout.preference_footer);
        if (getIcon() == null) {
            setIcon(R$drawable.settingslib_ic_info_outline_24);
        }
        setOrder(2147483646);
        if (TextUtils.isEmpty(getKey())) {
            setKey("footer_preference");
        }
    }

    static class FooterLearnMoreSpan extends URLSpan {
        private final View.OnClickListener mClickListener;

        FooterLearnMoreSpan(View.OnClickListener onClickListener) {
            super("");
            this.mClickListener = onClickListener;
        }

        public void onClick(View view) {
            View.OnClickListener onClickListener = this.mClickListener;
            if (onClickListener != null) {
                onClickListener.onClick(view);
            }
        }
    }
}
