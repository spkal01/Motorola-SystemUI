package com.motorola.systemui.desktop.overwrites.statusbar;

import android.view.View;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.statusbar.SysuiStatusBarStateController;

public class DesktopStatusBarStateControllerImpl implements SysuiStatusBarStateController {
    public void addCallback(StatusBarStateController.StateListener stateListener) {
    }

    public void addCallback(StatusBarStateController.StateListener stateListener, int i) {
    }

    public boolean fromShadeLocked() {
        return false;
    }

    public int getCurrentOrUpcomingState() {
        return 0;
    }

    public float getDozeAmount() {
        return 0.0f;
    }

    public float getInterpolatedDozeAmount() {
        return 0.0f;
    }

    public int getState() {
        return 0;
    }

    public boolean goingToFullShade() {
        return false;
    }

    public boolean isDozing() {
        return false;
    }

    public boolean isExpanded() {
        return true;
    }

    public boolean isKeyguardRequested() {
        return false;
    }

    public boolean isPulsing() {
        return false;
    }

    public boolean leaveOpenOnKeyguardHide() {
        return false;
    }

    public void removeCallback(StatusBarStateController.StateListener stateListener) {
    }

    public void setAndInstrumentDozeAmount(View view, float f, boolean z) {
    }

    public void setFullscreenState(boolean z) {
    }

    public boolean setIsDozing(boolean z) {
        return true;
    }

    public void setKeyguardRequested(boolean z) {
    }

    public void setLeaveOpenOnKeyguardHide(boolean z) {
    }

    public boolean setPanelExpanded(boolean z) {
        return true;
    }

    public void setPulsing(boolean z) {
    }

    public boolean setState(int i) {
        return true;
    }

    public boolean setState(int i, boolean z) {
        return true;
    }

    public void setUpcomingState(int i) {
    }
}
