package com.android.systemui.people;

import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.app.people.IPeopleManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.PersistableBundle;
import android.os.ServiceManager;
import android.preference.PreferenceManager;
import android.util.Log;
import com.android.systemui.people.widget.PeopleBackupHelper;
import com.android.systemui.people.widget.PeopleTileKey;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class PeopleBackupFollowUpJob extends JobService {
    private static final long CLEAN_UP_STORAGE_AFTER_DURATION = Duration.ofHours(48).toMillis();
    private static final long JOB_PERIODIC_DURATION = Duration.ofHours(6).toMillis();
    private Context mContext;
    private IPeopleManager mIPeopleManager;
    private JobScheduler mJobScheduler;
    private final Object mLock = new Object();
    private PackageManager mPackageManager;

    public boolean onStopJob(JobParameters jobParameters) {
        return false;
    }

    public static void scheduleJob(Context context) {
        PersistableBundle persistableBundle = new PersistableBundle();
        persistableBundle.putLong("start_date", System.currentTimeMillis());
        ((JobScheduler) context.getSystemService(JobScheduler.class)).schedule(new JobInfo.Builder(74823873, new ComponentName(context, PeopleBackupFollowUpJob.class)).setPeriodic(JOB_PERIODIC_DURATION).setExtras(persistableBundle).build());
    }

    public void onCreate() {
        super.onCreate();
        this.mContext = getApplicationContext();
        this.mPackageManager = getApplicationContext().getPackageManager();
        this.mIPeopleManager = IPeopleManager.Stub.asInterface(ServiceManager.getService("people"));
        this.mJobScheduler = (JobScheduler) this.mContext.getSystemService(JobScheduler.class);
    }

    public void setManagers(Context context, PackageManager packageManager, IPeopleManager iPeopleManager, JobScheduler jobScheduler) {
        this.mContext = context;
        this.mPackageManager = packageManager;
        this.mIPeopleManager = iPeopleManager;
        this.mJobScheduler = jobScheduler;
    }

    public boolean onStartJob(JobParameters jobParameters) {
        synchronized (this.mLock) {
            SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
            SharedPreferences.Editor edit = defaultSharedPreferences.edit();
            SharedPreferences sharedPreferences = getSharedPreferences("shared_follow_up", 0);
            SharedPreferences.Editor edit2 = sharedPreferences.edit();
            Map<String, Set<String>> processFollowUpFile = processFollowUpFile(sharedPreferences, edit2);
            if (shouldCancelJob(processFollowUpFile, jobParameters.getExtras().getLong("start_date"), System.currentTimeMillis())) {
                cancelJobAndClearRemainingWidgets(processFollowUpFile, edit2, defaultSharedPreferences);
            }
            edit.apply();
            edit2.apply();
        }
        PeopleBackupHelper.updateWidgets(this.mContext);
        return false;
    }

    public Map<String, Set<String>> processFollowUpFile(SharedPreferences sharedPreferences, SharedPreferences.Editor editor) {
        HashMap hashMap = new HashMap();
        for (Map.Entry next : sharedPreferences.getAll().entrySet()) {
            String str = (String) next.getKey();
            if (PeopleBackupHelper.isReadyForRestore(this.mIPeopleManager, this.mPackageManager, PeopleTileKey.fromString(str))) {
                editor.remove(str);
            } else {
                try {
                    hashMap.put((String) next.getKey(), (Set) next.getValue());
                } catch (Exception unused) {
                    Log.e("PeopleBackupFollowUpJob", "Malformed entry value: " + next.getValue());
                }
            }
        }
        return hashMap;
    }

    public boolean shouldCancelJob(Map<String, Set<String>> map, long j, long j2) {
        if (map.isEmpty()) {
            return true;
        }
        if (j2 - j > CLEAN_UP_STORAGE_AFTER_DURATION) {
            return true;
        }
        return false;
    }

    public void cancelJobAndClearRemainingWidgets(Map<String, Set<String>> map, SharedPreferences.Editor editor, SharedPreferences sharedPreferences) {
        removeUnavailableShortcutsFromSharedStorage(map, sharedPreferences);
        editor.clear();
        this.mJobScheduler.cancel(74823873);
    }

    private void removeUnavailableShortcutsFromSharedStorage(Map<String, Set<String>> map, SharedPreferences sharedPreferences) {
        for (Map.Entry next : map.entrySet()) {
            PeopleTileKey fromString = PeopleTileKey.fromString((String) next.getKey());
            if (!PeopleTileKey.isValid(fromString)) {
                Log.e("PeopleBackupFollowUpJob", "Malformed peopleTileKey in follow-up file: " + ((String) next.getKey()));
            } else {
                try {
                    for (String parseInt : (Set) next.getValue()) {
                        try {
                            int parseInt2 = Integer.parseInt(parseInt);
                            PeopleSpaceUtils.removeSharedPreferencesStorageForTile(this.mContext, fromString, parseInt2, sharedPreferences.getString(String.valueOf(parseInt2), ""));
                        } catch (NumberFormatException e) {
                            Log.e("PeopleBackupFollowUpJob", "Malformed widget id in follow-up file: " + e);
                        }
                    }
                } catch (Exception e2) {
                    Log.e("PeopleBackupFollowUpJob", "Malformed widget ids in follow-up file: " + e2);
                }
            }
        }
    }
}
