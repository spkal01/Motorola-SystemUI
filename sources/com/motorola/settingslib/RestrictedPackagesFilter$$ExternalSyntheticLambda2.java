package com.motorola.settingslib;

import java.util.function.Predicate;

public final /* synthetic */ class RestrictedPackagesFilter$$ExternalSyntheticLambda2 implements Predicate {
    public final /* synthetic */ String f$0;

    public /* synthetic */ RestrictedPackagesFilter$$ExternalSyntheticLambda2(String str) {
        this.f$0 = str;
    }

    public final boolean test(Object obj) {
        return RestrictedPackagesFilter.lambda$getRestrictedPackages$0(this.f$0, (RestrictedPackage) obj);
    }
}
