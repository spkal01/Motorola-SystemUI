package com.android.systemui.theme;

import android.content.om.FabricatedOverlay;
import android.content.om.OverlayIdentifier;
import android.content.om.OverlayInfo;
import android.content.om.OverlayManager;
import android.content.om.OverlayManagerTransaction;
import android.os.UserHandle;
import android.util.ArrayMap;
import android.util.Log;
import android.util.Pair;
import com.android.systemui.Dumpable;
import com.android.systemui.dump.DumpManager;
import com.google.android.collect.Lists;
import com.google.android.collect.Sets;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

public class ThemeOverlayApplier implements Dumpable {
    static final String ANDROID_PACKAGE = "android";
    private static final boolean DEBUG = Log.isLoggable("ThemeOverlayApplier", 3);
    static final String OVERLAY_CATEGORY_FONT = "android.theme.customization.font";
    static final String OVERLAY_CATEGORY_ICON_ANDROID = "android.theme.customization.icon_pack.android";
    static final String OVERLAY_CATEGORY_ICON_LAUNCHER = "android.theme.customization.icon_pack.launcher";
    static final String OVERLAY_CATEGORY_ICON_SETTINGS = "android.theme.customization.icon_pack.settings";
    static final String OVERLAY_CATEGORY_ICON_SYSUI = "android.theme.customization.icon_pack.systemui";
    static final String OVERLAY_CATEGORY_ICON_THEME_PICKER = "android.theme.customization.icon_pack.themepicker";
    static final String OVERLAY_CATEGORY_SHAPE = "android.theme.customization.adaptive_icon_shape";
    static final String[] PREFIX_RESOURCE = {"android:color/system_", "android:color-motdesk/system_", "android:color-motmobile/system_"};
    static final String SETTINGS_PACKAGE = "com.android.settings";
    static final Set<String> SYSTEM_USER_CATEGORIES = Sets.newHashSet(new String[]{"android.theme.customization.system_palette", "android.theme.customization.accent_color", OVERLAY_CATEGORY_FONT, OVERLAY_CATEGORY_SHAPE, OVERLAY_CATEGORY_ICON_ANDROID, OVERLAY_CATEGORY_ICON_SYSUI});
    static final String SYSUI_PACKAGE = "com.android.systemui";
    static final List<String> THEME_CATEGORIES = Lists.newArrayList(new String[]{"android.theme.customization.system_palette", OVERLAY_CATEGORY_ICON_LAUNCHER, OVERLAY_CATEGORY_SHAPE, OVERLAY_CATEGORY_FONT, "android.theme.customization.accent_color", OVERLAY_CATEGORY_ICON_ANDROID, OVERLAY_CATEGORY_ICON_SYSUI, OVERLAY_CATEGORY_ICON_SETTINGS, OVERLAY_CATEGORY_ICON_THEME_PICKER});
    private final Map<String, String> mCategoryToTargetPackage;
    private final Executor mExecutor;
    private final String mLauncherPackage;
    private final OverlayManager mOverlayManager;
    private final Map<String, Set<String>> mTargetPackageToCategories;
    private final String mThemePickerPackage;

    public ThemeOverlayApplier(OverlayManager overlayManager, Executor executor, String str, String str2, DumpManager dumpManager) {
        String str3 = str;
        String str4 = str2;
        ArrayMap arrayMap = new ArrayMap();
        this.mTargetPackageToCategories = arrayMap;
        ArrayMap arrayMap2 = new ArrayMap();
        this.mCategoryToTargetPackage = arrayMap2;
        this.mOverlayManager = overlayManager;
        this.mExecutor = executor;
        this.mLauncherPackage = str3;
        this.mThemePickerPackage = str4;
        arrayMap.put(ANDROID_PACKAGE, Sets.newHashSet(new String[]{"android.theme.customization.system_palette", "android.theme.customization.accent_color", OVERLAY_CATEGORY_FONT, OVERLAY_CATEGORY_SHAPE, OVERLAY_CATEGORY_ICON_ANDROID}));
        arrayMap.put(SYSUI_PACKAGE, Sets.newHashSet(new String[]{OVERLAY_CATEGORY_ICON_SYSUI}));
        arrayMap.put(SETTINGS_PACKAGE, Sets.newHashSet(new String[]{OVERLAY_CATEGORY_ICON_SETTINGS}));
        arrayMap.put(str3, Sets.newHashSet(new String[]{OVERLAY_CATEGORY_ICON_LAUNCHER}));
        arrayMap.put(str4, Sets.newHashSet(new String[]{OVERLAY_CATEGORY_ICON_THEME_PICKER}));
        arrayMap2.put("android.theme.customization.accent_color", ANDROID_PACKAGE);
        arrayMap2.put(OVERLAY_CATEGORY_FONT, ANDROID_PACKAGE);
        arrayMap2.put(OVERLAY_CATEGORY_SHAPE, ANDROID_PACKAGE);
        arrayMap2.put(OVERLAY_CATEGORY_ICON_ANDROID, ANDROID_PACKAGE);
        arrayMap2.put(OVERLAY_CATEGORY_ICON_SYSUI, SYSUI_PACKAGE);
        arrayMap2.put(OVERLAY_CATEGORY_ICON_SETTINGS, SETTINGS_PACKAGE);
        arrayMap2.put(OVERLAY_CATEGORY_ICON_LAUNCHER, str3);
        arrayMap2.put(OVERLAY_CATEGORY_ICON_THEME_PICKER, str4);
        dumpManager.registerDumpable("ThemeOverlayApplier", this);
    }

