package com.motorola.systemui.prc.media;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.android.systemui.R$color;
import com.android.systemui.R$dimen;
import com.motorola.systemui.cli.media.CliMediaPageModel;
import com.motorola.systemui.cli.media.CliMediaViewForKeyguard;

public class MediaViewForExpanded extends CliMediaViewForKeyguard {
    private int mActionsMoreThanThreeMargin;
    private int mActionsPadding;
    private boolean mIsNightMode;

    public MediaViewForExpanded(Context context) {
        this(context, (AttributeSet) null);
    }

    public MediaViewForExpanded(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, -1);
    }

    public MediaViewForExpanded(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        boolean z = true;
        this.mControllerActive = true;
        this.mTintAtions = false;
        this.mOnlyShowCompactActions = false;
        this.mIsNightMode = (context.getResources().getConfiguration().uiMode & 48) != 32 ? false : z;
        this.mActionsPadding = context.getResources().getDimensionPixelSize(R$dimen.prc_media_expanded_actions_padding);
        this.mActionsMoreThanThreeMargin = context.getResources().getDimensionPixelSize(R$dimen.prc_media_expanded_action_more_than_3_layout_margin);
    }

    public void updateMediaInfo(CliMediaViewForKeyguard.MediaInfo mediaInfo) {
        super.updateMediaInfo(mediaInfo);
        Bitmap bitmap = mediaInfo.cover;
        if (bitmap != null) {
            this.mAlbumArt.setImageBitmap(bitmap);
        } else {
            this.mAlbumArt.setImageBitmap((Bitmap) null);
        }
    }

    public void setCliMediaPage(CliMediaPageModel cliMediaPageModel) {
        this.mMediaPage = cliMediaPageModel;
        updateThemeColors();
        updateActionsPadding();
    }

    /* access modifiers changed from: protected */
    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        boolean z = (configuration.uiMode & 48) == 32;
        if (this.mIsNightMode != z) {
            this.mIsNightMode = z;
            updateThemeColors();
        }
    }

    private void updateThemeColors() {
        int i;
        if (this.mMediaPage.getIsStandardMedia()) {
            if (this.mIsNightMode) {
                i = -1;
            } else {
                i = this.mContext.getResources().getColor(R$color.prc_media_tile_previous_next_icon_color);
            }
            for (ImageView colorFilter : this.mViewActions) {
                colorFilter.setColorFilter(i);
            }
        }
    }

    private void updateActionsPadding() {
        if (this.mMediaPage.getIsStandardMedia()) {
            boolean z = this.mMediaPage.getMediaActions().size() > 3;
            for (ImageView imageView : this.mViewActions) {
                int i = this.mActionsPadding;
                imageView.setPaddingRelative(i, i, i, i);
                if (z) {
                    LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) imageView.getLayoutParams();
                    layoutParams.setMarginStart(this.mActionsMoreThanThreeMargin);
                    layoutParams.setMarginEnd(this.mActionsMoreThanThreeMargin);
                    imageView.setLayoutParams(layoutParams);
                }
            }
        }
    }
}
