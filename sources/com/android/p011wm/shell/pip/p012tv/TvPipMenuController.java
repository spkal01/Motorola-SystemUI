package com.android.p011wm.shell.pip.p012tv;

import android.app.RemoteAction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ParceledListSlice;
import android.os.Handler;
import android.util.Log;
import android.view.SurfaceControl;
import com.android.p011wm.shell.common.SystemWindows;
import com.android.p011wm.shell.pip.PipBoundsState;
import com.android.p011wm.shell.pip.PipMediaController;
import com.android.p011wm.shell.pip.PipMenuController;
import com.android.p011wm.shell.pip.p012tv.TvPipMenuView;
import java.util.ArrayList;
import java.util.List;

/* renamed from: com.android.wm.shell.pip.tv.TvPipMenuController */
public class TvPipMenuController implements PipMenuController, TvPipMenuView.Listener {
    private final List<RemoteAction> mAppActions = new ArrayList();
    private final Context mContext;
    private Delegate mDelegate;
    private SurfaceControl mLeash;
    private final Handler mMainHandler;
    private final List<RemoteAction> mMediaActions = new ArrayList();
    private TvPipMenuView mMenuView;
    private final PipBoundsState mPipBoundsState;
    private final SystemWindows mSystemWindows;

    /* renamed from: com.android.wm.shell.pip.tv.TvPipMenuController$Delegate */
    interface Delegate {
        void closePip();

        void movePipToFullscreen();

        void movePipToNormalPosition();
    }

    public TvPipMenuController(Context context, PipBoundsState pipBoundsState, SystemWindows systemWindows, PipMediaController pipMediaController, Handler handler) {
        this.mContext = context;
        this.mPipBoundsState = pipBoundsState;
        this.mSystemWindows = systemWindows;
        this.mMainHandler = handler;
        context.registerReceiverForAllUsers(new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                TvPipMenuController.this.hideMenu();
            }
        }, new IntentFilter("android.intent.action.CLOSE_SYSTEM_DIALOGS"), (String) null, handler);
        pipMediaController.addActionListener(new TvPipMenuController$$ExternalSyntheticLambda0(this));
    }

    /* access modifiers changed from: package-private */
    public void setDelegate(Delegate delegate) {
        Log.d("TvPipMenuController", "setDelegate(), delegate=" + delegate);
        if (this.mDelegate != null) {
            throw new IllegalStateException("The delegate has already been set and should not change.");
        } else if (delegate != null) {
            this.mDelegate = delegate;
        } else {
            throw new IllegalArgumentException("The delegate must not be null.");
        }
    }

    public void attach(SurfaceControl surfaceControl) {
        if (this.mDelegate != null) {
            this.mLeash = surfaceControl;
            attachPipMenuView();
            return;
        }
        throw new IllegalStateException("Delegate is not set.");
    }

    private void attachPipMenuView() {
        Log.d("TvPipMenuController", "attachPipMenuView()");
        if (this.mMenuView != null) {
            detachPipMenuView();
        }
        TvPipMenuView tvPipMenuView = new TvPipMenuView(this.mContext);
        this.mMenuView = tvPipMenuView;
        tvPipMenuView.setListener(this);
        this.mSystemWindows.addView(this.mMenuView, getPipMenuLayoutParams("PipMenuView", 0, 0), 0, 1);
    }

    public void showMenu() {
        Log.d("TvPipMenuController", "showMenu()");
        TvPipMenuView tvPipMenuView = this.mMenuView;
        if (tvPipMenuView != null) {
            this.mSystemWindows.updateViewLayout(tvPipMenuView, getPipMenuLayoutParams("PipMenuView", this.mPipBoundsState.getDisplayBounds().width(), this.mPipBoundsState.getDisplayBounds().height()));
            maybeUpdateMenuViewActions();
            this.mMenuView.show();
            if (this.mMenuView.getWindowSurfaceControl() != null && this.mLeash != null) {
                SurfaceControl.Transaction transaction = new SurfaceControl.Transaction();
                transaction.setRelativeLayer(this.mMenuView.getWindowSurfaceControl(), this.mLeash, -1);
                transaction.apply();
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void hideMenu() {
        hideMenu(true);
    }

    /* access modifiers changed from: package-private */
    public void hideMenu(boolean z) {
        Log.d("TvPipMenuController", "hideMenu(), movePipWindow=" + z);
        if (isMenuVisible()) {
            this.mMenuView.hide();
            if (z) {
                this.mDelegate.movePipToNormalPosition();
            }
        }
    }

    public void detach() {
        hideMenu();
        detachPipMenuView();
        this.mLeash = null;
    }

    private void detachPipMenuView() {
        Log.d("TvPipMenuController", "detachPipMenuView()");
        TvPipMenuView tvPipMenuView = this.mMenuView;
        if (tvPipMenuView != null) {
            this.mSystemWindows.removeView(tvPipMenuView);
            this.mMenuView = null;
        }
    }

    public void setAppActions(ParceledListSlice<RemoteAction> parceledListSlice) {
        Log.d("TvPipMenuController", "setAppActions()");
        updateAdditionalActionsList(this.mAppActions, parceledListSlice.getList());
    }

    /* access modifiers changed from: private */
    public void onMediaActionsChanged(List<RemoteAction> list) {
        Log.d("TvPipMenuController", "onMediaActionsChanged()");
        updateAdditionalActionsList(this.mMediaActions, list);
    }

    private void updateAdditionalActionsList(List<RemoteAction> list, List<RemoteAction> list2) {
        int size = list2 != null ? list2.size() : 0;
        if (size != 0 || !list.isEmpty()) {
            list.clear();
            if (size > 0) {
                list.addAll(list2);
            }
            maybeUpdateMenuViewActions();
        }
    }

    private void maybeUpdateMenuViewActions() {
        if (this.mMenuView != null) {
            if (!this.mAppActions.isEmpty()) {
                this.mMenuView.setAdditionalActions(this.mAppActions, this.mMainHandler);
            } else {
                this.mMenuView.setAdditionalActions(this.mMediaActions, this.mMainHandler);
            }
        }
    }

    public boolean isMenuVisible() {
        TvPipMenuView tvPipMenuView = this.mMenuView;
        return tvPipMenuView != null && tvPipMenuView.isVisible();
    }

    public void onBackPress() {
        hideMenu();
    }

    public void onCloseButtonClick() {
        this.mDelegate.closePip();
    }

    public void onFullscreenButtonClick() {
        this.mDelegate.movePipToFullscreen();
    }
}
