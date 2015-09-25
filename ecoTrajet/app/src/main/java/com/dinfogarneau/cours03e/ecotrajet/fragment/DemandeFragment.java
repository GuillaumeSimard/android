package com.dinfogarneau.cours03e.ecotrajet.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.ListView;

import com.dinfogarneau.cours03e.ecotrajet.R;
import com.dinfogarneau.cours03e.ecotrajet.map_activity;
import com.dinfogarneau.cours03e.ecotrajet.modifPracourActivity;

import static android.R.layout.simple_list_item_1;

/**
 * Created by Guillaume on 2015-09-11.
 */
public class DemandeFragment extends ListFragment {

    //déclaration des variables
    String[] lstDepart;
    private Dialog dialogConfirmation;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.demande_fragment, container, false);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);

        lstDepart = getResources().getStringArray(R.array.MesDemandes);
        this.setListAdapter(new ArrayAdapter<String>(getActivity(), simple_list_item_1, lstDepart));
        this.registerForContextMenu(this.getListView());
    }

    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        MenuInflater mInflater = getActivity().getMenuInflater();
        mInflater.inflate(R.menu.menu_fragment_demande, menu);
    }

    //événement appeller lorsque l'on appui sur une option du
    //menu contextuelle
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo menuInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.idDeleteDemande:
                afficherPopop();
                return true;
            default:
                Log.w("MainActivity", "Menu inconnu : " + item.getTitle());
        }
        return super.onContextItemSelected(item);
    }

    //méthode permmettant d'afficher un popop
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


}
