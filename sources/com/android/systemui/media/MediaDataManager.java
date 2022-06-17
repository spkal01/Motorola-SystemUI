package com.android.systemui.media;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.smartspace.SmartspaceAction;
import android.app.smartspace.SmartspaceConfig;
import android.app.smartspace.SmartspaceManager;
import android.app.smartspace.SmartspaceSession;
import android.app.smartspace.SmartspaceTarget;
import android.content.Context;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ImageDecoder;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.media.MediaDescription;
import android.media.MediaMetadata;
import android.media.session.MediaController;
import android.media.session.MediaSession;
import android.media.session.PlaybackState;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.UserHandle;
import android.service.notification.StatusBarNotification;
import android.text.TextUtils;
import android.util.Log;
import androidx.appcompat.R$styleable;
import com.android.settingslib.Utils;
import com.android.systemui.Dumpable;
import com.android.systemui.R$drawable;
import com.android.systemui.R$string;
import com.android.systemui.broadcast.BroadcastDispatcher;
import com.android.systemui.dump.DumpManager;
import com.android.systemui.plugins.ActivityStarter;
import com.android.systemui.plugins.BcSmartspaceDataPlugin;
import com.android.systemui.statusbar.NotificationMediaManager;
import com.android.systemui.statusbar.notification.row.HybridGroupManager;
import com.android.systemui.tuner.TunerService;
import com.android.systemui.util.Assert;
import com.android.systemui.util.concurrency.DelayableExecutor;
import com.android.systemui.util.time.SystemClock;
import java.io.FileDescriptor;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import kotlin.Unit;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Ref$ObjectRef;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/* compiled from: MediaDataManager.kt */
public final class MediaDataManager implements Dumpable, BcSmartspaceDataPlugin.SmartspaceTargetListener {
    @NotNull
    public static final Companion Companion = new Companion((DefaultConstructorMarker) null);
    @NotNull
    public static final String EXTRAS_MEDIA_SOURCE_PACKAGE_NAME = "package_name";
    public static final int MAX_COMPACT_ACTIONS = 3;
    @NotNull
    public static final String SMARTSPACE_UI_SURFACE_LABEL = "media_data_manager";
    /* access modifiers changed from: private */
    @NotNull
    public final ActivityStarter activityStarter;
    /* access modifiers changed from: private */
    public boolean allowMediaRecommendations;
    @NotNull
    private final MediaDataManager$appChangeReceiver$1 appChangeReceiver;
    @NotNull
    private final Executor backgroundExecutor;
    /* access modifiers changed from: private */
    public final int bgColor;
    @NotNull
    private final BroadcastDispatcher broadcastDispatcher;
    /* access modifiers changed from: private */
    @NotNull
    public final Context context;
    @NotNull
    private final DelayableExecutor foregroundExecutor;
    @NotNull
    private final Set<Listener> internalListeners;
    /* access modifiers changed from: private */
    @NotNull
    public final MediaControllerFactory mediaControllerFactory;
    @NotNull
    private final MediaDataFilter mediaDataFilter;
    /* access modifiers changed from: private */
    @NotNull
    public final LinkedHashMap<String, MediaData> mediaEntries;
    @NotNull
    private SmartspaceMediaData smartspaceMediaData;
    /* access modifiers changed from: private */
    @NotNull
    public final SmartspaceMediaDataProvider smartspaceMediaDataProvider;
    @Nullable
    private SmartspaceSession smartspaceSession;
    @NotNull
    private final SystemClock systemClock;
    private final int themeText;
    @NotNull
    private final TunerService tunerService;
    private boolean useMediaResumption;
    private final boolean useQsMediaPlayer;

