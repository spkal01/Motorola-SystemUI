package com.android.p011wm.shell.onehanded;

import android.content.Context;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import com.android.p011wm.shell.C2219R;
import java.io.PrintWriter;

/* renamed from: com.android.wm.shell.onehanded.OneHandedAccessibilityUtil */
public final class OneHandedAccessibilityUtil {
    private final AccessibilityManager mAccessibilityManager;
    private String mDescription;
    private final String mPackageName;
    private final String mStartOneHandedDescription;
    private final String mStopOneHandedDescription;

    public OneHandedAccessibilityUtil(Context context) {
        this.mAccessibilityManager = AccessibilityManager.getInstance(context);
        this.mPackageName = context.getPackageName();
        this.mStartOneHandedDescription = context.getResources().getString(C2219R.string.accessibility_action_start_one_handed);
        this.mStopOneHandedDescription = context.getResources().getString(C2219R.string.accessibility_action_stop_one_handed);
    }

    public String getOneHandedStartDescription() {
        return this.mStartOneHandedDescription;
    }

    public String getOneHandedStopDescription() {
        return this.mStopOneHandedDescription;
    }

    public void announcementForScreenReader(String str) {
        if (this.mAccessibilityManager.isTouchExplorationEnabled()) {
            this.mDescription = str;
            AccessibilityEvent obtain = AccessibilityEvent.obtain();
            obtain.setPackageName(this.mPackageName);
            obtain.setEventType(16384);
            obtain.getText().add(this.mDescription);
            this.mAccessibilityManager.sendAccessibilityEvent(obtain);
        }
    }

    public void dump(PrintWriter printWriter) {
        printWriter.println("OneHandedAccessibilityUtil");
        printWriter.print("  mPackageName=");
        printWriter.println(this.mPackageName);
        printWriter.print("  mDescription=");
        printWriter.println(this.mDescription);
    }
}
