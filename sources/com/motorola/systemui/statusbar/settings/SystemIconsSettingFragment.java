package com.motorola.systemui.statusbar.settings;

import android.os.Bundle;
import android.view.MenuItem;
import androidx.preference.PreferenceFragment;
import com.android.systemui.R$string;
import com.android.systemui.R$xml;

public class SystemIconsSettingFragment extends PreferenceFragment {
    private static final CharSequence KEY_DOZE = "doze";

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setHasOptionsMenu(true);
    }

    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void onCreatePreferences(Bundle bundle, String str) {
        addPreferencesFromResource(R$xml.system_icons_setting_prefs);
        if (!isNfcAvailable()) {
            getPreferenceScreen().removePreference(findPreference("nfc"));
        }
    }

    public void onResume() {
        super.onResume();
        getActivity().setTitle(R$string.system_icons_title);
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() != 16908332) {
            return super.onOptionsItemSelected(menuItem);
        }
        getActivity().finish();
        return true;
    }

    private boolean isNfcAvailable() {
        return getContext().getPackageManager().hasSystemFeature("android.hardware.nfc");
    }
}
