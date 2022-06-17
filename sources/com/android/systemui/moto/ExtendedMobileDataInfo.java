package com.android.systemui.moto;

import android.content.Context;
import com.android.systemui.statusbar.policy.NetworkController;

public class ExtendedMobileDataInfo {
    private static final NetworkController.IconState EMPTY_ICON_STATE = new NetworkController.IconState(false, 0, "");
    public NetworkController.IconState activityIcon;
    public NetworkController.IconState activityIconForQSCarrier;
    public boolean enableActivityIconOnSB = false;
    public boolean enableCustomActivityIconOnQS = false;
    public boolean enableCustomize = false;
    public boolean isShowAttRat;
    public boolean isShowVzwRat;
    public boolean mDisableDataSaver = false;
    public boolean mQsIn;
    public boolean mQsOut;
    public boolean mobileShowMobileWhenWifiActive = false;
    public int rejectCode = 0;
    public NetworkController.IconState roamIcon;
    public boolean showSeparatedSignalBars = false;
    public int slotId = 0;
    public Context subContext;
    public int typeIconForQSCarrier;

    public ExtendedMobileDataInfo() {
        NetworkController.IconState iconState = EMPTY_ICON_STATE;
        this.roamIcon = iconState;
        this.activityIcon = iconState;
        this.typeIconForQSCarrier = 0;
        this.activityIconForQSCarrier = iconState;
    }

    public void resetIcons() {
        NetworkController.IconState iconState = EMPTY_ICON_STATE;
        this.roamIcon = iconState;
        this.activityIcon = iconState;
        this.activityIconForQSCarrier = iconState;
    }

    public void clearActivityIcon() {
        NetworkController.IconState iconState = EMPTY_ICON_STATE;
        this.activityIcon = iconState;
        this.activityIconForQSCarrier = iconState;
    }
}
