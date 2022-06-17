package com.android.launcher3.icons;

import android.content.ComponentName;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.UserHandle;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.Log;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class IconProvider {
    private static final int CONFIG_ICON_MASK_RES_ID = Resources.getSystem().getIdentifier("config_icon_mask", "string", "android");
    private static final Map<String, ThemedIconDrawable$ThemeData> DISABLED_MAP = Collections.emptyMap();
    private final String ACTION_OVERLAY_CHANGED;
    private final List<ComponentName> mCalendars;
    private final List<ComponentName> mClocks;
    private final Context mContext;
    private Map<String, ThemedIconDrawable$ThemeData> mThemedIconMap;

    public IconProvider(Context context) {
        this(context, false);
    }

    public IconProvider(Context context, boolean z) {
        this.ACTION_OVERLAY_CHANGED = "android.intent.action.OVERLAY_CHANGED";
        this.mContext = context;
        this.mCalendars = parseComponents(context, R$array.dynamic_icon_calendar_component_list);
        this.mClocks = parseComponents(context, R$array.dynamic_icon_clock_component_list);
        if (!z) {
            this.mThemedIconMap = DISABLED_MAP;
        }
    }

    public Drawable getIcon(ActivityInfo activityInfo, int i) {
        return getIconWithOverrides(new IconProvider$$ExternalSyntheticLambda6(activityInfo), UserHandle.getUserHandleForUid(activityInfo.applicationInfo.uid), i, new IconProvider$$ExternalSyntheticLambda7(this, activityInfo, i));
    }

    private Drawable getIconWithOverrides(Supplier<ComponentName> supplier, UserHandle userHandle, int i, Supplier<Drawable> supplier2) {
        int i2;
        ComponentName componentName = supplier.get();
        String packageName = componentName.getPackageName();
        Drawable loadIcon = CustomAppIcons.getInstance().loadIcon(this.mContext, componentName);
        int i3 = 0;
        if (this.mCalendars.stream().map(IconProvider$$ExternalSyntheticLambda0.INSTANCE).anyMatch(new IconProvider$$ExternalSyntheticLambda3(packageName))) {
            if (loadIcon == null) {
                loadIcon = loadCalendarDrawable(supplier, i);
            }
            i2 = 1;
        } else if (this.mClocks.stream().map(IconProvider$$ExternalSyntheticLambda0.INSTANCE).anyMatch(new IconProvider$$ExternalSyntheticLambda2(packageName))) {
            loadIcon = loadClockDrawable(supplier, i, loadIcon);
            i2 = 2;
        } else {
            i2 = 0;
        }
        if (loadIcon == null) {
            loadIcon = supplier2.get();
        } else {
            i3 = i2;
        }
        ThemedIconDrawable$ThemeData themedIconDrawable$ThemeData = getThemedIconMap().get(packageName);
        return themedIconDrawable$ThemeData != null ? themedIconDrawable$ThemeData.wrapDrawable(loadIcon, i3) : loadIcon;
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Removed duplicated region for block: B:10:? A[RETURN, SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:8:0x001c  */
    /* renamed from: loadActivityInfoIcon */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public android.graphics.drawable.Drawable lambda$getIcon$3(android.content.pm.ActivityInfo r4, int r5) {
        /*
            r3 = this;
            int r0 = r4.getIconResource()
            if (r5 == 0) goto L_0x0019
            if (r0 == 0) goto L_0x0019
            android.content.Context r1 = r3.mContext     // Catch:{ NameNotFoundException | NotFoundException -> 0x0019 }
            android.content.pm.PackageManager r1 = r1.getPackageManager()     // Catch:{ NameNotFoundException | NotFoundException -> 0x0019 }
            android.content.pm.ApplicationInfo r2 = r4.applicationInfo     // Catch:{ NameNotFoundException | NotFoundException -> 0x0019 }
            android.content.res.Resources r1 = r1.getResourcesForApplication(r2)     // Catch:{ NameNotFoundException | NotFoundException -> 0x0019 }
            android.graphics.drawable.Drawable r5 = r1.getDrawableForDensity(r0, r5)     // Catch:{ NameNotFoundException | NotFoundException -> 0x0019 }
            goto L_0x001a
        L_0x0019:
            r5 = 0
        L_0x001a:
            if (r5 != 0) goto L_0x0026
            android.content.Context r3 = r3.mContext
            android.content.pm.PackageManager r3 = r3.getPackageManager()
            android.graphics.drawable.Drawable r5 = r4.loadIcon(r3)
        L_0x0026:
            return r5
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.icons.IconProvider.lambda$getIcon$3(android.content.pm.ActivityInfo, int):android.graphics.drawable.Drawable");
    }

    private Map<String, ThemedIconDrawable$ThemeData> getThemedIconMap() {
        Map<String, ThemedIconDrawable$ThemeData> map = this.mThemedIconMap;
        if (map != null) {
            return map;
        }
        ArrayMap arrayMap = new ArrayMap();
        try {
            Resources resources = this.mContext.getResources();
            int identifier = resources.getIdentifier("grayscale_icon_map", "xml", this.mContext.getPackageName());
            if (identifier != 0) {
                XmlResourceParser xml = resources.getXml(identifier);
                int depth = xml.getDepth();
                while (true) {
                    int next = xml.next();
                    if (next == 2 || next == 1) {
                    }
                }
                while (true) {
                    int next2 = xml.next();
                    if ((next2 == 3 && xml.getDepth() <= depth) || next2 == 1) {
                        break;
                    } else if (next2 == 2) {
                        if ("icon".equals(xml.getName())) {
                            String attributeValue = xml.getAttributeValue((String) null, "package");
                            int attributeResourceValue = xml.getAttributeResourceValue((String) null, "drawable", 0);
                            if (attributeResourceValue != 0 && !TextUtils.isEmpty(attributeValue)) {
                                arrayMap.put(attributeValue, new ThemedIconDrawable$ThemeData(resources, attributeResourceValue));
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            Log.e("IconProvider", "Unable to parse icon map", e);
        }
        this.mThemedIconMap = arrayMap;
        return arrayMap;
    }

    private Drawable loadCalendarDrawable(Supplier<ComponentName> supplier, int i) {
        String packageName = supplier.get().getPackageName();
        PackageManager packageManager = this.mContext.getPackageManager();
        try {
            Bundle bundle = packageManager.getActivityInfo(supplier.get(), 8320).metaData;
            Resources resourcesForApplication = packageManager.getResourcesForApplication(packageName);
            int dynamicIconId = getDynamicIconId(packageName, bundle, resourcesForApplication);
            if (dynamicIconId != 0) {
                return resourcesForApplication.getDrawableForDensity(dynamicIconId, i, (Resources.Theme) null);
            }
        } catch (PackageManager.NameNotFoundException unused) {
        }
        return null;
    }

    private Drawable loadClockDrawable(Supplier<ComponentName> supplier, int i, Drawable drawable) {
        return ClockDrawableWrapper.forPackage(this.mContext, supplier.get().getPackageName(), i, drawable);
    }

    private int getDynamicIconId(String str, Bundle bundle, Resources resources) {
        if (bundle == null) {
            return 0;
        }
        boolean isSystemUseRoundIcon = getIsSystemUseRoundIcon();
        StringBuilder sb = new StringBuilder();
        sb.append(str);
        sb.append(isSystemUseRoundIcon ? ".dynamic_icons_nexus_round" : ".dynamic_icons");
        int i = bundle.getInt(sb.toString(), 0);
        if (i == 0) {
            return 0;
        }
        try {
            TypedArray obtainTypedArray = resources.obtainTypedArray(i);
            int resourceId = obtainTypedArray.getResourceId(getDay(), 0);
            obtainTypedArray.recycle();
            return resourceId;
        } catch (Resources.NotFoundException unused) {
            return 0;
        }
    }

    static int getDay() {
        return Calendar.getInstance().get(5) - 1;
    }

    private boolean getIsSystemUseRoundIcon() {
        try {
            return Resources.getSystem().getBoolean(((Integer) Class.forName("com.android.internal.R$bool").getField("config_useRoundIcon").get((Object) null)).intValue());
        } catch (ClassNotFoundException | IllegalAccessException | NoSuchFieldException e) {
            Log.e("IconProvider", "getIsSystemUseRoundIcon -> Exception", e);
            return false;
        }
    }

    private static List<ComponentName> parseComponents(Context context, int i) {
        return (List) Arrays.stream(context.getResources().getStringArray(i)).filter(IconProvider$$ExternalSyntheticLambda5.INSTANCE).map(IconProvider$$ExternalSyntheticLambda1.INSTANCE).filter(IconProvider$$ExternalSyntheticLambda4.INSTANCE).collect(Collectors.toList());
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$parseComponents$6(String str) {
        return !TextUtils.isEmpty(str);
    }
}