    public MediaDataManager(@NotNull Context context2, @NotNull Executor executor, @NotNull DelayableExecutor delayableExecutor, @NotNull MediaControllerFactory mediaControllerFactory2, @NotNull BroadcastDispatcher broadcastDispatcher2, @NotNull DumpManager dumpManager, @NotNull MediaTimeoutListener mediaTimeoutListener, @NotNull MediaResumeListener mediaResumeListener, @NotNull MediaSessionBasedFilter mediaSessionBasedFilter, @NotNull MediaDeviceManager mediaDeviceManager, @NotNull MediaDataCombineLatest mediaDataCombineLatest, @NotNull MediaDataFilter mediaDataFilter2, @NotNull ActivityStarter activityStarter2, @NotNull SmartspaceMediaDataProvider smartspaceMediaDataProvider2, boolean z, boolean z2, @NotNull SystemClock systemClock2, @NotNull TunerService tunerService2) {
        Context context3 = context2;
        Executor executor2 = executor;
        DelayableExecutor delayableExecutor2 = delayableExecutor;
        MediaControllerFactory mediaControllerFactory3 = mediaControllerFactory2;
        BroadcastDispatcher broadcastDispatcher3 = broadcastDispatcher2;
        DumpManager dumpManager2 = dumpManager;
        MediaTimeoutListener mediaTimeoutListener2 = mediaTimeoutListener;
        MediaResumeListener mediaResumeListener2 = mediaResumeListener;
        MediaDataCombineLatest mediaDataCombineLatest2 = mediaDataCombineLatest;
        MediaDataFilter mediaDataFilter3 = mediaDataFilter2;
        ActivityStarter activityStarter3 = activityStarter2;
        SmartspaceMediaDataProvider smartspaceMediaDataProvider3 = smartspaceMediaDataProvider2;
        SystemClock systemClock3 = systemClock2;
        TunerService tunerService3 = tunerService2;
        Intrinsics.checkNotNullParameter(context3, "context");
        Intrinsics.checkNotNullParameter(executor2, "backgroundExecutor");
        Intrinsics.checkNotNullParameter(delayableExecutor2, "foregroundExecutor");
        Intrinsics.checkNotNullParameter(mediaControllerFactory3, "mediaControllerFactory");
        Intrinsics.checkNotNullParameter(broadcastDispatcher3, "broadcastDispatcher");
        Intrinsics.checkNotNullParameter(dumpManager2, "dumpManager");
        Intrinsics.checkNotNullParameter(mediaTimeoutListener2, "mediaTimeoutListener");
        Intrinsics.checkNotNullParameter(mediaResumeListener2, "mediaResumeListener");
        Intrinsics.checkNotNullParameter(mediaSessionBasedFilter, "mediaSessionBasedFilter");
        Intrinsics.checkNotNullParameter(mediaDeviceManager, "mediaDeviceManager");
        Intrinsics.checkNotNullParameter(mediaDataCombineLatest2, "mediaDataCombineLatest");
        Intrinsics.checkNotNullParameter(mediaDataFilter3, "mediaDataFilter");
        Intrinsics.checkNotNullParameter(activityStarter3, "activityStarter");
        Intrinsics.checkNotNullParameter(smartspaceMediaDataProvider3, "smartspaceMediaDataProvider");
        Intrinsics.checkNotNullParameter(systemClock3, "systemClock");
        Intrinsics.checkNotNullParameter(tunerService3, "tunerService");
        this.context = context3;
        this.backgroundExecutor = executor2;
        this.foregroundExecutor = delayableExecutor2;
        this.mediaControllerFactory = mediaControllerFactory3;
        this.broadcastDispatcher = broadcastDispatcher3;
        this.mediaDataFilter = mediaDataFilter3;
        this.activityStarter = activityStarter3;
        this.smartspaceMediaDataProvider = smartspaceMediaDataProvider3;
        this.useMediaResumption = z;
        this.useQsMediaPlayer = z2;
        this.systemClock = systemClock3;
        this.tunerService = tunerService3;
        this.themeText = Utils.getColorAttr(context3, 16842806).getDefaultColor();
        this.bgColor = context3.getColor(17170502);
        this.internalListeners = new LinkedHashSet();
        this.mediaEntries = new LinkedHashMap<>();
        this.smartspaceMediaData = MediaDataManagerKt.getEMPTY_SMARTSPACE_MEDIA_DATA();
        this.allowMediaRecommendations = com.android.systemui.util.Utils.allowMediaRecommendations(context2);
        MediaDataManager$appChangeReceiver$1 mediaDataManager$appChangeReceiver$1 = new MediaDataManager$appChangeReceiver$1(this);
        this.appChangeReceiver = mediaDataManager$appChangeReceiver$1;
        dumpManager2.registerDumpable("MediaDataManager", this);
        addInternalListener(mediaTimeoutListener2);
        addInternalListener(mediaResumeListener2);
        MediaSessionBasedFilter mediaSessionBasedFilter2 = mediaSessionBasedFilter;
        addInternalListener(mediaSessionBasedFilter2);
        mediaSessionBasedFilter.addListener(mediaDeviceManager);
        mediaSessionBasedFilter2.addListener(mediaDataCombineLatest2);
        mediaDeviceManager.addListener(mediaDataCombineLatest);
        mediaDataCombineLatest.addListener(mediaDataFilter2);
        mediaTimeoutListener2.setTimeoutCallback(new Function2<String, Boolean, Unit>(this) {
            final /* synthetic */ MediaDataManager this$0;

            {
                this.this$0 = r1;
            }

            public /* bridge */ /* synthetic */ Object invoke(Object obj, Object obj2) {
                invoke((String) obj, ((Boolean) obj2).booleanValue());
                return Unit.INSTANCE;
            }

            public final void invoke(@NotNull String str, boolean z) {
                Intrinsics.checkNotNullParameter(str, "token");
                MediaDataManager.m39x7e6a6a7c(this.this$0, str, z, false, 4, (Object) null);
            }
        });
        mediaResumeListener2.setManager(this);
        mediaDataFilter3.mo14190x3600a367(this);
        broadcastDispatcher3.registerReceiver(mediaDataManager$appChangeReceiver$1, new IntentFilter("android.intent.action.PACKAGES_SUSPENDED"), (Executor) null, UserHandle.ALL);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.PACKAGE_REMOVED");
        intentFilter.addAction("android.intent.action.PACKAGE_RESTARTED");
        intentFilter.addDataScheme("package");
        context3.registerReceiver(mediaDataManager$appChangeReceiver$1, intentFilter);
        smartspaceMediaDataProvider3.registerListener(this);
        Object systemService = context3.getSystemService(SmartspaceManager.class);
        Intrinsics.checkNotNullExpressionValue(systemService, "context.getSystemService(SmartspaceManager::class.java)");
        SmartspaceSession createSmartspaceSession = ((SmartspaceManager) systemService).createSmartspaceSession(new SmartspaceConfig.Builder(context3, SMARTSPACE_UI_SURFACE_LABEL).build());
        this.smartspaceSession = createSmartspaceSession;
        if (createSmartspaceSession != null) {
            createSmartspaceSession.addOnTargetsAvailableListener(Executors.newCachedThreadPool(), new MediaDataManager$2$1(this));
        }
        SmartspaceSession smartspaceSession2 = this.smartspaceSession;
        if (smartspaceSession2 != null) {
            smartspaceSession2.requestSmartspaceUpdate();
        }
        tunerService3.addTunable(new TunerService.Tunable(this) {
            final /* synthetic */ MediaDataManager this$0;

            {
                this.this$0 = r1;
            }

            public void onTuningChanged(@Nullable String str, @Nullable String str2) {
                MediaDataManager mediaDataManager = this.this$0;
                mediaDataManager.allowMediaRecommendations = com.android.systemui.util.Utils.allowMediaRecommendations(mediaDataManager.context);
                if (!this.this$0.allowMediaRecommendations) {
                    MediaDataManager mediaDataManager2 = this.this$0;
                    mediaDataManager2.dismissSmartspaceRecommendation(mediaDataManager2.getSmartspaceMediaData().getTargetId(), 0);
                }
            }
        }, "qs_media_recommend");
    }

