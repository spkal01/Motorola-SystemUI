package com.motorola.systemui.cli.navgesture.inputconsumers;

import android.view.InputMonitor;
import android.view.MotionEvent;
import com.motorola.systemui.cli.navgesture.BaseGestureActivity;
import com.motorola.systemui.cli.navgesture.IRecentsView;

public class OverviewInputConsumer<T extends BaseGestureActivity> implements InputConsumer {
    private final InputMonitor mInputMonitor;
    private final int[] mLocationOnScreen;
    private final boolean mStartingInActivityBounds;
    private final IRecentsView mTarget;

    public int getType() {
        return 2;
    }

    public OverviewInputConsumer(T t, InputMonitor inputMonitor, boolean z) {
        int[] iArr = new int[2];
        this.mLocationOnScreen = iArr;
        this.mInputMonitor = inputMonitor;
        this.mStartingInActivityBounds = z;
        IRecentsView overviewPanel = t.getOverviewPanel();
        this.mTarget = overviewPanel;
        overviewPanel.asView().getLocationOnScreen(iArr);
    }

    public void onMotionEvent(MotionEvent motionEvent) {
        int edgeFlags = motionEvent.getEdgeFlags();
        if (!this.mStartingInActivityBounds) {
            motionEvent.setEdgeFlags(edgeFlags | 256);
        }
        int[] iArr = this.mLocationOnScreen;
        motionEvent.offsetLocation((float) (-iArr[0]), (float) (-iArr[1]));
        int[] iArr2 = this.mLocationOnScreen;
        motionEvent.offsetLocation((float) iArr2[0], (float) iArr2[1]);
        motionEvent.setEdgeFlags(edgeFlags);
    }
}
