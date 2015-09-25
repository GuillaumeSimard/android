package com.dinfogarneau.cours03e.ecotrajet;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;


public class ChoseActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chose);
    }

    //methode onClick lorsque l'on clique sur le bouton inscripption
    public void onClickPasager(View v)
    {
        Intent intent = new Intent(this, HistoriqueActivity.class);
        this.startActivity(intent);
    }

    //methode onClick lorsque l'on clique sur le bouton inscripption
    public void onClickConducteur(View v)
    {
        Intent intent = new Intent(this, ConducteurActivity.class);
        this.startActivity(intent);
    }
}
