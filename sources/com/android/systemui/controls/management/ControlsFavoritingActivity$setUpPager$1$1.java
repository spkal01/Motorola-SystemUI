package com.android.systemui.controls.management;

import android.text.TextUtils;
import android.widget.TextView;
import androidx.viewpager2.widget.ViewPager2;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: ControlsFavoritingActivity.kt */
public final class ControlsFavoritingActivity$setUpPager$1$1 extends ViewPager2.OnPageChangeCallback {
    final /* synthetic */ ControlsFavoritingActivity this$0;

    ControlsFavoritingActivity$setUpPager$1$1(ControlsFavoritingActivity controlsFavoritingActivity) {
        this.this$0 = controlsFavoritingActivity;
    }

    public void onPageSelected(int i) {
        super.onPageSelected(i);
        CharSequence structureName = ((StructureContainer) this.this$0.listOfStructures.get(i)).getStructureName();
        if (TextUtils.isEmpty(structureName)) {
            structureName = this.this$0.appName;
        }
        TextView access$getTitleView$p = this.this$0.titleView;
        if (access$getTitleView$p != null) {
            access$getTitleView$p.setText(structureName);
            TextView access$getTitleView$p2 = this.this$0.titleView;
            if (access$getTitleView$p2 != null) {
                access$getTitleView$p2.requestFocus();
            } else {
                Intrinsics.throwUninitializedPropertyAccessException("titleView");
                throw null;
            }
        } else {
            Intrinsics.throwUninitializedPropertyAccessException("titleView");
            throw null;
        }
    }

    public void onPageScrolled(int i, float f, int i2) {
        super.onPageScrolled(i, f, i2);
        ManagementPageIndicator access$getPageIndicator$p = this.this$0.pageIndicator;
        if (access$getPageIndicator$p != null) {
            access$getPageIndicator$p.setLocation(((float) i) + f);
        } else {
            Intrinsics.throwUninitializedPropertyAccessException("pageIndicator");
            throw null;
        }
    }
}
