package com.android.systemui.privacy;

import android.content.Context;
import android.graphics.drawable.Drawable;
import androidx.appcompat.R$styleable;
import com.android.systemui.R$string;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import kotlin.Pair;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

/* compiled from: PrivacyChipBuilder.kt */
public final class PrivacyChipBuilder {
    @NotNull
    private final List<Pair<PrivacyApplication, List<PrivacyType>>> appsAndTypes;
    @NotNull
    private final Context context;
    private final String lastSeparator;
    private final String separator;
    @NotNull
    private final List<PrivacyType> types;

    public PrivacyChipBuilder(@NotNull Context context2, @NotNull List<PrivacyItem> list) {
        Intrinsics.checkNotNullParameter(context2, "context");
        Intrinsics.checkNotNullParameter(list, "itemsList");
        this.context = context2;
        this.separator = context2.getString(R$string.ongoing_privacy_dialog_separator);
        this.lastSeparator = context2.getString(R$string.ongoing_privacy_dialog_last_separator);
        LinkedHashMap linkedHashMap = new LinkedHashMap();
        for (PrivacyItem privacyItem : list) {
            PrivacyApplication application = privacyItem.getApplication();
            Object obj = linkedHashMap.get(application);
            if (obj == null) {
                obj = new ArrayList();
                linkedHashMap.put(application, obj);
            }
            ((List) obj).add(privacyItem.getPrivacyType());
        }
        this.appsAndTypes = CollectionsKt___CollectionsKt.sortedWith(MapsKt___MapsKt.toList(linkedHashMap), ComparisonsKt__ComparisonsKt.compareBy(C11413.INSTANCE, C11424.INSTANCE));
        ArrayList arrayList = new ArrayList(CollectionsKt__IterablesKt.collectionSizeOrDefault(list, 10));
        for (PrivacyItem privacyType : list) {
            arrayList.add(privacyType.getPrivacyType());
        }
        this.types = CollectionsKt___CollectionsKt.sorted(CollectionsKt___CollectionsKt.distinct(arrayList));
    }

    @NotNull
    public final List<Drawable> generateIcons() {
        List<PrivacyType> list = this.types;
        ArrayList arrayList = new ArrayList(CollectionsKt__IterablesKt.collectionSizeOrDefault(list, 10));
        for (PrivacyType icon : list) {
            arrayList.add(icon.getIcon(this.context));
        }
        return arrayList;
    }

    private final <T> StringBuilder joinWithAnd(List<? extends T> list) {
        List<? extends T> subList = list.subList(0, list.size() - 1);
        StringBuilder sb = new StringBuilder();
        String str = this.separator;
        Intrinsics.checkNotNullExpressionValue(str, "separator");
        StringBuilder sb2 = (StringBuilder) CollectionsKt___CollectionsKt.joinTo$default(subList, sb, str, (CharSequence) null, (CharSequence) null, 0, (CharSequence) null, (Function1) null, R$styleable.AppCompatTheme_windowMinWidthMajor, (Object) null);
        sb2.append(this.lastSeparator);
        sb2.append(CollectionsKt___CollectionsKt.last(list));
        return sb2;
    }

    @NotNull
    public final String joinTypes() {
        int size = this.types.size();
        if (size == 0) {
            return "";
        }
        if (size != 1) {
            List<PrivacyType> list = this.types;
            ArrayList arrayList = new ArrayList(CollectionsKt__IterablesKt.collectionSizeOrDefault(list, 10));
            for (PrivacyType name : list) {
                arrayList.add(name.getName(this.context));
            }
            String sb = joinWithAnd(arrayList).toString();
            Intrinsics.checkNotNullExpressionValue(sb, "types.map { it.getName(context) }.joinWithAnd().toString()");
            return sb;
        }
        String name2 = this.types.get(0).getName(this.context);
        Intrinsics.checkNotNullExpressionValue(name2, "types[0].getName(context)");
        return name2;
    }
}
