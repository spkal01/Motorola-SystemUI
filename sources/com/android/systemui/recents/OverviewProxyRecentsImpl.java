package com.android.systemui.recents;

import android.app.trust.TrustManager;
import android.content.Context;
import android.os.Handler;
import android.os.RemoteException;
import android.util.Log;
import com.android.systemui.Dependency;
import com.android.systemui.shared.recents.IOverviewProxy;
import com.android.systemui.statusbar.phone.StatusBar;
import dagger.Lazy;
import java.util.Optional;

public class OverviewProxyRecentsImpl implements RecentsImplementation {
    private Context mContext;
    private Handler mHandler;
    private OverviewProxyService mOverviewProxyService;
    private final Lazy<StatusBar> mStatusBarLazy;
    private TrustManager mTrustManager;

    public OverviewProxyRecentsImpl(Optional<Lazy<StatusBar>> optional) {
        this.mStatusBarLazy = optional.orElse((Object) null);
    }

    public void onStart(Context context) {
        this.mContext = context;
        this.mHandler = new Handler();
        this.mTrustManager = (TrustManager) context.getSystemService("trust");
        this.mOverviewProxyService = (OverviewProxyService) Dependency.get(OverviewProxyService.class);
    }

    public void showRecentApps(boolean z) {
        IOverviewProxy proxy = this.mOverviewProxyService.getProxy();
        if (proxy != null) {
            try {
                proxy.onOverviewShown(z);
            } catch (RemoteException e) {
                Log.e("OverviewProxyRecentsImpl", "Failed to send overview show event to launcher.", e);
            }
        }
    }

    public void hideRecentApps(boolean z, boolean z2) {
        IOverviewProxy proxy = this.mOverviewProxyService.getProxy();
        if (proxy != null) {
            try {
                proxy.onOverviewHidden(z, z2);
            } catch (RemoteException e) {
                Log.e("OverviewProxyRecentsImpl", "Failed to send overview hide event to launcher.", e);
            }
        }
    }

    public void toggleRecentApps() {
        if (this.mOverviewProxyService.getProxy() != null) {
            OverviewProxyRecentsImpl$$ExternalSyntheticLambda0 overviewProxyRecentsImpl$$ExternalSyntheticLambda0 = new OverviewProxyRecentsImpl$$ExternalSyntheticLambda0(this);
            Lazy<StatusBar> lazy = this.mStatusBarLazy;
            if (lazy == null || !lazy.get().isKeyguardShowing()) {
                overviewProxyRecentsImpl$$ExternalSyntheticLambda0.run();
            } else {
                this.mStatusBarLazy.get().executeRunnableDismissingKeyguard(new OverviewProxyRecentsImpl$$ExternalSyntheticLambda1(this, overviewProxyRecentsImpl$$ExternalSyntheticLambda0), (Runnable) null, true, false, true);
            }
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$toggleRecentApps$0() {
        try {
            if (this.mOverviewProxyService.getProxy() != null) {
                this.mOverviewProxyService.getProxy().onOverviewToggle();
                this.mOverviewProxyService.notifyToggleRecentApps();
            }
        } catch (RemoteException e) {
            Log.e("OverviewProxyRecentsImpl", "Cannot send toggle recents through proxy service.", e);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$toggleRecentApps$1(Runnable runnable) {
        this.mTrustManager.reportKeyguardShowingChanged();
        this.mHandler.post(runnable);
    }
}
