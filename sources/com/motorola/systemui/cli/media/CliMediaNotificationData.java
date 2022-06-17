package com.motorola.systemui.cli.media;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.VectorDrawable;
import android.media.MediaMetadata;
import android.media.session.MediaController;
import android.media.session.MediaSession;
import android.media.session.PlaybackState;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.service.notification.StatusBarNotification;
import android.text.TextUtils;
import android.util.Log;
import android.view.InflateException;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RemoteViews;
import android.widget.TextView;
import androidx.core.graphics.ColorUtils;
import com.android.internal.util.ArrayUtils;
import com.android.systemui.R$dimen;
import com.android.systemui.R$drawable;
import com.android.systemui.statusbar.notification.MediaNotificationProcessor;
import java.util.ArrayList;
import java.util.List;

public class CliMediaNotificationData {
    private static final boolean DEBUG = (!Build.IS_USER);
    private List<CliMediaAction> mActions;
    private Drawable mAppIcon;
    private String mAppName;
    private int mBackgroundColor;
    private PendingIntent mContentIntent;
    private Context mContext;
    private int mCurrentPosition;
    private Bitmap mImage;
    private boolean mIsStandardMedia;
    private String mKey;
    private boolean mMediaActive;
    private MediaController mMediaController;
    private float mMediaCoverRoundCornerRadius;
    private int mMediaDuration;
    private String mPackageName;
    private String mText;
    private String mTitle;

    public CliMediaNotificationData(Context context, StatusBarNotification statusBarNotification) {
        this.mContext = context;
        this.mMediaCoverRoundCornerRadius = (float) context.getResources().getDimensionPixelSize(R$dimen.cli_media_view_bg_radius);
        this.mKey = statusBarNotification.getKey();
        Notification notification = statusBarNotification.getNotification();
        Bundle bundle = notification.extras;
        this.mPackageName = statusBarNotification.getPackageName();
        this.mTitle = processTitle(notification, context);
        this.mText = processText(bundle);
        Bitmap processImage = processImage(bundle, context);
        this.mImage = processImage;
        this.mBackgroundColor = computeBackgroundColor(processImage);
        this.mActions = getActionsList(notification);
        this.mMediaActive = isMediaActive(bundle);
        this.mAppIcon = processAppIcon(notification);
        this.mAppName = processAppName(context, this.mPackageName);
        boolean z = DEBUG;
        if (z) {
            Log.i("CLI-QSMV-CliMediaNotificationData", "CliMediaNotificationData mMediaActive = " + this.mMediaActive + "; mPackageName = " + this.mPackageName + "; mAppName = " + this.mAppName);
        }
        MediaSession.Token token = (MediaSession.Token) statusBarNotification.getNotification().extras.getParcelable("android.mediaSession");
        if (token != null) {
            MediaController mediaController = new MediaController(this.mContext, token);
            this.mMediaController = mediaController;
            MediaMetadata metadata = mediaController.getMetadata();
            if (metadata != null) {
                this.mMediaDuration = (int) metadata.getLong("android.media.metadata.DURATION");
            } else {
                if (z) {
                    Log.e("CLI-QSMV-CliMediaNotificationData", "The media metadata is null.");
                }
                this.mMediaDuration = 0;
            }
            PlaybackState playbackState = mediaController.getPlaybackState();
            if (playbackState != null) {
                this.mCurrentPosition = (int) playbackState.getPosition();
            } else {
                if (z) {
                    Log.e("CLI-QSMV-CliMediaNotificationData", "The play back state is null");
                }
                this.mCurrentPosition = 0;
            }
        } else {
            Log.e("CLI-QSMV-CliMediaNotificationData", "The media token is null. Can't get the media time.");
        }
        this.mContentIntent = statusBarNotification.getNotification().contentIntent;
        this.mIsStandardMedia = notification.isMediaNotification();
    }

    public String getKey() {
        return this.mKey;
    }

    public Drawable getAppIcon() {
        return this.mAppIcon;
    }

    public String getAppName() {
        return this.mAppName;
    }

    public String getPackageName() {
        return this.mPackageName;
    }

    public String getTitle() {
        return this.mTitle;
    }

    public String getText() {
        return this.mText;
    }

    public Bitmap getImage() {
        return this.mImage;
    }

