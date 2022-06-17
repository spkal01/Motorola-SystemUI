package com.motorola.systemui.cli.media;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import com.android.settingslib.Utils;
import com.android.systemui.Dependency;
import com.android.systemui.R$drawable;
import com.android.systemui.R$id;
import com.android.systemui.R$layout;
import com.android.systemui.R$string;
import com.android.systemui.statusbar.FeatureFlags;
import com.android.systemui.statusbar.phone.StatusBarIconController;
import com.android.systemui.statusbar.phone.StatusIconContainer;
import com.motorola.systemui.cli.media.AudioOutputRouteControl;
import java.util.ArrayList;
import java.util.List;

public class CliMediaOutputRouteLayout extends LinearLayout implements AdapterView.OnItemClickListener, AdapterView.OnItemSelectedListener {
    private static final String TAG = CliMediaOutputRouteLayout.class.getSimpleName();
    private String mArtist;
    private AudioOutputRouteControl mAudioOutputRouteControl;
    /* access modifiers changed from: private */
    public CliMediaViewPager mCliMediaViewPager;
    private int mConfiguration;
    private Bitmap mCover;
    /* access modifiers changed from: private */
    public int mCurrentSelect;
    private Button mDone;
    private View.OnClickListener mDoneClick;
    /* access modifiers changed from: private */
    public final C2660H mHandler;
    private MediaDeviceListAdapter mListAdapter;
    private ListView mMediaDeviceListView;
    private final MediaDevicesUpdateCallback mMediaDeviceUpdateCallback;
    /* access modifiers changed from: private */
    public List<MediaDevice> mMediaDevices;
    /* access modifiers changed from: private */
    public final Object mMediaDevicesLock;
    private TextView mSubTitle;
    private StatusBarIconController.TintedIconManager mTintedIconManager;
    private TextView mTitle;
    private ImageView mTitleIcon;
    private String mTrackTitle;

    public void onNothingSelected(AdapterView<?> adapterView) {
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0(View view) {
        setVisibility(8);
    }

    /* renamed from: com.motorola.systemui.cli.media.CliMediaOutputRouteLayout$H */
    private class C2660H extends Handler {
        public C2660H() {
            super(Looper.getMainLooper());
        }

        public void handleMessage(Message message) {
            if (message.what == 1) {
                CliMediaOutputRouteLayout.this.updateList();
            }
        }
    }

    public CliMediaOutputRouteLayout(Context context) {
        this(context, (AttributeSet) null);
    }

    public CliMediaOutputRouteLayout(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, -1);
    }

    public CliMediaOutputRouteLayout(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mMediaDevices = new ArrayList();
        this.mMediaDevicesLock = new Object();
        this.mCurrentSelect = 0;
        this.mHandler = new C2660H();
        this.mConfiguration = 0;
        this.mMediaDeviceUpdateCallback = new MediaDevicesUpdateCallback();
        this.mDoneClick = new CliMediaOutputRouteLayout$$ExternalSyntheticLambda0(this);
        setClickable(true);
    }

    public void setCover(Bitmap bitmap) {
        this.mCover = bitmap;
    }

    public void setTrackTitle(String str) {
        this.mTrackTitle = str;
    }

    public void setArtist(String str) {
        this.mArtist = str;
    }

