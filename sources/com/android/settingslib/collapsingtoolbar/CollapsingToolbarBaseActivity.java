package com.android.settingslib.collapsingtoolbar;

import android.app.ActionBar;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.FragmentActivity;
import com.android.settingslib.utils.BuildCompatUtils;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.resources.TextAppearanceConfig;

public class CollapsingToolbarBaseActivity extends FragmentActivity {
    private AppBarLayout mAppBarLayout;
    private CollapsingToolbarLayout mCollapsingToolbarLayout;
    private int mCustomizeLayoutResId = 0;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        if (this.mCustomizeLayoutResId <= 0 || BuildCompatUtils.isAtLeastS()) {
            TextAppearanceConfig.setShouldLoadFontSynchronously(true);
            super.setContentView(R$layout.collapsing_toolbar_base_layout);
            this.mCollapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R$id.collapsing_toolbar);
            this.mAppBarLayout = (AppBarLayout) findViewById(R$id.app_bar);
            CollapsingToolbarLayout collapsingToolbarLayout = this.mCollapsingToolbarLayout;
            if (collapsingToolbarLayout != null) {
                collapsingToolbarLayout.setLineSpacingMultiplier(1.1f);
            }
            disableCollapsingToolbarLayoutScrollingBehavior();
            setActionBar((Toolbar) findViewById(R$id.action_bar));
            ActionBar actionBar = getActionBar();
            if (actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(true);
                actionBar.setHomeButtonEnabled(true);
                actionBar.setDisplayShowTitleEnabled(true);
                return;
            }
            return;
        }
        super.setContentView(this.mCustomizeLayoutResId);
    }

    public void setContentView(int i) {
        ViewGroup viewGroup = (ViewGroup) findViewById(R$id.content_frame);
        if (viewGroup != null) {
            viewGroup.removeAllViews();
        }
        LayoutInflater.from(this).inflate(i, viewGroup);
    }

    public void setContentView(View view) {
        ViewGroup viewGroup = (ViewGroup) findViewById(R$id.content_frame);
        if (viewGroup != null) {
            viewGroup.addView(view);
        }
    }

    public void setContentView(View view, ViewGroup.LayoutParams layoutParams) {
        ViewGroup viewGroup = (ViewGroup) findViewById(R$id.content_frame);
        if (viewGroup != null) {
            viewGroup.addView(view, layoutParams);
        }
    }

    public void setTitle(CharSequence charSequence) {
        CollapsingToolbarLayout collapsingToolbarLayout = this.mCollapsingToolbarLayout;
        if (collapsingToolbarLayout != null) {
            collapsingToolbarLayout.setTitle(charSequence);
        } else {
            super.setTitle(charSequence);
        }
    }

    public void setTitle(int i) {
        CollapsingToolbarLayout collapsingToolbarLayout = this.mCollapsingToolbarLayout;
        if (collapsingToolbarLayout != null) {
            collapsingToolbarLayout.setTitle(getText(i));
        } else {
            super.setTitle(i);
        }
    }

    public boolean onNavigateUp() {
        if (super.onNavigateUp()) {
            return true;
        }
        finishAfterTransition();
        return true;
    }

    private void disableCollapsingToolbarLayoutScrollingBehavior() {
        AppBarLayout appBarLayout = this.mAppBarLayout;
        if (appBarLayout != null) {
            AppBarLayout.Behavior behavior = new AppBarLayout.Behavior();
            behavior.setDragCallback(new AppBarLayout.Behavior.DragCallback() {
                public boolean canDrag(AppBarLayout appBarLayout) {
                    return false;
                }
            });
            ((CoordinatorLayout.LayoutParams) appBarLayout.getLayoutParams()).setBehavior(behavior);
        }
    }
}
