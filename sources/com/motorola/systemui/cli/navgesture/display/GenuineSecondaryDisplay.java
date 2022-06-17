package com.motorola.systemui.cli.navgesture.display;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Point;
import android.graphics.Rect;
import android.hardware.display.DisplayManager;
import android.view.Display;
import android.view.DisplayInfo;
import androidx.annotation.Keep;
import com.android.systemui.moto.MotoFeature;
import com.motorola.systemui.cli.navgesture.executors.AppExecutors;
import com.motorola.systemui.cli.navgesture.util.ResourceUtils;
import java.util.ArrayList;

@Keep
public class GenuineSecondaryDisplay implements SecondaryDisplay, DisplayManager.DisplayListener {
    private static final int CLI_DISPLAY = 1;
    private static final String PERMISSION_ACCESS_CLI = "com.motorola.hardware.ACCESS_CLI";
    private static final String TAG = "SecondaryDisplay";
    private Rect mAppFrame;
    private final Display mDisplay;
    private DisplayInfo mDisplayInfo;
    private final ArrayList<DisplayInfoChangeListener> mListeners = new ArrayList<>();
    private final Point mNavBarSize;

    public int getDisplayId() {
        return 1;
    }

    public /* bridge */ /* synthetic */ boolean isMockDisplay() {
        return super.isMockDisplay();
    }

    public /* bridge */ /* synthetic */ Configuration newDisplayConfiguration(Context context) {
        return super.newDisplayConfiguration(context);
    }

    public void onDisplayAdded(int i) {
    }

    public void onDisplayRemoved(int i) {
    }

    public GenuineSecondaryDisplay(Context context) {
        DisplayManager displayManager = (DisplayManager) MotoFeature.getCliContext(context).getSystemService(DisplayManager.class);
        this.mDisplay = displayManager.getDisplay(1);
        displayManager.registerDisplayListener(this, AppExecutors.background().getHandler());
        Resources resources = newDisplayContext(context).getResources();
        this.mNavBarSize = new Point(ResourceUtils.getNavbarSize("navigation_bar_width", resources), ResourceUtils.getNavbarSize("navigation_bar_gesture_height", resources));
    }

    public boolean hasSecondaryDisplaySupport(Context context) {
        return context.checkSelfPermission(PERMISSION_ACCESS_CLI) == 0;
    }

    public int getDensity() {
        ensureDisplayInfo();
        return this.mDisplayInfo.logicalDensityDpi;
    }

    public int getDisplayWidth() {
        ensureDisplayInfo();
        return this.mDisplayInfo.logicalWidth;
    }

    public int getDisplayHeight() {
        ensureDisplayInfo();
        return this.mDisplayInfo.logicalHeight;
    }

    public int getRotation() {
        return this.mDisplay.getRotation();
    }

    public Point getDisplaySize() {
        ensureDisplayInfo();
        return new Point(getDisplayWidth(), getDisplayHeight());
    }

    public Context newDisplayContext(Context context) {
        return context.createDisplayContext(this.mDisplay);
    }

    public boolean isValid() {
        Display display = this.mDisplay;
        return display != null && display.isValid();
    }

    public Rect getAppAvailableRect() {
        if (this.mAppFrame == null) {
            Rect rect = new Rect();
            this.mAppFrame = rect;
            this.mDisplay.getRectSize(rect);
        }
        return new Rect(this.mAppFrame);
    }

    public Point navBarSize() {
        return new Point(this.mNavBarSize);
    }

    public void getCurrentSizeRange(Point point, Point point2) {
        this.mDisplay.getCurrentSizeRange(point, point2);
    }

    private void ensureDisplayInfo() {
        if (this.mDisplayInfo == null) {
            DisplayInfo displayInfo = new DisplayInfo();
            this.mDisplayInfo = displayInfo;
            this.mDisplay.getDisplayInfo(displayInfo);
        }
    }

    public void addChangeListener(DisplayInfoChangeListener displayInfoChangeListener) {
        this.mListeners.add(displayInfoChangeListener);
    }

    public void removeChangeListener(DisplayInfoChangeListener displayInfoChangeListener) {
        this.mListeners.remove(displayInfoChangeListener);
    }

    public void onDisplayChanged(int i) {
        if (i == this.mDisplay.getDisplayId()) {
            AppExecutors.m97ui().execute(new GenuineSecondaryDisplay$$ExternalSyntheticLambda0(this));
        }
    }

    /* access modifiers changed from: private */
    public void onChange() {
        for (int size = this.mListeners.size() - 1; size >= 0; size--) {
            this.mListeners.get(size).onDisplayInfoChanged(this);
        }
    }
}
