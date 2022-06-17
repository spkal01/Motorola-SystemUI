package com.android.p011wm.shell.common;

import android.graphics.Rect;
import android.util.Log;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import kotlin.Lazy;
import kotlin.Pair;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Ref$ObjectRef;
import org.jetbrains.annotations.NotNull;

/* renamed from: com.android.wm.shell.common.FloatingContentCoordinator */
/* compiled from: FloatingContentCoordinator.kt */
public final class FloatingContentCoordinator {
    @NotNull
    public static final Companion Companion = new Companion((DefaultConstructorMarker) null);
    @NotNull
    private final Map<FloatingContent, Rect> allContentBounds = new HashMap();
    private boolean currentlyResolvingConflicts;

    /* renamed from: com.android.wm.shell.common.FloatingContentCoordinator$FloatingContent */
    /* compiled from: FloatingContentCoordinator.kt */
    public interface FloatingContent {
        @NotNull
        Rect getAllowedFloatingBoundsRegion();

        @NotNull
        Rect getFloatingBoundsOnScreen();

        void moveToBounds(@NotNull Rect rect);

        @NotNull
        Rect calculateNewBoundsOnOverlap(@NotNull Rect rect, @NotNull List<Rect> list) {
            Intrinsics.checkNotNullParameter(rect, "overlappingContentBounds");
            Intrinsics.checkNotNullParameter(list, "otherContentBounds");
            return FloatingContentCoordinator.Companion.findAreaForContentVertically(getFloatingBoundsOnScreen(), rect, list, getAllowedFloatingBoundsRegion());
        }
    }

    public final void onContentAdded(@NotNull FloatingContent floatingContent) {
        Intrinsics.checkNotNullParameter(floatingContent, "newContent");
        updateContentBounds();
        this.allContentBounds.put(floatingContent, floatingContent.getFloatingBoundsOnScreen());
        maybeMoveConflictingContent(floatingContent);
    }

    public final void onContentMoved(@NotNull FloatingContent floatingContent) {
        Intrinsics.checkNotNullParameter(floatingContent, "content");
        if (!this.currentlyResolvingConflicts) {
            if (!this.allContentBounds.containsKey(floatingContent)) {
                Log.wtf("FloatingCoordinator", "Received onContentMoved call before onContentAdded! This should never happen.");
                return;
            }
            updateContentBounds();
            maybeMoveConflictingContent(floatingContent);
        }
    }

    public final void onContentRemoved(@NotNull FloatingContent floatingContent) {
        Intrinsics.checkNotNullParameter(floatingContent, "removedContent");
        this.allContentBounds.remove(floatingContent);
    }

    private final void maybeMoveConflictingContent(FloatingContent floatingContent) {
        this.currentlyResolvingConflicts = true;
        Rect rect = this.allContentBounds.get(floatingContent);
        Intrinsics.checkNotNull(rect);
        Map<FloatingContent, Rect> map = this.allContentBounds;
        LinkedHashMap linkedHashMap = new LinkedHashMap();
        Iterator<Map.Entry<FloatingContent, Rect>> it = map.entrySet().iterator();
        while (true) {
            boolean z = false;
            if (!it.hasNext()) {
                break;
            }
            Map.Entry next = it.next();
            Rect rect2 = (Rect) next.getValue();
            if (!Intrinsics.areEqual((Object) (FloatingContent) next.getKey(), (Object) floatingContent) && Rect.intersects(rect, rect2)) {
                z = true;
            }
            if (z) {
                linkedHashMap.put(next.getKey(), next.getValue());
            }
        }
        for (Map.Entry entry : linkedHashMap.entrySet()) {
            FloatingContent floatingContent2 = (FloatingContent) entry.getKey();
            Rect calculateNewBoundsOnOverlap = floatingContent2.calculateNewBoundsOnOverlap(rect, CollectionsKt___CollectionsKt.minus(CollectionsKt___CollectionsKt.minus(this.allContentBounds.values(), (Rect) entry.getValue()), rect));
            if (!calculateNewBoundsOnOverlap.isEmpty()) {
                floatingContent2.moveToBounds(calculateNewBoundsOnOverlap);
                this.allContentBounds.put(floatingContent2, floatingContent2.getFloatingBoundsOnScreen());
            }
        }
        this.currentlyResolvingConflicts = false;
    }

    private final void updateContentBounds() {
        for (FloatingContent floatingContent : this.allContentBounds.keySet()) {
            this.allContentBounds.put(floatingContent, floatingContent.getFloatingBoundsOnScreen());
        }
    }

    /* renamed from: com.android.wm.shell.common.FloatingContentCoordinator$Companion */
    /* compiled from: FloatingContentCoordinator.kt */
    public static final class Companion {
        public /* synthetic */ Companion(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }

        private Companion() {
        }

