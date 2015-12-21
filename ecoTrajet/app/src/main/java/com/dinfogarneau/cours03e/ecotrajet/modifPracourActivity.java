package com.dinfogarneau.cours03e.ecotrajet;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.dinfogarneau.cours03e.ecotrajet.DataSource.ParcoursDataSource;
import com.dinfogarneau.cours03e.ecotrajet.data.Parcours;
import com.dinfogarneau.cours03e.ecotrajet.data.Utilisateur;
import com.dinfogarneau.cours03e.ecotrajet.fragment.DepartFragment;
import com.dinfogarneau.cours03e.ecotrajet.unit.JSonParser;
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

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.net.URI;
import java.util.ArrayList;
import java.util.Calendar;

public class modifPracourActivity extends FragmentActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener{

    //déclaration des variables
    private Dialog dialogConfirmation;
    private Parcours parcoursRecup = null;
    private ParcoursDataSource parcoursDb;
    private double lattitueDepart = 0;
    private double longitudeDepart = 0;
    private double lattitueArrive = 0;
    private double longitudeArrive = 0;
    private Utilisateur utilRecap;
    Marker markerDebut;
    Marker markerFin;
    Boolean estReinitialise = false;
    private Location mLastLocation = null;
    private boolean positionDetecte = false;
    private static LatLng PARCOURS_COORDONNE = null;
    private final static LatLng QUEBEC_HAUTE_VILLE = new LatLng(46.813395, -71.215954);
    public static String UTILISATEUR = "utilisateur";
    public static String PARCOURS = "parcours";

    ConnectivityManager connManager;
    NetworkInfo mWifi;
    NetworkInfo m3G;
    private Dialog dialogErreur;

    private GoogleMap mMap;
    TextView textTitre;
    Spinner spinerRegion;

    EditText nomParcours;
    EditText nbPersonne;
    EditText Heure;
    EditText cout;
    String Date;
    String[] strCoordoDepart = new String[2];
    String[] strCoordoArive = new String[2];
    public String coordoneeDebut = null;
    public String coordoneeDeArriver = null;

    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;

    int iCompteur = 1;
    boolean tourner = false;

    private final static String WEB_SERVICE_URL = "ecotrajet-1065.appspot.com";
    private final static String REST_UTILISATEURS = "/utilisateurs";
    private final static String REST_PARCOURS = "/Parcours";
    private final String TAG = this.getClass().getSimpleName();
    private HttpClient m_ClientHttp = new DefaultHttpClient();
    AlertDialog dialogAide;


