package com.dinfogarneau.cours03e.ecotrajet;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;


public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //methode onClick lorsque l'on clique sur le bouton inscripption
    public void onClickInscription(View v)
    {
        Intent intent = new Intent(this, InscriptionActivity.class);
        this.startActivity(intent);
    }

    //methode onClick lorsque l'on clique sur le bouton inscripption
    public void onClickConnexion(View v)
    {
        Intent intent = new Intent(this, ChoseActivity.class);
        this.startActivity(intent);
    }
}
