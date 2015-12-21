package com.dinfogarneau.cours03e.ecotrajet;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.dinfogarneau.cours03e.ecotrajet.DataSource.UtilisateurDataSource;
import com.dinfogarneau.cours03e.ecotrajet.data.Utilisateur;
import com.dinfogarneau.cours03e.ecotrajet.notification.AlarmReceiver;
import com.dinfogarneau.cours03e.ecotrajet.notification.BootReceiver;
import com.dinfogarneau.cours03e.ecotrajet.unit.JSonParser;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.net.URI;


public class MainActivity extends Activity {

    //déclaration des variables utiles au service web.
    private final static String WEB_SERVICE_URL = "ecotrajet-1065.appspot.com";
    private final static String REST_CONNEXION = "/connexion";

    // À toutes les 7 secondes (dans un but de démonstration uniquement).
    public static int INTERVAL_ALARM = 7000;

    // Identifiant pour l'intention en suspens de l'alarme.
    public static final int ID_ALARM = 12345;

    //déclaration des variables
    Utilisateur utilisateurConnecté = null;
    Utilisateur utilRecupSw = null;
    AlertDialog dialogAide;

    Utilisateur util = null;
    UtilisateurDataSource utilisateurbd;
    EditText editSurnom;
    EditText editMotPasse;
    // Le gestionnaire d'alarme d'Android.
    private AlarmManager alarmMgr;
    // L'intention lorsque l'alarme se déclenche.
    private Intent alarmIntent;
    TextView messageErreur;
    public static final String UTILISATEURCONNECTE = "Utilisateur connecté";
    private final String TAG = this.getClass().getSimpleName();

    private HttpClient m_ClientHttp = new DefaultHttpClient();

    ConnectivityManager connManager;
    NetworkInfo mWifi;
    NetworkInfo m3G;
    private Dialog dialogConfirmation;

    /**
     * Méthode utilisée afin de créer l'activité.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        utilisateurbd = new UtilisateurDataSource(this);
        editSurnom =(EditText) findViewById(R.id.txtConnexionSurnom);
        editMotPasse = (EditText) findViewById(R.id.txtConnexionMotDePasse);
        messageErreur = (TextView) findViewById(R.id.idMessageErreur);

        connManager = (ConnectivityManager) getSystemService(this.CONNECTIVITY_SERVICE);
        mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        m3G = connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        // Création de l'intention pour le déclenchement de l'alarme.
        this.alarmIntent = new Intent(this, AlarmReceiver.class);
        // Récupération du gestionnaire d'alarme.
        this.alarmMgr = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);

        // Création d'une nouvelle intention en suspens.
        PendingIntent alarmPendingIntent = PendingIntent.getBroadcast(this, ID_ALARM, this.alarmIntent,
                PendingIntent.FLAG_CANCEL_CURRENT);

        // Activation de l'alarme : donc l'intention en suspens est envoyée à l'alarm manager
        // Privilégiez l'utilisation de la méthode "setInexactRepeating" au lieu de "setRepeating".
        this.alarmMgr.setRepeating(AlarmManager.ELAPSED_REALTIME, 0, INTERVAL_ALARM, alarmPendingIntent);

        ComponentName receiver = new ComponentName(this, BootReceiver.class);
        PackageManager pm = this.getPackageManager();
        pm.setComponentEnabledSetting(
                receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);
    }

    /**
     * Méthode permettant la création du menu d'activité.
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    /**
     * Méthode permettant de gérer l'option choisi du menu.
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.aide_Connexion)
        {
            if(dialogAide== null) {
                dialogAide = new AlertDialog.Builder(this)
                        .setTitle(R.string.AideConnexion)
                        .setMessage(R.string.AideConnexionDetails)
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {

                            //méthode permettant la suppression
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .create();
                dialogAide.setOwnerActivity(this);
                dialogAide.show();
            }
            else{
                dialogAide.show();
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * methode onClick lorsque l'on clique sur le bouton inscripption
     * @param v
     */
    public void onClickInscription(View v)
    {
        Intent intent = new Intent(this, InscriptionActivity.class);
        this.startActivity(intent);
    }

