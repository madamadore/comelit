package it.matteoavanzini.comelit.services;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by emme on 22/05/2018.
 */

public class AlarmReceiver extends BroadcastReceiver {

    private final static String TAG = AlarmReceiver.class.getName();
    private final int ONE_MINUTE = 1000 * 60;
    private final int TWO_MINUTE = 1000 * 60 * 2;
    private final int FIFTY_MINUTE = 1000 * 60 * 50;
    private AlarmManager alarmMgr;
    private PendingIntent alarmIntent;

    @Override
    public void onReceive(Context context, Intent intent) {
        setAlarm(context);
    }

    private void setAlarm(Context context) {
        alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        //Intent intent = new Intent("it.comelit.receiver.START_SERVICE");
        //alarmIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent intentService = new Intent(context, PostDownloadService.class);
        intentService.setAction(PostDownloadService.ACTION_DOWNLOAD_POST);
        alarmIntent = PendingIntent.getService(context, 0, intentService, PendingIntent.FLAG_UPDATE_CURRENT);
        Log.d(TAG, "Set inexact repeting alarm");
        alarmMgr.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                ONE_MINUTE, TWO_MINUTE, alarmIntent);
    }
}
