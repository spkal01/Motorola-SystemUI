package com.android.p011wm.shell.sizecompatui;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.RippleDrawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import com.android.p011wm.shell.C2219R;

/* renamed from: com.android.wm.shell.sizecompatui.SizeCompatRestartButton */
public class SizeCompatRestartButton extends FrameLayout implements View.OnClickListener, View.OnLongClickListener {
    private SizeCompatUILayout mLayout;

    public SizeCompatRestartButton(Context context) {
        super(context);
    }

    public SizeCompatRestartButton(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public SizeCompatRestartButton(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    public SizeCompatRestartButton(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
    }

    /* access modifiers changed from: package-private */
    public void inject(SizeCompatUILayout sizeCompatUILayout) {
        this.mLayout = sizeCompatUILayout;
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        super.onFinishInflate();
        ImageButton imageButton = (ImageButton) findViewById(C2219R.C2222id.size_compat_restart_button);
        ColorStateList valueOf = ColorStateList.valueOf(-3355444);
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setShape(1);
        gradientDrawable.setColor(valueOf);
        imageButton.setBackground(new RippleDrawable(valueOf, (Drawable) null, gradientDrawable));
        imageButton.setOnClickListener(this);
        imageButton.setOnLongClickListener(this);
    }

    public void onClick(View view) {
        this.mLayout.onRestartButtonClicked();
    }

    public boolean onLongClick(View view) {
        this.mLayout.onRestartButtonLongClicked();
        return true;
    }
}
