package com.android.systemui.media.dialog;

import android.content.Context;
import android.os.Bundle;
import androidx.core.graphics.drawable.IconCompat;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.logging.UiEventLogger;
import com.android.settingslib.media.MediaDevice;
import com.android.systemui.R$dimen;

public class MediaOutputDialog extends MediaOutputBaseDialog {
    final UiEventLogger mUiEventLogger;

    /* access modifiers changed from: package-private */
    public int getHeaderIconRes() {
        return 0;
    }

    MediaOutputDialog(Context context, boolean z, MediaOutputController mediaOutputController, UiEventLogger uiEventLogger) {
        super(context, mediaOutputController);
        this.mUiEventLogger = uiEventLogger;
        this.mAdapter = new MediaOutputAdapter(this.mMediaOutputController);
        if (!z) {
            getWindow().setType(2038);
        }
        show();
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.mUiEventLogger.log(MediaOutputEvent.MEDIA_OUTPUT_DIALOG_SHOW);
    }

    /* access modifiers changed from: package-private */
    public IconCompat getHeaderIcon() {
        return this.mMediaOutputController.getHeaderIcon();
    }

    /* access modifiers changed from: package-private */
    public int getHeaderIconSize() {
        return this.mContext.getResources().getDimensionPixelSize(R$dimen.media_output_dialog_header_album_icon_size);
    }

    /* access modifiers changed from: package-private */
    public CharSequence getHeaderText() {
        return this.mMediaOutputController.getHeaderTitle();
    }

    /* access modifiers changed from: package-private */
    public CharSequence getHeaderSubtitle() {
        return this.mMediaOutputController.getHeaderSubTitle();
    }

    /* access modifiers changed from: package-private */
    public int getStopButtonVisibility() {
        MediaDevice currentConnectedMediaDevice = this.mMediaOutputController.getCurrentConnectedMediaDevice();
        if (currentConnectedMediaDevice != null && this.mMediaOutputController.isActiveRemoteDevice(currentConnectedMediaDevice)) {
            return 0;
        }
        return 8;
    }

    @VisibleForTesting
    public enum MediaOutputEvent implements UiEventLogger.UiEventEnum {
        MEDIA_OUTPUT_DIALOG_SHOW(655);
        
        private final int mId;

        private MediaOutputEvent(int i) {
            this.mId = i;
        }

        public int getId() {
            return this.mId;
        }
    }
}
