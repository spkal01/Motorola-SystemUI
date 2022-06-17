package com.android.systemui.statusbar.events;

import android.content.Context;
import android.view.View;
import com.android.systemui.privacy.OngoingPrivacyChip;
import com.android.systemui.privacy.PrivacyItem;
import java.util.List;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/* compiled from: StatusEvent.kt */
public final class PrivacyEvent implements StatusEvent {
    @Nullable
    private String contentDescription;
    private final boolean forceVisible;
    private final int priority;
    /* access modifiers changed from: private */
    @Nullable
    public OngoingPrivacyChip privacyChip;
    @NotNull
    private List<PrivacyItem> privacyItems;
    private final boolean showAnimation;
    @NotNull
    private final Function1<Context, View> viewCreator;

    public PrivacyEvent() {
        this(false, 1, (DefaultConstructorMarker) null);
    }

    public PrivacyEvent(boolean z) {
        this.showAnimation = z;
        this.priority = 100;
        this.forceVisible = true;
        this.privacyItems = CollectionsKt__CollectionsKt.emptyList();
        this.viewCreator = new PrivacyEvent$viewCreator$1(this);
    }

    /* JADX INFO: this call moved to the top of the method (can break code semantics) */
    public /* synthetic */ PrivacyEvent(boolean z, int i, DefaultConstructorMarker defaultConstructorMarker) {
        this((i & 1) != 0 ? true : z);
    }

    public boolean getShowAnimation() {
        return this.showAnimation;
    }

    @Nullable
    public String getContentDescription() {
        return this.contentDescription;
    }

    public void setContentDescription(@Nullable String str) {
        this.contentDescription = str;
    }

    public int getPriority() {
        return this.priority;
    }

    public boolean getForceVisible() {
        return this.forceVisible;
    }

    @NotNull
    public final List<PrivacyItem> getPrivacyItems() {
        return this.privacyItems;
    }

    public final void setPrivacyItems(@NotNull List<PrivacyItem> list) {
        Intrinsics.checkNotNullParameter(list, "<set-?>");
        this.privacyItems = list;
    }

    @NotNull
    public Function1<Context, View> getViewCreator() {
        return this.viewCreator;
    }

    @NotNull
    public String toString() {
        String simpleName = PrivacyEvent.class.getSimpleName();
        Intrinsics.checkNotNullExpressionValue(simpleName, "javaClass.simpleName");
        return simpleName;
    }

    public boolean shouldUpdateFromEvent(@Nullable StatusEvent statusEvent) {
        return (statusEvent instanceof PrivacyEvent) && (!Intrinsics.areEqual((Object) ((PrivacyEvent) statusEvent).privacyItems, (Object) this.privacyItems) || !Intrinsics.areEqual((Object) statusEvent.getContentDescription(), (Object) getContentDescription()));
    }

    public void updateFromEvent(@Nullable StatusEvent statusEvent) {
        if (statusEvent instanceof PrivacyEvent) {
            this.privacyItems = ((PrivacyEvent) statusEvent).privacyItems;
            setContentDescription(statusEvent.getContentDescription());
            OngoingPrivacyChip ongoingPrivacyChip = this.privacyChip;
            if (ongoingPrivacyChip != null) {
                ongoingPrivacyChip.setContentDescription(statusEvent.getContentDescription());
            }
            OngoingPrivacyChip ongoingPrivacyChip2 = this.privacyChip;
            if (ongoingPrivacyChip2 != null) {
                ongoingPrivacyChip2.setPrivacyList(((PrivacyEvent) statusEvent).privacyItems);
            }
        }
    }
}
