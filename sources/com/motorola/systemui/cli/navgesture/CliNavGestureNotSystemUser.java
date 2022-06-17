package com.motorola.systemui.cli.navgesture;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import com.motorola.systemui.cli.navgesture.ICliRecentsNotSystemUserCallbacks;

public class CliNavGestureNotSystemUser extends ICliRecentsNotSystemUserCallbacks.Stub {
    private final Handler mHandler = new Handler(Looper.getMainLooper()) {
        public void handleMessage(Message message) {
            int i = message.what;
            boolean z = true;
            if (i == 1) {
                CliNavGestureNotSystemUser.this.mImpl.onOverviewToggle();
            } else if (i == 2) {
                CliNavGestureNotSystemUser.this.mImpl.preloadOverView();
            } else if (i == 3) {
                int i2 = message.arg1;
                if (message.arg2 != 1) {
                    z = false;
                }
                CliNavGestureNotSystemUser.this.mImpl.setSystemUiFlag(i2, z);
            }
        }
    };
    CliNavGestureImpl mImpl;

    public CliNavGestureNotSystemUser(CliNavGestureImpl cliNavGestureImpl) {
        this.mImpl = cliNavGestureImpl;
    }

    public void onOverviewToggle() {
        this.mHandler.sendEmptyMessage(1);
    }

    public void setSystemUiFlag(int i, boolean z) {
        Message obtain = Message.obtain();
        obtain.what = 3;
        obtain.arg1 = i;
        obtain.arg2 = z ? 1 : 0;
        this.mHandler.sendMessage(obtain);
    }

    public void preloadOverView() {
        this.mHandler.sendEmptyMessage(2);
    }
}
