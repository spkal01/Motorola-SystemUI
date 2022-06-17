package com.motorola.settingslib;

import java.util.List;

class RestrictedPackage {
    final List<String> channelIds;
    final String packageName;

    public RestrictedPackage(Builder builder) {
        this.packageName = builder.packageName;
        this.channelIds = builder.channelIds;
    }

    public static class Builder {
        /* access modifiers changed from: private */
        public List<String> channelIds;
        /* access modifiers changed from: private */
        public final String packageName;

        public Builder(String str) {
            this.packageName = str;
        }

        public Builder setChannelIds(List<String> list) {
            this.channelIds = list;
            return this;
        }

        public RestrictedPackage build() {
            return new RestrictedPackage(this);
        }
    }
}
