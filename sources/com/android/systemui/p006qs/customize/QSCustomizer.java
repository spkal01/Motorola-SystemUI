package com.android.systemui.p006qs.customize;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.RecyclerView;
import com.android.systemui.R$drawable;
import com.android.systemui.R$fraction;
import com.android.systemui.R$id;
import com.android.systemui.R$layout;
import com.android.systemui.R$string;
import com.android.systemui.moto.MotoFeature;
import com.android.systemui.p006qs.QSDetailClipper;
import com.android.systemui.plugins.p005qs.C1129QS;
import com.android.systemui.statusbar.phone.LightBarController;
import com.android.systemui.statusbar.phone.NotificationsQuickSettingsContainer;

/* renamed from: com.android.systemui.qs.customize.QSCustomizer */
public class QSCustomizer extends LinearLayout {
    /* access modifiers changed from: private */
    public boolean isShown;
    private final QSDetailClipper mClipper;
    private final Animator.AnimatorListener mCollapseAnimationListener = new AnimatorListenerAdapter() {
        public void onAnimationEnd(Animator animator) {
            if (!QSCustomizer.this.isShown) {
                QSCustomizer.this.setVisibility(8);
            }
            QSCustomizer.this.mNotifQsContainer.setCustomizerAnimating(false);
        }

        public void onAnimationCancel(Animator animator) {
            if (!QSCustomizer.this.isShown) {
                QSCustomizer.this.setVisibility(8);
            }
            QSCustomizer.this.mNotifQsContainer.setCustomizerAnimating(false);
        }
    };
    private boolean mCustomizing;
    private boolean mIsNightMode;
    private boolean mIsPrcCustom;
    private boolean mIsShowingNavBackdrop;
    /* access modifiers changed from: private */
    public NotificationsQuickSettingsContainer mNotifQsContainer;
    /* access modifiers changed from: private */
    public boolean mOpening;
    private int mOrientation;
    /* access modifiers changed from: private */
    public C1129QS mQs;
    /* access modifiers changed from: private */
    public final RecyclerView mRecyclerView;
    private final Point mSizePoint = new Point();
    private final View mTransparentView;

    /* renamed from: mX */
    private int f119mX;

    /* renamed from: mY */
    private int f120mY;

    public boolean onTouchEvent(MotionEvent motionEvent) {
        return true;
    }

    public QSCustomizer(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        LayoutInflater.from(getContext()).inflate(R$layout.qs_customize_panel_content, this);
        this.mClipper = new QSDetailClipper(findViewById(R$id.customize_container));
        this.mIsPrcCustom = MotoFeature.getInstance(this.mContext).isCustomPanelView();
        updateToolBar();
        if (this.mIsPrcCustom) {
            this.mIsNightMode = (this.mContext.getResources().getConfiguration().uiMode & 48) == 32;
            updateBackgroundTheme();
        }
        RecyclerView recyclerView = (RecyclerView) findViewById(16908298);
        this.mRecyclerView = recyclerView;
        this.mTransparentView = findViewById(R$id.customizer_transparent_view);
        DefaultItemAnimator defaultItemAnimator = new DefaultItemAnimator();
        defaultItemAnimator.setMoveDuration(150);
        recyclerView.setItemAnimator(defaultItemAnimator);
        this.mOrientation = context.getResources().getConfiguration().orientation;
    }

    public void updateToolBar() {
        Toolbar toolbar = (Toolbar) findViewById(16908710);
        TypedValue typedValue = new TypedValue();
        this.mContext.getTheme().resolveAttribute(16843531, typedValue, true);
        if (!this.mIsPrcCustom) {
            toolbar.setNavigationIcon(getResources().getDrawable(typedValue.resourceId, this.mContext.getTheme()));
        }
        toolbar.getMenu().add(0, 1, 0, this.mContext.getString(17041326));
        toolbar.setTitle(R$string.qs_edit);
    }

