package com.android.systemui.people;

import com.android.systemui.people.widget.PeopleSpaceWidgetManager;

public final class PeopleProvider_MembersInjector {
    public static void injectMPeopleSpaceWidgetManager(PeopleProvider peopleProvider, PeopleSpaceWidgetManager peopleSpaceWidgetManager) {
        peopleProvider.mPeopleSpaceWidgetManager = peopleSpaceWidgetManager;
    }
}
