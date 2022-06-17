package com.motorola.systemui.cli.navgesture;

import android.view.Window;
import java.util.Arrays;

public class SystemUiController {
    private final int[] mStates = new int[5];
    private final Window mWindow;

    public SystemUiController(Window window) {
        this.mWindow = window;
    }

    public void updateUiState(int i, int i2) {
        int[] iArr = this.mStates;
        if (iArr[i] != i2) {
            iArr[i] = i2;
            int systemUiVisibility = this.mWindow.getDecorView().getSystemUiVisibility();
            int i3 = systemUiVisibility;
            for (int i4 : this.mStates) {
                if ((i4 & 1) != 0) {
                    i3 |= 16;
                } else if ((i4 & 2) != 0) {
                    i3 &= -17;
                }
                if ((i4 & 4) != 0) {
                    i3 |= 8192;
                } else if ((i4 & 8) != 0) {
                    i3 &= -8193;
                }
            }
            if (i3 != systemUiVisibility) {
                this.mWindow.getDecorView().setSystemUiVisibility(i3);
            }
        }
    }

    public String toString() {
        return "mStates=" + Arrays.toString(this.mStates);
    }
}
