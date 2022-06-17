package com.android.systemui.recents;

import android.content.Context;
import android.content.res.Configuration;
import java.io.PrintWriter;

public interface RecentsImplementation {
    void cancelPreloadRecentApps() {
    }

    void dump(PrintWriter printWriter) {
    }

    void hideRecentApps(boolean z, boolean z2) {
    }

    void onAppTransitionFinished() {
    }

    void onBootCompleted() {
    }

    void onConfigurationChanged(Configuration configuration) {
    }

    void onStart(Context context) {
    }

    void preloadRecentApps() {
    }

    void showRecentApps(boolean z) {
    }

    void toggleRecentApps() {
    }
}
