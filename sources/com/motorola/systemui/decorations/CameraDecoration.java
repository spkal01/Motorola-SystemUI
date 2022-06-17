package com.motorola.systemui.decorations;

import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.graphics.Rect;
import android.hardware.display.DisplayManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.android.systemui.R$drawable;
import com.android.systemui.R$id;
import com.android.systemui.R$layout;
import com.android.systemui.R$string;
import com.android.systemui.navigationbar.gestural.RegionSamplingHelper;
import com.motorola.android.provider.MotorolaSettings;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import kotlin.Pair;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/* compiled from: CameraDecoration.kt */
public final class CameraDecoration implements View.OnAttachStateChangeListener, DisplayManager.BacklightListener, RegionSamplingHelper.SamplingCallback {
    @NotNull
    public static final Companion Companion = new Companion((DefaultConstructorMarker) null);
    /* access modifiers changed from: private */
    public static final boolean DEBUG_CUD = (!Build.IS_USER);
    /* access modifiers changed from: private */
    public static final Uri HIGH_BRIGHTNESS_MODE_ENABLED_URI = MotorolaSettings.Global.getUriFor("screen_high_brightness_mode");
    @NotNull
    private final LinearLayout cameraDecorationLayout;
    @NotNull
    private final View cameraDecorationOverlay;
    @NotNull
    private final ImageView cameraProtectionView;
    private float mCameraDecorationOverlayAlpha;
    @NotNull
    private final HbmContentObserver mContentObserver;
    /* access modifiers changed from: private */
    @NotNull
    public final Context mContext;
    private float mCudAreaGray;
    private float mDisplayLight;
    private final DisplayManager mDisplayManager;
    @NotNull
    private final Handler mHandler;
    private boolean mIsFrontCameraOn;
    private boolean mIsInHBM;
    private boolean mIsInLBM;
    @NotNull
    private final LbmConfig mLbmConfig;
    private int mRotation;
    @NotNull
    private final RegionSamplingHelper mSamplingHelper;
    @NotNull
    private final Rect mSamplingRect = new Rect();
    private final WindowManager mWindowManager;

    public CameraDecoration(@NotNull Context context) {
        Intrinsics.checkNotNullParameter(context, "context");
        this.mContext = context;
        Handler handler = new Handler();
        this.mHandler = handler;
        WindowManager windowManager = (WindowManager) context.getSystemService(WindowManager.class);
        this.mWindowManager = windowManager;
        this.mDisplayManager = (DisplayManager) context.getSystemService(DisplayManager.class);
        this.mContentObserver = new HbmContentObserver(this, handler);
        this.mLbmConfig = new LbmConfig(context);
        this.mDisplayLight = -1.0f;
        this.mCudAreaGray = -1.0f;
        this.mRotation = context.getDisplay().getRotation();
        Companion.logDebug("CameraDecoration", "setupCameraProtection");
        View inflate = LayoutInflater.from(context).inflate(R$layout.zz_moto_camera_protection, (ViewGroup) null);
        Intrinsics.checkNotNullExpressionValue(inflate, "from(mContext)\n                .inflate(R.layout.zz_moto_camera_protection, null)");
        this.cameraDecorationOverlay = inflate;
        inflate.setSystemUiVisibility(256);
        inflate.setForceDarkAllowed(false);
        inflate.addOnAttachStateChangeListener(this);
        windowManager.addView(inflate, getCameraDecorWindowLayoutParams());
        View findViewById = inflate.findViewById(R$id.camera_protection_layout);
        Intrinsics.checkNotNullExpressionValue(findViewById, "cameraDecorationOverlay.findViewById(R.id.camera_protection_layout)");
        this.cameraDecorationLayout = (LinearLayout) findViewById;
        View findViewById2 = inflate.findViewById(R$id.camera_protection);
        Intrinsics.checkNotNullExpressionValue(findViewById2, "cameraDecorationOverlay.findViewById(R.id.camera_protection)");
        ImageView imageView = (ImageView) findViewById2;
        this.cameraProtectionView = imageView;
        imageView.setAlpha(0.0f);
        RegionSamplingHelper regionSamplingHelper = new RegionSamplingHelper(imageView, this);
        this.mSamplingHelper = regionSamplingHelper;
        regionSamplingHelper.setWindowVisible(true);
        startObserving();
        notifyCameraProtectionStateChange();
    }

