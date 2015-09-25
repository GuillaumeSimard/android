package com.dinfogarneau.cours03e.ecotrajet;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.dinfogarneau.cours03e.ecotrajet.adapters.OngletPagerAdapterConducteur;


public class ConducteurActivity extends FragmentActivity implements ActionBar.TabListener {

    private ViewPager viewPager;
    private ActionBar actionBar;
    private OngletPagerAdapterConducteur ongletsPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conducteur);

        // Initilisations.
        this.viewPager = (ViewPager) this.findViewById(R.id.pager);
        this.actionBar = this.getActionBar();
        this.ongletsPagerAdapter = new OngletPagerAdapterConducteur(this.getSupportFragmentManager());

        this.viewPager.setAdapter(this.ongletsPagerAdapter);

        // Permet d'avoir des onglets dans l'ActionBar.
        // "Deprecated" depuis API 21; ce n'est pas grave !
        this.actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        // Ajout des onglets à l'ActionBar.
        // Note : La classe actuelle va gérer le changement d'onglets lors du clique sur un onglet (interface "ActionBar.TabListener").
        this.actionBar.addTab(this.actionBar.newTab().setText(this.getString(R.string.textFragmentDepart)).setTabListener(this));
        this.actionBar.addTab(this.actionBar.newTab().setText(this.getString(R.string.textFragmentDemande)).setTabListener(this));

        // Gestion du changement d'onglets lorsque l'utilisateur fait un "swipe".
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }
            @Override
            public void onPageScrollStateChanged(int arg0) {
            }

        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_conducteur, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()){
            case R.id.idAjoutDepart:
                Intent i = new Intent(this, ajout_trajet.class);
                this.startActivity(i);
                break;
            case R.id.idVoirCarte:
                Intent iMap = new Intent(this, map_activity.class);
                this.startActivity(iMap);
                break;
            case R.id.idDeconnetion:
                Intent iD = new Intent(this, MainActivity.class);
                this.startActivity(iD);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
        viewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {

    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {

    }
}