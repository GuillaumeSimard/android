package com.dinfogarneau.cours03e.ecotrajet.notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Guillaume on 2015-12-14.
 */

public class AlarmReceiver extends BroadcastReceiver {
        public AlarmReceiver() {
        }

    /**
     * Méthode exécutée lors de la connexion permettant d'appeler la classe alarmServices.
     * @param context
     * @param intent
     */
        @Override
        public void onReceive(Context context, Intent intent) {
            Intent intentService = new Intent(context, AlarmService.class);
            context.startService(intentService);
        }
}
