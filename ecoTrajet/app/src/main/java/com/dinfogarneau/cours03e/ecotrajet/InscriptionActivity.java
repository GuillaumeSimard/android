package com.dinfogarneau.cours03e.ecotrajet;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.dinfogarneau.cours03e.ecotrajet.DataSource.UtilisateurDataSource;
import com.dinfogarneau.cours03e.ecotrajet.data.Utilisateur;
import com.dinfogarneau.cours03e.ecotrajet.unit.JSonParser;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.net.URI;
import java.util.ArrayList;


public class InscriptionActivity extends Activity {

    //déclaration des variable utilisé pour le services web.
    public static final String UTILISATEURCONNECTE = "Utilisateur connecté";
    private final static String WEB_SERVICE_URL = "ecotrajet-1065.appspot.com";
    private final static String REST_UTILISATEURS = "/utilisateurs";

    //déclaration des autres variable.
    Spinner spinnerTypePassage;
    UtilisateurDataSource utilisateurbd;
    Utilisateur utilAjout = null;
    ArrayList<String> lstType;
    EditText editNomUtil;
    EditText editPrenom;
    EditText editNomFamille;
    EditText courriel;
    EditText telephone;
    EditText motDePasse;
    ConnectivityManager connManager;
    NetworkInfo mWifi;
    NetworkInfo m3G;
    private Dialog dialogConfirmation;
    AlertDialog erreur;
    private final String TAG = this.getClass().getSimpleName();
    private HttpClient m_ClientHttp = new DefaultHttpClient();
    AlertDialog dialogAide;


    /**
     * Méthode utilisée afin de créer l'activité.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inscription);

        utilisateurbd = new UtilisateurDataSource(this);
        lstType = new ArrayList<String>();


        //initialisation des variables
        editNomUtil = (EditText) findViewById(R.id.idIdentifiantInsc);
        editPrenom = (EditText) findViewById(R.id.idPrenomInscInsc);
        editNomFamille = (EditText) findViewById(R.id.idNomInsc);
        courriel = (EditText) findViewById(R.id.idEmailInsc);
        telephone = (EditText) findViewById(R.id.idTelephoneInsc);
        motDePasse = (EditText) findViewById(R.id.idPswInsc);

        utilisateurbd.open();
        lstType = utilisateurbd.getAllType();

        connManager = (ConnectivityManager) getSystemService(this.CONNECTIVITY_SERVICE);
        mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        m3G = connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);



        //remplissage du spinner avec les éléments de la bd.
        spinnerTypePassage = (Spinner) findViewById(R.id.spinner_typeUtilisateur_id);
        ArrayAdapter<CharSequence> adapter = new ArrayAdapter <CharSequence> (this, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        for(int i = 0; i <= lstType.size() -1; i++)
        {
            adapter.add(lstType.get(i));
        }
        spinnerTypePassage.setAdapter(adapter);
        utilisateurbd.close();
    }

    /**
     * Méthode permettant la création du menu d'activité.
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_inscription, menu);
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

        switch(id)
        {
            case R.id.idInscMenu:

                //vérification que les champs soit remplie.
                if(editNomUtil.getText().toString().equals("") || editPrenom.getText().toString().equals("") ||
                        editNomFamille.getText().toString().equals("") || courriel.getText().toString().equals("") ||
                        telephone.getText().toString().equals("") || motDePasse.getText().toString().equals(""))
                {
                    afficherMessageErreur();
                }
                else
                {
                    utilAjout = new Utilisateur();
                    utilAjout.setM_nomUtilisateur(editNomUtil.getText().toString());
                    utilAjout.setM_prenom(editPrenom.getText().toString());
                    utilAjout.setM_nom(editNomFamille.getText().toString());
                    utilAjout.setM_courriel(courriel.getText().toString());
                    utilAjout.setM_noTelephone(telephone.getText().toString());
                    utilAjout.setM_motDePasse(motDePasse.getText().toString());
                    utilAjout.setM_idTypePassager(spinnerTypePassage.getSelectedItemPosition() + 1);


                    if((mWifi != null && mWifi.isConnected()) || (m3G != null && m3G.isConnected())) {
                        new PutNewUtilisateursTask().execute((Void) null);
                        Toast.makeText(this, "inscription réussie", Toast.LENGTH_SHORT).show();

                        if (utilAjout.getM_idTypePassager() == 1) {
                            Intent i = new Intent(this, ConducteurActivity.class);
                            i.putExtra(UTILISATEURCONNECTE, utilAjout);
                            startActivity(i);
                        } else {
                            Intent i = new Intent(this, HistoriqueActivity.class);
                            i.putExtra(UTILISATEURCONNECTE, utilAjout);
                            startActivity(i);
                        }
                    }
                    else
                    {
                        afficherMessage();
                    }
                }


                break;

            case R.id.aide_Inscription:
                if(dialogAide== null) {
                    dialogAide = new AlertDialog.Builder(this)
                            .setTitle(R.string.AideInscriptionMenu)
                            .setMessage(R.string.InscriptionAide)
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

                break;
        }

        return super.onOptionsItemSelected(item);
    }

    /**********************************************************************************************
     * Gestion des cycles
     *********************************************************************************************/

