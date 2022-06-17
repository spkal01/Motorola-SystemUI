package com.android.settingslib.widget;

import android.content.Context;
import android.util.AttributeSet;
import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;

public class TopIntroPreference extends Preference {
    public TopIntroPreference(Context context) {
        super(context);
        setLayoutResource(R$layout.top_intro_preference);
        setSelectable(false);
    }

    public TopIntroPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        setLayoutResource(R$layout.top_intro_preference);
        setSelectable(false);
    }

    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        preferenceViewHolder.setDividerAllowedAbove(false);
        preferenceViewHolder.setDividerAllowedBelow(false);
    }
}
