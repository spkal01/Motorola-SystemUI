package com.android.systemui.screenrecord;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RadioGroup;

public class RecordingRadioGroup extends RadioGroup {
    private int mDefaultColor;

    public RecordingRadioGroup(Context context) {
        this(context, (AttributeSet) null);
    }

    public RecordingRadioGroup(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mDefaultColor = 1660944383;
    }

    public int getBrushColor(boolean z) {
        RecordingRadioButton recordingRadioButton;
        if (z || (recordingRadioButton = (RecordingRadioButton) findViewById(getCheckedRadioButtonId())) == null) {
            return this.mDefaultColor;
        }
        return recordingRadioButton.getBrushColor();
    }
}
