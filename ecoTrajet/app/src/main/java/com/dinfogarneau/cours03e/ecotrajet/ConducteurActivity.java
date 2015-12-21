package com.dinfogarneau.cours03e.ecotrajet;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;

import com.dinfogarneau.cours03e.ecotrajet.adapters.OngletPagerAdapterConducteur;
import com.dinfogarneau.cours03e.ecotrajet.data.Utilisateur;


public class ConducteurActivity extends FragmentActivity implements ActionBar.TabListener {

    private ViewPager viewPager;
    private ActionBar actionBar;
    private OngletPagerAdapterConducteur ongletsPagerAdapter;
    public static Utilisateur utilisateurRecup;
    AlertDialog dialogAide;

    public static final String UTILISATEURCONNECTE = "Utilisateur connecté";

    // À toutes les 7 secondes (dans un but de démonstration uniquement).
    public static int INTERVAL_ALARM = 2000;

    // Identifiant pour l'intention en suspens de l'alarme.
    public static final int ID_ALARM = 12345;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conducteur);

        Intent intentrecu = this.getIntent();
        Bundle extra = intentrecu.getExtras();

        if(extra != null) {
            if (extra.getSerializable(InscriptionActivity.UTILISATEURCONNECTE) != null) {
                utilisateurRecup = (Utilisateur) extra.getSerializable(InscriptionActivity.UTILISATEURCONNECTE);
            } else if (extra.getSerializable(modifPracourActivity.UTILISATEUR) != null) {
                this.utilisateurRecup = (Utilisateur) extra.getSerializable(modifPracourActivity.UTILISATEUR);
            }
        }



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

    /**
     * Méthode permettant la création du menu.
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_conducteur, menu);
        return true;
    }

    /**
     * Méthode permettant de gerer le clique et l'option choisi lorsque l'on
     * appuit sur un élément du menu.
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()){
            case R.id.idAjoutDepart:
                Intent i = new Intent(this, ajout_trajet.class);
                i.putExtra(UTILISATEURCONNECTE, utilisateurRecup);
                this.startActivity(i);
                break;
            case R.id.idDeconnetion:
                deconnection();
                Intent iD = new Intent(this, MainActivity.class);
                this.startActivity(iD);
                break;
            case R.id.aide_Conducteur:
                if(dialogAide== null) {
                    dialogAide = new AlertDialog.Builder(this)
                            .setTitle(R.string.AideConducteurMenu)
                            .setMessage(R.string.ConducteurActivityAide)
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

    /**
     * Méthode permettant de gerer la déconnexion d'un utilisateur.
     */
    public void deconnection(){

        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
        editor.putString("nomUtilisateur","");
        editor.putString("motPasse", "");
        editor.commit();
    }

    /**********************************************************************************************
     * Gestion des cycles
     *********************************************************************************************/

    @Override
    protected void onStart() {
        super.onStart();
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
    }
}