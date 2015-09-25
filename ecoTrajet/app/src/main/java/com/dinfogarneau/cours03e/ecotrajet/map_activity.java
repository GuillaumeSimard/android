package com.dinfogarneau.cours03e.ecotrajet;

import android.content.Intent;
import android.location.Location;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;


public class map_activity extends FragmentActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener,
        GoogleMap.OnMapClickListener,
        GoogleMap.OnMapLongClickListener {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.

    // Objet servant à spécifier la qualité désirée de la localisation.
    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;
    // Dernière position obtenue.
    private Location mLastLocation = null;
    // Coordonnées initiales : Haute-ville de Québec.
    private final static LatLng QUEBEC_HAUTE_VILLE = new LatLng(46.813395, -71.215954);
    private boolean positionDetecte = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_activity);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        setUpMapIfNeeded();
        buildGoogleApiClient();
        createLocationRequest();
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
        Toast.makeText(this, "Connexion au service Google Play réussi", Toast.LENGTH_SHORT).show();
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
    public void onMapClick(LatLng latLng) {
        Intent i = new Intent(this, DescriptionParcoursActivity.class);
        startActivity(i);
    }

    @Override
    public void onMapLongClick(LatLng latLng) {

    }
}
