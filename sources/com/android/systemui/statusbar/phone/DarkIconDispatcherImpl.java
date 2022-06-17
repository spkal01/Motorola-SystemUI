package com.android.systemui.statusbar.phone;

import android.animation.ArgbEvaluator;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Rect;
import android.util.ArrayMap;
import android.widget.ImageView;
import androidx.appcompat.R$styleable;
import com.android.systemui.R$color;
import com.android.systemui.plugins.DarkIconDispatcher;
import com.android.systemui.statusbar.CommandQueue;
import com.android.systemui.statusbar.phone.LightBarTransitionsController;
import java.io.FileDescriptor;
import java.io.PrintWriter;

public class DarkIconDispatcherImpl implements SysuiDarkIconDispatcher, LightBarTransitionsController.DarkIntensityApplier {
    private float mDarkIntensity;
    private int mDarkModeIconColorSingleTone;
    private int mIconTint = -1;
    private int mLightModeIconColorSingleTone;
    private final ArrayMap<Object, DarkIconDispatcher.DarkReceiver> mReceivers = new ArrayMap<>();
    private final Rect mTintArea = new Rect();
    private final LightBarTransitionsController mTransitionsController;

    public int getTintAnimationDuration() {
        return R$styleable.AppCompatTheme_windowFixedHeightMajor;
    }

    public DarkIconDispatcherImpl(Context context, CommandQueue commandQueue) {
        this.mDarkModeIconColorSingleTone = context.getColor(R$color.dark_mode_icon_color_single_tone);
        this.mLightModeIconColorSingleTone = context.getColor(R$color.light_mode_icon_color_single_tone);
        this.mTransitionsController = new LightBarTransitionsController(context, this, commandQueue);
    }

    public LightBarTransitionsController getTransitionsController() {
        return this.mTransitionsController;
    }

    public void addDarkReceiver(DarkIconDispatcher.DarkReceiver darkReceiver) {
        this.mReceivers.put(darkReceiver, darkReceiver);
        darkReceiver.onDarkChanged(this.mTintArea, this.mDarkIntensity, this.mIconTint);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$addDarkReceiver$0(ImageView imageView, Rect rect, float f, int i) {
        imageView.setImageTintList(ColorStateList.valueOf(DarkIconDispatcher.getTint(this.mTintArea, imageView, this.mIconTint)));
    }

    public void addDarkReceiver(ImageView imageView) {
        DarkIconDispatcherImpl$$ExternalSyntheticLambda0 darkIconDispatcherImpl$$ExternalSyntheticLambda0 = new DarkIconDispatcherImpl$$ExternalSyntheticLambda0(this, imageView);
        this.mReceivers.put(imageView, darkIconDispatcherImpl$$ExternalSyntheticLambda0);
        darkIconDispatcherImpl$$ExternalSyntheticLambda0.onDarkChanged(this.mTintArea, this.mDarkIntensity, this.mIconTint);
    }

    public void removeDarkReceiver(DarkIconDispatcher.DarkReceiver darkReceiver) {
        this.mReceivers.remove(darkReceiver);
    }

    public void removeDarkReceiver(ImageView imageView) {
        this.mReceivers.remove(imageView);
    }

    public void applyDark(DarkIconDispatcher.DarkReceiver darkReceiver) {
        this.mReceivers.get(darkReceiver).onDarkChanged(this.mTintArea, this.mDarkIntensity, this.mIconTint);
    }

    public void setIconsDarkArea(Rect rect) {
        if (rect != null || !this.mTintArea.isEmpty()) {
            if (rect == null) {
                this.mTintArea.setEmpty();
            } else {
                this.mTintArea.set(rect);
            }
            applyIconTint();
        }
    }

    public void applyDarkIntensity(float f) {
        this.mDarkIntensity = f;
        this.mIconTint = ((Integer) ArgbEvaluator.getInstance().evaluate(f, Integer.valueOf(this.mLightModeIconColorSingleTone), Integer.valueOf(this.mDarkModeIconColorSingleTone))).intValue();
        applyIconTint();
    }

    private void applyIconTint() {
        for (int i = 0; i < this.mReceivers.size(); i++) {
            this.mReceivers.valueAt(i).onDarkChanged(this.mTintArea, this.mDarkIntensity, this.mIconTint);
        }
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        printWriter.println("DarkIconDispatcher: ");
        printWriter.println("  mIconTint: 0x" + Integer.toHexString(this.mIconTint));
        printWriter.println("  mDarkIntensity: " + this.mDarkIntensity + "f");
        StringBuilder sb = new StringBuilder();
        sb.append("  mTintArea: ");
        sb.append(this.mTintArea);
        printWriter.println(sb.toString());
    }
}
