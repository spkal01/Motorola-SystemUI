package com.motorola.systemui.desktop.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.view.View;
import android.widget.AdapterView;

public class TooltipPopupManager {
    private AdapterView.OnItemClickListener mAdapterViewOnItemClickListener = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
            TooltipPopupManager.this.mOnItemClickListener.onItemClick(i);
        }
    };
    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            action.hashCode();
            if (action.equals("android.intent.action.SCREEN_OFF")) {
                TooltipPopupManager.this.mTooltipPopup.hide(true);
            }
        }
    };
    private final Context mContext;
    /* access modifiers changed from: private */
    public OnItemClickListener mOnItemClickListener;
    private long mShowingId = -1;
    /* access modifiers changed from: private */
    public final TooltipPopup mTooltipPopup;

    public interface OnItemClickListener {
        void onItemClick(int i);
    }

    public TooltipPopupManager(Context context) {
        this.mContext = context;
        this.mTooltipPopup = new TooltipPopup(context);
        context.registerReceiver(this.mBroadcastReceiver, new IntentFilter("android.intent.action.SCREEN_OFF"));
    }

    private long beginShow() {
        long j = this.mShowingId + 1;
        this.mShowingId = j;
        return j;
    }

    public synchronized long show(int i, View view, int i2, int i3, CharSequence charSequence, View.OnClickListener onClickListener) {
        long beginShow;
        synchronized (this) {
            beginShow = beginShow();
            this.mTooltipPopup.show(beginShow, i, view, i2, i3, charSequence, onClickListener);
        }
        return beginShow;
    }

    public synchronized void hide(long j, boolean z) {
        if (getShowingId() == j) {
            this.mTooltipPopup.hide(z);
        }
    }

    public synchronized long getShowingId() {
        return this.mTooltipPopup.getShowingId();
    }
}
