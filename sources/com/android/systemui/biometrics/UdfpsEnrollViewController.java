package com.android.systemui.biometrics;

import android.graphics.PointF;
import com.android.systemui.R$integer;
import com.android.systemui.biometrics.UdfpsEnrollHelper;
import com.android.systemui.dump.DumpManager;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.statusbar.phone.StatusBar;
import java.io.FileDescriptor;
import java.io.PrintWriter;

public class UdfpsEnrollViewController extends UdfpsAnimationViewController<UdfpsEnrollView> {
    private boolean mEnableEnrollProgress = getResources().getBoolean(17891840);
    private final UdfpsEnrollHelper mEnrollHelper;
    private final UdfpsEnrollHelper.Listener mEnrollHelperListener = new UdfpsEnrollHelper.Listener() {
        public void onEnrollmentProgress(int i, int i2) {
            ((UdfpsEnrollView) UdfpsEnrollViewController.this.mView).onEnrollmentProgress(i, i2);
        }

        public void onLastStepAcquired() {
            ((UdfpsEnrollView) UdfpsEnrollViewController.this.mView).onLastStepAcquired();
        }
    };
    private final int mEnrollProgressBarRadius = getContext().getResources().getInteger(R$integer.config_udfpsEnrollProgressBar);

    /* access modifiers changed from: package-private */
    public String getTag() {
        return "UdfpsEnrollViewController";
    }

    public /* bridge */ /* synthetic */ void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        super.dump(fileDescriptor, printWriter, strArr);
    }

    protected UdfpsEnrollViewController(UdfpsEnrollView udfpsEnrollView, UdfpsEnrollHelper udfpsEnrollHelper, StatusBarStateController statusBarStateController, StatusBar statusBar, DumpManager dumpManager) {
        super(udfpsEnrollView, statusBarStateController, statusBar, dumpManager);
        this.mEnrollHelper = udfpsEnrollHelper;
        ((UdfpsEnrollView) this.mView).setEnrollHelper(udfpsEnrollHelper);
    }

    /* access modifiers changed from: protected */
    public void onViewAttached() {
        super.onViewAttached();
        if (this.mEnrollHelper.shouldShowProgressBar()) {
            this.mEnrollHelper.setListener(this.mEnrollHelperListener);
        }
    }

    public PointF getTouchTranslation() {
        if (!this.mEnrollHelper.isCenterEnrollmentComplete()) {
            return new PointF(0.0f, 0.0f);
        }
        return this.mEnrollHelper.getNextGuidedEnrollmentPoint();
    }

    public int getPaddingX() {
        if (!this.mEnableEnrollProgress) {
            return super.getPaddingX();
        }
        return this.mEnrollProgressBarRadius;
    }

    public int getPaddingY() {
        if (!this.mEnableEnrollProgress) {
            return super.getPaddingY();
        }
        return this.mEnrollProgressBarRadius;
    }
}
