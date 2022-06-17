package com.android.p011wm.shell.pip;

import android.content.res.Configuration;
import android.graphics.Rect;
import java.io.PrintWriter;
import java.util.function.Consumer;

/* renamed from: com.android.wm.shell.pip.Pip */
public interface Pip {
    IPip createExternalInterface() {
        return null;
    }

    void dump(PrintWriter printWriter) {
    }

    void hidePipMenu(Runnable runnable, Runnable runnable2) {
    }

    void onConfigurationChanged(Configuration configuration) {
    }

    void onDensityOrFontScaleChanged() {
    }

    void onOverlayChanged() {
    }

    void onSystemUiStateChanged(boolean z, int i) {
    }

    void registerSessionListenerForCurrentUser() {
    }

    void setPinnedStackAnimationType(int i) {
    }

    void setPipExclusionBoundsChangeListener(Consumer<Rect> consumer) {
    }

    void showPictureInPictureMenu() {
    }
}
