package com.motorola.systemui.cli.media;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.service.notification.StatusBarNotification;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import com.android.systemui.R$dimen;
import com.android.systemui.R$layout;
import com.android.systemui.p006qs.PageIndicator;
import com.android.systemui.statusbar.phone.ScrimController;
import com.android.systemui.statusbar.phone.ScrimState;
import com.google.android.collect.Lists;
import com.motorola.systemui.cli.media.CliMediaViewForQS;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class CliMediaViewPager extends ViewPager {
    /* access modifiers changed from: private */
    public static final boolean DEBUG = (!Build.IS_USER);
    private boolean isOnKeyguard;
    private PagerAdapter mAdapter;
    /* access modifiers changed from: private */
    public List<CliMediaView> mAllItemViews;
    private CliMediaViewForQS.MediaViewCallback mCallback;
    private CliMediaViewPagerOwn mCliMediaViewPagerOwn;
    private ArrayList<CliMediaVisibleListener> mCliMediaVisibleListeners;
    private Context mContext;
    private int mDesireHeight;
    /* access modifiers changed from: private */
    public boolean mExpanded;
    private Handler mHandler;
    private int mLargeMediaViewHeight;
    private MediaDevice mMediaDevice;
    private ArrayList<CliMediaPageModel> mMediaPageModels;
    private final ViewPager.OnPageChangeListener mPageChangeListener;
    /* access modifiers changed from: private */
    public PageIndicator mPageIndicator;
    private int mSmallMediaViewHeight;
    private CliMediaTutorialLayout mTutorialView;
    private OnScrimChangeCallback onScrimChangeCallback;

    public interface OnScrimChangeCallback {
        void transitionTo(ScrimState scrimState);
    }

    private enum OperateType {
        ADD,
        REMOVE,
        UPDATE
    }

    public CliMediaViewPager(Context context) {
        this(context, (AttributeSet) null);
    }

    public CliMediaViewPager(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mHandler = new Handler(Looper.getMainLooper());
        this.mMediaPageModels = new ArrayList<>();
        this.mAllItemViews = new CopyOnWriteArrayList();
        this.mCliMediaVisibleListeners = Lists.newArrayList();
        this.mPageChangeListener = new ViewPager.OnPageChangeListener() {
            int currentPosition;

            public void onPageScrollStateChanged(int i) {
            }

            public void onPageScrolled(int i, float f, int i2) {
                CliMediaViewPager.this.mPageIndicator.setLocation(((float) i) + f);
            }

            public void onPageSelected(int i) {
                this.currentPosition = i;
            }
        };
        this.mAdapter = new PagerAdapter() {
            public int getItemPosition(Object obj) {
                return -2;
            }

            public boolean isViewFromObject(View view, Object obj) {
                return view == obj;
            }

            public int getCount() {
                return CliMediaViewPager.this.mAllItemViews.size();
            }

            public Object instantiateItem(ViewGroup viewGroup, int i) {
                CliMediaView cliMediaView = (CliMediaView) CliMediaViewPager.this.mAllItemViews.get(i);
                boolean z = cliMediaView instanceof CliMediaViewForQS;
                if (z) {
                    if (CliMediaViewPager.DEBUG) {
                        Log.d("CLI-QSMV-CliViewPagerLayout", "Load item and update expanded: " + z);
                    }
                    CliMediaViewForQS cliMediaViewForQS = (CliMediaViewForQS) cliMediaView;
                    cliMediaViewForQS.setExpandedState(CliMediaViewPager.this.mExpanded);
                    if (CliMediaViewPager.this.mExpanded) {
                        CliMediaViewPager.this.updateMediaViewPagerHeight(CliMediaPagerHeightState.Large);
                    } else {
                        CliMediaViewPager.this.updateMediaViewPagerHeight(CliMediaPagerHeightState.Small);
                    }
                    viewGroup.addView(cliMediaViewForQS);
                } else if (cliMediaView instanceof CliMediaViewForKeyguard) {
                    viewGroup.addView((CliMediaViewForKeyguard) cliMediaView);
                }
                return cliMediaView;
            }

            public void destroyItem(ViewGroup viewGroup, int i, Object obj) {
                viewGroup.removeView((View) obj);
            }
        };
        this.onScrimChangeCallback = new OnScrimChangeCallback() {
            public void transitionTo(ScrimState scrimState) {
                if (scrimState == ScrimState.PULSING || scrimState == ScrimState.AOD) {
                    CliMediaViewPager.this.updateControllerActiveStateToItems(false);
                } else {
                    CliMediaViewPager.this.updateControllerActiveStateToItems(true);
                }
            }
        };
        this.mContext = context;
        Resources resources = getResources();
        this.mSmallMediaViewHeight = resources.getDimensionPixelSize(R$dimen.cli_small_media_view_height);
        this.mLargeMediaViewHeight = resources.getDimensionPixelSize(R$dimen.cli_large_media_view_height);
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        super.onFinishInflate();
        init();
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        for (CliMediaView parentDetachedFromWindow : this.mAllItemViews) {
            parentDetachedFromWindow.parentDetachedFromWindow();
        }
    }

    private void init() {
        setAdapter(this.mAdapter);
        setCurrentItem(0, false);
        addOnPageChangeListener(this.mPageChangeListener);
    }

    public void setPageIndicator(PageIndicator pageIndicator) {
        this.mPageIndicator = pageIndicator;
    }

    public void updatePagePanel(boolean z) {
        if (!z) {
            setCurrentItem(0);
        } else {
            updateToActivePanel();
        }
    }

    private void updateToActivePanel() {
        for (int i = 0; i < this.mMediaPageModels.size(); i++) {
            if (this.mMediaPageModels.get(i).getMediaActive()) {
                setCurrentItem(i);
                return;
            }
        }
    }

    private void updatePageModel(CliMediaPageModel cliMediaPageModel) {
        Iterator<CliMediaPageModel> it = this.mMediaPageModels.iterator();
        while (it.hasNext()) {
            CliMediaPageModel next = it.next();
            if (next.getPackageName().equals(cliMediaPageModel.getPackageName())) {
                next.updatePageModel(cliMediaPageModel);
                return;
            }
        }
    }

    private boolean isContainedMedia(String str) {
        Iterator<CliMediaPageModel> it = this.mMediaPageModels.iterator();
        while (it.hasNext()) {
            if (it.next().getPackageName().equals(str)) {
                return true;
            }
        }
        return false;
    }

    public void addMediaNotification(StatusBarNotification statusBarNotification) {
        CliMediaPageModel cliMediaPageModel = new CliMediaPageModel(new CliMediaNotificationData(this.mContext, statusBarNotification));
        if (isContainedMedia(cliMediaPageModel.getPackageName())) {
            updateItemViews(cliMediaPageModel, OperateType.UPDATE);
            this.mAdapter.notifyDataSetChanged();
            return;
        }
        this.mMediaPageModels.add(cliMediaPageModel);
        updateItemViews(cliMediaPageModel, OperateType.ADD);
        this.mAdapter.notifyDataSetChanged();
    }

    public void setMediaViewCallback(CliMediaViewForQS.MediaViewCallback mediaViewCallback) {
        this.mCallback = mediaViewCallback;
    }

    private CliMediaView getMediaItemView(CliMediaPageModel cliMediaPageModel) {
        int i;
        CliMediaViewPagerOwn cliMediaViewPagerOwn = this.mCliMediaViewPagerOwn;
        if (cliMediaViewPagerOwn == CliMediaViewPagerOwn.Keyguard) {
            i = R$layout.cli_media_view_for_keyguard;
        } else if (cliMediaViewPagerOwn == CliMediaViewPagerOwn.PRC_EXPANDED) {
            i = R$layout.prc_media_view_for_expanded_dialog;
        } else {
            i = R$layout.cli_media_view_for_qs;
        }
        CliMediaView cliMediaView = (CliMediaView) LayoutInflater.from(this.mContext).inflate(i, (ViewGroup) null);
        cliMediaView.setCliMediaPage(cliMediaPageModel);
        cliMediaView.updateMediaPage(cliMediaPageModel);
        cliMediaView.setMediaViewCallback(this.mCallback);
        return cliMediaView;
    }

    private void updateItemViews(CliMediaPageModel cliMediaPageModel, OperateType operateType) {
        this.mPageIndicator.setTintList(ColorStateList.valueOf(-1));
        int i = C26764.f191xc3bd0421[operateType.ordinal()];
        if (i == 1) {
            if (this.mAllItemViews.size() == 0) {
                if (this.mCliMediaViewPagerOwn == CliMediaViewPagerOwn.QS) {
                    updateMediaViewPagerHeight(CliMediaPagerHeightState.Small);
                } else {
                    updateMediaViewPagerHeight(CliMediaPagerHeightState.Large);
                }
            }
            CliMediaView mediaItemView = getMediaItemView(cliMediaPageModel);
            updateMediaOutputToMediaView(mediaItemView, this.mMediaDevice);
            this.mAllItemViews.add(mediaItemView);
            mediaItemView.addedToViewPager();
            this.mPageIndicator.setNumPages(this.mAllItemViews.size());
            printItemsPgkName();
        } else if (i == 2) {
            String packageName = cliMediaPageModel.getPackageName();
            Iterator<CliMediaView> it = this.mAllItemViews.iterator();
            while (true) {
                if (!it.hasNext()) {
                    break;
                }
                CliMediaView next = it.next();
                if (next.getPackageName().equals(packageName)) {
                    this.mAllItemViews.remove(next);
                    next.removedFromViewPager();
                    break;
                }
            }
            this.mPageIndicator.setNumPages(this.mAllItemViews.size());
            if (this.mAllItemViews.size() == 0) {
                updateMediaViewPagerHeight(CliMediaPagerHeightState.Zero);
            }
            printItemsPgkName();
        } else if (i == 3) {
            for (CliMediaView next2 : this.mAllItemViews) {
                if (next2.getPackageName().equals(cliMediaPageModel.getPackageName())) {
                    next2.setCliMediaPage(cliMediaPageModel);
                    next2.updateMediaPage(cliMediaPageModel);
                    updatePageModel(cliMediaPageModel);
                    return;
                }
            }
        }
    }

    private void printItemsPgkName() {
        if (DEBUG) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < this.mAllItemViews.size(); i++) {
                sb.append(this.mAllItemViews.get(i).getPackageName());
                if (i != this.mAllItemViews.size() - 1) {
                    sb.append(", ");
                }
            }
            Log.d("CLI-QSMV-CliViewPagerLayout", sb.toString());
        }
    }

    public void updateMediaNotification(StatusBarNotification statusBarNotification) {
        if (!isContainedMedia(statusBarNotification.getPackageName())) {
            addMediaNotification(statusBarNotification);
            return;
        }
        updateItemViews(new CliMediaPageModel(new CliMediaNotificationData(this.mContext, statusBarNotification)), OperateType.UPDATE);
        this.mAdapter.notifyDataSetChanged();
    }

    public void removeAllMedias() {
        if (this.mAllItemViews.size() > 0) {
            for (CliMediaView remove : this.mAllItemViews) {
                this.mAllItemViews.remove(remove);
            }
            this.mMediaPageModels.clear();
            this.mAdapter.notifyDataSetChanged();
        }
    }

    public void removeMediaNotification(StatusBarNotification statusBarNotification) {
        String packageName = statusBarNotification.getPackageName();
        if (isContainedMedia(packageName)) {
            removePageByName(packageName);
            this.mAdapter.notifyDataSetChanged();
        }
    }

    private void removePageByName(String str) {
        CliMediaPageModel cliMediaPageModel;
        Iterator<CliMediaPageModel> it = this.mMediaPageModels.iterator();
        while (true) {
            if (!it.hasNext()) {
                cliMediaPageModel = null;
                break;
            }
            cliMediaPageModel = it.next();
            if (cliMediaPageModel.getPackageName().equals(str)) {
                break;
            }
        }
        if (cliMediaPageModel != null) {
            this.mMediaPageModels.remove(cliMediaPageModel);
            updateItemViews(cliMediaPageModel, OperateType.REMOVE);
        }
    }

    public void setExpandedState(boolean z) {
        this.mExpanded = z;
        for (int i = 0; i < getChildCount(); i++) {
            if (getChildAt(i) instanceof CliMediaViewForQS) {
                ((CliMediaViewForQS) getChildAt(i)).setExpandedState(this.mExpanded);
            }
        }
    }

    public boolean isMediaPanelExpanded() {
        return this.mExpanded;
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        if (!this.isOnKeyguard) {
            for (int i3 = 0; i3 < getChildCount(); i3++) {
                getChildAt(i3).measure(i, View.MeasureSpec.makeMeasureSpec(0, 0));
            }
            i2 = View.MeasureSpec.makeMeasureSpec(this.mDesireHeight, 1073741824);
        }
        super.onMeasure(i, i2);
    }

    /* renamed from: com.motorola.systemui.cli.media.CliMediaViewPager$4 */
    static /* synthetic */ class C26764 {

        /* renamed from: $SwitchMap$com$motorola$systemui$cli$media$CliMediaPagerHeightState */
        static final /* synthetic */ int[] f190x69717d5e;

        /* renamed from: $SwitchMap$com$motorola$systemui$cli$media$CliMediaViewPager$OperateType */
        static final /* synthetic */ int[] f191xc3bd0421;

        /* JADX WARNING: Can't wrap try/catch for region: R(15:0|(2:1|2)|3|(2:5|6)|7|9|10|11|13|14|15|16|17|18|20) */
        /* JADX WARNING: Can't wrap try/catch for region: R(17:0|1|2|3|5|6|7|9|10|11|13|14|15|16|17|18|20) */
        /* JADX WARNING: Code restructure failed: missing block: B:21:?, code lost:
            return;
         */
        /* JADX WARNING: Failed to process nested try/catch */
        /* JADX WARNING: Missing exception handler attribute for start block: B:15:0x0039 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:17:0x0043 */
        static {
            /*
                com.motorola.systemui.cli.media.CliMediaPagerHeightState[] r0 = com.motorola.systemui.cli.media.CliMediaPagerHeightState.values()
                int r0 = r0.length
                int[] r0 = new int[r0]
                f190x69717d5e = r0
                r1 = 1
                com.motorola.systemui.cli.media.CliMediaPagerHeightState r2 = com.motorola.systemui.cli.media.CliMediaPagerHeightState.Zero     // Catch:{ NoSuchFieldError -> 0x0012 }
                int r2 = r2.ordinal()     // Catch:{ NoSuchFieldError -> 0x0012 }
                r0[r2] = r1     // Catch:{ NoSuchFieldError -> 0x0012 }
            L_0x0012:
                r0 = 2
                int[] r2 = f190x69717d5e     // Catch:{ NoSuchFieldError -> 0x001d }
                com.motorola.systemui.cli.media.CliMediaPagerHeightState r3 = com.motorola.systemui.cli.media.CliMediaPagerHeightState.Small     // Catch:{ NoSuchFieldError -> 0x001d }
                int r3 = r3.ordinal()     // Catch:{ NoSuchFieldError -> 0x001d }
                r2[r3] = r0     // Catch:{ NoSuchFieldError -> 0x001d }
            L_0x001d:
                r2 = 3
                int[] r3 = f190x69717d5e     // Catch:{ NoSuchFieldError -> 0x0028 }
                com.motorola.systemui.cli.media.CliMediaPagerHeightState r4 = com.motorola.systemui.cli.media.CliMediaPagerHeightState.Large     // Catch:{ NoSuchFieldError -> 0x0028 }
                int r4 = r4.ordinal()     // Catch:{ NoSuchFieldError -> 0x0028 }
                r3[r4] = r2     // Catch:{ NoSuchFieldError -> 0x0028 }
            L_0x0028:
                com.motorola.systemui.cli.media.CliMediaViewPager$OperateType[] r3 = com.motorola.systemui.cli.media.CliMediaViewPager.OperateType.values()
                int r3 = r3.length
                int[] r3 = new int[r3]
                f191xc3bd0421 = r3
                com.motorola.systemui.cli.media.CliMediaViewPager$OperateType r4 = com.motorola.systemui.cli.media.CliMediaViewPager.OperateType.ADD     // Catch:{ NoSuchFieldError -> 0x0039 }
                int r4 = r4.ordinal()     // Catch:{ NoSuchFieldError -> 0x0039 }
                r3[r4] = r1     // Catch:{ NoSuchFieldError -> 0x0039 }
            L_0x0039:
                int[] r1 = f191xc3bd0421     // Catch:{ NoSuchFieldError -> 0x0043 }
                com.motorola.systemui.cli.media.CliMediaViewPager$OperateType r3 = com.motorola.systemui.cli.media.CliMediaViewPager.OperateType.REMOVE     // Catch:{ NoSuchFieldError -> 0x0043 }
                int r3 = r3.ordinal()     // Catch:{ NoSuchFieldError -> 0x0043 }
                r1[r3] = r0     // Catch:{ NoSuchFieldError -> 0x0043 }
            L_0x0043:
                int[] r0 = f191xc3bd0421     // Catch:{ NoSuchFieldError -> 0x004d }
                com.motorola.systemui.cli.media.CliMediaViewPager$OperateType r1 = com.motorola.systemui.cli.media.CliMediaViewPager.OperateType.UPDATE     // Catch:{ NoSuchFieldError -> 0x004d }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x004d }
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x004d }
            L_0x004d:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.motorola.systemui.cli.media.CliMediaViewPager.C26764.<clinit>():void");
        }
    }

    public void updateMediaViewPagerHeight(CliMediaPagerHeightState cliMediaPagerHeightState) {
        if (DEBUG) {
            Log.d("CLI-QSMV-CliViewPagerLayout", "updateMediaViewPagerHeight: State = " + cliMediaPagerHeightState);
        }
        int i = C26764.f190x69717d5e[cliMediaPagerHeightState.ordinal()];
        if (i == 1) {
            updateMediaViewPagerHeightToZero();
        } else if (i == 2) {
            updateMediaViewPagerHeightToSmall();
        } else if (i == 3) {
            updateMediaViewPagerHeightToLarge();
        }
    }

    private void updateMediaViewPagerHeightToZero() {
        if (!this.isOnKeyguard) {
            this.mDesireHeight = 0;
            ViewGroup.LayoutParams layoutParams = getLayoutParams();
            layoutParams.height = 0;
            setLayoutParams(layoutParams);
        }
        updateViewVisibility(8, CliMediaPagerHeightState.Zero);
        Iterator<CliMediaVisibleListener> it = this.mCliMediaVisibleListeners.iterator();
        while (it.hasNext()) {
            it.next().visibilityChanged(false);
        }
    }

    private void updateMediaViewPagerHeightToSmall() {
        if (!this.isOnKeyguard) {
            this.mDesireHeight = this.mSmallMediaViewHeight;
            ViewGroup.LayoutParams layoutParams = getLayoutParams();
            layoutParams.height = this.mSmallMediaViewHeight;
            setLayoutParams(layoutParams);
        }
        updateViewVisibility(0, CliMediaPagerHeightState.Small);
        Iterator<CliMediaVisibleListener> it = this.mCliMediaVisibleListeners.iterator();
        while (it.hasNext()) {
            it.next().visibilityChanged(true);
        }
    }

    private void updateMediaViewPagerHeightToLarge() {
        if (!this.isOnKeyguard) {
            this.mDesireHeight = this.mLargeMediaViewHeight;
            ViewGroup.LayoutParams layoutParams = getLayoutParams();
            layoutParams.height = this.mLargeMediaViewHeight;
            setLayoutParams(layoutParams);
        }
        updateViewVisibility(0, CliMediaPagerHeightState.Large);
        Iterator<CliMediaVisibleListener> it = this.mCliMediaVisibleListeners.iterator();
        while (it.hasNext()) {
            it.next().visibilityChanged(true);
        }
    }

    public void registerVisibleChangeListener(CliMediaVisibleListener cliMediaVisibleListener) {
        Iterator<CliMediaVisibleListener> it = this.mCliMediaVisibleListeners.iterator();
        while (it.hasNext()) {
            if (cliMediaVisibleListener == it.next()) {
                Log.d("CLI-QSMV-CliViewPagerLayout", "The listener had been added.");
                return;
            }
        }
        if (cliMediaVisibleListener != null) {
            this.mCliMediaVisibleListeners.add(cliMediaVisibleListener);
            cliMediaVisibleListener.visibilityChanged(getVisibility() == 0);
        }
    }

    public void setIsOnKeyguard(boolean z) {
        this.isOnKeyguard = z;
    }

    public void setScrimController(ScrimController scrimController) {
        scrimController.setScrimChangeCallback(this.onScrimChangeCallback);
    }

    public void setControllerActiveState(boolean z) {
        updateControllerActiveStateToItems(z);
    }

    /* access modifiers changed from: private */
    public void updateControllerActiveStateToItems(boolean z) {
        if (DEBUG) {
            Log.d("CLI-QSMV-CliViewPagerLayout", "Update the state of controller to all items. Active: " + z);
        }
        for (CliMediaView updateControllerActive : this.mAllItemViews) {
            updateControllerActive.updateControllerActive(z);
        }
    }

    public void setMediaExpansion(float f) {
        Iterator<CliMediaVisibleListener> it = this.mCliMediaVisibleListeners.iterator();
        while (it.hasNext()) {
            it.next().setMediaExpansion(f);
        }
    }

    public void updateMediaDevicesFromOutput(MediaDevice mediaDevice) {
        if (mediaDevice != null) {
            updateMediaOutputInfo(mediaDevice);
            return;
        }
        Log.e("CLI-QSMV-CliViewPagerLayout", "The output device info is null. Upadte to default.");
        updateMediaOutputToDefault();
    }

    private void updateMediaOutputInfo(MediaDevice mediaDevice) {
        if (DEBUG) {
            Log.d("CLI-QSMV-CliViewPagerLayout", "updateMediaOutputInfo: Name=" + mediaDevice.getName());
        }
        this.mMediaDevice = mediaDevice;
        this.mHandler.post(new CliMediaViewPager$$ExternalSyntheticLambda1(this, mediaDevice));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$updateMediaOutputInfo$1(MediaDevice mediaDevice) {
        for (CliMediaView updateMediaOutputToMediaView : this.mAllItemViews) {
            updateMediaOutputToMediaView(updateMediaOutputToMediaView, mediaDevice);
        }
    }

    private void updateMediaOutputToDefault() {
        this.mHandler.post(new CliMediaViewPager$$ExternalSyntheticLambda0(this));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$updateMediaOutputToDefault$2() {
        for (CliMediaView updateMediaOutputToMediaView : this.mAllItemViews) {
            updateMediaOutputToMediaView(updateMediaOutputToMediaView, (MediaDevice) null);
        }
    }

    private void updateMediaOutputToMediaView(CliMediaView cliMediaView, MediaDevice mediaDevice) {
        if (mediaDevice != null) {
            cliMediaView.updateMediaOutputName(mediaDevice.getName());
            cliMediaView.updateMediaOutputIcon(mediaDevice.getIconId());
            return;
        }
        cliMediaView.updateMediaOutputName(17040208);
        cliMediaView.updateMediaOutputIcon(17303569);
    }

    public void setTutorialView(CliMediaTutorialLayout cliMediaTutorialLayout) {
        this.mTutorialView = cliMediaTutorialLayout;
    }

    public void setMediaViewPagerOwn(CliMediaViewPagerOwn cliMediaViewPagerOwn) {
        this.mCliMediaViewPagerOwn = cliMediaViewPagerOwn;
    }

    private void updateViewVisibility(int i, CliMediaPagerHeightState cliMediaPagerHeightState) {
        setVisibility(i);
        CliMediaTutorialLayout cliMediaTutorialLayout = this.mTutorialView;
        if (cliMediaTutorialLayout != null) {
            cliMediaTutorialLayout.updateTutorialVisibility(i, cliMediaPagerHeightState);
        }
    }

    public int getTutorialVisibility() {
        return this.mTutorialView.getVisibility();
    }

    public void restoreQSHeightWhenUserChanged() {
        ArrayList<CliMediaVisibleListener> arrayList = this.mCliMediaVisibleListeners;
        if (arrayList != null && arrayList.size() > 0) {
            Iterator<CliMediaVisibleListener> it = this.mCliMediaVisibleListeners.iterator();
            while (it.hasNext()) {
                it.next().visibilityChanged(false);
            }
        }
    }
}
