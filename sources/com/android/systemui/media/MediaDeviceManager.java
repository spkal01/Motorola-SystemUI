package com.android.systemui.media;

import android.graphics.drawable.Drawable;
import android.media.MediaRouter2Manager;
import android.media.RoutingSessionInfo;
import android.media.session.MediaController;
import android.media.session.MediaSession;
import com.android.settingslib.media.LocalMediaManager;
import com.android.settingslib.media.MediaDevice;
import com.android.systemui.Dumpable;
import com.android.systemui.dump.DumpManager;
import com.android.systemui.media.MediaDataManager;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executor;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/* compiled from: MediaDeviceManager.kt */
public final class MediaDeviceManager implements MediaDataManager.Listener, Dumpable {
    /* access modifiers changed from: private */
    @NotNull
    public final Executor bgExecutor;
    @NotNull
    private final MediaControllerFactory controllerFactory;
    @NotNull
    private final Map<String, Entry> entries = new LinkedHashMap();
    /* access modifiers changed from: private */
    @NotNull
    public final Executor fgExecutor;
    @NotNull
    private final Set<Listener> listeners = new LinkedHashSet();
    @NotNull
    private final LocalMediaManagerFactory localMediaManagerFactory;
    /* access modifiers changed from: private */
    @NotNull
    public final MediaRouter2Manager mr2manager;

    /* compiled from: MediaDeviceManager.kt */
    public interface Listener {
        void onKeyRemoved(@NotNull String str);

        void onMediaDeviceChanged(@NotNull String str, @Nullable String str2, @Nullable MediaDeviceData mediaDeviceData);
    }

    public MediaDeviceManager(@NotNull MediaControllerFactory mediaControllerFactory, @NotNull LocalMediaManagerFactory localMediaManagerFactory2, @NotNull MediaRouter2Manager mediaRouter2Manager, @NotNull Executor executor, @NotNull Executor executor2, @NotNull DumpManager dumpManager) {
        Intrinsics.checkNotNullParameter(mediaControllerFactory, "controllerFactory");
        Intrinsics.checkNotNullParameter(localMediaManagerFactory2, "localMediaManagerFactory");
        Intrinsics.checkNotNullParameter(mediaRouter2Manager, "mr2manager");
        Intrinsics.checkNotNullParameter(executor, "fgExecutor");
        Intrinsics.checkNotNullParameter(executor2, "bgExecutor");
        Intrinsics.checkNotNullParameter(dumpManager, "dumpManager");
        this.controllerFactory = mediaControllerFactory;
        this.localMediaManagerFactory = localMediaManagerFactory2;
        this.mr2manager = mediaRouter2Manager;
        this.fgExecutor = executor;
        this.bgExecutor = executor2;
        String name = MediaDeviceManager.class.getName();
        Intrinsics.checkNotNullExpressionValue(name, "javaClass.name");
        dumpManager.registerDumpable(name, this);
    }

    public void onSmartspaceMediaDataLoaded(@NotNull String str, @NotNull SmartspaceMediaData smartspaceMediaData, boolean z) {
        MediaDataManager.Listener.DefaultImpls.onSmartspaceMediaDataLoaded(this, str, smartspaceMediaData, z);
    }

    public void onSmartspaceMediaDataRemoved(@NotNull String str, boolean z) {
        MediaDataManager.Listener.DefaultImpls.onSmartspaceMediaDataRemoved(this, str, z);
    }

    public final boolean addListener(@NotNull Listener listener) {
        Intrinsics.checkNotNullParameter(listener, "listener");
        return this.listeners.add(listener);
    }

