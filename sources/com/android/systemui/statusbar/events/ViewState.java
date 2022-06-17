package com.android.systemui.statusbar.events;

import android.graphics.Rect;
import android.view.View;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/* compiled from: PrivacyDotViewController.kt */
final class ViewState {
    @Nullable
    private final String contentDescription;
    private final int cornerIndex;
    @Nullable
    private final View designatedCorner;
    private final int height;
    @Nullable
    private final Rect landscapeRect;
    private final boolean layoutRtl;
    @Nullable
    private final Rect portraitRect;
    private final boolean qsExpanded;
    private final int rotation;
    @Nullable
    private final Rect seascapeRect;
    private final boolean shadeExpanded;
    private final boolean systemPrivacyEventIsActive;
    @Nullable
    private final Rect upsideDownRect;
    private final boolean viewInitialized;

    public ViewState() {
        this(false, false, false, false, (Rect) null, (Rect) null, (Rect) null, (Rect) null, false, 0, 0, 0, (View) null, (String) null, 16383, (DefaultConstructorMarker) null);
    }

    public static /* synthetic */ ViewState copy$default(ViewState viewState, boolean z, boolean z2, boolean z3, boolean z4, Rect rect, Rect rect2, Rect rect3, Rect rect4, boolean z5, int i, int i2, int i3, View view, String str, int i4, Object obj) {
        ViewState viewState2 = viewState;
        int i5 = i4;
        return viewState.copy((i5 & 1) != 0 ? viewState2.viewInitialized : z, (i5 & 2) != 0 ? viewState2.systemPrivacyEventIsActive : z2, (i5 & 4) != 0 ? viewState2.shadeExpanded : z3, (i5 & 8) != 0 ? viewState2.qsExpanded : z4, (i5 & 16) != 0 ? viewState2.portraitRect : rect, (i5 & 32) != 0 ? viewState2.landscapeRect : rect2, (i5 & 64) != 0 ? viewState2.upsideDownRect : rect3, (i5 & 128) != 0 ? viewState2.seascapeRect : rect4, (i5 & 256) != 0 ? viewState2.layoutRtl : z5, (i5 & 512) != 0 ? viewState2.rotation : i, (i5 & 1024) != 0 ? viewState2.height : i2, (i5 & 2048) != 0 ? viewState2.cornerIndex : i3, (i5 & 4096) != 0 ? viewState2.designatedCorner : view, (i5 & 8192) != 0 ? viewState2.contentDescription : str);
    }

    @NotNull
    public final ViewState copy(boolean z, boolean z2, boolean z3, boolean z4, @Nullable Rect rect, @Nullable Rect rect2, @Nullable Rect rect3, @Nullable Rect rect4, boolean z5, int i, int i2, int i3, @Nullable View view, @Nullable String str) {
        return new ViewState(z, z2, z3, z4, rect, rect2, rect3, rect4, z5, i, i2, i3, view, str);
    }

