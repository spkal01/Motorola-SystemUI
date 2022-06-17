package com.android.systemui.media.dialog;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowInsets;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.systemui.Dependency;
import com.android.systemui.R$dimen;
import com.android.systemui.R$id;
import com.android.systemui.R$layout;
import com.android.systemui.R$style;
import com.android.systemui.media.dialog.MediaOutputController;
import com.android.systemui.p006qs.PageIndicator;
import com.android.systemui.statusbar.phone.SystemUIDialog;
import com.android.systemui.statusbar.policy.ConfigurationController;
import com.motorola.systemui.cli.media.CliMediaNotificationController;
import com.motorola.systemui.cli.media.CliMediaViewPager;
import com.motorola.systemui.cli.media.CliMediaViewPagerOwn;

public class PRCMediaPanelWithOutput extends SystemUIDialog implements MediaOutputController.Callback, ConfigurationController.ConfigurationListener {
    MediaOutputBaseAdapter mAdapter;
    final Context mContext;
    private LinearLayout mDeviceListLayout;
    private final ViewTreeObserver.OnGlobalLayoutListener mDeviceListLayoutListener = new PRCMediaPanelWithOutput$$ExternalSyntheticLambda0(this);
    private RecyclerView mDevicesRecyclerView;
    View mDialogView;
    private int mListMaxHeight;
    private final Handler mMainThreadHandler = new Handler(Looper.getMainLooper());
    final MediaOutputController mMediaOutputController;
    private CliMediaNotificationController mPRCMediaNotificationController;
    private CliMediaViewPager mPRCMediaViewPager;
    private PageIndicator mPageIndicator;
    Resources mRes;

    public void onMediaStoppedOrPaused() {
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0() {
        if (this.mRes.getConfiguration().orientation == 1 && this.mDeviceListLayout.getHeight() > this.mListMaxHeight) {
            ViewGroup.LayoutParams layoutParams = this.mDeviceListLayout.getLayoutParams();
            layoutParams.height = this.mListMaxHeight;
            this.mDeviceListLayout.setLayoutParams(layoutParams);
        }
    }

    public PRCMediaPanelWithOutput(Context context, boolean z, MediaOutputController mediaOutputController) {
        super(context, R$style.Theme_SystemUI_Dialog_PRCMediaExpanded);
        this.mContext = context;
        this.mRes = context.getResources();
        this.mMediaOutputController = mediaOutputController;
        this.mListMaxHeight = context.getResources().getDimensionPixelSize(R$dimen.prc_media_output_dialog_list_max_height);
        this.mAdapter = new PRCMediaOutputAdapter(mediaOutputController);
        if (!z) {
            getWindow().setType(2038);
        }
        show();
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.mPRCMediaNotificationController = new CliMediaNotificationController(this.mContext, CliMediaViewPagerOwn.PRC_EXPANDED);
        addOrUpdateCustomView();
        ((ConfigurationController) Dependency.get(ConfigurationController.class)).addCallback(this);
    }

    private void addOrUpdateCustomView() {
        this.mDialogView = LayoutInflater.from(this.mContext).inflate(R$layout.prc_media_with_output_dialog, (ViewGroup) null);
        Window window = getWindow();
        WindowManager.LayoutParams attributes = window.getAttributes();
        attributes.gravity = 17;
        attributes.setFitInsetsTypes(WindowInsets.Type.statusBars() | WindowInsets.Type.navigationBars());
        attributes.setFitInsetsSides(WindowInsets.Side.all());
        attributes.setFitInsetsIgnoringVisibility(true);
        window.setAttributes(attributes);
        window.setContentView(this.mDialogView);
        window.setLayout(-1, -2);
        window.setWindowAnimations(R$style.PRCMediaExpandedAnim);
        if (this.mRes.getConfiguration().orientation == 2) {
            window.setLayout(-2, -2);
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.mDialogView.getLayoutParams();
            layoutParams.width = this.mRes.getDimensionPixelSize(R$dimen.prc_media_expanded_horizontal_width);
            this.mDialogView.setLayoutParams(layoutParams);
        }
        this.mDevicesRecyclerView = (RecyclerView) this.mDialogView.requireViewById(R$id.list_result);
        this.mDeviceListLayout = (LinearLayout) this.mDialogView.requireViewById(R$id.device_list);
        this.mPRCMediaViewPager = (CliMediaViewPager) this.mDialogView.findViewById(R$id.prc_clock_media_panel);
        this.mPageIndicator = (PageIndicator) this.mDialogView.findViewById(R$id.prc_clock_media_panel_indicator);
        this.mPRCMediaViewPager.setMediaViewPagerOwn(CliMediaViewPagerOwn.PRC_EXPANDED);
        this.mPRCMediaNotificationController.setCliViewPager(this.mPRCMediaViewPager);
        this.mPRCMediaViewPager.setIsOnKeyguard(true);
        this.mPRCMediaViewPager.setPageIndicator(this.mPageIndicator);
        this.mDeviceListLayout.getViewTreeObserver().addOnGlobalLayoutListener(this.mDeviceListLayoutListener);
        this.mDevicesRecyclerView.setLayoutManager(new LinearLayoutManager(this.mContext));
        this.mDevicesRecyclerView.setAdapter(this.mAdapter);
    }

    public void onStart() {
        super.onStart();
        this.mMediaOutputController.start(this);
    }

    public void onStop() {
        super.onStop();
        this.mMediaOutputController.stop();
    }

    public void dismiss() {
        super.dismiss();
        if (this.mPRCMediaNotificationController != null) {
            Log.d("PRCMediaPanelWithOutput", "dismiss: Recycle controller.");
            this.mPRCMediaNotificationController.recycle();
            this.mPRCMediaNotificationController = null;
        }
        ((ConfigurationController) Dependency.get(ConfigurationController.class)).removeCallback(this);
    }

    /* access modifiers changed from: package-private */
    /* renamed from: refresh */
    public void lambda$onRouteChanged$2() {
        if (!this.mAdapter.isDragging() && !this.mAdapter.isAnimating()) {
            int currentActivePosition = this.mAdapter.getCurrentActivePosition();
            if (currentActivePosition >= 0) {
                this.mAdapter.notifyItemChanged(currentActivePosition);
            } else {
                this.mAdapter.notifyDataSetChanged();
            }
        }
    }

    public void onMediaChanged() {
        this.mMainThreadHandler.post(new PRCMediaPanelWithOutput$$ExternalSyntheticLambda2(this));
    }

    public void onRouteChanged() {
        this.mMainThreadHandler.post(new PRCMediaPanelWithOutput$$ExternalSyntheticLambda1(this));
    }

    public void dismissDialog() {
        dismiss();
    }

    public void onWindowFocusChanged(boolean z) {
        super.onWindowFocusChanged(z);
    }

    public void onConfigChanged(Configuration configuration) {
        Log.i("PRCMediaPanelWithOutput", "onConfigChanged");
        addOrUpdateCustomView();
    }
}