    public void onMediaDataLoaded(@NotNull String str, @Nullable String str2, @NotNull MediaData mediaData, boolean z, boolean z2) {
        MediaController mediaController;
        Entry remove;
        Intrinsics.checkNotNullParameter(str, "key");
        Intrinsics.checkNotNullParameter(mediaData, "data");
        if (!(str2 == null || Intrinsics.areEqual((Object) str2, (Object) str) || (remove = this.entries.remove(str2)) == null)) {
            remove.stop();
        }
        Entry entry = this.entries.get(str);
        if (entry == null || !Intrinsics.areEqual((Object) entry.getToken(), (Object) mediaData.getToken())) {
            if (entry != null) {
                entry.stop();
            }
            MediaSession.Token token = mediaData.getToken();
            if (token == null) {
                mediaController = null;
            } else {
                mediaController = this.controllerFactory.create(token);
            }
            Entry entry2 = new Entry(this, str, str2, mediaController, this.localMediaManagerFactory.create(mediaData.getPackageName()));
            this.entries.put(str, entry2);
            entry2.start();
        }
    }

    public void onMediaDataRemoved(@NotNull String str) {
        Intrinsics.checkNotNullParameter(str, "key");
        Entry remove = this.entries.remove(str);
        if (remove != null) {
            remove.stop();
        }
        if (remove != null) {
            for (Listener onKeyRemoved : this.listeners) {
                onKeyRemoved.onKeyRemoved(str);
            }
        }
    }

    public void dump(@NotNull FileDescriptor fileDescriptor, @NotNull PrintWriter printWriter, @NotNull String[] strArr) {
        Intrinsics.checkNotNullParameter(fileDescriptor, "fd");
        Intrinsics.checkNotNullParameter(printWriter, "pw");
        Intrinsics.checkNotNullParameter(strArr, "args");
        printWriter.println("MediaDeviceManager state:");
        this.entries.forEach(new MediaDeviceManager$dump$1$1(printWriter, fileDescriptor, printWriter, strArr));
    }

    /* access modifiers changed from: private */
    public final void processDevice(String str, String str2, MediaDevice mediaDevice) {
        boolean z = mediaDevice != null;
        String str3 = null;
        Drawable iconWithoutBackground = mediaDevice == null ? null : mediaDevice.getIconWithoutBackground();
        if (mediaDevice != null) {
            str3 = mediaDevice.getName();
        }
        MediaDeviceData mediaDeviceData = new MediaDeviceData(z, iconWithoutBackground, str3);
        for (Listener onMediaDeviceChanged : this.listeners) {
            onMediaDeviceChanged.onMediaDeviceChanged(str, str2, mediaDeviceData);
        }
    }

    /* compiled from: MediaDeviceManager.kt */
    private final class Entry extends MediaController.Callback implements LocalMediaManager.DeviceCallback {
        @Nullable
        private final MediaController controller;
        @Nullable
        private MediaDevice current;
        @NotNull
        private final String key;
        @NotNull
        private final LocalMediaManager localMediaManager;
        @Nullable
        private final String oldKey;
        /* access modifiers changed from: private */
        public int playbackType;
        /* access modifiers changed from: private */
        public boolean started;
        final /* synthetic */ MediaDeviceManager this$0;

        public Entry(@NotNull MediaDeviceManager mediaDeviceManager, @Nullable String str, @Nullable String str2, @NotNull MediaController mediaController, LocalMediaManager localMediaManager2) {
            Intrinsics.checkNotNullParameter(mediaDeviceManager, "this$0");
            Intrinsics.checkNotNullParameter(str, "key");
            Intrinsics.checkNotNullParameter(localMediaManager2, "localMediaManager");
            this.this$0 = mediaDeviceManager;
            this.key = str;
            this.oldKey = str2;
            this.controller = mediaController;
            this.localMediaManager = localMediaManager2;
        }

        @NotNull
        public final String getKey() {
            return this.key;
        }

        @Nullable
        public final String getOldKey() {
            return this.oldKey;
        }

        @Nullable
        public final MediaController getController() {
            return this.controller;
        }

        @NotNull
        public final LocalMediaManager getLocalMediaManager() {
            return this.localMediaManager;
        }

        @Nullable
        public final MediaSession.Token getToken() {
            MediaController mediaController = this.controller;
            if (mediaController == null) {
                return null;
            }
            return mediaController.getSessionToken();
        }

        private final void setCurrent(MediaDevice mediaDevice) {
            if (!this.started || !Intrinsics.areEqual((Object) mediaDevice, (Object) this.current)) {
                this.current = mediaDevice;
                this.this$0.fgExecutor.execute(new MediaDeviceManager$Entry$current$1(this.this$0, this, mediaDevice));
            }
        }

