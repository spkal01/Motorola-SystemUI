package com.android.systemui.statusbar.notification.stack;

import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.android.internal.annotations.VisibleForTesting;
import com.android.systemui.R$layout;
import com.android.systemui.media.KeyguardMediaController;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.statusbar.notification.NotificationSectionsFeatureManager;
import com.android.systemui.statusbar.notification.collection.render.SectionHeaderController;
import com.android.systemui.statusbar.notification.row.ExpandableNotificationRow;
import com.android.systemui.statusbar.notification.row.ExpandableView;
import com.android.systemui.statusbar.notification.row.StackScrollerDecorView;
import com.android.systemui.statusbar.notification.stack.StackScrollAlgorithm;
import com.android.systemui.statusbar.policy.ConfigurationController;
import com.android.systemui.util.ConvenienceExtensionsKt;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import kotlin.NoWhenBranchMatchedException;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/* compiled from: NotificationSectionsManager.kt */
public final class NotificationSectionsManager implements StackScrollAlgorithm.SectionProvider {
    @NotNull
    public static final Companion Companion = new Companion((DefaultConstructorMarker) null);
    @NotNull
    private final SectionHeaderController alertingHeaderController;
    @NotNull
    private final ConfigurationController configurationController;
    @NotNull
    private final NotificationSectionsManager$configurationListener$1 configurationListener = new NotificationSectionsManager$configurationListener$1(this);
    @NotNull
    private final SectionHeaderController incomingHeaderController;
    private boolean initialized;
    @NotNull
    private final KeyguardMediaController keyguardMediaController;
    @NotNull
    private final NotificationSectionsLogger logger;
    @Nullable
    private MediaHeaderView mediaControlsView;
    /* access modifiers changed from: private */
    public ViewGroup parent;
    @NotNull
    private final SectionHeaderController peopleHeaderController;
    @NotNull
    private final NotificationSectionsFeatureManager sectionsFeatureManager;
    @NotNull
    private final SectionHeaderController silentHeaderController;
    @NotNull
    private final StatusBarStateController statusBarStateController;

    /* compiled from: NotificationSectionsManager.kt */
    private interface SectionUpdateState<T extends ExpandableView> {
        void adjustViewPosition();

        @Nullable
        Integer getCurrentPosition();

        @Nullable
        Integer getTargetPosition();

        void setCurrentPosition(@Nullable Integer num);

        void setTargetPosition(@Nullable Integer num);
    }

    @VisibleForTesting
    public static /* synthetic */ void getAlertingHeaderView$annotations() {
    }

    @VisibleForTesting
    public static /* synthetic */ void getIncomingHeaderView$annotations() {
    }

    @VisibleForTesting
    public static /* synthetic */ void getPeopleHeaderView$annotations() {
    }

    @VisibleForTesting
    public static /* synthetic */ void getSilentHeaderView$annotations() {
    }

    public NotificationSectionsManager(@NotNull StatusBarStateController statusBarStateController2, @NotNull ConfigurationController configurationController2, @NotNull KeyguardMediaController keyguardMediaController2, @NotNull NotificationSectionsFeatureManager notificationSectionsFeatureManager, @NotNull NotificationSectionsLogger notificationSectionsLogger, @NotNull SectionHeaderController sectionHeaderController, @NotNull SectionHeaderController sectionHeaderController2, @NotNull SectionHeaderController sectionHeaderController3, @NotNull SectionHeaderController sectionHeaderController4) {
        Intrinsics.checkNotNullParameter(statusBarStateController2, "statusBarStateController");
        Intrinsics.checkNotNullParameter(configurationController2, "configurationController");
        Intrinsics.checkNotNullParameter(keyguardMediaController2, "keyguardMediaController");
        Intrinsics.checkNotNullParameter(notificationSectionsFeatureManager, "sectionsFeatureManager");
        Intrinsics.checkNotNullParameter(notificationSectionsLogger, "logger");
        Intrinsics.checkNotNullParameter(sectionHeaderController, "incomingHeaderController");
        Intrinsics.checkNotNullParameter(sectionHeaderController2, "peopleHeaderController");
        Intrinsics.checkNotNullParameter(sectionHeaderController3, "alertingHeaderController");
        Intrinsics.checkNotNullParameter(sectionHeaderController4, "silentHeaderController");
        this.statusBarStateController = statusBarStateController2;
        this.configurationController = configurationController2;
        this.keyguardMediaController = keyguardMediaController2;
        this.sectionsFeatureManager = notificationSectionsFeatureManager;
        this.logger = notificationSectionsLogger;
        this.incomingHeaderController = sectionHeaderController;
        this.peopleHeaderController = sectionHeaderController2;
        this.alertingHeaderController = sectionHeaderController3;
        this.silentHeaderController = sectionHeaderController4;
    }

    @Nullable
    public final SectionHeaderView getSilentHeaderView() {
        return this.silentHeaderController.getHeaderView();
    }

    @Nullable
    public final SectionHeaderView getAlertingHeaderView() {
        return this.alertingHeaderController.getHeaderView();
    }

    @Nullable
    public final SectionHeaderView getIncomingHeaderView() {
        return this.incomingHeaderController.getHeaderView();
    }

    @Nullable
    public final SectionHeaderView getPeopleHeaderView() {
        return this.peopleHeaderController.getHeaderView();
    }

