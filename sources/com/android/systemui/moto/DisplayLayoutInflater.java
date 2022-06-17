package com.android.systemui.moto;

import android.content.Context;
import android.view.LayoutInflater;

public class DisplayLayoutInflater {
    private final LayoutInflater mLayoutInflater;

    private DisplayLayoutInflater(Context context) {
        this.mLayoutInflater = LayoutInflater.from(context);
    }

    public LayoutInflater getLayoutInflater() {
        return this.mLayoutInflater;
    }

    public static DisplayLayoutInflater create(Context context) {
        return new DisplayLayoutInflater(context);
    }
}
