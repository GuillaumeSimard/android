package com.dinfogarneau.cours03e.ecotrajet;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.dinfogarneau.cours03e.ecotrajet.data.Parcours;
import com.dinfogarneau.cours03e.ecotrajet.data.Utilisateur;
import com.dinfogarneau.cours03e.ecotrajet.unit.JSonParser;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

import java.net.URI;
import java.util.ArrayList;

/**
 * Created by Remy Huot on 2015-09-18.
 */
public class ResultatRechercheActivity extends ListActivity {

    //déclaration des variables

    public static final String PARCOUR_CLICK = "Parcours_click";
    public static final String UTIL = "utilisateur";

    private final static String WEB_SERVICE_URL = "ecotrajet-1065.appspot.com";
    private final static String REST_PARCOURS = "/parcours";
    private ArrayList<Parcours> lstDepart = new ArrayList<Parcours>();
    private ArrayList<RowDepart> m_RowModels  = new ArrayList<RowDepart>();
    AlertDialog dialogAide;

    private final String TAG = this.getClass().getSimpleName();
    private HttpClient m_ClientHttp = new DefaultHttpClient();
    private String DateRecus;
    private String pointDebut;
    private String pointArriver;
    private Utilisateur util;
    String date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resultatrecherche);

        Intent intentrecu = this.getIntent();
        Bundle extra = intentrecu.getExtras();

        DateRecus = String.valueOf(extra.getSerializable(RechercheActivity.DATEVOULU));
        pointDebut = String.valueOf(extra.getSerializable(RechercheActivity.POINTDEPART));
        pointArriver = String.valueOf(extra.getSerializable(RechercheActivity.POINTARRIVER));
        util = (Utilisateur) extra.getSerializable(RechercheActivity.UTILISATEURCONNECTE);
        new ParcoursListTask().execute((Void) null);
        getActionBar().setHomeButtonEnabled(true);
        getActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_result_recherche, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent i = new Intent(this, RechercheActivity.class);
                i.putExtra(UTIL, this.util);
                startActivity(i);
                break;
            case R.id.idAideResultat:

                if(dialogAide== null) {
                    dialogAide = new AlertDialog.Builder(this)
                            .setTitle(R.string.AideResultat)
                            .setMessage(R.string.AideResultatRecherche)
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
            case R.id.idDeconnetion:
                deconnection();
                Intent iD = new Intent(this, MainActivity.class);
                this.startActivity(iD);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        Intent intent = new Intent(this, DescriptionParcoursActivity.class);
        intent.putExtra(UTIL, this.util);
        intent.putExtra(PARCOUR_CLICK, lstDepart.get((position)));
        this.startActivity(intent);
    }

    /**********************************************************************************************
     * Gestion des cycles
     *********************************************************************************************/

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
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

    /***********************************************************************************************
     *Sous classe permettant de gerer les different layout de la list.
     **********************************************************************************************/
    public static class RowDepart {
        private String m_Content;
        private String m_Passage;

        public RowDepart(String content) {
            this.m_Content = content;
        }


        public String getContent() {
            return m_Content;
        }


        public void setContent(String content) {
            this.m_Content = content;
        }

        public String getPassager() {
            return m_Passage;
        }


        public void setPasager(String passager) {
            this.m_Passage = passager;
        }

        @Override
        public String toString() {
            return this.m_Content;
        }
    }

    private static void refreshRow(View row, RowDepart model) {

        TextView txtNom = (TextView)row.findViewById(R.id.lbl_content);
        txtNom.setTextColor(Color.BLACK);
    }

    /***********************************************************************************************
     *Sous classe permettant de gerer les different layout de la list.
     **********************************************************************************************/
    private class Adapteur extends ArrayAdapter<RowDepart> {
        public Adapteur() {
            super(getApplicationContext(), R.layout.row_departs, R.id.lbl_content, m_RowModels);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View row = super.getView(position, convertView, parent);
            refreshRow(row, m_RowModels.get(position));
            return row;
        }
    }


    /*****************************************************************************************
     // Classe interne permettant d'effectuer la tâche asynchrone de supprimer un Parcours
     // ***************************************************************************************/
    private class ParcoursListTask extends AsyncTask<Void, Void, ArrayList<Parcours> > {
        Exception m_Exp;


        @Override
        protected ArrayList<Parcours> doInBackground(Void... unused) {
            ArrayList<Parcours> lstParcours = new ArrayList<Parcours>();

            try {
                URI uri = new URI("http", WEB_SERVICE_URL, REST_PARCOURS, "dateParcour=" +  DateRecus + "&coordoDepart=" + pointDebut + "&coordoArrive="+ pointArriver, null );
                HttpGet requeteGet = new HttpGet(uri);


                String body = m_ClientHttp.execute(requeteGet, new BasicResponseHandler());
                Log.i(TAG, "Put terminé");

                lstParcours = JSonParser.deserialiserJsonListePat(body);

            } catch (Exception e) {
                m_Exp = e;
            }
            return lstParcours;
        }

        @Override
        protected void onPostExecute(ArrayList<Parcours> lstParcours) {

            if (m_Exp == null && lstParcours != null) {
                lstDepart.clear();
                lstDepart.addAll(lstParcours);
                setListAdapter(
                        new ArrayAdapter<Parcours>(
                                getApplicationContext(), android.R.layout.simple_list_item_1, lstDepart));

                m_RowModels = new ArrayList<RowDepart>();
                String passager;
                for(Parcours nomParcours : lstDepart)
                {
                    passager = nomParcours.getM_nbPlacePrise() + "/" + nomParcours.getM_nbPlaceDisponible();
                    RowDepart row = new RowDepart(nomParcours.getM_nomParcour());
                    row.setPasager( passager);
                    m_RowModels.add(row);

                }
                setListAdapter(new Adapteur());

            } else {
                Log.e(TAG, "Erreur lors de la récupération des personnes (GET)", m_Exp);
            }
        }
    }

    /**
     * Méthode permettant de gerer la déconnexion d'un utilisateur.
     */
    public void deconnection(){

        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
        editor.putString("nomUtilisateur","");
        editor.putString("motPasse", "");
        editor.commit();
    }
}