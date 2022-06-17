package com.motorola.systemui.cli.media;

import com.motorola.systemui.cli.media.CliMediaViewForQS;

public interface CliMediaView {
    void addedToViewPager() {
    }

    String getPackageName();

    void parentDetachedFromWindow() {
    }

    void removedFromViewPager() {
    }

    void setCliMediaPage(CliMediaPageModel cliMediaPageModel);

    void setMediaViewCallback(CliMediaViewForQS.MediaViewCallback mediaViewCallback) {
    }

    void updateControllerActive(boolean z) {
    }

    void updateMediaOutputIcon(int i) {
    }

    void updateMediaOutputName(int i) {
    }

    void updateMediaOutputName(String str) {
    }

    void updateMediaPage(CliMediaPageModel cliMediaPageModel);
}
