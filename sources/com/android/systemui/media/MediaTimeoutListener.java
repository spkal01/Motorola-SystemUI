package com.android.systemui.media;

import android.media.session.MediaController;
import android.media.session.PlaybackState;
import android.util.Log;
import com.android.systemui.media.MediaDataManager;
import com.android.systemui.statusbar.NotificationMediaManager;
import com.android.systemui.util.concurrency.DelayableExecutor;
import java.util.LinkedHashMap;
import java.util.Map;
import kotlin.Unit;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/* compiled from: MediaTimeoutListener.kt */
public final class MediaTimeoutListener implements MediaDataManager.Listener {
    /* access modifiers changed from: private */
    @NotNull
    public final DelayableExecutor mainExecutor;
    /* access modifiers changed from: private */
    @NotNull
    public final MediaControllerFactory mediaControllerFactory;
    /* access modifiers changed from: private */
    @NotNull
    public final Map<String, PlaybackStateListener> mediaListeners = new LinkedHashMap();
    public Function2<? super String, ? super Boolean, Unit> timeoutCallback;

    public MediaTimeoutListener(@NotNull MediaControllerFactory mediaControllerFactory2, @NotNull DelayableExecutor delayableExecutor) {
        Intrinsics.checkNotNullParameter(mediaControllerFactory2, "mediaControllerFactory");
        Intrinsics.checkNotNullParameter(delayableExecutor, "mainExecutor");
        this.mediaControllerFactory = mediaControllerFactory2;
        this.mainExecutor = delayableExecutor;
    }

    public void onSmartspaceMediaDataLoaded(@NotNull String str, @NotNull SmartspaceMediaData smartspaceMediaData, boolean z) {
        MediaDataManager.Listener.DefaultImpls.onSmartspaceMediaDataLoaded(this, str, smartspaceMediaData, z);
    }

    public void onSmartspaceMediaDataRemoved(@NotNull String str, boolean z) {
        MediaDataManager.Listener.DefaultImpls.onSmartspaceMediaDataRemoved(this, str, z);
    }

    @NotNull
    public final Function2<String, Boolean, Unit> getTimeoutCallback() {
        Function2<? super String, ? super Boolean, Unit> function2 = this.timeoutCallback;
        if (function2 != null) {
            return function2;
        }
        Intrinsics.throwUninitializedPropertyAccessException("timeoutCallback");
        throw null;
    }

    public final void setTimeoutCallback(@NotNull Function2<? super String, ? super Boolean, Unit> function2) {
        Intrinsics.checkNotNullParameter(function2, "<set-?>");
        this.timeoutCallback = function2;
    }

