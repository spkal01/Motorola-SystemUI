package com.motorola.systemui.cli.navgesture;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.android.systemui.R$id;
import com.android.systemui.R$layout;
import com.motorola.systemui.cli.navgesture.util.DebugLog;
import com.motorola.systemui.cli.navgesture.view.RecentsViewContainer;

public class CliRecentsActivity extends AbstractRecentGestureLauncher {
    private static final String LOG_TAG = "CliRecentsActivity";
    private int mPendingRequestCode = -1;
    private RecentsViewContainer mRecentsViewContainer;
    private ViewGroup mRootView;

    /* access modifiers changed from: protected */
    public void onCreateBeforeInflaterView(Bundle bundle) {
        super.onCreateBeforeInflaterView(bundle);
    }

    /* access modifiers changed from: protected */
    public View onCreateInflaterView(LayoutInflater layoutInflater) {
        return layoutInflater.inflate(R$layout.zz_moto_cli_recents_activity, (ViewGroup) null);
    }

    /* access modifiers changed from: protected */
    public void onCreateSetupViews(Bundle bundle) {
        super.onCreateSetupViews(bundle);
        this.mRootView = (ViewGroup) findViewById(R$id.root_view);
        RecentsViewContainer recentsViewContainer = (RecentsViewContainer) requireViewById(R$id.recents_container);
        this.mRecentsViewContainer = recentsViewContainer;
        recentsViewContainer.setup();
        String str = LOG_TAG;
        DebugLog.m98d(str, "setupViews:config = " + getResources().getConfiguration());
    }

    public ViewGroup getRootView() {
        return this.mRootView;
    }

    public IRecentsView getOverviewPanel() {
        return this.mRecentsViewContainer;
    }

    /* access modifiers changed from: protected */
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.d(LOG_TAG, "onNewIntent");
    }

    /* access modifiers changed from: protected */
    public void onStart() {
        super.onStart();
        Log.d(LOG_TAG, "onStart");
    }

    /* access modifiers changed from: protected */
    public void onResume() {
        super.onResume();
        this.mRootView.setSystemUiVisibility(5890);
    }

    public void onDestroy() {
        super.onDestroy();
        Log.d(LOG_TAG, "onDestroy");
    }

    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        String str = LOG_TAG;
        DebugLog.m98d(str, "onConfigurationChanged: newConfig = " + configuration);
    }

    public void onBackPressed() {
        RecentsViewContainer recentsViewContainer = this.mRecentsViewContainer;
        if (recentsViewContainer == null) {
            finish();
        } else if (!recentsViewContainer.onBackPressed()) {
            this.mRecentsViewContainer.startHome();
        }
    }
}