    public final void startObserving() {
        Companion.logDebug("CameraDecoration", "startObserving");
        ContentResolver contentResolver = this.mContext.getContentResolver();
        Uri uri = HIGH_BRIGHTNESS_MODE_ENABLED_URI;
        contentResolver.registerContentObserver(uri, false, this.mContentObserver, -1);
        this.mContentObserver.onChange(true, uri);
        this.mDisplayManager.registerBacklightChangeListener(this);
    }

    public final void stopObserve() {
        Companion.logDebug("CameraDecoration", "stopObserve");
        this.mContext.getContentResolver().unregisterContentObserver(this.mContentObserver);
        this.mDisplayManager.unRegisterBacklightChangeListener(this);
    }

    public final void onFrontCameraStateChange(boolean z) {
        this.mIsFrontCameraOn = z;
        notifyCameraProtectionStateChange();
    }

    public final void updateOrientation(int i) {
        this.mRotation = i;
        Companion.logDebug("CameraDecoration", Intrinsics.stringPlus("updateOrientation: ", Integer.valueOf(i)));
        this.cameraDecorationOverlay.setVisibility((this.mRotation == 0 || this.mIsFrontCameraOn) ? 0 : 4);
        LinearLayout linearLayout = this.cameraDecorationLayout;
        int i2 = this.mRotation;
        linearLayout.setGravity(i2 != 1 ? i2 != 2 ? i2 != 3 ? 49 : 21 : 81 : 19);
    }

    private final WindowManager.LayoutParams getCameraDecorWindowLayoutParams() {
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams(-1, -1, 2024, 545259816, -3);
        layoutParams.privateFlags |= 1048656;
        layoutParams.setTitle("CameraDecorOverlay");
        layoutParams.gravity = 51;
        layoutParams.layoutInDisplayCutoutMode = 3;
        layoutParams.setFitInsetsTypes(0);
        layoutParams.privateFlags |= 16777216;
        return layoutParams;
    }

    public final void setHbm(boolean z) {
        Companion.logDebug("CameraDecoration", Intrinsics.stringPlus("setHbm: ", Boolean.valueOf(z)));
        this.mIsInHBM = z;
        notifyCameraProtectionStateChange();
    }

    public final void setLbm(boolean z) {
        Companion.logDebug("CameraDecoration", Intrinsics.stringPlus("setLbm: ", Boolean.valueOf(z)));
        this.mIsInLBM = z;
        if (z) {
            this.mSamplingHelper.start(this.mSamplingRect);
        } else {
            this.mSamplingHelper.stop();
            this.mCudAreaGray = -1.0f;
        }
        notifyCameraProtectionStateChange();
    }

    public final void notifyCameraProtectionStateChange() {
        if (this.mIsFrontCameraOn) {
            Companion.logDebug("CameraDecoration", "notifyCameraProtectionStateChange: FrontCameraOn");
            this.cameraProtectionView.setImageResource(R$drawable.zz_moto_camera_protection);
            this.cameraProtectionView.setAlpha(1.0f);
        } else if (this.mIsInHBM) {
            Companion.logDebug("CameraDecoration", "notifyCameraProtectionStateChange: InHBM");
            this.cameraProtectionView.setImageResource(R$drawable.zz_moto_camera_decoration);
            this.cameraProtectionView.setAlpha(1.0f);
        } else if (this.mIsInLBM) {
            Companion.logDebug("CameraDecoration", "notifyCameraProtectionStateChange: InLBM");
            this.cameraProtectionView.setImageResource(R$drawable.zz_moto_camera_protection);
            this.cameraProtectionView.setAlpha(this.mCameraDecorationOverlayAlpha);
        } else {
            Companion.logDebug("CameraDecoration", "notifyCameraProtectionStateChange: dismiss decoration");
            this.cameraProtectionView.setAlpha(0.0f);
        }
    }

    private final void notifyLbmAlphaChange() {
        if (this.mIsInLBM) {
            float f = this.mDisplayLight;
            if (f >= 0.0f) {
                float f2 = this.mCudAreaGray;
                if (f2 >= 0.0f) {
                    this.mCameraDecorationOverlayAlpha = this.mLbmConfig.mapAlphaValue(f, f2);
                    notifyCameraProtectionStateChange();
                }
            }
        }
    }

