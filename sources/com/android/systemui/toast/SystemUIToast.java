package com.android.systemui.toast;

import android.animation.Animator;
import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.UserHandle;
import android.util.IconDrawableFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.systemui.R$id;
import com.android.systemui.R$layout;
import com.android.systemui.plugins.ToastPlugin;

public class SystemUIToast implements ToastPlugin.Toast {
    final Context mContext;
    private int mDefaultGravity;
    final int mDefaultHorizontalMargin;
    final int mDefaultVerticalMargin;
    final int mDefaultX;
    private int mDefaultY;
    private final Animator mInAnimator;
    private final LayoutInflater mLayoutInflater;
    private final Animator mOutAnimator;
    private final String mPackageName;
    final ToastPlugin.Toast mPluginToast;
    final CharSequence mText;
    private final View mToastView;
    private final int mUserId;

    private static boolean hasFlag(int i, int i2) {
        return (i & i2) != 0;
    }

    SystemUIToast(LayoutInflater layoutInflater, Context context, CharSequence charSequence, String str, int i, int i2) {
        this(layoutInflater, context, charSequence, (ToastPlugin.Toast) null, str, i, i2);
    }

    SystemUIToast(LayoutInflater layoutInflater, Context context, CharSequence charSequence, ToastPlugin.Toast toast, String str, int i, int i2) {
        this.mDefaultX = 0;
        this.mDefaultHorizontalMargin = 0;
        this.mDefaultVerticalMargin = 0;
        this.mLayoutInflater = layoutInflater;
        this.mContext = context;
        this.mText = charSequence;
        this.mPluginToast = toast;
        this.mPackageName = str;
        this.mUserId = i;
        this.mToastView = inflateToastView();
        this.mInAnimator = createInAnimator();
        this.mOutAnimator = createOutAnimator();
        onOrientationChange(i2);
    }

    public Integer getGravity() {
        if (!isPluginToast() || this.mPluginToast.getGravity() == null) {
            return Integer.valueOf(this.mDefaultGravity);
        }
        return this.mPluginToast.getGravity();
    }

    public Integer getXOffset() {
        if (!isPluginToast() || this.mPluginToast.getXOffset() == null) {
            return 0;
        }
        return this.mPluginToast.getXOffset();
    }

    public Integer getYOffset() {
        if (!isPluginToast() || this.mPluginToast.getYOffset() == null) {
            return Integer.valueOf(this.mDefaultY);
        }
        return this.mPluginToast.getYOffset();
    }

    public Integer getHorizontalMargin() {
        if (!isPluginToast() || this.mPluginToast.getHorizontalMargin() == null) {
            return 0;
        }
        return this.mPluginToast.getHorizontalMargin();
    }

    public Integer getVerticalMargin() {
        if (!isPluginToast() || this.mPluginToast.getVerticalMargin() == null) {
            return 0;
        }
        return this.mPluginToast.getVerticalMargin();
    }

    public View getView() {
        return this.mToastView;
    }

    public Animator getInAnimation() {
        return this.mInAnimator;
    }

    public Animator getOutAnimation() {
        return this.mOutAnimator;
    }

    public boolean hasCustomAnimation() {
        return (getInAnimation() == null && getOutAnimation() == null) ? false : true;
    }

    private boolean isPluginToast() {
        return this.mPluginToast != null;
    }

    private View inflateToastView() {
        if (isPluginToast() && this.mPluginToast.getView() != null) {
            return this.mPluginToast.getView();
        }
        ApplicationInfo applicationInfo = null;
        View inflate = this.mLayoutInflater.inflate(R$layout.text_toast, (ViewGroup) null);
        TextView textView = (TextView) inflate.findViewById(R$id.text);
        ImageView imageView = (ImageView) inflate.findViewById(R$id.icon);
        textView.setText(this.mText);
        try {
            applicationInfo = this.mContext.getPackageManager().getApplicationInfoAsUser(this.mPackageName, 0, this.mUserId);
        } catch (PackageManager.NameNotFoundException unused) {
            Log.e("SystemUIToast", "Package name not found package=" + this.mPackageName + " user=" + this.mUserId);
        }
        if (applicationInfo == null || applicationInfo.targetSdkVersion >= 31) {
            Drawable badgedIcon = getBadgedIcon(this.mContext, this.mPackageName, this.mUserId);
            if (badgedIcon == null) {
                imageView.setVisibility(8);
            } else {
                imageView.setImageDrawable(badgedIcon);
                if (!(applicationInfo == null || applicationInfo.labelRes == 0)) {
                    try {
                        imageView.setContentDescription(this.mContext.getPackageManager().getResourcesForApplication(applicationInfo, new Configuration(this.mContext.getResources().getConfiguration())).getString(applicationInfo.labelRes));
                    } catch (PackageManager.NameNotFoundException unused2) {
                        Log.d("SystemUIToast", "Cannot find application resources for icon label.");
                    }
                }
            }
        } else {
            textView.setMaxLines(Integer.MAX_VALUE);
            inflate.findViewById(R$id.icon).setVisibility(8);
        }
        return inflate;
    }

    public void onOrientationChange(int i) {
        ToastPlugin.Toast toast = this.mPluginToast;
        if (toast != null) {
            toast.onOrientationChange(i);
        }
        this.mDefaultY = this.mContext.getResources().getDimensionPixelSize(17105593);
        this.mDefaultGravity = this.mContext.getResources().getInteger(17694962);
    }

    private Animator createInAnimator() {
        if (!isPluginToast() || this.mPluginToast.getInAnimation() == null) {
            return ToastDefaultAnimation.Companion.toastIn(getView());
        }
        return this.mPluginToast.getInAnimation();
    }

    private Animator createOutAnimator() {
        if (!isPluginToast() || this.mPluginToast.getOutAnimation() == null) {
            return ToastDefaultAnimation.Companion.toastOut(getView());
        }
        return this.mPluginToast.getOutAnimation();
    }

    public static Drawable getBadgedIcon(Context context, String str, int i) {
        if (!(context.getApplicationContext() instanceof Application)) {
            return null;
        }
        try {
            PackageManager packageManager = context.getPackageManager();
            ApplicationInfo applicationInfoAsUser = packageManager.getApplicationInfoAsUser(str, 128, i);
            if (applicationInfoAsUser != null) {
                if (showApplicationIcon(applicationInfoAsUser, packageManager)) {
                    return IconDrawableFactory.newInstance(context).getBadgedIcon(applicationInfoAsUser, UserHandle.getUserId(applicationInfoAsUser.uid));
                }
            }
            return null;
        } catch (PackageManager.NameNotFoundException unused) {
            Log.e("SystemUIToast", "Couldn't find application info for packageName=" + str + " userId=" + i);
            return null;
        }
    }

    private static boolean showApplicationIcon(ApplicationInfo applicationInfo, PackageManager packageManager) {
        if (!hasFlag(applicationInfo.flags, 128)) {
            return !hasFlag(applicationInfo.flags, 1);
        }
        if (packageManager.getLaunchIntentForPackage(applicationInfo.packageName) != null) {
            return true;
        }
        return false;
    }
}
