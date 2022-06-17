package com.motorola.systemui.cli.media;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

public class CliScrollTextView extends TextView {
    public boolean isFocused() {
        return true;
    }

    public CliScrollTextView(Context context) {
        super(context);
    }

    public CliScrollTextView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public CliScrollTextView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    public CliScrollTextView(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
    }
}
