package com.android.systemui.media;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ResolveInfo;
import android.os.UserHandle;
import android.util.Log;
import com.android.internal.annotations.VisibleForTesting;
import com.android.systemui.Dumpable;
import com.android.systemui.broadcast.BroadcastDispatcher;
import com.android.systemui.dump.DumpManager;
import com.android.systemui.media.MediaDataManager;
import com.android.systemui.tuner.TunerService;
import com.android.systemui.util.Utils;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.Regex;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/* compiled from: MediaResumeListener.kt */
public final class MediaResumeListener implements MediaDataManager.Listener, Dumpable {
    @NotNull
    private final Executor backgroundExecutor;
    @NotNull
    private final BroadcastDispatcher broadcastDispatcher;
    /* access modifiers changed from: private */
    @NotNull
    public final Context context;
    /* access modifiers changed from: private */
    public int currentUserId;
    /* access modifiers changed from: private */
    @Nullable
    public ResumeMediaBrowser mediaBrowser;
    @NotNull
    private final MediaResumeListener$mediaBrowserCallback$1 mediaBrowserCallback;
    /* access modifiers changed from: private */
    @NotNull
    public final ResumeMediaBrowserFactory mediaBrowserFactory;
    /* access modifiers changed from: private */
    public MediaDataManager mediaDataManager;
    @NotNull
    private final ConcurrentLinkedQueue<ComponentName> resumeComponents = new ConcurrentLinkedQueue<>();
    @NotNull
    private final TunerService tunerService;
    /* access modifiers changed from: private */
    public boolean useMediaResumption;
    @NotNull
    private final BroadcastReceiver userChangeReceiver;

    @VisibleForTesting
    public static /* synthetic */ void getUserChangeReceiver$annotations() {
    }

    public MediaResumeListener(@NotNull Context context2, @NotNull BroadcastDispatcher broadcastDispatcher2, @NotNull Executor executor, @NotNull TunerService tunerService2, @NotNull ResumeMediaBrowserFactory resumeMediaBrowserFactory, @NotNull DumpManager dumpManager) {
        Intrinsics.checkNotNullParameter(context2, "context");
        Intrinsics.checkNotNullParameter(broadcastDispatcher2, "broadcastDispatcher");
        Intrinsics.checkNotNullParameter(executor, "backgroundExecutor");
        Intrinsics.checkNotNullParameter(tunerService2, "tunerService");
        Intrinsics.checkNotNullParameter(resumeMediaBrowserFactory, "mediaBrowserFactory");
        Intrinsics.checkNotNullParameter(dumpManager, "dumpManager");
        this.context = context2;
        this.broadcastDispatcher = broadcastDispatcher2;
        this.backgroundExecutor = executor;
        this.tunerService = tunerService2;
        this.mediaBrowserFactory = resumeMediaBrowserFactory;
        this.useMediaResumption = Utils.useMediaResumption(context2);
        this.currentUserId = context2.getUserId();
        MediaResumeListener$userChangeReceiver$1 mediaResumeListener$userChangeReceiver$1 = new MediaResumeListener$userChangeReceiver$1(this);
        this.userChangeReceiver = mediaResumeListener$userChangeReceiver$1;
        this.mediaBrowserCallback = new MediaResumeListener$mediaBrowserCallback$1(this);
        if (this.useMediaResumption) {
            dumpManager.registerDumpable("MediaResumeListener", this);
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction("android.intent.action.USER_UNLOCKED");
            intentFilter.addAction("android.intent.action.USER_SWITCHED");
            broadcastDispatcher2.registerReceiver(mediaResumeListener$userChangeReceiver$1, intentFilter, (Executor) null, UserHandle.ALL);
            loadSavedComponents();
        }
    }

    public void onMediaDataRemoved(@NotNull String str) {
        MediaDataManager.Listener.DefaultImpls.onMediaDataRemoved(this, str);
    }

    public void onSmartspaceMediaDataLoaded(@NotNull String str, @NotNull SmartspaceMediaData smartspaceMediaData, boolean z) {
        MediaDataManager.Listener.DefaultImpls.onSmartspaceMediaDataLoaded(this, str, smartspaceMediaData, z);
    }

    public void onSmartspaceMediaDataRemoved(@NotNull String str, boolean z) {
        MediaDataManager.Listener.DefaultImpls.onSmartspaceMediaDataRemoved(this, str, z);
    }

    public final void setManager(@NotNull MediaDataManager mediaDataManager2) {
        Intrinsics.checkNotNullParameter(mediaDataManager2, "manager");
        this.mediaDataManager = mediaDataManager2;
        this.tunerService.addTunable(new MediaResumeListener$setManager$1(this), "qs_media_resumption");
    }

