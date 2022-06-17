package com.android.systemui.user;

import com.android.settingslib.users.EditUserInfoController;

public class UserModule {
    /* access modifiers changed from: package-private */
    public EditUserInfoController provideEditUserInfoController() {
        return new EditUserInfoController("com.android.systemui.fileprovider");
    }
}
