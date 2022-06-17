package com.motorola.systemui.statusbar.settings;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragment;
import com.android.settingslib.collapsingtoolbar.CollapsingToolbarBaseActivity;
import com.android.systemui.Dependency;
import com.android.systemui.R$id;
import com.android.systemui.fragments.FragmentService;

public class StatusbarSettingActivity extends CollapsingToolbarBaseActivity implements PreferenceFragment.OnPreferenceStartFragmentCallback {
    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }
        if (getFragmentManager().findFragmentByTag("tuner") == null) {
            getFragmentManager().beginTransaction().replace(R$id.content_frame, new StatusbarSettingFragment(), "tuner").commit();
        }
    }

    /* access modifiers changed from: protected */
    public void onDestroy() {
        super.onDestroy();
        Dependency.destroy(FragmentService.class, StatusbarSettingActivity$$ExternalSyntheticLambda0.INSTANCE);
    }

    public boolean onMenuItemSelected(int i, MenuItem menuItem) {
        if (menuItem.getItemId() != 16908332) {
            return super.onMenuItemSelected(i, menuItem);
        }
        onBackPressed();
        return true;
    }

    public void onBackPressed() {
        if (!getFragmentManager().popBackStackImmediate()) {
            super.onBackPressed();
        }
    }

    public boolean onPreferenceStartFragment(PreferenceFragment preferenceFragment, Preference preference) {
        Log.d("StatusbarSettingActivity", "onPreferenceStartFragment");
        if ("system_icons".equals(preference.getKey())) {
            SystemIconsSettingFragment systemIconsSettingFragment = new SystemIconsSettingFragment();
            Bundle bundle = new Bundle(1);
            bundle.putString("androidx.preference.PreferenceFragmentCompat.PREFERENCE_ROOT", preference.getKey());
            systemIconsSettingFragment.setArguments(bundle);
            FragmentTransaction beginTransaction = getFragmentManager().beginTransaction();
            setTitle(preference.getTitle());
            beginTransaction.replace(R$id.content_frame, systemIconsSettingFragment);
            beginTransaction.addToBackStack("PreferenceFragment");
            beginTransaction.commit();
            return true;
        }
        Log.d("StatusbarSettingActivity", "Problem launching fragment");
        return false;
    }
}
