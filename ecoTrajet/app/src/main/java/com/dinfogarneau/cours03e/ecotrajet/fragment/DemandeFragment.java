package com.dinfogarneau.cours03e.ecotrajet.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
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
import android.widget.TextView;

import com.dinfogarneau.cours03e.ecotrajet.ConducteurActivity;
import com.dinfogarneau.cours03e.ecotrajet.R;
import com.dinfogarneau.cours03e.ecotrajet.unit.JSonParser;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

import java.net.URI;
import java.util.ArrayList;

/**
 * Created by Guillaume on 2015-09-11.
 */
public class DemandeFragment extends ListFragment {

    //déclaration des variables
    ArrayList<String> lstDemande;
    private Dialog dialogConfirmation;
    private final static String WEB_SERVICE_URL = "ecotrajet-1065.appspot.com";
    private final static String REST_UTILISATEURS = "/utilisateurs";
    private final static String REST_DEMANDE = "/demandeParcour";
    private final static String REST_DEMANDES = "/demandeParcours";
    private final static String REST_PARCOURS = "/parcours";
    private final static String REST_PASSAGER = "/passager";
    private String[] infoParcours = new String[2];
    private final String TAG = this.getClass().getSimpleName();
    private HttpClient m_ClientHttp = new DefaultHttpClient();
    TextView nom;
    // Intervalle entre les déclenchements de l'alarme.


    /**
     * Méthode zappeler lors de la création du fragment
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.demande_fragment, container, false);
        return rootView;
    }

    /**
     * Méthode appeller lors de la création du fragment
     * @param savedInstanceState
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);
        lstDemande = new ArrayList<>();
        nom = (TextView)getActivity().findViewById(R.id.lblNomDemandes);
        new DownloadAddListTask().execute((Void) null);
        this.registerForContextMenu(this.getListView());
        nom.setText(ConducteurActivity.utilisateurRecup.getM_prenom() + " " + ConducteurActivity.utilisateurRecup.getM_nom());

    }

    /**
     * création du menu contextuelle
     * @param menu
     * @param v
     * @param menuInfo
     */
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        MenuInflater mInflater = getActivity().getMenuInflater();
        mInflater.inflate(R.menu.menu_fragment_demande, menu);
    }

    /**
     *événement appeller lorsque l'on appui sur une option du
     //menu contextuelle
     * @param item
     * @return
     */
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo menuInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        infoParcours = lstDemande.get(menuInfo.position).split(";");
        switch (item.getItemId()) {
            case R.id.idDeleteDemande:

                afficherPopop();
                return true;
            case R.id.idAcceptDemande:
                new AcceptDemandeListTask().execute((Void) null);
                return true;
            default:
                Log.w("MainActivity", "Menu inconnu : " + item.getTitle());
        }
        return super.onContextItemSelected(item);
    }

    /**********************************************************************************************
     * Gestion des cycles
     *********************************************************************************************/

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    /**
     *    méthode permmettant d'afficher un popop
     */
    private void afficherPopop() {

        if(dialogConfirmation== null) {
            dialogConfirmation = new AlertDialog.Builder(getActivity())
                    .setTitle(R.string.titrePopopDeleteDepart)
                    .setMessage(R.string.titrePopopDeleteDemande)
                    .setNegativeButton(android.R.string.cancel, null)
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {

                        //méthode permettant la suppression
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            new DeleteDemandeListTask().execute((Void) null);
                        }
                    })
                    .create();
            dialogConfirmation.setOwnerActivity(getActivity());
            dialogConfirmation.show();
        }
        else{
            dialogConfirmation.show();
        }
    }

    /**********************************************************************************************
     * classe permettant la connexion et la requête retournant tous les demande d'un conducteur connecter
     **********************************************************************************************/
    private class DownloadAddListTask extends AsyncTask<Void, Void, ArrayList<String> > {
        Exception m_Exp;

        // Méthode exécutée SYNChrone avant l'exécution de la tâche asynchrone.

        // Méthode exécutée ASYNChrone: la tâche en tant que telle.
        @Override
        protected ArrayList<String> doInBackground(Void... unused) {

            ArrayList<String> listSurnom = new ArrayList<>();
            try {
                URI uri = new URI("http", WEB_SERVICE_URL,REST_UTILISATEURS +  "/" +ConducteurActivity.utilisateurRecup.getM_nomUtilisateur() +  REST_DEMANDE , null, null);
                HttpGet requeteGet = new HttpGet(uri);

                String body = m_ClientHttp.execute(requeteGet, new BasicResponseHandler());
                Log.i(TAG, "Reçu (GET) : " + body);

                listSurnom = JSonParser.deserialiserJsonListeDemande(body);

            } catch (Exception e) {
                m_Exp = e;
            }

            return listSurnom;
        }

        // Méthode exécutée SYNChrone après l'exécution de la tâche asynchrone.
        // Elle reçoit en paramètre la valeur de retour de "doInBackground".
        @Override
        protected void onPostExecute(ArrayList<String> p_Surnom) {

            if (m_Exp == null && p_Surnom != null) {
                lstDemande.clear();
                lstDemande.addAll(p_Surnom);
                setListAdapter(
                        new ArrayAdapter<String>(
                                getActivity(), android.R.layout.simple_list_item_1, lstDemande));

            } else {
                Log.e(TAG, "Erreur lors de la récupération des personnes (GET)", m_Exp);
            }
        }
    }

    /*****************************************************************************************
    // Classe interne permettant d'effectuer la tâche asynchrone d'accepter une demande
    // ***************************************************************************************/
    private class AcceptDemandeListTask extends AsyncTask<Void, Void, ArrayList<String> > {
        Exception m_Exp;


        // Méthode exécutée ASYNChrone: la tâche en tant que telle.
        @Override
        protected ArrayList<String> doInBackground(Void... unused) {

            try {
                URI uri = new URI("http", WEB_SERVICE_URL, REST_UTILISATEURS + "/" + ConducteurActivity.utilisateurRecup.getM_nomUtilisateur() + REST_PARCOURS  + "/" + infoParcours[0] + REST_PASSAGER + "/" +  infoParcours[1].trim(), null, null);
                HttpPut requetePut = new HttpPut(uri);
                m_ClientHttp.execute(requetePut);
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
                new DeleteDemandeListTask().execute((Void) null);

            } else {
                Log.e(TAG, "Erreur lors de la récupération des personnes (GET)", m_Exp);
            }
        }
    }

    /*****************************************************************************************
     // Classe interne permettant d'effectuer la tâche asynchrone de refuser une demande
     // ***************************************************************************************/
    private class DeleteDemandeListTask extends AsyncTask<Void, Void, ArrayList<String> > {
        Exception m_Exp;


        // Méthode exécutée ASYNChrone: la tâche en tant que telle.
        @Override
        protected ArrayList<String> doInBackground(Void... unused) {

            try {

                URI uri = new URI("http", WEB_SERVICE_URL, REST_UTILISATEURS + "/" + ConducteurActivity.utilisateurRecup.getM_nomUtilisateur() + REST_PARCOURS + "/" + infoParcours[0] + REST_DEMANDES + "/" + infoParcours[1].trim(), null, null);
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

                //nouvelle requète au service web.  afin de telecharger la nouvelle liste.
                new DownloadAddListTask().execute((Void) null);

            } else {
                Log.e(TAG, "Erreur lors de la récupération des personnes (GET)", m_Exp);
            }
        }
    }
}