    /* access modifiers changed from: package-private */
    public void updateResources() {
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) this.mTransparentView.getLayoutParams();
        layoutParams.height = this.mContext.getResources().getDimensionPixelSize(17105483);
        this.mTransparentView.setLayoutParams(layoutParams);
    }

    /* access modifiers changed from: package-private */
    public void updateNavBackDrop(Configuration configuration, LightBarController lightBarController) {
        View findViewById = findViewById(R$id.nav_bar_background);
        if (this.mIsPrcCustom) {
            findViewById.setBackground((Drawable) null);
        }
        int i = 0;
        boolean z = configuration.smallestScreenWidthDp >= 600 || configuration.orientation != 2;
        this.mIsShowingNavBackdrop = z;
        if (findViewById != null) {
            if (!z) {
                i = 8;
            }
            findViewById.setVisibility(i);
        }
        updateNavColors(lightBarController);
    }

    /* access modifiers changed from: package-private */
    public void updateNavColors(LightBarController lightBarController) {
        lightBarController.setQsCustomizing(this.mIsShowingNavBackdrop && this.isShown);
    }

    public void setContainer(NotificationsQuickSettingsContainer notificationsQuickSettingsContainer) {
        this.mNotifQsContainer = notificationsQuickSettingsContainer;
    }

    public void setQs(C1129QS qs) {
        this.mQs = qs;
    }

    /* access modifiers changed from: package-private */
    public void show(int i, int i2, TileAdapter tileAdapter) {
        if (!this.isShown) {
            int[] locationOnScreen = findViewById(R$id.customize_container).getLocationOnScreen();
            this.f119mX = i - locationOnScreen[0];
            this.f120mY = i2 - locationOnScreen[1];
            this.isShown = true;
            this.mOpening = true;
            setVisibility(0);
            this.mClipper.animateCircularClip(this.f119mX, this.f120mY, true, new ExpandAnimatorListener(tileAdapter));
            this.mNotifQsContainer.setCustomizerAnimating(true);
            this.mNotifQsContainer.setCustomizerShowing(true);
        }
    }

    /* access modifiers changed from: package-private */
    public void showImmediately() {
        if (!this.isShown) {
            setVisibility(0);
            this.mClipper.cancelAnimator();
            this.mClipper.showBackground();
            this.isShown = true;
            setCustomizing(true);
            this.mNotifQsContainer.setCustomizerAnimating(false);
            this.mNotifQsContainer.setCustomizerShowing(true);
        }
    }

    public void hide(boolean z) {
        if (this.isShown) {
            this.isShown = false;
            this.mClipper.cancelAnimator();
            this.mOpening = false;
            if (z) {
                this.mClipper.animateCircularClip(this.f119mX, this.f120mY, false, this.mCollapseAnimationListener);
            } else {
                setVisibility(8);
            }
            this.mNotifQsContainer.setCustomizerAnimating(z);
            this.mNotifQsContainer.setCustomizerShowing(false);
        }
    }

    public boolean isShown() {
        return this.isShown;
    }

    /* access modifiers changed from: package-private */
    public void setCustomizing(boolean z) {
        this.mCustomizing = z;
        this.mQs.notifyCustomizeChanged();
    }

    public boolean isCustomizing() {
        return this.mCustomizing || this.mOpening;
    }

    public void setEditLocation(int i, int i2) {
        int[] locationOnScreen = findViewById(R$id.customize_container).getLocationOnScreen();
        this.f119mX = i - locationOnScreen[0];
        this.f120mY = i2 - locationOnScreen[1];
    }

    /* renamed from: com.android.systemui.qs.customize.QSCustomizer$ExpandAnimatorListener */
    class ExpandAnimatorListener extends AnimatorListenerAdapter {
        private final TileAdapter mTileAdapter;

        ExpandAnimatorListener(TileAdapter tileAdapter) {
            this.mTileAdapter = tileAdapter;
        }

        public void onAnimationEnd(Animator animator) {
            if (QSCustomizer.this.isShown) {
                QSCustomizer.this.setCustomizing(true);
            }
            boolean unused = QSCustomizer.this.mOpening = false;
            QSCustomizer.this.mNotifQsContainer.setCustomizerAnimating(false);
            QSCustomizer.this.mRecyclerView.setAdapter(this.mTileAdapter);
        }

        public void onAnimationCancel(Animator animator) {
            boolean unused = QSCustomizer.this.mOpening = false;
            QSCustomizer.this.mQs.notifyCustomizeChanged();
            QSCustomizer.this.mNotifQsContainer.setCustomizerAnimating(false);
        }
    }

    public RecyclerView getRecyclerView() {
        return this.mRecyclerView;
    }

    public boolean isOpening() {
        return this.mOpening;
    }

    /* access modifiers changed from: protected */
    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        if (this.mIsPrcCustom) {
            this.mContext.getResources();
            boolean z = (configuration.uiMode & 48) == 32;
            if (this.mIsNightMode != z) {
                this.mIsNightMode = z;
                updateBackgroundTheme();
            }
            int i = this.mOrientation;
            int i2 = configuration.orientation;
            if (i != i2) {
                this.mOrientation = i2;
                updateRecyclerViewLayout(i2);
            }
        }
    }

    public void updateRecyclerViewLayout(int i) {
        if (this.mIsPrcCustom) {
            this.mSizePoint.set(0, 0);
            Resources resources = this.mContext.getResources();
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) this.mRecyclerView.getLayoutParams();
            if (i == 2) {
                layoutParams.width = (int) (((float) getDisplaySize(true)) * resources.getFraction(R$fraction.zz_moto_prc_customizer_view_width_precentage, 1, 1));
                layoutParams.gravity = 1;
            } else {
                layoutParams.width = -1;
            }
            this.mRecyclerView.setLayoutParams(layoutParams);
        }
    }

    private void updateBackgroundTheme() {
        Resources resources = this.mContext.getResources();
        View findViewById = findViewById(R$id.customize_container);
        TransitionDrawable transitionDrawable = (TransitionDrawable) findViewById.getBackground();
        transitionDrawable.setDrawable(1, resources.getDrawable(R$drawable.prc_qs_customizer_background));
        findViewById.setBackground(transitionDrawable);
    }

    private int getDisplaySize(boolean z) {
        if (this.mSizePoint.y == 0) {
            Display display = getDisplay();
            if (display == null) {
                display = this.mContext.getDisplay();
            }
            display.getRealSize(this.mSizePoint);
        }
        if (z) {
            return this.mSizePoint.x;
        }
        return this.mSizePoint.y;
    }
}
