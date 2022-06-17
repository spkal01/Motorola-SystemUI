package com.android.p011wm.shell.pip.phone;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.ImageView;
import com.android.p011wm.shell.C2219R;

/* renamed from: com.android.wm.shell.pip.phone.PipMenuActionView */
public class PipMenuActionView extends FrameLayout {
    private ImageView mImageView;

    public PipMenuActionView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        super.onFinishInflate();
        this.mImageView = (ImageView) findViewById(C2219R.C2222id.image);
    }

    public void setImageDrawable(Drawable drawable) {
        this.mImageView.setImageDrawable(drawable);
    }
}
