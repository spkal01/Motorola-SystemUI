package com.motorola.systemui.cli.media;

import android.content.Context;
import android.graphics.drawable.ShapeDrawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.android.systemui.Prefs;
import com.android.systemui.R$color;
import com.android.systemui.R$id;
import com.android.systemui.R$string;
import com.android.systemui.recents.TriangleShape;

public class CliMediaTutorialLayout extends LinearLayout {
    /* access modifiers changed from: private */
    public CliMediaViewPagerOwn mCliMediaViewPagerOwn;
    private View.OnClickListener mCloseListener;
    private ImageView mTutorialClose;
    private TextView mTutorialTextView;
    private ImageView mUpwardTriangle;

    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        return false;
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        return true;
    }

    public CliMediaTutorialLayout(Context context) {
        this(context, (AttributeSet) null);
    }

    public CliMediaTutorialLayout(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, -1);
    }

    public CliMediaTutorialLayout(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mCloseListener = new View.OnClickListener() {
            public void onClick(View view) {
                if (CliMediaTutorialLayout.this.mCliMediaViewPagerOwn == CliMediaViewPagerOwn.Keyguard) {
                    Prefs.putInt(CliMediaTutorialLayout.this.mContext, "CliKeyguardMediaPanelTutorial", 1);
                } else if (CliMediaTutorialLayout.this.mCliMediaViewPagerOwn == CliMediaViewPagerOwn.QS) {
                    Prefs.putInt(CliMediaTutorialLayout.this.mContext, "CliQsMediaPanelTutorial", 1);
                }
                CliMediaTutorialLayout.this.setVisibility(8);
            }
        };
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        super.onFinishInflate();
        this.mUpwardTriangle = (ImageView) findViewById(R$id.upward_triangle);
        this.mTutorialTextView = (TextView) findViewById(R$id.tutorial_text);
        ImageView imageView = (ImageView) findViewById(R$id.tutorial_close);
        this.mTutorialClose = imageView;
        imageView.setOnClickListener(this.mCloseListener);
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        CliMediaViewPagerOwn cliMediaViewPagerOwn = this.mCliMediaViewPagerOwn;
        if (cliMediaViewPagerOwn == CliMediaViewPagerOwn.Keyguard) {
            this.mTutorialTextView.setText(R$string.cli_keyguard_media_panel_tutorial);
        } else if (cliMediaViewPagerOwn == CliMediaViewPagerOwn.QS) {
            this.mTutorialTextView.setText(R$string.cli_qs_media_panel_tutorial);
            setPaddingRelative(0, 130, 0, 0);
        }
        ViewGroup.LayoutParams layoutParams = this.mUpwardTriangle.getLayoutParams();
        ShapeDrawable shapeDrawable = new ShapeDrawable(TriangleShape.create((float) layoutParams.width, (float) layoutParams.height, true));
        shapeDrawable.getPaint().setColor(this.mContext.getColor(R$color.cli_education_color));
        this.mUpwardTriangle.setBackground(shapeDrawable);
    }

    public void setCliMediaViewPagerOwn(CliMediaViewPagerOwn cliMediaViewPagerOwn) {
        this.mCliMediaViewPagerOwn = cliMediaViewPagerOwn;
    }

    public void updateTutorialVisibility(int i, CliMediaPagerHeightState cliMediaPagerHeightState) {
        if (cliMediaPagerHeightState == CliMediaPagerHeightState.Small) {
            setVisibility(8);
        } else if (i == 0) {
            CliMediaViewPagerOwn cliMediaViewPagerOwn = this.mCliMediaViewPagerOwn;
            if (cliMediaViewPagerOwn == CliMediaViewPagerOwn.Keyguard) {
                if (Prefs.getInt(this.mContext, "CliKeyguardMediaPanelTutorial", 0) != 1) {
                    setVisibility(0);
                } else {
                    setVisibility(8);
                }
            } else if (cliMediaViewPagerOwn != CliMediaViewPagerOwn.QS) {
            } else {
                if (Prefs.getInt(this.mContext, "CliQsMediaPanelTutorial", 0) != 1) {
                    setVisibility(0);
                } else {
                    setVisibility(8);
                }
            }
        } else {
            setVisibility(i);
        }
    }
}
