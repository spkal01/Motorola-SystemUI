package com.android.systemui.media;

import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.media.MediaDescription;
import android.media.browse.MediaBrowser;
import android.media.session.MediaController;
import android.media.session.MediaSession;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import com.android.internal.annotations.VisibleForTesting;
import java.util.List;

public class ResumeMediaBrowser {
    private MediaBrowserFactory mBrowserFactory;
    /* access modifiers changed from: private */
    public final Callback mCallback;
    /* access modifiers changed from: private */
    public ComponentName mComponentName;
    private final MediaBrowser.ConnectionCallback mConnectionCallback = new MediaBrowser.ConnectionCallback() {
        public void onConnected() {
            Log.d("ResumeMediaBrowser", "Service connected for " + ResumeMediaBrowser.this.mComponentName);
            if (ResumeMediaBrowser.this.mMediaBrowser != null && ResumeMediaBrowser.this.mMediaBrowser.isConnected()) {
                String root = ResumeMediaBrowser.this.mMediaBrowser.getRoot();
                if (!TextUtils.isEmpty(root)) {
                    if (ResumeMediaBrowser.this.mCallback != null) {
                        ResumeMediaBrowser.this.mCallback.onConnected();
                    }
                    if (ResumeMediaBrowser.this.mMediaBrowser != null) {
                        ResumeMediaBrowser.this.mMediaBrowser.subscribe(root, ResumeMediaBrowser.this.mSubscriptionCallback);
                        return;
                    }
                    return;
                }
            }
            if (ResumeMediaBrowser.this.mCallback != null) {
                ResumeMediaBrowser.this.mCallback.onError();
            }
            ResumeMediaBrowser.this.disconnect();
        }

        public void onConnectionSuspended() {
            Log.d("ResumeMediaBrowser", "Connection suspended for " + ResumeMediaBrowser.this.mComponentName);
            if (ResumeMediaBrowser.this.mCallback != null) {
                ResumeMediaBrowser.this.mCallback.onError();
            }
            ResumeMediaBrowser.this.disconnect();
        }

        public void onConnectionFailed() {
            Log.d("ResumeMediaBrowser", "Connection failed for " + ResumeMediaBrowser.this.mComponentName);
            if (ResumeMediaBrowser.this.mCallback != null) {
                ResumeMediaBrowser.this.mCallback.onError();
            }
            ResumeMediaBrowser.this.disconnect();
        }
    };
    private final Context mContext;
    /* access modifiers changed from: private */
    public MediaBrowser mMediaBrowser;
    /* access modifiers changed from: private */
    public final MediaBrowser.SubscriptionCallback mSubscriptionCallback = new MediaBrowser.SubscriptionCallback() {
        public void onChildrenLoaded(String str, List<MediaBrowser.MediaItem> list) {
            if (list.size() == 0) {
                Log.d("ResumeMediaBrowser", "No children found for " + ResumeMediaBrowser.this.mComponentName);
                if (ResumeMediaBrowser.this.mCallback != null) {
                    ResumeMediaBrowser.this.mCallback.onError();
                }
            } else {
                MediaBrowser.MediaItem mediaItem = list.get(0);
                MediaDescription description = mediaItem.getDescription();
                if (!mediaItem.isPlayable() || ResumeMediaBrowser.this.mMediaBrowser == null || !ResumeMediaBrowser.this.mMediaBrowser.isConnected()) {
                    Log.d("ResumeMediaBrowser", "Child found but not playable for " + ResumeMediaBrowser.this.mComponentName);
                    if (ResumeMediaBrowser.this.mCallback != null) {
                        ResumeMediaBrowser.this.mCallback.onError();
                    }
                } else if (ResumeMediaBrowser.this.mCallback != null) {
                    try {
                        ResumeMediaBrowser.this.mCallback.addTrack(description, ResumeMediaBrowser.this.mMediaBrowser.getServiceComponent(), ResumeMediaBrowser.this);
                    } catch (IllegalStateException unused) {
                        Log.e("ResumeMediaBrowser", "IllegalStateException: MediaBrowser is not connected");
                        ResumeMediaBrowser.this.mCallback.onError();
                    }
                }
            }
            ResumeMediaBrowser.this.disconnect();
        }

        public void onError(String str) {
            Log.d("ResumeMediaBrowser", "Subscribe error for " + ResumeMediaBrowser.this.mComponentName + ": " + str);
            if (ResumeMediaBrowser.this.mCallback != null) {
                ResumeMediaBrowser.this.mCallback.onError();
            }
            ResumeMediaBrowser.this.disconnect();
        }

        public void onError(String str, Bundle bundle) {
            Log.d("ResumeMediaBrowser", "Subscribe error for " + ResumeMediaBrowser.this.mComponentName + ": " + str + ", options: " + bundle);
            if (ResumeMediaBrowser.this.mCallback != null) {
                ResumeMediaBrowser.this.mCallback.onError();
            }
            ResumeMediaBrowser.this.disconnect();
        }
    };

