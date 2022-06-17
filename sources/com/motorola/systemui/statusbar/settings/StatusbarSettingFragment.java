package com.motorola.systemui.statusbar.settings;

import android.os.Bundle;
import android.provider.Settings;
import android.view.MenuItem;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragment;
import androidx.preference.SwitchPreference;
import com.android.systemui.R$string;
import com.android.systemui.R$xml;
import com.motorola.android.provider.MotorolaSettings;

public class StatusbarSettingFragment extends PreferenceFragment {
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setHasOptionsMenu(true);
    }

    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void onCreatePreferences(Bundle bundle, String str) {
        addPreferencesFromResource(R$xml.statusbar_setting_prefs);
    }

    public void onResume() {
        super.onResume();
        getActivity().setTitle(R$string.status_bar_setting_title);
        updateState();
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() != 16908332) {
            return super.onOptionsItemSelected(menuItem);
        }
        getActivity().finish();
        return true;
    }

    public boolean onPreferenceTreeClick(Preference preference) {
        if (preference.getKey().equals("battery_pct")) {
            Settings.System.putInt(getContext().getContentResolver(), "status_bar_show_battery_percent", ((SwitchPreference) preference).isChecked() ? 1 : 0);
            return true;
        } else if (preference.getKey().equals("internet_speed")) {
            MotorolaSettings.Global.putInt(getContext().getContentResolver(), "internet_speed_switch", ((SwitchPreference) preference).isChecked() ? 1 : 0);
            return true;
        } else if (!preference.getKey().equals("notification_icons")) {
            return super.onPreferenceTreeClick(preference);
        } else {
            MotorolaSettings.Global.putInt(getContext().getContentResolver(), "show_notification_icons", ((SwitchPreference) preference).isChecked() ? 1 : 0);
            return true;
        }
    }

    private void updateState() {
        boolean z = false;
        ((SwitchPreference) findPreference("battery_pct")).setChecked(Settings.System.getInt(getContext().getContentResolver(), "status_bar_show_battery_percent", 0) == 1);
        ((SwitchPreference) findPreference("internet_speed")).setChecked(MotorolaSettings.Global.getInt(getContext().getContentResolver(), "internet_speed_switch", 0) == 1);
        int i = MotorolaSettings.Global.getInt(getContext().getContentResolver(), "show_notification_icons", 1);
        SwitchPreference switchPreference = (SwitchPreference) findPreference("notification_icons");
        if (i == 1) {
            z = true;
        }
        switchPreference.setChecked(z);
    }
}
