package com.motorola.systemui.prc.media;

import android.app.PendingIntent;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import com.android.systemui.Dependency;
import com.android.systemui.R$color;
import com.android.systemui.R$dimen;
import com.android.systemui.R$drawable;
import com.android.systemui.R$id;
import com.android.systemui.media.dialog.MediaOutputDialogFactory;
import com.android.systemui.moto.DesktopFeature;
import com.android.systemui.plugins.FalsingManager;
import com.motorola.systemui.cli.media.CliMediaAction;
import com.motorola.systemui.cli.media.CliMediaPageModel;
import java.util.List;

public class MediaTileLayout extends ConstraintLayout implements View.OnClickListener {
    private static final boolean DEBUG = (!Build.IS_USER);
    private int mActionPadding;
    private TextView mArtist;
    private CliMediaPageModel mCurrentMediaModel;
    private FalsingManager mFalsingManager;
    private boolean mIsNightMode;
    private boolean mIsNoPlayer;
    private CardView mMediaTileBg;
    private ImageButton mNext;
    private TextView mNoPlayer;
    private ImageView mOutputIcon;
    private ImageButton mPlayPause;
    private ImageButton mPrevious;
    private TextView mSongName;

    public MediaTileLayout(Context context) {
        this(context, (AttributeSet) null);
    }