    /* compiled from: MediaDataManager.kt */
    public static final class Companion {
        public /* synthetic */ Companion(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }

        private Companion() {
        }
    }

    @NotNull
    public final SmartspaceMediaData getSmartspaceMediaData() {
        return this.smartspaceMediaData;
    }

    /* JADX INFO: this call moved to the top of the method (can break code semantics) */
    public MediaDataManager(@NotNull Context context2, @NotNull Executor executor, @NotNull DelayableExecutor delayableExecutor, @NotNull MediaControllerFactory mediaControllerFactory2, @NotNull DumpManager dumpManager, @NotNull BroadcastDispatcher broadcastDispatcher2, @NotNull MediaTimeoutListener mediaTimeoutListener, @NotNull MediaResumeListener mediaResumeListener, @NotNull MediaSessionBasedFilter mediaSessionBasedFilter, @NotNull MediaDeviceManager mediaDeviceManager, @NotNull MediaDataCombineLatest mediaDataCombineLatest, @NotNull MediaDataFilter mediaDataFilter2, @NotNull ActivityStarter activityStarter2, @NotNull SmartspaceMediaDataProvider smartspaceMediaDataProvider2, @NotNull SystemClock systemClock2, @NotNull TunerService tunerService2) {
        this(context2, executor, delayableExecutor, mediaControllerFactory2, broadcastDispatcher2, dumpManager, mediaTimeoutListener, mediaResumeListener, mediaSessionBasedFilter, mediaDeviceManager, mediaDataCombineLatest, mediaDataFilter2, activityStarter2, smartspaceMediaDataProvider2, com.android.systemui.util.Utils.useMediaResumption(context2), com.android.systemui.util.Utils.useQsMediaPlayer(context2), systemClock2, tunerService2);
        Intrinsics.checkNotNullParameter(context2, "context");
        Intrinsics.checkNotNullParameter(executor, "backgroundExecutor");
        Intrinsics.checkNotNullParameter(delayableExecutor, "foregroundExecutor");
        Intrinsics.checkNotNullParameter(mediaControllerFactory2, "mediaControllerFactory");
        Intrinsics.checkNotNullParameter(dumpManager, "dumpManager");
        Intrinsics.checkNotNullParameter(broadcastDispatcher2, "broadcastDispatcher");
        Intrinsics.checkNotNullParameter(mediaTimeoutListener, "mediaTimeoutListener");
        Intrinsics.checkNotNullParameter(mediaResumeListener, "mediaResumeListener");
        Intrinsics.checkNotNullParameter(mediaSessionBasedFilter, "mediaSessionBasedFilter");
        Intrinsics.checkNotNullParameter(mediaDeviceManager, "mediaDeviceManager");
        Intrinsics.checkNotNullParameter(mediaDataCombineLatest, "mediaDataCombineLatest");
        Intrinsics.checkNotNullParameter(mediaDataFilter2, "mediaDataFilter");
        Intrinsics.checkNotNullParameter(activityStarter2, "activityStarter");
        Intrinsics.checkNotNullParameter(smartspaceMediaDataProvider2, "smartspaceMediaDataProvider");
        Intrinsics.checkNotNullParameter(systemClock2, "clock");
        Intrinsics.checkNotNullParameter(tunerService2, "tunerService");
    }

    public final void onNotificationAdded(@NotNull String str, @NotNull StatusBarNotification statusBarNotification) {
        String str2 = str;
        StatusBarNotification statusBarNotification2 = statusBarNotification;
        Intrinsics.checkNotNullParameter(str2, "key");
        Intrinsics.checkNotNullParameter(statusBarNotification2, "sbn");
        if (!this.useQsMediaPlayer || !MediaDataManagerKt.isMediaNotification(statusBarNotification)) {
            onNotificationRemoved(str);
            return;
        }
        Assert.isMainThread();
        String packageName = statusBarNotification.getPackageName();
        Intrinsics.checkNotNullExpressionValue(packageName, "sbn.packageName");
        String findExistingEntry = findExistingEntry(str2, packageName);
        if (findExistingEntry == null) {
            MediaData access$getLOADING$p = MediaDataManagerKt.LOADING;
            String packageName2 = statusBarNotification.getPackageName();
            Intrinsics.checkNotNullExpressionValue(packageName2, "sbn.packageName");
            this.mediaEntries.put(str2, MediaData.copy$default(access$getLOADING$p, 0, false, 0, (String) null, (Icon) null, (CharSequence) null, (CharSequence) null, (Icon) null, (List) null, (List) null, packageName2, (MediaSession.Token) null, (PendingIntent) null, (MediaDeviceData) null, false, (Runnable) null, false, false, (String) null, false, (Boolean) null, false, 0, 8387583, (Object) null));
        } else if (!Intrinsics.areEqual((Object) findExistingEntry, (Object) str2)) {
            MediaData mediaData = (MediaData) this.mediaEntries.remove(findExistingEntry);
            Intrinsics.checkNotNull(mediaData);
            this.mediaEntries.put(str2, mediaData);
        }
        loadMediaData(str2, statusBarNotification2, findExistingEntry);
    }

