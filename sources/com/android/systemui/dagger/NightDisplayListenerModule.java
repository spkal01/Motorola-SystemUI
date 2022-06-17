package com.android.systemui.dagger;

import android.content.Context;
import android.hardware.display.NightDisplayListener;
import android.os.Handler;

public class NightDisplayListenerModule {
    public NightDisplayListener provideNightDisplayListener(Context context, Handler handler) {
        return new NightDisplayListener(context, handler);
    }

    public static class Builder {
        private final Handler mBgHandler;
        private final Context mContext;
        private int mUserId = 0;

        public Builder(Context context, Handler handler) {
            this.mContext = context;
            this.mBgHandler = handler;
        }

        public Builder setUser(int i) {
            this.mUserId = i;
            return this;
        }

        public NightDisplayListener build() {
            return new NightDisplayListener(this.mContext, this.mUserId, this.mBgHandler);
        }
    }
}
