package com.dinfogarneau.cours03e.ecotrajet.notification;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;

import com.dinfogarneau.cours03e.ecotrajet.ConducteurActivity;
import com.dinfogarneau.cours03e.ecotrajet.R;
import com.dinfogarneau.cours03e.ecotrajet.data.Message;
import com.dinfogarneau.cours03e.ecotrajet.unit.JSonParser;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

import java.net.URI;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Guillaume on 2015-12-14.
 */
public class AlarmService  extends IntentService {

    //Attributs de la classe.
    private final static String WEB_SERVICE_URL = "ecotrajet-1065.appspot.com";
    private final static String REST_UTILISATEURS = "/utilisateurs";
    private final static String REST_DEMANDE = "/demandeParcour";
    // Clé pour l'information attachée à l'intention.
    public static final String EXTRA_INFO = "message";
    private String surnom;
    private final String TAG = this.getClass().getSimpleName();
    private HttpClient m_ClientHttp = new DefaultHttpClient();
    private NotificationManager notifMgr;

    Notification notif;
    // Identifiant unique pour la notification.
    private static final int ID_NOTIF = 12345;

    private String[] infoParcours = new String[2];

    public AlarmService() {
        super("AlarmService");
    }

    /**
     * Méthode exécutant une lourde tâche.
     * @param intent
     */
    @Override
    protected void onHandleIntent(Intent intent) {

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        surnom = preferences.getString("surnom", "");
        callWebServices();


    }


    /**********************************************************************************************
     * classe permettant la connexion et la requête retournant tous les demande d'un conducteur connecter
     **********************************************************************************************/
    private class DownloadAddListTask extends AsyncTask<Void, Void, Void> {
        Exception m_Exp;
        Message msg;
        String id;

        public DownloadAddListTask(String id)
        {
            this.id = id;
        }

        // Méthode exécutée ASYNChrone: la tâche en tant que telle.
        @Override
        protected Void doInBackground(Void... unused) {

            try {

                //création de l'uri et requete au services web.
                URI uri = new URI("http", WEB_SERVICE_URL,REST_UTILISATEURS +  "/" + surnom +  REST_DEMANDE , null, null);
                HttpGet requeteGet = new HttpGet(uri);

                String body = m_ClientHttp.execute(requeteGet, new BasicResponseHandler());
                Log.i(TAG, "Reçu (GET) : " + body);

             msg = JSonParser.parseMessage(body,surnom);

            } catch (Exception e) {
                m_Exp = e;
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            if (m_Exp != null) {
                Log.e(TAG, "Error while posting", m_Exp);
                //Toast.makeText(MainActivity.this, "rien de rien", Toast.LENGTH_SHORT).show();
            }
            else {
                if (msg!=null) {

                    //envoie du broadcast et de la notification
                    Intent messageIntent = new Intent("com.dinfogarneau.cours624.demo.gae_client.USER_ACTION");
                    messageIntent.putExtra("message", msg.toString());
                    sendBroadcast(messageIntent);

                    notifActivate();
                }
                else {
                }
            }
        }
    }

    /**
     * Méthode qui permet d'aller vérifier sur le services web à chaque 50 secondes
     * s'il y a eu une nouvelle demande pour cette personnes
     */
    private void callWebServices() {
        final Handler handler = new Handler();
        Timer timer = new Timer();
        // méthode anonyme du timer passée au handler
        TimerTask doAsynchronousTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        try {
                            DownloadAddListTask performDemande = new DownloadAddListTask(surnom);
                            performDemande.execute((Void) null);
                        } catch (Exception e) {
                        }
                    }
                });
            }
        };
        timer.schedule(doAsynchronousTask, 0, 50000);
    }

    /**
     * Méthode permettant l'envoie de la notification.
     */
    public void notifActivate()
    {
        // Texte dans "status bar", titre et texte de la notification.
        String statusBarNotif = getString(R.string.statusBarNotif);
        // Récupération du "NotificationManager".
        this.notifMgr = (NotificationManager) this.getSystemService(NOTIFICATION_SERVICE);
        // Création d'un nouvelle notification.
        notif = new Notification(R.mipmap.ic_launcher, statusBarNotif, System.currentTimeMillis());
        Notification.Builder builder = new Notification.Builder(AlarmService.this);
        // Pour faire disparaître la notification lorsque l'utilisateur la clique.
        notif.flags |= Notification.FLAG_AUTO_CANCEL;

        // Création d'une intention de retour lorsqu'on clique sur la notification.
        Intent i = new Intent(this, ConducteurActivity.class);
        // Ajout d'information dans l'intention.

        // Création d'une nouvelle intention en suspens.
        PendingIntent pi = PendingIntent.getActivity(this, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);
        // Configuration de la notification.

        builder.setAutoCancel(false);
        builder.setTicker("écoTrajet");
        builder.setContentTitle("notification reçus");
        builder.setContentText("nouvelle demande de parcours reçus");
        builder.setSmallIcon(R.mipmap.ecotrajet);
        builder.setContentIntent(pi);
        builder.setOngoing(true);
        builder.setSubText("This is subtext...");   //API level 16
        builder.setNumber(100);
        builder.build();

        notif = builder.getNotification();
        // Envoie de la notification.
        notifMgr.notify(ID_NOTIF, notif);
    }
}