    public boolean getMediaActive() {
        return this.mMediaActive;
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

    public List<CliMediaAction> getMediaActions() {
        return this.mActions;
    }

    public PendingIntent getContentIntent() {
        return this.mContentIntent;
    }

    public boolean getIsStandardMedia() {
        return this.mIsStandardMedia;
    }

    private boolean isMediaActive(Bundle bundle) {
        return bundle.getBoolean("EXTRA_MUSIC_ACTIVE", false);
    }

    private List<CliMediaAction> getActionsList(Notification notification) {
        ArrayList arrayList = new ArrayList();
        Notification.Action[] actionArr = notification.actions;
        if (actionArr != null) {
            if (notification.extras.containsKey("android.compactActions")) {
                int[] intArray = notification.extras.getIntArray("android.compactActions");
                for (int i = 0; i < actionArr.length; i++) {
                    if (ArrayUtils.contains(intArray, i)) {
                        addAction(arrayList, actionArr[i], notification, true);
                    } else {
                        addAction(arrayList, actionArr[i], notification, false);
                    }
                }
            } else {
                for (Notification.Action addAction : actionArr) {
                    addAction(arrayList, addAction, notification, true);
                }
            }
        }
        return arrayList;
    }

    private void addAction(List<CliMediaAction> list, Notification.Action action, Notification notification, boolean z) {
        addCustomExtrasOnAction(action, notification);
        list.add(new CliMediaAction(action, this.mContext, z));
    }

    private void addCustomExtrasOnAction(Notification.Action action, Notification notification) {
        Bundle bundle = notification.extras;
        action.getExtras().putString("PACKAGE_EXTRA", this.mPackageName);
        action.getExtras().putInt("USER_EXTRA", bundle.getInt("USER_EXTRA", -10000));
        action.getExtras().putParcelable("NOTIFICATION_INTENT_EXTRA", notification.contentIntent);
    }

    private Bitmap processImage(Bundle bundle, Context context) {
        Bitmap bitmap;
        Parcelable processExtraImage = processExtraImage(bundle);
        if (processExtraImage != null) {
            if (DEBUG) {
                Log.d("CLI-QSMV-CliMediaNotificationData", "will load image from original image");
            }
            bitmap = getBitmapFromRawImage(context, processExtraImage);
        } else {
            if (DEBUG) {
                Log.d("CLI-QSMV-CliMediaNotificationData", "will load default album image");
            }
            bitmap = getDefaultCoverBitmap(context);
        }
        return bitmapRound(bitmap, this.mMediaCoverRoundCornerRadius);
    }

    private Bitmap getDefaultCoverBitmap(Context context) {
        VectorDrawable vectorDrawable = (VectorDrawable) context.getDrawable(R$drawable.cli_media_cover_default);
        Bitmap createBitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(createBitmap);
        vectorDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        vectorDrawable.draw(canvas);
        return createBitmap;
    }

    private Bitmap bitmapRound(Bitmap bitmap, float f) {
        if (bitmap == null) {
            return null;
        }
        Bitmap createBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(createBitmap);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        RectF rectF = new RectF(rect);
        canvas.drawARGB(0, 0, 0, 0);
        canvas.drawRoundRect(rectF, f, f, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return createBitmap;
    }

    private Bitmap getBitmapFromRawImage(Context context, Parcelable parcelable) {
        return new BitmapBuilder(parcelable, context).scale(getScale(context)).build();
    }

    private Rect getScale(Context context) {
        return new Rect(0, 0, context.getResources().getDimensionPixelSize(R$dimen.cli_max_media_photo_width), context.getResources().getDimensionPixelSize(R$dimen.cli_max_media_photo_height));
    }

    private Parcelable processExtraImage(Bundle bundle) {
        if (bundle.containsKey("android.largeIcon.big") && bundle.getParcelable("android.largeIcon.big") != null) {
            return bundle.getParcelable("android.largeIcon.big");
        }
        if (bundle.containsKey("android.largeIcon") && bundle.getParcelable("android.largeIcon") != null) {
            return bundle.getParcelable("android.largeIcon");
        }
        if (bundle.containsKey("android.picture")) {
            return bundle.getParcelable("android.picture");
        }
        return null;
    }

    private String processText(Bundle bundle) {
        CharSequence charSequence;
        if (bundle.containsKey("android.bigText")) {
            charSequence = bundle.getCharSequence("android.bigText");
        } else {
            charSequence = bundle.containsKey("android.text") ? bundle.getCharSequence("android.text") : null;
        }
        if (!TextUtils.isEmpty(charSequence)) {
            return charSequence.toString();
        }
        Log.i("CLI-QSMV-CliMediaNotificationData", "Unable to get the text.");
        return "";
    }

    private String processTitle(Notification notification, Context context) {
        CharSequence charSequence;
        if (notification.extras.containsKey("android.title.big")) {
            charSequence = notification.extras.getCharSequence("android.title.big");
        } else {
            charSequence = notification.extras.containsKey("android.title") ? notification.extras.getCharSequence("android.title") : null;
        }
        if (TextUtils.isEmpty(charSequence)) {
            charSequence = getTitleFromRemoteViews(notification, context);
        }
        if (!TextUtils.isEmpty(charSequence)) {
            return charSequence.toString();
        }
        Log.w("CLI-QSMV-CliMediaNotificationData", "Unable to get the title.");
        return "";
    }

    private CharSequence getTitleFromRemoteViews(Notification notification, Context context) {
        View view;
        try {
            RemoteViews remoteViews = notification.bigContentView;
            if (remoteViews != null) {
                view = remoteViews.apply(context, (ViewGroup) null);
            } else {
                RemoteViews remoteViews2 = notification.contentView;
                if (remoteViews2 != null) {
                    view = remoteViews2.apply(context, (ViewGroup) null);
                }
                view = null;
            }
        } catch (Resources.NotFoundException unused) {
            if (DEBUG) {
                Log.d("CLI-QSMV-CliMediaNotificationData", "getTitleFromRemoteViews: Resource not found");
            }
        } catch (InflateException | SecurityException unused2) {
            if (DEBUG) {
                Log.d("CLI-QSMV-CliMediaNotificationData", "getTitleFromRemoteViews: View can't be inflated");
            }
        }
        if (view == null || !(view instanceof ViewGroup)) {
            return null;
        }
        return searchText((ViewGroup) view);
    }

    private CharSequence searchText(ViewGroup viewGroup) {
        int childCount = viewGroup.getChildCount();
        CharSequence charSequence = null;
        for (int i = 0; i < childCount; i++) {
            View childAt = viewGroup.getChildAt(i);
            if (childAt.getVisibility() == 0) {
                if (childAt instanceof ViewGroup) {
                    charSequence = searchText((ViewGroup) childAt);
                } else if (childAt instanceof TextView) {
                    charSequence = ((TextView) childAt).getText();
                }
            }
        }
        return charSequence;
    }

    private String processAppName(Context context, String str) {
        PackageManager packageManager = context.getPackageManager();
        try {
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(str, 795136);
            if (applicationInfo != null) {
                return String.valueOf(packageManager.getApplicationLabel(applicationInfo));
            }
            return "";
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("CLI-QSMV-CliMediaNotificationData", "Can't get the app name packageName = " + str + "; e = " + e.getMessage());
            return "";
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:18:0x0040 A[RETURN] */
    /* JADX WARNING: Removed duplicated region for block: B:19:0x0041  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private android.graphics.drawable.Drawable processAppIcon(android.app.Notification r3) {
        /*
            r2 = this;
            android.graphics.drawable.Icon r0 = r3.getSmallIcon()
            r1 = -1
            if (r0 == 0) goto L_0x0015
            android.content.Context r2 = r2.mContext
            android.graphics.drawable.Drawable r2 = r0.loadDrawable(r2)
            android.graphics.drawable.Drawable r2 = androidx.core.graphics.drawable.DrawableCompat.wrap(r2)
            androidx.core.graphics.drawable.DrawableCompat.setTint(r2, r1)
            return r2
        L_0x0015:
            android.graphics.drawable.Icon r3 = r3.getLargeIcon()
            if (r3 == 0) goto L_0x0029
            android.content.Context r2 = r2.mContext
            android.graphics.drawable.Drawable r2 = r3.loadDrawable(r2)
            android.graphics.drawable.Drawable r2 = androidx.core.graphics.drawable.DrawableCompat.wrap(r2)
            androidx.core.graphics.drawable.DrawableCompat.setTint(r2, r1)
            return r2
        L_0x0029:
            r3 = 0
            android.content.Context r0 = r2.mContext     // Catch:{ NameNotFoundException -> 0x003c }
            android.content.Context r0 = r0.getApplicationContext()     // Catch:{ NameNotFoundException -> 0x003c }
            android.content.pm.PackageManager r0 = r0.getPackageManager()     // Catch:{ NameNotFoundException -> 0x003c }
            java.lang.String r2 = r2.mPackageName     // Catch:{ NameNotFoundException -> 0x003d }
            r1 = 0
            android.content.pm.ApplicationInfo r2 = r0.getApplicationInfo(r2, r1)     // Catch:{ NameNotFoundException -> 0x003d }
            goto L_0x003e
        L_0x003c:
            r0 = r3
        L_0x003d:
            r2 = r3
        L_0x003e:
            if (r2 != 0) goto L_0x0041
            return r3
        L_0x0041:
            android.graphics.drawable.Drawable r2 = r0.getApplicationIcon(r2)
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.motorola.systemui.cli.media.CliMediaNotificationData.processAppIcon(android.app.Notification):android.graphics.drawable.Drawable");
    }

    private int computeBackgroundColor(Bitmap bitmap) {
        int rgb = bitmap != null ? MediaNotificationProcessor.findBackgroundSwatch(MediaNotificationProcessor.generateArtworkPaletteBuilder(bitmap).generate()).getRgb() : -1;
        float[] fArr = {0.0f, 0.0f, 0.0f};
        ColorUtils.colorToHSL(rgb, fArr);
        float f = fArr[2];
        if (f < 0.05f || f > 0.95f) {
            fArr[1] = 0.0f;
        }
        fArr[1] = fArr[1] * 0.8f;
        fArr[2] = 0.25f;
        return ColorUtils.HSLToColor(fArr);
    }
}
