package com.android.systemui.p006qs.external;

import android.content.Context;
import android.content.SharedPreferences;
import android.service.quicksettings.Tile;
import android.util.Log;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONException;

/* renamed from: com.android.systemui.qs.external.CustomTileStatePersister */
/* compiled from: CustomTileStatePersister.kt */
public final class CustomTileStatePersister {
    @NotNull
    public static final Companion Companion = new Companion((DefaultConstructorMarker) null);
    private final SharedPreferences sharedPreferences;

    /* renamed from: com.android.systemui.qs.external.CustomTileStatePersister$Companion */
    /* compiled from: CustomTileStatePersister.kt */
    public static final class Companion {
        public /* synthetic */ Companion(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }

        private Companion() {
        }
    }

    public CustomTileStatePersister(@NotNull Context context) {
        Intrinsics.checkNotNullParameter(context, "context");
        this.sharedPreferences = context.getSharedPreferences("custom_tiles_state", 0);
    }

    @Nullable
    public final Tile readState(@NotNull TileServiceKey tileServiceKey) {
        Intrinsics.checkNotNullParameter(tileServiceKey, "key");
        String string = this.sharedPreferences.getString(tileServiceKey.toString(), (String) null);
        if (string == null) {
            return null;
        }
        try {
            return CustomTileStatePersisterKt.readTileFromString(string);
        } catch (JSONException e) {
            Log.e("TileServicePersistence", Intrinsics.stringPlus("Bad saved state: ", string), e);
            return null;
        }
    }

    public final void persistState(@NotNull TileServiceKey tileServiceKey, @NotNull Tile tile) {
        Intrinsics.checkNotNullParameter(tileServiceKey, "key");
        Intrinsics.checkNotNullParameter(tile, "tile");
        this.sharedPreferences.edit().putString(tileServiceKey.toString(), CustomTileStatePersisterKt.writeToString(tile)).apply();
    }

    public final void removeState(@NotNull TileServiceKey tileServiceKey) {
        Intrinsics.checkNotNullParameter(tileServiceKey, "key");
        this.sharedPreferences.edit().remove(tileServiceKey.toString()).apply();
    }
}