    public void onViewAttachedToWindow(@Nullable View view) {
        startObserving();
    }

    public void onViewDetachedFromWindow(@Nullable View view) {
        stopObserve();
    }

    public void onBacklightChanged(float f) {
        float defaultDisplayNits = this.mDisplayManager.getDefaultDisplayNits(f);
        this.mDisplayLight = defaultDisplayNits;
        boolean z = defaultDisplayNits <= this.mLbmConfig.getLbmLight() && this.mLbmConfig.isLbmEnabled();
        if (this.mIsInLBM == z) {
            notifyLbmAlphaChange();
        } else {
            setLbm(z);
        }
    }

    public void onRegionDarknessChanged(boolean z) {
        this.mCudAreaGray = this.mSamplingHelper.getCurrentMedianLuma() * ((float) 255);
        notifyLbmAlphaChange();
    }

    @NotNull
    public Rect getSampledRegion(@NotNull View view) {
        Intrinsics.checkNotNullParameter(view, "sampledView");
        this.mSamplingRect.set(this.cameraProtectionView.getLeft(), this.cameraProtectionView.getTop(), this.cameraProtectionView.getRight(), this.cameraProtectionView.getBottom());
        Companion.logDebug("CameraDecoration", Intrinsics.stringPlus("getSampledRegion: ", this.mSamplingRect));
        return this.mSamplingRect;
    }

    /* compiled from: CameraDecoration.kt */
    public static final class Companion {
        public /* synthetic */ Companion(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }

        private Companion() {
        }

        /* access modifiers changed from: private */
        public final void logDebug(String str, String str2) {
            if (CameraDecoration.DEBUG_CUD) {
                Log.d(str, str2);
            }
        }
    }

    /* compiled from: CameraDecoration.kt */
    private static final class LbmConfig {
        @NotNull
        private final String configSplit = "|";
        @NotNull
        private final String graySplit = ",";
        @NotNull
        private final String itemSplit = ";";
        private float lbmLight;
        @NotNull
        private final String lightSplit = ":";
        private final String mConfigString;
        @NotNull
        private final ArrayList<Config> mConfigs;

        /* compiled from: CameraDecoration.kt */
        private static final class Config {
            @NotNull
            private final ArrayList<Pair<Float, Float>> grayList;
            private final float light;

            public boolean equals(@Nullable Object obj) {
                if (this == obj) {
                    return true;
                }
                if (!(obj instanceof Config)) {
                    return false;
                }
                Config config = (Config) obj;
                return Intrinsics.areEqual((Object) Float.valueOf(this.light), (Object) Float.valueOf(config.light)) && Intrinsics.areEqual((Object) this.grayList, (Object) config.grayList);
            }

            public int hashCode() {
                return (Float.hashCode(this.light) * 31) + this.grayList.hashCode();
            }

            @NotNull
            public String toString() {
                return "Config(light=" + this.light + ", grayList=" + this.grayList + ')';
            }

            public Config(float f, @NotNull ArrayList<Pair<Float, Float>> arrayList) {
                Intrinsics.checkNotNullParameter(arrayList, "grayList");
                this.light = f;
                this.grayList = arrayList;
            }

            @NotNull
            public final ArrayList<Pair<Float, Float>> getGrayList() {
                return this.grayList;
            }

            public final float getLight() {
                return this.light;
            }
        }

