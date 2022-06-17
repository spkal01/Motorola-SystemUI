package com.android.systemui.media.dialog;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowInsets;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.core.graphics.drawable.IconCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.systemui.R$dimen;
import com.android.systemui.R$id;
import com.android.systemui.R$layout;
import com.android.systemui.R$style;
import com.android.systemui.media.dialog.MediaOutputController;
import com.android.systemui.statusbar.phone.SystemUIDialog;

public abstract class MediaOutputBaseDialog extends SystemUIDialog implements MediaOutputController.Callback {
    MediaOutputBaseAdapter mAdapter;
    final Context mContext;
    private LinearLayout mDeviceListLayout;
    private final ViewTreeObserver.OnGlobalLayoutListener mDeviceListLayoutListener = new MediaOutputBaseDialog$$ExternalSyntheticLambda3(this);
    private RecyclerView mDevicesRecyclerView;
    View mDialogView;
    private Button mDoneButton;
    private ImageView mHeaderIcon;
    private TextView mHeaderSubtitle;
    private TextView mHeaderTitle;
    private final RecyclerView.LayoutManager mLayoutManager;
    private int mListMaxHeight;
    private final Handler mMainThreadHandler = new Handler(Looper.getMainLooper());
    final MediaOutputController mMediaOutputController;
    private Button mStopButton;

    /* access modifiers changed from: package-private */
    public abstract IconCompat getHeaderIcon();

    /* access modifiers changed from: package-private */
    public abstract int getHeaderIconRes();

    /* access modifiers changed from: package-private */
    public abstract int getHeaderIconSize();

    /* access modifiers changed from: package-private */
    public abstract CharSequence getHeaderSubtitle();

    /* access modifiers changed from: package-private */
    public abstract CharSequence getHeaderText();

    /* access modifiers changed from: package-private */
    public abstract int getStopButtonVisibility();

