package com.android.systemui.controls.management;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.os.Bundle;
import android.service.controls.Control;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.systemui.R$dimen;
import com.android.systemui.R$id;
import com.android.systemui.R$layout;
import com.android.systemui.R$string;
import com.android.systemui.broadcast.BroadcastDispatcher;
import com.android.systemui.controls.controller.ControlInfo;
import com.android.systemui.controls.controller.ControlsController;
import com.android.systemui.controls.p004ui.RenderInfo;
import com.android.systemui.statusbar.phone.SystemUIDialog;
import com.android.systemui.util.LifecycleActivity;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/* compiled from: ControlsRequestDialog.kt */
public class ControlsRequestDialog extends LifecycleActivity implements DialogInterface.OnClickListener, DialogInterface.OnCancelListener {
    @NotNull
    public static final Companion Companion = new Companion((DefaultConstructorMarker) null);
    @NotNull
    private final BroadcastDispatcher broadcastDispatcher;
    @NotNull
    private final ControlsRequestDialog$callback$1 callback = new ControlsRequestDialog$callback$1();
    private Control control;
    private ComponentName controlComponent;
    /* access modifiers changed from: private */
    @NotNull
    public final ControlsController controller;
    @NotNull
    private final ControlsListingController controlsListingController;
    @NotNull
    private final ControlsRequestDialog$currentUserTracker$1 currentUserTracker;
    @Nullable
    private Dialog dialog;

    public ControlsRequestDialog(@NotNull ControlsController controlsController, @NotNull BroadcastDispatcher broadcastDispatcher2, @NotNull ControlsListingController controlsListingController2) {
        Intrinsics.checkNotNullParameter(controlsController, "controller");
        Intrinsics.checkNotNullParameter(broadcastDispatcher2, "broadcastDispatcher");
        Intrinsics.checkNotNullParameter(controlsListingController2, "controlsListingController");
        this.controller = controlsController;
        this.broadcastDispatcher = broadcastDispatcher2;
        this.controlsListingController = controlsListingController2;
        this.currentUserTracker = new ControlsRequestDialog$currentUserTracker$1(this, broadcastDispatcher2);
    }

    /* compiled from: ControlsRequestDialog.kt */
    public static final class Companion {
        public /* synthetic */ Companion(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }

        private Companion() {
        }
    }

    /* access modifiers changed from: protected */
    public void onCreate(@Nullable Bundle bundle) {
        super.onCreate(bundle);
        this.currentUserTracker.startTracking();
        this.controlsListingController.addCallback(this.callback);
        int intExtra = getIntent().getIntExtra("android.intent.extra.USER_ID", -10000);
        int currentUserId = this.controller.getCurrentUserId();
        if (intExtra != currentUserId) {
            Log.w("ControlsRequestDialog", "Current user (" + currentUserId + ") different from request user (" + intExtra + ')');
            finish();
        }
        ComponentName componentName = (ComponentName) getIntent().getParcelableExtra("android.intent.extra.COMPONENT_NAME");
        if (componentName == null) {
            Log.e("ControlsRequestDialog", "Request did not contain componentName");
            finish();
            return;
        }
        this.controlComponent = componentName;
        Control parcelableExtra = getIntent().getParcelableExtra("android.service.controls.extra.CONTROL");
        if (parcelableExtra == null) {
            Log.e("ControlsRequestDialog", "Request did not contain control");
            finish();
            return;
        }
        this.control = parcelableExtra;
    }

    /* access modifiers changed from: protected */
    public void onResume() {
        super.onResume();
        CharSequence verifyComponentAndGetLabel = verifyComponentAndGetLabel();
        if (verifyComponentAndGetLabel == null) {
            StringBuilder sb = new StringBuilder();
            sb.append("The component specified (");
            ComponentName componentName = this.controlComponent;
            if (componentName != null) {
                sb.append(componentName.flattenToString());
                sb.append(" is not a valid ControlsProviderService");
                Log.e("ControlsRequestDialog", sb.toString());
                finish();
                return;
            }
            Intrinsics.throwUninitializedPropertyAccessException("controlComponent");
            throw null;
        }
        if (isCurrentFavorite()) {
            StringBuilder sb2 = new StringBuilder();
            sb2.append("The control ");
            Control control2 = this.control;
            if (control2 != null) {
                sb2.append(control2.getTitle());
                sb2.append(" is already a favorite");
                Log.w("ControlsRequestDialog", sb2.toString());
                finish();
            } else {
                Intrinsics.throwUninitializedPropertyAccessException("control");
                throw null;
            }
        }
        Dialog createDialog = createDialog(verifyComponentAndGetLabel);
        this.dialog = createDialog;
        if (createDialog != null) {
            createDialog.show();
        }
    }