    /* access modifiers changed from: private */
    public final void removeAllForPackage(String str) {
        Assert.isMainThread();
        LinkedHashMap<String, MediaData> linkedHashMap = this.mediaEntries;
        LinkedHashMap linkedHashMap2 = new LinkedHashMap();
        for (Map.Entry next : linkedHashMap.entrySet()) {
            if (Intrinsics.areEqual((Object) ((MediaData) next.getValue()).getPackageName(), (Object) str)) {
                linkedHashMap2.put(next.getKey(), next.getValue());
            }
        }
        for (Map.Entry key : linkedHashMap2.entrySet()) {
            removeEntry((String) key.getKey());
        }
    }

    public final void setResumeAction(@NotNull String str, @Nullable Runnable runnable) {
        Intrinsics.checkNotNullParameter(str, "key");
        MediaData mediaData = this.mediaEntries.get(str);
        if (mediaData != null) {
            mediaData.setResumeAction(runnable);
            mediaData.setHasCheckedForResume(true);
        }
    }

    public final void addResumptionControls(int i, @NotNull MediaDescription mediaDescription, @NotNull Runnable runnable, @NotNull MediaSession.Token token, @NotNull String str, @NotNull PendingIntent pendingIntent, @NotNull String str2) {
        String str3 = str2;
        Intrinsics.checkNotNullParameter(mediaDescription, "desc");
        Intrinsics.checkNotNullParameter(runnable, "action");
        Intrinsics.checkNotNullParameter(token, "token");
        Intrinsics.checkNotNullParameter(str, "appName");
        Intrinsics.checkNotNullParameter(pendingIntent, "appIntent");
        Intrinsics.checkNotNullParameter(str3, "packageName");
        if (!this.mediaEntries.containsKey(str3)) {
            this.mediaEntries.put(str3, MediaData.copy$default(MediaDataManagerKt.LOADING, 0, false, 0, (String) null, (Icon) null, (CharSequence) null, (CharSequence) null, (Icon) null, (List) null, (List) null, str2, (MediaSession.Token) null, (PendingIntent) null, (MediaDeviceData) null, false, runnable, false, false, (String) null, true, (Boolean) null, false, 0, 7830527, (Object) null));
        }
        this.backgroundExecutor.execute(new MediaDataManager$addResumptionControls$1(this, i, mediaDescription, runnable, token, str, pendingIntent, str2));
    }

    private final String findExistingEntry(String str, String str2) {
        if (this.mediaEntries.containsKey(str)) {
            return str;
        }
        if (this.mediaEntries.containsKey(str2)) {
            return str2;
        }
        return null;
    }

    private final void loadMediaData(String str, StatusBarNotification statusBarNotification, String str2) {
        this.backgroundExecutor.execute(new MediaDataManager$loadMediaData$1(this, str, statusBarNotification, str2));
    }

    public final void addListener(@NotNull Listener listener) {
        Intrinsics.checkNotNullParameter(listener, "listener");
        this.mediaDataFilter.addListener(listener);
    }

    public final void removeListener(@NotNull Listener listener) {
        Intrinsics.checkNotNullParameter(listener, "listener");
        this.mediaDataFilter.removeListener(listener);
    }

    private final boolean addInternalListener(Listener listener) {
        return this.internalListeners.add(listener);
    }

    private final void notifyMediaDataLoaded(String str, String str2, MediaData mediaData) {
        for (Listener onMediaDataLoaded$default : this.internalListeners) {
            Listener.DefaultImpls.onMediaDataLoaded$default(onMediaDataLoaded$default, str, str2, mediaData, false, false, 24, (Object) null);
        }
    }

    private final void notifySmartspaceMediaDataLoaded(String str, SmartspaceMediaData smartspaceMediaData2) {
        for (Listener onSmartspaceMediaDataLoaded$default : this.internalListeners) {
            Listener.DefaultImpls.onSmartspaceMediaDataLoaded$default(onSmartspaceMediaDataLoaded$default, str, smartspaceMediaData2, false, 4, (Object) null);
        }
    }

    private final void notifyMediaDataRemoved(String str) {
        for (Listener onMediaDataRemoved : this.internalListeners) {
            onMediaDataRemoved.onMediaDataRemoved(str);
        }
    }

    /* access modifiers changed from: private */
    public final void notifySmartspaceMediaDataRemoved(String str, boolean z) {
        for (Listener onSmartspaceMediaDataRemoved : this.internalListeners) {
            onSmartspaceMediaDataRemoved.onSmartspaceMediaDataRemoved(str, z);
        }
    }

    /* renamed from: setTimedOut$frameworks__base__packages__SystemUI__android_common__SystemUI_core$default */
    public static /* synthetic */ void m39x7e6a6a7c(MediaDataManager mediaDataManager, String str, boolean z, boolean z2, int i, Object obj) {
        if ((i & 4) != 0) {
            z2 = false;
        }
        mediaDataManager.mo14208x855293df(str, z, z2);
    }

    /* renamed from: setTimedOut$frameworks__base__packages__SystemUI__android_common__SystemUI_core */
    public final void mo14208x855293df(@NotNull String str, boolean z, boolean z2) {
        Intrinsics.checkNotNullParameter(str, "token");
        MediaData mediaData = this.mediaEntries.get(str);
        if (mediaData != null) {
            if (mediaData.getActive() != (!z) || z2) {
                mediaData.setActive(!z);
                Log.d("MediaDataManager", "Updating " + str + " timedOut: " + z);
                onMediaDataLoaded(str, str, mediaData);
            }
        }
    }

    /* access modifiers changed from: private */
    public final void removeEntry(String str) {
        this.mediaEntries.remove(str);
        notifyMediaDataRemoved(str);
    }