    @VisibleForTesting
    @Nullable
    public final MediaHeaderView getMediaControlsView() {
        return this.mediaControlsView;
    }

    public final void initialize(@NotNull ViewGroup viewGroup, @NotNull LayoutInflater layoutInflater) {
        Intrinsics.checkNotNullParameter(viewGroup, "parent");
        Intrinsics.checkNotNullParameter(layoutInflater, "layoutInflater");
        if (!this.initialized) {
            this.initialized = true;
            this.parent = viewGroup;
            reinflateViews(layoutInflater);
            this.configurationController.addCallback(this.configurationListener);
            return;
        }
        throw new IllegalStateException("NotificationSectionsManager already initialized".toString());
    }

    /* JADX WARNING: Removed duplicated region for block: B:19:0x0036  */
    /* JADX WARNING: Removed duplicated region for block: B:27:0x0051  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private final <T extends com.android.systemui.statusbar.notification.row.ExpandableView> T reinflateView(T r6, android.view.LayoutInflater r7, int r8) {
        /*
            r5 = this;
            r0 = -1
            r1 = 0
            java.lang.String r2 = "parent"
            if (r6 != 0) goto L_0x0008
        L_0x0006:
            r3 = r0
            goto L_0x0032
        L_0x0008:
            android.view.ViewGroup r3 = r6.getTransientContainer()
            if (r3 != 0) goto L_0x000f
            goto L_0x0012
        L_0x000f:
            r3.removeView(r6)
        L_0x0012:
            android.view.ViewParent r3 = r6.getParent()
            android.view.ViewGroup r4 = r5.parent
            if (r4 == 0) goto L_0x0055
            if (r3 != r4) goto L_0x0006
            if (r4 == 0) goto L_0x002e
            int r3 = r4.indexOfChild(r6)
            android.view.ViewGroup r4 = r5.parent
            if (r4 == 0) goto L_0x002a
            r4.removeView(r6)
            goto L_0x0032
        L_0x002a:
            kotlin.jvm.internal.Intrinsics.throwUninitializedPropertyAccessException(r2)
            throw r1
        L_0x002e:
            kotlin.jvm.internal.Intrinsics.throwUninitializedPropertyAccessException(r2)
            throw r1
        L_0x0032:
            android.view.ViewGroup r6 = r5.parent
            if (r6 == 0) goto L_0x0051
            r4 = 0
            android.view.View r6 = r7.inflate(r8, r6, r4)
            java.lang.String r7 = "null cannot be cast to non-null type T of com.android.systemui.statusbar.notification.stack.NotificationSectionsManager.reinflateView"
            java.util.Objects.requireNonNull(r6, r7)
            com.android.systemui.statusbar.notification.row.ExpandableView r6 = (com.android.systemui.statusbar.notification.row.ExpandableView) r6
            if (r3 == r0) goto L_0x0050
            android.view.ViewGroup r5 = r5.parent
            if (r5 == 0) goto L_0x004c
            r5.addView(r6, r3)
            goto L_0x0050
        L_0x004c:
            kotlin.jvm.internal.Intrinsics.throwUninitializedPropertyAccessException(r2)
            throw r1
        L_0x0050:
            return r6
        L_0x0051:
            kotlin.jvm.internal.Intrinsics.throwUninitializedPropertyAccessException(r2)
            throw r1
        L_0x0055:
            kotlin.jvm.internal.Intrinsics.throwUninitializedPropertyAccessException(r2)
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.statusbar.notification.stack.NotificationSectionsManager.reinflateView(com.android.systemui.statusbar.notification.row.ExpandableView, android.view.LayoutInflater, int):com.android.systemui.statusbar.notification.row.ExpandableView");
    }

    @NotNull
    public final NotificationSection[] createSectionsForBuckets() {
        int[] notificationBuckets = this.sectionsFeatureManager.getNotificationBuckets();
        ArrayList arrayList = new ArrayList(notificationBuckets.length);
        int length = notificationBuckets.length;
        int i = 0;
        while (i < length) {
            int i2 = notificationBuckets[i];
            ViewGroup viewGroup = this.parent;
            if (viewGroup != null) {
                arrayList.add(new NotificationSection(viewGroup, i2));
                i++;
            } else {
                Intrinsics.throwUninitializedPropertyAccessException("parent");
                throw null;
            }
        }
        Object[] array = arrayList.toArray(new NotificationSection[0]);
        Objects.requireNonNull(array, "null cannot be cast to non-null type kotlin.Array<T>");
        return (NotificationSection[]) array;
    }

    public final void reinflateViews(@NotNull LayoutInflater layoutInflater) {
        Intrinsics.checkNotNullParameter(layoutInflater, "layoutInflater");
        SectionHeaderController sectionHeaderController = this.silentHeaderController;
        ViewGroup viewGroup = this.parent;
        if (viewGroup != null) {
            sectionHeaderController.reinflateView(viewGroup);
            SectionHeaderController sectionHeaderController2 = this.alertingHeaderController;
            ViewGroup viewGroup2 = this.parent;
            if (viewGroup2 != null) {
                sectionHeaderController2.reinflateView(viewGroup2);
                SectionHeaderController sectionHeaderController3 = this.peopleHeaderController;
                ViewGroup viewGroup3 = this.parent;
                if (viewGroup3 != null) {
                    sectionHeaderController3.reinflateView(viewGroup3);
                    SectionHeaderController sectionHeaderController4 = this.incomingHeaderController;
                    ViewGroup viewGroup4 = this.parent;
                    if (viewGroup4 != null) {
                        sectionHeaderController4.reinflateView(viewGroup4);
                        MediaHeaderView mediaHeaderView = (MediaHeaderView) reinflateView(this.mediaControlsView, layoutInflater, R$layout.keyguard_media_header);
                        this.mediaControlsView = mediaHeaderView;
                        this.keyguardMediaController.attachSinglePaneContainer(mediaHeaderView);
                        return;
                    }
                    Intrinsics.throwUninitializedPropertyAccessException("parent");
                    throw null;
                }
                Intrinsics.throwUninitializedPropertyAccessException("parent");
                throw null;
            }
            Intrinsics.throwUninitializedPropertyAccessException("parent");
            throw null;
        }
        Intrinsics.throwUninitializedPropertyAccessException("parent");
        throw null;
    }

    public boolean beginsSection(@NotNull View view, @Nullable View view2) {
        Intrinsics.checkNotNullParameter(view, "view");
        return view == getSilentHeaderView() || view == this.mediaControlsView || view == getPeopleHeaderView() || view == getAlertingHeaderView() || view == getIncomingHeaderView() || !Intrinsics.areEqual((Object) getBucket(view), (Object) getBucket(view2));
    }

    /* access modifiers changed from: private */
    public final Integer getBucket(View view) {
        if (view == getSilentHeaderView()) {
            return 6;
        }
        if (view == getIncomingHeaderView()) {
            return 2;
        }
        if (view == this.mediaControlsView) {
            return 1;
        }
        if (view == getPeopleHeaderView()) {
            return 4;
        }
        if (view == getAlertingHeaderView()) {
            return 5;
        }
        if (view instanceof ExpandableNotificationRow) {
            return Integer.valueOf(((ExpandableNotificationRow) view).getEntry().getBucket());
        }
        return null;
    }

