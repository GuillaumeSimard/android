package com.dinfogarneau.cours03e.ecotrajet;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;

import android.location.Location;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.dinfogarneau.cours03e.ecotrajet.DataSource.ParcourPassagerDataSource;
import com.dinfogarneau.cours03e.ecotrajet.DataSource.ParcoursDataSource;
import com.dinfogarneau.cours03e.ecotrajet.data.Parcours;
import com.dinfogarneau.cours03e.ecotrajet.data.ParcoursUtil;
import com.dinfogarneau.cours03e.ecotrajet.data.Utilisateur;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

import static com.google.android.gms.common.api.GoogleApiClient.*;


public class ajout_trajet extends FragmentActivity implements
        ConnectionCallbacks,
        OnConnectionFailedListener,
        LocationListener,
        GoogleMap.OnMapLongClickListener {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.

    // Objet servant à spécifier la qualité désirée de la localisation.
    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;
    private Parcours parcoursAdd;
    private Spinner spinerRegion;
    private ParcoursDataSource parcoursDb;
    private ParcourPassagerDataSource parcoursPassagerDb;
    private Utilisateur utilisateurRecup;
    private double lattitueDepart = 0;
    private double longitudeDepart = 0;
    private double lattitueArrive = 0;
    private double longitudeArrive = 0;

    EditText editNom;
    EditText nbPlace;
    EditText cout;
    EditText heure;
    String Date;
    ParcoursUtil parcoursUtilAdd;

    String[] strCoordoDepart = new String[2];
    String[] strCoordoArive = new String[2];

    public static final String UTILISATEURCONNECTE = "Utilisateur connecté";

    Marker markerDebut;
    Marker markerFin;

    // Dernière position obtenue.
    private Location mLastLocation = null;
    private final String TAG = getClass().getSimpleName();

    // Coordonnées initiales : Haute-ville de Québec.
    private final static LatLng QUEBEC_HAUTE_VILLE = new LatLng(46.813395, -71.215954);
    private boolean positionDetecte = false;
    private Dialog dialogConfirmation;
    public String coordoneeDebut = null;
    public String coordoneeDeArriver = null;
    int compteurClick = 1;

    TextView nom;

    //variable de gestion du calendrier
    CalendarView calendar;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ajout_trajet);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        editNom = (EditText) findViewById(R.id.idNomParcours);
        nbPlace = (EditText) findViewById(R.id.idNbPersonne);
        cout = (EditText) findViewById(R.id.idCoutPersonne);
        heure = (EditText) findViewById(R.id.idHeureDepart);



        parcoursDb = new ParcoursDataSource(this);
        parcoursPassagerDb = new ParcourPassagerDataSource((this));

        setUpMapIfNeeded();
        buildGoogleApiClient();
        createLocationRequest();
        initializeCalendar();

        final Intent intent = getIntent();
        Intent intentrecu = this.getIntent();
        Bundle extra = intentrecu.getExtras();
        this.utilisateurRecup = (Utilisateur) extra.getSerializable(InscriptionActivity.UTILISATEURCONNECTE);

        //remplissage du spinner avec les éléments de la bd.
        spinerRegion = (Spinner) findViewById(R.id.spinner_egion_id);
        ArrayAdapter<CharSequence> adapter = new ArrayAdapter <CharSequence> (this, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        parcoursDb.open();
        ArrayList<String> lstRegion = new ArrayList<String>();
        lstRegion = parcoursDb.getAllRegion();

        for(int i = 0; i <= lstRegion.size() -1 ;i++ )
        {
            adapter.add(lstRegion.get(i));
        }
        spinerRegion.setAdapter(adapter);
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            public void onMapClick(LatLng latLng) {

                if (compteurClick <= 2) {
                    if (compteurClick == 1) {
                        parcoursAdd = new Parcours();
                        coordoneeDebut = String.valueOf(latLng.latitude) + ";" + String.valueOf(latLng.longitude);
                        parcoursAdd.setM_coordonneDeparts(coordoneeDebut);

                        markerDebut = mMap.addMarker(new MarkerOptions()
                                .position(new LatLng(latLng.latitude,latLng.longitude))
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.parcour_debut)));
                        compteurClick++;
                    } else {
                        coordoneeDeArriver = String.valueOf(latLng.latitude) + ";" + String.valueOf(latLng.longitude);
                        parcoursAdd.setM_coordonneArrive(coordoneeDeArriver);

                        markerFin = mMap.addMarker(new MarkerOptions()
                                .position(new LatLng(latLng.latitude,latLng.longitude))
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.parcour_fin)));
                        compteurClick++;
                    }
                }
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle p_OutState) {
        super.onSaveInstanceState(p_OutState);

        if(coordoneeDebut != null)
        {
            p_OutState.putString("coordonneDepart",coordoneeDebut);
        }

        if(coordoneeDeArriver != null)
        {
            p_OutState.putString("coordonneArrivee",coordoneeDeArriver);
        }

        if(this.parcoursAdd != null){
            p_OutState.putSerializable("parcours", this.parcoursAdd);
        }
        Log.i(TAG, "onSaveInstanceState()");
    }

    @Override
    protected void onRestoreInstanceState(Bundle p_SavedInstanceState) {
        super.onRestoreInstanceState(p_SavedInstanceState);
        if (p_SavedInstanceState != null) {

            boolean reinitialiserRecup = p_SavedInstanceState.getBoolean("estReinitialiser");

            Parcours parcourR;
            String coordoDepartRecup = p_SavedInstanceState.getString("coordonneDepart");
            String coordoArriveRecup = p_SavedInstanceState.getString("coordonneArrivee");
            parcourR = (Parcours) p_SavedInstanceState.getSerializable("parcours");

            if(coordoDepartRecup != null){
                coordoneeDebut = coordoDepartRecup;
            }

            if(coordoArriveRecup != null){
                coordoneeDeArriver = coordoArriveRecup;
            }

            if(parcourR != null){
                this.parcoursAdd = parcourR;
            }

            if( coordoneeDebut != null &&  coordoneeDeArriver != null){
                mMap.clear();
                strCoordoDepart = coordoneeDebut.split(";");
                strCoordoArive = coordoneeDeArriver.split(";");

                for (int i = 0; i <= 1; i++) {

                    if (i == 0) {
                        lattitueDepart = Double.parseDouble(strCoordoDepart[i]);
                        lattitueArrive = Double.parseDouble(strCoordoArive[i]);
                    } else {
                        longitudeDepart = Double.parseDouble(strCoordoDepart[i]);
                        longitudeArrive = Double.parseDouble(strCoordoArive[i]);
                    }
                }
                setUpMap();
                UpdateParcoursMap();
            }
        }
        Log.i(TAG, "onRestoreInstanceState()");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_ajout_trajet, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.idAjoutTrajetM) {
            afficherPopop();
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

                //initialisation des markers
                markerDebut = mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(lattitueDepart, longitudeDepart))
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.parcour_debut)));

                markerFin = mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(lattitueArrive, longitudeArrive))
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.parcour_fin)));
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */

    private void setUpMap() {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(QUEBEC_HAUTE_VILLE, 15));
        // Permet d'afficher la position de l'utilisateur.
        mMap.setMyLocationEnabled(true);
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    protected void createLocationRequest() {
        // Voir les indications :
        // @see http://developer.android.com/training/location/receive-location-updates.html
        mLocationRequest = LocationRequest.create();
        // Voir les constantes PRIORITY_XXXXX
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        // Intervale minimal des mises à jour.
        // Des mises à jour trop fréquentes peuvent faire "flicker" le UI.
        mLocationRequest.setFastestInterval(5 * 1000);
        // Intervalle maximal pour les mises à jour de la position.
        mLocationRequest.setInterval(10 * 1000);
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Connexion au service Google Play.
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        // Déconnexion du service Google Play.
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    /**
     * Mise à jour de l'interface lorsqu'une nouvelle localisation est obtenue.
     */
    private void majPosition() {
        if (mLastLocation != null) {

            if(positionDetecte == false){
                // Centrage de la carte sur la position obtenue.
                mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude())));
                positionDetecte = true;
            }
            // Affichage de l'information sur la position obtenue à l'écran.
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
        if (mGoogleApiClient.isConnected()) {
            this.startLocationUpdates();
        }
    }

    protected void startLocationUpdates() {
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest,this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mGoogleApiClient.isConnected()) {
            this.stopLocationUpdates();
        }
    }

    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }

    /* Implémentation de l'interface "GoogleApiClient.ConnectionCallbacks" */
    /* =================================================================== */

    // Méthode appelée lorsque le client réussi à se connecter au service de localisation.
    @Override
    public void onConnected(Bundle bundle) {
        Log.d("MapsActivity", "GoogleApiClient.ConnectionCallbacks.onConnected");
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        // Mise à jour du UI.
        majPosition();
        // On demande de recevoir les mises à jour de la localisation.
        this.startLocationUpdates();
    }

    // Méthode appelée lorsque le client est temporairement déconnecté du service de localisation.
    @Override
    public void onConnectionSuspended(int i) {
        Log.d("MapsActivity", "GoogleApiClient.ConnectionCallbacks.onConnectionSuspended");
    }

    /* Implémentation de l'interface "GoogleApiClient.OnConnectionFailedListener" */
    /* ========================================================================== */

    // Méthode appelée lorsqu'une erreur survient lors de la tentative de connexion au
    // service de localisation.
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.e("MapsActivity", "GoogleApiClient.OnConnectionFailedListener.onConnectionFailed : " + connectionResult);
        Toast.makeText(this, "Erreur lors de la connexion au service Google Play", Toast.LENGTH_SHORT).show();
    }

	/* Implémentation de l'interface "LocationListener" */
    /* ================================================ */

    @Override
    public void onLocationChanged(Location location) {
        majPosition();
    }


    @Override
    public void onMapLongClick(LatLng latLng) {

    }

    /*méthode permettant de générer le popop de confirmation*/
    private void afficherPopop() {

        if(dialogConfirmation== null) {
            dialogConfirmation = new AlertDialog.Builder(this)
                    .setTitle(R.string.titrePopopCofirm)
                    .setMessage(R.string.textePopoAjout)
                    .setNegativeButton(android.R.string.cancel, null)
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {

                        //méthode permettant l'ajout
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            int iNombre;
                            if(parcoursAdd != null) {
                                iNombre =  parcoursDb.LstId();

                                parcoursAdd.setM_nomParcour(editNom.getText().toString());
                                parcoursAdd.setM_idRegion(spinerRegion.getSelectedItemPosition() + 1);
                                parcoursAdd.setM_dateParcours(Date);
                                parcoursAdd.setM_Heure(heure.getText().toString());

                                if(!nbPlace.getText().toString().equals("")) {
                                    parcoursAdd.setM_nbPlaceDisponible(Integer.parseInt(nbPlace.getText().toString()));
                                }

                                parcoursAdd.setM_nbPlacePrise(0);

                                if(!cout.getText().toString().equals("")) {
                                    parcoursAdd.setM_coutPersonne(Integer.parseInt(cout.getText().toString()));
                                }
                                parcoursAdd.setM_idParcour(iNombre + 1);

                                    if(Date != null)
                                    {

                                        if(!editNom.getText().toString().equals("") && !nbPlace.getText().toString().equals("")
                                            && !cout.getText().toString().equals("") && !heure.getText().toString().equals("")) {

                                            parcoursUtilAdd = new ParcoursUtil(parcoursAdd.getM_idParcour(), utilisateurRecup.getM_nomUtilisateur());
                                            parcoursDb.insert(parcoursAdd);
                                            parcoursPassagerDb.open();
                                            parcoursPassagerDb.insert((parcoursUtilAdd));
                                            parcoursPassagerDb.close();

                                            Intent i = new Intent(getApplicationContext(),ConducteurActivity.class);
                                            i.putExtra(UTILISATEURCONNECTE, utilisateurRecup);
                                            startActivity(i);
                                        }
                                        else{
                                            Toast.makeText(getApplicationContext(), "Tous les champs doivent être remplis pour pouvoir ajouter", Toast.LENGTH_SHORT).show();

                                        }

                                    }
                                    else{
                                        Toast.makeText(getApplicationContext(), "vous devez choisir une date sur le calendrier afin de pouvoir ajouter", Toast.LENGTH_SHORT).show();
                                    }

                                }
                                else{
                                    Toast.makeText(getApplicationContext(), "vous devez entrer des coordonnées sur la carte pour ajouter un parcours", Toast.LENGTH_SHORT).show();
                                }
                            }
                    })
                    .create();
            dialogConfirmation.setOwnerActivity(this);
            dialogConfirmation.show();
        }
        else{
            dialogConfirmation.show();
        }
    }

    //méthode permettant d'initialiser le calendrier
    public void initializeCalendar() {

        //initialisation du calendrier
        calendar = (CalendarView)findViewById(R.id.calendar);
        calendar.setShowWeekNumber(false);
        calendar.setFirstDayOfWeek(2);
        calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {

            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int day) {

                if(month < 10)
                {
                    Date = String.valueOf(day) + "/" + "0"+ String.valueOf(month+1) + "/" + String.valueOf(year);
                }
                else
                {
                    Date = String.valueOf(day) + "/" + String.valueOf(month+1) + "/" + String.valueOf(year);
                }
                Toast.makeText(getApplicationContext(), day + "/" + month + "/" + year, Toast.LENGTH_LONG).show();
            }

        });

    }

}
