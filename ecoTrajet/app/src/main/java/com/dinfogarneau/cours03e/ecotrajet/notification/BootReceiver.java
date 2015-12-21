package com.dinfogarneau.cours03e.ecotrajet.notification;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.dinfogarneau.cours03e.ecotrajet.MainActivity;

/**
 * Created by Guillaume on 2015-12-14.
 */
public class BootReceiver extends BroadcastReceiver {

    /**
     * Méthode et classe permettant d'activiter l'alarm au démarrage de l'appareil.
     * @param context
     * @param intent
     */
    @Override
    public void onReceive(Context context, Intent intent) {
            // Activation de l'alarme.

        Intent alarmIntent = new Intent(context, AlarmReceiver.class);
            PendingIntent alarmPendingIntent = PendingIntent.getBroadcast(
                    context,
                    MainActivity.ID_ALARM,
                    alarmIntent,
                    PendingIntent.FLAG_CANCEL_CURRENT);

            AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

            alarmMgr.setRepeating(
                    AlarmManager.ELAPSED_REALTIME,
                    0,
                    MainActivity.INTERVAL_ALARM,
                    alarmPendingIntent);
    }
}