    /**
     * Méthode utilisée afin de créer l'activité.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modif_parcour);

        parcoursDb = new ParcoursDataSource((this));


        Intent intentrecu = this.getIntent();
        Bundle extra = intentrecu.getExtras();

        this.utilRecap = (Utilisateur) extra.getSerializable(DepartFragment.UTILISATEUR);
        this.parcoursRecup = (Parcours) extra.getSerializable(DepartFragment.PARCOUR_CLICK);


        textTitre = (TextView) findViewById(R.id.idTextEnteteModif);
        nomParcours = (EditText) findViewById(R.id.idNomParcoursModif);
        nbPersonne = (EditText) findViewById(R.id.idNbPlaceModif);
        Heure = (EditText) findViewById(R.id.idHeureModif);
        cout = (EditText) findViewById(R.id.idCoutPersonneModif);

        if(this.parcoursRecup.getM_coordonneArrive() == null){
            estReinitialise = true;
        }
        else{
            estReinitialise = false;
        }

        //set des variable
        nomParcours.setText(this.parcoursRecup.getM_nomParcour());
        nbPersonne.setText(String.valueOf(this.parcoursRecup.getM_nbPlaceDisponible()));
        Heure.setText(String.valueOf(this.parcoursRecup.getM_Heure()));
        cout.setText(String.valueOf(this.parcoursRecup.getM_coutPersonne()));

        //récupération des coordonnées d'un trajet

        if(tourner == false) {

            if (estReinitialise == false) {

                if (this.parcoursRecup.getM_coordonneDeparts() != null && this.parcoursRecup.getM_coordonneArrive() != null) {
                    if (coordoneeDebut == null && coordoneeDeArriver == null) {
                        coordoneeDebut = parcoursRecup.getM_coordonneDeparts();
                        coordoneeDeArriver = parcoursRecup.getM_coordonneArrive();
                    }


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
                }
            }
        }


        textTitre.setText("modification du parcours :" + this.parcoursRecup.getM_nomParcour());

        //remplissage du spinner avec les éléments de la bd.
        spinerRegion = (Spinner) findViewById(R.id.idSpinnerRegionModif);
        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(this, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        parcoursDb.open();
        ArrayList<String> lstRegion = new ArrayList<String>();
        lstRegion = parcoursDb.getAllRegion();

        for (int i = 0; i <= lstRegion.size() - 1; i++) {
            adapter.add(lstRegion.get(i));
        }
        spinerRegion.setAdapter(adapter);
        spinerRegion.setSelection(this.parcoursRecup.getM_idRegion() - 1);



        setUpMapIfNeeded();
            if(this.parcoursRecup.getM_coordonneDeparts() == null && this.parcoursRecup.getM_coordonneArrive() == null){       setUpMapIfNeeded();
                buildGoogleApiClient();
                createLocationRequest();
            }
            else{
                if(estReinitialise == false) {
                    UpdateParcoursMap();
                }
            }


        //initialisation du calendrier
        initializeCalendar();

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            public void onMapClick(LatLng latLng) {

                if (estReinitialise) {
                    if (iCompteur <= 2) {
                        if (iCompteur == 1) {
                            coordoneeDebut = String.valueOf(latLng.latitude) + ";" + String.valueOf(latLng.longitude);
                            parcoursDb.open();
                            parcoursRecup.setM_coordonneDeparts(coordoneeDebut);

                            markerDebut = mMap.addMarker(new MarkerOptions()
                                    .position(new LatLng(latLng.latitude, latLng.longitude))
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.parcour_debut)));
                            iCompteur++;
                        } else {
                            coordoneeDeArriver = String.valueOf(latLng.latitude) + ";" + String.valueOf(latLng.longitude);
                            parcoursDb.open();
                            parcoursRecup.setM_coordonneArrive(coordoneeDeArriver);

                            markerFin = mMap.addMarker(new MarkerOptions()
                                    .position(new LatLng(latLng.latitude, latLng.longitude))
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.parcour_fin)));
                            iCompteur++;
                        }
                    }
                }
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle p_OutState) {
        super.onSaveInstanceState(p_OutState);
        if (estReinitialise != null) {
            p_OutState.putBoolean("estReinitialiser", estReinitialise);
        }

        if(coordoneeDebut != null)
        {
            p_OutState.putString("coordonneDepart",coordoneeDebut);
        }

        if(coordoneeDeArriver != null)
        {
            p_OutState.putString("coordonneArrivee",coordoneeDeArriver);
        }

        if(this.parcoursRecup != null){
            p_OutState.putSerializable("parcours", this.parcoursRecup);
        }

        if(Date != "")
        {
            p_OutState.putString("date", Date);
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
            String dateRecup = p_SavedInstanceState.getString("date");


                estReinitialise = reinitialiserRecup;

                if(coordoDepartRecup != null){
                    coordoneeDebut = coordoDepartRecup;
                }

                if(coordoArriveRecup != null){
                    coordoneeDeArriver = coordoArriveRecup;
                }

                if(parcourR != null){
                    this.parcoursRecup = parcourR;
                }

                if(dateRecup != null)
                {
                    this.Date = dateRecup;
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

                estReinitialise = false;
                tourner = true;

                    setUpMap();
                    UpdateParcoursMap();

            }
            else{
                buildGoogleApiClient();
                createLocationRequest();
            }

            initializeCalendar();
        }
        Log.i(TAG, "onRestoreInstanceState()");
    }
                    @Override
                    public boolean onCreateOptionsMenu(Menu menu) {
                        // Inflate the menu; this adds items to the action bar if it is present.
                        getMenuInflater().inflate(R.menu.menu_modif_pracour, menu);
                        return true;
                    }

                    @Override
                    public boolean onOptionsItemSelected(MenuItem item) {
                        // Handle action bar item clicks here. The action bar will
                        // automatically handle clicks on the Home/Up button, so long
                        // as you specify a parent activity in AndroidManifest.xml.
                        int id = item.getItemId();

                        switch(id)
                        {
                            case R.id.idModifParcour:
                                afficherPopop();
                                break;

                            case R.id.aide_ModifParcours:

                                if(dialogAide== null) {
                                    dialogAide = new AlertDialog.Builder(this)
                                            .setTitle(R.string.AideModifParcoursMenu)
                                            .setMessage(R.string.ModificationParcoursAide)
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

                    private void UpdateParcoursMap() {

                        if( estReinitialise == false) {

                            if (this.parcoursRecup.getM_coordonneDeparts() != null && this.parcoursRecup.getM_coordonneArrive() != null) {
                                //initialisation des markers
                                markerDebut = mMap.addMarker(new MarkerOptions()
                                        .position(new LatLng(lattitueDepart, longitudeDepart))
                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.parcour_debut)));

                                markerFin = mMap.addMarker(new MarkerOptions()
                                        .position(new LatLng(lattitueArrive, longitudeArrive))
                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.parcour_fin)));
                            }
                        }
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

                        if(this.parcoursRecup.getM_coordonneDeparts() != null && this.parcoursRecup.getM_coordonneArrive() != null) {
                            PARCOURS_COORDONNE = new LatLng(lattitueDepart, longitudeDepart);
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(PARCOURS_COORDONNE, 14));
                        }
                        else{
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(QUEBEC_HAUTE_VILLE, 15));
                        }
                            // Permet d'afficher la position de l'utilisateur.
                            mMap.setMyLocationEnabled(true);
                    }


                    /*méthode permettant de générer le popop de confirmation*/
                    private void afficherPopop() {

                        if (dialogConfirmation == null) {
                            dialogConfirmation = new AlertDialog.Builder(this)
                                    .setTitle(R.string.titrePopopModifDepart)
                                    .setMessage(R.string.textePopopModifDepart)
                                    .setNegativeButton(android.R.string.cancel, null)
                                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {

                                        //méthode permettant la suppression
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            parcoursRecup.setM_nomParcour(nomParcours.getText().toString());
                                            parcoursRecup.setM_dateParcours(Date);
                                            parcoursRecup.setM_nbPlaceDisponible(Integer.parseInt(nbPersonne.getText().toString()));
                                            parcoursRecup.setM_Heure(Heure.getText().toString());
                                            parcoursRecup.setM_idRegion(spinerRegion.getSelectedItemPosition() + 1);
                                            parcoursRecup.setM_nbPlacePrise(parcoursRecup.getM_nbPlacePrise());
                                            parcoursRecup.setM_coutPersonne(Double.parseDouble(cout.getText().toString()));
                                            parcoursRecup.setM_idParcour(parcoursRecup.getM_idParcour());

                                            if(parcoursRecup.getM_coordonneDeparts() != null && parcoursRecup.getM_coordonneArrive() != null){

                                                    new UpdateParcoursTask().execute((Void) null);

                                                    Intent intentConducteur = new Intent(getApplicationContext(), ConducteurActivity.class);
                                                    intentConducteur.putExtra(UTILISATEUR, utilRecap);
                                                    startActivity(intentConducteur);
                                            }
                                            else{
                                                Toast.makeText(getApplicationContext(), "vous devez entrer des coordonné pour modifier ce parcours", Toast.LENGTH_SHORT).show();
                                                estReinitialise = true;

                                            }
                                        }
                                    })
                                    .create();
                            dialogConfirmation.setOwnerActivity(this);
                            dialogConfirmation.show();
                        } else {
                            dialogConfirmation.show();
                        }
                    }

                    //méthode permettant d'initialiser le calendrier
                    public void initializeCalendar() {
                        CalendarView calendar = (CalendarView) findViewById(R.id.calendar);
                        Calendar calendarInstance = Calendar.getInstance();

                        String parts[] = new String[3];
                        if(Date == null) {
                            parts = this.parcoursRecup.getM_dateParcours().split("/");
                            Date = parcoursRecup.getM_dateParcours();

                        }
                        else{
                            parts =this.Date.split("/");
                        }



                        //set des valeur dans le calendrier.
                        int jour = Integer.parseInt(parts[0]);
                        int mois = Integer.parseInt(parts[1]);
                        int annee = Integer.parseInt(parts[2]);
                        calendarInstance.set(Calendar.YEAR, annee);
                        calendarInstance.set(Calendar.MONTH, mois-1);
                        calendarInstance.set(Calendar.DAY_OF_MONTH, jour);
                        calendar.setDate(calendarInstance.getTimeInMillis(),true,true);

                        calendar.setShowWeekNumber(false);
                        calendar.setFirstDayOfWeek(2);

                        calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {

                            @Override
                            public void onSelectedDayChange(CalendarView view, int year, int month, int day) {

                                if (month < 10) {
                                    Date = String.valueOf(day) + "/" + "0" + String.valueOf(month +1) + "/" + String.valueOf(year);
                                } else {
                                    Date = String.valueOf(day) + "/" + String.valueOf(month +1) + "/" + String.valueOf(year);
                                }
                            }

                        });
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

    /****************************************************************************************
     * Gestion des cycles
     ***************************************************************************************/

    @Override
    protected void onStart() {
        super.onStart();

        if (this.parcoursRecup.getM_coordonneDeparts() == null && this.parcoursRecup.getM_coordonneArrive() == null) {

            if(mGoogleApiClient != null) {
                // Connexion au service Google Play.
                mGoogleApiClient.connect();
            }

        }
    }

    @Override
    protected void onStop() {

        if (this.parcoursRecup.getM_coordonneDeparts() == null && this.parcoursRecup.getM_coordonneArrive() == null) {

            if(mGoogleApiClient != null) {
                // Déconnexion du service Google Play.
                mGoogleApiClient.disconnect();
            }
        }
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (this.parcoursRecup.getM_coordonneDeparts() == null && this.parcoursRecup.getM_coordonneArrive() == null) {
            setUpMapIfNeeded();
            if(mGoogleApiClient != null) {
                if (mGoogleApiClient.isConnected()) {
                    this.startLocationUpdates();
                }
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }



    protected void startLocationUpdates() {

        if(mGoogleApiClient != null) {
            if (this.parcoursRecup.getM_coordonneDeparts() == null && this.parcoursRecup.getM_coordonneDeparts() == null) {
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
            }
        }
    }




    /* Implémentation de l'interface "GoogleApiClient.ConnectionCallbacks" */
    /* =================================================================== */

    // Méthode appelée lorsque le client réussi à se connecter au service de localisation.
    @Override
    public void onConnected(Bundle bundle) {

        if(mGoogleApiClient != null) {
            Log.d("modifParourActivity", "GoogleApiClient.ConnectionCallbacks.onConnected");
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            // Mise à jour du UI.
            majPosition();
            // On demande de recevoir les mises à jour de la localisation.
            this.startLocationUpdates();
        }
    }

    // Méthode appelée lorsque le client est temporairement déconnecté du service de localisation.
    @Override
    public void onConnectionSuspended(int i) {
        Log.d("modifParourActivity", "GoogleApiClient.ConnectionCallbacks.onConnectionSuspended");
    }

    /* Implémentation de l'interface "GoogleApiClient.OnConnectionFailedListener" */
    /* ========================================================================== */

    // Méthode appelée lorsqu'une erreur survient lors de la tentative de connexion au
    // service de localisation.
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.e("modifParourActivity", "GoogleApiClient.OnConnectionFailedListener.onConnectionFailed : " + connectionResult);
        Toast.makeText(this, "Erreur lors de la connexion au service Google Play", Toast.LENGTH_SHORT).show();
    }

	/* Implémentation de l'interface "LocationListener" */
    /* ================================================ */

    @Override
    public void onLocationChanged(Location location) {
    }


    //Méthode permettant de reinitialiser les coordonnées de départs d'un parcours
    public void OnClickDeleteMarker(View v){

        if(estReinitialise == false)
        {
            this.parcoursRecup.setM_coordonneDeparts(null);
            this.parcoursRecup.setM_coordonneArrive(null);
            coordoneeDeArriver = null;
            estReinitialise = true;
            parcoursDb.open();
            parcoursDb.update(this.parcoursRecup);
            parcoursDb.close();
            mMap.clear();
        }
        else{
            estReinitialise = false;
        }
    }
    /**********************************************************************************************
     * Sous classe Asynchrone permettant de modifier d'un parcours au services web
     **********************************************************************************************/
    private class UpdateParcoursTask extends AsyncTask<Void, Void, Void> {
        Exception m_Exp;

        @Override
        protected void onPreExecute() {
            setProgressBarIndeterminateVisibility(true);
        }

        @Override
        protected Void doInBackground(Void ... unused) {
            try {

                URI uri = new URI("http", WEB_SERVICE_URL, REST_UTILISATEURS + "/" + utilRecap.getM_nomUtilisateur()  + REST_PARCOURS + "/" + parcoursRecup.getM_idServiceWeb(), null, null);
                HttpPut putMethod = new HttpPut(uri);

                JSONObject obj = JSonParser.serialiserJsonParcours(parcoursRecup);
                putMethod.setEntity(new StringEntity(obj.toString()));
                putMethod.addHeader("Content-Type", "application/json");

                m_ClientHttp.execute(putMethod, new BasicResponseHandler());
                Log.i(TAG, "Put terminé");

            } catch (Exception e) {
                m_Exp = e;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            setProgressBarIndeterminateVisibility(false);

            if (m_Exp == null) {
            } else {
                Log.e(TAG, "Error while posting", m_Exp);
                Toast.makeText(modifPracourActivity.this, getString(R.string.comm_error), Toast.LENGTH_SHORT).show();
            }
        }
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
}
