package com.dinfogarneau.cours03e.ecotrajet.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.dinfogarneau.cours03e.ecotrajet.ConducteurActivity;
import com.dinfogarneau.cours03e.ecotrajet.DataSource.ParcoursDataSource;
import com.dinfogarneau.cours03e.ecotrajet.DescriptionParcoursActivity;
import com.dinfogarneau.cours03e.ecotrajet.R;
import com.dinfogarneau.cours03e.ecotrajet.data.Parcours;
import com.dinfogarneau.cours03e.ecotrajet.modifPracourActivity;
import com.dinfogarneau.cours03e.ecotrajet.unit.JSonParser;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

import java.net.URI;
import java.util.ArrayList;

/**
 * Created by Guillaume on 2015-09-11.
 */
public class DepartFragment extends ListFragment {

    //déclaration des variable util lors de la communication avec le service web
    public static final String PARCOUR_CLICK = "Parcours_click";
    public static final String UTILISATEUR = "UTILISATEUR";
    private final static String WEB_SERVICE_URL = "ecotrajet-1065.appspot.com";
    private final static String REST_UTILISATEURS = "/utilisateurs";
    private final static String REST_PARCOURS = "/Parcours";
    private final static String REST_PARCOURS_D = "/parcours";


    //déclaration des variables
    private ArrayList<Parcours> lstDepart = new ArrayList<Parcours>();
    private ArrayList<RowDepart> m_RowModels  = new ArrayList<RowDepart>();
    private int[] color_arr={Color.BLUE,Color.CYAN,Color.DKGRAY,Color.GREEN,Color.RED,Color.BLACK};
    private ParcoursDataSource parcoursDataSource;
    private Parcours parcoursDelete;
    TextView nom;
    private final String TAG = this.getClass().getSimpleName();
    private HttpClient m_ClientHttp = new DefaultHttpClient();


