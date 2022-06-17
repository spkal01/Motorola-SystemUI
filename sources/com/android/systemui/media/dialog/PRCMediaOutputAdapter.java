package com.android.systemui.media.dialog;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.android.settingslib.Utils;
import com.android.settingslib.media.MediaDevice;
import com.android.systemui.R$color;
import com.android.systemui.R$drawable;
import com.android.systemui.R$layout;
import com.android.systemui.R$string;
import com.android.systemui.media.dialog.MediaOutputBaseAdapter;
import java.util.List;

public class PRCMediaOutputAdapter extends MediaOutputBaseAdapter {
    private static final boolean DEBUG = Log.isLoggable("PRCMediaOutputAdapter", 3);
    /* access modifiers changed from: private */
    public ViewGroup mConnectedItem;
    /* access modifiers changed from: private */
    public int mDeviceIconTint;
    /* access modifiers changed from: private */
    public int mDeviceNameColor;
    /* access modifiers changed from: private */
    public boolean mIncludeDynamicGroup;

    public PRCMediaOutputAdapter(MediaOutputController mediaOutputController) {
        super(mediaOutputController);
    }

    public MediaOutputBaseAdapter.MediaDeviceBaseViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        Context context = viewGroup.getContext();
        this.mContext = context;
        this.mDeviceNameColor = context.getResources().getColor(R$color.prc_media_output_device_name);
        this.mDeviceIconTint = this.mContext.getResources().getColor(R$color.prc_media_output_device_icon_tint);
        this.mHolderView = LayoutInflater.from(this.mContext).inflate(R$layout.prc_media_output_list_item, viewGroup, false);
        return new MediaDeviceViewHolder(this.mHolderView);
    }

    public void onBindViewHolder(MediaOutputBaseAdapter.MediaDeviceBaseViewHolder mediaDeviceBaseViewHolder, int i) {
        int size = this.mController.getMediaDevices().size();
        boolean z = false;
        boolean z2 = true;
        if (i == size && this.mController.isZeroMode()) {
            mediaDeviceBaseViewHolder.onBind(1, false, true);
        } else if (this.mIncludeDynamicGroup) {
            if (i == 0) {
                mediaDeviceBaseViewHolder.onBind(3, true, false);
                return;
            }
            MediaDevice mediaDevice = (MediaDevice) ((List) this.mController.getMediaDevices()).get(i - 1);
            if (i != size) {
                z2 = false;
            }
            mediaDeviceBaseViewHolder.onBind(mediaDevice, false, z2, i);
        } else if (i < size) {
            MediaDevice mediaDevice2 = (MediaDevice) ((List) this.mController.getMediaDevices()).get(i);
            boolean z3 = i == 0;
            if (i == size - 1) {
                z = true;
            }
            mediaDeviceBaseViewHolder.onBind(mediaDevice2, z3, z, i);
        } else if (DEBUG) {
            Log.d("PRCMediaOutputAdapter", "Incorrect position: " + i);
        }
    }

    public int getItemCount() {
        this.mIncludeDynamicGroup = this.mController.getSelectedMediaDevice().size() > 1;
        if (this.mController.isZeroMode() || this.mIncludeDynamicGroup) {
            return this.mController.getMediaDevices().size() + 1;
        }
        return this.mController.getMediaDevices().size();
    }

    /* access modifiers changed from: package-private */
    public CharSequence getItemTitle(MediaDevice mediaDevice) {
        if (mediaDevice.getDeviceType() != 4 || mediaDevice.isConnected()) {
            return super.getItemTitle(mediaDevice);
        }
        String name = mediaDevice.getName();
        return new SpannableString(this.mContext.getString(R$string.media_output_dialog_disconnected, new Object[]{name}));
    }

    class MediaDeviceViewHolder extends MediaOutputBaseAdapter.MediaDeviceBaseViewHolder {
        MediaDeviceViewHolder(View view) {
            super(view);
        }

        /* access modifiers changed from: package-private */
        public void onBind(MediaDevice mediaDevice, boolean z, boolean z2, int i) {
            this.mTitleText.setTextColor(PRCMediaOutputAdapter.this.mDeviceNameColor);
            this.mTwoLineTitleText.setTextColor(PRCMediaOutputAdapter.this.mDeviceNameColor);
            this.mTitleIcon.setImageDrawable(mediaDevice.getIconWithoutBackground());
            this.mTitleIcon.setColorFilter(PRCMediaOutputAdapter.this.mDeviceIconTint);
            boolean z3 = !PRCMediaOutputAdapter.this.mIncludeDynamicGroup && PRCMediaOutputAdapter.this.isCurrentlyConnected(mediaDevice);
            if (z3) {
                ViewGroup unused = PRCMediaOutputAdapter.this.mConnectedItem = this.mContainerLayout;
            }
            this.mBottomDivider.setVisibility(8);
            this.mCheckBox.setVisibility(8);
            if (!z3 || !PRCMediaOutputAdapter.this.mController.isActiveRemoteDevice(mediaDevice) || PRCMediaOutputAdapter.this.mController.getSelectableMediaDevice().size() <= 0) {
                this.mDivider.setVisibility(8);
                this.mAddIcon.setVisibility(8);
            } else {
                this.mDivider.setVisibility(0);
                this.mDivider.setTransitionAlpha(1.0f);
                this.mAddIcon.setVisibility(0);
                this.mAddIcon.setTransitionAlpha(1.0f);
                this.mAddIcon.setOnClickListener(new C1049xf29f5fe3(this));
            }
            PRCMediaOutputAdapter pRCMediaOutputAdapter = PRCMediaOutputAdapter.this;
            if (pRCMediaOutputAdapter.mCurrentActivePosition == i) {
                pRCMediaOutputAdapter.mCurrentActivePosition = -1;
            }
            if (pRCMediaOutputAdapter.mController.isTransferring()) {
                if (mediaDevice.getState() != 1 || PRCMediaOutputAdapter.this.mController.hasAdjustVolumeUserRestriction()) {
                    setSingleLineLayout(PRCMediaOutputAdapter.this.getItemTitle(mediaDevice), false);
                } else {
                    setTwoLineLayout(mediaDevice, true, false, true, false);
                }
            } else if (mediaDevice.getState() == 3) {
                setTwoLineLayout(mediaDevice, false, false, false, true);
                this.mSubTitleText.setText(R$string.media_output_dialog_connect_failed);
                this.mContainerLayout.setOnClickListener(new C1050xf29f5fe4(this, mediaDevice));
            } else if (PRCMediaOutputAdapter.this.mController.hasAdjustVolumeUserRestriction() || !z3) {
                setSingleLineLayout(PRCMediaOutputAdapter.this.getItemTitle(mediaDevice), false);
                this.mContainerLayout.setOnClickListener(new C1051xf29f5fe5(this, mediaDevice));
            } else {
                setTwoLineLayout(mediaDevice, true, true, false, false);
                initSeekbar(mediaDevice);
                PRCMediaOutputAdapter.this.mCurrentActivePosition = i;
            }
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$onBind$0(View view) {
            onEndItemClick();
        }

        /* access modifiers changed from: package-private */
        public void onBind(int i, boolean z, boolean z2) {
            super.onBind(i, z, z2);
            this.mTitleText.setTextColor(PRCMediaOutputAdapter.this.mDeviceNameColor);
            this.mTwoLineTitleText.setTextColor(PRCMediaOutputAdapter.this.mDeviceNameColor);
            this.mTitleIcon.setColorFilter(PRCMediaOutputAdapter.this.mDeviceIconTint);
            if (i == 1) {
                this.mCheckBox.setVisibility(8);
                this.mDivider.setVisibility(8);
                this.mAddIcon.setVisibility(8);
                this.mBottomDivider.setVisibility(8);
                setSingleLineLayout(PRCMediaOutputAdapter.this.mContext.getText(R$string.media_output_dialog_pairing_new), false);
                Drawable drawable = PRCMediaOutputAdapter.this.mContext.getDrawable(R$drawable.ic_add);
                drawable.setColorFilter(new PorterDuffColorFilter(Utils.getColorAccentDefaultColor(PRCMediaOutputAdapter.this.mContext), PorterDuff.Mode.SRC_IN));
                this.mTitleIcon.setImageDrawable(drawable);
                this.mContainerLayout.setOnClickListener(new C1047xf29f5fe1(this));
            } else if (i == 3) {
                ViewGroup unused = PRCMediaOutputAdapter.this.mConnectedItem = this.mContainerLayout;
                this.mBottomDivider.setVisibility(8);
                this.mCheckBox.setVisibility(8);
                if (PRCMediaOutputAdapter.this.mController.getSelectableMediaDevice().size() > 0) {
                    this.mDivider.setVisibility(0);
                    this.mDivider.setTransitionAlpha(1.0f);
                    this.mAddIcon.setVisibility(0);
                    this.mAddIcon.setTransitionAlpha(1.0f);
                    this.mAddIcon.setOnClickListener(new C1048xf29f5fe2(this));
                } else {
                    this.mDivider.setVisibility(8);
                    this.mAddIcon.setVisibility(8);
                }
                this.mTitleIcon.setImageDrawable(getSpeakerDrawable());
                CharSequence sessionName = PRCMediaOutputAdapter.this.mController.getSessionName();
                if (TextUtils.isEmpty(sessionName)) {
                    sessionName = PRCMediaOutputAdapter.this.mContext.getString(R$string.media_output_dialog_group);
                }
                setTwoLineLayout(sessionName, true, true, false, false);
                initSessionSeekbar();
            }
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$onBind$3(View view) {
            onItemClick(1);
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$onBind$4(View view) {
            onEndItemClick();
        }

        /* access modifiers changed from: private */
        /* renamed from: onItemClick */
        public void lambda$onBind$2(View view, MediaDevice mediaDevice) {
            if (!PRCMediaOutputAdapter.this.mController.isTransferring()) {
                PRCMediaOutputAdapter pRCMediaOutputAdapter = PRCMediaOutputAdapter.this;
                pRCMediaOutputAdapter.mCurrentActivePosition = -1;
                playSwitchingAnim(pRCMediaOutputAdapter.mConnectedItem, view);
                PRCMediaOutputAdapter.this.mController.connectDevice(mediaDevice);
                mediaDevice.setState(1);
                if (!PRCMediaOutputAdapter.this.isAnimating()) {
                    PRCMediaOutputAdapter.this.notifyDataSetChanged();
                }
            }
        }

        private void onItemClick(int i) {
            if (i == 1) {
                PRCMediaOutputAdapter.this.mController.launchBluetoothPairing();
            }
        }

        private void onEndItemClick() {
            PRCMediaOutputAdapter.this.mController.launchMediaOutputGroupDialog();
        }
    }
}
