package com.android.p011wm.shell.splitscreen;

/* renamed from: com.android.wm.shell.splitscreen.SplitScreen */
public interface SplitScreen {

    /* renamed from: com.android.wm.shell.splitscreen.SplitScreen$SplitScreenListener */
    public interface SplitScreenListener {
        void onStagePositionChanged(int i, int i2);

        void onTaskStageChanged(int i, int i2, boolean z);
    }

    ISplitScreen createExternalInterface() {
        return null;
    }

    static String stageTypeToString(int i) {
        if (i == -1) {
            return "UNDEFINED";
        }
        if (i == 0) {
            return "MAIN";
        }
        if (i == 1) {
            return "SIDE";
        }
        return "UNKNOWN(" + i + ")";
    }
}
