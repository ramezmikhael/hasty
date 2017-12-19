package space.ramez.hasty;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Ramez on 11/14/2016.
 */

public class BootCompleteReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: save a variable to re-run the service if it was running before the
        // device restarts.
//        context.startService(new Intent(context, WalkingMonitor.class));
    }
}
