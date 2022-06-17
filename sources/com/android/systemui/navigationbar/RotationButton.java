package com.android.systemui.navigationbar;

import android.view.View;
import com.android.systemui.navigationbar.buttons.KeyButtonDrawable;
import java.util.function.Consumer;

public interface RotationButton {
    View getCurrentView();

    KeyButtonDrawable getImageDrawable();

    boolean hide();

    boolean isVisible();

    void setCanShowRotationButton(boolean z) {
    }

    void setDarkIntensity(float f);

    void setOnClickListener(View.OnClickListener onClickListener);

    void setOnHoverListener(View.OnHoverListener onHoverListener);

    void setRotationButtonController(RotationButtonController rotationButtonController);

    void setVisibilityChangedCallback(Consumer<Boolean> consumer);

    boolean show();

    void updateIcon(int i, int i2);

    boolean acceptRotationProposal() {
        return getCurrentView() != null;
    }
}
