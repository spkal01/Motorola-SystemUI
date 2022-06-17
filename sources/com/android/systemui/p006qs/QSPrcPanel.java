package com.android.systemui.p006qs;

import android.animation.ArgbEvaluator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.ContentObserver;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.RippleDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.provider.Settings;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import com.android.settingslib.Utils;
import com.android.systemui.Dependency;
import com.android.systemui.Dumpable;
import com.android.systemui.R$attr;
import com.android.systemui.R$color;
import com.android.systemui.R$dimen;
import com.android.systemui.R$drawable;
import com.android.systemui.R$fraction;
import com.android.systemui.R$id;
import com.android.systemui.moto.DesktopFeature;
import com.android.systemui.moto.DualSimIconController;
import com.android.systemui.moto.MotoFeature;
import com.android.systemui.plugins.FalsingManager;
import com.android.systemui.shared.system.QuickStepContract;
import com.motorola.systemui.cli.media.CliMediaViewPagerOwn;
import com.motorola.systemui.prc.media.MediaTileController;
import com.motorola.systemui.prc.media.MediaTileLayout;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/* renamed from: com.android.systemui.qs.QSPrcPanel */
public class QSPrcPanel extends FrameLayout implements Dumpable {
    static final boolean DEBUG = (!Build.IS_USER);
    private int mActiveSubsCount;
    private boolean mAirplaneMode;
    private Button mAutoBtn;
    private int mAutoBtnMarginStart;
    private int mAutoBtnSize;
    private int mAutoTextDisableColor;
    private int mAutoTextEnableColor;
    private int mBrightnessMarginStart;
    private ContentObserver mBrightnessObserver;
    private int mBrightnessTopMargin;
    protected View mBrightnessView;
    private int mBrightnessWidth;
    private DualSimIconController.Callback mCallback = new DualSimIconController.Callback() {
        public void onActiveSubsCountChanged(int i) {
            QSPrcPanel.this.updateActiveSubsCount(i);
        }

        public void onAirplaneModeChanged(boolean z) {
            QSPrcPanel.this.updateAirplaneMode(z);
        }
    };
    private final ValueAnimator.AnimatorUpdateListener mColorUpdater = new QSPrcPanel$$ExternalSyntheticLambda0(this);
    protected final Context mContext;
    private int mDisableColor;
    private View mDivider;
    private int mDividerHeight;
    private int mDividerMarginStart;
    private int mEnableColor;
    /* access modifiers changed from: private */
    public FalsingManager mFalsingManager;
    private View mFixedPanel;
    private int mFixedTilePanelMarginEnd;
    private int mFixedTilePanelMarginStart;
    private int mFooterMargin;
    private int mFooterMarginStart;
    private int mFooterPaddingVertical;
    private int mFooterTopMargin;
    private int mFooterViewHeight;
    private int mFooterViewWidth;
    private boolean mIsAuto;
    private final boolean mIsDestkop;
    private View mMediaFixedPanel;
    private View mMediaPanel;
    private MediaTileController mMediaTileController;
    private MediaTileLayout mMediaTileLayout;
    private int mMediaTilePanelHeightDeskTop;
    private int mMediaTilePanelHeightLand;
    private int mMediaTilePanelHeightPort;
    private int mMediaTilePanelMarginStart;
    private int mMediaTilePanelTopMargin;
    private int mMediaTilePanelWidth;
    private int mNavBarHeight;
    private final List<OnConfigurationChangedListener> mOnConfigurationChangedListeners = new ArrayList();
    private int mOrientation;
    private View mQSFooterView;
    private int mQSoffestHeight;
    private int mScreenHeight;
    private int mScreenWidth;
    private UnfixedPanelScrollView mScrollView;
    private View.OnTouchListener mScrollViewTouchListener = new View.OnTouchListener() {
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (motionEvent.getActionMasked() != 1) {
                return false;
            }
            QSPrcPanel.this.mFalsingManager.isFalseTouch(15);
            return false;
        }
    };
    private ViewGroup mSecurityFooterContainer;
    private boolean mShowMotoQSCarrierGroup;
    private int mSideMargins;
    private final Point mSizePoint = new Point();
    private PrcUnfixedTileLayout mUnfixedTilePanel;
    private int mUnfixedTileScrollViewHeight;
    private int mUnfixedTileScrollViewMarginStart;
    private int mUnfixedTileScrollViewTopMarin;
    private int mUnfixedTileScrollViewWidth;
    private ValueAnimator mValueAnimation = new ValueAnimator();

    /* renamed from: com.android.systemui.qs.QSPrcPanel$OnConfigurationChangedListener */
    interface OnConfigurationChangedListener {
        void onConfigurationChange(Configuration configuration);
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
    }

    public void initialize() {
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0(ValueAnimator valueAnimator) {
        setAutoBtnColor(((Integer) valueAnimator.getAnimatedValue("background")).intValue(), ((Integer) valueAnimator.getAnimatedValue("text")).intValue());
    }

    public QSPrcPanel(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mContext = context;
        this.mIsDestkop = DesktopFeature.isDesktopDisplayContext(context);
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        super.onFinishInflate();
        if (MotoFeature.getInstance(this.mContext).isCustomPanelView()) {
            this.mMediaFixedPanel = findViewById(R$id.prc_media_fixed_panel);
            this.mScrollView = (UnfixedPanelScrollView) findViewById(R$id.prc_qs_tile_scroll_view);
            this.mUnfixedTilePanel = (PrcUnfixedTileLayout) findViewById(R$id.prc_unfixed_qs_panel);
            this.mFixedPanel = findViewById(R$id.qs_prc_fixed_tile_panel);
            this.mSecurityFooterContainer = (ViewGroup) findViewById(R$id.prc_security_footer_container);
            View findViewById = findViewById(R$id.qs_prc_media_panel);
            this.mMediaPanel = findViewById;
            this.mMediaTileLayout = (MediaTileLayout) findViewById.findViewById(R$id.media_tile_layout);
            Button button = (Button) findViewById(R$id.auto_btn);
            this.mAutoBtn = button;
            button.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    if (!QSPrcPanel.this.mFalsingManager.isFalseTap(1)) {
                        QSPrcPanel.this.updateBrightnessMode();
                    }
                }
            });
            this.mDivider = findViewById(R$id.qs_prc_divider);
            this.mOrientation = getContext().getResources().getConfiguration().orientation;
            updateResources();
            this.mScrollView.addOnLayoutChangeListener(new QSPrcPanel$$ExternalSyntheticLambda3(this));
            this.mUnfixedTilePanel.addOnLayoutChangeListener(new QSPrcPanel$$ExternalSyntheticLambda1(this));
            this.mSecurityFooterContainer.addOnLayoutChangeListener(new QSPrcPanel$$ExternalSyntheticLambda2(this));
            if (needUpdateTopPaddingForCarrierGroup()) {
                lambda$updateAirplaneMode$6();
            }
            updateColors();
            this.mValueAnimation.setDuration(350);
            this.mValueAnimation.addUpdateListener(this.mColorUpdater);
            this.mBrightnessObserver = new BrightnessModeObserver(new Handler(this.mContext.getMainLooper()));
            updateBrightnessState();
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onFinishInflate$1(View view, int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8) {
        updateScrollViewBounds();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onFinishInflate$2(View view, int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8) {
        if (this.mOrientation == 1 && i8 != i4) {
            updateResources();
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onFinishInflate$3(View view, int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8) {
        if (this.mOrientation == 1 && i8 != i4) {
            updateResources();
        }
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (MotoFeature.getInstance(this.mContext).isCustomPanelView()) {
            if (this.mMediaTileController != null) {
                Log.d("QSPrcPanel", "onAttachedToWindow: Recycle controller.");
                this.mMediaTileController.recycle();
                this.mMediaTileController = null;
            }
            if (DEBUG) {
                Log.d("QSPrcPanel", "onAttachedToWindow: Media tile controller init.");
            }
            this.mMediaTileController = new MediaTileController(this.mMediaTileLayout, CliMediaViewPagerOwn.PRC_TILE);
            updateResources();
            registerBrightnessModeObserver();
            this.mScrollView.setScrollingEnabled(this.mOrientation == 2);
            addActiveSubsCallback();
        }
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (MotoFeature.getInstance(this.mContext).isCustomPanelView()) {
            if (this.mMediaTileController != null) {
                Log.d("QSPrcPanel", "onDetachedFromWindow: Recycle controller.");
                this.mMediaTileController.recycle();
                this.mMediaTileController = null;
            }
            unRegisterBrightnessModeObserver();
            removeActiveSubsCallback();
        }
    }

    public TileLayout getUnfixedTileLayout() {
        return this.mUnfixedTilePanel;
    }

    public void scrollToStart() {
        UnfixedPanelScrollView unfixedPanelScrollView = this.mScrollView;
        if (unfixedPanelScrollView != null) {
            unfixedPanelScrollView.scrollTo(0, 0);
        }
    }

    public void setQSFooterView(View view) {
        View view2 = this.mQSFooterView;
        if (view2 != null) {
            removeView(view2);
            this.mQSFooterView = null;
        }
        ViewGroup viewGroup = (ViewGroup) view.getParent();
        if (viewGroup != null) {
            viewGroup.removeView(view);
        }
        addView(view);
        this.mQSFooterView = view;
        updateResources();
    }

    public void removeQSFooter(View view) {
        ViewGroup viewGroup = (ViewGroup) view.getParent();
        if (viewGroup != null) {
            viewGroup.removeView(view);
        }
        this.mQSFooterView = null;
    }

    public void setFalsingManager(FalsingManager falsingManager) {
        this.mFalsingManager = falsingManager;
        this.mScrollView.setOnTouchListener(this.mScrollViewTouchListener);
        this.mMediaTileLayout.setFalsingManager(this.mFalsingManager);
    }

    private void registerBrightnessModeObserver() {
        if (this.mBrightnessObserver != null) {
            this.mContext.getContentResolver().registerContentObserver(Settings.System.getUriFor("screen_brightness_mode"), false, this.mBrightnessObserver, -1);
        }
    }

    private void unRegisterBrightnessModeObserver() {
        if (this.mBrightnessObserver != null) {
            this.mContext.getContentResolver().unregisterContentObserver(this.mBrightnessObserver);
        }
    }

    /* access modifiers changed from: private */
    public void updateBrightnessMode() {
        int i = 0;
        if (Settings.System.getIntForUser(this.mContext.getContentResolver(), "screen_brightness_mode", 0, -3) == 0) {
            i = 1;
        }
        Settings.System.putIntForUser(this.mContext.getContentResolver(), "screen_brightness_mode", i, -3);
    }

    private void updateColors() {
        this.mEnableColor = Utils.getColorAttrDefaultColor(this.mContext, R$attr.prcQSTileActiveColorForUnfiexed);
        this.mDisableColor = Utils.getColorAttrDefaultColor(this.mContext, R$attr.prcAutoBtnInActiveColor);
        this.mAutoTextEnableColor = Utils.getColorAttrDefaultColor(this.mContext, R$attr.prcAutoTextActiveColor);
        this.mAutoTextDisableColor = Utils.getColorAttrDefaultColor(this.mContext, R$attr.prcAutoTextInActiveColor);
    }

    /* access modifiers changed from: private */
    public void updateBrightnessState() {
        PropertyValuesHolder propertyValuesHolder;
        PropertyValuesHolder propertyValuesHolder2;
        this.mIsAuto = isBrightnessModeAutomatic();
        this.mValueAnimation.cancel();
        if (this.mIsAuto) {
            propertyValuesHolder2 = colorValuesHolder("background", this.mDisableColor, this.mEnableColor);
            propertyValuesHolder = colorValuesHolder("text", this.mAutoTextDisableColor, this.mAutoTextEnableColor);
        } else {
            propertyValuesHolder2 = colorValuesHolder("background", this.mEnableColor, this.mDisableColor);
            propertyValuesHolder = colorValuesHolder("text", this.mAutoTextEnableColor, this.mAutoTextDisableColor);
        }
        this.mValueAnimation.setValues(new PropertyValuesHolder[]{propertyValuesHolder2, propertyValuesHolder});
        this.mValueAnimation.start();
    }

    private PropertyValuesHolder colorValuesHolder(String str, int i, int i2) {
        PropertyValuesHolder ofInt = PropertyValuesHolder.ofInt(str, new int[]{i, i2});
        ofInt.setEvaluator(ArgbEvaluator.getInstance());
        return ofInt;
    }

    private void setAutoBtnColor(int i, int i2) {
        RippleDrawable rippleDrawable = (RippleDrawable) this.mAutoBtn.getBackground();
        Drawable findDrawableByLayerId = rippleDrawable.findDrawableByLayerId(R$id.background);
        Drawable findDrawableByLayerId2 = rippleDrawable.findDrawableByLayerId(R$id.auto_text);
        findDrawableByLayerId.setTint(i);
        findDrawableByLayerId2.setTint(i2);
    }

    public void setBrightnessView(View view) {
        LinearLayout linearLayout = (LinearLayout) findViewById(R$id.qs_layout_brightness);
        View view2 = this.mBrightnessView;
        if (view2 != null) {
            linearLayout.removeView(view2);
        }
        linearLayout.addView(view, 0);
        this.mBrightnessView = view;
        setBrightnessViewMargin();
    }

    public View getBrightnessView() {
        return this.mBrightnessView;
    }

    public void setBrightnessViewMargin() {
        View view = this.mBrightnessView;
        if (view != null) {
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) view.getLayoutParams();
            layoutParams.setMarginStart(this.mBrightnessMarginStart);
            layoutParams.width = this.mBrightnessWidth;
            layoutParams.height = -2;
            this.mBrightnessView.setLayoutParams(layoutParams);
            LinearLayout.LayoutParams layoutParams2 = (LinearLayout.LayoutParams) this.mAutoBtn.getLayoutParams();
            layoutParams2.setMarginStart(this.mAutoBtnMarginStart);
            int i = this.mAutoBtnSize;
            layoutParams2.width = i;
            layoutParams2.height = i;
            this.mAutoBtn.setLayoutParams(layoutParams2);
        }
        LinearLayout linearLayout = (LinearLayout) findViewById(R$id.qs_layout_brightness);
        FrameLayout.LayoutParams layoutParams3 = (FrameLayout.LayoutParams) linearLayout.getLayoutParams();
        layoutParams3.width = -2;
        layoutParams3.height = -2;
        layoutParams3.topMargin = this.mBrightnessTopMargin;
        linearLayout.setLayoutParams(layoutParams3);
    }

    /* access modifiers changed from: package-private */
    public void addOnConfigurationChangedListener(OnConfigurationChangedListener onConfigurationChangedListener) {
        this.mOnConfigurationChangedListeners.add(onConfigurationChangedListener);
    }

    /* access modifiers changed from: package-private */
    public void removeOnConfigurationChangedListener(OnConfigurationChangedListener onConfigurationChangedListener) {
        this.mOnConfigurationChangedListeners.remove(onConfigurationChangedListener);
    }

    /* access modifiers changed from: protected */
    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        if (MotoFeature.getInstance(this.mContext).isCustomPanelView()) {
            if (DEBUG) {
                Log.i("QSPrcPanel", "onConfigurationChanged newConfig.orientation = " + configuration.orientation);
            }
            updateDimens();
            this.mOnConfigurationChangedListeners.forEach(new QSPrcPanel$$ExternalSyntheticLambda6(configuration));
            this.mFixedPanel.setBackground(getResources().getDrawable(R$drawable.zz_moto_prc_fixed_panel_bg));
            if (needUpdateTopPaddingForCarrierGroup()) {
                lambda$updateAirplaneMode$6();
            }
            updateColors();
            updateBrightnessState();
            this.mDivider.setBackgroundColor(getResources().getColor(R$color.prc_qs_customize_divider_color));
        }
    }

    private void updateDisplaySize() {
        Display display = getDisplay();
        if (display == null) {
            display = this.mContext.getDisplay();
        }
        display.getRealSize(this.mSizePoint);
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        super.onMeasure(i, i2);
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        super.onLayout(z, i, i2, i3, i4);
    }

    private void updateScrollViewBounds() {
        this.mScrollView.setClipBounds(new Rect(-((FrameLayout.LayoutParams) this.mScrollView.getLayoutParams()).leftMargin, 0, this.mScrollView.getWidth(), this.mScrollView.getHeight()));
    }

    private void updateScrollViewState() {
        int i = getContext().getResources().getConfiguration().orientation;
        if (this.mOrientation != i) {
            this.mOrientation = i;
            if (DEBUG) {
                Log.i("QSPrcPanel", "updateScrollViewState mOrientation = " + this.mOrientation);
            }
            this.mScrollView.setScrollingEnabled(this.mOrientation == 2);
        }
    }

    private void updateDimens() {
        updateDisplaySize();
        Point point = this.mSizePoint;
        this.mScreenWidth = point.x;
        this.mScreenHeight = point.y;
        if (DEBUG) {
            Log.i("QSPrcPanel", "updateDimens mScreenWidth = " + this.mScreenWidth + " mScreenHeight = " + this.mScreenHeight + " mIsDestkop = " + this.mIsDestkop + " mOrientation = " + this.mOrientation);
        }
        Resources resources = this.mContext.getResources();
        this.mQSoffestHeight = resources.getDimensionPixelSize(17105483);
        this.mNavBarHeight = resources.getDimensionPixelSize(17105362);
        if (this.mOrientation == 2 && !isNavGesturalMode()) {
            this.mScreenWidth -= this.mNavBarHeight;
        }
        this.mFooterViewWidth = (int) (((float) this.mScreenWidth) * resources.getFraction(R$fraction.zz_moto_prc_footer_view_width_precentage, 1, 1));
        this.mFooterViewHeight = resources.getDimensionPixelSize(R$dimen.zz_moto_prc_footer_view_height);
        this.mFooterMarginStart = (int) (((float) this.mScreenWidth) * resources.getFraction(R$fraction.zz_moto_prc_footer_view_marginStart_precentage, 1, 1));
        int dimensionPixelSize = resources.getDimensionPixelSize(R$dimen.zz_moto_prc_footer_view_topMargin);
        this.mFooterTopMargin = dimensionPixelSize;
        this.mFooterTopMargin = dimensionPixelSize + this.mQSoffestHeight;
        this.mFooterMargin = resources.getDimensionPixelSize(R$dimen.zz_moto_qs_tile_margin_horizontal);
        this.mFooterPaddingVertical = resources.getDimensionPixelSize(R$dimen.zz_moto_prc_footer_view_padding_vertical);
        this.mBrightnessWidth = (int) (((float) this.mScreenWidth) * resources.getFraction(R$fraction.zz_moto_prc_brightness_width_precentage, 1, 1));
        int dimensionPixelSize2 = resources.getDimensionPixelSize(R$dimen.zz_moto_prc_brightness_topMargin);
        this.mBrightnessTopMargin = dimensionPixelSize2;
        this.mBrightnessTopMargin = dimensionPixelSize2 + this.mQSoffestHeight;
        this.mBrightnessMarginStart = resources.getDimensionPixelSize(R$dimen.zz_moto_prc_brightness_marginStart);
        this.mMediaTilePanelWidth = (int) (((float) this.mScreenWidth) * resources.getFraction(R$fraction.zz_moto_prc_media_tile_panel_width_precentage, 1, 1));
        float fraction = resources.getFraction(R$fraction.zz_moto_prc_media_tile_panel_aspect_ratio, 1, 1);
        int dimensionPixelSize3 = getResources().getDimensionPixelSize(R$dimen.notification_side_paddings);
        this.mSideMargins = dimensionPixelSize3;
        this.mMediaTilePanelHeightLand = (int) (((float) this.mMediaTilePanelWidth) * fraction);
        this.mMediaTilePanelHeightPort = (int) (((float) (this.mScreenWidth - (dimensionPixelSize3 * 2))) * fraction);
        this.mMediaTilePanelHeightDeskTop = resources.getDimensionPixelSize(R$dimen.zz_moto_prc_media_tile_panel_height);
        this.mMediaTilePanelMarginStart = resources.getDimensionPixelSize(R$dimen.zz_moto_prc_media_tile_panel_marginStart);
        int dimensionPixelSize4 = resources.getDimensionPixelSize(R$dimen.zz_moto_prc_media_tile_panel_topMargin);
        this.mMediaTilePanelTopMargin = dimensionPixelSize4;
        this.mMediaTilePanelTopMargin = dimensionPixelSize4 + this.mQSoffestHeight;
        this.mFixedTilePanelMarginStart = resources.getDimensionPixelSize(R$dimen.zz_moto_prc_fixed_tile_panel_margin_start);
        this.mFixedTilePanelMarginEnd = resources.getDimensionPixelSize(R$dimen.zz_moto_prc_fixed_tile_panel_margin_end);
        this.mUnfixedTileScrollViewWidth = (int) (((float) this.mScreenWidth) * resources.getFraction(R$fraction.zz_moto_prc_unfixed_tile_scrollview_width_precentage, 1, 1));
        this.mUnfixedTileScrollViewHeight = (int) (((float) this.mScreenHeight) * resources.getFraction(R$fraction.zz_moto_prc_unfixed_tile_scrollview_height_precentage, 1, 1));
        int dimensionPixelSize5 = resources.getDimensionPixelSize(R$dimen.zz_moto_prc_unfixed_tile_scrollview_topMargin);
        this.mUnfixedTileScrollViewTopMarin = dimensionPixelSize5;
        this.mUnfixedTileScrollViewTopMarin = dimensionPixelSize5 + this.mQSoffestHeight;
        if (this.mOrientation == 1) {
            this.mUnfixedTileScrollViewTopMarin = this.mMediaTilePanelTopMargin + this.mMediaTilePanelHeightPort + resources.getDimensionPixelSize(R$dimen.zz_moto_qs_tile_margin_vertical);
        }
        this.mUnfixedTileScrollViewMarginStart = (int) (((float) this.mScreenWidth) * resources.getFraction(R$fraction.zz_moto_prc_unfixed_tile_scrollview_marginStart_precentage, 1, 1));
        this.mAutoBtnMarginStart = resources.getDimensionPixelSize(R$dimen.zz_moto_prc_auto_btn_margin_start);
        this.mAutoBtnSize = resources.getDimensionPixelSize(R$dimen.zz_moto_prc_auto_btn_width_height);
        this.mDividerMarginStart = (int) (((float) this.mScreenWidth) * resources.getFraction(R$fraction.zz_moto_prc_divider_marginStart_precentage, 1, 1));
        this.mDividerHeight = (int) (((float) this.mScreenHeight) * resources.getFraction(R$fraction.zz_moto_prc_divider_height_precentage, 1, 1));
        if (this.mIsDestkop) {
            this.mMediaTilePanelTopMargin = 0;
            this.mUnfixedTileScrollViewMarginStart = this.mMediaTilePanelMarginStart;
            this.mUnfixedTileScrollViewTopMarin = 0 + this.mMediaTilePanelHeightDeskTop + resources.getDimensionPixelSize(R$dimen.zz_moto_qs_tile_margin_vertical);
        }
    }

    public void updateResources() {
        updateDimens();
        updateScrollViewState();
        if (this.mOrientation != 2 || this.mIsDestkop) {
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.mMediaFixedPanel.getLayoutParams();
            layoutParams.topMargin = this.mMediaTilePanelTopMargin;
            layoutParams.setMarginStart(this.mMediaTilePanelMarginStart);
            layoutParams.width = -1;
            layoutParams.height = this.mIsDestkop ? this.mMediaTilePanelHeightDeskTop : this.mMediaTilePanelHeightPort;
            this.mMediaFixedPanel.setLayoutParams(layoutParams);
            FrameLayout.LayoutParams layoutParams2 = (FrameLayout.LayoutParams) this.mScrollView.getLayoutParams();
            layoutParams2.topMargin = this.mUnfixedTileScrollViewTopMarin;
            layoutParams2.width = -1;
            layoutParams2.height = -2;
            layoutParams2.setMarginStart(0);
            this.mScrollView.setLayoutParams(layoutParams2);
            this.mDivider.setVisibility(8);
            ViewGroup viewGroup = this.mSecurityFooterContainer;
            if (viewGroup != null) {
                FrameLayout.LayoutParams layoutParams3 = (FrameLayout.LayoutParams) viewGroup.getLayoutParams();
                layoutParams3.topMargin = this.mUnfixedTileScrollViewTopMarin + this.mUnfixedTilePanel.getTileLayoutHeight();
                layoutParams3.width = -1;
                layoutParams3.height = -2;
                layoutParams3.setMarginStart(this.mFooterMargin / 2);
                layoutParams3.setMarginEnd(this.mFooterMargin / 2);
                this.mSecurityFooterContainer.setLayoutParams(layoutParams3);
                this.mSecurityFooterContainer.setVisibility(0);
            }
            View view = this.mQSFooterView;
            if (view != null) {
                FrameLayout.LayoutParams layoutParams4 = (FrameLayout.LayoutParams) view.getLayoutParams();
                layoutParams4.topMargin = this.mUnfixedTileScrollViewTopMarin + this.mUnfixedTilePanel.getTileLayoutHeight();
                if (this.mSecurityFooterContainer.getHeight() > 0) {
                    layoutParams4.topMargin += this.mSecurityFooterContainer.getHeight() + this.mFooterPaddingVertical;
                }
                layoutParams4.width = -1;
                layoutParams4.height = this.mFooterViewHeight;
                layoutParams4.setMarginStart(this.mFooterMargin / 2);
                layoutParams4.setMarginEnd(this.mFooterMargin / 2);
                this.mQSFooterView.setLayoutParams(layoutParams4);
            }
        } else {
            FrameLayout.LayoutParams layoutParams5 = (FrameLayout.LayoutParams) this.mMediaFixedPanel.getLayoutParams();
            layoutParams5.topMargin = this.mMediaTilePanelTopMargin;
            layoutParams5.setMarginStart(this.mMediaTilePanelMarginStart);
            layoutParams5.width = this.mMediaTilePanelWidth;
            layoutParams5.height = this.mMediaTilePanelHeightLand;
            this.mMediaFixedPanel.setLayoutParams(layoutParams5);
            FrameLayout.LayoutParams layoutParams6 = (FrameLayout.LayoutParams) this.mScrollView.getLayoutParams();
            layoutParams6.topMargin = this.mUnfixedTileScrollViewTopMarin;
            layoutParams6.height = this.mUnfixedTileScrollViewHeight;
            layoutParams6.width = this.mUnfixedTileScrollViewWidth;
            layoutParams6.setMarginStart(this.mUnfixedTileScrollViewMarginStart);
            layoutParams6.bottomMargin = 0;
            this.mScrollView.setLayoutParams(layoutParams6);
            View view2 = this.mQSFooterView;
            if (view2 != null) {
                FrameLayout.LayoutParams layoutParams7 = (FrameLayout.LayoutParams) view2.getLayoutParams();
                layoutParams7.topMargin = this.mFooterTopMargin;
                layoutParams7.width = this.mFooterViewWidth;
                layoutParams7.setMarginStart(this.mFooterMarginStart);
                layoutParams7.height = this.mFooterViewHeight;
                this.mQSFooterView.setLayoutParams(layoutParams7);
            }
            FrameLayout.LayoutParams layoutParams8 = (FrameLayout.LayoutParams) this.mDivider.getLayoutParams();
            layoutParams8.topMargin = this.mFooterTopMargin;
            layoutParams8.height = this.mDividerHeight;
            layoutParams8.setMarginStart(this.mDividerMarginStart);
            this.mDivider.setLayoutParams(layoutParams8);
            this.mDivider.setVisibility(0);
            ViewGroup viewGroup2 = this.mSecurityFooterContainer;
            if (viewGroup2 != null) {
                FrameLayout.LayoutParams layoutParams9 = (FrameLayout.LayoutParams) viewGroup2.getLayoutParams();
                layoutParams9.topMargin = 0;
                this.mSecurityFooterContainer.setLayoutParams(layoutParams9);
                this.mSecurityFooterContainer.setVisibility(8);
            }
        }
        setBrightnessViewMargin();
        LinearLayout.LayoutParams layoutParams10 = (LinearLayout.LayoutParams) this.mFixedPanel.getLayoutParams();
        layoutParams10.width = 0;
        layoutParams10.height = -1;
        layoutParams10.setMarginStart(this.mFixedTilePanelMarginStart);
        layoutParams10.setMarginEnd(this.mFixedTilePanelMarginEnd);
        this.mFixedPanel.setLayoutParams(layoutParams10);
        LinearLayout.LayoutParams layoutParams11 = (LinearLayout.LayoutParams) this.mMediaPanel.getLayoutParams();
        layoutParams11.width = 0;
        layoutParams11.height = -1;
        layoutParams11.setMarginStart(this.mFixedTilePanelMarginStart);
        layoutParams11.setMarginEnd(this.mFixedTilePanelMarginEnd);
        this.mMediaPanel.setLayoutParams(layoutParams11);
    }

    public boolean isBrightnessModeAutomatic() {
        return Settings.System.getIntForUser(this.mContext.getContentResolver(), "screen_brightness_mode", 0, -2) == 1;
    }

    /* renamed from: com.android.systemui.qs.QSPrcPanel$BrightnessModeObserver */
    private final class BrightnessModeObserver extends ContentObserver {
        public BrightnessModeObserver(Handler handler) {
            super(handler);
        }

        public void onChange(boolean z, Uri uri) {
            super.onChange(z, uri);
            QSPrcPanel.this.updateBrightnessState();
        }
    }

    public int getDesktopQsPanelMaxHeight() {
        if (this.mIsDestkop) {
            return (((this.mUnfixedTilePanel.getOneCellHeight() + this.mUnfixedTilePanel.getOneCellMarginVertical()) * 2) - this.mUnfixedTilePanel.getOneCellMarginVertical()) + this.mUnfixedTileScrollViewTopMarin;
        }
        return 0;
    }

    public int getUnFixedTileTop() {
        return this.mUnfixedTileScrollViewTopMarin;
    }

    private boolean isNavGesturalMode() {
        return QuickStepContract.isGesturalMode(getResources().getInteger(17694885));
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (motionEvent.getActionMasked() == 1) {
            this.mFalsingManager.isFalseTouch(15);
        }
        return true;
    }

    /* access modifiers changed from: private */
    public void updateActiveSubsCount(int i) {
        this.mActiveSubsCount = i;
        if (needUpdateTopPaddingForCarrierGroup()) {
            post(new QSPrcPanel$$ExternalSyntheticLambda5(this));
        }
    }

    /* access modifiers changed from: private */
    public void updateAirplaneMode(boolean z) {
        this.mAirplaneMode = z;
        if (needUpdateTopPaddingForCarrierGroup()) {
            post(new QSPrcPanel$$ExternalSyntheticLambda4(this));
        }
    }

    private void addActiveSubsCallback() {
        ((DualSimIconController) Dependency.get(DualSimIconController.class)).addCallback(this.mCallback);
    }

    private void removeActiveSubsCallback() {
        ((DualSimIconController) Dependency.get(DualSimIconController.class)).removeCallback(this.mCallback);
    }

    /* access modifiers changed from: protected */
    /* renamed from: updatePadding */
    public void lambda$updateAirplaneMode$6() {
        setPaddingRelative(getPaddingStart(), this.mShowMotoQSCarrierGroup ? this.mContext.getResources().getDimensionPixelSize(R$dimen.qs_panel_extra_carrier_padding_top) : 0, getPaddingEnd(), getPaddingBottom());
    }

    private boolean needUpdateTopPaddingForCarrierGroup() {
        boolean z = this.mOrientation == 1 && this.mActiveSubsCount == 2 && !this.mIsDestkop && !this.mAirplaneMode;
        if (this.mShowMotoQSCarrierGroup == z) {
            return false;
        }
        this.mShowMotoQSCarrierGroup = z;
        return true;
    }
}
