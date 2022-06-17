package com.motorola.systemui.cli.media;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.Icon;
import android.media.session.MediaController;
import android.media.session.MediaSession;
import android.media.session.PlaybackState;
import android.os.Build;
import android.util.Log;
import android.view.InflateException;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RemoteViews;
import com.android.systemui.R$dimen;
import com.android.systemui.R$drawable;
import com.android.systemui.R$string;
import java.util.ArrayList;

public class InternalMediaNotification extends Notification {
    private static final boolean DEBUG = (!Build.IS_USER);

    public InternalMediaNotification(Context context, MediaController mediaController, Notification notification, CliMediaViewPagerOwn cliMediaViewPagerOwn) {
        if (DEBUG) {
            Log.d("CLI-QSMV-InternalMediaNotification", "InternalMediaNotification - package: " + mediaController.getPackageName() + ", media session token: " + mediaController.getSessionToken());
        }
        this.visibility = notification.visibility;
        this.priority = notification.priority;
        this.category = notification.category;
        this.number = notification.number;
        this.flags = notification.flags;
        this.tickerText = notification.tickerText;
        this.contentIntent = notification.contentIntent;
        this.icon = notification.icon;
        initializeActions(context, mediaController.getPlaybackState(), mediaController.getSessionToken(), cliMediaViewPagerOwn);
        initializeExtras(context, mediaController.getMetadata(), notification);
    }

    private void initializeActions(Context context, PlaybackState playbackState, MediaSession.Token token, CliMediaViewPagerOwn cliMediaViewPagerOwn) {
        int i;
        int i2;
        int i3;
        int i4;
        ArrayList arrayList;
        Resources resources;
        Resources resources2;
        String str;
        ArrayList arrayList2;
        CliMediaViewPagerOwn cliMediaViewPagerOwn2 = cliMediaViewPagerOwn;
        if (playbackState != null) {
            long actions = playbackState.getActions();
            boolean z = DEBUG;
            if (z) {
                Log.d("CLI-QSMV-InternalMediaNotification", "playbackActions = " + actions);
            }
            ArrayList arrayList3 = new ArrayList();
            Resources resources3 = context.getResources();
            if (cliMediaViewPagerOwn2 == CliMediaViewPagerOwn.PRC_EXPANDED || cliMediaViewPagerOwn2 == CliMediaViewPagerOwn.PRC_TILE) {
                i4 = R$drawable.prc_media_ic_play;
                i3 = R$drawable.prc_media_ic_pause;
                i2 = R$drawable.prc_media_ic_previous;
                i = R$drawable.prc_media_ic_next;
            } else {
                i4 = R$drawable.cli_media_ic_player_play;
                i3 = R$drawable.cli_media_ic_player_pause;
                i2 = R$drawable.cli_media_ic_player_prev;
                i = R$drawable.cli_media_ic_player_next;
            }
            int i5 = i4;
            int i6 = i3;
            int i7 = i2;
            int i8 = i;
            if (actions == 0) {
                if (z) {
                    Log.d("CLI-QSMV-InternalMediaNotification", "No playback actions set. Adding based on play state: " + playbackState.getState());
                }
                if (playbackState.getState() == 3) {
                    arrayList2 = arrayList3;
                    addAction(context, actions, arrayList3, 0, i6, token, "com.android.systemui.ACTION_MEDIA_PAUSE", resources3.getString(R$string.cli_media_action_title_pause), cliMediaViewPagerOwn);
                } else if (playbackState.getState() == 2) {
                    arrayList2 = arrayList3;
                    addAction(context, actions, arrayList3, 0, i5, token, "com.android.systemui.ACTION_MEDIA_PLAY", resources3.getString(R$string.cli_media_action_title_play), cliMediaViewPagerOwn);
                } else {
                    arrayList = arrayList3;
                }
                arrayList = arrayList2;
            } else {
                Resources resources4 = resources3;
                arrayList = arrayList3;
                addAction(context, actions, arrayList3, 16, i7, token, "com.android.systemui.ACTION_MEDIA_PREVIOUS", resources3.getString(R$string.cli_media_action_title_previous), cliMediaViewPagerOwn);
                if ((actions & 512) == 512) {
                    int i9 = playbackState.getState() == 3 ? i6 : i5;
                    if (playbackState.getState() == 3) {
                        resources2 = resources4;
                        str = resources2.getString(R$string.cli_media_action_title_pause);
                    } else {
                        resources2 = resources4;
                        str = resources2.getString(R$string.cli_media_action_title_play);
                    }
                    resources = resources2;
                    addAction(context, actions, arrayList, 512, i9, token, "com.android.systemui.ACTION_PLAY_PAUSE", str, cliMediaViewPagerOwn);
                } else {
                    resources = resources4;
                    if (playbackState.getState() == 3) {
                        addAction(context, actions, arrayList, 2, i6, token, "com.android.systemui.ACTION_MEDIA_PAUSE", resources.getString(R$string.cli_media_action_title_pause), cliMediaViewPagerOwn);
                    } else if (playbackState.getState() == 2) {
                        addAction(context, actions, arrayList, 4, i5, token, "com.android.systemui.ACTION_MEDIA_PLAY", resources.getString(R$string.cli_media_action_title_play), cliMediaViewPagerOwn);
                    }
                }
                addAction(context, actions, arrayList, 32, i8, token, "com.android.systemui.ACTION_MEDIA_NEXT", resources.getString(R$string.cli_media_action_title_next), cliMediaViewPagerOwn);
            }
            if (z) {
                Log.d("CLI-QSMV-InternalMediaNotification", "Actions count: " + arrayList.size());
            }
            this.actions = (Notification.Action[]) arrayList.toArray(new Notification.Action[arrayList.size()]);
        } else if (DEBUG) {
            Log.d("CLI-QSMV-InternalMediaNotification", "initializeActions: Couldn't find the playbackState");
        }
    }

