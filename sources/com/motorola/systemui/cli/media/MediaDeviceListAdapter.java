package com.motorola.systemui.cli.media;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import com.android.systemui.R$id;
import java.util.List;

public class MediaDeviceListAdapter extends ArrayAdapter<MediaDevice> {
    private int mResourceId;
    private int mSelect = 0;
    /* access modifiers changed from: private */
    public MediaDevice mSelectMediaDevice;
    /* access modifiers changed from: private */
    public boolean mTrackingTouch = false;

    public MediaDeviceListAdapter(Context context, int i, List<MediaDevice> list) {
        super(context, i, list);
        this.mResourceId = i;
    }

    public void changeSelected(int i) {
        if (i != this.mSelect) {
            this.mSelect = i;
            notifyDataSetChanged();
        }
    }

    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        MediaDevice mediaDevice = (MediaDevice) getItem(i);
        if (view == null) {
            view = LayoutInflater.from(getContext()).inflate(this.mResourceId, viewGroup, false);
            viewHolder = new ViewHolder();
            viewHolder.mIcon = (ImageView) view.findViewById(R$id.media_device_icon);
            viewHolder.mName = (TextView) view.findViewById(R$id.media_device_name);
            viewHolder.mSeekBar = (SeekBar) view.findViewById(R$id.media_device_seek_bar);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.mIcon.setImageResource(mediaDevice.getIconId());
        viewHolder.mName.setText(mediaDevice.getName());
        if (this.mSelect == i) {
            this.mSelectMediaDevice = mediaDevice;
            viewHolder.mSeekBar.setVisibility(0);
            viewHolder.mSeekBar.setMax(mediaDevice.getMaxVolume());
            viewHolder.mSeekBar.setProgress(mediaDevice.getCurrentVolume());
            viewHolder.mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                public void onProgressChanged(SeekBar seekBar, int i, boolean z) {
                    if (MediaDeviceListAdapter.this.mTrackingTouch) {
                        MediaDeviceListAdapter.this.mSelectMediaDevice.requestSetVolume(i);
                    }
                }

                public void onStartTrackingTouch(SeekBar seekBar) {
                    boolean unused = MediaDeviceListAdapter.this.mTrackingTouch = true;
                }

                public void onStopTrackingTouch(SeekBar seekBar) {
                    boolean unused = MediaDeviceListAdapter.this.mTrackingTouch = false;
                }
            });
        } else {
            viewHolder.mSeekBar.setVisibility(8);
        }
        return view;
    }

    public boolean IsInTrackingTouch() {
        return this.mTrackingTouch;
    }

    class ViewHolder {
        ImageView mIcon;
        TextView mName;
        SeekBar mSeekBar;

        ViewHolder() {
        }
    }
}
