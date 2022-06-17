package com.motorola.settingslib;

import java.util.function.Function;

public final /* synthetic */ class RestrictedPackagesFilter$$ExternalSyntheticLambda1 implements Function {
    public static final /* synthetic */ RestrictedPackagesFilter$$ExternalSyntheticLambda1 INSTANCE = new RestrictedPackagesFilter$$ExternalSyntheticLambda1();

    private /* synthetic */ RestrictedPackagesFilter$$ExternalSyntheticLambda1() {
    }

    public final Object apply(Object obj) {
        return ((RestrictedPackage) obj).packageName;
    }
}