    public boolean equals(@Nullable Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof ViewState)) {
            return false;
        }
        ViewState viewState = (ViewState) obj;
        return this.viewInitialized == viewState.viewInitialized && this.systemPrivacyEventIsActive == viewState.systemPrivacyEventIsActive && this.shadeExpanded == viewState.shadeExpanded && this.qsExpanded == viewState.qsExpanded && Intrinsics.areEqual((Object) this.portraitRect, (Object) viewState.portraitRect) && Intrinsics.areEqual((Object) this.landscapeRect, (Object) viewState.landscapeRect) && Intrinsics.areEqual((Object) this.upsideDownRect, (Object) viewState.upsideDownRect) && Intrinsics.areEqual((Object) this.seascapeRect, (Object) viewState.seascapeRect) && this.layoutRtl == viewState.layoutRtl && this.rotation == viewState.rotation && this.height == viewState.height && this.cornerIndex == viewState.cornerIndex && Intrinsics.areEqual((Object) this.designatedCorner, (Object) viewState.designatedCorner) && Intrinsics.areEqual((Object) this.contentDescription, (Object) viewState.contentDescription);
    }

    public int hashCode() {
        boolean z = this.viewInitialized;
        boolean z2 = true;
        if (z) {
            z = true;
        }
        int i = (z ? 1 : 0) * true;
        boolean z3 = this.systemPrivacyEventIsActive;
        if (z3) {
            z3 = true;
        }
        int i2 = (i + (z3 ? 1 : 0)) * 31;
        boolean z4 = this.shadeExpanded;
        if (z4) {
            z4 = true;
        }
        int i3 = (i2 + (z4 ? 1 : 0)) * 31;
        boolean z5 = this.qsExpanded;
        if (z5) {
            z5 = true;
        }
        int i4 = (i3 + (z5 ? 1 : 0)) * 31;
        Rect rect = this.portraitRect;
        int i5 = 0;
        int hashCode = (i4 + (rect == null ? 0 : rect.hashCode())) * 31;
        Rect rect2 = this.landscapeRect;
        int hashCode2 = (hashCode + (rect2 == null ? 0 : rect2.hashCode())) * 31;
        Rect rect3 = this.upsideDownRect;
        int hashCode3 = (hashCode2 + (rect3 == null ? 0 : rect3.hashCode())) * 31;
        Rect rect4 = this.seascapeRect;
        int hashCode4 = (hashCode3 + (rect4 == null ? 0 : rect4.hashCode())) * 31;
        boolean z6 = this.layoutRtl;
        if (!z6) {
            z2 = z6;
        }
        int hashCode5 = (((((((hashCode4 + (z2 ? 1 : 0)) * 31) + Integer.hashCode(this.rotation)) * 31) + Integer.hashCode(this.height)) * 31) + Integer.hashCode(this.cornerIndex)) * 31;
        View view = this.designatedCorner;
        int hashCode6 = (hashCode5 + (view == null ? 0 : view.hashCode())) * 31;
        String str = this.contentDescription;
        if (str != null) {
            i5 = str.hashCode();
        }
        return hashCode6 + i5;
    }

    @NotNull
    public String toString() {
        return "ViewState(viewInitialized=" + this.viewInitialized + ", systemPrivacyEventIsActive=" + this.systemPrivacyEventIsActive + ", shadeExpanded=" + this.shadeExpanded + ", qsExpanded=" + this.qsExpanded + ", portraitRect=" + this.portraitRect + ", landscapeRect=" + this.landscapeRect + ", upsideDownRect=" + this.upsideDownRect + ", seascapeRect=" + this.seascapeRect + ", layoutRtl=" + this.layoutRtl + ", rotation=" + this.rotation + ", height=" + this.height + ", cornerIndex=" + this.cornerIndex + ", designatedCorner=" + this.designatedCorner + ", contentDescription=" + this.contentDescription + ')';
    }

    public ViewState(boolean z, boolean z2, boolean z3, boolean z4, @Nullable Rect rect, @Nullable Rect rect2, @Nullable Rect rect3, @Nullable Rect rect4, boolean z5, int i, int i2, int i3, @Nullable View view, @Nullable String str) {
        this.viewInitialized = z;
        this.systemPrivacyEventIsActive = z2;
        this.shadeExpanded = z3;
        this.qsExpanded = z4;
        this.portraitRect = rect;
        this.landscapeRect = rect2;
        this.upsideDownRect = rect3;
        this.seascapeRect = rect4;
        this.layoutRtl = z5;
        this.rotation = i;
        this.height = i2;
        this.cornerIndex = i3;
        this.designatedCorner = view;
        this.contentDescription = str;
    }

    /* JADX WARNING: Illegal instructions before constructor call */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ ViewState(boolean r16, boolean r17, boolean r18, boolean r19, android.graphics.Rect r20, android.graphics.Rect r21, android.graphics.Rect r22, android.graphics.Rect r23, boolean r24, int r25, int r26, int r27, android.view.View r28, java.lang.String r29, int r30, kotlin.jvm.internal.DefaultConstructorMarker r31) {
        /*
            r15 = this;
            r0 = r30
            r1 = r0 & 1
            r2 = 0
            if (r1 == 0) goto L_0x0009
            r1 = r2
            goto L_0x000b
        L_0x0009:
            r1 = r16
        L_0x000b:
            r3 = r0 & 2
            if (r3 == 0) goto L_0x0011
            r3 = r2
            goto L_0x0013
        L_0x0011:
            r3 = r17
        L_0x0013:
            r4 = r0 & 4
            if (r4 == 0) goto L_0x0019
            r4 = r2
            goto L_0x001b
        L_0x0019:
            r4 = r18
        L_0x001b:
            r5 = r0 & 8
            if (r5 == 0) goto L_0x0021
            r5 = r2
            goto L_0x0023
        L_0x0021:
            r5 = r19
        L_0x0023:
            r6 = r0 & 16
            r7 = 0
            if (r6 == 0) goto L_0x002a
            r6 = r7
            goto L_0x002c
        L_0x002a:
            r6 = r20
        L_0x002c:
            r8 = r0 & 32
            if (r8 == 0) goto L_0x0032
            r8 = r7
            goto L_0x0034
        L_0x0032:
            r8 = r21
        L_0x0034:
            r9 = r0 & 64
            if (r9 == 0) goto L_0x003a
            r9 = r7
            goto L_0x003c
        L_0x003a:
            r9 = r22
        L_0x003c:
            r10 = r0 & 128(0x80, float:1.794E-43)
            if (r10 == 0) goto L_0x0042
            r10 = r7
            goto L_0x0044
        L_0x0042:
            r10 = r23
        L_0x0044:
            r11 = r0 & 256(0x100, float:3.59E-43)
            if (r11 == 0) goto L_0x004a
            r11 = r2
            goto L_0x004c
        L_0x004a:
            r11 = r24
        L_0x004c:
            r12 = r0 & 512(0x200, float:7.175E-43)
            if (r12 == 0) goto L_0x0052
            r12 = r2
            goto L_0x0054
        L_0x0052:
            r12 = r25
        L_0x0054:
            r13 = r0 & 1024(0x400, float:1.435E-42)
            if (r13 == 0) goto L_0x0059
            goto L_0x005b
        L_0x0059:
            r2 = r26
        L_0x005b:
            r13 = r0 & 2048(0x800, float:2.87E-42)
            if (r13 == 0) goto L_0x0061
            r13 = -1
            goto L_0x0063
        L_0x0061:
            r13 = r27
        L_0x0063:
            r14 = r0 & 4096(0x1000, float:5.74E-42)
            if (r14 == 0) goto L_0x0069
            r14 = r7
            goto L_0x006b
        L_0x0069:
            r14 = r28
        L_0x006b:
            r0 = r0 & 8192(0x2000, float:1.14794E-41)
            if (r0 == 0) goto L_0x0070
            goto L_0x0072
        L_0x0070:
            r7 = r29
        L_0x0072:
            r16 = r1
            r17 = r3
            r18 = r4
            r19 = r5
            r20 = r6
            r21 = r8
            r22 = r9
            r23 = r10
            r24 = r11
            r25 = r12
            r26 = r2
            r27 = r13
            r28 = r14
            r29 = r7
            r15.<init>(r16, r17, r18, r19, r20, r21, r22, r23, r24, r25, r26, r27, r28, r29)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.statusbar.events.ViewState.<init>(boolean, boolean, boolean, boolean, android.graphics.Rect, android.graphics.Rect, android.graphics.Rect, android.graphics.Rect, boolean, int, int, int, android.view.View, java.lang.String, int, kotlin.jvm.internal.DefaultConstructorMarker):void");
    }

    public final boolean getViewInitialized() {
        return this.viewInitialized;
    }

    public final boolean getLayoutRtl() {
        return this.layoutRtl;
    }

    public final int getRotation() {
        return this.rotation;
    }

    @Nullable
    public final View getDesignatedCorner() {
        return this.designatedCorner;
    }

    @Nullable
    public final String getContentDescription() {
        return this.contentDescription;
    }

    public final boolean shouldShowDot() {
        return this.systemPrivacyEventIsActive && !this.shadeExpanded && !this.qsExpanded;
    }

    public final boolean needsLayout(@NotNull ViewState viewState) {
        Intrinsics.checkNotNullParameter(viewState, "other");
        return this.rotation != viewState.rotation || this.layoutRtl != viewState.layoutRtl || !Intrinsics.areEqual((Object) this.portraitRect, (Object) viewState.portraitRect) || !Intrinsics.areEqual((Object) this.landscapeRect, (Object) viewState.landscapeRect) || !Intrinsics.areEqual((Object) this.upsideDownRect, (Object) viewState.upsideDownRect) || !Intrinsics.areEqual((Object) this.seascapeRect, (Object) viewState.seascapeRect);
    }

    @NotNull
    public final Rect contentRectForRotation(int i) {
        if (i == 0) {
            Rect rect = this.portraitRect;
            Intrinsics.checkNotNull(rect);
            return rect;
        } else if (i == 1) {
            Rect rect2 = this.landscapeRect;
            Intrinsics.checkNotNull(rect2);
            return rect2;
        } else if (i == 2) {
            Rect rect3 = this.upsideDownRect;
            Intrinsics.checkNotNull(rect3);
            return rect3;
        } else if (i == 3) {
            Rect rect4 = this.seascapeRect;
            Intrinsics.checkNotNull(rect4);
            return rect4;
        } else {
            throw new IllegalArgumentException("not a rotation (" + i + ')');
        }
    }
}
