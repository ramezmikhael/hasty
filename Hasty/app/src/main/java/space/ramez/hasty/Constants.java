package space.ramez.hasty;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * Created by Ramez on 11/19/2016.
 */

public class Constants {

    private static final String TAG = Constants.class.getSimpleName();

    public static int PREF_SENSITIVITY = 3;
    public static boolean PREF_VIBRATE = true;
    public static String PREF_SOUND_URI = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION).toString();

    public static void assignPrefs(Context context) {
        
        Log.d(TAG, "Entered assignPref method");
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);

        PREF_SENSITIVITY = Integer.valueOf(preferences.getString("notifications_alert_sensitivity", "3"));
        PREF_VIBRATE = preferences.getBoolean("notifications_alert_vibrate", true);
        PREF_SOUND_URI = preferences.getString("notifications_alert_sound", RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION).toString());

        Log.d(TAG, "sens: " + String.valueOf(PREF_SENSITIVITY));
        Log.d(TAG, "vibr: " + String.valueOf(PREF_VIBRATE));
        Log.d(TAG, "sund: " + PREF_SOUND_URI);
    }
}
