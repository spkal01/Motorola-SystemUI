package com.motorola.systemui.statusbar.policy;

public interface CellLocationController {
    void registerListeners(int i);

    void requestAreaInfo(int i);

    void setLatestBrazilAreaInfo(int i, String str);

    void unregisterListeners();
}
