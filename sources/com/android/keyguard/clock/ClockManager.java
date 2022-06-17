package com.android.keyguard.clock;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.database.ContentObserver;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.ArrayMap;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import androidx.lifecycle.Observer;
import com.android.systemui.broadcast.BroadcastDispatcher;
import com.android.systemui.colorextraction.SysuiColorExtractor;
import com.android.systemui.dock.DockManager;
import com.android.systemui.plugins.ClockPlugin;
import com.android.systemui.plugins.PluginListener;
import com.android.systemui.settings.CurrentUserObservable;
import com.android.systemui.shared.plugins.PluginManager;
import com.android.systemui.util.InjectionInflationController;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

public final class ClockManager {
    private final List<Supplier<ClockPlugin>> mBuiltinClocks;
    private final ContentObserver mContentObserver;
    private final ContentResolver mContentResolver;
    private final Context mContext;
    /* access modifiers changed from: private */
    public final CurrentUserObservable mCurrentUserObservable;
    private final Observer<Integer> mCurrentUserObserver;
    private final DockManager.DockEventListener mDockEventListener;
    private final DockManager mDockManager;
    /* access modifiers changed from: private */
    public final int mHeight;
    private boolean mIsDocked;
    private final Map<ClockChangedListener, AvailableClocks> mListeners;
    private final Handler mMainHandler;
    private final PluginManager mPluginManager;
    private final AvailableClocks mPreviewClocks;
    /* access modifiers changed from: private */
    public final SettingsWrapper mSettingsWrapper;
    /* access modifiers changed from: private */
    public final int mWidth;

    public interface ClockChangedListener {
        void onClockChanged(ClockPlugin clockPlugin);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0(Integer num) {
        reload();
    }

    public ClockManager(Context context, InjectionInflationController injectionInflationController, PluginManager pluginManager, SysuiColorExtractor sysuiColorExtractor, DockManager dockManager, BroadcastDispatcher broadcastDispatcher) {
        this(context, injectionInflationController, pluginManager, sysuiColorExtractor, context.getContentResolver(), new CurrentUserObservable(broadcastDispatcher), new SettingsWrapper(context.getContentResolver()), dockManager);
    }