        @NotNull
        public final Rect findAreaForContentVertically(@NotNull Rect rect, @NotNull Rect rect2, @NotNull Collection<Rect> collection, @NotNull Rect rect3) {
            Intrinsics.checkNotNullParameter(rect, "contentRect");
            Intrinsics.checkNotNullParameter(rect2, "newlyOverlappingRect");
            Intrinsics.checkNotNullParameter(collection, "exclusionRects");
            Intrinsics.checkNotNullParameter(rect3, "allowedBounds");
            boolean z = true;
            boolean z2 = rect2.centerY() < rect.centerY();
            ArrayList arrayList = new ArrayList();
            for (T next : collection) {
                if (FloatingContentCoordinator.Companion.rectsIntersectVertically((Rect) next, rect)) {
                    arrayList.add(next);
                }
            }
            ArrayList arrayList2 = new ArrayList();
            ArrayList arrayList3 = new ArrayList();
            for (Object next2 : arrayList) {
                if (((Rect) next2).top < rect.top) {
                    arrayList2.add(next2);
                } else {
                    arrayList3.add(next2);
                }
            }
            Pair pair = new Pair(arrayList2, arrayList3);
            Ref$ObjectRef ref$ObjectRef = new Ref$ObjectRef();
            ref$ObjectRef.element = pair.component1();
            Ref$ObjectRef ref$ObjectRef2 = new Ref$ObjectRef();
            ref$ObjectRef2.element = pair.component2();
            Lazy lazy = LazyKt__LazyJVMKt.lazy(new C2297xa8ad3931(rect, ref$ObjectRef, rect2));
            Lazy lazy2 = LazyKt__LazyJVMKt.lazy(new C2298xe284ccc5(rect, ref$ObjectRef2, rect2));
            Lazy lazy3 = LazyKt__LazyJVMKt.lazy(new C2299x994e5850(rect3, lazy));
            Lazy lazy4 = LazyKt__LazyJVMKt.lazy(new C2300xf7317e4(rect3, lazy2));
            if ((!z2 || !m701findAreaForContentVertically$lambda5(lazy4)) && (z2 || m700findAreaForContentVertically$lambda4(lazy3))) {
                z = false;
            }
            Rect r7 = z ? m699findAreaForContentVertically$lambda3(lazy2) : m698findAreaForContentVertically$lambda2(lazy);
            return rect3.contains(r7) ? r7 : new Rect();
        }

        /* access modifiers changed from: private */
        /* renamed from: findAreaForContentVertically$lambda-2  reason: not valid java name */
        public static final Rect m698findAreaForContentVertically$lambda2(Lazy<Rect> lazy) {
            return lazy.getValue();
        }

        /* access modifiers changed from: private */
        /* renamed from: findAreaForContentVertically$lambda-3  reason: not valid java name */
        public static final Rect m699findAreaForContentVertically$lambda3(Lazy<Rect> lazy) {
            return lazy.getValue();
        }

        /* renamed from: findAreaForContentVertically$lambda-4  reason: not valid java name */
        private static final boolean m700findAreaForContentVertically$lambda4(Lazy<Boolean> lazy) {
            return lazy.getValue().booleanValue();
        }

        /* renamed from: findAreaForContentVertically$lambda-5  reason: not valid java name */
        private static final boolean m701findAreaForContentVertically$lambda5(Lazy<Boolean> lazy) {
            return lazy.getValue().booleanValue();
        }

        /* JADX WARNING: Code restructure failed: missing block: B:4:0x000a, code lost:
            r2 = r3.right;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        private final boolean rectsIntersectVertically(android.graphics.Rect r3, android.graphics.Rect r4) {
            /*
                r2 = this;
                int r2 = r3.left
                int r0 = r4.left
                if (r2 < r0) goto L_0x000a
                int r1 = r4.right
                if (r2 <= r1) goto L_0x0012
            L_0x000a:
                int r2 = r3.right
                int r3 = r4.right
                if (r2 > r3) goto L_0x0014
                if (r2 < r0) goto L_0x0014
            L_0x0012:
                r2 = 1
                goto L_0x0015
            L_0x0014:
                r2 = 0
            L_0x0015:
                return r2
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.p011wm.shell.common.FloatingContentCoordinator.Companion.rectsIntersectVertically(android.graphics.Rect, android.graphics.Rect):boolean");
        }

        @NotNull
        public final Rect findAreaForContentAboveOrBelow(@NotNull Rect rect, @NotNull Collection<Rect> collection, boolean z) {
            Intrinsics.checkNotNullParameter(rect, "contentRect");
            Intrinsics.checkNotNullParameter(collection, "exclusionRects");
            List<T> sortedWith = CollectionsKt___CollectionsKt.sortedWith(collection, new C2296x8b489ee0(z));
            Rect rect2 = new Rect(rect);
            for (T t : sortedWith) {
                if (!Rect.intersects(rect2, t)) {
                    break;
                }
                rect2.offsetTo(rect2.left, t.top + (z ? -rect.height() : t.height()));
            }
            return rect2;
        }
    }
}