    /* access modifiers changed from: private */
    public final void loadSavedComponents() {
        List<String> split;
        boolean z;
        this.resumeComponents.clear();
        List<T> list = null;
        String string = this.context.getSharedPreferences("media_control_prefs", 0).getString(Intrinsics.stringPlus("browser_components_", Integer.valueOf(this.currentUserId)), (String) null);
        if (string != null && (split = new Regex(":").split(string, 0)) != null) {
            if (!split.isEmpty()) {
                ListIterator<String> listIterator = split.listIterator(split.size());
                while (true) {
                    if (!listIterator.hasPrevious()) {
                        break;
                    }
                    if (listIterator.previous().length() == 0) {
                        z = true;
                        continue;
                    } else {
                        z = false;
                        continue;
                    }
                    if (!z) {
                        list = CollectionsKt___CollectionsKt.take(split, listIterator.nextIndex() + 1);
                        break;
                    }
                }
            }
            list = CollectionsKt__CollectionsKt.emptyList();
        }
        if (list != null) {
            for (T split$default : list) {
                List split$default2 = StringsKt__StringsKt.split$default(split$default, new String[]{"/"}, false, 0, 6, (Object) null);
                this.resumeComponents.add(new ComponentName((String) split$default2.get(0), (String) split$default2.get(1)));
            }
        }
        String arrays = Arrays.toString(this.resumeComponents.toArray());
        Intrinsics.checkNotNullExpressionValue(arrays, "java.util.Arrays.toString(this)");
        Log.d("MediaResumeListener", Intrinsics.stringPlus("loaded resume components ", arrays));
    }

    /* access modifiers changed from: private */
    public final void loadMediaResumptionControls() {
        if (this.useMediaResumption) {
            for (ComponentName create : this.resumeComponents) {
                this.mediaBrowserFactory.create(this.mediaBrowserCallback, create).findRecentMedia();
            }
        }
    }

    public void onMediaDataLoaded(@NotNull String str, @Nullable String str2, @NotNull MediaData mediaData, boolean z, boolean z2) {
        ArrayList arrayList;
        Intrinsics.checkNotNullParameter(str, "key");
        Intrinsics.checkNotNullParameter(mediaData, "data");
        if (this.useMediaResumption) {
            if (!str.equals(str2)) {
                ResumeMediaBrowser resumeMediaBrowser = this.mediaBrowser;
                if (resumeMediaBrowser != null) {
                    resumeMediaBrowser.disconnect();
                }
                this.mediaBrowser = null;
            }
            if (mediaData.getResumeAction() == null && !mediaData.getHasCheckedForResume() && mediaData.isLocalSession()) {
                Log.d("MediaResumeListener", Intrinsics.stringPlus("Checking for service component for ", mediaData.getPackageName()));
                List<ResolveInfo> queryIntentServices = this.context.getPackageManager().queryIntentServices(new Intent("android.media.browse.MediaBrowserService"), 0);
                if (queryIntentServices == null) {
                    arrayList = null;
                } else {
                    arrayList = new ArrayList();
                    for (T next : queryIntentServices) {
                        if (Intrinsics.areEqual((Object) ((ResolveInfo) next).serviceInfo.packageName, (Object) mediaData.getPackageName())) {
                            arrayList.add(next);
                        }
                    }
                }
                if (arrayList == null || arrayList.size() <= 0) {
                    MediaDataManager mediaDataManager2 = this.mediaDataManager;
                    if (mediaDataManager2 != null) {
                        mediaDataManager2.setResumeAction(str, (Runnable) null);
                    } else {
                        Intrinsics.throwUninitializedPropertyAccessException("mediaDataManager");
                        throw null;
                    }
                } else {
                    this.backgroundExecutor.execute(new MediaResumeListener$onMediaDataLoaded$1(this, str, arrayList));
                }
            }
        }
    }

    /* access modifiers changed from: private */
    public final void tryUpdateResumptionList(String str, ComponentName componentName) {
        Log.d("MediaResumeListener", Intrinsics.stringPlus("Testing if we can connect to ", componentName));
        MediaDataManager mediaDataManager2 = this.mediaDataManager;
        if (mediaDataManager2 != null) {
            mediaDataManager2.setResumeAction(str, (Runnable) null);
            ResumeMediaBrowser resumeMediaBrowser = this.mediaBrowser;
            if (resumeMediaBrowser != null) {
                resumeMediaBrowser.disconnect();
            }
            ResumeMediaBrowser create = this.mediaBrowserFactory.create(new MediaResumeListener$tryUpdateResumptionList$1(componentName, this, str), componentName);
            this.mediaBrowser = create;
            if (create != null) {
                create.testConnection();
                return;
            }
            return;
        }
        Intrinsics.throwUninitializedPropertyAccessException("mediaDataManager");
        throw null;
    }

    /* access modifiers changed from: private */
    public final void updateResumptionList(ComponentName componentName) {
        this.resumeComponents.remove(componentName);
        this.resumeComponents.add(componentName);
        if (this.resumeComponents.size() > 5) {
            this.resumeComponents.remove();
        }
        StringBuilder sb = new StringBuilder();
        for (ComponentName flattenToString : this.resumeComponents) {
            sb.append(flattenToString.flattenToString());
            sb.append(":");
        }
        this.context.getSharedPreferences("media_control_prefs", 0).edit().putString(Intrinsics.stringPlus("browser_components_", Integer.valueOf(this.currentUserId)), sb.toString()).apply();
    }

    /* access modifiers changed from: private */
    public final Runnable getResumeAction(ComponentName componentName) {
        return new MediaResumeListener$getResumeAction$1(this, componentName);
    }

    public void dump(@NotNull FileDescriptor fileDescriptor, @NotNull PrintWriter printWriter, @NotNull String[] strArr) {
        Intrinsics.checkNotNullParameter(fileDescriptor, "fd");
        Intrinsics.checkNotNullParameter(printWriter, "pw");
        Intrinsics.checkNotNullParameter(strArr, "args");
        printWriter.println(Intrinsics.stringPlus("resumeComponents: ", this.resumeComponents));
    }
}
