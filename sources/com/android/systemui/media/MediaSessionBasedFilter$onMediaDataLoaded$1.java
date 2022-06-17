package com.android.systemui.media;

/* compiled from: MediaSessionBasedFilter.kt */
final class MediaSessionBasedFilter$onMediaDataLoaded$1 implements Runnable {
    final /* synthetic */ MediaData $data;
    final /* synthetic */ boolean $immediately;
    final /* synthetic */ String $key;
    final /* synthetic */ String $oldKey;
    final /* synthetic */ MediaSessionBasedFilter this$0;

    MediaSessionBasedFilter$onMediaDataLoaded$1(MediaData mediaData, String str, String str2, MediaSessionBasedFilter mediaSessionBasedFilter, boolean z) {
        this.$data = mediaData;
        this.$oldKey = str;
        this.$key = str2;
        this.this$0 = mediaSessionBasedFilter;
        this.$immediately = z;
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v17, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v2, resolved type: android.media.session.MediaController} */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public final void run() {
        /*
            r9 = this;
            com.android.systemui.media.MediaData r0 = r9.$data
            android.media.session.MediaSession$Token r0 = r0.getToken()
            if (r0 != 0) goto L_0x0009
            goto L_0x0012
        L_0x0009:
            com.android.systemui.media.MediaSessionBasedFilter r1 = r9.this$0
            java.util.Set r1 = r1.tokensWithNotifications
            r1.add(r0)
        L_0x0012:
            java.lang.String r0 = r9.$oldKey
            r1 = 0
            r2 = 1
            if (r0 == 0) goto L_0x0022
            java.lang.String r3 = r9.$key
            boolean r0 = kotlin.jvm.internal.Intrinsics.areEqual((java.lang.Object) r3, (java.lang.Object) r0)
            if (r0 != 0) goto L_0x0022
            r0 = r2
            goto L_0x0023
        L_0x0022:
            r0 = r1
        L_0x0023:
            if (r0 == 0) goto L_0x004d
            com.android.systemui.media.MediaSessionBasedFilter r3 = r9.this$0
            java.util.Map r3 = r3.keyedTokens
            java.lang.String r4 = r9.$oldKey
            java.lang.String r5 = "null cannot be cast to non-null type kotlin.collections.MutableMap<K, V>"
            java.util.Objects.requireNonNull(r3, r5)
            java.util.Map r3 = kotlin.jvm.internal.TypeIntrinsics.asMutableMap(r3)
            java.lang.Object r3 = r3.remove(r4)
            java.util.Set r3 = (java.util.Set) r3
            if (r3 != 0) goto L_0x003f
            goto L_0x004d
        L_0x003f:
            com.android.systemui.media.MediaSessionBasedFilter r4 = r9.this$0
            java.lang.String r5 = r9.$key
            java.util.Map r4 = r4.keyedTokens
            java.lang.Object r3 = r4.put(r5, r3)
            java.util.Set r3 = (java.util.Set) r3
        L_0x004d:
            com.android.systemui.media.MediaData r3 = r9.$data
            android.media.session.MediaSession$Token r3 = r3.getToken()
            r4 = 0
            if (r3 == 0) goto L_0x0094
            com.android.systemui.media.MediaSessionBasedFilter r3 = r9.this$0
            java.util.Map r3 = r3.keyedTokens
            java.lang.String r5 = r9.$key
            java.lang.Object r3 = r3.get(r5)
            java.util.Set r3 = (java.util.Set) r3
            if (r3 != 0) goto L_0x0068
            r3 = r4
            goto L_0x0076
        L_0x0068:
            com.android.systemui.media.MediaData r5 = r9.$data
            android.media.session.MediaSession$Token r5 = r5.getToken()
            boolean r3 = r3.add(r5)
            java.lang.Boolean r3 = java.lang.Boolean.valueOf(r3)
        L_0x0076:
            if (r3 != 0) goto L_0x0094
            com.android.systemui.media.MediaSessionBasedFilter r3 = r9.this$0
            com.android.systemui.media.MediaData r5 = r9.$data
            java.lang.String r6 = r9.$key
            android.media.session.MediaSession$Token[] r7 = new android.media.session.MediaSession.Token[r2]
            android.media.session.MediaSession$Token r5 = r5.getToken()
            r7[r1] = r5
            java.util.Set r5 = kotlin.collections.SetsKt__SetsKt.mutableSetOf(r7)
            java.util.Map r3 = r3.keyedTokens
            java.lang.Object r3 = r3.put(r6, r5)
            java.util.Set r3 = (java.util.Set) r3
        L_0x0094:
            com.android.systemui.media.MediaSessionBasedFilter r3 = r9.this$0
            java.util.LinkedHashMap r3 = r3.packageControllers
            com.android.systemui.media.MediaData r5 = r9.$data
            java.lang.String r5 = r5.getPackageName()
            java.lang.Object r3 = r3.get(r5)
            java.util.List r3 = (java.util.List) r3
            if (r3 != 0) goto L_0x00aa
            r5 = r4
            goto L_0x00e3
        L_0x00aa:
            java.util.ArrayList r5 = new java.util.ArrayList
            r5.<init>()
            java.util.Iterator r3 = r3.iterator()
        L_0x00b3:
            boolean r6 = r3.hasNext()
            if (r6 == 0) goto L_0x00e3
            java.lang.Object r6 = r3.next()
            r7 = r6
            android.media.session.MediaController r7 = (android.media.session.MediaController) r7
            android.media.session.MediaController$PlaybackInfo r7 = r7.getPlaybackInfo()
            if (r7 != 0) goto L_0x00c8
            r7 = r4
            goto L_0x00d0
        L_0x00c8:
            int r7 = r7.getPlaybackType()
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
        L_0x00d0:
            r8 = 2
            if (r7 != 0) goto L_0x00d4
            goto L_0x00dc
        L_0x00d4:
            int r7 = r7.intValue()
            if (r7 != r8) goto L_0x00dc
            r7 = r2
            goto L_0x00dd
        L_0x00dc:
            r7 = r1
        L_0x00dd:
            if (r7 == 0) goto L_0x00b3
            r5.add(r6)
            goto L_0x00b3
        L_0x00e3:
            if (r5 != 0) goto L_0x00e7
            r1 = r4
            goto L_0x00ef
        L_0x00e7:
            int r1 = r5.size()
            java.lang.Integer r1 = java.lang.Integer.valueOf(r1)
        L_0x00ef:
            if (r1 != 0) goto L_0x00f2
            goto L_0x00ff
        L_0x00f2:
            int r1 = r1.intValue()
            if (r1 != r2) goto L_0x00ff
            java.lang.Object r1 = kotlin.collections.CollectionsKt___CollectionsKt.firstOrNull(r5)
            r4 = r1
            android.media.session.MediaController r4 = (android.media.session.MediaController) r4
        L_0x00ff:
            if (r0 != 0) goto L_0x0179
            if (r4 == 0) goto L_0x0179
            android.media.session.MediaSession$Token r0 = r4.getSessionToken()
            com.android.systemui.media.MediaData r1 = r9.$data
            android.media.session.MediaSession$Token r1 = r1.getToken()
            boolean r0 = kotlin.jvm.internal.Intrinsics.areEqual((java.lang.Object) r0, (java.lang.Object) r1)
            if (r0 != 0) goto L_0x0179
            com.android.systemui.media.MediaSessionBasedFilter r0 = r9.this$0
            java.util.Set r0 = r0.tokensWithNotifications
            android.media.session.MediaSession$Token r1 = r4.getSessionToken()
            boolean r0 = r0.contains(r1)
            if (r0 != 0) goto L_0x0124
            goto L_0x0179
        L_0x0124:
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r1 = "filtering key="
            r0.append(r1)
            java.lang.String r1 = r9.$key
            r0.append(r1)
            java.lang.String r1 = " local="
            r0.append(r1)
            com.android.systemui.media.MediaData r1 = r9.$data
            android.media.session.MediaSession$Token r1 = r1.getToken()
            r0.append(r1)
            java.lang.String r1 = " remote="
            r0.append(r1)
            android.media.session.MediaSession$Token r1 = r4.getSessionToken()
            r0.append(r1)
            java.lang.String r0 = r0.toString()
            java.lang.String r1 = "MediaSessionBasedFilter"
            android.util.Log.d(r1, r0)
            com.android.systemui.media.MediaSessionBasedFilter r0 = r9.this$0
            java.util.Map r0 = r0.keyedTokens
            java.lang.String r1 = r9.$key
            java.lang.Object r0 = r0.get(r1)
            java.util.Set r0 = (java.util.Set) r0
            kotlin.jvm.internal.Intrinsics.checkNotNull(r0)
            android.media.session.MediaSession$Token r1 = r4.getSessionToken()
            boolean r0 = r0.contains(r1)
            if (r0 != 0) goto L_0x0186
            com.android.systemui.media.MediaSessionBasedFilter r0 = r9.this$0
            java.lang.String r9 = r9.$key
            r0.dispatchMediaDataRemoved(r9)
            goto L_0x0186
        L_0x0179:
            com.android.systemui.media.MediaSessionBasedFilter r0 = r9.this$0
            java.lang.String r1 = r9.$key
            java.lang.String r2 = r9.$oldKey
            com.android.systemui.media.MediaData r3 = r9.$data
            boolean r9 = r9.$immediately
            r0.dispatchMediaDataLoaded(r1, r2, r3, r9)
        L_0x0186:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.media.MediaSessionBasedFilter$onMediaDataLoaded$1.run():void");
    }
}
