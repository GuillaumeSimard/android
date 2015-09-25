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

import com.dinfogarneau.cours03e.ecotrajet.R;
import com.dinfogarneau.cours03e.ecotrajet.map_activity;
import com.dinfogarneau.cours03e.ecotrajet.modifPracourActivity;

import java.util.ArrayList;

import static android.R.layout.simple_list_item_1;

/**
 * Created by Guillaume on 2015-09-11.
 */
public class DepartFragment extends ListFragment {

    //déclaration des variables
    private String[] lstDepart;
    private Dialog dialogConfirmation;
    private ArrayList<RowDepart> m_RowModels;
    private int[] color_arr={Color.BLUE,Color.CYAN,Color.DKGRAY,Color.GREEN,Color.RED,Color.BLACK};
    private  TextView txtNbPassager;
    private  TextView txtNom;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.depart_fragment, container, false);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);

        lstDepart = getResources().getStringArray(R.array.MesDeparts);
        this.m_RowModels = new ArrayList<RowDepart>();
        for (String token : lstDepart) {
            m_RowModels.add(new RowDepart(token));
        }
        this.setListAdapter(new Adapteur());
        this.registerForContextMenu(this.getListView());
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
                startActivity(i);
                return true;

            case R.id.idDeleteDeparts:
                afficherPopop();
               return true;
            default:
                Log.w("MainActivity", "Menu inconnu : " + item.getTitle());
        }
        return super.onContextItemSelected(item);
    }

    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        Intent i = new Intent(getActivity(), map_activity.class);
        this.startActivity(i);
    }

    //méthode permmettant d'afficher un popop
    private void afficherPopop() {

        if(dialogConfirmation== null) {
            dialogConfirmation = new AlertDialog.Builder(getActivity())
                    .setTitle(R.string.titrePopopDeleteDepart)
                    .setMessage(R.string.textePopoSupprimer)
                    .setNegativeButton(android.R.string.cancel, null)
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {

                        //méthode permettant la suppression
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

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

            //définir nombre pair ou impair
            if((position % 2) == 0){
                txtNom.setTextColor(color_arr[2]);
                txtNbPassager.setText("2/4");
            }else{
                txtNom.setTextColor(color_arr[4]);
                txtNbPassager.setText("complet");
            }
            txtNbPassager.setTextColor(color_arr[5]);


            return row;
        }
    }
}




