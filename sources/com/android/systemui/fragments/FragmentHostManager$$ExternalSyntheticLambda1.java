package com.android.systemui.fragments;

import android.app.Fragment;
import com.android.systemui.fragments.FragmentHostManager;
import java.util.function.Consumer;

public final /* synthetic */ class FragmentHostManager$$ExternalSyntheticLambda1 implements Consumer {
    public final /* synthetic */ String f$0;
    public final /* synthetic */ Fragment f$1;

    public /* synthetic */ FragmentHostManager$$ExternalSyntheticLambda1(String str, Fragment fragment) {
        this.f$0 = str;
        this.f$1 = fragment;
    }

    public final void accept(Object obj) {
        ((FragmentHostManager.FragmentListener) obj).onFragmentViewDestroyed(this.f$0, this.f$1);
    }
}
