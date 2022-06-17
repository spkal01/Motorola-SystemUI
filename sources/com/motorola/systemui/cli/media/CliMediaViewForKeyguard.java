package com.motorola.systemui.cli.media;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Typeface;
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
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextClock;
import android.widget.TextView;
import androidx.constraintlayout.widget.ConstraintLayout;
import com.android.keyguard.KeyguardUpdateMonitor;
import com.android.keyguard.KeyguardUpdateMonitorCallback;
import com.android.systemui.Dependency;
import com.android.systemui.R$animator;
import com.android.systemui.R$dimen;
import com.android.systemui.R$id;
import com.android.systemui.keyguard.ScreenLifecycle;
import com.android.systemui.moto.MotoFeature;
import com.android.systemui.statusbar.phone.CliStatusBar;
import com.android.systemui.statusbar.phone.StatusBar;
import java.io.File;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class CliMediaViewForKeyguard extends ConstraintLayout implements CliMediaView, SeekBar.OnSeekBarChangeListener, View.OnClickListener {
    /* access modifiers changed from: private */
    public static final boolean DEBUG = (!Build.IS_USER);
    /* access modifiers changed from: private */
    public boolean isSeekBarTouching;
    private ImageView mAction0;
    private ImageView mAction1;
    private ImageView mAction2;
    private ImageView mAction3;
    private ImageView mAction4;
    private LinearLayout mActionsLayout;
    public ImageView mAlbumArt;
    private ImageView mAppIcon;
    private TextView mAppName;
    /* access modifiers changed from: private */
    public TextView mArtist;
    private CliStatusBar mCliStatusBar;
    /* access modifiers changed from: private */
    public TextClock mClockView;
    public boolean mControllerActive;
    private BitmapDrawable mCurrentDrawable;
    private String mCurrentMediaText;
    private KeyguardUpdateMonitorCallback mKeyguardUpdateCallback;
    /* access modifiers changed from: private */
    public Handler mMainThreadHandler;
    private boolean mMediaActive;
    /* access modifiers changed from: private */
    public TextView mMediaElapsedTime;
    private MediaInfo mMediaInfo;
    private CliMediaOutputRouteLayout mMediaOutputLayout;
    public CliMediaPageModel mMediaPage;
    /* access modifiers changed from: private */
    public SeekBar mMediaProgressBar;
    private LinearLayout mMediaSeamless;
    private TextView mMediaTotalTime;
    protected boolean mOnlyShowCompactActions;
    private Bitmap mOriginalCover;
    private ScreenLifecycle mScreenLifecycle;
    private final ScreenLifecycle.Observer mScreenObserver;
    private TextView mSongName;
    private StatusBar mStatusBar;
    /* access modifiers changed from: private */
    public Timer mTimer;
    protected boolean mTintAtions;
    private KeyguardUpdateMonitor mUpdateMonitor;
    public ImageView[] mViewActions;

    private void updateBackground(int i) {
    }

    public void onProgressChanged(SeekBar seekBar, int i, boolean z) {
    }

    public CliMediaViewForKeyguard(Context context) {
        this(context, (AttributeSet) null);
    }

    public CliMediaViewForKeyguard(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, -1);
    }

    public CliMediaViewForKeyguard(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mMainThreadHandler = new Handler(Looper.getMainLooper());
        this.mMediaInfo = new MediaInfo();
        this.mCurrentDrawable = null;
        this.mCurrentMediaText = null;
        this.mTintAtions = true;
        this.mOnlyShowCompactActions = true;
        this.mKeyguardUpdateCallback = new KeyguardUpdateMonitorCallback() {
            public void onTimeChanged() {
                CliMediaViewForKeyguard.this.mClockView.refreshTime();
            }

            public void onKeyguardVisibilityChanged(boolean z) {
                if (z) {
                    CliMediaViewForKeyguard.this.mClockView.refreshTime();
                }
            }
        };
        this.mScreenObserver = new ScreenLifecycle.Observer() {
            public void onScreenTurningOn() {
                CliMediaViewForKeyguard.this.mClockView.refreshTime();
            }
        };
        this.mStatusBar = (StatusBar) Dependency.get(StatusBar.class);
        if (MotoFeature.getInstance(context).isSupportCli()) {
            this.mCliStatusBar = (CliStatusBar) Dependency.get(CliStatusBar.class);
        }
        this.mScreenLifecycle = (ScreenLifecycle) Dependency.get(ScreenLifecycle.class);
        this.mUpdateMonitor = (KeyguardUpdateMonitor) Dependency.get(KeyguardUpdateMonitor.class);
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
        this.mMediaElapsedTime = (TextView) findViewById(R$id.media_elapsed_time);
        this.mMediaTotalTime = (TextView) findViewById(R$id.media_total_time);
        this.mMediaSeamless = (LinearLayout) findViewById(R$id.media_seamless);
        this.mClockView = (TextClock) findViewById(R$id.clock);
        updateClockFontAndSize();
        this.mActionsLayout = (LinearLayout) findViewById(R$id.actions);
        this.mAction0 = (ImageView) findViewById(R$id.action0);
        this.mAction1 = (ImageView) findViewById(R$id.action1);
        this.mAction2 = (ImageView) findViewById(R$id.action2);
        this.mAction3 = (ImageView) findViewById(R$id.action3);
        ImageView imageView = (ImageView) findViewById(R$id.action4);
        this.mAction4 = imageView;
        this.mViewActions = new ImageView[]{this.mAction0, this.mAction1, this.mAction2, this.mAction3, imageView};
        this.mAppIcon.setOnClickListener(this);
        this.mAppName.setOnClickListener(this);
        this.mMediaSeamless.setOnClickListener(this);
        this.mMediaProgressBar.setOnSeekBarChangeListener(this);
    }

    private void updateClockFontAndSize() {
        this.mClockView.setTextSize(0, (float) getResources().getDimensionPixelSize(R$dimen.cli_keyguard_mediapanel_clock_size));
        File file = new File("/system/fonts/Newfont_Light.ttf");
        if (file.exists()) {
            this.mClockView.setTypeface(Typeface.createFromFile(file));
            return;
        }
        Log.e("CLI-QSMV-CliMediaViewForKeyguard", "Newfont files can not be found");
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
        Log.e("CLI-QSMV-CliMediaViewForKeyguard", "CLI-QSMV-CliMediaViewForKeyguard: onInterceptTouchEvent");
        return super.onInterceptTouchEvent(motionEvent);
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (DEBUG && this.mMediaPage != null) {
            Log.d("CLI-QSMV-CliMediaViewForKeyguard", "onAttachedToWindow  [" + this.mMediaPage.getAppName() + "]  " + this);
        }
        this.mScreenLifecycle.addObserver(this.mScreenObserver);
        this.mUpdateMonitor.registerCallback(this.mKeyguardUpdateCallback);
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (DEBUG && this.mMediaPage != null) {
            Log.d("CLI-QSMV-CliMediaViewForKeyguard", "onDetachedFromWindow  [" + this.mMediaPage.getAppName() + "]  " + this);
        }
        this.mScreenLifecycle.removeObserver(this.mScreenObserver);
        this.mUpdateMonitor.removeCallback(this.mKeyguardUpdateCallback);
    }

    /* access modifiers changed from: protected */
    public Parcelable onSaveInstanceState() {
        SavedState savedState = new SavedState(super.onSaveInstanceState());
        savedState.appName = this.mMediaInfo.appName;
        savedState.artist = this.mMediaInfo.artist;
        savedState.trackTitle = this.mMediaInfo.trackTitle;
        savedState.albumTitle = this.mMediaInfo.albumTitle;
        MediaInfo mediaInfo = this.mMediaInfo;
        savedState.cover = mediaInfo.cover;
        savedState.appIcon = mediaInfo.appIcon;
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
        MediaInfo mediaInfo = this.mMediaInfo;
        mediaInfo.cover = savedState.cover;
        Drawable unused4 = mediaInfo.appIcon = savedState.appIcon;
        int unused5 = this.mMediaInfo.currentPosition = savedState.currentPosition;
        int unused6 = this.mMediaInfo.mediaDuration = savedState.mediaDuration;
        int unused7 = this.mMediaInfo.bgColor = savedState.bgColor;
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
        this.mMediaInfo.cover = cliMediaPageModel.getImage();
        int unused5 = this.mMediaInfo.currentPosition = cliMediaPageModel.getCurrentPosition();
        int unused6 = this.mMediaInfo.mediaDuration = cliMediaPageModel.getMediaDuration();
        int unused7 = this.mMediaInfo.bgColor = cliMediaPageModel.getBackgroundColor();
        updateMediaInfoView();
        updateMediaPageActions(getContext(), cliMediaPageModel.getMediaActions());
        updateBackground(cliMediaPageModel.getBackgroundColor());
        updateMediaPlayingTime(this.mMediaActive, cliMediaPageModel.getMediaActive());
        this.mMediaActive = cliMediaPageModel.getMediaActive();
    }

    public void addedToViewPager() {
        if (DEBUG && this.mMediaPage != null) {
            Log.d("CLI-QSMV-CliMediaViewForKeyguard", "addedToViewPager: " + this.mMediaPage.getAppName());
        }
    }

    public void removedFromViewPager() {
        if (DEBUG && this.mMediaPage != null) {
            Log.d("CLI-QSMV-CliMediaViewForKeyguard", "removedFromViewPager" + this.mMediaPage.getAppName());
        }
        recycle();
    }

    public void parentDetachedFromWindow() {
        if (DEBUG && this.mMediaPage != null) {
            Log.d("CLI-QSMV-CliMediaViewForKeyguard", "removedFromViewPager" + this.mMediaPage.getAppName());
        }
        recycle();
    }

    private void recycle() {
        if (this.mTimer != null) {
            if (DEBUG) {
                Log.d("CLI-QSMV-CliMediaViewForKeyguard", "cancel mTimer: " + this.mTimer);
            }
            this.mTimer.cancel();
            this.mTimer.purge();
            this.mTimer = null;
        }
        Bitmap bitmap = this.mOriginalCover;
        if (bitmap != null && !bitmap.isRecycled()) {
            this.mOriginalCover.recycle();
            this.mOriginalCover = null;
        }
        MediaInfo mediaInfo = this.mMediaInfo;
        if (mediaInfo != null) {
            Bitmap bitmap2 = mediaInfo.cover;
            if (bitmap2 != null && !bitmap2.isRecycled()) {
                this.mMediaInfo.cover.recycle();
                this.mMediaInfo.cover = null;
            }
            this.mMediaInfo = null;
        }
        CliMediaPageModel cliMediaPageModel = this.mMediaPage;
        if (cliMediaPageModel != null) {
            if (cliMediaPageModel.getImage() != null && !this.mMediaPage.getImage().isRecycled()) {
                this.mMediaPage.getImage().recycle();
            }
            this.mMediaPage = null;
        }
        this.mAlbumArt.setImageBitmap((Bitmap) null);
    }

    private void updateMediaPlayingTime(boolean z, boolean z2) {
        if (DEBUG && this.mMediaPage != null) {
            Log.d("CLI-QSMV-CliMediaViewForKeyguard", "updateMediaPlayingTime:[" + this.mMediaPage.getAppName() + "] old=" + z + "  new=" + z2);
        }
        if (z != z2) {
            if (z2) {
                Timer timer = this.mTimer;
                if (timer != null) {
                    timer.cancel();
                    this.mTimer.purge();
                    this.mTimer = null;
                }
                Timer timer2 = new Timer();
                this.mTimer = timer2;
                timer2.schedule(new TimerTask() {
                    public void run() {
                        MediaController mediaController = CliMediaViewForKeyguard.this.mMediaPage.getMediaController();
                        if (mediaController == null) {
                            CliMediaViewForKeyguard.this.mTimer.purge();
                            return;
                        }
                        PlaybackState playbackState = mediaController.getPlaybackState();
                        if (playbackState == null) {
                            CliMediaViewForKeyguard.this.mTimer.purge();
                            return;
                        }
                        long position = playbackState.getPosition();
                        if (!CliMediaViewForKeyguard.this.isSeekBarTouching) {
                            CliMediaViewForKeyguard.this.mMainThreadHandler.post(new CliMediaViewForKeyguard$3$$ExternalSyntheticLambda0(this, position));
                        }
                    }

                    /* access modifiers changed from: private */
                    public /* synthetic */ void lambda$run$0(long j) {
                        CliMediaViewForKeyguard.this.mMediaProgressBar.setProgress((int) j);
                        CliMediaViewForKeyguard.this.mMediaElapsedTime.setText(DateUtils.formatElapsedTime(j / 1000));
                    }
                }, 0, 1000);
                return;
            }
            int unused = this.mMediaInfo.currentPosition = this.mMediaProgressBar.getProgress();
            Timer timer3 = this.mTimer;
            if (timer3 != null) {
                timer3.cancel();
                this.mTimer.purge();
                this.mTimer = null;
            }
        }
    }

    private void updateMediaPageActions(Context context, List<CliMediaAction> list) {
        if (DEBUG) {
            Log.d("CLI-QSMV-CliMediaViewForKeyguard", "actions size: " + list.size());
        }
        if (list == null || list.size() == 0) {
            Log.w("CLI-QSMV-CliMediaViewForKeyguard", "the media no actions.");
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
                    final CliMediaAction cliMediaAction = list.get(i);
                    if (cliMediaAction.getIcon() == null) {
                        imageView.setVisibility(8);
                        Log.w("CLI-QSMV-CliMediaViewForKeyguard", "The icon is null, don't show the controller button. index = " + i + ". Gone.");
                    } else if (cliMediaAction.getCompactState() || !this.mOnlyShowCompactActions) {
                        Drawable icon = cliMediaAction.getIcon();
                        if (icon != null) {
                            imageView.setImageDrawable(icon);
                            if (this.mTintAtions) {
                                imageView.setColorFilter(-1);
                            }
                        }
                        imageView.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View view) {
                                if (!CliMediaViewForKeyguard.this.mControllerActive) {
                                    if (CliMediaViewForKeyguard.DEBUG) {
                                        Log.d("CLI-QSMV-CliMediaViewForKeyguard", "The media controllers are not active.");
                                    }
                                    CliMediaViewForKeyguard.this.setContronllerActiveToParent();
                                    return;
                                }
                                PendingIntent pendingIntent = cliMediaAction.getPendingIntent();
                                if (pendingIntent == null) {
                                    Log.i("CLI-QSMV-CliMediaViewForKeyguard", "The action doesn't have click inent.");
                                    return;
                                }
                                try {
                                    CliMediaViewForKeyguard.this.performHaptic(view);
                                    pendingIntent.send();
                                } catch (PendingIntent.CanceledException unused) {
                                    Log.e("CLI-QSMV-CliMediaViewForKeyguard", "Intent canceled, unable to send remote input result.");
                                }
                            }
                        });
                    } else {
                        imageView.setVisibility(8);
                        if (DEBUG) {
                            Log.d("CLI-QSMV-CliMediaViewForKeyguard", "action is not compact state. index = " + i + ". Gone.");
                        }
                    }
                }
                i++;
            } else {
                return;
            }
        }
    }

    /* access modifiers changed from: private */
    public void performHaptic(View view) {
        view.performHapticFeedback(1);
    }

    public void updateMediaInfo(MediaInfo mediaInfo) {
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
            Bitmap bitmap2 = mediaInfo.cover;
            if (bitmap2 != null) {
                Bitmap copy = bitmap2.copy(Bitmap.Config.ARGB_8888, true);
                new Canvas(copy).drawARGB(153, 0, 0, 0);
                this.mAlbumArt.setImageBitmap(copy);
            } else {
                this.mAlbumArt.setImageBitmap((Bitmap) null);
            }
            this.mOriginalCover = mediaInfo.cover;
        }
    }

    public void updateMediaInfoView() {
        updateMediaInfo(this.mMediaInfo);
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
                CliMediaViewForKeyguard.this.mArtist.setText(str);
                CliMediaViewForKeyguard.this.animateShowTransition();
            }

            public void onAnimationCancel(Animator animator) {
                CliMediaViewForKeyguard.this.mArtist.setText(str);
                CliMediaViewForKeyguard.this.animateShowTransition();
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

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        super.onMeasure(i, i2);
    }

    public void onClick(View view) {
        if (view == this.mAppIcon || view == this.mAppName) {
            CliStatusBar cliStatusBar = this.mCliStatusBar;
            if (cliStatusBar != null) {
                cliStatusBar.animateExpandedVisible(false);
            }
            StatusBar statusBar = this.mStatusBar;
            if (statusBar != null) {
                statusBar.triggerNotificationClickAndRequestUnlockInternal(this.mMediaPage.getKey(), this.mMediaPage.getContentIntent(), (Intent) null);
            }
        } else if (view == this.mMediaSeamless) {
            if (this.mMediaOutputLayout == null) {
                this.mMediaOutputLayout = (CliMediaOutputRouteLayout) getRootView().findViewById(R$id.media_output_layout);
            }
            this.mMediaOutputLayout.setCover(this.mMediaInfo.cover);
            this.mMediaOutputLayout.setTrackTitle(this.mMediaInfo.trackTitle);
            this.mMediaOutputLayout.setArtist(this.mMediaInfo.artist);
            this.mMediaOutputLayout.setVisibility(0);
        }
    }

    public class MediaInfo {
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
        public Bitmap cover;
        /* access modifiers changed from: private */
        public int currentPosition;
        /* access modifiers changed from: private */
        public int mediaDuration;
        /* access modifiers changed from: private */
        public String trackTitle;

        public MediaInfo() {
        }

        public String toString() {
            return "MediaInfo: [" + "appName=" + this.appName + "  title=" + this.trackTitle + "  albumTitle" + this.albumTitle + "  artist=" + this.artist + "]";
        }
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

    public void updateControllerActive(boolean z) {
        boolean z2 = DEBUG;
        if (z2) {
            Log.d("CLI-QSMV-CliMediaViewForKeyguard", "Update the active state of media controllers to : " + z);
        }
        if (this.mControllerActive != z) {
            this.mControllerActive = z;
            this.mActionsLayout.setAlpha(z ? 1.0f : 0.3f);
        } else if (z2) {
            Log.d("CLI-QSMV-CliMediaViewForKeyguard", "The state is not change. The state is active : " + z);
        }
    }

    /* access modifiers changed from: private */
    public void setContronllerActiveToParent() {
        if (getParent() instanceof CliMediaViewPager) {
            ((CliMediaViewPager) getParent()).setControllerActiveState(true);
        }
    }
}