    public void setVisibility(int i) {
        super.setVisibility(i);
        if (i == 0) {
            Bitmap bitmap = this.mCover;
            if (bitmap != null) {
                this.mTitleIcon.setImageBitmap(bitmap);
            } else {
                this.mTitleIcon.setImageResource(R$drawable.ic_cli_media_output_title_icon);
            }
            if (!TextUtils.isEmpty(this.mTrackTitle)) {
                this.mTitle.setText(this.mTrackTitle);
            } else {
                this.mTitle.setText(R$string.cli_media_output_header_title);
            }
            if (!TextUtils.isEmpty(this.mArtist)) {
                this.mSubTitle.setText(this.mArtist);
            } else {
                this.mSubTitle.setText(17040208);
            }
        } else {
            this.mCover = null;
            this.mTrackTitle = null;
            this.mArtist = null;
        }
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        super.onFinishInflate();
        int colorAttrDefaultColor = Utils.getColorAttrDefaultColor(this.mContext, 16842800);
        StatusBarIconController.TintedIconManager tintedIconManager = new StatusBarIconController.TintedIconManager((StatusIconContainer) findViewById(R$id.cli_statusIcons), (FeatureFlags) Dependency.get(FeatureFlags.class));
        this.mTintedIconManager = tintedIconManager;
        tintedIconManager.setTint(colorAttrDefaultColor);
        this.mTitleIcon = (ImageView) findViewById(R$id.media_cover);
        this.mTitle = (TextView) findViewById(R$id.header_title);
        this.mSubTitle = (TextView) findViewById(R$id.header_subtitle);
        this.mMediaDeviceListView = (ListView) findViewById(R$id.media_device_list);
        this.mDone = (Button) findViewById(R$id.done);
        this.mMediaDeviceListView.setOnItemClickListener(this);
        this.mMediaDeviceListView.setOnItemSelectedListener(this);
        this.mDone.setOnClickListener(this.mDoneClick);
        this.mTitle.setText(R$string.cli_media_output_header_title);
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (this.mTintedIconManager != null) {
            ((StatusBarIconController) Dependency.get(StatusBarIconController.class)).addIconGroup(this.mTintedIconManager);
        }
        AudioOutputRouteControl audioOutputRouteControl = new AudioOutputRouteControl(this.mContext);
        this.mAudioOutputRouteControl = audioOutputRouteControl;
        audioOutputRouteControl.registerCallback(this.mMediaDeviceUpdateCallback);
        this.mAudioOutputRouteControl.startScan();
        getMediaDevices();
        this.mListAdapter = new MediaDeviceListAdapter(this.mContext, R$layout.cli_media_output_item, this.mMediaDevices);
        if (this.mAudioOutputRouteControl.getConnectDeviceIndex() >= 0) {
            this.mListAdapter.changeSelected(this.mAudioOutputRouteControl.getConnectDeviceIndex());
        }
        this.mMediaDeviceListView.setAdapter(this.mListAdapter);
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        if (this.mTintedIconManager != null) {
            ((StatusBarIconController) Dependency.get(StatusBarIconController.class)).removeIconGroup(this.mTintedIconManager);
        }
        super.onDetachedFromWindow();
        this.mAudioOutputRouteControl.release();
        this.mAudioOutputRouteControl.unregisterCallback(this.mMediaDeviceUpdateCallback);
    }

    public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
        this.mListAdapter.changeSelected(i);
        this.mAudioOutputRouteControl.connectNewMediaDeviceByPositionForMedia(i);
        this.mCurrentSelect = i;
    }

    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long j) {
        this.mListAdapter.changeSelected(i);
        this.mCurrentSelect = i;
    }

    private void getMediaDevices() {
        synchronized (this.mMediaDevicesLock) {
            this.mMediaDevices.clear();
            this.mMediaDevices.addAll(this.mAudioOutputRouteControl.getMediaDevices());
        }
    }

    /* access modifiers changed from: private */
    public void updateList() {
        String str = TAG;
        Log.d(str, "mCurrentSelect = " + this.mCurrentSelect);
        synchronized (this.mMediaDevicesLock) {
            int i = this.mCurrentSelect;
            if (i >= 0) {
                this.mListAdapter.changeSelected(i);
            }
            if (!this.mListAdapter.IsInTrackingTouch()) {
                this.mListAdapter.notifyDataSetChanged();
            }
        }
    }

    class MediaDevicesUpdateCallback extends AudioOutputRouteControl.Callback {
        MediaDevicesUpdateCallback() {
        }

        public void onMediaDevicesUpdate(List<MediaDevice> list, int i) {
            synchronized (CliMediaOutputRouteLayout.this.mMediaDevicesLock) {
                CliMediaOutputRouteLayout.this.mMediaDevices.clear();
                CliMediaOutputRouteLayout.this.mMediaDevices.addAll(list);
                int unused = CliMediaOutputRouteLayout.this.mCurrentSelect = i;
                if (CliMediaOutputRouteLayout.this.mCliMediaViewPager != null) {
                    CliMediaOutputRouteLayout.this.mCliMediaViewPager.updateMediaDevicesFromOutput((MediaDevice) CliMediaOutputRouteLayout.this.mMediaDevices.get(CliMediaOutputRouteLayout.this.mCurrentSelect));
                }
                CliMediaOutputRouteLayout.this.mHandler.sendEmptyMessage(1);
            }
        }
    }

    public void setMediaViewPager(CliMediaViewPager cliMediaViewPager) {
        this.mCliMediaViewPager = cliMediaViewPager;
        AudioOutputRouteControl audioOutputRouteControl = this.mAudioOutputRouteControl;
        if (audioOutputRouteControl != null) {
            audioOutputRouteControl.startScan();
        }
    }
}
