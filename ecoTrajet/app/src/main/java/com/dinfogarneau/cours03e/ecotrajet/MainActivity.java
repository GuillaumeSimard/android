package com.dinfogarneau.cours03e.ecotrajet;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.dinfogarneau.cours03e.ecotrajet.DataSource.UtilisateurDataSource;
import com.dinfogarneau.cours03e.ecotrajet.data.Utilisateur;


public class MainActivity extends Activity {

    //déclaration des variables
    Utilisateur utilisateurConnecté = null;
    UtilisateurDataSource utilisateurbd;
    EditText editSurnom;
    EditText editMotPasse;
    TextView messageErreur;

    public static final String UTILISATEURCONNECTE = "Utilisateur connecté";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        utilisateurbd = new UtilisateurDataSource(this);
        editSurnom =(EditText) findViewById(R.id.txtConnexionSurnom);
        editMotPasse = (EditText) findViewById(R.id.txtConnexionMotDePasse);
        messageErreur = (TextView) findViewById(R.id.idMessageErreur);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //methode onClick lorsque l'on clique sur le bouton inscripption
    public void onClickInscription(View v)
    {
        Intent intent = new Intent(this, InscriptionActivity.class);
        this.startActivity(intent);
    }

    //methode onClick lorsque l'on clique sur le bouton inscripption
    public void onClickConnexion(View v)
    {
        utilisateurbd.open();

        if(!editSurnom.getText().toString().equals("") || !editMotPasse.getText().toString().equals("")){
            utilisateurConnecté = utilisateurbd.connexionUtil(editSurnom.getText().toString(),editMotPasse.getText().toString());
        }
        else
        {
            messageErreur.setText("les deux champs doivent être remplis" );
        }

        if(utilisateurConnecté != null) {
            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(MainActivity.this).edit();
            editor.clear();

            editor.putString("nomUtilisateur", utilisateurConnecté.getM_nomUtilisateur());
            editor.putString("MotPasse", utilisateurConnecté.getM_motDePasse());
            editor.commit();

            switch(utilisateurConnecté.getM_idTypePassager())
            {

                case 1:
                    Intent intentConducteur = new Intent(this, ConducteurActivity.class);
                    intentConducteur.putExtra(UTILISATEURCONNECTE,utilisateurConnecté);
                    this.startActivity(intentConducteur);
                    break;
                case 2:
                    Intent intentPassager = new Intent(this, HistoriqueActivity.class);
                    intentPassager.putExtra(UTILISATEURCONNECTE,utilisateurConnecté);
                    this.startActivity(intentPassager);
                    break;

            }

        }
        else{
            messageErreur.setText("Identifiant ou mot de passe incorecte" );
        }
    }
}
