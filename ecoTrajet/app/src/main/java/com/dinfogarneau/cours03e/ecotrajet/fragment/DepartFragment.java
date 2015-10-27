package com.dinfogarneau.cours03e.ecotrajet.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.dinfogarneau.cours03e.ecotrajet.ConducteurActivity;
import com.dinfogarneau.cours03e.ecotrajet.DataSource.ParcourPassagerDataSource;
import com.dinfogarneau.cours03e.ecotrajet.DataSource.ParcoursDataSource;
import com.dinfogarneau.cours03e.ecotrajet.DescriptionParcoursActivity;
import com.dinfogarneau.cours03e.ecotrajet.R;
import com.dinfogarneau.cours03e.ecotrajet.data.Parcours;
import com.dinfogarneau.cours03e.ecotrajet.data.ParcoursUtil;
import com.dinfogarneau.cours03e.ecotrajet.map_activity;
import com.dinfogarneau.cours03e.ecotrajet.modifPracourActivity;

import java.util.ArrayList;

import static android.R.layout.simple_list_item_1;

/**
 * Created by Guillaume on 2015-09-11.
 */
public class DepartFragment extends ListFragment {

    //déclaration des variables
    private ArrayList<Parcours> lstDepart;
    private ArrayList<RowDepart> m_RowModels;
    private int[] color_arr={Color.BLUE,Color.CYAN,Color.DKGRAY,Color.GREEN,Color.RED,Color.BLACK};
    private  TextView txtNbPassager;
    private  TextView txtNom;
    private ParcoursDataSource parcoursDataSource;
    private ParcourPassagerDataSource parcoursPassager;


    TextView nom;
    public static final String PARCOUR_CLICK = "Parcours_click";
    public static final String UTILISATEUR = "UTILISATEUR";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.depart_fragment, container, false);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);
        nom = (TextView)getActivity().findViewById(R.id.lblNomUtil);
        this.parcoursDataSource = new ParcoursDataSource(getActivity());
        this.parcoursPassager = new ParcourPassagerDataSource(getActivity());
        this.parcoursDataSource.open();

        lstDepart = parcoursDataSource.getAllParcoursConduc(ConducteurActivity.utilisateurRecup.getM_nomUtilisateur());
        this.m_RowModels = new ArrayList<RowDepart>();
        for (Parcours token : lstDepart) {
            m_RowModels.add(new RowDepart(token.getM_nomParcour()));
        }
        this.setListAdapter(new Adapteur());
        this.registerForContextMenu(this.getListView());

        nom.setText(ConducteurActivity.utilisateurRecup.getM_prenom() + " " + ConducteurActivity.utilisateurRecup.getM_nom());
    }

    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        MenuInflater mInflater = getActivity().getMenuInflater();
        mInflater.inflate(R.menu.menu_context_depart_fragment, menu);
    }

    //événement appeller lorsque l'on appui sur une option du
    //menu contextuelle
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
                parcoursDataSource.open();
                parcoursPassager.open();
                parcoursDataSource.delete(menuInfo.position + 1);
                 parcoursPassager.delete(menuInfo.position + 1, ConducteurActivity.utilisateurRecup.getM_nomUtilisateur());

                lstDepart = parcoursDataSource.getAllParcoursConduc(ConducteurActivity.utilisateurRecup.getM_nomUtilisateur());
                this.m_RowModels = null;
                this.m_RowModels = new ArrayList<RowDepart>();
                for (Parcours token : lstDepart) {
                    m_RowModels.add(new RowDepart(token.getM_nomParcour()));
                }
                this.setListAdapter(new Adapteur());

                parcoursDataSource.close();
                parcoursPassager.close();
               return true;
            default:
                Log.w("MainActivity", "Menu inconnu : " + item.getTitle());
        }
        return super.onContextItemSelected(item);
    }

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

        public RowDepart(String content) {
            this.m_Content = content;
        }

        public String getContent() {
            return m_Content;
        }

        public void setContent(String content) {
            this.m_Content = content;
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

            txtNbPassager =(TextView)row.findViewById(R.id.idRow_depart_nb_place);
            txtNom = (TextView)row.findViewById(R.id.lbl_content);

            for(int i = 0; i <= lstDepart.size() -1; i++)
            {
               if(lstDepart.get(i).getM_nbPlacePrise() == 0){
                   txtNbPassager.setTextColor(color_arr[5]);
               }
                else if(lstDepart.get(i).getM_nbPlacePrise() < lstDepart.get(i).getM_nbPlaceDisponible()){
                   txtNom.setTextColor(color_arr[2]);
               }
                else{
                   txtNom.setTextColor(color_arr[4]);
               }

                txtNbPassager.setText(lstDepart.get(i).getM_nbPlacePrise() + " / " + lstDepart.get(i).getM_nbPlaceDisponible() );
            }
            return row;
        }
    }
}




