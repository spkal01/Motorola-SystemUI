package com.motorola.systemui.cli.navgesture.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.Point;
import android.hardware.display.DisplayManager;
import android.util.Log;
import android.view.Display;
import com.motorola.systemui.cli.navgesture.executors.AppExecutors;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class ConfigMonitor extends BroadcastReceiver implements DisplayManager.DisplayListener {
    private Consumer<Context> mCallback;
    private final Context mContext;
    private final int mDensity;
    private final int mDisplayId;
    private Supplier<Display> mDisplaySupplier;
    private final float mFontScale;
    private final Point mLargestSize;
    private final Point mRealSize;
    private final Point mSmallestSize;
    private final Point mTmpPoint1 = new Point();
    private final Point mTmpPoint2 = new Point();

    public void onDisplayAdded(int i) {
    }

    public void onDisplayRemoved(int i) {
    }

    public ConfigMonitor(Context context, Consumer<Context> consumer, Supplier<Display> supplier) {
        this.mContext = context;
        Configuration configuration = context.getResources().getConfiguration();
        Log.d("ConfigMonitor", "ConfigMonitor#ConfigMonitor: init config = " + configuration);
        this.mFontScale = configuration.fontScale;
        this.mDensity = configuration.densityDpi;
        Display display = supplier.get();
        this.mDisplayId = display.getDisplayId();
        Point point = new Point();
        this.mRealSize = point;
        display.getRealSize(point);
        Point point2 = new Point();
        this.mSmallestSize = point2;
        Point point3 = new Point();
        this.mLargestSize = point3;
        display.getCurrentSizeRange(point2, point3);
        this.mCallback = consumer;
        this.mDisplaySupplier = supplier;
        context.registerReceiver(this, new IntentFilter("android.intent.action.CONFIGURATION_CHANGED"));
        ((DisplayManager) context.getSystemService(DisplayManager.class)).registerDisplayListener(this, AppExecutors.background().getHandler());
    }

    public void onReceive(Context context, Intent intent) {
        Configuration configuration = context.getResources().getConfiguration();
        if (this.mFontScale != configuration.fontScale || this.mDensity != configuration.densityDpi) {
            Log.d("ConfigMonitor", "Configuration changed.");
            notifyChange();
        }
    }

    public void onDisplayChanged(int i) {
        if (i == this.mDisplayId) {
            Display display = this.mDisplaySupplier.get();
            display.getRealSize(this.mTmpPoint1);
            if (!this.mRealSize.equals(this.mTmpPoint1)) {
                Point point = this.mRealSize;
                Point point2 = this.mTmpPoint1;
                if (!point.equals(point2.y, point2.x)) {
                    Log.d("ConfigMonitor", String.format("Display size changed from %s to %s", new Object[]{this.mRealSize, this.mTmpPoint1}));
                    notifyChange();
                    return;
                }
            }
            display.getCurrentSizeRange(this.mTmpPoint1, this.mTmpPoint2);
            if (!this.mSmallestSize.equals(this.mTmpPoint1) || !this.mLargestSize.equals(this.mTmpPoint2)) {
                Log.d("ConfigMonitor", String.format("Available size changed from [%s, %s] to [%s, %s]", new Object[]{this.mSmallestSize, this.mLargestSize, this.mTmpPoint1, this.mTmpPoint2}));
                notifyChange();
            }
        }
    }

    private synchronized void notifyChange() {
        Consumer<Context> consumer = this.mCallback;
        if (consumer != null) {
            this.mCallback = null;
            AppExecutors.m97ui().execute(new ConfigMonitor$$ExternalSyntheticLambda0(this, consumer));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$notifyChange$0(Consumer consumer) {
        consumer.accept(this.mContext);
    }

    public void unregister() {
        try {
            this.mContext.unregisterReceiver(this);
            ((DisplayManager) this.mContext.getSystemService(DisplayManager.class)).unregisterDisplayListener(this);
        } catch (Exception e) {
            Log.e("ConfigMonitor", "Failed to unregister config monitor", e);
        }
    }
}