        public LbmConfig(@NotNull Context context) {
            Intrinsics.checkNotNullParameter(context, "context");
            String string = context.getString(R$string.config_decorationAlphaInLbm);
            this.mConfigString = string;
            this.mConfigs = new ArrayList<>();
            this.lbmLight = -1.0f;
            try {
                CameraDecoration.Companion.logDebug("CameraDecoration", Intrinsics.stringPlus("load alpha config: ", string));
                Intrinsics.checkNotNullExpressionValue(string, "mConfigString");
                for (String split$default : StringsKt__StringsKt.split$default(string, new String[]{"|"}, false, 0, 6, (Object) null)) {
                    List split$default2 = StringsKt__StringsKt.split$default(split$default, new String[]{this.lightSplit}, false, 0, 6, (Object) null);
                    ArrayList arrayList = new ArrayList();
                    String str = (String) split$default2.get(0);
                    if (str != null) {
                        float parseFloat = Float.parseFloat(StringsKt__StringsKt.trim(str).toString());
                        String str2 = (String) split$default2.get(1);
                        if (str2 != null) {
                            for (String split$default3 : StringsKt__StringsKt.split$default(StringsKt__StringsKt.trim(str2).toString(), new String[]{this.itemSplit}, false, 0, 6, (Object) null)) {
                                List split$default4 = StringsKt__StringsKt.split$default(split$default3, new String[]{this.graySplit}, false, 0, 6, (Object) null);
                                String str3 = (String) split$default4.get(0);
                                if (str3 != null) {
                                    Float valueOf = Float.valueOf(Float.parseFloat(StringsKt__StringsKt.trim(str3).toString()));
                                    String str4 = (String) split$default4.get(1);
                                    if (str4 != null) {
                                        arrayList.add(new Pair(valueOf, Float.valueOf(Float.parseFloat(StringsKt__StringsKt.trim(str4).toString()))));
                                    } else {
                                        throw new NullPointerException("null cannot be cast to non-null type kotlin.CharSequence");
                                    }
                                } else {
                                    throw new NullPointerException("null cannot be cast to non-null type kotlin.CharSequence");
                                }
                            }
                            if (arrayList.size() > 1) {
                                CollectionsKt__MutableCollectionsJVMKt.sortWith(arrayList, new CameraDecoration$LbmConfig$special$$inlined$sortBy$1());
                            }
                            this.mConfigs.add(new Config(parseFloat, arrayList));
                        } else {
                            throw new NullPointerException("null cannot be cast to non-null type kotlin.CharSequence");
                        }
                    } else {
                        throw new NullPointerException("null cannot be cast to non-null type kotlin.CharSequence");
                    }
                }
                ArrayList<Config> arrayList2 = this.mConfigs;
                if (arrayList2.size() > 1) {
                    CollectionsKt__MutableCollectionsJVMKt.sortWith(arrayList2, new CameraDecoration$LbmConfig$special$$inlined$sortBy$2());
                }
                ArrayList<Config> arrayList3 = this.mConfigs;
                this.lbmLight = arrayList3.get(arrayList3.size() - 1).getLight();
            } catch (Exception e) {
                CameraDecoration.Companion.logDebug("CameraDecoration", Intrinsics.stringPlus("format config failed: ", e));
            }
            CameraDecoration.Companion.logDebug("CameraDecoration", Intrinsics.stringPlus("loaded configs: ", this.mConfigs));
        }

        public final float getLbmLight() {
            return this.lbmLight;
        }

        public final boolean isLbmEnabled() {
            return !this.mConfigs.isEmpty();
        }

        public final float mapAlphaValue(float f, float f2) {
            Iterator<Config> it = this.mConfigs.iterator();
            while (true) {
                if (!it.hasNext()) {
                    break;
                }
                Config next = it.next();
                if (f <= next.getLight()) {
                    Iterator<Pair<Float, Float>> it2 = next.getGrayList().iterator();
                    while (it2.hasNext()) {
                        Pair next2 = it2.next();
                        if (f2 <= ((Number) next2.getFirst()).floatValue()) {
                            return ((Number) next2.getSecond()).floatValue();
                        }
                    }
                }
            }
            return 0.0f;
        }
    }

    /* compiled from: CameraDecoration.kt */
    private final class HbmContentObserver extends ContentObserver {
        final /* synthetic */ CameraDecoration this$0;

        /* JADX INFO: super call moved to the top of the method (can break code semantics) */
        public HbmContentObserver(@Nullable CameraDecoration cameraDecoration, Handler handler) {
            super(handler);
            Intrinsics.checkNotNullParameter(cameraDecoration, "this$0");
            this.this$0 = cameraDecoration;
        }

        public void onChange(boolean z, @Nullable Uri uri) {
            if (Intrinsics.areEqual((Object) uri, (Object) CameraDecoration.HIGH_BRIGHTNESS_MODE_ENABLED_URI)) {
                CameraDecoration cameraDecoration = this.this$0;
                boolean z2 = false;
                if (MotorolaSettings.Global.getInt(cameraDecoration.mContext.getContentResolver(), "screen_high_brightness_mode", 0) == 1) {
                    z2 = true;
                }
                cameraDecoration.setHbm(z2);
            }
        }
    }
}
