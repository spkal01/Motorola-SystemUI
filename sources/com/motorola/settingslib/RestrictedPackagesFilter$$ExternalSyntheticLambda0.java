package com.motorola.settingslib;

import java.util.function.Function;

public final /* synthetic */ class RestrictedPackagesFilter$$ExternalSyntheticLambda0 implements Function {
    public final /* synthetic */ RestrictedPackagesFileParser f$0;

    public /* synthetic */ RestrictedPackagesFilter$$ExternalSyntheticLambda0(RestrictedPackagesFileParser restrictedPackagesFileParser) {
        this.f$0 = restrictedPackagesFileParser;
    }

    public final Object apply(Object obj) {
        return this.f$0.getPackagesFromFile((String) obj);
    }
}