    /* access modifiers changed from: package-private */
    public void applyCurrentUserOverlays(Map<String, OverlayIdentifier> map, FabricatedOverlay[] fabricatedOverlayArr, int i, Set<UserHandle> set) {
        this.mExecutor.execute(new ThemeOverlayApplier$$ExternalSyntheticLambda0(this, map, fabricatedOverlayArr, i, set));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$applyCurrentUserOverlays$7(Map map, FabricatedOverlay[] fabricatedOverlayArr, int i, Set set) {
        HashSet hashSet = new HashSet(THEME_CATEGORIES);
        ArrayList arrayList = new ArrayList();
        ((Set) hashSet.stream().map(new ThemeOverlayApplier$$ExternalSyntheticLambda2(this)).collect(Collectors.toSet())).forEach(new ThemeOverlayApplier$$ExternalSyntheticLambda1(this, arrayList));
        List<Pair> list = (List) arrayList.stream().filter(new ThemeOverlayApplier$$ExternalSyntheticLambda4(this)).filter(new ThemeOverlayApplier$$ExternalSyntheticLambda6(hashSet)).filter(new ThemeOverlayApplier$$ExternalSyntheticLambda5(map)).filter(ThemeOverlayApplier$$ExternalSyntheticLambda7.INSTANCE).map(ThemeOverlayApplier$$ExternalSyntheticLambda3.INSTANCE).collect(Collectors.toList());
        OverlayManagerTransaction.Builder transactionBuilder = getTransactionBuilder();
        HashSet hashSet2 = new HashSet();
        if (fabricatedOverlayArr != null) {
            for (FabricatedOverlay fabricatedOverlay : fabricatedOverlayArr) {
                hashSet2.add(fabricatedOverlay.getIdentifier());
                transactionBuilder.registerFabricatedOverlay(fabricatedOverlay);
            }
        }
        for (Pair pair : list) {
            OverlayIdentifier overlayIdentifier = new OverlayIdentifier((String) pair.second);
            setEnabled(transactionBuilder, overlayIdentifier, (String) pair.first, i, set, false, hashSet2.contains(overlayIdentifier));
        }
        for (String next : THEME_CATEGORIES) {
            if (map.containsKey(next)) {
                OverlayIdentifier overlayIdentifier2 = (OverlayIdentifier) map.get(next);
                setEnabled(transactionBuilder, overlayIdentifier2, next, i, set, true, hashSet2.contains(overlayIdentifier2));
            }
        }
        try {
            this.mOverlayManager.commit(transactionBuilder.build());
        } catch (IllegalStateException | SecurityException e) {
            Log.e("ThemeOverlayApplier", "setEnabled failed", e);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ String lambda$applyCurrentUserOverlays$0(String str) {
        return this.mCategoryToTargetPackage.get(str);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$applyCurrentUserOverlays$1(List list, String str) {
        list.addAll(this.mOverlayManager.getOverlayInfosForTarget(str, UserHandle.SYSTEM));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ boolean lambda$applyCurrentUserOverlays$2(OverlayInfo overlayInfo) {
        return this.mTargetPackageToCategories.get(overlayInfo.targetPackageName).contains(overlayInfo.category);
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$applyCurrentUserOverlays$4(Map map, OverlayInfo overlayInfo) {
        return !map.containsValue(new OverlayIdentifier(overlayInfo.packageName));
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ Pair lambda$applyCurrentUserOverlays$6(OverlayInfo overlayInfo) {
        return new Pair(overlayInfo.category, overlayInfo.packageName);
    }

    /* access modifiers changed from: protected */
    public OverlayManagerTransaction.Builder getTransactionBuilder() {
        return new OverlayManagerTransaction.Builder();
    }

    private void setEnabled(OverlayManagerTransaction.Builder builder, OverlayIdentifier overlayIdentifier, String str, int i, Set<UserHandle> set, boolean z, boolean z2) {
        if (DEBUG) {
            Log.d("ThemeOverlayApplier", "setEnabled: " + overlayIdentifier.getPackageName() + " category: " + str + ": " + z);
        }
        if (this.mOverlayManager.getOverlayInfo(overlayIdentifier, UserHandle.of(i)) != null || z2) {
            builder.setEnabled(overlayIdentifier, z, i);
            if (i != UserHandle.SYSTEM.getIdentifier() && SYSTEM_USER_CATEGORIES.contains(str)) {
                builder.setEnabled(overlayIdentifier, z, UserHandle.SYSTEM.getIdentifier());
            }
            OverlayInfo overlayInfo = this.mOverlayManager.getOverlayInfo(overlayIdentifier, UserHandle.SYSTEM);
            if (overlayInfo != null && !overlayInfo.targetPackageName.equals(this.mLauncherPackage) && !overlayInfo.targetPackageName.equals(this.mThemePickerPackage)) {
                for (UserHandle identifier : set) {
                    builder.setEnabled(overlayIdentifier, z, identifier.getIdentifier());
                }
                return;
            }
            return;
        }
        Log.i("ThemeOverlayApplier", "Won't enable " + overlayIdentifier + ", it doesn't exist for user" + i);
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        printWriter.println("mTargetPackageToCategories=" + this.mTargetPackageToCategories);
        printWriter.println("mCategoryToTargetPackage=" + this.mCategoryToTargetPackage);
    }
}
