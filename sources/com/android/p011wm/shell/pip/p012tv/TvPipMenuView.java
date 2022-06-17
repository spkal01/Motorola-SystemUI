package com.android.p011wm.shell.pip.p012tv;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.app.PendingIntent;
import android.app.RemoteAction;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.IWindow;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.SurfaceControl;
import android.view.View;
import android.view.ViewRootImpl;
import android.view.WindowManagerGlobal;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import com.android.p011wm.shell.C2219R;
import java.util.ArrayList;
import java.util.List;

/* renamed from: com.android.wm.shell.pip.tv.TvPipMenuView */
public class TvPipMenuView extends FrameLayout implements View.OnClickListener {
    private final LinearLayout mActionButtonsContainer;
    private final List<TvPipMenuActionButton> mAdditionalButtons;
    private final Animator mFadeInAnimation;
    private final Animator mFadeOutAnimation;
    private Listener mListener;

    /* renamed from: com.android.wm.shell.pip.tv.TvPipMenuView$Listener */
    interface Listener {
        void onBackPress();

        void onCloseButtonClick();

        void onFullscreenButtonClick();
    }

    public TvPipMenuView(Context context) {
        this(context, (AttributeSet) null);
    }

    public TvPipMenuView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public TvPipMenuView(Context context, AttributeSet attributeSet, int i) {
        this(context, attributeSet, i, 0);
    }

    public TvPipMenuView(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        this.mAdditionalButtons = new ArrayList();
        FrameLayout.inflate(context, C2219R.layout.tv_pip_menu, this);
        LinearLayout linearLayout = (LinearLayout) findViewById(C2219R.C2222id.tv_pip_menu_action_buttons);
        this.mActionButtonsContainer = linearLayout;
        linearLayout.findViewById(C2219R.C2222id.tv_pip_menu_fullscreen_button).setOnClickListener(this);
        linearLayout.findViewById(C2219R.C2222id.tv_pip_menu_close_button).setOnClickListener(this);
        Animator loadAnimator = AnimatorInflater.loadAnimator(this.mContext, C2219R.anim.tv_pip_menu_fade_in_animation);
        this.mFadeInAnimation = loadAnimator;
        loadAnimator.setTarget(linearLayout);
        Animator loadAnimator2 = AnimatorInflater.loadAnimator(this.mContext, C2219R.anim.tv_pip_menu_fade_out_animation);
        this.mFadeOutAnimation = loadAnimator2;
        loadAnimator2.setTarget(linearLayout);
    }

    /* access modifiers changed from: package-private */
    public void setListener(Listener listener) {
        this.mListener = listener;
    }

    /* access modifiers changed from: package-private */
    public void show() {
        Log.d("TvPipMenuView", "show()");
        this.mFadeInAnimation.start();
        setAlpha(1.0f);
        grantWindowFocus(true);
    }

    /* access modifiers changed from: package-private */
    public void hide() {
        Log.d("TvPipMenuView", "hide()");
        this.mFadeOutAnimation.start();
        setAlpha(0.0f);
        grantWindowFocus(false);
    }

    /* access modifiers changed from: package-private */
    public boolean isVisible() {
        return getAlpha() == 1.0f;
    }

    private void grantWindowFocus(boolean z) {
        Log.d("TvPipMenuView", "grantWindowFocus(" + z + ")");
        try {
            WindowManagerGlobal.getWindowSession().grantEmbeddedWindowFocus((IWindow) null, getViewRootImpl().getInputToken(), z);
        } catch (Exception e) {
            Log.e("TvPipMenuView", "Unable to update focus", e);
        }
    }

    /* access modifiers changed from: package-private */
    public void setAdditionalActions(List<RemoteAction> list, Handler handler) {
        Log.d("TvPipMenuView", "setAdditionalActions()");
        int size = list.size();
        int size2 = this.mAdditionalButtons.size();
        if (size > size2) {
            LayoutInflater from = LayoutInflater.from(this.mContext);
            while (size > size2) {
                TvPipMenuActionButton tvPipMenuActionButton = (TvPipMenuActionButton) from.inflate(C2219R.layout.tv_pip_menu_additional_action_button, this.mActionButtonsContainer, false);
                tvPipMenuActionButton.setOnClickListener(this);
                this.mActionButtonsContainer.addView(tvPipMenuActionButton);
                this.mAdditionalButtons.add(tvPipMenuActionButton);
                size2++;
            }
        } else if (size < size2) {
            while (size < size2) {
                View view = this.mAdditionalButtons.get(size2 - 1);
                view.setVisibility(8);
                view.setTag((Object) null);
                size2--;
            }
        }
        for (int i = 0; i < size; i++) {
            RemoteAction remoteAction = list.get(i);
            TvPipMenuActionButton tvPipMenuActionButton2 = this.mAdditionalButtons.get(i);
            tvPipMenuActionButton2.setVisibility(0);
            tvPipMenuActionButton2.setTextAndDescription(remoteAction.getContentDescription());
            tvPipMenuActionButton2.setEnabled(remoteAction.isEnabled());
            tvPipMenuActionButton2.setAlpha(remoteAction.isEnabled() ? 1.0f : 0.54f);
            tvPipMenuActionButton2.setTag(remoteAction);
            remoteAction.getIcon().loadDrawableAsync(this.mContext, new TvPipMenuView$$ExternalSyntheticLambda0(tvPipMenuActionButton2), handler);
        }
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$setAdditionalActions$0(TvPipMenuActionButton tvPipMenuActionButton, Drawable drawable) {
        drawable.setTint(-1);
        tvPipMenuActionButton.setImageDrawable(drawable);
    }

    /* access modifiers changed from: package-private */
    public SurfaceControl getWindowSurfaceControl() {
        SurfaceControl surfaceControl;
        ViewRootImpl viewRootImpl = getViewRootImpl();
        if (viewRootImpl == null || (surfaceControl = viewRootImpl.getSurfaceControl()) == null || !surfaceControl.isValid()) {
            return null;
        }
        return surfaceControl;
    }

    public void onClick(View view) {
        if (this.mListener != null) {
            int id = view.getId();
            if (id == C2219R.C2222id.tv_pip_menu_fullscreen_button) {
                this.mListener.onFullscreenButtonClick();
            } else if (id == C2219R.C2222id.tv_pip_menu_close_button) {
                this.mListener.onCloseButtonClick();
            } else {
                RemoteAction remoteAction = (RemoteAction) view.getTag();
                if (remoteAction != null) {
                    try {
                        remoteAction.getActionIntent().send();
                    } catch (PendingIntent.CanceledException e) {
                        Log.w("TvPipMenuView", "Failed to send action", e);
                    }
                } else {
                    Log.w("TvPipMenuView", "RemoteAction is null");
                }
            }
        }
    }

    public boolean dispatchKeyEvent(KeyEvent keyEvent) {
        Listener listener;
        if (keyEvent.getAction() != 1 || keyEvent.getKeyCode() != 4 || (listener = this.mListener) == null) {
            return super.dispatchKeyEvent(keyEvent);
        }
        listener.onBackPress();
        return true;
    }
}
