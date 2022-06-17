package com.motorola.systemui.cli.media;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

public class CliMediaAction {
    private static final boolean DEBUG = (!Build.IS_USER);
    private Drawable mActionIcon;
    private boolean mCompact;
    private Context mContext;
    private PendingIntent mPendingIntent;

    public CliMediaAction(Notification.Action action, Context context, boolean z) {
        this.mContext = context;
        this.mPendingIntent = action.actionIntent;
        this.mCompact = z;
        this.mActionIcon = processIcon(action);
    }

    public PendingIntent getPendingIntent() {
        return this.mPendingIntent;
    }

    public Drawable getIcon() {
        return this.mActionIcon;
    }

    public boolean getCompactState() {
        return this.mCompact;
    }

    private Drawable processIcon(Notification.Action action) {
        Icon icon;
        boolean z = DEBUG;
        if (z) {
            Log.d("CLI-QSMV-CliMediaAction", "processIcon");
        }
        if (action.getExtras().getBoolean("INTERNAL_ACTION_EXTRA", false)) {
            if (z) {
                Log.d("CLI-QSMV-CliMediaAction", "Using internal icon");
            }
            icon = action.getIcon();
        } else {
            String string = action.getExtras().getString("PACKAGE_EXTRA", "");
            if (TextUtils.isEmpty(string)) {
                Log.w("CLI-QSMV-CliMediaAction", "processIcon packageName is null.");
            }
            icon = Icon.createWithResource(string, action.icon);
        }
        if (icon != null) {
            return icon.loadDrawable(this.mContext);
        }
        return null;
    }
}
