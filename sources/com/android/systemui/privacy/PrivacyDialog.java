package com.android.systemui.privacy;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowInsets;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.settingslib.Utils;
import com.android.systemui.R$dimen;
import com.android.systemui.R$drawable;
import com.android.systemui.R$id;
import com.android.systemui.R$layout;
import com.android.systemui.R$string;
import com.android.systemui.R$style;
import com.android.systemui.statusbar.phone.SystemUIDialog;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import kotlin.NoWhenBranchMatchedException;
import kotlin.Unit;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/* compiled from: PrivacyDialog.kt */
public final class PrivacyDialog extends SystemUIDialog {
    @NotNull
    private final View.OnClickListener clickListener;
    @NotNull
    private final List<WeakReference<OnDialogDismissed>> dismissListeners = new ArrayList();
    @NotNull
    private final AtomicBoolean dismissed = new AtomicBoolean(false);
    @NotNull
    private final String enterpriseText;
    private final int iconColorSolid = Utils.getColorAttrDefaultColor(getContext(), 16843827);
    @NotNull
    private final List<PrivacyElement> list;
    private final String phonecall;
    private ViewGroup rootView;

    /* compiled from: PrivacyDialog.kt */
    public interface OnDialogDismissed {
        void onDialogDismissed();
    }

    /* compiled from: PrivacyDialog.kt */
    public /* synthetic */ class WhenMappings {
        public static final /* synthetic */ int[] $EnumSwitchMapping$0;

        static {
            int[] iArr = new int[PrivacyType.values().length];
            iArr[PrivacyType.TYPE_LOCATION.ordinal()] = 1;
            iArr[PrivacyType.TYPE_CAMERA.ordinal()] = 2;
            iArr[PrivacyType.TYPE_MICROPHONE.ordinal()] = 3;
            $EnumSwitchMapping$0 = iArr;
        }
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public PrivacyDialog(@NotNull Context context, @NotNull List<PrivacyElement> list2, @NotNull Function2<? super String, ? super Integer, Unit> function2) {
        super(context, R$style.PrivacyDialog);
        Intrinsics.checkNotNullParameter(context, "context");
        Intrinsics.checkNotNullParameter(list2, "list");
        Intrinsics.checkNotNullParameter(function2, "activityStarter");
        this.list = list2;
        this.enterpriseText = Intrinsics.stringPlus(" ", context.getString(R$string.ongoing_privacy_dialog_enterprise));
        this.phonecall = context.getString(R$string.ongoing_privacy_dialog_phonecall);
        this.clickListener = new PrivacyDialog$clickListener$1(function2);
    }

    /* access modifiers changed from: protected */
    public void onCreate(@Nullable Bundle bundle) {
        super.onCreate(bundle);
        Window window = getWindow();
        if (window != null) {
            window.getAttributes().setFitInsetsTypes(window.getAttributes().getFitInsetsTypes() | WindowInsets.Type.statusBars());
            window.getAttributes().receiveInsetsIgnoringZOrder = true;
            window.setLayout(window.getContext().getResources().getDimensionPixelSize(R$dimen.qs_panel_width), -2);
            window.setGravity(49);
        }
        setContentView(R$layout.privacy_dialog);
        View requireViewById = requireViewById(R$id.root);
        Intrinsics.checkNotNullExpressionValue(requireViewById, "requireViewById<ViewGroup>(R.id.root)");
        this.rootView = (ViewGroup) requireViewById;
        for (PrivacyElement privacyElement : this.list) {
            ViewGroup viewGroup = this.rootView;
            if (viewGroup != null) {
                viewGroup.addView(createView(privacyElement));
            } else {
                Intrinsics.throwUninitializedPropertyAccessException("rootView");
                throw null;
            }
        }
    }

    public final void addOnDismissListener(@NotNull OnDialogDismissed onDialogDismissed) {
        Intrinsics.checkNotNullParameter(onDialogDismissed, "listener");
        if (this.dismissed.get()) {
            onDialogDismissed.onDialogDismissed();
        } else {
            this.dismissListeners.add(new WeakReference(onDialogDismissed));
        }
    }

    /* access modifiers changed from: protected */
    public void onStop() {
        super.onStop();
        this.dismissed.set(true);
        Iterator<WeakReference<OnDialogDismissed>> it = this.dismissListeners.iterator();
        while (it.hasNext()) {
            it.remove();
            OnDialogDismissed onDialogDismissed = (OnDialogDismissed) it.next().get();
            if (onDialogDismissed != null) {
                onDialogDismissed.onDialogDismissed();
            }
        }
    }

    private final View createView(PrivacyElement privacyElement) {
        LayoutInflater from = LayoutInflater.from(getContext());
        int i = R$layout.privacy_dialog_item;
        ViewGroup viewGroup = this.rootView;
        if (viewGroup != null) {
            View inflate = from.inflate(i, viewGroup, false);
            Objects.requireNonNull(inflate, "null cannot be cast to non-null type android.view.ViewGroup");
            ViewGroup viewGroup2 = (ViewGroup) inflate;
            LayerDrawable drawableForType = getDrawableForType(privacyElement.getType());
            int i2 = R$id.icon;
            drawableForType.findDrawableByLayerId(i2).setTint(this.iconColorSolid);
            ImageView imageView = (ImageView) viewGroup2.requireViewById(i2);
            imageView.setImageDrawable(drawableForType);
            PrivacyType type = privacyElement.getType();
            Context context = imageView.getContext();
            Intrinsics.checkNotNullExpressionValue(context, "context");
            imageView.setContentDescription(type.getName(context));
            int stringIdForState = getStringIdForState(privacyElement.getActive());
            CharSequence applicationName = privacyElement.getPhoneCall() ? this.phonecall : privacyElement.getApplicationName();
            if (privacyElement.getEnterprise()) {
                applicationName = TextUtils.concat(new CharSequence[]{applicationName, this.enterpriseText});
            }
            CharSequence string = getContext().getString(stringIdForState, new Object[]{applicationName});
            CharSequence attribution = privacyElement.getAttribution();
            if (attribution != null) {
                CharSequence concat = TextUtils.concat(new CharSequence[]{string, " ", getContext().getString(R$string.ongoing_privacy_dialog_attribution_text, new Object[]{attribution})});
                if (concat != null) {
                    string = concat;
                }
            }
            ((TextView) viewGroup2.requireViewById(R$id.text)).setText(string);
            if (privacyElement.getPhoneCall()) {
                viewGroup2.requireViewById(R$id.chevron).setVisibility(8);
            }
            viewGroup2.setTag(privacyElement);
            if (!privacyElement.getPhoneCall()) {
                viewGroup2.setOnClickListener(this.clickListener);
            }
            return viewGroup2;
        }
        Intrinsics.throwUninitializedPropertyAccessException("rootView");
        throw null;
    }

    private final int getStringIdForState(boolean z) {
        if (z) {
            return R$string.ongoing_privacy_dialog_using_op;
        }
        return R$string.ongoing_privacy_dialog_recent_op;
    }

    private final LayerDrawable getDrawableForType(PrivacyType privacyType) {
        int i;
        Context context = getContext();
        int i2 = WhenMappings.$EnumSwitchMapping$0[privacyType.ordinal()];
        if (i2 == 1) {
            i = R$drawable.privacy_item_circle_location;
        } else if (i2 == 2) {
            i = R$drawable.privacy_item_circle_camera;
        } else if (i2 == 3) {
            i = R$drawable.privacy_item_circle_microphone;
        } else {
            throw new NoWhenBranchMatchedException();
        }
        Drawable drawable = context.getDrawable(i);
        Objects.requireNonNull(drawable, "null cannot be cast to non-null type android.graphics.drawable.LayerDrawable");
        return (LayerDrawable) drawable;
    }

    /* compiled from: PrivacyDialog.kt */
    public static final class PrivacyElement {
        private final boolean active;
        @NotNull
        private final CharSequence applicationName;
        @Nullable
        private final CharSequence attribution;
        @NotNull
        private final StringBuilder builder;
        private final boolean enterprise;
        private final long lastActiveTimestamp;
        @NotNull
        private final String packageName;
        private final boolean phoneCall;
        @NotNull
        private final PrivacyType type;
        private final int userId;

        public boolean equals(@Nullable Object obj) {
            if (this == obj) {
                return true;
            }
            if (!(obj instanceof PrivacyElement)) {
                return false;
            }
            PrivacyElement privacyElement = (PrivacyElement) obj;
            return this.type == privacyElement.type && Intrinsics.areEqual((Object) this.packageName, (Object) privacyElement.packageName) && this.userId == privacyElement.userId && Intrinsics.areEqual((Object) this.applicationName, (Object) privacyElement.applicationName) && Intrinsics.areEqual((Object) this.attribution, (Object) privacyElement.attribution) && this.lastActiveTimestamp == privacyElement.lastActiveTimestamp && this.active == privacyElement.active && this.enterprise == privacyElement.enterprise && this.phoneCall == privacyElement.phoneCall;
        }

        public int hashCode() {
            int hashCode = ((((((this.type.hashCode() * 31) + this.packageName.hashCode()) * 31) + Integer.hashCode(this.userId)) * 31) + this.applicationName.hashCode()) * 31;
            CharSequence charSequence = this.attribution;
            int hashCode2 = (((hashCode + (charSequence == null ? 0 : charSequence.hashCode())) * 31) + Long.hashCode(this.lastActiveTimestamp)) * 31;
            boolean z = this.active;
            boolean z2 = true;
            if (z) {
                z = true;
            }
            int i = (hashCode2 + (z ? 1 : 0)) * 31;
            boolean z3 = this.enterprise;
            if (z3) {
                z3 = true;
            }
            int i2 = (i + (z3 ? 1 : 0)) * 31;
            boolean z4 = this.phoneCall;
            if (!z4) {
                z2 = z4;
            }
            return i2 + (z2 ? 1 : 0);
        }

        public PrivacyElement(@NotNull PrivacyType privacyType, @NotNull String str, int i, @NotNull CharSequence charSequence, @Nullable CharSequence charSequence2, long j, boolean z, boolean z2, boolean z3) {
            Intrinsics.checkNotNullParameter(privacyType, "type");
            Intrinsics.checkNotNullParameter(str, "packageName");
            Intrinsics.checkNotNullParameter(charSequence, "applicationName");
            this.type = privacyType;
            this.packageName = str;
            this.userId = i;
            this.applicationName = charSequence;
            this.attribution = charSequence2;
            this.lastActiveTimestamp = j;
            this.active = z;
            this.enterprise = z2;
            this.phoneCall = z3;
            StringBuilder sb = new StringBuilder("PrivacyElement(");
            this.builder = sb;
            sb.append(Intrinsics.stringPlus("type=", privacyType.getLogName()));
            sb.append(Intrinsics.stringPlus(", packageName=", str));
            sb.append(Intrinsics.stringPlus(", userId=", Integer.valueOf(i)));
            sb.append(Intrinsics.stringPlus(", appName=", charSequence));
            if (charSequence2 != null) {
                sb.append(Intrinsics.stringPlus(", attribution=", charSequence2));
            }
            sb.append(Intrinsics.stringPlus(", lastActive=", Long.valueOf(j)));
            if (z) {
                sb.append(", active");
            }
            if (z2) {
                sb.append(", enterprise");
            }
            if (z3) {
                sb.append(", phoneCall");
            }
            sb.append(")");
        }

        @NotNull
        public final PrivacyType getType() {
            return this.type;
        }

        @NotNull
        public final String getPackageName() {
            return this.packageName;
        }

        public final int getUserId() {
            return this.userId;
        }

        @NotNull
        public final CharSequence getApplicationName() {
            return this.applicationName;
        }

        @Nullable
        public final CharSequence getAttribution() {
            return this.attribution;
        }

        public final long getLastActiveTimestamp() {
            return this.lastActiveTimestamp;
        }

        public final boolean getActive() {
            return this.active;
        }

        public final boolean getEnterprise() {
            return this.enterprise;
        }

        public final boolean getPhoneCall() {
            return this.phoneCall;
        }

        @NotNull
        public String toString() {
            String sb = this.builder.toString();
            Intrinsics.checkNotNullExpressionValue(sb, "builder.toString()");
            return sb;
        }
    }
}