    private final void logShadeChild(int i, View view) {
        if (view == getIncomingHeaderView()) {
            this.logger.logIncomingHeader(i);
        } else if (view == this.mediaControlsView) {
            this.logger.logMediaControls(i);
        } else if (view == getPeopleHeaderView()) {
            this.logger.logConversationsHeader(i);
        } else if (view == getAlertingHeaderView()) {
            this.logger.logAlertingHeader(i);
        } else if (view == getSilentHeaderView()) {
            this.logger.logSilentHeader(i);
        } else if (!(view instanceof ExpandableNotificationRow)) {
            this.logger.logOther(i, view.getClass());
        } else {
            ExpandableNotificationRow expandableNotificationRow = (ExpandableNotificationRow) view;
            boolean isHeadsUp = expandableNotificationRow.isHeadsUp();
            int bucket = expandableNotificationRow.getEntry().getBucket();
            if (bucket == 2) {
                this.logger.logHeadsUp(i, isHeadsUp);
            } else if (bucket == 4) {
                this.logger.logConversation(i, isHeadsUp);
            } else if (bucket == 5) {
                this.logger.logAlerting(i, isHeadsUp);
            } else if (bucket == 6) {
                this.logger.logSilent(i, isHeadsUp);
            }
        }
    }

    private final void logShadeContents() {
        ViewGroup viewGroup = this.parent;
        if (viewGroup != null) {
            int i = 0;
            for (View next : ConvenienceExtensionsKt.getChildren(viewGroup)) {
                int i2 = i + 1;
                if (i < 0) {
                    CollectionsKt__CollectionsKt.throwIndexOverflow();
                }
                logShadeChild(i, next);
                i = i2;
            }
            return;
        }
        Intrinsics.throwUninitializedPropertyAccessException("parent");
        throw null;
    }

    private final boolean isUsingMultipleSections() {
        return this.sectionsFeatureManager.getNumberOfBuckets() > 1;
    }

    @VisibleForTesting
    public final void updateSectionBoundaries() {
        updateSectionBoundaries("test");
    }

    private final <T extends ExpandableView> SectionUpdateState<T> expandableViewHeaderState(T t) {
        return new NotificationSectionsManager$expandableViewHeaderState$1(t, this);
    }

