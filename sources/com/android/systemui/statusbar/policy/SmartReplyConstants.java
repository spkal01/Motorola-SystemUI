package com.android.systemui.statusbar.policy;

import android.content.Context;
import android.content.res.Resources;
import android.os.Handler;
import android.provider.DeviceConfig;
import android.text.TextUtils;
import android.util.KeyValueListParser;
import android.util.Log;
import com.android.systemui.R$bool;
import com.android.systemui.R$integer;
import com.android.systemui.util.DeviceConfigProxy;

public final class SmartReplyConstants {
    private final Context mContext;
    private final boolean mDefaultEditChoicesBeforeSending;
    private final boolean mDefaultEnabled;
    private final int mDefaultMaxNumActions;
    private final int mDefaultMaxSqueezeRemeasureAttempts;
    private final int mDefaultMinNumSystemGeneratedReplies;
    private final int mDefaultOnClickInitDelay;
    private final boolean mDefaultRequiresP;
    private final boolean mDefaultShowInHeadsUp;
    private final DeviceConfigProxy mDeviceConfig;
    private volatile boolean mEditChoicesBeforeSending;
    private volatile boolean mEnabled;
    private final Handler mHandler;
    private volatile int mMaxNumActions;
    private volatile int mMaxSqueezeRemeasureAttempts;
    private volatile int mMinNumSystemGeneratedReplies;
    private volatile long mOnClickInitDelay;
    private final DeviceConfig.OnPropertiesChangedListener mOnPropertiesChangedListener = new DeviceConfig.OnPropertiesChangedListener() {
        public void onPropertiesChanged(DeviceConfig.Properties properties) {
            if (!"systemui".equals(properties.getNamespace())) {
                Log.e("SmartReplyConstants", "Received update from DeviceConfig for unrelated namespace: " + properties.getNamespace());
                return;
            }
            SmartReplyConstants.this.updateConstants();
        }
    };
    private final KeyValueListParser mParser = new KeyValueListParser(',');
    private volatile boolean mRequiresTargetingP;
    private volatile boolean mShowInHeadsUp;

    public SmartReplyConstants(Handler handler, Context context, DeviceConfigProxy deviceConfigProxy) {
        this.mHandler = handler;
        this.mContext = context;
        Resources resources = context.getResources();
        this.mDefaultEnabled = resources.getBoolean(R$bool.config_smart_replies_in_notifications_enabled);
        this.mDefaultRequiresP = resources.getBoolean(R$bool.config_smart_replies_in_notifications_requires_targeting_p);
        this.mDefaultMaxSqueezeRemeasureAttempts = resources.getInteger(R$integer.f69xb8282359);
        this.mDefaultEditChoicesBeforeSending = resources.getBoolean(R$bool.f66xa48abd95);
        this.mDefaultShowInHeadsUp = resources.getBoolean(R$bool.config_smart_replies_in_notifications_show_in_heads_up);
        this.mDefaultMinNumSystemGeneratedReplies = resources.getInteger(R$integer.f70xce369515);
        this.mDefaultMaxNumActions = resources.getInteger(R$integer.config_smart_replies_in_notifications_max_num_actions);
        this.mDefaultOnClickInitDelay = resources.getInteger(R$integer.config_smart_replies_in_notifications_onclick_init_delay);
        this.mDeviceConfig = deviceConfigProxy;
        registerDeviceConfigListener();
        updateConstants();
    }

    private void registerDeviceConfigListener() {
        this.mDeviceConfig.addOnPropertiesChangedListener("systemui", new SmartReplyConstants$$ExternalSyntheticLambda0(this), this.mOnPropertiesChangedListener);
    }

    /* access modifiers changed from: private */
    public void postToHandler(Runnable runnable) {
        this.mHandler.post(runnable);
    }

    /* access modifiers changed from: private */
    public void updateConstants() {
        synchronized (this) {
            this.mEnabled = readDeviceConfigBooleanOrDefaultIfEmpty("ssin_enabled", this.mDefaultEnabled);
            this.mRequiresTargetingP = readDeviceConfigBooleanOrDefaultIfEmpty("ssin_requires_targeting_p", this.mDefaultRequiresP);
            this.mMaxSqueezeRemeasureAttempts = this.mDeviceConfig.getInt("systemui", "ssin_max_squeeze_remeasure_attempts", this.mDefaultMaxSqueezeRemeasureAttempts);
            this.mEditChoicesBeforeSending = readDeviceConfigBooleanOrDefaultIfEmpty("ssin_edit_choices_before_sending", this.mDefaultEditChoicesBeforeSending);
            this.mShowInHeadsUp = readDeviceConfigBooleanOrDefaultIfEmpty("ssin_show_in_heads_up", this.mDefaultShowInHeadsUp);
            this.mMinNumSystemGeneratedReplies = this.mDeviceConfig.getInt("systemui", "ssin_min_num_system_generated_replies", this.mDefaultMinNumSystemGeneratedReplies);
            this.mMaxNumActions = this.mDeviceConfig.getInt("systemui", "ssin_max_num_actions", this.mDefaultMaxNumActions);
            this.mOnClickInitDelay = (long) this.mDeviceConfig.getInt("systemui", "ssin_onclick_init_delay", this.mDefaultOnClickInitDelay);
        }
    }

    private boolean readDeviceConfigBooleanOrDefaultIfEmpty(String str, boolean z) {
        String property = this.mDeviceConfig.getProperty("systemui", str);
        if (TextUtils.isEmpty(property)) {
            return z;
        }
        if ("true".equals(property)) {
            return true;
        }
        if ("false".equals(property)) {
            return false;
        }
        return z;
    }

    public boolean isEnabled() {
        return this.mEnabled;
    }

    public boolean requiresTargetingP() {
        return this.mRequiresTargetingP;
    }

    public int getMaxSqueezeRemeasureAttempts() {
        return this.mMaxSqueezeRemeasureAttempts;
    }

    public boolean getEffectiveEditChoicesBeforeSending(int i) {
        if (i == 1) {
            return false;
        }
        if (i != 2) {
            return this.mEditChoicesBeforeSending;
        }
        return true;
    }

    public boolean getShowInHeadsUp() {
        return this.mShowInHeadsUp;
    }

    public int getMinNumSystemGeneratedReplies() {
        return this.mMinNumSystemGeneratedReplies;
    }

    public int getMaxNumActions() {
        return this.mMaxNumActions;
    }

    public long getOnClickInitDelay() {
        return this.mOnClickInitDelay;
    }
}
