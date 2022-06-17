package com.android.launcher3.icons;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.Log;
import com.android.launcher3.util.ReflectUtils;
import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public final class CustomAppIcons {
    public static final Pattern PATTERN_DOT = Pattern.compile("\\.");
    public static final Pattern PATTERN_UNDERSCORE = Pattern.compile("(?<!_)_(?!_)");
    public static final Pattern PATTERN_UNDERSCORE2 = Pattern.compile("__");
    private static CustomAppIcons sInstance;
    private SoftReference<Context> mCaiContext;
    private Map<String, Integer> mCustomAppIconMap;
    private final Map<Integer, BaseIconFactory> mIconFactoryMap = new HashMap();
    private IconPack mIconPack;
    private Boolean mIsPrcProduct;

    public static CustomAppIcons getInstance() {
        if (sInstance == null) {
            synchronized (CustomAppIcons.class) {
                if (sInstance == null) {
                    sInstance = new CustomAppIcons();
                }
            }
        }
        return sInstance;
    }

    private CustomAppIcons() {
    }

    private Drawable getCustomAppIcon(Context context, String str, String str2) throws PackageManager.NameNotFoundException {
        String str3 = "";
        String lowerCase = str == null ? str3 : str.toLowerCase();
        if (str2 != null) {
            str3 = str2.toLowerCase();
        }
        Drawable drawable = null;
        Log.d("CustomAppIcons", "Get custom app icon: " + context + " | " + lowerCase + " | " + str3);
        String appliedIconPack = IconPackManager.getAppliedIconPack(context);
        IconPack iconPack = this.mIconPack;
        if (iconPack == null || !appliedIconPack.equals(iconPack.getPackageName())) {
            this.mIconPack = IconPack.newIconPack(context, appliedIconPack);
        }
        if (!this.mIconPack.isDefault()) {
            drawable = this.mIconPack.getAppIcon(lowerCase, str3);
            if (drawable == null && isPrcProduct()) {
                Context caiContext = getCaiContext(context);
                Integer num = getCustomAppIconMap(caiContext).get(lowerCase);
                if (num != null) {
                    drawable = caiContext.getResources().getDrawable(num.intValue(), caiContext.getTheme());
                }
            }
        } else if (isPrcProduct()) {
            Context caiContext2 = getCaiContext(context);
            Integer num2 = getCustomAppIconMap(caiContext2).get(lowerCase);
            if (num2 != null) {
                drawable = caiContext2.getResources().getDrawable(num2.intValue(), caiContext2.getTheme());
            }
        }
        Log.d("CustomAppIcons", "Icon from CustomAppIcons: " + lowerCase + " | " + str3 + " | " + drawable);
        return drawable;
    }

    private boolean isCertainPlugin(ComponentName componentName, String str, String str2) {
        if (componentName != null && TextUtils.equals(componentName.getPackageName(), str) && TextUtils.equals(componentName.getClassName(), str2)) {
            return true;
        }
        return false;
    }

    public Drawable loadIcon(Context context, ComponentName componentName) {
        try {
            if (isLauncherActivity(context.getPackageManager(), componentName) || isSupportAdditional(componentName)) {
                return getCustomAppIcon(context, componentName.getPackageName(), componentName.getClassName());
            }
            return null;
        } catch (Exception unused) {
            Log.e("CustomAppIcons", "Get error on getting custom icon drawable for " + componentName);
            return null;
        }
    }

    private boolean isSupportAdditional(ComponentName componentName) {
        return isMyScreen(componentName) || istimeWeather(componentName) || isCertainPlugin(componentName, "com.motorola.cn.smartservice", "com.motorola.plugins.CommuteSuggestionsPlugin") || isCertainPlugin(componentName, "com.motorola.cn.smartservice", "com.motorola.plugins.SmartExpressPlugin") || isCertainPlugin(componentName, "com.motorola.cn.calendar", "com.motorola.plugins.TripPlugin");
    }

    private boolean isMyScreen(ComponentName componentName) {
        return TextUtils.equals(componentName.getPackageName(), "com.motorola.myscreen");
    }

    private boolean istimeWeather(ComponentName componentName) {
        return TextUtils.equals(componentName.getPackageName(), "com.motorola.timeweatherwidget");
    }

    public static boolean isLauncherActivity(PackageManager packageManager, ComponentName componentName) {
        Intent intent = new Intent("android.intent.action.MAIN");
        intent.addCategory("android.intent.category.LAUNCHER");
        intent.setComponent(componentName);
        if (packageManager.queryIntentActivities(intent, 0).size() > 0) {
            return true;
        }
        return false;
    }

    private Context getCaiContext(Context context) throws PackageManager.NameNotFoundException {
        SoftReference<Context> softReference = this.mCaiContext;
        if (softReference == null || softReference.get() == null) {
            synchronized (this) {
                SoftReference<Context> softReference2 = this.mCaiContext;
                if (softReference2 == null || softReference2.get() == null) {
                    this.mCaiContext = new SoftReference<>(context.createPackageContext("com.motorola.launcherconfig", 0));
                }
            }
        }
        return this.mCaiContext.get();
    }

    public boolean isPrcProduct() {
        if (this.mIsPrcProduct == null) {
            synchronized (this) {
                if (this.mIsPrcProduct == null) {
                    this.mIsPrcProduct = Boolean.valueOf(TextUtils.equals(getSystemProperty("ro.product.is_prc", (String) null), "true"));
                }
            }
        }
        return this.mIsPrcProduct.booleanValue();
    }

    public static String getSystemProperty(String str, String str2) {
        try {
            String str3 = (String) Class.forName("android.os.SystemProperties").getDeclaredMethod("get", new Class[]{String.class}).invoke((Object) null, new Object[]{str});
            if (!TextUtils.isEmpty(str3)) {
                return str3;
            }
            return str2;
        } catch (Exception unused) {
            Log.d("CustomAppIcons", "Unable to read system properties");
        }
    }

    private Map<String, Integer> getCustomAppIconMap(Context context) {
        if (this.mCustomAppIconMap == null) {
            synchronized (this) {
                if (this.mCustomAppIconMap == null) {
                    this.mCustomAppIconMap = new HashMap();
                    try {
                        Log.d("CustomAppIcons", "Start cache drawables resources: " + context);
                        Resources resources = context.getResources();
                        for (String str : resources.getStringArray(resources.getIdentifier("app_icons", "array", "com.motorola.launcherconfig"))) {
                            if (str.startsWith("appicon_")) {
                                String replaceAll = PATTERN_UNDERSCORE2.matcher(PATTERN_UNDERSCORE.matcher(str.substring(8)).replaceAll(".")).replaceAll("_");
                                this.mCustomAppIconMap.put(replaceAll, Integer.valueOf(resources.getIdentifier(str, "drawable", "com.motorola.launcherconfig")));
                                Log.d("CustomAppIcons", "Map app icon: " + replaceAll);
                            }
                        }
                    } catch (Exception e) {
                        Log.e("CustomAppIcons", "Exception on getting app icons map", e);
                    }
                }
            }
        }
        return this.mCustomAppIconMap;
    }

    public Drawable loadIcon(Context context, Drawable drawable) {
        Drawable loadIcon;
        try {
            Log.e("CustomAppIcons", "Load icon from drawable: " + drawable);
            ComponentName componentName = (ComponentName) ReflectUtils.getFieldValue(drawable, "component");
            if (componentName != null && (loadIcon = loadIcon(context, componentName)) != null) {
                return loadIcon;
            }
            Drawable drawable2 = (Drawable) ReflectUtils.getFieldValue(drawable, "originDrawable");
            if (drawable2 != null) {
                return drawable2;
            }
            return drawable;
        } catch (Exception e) {
            Log.e("CustomAppIcons", "Exception on loading icon from drawable: " + drawable, e);
        }
    }
}
