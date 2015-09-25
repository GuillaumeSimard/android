package com.dinfogarneau.cours03e.ecotrajet;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import static android.R.layout.simple_list_item_1;

/**
 * Created by Remy Huot on 2015-09-18.
 */
public class ResultatRechercheActivity extends ListActivity {

    //déclaration des variables
    String[] lstResultat;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resultatrecherche);
        lstResultat = getResources().getStringArray(R.array.Resultat);
        this.setListAdapter(new ArrayAdapter<String>(this, simple_list_item_1, lstResultat));
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        Intent intent = new Intent(this, DescriptionParcoursActivity.class);
        this.startActivity(intent);
    }
}