    /* access modifiers changed from: protected */
    public void onDestroy() {
        Dialog dialog2 = this.dialog;
        if (dialog2 != null) {
            dialog2.dismiss();
        }
        this.currentUserTracker.stopTracking();
        this.controlsListingController.removeCallback(this.callback);
        super.onDestroy();
    }

    private final CharSequence verifyComponentAndGetLabel() {
        ControlsListingController controlsListingController2 = this.controlsListingController;
        ComponentName componentName = this.controlComponent;
        if (componentName != null) {
            return controlsListingController2.getAppLabel(componentName);
        }
        Intrinsics.throwUninitializedPropertyAccessException("controlComponent");
        throw null;
    }

    /* JADX WARNING: Removed duplicated region for block: B:29:0x0065 A[SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private final boolean isCurrentFavorite() {
        /*
            r7 = this;
            com.android.systemui.controls.controller.ControlsController r0 = r7.controller
            android.content.ComponentName r1 = r7.controlComponent
            r2 = 0
            if (r1 == 0) goto L_0x0066
            java.util.List r0 = r0.getFavoritesForComponent(r1)
            boolean r1 = r0 instanceof java.util.Collection
            r3 = 1
            r4 = 0
            if (r1 == 0) goto L_0x0019
            boolean r1 = r0.isEmpty()
            if (r1 == 0) goto L_0x0019
        L_0x0017:
            r3 = r4
            goto L_0x0065
        L_0x0019:
            java.util.Iterator r0 = r0.iterator()
        L_0x001d:
            boolean r1 = r0.hasNext()
            if (r1 == 0) goto L_0x0017
            java.lang.Object r1 = r0.next()
            com.android.systemui.controls.controller.StructureInfo r1 = (com.android.systemui.controls.controller.StructureInfo) r1
            java.util.List r1 = r1.getControls()
            boolean r5 = r1 instanceof java.util.Collection
            if (r5 == 0) goto L_0x0039
            boolean r5 = r1.isEmpty()
            if (r5 == 0) goto L_0x0039
        L_0x0037:
            r1 = r4
            goto L_0x0063
        L_0x0039:
            java.util.Iterator r1 = r1.iterator()
        L_0x003d:
            boolean r5 = r1.hasNext()
            if (r5 == 0) goto L_0x0037
            java.lang.Object r5 = r1.next()
            com.android.systemui.controls.controller.ControlInfo r5 = (com.android.systemui.controls.controller.ControlInfo) r5
            java.lang.String r5 = r5.getControlId()
            android.service.controls.Control r6 = r7.control
            if (r6 == 0) goto L_0x005d
            java.lang.String r6 = r6.getControlId()
            boolean r5 = kotlin.jvm.internal.Intrinsics.areEqual((java.lang.Object) r5, (java.lang.Object) r6)
            if (r5 == 0) goto L_0x003d
            r1 = r3
            goto L_0x0063
        L_0x005d:
            java.lang.String r7 = "control"
            kotlin.jvm.internal.Intrinsics.throwUninitializedPropertyAccessException(r7)
            throw r2
        L_0x0063:
            if (r1 == 0) goto L_0x001d
        L_0x0065:
            return r3
        L_0x0066:
            java.lang.String r7 = "controlComponent"
            kotlin.jvm.internal.Intrinsics.throwUninitializedPropertyAccessException(r7)
            throw r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.controls.management.ControlsRequestDialog.isCurrentFavorite():boolean");
    }

    @NotNull
    public final Dialog createDialog(@NotNull CharSequence charSequence) {
        Intrinsics.checkNotNullParameter(charSequence, "label");
        RenderInfo.Companion companion = RenderInfo.Companion;
        ComponentName componentName = this.controlComponent;
        if (componentName != null) {
            Control control2 = this.control;
            if (control2 != null) {
                RenderInfo lookup$default = RenderInfo.Companion.lookup$default(companion, this, componentName, control2.getDeviceType(), 0, 8, (Object) null);
                View inflate = LayoutInflater.from(this).inflate(R$layout.controls_dialog, (ViewGroup) null);
                ImageView imageView = (ImageView) inflate.requireViewById(R$id.icon);
                imageView.setImageDrawable(lookup$default.getIcon());
                imageView.setImageTintList(imageView.getContext().getResources().getColorStateList(lookup$default.getForeground(), imageView.getContext().getTheme()));
                TextView textView = (TextView) inflate.requireViewById(R$id.title);
                Control control3 = this.control;
                if (control3 != null) {
                    textView.setText(control3.getTitle());
                    TextView textView2 = (TextView) inflate.requireViewById(R$id.subtitle);
                    Control control4 = this.control;
                    if (control4 != null) {
                        textView2.setText(control4.getSubtitle());
                        inflate.requireViewById(R$id.control).setElevation(inflate.getResources().getFloat(R$dimen.control_card_elevation));
                        AlertDialog create = new AlertDialog.Builder(this).setTitle(getString(R$string.controls_dialog_title)).setMessage(getString(R$string.controls_dialog_message, new Object[]{charSequence})).setPositiveButton(R$string.controls_dialog_ok, this).setNegativeButton(17039360, this).setOnCancelListener(this).setView(inflate).create();
                        SystemUIDialog.registerDismissListener(create);
                        create.setCanceledOnTouchOutside(true);
                        Intrinsics.checkNotNullExpressionValue(create, "dialog");
                        return create;
                    }
                    Intrinsics.throwUninitializedPropertyAccessException("control");
                    throw null;
                }
                Intrinsics.throwUninitializedPropertyAccessException("control");
                throw null;
            }
            Intrinsics.throwUninitializedPropertyAccessException("control");
            throw null;
        }
        Intrinsics.throwUninitializedPropertyAccessException("controlComponent");
        throw null;
    }

    public void onCancel(@Nullable DialogInterface dialogInterface) {
        finish();
    }

    public void onClick(@Nullable DialogInterface dialogInterface, int i) {
        if (i == -1) {
            ControlsController controlsController = this.controller;
            ComponentName componentName = this.controlComponent;
            if (componentName != null) {
                Control control2 = this.control;
                if (control2 != null) {
                    CharSequence structure = control2.getStructure();
                    if (structure == null) {
                        structure = "";
                    }
                    Control control3 = this.control;
                    if (control3 != null) {
                        String controlId = control3.getControlId();
                        Intrinsics.checkNotNullExpressionValue(controlId, "control.controlId");
                        Control control4 = this.control;
                        if (control4 != null) {
                            CharSequence title = control4.getTitle();
                            Intrinsics.checkNotNullExpressionValue(title, "control.title");
                            Control control5 = this.control;
                            if (control5 != null) {
                                CharSequence subtitle = control5.getSubtitle();
                                Intrinsics.checkNotNullExpressionValue(subtitle, "control.subtitle");
                                Control control6 = this.control;
                                if (control6 != null) {
                                    controlsController.addFavorite(componentName, structure, new ControlInfo(controlId, title, subtitle, control6.getDeviceType()));
                                } else {
                                    Intrinsics.throwUninitializedPropertyAccessException("control");
                                    throw null;
                                }
                            } else {
                                Intrinsics.throwUninitializedPropertyAccessException("control");
                                throw null;
                            }
                        } else {
                            Intrinsics.throwUninitializedPropertyAccessException("control");
                            throw null;
                        }
                    } else {
                        Intrinsics.throwUninitializedPropertyAccessException("control");
                        throw null;
                    }
                } else {
                    Intrinsics.throwUninitializedPropertyAccessException("control");
                    throw null;
                }
            } else {
                Intrinsics.throwUninitializedPropertyAccessException("controlComponent");
                throw null;
            }
        }
        finish();
    }
}