    public final boolean dismissMediaData(@NotNull String str, long j) {
        Intrinsics.checkNotNullParameter(str, "key");
        boolean z = this.mediaEntries.get(str) != null;
        this.backgroundExecutor.execute(new MediaDataManager$dismissMediaData$1(this, str));
        this.foregroundExecutor.executeDelayed(new MediaDataManager$dismissMediaData$2(this, str), j);
        return z;
    }

    public final void dismissSmartspaceRecommendation(@NotNull String str, long j) {
        Intrinsics.checkNotNullParameter(str, "key");
        if (Intrinsics.areEqual((Object) this.smartspaceMediaData.getTargetId(), (Object) str)) {
            Log.d("MediaDataManager", "Dismissing Smartspace media target");
            if (this.smartspaceMediaData.isActive()) {
                this.smartspaceMediaData = SmartspaceMediaData.copy$default(MediaDataManagerKt.getEMPTY_SMARTSPACE_MEDIA_DATA(), this.smartspaceMediaData.getTargetId(), false, false, (String) null, (SmartspaceAction) null, (List) null, 0, R$styleable.AppCompatTheme_windowNoTitle, (Object) null);
            }
            this.foregroundExecutor.executeDelayed(new MediaDataManager$dismissSmartspaceRecommendation$1(this), j);
        }
    }

    /* access modifiers changed from: private */
    public final void loadMediaDataInBgForResumption(int i, MediaDescription mediaDescription, Runnable runnable, MediaSession.Token token, String str, PendingIntent pendingIntent, String str2) {
        if (TextUtils.isEmpty(mediaDescription.getTitle())) {
            Log.e("MediaDataManager", "Description incomplete");
            this.mediaEntries.remove(str2);
            return;
        }
        String str3 = str2;
        Log.d("MediaDataManager", "adding track for " + i + " from browser: " + mediaDescription);
        Bitmap iconBitmap = mediaDescription.getIconBitmap();
        if (iconBitmap == null && mediaDescription.getIconUri() != null) {
            Uri iconUri = mediaDescription.getIconUri();
            Intrinsics.checkNotNull(iconUri);
            iconBitmap = loadBitmapFromUri(iconUri);
        }
        this.foregroundExecutor.execute(new MediaDataManager$loadMediaDataInBgForResumption$1(this, str2, i, str, mediaDescription, iconBitmap != null ? Icon.createWithBitmap(iconBitmap) : null, getResumeMediaAction(runnable), token, pendingIntent, runnable, this.systemClock.elapsedRealtime()));
    }

