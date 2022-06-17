package com.motorola.systemui.cli.media;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.session.MediaController;
import android.media.session.PlaybackState;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.format.DateUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.motion.widget.MotionLayout;
import com.android.systemui.Dependency;
import com.android.systemui.R$animator;
import com.android.systemui.R$drawable;
import com.android.systemui.R$id;
import com.android.systemui.statusbar.phone.CliStatusBar;
import com.android.systemui.statusbar.phone.StatusBar;
import com.android.systemui.util.Utils;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class CliMediaViewForQS extends MotionLayout implements CliMediaView, SeekBar.OnSeekBarChangeListener, MotionLayout.TransitionListener {
    private static final boolean DEBUG = (!Build.IS_USER);
    /* access modifiers changed from: private */
    public boolean isSeekBarTouching;
    private ImageView mAction0;
    private ImageView mAction1;
    private ImageView mAction2;
    private ImageView mAction3;
    private ImageView mAction4;
    private LinearLayout mActionsLayout;
    private ImageView mAlbumArt;
    private ImageView mAppIcon;
    private TextView mAppName;
    private ImageView mArrow;
    /* access modifiers changed from: private */
    public TextView mArtist;
    private MediaViewCallback mCallback;
    private CliStatusBar mCliStatusBar;
    private ImageGradientColorizer mColorizer;
    private Context mContext;
    private BitmapDrawable mCurrentDrawable;
    private String mCurrentMediaText;
    private boolean mExpanded;
    /* access modifiers changed from: private */
    public Handler mMainThreadHandler;
    private boolean mMediaActive;
    private View mMediaClose;
    /* access modifiers changed from: private */
    public TextView mMediaElapsedTime;
    private MediaInfo mMediaInfo;
    private CliMediaOutputRouteLayout mMediaOutputLayout;
    /* access modifiers changed from: private */
    public CliMediaPageModel mMediaPage;
    private boolean mMediaPanelScrolling;
    /* access modifiers changed from: private */
    public SeekBar mMediaProgressBar;
    private FrameLayout mMediaProgressTimeLayout;
    private LinearLayout mMediaSeamless;
    private ImageView mMediaSeamlessImage;
    private TextView mMediaSeamlessText;
    private TextView mMediaTotalTime;
    private CardView mMediaViewBg;
    private Bitmap mOriginalCover;
    private TextView mSongName;
    private long mStartTime;
    private float mStartX;
    private float mStartY;
    private StatusBar mStatusBar;
    /* access modifiers changed from: private */
    public Timer mTimer;
    private ImageView[] mViewActions;

    interface MediaViewCallback {
        void onMediaCloseClicked(String str, String str2);
    }

    public void onProgressChanged(SeekBar seekBar, int i, boolean z) {
    }

    public CliMediaViewForQS(Context context) {
        this(context, (AttributeSet) null);
    }

    public CliMediaViewForQS(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, -1);
    }

    public CliMediaViewForQS(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mMainThreadHandler = new Handler(Looper.getMainLooper());
        this.mMediaInfo = new MediaInfo();
        this.mCurrentDrawable = null;
        this.mCurrentMediaText = null;
        this.mContext = context;
        this.mStatusBar = (StatusBar) Dependency.get(StatusBar.class);
        this.mCliStatusBar = (CliStatusBar) Dependency.get(CliStatusBar.class);
        this.mColorizer = new ImageGradientColorizer();
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        super.onFinishInflate();
        this.mAppIcon = (ImageView) findViewById(R$id.icon);
        this.mAppName = (TextView) findViewById(R$id.app_name);
        this.mAlbumArt = (ImageView) findViewById(R$id.album_art);
        this.mSongName = (TextView) findViewById(R$id.song_name);
        this.mArtist = (TextView) findViewById(R$id.artist);
        this.mMediaProgressBar = (SeekBar) findViewById(R$id.media_progress_bar);
        this.mMediaProgressTimeLayout = (FrameLayout) findViewById(R$id.cli_media_progress_time);
        this.mMediaElapsedTime = (TextView) findViewById(R$id.media_elapsed_time);
        this.mMediaTotalTime = (TextView) findViewById(R$id.media_total_time);
        this.mMediaSeamless = (LinearLayout) findViewById(R$id.media_seamless);
        this.mMediaSeamlessImage = (ImageView) findViewById(R$id.media_seamless_image);
        this.mMediaSeamlessText = (TextView) findViewById(R$id.media_seamless_text);
        this.mMediaClose = findViewById(R$id.media_close);
        this.mActionsLayout = (LinearLayout) findViewById(R$id.actions);
        this.mAction0 = (ImageView) findViewById(R$id.action0);
        this.mAction1 = (ImageView) findViewById(R$id.action1);
        this.mAction2 = (ImageView) findViewById(R$id.action2);
        this.mAction3 = (ImageView) findViewById(R$id.action3);
        this.mAction4 = (ImageView) findViewById(R$id.action4);
        this.mMediaViewBg = (CardView) findViewById(R$id.media_view_bg);
        this.mArrow = (ImageView) findViewById(R$id.arrow);
        this.mViewActions = new ImageView[]{this.mAction0, this.mAction1, this.mAction2, this.mAction3, this.mAction4};
        this.mActionsLayout.setLayoutDirection(0);
        this.mMediaProgressBar.setLayoutDirection(0);
        this.mMediaProgressTimeLayout.setLayoutDirection(0);
        this.mMediaProgressBar.setOnSeekBarChangeListener(this);
        transitionToStart();
        setTransitionListener(this);
    }

    public void onStartTrackingTouch(SeekBar seekBar) {
        this.isSeekBarTouching = true;
    }

    public void onStopTrackingTouch(SeekBar seekBar) {
        this.isSeekBarTouching = false;
        MediaController mediaController = this.mMediaPage.getMediaController();
        if (mediaController != null) {
            mediaController.getTransportControls().seekTo((long) seekBar.getProgress());
        }
    }

    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        Log.e("CLI-QSMV-CliMediaViewForQS", "CLI-QSMV-CliMediaViewForQS: onInterceptTouchEvent");
        return super.onInterceptTouchEvent(motionEvent);
    }

    public boolean dispatchTouchEvent(MotionEvent motionEvent) {
        int actionMasked = motionEvent.getActionMasked();
        if (actionMasked == 0) {
            this.mStartX = motionEvent.getX();
            this.mStartY = motionEvent.getY();
            this.mStartTime = System.currentTimeMillis();
        } else if (actionMasked == 1 && isAClick(motionEvent.getX(), motionEvent.getY()) && handleClickEvent(motionEvent)) {
            Log.i("CLI-QSMV-CliMediaViewForQS", "Handle click event, stop dispatch the touch events.");
            return true;
        }
        return super.dispatchTouchEvent(motionEvent);
    }

    private boolean handleClickEvent(MotionEvent motionEvent) {
        if (DEBUG) {
            Log.d("CLI-QSMV-CliMediaViewForQS", "Touch point: (x, y) = (" + motionEvent.getRawX() + ", " + motionEvent.getRawY() + ")");
        }
        if (this.mMediaPanelScrolling || touchEventOnMediaProgressBar(motionEvent)) {
            return false;
        }
        if (touchEventOnAppIconAndName(motionEvent) || touchEventOnActions(motionEvent) || touchEventOnMediaSeamless(motionEvent) || touchEventOnMediaClose(motionEvent) || touchEventOnPanelBgView(motionEvent)) {
            return true;
        }
        return false;
    }

    private boolean touchEventInsideTargetView(View view, MotionEvent motionEvent) {
        if (view.getVisibility() != 0) {
            return false;
        }
        int[] iArr = new int[2];
        view.getLocationOnScreen(iArr);
        if (DEBUG) {
            Log.d("CLI-QSMV-CliMediaViewForQS", "View: x: [" + iArr[0] + ", " + (iArr[0] + view.getWidth()) + "] y: [" + iArr[1] + ", " + (iArr[1] + view.getHeight()) + "]  Touch on view: " + view);
        }
        if (motionEvent.getRawX() <= ((float) iArr[0]) || motionEvent.getRawX() >= ((float) (iArr[0] + view.getWidth())) || motionEvent.getRawY() <= ((float) iArr[1]) || motionEvent.getRawY() >= ((float) (iArr[1] + view.getHeight()))) {
            return false;
        }
        return true;
    }

    private boolean touchEventOnPanelBgView(MotionEvent motionEvent) {
        boolean z = touchEventInsideTargetView(this.mMediaViewBg, motionEvent);
        if (z) {
            if (this.mExpanded) {
                transitionToStart();
            } else {
                transitionToEnd();
            }
        }
        return z;
    }

    private boolean touchEventOnActions(MotionEvent motionEvent) {
        int i = 0;
        while (true) {
            ImageView[] imageViewArr = this.mViewActions;
            if (i >= imageViewArr.length) {
                return false;
            }
            if (touchEventInsideTargetView(imageViewArr[i], motionEvent)) {
                PendingIntent pendingIntent = this.mMediaPage.getMediaActions().get(i).getPendingIntent();
                if (pendingIntent == null) {
                    Log.i("CLI-QSMV-CliMediaViewForQS", "The action doesn't have click inent.");
                }
                try {
                    performHaptic(this.mViewActions[i]);
                    pendingIntent.send();
                    return true;
                } catch (PendingIntent.CanceledException unused) {
                    Log.e("CLI-QSMV-CliMediaViewForQS", "Intent canceled, unable to send remote input result.");
                    return true;
                }
            } else {
                i++;
            }
        }
    }

    private boolean touchEventOnAppIconAndName(MotionEvent motionEvent) {
        boolean z = touchEventInsideTargetView(this.mAppIcon, motionEvent) || touchEventInsideTargetView(this.mAppName, motionEvent);
        if (z) {
            CliStatusBar cliStatusBar = this.mCliStatusBar;
            if (cliStatusBar != null) {
                cliStatusBar.animateExpandedVisible(false);
            }
            StatusBar statusBar = this.mStatusBar;
            if (statusBar != null) {
                statusBar.triggerNotificationClickAndRequestUnlockInternal(this.mMediaPage.getKey(), this.mMediaPage.getContentIntent(), (Intent) null);
            }
        }
        return z;
    }

    private boolean touchEventOnMediaProgressBar(MotionEvent motionEvent) {
        return touchEventInsideTargetView(this.mMediaProgressBar, motionEvent) || touchEventInsideTargetView(this.mMediaProgressTimeLayout, motionEvent);
    }

    private boolean touchEventOnMediaSeamless(MotionEvent motionEvent) {
        boolean z = touchEventInsideTargetView(this.mMediaSeamless, motionEvent);
        if (z) {
            if (this.mMediaOutputLayout == null) {
                this.mMediaOutputLayout = (CliMediaOutputRouteLayout) getRootView().findViewById(R$id.media_output_layout);
            }
            this.mMediaOutputLayout.setCover(this.mMediaInfo.cover);
            this.mMediaOutputLayout.setTrackTitle(this.mMediaInfo.trackTitle);
            this.mMediaOutputLayout.setArtist(this.mMediaInfo.artist);
            this.mMediaOutputLayout.setVisibility(0);
        }
        return z;
    }

    private boolean touchEventOnMediaClose(MotionEvent motionEvent) {
        MediaViewCallback mediaViewCallback;
        boolean z = touchEventInsideTargetView(this.mMediaClose, motionEvent);
        if (z && (mediaViewCallback = this.mCallback) != null) {
            mediaViewCallback.onMediaCloseClicked(this.mMediaPage.getKey(), getPackageName());
        }
        return z;
    }

    private boolean isAClick(float f, float f2) {
        return Math.abs(this.mStartX - f) < 20.0f && Math.abs(this.mStartY - f2) < 20.0f && System.currentTimeMillis() - this.mStartTime < 400;
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    /* access modifiers changed from: protected */
    public Parcelable onSaveInstanceState() {
        SavedState savedState = new SavedState(super.onSaveInstanceState());
        savedState.appName = this.mMediaInfo.appName;
        savedState.artist = this.mMediaInfo.artist;
        savedState.trackTitle = this.mMediaInfo.trackTitle;
        savedState.albumTitle = this.mMediaInfo.albumTitle;
        savedState.cover = this.mMediaInfo.cover;
        savedState.appIcon = this.mMediaInfo.appIcon;
        savedState.currentPosition = this.mMediaInfo.currentPosition;
        savedState.mediaDuration = this.mMediaInfo.mediaDuration;
        savedState.bgColor = this.mMediaInfo.bgColor;
        return savedState;
    }

    /* access modifiers changed from: protected */
    public void onRestoreInstanceState(Parcelable parcelable) {
        if (!(parcelable instanceof SavedState)) {
            super.onRestoreInstanceState(parcelable);
            return;
        }
        SavedState savedState = (SavedState) parcelable;
        super.onRestoreInstanceState(savedState.getSuperState());
        String unused = this.mMediaInfo.artist = savedState.artist;
        String unused2 = this.mMediaInfo.trackTitle = savedState.trackTitle;
        String unused3 = this.mMediaInfo.albumTitle = savedState.albumTitle;
        Bitmap unused4 = this.mMediaInfo.cover = savedState.cover;
        Drawable unused5 = this.mMediaInfo.appIcon = savedState.appIcon;
        int unused6 = this.mMediaInfo.currentPosition = savedState.currentPosition;
        int unused7 = this.mMediaInfo.mediaDuration = savedState.mediaDuration;
        int unused8 = this.mMediaInfo.bgColor = savedState.bgColor;
        updateMediaInfoView();
    }

    public void setCliMediaPage(CliMediaPageModel cliMediaPageModel) {
        this.mMediaPage = cliMediaPageModel;
    }

    public String getPackageName() {
        return this.mMediaPage.getPackageName();
    }

    public void updateMediaPage(CliMediaPageModel cliMediaPageModel) {
        Drawable unused = this.mMediaInfo.appIcon = cliMediaPageModel.getAppIcon();
        String unused2 = this.mMediaInfo.appName = cliMediaPageModel.getAppName();
        String unused3 = this.mMediaInfo.trackTitle = cliMediaPageModel.getTrackTitle();
        String unused4 = this.mMediaInfo.artist = cliMediaPageModel.getArtist();
        Bitmap unused5 = this.mMediaInfo.cover = cliMediaPageModel.getImage();
        int unused6 = this.mMediaInfo.currentPosition = cliMediaPageModel.getCurrentPosition();
        int unused7 = this.mMediaInfo.mediaDuration = cliMediaPageModel.getMediaDuration();
        int unused8 = this.mMediaInfo.bgColor = cliMediaPageModel.getBackgroundColor();
        updateMediaInfoView();
        updateMediaPageActions(getContext(), cliMediaPageModel.getMediaActions());
        updateBackground(cliMediaPageModel.getBackgroundColor());
        updateMediaPlayingTime(this.mMediaActive, cliMediaPageModel.getMediaActive());
        this.mMediaActive = cliMediaPageModel.getMediaActive();
    }

    public void updateMediaOutputName(String str) {
        if (DEBUG) {
            Log.i("CLI-QSMV-CliMediaViewForQS", "updateMediaOutputName: name=" + str);
        }
        this.mMediaSeamlessText.setText(str);
    }

    public void updateMediaOutputName(int i) {
        if (DEBUG) {
            Log.i("CLI-QSMV-CliMediaViewForQS", "updateMediaOutputName: name=Switch output");
        }
        this.mMediaSeamlessText.setText(i);
    }

    public void updateMediaOutputIcon(int i) {
        this.mMediaSeamlessImage.setImageResource(i);
    }

    private void updateMediaPlayingTime(boolean z, boolean z2) {
        if (z != z2) {
            if (z2) {
                Timer timer = new Timer();
                this.mTimer = timer;
                timer.schedule(new TimerTask() {
                    public void run() {
                        MediaController mediaController = CliMediaViewForQS.this.mMediaPage.getMediaController();
                        if (mediaController == null) {
                            CliMediaViewForQS.this.mTimer.cancel();
                            CliMediaViewForQS.this.mTimer.purge();
                            return;
                        }
                        PlaybackState playbackState = mediaController.getPlaybackState();
                        if (playbackState == null) {
                            CliMediaViewForQS.this.mTimer.cancel();
                            CliMediaViewForQS.this.mTimer.purge();
                            return;
                        }
                        long position = playbackState.getPosition();
                        if (!CliMediaViewForQS.this.isSeekBarTouching) {
                            CliMediaViewForQS.this.mMainThreadHandler.post(new CliMediaViewForQS$1$$ExternalSyntheticLambda0(this, position));
                        }
                    }

                    /* access modifiers changed from: private */
                    public /* synthetic */ void lambda$run$0(long j) {
                        CliMediaViewForQS.this.mMediaProgressBar.setProgress((int) j);
                        CliMediaViewForQS.this.mMediaElapsedTime.setText(DateUtils.formatElapsedTime(j / 1000));
                    }
                }, 0, 1000);
                return;
            }
            int unused = this.mMediaInfo.currentPosition = this.mMediaProgressBar.getProgress();
            this.mTimer.cancel();
            this.mTimer.purge();
        }
    }

    private void updateBackground(int i) {
        this.mMediaViewBg.setCardBackgroundColor(i);
    }

    private void updateMediaPageActions(Context context, List<CliMediaAction> list) {
        if (list == null || list.size() == 0) {
            Log.w("CLI-QSMV-CliMediaViewForQS", "the media no actions.");
            return;
        }
        int size = list.size();
        int i = 0;
        while (true) {
            ImageView[] imageViewArr = this.mViewActions;
            if (i < imageViewArr.length) {
                ImageView imageView = imageViewArr[i];
                imageView.setVisibility(0);
                if (i >= size) {
                    imageView.setVisibility(8);
                } else {
                    CliMediaAction cliMediaAction = list.get(i);
                    Drawable icon = cliMediaAction.getIcon();
                    if (icon == null) {
                        imageView.setVisibility(8);
                    } else {
                        imageView.setImageDrawable(icon);
                        imageView.setColorFilter(-1);
                        if (!this.mExpanded && !cliMediaAction.getCompactState()) {
                            imageView.setVisibility(8);
                        }
                    }
                }
                i++;
            } else {
                return;
            }
        }
    }

    private void performHaptic(View view) {
        view.performHapticFeedback(1);
    }

    private void updateMediaInfo(MediaInfo mediaInfo) {
        this.mAppIcon.setImageDrawable(mediaInfo.appIcon);
        this.mAppName.setText(mediaInfo.appName);
        this.mSongName.setText(mediaInfo.trackTitle);
        this.mMediaElapsedTime.setText(DateUtils.formatElapsedTime(((long) mediaInfo.currentPosition) / 1000));
        this.mMediaTotalTime.setText(DateUtils.formatElapsedTime(((long) mediaInfo.mediaDuration) / 1000));
        this.mMediaProgressBar.setMax(mediaInfo.mediaDuration);
        this.mMediaProgressBar.setProgress(mediaInfo.currentPosition);
        if (this.mCurrentMediaText == null) {
            this.mArtist.setText(mediaInfo.artist);
        } else {
            setTransitionValue(mediaInfo.artist, this.mCurrentMediaText);
        }
        this.mCurrentMediaText = mediaInfo.artist;
        Bitmap bitmap = this.mOriginalCover;
        if (bitmap == null || !bitmap.sameAs(mediaInfo.cover)) {
            if (mediaInfo.cover != null) {
                this.mAlbumArt.setImageBitmap(this.mColorizer.colorize(new BitmapDrawable(this.mContext.getResources(), mediaInfo.cover), this.mMediaInfo.bgColor, false));
            } else {
                this.mAlbumArt.setImageBitmap((Bitmap) null);
            }
            this.mOriginalCover = mediaInfo.cover;
        }
    }

    public void updateMediaInfoView() {
        updateMediaInfo(this.mMediaInfo);
        updateMediaCloseVisibility();
    }

    public void setTransitionValue(String str, String str2) {
        if (str2 != null && !str2.equals(str)) {
            animateHideTransition(str);
        }
    }

    public void animateHideTransition(final String str) {
        Animator loadAnimator = loadAnimator(this.mContext, R$animator.zz_moto_cli_media_text_fade_out);
        loadAnimator.setTarget(this.mArtist);
        loadAnimator.addListener(new Animator.AnimatorListener() {
            public void onAnimationRepeat(Animator animator) {
            }

            public void onAnimationStart(Animator animator) {
            }

            public void onAnimationEnd(Animator animator) {
                CliMediaViewForQS.this.mArtist.setText(str);
                CliMediaViewForQS.this.animateShowTransition();
            }

            public void onAnimationCancel(Animator animator) {
                CliMediaViewForQS.this.mArtist.setText(str);
                CliMediaViewForQS.this.animateShowTransition();
            }
        });
        loadAnimator.start();
    }

    /* access modifiers changed from: private */
    public void animateShowTransition() {
        Animator loadAnimator = loadAnimator(this.mContext, R$animator.zz_moto_cli_media_text_fade_in);
        loadAnimator.setTarget(this.mArtist);
        loadAnimator.start();
    }

    private Animator loadAnimator(Context context, int i) {
        return AnimatorInflater.loadAnimator(context, i);
    }

    public void onTransitionStarted(MotionLayout motionLayout, int i, int i2) {
        this.mMediaPanelScrolling = true;
        if (DEBUG) {
            Log.d("CLI-QSMV-CliMediaViewForQS", "onTransitionStarted: current media view expanded state is expanded: " + this.mExpanded);
        }
        updateViewPagerHeight(CliMediaPagerHeightState.Large);
        CliMediaPageModel cliMediaPageModel = this.mMediaPage;
        if (cliMediaPageModel != null) {
            List<CliMediaAction> mediaActions = cliMediaPageModel.getMediaActions();
            if (this.mExpanded) {
                for (int i3 = 0; i3 < this.mViewActions.length; i3++) {
                    if (i3 >= mediaActions.size()) {
                        Log.e("CLI-QSMV-CliMediaViewForQS", "list.size() = " + mediaActions.size());
                        this.mViewActions[i3].setVisibility(8);
                    }
                }
                return;
            }
            for (int i4 = 0; i4 < this.mViewActions.length; i4++) {
                if (i4 >= mediaActions.size()) {
                    this.mViewActions[i4].setVisibility(8);
                } else {
                    this.mViewActions[i4].setVisibility(0);
                }
            }
        }
    }

    public void onTransitionChange(MotionLayout motionLayout, int i, int i2, float f) {
        ((CliMediaViewPager) getParent()).setMediaExpansion(f);
    }

    public void onTransitionCompleted(MotionLayout motionLayout, int i) {
        if (DEBUG) {
            Log.d("CLI-QSMV-CliMediaViewForQS", "onTransitionCompleted: getProgress = " + getProgress());
        }
        boolean z = getProgress() == 1.0f;
        updateViewPagerHeight(z ? CliMediaPagerHeightState.Large : CliMediaPagerHeightState.Small);
        if (z != this.mExpanded && (getParent() instanceof CliMediaViewPager)) {
            ((CliMediaViewPager) getParent()).setExpandedState(z);
        }
        List<CliMediaAction> mediaActions = this.mMediaPage.getMediaActions();
        if (this.mExpanded) {
            for (int i2 = 0; i2 < this.mViewActions.length; i2++) {
                if (i2 >= mediaActions.size()) {
                    this.mViewActions[i2].setVisibility(8);
                } else {
                    this.mViewActions[i2].setVisibility(0);
                }
            }
        } else {
            for (int i3 = 0; i3 < this.mViewActions.length; i3++) {
                if (i3 >= mediaActions.size()) {
                    this.mViewActions[i3].setVisibility(8);
                } else if (!mediaActions.get(i3).getCompactState()) {
                    this.mViewActions[i3].setVisibility(8);
                }
            }
        }
        this.mMediaPanelScrolling = false;
    }

    public void onTransitionTrigger(MotionLayout motionLayout, int i, boolean z, float f) {
        Log.e("CLI-QSMV-CliMediaViewForQS", "onTransitionTrigger");
    }

    private void updateViewPagerHeight(CliMediaPagerHeightState cliMediaPagerHeightState) {
        if (getParent() instanceof CliMediaViewPager) {
            ((CliMediaViewPager) getParent()).updateMediaViewPagerHeight(cliMediaPagerHeightState);
        }
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        super.onMeasure(i, i2);
    }

    class MediaInfo {
        /* access modifiers changed from: private */
        public String albumTitle;
        /* access modifiers changed from: private */
        public Drawable appIcon;
        /* access modifiers changed from: private */
        public String appName;
        /* access modifiers changed from: private */
        public String artist;
        /* access modifiers changed from: private */
        public int bgColor;
        /* access modifiers changed from: private */
        public Bitmap cover;
        /* access modifiers changed from: private */
        public int currentPosition;
        /* access modifiers changed from: private */
        public int mediaDuration;
        /* access modifiers changed from: private */
        public String trackTitle;

        MediaInfo() {
        }

        public String toString() {
            return "MediaInfo: [" + "appName=" + this.appName + "  title=" + this.trackTitle + "  albumTitle" + this.albumTitle + "  artist=" + this.artist + "]";
        }
    }

    public void setMediaViewCallback(MediaViewCallback mediaViewCallback) {
        this.mCallback = mediaViewCallback;
    }

    private void updateMediaCloseVisibility() {
        int i = 0;
        this.mMediaClose.setVisibility(!Utils.useMediaResumption(this.mContext) ? 0 : 8);
        ImageView imageView = this.mArrow;
        if (this.mMediaClose.getVisibility() == 0) {
            i = 8;
        }
        imageView.setVisibility(i);
    }

    static class SavedState extends View.BaseSavedState {
        public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator<SavedState>() {
            public SavedState createFromParcel(Parcel parcel) {
                return new SavedState(parcel);
            }

            public SavedState[] newArray(int i) {
                return new SavedState[i];
            }
        };
        String albumTitle;
        Drawable appIcon;
        String appName;
        String artist;
        int bgColor;
        Bitmap cover;
        int currentPosition;
        int mediaDuration;
        String trackTitle;

        public SavedState(Parcel parcel) {
            super(parcel);
            this.appName = parcel.readString();
            this.artist = parcel.readString();
            this.trackTitle = parcel.readString();
            this.albumTitle = parcel.readString();
            this.currentPosition = parcel.readInt();
            this.mediaDuration = parcel.readInt();
            this.cover = (Bitmap) Bitmap.CREATOR.createFromParcel(parcel);
            this.bgColor = parcel.readInt();
        }

        public SavedState(Parcelable parcelable) {
            super(parcelable);
        }

        public void writeToParcel(Parcel parcel, int i) {
            super.writeToParcel(parcel, i);
            parcel.writeString(this.appName);
            parcel.writeString(this.artist);
            parcel.writeString(this.trackTitle);
            parcel.writeString(this.albumTitle);
            this.cover.writeToParcel(parcel, i);
            parcel.writeInt(this.bgColor);
        }
    }

    public void setExpandedState(boolean z) {
        if (DEBUG && this.mMediaPage != null) {
            Log.d("CLI-QSMV-CliMediaViewForQS", "[" + this.mMediaPage.getAppName() + "]  Expanded state: old = " + this.mExpanded + "  new = " + z);
        }
        if (this.mExpanded != z) {
            if (z) {
                setProgress(1.0f);
                this.mArrow.setImageResource(R$drawable.cli_media_view_down_arrow);
            } else {
                setProgress(0.0f);
                this.mArrow.setImageResource(R$drawable.cli_media_view_up_arrow);
            }
            this.mExpanded = z;
        }
    }

    /* access modifiers changed from: protected */
    public MotionLayout.MotionTracker obtainVelocityTracker() {
        return MyTracker.obtain();
    }

    private static class MyTracker implements MotionLayout.MotionTracker {

        /* renamed from: me */
        private static MyTracker f189me = new MyTracker();
        VelocityTracker tracker;

        private MyTracker() {
        }

        public static MyTracker obtain() {
            f189me.tracker = VelocityTracker.obtain();
            return f189me;
        }

        public void recycle() {
            VelocityTracker velocityTracker = this.tracker;
            if (velocityTracker != null) {
                velocityTracker.recycle();
                this.tracker = null;
            }
        }

        public void addMovement(MotionEvent motionEvent) {
            VelocityTracker velocityTracker = this.tracker;
            if (velocityTracker != null) {
                velocityTracker.addMovement(motionEvent);
            }
        }

        public void computeCurrentVelocity(int i) {
            VelocityTracker velocityTracker = this.tracker;
            if (velocityTracker != null) {
                velocityTracker.computeCurrentVelocity(i);
            }
        }

        public float getXVelocity() {
            VelocityTracker velocityTracker = this.tracker;
            if (velocityTracker != null) {
                return velocityTracker.getXVelocity();
            }
            return 0.0f;
        }

        public float getYVelocity() {
            VelocityTracker velocityTracker = this.tracker;
            if (velocityTracker != null) {
                return velocityTracker.getYVelocity();
            }
            return 0.0f;
        }
    }
}