    @Override
    protected void onStart() {

        this.utilisateurbd.open();
        super.onStart();
    }

    @Override
    protected void onStop() {
        this.utilisateurbd.close();
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

    /**********************************************************************************************
     * classe permettant la connexion et l'ajout d'un nouvel utilisateur sur le services web
     **********************************************************************************************/
    private class PutNewUtilisateursTask extends AsyncTask<Void, Void, Void> {
        Exception m_Exp;

        @Override
        protected void onPreExecute() {
            setProgressBarIndeterminateVisibility(true);
        }

        @Override
        protected Void doInBackground(Void ... unused) {
            try {

                URI uri = new URI("http", WEB_SERVICE_URL, REST_UTILISATEURS + "/"+ utilAjout.getM_nomUtilisateur(), null, null);
                HttpPut requetePut = new HttpPut(uri);

                JSONObject obj = JSonParser.serialiserJsonUtilisateur(utilAjout);
                requetePut.setEntity(new StringEntity(obj.toString()));
                requetePut.addHeader("Content-Type", "application/json");

                m_ClientHttp.execute(requetePut, new BasicResponseHandler());
                Log.i(TAG, "Put terminé");

            } catch (Exception e) {
                m_Exp = e;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            setProgressBarIndeterminateVisibility(false);

            if (m_Exp == null) {
                // Rechargement de la liste des personnes.
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(InscriptionActivity.this).edit();
                editor.clear();
                // Pour ajouter ou supprimer des préférences en code.
                editor.putString("surnom", utilAjout.getM_nomUtilisateur());
                editor.putString("motPasse", utilAjout.getM_motDePasse());
                editor.putString("adresseCourriel", utilAjout.getM_courriel());

                //ajout de l'utilisateur à la base de données
                Utilisateur utilAdd = new Utilisateur(utilAjout.getM_nomUtilisateur(),utilAjout.getM_prenom(),
                        utilAjout.getM_nom(), utilAjout.getM_noTelephone(), utilAjout.getM_courriel(),
                        utilAjout.getM_motDePasse(),utilAjout.getM_idTypePassager());

                utilisateurbd.insert(utilAdd);

            } else {
                Log.e(TAG, "erreur de commmunication avec le services web ", m_Exp);
                Toast.makeText(InscriptionActivity.this, getString(R.string.comm_error), Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * méthode permmettant d'afficher un popop
     */
    private void afficherMessageErreur() {

        if(erreur== null) {
            erreur = new AlertDialog.Builder(this)
                    .setTitle(R.string.TitreErreur)
                    .setMessage(R.string.TextErreurConnexion)
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {

                        //méthode permettant la suppression
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    })
                    .create();
            erreur.show();
        }
        else{
            erreur.show();
        }
    }

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
}