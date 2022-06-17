package com.android.systemui.tuner;

import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.UserInfo;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerExecutor;
import android.os.Looper;
import android.os.UserManager;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.ArraySet;
import com.android.internal.util.ArrayUtils;
import com.android.systemui.DejankUtils;
import com.android.systemui.R$string;
import com.android.systemui.demomode.DemoModeController;
import com.android.systemui.moto.MotoFeature;
import com.android.systemui.settings.UserTracker;
import com.android.systemui.statusbar.phone.StatusBarIconController;
import com.android.systemui.statusbar.phone.SystemUIDialog;
import com.android.systemui.tuner.TunerService;
import com.android.systemui.util.leak.LeakDetector;
import com.motorola.systemui.statusbar.settings.StatusbarSettingActivity;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class TunerServiceImpl extends TunerService {
    private static final String[] RESET_EXCEPTION_LIST = {"sysui_qs_tiles", "doze_always_on", "qs_media_resumption", "qs_media_recommend"};
    private ContentResolver mContentResolver;
    private final Context mContext;
    /* access modifiers changed from: private */
    public int mCurrentUser;
    private UserTracker.Callback mCurrentUserTracker;
    private final DemoModeController mDemoModeController;
    private final LeakDetector mLeakDetector;
    private final ArrayMap<Uri, String> mListeningUris = new ArrayMap<>();
    private final Observer mObserver = new Observer();
    private ComponentName mSettingComponent;
    private final ConcurrentHashMap<String, Set<TunerService.Tunable>> mTunableLookup = new ConcurrentHashMap<>();
    private final HashSet<TunerService.Tunable> mTunables;
    private final ComponentName mTunerComponent;
    /* access modifiers changed from: private */
    public UserTracker mUserTracker;

    public TunerServiceImpl(Context context, Handler handler, LeakDetector leakDetector, DemoModeController demoModeController, UserTracker userTracker) {
        super(context);
        this.mTunables = LeakDetector.ENABLED ? new HashSet<>() : null;
        this.mContext = context;
        this.mContentResolver = context.getContentResolver();
        this.mLeakDetector = leakDetector;
        this.mDemoModeController = demoModeController;
        this.mUserTracker = userTracker;
        this.mTunerComponent = new ComponentName(context, TunerActivity.class);
        if (MotoFeature.isPrcProduct()) {
            this.mSettingComponent = new ComponentName(context, StatusbarSettingActivity.class);
            setSettingEnabled(true);
        }
        for (UserInfo userHandle : UserManager.get(context).getUsers()) {
            this.mCurrentUser = userHandle.getUserHandle().getIdentifier();
            if (getValue("sysui_tuner_version", 0) != 4) {
                upgradeTuner(getValue("sysui_tuner_version", 0), 4, handler);
            }
        }
        this.mCurrentUser = this.mUserTracker.getUserId();
        C20941 r2 = new UserTracker.Callback() {
            public void onUserChanged(int i, Context context) {
                int unused = TunerServiceImpl.this.mCurrentUser = i;
                TunerServiceImpl.this.reloadAll();
                TunerServiceImpl.this.reregisterAll();
            }
        };
        this.mCurrentUserTracker = r2;
        this.mUserTracker.addCallback(r2, new HandlerExecutor(handler));
    }

    private void upgradeTuner(int i, int i2, Handler handler) {
        String value;
        if (i < 1 && (value = getValue("icon_blacklist")) != null) {
            ArraySet<String> iconHideList = StatusBarIconController.getIconHideList(this.mContext, value);
            iconHideList.add("rotate");
            iconHideList.add("headset");
            Settings.Secure.putStringForUser(this.mContentResolver, "icon_blacklist", TextUtils.join(",", iconHideList), this.mCurrentUser);
        }
        if (i < 2) {
            setTunerEnabled(false);
        }
        if (i < 4) {
            handler.postDelayed(new TunerServiceImpl$$ExternalSyntheticLambda1(this, this.mCurrentUser), 5000);
        }
        setValue("sysui_tuner_version", i2);
    }

    public String getValue(String str) {
        return Settings.Secure.getStringForUser(this.mContentResolver, str, this.mCurrentUser);
    }

    public void setValue(String str, String str2) {
        Settings.Secure.putStringForUser(this.mContentResolver, str, str2, this.mCurrentUser);
    }

    public int getValue(String str, int i) {
        return Settings.Secure.getIntForUser(this.mContentResolver, str, i, this.mCurrentUser);
    }

    public void setValue(String str, int i) {
        Settings.Secure.putIntForUser(this.mContentResolver, str, i, this.mCurrentUser);
    }

    public void addTunable(TunerService.Tunable tunable, String... strArr) {
        for (String addTunable : strArr) {
            addTunable(tunable, addTunable);
        }
    }

    private void addTunable(TunerService.Tunable tunable, String str) {
        if (!this.mTunableLookup.containsKey(str)) {
            this.mTunableLookup.put(str, new ArraySet());
        }
        this.mTunableLookup.get(str).add(tunable);
        if (LeakDetector.ENABLED) {
            this.mTunables.add(tunable);
            this.mLeakDetector.trackCollection(this.mTunables, "TunerService.mTunables");
        }
        Uri uriFor = Settings.Secure.getUriFor(str);
        if (!this.mListeningUris.containsKey(uriFor)) {
            this.mListeningUris.put(uriFor, str);
            this.mContentResolver.registerContentObserver(uriFor, false, this.mObserver, this.mCurrentUser);
        }
        tunable.onTuningChanged(str, (String) DejankUtils.whitelistIpcs(new TunerServiceImpl$$ExternalSyntheticLambda2(this, str)));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ String lambda$addTunable$1(String str) {
        return Settings.Secure.getStringForUser(this.mContentResolver, str, this.mCurrentUser);
    }

    public void removeTunable(TunerService.Tunable tunable) {
        for (Set<TunerService.Tunable> remove : this.mTunableLookup.values()) {
            remove.remove(tunable);
        }
        if (LeakDetector.ENABLED) {
            this.mTunables.remove(tunable);
        }
    }

    /* access modifiers changed from: protected */
    public void reregisterAll() {
        if (this.mListeningUris.size() != 0) {
            this.mContentResolver.unregisterContentObserver(this.mObserver);
            for (Uri registerContentObserver : this.mListeningUris.keySet()) {
                this.mContentResolver.registerContentObserver(registerContentObserver, false, this.mObserver, this.mCurrentUser);
            }
        }
    }

    /* access modifiers changed from: private */
    public void reloadSetting(Uri uri) {
        String str = this.mListeningUris.get(uri);
        Set<TunerService.Tunable> set = this.mTunableLookup.get(str);
        if (set != null) {
            String stringForUser = Settings.Secure.getStringForUser(this.mContentResolver, str, this.mCurrentUser);
            for (TunerService.Tunable onTuningChanged : set) {
                onTuningChanged.onTuningChanged(str, stringForUser);
            }
        }
    }

    /* access modifiers changed from: private */
    public void reloadAll() {
        for (String next : this.mTunableLookup.keySet()) {
            String stringForUser = Settings.Secure.getStringForUser(this.mContentResolver, next, this.mCurrentUser);
            for (TunerService.Tunable onTuningChanged : this.mTunableLookup.get(next)) {
                onTuningChanged.onTuningChanged(next, stringForUser);
            }
        }
    }

    public void clearAll() {
        lambda$upgradeTuner$0(this.mCurrentUser);
    }

    /* renamed from: clearAllFromUser */
    public void lambda$upgradeTuner$0(int i) {
        this.mDemoModeController.requestFinishDemoMode();
        this.mDemoModeController.requestSetDemoModeAllowed(false);
        for (String next : this.mTunableLookup.keySet()) {
            if (!ArrayUtils.contains(RESET_EXCEPTION_LIST, next)) {
                Settings.Secure.putStringForUser(this.mContentResolver, next, (String) null, i);
            }
        }
    }

    public void setTunerEnabled(boolean z) {
        this.mUserTracker.getUserContext().getPackageManager().setComponentEnabledSetting(this.mTunerComponent, z ? 1 : 2, 1);
    }

    public boolean isTunerEnabled() {
        return this.mUserTracker.getUserContext().getPackageManager().getComponentEnabledSetting(this.mTunerComponent) == 1;
    }

    public void showResetRequest(Runnable runnable) {
        SystemUIDialog systemUIDialog = new SystemUIDialog(this.mContext);
        systemUIDialog.setShowForAllUsers(true);
        systemUIDialog.setMessage(R$string.remove_from_settings_prompt);
        systemUIDialog.setButton(-2, this.mContext.getString(R$string.cancel), (DialogInterface.OnClickListener) null);
        systemUIDialog.setButton(-1, this.mContext.getString(R$string.qs_customize_remove), new TunerServiceImpl$$ExternalSyntheticLambda0(this, runnable));
        systemUIDialog.show();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$showResetRequest$2(Runnable runnable, DialogInterface dialogInterface, int i) {
        this.mContext.sendBroadcast(new Intent("com.android.systemui.action.CLEAR_TUNER"));
        setTunerEnabled(false);
        Settings.Secure.putInt(this.mContext.getContentResolver(), "seen_tuner_warning", 0);
        if (runnable != null) {
            runnable.run();
        }
    }

    private class Observer extends ContentObserver {
        public Observer() {
            super(new Handler(Looper.getMainLooper()));
        }

        public void onChange(boolean z, Collection<Uri> collection, int i, int i2) {
            if (i2 == TunerServiceImpl.this.mUserTracker.getUserId()) {
                for (Uri access$300 : collection) {
                    TunerServiceImpl.this.reloadSetting(access$300);
                }
            }
        }
    }

    private void setSettingEnabled(boolean z) {
        this.mUserTracker.getUserContext().getPackageManager().setComponentEnabledSetting(this.mSettingComponent, z ? 1 : 2, 1);
    }
}
