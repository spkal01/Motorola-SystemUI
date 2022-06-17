package androidx.slice;

import android.content.Context;
import android.os.Bundle;
import androidx.slice.core.SliceAction;
import androidx.slice.core.SliceActionImpl;
import androidx.slice.core.SliceQuery;
import androidx.slice.widget.ListContent;
import androidx.slice.widget.RowContent;
import java.util.ArrayList;
import java.util.List;

public class SliceMetadata {
    private Context mContext;
    private long mExpiry;
    private RowContent mHeaderContent;
    private final Bundle mHostExtras;
    private long mLastUpdated;
    private ListContent mListContent;
    private SliceAction mPrimaryAction;
    private Slice mSlice;
    private List<SliceAction> mSliceActions;
    private int mTemplateType;

    public static SliceMetadata from(Context context, Slice slice) {
        return new SliceMetadata(context, slice);
    }

    /* JADX WARNING: Removed duplicated region for block: B:18:0x007d  */
    /* JADX WARNING: Removed duplicated region for block: B:34:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private SliceMetadata(android.content.Context r5, androidx.slice.Slice r6) {
        /*
            r4 = this;
            r4.<init>()
            r4.mSlice = r6
            r4.mContext = r5
            java.lang.String r5 = "long"
            java.lang.String r0 = "ttl"
            r1 = 0
            androidx.slice.SliceItem r0 = androidx.slice.core.SliceQuery.find((androidx.slice.Slice) r6, (java.lang.String) r5, (java.lang.String) r0, (java.lang.String) r1)
            if (r0 == 0) goto L_0x0019
            long r2 = r0.getLong()
            r4.mExpiry = r2
        L_0x0019:
            java.lang.String r0 = "last_updated"
            androidx.slice.SliceItem r5 = androidx.slice.core.SliceQuery.find((androidx.slice.Slice) r6, (java.lang.String) r5, (java.lang.String) r0, (java.lang.String) r1)
            if (r5 == 0) goto L_0x0027
            long r0 = r5.getLong()
            r4.mLastUpdated = r0
        L_0x0027:
            java.lang.String r5 = "bundle"
            java.lang.String r0 = "host_extras"
            androidx.slice.SliceItem r5 = androidx.slice.core.SliceQuery.findSubtype((androidx.slice.Slice) r6, (java.lang.String) r5, (java.lang.String) r0)
            if (r5 == 0) goto L_0x003c
            java.lang.Object r5 = r5.mObj
            boolean r0 = r5 instanceof android.os.Bundle
            if (r0 == 0) goto L_0x003c
            android.os.Bundle r5 = (android.os.Bundle) r5
            r4.mHostExtras = r5
            goto L_0x0040
        L_0x003c:
            android.os.Bundle r5 = android.os.Bundle.EMPTY
            r4.mHostExtras = r5
        L_0x0040:
            androidx.slice.widget.ListContent r5 = new androidx.slice.widget.ListContent
            r5.<init>(r6)
            r4.mListContent = r5
            androidx.slice.widget.RowContent r5 = r5.getHeader()
            r4.mHeaderContent = r5
            androidx.slice.widget.ListContent r5 = r4.mListContent
            int r5 = r5.getHeaderTemplateType()
            r4.mTemplateType = r5
            androidx.slice.widget.ListContent r5 = r4.mListContent
            android.content.Context r6 = r4.mContext
            androidx.slice.core.SliceAction r5 = r5.getShortcut(r6)
            r4.mPrimaryAction = r5
            androidx.slice.widget.ListContent r5 = r4.mListContent
            java.util.List r5 = r5.getSliceActions()
            r4.mSliceActions = r5
            if (r5 != 0) goto L_0x00b6
            androidx.slice.widget.RowContent r5 = r4.mHeaderContent
            if (r5 == 0) goto L_0x00b6
            androidx.slice.SliceItem r5 = r5.getSliceItem()
            java.lang.String r6 = "list_item"
            java.lang.String[] r6 = new java.lang.String[]{r6}
            boolean r5 = androidx.slice.core.SliceQuery.hasHints(r5, r6)
            if (r5 == 0) goto L_0x00b6
            androidx.slice.widget.RowContent r5 = r4.mHeaderContent
            java.util.List r5 = r5.getEndItems()
            java.util.ArrayList r6 = new java.util.ArrayList
            r6.<init>()
            r0 = 0
        L_0x0089:
            int r1 = r5.size()
            if (r0 >= r1) goto L_0x00ae
            java.lang.Object r1 = r5.get(r0)
            androidx.slice.SliceItem r1 = (androidx.slice.SliceItem) r1
            java.lang.String r2 = "action"
            androidx.slice.SliceItem r1 = androidx.slice.core.SliceQuery.find((androidx.slice.SliceItem) r1, (java.lang.String) r2)
            if (r1 == 0) goto L_0x00ab
            androidx.slice.core.SliceActionImpl r1 = new androidx.slice.core.SliceActionImpl
            java.lang.Object r2 = r5.get(r0)
            androidx.slice.SliceItem r2 = (androidx.slice.SliceItem) r2
            r1.<init>(r2)
            r6.add(r1)
        L_0x00ab:
            int r0 = r0 + 1
            goto L_0x0089
        L_0x00ae:
            int r5 = r6.size()
            if (r5 <= 0) goto L_0x00b6
            r4.mSliceActions = r6
        L_0x00b6:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: androidx.slice.SliceMetadata.<init>(android.content.Context, androidx.slice.Slice):void");
    }

    public List<SliceAction> getSliceActions() {
        return this.mSliceActions;
    }

    public int getLoadingState() {
        boolean z = SliceQuery.find(this.mSlice, (String) null, "partial", (String) null) != null;
        if (!this.mListContent.isValid()) {
            return 0;
        }
        if (z) {
            return 1;
        }
        return 2;
    }

    public long getLastUpdatedTime() {
        return this.mLastUpdated;
    }

    public boolean isPermissionSlice() {
        return this.mSlice.hasHint("permission_request");
    }

    public static List<SliceAction> getSliceActions(Slice slice) {
        SliceItem find = SliceQuery.find(slice, "slice", "actions", (String) null);
        List<SliceItem> findAll = find != null ? SliceQuery.findAll(find, "slice", new String[]{"actions", "shortcut"}, (String[]) null) : null;
        if (findAll == null) {
            return null;
        }
        ArrayList arrayList = new ArrayList(findAll.size());
        for (int i = 0; i < findAll.size(); i++) {
            arrayList.add(new SliceActionImpl(findAll.get(i)));
        }
        return arrayList;
    }

    public boolean isExpired() {
        long currentTimeMillis = System.currentTimeMillis();
        long j = this.mExpiry;
        return (j == 0 || j == -1 || currentTimeMillis <= j) ? false : true;
    }

    public boolean neverExpires() {
        return this.mExpiry == -1;
    }

    public long getTimeToExpiry() {
        long currentTimeMillis = System.currentTimeMillis();
        long j = this.mExpiry;
        if (j == 0 || j == -1 || currentTimeMillis > j) {
            return 0;
        }
        return j - currentTimeMillis;
    }

    public ListContent getListContent() {
        return this.mListContent;
    }
}
