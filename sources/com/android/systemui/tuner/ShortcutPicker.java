package com.android.systemui.tuner;

import android.content.Context;
import android.content.pm.LauncherActivityInfo;
import android.content.pm.LauncherApps;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Process;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceFragment;
import androidx.preference.PreferenceScreen;
import androidx.preference.PreferenceViewHolder;
import com.android.systemui.Dependency;
import com.android.systemui.R$drawable;
import com.android.systemui.R$string;
import com.android.systemui.tuner.ShortcutParser;
import com.android.systemui.tuner.TunerService;
import java.util.ArrayList;
import java.util.List;

public class ShortcutPicker extends PreferenceFragment implements TunerService.Tunable {
    private String mKey;
    private SelectablePreference mNonePreference;
    private final ArrayList<SelectablePreference> mSelectablePreferences = new ArrayList<>();
    private TunerService mTunerService;

    public void onCreatePreferences(Bundle bundle, String str) {
        Context context = getPreferenceManager().getContext();
        PreferenceScreen createPreferenceScreen = getPreferenceManager().createPreferenceScreen(context);
        createPreferenceScreen.setOrderingAsAdded(true);
        PreferenceCategory preferenceCategory = new PreferenceCategory(context);
        preferenceCategory.setTitle(R$string.tuner_other_apps);
        SelectablePreference selectablePreference = new SelectablePreference(context);
        this.mNonePreference = selectablePreference;
        this.mSelectablePreferences.add(selectablePreference);
        this.mNonePreference.setTitle(R$string.lockscreen_none);
        this.mNonePreference.setIcon(R$drawable.ic_remove_circle);
        createPreferenceScreen.addPreference(this.mNonePreference);
        List<LauncherActivityInfo> activityList = ((LauncherApps) getContext().getSystemService(LauncherApps.class)).getActivityList((String) null, Process.myUserHandle());
        createPreferenceScreen.addPreference(preferenceCategory);
        activityList.forEach(new ShortcutPicker$$ExternalSyntheticLambda1(this, context, createPreferenceScreen, preferenceCategory));
        createPreferenceScreen.removePreference(preferenceCategory);
        for (int i = 0; i < preferenceCategory.getPreferenceCount(); i++) {
            Preference preference = preferenceCategory.getPreference(0);
            preferenceCategory.removePreference(preference);
            preference.setOrder(Integer.MAX_VALUE);
            createPreferenceScreen.addPreference(preference);
        }
        setPreferenceScreen(createPreferenceScreen);
        this.mKey = getArguments().getString("androidx.preference.PreferenceFragmentCompat.PREFERENCE_ROOT");
        TunerService tunerService = (TunerService) Dependency.get(TunerService.class);
        this.mTunerService = tunerService;
        tunerService.addTunable(this, this.mKey);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onCreatePreferences$1(Context context, PreferenceScreen preferenceScreen, PreferenceCategory preferenceCategory, LauncherActivityInfo launcherActivityInfo) {
        try {
            List<ShortcutParser.Shortcut> shortcuts = new ShortcutParser(getContext(), launcherActivityInfo.getComponentName()).getShortcuts();
            AppPreference appPreference = new AppPreference(context, launcherActivityInfo);
            this.mSelectablePreferences.add(appPreference);
            if (shortcuts.size() != 0) {
                preferenceScreen.addPreference(appPreference);
                shortcuts.forEach(new ShortcutPicker$$ExternalSyntheticLambda0(this, context, launcherActivityInfo, preferenceScreen));
                return;
            }
            preferenceCategory.addPreference(appPreference);
        } catch (PackageManager.NameNotFoundException unused) {
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onCreatePreferences$0(Context context, LauncherActivityInfo launcherActivityInfo, PreferenceScreen preferenceScreen, ShortcutParser.Shortcut shortcut) {
        ShortcutPreference shortcutPreference = new ShortcutPreference(context, shortcut, launcherActivityInfo.getLabel());
        this.mSelectablePreferences.add(shortcutPreference);
        preferenceScreen.addPreference(shortcutPreference);
    }

    public boolean onPreferenceTreeClick(Preference preference) {
        this.mTunerService.setValue(this.mKey, preference.toString());
        getActivity().onBackPressed();
        return true;
    }

    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        if ("sysui_keyguard_left".equals(this.mKey)) {
            getActivity().setTitle(R$string.lockscreen_shortcut_left);
        } else {
            getActivity().setTitle(R$string.lockscreen_shortcut_right);
        }
    }

    public void onDestroy() {
        super.onDestroy();
        this.mTunerService.removeTunable(this);
    }

    public void onTuningChanged(String str, String str2) {
        if (str2 == null) {
            str2 = "";
        }
        this.mSelectablePreferences.forEach(new ShortcutPicker$$ExternalSyntheticLambda2(str2));
    }

    private static class AppPreference extends SelectablePreference {
        private boolean mBinding;
        private final LauncherActivityInfo mInfo;

        public AppPreference(Context context, LauncherActivityInfo launcherActivityInfo) {
            super(context);
            this.mInfo = launcherActivityInfo;
            setTitle((CharSequence) context.getString(R$string.tuner_launch_app, new Object[]{launcherActivityInfo.getLabel()}));
            setSummary((CharSequence) context.getString(R$string.tuner_app, new Object[]{launcherActivityInfo.getLabel()}));
        }

        public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
            this.mBinding = true;
            if (getIcon() == null) {
                setIcon(this.mInfo.getBadgedIcon(getContext().getResources().getConfiguration().densityDpi));
            }
            this.mBinding = false;
            super.onBindViewHolder(preferenceViewHolder);
        }

        /* access modifiers changed from: protected */
        public void notifyChanged() {
            if (!this.mBinding) {
                super.notifyChanged();
            }
        }

        public String toString() {
            return this.mInfo.getComponentName().flattenToString();
        }
    }

    private static class ShortcutPreference extends SelectablePreference {
        private boolean mBinding;
        private final ShortcutParser.Shortcut mShortcut;

        public ShortcutPreference(Context context, ShortcutParser.Shortcut shortcut, CharSequence charSequence) {
            super(context);
            this.mShortcut = shortcut;
            setTitle((CharSequence) shortcut.label);
            setSummary((CharSequence) context.getString(R$string.tuner_app, new Object[]{charSequence}));
        }

        public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
            this.mBinding = true;
            if (getIcon() == null) {
                setIcon(this.mShortcut.icon.loadDrawable(getContext()));
            }
            this.mBinding = false;
            super.onBindViewHolder(preferenceViewHolder);
        }

        /* access modifiers changed from: protected */
        public void notifyChanged() {
            if (!this.mBinding) {
                super.notifyChanged();
            }
        }

        public String toString() {
            return this.mShortcut.toString();
        }
    }
}
