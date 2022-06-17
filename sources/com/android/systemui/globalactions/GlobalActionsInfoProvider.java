package com.android.systemui.globalactions;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.service.quickaccesswallet.QuickAccessWalletClient;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.android.systemui.R$bool;
import com.android.systemui.R$id;
import com.android.systemui.R$layout;
import com.android.systemui.R$string;
import com.android.systemui.controls.controller.ControlsController;
import com.android.systemui.plugins.ActivityStarter;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

/* compiled from: GlobalActionsInfoProvider.kt */
public final class GlobalActionsInfoProvider {
    /* access modifiers changed from: private */
    @NotNull
    public final ActivityStarter activityStarter;
    @NotNull
    private final Context context;
    @NotNull
    private final ControlsController controlsController;
    /* access modifiers changed from: private */
    @NotNull
    public PendingIntent pendingIntent;
    @NotNull
    private final QuickAccessWalletClient walletClient;

    public GlobalActionsInfoProvider(@NotNull Context context2, @NotNull QuickAccessWalletClient quickAccessWalletClient, @NotNull ControlsController controlsController2, @NotNull ActivityStarter activityStarter2) {
        Intrinsics.checkNotNullParameter(context2, "context");
        Intrinsics.checkNotNullParameter(quickAccessWalletClient, "walletClient");
        Intrinsics.checkNotNullParameter(controlsController2, "controlsController");
        Intrinsics.checkNotNullParameter(activityStarter2, "activityStarter");
        this.context = context2;
        this.walletClient = quickAccessWalletClient;
        this.controlsController = controlsController2;
        this.activityStarter = activityStarter2;
        String string = context2.getResources().getString(R$string.global_actions_change_url);
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.setData(Uri.parse(string));
        intent.addFlags(268435456);
        PendingIntent activity = PendingIntent.getActivity(context2, 0, intent, 67108864);
        Intrinsics.checkNotNullExpressionValue(activity, "getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)");
        this.pendingIntent = activity;
    }

    public final void addPanel(@NotNull Context context2, @NotNull ViewGroup viewGroup, int i, @NotNull Runnable runnable) {
        Intrinsics.checkNotNullParameter(context2, "context");
        Intrinsics.checkNotNullParameter(viewGroup, "parent");
        Intrinsics.checkNotNullParameter(runnable, "dismissParent");
        if (!(context2.getResources().getConfiguration().orientation == 2) || i <= 4) {
            View inflate = LayoutInflater.from(context2).inflate(R$layout.global_actions_change_panel, viewGroup, false);
            Object serviceLabel = this.walletClient.getServiceLabel();
            if (serviceLabel == null) {
                serviceLabel = context2.getString(R$string.wallet_title);
            }
            TextView textView = (TextView) inflate.findViewById(R$id.global_actions_change_message);
            if (textView != null) {
                textView.setText(context2.getString(R$string.global_actions_change_description, new Object[]{serviceLabel}));
            }
            inflate.setOnClickListener(new GlobalActionsInfoProvider$addPanel$1(runnable, this));
            viewGroup.addView(inflate, 0);
            incrementViewCount();
        }
    }

    public final boolean shouldShowMessage() {
        int i;
        if (!this.context.getResources().getBoolean(R$bool.global_actions_show_change_info)) {
            return false;
        }
        SharedPreferences sharedPreferences = this.context.getSharedPreferences("global_actions_info_prefs", 0);
        if (sharedPreferences.contains("view_count") || hadContent()) {
            i = sharedPreferences.getInt("view_count", 0);
        } else {
            i = -1;
        }
        if (i <= -1 || i >= 3) {
            return false;
        }
        return true;
    }

    private final boolean hadContent() {
        boolean z = this.controlsController.getFavorites().size() > 0;
        boolean isWalletFeatureAvailable = this.walletClient.isWalletFeatureAvailable();
        Log.d("GlobalActionsInfo", "Previously had controls " + z + ", cards " + isWalletFeatureAvailable);
        if (z || isWalletFeatureAvailable) {
            return true;
        }
        return false;
    }

    private final void incrementViewCount() {
        SharedPreferences sharedPreferences = this.context.getSharedPreferences("global_actions_info_prefs", 0);
        sharedPreferences.edit().putInt("view_count", sharedPreferences.getInt("view_count", 0) + 1).apply();
    }
}
