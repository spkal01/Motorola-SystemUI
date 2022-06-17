package com.android.systemui.moto;

import android.app.ActivityTaskManager;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.util.Log;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

/* compiled from: MomentsHelper.kt */
public final class MomentsHelper {
    /* access modifiers changed from: private */
    @NotNull
    public static final Uri CONTENT_URI;
    @NotNull
    public static final Companion Companion = new Companion((DefaultConstructorMarker) null);

    @NotNull
    public static final String getTopPackage() {
        return Companion.getTopPackage();
    }

    public static final void insertMoments(@NotNull Context context, @NotNull String str, int i, @NotNull Uri uri) {
        Companion.insertMoments(context, str, i, uri);
    }

    /* compiled from: MomentsHelper.kt */
    public static final class Companion {
        public /* synthetic */ Companion(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }

        private Companion() {
        }

        public final void insertMoments(@NotNull Context context, @NotNull String str, int i, @NotNull Uri uri) {
            Intrinsics.checkNotNullParameter(context, "context");
            Intrinsics.checkNotNullParameter(str, "packageName");
            Intrinsics.checkNotNullParameter(uri, "uri");
            try {
                ContentValues contentValues = new ContentValues();
                contentValues.put("uri", uri.toString());
                contentValues.put("pkgName", str);
                contentValues.put("timestamp", Long.valueOf(System.currentTimeMillis()));
                contentValues.put("media_type", Integer.valueOf(i));
                Uri insert = context.getContentResolver().insert(MomentsHelper.CONTENT_URI, contentValues);
                Log.d("ContentValues", "insertMoments package=" + str + ";uri=" + uri + ";result=" + insert);
            } catch (Exception e) {
                Log.d("ContentValues", Intrinsics.stringPlus("insertMoments e=", e));
            }
        }

        @NotNull
        public final String getTopPackage() {
            ComponentName componentName;
            ActivityTaskManager.RootTaskInfo focusedRootTaskInfo = ActivityTaskManager.getService().getFocusedRootTaskInfo();
            if (focusedRootTaskInfo == null || (componentName = focusedRootTaskInfo.topActivity) == null) {
                return "unknown";
            }
            String packageName = componentName.getPackageName();
            Intrinsics.checkNotNullExpressionValue(packageName, "focusedTask.topActivity.getPackageName()");
            return packageName;
        }
    }

    static {
        Uri parse = Uri.parse("content://com.motorola.gamemode.moments.provider/moments_table");
        Intrinsics.checkNotNullExpressionValue(parse, "parse(\"content://$AUTHORITY/$MOMENTS_TABLE\")");
        CONTENT_URI = parse;
    }
}
