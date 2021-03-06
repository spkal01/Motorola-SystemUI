package com.android.systemui.statusbar.phone;

public interface ShadeController {
    void addPostCollapseAction(Runnable runnable);

    void animateCollapsePanels();

    void animateCollapsePanels(int i);

    void animateCollapsePanels(int i, boolean z);

    void animateCollapsePanels(int i, boolean z, boolean z2);

    void animateCollapsePanels(int i, boolean z, boolean z2, float f);

    boolean closeShadeIfOpen();

    void collapseCliStack();

    void collapsePanel(boolean z);

    boolean collapsePanel();

    void instantExpandNotificationsPanel();

    void postOnShadeExpanded(Runnable runnable);

    void runPostCollapseRunnables();
}
