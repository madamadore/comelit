package it.matteoavanzini.comelit.services;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by emme on 22/05/2018.
 */

public class AlarmReceiver extends BroadcastReceiver {

    private final int A_MINUTE = 1000 * 60;
    private final int FIFTY_MINUTE = 1000 * 60 * 50;
    private AlarmManager alarmMgr;
    private PendingIntent alarmIntent;

    @Override
    public void onReceive(Context context, Intent intent) {
        setAlarm(context);
    }

    private void setAlarm(Context context) {
        alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, PostDownloadService.class);
        intent.setAction(PostDownloadService.ACTION_DOWNLOAD_POST);
        alarmIntent = PendingIntent.getService(context, 0, intent, 0);

        alarmMgr.setInexactRepeating(AlarmManager.ELAPSED_REALTIME,
                AlarmManager.INTERVAL_FIFTEEN_MINUTES, FIFTY_MINUTE, alarmIntent);
    }
}