    /* access modifiers changed from: private */
    public final void loadMediaDataInBg(String str, StatusBarNotification statusBarNotification, String str2) {
        Bitmap bitmap;
        Icon icon;
        T t;
        int i;
        Notification.Action[] actionArr;
        Icon icon2;
        T t2;
        MediaSession.Token token = (MediaSession.Token) statusBarNotification.getNotification().extras.getParcelable("android.mediaSession");
        if (token != null) {
            MediaController create = this.mediaControllerFactory.create(token);
            MediaMetadata metadata = create.getMetadata();
            Notification notification = statusBarNotification.getNotification();
            Intrinsics.checkNotNullExpressionValue(notification, "sbn.notification");
            if (metadata == null) {
                bitmap = null;
            } else {
                bitmap = metadata.getBitmap("android.media.metadata.ART");
            }
            if (bitmap == null) {
                if (metadata == null) {
                    bitmap = null;
                } else {
                    bitmap = metadata.getBitmap("android.media.metadata.ALBUM_ART");
                }
            }
            if (bitmap == null && metadata != null) {
                bitmap = loadBitmapFromUri(metadata);
            }
            if (bitmap == null) {
                icon = notification.getLargeIcon();
            } else {
                icon = Icon.createWithBitmap(bitmap);
            }
            Icon icon3 = icon;
            int i2 = 0;
            if (icon3 != null && bitmap == null) {
                if (icon3.getType() == 1 || icon3.getType() == 5) {
                    icon3.getBitmap();
                } else {
                    Drawable loadDrawable = icon3.loadDrawable(this.context);
                    Intrinsics.checkNotNullExpressionValue(loadDrawable, "artWorkIcon.loadDrawable(context)");
                    Canvas canvas = new Canvas(Bitmap.createBitmap(loadDrawable.getIntrinsicWidth(), loadDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888));
                    loadDrawable.setBounds(0, 0, loadDrawable.getIntrinsicWidth(), loadDrawable.getIntrinsicHeight());
                    loadDrawable.draw(canvas);
                }
            }
            String loadHeaderAppName = Notification.Builder.recoverBuilder(this.context, notification).loadHeaderAppName();
            Icon smallIcon = statusBarNotification.getNotification().getSmallIcon();
            Ref$ObjectRef ref$ObjectRef = new Ref$ObjectRef();
            T string = metadata == null ? null : metadata.getString("android.media.metadata.DISPLAY_TITLE");
            ref$ObjectRef.element = string;
            if (string == null) {
                if (metadata == null) {
                    t2 = null;
                } else {
                    t2 = metadata.getString("android.media.metadata.TITLE");
                }
                ref$ObjectRef.element = t2;
            }
            if (ref$ObjectRef.element == null) {
                ref$ObjectRef.element = HybridGroupManager.resolveTitle(notification);
            }
            Ref$ObjectRef ref$ObjectRef2 = new Ref$ObjectRef();
            T string2 = metadata == null ? null : metadata.getString("android.media.metadata.ARTIST");
            ref$ObjectRef2.element = string2;
            if (string2 == null) {
                ref$ObjectRef2.element = HybridGroupManager.resolveText(notification);
            }
            ArrayList arrayList = new ArrayList();
            Notification.Action[] actionArr2 = notification.actions;
            Ref$ObjectRef ref$ObjectRef3 = new Ref$ObjectRef();
            int[] intArray = notification.extras.getIntArray("android.compactActions");
            if (intArray == null) {
                t = null;
            } else {
                t = ArraysKt___ArraysKt.toMutableList(intArray);
            }
            if (t == null) {
                t = new ArrayList();
            }
            ref$ObjectRef3.element = t;
            int size = ((List) t).size();
            int i3 = MAX_COMPACT_ACTIONS;
            if (size > i3) {
                Log.e("MediaDataManager", "Too many compact actions for " + str + ", limiting to first " + i3);
                i2 = 0;
                ref$ObjectRef3.element = ((List) ref$ObjectRef3.element).subList(0, i3);
            }
            if (actionArr2 != null) {
                int length = actionArr2.length;
                int i4 = i2;
                while (i4 < length) {
                    Notification.Action action = actionArr2[i4];
                    int i5 = i4 + 1;
                    if (action.getIcon() == null) {
                        actionArr = actionArr2;
                        StringBuilder sb = new StringBuilder();
                        i = length;
                        sb.append("No icon for action ");
                        sb.append(i4);
                        sb.append(' ');
                        sb.append(action.title);
                        Log.i("MediaDataManager", sb.toString());
                        ((List) ref$ObjectRef3.element).remove(Integer.valueOf(i4));
                    } else {
                        actionArr = actionArr2;
                        i = length;
                        MediaDataManager$loadMediaDataInBg$runnable$1 mediaDataManager$loadMediaDataInBg$runnable$1 = action.actionIntent != null ? new MediaDataManager$loadMediaDataInBg$runnable$1(action, this) : null;
                        Icon icon4 = action.getIcon();
                        Integer valueOf = icon4 == null ? null : Integer.valueOf(icon4.getType());
                        if (valueOf != null && valueOf.intValue() == 2) {
                            String packageName = statusBarNotification.getPackageName();
                            Icon icon5 = action.getIcon();
                            Intrinsics.checkNotNull(icon5);
                            icon2 = Icon.createWithResource(packageName, icon5.getResId());
                        } else {
                            icon2 = action.getIcon();
                        }
                        arrayList.add(new MediaAction(icon2.setTint(this.themeText), mediaDataManager$loadMediaDataInBg$runnable$1, action.title));
                    }
                    i4 = i5;
                    actionArr2 = actionArr;
                    length = i;
                }
            }
            MediaController.PlaybackInfo playbackInfo = create.getPlaybackInfo();
            Integer valueOf2 = playbackInfo == null ? null : Integer.valueOf(playbackInfo.getPlaybackType());
            boolean z = valueOf2 != null && valueOf2.intValue() == 1;
            PlaybackState playbackState = create.getPlaybackState();
            Boolean valueOf3 = playbackState == null ? null : Boolean.valueOf(NotificationMediaManager.isPlayingState(playbackState.getState()));
            long elapsedRealtime = this.systemClock.elapsedRealtime();
            MediaDataManager$loadMediaDataInBg$1 mediaDataManager$loadMediaDataInBg$1 = r0;
            Ref$ObjectRef ref$ObjectRef4 = ref$ObjectRef2;
            DelayableExecutor delayableExecutor = this.foregroundExecutor;
            MediaDataManager$loadMediaDataInBg$1 mediaDataManager$loadMediaDataInBg$12 = new MediaDataManager$loadMediaDataInBg$1(this, str, str2, statusBarNotification, loadHeaderAppName, smallIcon, ref$ObjectRef4, ref$ObjectRef, icon3, arrayList, ref$ObjectRef3, token, notification, z, valueOf3, elapsedRealtime);
            delayableExecutor.execute(mediaDataManager$loadMediaDataInBg$1);
        }
    }

    private final Bitmap loadBitmapFromUri(MediaMetadata mediaMetadata) {
        String[] access$getART_URIS$p = MediaDataManagerKt.ART_URIS;
        int length = access$getART_URIS$p.length;
        int i = 0;
        while (i < length) {
            String str = access$getART_URIS$p[i];
            i++;
            String string = mediaMetadata.getString(str);
            if (!TextUtils.isEmpty(string)) {
                Uri parse = Uri.parse(string);
                Intrinsics.checkNotNullExpressionValue(parse, "parse(uriString)");
                Bitmap loadBitmapFromUri = loadBitmapFromUri(parse);
                if (loadBitmapFromUri != null) {
                    Log.d("MediaDataManager", Intrinsics.stringPlus("loaded art from ", str));
                    return loadBitmapFromUri;
                }
            }
        }
        return null;
    }

    /* access modifiers changed from: private */
    public final boolean sendPendingIntent(PendingIntent pendingIntent) {
        try {
            pendingIntent.send();
            return true;
        } catch (PendingIntent.CanceledException e) {
            Log.d("MediaDataManager", "Intent canceled", e);
            return false;
        }
    }

    private final Bitmap loadBitmapFromUri(Uri uri) {
        if (uri.getScheme() == null) {
            return null;
        }
        if (!uri.getScheme().equals("content") && !uri.getScheme().equals("android.resource") && !uri.getScheme().equals("file")) {
            return null;
        }
        try {
            return ImageDecoder.decodeBitmap(ImageDecoder.createSource(this.context.getContentResolver(), uri), MediaDataManager$loadBitmapFromUri$1.INSTANCE);
        } catch (IOException e) {
            Log.e("MediaDataManager", "Unable to load bitmap", e);
            return null;
        } catch (RuntimeException e2) {
            Log.e("MediaDataManager", "Unable to load bitmap", e2);
            return null;
        }
    }

