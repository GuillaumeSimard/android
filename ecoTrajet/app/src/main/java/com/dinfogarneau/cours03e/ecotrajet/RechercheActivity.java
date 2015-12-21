package com.dinfogarneau.cours03e.ecotrajet;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.Toast;

import com.dinfogarneau.cours03e.ecotrajet.data.Utilisateur;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;


public class RechercheActivity extends FragmentActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener,
        GoogleMap.OnMapLongClickListener {
    /** Called when the activity is first created. */

    public static final String UTILISATEURCONNECTE = "Utilisateur connecté";
    public static final String DATEVOULU = "DATE";
    public static final String POINTDEPART = "depart";
    public static final String POINTARRIVER = "arriver";

    //déclaration des variables.
    CalendarView calendarFirst;
    CalendarView calendarSecond;
    Utilisateur UtilsateurRecup;
    private double lattitueDepart = 0;
    private double longitudeDepart = 0;
    private double lattitueArrive = 0;
    private double longitudeArrive = 0;

    private SupportMapFragment fragment;

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.

    // Objet servant à spécifier la qualité désirée de la localisation.
    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;

    Marker markerDebut;
    Marker markerFin;

    ConnectivityManager connManager;
    NetworkInfo mWifi;
    NetworkInfo m3G;
    private Dialog dialogConfirmation;


    // Dernière position obtenue.
    private Location mLastLocation = null;

    // Coordonnées initiales : Haute-ville de Québec.
    private final static LatLng QUEBEC_HAUTE_VILLE = new LatLng(46.813395, -71.215954);
    private boolean positionDetecte = false;
    public String coordoneeDebut = null;
    public String coordoneeDeArriver = null;
    public String dateVoulu;
    int compteurClick = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recherche);
        Button btn = (Button)this.findViewById(R.id.btnRechercher);
        calendarFirst = (CalendarView) findViewById(R.id.calendar);

        getActionBar().setHomeButtonEnabled(true);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        final Intent intent = getIntent();
        Intent intentrecu = this.getIntent();
        Bundle extra = intentrecu.getExtras();
        this.UtilsateurRecup = (Utilisateur) extra.getSerializable(HistoriqueActivity.UTILISATEURCONNECTE);
        if (this.UtilsateurRecup == null)
        {
            this.UtilsateurRecup = (Utilisateur) extra.getSerializable(ResultatRechercheActivity.UTIL);
        }


        //initialisation des calendriers
        setUpMapIfNeeded();
        buildGoogleApiClient();
        createLocationRequest();
        initializeCalendar();

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            public void onMapClick(LatLng latLng) {

                if (compteurClick <= 2) {
                    if (compteurClick == 1) {

                        coordoneeDebut = String.valueOf(latLng.latitude) + " " + String.valueOf(latLng.longitude);
                        markerDebut = mMap.addMarker(new MarkerOptions()
                                .position(new LatLng(latLng.latitude, latLng.longitude))
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.parcour_debut)));
                        compteurClick++;
                    } else {
                        coordoneeDeArriver = String.valueOf(latLng.latitude) + " " + String.valueOf(latLng.longitude);

                        markerFin = mMap.addMarker(new MarkerOptions()
                                .position(new LatLng(latLng.latitude, latLng.longitude))
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.parcour_fin)));
                        compteurClick++;
                    }
                }
            }
        });
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_recherche, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.aide_Recherche:
                if(dialogConfirmation== null) {
                    dialogConfirmation = new AlertDialog.Builder(this)
                            .setTitle(R.string.AideRechercheMenu)
                            .setMessage(R.string.RechercheParcourAide)
                            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {

                                //méthode permettant la suppression
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            })
                            .create();
                    dialogConfirmation.setOwnerActivity(this);
                    dialogConfirmation.show();
                }
                else{
                    dialogConfirmation.show();
                }
                break;

            case android.R.id.home:
                Intent i = new Intent(this, HistoriqueActivity.class);
                i.putExtra(UTILISATEURCONNECTE, this.UtilsateurRecup);
                startActivity(i);
                break;
            case R.id.idDeconnetion:
                deconnection();
                Intent iD = new Intent(this, MainActivity.class);
                this.startActivity(iD);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    //Méthode permettant d'initiaiser le calendrier
    public void initializeCalendar() {
        //initialisation du calendrier
        calendarFirst = (CalendarView) this.findViewById(R.id.calendar);
        calendarFirst.setShowWeekNumber(false);
        calendarFirst.setFirstDayOfWeek(2);
        calendarFirst.setSelectedWeekBackgroundColor(getResources().getColor(R.color.green));
        calendarFirst.setUnfocusedMonthDateColor(getResources().getColor(R.color.transparent));
        calendarFirst.setWeekSeparatorLineColor(getResources().getColor(R.color.transparent));
        calendarFirst.setSelectedDateVerticalBar(R.color.darkgreen);
        calendarFirst.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int day) {
                Toast.makeText(getApplicationContext(), day + "/" + month + "/" + year, Toast.LENGTH_LONG).show();
                dateVoulu = String.valueOf(day) + "/" + String.valueOf(month +1) + "/" + String.valueOf(year);
            }
        });
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
        mGoogleApiClient = new GoogleApiClient.Builder(this)
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


    /********************************************************************************************
     * Gestion des cycles
     *******************************************************************************************/
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

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
        if (mGoogleApiClient.isConnected()) {
            this.startLocationUpdates();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mGoogleApiClient.isConnected()) {
            this.stopLocationUpdates();
        }
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



    protected void startLocationUpdates() {
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
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


    public void onClickRechercher(View v) {

            Intent intent = new Intent(this, ResultatRechercheActivity.class);
            intent.putExtra(UTILISATEURCONNECTE, this.UtilsateurRecup);
            intent.putExtra(DATEVOULU, dateVoulu);
            intent.putExtra(POINTDEPART, coordoneeDebut);
            intent.putExtra(POINTARRIVER, coordoneeDeArriver);
            this.startActivity(intent);
    }

    //méthode permmettant d'afficher un popop
    private void afficherMessage() {

        if(dialogConfirmation== null) {
            dialogConfirmation = new AlertDialog.Builder(this)
                    .setTitle(R.string.titreErreurConnection)
                    .setMessage(R.string.btextErreurConnexion)
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {

                        //méthode permettant la suppression
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    })
                    .create();
            dialogConfirmation.show();
        }
        else{
            dialogConfirmation.show();
        }
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

}