    /* access modifiers changed from: package-private */
    public void onHeaderIconClick() {
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0() {
        if (this.mDeviceListLayout.getHeight() > this.mListMaxHeight) {
            ViewGroup.LayoutParams layoutParams = this.mDeviceListLayout.getLayoutParams();
            layoutParams.height = this.mListMaxHeight;
            this.mDeviceListLayout.setLayoutParams(layoutParams);
        }
    }

    public MediaOutputBaseDialog(Context context, MediaOutputController mediaOutputController) {
        super(context, R$style.Theme_SystemUI_Dialog_MediaOutput);
        this.mContext = context;
        this.mMediaOutputController = mediaOutputController;
        this.mLayoutManager = new LinearLayoutManager(context);
        this.mListMaxHeight = context.getResources().getDimensionPixelSize(R$dimen.media_output_dialog_list_max_height);
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.mDialogView = LayoutInflater.from(this.mContext).inflate(R$layout.media_output_dialog, (ViewGroup) null);
        Window window = getWindow();
        WindowManager.LayoutParams attributes = window.getAttributes();
        attributes.gravity = 80;
        attributes.setFitInsetsTypes(WindowInsets.Type.statusBars() | WindowInsets.Type.navigationBars());
        attributes.setFitInsetsSides(WindowInsets.Side.all());
        attributes.setFitInsetsIgnoringVisibility(true);
        window.setAttributes(attributes);
        window.setContentView(this.mDialogView);
        window.setLayout(-1, -2);
        window.setWindowAnimations(R$style.Animation_MediaOutputDialog);
        this.mHeaderTitle = (TextView) this.mDialogView.requireViewById(R$id.header_title);
        this.mHeaderSubtitle = (TextView) this.mDialogView.requireViewById(R$id.header_subtitle);
        this.mHeaderIcon = (ImageView) this.mDialogView.requireViewById(R$id.header_icon);
        this.mDevicesRecyclerView = (RecyclerView) this.mDialogView.requireViewById(R$id.list_result);
        this.mDeviceListLayout = (LinearLayout) this.mDialogView.requireViewById(R$id.device_list);
        this.mDoneButton = (Button) this.mDialogView.requireViewById(R$id.done);
        this.mStopButton = (Button) this.mDialogView.requireViewById(R$id.stop);
        this.mDeviceListLayout.getViewTreeObserver().addOnGlobalLayoutListener(this.mDeviceListLayoutListener);
        this.mDevicesRecyclerView.setLayoutManager(this.mLayoutManager);
        this.mDevicesRecyclerView.setAdapter(this.mAdapter);
        this.mHeaderIcon.setOnClickListener(new MediaOutputBaseDialog$$ExternalSyntheticLambda0(this));
        this.mDoneButton.setOnClickListener(new MediaOutputBaseDialog$$ExternalSyntheticLambda1(this));
        this.mStopButton.setOnClickListener(new MediaOutputBaseDialog$$ExternalSyntheticLambda2(this));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onCreate$1(View view) {
        onHeaderIconClick();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onCreate$2(View view) {
        dismiss();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onCreate$3(View view) {
        this.mMediaOutputController.releaseSession();
        dismiss();
    }

    public void onStart() {
        super.onStart();
        this.mMediaOutputController.start(this);
    }

    public void onStop() {
        super.onStop();
        this.mMediaOutputController.stop();
    }

    /* access modifiers changed from: package-private */
    /* renamed from: refresh */
    public void lambda$onRouteChanged$5() {
        int headerIconRes = getHeaderIconRes();
        IconCompat headerIcon = getHeaderIcon();
        if (headerIconRes != 0) {
            this.mHeaderIcon.setVisibility(0);
            this.mHeaderIcon.setImageResource(headerIconRes);
        } else if (headerIcon != null) {
            this.mHeaderIcon.setVisibility(0);
            this.mHeaderIcon.setImageIcon(headerIcon.toIcon(this.mContext));
        } else {
            this.mHeaderIcon.setVisibility(8);
        }
        if (this.mHeaderIcon.getVisibility() == 0) {
            int headerIconSize = getHeaderIconSize();
            this.mHeaderIcon.setLayoutParams(new LinearLayout.LayoutParams(this.mContext.getResources().getDimensionPixelSize(R$dimen.media_output_dialog_header_icon_padding) + headerIconSize, headerIconSize));
        }
        this.mHeaderTitle.setText(getHeaderText());
        CharSequence headerSubtitle = getHeaderSubtitle();
        if (TextUtils.isEmpty(headerSubtitle)) {
            this.mHeaderSubtitle.setVisibility(8);
            this.mHeaderTitle.setGravity(8388627);
        } else {
            this.mHeaderSubtitle.setVisibility(0);
            this.mHeaderSubtitle.setText(headerSubtitle);
            this.mHeaderTitle.setGravity(0);
        }
        if (!this.mAdapter.isDragging() && !this.mAdapter.isAnimating()) {
            int currentActivePosition = this.mAdapter.getCurrentActivePosition();
            if (currentActivePosition >= 0) {
                this.mAdapter.notifyItemChanged(currentActivePosition);
            } else {
                this.mAdapter.notifyDataSetChanged();
            }
        }
        this.mStopButton.setVisibility(getStopButtonVisibility());
    }

    public void onMediaChanged() {
        this.mMainThreadHandler.post(new MediaOutputBaseDialog$$ExternalSyntheticLambda5(this));
    }

    public void onMediaStoppedOrPaused() {
        if (isShowing()) {
            dismiss();
        }
    }

    public void onRouteChanged() {
        this.mMainThreadHandler.post(new MediaOutputBaseDialog$$ExternalSyntheticLambda4(this));
    }

    public void dismissDialog() {
        dismiss();
    }

    public void onWindowFocusChanged(boolean z) {
        super.onWindowFocusChanged(z);
        if (!z && isShowing()) {
            dismiss();
        }
    }
}