    public static class Callback {
        public void addTrack(MediaDescription mediaDescription, ComponentName componentName, ResumeMediaBrowser resumeMediaBrowser) {
            throw null;
        }

        public void onConnected() {
        }

        public void onError() {
        }
    }

    public ResumeMediaBrowser(Context context, Callback callback, ComponentName componentName, MediaBrowserFactory mediaBrowserFactory) {
        this.mContext = context;
        this.mCallback = callback;
        this.mComponentName = componentName;
        this.mBrowserFactory = mediaBrowserFactory;
    }

    public void findRecentMedia() {
        Log.d("ResumeMediaBrowser", "Connecting to " + this.mComponentName);
        disconnect();
        Bundle bundle = new Bundle();
        bundle.putBoolean("android.service.media.extra.RECENT", true);
        MediaBrowser create = this.mBrowserFactory.create(this.mComponentName, this.mConnectionCallback, bundle);
        this.mMediaBrowser = create;
        create.connect();
    }

    /* access modifiers changed from: protected */
    public void disconnect() {
        MediaBrowser mediaBrowser = this.mMediaBrowser;
        if (mediaBrowser != null) {
            mediaBrowser.disconnect();
        }
        this.mMediaBrowser = null;
    }

    public void restart() {
        disconnect();
        Bundle bundle = new Bundle();
        bundle.putBoolean("android.service.media.extra.RECENT", true);
        MediaBrowser create = this.mBrowserFactory.create(this.mComponentName, new MediaBrowser.ConnectionCallback() {
            public void onConnected() {
                if (ResumeMediaBrowser.this.mMediaBrowser == null || !ResumeMediaBrowser.this.mMediaBrowser.isConnected()) {
                    if (ResumeMediaBrowser.this.mCallback != null) {
                        ResumeMediaBrowser.this.mCallback.onError();
                    }
                    ResumeMediaBrowser.this.disconnect();
                    return;
                }
                Log.d("ResumeMediaBrowser", "Connected for restart " + ResumeMediaBrowser.this.mMediaBrowser.isConnected());
                MediaController createMediaController = ResumeMediaBrowser.this.createMediaController(ResumeMediaBrowser.this.mMediaBrowser.getSessionToken());
                createMediaController.getTransportControls();
                createMediaController.getTransportControls().prepare();
                createMediaController.getTransportControls().play();
                if (ResumeMediaBrowser.this.mCallback != null) {
                    ResumeMediaBrowser.this.mCallback.onConnected();
                }
            }

            public void onConnectionFailed() {
                if (ResumeMediaBrowser.this.mCallback != null) {
                    ResumeMediaBrowser.this.mCallback.onError();
                }
                ResumeMediaBrowser.this.disconnect();
            }

            public void onConnectionSuspended() {
                if (ResumeMediaBrowser.this.mCallback != null) {
                    ResumeMediaBrowser.this.mCallback.onError();
                }
                ResumeMediaBrowser.this.disconnect();
            }
        }, bundle);
        this.mMediaBrowser = create;
        create.connect();
    }

    /* access modifiers changed from: protected */
    @VisibleForTesting
    public MediaController createMediaController(MediaSession.Token token) {
        return new MediaController(this.mContext, token);
    }

    public MediaSession.Token getToken() {
        MediaBrowser mediaBrowser = this.mMediaBrowser;
        if (mediaBrowser == null || !mediaBrowser.isConnected()) {
            return null;
        }
        return this.mMediaBrowser.getSessionToken();
    }

    public PendingIntent getAppIntent() {
        return PendingIntent.getActivity(this.mContext, 0, this.mContext.getPackageManager().getLaunchIntentForPackage(this.mComponentName.getPackageName()), 33554432);
    }

    public void testConnection() {
        disconnect();
        Bundle bundle = new Bundle();
        bundle.putBoolean("android.service.media.extra.RECENT", true);
        MediaBrowser create = this.mBrowserFactory.create(this.mComponentName, this.mConnectionCallback, bundle);
        this.mMediaBrowser = create;
        create.connect();
    }
}