    public MediaTileLayout(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, -1);
    }

    public MediaTileLayout(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        boolean z = true;
        this.mIsNoPlayer = true;
        this.mIsNightMode = (context.getResources().getConfiguration().uiMode & 48) != 32 ? false : z;
        this.mActionPadding = context.getResources().getDimensionPixelSize(R$dimen.prc_media_tile_actions_padding_for_non_standard);
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        super.onFinishInflate();
        this.mMediaTileBg = (CardView) findViewById(R$id.media_tile_bg);
        this.mOutputIcon = (ImageView) findViewById(R$id.output_icon);
        this.mSongName = (TextView) findViewById(R$id.song_name);
        this.mArtist = (TextView) findViewById(R$id.artist);
        this.mNoPlayer = (TextView) findViewById(R$id.no_player);
        this.mPlayPause = (ImageButton) findViewById(R$id.play_pause);
        this.mPrevious = (ImageButton) findViewById(R$id.previous);
        this.mNext = (ImageButton) findViewById(R$id.next);
        this.mPlayPause.setOnClickListener(this);
        this.mPrevious.setOnClickListener(this);
        this.mNext.setOnClickListener(this);
        this.mMediaTileBg.setOnClickListener(this);
    }

    public void updateForNoPlayer() {
        this.mIsNoPlayer = true;
        this.mCurrentMediaModel = null;
        this.mOutputIcon.setImageResource(17303569);
        this.mSongName.setVisibility(4);
        this.mArtist.setVisibility(4);
        this.mNoPlayer.setVisibility(0);
        this.mPrevious.clearColorFilter();
        this.mPlayPause.clearColorFilter();
        this.mNext.clearColorFilter();
        this.mPrevious.setImageResource(R$drawable.prc_media_ic_previous_inactive);
        this.mPlayPause.setImageResource(R$drawable.prc_media_ic_play_inactive);
        this.mNext.setImageResource(R$drawable.prc_media_ic_next_inactive);
        setClickListenForAction(this.mPrevious, (CliMediaAction) null);
        setClickListenForAction(this.mPlayPause, (CliMediaAction) null);
        setClickListenForAction(this.mNext, (CliMediaAction) null);
        updateActionsPadding();
    }

    public void updateMediaTile(CliMediaPageModel cliMediaPageModel) {
        this.mCurrentMediaModel = cliMediaPageModel;
        if (cliMediaPageModel == null) {
            updateForNoPlayer();
            return;
        }
        this.mIsNoPlayer = false;
        this.mSongName.setVisibility(0);
        this.mArtist.setVisibility(0);
        this.mNoPlayer.setVisibility(4);
        this.mSongName.setText(cliMediaPageModel.getTrackTitle());
        this.mArtist.setText(cliMediaPageModel.getArtist());
        updateActions();
    }

    private void updateActions() {
        List<CliMediaAction> mediaActions = this.mCurrentMediaModel.getMediaActions();
        if (mediaActions == null || mediaActions.isEmpty()) {
            Log.w("MediaTileLayout", "This media is no actions");
        } else if (DEBUG) {
            Log.d("MediaTileLayout", "actions.size = " + mediaActions.size());
        }
        updatePreviousAction(mediaActions);
        updatePlayPauseAction(mediaActions);
        updateNextAction(mediaActions);
        updateActionsPadding();
    }

    private void updateActionsPadding() {
        CliMediaPageModel cliMediaPageModel = this.mCurrentMediaModel;
        if (cliMediaPageModel == null || !cliMediaPageModel.getIsStandardMedia()) {
            this.mPrevious.setPaddingRelative(0, 0, 0, 0);
            this.mPlayPause.setPaddingRelative(0, 0, 0, 0);
            this.mNext.setPaddingRelative(0, 0, 0, 0);
            return;
        }
        ImageButton imageButton = this.mPrevious;
        int i = this.mActionPadding;
        imageButton.setPaddingRelative(i, i, i, i);
        ImageButton imageButton2 = this.mPlayPause;
        int i2 = this.mActionPadding;
        imageButton2.setPaddingRelative(i2, i2, i2, i2);
        ImageButton imageButton3 = this.mNext;
        int i3 = this.mActionPadding;
        imageButton3.setPaddingRelative(i3, i3, i3, i3);
    }

    private void updatePreviousAction(List<CliMediaAction> list) {
        int i = 1;
        if (list.size() <= 1) {
            this.mPrevious.setVisibility(8);
            return;
        }
        if (list.size() <= 3) {
            i = 0;
        }
        if (list.size() > i) {
            this.mPrevious.setVisibility(0);
            CliMediaAction cliMediaAction = list.get(i);
            Drawable icon = cliMediaAction.getIcon();
            if (icon == null) {
                this.mPrevious.setVisibility(8);
                return;
            }
            this.mPrevious.setImageDrawable(icon);
            this.mPrevious.setColorFilter(this.mContext.getResources().getColor(R$color.prc_media_tile_previous_next_icon_color));
            setClickListenForAction(this.mPrevious, cliMediaAction);
            return;
        }
        setClickListenForAction(this.mPrevious, (CliMediaAction) null);
    }

    private void updatePlayPauseAction(List<CliMediaAction> list) {
        int i = 1;
        if (list.size() == 1) {
            i = 0;
        } else if (list.size() > 3) {
            i = 2;
        }
        if (list.size() > i) {
            this.mPlayPause.setVisibility(0);
            CliMediaAction cliMediaAction = list.get(i);
            Drawable icon = cliMediaAction.getIcon();
            if (icon == null) {
                this.mPlayPause.setVisibility(8);
                return;
            }
            this.mPlayPause.setImageDrawable(icon);
            if (this.mCurrentMediaModel.getIsStandardMedia()) {
                this.mPlayPause.setColorFilter(this.mContext.getResources().getColor(R$color.prc_media_tile_previous_next_icon_color));
            } else {
                this.mPlayPause.clearColorFilter();
                if (DEBUG) {
                    Log.d("MediaTileLayout", "MediaActive: " + this.mCurrentMediaModel.getMediaActive() + " App: " + this.mCurrentMediaModel.getAppName());
                }
                if (this.mCurrentMediaModel.getMediaActive()) {
                    this.mPlayPause.setImageResource(R$drawable.prc_media_ic_pause);
                } else {
                    this.mPlayPause.setImageResource(R$drawable.prc_media_ic_play);
                }
            }
            setClickListenForAction(this.mPlayPause, cliMediaAction);
            return;
        }
        setClickListenForAction(this.mPrevious, (CliMediaAction) null);
    }

    private void updateNextAction(List<CliMediaAction> list) {
        if (list.size() <= 1) {
            this.mNext.setVisibility(8);
            return;
        }
        int i = 2;
        if (list.size() > 3) {
            i = 3;
        }
        if (list.size() > i) {
            this.mNext.setVisibility(0);
            CliMediaAction cliMediaAction = list.get(i);
            Drawable icon = cliMediaAction.getIcon();
            if (icon == null) {
                this.mNext.setVisibility(8);
                return;
            }
            this.mNext.setImageDrawable(icon);
            this.mNext.setColorFilter(this.mContext.getResources().getColor(R$color.prc_media_tile_previous_next_icon_color));
            setClickListenForAction(this.mNext, cliMediaAction);
            return;
        }
        setClickListenForAction(this.mNext, (CliMediaAction) null);
    }

    private void setClickListenForAction(View view, CliMediaAction cliMediaAction) {
        if (cliMediaAction == null) {
            view.setOnClickListener((View.OnClickListener) null);
            return;
        }
        PendingIntent pendingIntent = cliMediaAction.getPendingIntent();
        Log.d("MediaTileLayout", "setClickListenForAction:   pi:" + pendingIntent);
        if (pendingIntent == null) {
            Log.i("MediaTileLayout", "This action doesn't have click intent");
        } else {
            view.setOnClickListener(new MediaTileLayout$$ExternalSyntheticLambda0(this, pendingIntent));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$setClickListenForAction$0(PendingIntent pendingIntent, View view) {
        try {
            if (this.mFalsingManager.isFalseTap(1)) {
                Log.i("MediaTileLayout", "setClickListenForAction isFalseTap");
                return;
            }
            performHaptic(view);
            pendingIntent.send();
        } catch (Exception unused) {
            Log.e("MediaTileLayout", "Intent canceled, unable to send remote input result.");
        }
    }

    private void performHaptic(View view) {
        view.performHapticFeedback(1);
    }

    public void onClick(View view) {
        if (this.mFalsingManager.isFalseTap(1)) {
            Log.i("MediaTileLayout", "onClick isFalseTap");
        } else if (view == this.mMediaTileBg && !this.mIsNoPlayer) {
            if (DesktopFeature.isDesktopDisplayContext(getContext())) {
                CliMediaPageModel cliMediaPageModel = this.mCurrentMediaModel;
                if (cliMediaPageModel != null) {
                    PendingIntent contentIntent = cliMediaPageModel.getContentIntent();
                    if (contentIntent != null) {
                        try {
                            contentIntent.send();
                        } catch (PendingIntent.CanceledException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Log.w("MediaTileLayout", "onClick pendingIntent is null");
                    }
                } else {
                    Log.w("MediaTileLayout", "onClick mCurrentMediaModel is null");
                }
            } else {
                ((MediaOutputDialogFactory) Dependency.get(MediaOutputDialogFactory.class)).showPrcMediaViewPagerWithOutput(this.mCurrentMediaModel.getPackageName(), true);
            }
        }
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

    public void updateThemeColors() {
        Log.i("MediaTileLayout", "updateThemeColors");
        Resources resources = this.mContext.getResources();
        this.mMediaTileBg.setCardBackgroundColor(resources.getColor(R$color.prc_media_tile_bg));
        this.mOutputIcon.setColorFilter(resources.getColor(R$color.prc_media_tile_output_icon_tint));
        this.mSongName.setTextColor(resources.getColor(R$color.prc_media_song_name));
        this.mArtist.setTextColor(resources.getColor(R$color.prc_media_artist));
        this.mNoPlayer.setTextColor(resources.getColor(R$color.prc_media_no_player));
        if (this.mCurrentMediaModel == null) {
            this.mPrevious.clearColorFilter();
            this.mPlayPause.clearColorFilter();
            this.mNext.clearColorFilter();
            this.mPrevious.setImageResource(R$drawable.prc_media_ic_previous_inactive);
            this.mPlayPause.setImageResource(R$drawable.prc_media_ic_play_inactive);
            this.mNext.setImageResource(R$drawable.prc_media_ic_next_inactive);
            return;
        }
        updateActions();
    }

    public void setFalsingManager(FalsingManager falsingManager) {
        this.mFalsingManager = falsingManager;
    }
}
