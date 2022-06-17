package com.motorola.systemui.cli.media;

import android.app.PendingIntent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.session.MediaController;
import java.util.List;

public class CliMediaPageModel {
    private List<CliMediaAction> mActions;
    private Drawable mAppIcon;
    private String mAppName;
    private String mArtist;
    private int mBackgroundColor;
    private PendingIntent mContentIntent;
    private int mCurrentPosition;
    private Bitmap mImage;
    private boolean mIsStandardMedia;
    private String mKey;
    private boolean mMediaActive;
    private MediaController mMediaController;
    private int mMediaDuration;
    private String mPackageName;
    private String mTrackTitle;

    public CliMediaPageModel(CliMediaNotificationData cliMediaNotificationData) {
        this.mKey = cliMediaNotificationData.getKey();
        this.mAppName = cliMediaNotificationData.getAppName();
        this.mAppIcon = cliMediaNotificationData.getAppIcon();
        this.mPackageName = cliMediaNotificationData.getPackageName();
        this.mTrackTitle = cliMediaNotificationData.getTitle();
        this.mArtist = cliMediaNotificationData.getText();
        this.mImage = cliMediaNotificationData.getImage();
        this.mActions = cliMediaNotificationData.getMediaActions();
        this.mMediaActive = cliMediaNotificationData.getMediaActive();
        this.mBackgroundColor = cliMediaNotificationData.getBackgroundColor();
        this.mMediaDuration = cliMediaNotificationData.getMediaDuration();
        this.mCurrentPosition = cliMediaNotificationData.getCurrentPosition();
        this.mMediaController = cliMediaNotificationData.getMediaController();
        this.mContentIntent = cliMediaNotificationData.getContentIntent();
        this.mIsStandardMedia = cliMediaNotificationData.getIsStandardMedia();
    }

    public void updatePageModel(CliMediaPageModel cliMediaPageModel) {
        this.mKey = cliMediaPageModel.getKey();
        this.mAppIcon = cliMediaPageModel.getAppIcon();
        this.mAppName = cliMediaPageModel.getAppName();
        this.mPackageName = cliMediaPageModel.getPackageName();
        this.mTrackTitle = cliMediaPageModel.getTrackTitle();
        this.mArtist = cliMediaPageModel.getArtist();
        this.mImage = cliMediaPageModel.getImage();
        this.mActions = cliMediaPageModel.getMediaActions();
        this.mMediaActive = cliMediaPageModel.getMediaActive();
        this.mBackgroundColor = cliMediaPageModel.getBackgroundColor();
        this.mMediaDuration = cliMediaPageModel.getMediaDuration();
        this.mCurrentPosition = cliMediaPageModel.getCurrentPosition();
        this.mMediaController = cliMediaPageModel.getMediaController();
        this.mContentIntent = cliMediaPageModel.getContentIntent();
        this.mIsStandardMedia = cliMediaPageModel.getIsStandardMedia();
    }

    public String getKey() {
        return this.mKey;
    }

    public String getPackageName() {
        return this.mPackageName;
    }

    public String getTrackTitle() {
        return this.mTrackTitle;
    }

    public String getArtist() {
        return this.mArtist;
    }

    public Bitmap getImage() {
        return this.mImage;
    }

    public List<CliMediaAction> getMediaActions() {
        return this.mActions;
    }

    public boolean getMediaActive() {
        return this.mMediaActive;
    }

    public String getAppName() {
        return this.mAppName;
    }

    public Drawable getAppIcon() {
        return this.mAppIcon;
    }

    public int getBackgroundColor() {
        return this.mBackgroundColor;
    }

    public int getMediaDuration() {
        return this.mMediaDuration;
    }

    public int getCurrentPosition() {
        return this.mCurrentPosition;
    }

    public MediaController getMediaController() {
        return this.mMediaController;
    }

    public PendingIntent getContentIntent() {
        return this.mContentIntent;
    }

    public boolean getIsStandardMedia() {
        return this.mIsStandardMedia;
    }
}
