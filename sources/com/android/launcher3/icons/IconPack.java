package com.android.launcher3.icons;

import android.content.ComponentName;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.AdaptiveIconDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import com.android.launcher3.icons.ImageUtils;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public class IconPack {
    public static final Pattern COMPONENT_DRAWABLE_NAME_PATTERN = Pattern.compile("[.|/]");
    public static final IconPack DEFAULT_ICON_PACK;
    public static final Pattern DOUBLE_UNDERSCORE_PATTERN = Pattern.compile("__");
    public static final Pattern UNDERSCORE_PATTERN = Pattern.compile("_");
    private static Paint sMaskPaint;
    private static Paint sMaskPaint2;
    private static Paint sPaint = new Paint(7);
    private final Object mBackEdgePointsLock = new Object();
    private Bundle mBackHueColorBundle = new Bundle();
    private Map<Bitmap, Float> mBackHueColorMap = new HashMap();
    private ArrayList<String> mBackImageNames = new ArrayList<>();
    private List<Bitmap> mBackImages = new ArrayList();
    private Map<String, String> mCalendarComponentDrawables = new HashMap();
    private Map<String, String> mCalendarPackageDrawables = new HashMap();
    private Map<String, String> mComponentDrawables = new HashMap();
    private float mFactor = 1.0f;
    private boolean mForceIconBack = false;
    private Bitmap mFrontImage = null;
    private String mFrontImageName = null;
    private Context mIconPackContext;
    private Resources mIconPackRes = null;
    private boolean mIsDefault = false;
    private boolean mLoaded = false;
    private Bitmap mMaskImage = null;
    private String mMaskImageName = null;
    private String mName;
    private Map<String, String> mPackageDrawables = new HashMap();
    private String mPackageName;
    private PackageManager mPm;
    private Map<String, String> mPreviewDrawables = new HashMap();
    private Resources mRes;
    private final Bitmap mTestIcon;
    private final Canvas mTestIconCanvas;

    public static int getPaletteColorFromBitmap(Bitmap bitmap) {
        return 0;
    }

    static {
        Paint paint = new Paint(7);
        sMaskPaint = paint;
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
        Paint paint2 = new Paint(7);
        sMaskPaint2 = paint2;
        paint2.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_ATOP));
        IconPack iconPack = new IconPack("system");
        DEFAULT_ICON_PACK = iconPack;
        iconPack.mLoaded = true;
        iconPack.mIsDefault = true;
    }

    private IconPack(Context context, String str, String str2) throws PackageManager.NameNotFoundException {
        Bitmap createBitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888);
        this.mTestIcon = createBitmap;
        this.mTestIconCanvas = new Canvas(createBitmap);
        this.mPm = context.getPackageManager();
        this.mRes = context.getResources();
        this.mPackageName = str;
        this.mName = str2;
        this.mIconPackContext = context.createPackageContext(str, 0);
        logd("new IconPack - packageName=" + str + " | name=" + str2, new boolean[0]);
    }

    private IconPack(String str) {
        Bitmap createBitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888);
        this.mTestIcon = createBitmap;
        this.mTestIconCanvas = new Canvas(createBitmap);
        this.mPackageName = str;
        logd("new IconPack - packageName=" + str, new boolean[0]);
    }

    public void load() {
        load((Bundle) null);
    }

    public synchronized void load(Bundle bundle) {
        if (!this.mLoaded) {
            if (bundle != null) {
                loadFromIconPackInfo(this.mPm, bundle);
            } else {
                loadFromXml(this.mPm);
            }
            this.mLoaded = true;
            Log.d("IconPack", "Loaded icons: " + getComponentDrawables());
        }
    }

    private void loadFromIconPackInfo(PackageManager packageManager, Bundle bundle) {
        try {
            this.mIconPackRes = packageManager.getResourcesForApplication(this.mPackageName);
        } catch (PackageManager.NameNotFoundException unused) {
        }
        this.mComponentDrawables.putAll(getComponentDrawables(bundle));
        this.mCalendarComponentDrawables.putAll(getCalendarComponentDrawables(bundle));
        this.mCalendarPackageDrawables.putAll(getCalendarPackageDrawables(bundle));
        ArrayList<String> backImageNames = getBackImageNames(bundle);
        if (backImageNames != null) {
            this.mBackImageNames.addAll(backImageNames);
        }
        this.mForceIconBack = getForceIconBack(bundle);
        Bundle backHueColorBundle = getBackHueColorBundle(bundle);
        if (backHueColorBundle != null) {
            this.mBackHueColorBundle.putAll(backHueColorBundle);
        }
        this.mMaskImageName = getMaskImageName(bundle);
        this.mFrontImageName = getFrontImageName(bundle);
        this.mFactor = getFactor(bundle);
        Iterator<String> it = this.mBackImageNames.iterator();
        while (it.hasNext()) {
            String next = it.next();
            Bitmap resizeMaxSized = resizeMaxSized(loadBitmap(next), 256);
            if (resizeMaxSized != null) {
                this.mBackImages.add(resizeMaxSized);
                this.mBackHueColorMap.put(resizeMaxSized, Float.valueOf(this.mBackHueColorBundle.getFloat(next)));
            }
        }
        this.mMaskImage = resizeMaxSized(loadBitmap(this.mMaskImageName), 256);
        this.mFrontImage = resizeMaxSized(loadBitmap(this.mFrontImageName), 256);
    }

    private Bitmap resizeMaxSized(Bitmap bitmap, int i) {
        if (bitmap == null) {
            return null;
        }
        if (bitmap.getWidth() > i || bitmap.getHeight() > i) {
            return ImageUtils.createScaledBitmapByWidth(bitmap, i, ImageUtils.ScalingLogic.CROP);
        }
        return bitmap;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:20:0x0048, code lost:
        r2 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:22:?, code lost:
        logd("Cannot find iconPack " + r2.mPackageName, new boolean[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:33:?, code lost:
        r3 = null.xpp;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:37:0x007e, code lost:
        ((android.content.res.XmlResourceParser) r3).close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:38:0x0083, code lost:
        r3 = null.f56is;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:39:0x0085, code lost:
        if (r3 != null) goto L_0x0087;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:40:0x0087, code lost:
        com.android.launcher3.icons.GzipCompression.closeQuietly(r3);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:41:0x008a, code lost:
        throw r2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:45:?, code lost:
        return;
     */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Missing exception handler attribute for start block: B:21:0x004a */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void loadFromXml(android.content.pm.PackageManager r3) {
        /*
            r2 = this;
            r0 = 0
            java.lang.String r1 = r2.mPackageName     // Catch:{ NameNotFoundException -> 0x004a }
            android.content.res.Resources r3 = r3.getResourcesForApplication(r1)     // Catch:{ NameNotFoundException -> 0x004a }
            r2.mIconPackRes = r3     // Catch:{ NameNotFoundException -> 0x004a }
            java.lang.String r3 = "icon_config"
            com.android.launcher3.icons.IconPack$XppHolder r0 = r2.loadXpp(r3)     // Catch:{ NameNotFoundException -> 0x004a }
            if (r0 == 0) goto L_0x0017
            org.xmlpull.v1.XmlPullParser r3 = r0.xpp     // Catch:{ NameNotFoundException -> 0x004a }
            r2.loadIcons(r3)     // Catch:{ NameNotFoundException -> 0x004a }
            goto L_0x0033
        L_0x0017:
            java.lang.String r3 = "theme_resources"
            com.android.launcher3.icons.IconPack$XppHolder r0 = r2.loadXpp(r3)     // Catch:{ NameNotFoundException -> 0x004a }
            if (r0 == 0) goto L_0x0026
            org.xmlpull.v1.XmlPullParser r3 = r0.xpp     // Catch:{ NameNotFoundException -> 0x004a }
            r2.loadIcons(r3)     // Catch:{ NameNotFoundException -> 0x004a }
            goto L_0x0033
        L_0x0026:
            java.lang.String r3 = "icons"
            com.android.launcher3.icons.IconPack$XppHolder r0 = r2.loadXpp(r3)     // Catch:{ NameNotFoundException -> 0x004a }
            if (r0 == 0) goto L_0x0033
            org.xmlpull.v1.XmlPullParser r3 = r0.xpp     // Catch:{ NameNotFoundException -> 0x004a }
            r2.loadIcons(r3)     // Catch:{ NameNotFoundException -> 0x004a }
        L_0x0033:
            org.xmlpull.v1.XmlPullParser r2 = r0.xpp     // Catch:{ Exception -> 0x0075 }
            if (r2 == 0) goto L_0x0040
            boolean r3 = r2 instanceof android.content.res.XmlResourceParser     // Catch:{ Exception -> 0x0075 }
            if (r3 == 0) goto L_0x0040
            android.content.res.XmlResourceParser r2 = (android.content.res.XmlResourceParser) r2     // Catch:{ Exception -> 0x0075 }
            r2.close()     // Catch:{ Exception -> 0x0075 }
        L_0x0040:
            java.io.InputStream r2 = r0.f56is     // Catch:{ Exception -> 0x0075 }
            if (r2 == 0) goto L_0x0075
        L_0x0044:
            com.android.launcher3.icons.GzipCompression.closeQuietly(r2)     // Catch:{ Exception -> 0x0075 }
            goto L_0x0075
        L_0x0048:
            r2 = move-exception
            goto L_0x0076
        L_0x004a:
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ all -> 0x0048 }
            r3.<init>()     // Catch:{ all -> 0x0048 }
            java.lang.String r1 = "Cannot find iconPack "
            r3.append(r1)     // Catch:{ all -> 0x0048 }
            java.lang.String r2 = r2.mPackageName     // Catch:{ all -> 0x0048 }
            r3.append(r2)     // Catch:{ all -> 0x0048 }
            java.lang.String r2 = r3.toString()     // Catch:{ all -> 0x0048 }
            r3 = 0
            boolean[] r3 = new boolean[r3]     // Catch:{ all -> 0x0048 }
            logd(r2, r3)     // Catch:{ all -> 0x0048 }
            org.xmlpull.v1.XmlPullParser r2 = r0.xpp     // Catch:{ Exception -> 0x0075 }
            if (r2 == 0) goto L_0x0070
            boolean r3 = r2 instanceof android.content.res.XmlResourceParser     // Catch:{ Exception -> 0x0075 }
            if (r3 == 0) goto L_0x0070
            android.content.res.XmlResourceParser r2 = (android.content.res.XmlResourceParser) r2     // Catch:{ Exception -> 0x0075 }
            r2.close()     // Catch:{ Exception -> 0x0075 }
        L_0x0070:
            java.io.InputStream r2 = r0.f56is     // Catch:{ Exception -> 0x0075 }
            if (r2 == 0) goto L_0x0075
            goto L_0x0044
        L_0x0075:
            return
        L_0x0076:
            org.xmlpull.v1.XmlPullParser r3 = r0.xpp     // Catch:{ Exception -> 0x008a }
            if (r3 == 0) goto L_0x0083
            boolean r1 = r3 instanceof android.content.res.XmlResourceParser     // Catch:{ Exception -> 0x008a }
            if (r1 == 0) goto L_0x0083
            android.content.res.XmlResourceParser r3 = (android.content.res.XmlResourceParser) r3     // Catch:{ Exception -> 0x008a }
            r3.close()     // Catch:{ Exception -> 0x008a }
        L_0x0083:
            java.io.InputStream r3 = r0.f56is     // Catch:{ Exception -> 0x008a }
            if (r3 == 0) goto L_0x008a
            com.android.launcher3.icons.GzipCompression.closeQuietly(r3)     // Catch:{ Exception -> 0x008a }
        L_0x008a:
            throw r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.icons.IconPack.loadFromXml(android.content.pm.PackageManager):void");
    }

    public Drawable getAppIcon(String str, String str2) {
        Resources resources;
        int identifier;
        String str3 = "";
        String lowerCase = str == null ? str3 : str.toLowerCase();
        if (str2 != null) {
            str3 = str2.toLowerCase();
        }
        String drawableName = getDrawableName(new ComponentName(lowerCase, str3));
        if (drawableName == null || (identifier = resources.getIdentifier(drawableName, "mipmap", this.mPackageName)) == 0) {
            return null;
        }
        Drawable drawable = (resources = this.mIconPackContext.getResources()).getDrawable(identifier, this.mIconPackContext.getTheme());
        if (drawable instanceof AdaptiveIconDrawable) {
            return drawable;
        }
        return null;
    }

    private class XppHolder {

        /* renamed from: is */
        InputStream f56is;
        XmlPullParser xpp;

        private XppHolder() {
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:28:0x009c  */
    /* JADX WARNING: Removed duplicated region for block: B:30:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private com.android.launcher3.icons.IconPack.XppHolder loadXpp(java.lang.String r10) {
        /*
            r9 = this;
            java.lang.String r0 = ".xml failed"
            java.lang.String r1 = "Load xpp for "
            java.lang.String r2 = "IconPack"
            r3 = 0
            android.content.res.Resources r4 = r9.mIconPackRes     // Catch:{ IOException -> 0x0080, XmlPullParserException -> 0x0067 }
            java.lang.String r5 = "xml"
            java.lang.String r6 = r9.mPackageName     // Catch:{ IOException -> 0x0080, XmlPullParserException -> 0x0067 }
            int r4 = r4.getIdentifier(r10, r5, r6)     // Catch:{ IOException -> 0x0080, XmlPullParserException -> 0x0067 }
            if (r4 == 0) goto L_0x001b
            android.content.res.Resources r5 = r9.mIconPackRes     // Catch:{ IOException -> 0x0080, XmlPullParserException -> 0x0067 }
            android.content.res.XmlResourceParser r4 = r5.getXml(r4)     // Catch:{ IOException -> 0x0080, XmlPullParserException -> 0x0067 }
            goto L_0x001c
        L_0x001b:
            r4 = r3
        L_0x001c:
            if (r4 != 0) goto L_0x0065
            android.content.res.Resources r5 = r9.mIconPackRes     // Catch:{ IOException -> 0x005f, XmlPullParserException -> 0x0059 }
            android.content.res.AssetManager r5 = r5.getAssets()     // Catch:{ IOException -> 0x005f, XmlPullParserException -> 0x0059 }
            java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch:{ IOException -> 0x005f, XmlPullParserException -> 0x0059 }
            r6.<init>()     // Catch:{ IOException -> 0x005f, XmlPullParserException -> 0x0059 }
            r6.append(r10)     // Catch:{ IOException -> 0x005f, XmlPullParserException -> 0x0059 }
            java.lang.String r7 = ".xml"
            r6.append(r7)     // Catch:{ IOException -> 0x005f, XmlPullParserException -> 0x0059 }
            java.lang.String r6 = r6.toString()     // Catch:{ IOException -> 0x005f, XmlPullParserException -> 0x0059 }
            java.io.InputStream r5 = r5.open(r6)     // Catch:{ IOException -> 0x005f, XmlPullParserException -> 0x0059 }
            org.xmlpull.v1.XmlPullParserFactory r6 = org.xmlpull.v1.XmlPullParserFactory.newInstance()     // Catch:{ IOException -> 0x0053, XmlPullParserException -> 0x004d }
            r7 = 1
            r6.setNamespaceAware(r7)     // Catch:{ IOException -> 0x0053, XmlPullParserException -> 0x004d }
            org.xmlpull.v1.XmlPullParser r4 = r6.newPullParser()     // Catch:{ IOException -> 0x0053, XmlPullParserException -> 0x004d }
            java.lang.String r6 = "utf-8"
            r4.setInput(r5, r6)     // Catch:{ IOException -> 0x0053, XmlPullParserException -> 0x004d }
            goto L_0x009a
        L_0x004d:
            r6 = move-exception
            r8 = r5
            r5 = r4
            r4 = r6
            r6 = r8
            goto L_0x006a
        L_0x0053:
            r6 = move-exception
            r8 = r5
            r5 = r4
            r4 = r6
            r6 = r8
            goto L_0x0083
        L_0x0059:
            r5 = move-exception
            r6 = r3
            r8 = r5
            r5 = r4
            r4 = r8
            goto L_0x006a
        L_0x005f:
            r5 = move-exception
            r6 = r3
            r8 = r5
            r5 = r4
            r4 = r8
            goto L_0x0083
        L_0x0065:
            r5 = r3
            goto L_0x009a
        L_0x0067:
            r4 = move-exception
            r5 = r3
            r6 = r5
        L_0x006a:
            java.lang.StringBuilder r7 = new java.lang.StringBuilder
            r7.<init>()
            r7.append(r1)
            r7.append(r10)
            r7.append(r0)
            java.lang.String r10 = r7.toString()
            android.util.Log.d(r2, r10, r4)
            goto L_0x0098
        L_0x0080:
            r4 = move-exception
            r5 = r3
            r6 = r5
        L_0x0083:
            java.lang.StringBuilder r7 = new java.lang.StringBuilder
            r7.<init>()
            r7.append(r1)
            r7.append(r10)
            r7.append(r0)
            java.lang.String r10 = r7.toString()
            android.util.Log.d(r2, r10, r4)
        L_0x0098:
            r4 = r5
            r5 = r6
        L_0x009a:
            if (r4 == 0) goto L_0x00a6
            com.android.launcher3.icons.IconPack$XppHolder r10 = new com.android.launcher3.icons.IconPack$XppHolder
            r10.<init>()
            r10.xpp = r4
            r10.f56is = r5
            r3 = r10
        L_0x00a6:
            return r3
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.icons.IconPack.loadXpp(java.lang.String):com.android.launcher3.icons.IconPack$XppHolder");
    }

    private void loadIcons(XmlPullParser xmlPullParser) {
        if (xmlPullParser != null) {
            try {
                int eventType = xmlPullParser.getEventType();
                while (eventType != 1) {
                    if (eventType == 2) {
                        if (xmlPullParser.getName().equalsIgnoreCase("preview")) {
                            loadPreviewRes(xmlPullParser);
                        }
                        if (xmlPullParser.getName().equalsIgnoreCase("item")) {
                            loadComponentRes(xmlPullParser);
                        } else if (xmlPullParser.getName().equalsIgnoreCase("calendar")) {
                            loadCalendarRes(xmlPullParser);
                        } else if (xmlPullParser.getName().equalsIgnoreCase("AppIcon")) {
                            loadAppIconRes(xmlPullParser);
                        } else if (xmlPullParser.getName().equals("iconback")) {
                            loadIconBack(xmlPullParser);
                        } else if (xmlPullParser.getName().equals("iconmask")) {
                            loadIconMask(xmlPullParser);
                        } else if (xmlPullParser.getName().equals("iconupon")) {
                            loadIconUpon(xmlPullParser);
                        } else if (xmlPullParser.getName().equals("scale")) {
                            loadScale(xmlPullParser);
                        } else if (xmlPullParser.getName().equalsIgnoreCase("drawable")) {
                            loadDrawableRes(xmlPullParser);
                        }
                    }
                    eventType = xmlPullParser.next();
                }
            } catch (IOException e) {
                Log.d("IconPack", "Load icons failed", e);
            } catch (XmlPullParserException e2) {
                Log.d("IconPack", "Load icons failed", e2);
            }
            for (int i = 0; i < this.mBackImages.size(); i++) {
                Bitmap bitmap = this.mBackImages.get(i);
                float hueColorFromColor = getHueColorFromColor(getPaletteColorFromBitmap(bitmap));
                this.mBackHueColorMap.put(bitmap, Float.valueOf(hueColorFromColor));
                this.mBackHueColorBundle.putFloat(this.mBackImageNames.get(i), hueColorFromColor);
            }
        }
    }

    private void loadAppIconRes(XmlPullParser xmlPullParser) {
        String str = null;
        String str2 = null;
        for (int i = 0; i < xmlPullParser.getAttributeCount(); i++) {
            if (xmlPullParser.getAttributeName(i).equalsIgnoreCase("name")) {
                str = getComponentNameFlattenString(xmlPullParser.getAttributeValue(i));
            } else if (xmlPullParser.getAttributeName(i).equalsIgnoreCase("image")) {
                str2 = xmlPullParser.getAttributeValue(i);
            }
        }
        if (!TextUtils.isEmpty(str)) {
            if (!this.mComponentDrawables.containsKey(str)) {
                this.mComponentDrawables.put(str, str2);
            }
            ComponentName unflattenFromString = ComponentName.unflattenFromString(str);
            if (unflattenFromString != null) {
                String flattenToString = new ComponentName(unflattenFromString.getPackageName(), "").flattenToString();
                if (!this.mComponentDrawables.containsKey(flattenToString)) {
                    this.mComponentDrawables.put(flattenToString, str2);
                }
            }
        }
    }

    private void loadIconBack(XmlPullParser xmlPullParser) {
        for (int i = 0; i < xmlPullParser.getAttributeCount(); i++) {
            if (xmlPullParser.getAttributeName(i).startsWith("img")) {
                String attributeValue = xmlPullParser.getAttributeValue(i);
                Bitmap loadBitmap = loadBitmap(attributeValue);
                if (loadBitmap != null) {
                    this.mBackImages.add(loadBitmap);
                    this.mBackImageNames.add(attributeValue);
                }
            } else if (xmlPullParser.getAttributeName(i).equalsIgnoreCase("force")) {
                this.mForceIconBack = Boolean.valueOf(xmlPullParser.getAttributeValue(i)).booleanValue();
            }
        }
    }

    private void loadIconMask(XmlPullParser xmlPullParser) {
        if (xmlPullParser.getAttributeCount() > 0 && xmlPullParser.getAttributeName(0).equals("img1")) {
            String attributeValue = xmlPullParser.getAttributeValue(0);
            this.mMaskImage = loadBitmap(attributeValue);
            this.mMaskImageName = attributeValue;
        }
    }

    private void loadIconUpon(XmlPullParser xmlPullParser) {
        if (xmlPullParser.getAttributeCount() > 0 && xmlPullParser.getAttributeName(0).equals("img1")) {
            String attributeValue = xmlPullParser.getAttributeValue(0);
            this.mFrontImage = loadBitmap(attributeValue);
            this.mFrontImageName = attributeValue;
        }
    }

    private void loadScale(XmlPullParser xmlPullParser) {
        if (xmlPullParser.getAttributeCount() > 0 && xmlPullParser.getAttributeName(0).equals("factor")) {
            try {
                this.mFactor = Float.valueOf(xmlPullParser.getAttributeValue(0)).floatValue();
            } catch (NumberFormatException unused) {
                this.mFactor = 1.0f;
            }
        }
    }

    private void loadDrawableRes(XmlPullParser xmlPullParser) {
        String str = null;
        String str2 = null;
        for (int i = 0; i < xmlPullParser.getAttributeCount(); i++) {
            if (xmlPullParser.getAttributeName(i).equalsIgnoreCase("name")) {
                str2 = UNDERSCORE_PATTERN.matcher(DOUBLE_UNDERSCORE_PATTERN.split(xmlPullParser.getAttributeValue(i))[0]).replaceAll(".");
            }
        }
        try {
            str = xmlPullParser.nextText();
            if (str.startsWith("@drawable/")) {
                str = str.substring(10);
            }
        } catch (Exception e) {
            Log.d("IconPack", "Get drawable value failed", e);
        }
        if (!TextUtils.isEmpty(str2)) {
            String flattenToString = new ComponentName(str2, "").flattenToString();
            if (!this.mComponentDrawables.containsKey(flattenToString)) {
                this.mComponentDrawables.put(flattenToString, str);
            }
        }
    }

    private void loadPreviewRes(XmlPullParser xmlPullParser) {
        String str = null;
        String str2 = null;
        for (int i = 0; i < xmlPullParser.getAttributeCount(); i++) {
            if (xmlPullParser.getAttributeName(i).equalsIgnoreCase("orientation")) {
                str = xmlPullParser.getAttributeValue(i);
            } else if (xmlPullParser.getAttributeName(i).equalsIgnoreCase("drawable")) {
                str2 = xmlPullParser.getAttributeValue(i);
            }
        }
        if (!TextUtils.isEmpty(str) && !this.mPreviewDrawables.containsKey(str)) {
            this.mPreviewDrawables.put(str, str2);
        }
    }

    private void loadComponentRes(XmlPullParser xmlPullParser) {
        String str = null;
        String str2 = null;
        String str3 = null;
        for (int i = 0; i < xmlPullParser.getAttributeCount(); i++) {
            if (xmlPullParser.getAttributeName(i).equalsIgnoreCase("component")) {
                str = getComponentNameFlattenString(xmlPullParser.getAttributeValue(i));
            } else if (xmlPullParser.getAttributeName(i).equalsIgnoreCase("drawable")) {
                str2 = xmlPullParser.getAttributeValue(i);
            } else if (xmlPullParser.getAttributeName(i).equalsIgnoreCase("package")) {
                str3 = getPackageNameFlattenString(xmlPullParser.getAttributeValue(i));
            }
        }
        if (!TextUtils.isEmpty(str)) {
            if (!this.mComponentDrawables.containsKey(str)) {
                this.mComponentDrawables.put(str, str2);
            }
            ComponentName unflattenFromString = ComponentName.unflattenFromString(str);
            if (unflattenFromString != null) {
                String flattenToString = new ComponentName(unflattenFromString.getPackageName(), "").flattenToString();
                if (!this.mComponentDrawables.containsKey(flattenToString)) {
                    this.mComponentDrawables.put(flattenToString, str2);
                }
            }
        } else if (!TextUtils.isEmpty(str3) && !this.mPackageDrawables.containsKey(str3)) {
            this.mPackageDrawables.put(str3, str2);
        }
    }

    private void loadCalendarRes(XmlPullParser xmlPullParser) {
        String str = null;
        String str2 = null;
        for (int i = 0; i < xmlPullParser.getAttributeCount(); i++) {
            if (xmlPullParser.getAttributeName(i).equalsIgnoreCase("component")) {
                str = getComponentNameFlattenString(xmlPullParser.getAttributeValue(i));
            } else if (xmlPullParser.getAttributeName(i).equalsIgnoreCase("prefix")) {
                str2 = xmlPullParser.getAttributeValue(i);
            }
        }
        if (!TextUtils.isEmpty(str)) {
            if (!this.mCalendarComponentDrawables.containsKey(str)) {
                this.mCalendarComponentDrawables.put(str, str2);
            }
            ComponentName unflattenFromString = ComponentName.unflattenFromString(str);
            if (unflattenFromString != null && !this.mCalendarPackageDrawables.containsKey(unflattenFromString.getPackageName())) {
                this.mCalendarPackageDrawables.put(unflattenFromString.getPackageName(), str2);
            }
        }
    }

    private Bitmap loadBitmap(String str) {
        return loadBitmap(str, 0);
    }

    private Bitmap loadBitmap(String str, int i) {
        Resources resources;
        if (str == null || (resources = this.mIconPackRes) == null) {
            return null;
        }
        return loadBitmap(resources.getIdentifier(str, "mipmap", this.mPackageName), i);
    }

    private Bitmap loadBitmap(int i, int i2) {
        if (i == 0) {
            return null;
        }
        return decodeIconBitmapFromResource(this.mIconPackRes, i, i2);
    }

    private String getDrawableName(ComponentName componentName) {
        if (componentName == null) {
            return null;
        }
        String flattenToString = componentName.flattenToString();
        String calendarDrawable = getCalendarDrawable(flattenToString);
        if (calendarDrawable == null) {
            calendarDrawable = this.mComponentDrawables.get(flattenToString);
        }
        if (calendarDrawable == null) {
            calendarDrawable = this.mComponentDrawables.get(new ComponentName(componentName.getPackageName(), "").flattenToString());
        }
        if (calendarDrawable == null) {
            calendarDrawable = this.mPackageDrawables.get(componentName.getPackageName());
        }
        return (calendarDrawable != null || flattenToString == null) ? calendarDrawable : COMPONENT_DRAWABLE_NAME_PATTERN.matcher(flattenToString.toLowerCase()).replaceAll("_");
    }

    public boolean equals(Object obj) {
        String str;
        if (!(obj instanceof IconPack) || (str = this.mPackageName) == null || !str.equals(((IconPack) obj).mPackageName)) {
            return super.equals(obj);
        }
        return true;
    }

    public String getPackageName() {
        return this.mPackageName;
    }

    public boolean isDefault() {
        return this.mIsDefault;
    }

    private String getCalendarDrawable(String str) {
        String str2 = this.mCalendarComponentDrawables.get(str);
        if (str2 == null) {
            return str2;
        }
        return str2 + generateResourceIndexWithCalendarDate();
    }

    public Map<String, String> getComponentDrawables() {
        return this.mComponentDrawables;
    }

    public static IconPack newIconPack(Context context, String str) {
        return newIconPack(context, str, (String) null);
    }

    public static IconPack newIconPack(Context context, String str, String str2) {
        if (str == null || "system".equals(str) || !isAppEnabled(context.getPackageManager(), str)) {
            return DEFAULT_ICON_PACK;
        }
        try {
            IconPack iconPack = new IconPack(context, str, str2);
            iconPack.load();
            return iconPack;
        } catch (Exception unused) {
            Log.w("IconPack", "Cannot new Icon pack: " + str);
            return DEFAULT_ICON_PACK;
        }
    }

    public static Map<String, String> getComponentDrawables(Bundle bundle) {
        return JsonUtils.jsonToMap(GzipCompression.decompress(bundle.getByteArray("componentDrawables")));
    }

    public static Map<String, String> getCalendarComponentDrawables(Bundle bundle) {
        return JsonUtils.jsonToMap(GzipCompression.decompress(bundle.getByteArray("calendarComponentDrawables")));
    }

    public static Map<String, String> getCalendarPackageDrawables(Bundle bundle) {
        return JsonUtils.jsonToMap(GzipCompression.decompress(bundle.getByteArray("calendarPackageDrawables")));
    }

    public static ArrayList<String> getBackImageNames(Bundle bundle) {
        return bundle.getStringArrayList("backImageNames");
    }

    public static boolean getForceIconBack(Bundle bundle) {
        return bundle.getBoolean("forceIconBack");
    }

    public static Bundle getBackHueColorBundle(Bundle bundle) {
        return bundle.getBundle("backHueColorBundle");
    }

    public static String getMaskImageName(Bundle bundle) {
        return bundle.getString("maskImageName");
    }

    public static String getFrontImageName(Bundle bundle) {
        return bundle.getString("frontImageName");
    }

    public static float getFactor(Bundle bundle) {
        return bundle.getFloat("factor");
    }

    public static boolean isAppEnabled(PackageManager packageManager, String str) {
        return isAppEnabled(packageManager, str, 0);
    }

    public static boolean isAppEnabled(PackageManager packageManager, String str, int i) {
        try {
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(str, i);
            if (applicationInfo == null || !applicationInfo.enabled) {
                return false;
            }
            return true;
        } catch (PackageManager.NameNotFoundException unused) {
            return false;
        }
    }

    public static String getComponentNameFlattenString(String str) {
        return (!str.contains("ComponentInfo{") || !str.contains("}")) ? str : str.substring(14, str.indexOf("}")).toLowerCase();
    }

    private static String getPackageNameFlattenString(String str) {
        return (!str.contains("PackageInfo{") || !str.contains("PackageInfo{")) ? str : str.substring(12, str.indexOf("}")).toLowerCase();
    }

    public static Bitmap decodeSampledBitmapFromResource(Resources resources, int i, int i2, int i3) {
        if (i2 <= 0 || i3 <= 0) {
            return BitmapFactory.decodeResource(resources, i);
        }
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(resources, i, options);
        options.inSampleSize = calculateInSampleSize(options, i2, i3);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(resources, i, options);
    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int i, int i2) {
        int i3 = options.outHeight;
        int i4 = options.outWidth;
        int i5 = 1;
        if (i3 > i2 || i4 > i) {
            int i6 = i3 / 2;
            int i7 = i4 / 2;
            while (i6 / i5 >= i2 && i7 / i5 >= i) {
                i5 *= 2;
            }
        }
        return i5;
    }

    public static Bitmap decodeIconBitmapFromResource(Resources resources, int i, int i2) {
        return decodeSampledBitmapFromResource(resources, i, i2, i2);
    }

    public static int generateResourceIndexWithCalendarDate() {
        return generateResourceIndexWithCalendarDate(false);
    }

    public static int generateResourceIndexWithCalendarDate(boolean z) {
        return Calendar.getInstance().get(5) - (z ? 1 : 0);
    }

    public static void logd(String str, boolean... zArr) {
        logd(true, "IconPack", str, zArr);
    }

    public static void logd(boolean z, String str, String str2, boolean... zArr) {
        if (!z) {
            return;
        }
        if (zArr == null || zArr.length <= 0 || !zArr[0]) {
            Log.d(str, str2);
            return;
        }
        Log.d(str, str2, new RuntimeException("" + System.currentTimeMillis()));
    }

    public static float getHueColorFromColor(int i) {
        float[] fArr = new float[3];
        Color.colorToHSV(i, fArr);
        return fArr[0];
    }
}
