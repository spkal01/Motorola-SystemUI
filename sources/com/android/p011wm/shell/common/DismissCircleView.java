package com.android.p011wm.shell.common;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.widget.FrameLayout;
import android.widget.ImageView;
import com.android.p011wm.shell.C2219R;

/* renamed from: com.android.wm.shell.common.DismissCircleView */
public class DismissCircleView extends FrameLayout {
    private final ImageView mIconView;

    public DismissCircleView(Context context) {
        super(context);
        ImageView imageView = new ImageView(getContext());
        this.mIconView = imageView;
        Resources resources = getResources();
        setBackground(resources.getDrawable(C2219R.C2221drawable.dismiss_circle_background));
        imageView.setImageDrawable(resources.getDrawable(C2219R.C2221drawable.pip_ic_close_white));
        addView(imageView);
        setViewSizes();
    }

    /* access modifiers changed from: protected */
    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        setViewSizes();
    }

    private void setViewSizes() {
        int dimensionPixelSize = getResources().getDimensionPixelSize(C2219R.dimen.dismiss_target_x_size);
        this.mIconView.setLayoutParams(new FrameLayout.LayoutParams(dimensionPixelSize, dimensionPixelSize, 17));
    }
}