    private void addAction(Context context, long j, ArrayList<Notification.Action> arrayList, long j2, int i, MediaSession.Token token, String str, String str2, CliMediaViewPagerOwn cliMediaViewPagerOwn) {
        if (DEBUG) {
            Log.d("CLI-QSMV-InternalMediaNotification", "(playbackActions & actionMask) = " + (j & j2) + "  actionMask = " + j2 + "  own = " + cliMediaViewPagerOwn.name());
        }
        if ((j & j2) == j2) {
            Intent intent = new Intent(str);
            intent.setPackage(context.getPackageName());
            intent.putExtra("EXTRA_TOKEN", token);
            intent.putExtra("EXTRA_CLI_MEDIA_VIEW_PAGER_OWN", cliMediaViewPagerOwn.name());
            int hashCode = token.hashCode();
            if (cliMediaViewPagerOwn == CliMediaViewPagerOwn.Keyguard) {
                hashCode++;
            } else if (cliMediaViewPagerOwn == CliMediaViewPagerOwn.PRC_TILE) {
                hashCode += 2;
            } else if (cliMediaViewPagerOwn == CliMediaViewPagerOwn.PRC_EXPANDED) {
                hashCode += 3;
            }
            Notification.Action.Builder builder = new Notification.Action.Builder(Icon.createWithResource(context, i), str2, PendingIntent.getBroadcast(context, hashCode, intent, 201326592));
            builder.getExtras().putBoolean("INTERNAL_ACTION_EXTRA", true);
            arrayList.add(builder.build());
        }
    }