    //methode onClick lorsque l'on clique sur le bouton inscripption
    public void onClickConnexion(View v) {

        if (!editSurnom.getText().toString().equals("") || !editMotPasse.getText().toString().equals("")) {

            util = new Utilisateur(editSurnom.getText().toString(),editMotPasse.getText().toString());

            if((mWifi != null && mWifi.isConnected()) || (m3G != null && m3G.isConnected())) {
                new CreateConnexionTask().execute((Void) null);
            }
            else{
                afficherMessage();
            }

        } else {
            messageErreur.setText("Tout les champs doivent être remplis");
        }

    }

    /**********************************************************************************************
     * Classe permettant la communication au service web afin de pouvoir se connecter.
     **********************************************************************************************/
        private class CreateConnexionTask extends AsyncTask<Void, Void, Void> {
            Exception m_Exp;

            // Méthode exécutée SYNChrone avant l'exécution de la tâche asynchrone.
            @Override
            protected void onPreExecute() {
                setProgressBarIndeterminateVisibility(true);
            }

            // Méthode exécutée ASYNChrone: la tâche en tant que telle.
            @Override
            protected Void doInBackground(Void... unused) {

                try {
                    URI uri = new URI("http", WEB_SERVICE_URL, REST_CONNEXION, null, null);
                    HttpPost requetePost = new HttpPost(uri);


                    JSONObject obj = JSonParser.serialiserJsonUtilisateur(util);
                    requetePost.setEntity(new StringEntity(obj.toString()));
                    requetePost.addHeader("Content-Type", "application/json");

                    String body = m_ClientHttp.execute(requetePost, new BasicResponseHandler());
                    utilisateurConnecté = JSonParser.deserialiserJsonUtilisateur(body);
                    Log.i(TAG, "ajout terminé avec succès");

                } catch (Exception e) {
                    m_Exp = e;
                }
                return null;
            }

            // Méthode exécutée SYNChrone après l'exécution de la tâche asynchrone.
            // Elle reçoit en paramètre la valeur de retour de "doInBackground".
            @Override
            protected void onPostExecute(Void unused) {
                setProgressBarIndeterminateVisibility(false);

                if (m_Exp == null) {
                    // Rechargement de la liste des personnes.
                    SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(MainActivity.this).edit();
                    editor.clear();
                    // Pour ajouter ou supprimer des préférences en code.
                    editor.putString("surnom", utilisateurConnecté.getM_nomUtilisateur());
                    editor.putString("motPasse", utilisateurConnecté.getM_motDePasse());
                    // editor.remove(key);
                    editor.commit();

                    utilisateurbd.open();
                    utilRecupSw = utilisateurbd.RecupUtilisateur((utilisateurConnecté.getM_nomUtilisateur()));

                    switch (utilRecupSw.getM_idTypePassager()) {

                        case 1:
                            Intent intentConducteur = new Intent(MainActivity.this, ConducteurActivity.class);
                            intentConducteur.putExtra(UTILISATEURCONNECTE, utilRecupSw);
                            MainActivity.this.startActivity(intentConducteur);
                            break;
                        case 2:
                            Intent intentPassager = new Intent(MainActivity.this, HistoriqueActivity.class);
                            intentPassager.putExtra(UTILISATEURCONNECTE, utilRecupSw);
                            MainActivity.this.startActivity(intentPassager);
                            break;
                    }
                } else {
                    messageErreur.setText("La combinaison nom d'utilisateur et mot de passe incorrecte.");
                    Toast.makeText(MainActivity.this, getString(R.string.comm_error), Toast.LENGTH_SHORT).show();
                }
            }
        }

    /**
     * méthode permmettant d'afficher un popop
     */
    private void afficherMessage() {

        if(dialogConfirmation== null) {
            dialogConfirmation = new AlertDialog.Builder(this)
                    .setTitle(R.string.titreErreurConnection)
                    .setMessage(R.string.btextErreurConnexion)
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {

                        //méthode permettant la suppression
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    })
                    .create();
            dialogConfirmation.show();
        }
        else{
            dialogConfirmation.show();
        }
    }

    /**********************************************************************************************
     * Gestion des cycles
     *********************************************************************************************/

    @Override
    protected void onStart() {
        utilisateurbd.open();
        super.onStart();
    }

    @Override
    protected void onStop() {
        utilisateurbd.close();
        super.onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
