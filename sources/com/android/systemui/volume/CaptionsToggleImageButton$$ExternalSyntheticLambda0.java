package com.android.systemui.volume;

import android.view.View;
import androidx.core.view.accessibility.AccessibilityViewCommand;

public final /* synthetic */ class CaptionsToggleImageButton$$ExternalSyntheticLambda0 implements AccessibilityViewCommand {
    public final /* synthetic */ CaptionsToggleImageButton f$0;

    public /* synthetic */ CaptionsToggleImageButton$$ExternalSyntheticLambda0(CaptionsToggleImageButton captionsToggleImageButton) {
        this.f$0 = captionsToggleImageButton;
    }

    public final boolean perform(View view, AccessibilityViewCommand.CommandArguments commandArguments) {
        return this.f$0.lambda$setCaptionsEnabled$0(view, commandArguments);
    }
}