    private final MediaAction getResumeMediaAction(Runnable runnable) {
        return new MediaAction(Icon.createWithResource(this.context, R$drawable.lb_ic_play).setTint(this.themeText), runnable, this.context.getString(R$string.controls_media_resume));
    }

    public final void onMediaDataLoaded(@NotNull String str, @Nullable String str2, @NotNull MediaData mediaData) {
        Intrinsics.checkNotNullParameter(str, "key");
        Intrinsics.checkNotNullParameter(mediaData, "data");
        Assert.isMainThread();
        if (this.mediaEntries.containsKey(str)) {
            this.mediaEntries.put(str, mediaData);
            notifyMediaDataLoaded(str, str2, mediaData);
        }
    }

    public void onSmartspaceTargetsUpdated(@NotNull List<? extends Parcelable> list) {
        Intrinsics.checkNotNullParameter(list, "targets");
        if (!this.allowMediaRecommendations) {
            Log.d("MediaDataManager", "Smartspace recommendation is disabled in Settings.");
            return;
        }
        ArrayList arrayList = new ArrayList();
        for (T next : list) {
            if (next instanceof SmartspaceTarget) {
                arrayList.add(next);
            }
        }
        int size = arrayList.size();
        if (size != 0) {
            if (size != 1) {
                Log.wtf("MediaDataManager", "More than 1 Smartspace Media Update. Resetting the status...");
                notifySmartspaceMediaDataRemoved(this.smartspaceMediaData.getTargetId(), false);
                this.smartspaceMediaData = MediaDataManagerKt.getEMPTY_SMARTSPACE_MEDIA_DATA();
                return;
            }
            SmartspaceTarget smartspaceTarget = (SmartspaceTarget) arrayList.get(0);
            if (!Intrinsics.areEqual((Object) this.smartspaceMediaData.getTargetId(), (Object) smartspaceTarget.getSmartspaceTargetId())) {
                Log.d("MediaDataManager", "Forwarding Smartspace media update.");
                SmartspaceMediaData smartspaceMediaData2 = toSmartspaceMediaData(smartspaceTarget, true);
                this.smartspaceMediaData = smartspaceMediaData2;
                notifySmartspaceMediaDataLoaded(smartspaceMediaData2.getTargetId(), this.smartspaceMediaData);
            }
        } else if (this.smartspaceMediaData.isActive()) {
            Log.d("MediaDataManager", "Set Smartspace media to be inactive for the data update");
            SmartspaceMediaData copy$default = SmartspaceMediaData.copy$default(MediaDataManagerKt.getEMPTY_SMARTSPACE_MEDIA_DATA(), this.smartspaceMediaData.getTargetId(), false, false, (String) null, (SmartspaceAction) null, (List) null, 0, R$styleable.AppCompatTheme_windowNoTitle, (Object) null);
            this.smartspaceMediaData = copy$default;
            notifySmartspaceMediaDataRemoved(copy$default.getTargetId(), false);
        }
    }

    public final void onNotificationRemoved(@NotNull String str) {
        String str2 = str;
        Intrinsics.checkNotNullParameter(str2, "key");
        Assert.isMainThread();
        MediaData mediaData = (MediaData) this.mediaEntries.remove(str2);
        if (this.useMediaResumption) {
            Boolean bool = null;
            if ((mediaData == null ? null : mediaData.getResumeAction()) != null) {
                if (mediaData != null) {
                    bool = Boolean.valueOf(mediaData.isLocalSession());
                }
                if (bool.booleanValue()) {
                    Log.d("MediaDataManager", "Not removing " + str2 + " because resumable");
                    Runnable resumeAction = mediaData.getResumeAction();
                    Intrinsics.checkNotNull(resumeAction);
                    boolean z = false;
                    MediaData copy$default = MediaData.copy$default(mediaData, 0, false, 0, (String) null, (Icon) null, (CharSequence) null, (CharSequence) null, (Icon) null, CollectionsKt__CollectionsJVMKt.listOf(getResumeMediaAction(resumeAction)), CollectionsKt__CollectionsJVMKt.listOf(0), (String) null, (MediaSession.Token) null, (PendingIntent) null, (MediaDeviceData) null, false, (Runnable) null, false, true, (String) null, false, (Boolean) null, true, 0, 6141183, (Object) null);
                    String packageName = mediaData.getPackageName();
                    if (this.mediaEntries.put(packageName, copy$default) == null) {
                        z = true;
                    }
                    if (z) {
                        notifyMediaDataLoaded(packageName, str2, copy$default);
                        return;
                    }
                    notifyMediaDataRemoved(str);
                    notifyMediaDataLoaded(packageName, packageName, copy$default);
                    return;
                }
            }
        }
        if (mediaData != null) {
            notifyMediaDataRemoved(str);
        }
    }

    public final void setMediaResumptionEnabled(boolean z) {
        if (this.useMediaResumption != z) {
            this.useMediaResumption = z;
            if (!z) {
                LinkedHashMap<String, MediaData> linkedHashMap = this.mediaEntries;
                LinkedHashMap linkedHashMap2 = new LinkedHashMap();
                for (Map.Entry next : linkedHashMap.entrySet()) {
                    if (!((MediaData) next.getValue()).getActive()) {
                        linkedHashMap2.put(next.getKey(), next.getValue());
                    }
                }
                for (Map.Entry entry : linkedHashMap2.entrySet()) {
                    this.mediaEntries.remove(entry.getKey());
                    notifyMediaDataRemoved((String) entry.getKey());
                }
            }
        }
    }

    public final void onSwipeToDismiss() {
        this.mediaDataFilter.onSwipeToDismiss();
    }