        public final void start() {
            this.this$0.bgExecutor.execute(new MediaDeviceManager$Entry$start$1(this));
        }

        public final void stop() {
            this.this$0.bgExecutor.execute(new MediaDeviceManager$Entry$stop$1(this));
        }

        public final void dump(@NotNull FileDescriptor fileDescriptor, @NotNull PrintWriter printWriter, @NotNull String[] strArr) {
            RoutingSessionInfo routingSessionInfo;
            List list;
            MediaController.PlaybackInfo playbackInfo;
            Intrinsics.checkNotNullParameter(fileDescriptor, "fd");
            Intrinsics.checkNotNullParameter(printWriter, "pw");
            Intrinsics.checkNotNullParameter(strArr, "args");
            MediaController mediaController = this.controller;
            Integer num = null;
            if (mediaController == null) {
                routingSessionInfo = null;
            } else {
                routingSessionInfo = this.this$0.mr2manager.getRoutingSessionForMediaController(mediaController);
            }
            if (routingSessionInfo == null) {
                list = null;
            } else {
                list = this.this$0.mr2manager.getSelectedRoutes(routingSessionInfo);
            }
            MediaDevice mediaDevice = this.current;
            printWriter.println(Intrinsics.stringPlus("    current device is ", mediaDevice == null ? null : mediaDevice.getName()));
            MediaController controller2 = getController();
            if (!(controller2 == null || (playbackInfo = controller2.getPlaybackInfo()) == null)) {
                num = Integer.valueOf(playbackInfo.getPlaybackType());
            }
            printWriter.println("    PlaybackType=" + num + " (1 for local, 2 for remote) cached=" + this.playbackType);
            printWriter.println(Intrinsics.stringPlus("    routingSession=", routingSessionInfo));
            printWriter.println(Intrinsics.stringPlus("    selectedRoutes=", list));
        }

        public void onAudioInfoChanged(@Nullable MediaController.PlaybackInfo playbackInfo) {
            int playbackType2 = playbackInfo == null ? 0 : playbackInfo.getPlaybackType();
            if (playbackType2 != this.playbackType) {
                this.playbackType = playbackType2;
                updateCurrent();
            }
        }

        public void onDeviceListUpdate(@Nullable List<? extends MediaDevice> list) {
            this.this$0.bgExecutor.execute(new MediaDeviceManager$Entry$onDeviceListUpdate$1(this));
        }

        public void onSelectedDeviceStateChanged(@NotNull MediaDevice mediaDevice, int i) {
            Intrinsics.checkNotNullParameter(mediaDevice, "device");
            this.this$0.bgExecutor.execute(new MediaDeviceManager$Entry$onSelectedDeviceStateChanged$1(this));
        }

        /* JADX WARNING: type inference failed for: r2v3, types: [kotlin.Unit] */
        /* access modifiers changed from: private */
        /* JADX WARNING: Multi-variable type inference failed */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public final void updateCurrent() {
            /*
                r4 = this;
                com.android.settingslib.media.LocalMediaManager r0 = r4.localMediaManager
                com.android.settingslib.media.MediaDevice r0 = r0.getCurrentConnectedDevice()
                android.media.session.MediaController r1 = r4.controller
                r2 = 0
                if (r1 != 0) goto L_0x000c
                goto L_0x001e
            L_0x000c:
                com.android.systemui.media.MediaDeviceManager r3 = r4.this$0
                android.media.MediaRouter2Manager r3 = r3.mr2manager
                android.media.RoutingSessionInfo r1 = r3.getRoutingSessionForMediaController(r1)
                if (r1 == 0) goto L_0x0019
                r2 = r0
            L_0x0019:
                r4.setCurrent(r2)
                kotlin.Unit r2 = kotlin.Unit.INSTANCE
            L_0x001e:
                if (r2 != 0) goto L_0x0023
                r4.setCurrent(r0)
            L_0x0023:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.media.MediaDeviceManager.Entry.updateCurrent():void");
        }
    }
}
