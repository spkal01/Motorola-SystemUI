package com.android.systemui.statusbar.notification.row;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import com.android.systemui.R$id;
import com.android.systemui.statusbar.phone.HeadsUpManagerPhone;
import com.android.systemui.statusbar.policy.HeadsUpManager;

public class CliHeadsUpView extends FrameLayout {
    protected NotificationContentView mCliPrivateLayout;
    protected HeadsUpManagerPhone mHeadsUpManager;

    public CliHeadsUpView(Context context) {
        this(context, (AttributeSet) null);
    }

    public CliHeadsUpView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        super.onFinishInflate();
        this.mCliPrivateLayout = (NotificationContentView) findViewById(R$id.expanded);
    }

    public void setOnDoubleClick(OnDoubleClickListener onDoubleClickListener) {
        setOnClickListener(onDoubleClickListener);
    }

    public void dismissWithSwipe() {
        ExpandableNotificationRow containingNotification = this.mCliPrivateLayout.getContainingNotification();
        if (containingNotification != null && !containingNotification.isRemoved()) {
            HeadsUpManagerPhone headsUpManagerPhone = this.mHeadsUpManager;
            if (headsUpManagerPhone != null) {
                headsUpManagerPhone.addSwipedOutNotification(containingNotification.getEntry().getSbn().getKey());
            }
            containingNotification.performDismiss(false);
        }
    }

    public ExpandableNotificationRow getContainingNotification() {
        return this.mCliPrivateLayout.getContainingNotification();
    }

    public void setHeadsUpManager(HeadsUpManager headsUpManager) {
        if (headsUpManager instanceof HeadsUpManagerPhone) {
            this.mHeadsUpManager = (HeadsUpManagerPhone) headsUpManager;
        }
    }

    public static abstract class OnDoubleClickListener implements View.OnClickListener {
        private long mLastClickTime;

        public abstract void onDoubleClick(View view);

        public void onClick(View view) {
            long currentTimeMillis = System.currentTimeMillis();
            if (currentTimeMillis - this.mLastClickTime < 300) {
                this.mLastClickTime = 0;
                onDoubleClick(view);
                return;
            }
            this.mLastClickTime = currentTimeMillis;
        }
    }
}