    private final <T extends StackScrollerDecorView> SectionUpdateState<T> decorViewHeaderState(T t) {
        return new NotificationSectionsManager$decorViewHeaderState$1(expandableViewHeaderState(t), t);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:59:0x0118, code lost:
        if ((r1.getVisibility() == 8) == false) goto L_0x011a;
     */
    /* JADX WARNING: Removed duplicated region for block: B:102:0x0195  */
    /* JADX WARNING: Removed duplicated region for block: B:103:0x0198  */
    /* JADX WARNING: Removed duplicated region for block: B:111:0x01bc A[LOOP:0: B:32:0x0096->B:111:0x01bc, LOOP_END] */
    /* JADX WARNING: Removed duplicated region for block: B:171:0x01cd A[EDGE_INSN: B:171:0x01cd->B:115:0x01cd ?: BREAK  , SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:75:0x0145  */
    /* JADX WARNING: Removed duplicated region for block: B:78:0x014c  */
    /* JADX WARNING: Removed duplicated region for block: B:85:0x015f A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:92:0x0176 A[ADDED_TO_REGION] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public final void updateSectionBoundaries(@org.jetbrains.annotations.NotNull java.lang.String r24) {
        /*
            r23 = this;
            r7 = r23
            r0 = r24
            java.lang.String r1 = "reason"
            kotlin.jvm.internal.Intrinsics.checkNotNullParameter(r0, r1)
            boolean r1 = r23.isUsingMultipleSections()
            if (r1 != 0) goto L_0x0010
            return
        L_0x0010:
            com.android.systemui.statusbar.notification.stack.NotificationSectionsLogger r1 = r7.logger
            r1.logStartSectionUpdate(r0)
            com.android.systemui.plugins.statusbar.StatusBarStateController r0 = r7.statusBarStateController
            int r0 = r0.getState()
            r8 = 0
            r9 = 1
            if (r0 == r9) goto L_0x0021
            r10 = r9
            goto L_0x0022
        L_0x0021:
            r10 = r8
        L_0x0022:
            com.android.systemui.statusbar.notification.NotificationSectionsFeatureManager r0 = r7.sectionsFeatureManager
            boolean r11 = r0.isMediaControlsEnabled()
            com.android.systemui.statusbar.notification.stack.MediaHeaderView r0 = r7.mediaControlsView
            if (r0 != 0) goto L_0x002e
            r13 = 0
            goto L_0x0033
        L_0x002e:
            com.android.systemui.statusbar.notification.stack.NotificationSectionsManager$SectionUpdateState r0 = r7.expandableViewHeaderState(r0)
            r13 = r0
        L_0x0033:
            com.android.systemui.statusbar.notification.stack.SectionHeaderView r0 = r23.getIncomingHeaderView()
            if (r0 != 0) goto L_0x003b
            r14 = 0
            goto L_0x0040
        L_0x003b:
            com.android.systemui.statusbar.notification.stack.NotificationSectionsManager$SectionUpdateState r0 = r7.decorViewHeaderState(r0)
            r14 = r0
        L_0x0040:
            com.android.systemui.statusbar.notification.stack.SectionHeaderView r0 = r23.getPeopleHeaderView()
            if (r0 != 0) goto L_0x0048
            r15 = 0
            goto L_0x004d
        L_0x0048:
            com.android.systemui.statusbar.notification.stack.NotificationSectionsManager$SectionUpdateState r0 = r7.decorViewHeaderState(r0)
            r15 = r0
        L_0x004d:
            com.android.systemui.statusbar.notification.stack.SectionHeaderView r0 = r23.getAlertingHeaderView()
            if (r0 != 0) goto L_0x0056
            r16 = 0
            goto L_0x005c
        L_0x0056:
            com.android.systemui.statusbar.notification.stack.NotificationSectionsManager$SectionUpdateState r0 = r7.decorViewHeaderState(r0)
            r16 = r0
        L_0x005c:
            com.android.systemui.statusbar.notification.stack.SectionHeaderView r0 = r23.getSilentHeaderView()
            if (r0 != 0) goto L_0x0064
            r6 = 0
            goto L_0x0069
        L_0x0064:
            com.android.systemui.statusbar.notification.stack.NotificationSectionsManager$SectionUpdateState r0 = r7.decorViewHeaderState(r0)
            r6 = r0
        L_0x0069:
            r0 = 5
            com.android.systemui.statusbar.notification.stack.NotificationSectionsManager$SectionUpdateState[] r0 = new com.android.systemui.statusbar.notification.stack.NotificationSectionsManager.SectionUpdateState[r0]
            r0[r8] = r13
            r0[r9] = r14
            r5 = 2
            r0[r5] = r15
            r1 = 3
            r0[r1] = r16
            r4 = 4
            r0[r4] = r6
            kotlin.sequences.Sequence r0 = kotlin.sequences.SequencesKt__SequencesKt.sequenceOf(r0)
            kotlin.sequences.Sequence r3 = kotlin.sequences.SequencesKt___SequencesKt.filterNotNull(r0)
            android.view.ViewGroup r0 = r7.parent
            java.lang.String r17 = "parent"
            if (r0 == 0) goto L_0x02a1
            int r0 = r0.getChildCount()
            int r0 = r0 - r9
            r2 = -1
            if (r2 > r0) goto L_0x01c9
            r1 = r0
            r19 = r8
            r20 = r19
            r18 = 0
        L_0x0096:
            int r0 = r1 + -1
            android.view.ViewGroup r2 = r7.parent
            if (r2 == 0) goto L_0x01c4
            android.view.View r2 = r2.getChildAt(r1)
            if (r2 != 0) goto L_0x00aa
            r8 = r0
            r21 = r1
            r0 = r2
            r12 = r3
            r22 = r6
            goto L_0x0100
        L_0x00aa:
            r7.logShadeChild(r1, r2)
            r8 = r0
            r0 = r23
            r21 = r1
            r1 = r13
            r24 = r2
            r12 = -1
            r2 = r14
            r12 = r3
            r3 = r15
            r4 = r16
            r5 = r6
            r22 = r6
            r6 = r24
            com.android.systemui.statusbar.notification.stack.NotificationSectionsManager$SectionUpdateState r0 = updateSectionBoundaries$getSectionState(r0, r1, r2, r3, r4, r5, r6)
            if (r0 != 0) goto L_0x00c7
            goto L_0x00fe
        L_0x00c7:
            java.lang.Integer r1 = java.lang.Integer.valueOf(r21)
            r0.setCurrentPosition(r1)
            com.android.systemui.statusbar.notification.stack.NotificationSectionsManager$updateSectionBoundaries$1$1$1 r1 = new com.android.systemui.statusbar.notification.stack.NotificationSectionsManager$updateSectionBoundaries$1$1$1
            r1.<init>(r0)
            kotlin.sequences.Sequence r0 = com.android.systemui.util.ConvenienceExtensionsKt.takeUntil(r12, r1)
            java.util.Iterator r0 = r0.iterator()
        L_0x00db:
            boolean r1 = r0.hasNext()
            if (r1 == 0) goto L_0x00fc
            java.lang.Object r1 = r0.next()
            com.android.systemui.statusbar.notification.stack.NotificationSectionsManager$SectionUpdateState r1 = (com.android.systemui.statusbar.notification.stack.NotificationSectionsManager.SectionUpdateState) r1
            java.lang.Integer r2 = r1.getTargetPosition()
            if (r2 != 0) goto L_0x00ef
            r2 = 0
            goto L_0x00f8
        L_0x00ef:
            int r2 = r2.intValue()
            int r2 = r2 - r9
            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)
        L_0x00f8:
            r1.setTargetPosition(r2)
            goto L_0x00db
        L_0x00fc:
            kotlin.Unit r0 = kotlin.Unit.INSTANCE
        L_0x00fe:
            r0 = r24
        L_0x0100:
            boolean r1 = r0 instanceof com.android.systemui.statusbar.notification.row.ExpandableNotificationRow
            if (r1 == 0) goto L_0x0108
            r1 = r0
            com.android.systemui.statusbar.notification.row.ExpandableNotificationRow r1 = (com.android.systemui.statusbar.notification.row.ExpandableNotificationRow) r1
            goto L_0x0109
        L_0x0108:
            r1 = 0
        L_0x0109:
            if (r1 != 0) goto L_0x010d
        L_0x010b:
            r1 = 0
            goto L_0x011a
        L_0x010d:
            int r2 = r1.getVisibility()
            r3 = 8
            if (r2 != r3) goto L_0x0117
            r2 = r9
            goto L_0x0118
        L_0x0117:
            r2 = 0
        L_0x0118:
            if (r2 != 0) goto L_0x010b
        L_0x011a:
            if (r19 != 0) goto L_0x0148
            if (r18 != 0) goto L_0x0120
        L_0x011e:
            r2 = 0
            goto L_0x013c
        L_0x0120:
            int r2 = r18.intValue()
            if (r1 != 0) goto L_0x0128
            r3 = 0
            goto L_0x012c
        L_0x0128:
            com.android.systemui.statusbar.notification.collection.NotificationEntry r3 = r1.getEntry()
        L_0x012c:
            if (r3 != 0) goto L_0x012f
            goto L_0x011e
        L_0x012f:
            int r3 = r3.getBucket()
            if (r2 >= r3) goto L_0x0137
            r2 = r9
            goto L_0x0138
        L_0x0137:
            r2 = 0
        L_0x0138:
            java.lang.Boolean r2 = java.lang.Boolean.valueOf(r2)
        L_0x013c:
            java.lang.Boolean r3 = java.lang.Boolean.TRUE
            boolean r2 = kotlin.jvm.internal.Intrinsics.areEqual((java.lang.Object) r2, (java.lang.Object) r3)
            if (r2 == 0) goto L_0x0145
            goto L_0x0148
        L_0x0145:
            r19 = 0
            goto L_0x014a
        L_0x0148:
            r19 = r9
        L_0x014a:
            if (r19 == 0) goto L_0x015c
            if (r1 != 0) goto L_0x0150
            r2 = 0
            goto L_0x0154
        L_0x0150:
            com.android.systemui.statusbar.notification.collection.NotificationEntry r2 = r1.getEntry()
        L_0x0154:
            if (r2 != 0) goto L_0x0157
            goto L_0x015c
        L_0x0157:
            r3 = 2
            r2.setBucket(r3)
            goto L_0x015d
        L_0x015c:
            r3 = 2
        L_0x015d:
            if (r18 == 0) goto L_0x0173
            if (r0 == 0) goto L_0x0171
            if (r1 == 0) goto L_0x0173
            com.android.systemui.statusbar.notification.collection.NotificationEntry r0 = r1.getEntry()
            int r0 = r0.getBucket()
            int r2 = r18.intValue()
            if (r2 == r0) goto L_0x0173
        L_0x0171:
            r0 = r9
            goto L_0x0174
        L_0x0173:
            r0 = 0
        L_0x0174:
            if (r0 == 0) goto L_0x0191
            if (r10 == 0) goto L_0x0191
            r0 = 6
            if (r18 != 0) goto L_0x017c
            goto L_0x0191
        L_0x017c:
            int r2 = r18.intValue()
            if (r2 != r0) goto L_0x0191
            r0 = r22
            if (r0 != 0) goto L_0x0187
            goto L_0x0193
        L_0x0187:
            int r2 = r21 + 1
            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)
            r0.setTargetPosition(r2)
            goto L_0x0193
        L_0x0191:
            r0 = r22
        L_0x0193:
            if (r1 != 0) goto L_0x0198
            r1 = -1
            r4 = 4
            goto L_0x01b9
        L_0x0198:
            if (r20 != 0) goto L_0x01a9
            com.android.systemui.statusbar.notification.collection.NotificationEntry r2 = r1.getEntry()
            int r2 = r2.getBucket()
            r4 = 4
            if (r2 != r4) goto L_0x01a6
            goto L_0x01aa
        L_0x01a6:
            r20 = 0
            goto L_0x01ac
        L_0x01a9:
            r4 = 4
        L_0x01aa:
            r20 = r9
        L_0x01ac:
            com.android.systemui.statusbar.notification.collection.NotificationEntry r1 = r1.getEntry()
            int r1 = r1.getBucket()
            java.lang.Integer r18 = java.lang.Integer.valueOf(r1)
            r1 = -1
        L_0x01b9:
            if (r1 <= r8) goto L_0x01bc
            goto L_0x01cd
        L_0x01bc:
            r6 = r0
            r2 = r1
            r5 = r3
            r1 = r8
            r3 = r12
            r8 = 0
            goto L_0x0096
        L_0x01c4:
            kotlin.jvm.internal.Intrinsics.throwUninitializedPropertyAccessException(r17)
            r0 = 0
            throw r0
        L_0x01c9:
            r1 = r2
            r12 = r3
            r3 = r5
            r0 = r6
        L_0x01cd:
            r2 = 0
            if (r13 != 0) goto L_0x01d1
            goto L_0x01dc
        L_0x01d1:
            if (r11 == 0) goto L_0x01d8
            java.lang.Integer r4 = java.lang.Integer.valueOf(r2)
            goto L_0x01d9
        L_0x01d8:
            r4 = 0
        L_0x01d9:
            r13.setTargetPosition(r4)
        L_0x01dc:
            com.android.systemui.statusbar.notification.stack.NotificationSectionsLogger r4 = r7.logger
            java.lang.String r5 = "New header target positions:"
            r4.logStr(r5)
            com.android.systemui.statusbar.notification.stack.NotificationSectionsLogger r4 = r7.logger
            if (r13 != 0) goto L_0x01e9
        L_0x01e7:
            r5 = r1
            goto L_0x01f4
        L_0x01e9:
            java.lang.Integer r5 = r13.getTargetPosition()
            if (r5 != 0) goto L_0x01f0
            goto L_0x01e7
        L_0x01f0:
            int r5 = r5.intValue()
        L_0x01f4:
            r4.logMediaControls(r5)
            com.android.systemui.statusbar.notification.stack.NotificationSectionsLogger r4 = r7.logger
            if (r14 != 0) goto L_0x01fd
        L_0x01fb:
            r5 = r1
            goto L_0x0208
        L_0x01fd:
            java.lang.Integer r5 = r14.getTargetPosition()
            if (r5 != 0) goto L_0x0204
            goto L_0x01fb
        L_0x0204:
            int r5 = r5.intValue()
        L_0x0208:
            r4.logIncomingHeader(r5)
            com.android.systemui.statusbar.notification.stack.NotificationSectionsLogger r4 = r7.logger
            if (r15 != 0) goto L_0x0211
        L_0x020f:
            r5 = r1
            goto L_0x021c
        L_0x0211:
            java.lang.Integer r5 = r15.getTargetPosition()
            if (r5 != 0) goto L_0x0218
            goto L_0x020f
        L_0x0218:
            int r5 = r5.intValue()
        L_0x021c:
            r4.logConversationsHeader(r5)
            com.android.systemui.statusbar.notification.stack.NotificationSectionsLogger r4 = r7.logger
            if (r16 != 0) goto L_0x0225
        L_0x0223:
            r5 = r1
            goto L_0x0230
        L_0x0225:
            java.lang.Integer r5 = r16.getTargetPosition()
            if (r5 != 0) goto L_0x022c
            goto L_0x0223
        L_0x022c:
            int r5 = r5.intValue()
        L_0x0230:
            r4.logAlertingHeader(r5)
            com.android.systemui.statusbar.notification.stack.NotificationSectionsLogger r4 = r7.logger
            if (r0 != 0) goto L_0x0238
            goto L_0x0244
        L_0x0238:
            java.lang.Integer r0 = r0.getTargetPosition()
            if (r0 != 0) goto L_0x023f
            goto L_0x0244
        L_0x023f:
            int r0 = r0.intValue()
            r1 = r0
        L_0x0244:
            r4.logSilentHeader(r1)
            java.lang.Iterable r0 = kotlin.sequences.SequencesKt___SequencesKt.asIterable(r12)
            java.util.List r0 = kotlin.collections.CollectionsKt___CollectionsKt.reversed(r0)
            java.util.Iterator r0 = r0.iterator()
        L_0x0253:
            boolean r1 = r0.hasNext()
            if (r1 == 0) goto L_0x0263
            java.lang.Object r1 = r0.next()
            com.android.systemui.statusbar.notification.stack.NotificationSectionsManager$SectionUpdateState r1 = (com.android.systemui.statusbar.notification.stack.NotificationSectionsManager.SectionUpdateState) r1
            r1.adjustViewPosition()
            goto L_0x0253
        L_0x0263:
            com.android.systemui.statusbar.notification.stack.NotificationSectionsLogger r0 = r7.logger
            java.lang.String r1 = "Final order:"
            r0.logStr(r1)
            r23.logShadeContents()
            com.android.systemui.statusbar.notification.stack.NotificationSectionsLogger r0 = r7.logger
            java.lang.String r1 = "Section boundary update complete"
            r0.logStr(r1)
            com.android.systemui.statusbar.notification.stack.SectionHeaderView r0 = r23.getSilentHeaderView()
            if (r0 != 0) goto L_0x027b
            goto L_0x029b
        L_0x027b:
            android.view.ViewGroup r1 = r7.parent
            if (r1 == 0) goto L_0x029c
            boolean r4 = r1 instanceof com.android.systemui.statusbar.notification.stack.NotificationStackScrollLayout
            if (r4 == 0) goto L_0x028a
            com.android.systemui.statusbar.notification.stack.NotificationStackScrollLayout r1 = (com.android.systemui.statusbar.notification.stack.NotificationStackScrollLayout) r1
            boolean r8 = r1.hasActiveClearableNotifications(r3)
            goto L_0x0296
        L_0x028a:
            boolean r4 = r1 instanceof com.android.systemui.statusbar.notification.stack.DesktopNotificationStackScrollLayout
            if (r4 == 0) goto L_0x0295
            com.android.systemui.statusbar.notification.stack.DesktopNotificationStackScrollLayout r1 = (com.android.systemui.statusbar.notification.stack.DesktopNotificationStackScrollLayout) r1
            boolean r8 = r1.hasActiveClearableNotifications(r3)
            goto L_0x0296
        L_0x0295:
            r8 = r2
        L_0x0296:
            r0.setAreThereDismissableGentleNotifs(r8)
            kotlin.Unit r0 = kotlin.Unit.INSTANCE
        L_0x029b:
            return
        L_0x029c:
            kotlin.jvm.internal.Intrinsics.throwUninitializedPropertyAccessException(r17)
            r0 = 0
            throw r0
        L_0x02a1:
            r0 = 0
            kotlin.jvm.internal.Intrinsics.throwUninitializedPropertyAccessException(r17)
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.statusbar.notification.stack.NotificationSectionsManager.updateSectionBoundaries(java.lang.String):void");
    }

    private static final SectionUpdateState<ExpandableView> updateSectionBoundaries$getSectionState(NotificationSectionsManager notificationSectionsManager, SectionUpdateState<? extends MediaHeaderView> sectionUpdateState, SectionUpdateState<? extends SectionHeaderView> sectionUpdateState2, SectionUpdateState<? extends SectionHeaderView> sectionUpdateState3, SectionUpdateState<? extends SectionHeaderView> sectionUpdateState4, SectionUpdateState<? extends SectionHeaderView> sectionUpdateState5, View view) {
        if (view == notificationSectionsManager.mediaControlsView) {
            return sectionUpdateState;
        }
        if (view == notificationSectionsManager.getIncomingHeaderView()) {
            return sectionUpdateState2;
        }
        if (view == notificationSectionsManager.getPeopleHeaderView()) {
            return sectionUpdateState3;
        }
        if (view == notificationSectionsManager.getAlertingHeaderView()) {
            return sectionUpdateState4;
        }
        if (view == notificationSectionsManager.getSilentHeaderView()) {
            return sectionUpdateState5;
        }
        return null;
    }

    /* compiled from: NotificationSectionsManager.kt */
    private static abstract class SectionBounds {
        public /* synthetic */ SectionBounds(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }

        private SectionBounds() {
        }

        /* compiled from: NotificationSectionsManager.kt */
        public static final class Many extends SectionBounds {
            @NotNull
            private final ExpandableView first;
            @NotNull
            private final ExpandableView last;

            public static /* synthetic */ Many copy$default(Many many, ExpandableView expandableView, ExpandableView expandableView2, int i, Object obj) {
                if ((i & 1) != 0) {
                    expandableView = many.first;
                }
                if ((i & 2) != 0) {
                    expandableView2 = many.last;
                }
                return many.copy(expandableView, expandableView2);
            }

            @NotNull
            public final Many copy(@NotNull ExpandableView expandableView, @NotNull ExpandableView expandableView2) {
                Intrinsics.checkNotNullParameter(expandableView, "first");
                Intrinsics.checkNotNullParameter(expandableView2, "last");
                return new Many(expandableView, expandableView2);
            }

            public boolean equals(@Nullable Object obj) {
                if (this == obj) {
                    return true;
                }
                if (!(obj instanceof Many)) {
                    return false;
                }
                Many many = (Many) obj;
                return Intrinsics.areEqual((Object) this.first, (Object) many.first) && Intrinsics.areEqual((Object) this.last, (Object) many.last);
            }

            public int hashCode() {
                return (this.first.hashCode() * 31) + this.last.hashCode();
            }

            @NotNull
            public String toString() {
                return "Many(first=" + this.first + ", last=" + this.last + ')';
            }

            @NotNull
            public final ExpandableView getFirst() {
                return this.first;
            }

            @NotNull
            public final ExpandableView getLast() {
                return this.last;
            }

            /* JADX INFO: super call moved to the top of the method (can break code semantics) */
            public Many(@NotNull ExpandableView expandableView, @NotNull ExpandableView expandableView2) {
                super((DefaultConstructorMarker) null);
                Intrinsics.checkNotNullParameter(expandableView, "first");
                Intrinsics.checkNotNullParameter(expandableView2, "last");
                this.first = expandableView;
                this.last = expandableView2;
            }
        }

        /* compiled from: NotificationSectionsManager.kt */
        public static final class One extends SectionBounds {
            @NotNull
            private final ExpandableView lone;

            public boolean equals(@Nullable Object obj) {
                if (this == obj) {
                    return true;
                }
                return (obj instanceof One) && Intrinsics.areEqual((Object) this.lone, (Object) ((One) obj).lone);
            }

            public int hashCode() {
                return this.lone.hashCode();
            }

            @NotNull
            public String toString() {
                return "One(lone=" + this.lone + ')';
            }

            /* JADX INFO: super call moved to the top of the method (can break code semantics) */
            public One(@NotNull ExpandableView expandableView) {
                super((DefaultConstructorMarker) null);
                Intrinsics.checkNotNullParameter(expandableView, "lone");
                this.lone = expandableView;
            }

            @NotNull
            public final ExpandableView getLone() {
                return this.lone;
            }
        }

        /* compiled from: NotificationSectionsManager.kt */
        public static final class None extends SectionBounds {
            @NotNull
            public static final None INSTANCE = new None();

            private None() {
                super((DefaultConstructorMarker) null);
            }
        }

        @NotNull
        public final SectionBounds addNotif(@NotNull ExpandableView expandableView) {
            Intrinsics.checkNotNullParameter(expandableView, "notif");
            if (this instanceof None) {
                return new One(expandableView);
            }
            if (this instanceof One) {
                return new Many(((One) this).getLone(), expandableView);
            }
            if (this instanceof Many) {
                return Many.copy$default((Many) this, (ExpandableView) null, expandableView, 1, (Object) null);
            }
            throw new NoWhenBranchMatchedException();
        }

        public final boolean updateSection(@NotNull NotificationSection notificationSection) {
            Intrinsics.checkNotNullParameter(notificationSection, "section");
            if (this instanceof None) {
                return setFirstAndLastVisibleChildren(notificationSection, (ExpandableView) null, (ExpandableView) null);
            }
            if (this instanceof One) {
                One one = (One) this;
                return setFirstAndLastVisibleChildren(notificationSection, one.getLone(), one.getLone());
            } else if (this instanceof Many) {
                Many many = (Many) this;
                return setFirstAndLastVisibleChildren(notificationSection, many.getFirst(), many.getLast());
            } else {
                throw new NoWhenBranchMatchedException();
            }
        }

        private final boolean setFirstAndLastVisibleChildren(NotificationSection notificationSection, ExpandableView expandableView, ExpandableView expandableView2) {
            return notificationSection.setFirstVisibleChild(expandableView) || notificationSection.setLastVisibleChild(expandableView2);
        }
    }

    public final boolean updateFirstAndLastViewsForAllSections(@NotNull NotificationSection[] notificationSectionArr, @NotNull List<? extends ExpandableView> list) {
        SparseArray sparseArray;
        Intrinsics.checkNotNullParameter(notificationSectionArr, "sections");
        Intrinsics.checkNotNullParameter(list, "children");
        C1705x74c1ef3e notificationSectionsManager$updateFirstAndLastViewsForAllSections$$inlined$groupingBy$1 = new C1705x74c1ef3e(CollectionsKt___CollectionsKt.asSequence(list), this);
        SectionBounds.None none = SectionBounds.None.INSTANCE;
        int length = notificationSectionArr.length;
        if (length < 0) {
            sparseArray = new SparseArray();
        } else {
            sparseArray = new SparseArray(length);
        }
        Iterator sourceIterator = notificationSectionsManager$updateFirstAndLastViewsForAllSections$$inlined$groupingBy$1.sourceIterator();
        while (sourceIterator.hasNext()) {
            Object next = sourceIterator.next();
            int intValue = ((Number) notificationSectionsManager$updateFirstAndLastViewsForAllSections$$inlined$groupingBy$1.keyOf(next)).intValue();
            Object obj = sparseArray.get(intValue);
            if (obj == null) {
                obj = none;
            }
            sparseArray.put(intValue, ((SectionBounds) obj).addNotif((ExpandableView) next));
        }
        boolean z = false;
        for (NotificationSection notificationSection : notificationSectionArr) {
            SectionBounds sectionBounds = (SectionBounds) sparseArray.get(notificationSection.getBucket());
            if (sectionBounds == null) {
                sectionBounds = SectionBounds.None.INSTANCE;
            }
            z = sectionBounds.updateSection(notificationSection) || z;
        }
        return z;
    }

    public final void setHeaderForegroundColor(int i) {
        SectionHeaderView peopleHeaderView = getPeopleHeaderView();
        if (peopleHeaderView != null) {
            peopleHeaderView.setForegroundColor(i);
        }
        SectionHeaderView silentHeaderView = getSilentHeaderView();
        if (silentHeaderView != null) {
            silentHeaderView.setForegroundColor(i);
        }
        SectionHeaderView alertingHeaderView = getAlertingHeaderView();
        if (alertingHeaderView != null) {
            alertingHeaderView.setForegroundColor(i);
        }
    }

    /* compiled from: NotificationSectionsManager.kt */
    public static final class Companion {
        public /* synthetic */ Companion(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }

        private Companion() {
        }
    }
}
