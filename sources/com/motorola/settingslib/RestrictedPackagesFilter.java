package com.motorola.settingslib;

import android.content.Context;
import android.text.TextUtils;
import com.android.wifitrackerlib.WifiPickerTracker$$ExternalSyntheticLambda11;
import com.motorola.android.provider.MotorolaSettings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class RestrictedPackagesFilter {
    private Set<String> mPackages;
    private final List<String> mPaths = new ArrayList();

    RestrictedPackagesFilter() {
    }

    public final RestrictedPackagesFilter addPath(String str) {
        if (!TextUtils.isEmpty(str)) {
            this.mPaths.add(str);
        }
        return this;
    }

    public final boolean contains(Context context, String str) {
        if (TextUtils.isEmpty(str)) {
            return false;
        }
        if (this.mPackages == null) {
            HashSet hashSet = new HashSet();
            this.mPackages = hashSet;
            hashSet.addAll(getRestrictedPackages(context));
        }
        return this.mPackages.contains(str);
    }

    private Set<String> getRestrictedPackages(Context context) {
        String string = MotorolaSettings.Global.getString(context.getContentResolver(), "channel_id");
        RestrictedPackagesFileParser instance = RestrictedPackagesFileParser.getInstance();
        if (this.mPaths.isEmpty()) {
            return Collections.emptySet();
        }
        Stream stream = this.mPaths.stream();
        Objects.requireNonNull(instance);
        return (Set) stream.map(new RestrictedPackagesFilter$$ExternalSyntheticLambda0(instance)).flatMap(WifiPickerTracker$$ExternalSyntheticLambda11.INSTANCE).filter(new RestrictedPackagesFilter$$ExternalSyntheticLambda2(string)).map(RestrictedPackagesFilter$$ExternalSyntheticLambda1.INSTANCE).collect(Collectors.toSet());
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$getRestrictedPackages$0(String str, RestrictedPackage restrictedPackage) {
        List<String> list = restrictedPackage.channelIds;
        if (!(list != null && !list.isEmpty()) || TextUtils.isEmpty(str)) {
            return true;
        }
        return restrictedPackage.channelIds.contains(str);
    }
}