    /* JADX WARNING: type inference failed for: r6v3, types: [android.os.Parcelable] */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void initializeExtras(android.content.Context r5, android.media.MediaMetadata r6, android.app.Notification r7) {
        /*
            r4 = this;
            android.os.Bundle r0 = r4.extras
            java.lang.String r1 = "android.template"
            java.lang.String r2 = "android.app.Notification$InternalMediaStyle"
            r0.putString(r1, r2)
            java.lang.String r0 = "CLI-QSMV-InternalMediaNotification"
            if (r6 == 0) goto L_0x0078
            java.lang.String r1 = "android.media.metadata.ARTIST"
            java.lang.CharSequence r1 = r6.getText(r1)
            if (r1 != 0) goto L_0x001b
            java.lang.String r1 = "android.media.metadata.ALBUM_ARTIST"
            java.lang.CharSequence r1 = r6.getText(r1)
        L_0x001b:
            if (r1 == 0) goto L_0x0025
            android.os.Bundle r2 = r4.extras
            java.lang.String r3 = "android.bigText"
            r2.putCharSequence(r3, r1)
            goto L_0x002e
        L_0x0025:
            boolean r1 = DEBUG
            if (r1 == 0) goto L_0x002e
            java.lang.String r1 = "No artist found"
            android.util.Log.d(r0, r1)
        L_0x002e:
            java.lang.String r1 = "android.media.metadata.TITLE"
            java.lang.CharSequence r1 = r6.getText(r1)
            if (r1 == 0) goto L_0x003e
            android.os.Bundle r2 = r4.extras
            java.lang.String r3 = "android.title.big"
            r2.putCharSequence(r3, r1)
            goto L_0x0047
        L_0x003e:
            boolean r1 = DEBUG
            if (r1 == 0) goto L_0x0047
            java.lang.String r1 = "No title found"
            android.util.Log.d(r0, r1)
        L_0x0047:
            java.lang.String r1 = "android.media.metadata.ALBUM"
            java.lang.CharSequence r1 = r6.getText(r1)
            if (r1 == 0) goto L_0x0057
            android.os.Bundle r2 = r4.extras
            java.lang.String r3 = "android.subText"
            r2.putCharSequence(r3, r1)
            goto L_0x0060
        L_0x0057:
            boolean r1 = DEBUG
            if (r1 == 0) goto L_0x0060
            java.lang.String r1 = "No album found"
            android.util.Log.d(r0, r1)
        L_0x0060:
            java.lang.String r1 = "android.media.metadata.ALBUM_ART"
            android.graphics.Bitmap r1 = r6.getBitmap(r1)
            if (r1 != 0) goto L_0x0079
            boolean r1 = DEBUG
            if (r1 == 0) goto L_0x0071
            java.lang.String r1 = "KEY_ALBUM_ART not found, falling back to metadata KEY_ART"
            android.util.Log.d(r0, r1)
        L_0x0071:
            java.lang.String r1 = "android.media.metadata.ART"
            android.graphics.Bitmap r1 = r6.getBitmap(r1)
            goto L_0x0079
        L_0x0078:
            r1 = 0
        L_0x0079:
            if (r1 != 0) goto L_0x008f
            boolean r6 = DEBUG
            if (r6 == 0) goto L_0x0084
            java.lang.String r6 = "KEY_ART not found, falling back to notification EXTRA_PICTURE"
            android.util.Log.d(r0, r6)
        L_0x0084:
            android.os.Bundle r6 = r7.extras
            java.lang.String r0 = "android.picture"
            android.os.Parcelable r6 = r6.getParcelable(r0)
            r1 = r6
            android.graphics.Bitmap r1 = (android.graphics.Bitmap) r1
        L_0x008f:
            if (r1 != 0) goto L_0x0095
            android.graphics.Bitmap r1 = r4.getBitmapFromNotificationView(r7, r5)
        L_0x0095:
            if (r1 == 0) goto L_0x009a
            r4.addPictureExtra(r5, r1)
        L_0x009a:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.motorola.systemui.cli.media.InternalMediaNotification.initializeExtras(android.content.Context, android.media.MediaMetadata, android.app.Notification):void");
    }

    private void addPictureExtra(Context context, Bitmap bitmap) {
        Resources resources = context.getResources();
        int dimensionPixelSize = resources.getDimensionPixelSize(R$dimen.cli_notification_max_media_photo_width);
        int dimensionPixelSize2 = resources.getDimensionPixelSize(R$dimen.cli_notification_max_media_photo_height);
        int height = bitmap.getHeight();
        int width = bitmap.getWidth();
        if (height > dimensionPixelSize2 || width > dimensionPixelSize) {
            if (DEBUG) {
                Log.d("CLI-QSMV-InternalMediaNotification", "Rescaling bitmap");
            }
            bitmap = new BitmapBuilder(bitmap).scale(dimensionPixelSize, dimensionPixelSize2).build();
        }
        this.extras.putParcelable("android.picture", bitmap);
    }

    private Bitmap getBitmapFromNotificationView(Notification notification, Context context) {
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
        } catch (Resources.NotFoundException | InflateException | RemoteViews.ActionException | SecurityException unused) {
            if (DEBUG) {
                Log.d("CLI-QSMV-InternalMediaNotification", "getBitmapFromNotificationView: View can't be inflated");
            }
        }
        if (view != null) {
            return getChildImageView(view);
        }
        return null;
    }

    private Bitmap getChildImageView(View view) {
        if (!(view instanceof ViewGroup)) {
            return null;
        }
        ViewGroup viewGroup = (ViewGroup) view;
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            View childAt = viewGroup.getChildAt(i);
            if (childAt instanceof ImageView) {
                return new BitmapBuilder(((ImageView) childAt).getDrawable()).build();
            }
        }
        return null;
    }
}
