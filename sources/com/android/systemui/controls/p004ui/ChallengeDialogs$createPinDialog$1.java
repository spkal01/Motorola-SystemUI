package com.android.systemui.controls.p004ui;

import android.app.AlertDialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;

/* renamed from: com.android.systemui.controls.ui.ChallengeDialogs$createPinDialog$1 */
/* compiled from: ChallengeDialogs.kt */
public final class ChallengeDialogs$createPinDialog$1 extends AlertDialog {
    ChallengeDialogs$createPinDialog$1(Context context) {
        super(context, 16974545);
    }

    public void dismiss() {
        View decorView;
        InputMethodManager inputMethodManager;
        Window window = getWindow();
        if (!(window == null || (decorView = window.getDecorView()) == null || (inputMethodManager = (InputMethodManager) decorView.getContext().getSystemService(InputMethodManager.class)) == null)) {
            inputMethodManager.hideSoftInputFromWindow(decorView.getWindowToken(), 0);
        }
        super.dismiss();
    }
}