    ClockManager(Context context, InjectionInflationController injectionInflationController, PluginManager pluginManager, SysuiColorExtractor sysuiColorExtractor, ContentResolver contentResolver, CurrentUserObservable currentUserObservable, SettingsWrapper settingsWrapper, DockManager dockManager) {
        this.mBuiltinClocks = new ArrayList();
        Handler handler = new Handler(Looper.getMainLooper());
        this.mMainHandler = handler;
        this.mContentObserver = new ContentObserver(handler) {
            public void onChange(boolean z, Collection<Uri> collection, int i, int i2) {
                if (Objects.equals(Integer.valueOf(i2), ClockManager.this.mCurrentUserObservable.getCurrentUser().getValue())) {
                    ClockManager.this.reload();
                }
            }
        };
        this.mCurrentUserObserver = new ClockManager$$ExternalSyntheticLambda0(this);
        this.mDockEventListener = new DockManager.DockEventListener() {
        };
        this.mListeners = new ArrayMap();
        this.mContext = context;
        this.mPluginManager = pluginManager;
        this.mContentResolver = contentResolver;
        this.mSettingsWrapper = settingsWrapper;
        this.mCurrentUserObservable = currentUserObservable;
        this.mDockManager = dockManager;
        this.mPreviewClocks = new AvailableClocks();
        Resources resources = context.getResources();
        addBuiltinClock(new ClockManager$$ExternalSyntheticLambda3(resources, injectionInflationController.injectable(LayoutInflater.from(context)), sysuiColorExtractor));
        DisplayMetrics displayMetrics = resources.getDisplayMetrics();
        this.mWidth = displayMetrics.widthPixels;
        this.mHeight = displayMetrics.heightPixels;
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ ClockPlugin lambda$new$1(Resources resources, LayoutInflater layoutInflater, SysuiColorExtractor sysuiColorExtractor) {
        return new DefaultClockController(resources, layoutInflater, sysuiColorExtractor);
    }

    public void addOnClockChangedListener(ClockChangedListener clockChangedListener) {
        if (this.mListeners.isEmpty()) {
            register();
        }
        AvailableClocks availableClocks = new AvailableClocks();
        for (int i = 0; i < this.mBuiltinClocks.size(); i++) {
            availableClocks.addClockPlugin((ClockPlugin) this.mBuiltinClocks.get(i).get());
        }
        this.mListeners.put(clockChangedListener, availableClocks);
        this.mPluginManager.addPluginListener(availableClocks, (Class<?>) ClockPlugin.class, true);
        reload();
    }

    public void removeOnClockChangedListener(ClockChangedListener clockChangedListener) {
        this.mPluginManager.removePluginListener(this.mListeners.remove(clockChangedListener));
        if (this.mListeners.isEmpty()) {
            unregister();
        }
    }

    /* access modifiers changed from: package-private */
    public List<ClockInfo> getClockInfos() {
        return this.mPreviewClocks.getInfo();
    }

    /* access modifiers changed from: package-private */
    public boolean isDocked() {
        return this.mIsDocked;
    }

    /* access modifiers changed from: package-private */
    public ContentObserver getContentObserver() {
        return this.mContentObserver;
    }

    /* access modifiers changed from: package-private */
    public void addBuiltinClock(Supplier<ClockPlugin> supplier) {
        this.mPreviewClocks.addClockPlugin(supplier.get());
        this.mBuiltinClocks.add(supplier);
    }

    private void register() {
        this.mPluginManager.addPluginListener(this.mPreviewClocks, (Class<?>) ClockPlugin.class, true);
        this.mContentResolver.registerContentObserver(Settings.Secure.getUriFor("lock_screen_custom_clock_face"), false, this.mContentObserver, -1);
        this.mContentResolver.registerContentObserver(Settings.Secure.getUriFor("docked_clock_face"), false, this.mContentObserver, -1);
        this.mCurrentUserObservable.getCurrentUser().observeForever(this.mCurrentUserObserver);
        DockManager dockManager = this.mDockManager;
        if (dockManager != null) {
            dockManager.addListener(this.mDockEventListener);
        }
    }

    private void unregister() {
        this.mPluginManager.removePluginListener(this.mPreviewClocks);
        this.mContentResolver.unregisterContentObserver(this.mContentObserver);
        this.mCurrentUserObservable.getCurrentUser().removeObserver(this.mCurrentUserObserver);
        DockManager dockManager = this.mDockManager;
        if (dockManager != null) {
            dockManager.removeListener(this.mDockEventListener);
        }
    }

    /* access modifiers changed from: private */
    public void reload() {
        this.mPreviewClocks.reloadCurrentClock();
        this.mListeners.forEach(new ClockManager$$ExternalSyntheticLambda2(this));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$reload$3(ClockChangedListener clockChangedListener, AvailableClocks availableClocks) {
        availableClocks.reloadCurrentClock();
        ClockPlugin currentClock = availableClocks.getCurrentClock();
        if (Looper.myLooper() == Looper.getMainLooper()) {
            if (currentClock instanceof DefaultClockController) {
                currentClock = null;
            }
            clockChangedListener.onClockChanged(currentClock);
            return;
        }
        this.mMainHandler.post(new ClockManager$$ExternalSyntheticLambda1(clockChangedListener, currentClock));
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$reload$2(ClockChangedListener clockChangedListener, ClockPlugin clockPlugin) {
        if (clockPlugin instanceof DefaultClockController) {
            clockPlugin = null;
        }
        clockChangedListener.onClockChanged(clockPlugin);
    }

    private final class AvailableClocks implements PluginListener<ClockPlugin> {
        private final List<ClockInfo> mClockInfo;
        private final Map<String, ClockPlugin> mClocks;
        private ClockPlugin mCurrentClock;

        private AvailableClocks() {
            this.mClocks = new ArrayMap();
            this.mClockInfo = new ArrayList();
        }

        public void onPluginConnected(ClockPlugin clockPlugin, Context context) {
            addClockPlugin(clockPlugin);
            reloadIfNeeded(clockPlugin);
        }

        public void onPluginDisconnected(ClockPlugin clockPlugin) {
            removeClockPlugin(clockPlugin);
            reloadIfNeeded(clockPlugin);
        }

        /* access modifiers changed from: package-private */
        public ClockPlugin getCurrentClock() {
            return this.mCurrentClock;
        }

        /* access modifiers changed from: package-private */
        public List<ClockInfo> getInfo() {
            return this.mClockInfo;
        }

        /* access modifiers changed from: package-private */
        public void addClockPlugin(ClockPlugin clockPlugin) {
            String name = clockPlugin.getClass().getName();
            this.mClocks.put(clockPlugin.getClass().getName(), clockPlugin);
            this.mClockInfo.add(ClockInfo.builder().setName(clockPlugin.getName()).setTitle(new ClockManager$AvailableClocks$$ExternalSyntheticLambda2(clockPlugin)).setId(name).setThumbnail(new ClockManager$AvailableClocks$$ExternalSyntheticLambda1(clockPlugin)).setPreview(new ClockManager$AvailableClocks$$ExternalSyntheticLambda0(this, clockPlugin)).build());
        }

        /* access modifiers changed from: private */
        public /* synthetic */ Bitmap lambda$addClockPlugin$0(ClockPlugin clockPlugin) {
            return clockPlugin.getPreview(ClockManager.this.mWidth, ClockManager.this.mHeight);
        }

        private void removeClockPlugin(ClockPlugin clockPlugin) {
            String name = clockPlugin.getClass().getName();
            this.mClocks.remove(name);
            for (int i = 0; i < this.mClockInfo.size(); i++) {
                if (name.equals(this.mClockInfo.get(i).getId())) {
                    this.mClockInfo.remove(i);
                    return;
                }
            }
        }

        private void reloadIfNeeded(ClockPlugin clockPlugin) {
            boolean z = true;
            boolean z2 = clockPlugin == this.mCurrentClock;
            reloadCurrentClock();
            if (clockPlugin != this.mCurrentClock) {
                z = false;
            }
            if (z2 || z) {
                ClockManager.this.reload();
            }
        }

        /* access modifiers changed from: package-private */
        public void reloadCurrentClock() {
            this.mCurrentClock = getClockPlugin();
        }

        private ClockPlugin getClockPlugin() {
            ClockPlugin clockPlugin;
            String dockedClockFace;
            if (!ClockManager.this.isDocked() || (dockedClockFace = ClockManager.this.mSettingsWrapper.getDockedClockFace(ClockManager.this.mCurrentUserObservable.getCurrentUser().getValue().intValue())) == null) {
                clockPlugin = null;
            } else {
                clockPlugin = this.mClocks.get(dockedClockFace);
                if (clockPlugin != null) {
                    return clockPlugin;
                }
            }
            String lockScreenCustomClockFace = ClockManager.this.mSettingsWrapper.getLockScreenCustomClockFace(ClockManager.this.mCurrentUserObservable.getCurrentUser().getValue().intValue());
            return lockScreenCustomClockFace != null ? this.mClocks.get(lockScreenCustomClockFace) : clockPlugin;
        }
    }
}
