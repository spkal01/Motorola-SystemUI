package com.android.systemui.statusbar.notification.stack;

import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.util.AttributeSet;
import com.android.systemui.statusbar.notification.row.ExpandableView;

public class MediaHeaderView extends ExpandableView {
    public void performAddAnimation(long j, long j2, boolean z) {
    }

    public long performRemoveAnimation(long j, long j2, float f, boolean z, float f2, Runnable runnable, AnimatorListenerAdapter animatorListenerAdapter) {
        return 0;
    }

    public MediaHeaderView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }
}