    /* JADX WARNING: Failed to insert additional move for type inference */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onMediaDataLoaded(@org.jetbrains.annotations.NotNull java.lang.String r4, @org.jetbrains.annotations.Nullable java.lang.String r5, @org.jetbrains.annotations.NotNull com.android.systemui.media.MediaData r6, boolean r7, boolean r8) {
        /*
            r3 = this;
            java.lang.String r7 = "key"
            kotlin.jvm.internal.Intrinsics.checkNotNullParameter(r4, r7)
            java.lang.String r7 = "data"
            kotlin.jvm.internal.Intrinsics.checkNotNullParameter(r6, r7)
            java.util.Map<java.lang.String, com.android.systemui.media.MediaTimeoutListener$PlaybackStateListener> r7 = r3.mediaListeners
            java.lang.Object r7 = r7.get(r4)
            com.android.systemui.media.MediaTimeoutListener$PlaybackStateListener r7 = (com.android.systemui.media.MediaTimeoutListener.PlaybackStateListener) r7
            java.lang.String r8 = "MediaTimeout"
            if (r7 != 0) goto L_0x0018
            r7 = 0
            goto L_0x0028
        L_0x0018:
            boolean r0 = r7.getDestroyed()
            if (r0 != 0) goto L_0x001f
            return
        L_0x001f:
            java.lang.String r0 = "Reusing destroyed listener "
            java.lang.String r0 = kotlin.jvm.internal.Intrinsics.stringPlus(r0, r4)
            android.util.Log.d(r8, r0)
        L_0x0028:
            r0 = 0
            if (r5 == 0) goto L_0x0033
            boolean r1 = kotlin.jvm.internal.Intrinsics.areEqual((java.lang.Object) r4, (java.lang.Object) r5)
            if (r1 != 0) goto L_0x0033
            r1 = 1
            goto L_0x0034
        L_0x0033:
            r1 = r0
        L_0x0034:
            if (r1 == 0) goto L_0x008a
            java.util.Map<java.lang.String, com.android.systemui.media.MediaTimeoutListener$PlaybackStateListener> r7 = r3.mediaListeners
            java.lang.String r1 = "null cannot be cast to non-null type kotlin.collections.MutableMap<K, V>"
            java.util.Objects.requireNonNull(r7, r1)
            java.util.Map r7 = kotlin.jvm.internal.TypeIntrinsics.asMutableMap(r7)
            java.lang.Object r7 = r7.remove(r5)
            if (r7 == 0) goto L_0x0069
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = "migrating key "
            r1.append(r2)
            r1.append(r5)
            java.lang.String r5 = " to "
            r1.append(r5)
            r1.append(r4)
            java.lang.String r5 = ", for resumption"
            r1.append(r5)
            java.lang.String r5 = r1.toString()
            android.util.Log.d(r8, r5)
            goto L_0x008a
        L_0x0069:
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = "Old key "
            r1.append(r2)
            r1.append(r5)
            java.lang.String r5 = " for player "
            r1.append(r5)
            r1.append(r4)
            java.lang.String r5 = " doesn't exist. Continuing..."
            r1.append(r5)
            java.lang.String r5 = r1.toString()
            android.util.Log.w(r8, r5)
        L_0x008a:
            com.android.systemui.media.MediaTimeoutListener$PlaybackStateListener r7 = (com.android.systemui.media.MediaTimeoutListener.PlaybackStateListener) r7
            if (r7 != 0) goto L_0x0099
            java.util.Map<java.lang.String, com.android.systemui.media.MediaTimeoutListener$PlaybackStateListener> r5 = r3.mediaListeners
            com.android.systemui.media.MediaTimeoutListener$PlaybackStateListener r7 = new com.android.systemui.media.MediaTimeoutListener$PlaybackStateListener
            r7.<init>(r3, r4, r6)
            r5.put(r4, r7)
            return
        L_0x0099:
            java.lang.Boolean r5 = r7.getPlaying()
            if (r5 != 0) goto L_0x00a0
            goto L_0x00a4
        L_0x00a0:
            boolean r0 = r5.booleanValue()
        L_0x00a4:
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            java.lang.String r1 = "updating listener for "
            r5.append(r1)
            r5.append(r4)
            java.lang.String r1 = ", was playing? "
            r5.append(r1)
            r5.append(r0)
            java.lang.String r5 = r5.toString()
            android.util.Log.d(r8, r5)
            r7.setMediaData(r6)
            r7.setKey(r4)
            java.util.Map<java.lang.String, com.android.systemui.media.MediaTimeoutListener$PlaybackStateListener> r5 = r3.mediaListeners
            r5.put(r4, r7)
            java.lang.Boolean r5 = java.lang.Boolean.valueOf(r0)
            java.lang.Boolean r6 = r7.getPlaying()
            boolean r5 = kotlin.jvm.internal.Intrinsics.areEqual((java.lang.Object) r5, (java.lang.Object) r6)
            if (r5 != 0) goto L_0x00e4
            com.android.systemui.util.concurrency.DelayableExecutor r5 = r3.mainExecutor
            com.android.systemui.media.MediaTimeoutListener$onMediaDataLoaded$2$1 r6 = new com.android.systemui.media.MediaTimeoutListener$onMediaDataLoaded$2$1
            r6.<init>(r3, r4)
            r5.execute(r6)
        L_0x00e4:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.media.MediaTimeoutListener.onMediaDataLoaded(java.lang.String, java.lang.String, com.android.systemui.media.MediaData, boolean, boolean):void");
    }

    public void onMediaDataRemoved(@NotNull String str) {
        Intrinsics.checkNotNullParameter(str, "key");
        PlaybackStateListener remove = this.mediaListeners.remove(str);
        if (remove != null) {
            remove.destroy();
        }
    }

    /* compiled from: MediaTimeoutListener.kt */
    private final class PlaybackStateListener extends MediaController.Callback {
        /* access modifiers changed from: private */
        @Nullable
        public Runnable cancellation;
        private boolean destroyed;
        @NotNull
        private String key;
        @Nullable
        private MediaController mediaController;
        @NotNull
        private MediaData mediaData;
        @Nullable
        private Boolean playing;
        final /* synthetic */ MediaTimeoutListener this$0;
        private boolean timedOut;

        public PlaybackStateListener(@NotNull MediaTimeoutListener mediaTimeoutListener, @NotNull String str, MediaData mediaData2) {
            Intrinsics.checkNotNullParameter(mediaTimeoutListener, "this$0");
            Intrinsics.checkNotNullParameter(str, "key");
            Intrinsics.checkNotNullParameter(mediaData2, "data");
            this.this$0 = mediaTimeoutListener;
            this.key = str;
            this.mediaData = mediaData2;
            setMediaData(mediaData2);
        }

        @NotNull
        public final String getKey() {
            return this.key;
        }

        public final void setKey(@NotNull String str) {
            Intrinsics.checkNotNullParameter(str, "<set-?>");
            this.key = str;
        }

        public final boolean getTimedOut() {
            return this.timedOut;
        }

        public final void setTimedOut(boolean z) {
            this.timedOut = z;
        }

        @Nullable
        public final Boolean getPlaying() {
            return this.playing;
        }

        public final boolean getDestroyed() {
            return this.destroyed;
        }

        public final void setMediaData(@NotNull MediaData mediaData2) {
            Intrinsics.checkNotNullParameter(mediaData2, "value");
            this.destroyed = false;
            MediaController mediaController2 = this.mediaController;
            if (mediaController2 != null) {
                mediaController2.unregisterCallback(this);
            }
            this.mediaData = mediaData2;
            PlaybackState playbackState = null;
            MediaController create = mediaData2.getToken() != null ? this.this$0.mediaControllerFactory.create(this.mediaData.getToken()) : null;
            this.mediaController = create;
            if (create != null) {
                create.registerCallback(this);
            }
            MediaController mediaController3 = this.mediaController;
            if (mediaController3 != null) {
                playbackState = mediaController3.getPlaybackState();
            }
            processState(playbackState, false);
        }

        public final void destroy() {
            MediaController mediaController2 = this.mediaController;
            if (mediaController2 != null) {
                mediaController2.unregisterCallback(this);
            }
            Runnable runnable = this.cancellation;
            if (runnable != null) {
                runnable.run();
            }
            this.destroyed = true;
        }

        public void onPlaybackStateChanged(@Nullable PlaybackState playbackState) {
            processState(playbackState, true);
        }

        public void onSessionDestroyed() {
            Log.d("MediaTimeout", Intrinsics.stringPlus("Session destroyed for ", this.key));
            destroy();
        }

        private final void processState(PlaybackState playbackState, boolean z) {
            Log.v("MediaTimeout", "processState " + this.key + ": " + playbackState);
            boolean z2 = playbackState != null && NotificationMediaManager.isPlayingState(playbackState.getState());
            if (!Intrinsics.areEqual((Object) this.playing, (Object) Boolean.valueOf(z2)) || this.playing == null) {
                this.playing = Boolean.valueOf(z2);
                if (!z2) {
                    Log.v("MediaTimeout", Intrinsics.stringPlus("schedule timeout for ", this.key));
                    if (this.cancellation != null) {
                        Log.d("MediaTimeout", "cancellation already exists, continuing.");
                        return;
                    }
                    expireMediaTimeout(this.key, Intrinsics.stringPlus("PLAYBACK STATE CHANGED - ", playbackState));
                    this.cancellation = this.this$0.mainExecutor.executeDelayed(new MediaTimeoutListener$PlaybackStateListener$processState$1(this, this.this$0), MediaTimeoutListenerKt.PAUSED_MEDIA_TIMEOUT);
                    return;
                }
                String str = this.key;
                expireMediaTimeout(str, "playback started - " + playbackState + ", " + this.key);
                this.timedOut = false;
                if (z) {
                    this.this$0.getTimeoutCallback().invoke(this.key, Boolean.valueOf(this.timedOut));
                }
            }
        }

        private final void expireMediaTimeout(String str, String str2) {
            Runnable runnable = this.cancellation;
            if (runnable != null) {
                Log.v("MediaTimeout", "media timeout cancelled for  " + str + ", reason: " + str2);
                runnable.run();
            }
            this.cancellation = null;
        }
    }
}
