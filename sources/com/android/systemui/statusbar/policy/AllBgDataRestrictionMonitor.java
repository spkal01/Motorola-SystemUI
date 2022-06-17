package com.android.systemui.statusbar.policy;

import android.content.Context;
import android.database.ContentObserver;
import android.os.Handler;
import com.android.systemui.R$bool;
import com.motorola.android.provider.MotorolaSettings;

public class AllBgDataRestrictionMonitor {
    private boolean mAllBgDataRestricted = false;
    private final Context mContext;
    private Listener mListener;
    private final ContentObserver mStateObserver;

    public interface Listener {
        void onAllBgDataRestrictionChanged(boolean z);
    }

    public AllBgDataRestrictionMonitor(Context context) {
        this.mContext = context;
        if (context.getResources().getBoolean(R$bool.config_allow_unavailable_data_saver)) {
            C19841 r1 = new ContentObserver((Handler) null) {
                public void onChange(boolean z) {
                    AllBgDataRestrictionMonitor.this.onStateChanged();
                }
            };
            this.mStateObserver = r1;
            context.getContentResolver().registerContentObserver(MotorolaSettings.Global.getUriFor("restrict_all_background_data"), false, r1);
            this.mAllBgDataRestricted = getAllBgDataRestrictedState();
            return;
        }
        this.mStateObserver = null;
    }

    public void registerListener(Listener listener) {
        this.mListener = listener;
    }

    public boolean isRestricted() {
        return this.mAllBgDataRestricted;
    }

    /* access modifiers changed from: private */
    public void onStateChanged() {
        boolean allBgDataRestrictedState = getAllBgDataRestrictedState();
        if (allBgDataRestrictedState != this.mAllBgDataRestricted) {
            this.mAllBgDataRestricted = allBgDataRestrictedState;
            Listener listener = this.mListener;
            if (listener != null) {
                listener.onAllBgDataRestrictionChanged(allBgDataRestrictedState);
            }
        }
    }

    private boolean getAllBgDataRestrictedState() {
        return Boolean.parseBoolean(MotorolaSettings.Global.getString(this.mContext.getContentResolver(), "restrict_all_background_data"));
    }
}
