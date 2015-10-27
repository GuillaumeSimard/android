package com.dinfogarneau.cours03e.ecotrajet;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dinfogarneau.cours03e.ecotrajet.DataSource.ParcourPassagerDataSource;
import com.dinfogarneau.cours03e.ecotrajet.DataSource.ParcoursDataSource;
import com.dinfogarneau.cours03e.ecotrajet.R;
import com.dinfogarneau.cours03e.ecotrajet.data.Parcours;
import com.dinfogarneau.cours03e.ecotrajet.data.Utilisateur;
import com.dinfogarneau.cours03e.ecotrajet.fragment.DepartFragment;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Calendar;

/**
 * Created by Remy Huot on 2015-09-18.
 */
public class DescriptionParcoursActivity extends FragmentActivity {

    private Parcours parcourRecup;
    private Utilisateur utilRecup;
    private double lattitueDepart;
    private double longitudeDepart;
    private double lattitueArrive;
    private double longitudeArrive;
    DescriptionParcoursActivity des;
    Marker markerDebut;
    Marker markerFin;
    private ParcourPassagerDataSource parcoursUtilBd;
    private ParcoursDataSource parcoursBd;

    TextView nom;
    TextView date;
    TextView nbPassagerDispo;
    TextView heure;
    TextView coutPersonne;
    TextView nomConducteur;
    TextView region;

    private static LatLng PARCOURS_COORDONNE = null;

    private GoogleMap mMap;

    Button btn;
    LinearLayout linear;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_descriptionparcours);

        this.des = new DescriptionParcoursActivity();
        String coordonneeDepart;
        String coordonneeArrive;
        String[] strCoordoDepart = new String[2];
        String[] strCoordoArive = new String[2];

        Intent intentrecu = this.getIntent();
        Bundle extra = intentrecu.getExtras();
        this.parcourRecup = (Parcours) extra.getSerializable(DepartFragment.PARCOUR_CLICK);
        this.utilRecup = (Utilisateur) extra.getSerializable(DepartFragment.UTILISATEUR);

        //initialisation des tables
        parcoursUtilBd = new ParcourPassagerDataSource(this);
        parcoursBd = new ParcoursDataSource(this);

        linear = (LinearLayout) findViewById(R.id.idLenearLayout);
        btn = (Button) findViewById(R.id.btnDemanderDescParcours);
        nom = (TextView) findViewById(R.id.idNomDesc);
        date = (TextView) findViewById(R.id.idDateDes);
        nbPassagerDispo = (TextView) findViewById(R.id.idNbPesonneDes);
        heure = (TextView) findViewById(R.id.idHeureDes);
        coutPersonne = (TextView) findViewById(R.id.idCoutPersonneDes);
        nomConducteur = (TextView) findViewById(R.id.idNomConducteurDes);
        region = (TextView) findViewById(R.id.idRegionDes);

        //specification du texte à l'intérieur
        nom.setText(this.parcourRecup.getM_nomParcour());
        date.setText(this.parcourRecup.getM_dateParcours());
        nbPassagerDispo.setText(String.valueOf(this.parcourRecup.getM_nbPlaceDisponible()));
        heure.setText(this.parcourRecup.getM_Heure());
        coutPersonne.setText(String.valueOf(this.parcourRecup.getM_coutPersonne()));
        parcoursUtilBd.open();
        nomConducteur.setText(parcoursUtilBd.findParcourConduc(this.parcourRecup.getM_idParcour()));
        parcoursUtilBd.close();
        parcoursBd.open();
        region.setText(parcoursBd.getRegionName(this.parcourRecup.getM_idRegion()));
        parcoursBd.close();

        if (this.parcourRecup.getM_coordonneDeparts() != null && this.parcourRecup.getM_coordonneArrive() != null){

            coordonneeDepart = parcourRecup.getM_coordonneDeparts();
        coordonneeArrive = parcourRecup.getM_coordonneArrive();
        strCoordoDepart = coordonneeDepart.split(";");
        strCoordoArive = coordonneeArrive.split(";");

        for (int i = 0; i <= 1; i++) {

            if (i == 0) {
                lattitueDepart = Double.parseDouble(strCoordoDepart[i]);
                lattitueArrive = Double.parseDouble(strCoordoArive[i]);
            } else {
                longitudeDepart = Double.parseDouble(strCoordoDepart[i]);
                longitudeArrive = Double.parseDouble(strCoordoArive[i]);
            }
        }
    }

        if(utilRecup.getM_idTypePassager() == 1)
        {
            linear.setVisibility(View.INVISIBLE);
        }

        setUpMapIfNeeded();
        UpdateParcoursMap();
        getActionBar().setDisplayHomeAsUpEnabled(true);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
        /**
         * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
         * installed) and the map has not already been instantiated.. This will ensure that we only ever
         * call {@link #setUpMap()} once when {@link #mMap} is not null.
         * <p/>
         * If it isn't installed {@link SupportMapFragment} (and
         * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
         * install/update the Google Play services APK on their device.
         * <p/>
         * A user can return to this FragmentActivity after following the prompt and correctly
         * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
         * have been completely destroyed during this process (it is likely that it would only be
         * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
         * method in {@link #onResume()} to guarantee that it will be called.
         */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    private void UpdateParcoursMap() {

        if (this.parcourRecup.getM_coordonneDeparts() != null && this.parcourRecup.getM_coordonneArrive() != null) {
            //initialisation des markers
            markerDebut = mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(lattitueDepart, longitudeDepart))
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.parcour_debut)));

            markerFin = mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(lattitueArrive, longitudeArrive))
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.parcour_fin)));
        }
    }


    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */

    private void setUpMap() {

        PARCOURS_COORDONNE = new LatLng(lattitueDepart, longitudeDepart);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(PARCOURS_COORDONNE, 15));
        // Permet d'afficher la position de l'utilisateur.
        mMap.setMyLocationEnabled(true);
    }


    public void onClickDemander(View v) {
        Toast.makeText(this, "La demande à été envoyée.", Toast.LENGTH_SHORT).show();
        btn.setText("Demande envoyée");
    }

}
