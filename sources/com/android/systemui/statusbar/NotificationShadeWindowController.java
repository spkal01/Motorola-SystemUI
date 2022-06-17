package com.android.systemui.statusbar;

import android.view.ViewGroup;
import com.android.systemui.statusbar.RemoteInputController;
import com.android.systemui.statusbar.phone.StatusBarWindowCallback;
import java.util.function.Consumer;

public interface NotificationShadeWindowController extends RemoteInputController.Callback {

    public interface ForcePluginOpenListener {
        void onChange(boolean z);
    }

    public interface OtherwisedCollapsedListener {
        void setWouldOtherwiseCollapse(boolean z);
    }

    void attach() {
    }

    boolean getForcePluginOpen() {
        return false;
    }

    boolean getPanelExpanded() {
        return false;
    }

    boolean isLaunchingActivity() {
        return false;
    }

    boolean isShowingWallpaper() {
        return false;
    }

    void notifyStateChangedCallbacks() {
    }

    void onRemoteInputActive(boolean z) {
    }

    void registerCallback(StatusBarWindowCallback statusBarWindowCallback) {
    }

    void setBackdropShowing(boolean z) {
    }

    void setBackgroundBlurRadius(int i) {
    }

    void setBouncerShowing(boolean z) {
    }

    void setDozeScreenBrightness(int i) {
    }

    void setFaceAuthDisplayBrightness(float f) {
    }

    void setForceDozeBrightness(boolean z) {
    }

    void setForcePluginOpen(boolean z, Object obj) {
    }

    void setForcePluginOpenListener(ForcePluginOpenListener forcePluginOpenListener) {
    }

    void setForceWindowCollapsed(boolean z) {
    }

    void setHeadsUpShowing(boolean z) {
    }

    void setKeyguardFadingAway(boolean z) {
    }

    void setKeyguardGoingAway(boolean z) {
    }

    void setKeyguardNeedsInput(boolean z) {
    }

    void setKeyguardOccluded(boolean z) {
    }

    void setKeyguardShowing(boolean z) {
    }

    void setLaunchingActivity(boolean z) {
    }

    void setLightRevealScrimAmount(float f) {
    }

    void setNotTouchable(boolean z) {
    }

    void setNotificationShadeFocusable(boolean z) {
    }

    void setNotificationShadeView(ViewGroup viewGroup) {
    }

    void setPanelExpanded(boolean z) {
    }

    void setPanelVisible(boolean z) {
    }

    void setQsExpanded(boolean z) {
    }

    void setRequestTopUi(boolean z, String str) {
    }

    void setScrimsVisibility(int i) {
    }

    void setScrimsVisibilityListener(Consumer<Integer> consumer) {
    }

    void setStateListener(OtherwisedCollapsedListener otherwisedCollapsedListener) {
    }

    void setWallpaperSupportsAmbientMode(boolean z) {
    }
}
