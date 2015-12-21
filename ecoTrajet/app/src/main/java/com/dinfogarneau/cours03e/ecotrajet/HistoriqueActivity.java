package com.dinfogarneau.cours03e.ecotrajet;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
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
public class HistoriqueActivity extends ListActivity {

    //déclaration desvariable util afin de communiquer avec le service web et au transfert de donnée.
    private final static String WEB_SERVICE_URL = "ecotrajet-1065.appspot.com";
    private final static String REST_UTILISATEURS = "/utilisateurs";
    private final static String REST_PARCOURS = "/Parcours";
    public static final String UTILISATEURCONNECTE = "Utilisateur connecté";
    public static final String PARCOURS_CLICK = "parcours";

    //déclaration des variables
    private String[] lstDepartPassager;
    private Utilisateur utilisateurRecup;
    private ArrayList<Parcours> lstDepart = new ArrayList<Parcours>();
    private ArrayList<RowDepart> m_RowModels  = new ArrayList<RowDepart>();
    private final String TAG = this.getClass().getSimpleName();
    private HttpClient m_ClientHttp = new DefaultHttpClient();
    AlertDialog dialogAide;
    TextView nom;

    /**
     * Méthode utiliser lors de la création de l'activité.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historique);

        nom = (TextView) this.findViewById(R.id.lblNomUtilP);

        Intent intentrecu = this.getIntent();
        Bundle extra = intentrecu.getExtras();
        this.utilisateurRecup = (Utilisateur) extra.getSerializable(RechercheActivity.UTILISATEURCONNECTE);
        if (this.utilisateurRecup == null)
        {
            this.utilisateurRecup = (Utilisateur) extra.getSerializable(InscriptionActivity.UTILISATEURCONNECTE);

            if(this.utilisateurRecup == null)
            {
                this.utilisateurRecup = (Utilisateur) extra.getSerializable(DescriptionParcoursActivity.UTILISATEURPASSE);
            }
        }

        nom.setText(this.utilisateurRecup.getM_prenom() + " " + this.utilisateurRecup.getM_nom());

        new ParcoursListTask().execute((Void) null);

    }

    /**
     * Méthode permettant la création du menu d'activité.
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_historique, menu);
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

        switch (id)
        {
            case R.id.idInscHistorique:
                Intent intent = new Intent(this, RechercheActivity.class);
                intent.putExtra(UTILISATEURCONNECTE, this.utilisateurRecup);
                this.startActivity(intent);
                break;

            case R.id.idDeconnetion:
                deconnection();
                Intent iD = new Intent(this, MainActivity.class);
                this.startActivity(iD);
                break;

            case R.id.aide_Historique:

                if(dialogAide== null) {
                    dialogAide = new AlertDialog.Builder(this)
                            .setTitle(R.string.AideHistoriqueMenu)
                            .setMessage(R.string.HistoriqueParcoursAide)
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

    /**
     * Méthode utiliser lorsque l'utilisateur appuit sur un élément de la liste.
     * @param l
     * @param v
     * @param position
     * @param id
     */
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        Intent i = new Intent(this, DescriptionParcoursActivity.class);
        i.putExtra(PARCOURS_CLICK, lstDepart.get((position)));
        i.putExtra(UTILISATEURCONNECTE, this.utilisateurRecup);
        this.startActivity(i);
    }

    /**
     * Méthode pertmettant de mettre à jours l'affichage de la liste.
     * @param row
     * @param model
     */
    private static void refreshRow(View row, RowDepart model) {

        TextView txtNom = (TextView)row.findViewById(R.id.lbl_content);
        txtNom.setTextColor(Color.BLACK);
    }

    /**********************************************************************************************
     * Gestion des cycles
     *********************************************************************************************/

    @Override
    protected void onStart() {
        super.onStart();

        //requète au services web
        new ParcoursListTask().execute((Void) null);
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

        //requète au services web
        new ParcoursListTask().execute((Void) null);
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

                SystemClock.sleep(500);
                URI uri = new URI("http", WEB_SERVICE_URL, REST_UTILISATEURS + "/" + utilisateurRecup.getM_nomUtilisateur() + REST_PARCOURS, null, null);
                HttpGet requeteGet = new HttpGet(uri);



                String body = m_ClientHttp.execute(requeteGet, new BasicResponseHandler());
                Log.i(TAG, "Put terminé");

                lstParcours = JSonParser.deserialiserJsonListeParcours(body);

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

    public void deconnection(){

        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
        editor.putString("nomUtilisateur","");
        editor.putString("motPasse", "");
        editor.commit();
    }

}
