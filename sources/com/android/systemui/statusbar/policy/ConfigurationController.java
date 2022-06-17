package com.android.systemui.statusbar.policy;

import android.content.res.Configuration;

public interface ConfigurationController extends CallbackController<ConfigurationListener> {

    public interface ConfigurationListener {
        void onConfigChanged(Configuration configuration) {
        }

        void onDensityOrFontScaleChanged() {
        }

        void onLayoutDirectionChanged(boolean z) {
        }

        void onLocaleListChanged() {
        }

        void onOverlayChanged() {
        }

        void onSmallestScreenWidthChanged() {
        }

        void onThemeChanged() {
        }

        void onUiModeChanged() {
        }
    }

    boolean isLayoutRtl();

    void notifyThemeChanged();

    void onConfigurationChanged(Configuration configuration);
}
