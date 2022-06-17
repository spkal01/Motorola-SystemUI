package com.motorola.systemui.cli.media;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaRoute2Info;
import android.media.MediaRouter2Manager;
import com.android.systemui.R$drawable;
import com.android.systemui.R$string;

public class MediaDevice {
    protected final AudioManager mAudioManager;
    protected final Context mContext;
    protected final String mPackageName;
    protected final MediaRoute2Info mRouteInfo;
    protected final MediaRouter2Manager mRouterManager;
    private int mType;

    MediaDevice(Context context, MediaRouter2Manager mediaRouter2Manager, MediaRoute2Info mediaRoute2Info, String str, AudioManager audioManager) {
        this.mContext = context;
        this.mAudioManager = audioManager;
        this.mRouteInfo = mediaRoute2Info;
        this.mRouterManager = mediaRouter2Manager;
        this.mPackageName = str;
        setType(mediaRoute2Info);
    }

    private void setType(MediaRoute2Info mediaRoute2Info) {
        if (mediaRoute2Info == null) {
            this.mType = 4;
            return;
        }
        int type = mediaRoute2Info.getType();
        if (type == 2) {
            this.mType = 7;
        } else if (type == 3 || type == 4) {
            this.mType = 2;
        } else {
            if (type != 8) {
                if (!(type == 9 || type == 22)) {
                    if (type != 23) {
                        if (type != 2000) {
                            switch (type) {
                                case 11:
                                case 12:
                                case 13:
                                    break;
                                default:
                                    this.mType = 5;
                                    return;
                            }
                        } else {
                            this.mType = 6;
                            return;
                        }
                    }
                }
                this.mType = 1;
                return;
            }
            this.mType = 4;
        }
    }

    public String getName() {
        CharSequence name = this.mRouteInfo.getName();
        int type = this.mRouteInfo.getType();
        if (type == 2) {
            name = this.mContext.getString(R$string.media_transfer_this_device_name);
        } else if (type == 3 || type == 4 || type == 11 || type == 12 || type == 22) {
            name = this.mContext.getString(R$string.media_transfer_wired_usb_device_name);
        }
        return name.toString();
    }

    public int getIconId() {
        int i = R$drawable.ic_cli_headset;
        int type = this.mRouteInfo.getType();
        if (type == 2) {
            return R$drawable.ic_cli_phone;
        }
        if (type == 8 || type == 23) {
            return R$drawable.ic_cli_bluetooth;
        }
        return i;
    }

    public boolean connect() {
        this.mRouterManager.selectRoute(this.mPackageName, this.mRouteInfo);
        return true;
    }

    public int getMaxVolume() {
        return this.mRouteInfo.getVolumeMax();
    }

    public int getCurrentVolume() {
        return this.mRouteInfo.getVolume();
    }

    public void requestSetVolume(int i) {
        if (i >= 9) {
            int type = this.mRouteInfo.getType();
            if (type == 3 || type == 4 || type == 8 || type == 22 || type == 23) {
                this.mAudioManager.disableSafeMediaVolume();
            }
        }
        this.mRouterManager.setRouteVolume(this.mRouteInfo, i);
    }

    public MediaRoute2Info getRouteInfo() {
        return this.mRouteInfo;
    }

    public boolean isHdmiDevice() {
        return this.mRouteInfo.getType() == 9;
    }

    public boolean isPhoneDevice() {
        int type = this.mRouteInfo.getType();
        if (type == 2 || type == 3 || type == 4 || type == 9 || type == 22) {
            return true;
        }
        switch (type) {
            case 11:
            case 12:
            case 13:
                return true;
            default:
                return false;
        }
    }
}
