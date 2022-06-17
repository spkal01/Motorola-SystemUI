package com.android.systemui.user;

import android.app.Activity;
import android.app.Dialog;
import android.app.IActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.UserInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import com.android.settingslib.R$string;
import com.android.settingslib.users.EditUserInfoController;
import com.android.systemui.R$layout;

public class CreateUserActivity extends Activity {
    private final IActivityManager mActivityManager;
    private final EditUserInfoController mEditUserInfoController;
    private Dialog mSetupUserDialog;
    private final UserCreator mUserCreator;

    public static Intent createIntentForStart(Context context) {
        return new Intent(context, CreateUserActivity.class);
    }

    public CreateUserActivity(UserCreator userCreator, EditUserInfoController editUserInfoController, IActivityManager iActivityManager) {
        this.mUserCreator = userCreator;
        this.mEditUserInfoController = editUserInfoController;
        this.mActivityManager = iActivityManager;
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setShowWhenLocked(true);
        setContentView(R$layout.activity_create_new_user);
        if (bundle != null) {
            this.mEditUserInfoController.onRestoreInstanceState(bundle);
        }
        Dialog createDialog = createDialog();
        this.mSetupUserDialog = createDialog;
        createDialog.show();
    }

    /* access modifiers changed from: protected */
    public void onSaveInstanceState(Bundle bundle) {
        Dialog dialog = this.mSetupUserDialog;
        if (dialog != null && dialog.isShowing()) {
            bundle.putBundle("create_user_dialog_state", this.mSetupUserDialog.onSaveInstanceState());
        }
        this.mEditUserInfoController.onSaveInstanceState(bundle);
        super.onSaveInstanceState(bundle);
    }

    /* access modifiers changed from: protected */
    public void onRestoreInstanceState(Bundle bundle) {
        Dialog dialog;
        super.onRestoreInstanceState(bundle);
        Bundle bundle2 = bundle.getBundle("create_user_dialog_state");
        if (bundle2 != null && (dialog = this.mSetupUserDialog) != null) {
            dialog.onRestoreInstanceState(bundle2);
        }
    }

    private Dialog createDialog() {
        return this.mEditUserInfoController.createDialog(this, new CreateUserActivity$$ExternalSyntheticLambda0(this), (Drawable) null, getString(R$string.user_new_user_name), getString(com.android.systemui.R$string.user_add_user), new CreateUserActivity$$ExternalSyntheticLambda3(this), new CreateUserActivity$$ExternalSyntheticLambda1(this));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createDialog$0(Intent intent, int i) {
        this.mEditUserInfoController.startingActivityForResult();
        startActivityForResult(intent, i);
    }

    /* access modifiers changed from: protected */
    public void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
        this.mEditUserInfoController.onActivityResult(i, i2, intent);
    }

    public void onBackPressed() {
        super.onBackPressed();
        Dialog dialog = this.mSetupUserDialog;
        if (dialog != null) {
            dialog.dismiss();
        }
    }

    /* access modifiers changed from: private */
    public void addUserNow(String str, Drawable drawable) {
        this.mSetupUserDialog.dismiss();
        if (str == null || str.trim().isEmpty()) {
            str = getString(com.android.systemui.R$string.user_new_user_name);
        }
        this.mUserCreator.createUser(str, drawable, new CreateUserActivity$$ExternalSyntheticLambda4(this), new CreateUserActivity$$ExternalSyntheticLambda2(this));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$addUserNow$1(UserInfo userInfo) {
        switchToUser(userInfo.id);
        finishIfNeeded();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$addUserNow$2() {
        Log.e("CreateUserActivity", "Unable to create user");
        finishIfNeeded();
    }

    private void finishIfNeeded() {
        if (!isFinishing() && !isDestroyed()) {
            finish();
        }
    }

    private void switchToUser(int i) {
        try {
            this.mActivityManager.switchUser(i);
        } catch (RemoteException e) {
            Log.e("CreateUserActivity", "Couldn't switch user.", e);
        }
    }
}