    /**
     * Méthode appelée lors de la création du fragment.
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.depart_fragment, container, false);
        return rootView;
    }

    /**
     * Méthode appelée lors de la création du fragment.
     * @param savedInstanceState
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);
        new GetUserParcours().execute((Void) null);
        nom = (TextView)getActivity().findViewById(R.id.lblNomUtil);
        this.registerForContextMenu(this.getListView());

        nom.setText(ConducteurActivity.utilisateurRecup.getM_prenom() + " " + ConducteurActivity.utilisateurRecup.getM_nom());
    }

    /**
     * Méthode permettant la création du menu contextuelle.
     * @param menu
     * @param v
     * @param menuInfo
     */
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        MenuInflater mInflater = getActivity().getMenuInflater();
        mInflater.inflate(R.menu.menu_context_depart_fragment, menu);
    }


    /**
     * Méthode Permettant de mettre à jours l'affichage de chacun des élément de la liste.
     * @param row
     * @param model
     */
    private static void refreshRow(View row, RowDepart model) {

        TextView  txtNbPassager =(TextView)row.findViewById(R.id.idRow_depart_nb_place);
        TextView txtNom = (TextView)row.findViewById(R.id.lbl_content);

        String[] passager = new String[2];
        passager = model.getPassager().split("/");
        String prise = passager[0];
        String complet = passager[1];

        if(Integer.parseInt(prise) == 0)
        {
            txtNbPassager.setTextColor(Color.BLACK);
        }
        else
        {
            if(Integer.parseInt(prise) < Integer.parseInt(complet))
            {
                txtNom.setTextColor(Color.BLUE);
            }
            else
            {
                txtNom.setTextColor(Color.RED);
            }
        }
        txtNbPassager.setText(model.getPassager());
    }

    /**
     *événement appeller lorsque l'on appui sur une option du
     *menu contextuelle
     * @param item
     * @return
     */
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo menuInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        switch (item.getItemId()) {

            case R.id.idModifDepart:
                Intent i = new Intent(getActivity(), modifPracourActivity.class);
                i.putExtra(PARCOUR_CLICK, lstDepart.get(menuInfo.position));
                i.putExtra(UTILISATEUR,ConducteurActivity.utilisateurRecup );
                startActivity(i);
                return true;

            case R.id.idDeleteDeparts:
                parcoursDataSource.delete(lstDepart.get(menuInfo.position).getM_idParcour());

                parcoursDelete = lstDepart.get((menuInfo.position));
                new DeleteParcoursListTask().execute((Void) null);
               return true;
            default:
                Log.w("MainActivity", "Menu inconnu : " + item.getTitle());
        }
        return super.onContextItemSelected(item);
    }

    /**
     * Méthode Appeler lorsque l'utilisateur click sur un évènement de la liste.
     * @param l
     * @param v
     * @param position
     * @param id
     */
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);


        Intent i = new Intent(getActivity(), DescriptionParcoursActivity.class);
        i.putExtra(UTILISATEUR, ConducteurActivity.utilisateurRecup);
        i.putExtra(PARCOUR_CLICK, lstDepart.get(position));
        this.startActivity(i);
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
            super(getActivity(), R.layout.row_departs, R.id.lbl_content, m_RowModels);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View row = super.getView(position, convertView, parent);
            refreshRow(row, m_RowModels.get(position));
            return row;
        }
    }

    /**********************************************************************************************
     * classe permettant la connexion et la requête retournant tous les parcours d'un utilisateur connecter
     **********************************************************************************************/
    private class GetUserParcours extends AsyncTask<Void, Void, ArrayList<Parcours>>{
        Exception m_Exp;


        @Override
        protected ArrayList<Parcours> doInBackground(Void ... unused) {
            ArrayList<Parcours> lstParcours = new ArrayList<Parcours>();

            try {

                URI uri = new URI("http", WEB_SERVICE_URL, REST_UTILISATEURS + "/" +ConducteurActivity.utilisateurRecup.getM_nomUtilisateur() + REST_PARCOURS, null, null);
                HttpGet requeteGet = new HttpGet(uri);


                String body =  m_ClientHttp.execute(requeteGet, new BasicResponseHandler());
                Log.i(TAG, "Put terminé");

                lstParcours = JSonParser.deserialiserJsonListePa(body);

            } catch (Exception e) {
                m_Exp = e;
            }
            return lstParcours;
        }

        @Override
        protected void onPostExecute(ArrayList<Parcours> lstParcours) {
            SystemClock.sleep(500);
            if (m_Exp == null && lstParcours != null) {
                lstDepart.clear();
                lstDepart.addAll(lstParcours);
                setListAdapter(
                        new ArrayAdapter<Parcours>(
                                getActivity(), android.R.layout.simple_list_item_1, lstDepart));

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

    /*****************************************************************************************
     // Classe interne permettant d'effectuer la tâche asynchrone de supprimer un Parcours
     // ***************************************************************************************/
    private class DeleteParcoursListTask extends AsyncTask<Void, Void, ArrayList<String> > {
        Exception m_Exp;


        // Méthode exécutée ASYNChrone: la tâche en tant que telle.
        @Override
        protected ArrayList<String> doInBackground(Void... unused) {

            try {

                URI uri = new URI("http", WEB_SERVICE_URL,REST_PARCOURS_D + "/" + parcoursDelete.getM_idServiceWeb() , null, null);
                HttpDelete requeteDelete = new HttpDelete(uri);
                m_ClientHttp.execute(requeteDelete);
            } catch (Exception e) {
                m_Exp = e;
            }
            return null;
        }

        // Méthode exécutée SYNChrone après l'exécution de la tâche asynchrone.
        // Elle reçoit en paramètre la valeur de retour de "doInBackground".
        @Override
        protected void onPostExecute(ArrayList<String> p_Surnom) {

            if (m_Exp == null) {
                new GetUserParcours().execute((Void) null);
            } else {
                Log.e(TAG, "Erreur lors de la récupération des personnes (GET)", m_Exp);
            }
        }
    }

    /**********************************************************************************************
     * Gestion des cycles
     *********************************************************************************************/

    @Override
    public void onStart() {
        super.onStart();

        parcoursDataSource = new ParcoursDataSource(getActivity());
        //ouverure de la connexion bd
        parcoursDataSource.open();

        //requete au service web
        new GetUserParcours().execute((Void) null);

    }



    @Override
    public void onStop() {
        super.onStop();

        //fermeture de la connexion bd
        parcoursDataSource.close();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();

        //requete au service web.
        new GetUserParcours().execute((Void) null);

    }
}




