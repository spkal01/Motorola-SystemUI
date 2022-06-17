package com.android.p011wm.shell.bubbles.storage;

import android.content.pm.LauncherApps;
import android.os.UserHandle;
import android.util.SparseArray;
import com.android.internal.annotations.VisibleForTesting;
import com.android.p011wm.shell.bubbles.ShortcutKey;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

/* renamed from: com.android.wm.shell.bubbles.storage.BubbleVolatileRepository */
/* compiled from: BubbleVolatileRepository.kt */
public final class BubbleVolatileRepository {
    private int capacity = 16;
    @NotNull
    private SparseArray<List<BubbleEntity>> entitiesByUser = new SparseArray<>();
    @NotNull
    private final LauncherApps launcherApps;

    @VisibleForTesting
    public static /* synthetic */ void getCapacity$annotations() {
    }

    public BubbleVolatileRepository(@NotNull LauncherApps launcherApps2) {
        Intrinsics.checkNotNullParameter(launcherApps2, "launcherApps");
        this.launcherApps = launcherApps2;
    }

    @NotNull
    public final synchronized SparseArray<List<BubbleEntity>> getBubbles() {
        SparseArray<List<BubbleEntity>> sparseArray;
        sparseArray = new SparseArray<>();
        int i = 0;
        int size = this.entitiesByUser.size();
        if (size > 0) {
            while (true) {
                int i2 = i + 1;
                int keyAt = this.entitiesByUser.keyAt(i);
                List valueAt = this.entitiesByUser.valueAt(i);
                Intrinsics.checkNotNullExpressionValue(valueAt, "v");
                sparseArray.put(keyAt, CollectionsKt___CollectionsKt.toList(valueAt));
                if (i2 >= size) {
                    break;
                }
                i = i2;
            }
        }
        return sparseArray;
    }

    @NotNull
    public final synchronized List<BubbleEntity> getEntities(int i) {
        List<BubbleEntity> list;
        list = this.entitiesByUser.get(i);
        if (list == null) {
            list = new ArrayList<>();
            this.entitiesByUser.put(i, list);
        } else {
            Intrinsics.checkNotNullExpressionValue(list, "entities");
        }
        return list;
    }

    public final synchronized void addBubbles(int i, @NotNull List<BubbleEntity> list) {
        Intrinsics.checkNotNullParameter(list, "bubbles");
        if (!list.isEmpty()) {
            List entities = getEntities(i);
            List<T> takeLast = CollectionsKt___CollectionsKt.takeLast(list, this.capacity);
            ArrayList arrayList = new ArrayList();
            for (T next : takeLast) {
                if (!entities.removeIf(new BubbleVolatileRepository$addBubbles$uniqueBubbles$1$1((BubbleEntity) next))) {
                    arrayList.add(next);
                }
            }
            int size = (entities.size() + takeLast.size()) - this.capacity;
            if (size > 0) {
                uncache(CollectionsKt___CollectionsKt.take(entities, size));
                entities = CollectionsKt___CollectionsKt.toMutableList(CollectionsKt___CollectionsKt.drop(entities, size));
            }
            entities.addAll(takeLast);
            this.entitiesByUser.put(i, entities);
            cache(arrayList);
        }
    }

    public final synchronized void removeBubbles(int i, @NotNull List<BubbleEntity> list) {
        Intrinsics.checkNotNullParameter(list, "bubbles");
        ArrayList arrayList = new ArrayList();
        for (T next : list) {
            if (getEntities(i).removeIf(new BubbleVolatileRepository$removeBubbles$1$1((BubbleEntity) next))) {
                arrayList.add(next);
            }
        }
        uncache(arrayList);
    }

    private final void cache(List<BubbleEntity> list) {
        LinkedHashMap linkedHashMap = new LinkedHashMap();
        for (T next : list) {
            BubbleEntity bubbleEntity = (BubbleEntity) next;
            ShortcutKey shortcutKey = new ShortcutKey(bubbleEntity.getUserId(), bubbleEntity.getPackageName());
            Object obj = linkedHashMap.get(shortcutKey);
            if (obj == null) {
                obj = new ArrayList();
                linkedHashMap.put(shortcutKey, obj);
            }
            ((List) obj).add(next);
        }
        for (Map.Entry entry : linkedHashMap.entrySet()) {
            ShortcutKey shortcutKey2 = (ShortcutKey) entry.getKey();
            List<BubbleEntity> list2 = (List) entry.getValue();
            LauncherApps launcherApps2 = this.launcherApps;
            String pkg = shortcutKey2.getPkg();
            ArrayList arrayList = new ArrayList(CollectionsKt__IterablesKt.collectionSizeOrDefault(list2, 10));
            for (BubbleEntity shortcutId : list2) {
                arrayList.add(shortcutId.getShortcutId());
            }
            launcherApps2.cacheShortcuts(pkg, arrayList, UserHandle.of(shortcutKey2.getUserId()), 1);
        }
    }

    private final void uncache(List<BubbleEntity> list) {
        LinkedHashMap linkedHashMap = new LinkedHashMap();
        for (T next : list) {
            BubbleEntity bubbleEntity = (BubbleEntity) next;
            ShortcutKey shortcutKey = new ShortcutKey(bubbleEntity.getUserId(), bubbleEntity.getPackageName());
            Object obj = linkedHashMap.get(shortcutKey);
            if (obj == null) {
                obj = new ArrayList();
                linkedHashMap.put(shortcutKey, obj);
            }
            ((List) obj).add(next);
        }
        for (Map.Entry entry : linkedHashMap.entrySet()) {
            ShortcutKey shortcutKey2 = (ShortcutKey) entry.getKey();
            List<BubbleEntity> list2 = (List) entry.getValue();
            LauncherApps launcherApps2 = this.launcherApps;
            String pkg = shortcutKey2.getPkg();
            ArrayList arrayList = new ArrayList(CollectionsKt__IterablesKt.collectionSizeOrDefault(list2, 10));
            for (BubbleEntity shortcutId : list2) {
                arrayList.add(shortcutId.getShortcutId());
            }
            launcherApps2.uncacheShortcuts(pkg, arrayList, UserHandle.of(shortcutKey2.getUserId()), 1);
        }
    }
}
