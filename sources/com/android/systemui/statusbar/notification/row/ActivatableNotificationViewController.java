package com.android.systemui.statusbar.notification.row;

import android.os.SystemClock;
import android.view.MotionEvent;
import android.view.View;
import android.view.accessibility.AccessibilityManager;
import com.android.systemui.Gefingerpoken;
import com.android.systemui.classifier.FalsingCollector;
import com.android.systemui.plugins.FalsingManager;
import com.android.systemui.statusbar.notification.row.ActivatableNotificationView;
import com.android.systemui.statusbar.phone.NotificationTapHelper;
import com.android.systemui.util.ViewController;
import java.util.Objects;

public class ActivatableNotificationViewController extends ViewController<ActivatableNotificationView> {
    /* access modifiers changed from: private */
    public final AccessibilityManager mAccessibilityManager;
    private final ExpandableOutlineViewController mExpandableOutlineViewController;
    /* access modifiers changed from: private */
    public final FalsingCollector mFalsingCollector;
    /* access modifiers changed from: private */
    public final FalsingManager mFalsingManager;
    private final NotificationTapHelper mNotificationTapHelper;
    private final TouchHandler mTouchHandler = new TouchHandler();

    /* access modifiers changed from: protected */
    public void onViewAttached() {
    }

    /* access modifiers changed from: protected */
    public void onViewDetached() {
    }

    public ActivatableNotificationViewController(ActivatableNotificationView activatableNotificationView, NotificationTapHelper.Factory factory, ExpandableOutlineViewController expandableOutlineViewController, AccessibilityManager accessibilityManager, FalsingManager falsingManager, FalsingCollector falsingCollector) {
        super(activatableNotificationView);
        this.mExpandableOutlineViewController = expandableOutlineViewController;
        this.mAccessibilityManager = accessibilityManager;
        this.mFalsingManager = falsingManager;
        this.mFalsingCollector = falsingCollector;
        ActivatableNotificationViewController$$ExternalSyntheticLambda0 activatableNotificationViewController$$ExternalSyntheticLambda0 = new ActivatableNotificationViewController$$ExternalSyntheticLambda0(this);
        ActivatableNotificationView activatableNotificationView2 = (ActivatableNotificationView) this.mView;
        Objects.requireNonNull(activatableNotificationView2);
        ActivatableNotificationViewController$$ExternalSyntheticLambda1 activatableNotificationViewController$$ExternalSyntheticLambda1 = new ActivatableNotificationViewController$$ExternalSyntheticLambda1(activatableNotificationView2);
        ActivatableNotificationView activatableNotificationView3 = (ActivatableNotificationView) this.mView;
        Objects.requireNonNull(activatableNotificationView3);
        this.mNotificationTapHelper = factory.create(activatableNotificationViewController$$ExternalSyntheticLambda0, activatableNotificationViewController$$ExternalSyntheticLambda1, new ActivatableNotificationViewController$$ExternalSyntheticLambda2(activatableNotificationView3));
        ((ActivatableNotificationView) this.mView).setOnActivatedListener(new ActivatableNotificationView.OnActivatedListener() {
            public void onActivationReset(ActivatableNotificationView activatableNotificationView) {
            }

            public void onActivated(ActivatableNotificationView activatableNotificationView) {
                ActivatableNotificationViewController.this.mFalsingCollector.onNotificationActive();
            }
        });
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0(boolean z) {
        if (z) {
            ((ActivatableNotificationView) this.mView).makeActive();
            this.mFalsingCollector.onNotificationActive();
            return;
        }
        ((ActivatableNotificationView) this.mView).makeInactive(true);
    }

    public void onInit() {
        this.mExpandableOutlineViewController.init();
        ((ActivatableNotificationView) this.mView).setOnTouchListener(this.mTouchHandler);
        ((ActivatableNotificationView) this.mView).setTouchHandler(this.mTouchHandler);
        ((ActivatableNotificationView) this.mView).setAccessibilityManager(this.mAccessibilityManager);
    }

    class TouchHandler implements Gefingerpoken, View.OnTouchListener {
        private boolean mBlockNextTouch;

        public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
            return false;
        }

        public boolean onTouchEvent(MotionEvent motionEvent) {
            return false;
        }

        TouchHandler() {
        }

        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (this.mBlockNextTouch) {
                this.mBlockNextTouch = false;
                return true;
            }
            if (motionEvent.getAction() == 1) {
                ((ActivatableNotificationView) ActivatableNotificationViewController.this.mView).setLastActionUpTime(SystemClock.uptimeMillis());
            }
            if (ActivatableNotificationViewController.this.mAccessibilityManager.isTouchExplorationEnabled() || motionEvent.getAction() != 1) {
                return false;
            }
            boolean isFalseTap = ActivatableNotificationViewController.this.mFalsingManager.isFalseTap(1);
            if (!isFalseTap && (view instanceof ActivatableNotificationView)) {
                ((ActivatableNotificationView) view).onTap();
            }
            return isFalseTap;
        }
    }
}
