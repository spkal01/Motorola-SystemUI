package com.android.keyguard;

import android.view.ViewGroup;
import com.android.systemui.util.ViewController;

public class KeyguardRootViewController extends ViewController<ViewGroup> {
    /* access modifiers changed from: protected */
    public void onViewAttached() {
    }

    /* access modifiers changed from: protected */
    public void onViewDetached() {
    }

    public KeyguardRootViewController(ViewGroup viewGroup) {
        super(viewGroup);
    }

    public ViewGroup getView() {
        return (ViewGroup) this.mView;
    }
}
