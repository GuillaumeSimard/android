package com.dinfogarneau.cours03e.ecotrajet;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.dinfogarneau.cours03e.ecotrajet.DataSource.UtilisateurDataSource;
import com.dinfogarneau.cours03e.ecotrajet.data.Utilisateur;

import java.util.ArrayList;


public class InscriptionActivity extends Activity {

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

    public static final String UTILISATEURCONNECTE = "Utilisateur connecté";


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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_inscription, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.idInscMenu) {

            utilAjout = new Utilisateur();
            utilAjout.setM_nomUtilisateur(editNomUtil.getText().toString());
            utilAjout.setM_prenom(editPrenom.getText().toString());
            utilAjout.setM_nom(editNomFamille.getText().toString());
            utilAjout.setM_courriel(courriel.getText().toString());
            utilAjout.setM_noTelephone(telephone.getText().toString());
            utilAjout.setM_motDePasse(motDePasse.getText().toString());
            utilAjout.setM_idTypePassager(spinnerTypePassage.getSelectedItemPosition()+1);


            utilisateurbd.open();
            utilisateurbd.insert(utilAjout);
            Toast.makeText(this, "inscription réussie", Toast.LENGTH_SHORT).show();

            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(InscriptionActivity.this).edit();
            editor.clear();

            editor.putString("nomUtilisateur", utilAjout.getM_nomUtilisateur());
            editor.putString("MotPasse", utilAjout.getM_motDePasse());
            editor.commit();

            if(utilAjout.getM_idTypePassager() == 1)
            {
                Intent i  = new Intent(this, ConducteurActivity.class);
                i.putExtra(UTILISATEURCONNECTE, utilAjout);
                startActivity(i);
            }
            else
            {
                Intent i  = new Intent(this, HistoriqueActivity.class);
                i.putExtra(UTILISATEURCONNECTE, utilAjout);
                startActivity(i);
            }


            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}