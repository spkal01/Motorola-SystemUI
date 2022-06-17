package com.motorola.systemui.screenshot;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

public class LensRouterActivity extends Activity {
    private static final boolean DEBUG = Build.IS_DEBUGGABLE;
    private static final String LENS_AVAILABILITY_PROVIDER_URI = String.format("content://%s/publicvalue/lens_oem_availability", new Object[]{"com.google.android.googlequicksearchbox.GsaPublicContentProvider"});

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        startGoogleLens();
        finish();
    }

    /* access modifiers changed from: protected */
    public void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
        if (DEBUG) {
            Log.d("LensRouterActivity", "onActivityResult resultCode: " + i2);
        }
    }

    private void startGoogleLens() {
        try {
            Uri data = getIntent().getData();
            grantUriPermission("com.google.android.googlequicksearchbox", data, 1);
            startActivityForResult(new Intent("android.intent.action.VIEW", Uri.parse("googleapp://lens").buildUpon().appendQueryParameter("LensBitmapUriKey", data.toString()).build()), 0);
        } catch (Exception e) {
            Log.e("LensRouterActivity", "Start Google Lens failed. Exception: " + e);
        }
    }

    public static boolean isLensAvailable(Context context) {
        boolean z = false;
        try {
            Cursor query = context.getContentResolver().query(Uri.parse(LENS_AVAILABILITY_PROVIDER_URI), (String[]) null, (String) null, (String[]) null, (String) null);
            boolean z2 = DEBUG;
            if (z2) {
                Log.d("LensRouterActivity", "Lens availability cursor: " + query);
            }
            if (query != null) {
                if (query.getCount() > 0) {
                    query.moveToFirst();
                    int parseInt = Integer.parseInt(query.getString(0));
                    if (parseInt == 0) {
                        z = true;
                    }
                    if (z2) {
                        Log.d("LensRouterActivity", "Lens availability status: " + parseInt);
                    }
                }
                query.close();
            }
        } catch (Exception e) {
            Log.e("LensRouterActivity", "Query Lens capability failed. Exception: " + e);
        }
        return z;
    }
}