    public final boolean hasActiveMedia() {
        return this.mediaDataFilter.hasActiveMedia();
    }

    public final boolean hasAnyMedia() {
        return this.mediaDataFilter.hasAnyMedia();
    }

    /* compiled from: MediaDataManager.kt */
    public interface Listener {
        void onMediaDataLoaded(@NotNull String str, @Nullable String str2, @NotNull MediaData mediaData, boolean z, boolean z2);

        void onMediaDataRemoved(@NotNull String str);

        void onSmartspaceMediaDataLoaded(@NotNull String str, @NotNull SmartspaceMediaData smartspaceMediaData, boolean z);

        void onSmartspaceMediaDataRemoved(@NotNull String str, boolean z);

        /* compiled from: MediaDataManager.kt */
        public static final class DefaultImpls {
            public static void onMediaDataRemoved(@NotNull Listener listener, @NotNull String str) {
                Intrinsics.checkNotNullParameter(listener, "this");
                Intrinsics.checkNotNullParameter(str, "key");
            }

            public static void onSmartspaceMediaDataLoaded(@NotNull Listener listener, @NotNull String str, @NotNull SmartspaceMediaData smartspaceMediaData, boolean z) {
                Intrinsics.checkNotNullParameter(listener, "this");
                Intrinsics.checkNotNullParameter(str, "key");
                Intrinsics.checkNotNullParameter(smartspaceMediaData, "data");
            }

            public static void onSmartspaceMediaDataRemoved(@NotNull Listener listener, @NotNull String str, boolean z) {
                Intrinsics.checkNotNullParameter(listener, "this");
                Intrinsics.checkNotNullParameter(str, "key");
            }

            public static /* synthetic */ void onMediaDataLoaded$default(Listener listener, String str, String str2, MediaData mediaData, boolean z, boolean z2, int i, Object obj) {
                if (obj == null) {
                    if ((i & 8) != 0) {
                        z = true;
                    }
                    boolean z3 = z;
                    if ((i & 16) != 0) {
                        z2 = false;
                    }
                    listener.onMediaDataLoaded(str, str2, mediaData, z3, z2);
                    return;
                }
                throw new UnsupportedOperationException("Super calls with default arguments not supported in this target, function: onMediaDataLoaded");
            }

            public static /* synthetic */ void onSmartspaceMediaDataLoaded$default(Listener listener, String str, SmartspaceMediaData smartspaceMediaData, boolean z, int i, Object obj) {
                if (obj == null) {
                    if ((i & 4) != 0) {
                        z = false;
                    }
                    listener.onSmartspaceMediaDataLoaded(str, smartspaceMediaData, z);
                    return;
                }
                throw new UnsupportedOperationException("Super calls with default arguments not supported in this target, function: onSmartspaceMediaDataLoaded");
            }
        }
    }

    private final SmartspaceMediaData toSmartspaceMediaData(SmartspaceTarget smartspaceTarget, boolean z) {
        String packageName = packageName(smartspaceTarget);
        if (packageName == null) {
            SmartspaceMediaData empty_smartspace_media_data = MediaDataManagerKt.getEMPTY_SMARTSPACE_MEDIA_DATA();
            String smartspaceTargetId = smartspaceTarget.getSmartspaceTargetId();
            Intrinsics.checkNotNullExpressionValue(smartspaceTargetId, "target.smartspaceTargetId");
            return SmartspaceMediaData.copy$default(empty_smartspace_media_data, smartspaceTargetId, z, false, (String) null, (SmartspaceAction) null, (List) null, 0, R$styleable.AppCompatTheme_windowMinWidthMajor, (Object) null);
        }
        String smartspaceTargetId2 = smartspaceTarget.getSmartspaceTargetId();
        Intrinsics.checkNotNullExpressionValue(smartspaceTargetId2, "target.smartspaceTargetId");
        SmartspaceAction baseAction = smartspaceTarget.getBaseAction();
        List iconGrid = smartspaceTarget.getIconGrid();
        Intrinsics.checkNotNullExpressionValue(iconGrid, "target.iconGrid");
        return new SmartspaceMediaData(smartspaceTargetId2, z, true, packageName, baseAction, iconGrid, 0);
    }

    private final String packageName(SmartspaceTarget smartspaceTarget) {
        String string;
        List<SmartspaceAction> iconGrid = smartspaceTarget.getIconGrid();
        if (iconGrid == null || iconGrid.isEmpty()) {
            Log.w("MediaDataManager", "Empty or null media recommendation list.");
            return null;
        }
        for (SmartspaceAction extras : iconGrid) {
            Bundle extras2 = extras.getExtras();
            if (extras2 != null && (string = extras2.getString(EXTRAS_MEDIA_SOURCE_PACKAGE_NAME)) != null) {
                return string;
            }
        }
        Log.w("MediaDataManager", "No valid package name is provided.");
        return null;
    }

    public void dump(@NotNull FileDescriptor fileDescriptor, @NotNull PrintWriter printWriter, @NotNull String[] strArr) {
        Intrinsics.checkNotNullParameter(fileDescriptor, "fd");
        Intrinsics.checkNotNullParameter(printWriter, "pw");
        Intrinsics.checkNotNullParameter(strArr, "args");
        printWriter.println(Intrinsics.stringPlus("internalListeners: ", this.internalListeners));
        printWriter.println(Intrinsics.stringPlus("externalListeners: ", this.mediaDataFilter.mo14182xef59304f()));
        printWriter.println(Intrinsics.stringPlus("mediaEntries: ", this.mediaEntries));
        printWriter.println(Intrinsics.stringPlus("useMediaResumption: ", Boolean.valueOf(this.useMediaResumption)));
    }
}
