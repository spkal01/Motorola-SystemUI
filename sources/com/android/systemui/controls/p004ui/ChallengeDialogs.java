package com.android.systemui.controls.p004ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.res.Resources;
import android.service.controls.actions.BooleanAction;
import android.service.controls.actions.CommandAction;
import android.service.controls.actions.ControlAction;
import android.service.controls.actions.FloatAction;
import android.service.controls.actions.ModeAction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import com.android.systemui.R$layout;
import com.android.systemui.R$string;
import kotlin.Pair;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/* renamed from: com.android.systemui.controls.ui.ChallengeDialogs */
/* compiled from: ChallengeDialogs.kt */
public final class ChallengeDialogs {
    @NotNull
    public static final ChallengeDialogs INSTANCE = new ChallengeDialogs();

    private ChallengeDialogs() {
    }

    @Nullable
    public final Dialog createPinDialog(@NotNull ControlViewHolder controlViewHolder, boolean z, boolean z2, @NotNull Function0<Unit> function0) {
        Pair pair;
        Intrinsics.checkNotNullParameter(controlViewHolder, "cvh");
        Intrinsics.checkNotNullParameter(function0, "onCancel");
        ControlAction lastAction = controlViewHolder.getLastAction();
        if (lastAction == null) {
            Log.e("ControlsUiController", "PIN Dialog attempted but no last action is set. Will not show");
            return null;
        }
        Resources resources = controlViewHolder.getContext().getResources();
        if (z2) {
            pair = new Pair(resources.getString(R$string.controls_pin_wrong), Integer.valueOf(R$string.controls_pin_instructions_retry));
        } else {
            pair = new Pair(resources.getString(R$string.controls_pin_verify, new Object[]{controlViewHolder.getTitle().getText()}), Integer.valueOf(R$string.controls_pin_instructions));
        }
        int intValue = ((Number) pair.component2()).intValue();
        ChallengeDialogs$createPinDialog$1 challengeDialogs$createPinDialog$1 = new ChallengeDialogs$createPinDialog$1(controlViewHolder.getContext());
        challengeDialogs$createPinDialog$1.setTitle((String) pair.component1());
        challengeDialogs$createPinDialog$1.setView(LayoutInflater.from(challengeDialogs$createPinDialog$1.getContext()).inflate(R$layout.controls_dialog_pin, (ViewGroup) null));
        challengeDialogs$createPinDialog$1.setButton(-1, challengeDialogs$createPinDialog$1.getContext().getText(17039370), new ChallengeDialogs$createPinDialog$2$1(controlViewHolder, lastAction));
        challengeDialogs$createPinDialog$1.setButton(-2, challengeDialogs$createPinDialog$1.getContext().getText(17039360), new ChallengeDialogs$createPinDialog$2$2(function0));
        Window window = challengeDialogs$createPinDialog$1.getWindow();
        window.setType(2020);
        window.setSoftInputMode(4);
        challengeDialogs$createPinDialog$1.setOnShowListener(new ChallengeDialogs$createPinDialog$2$4(challengeDialogs$createPinDialog$1, intValue, z));
        return challengeDialogs$createPinDialog$1;
    }

    @Nullable
    public final Dialog createConfirmationDialog(@NotNull ControlViewHolder controlViewHolder, @NotNull Function0<Unit> function0) {
        Intrinsics.checkNotNullParameter(controlViewHolder, "cvh");
        Intrinsics.checkNotNullParameter(function0, "onCancel");
        ControlAction lastAction = controlViewHolder.getLastAction();
        if (lastAction == null) {
            Log.e("ControlsUiController", "Confirmation Dialog attempted but no last action is set. Will not show");
            return null;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(controlViewHolder.getContext(), 16974545);
        builder.setTitle(controlViewHolder.getContext().getResources().getString(R$string.controls_confirmation_message, new Object[]{controlViewHolder.getTitle().getText()}));
        builder.setPositiveButton(17039370, new ChallengeDialogs$createConfirmationDialog$builder$1$1(controlViewHolder, lastAction));
        builder.setNegativeButton(17039360, new ChallengeDialogs$createConfirmationDialog$builder$1$2(function0));
        AlertDialog create = builder.create();
        create.getWindow().setType(2020);
        return create;
    }

    /* access modifiers changed from: private */
    public final void setInputType(EditText editText, boolean z) {
        if (z) {
            editText.setInputType(129);
        } else {
            editText.setInputType(18);
        }
    }

    /* access modifiers changed from: private */
    public final ControlAction addChallengeValue(ControlAction controlAction, String str) {
        String templateId = controlAction.getTemplateId();
        if (controlAction instanceof BooleanAction) {
            return new BooleanAction(templateId, ((BooleanAction) controlAction).getNewState(), str);
        }
        if (controlAction instanceof FloatAction) {
            return new FloatAction(templateId, ((FloatAction) controlAction).getNewValue(), str);
        }
        if (controlAction instanceof CommandAction) {
            return new CommandAction(templateId, str);
        }
        if (controlAction instanceof ModeAction) {
            return new ModeAction(templateId, ((ModeAction) controlAction).getNewMode(), str);
        }
        throw new IllegalStateException(Intrinsics.stringPlus("'action' is not a known type: ", controlAction));
    }
}
