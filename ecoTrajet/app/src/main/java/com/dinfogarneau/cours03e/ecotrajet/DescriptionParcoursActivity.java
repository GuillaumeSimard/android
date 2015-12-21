package com.dinfogarneau.cours03e.ecotrajet;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dinfogarneau.cours03e.ecotrajet.DataSource.ParcoursDataSource;
import com.dinfogarneau.cours03e.ecotrajet.data.Parcours;
import com.dinfogarneau.cours03e.ecotrajet.data.Utilisateur;
import com.dinfogarneau.cours03e.ecotrajet.drawMap.mapDra;
import com.dinfogarneau.cours03e.ecotrajet.fragment.DepartFragment;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.impl.client.DefaultHttpClient;
import org.w3c.dom.Document;

import java.net.URI;
import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Created by Remy Huot on 2015-09-18.
 */
public class DescriptionParcoursActivity extends FragmentActivity  implements GoogleMap.OnMarkerClickListener {

    //déclaration des variable utile au service web.
    private final static String WEB_SERVICE_URL = "ecotrajet-1065.appspot.com";
    private final static String REST_UTILISATEURS = "/utilisateurs";
    private final static String REST_PARCOURS = "/parcours";
    private final static String REST_DEMANDE_A = "/demandeParcours";

    // Clé pour l'information attachée à l'intention.
    public static final String UTILISATEURPASSE = "util";


    //déclaration des autres variables
    public final static String PARCOUR = "";
    public final static String UTILISATEUR = "";
    private Parcours parcourRecup;
    private Utilisateur utilRecup;
    private double lattitueDepart;
    private double longitudeDepart;
    private double lattitueArrive;
    private double longitudeArrive;
    DescriptionParcoursActivity des;
    Marker markerDebut;
    Marker markerFin;
    private ParcoursDataSource parcoursBd;
    private Dialog dialogConfirmation;
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
    private final String TAG = this.getClass().getSimpleName();
    private HttpClient m_ClientHttp = new DefaultHttpClient();
    private mapDra dessinTrajet;
    private Document document;
    URI uri;

    ArrayList<URI> strDemandeDeconn = new ArrayList<URI>();

    // Identifiant unique pour la notification.
    private static final int ID_NOTIF = 12345;

    /**
     * Méthode appelé lors de la création de l'activité.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_descriptionparcours);

        this.registerReceiver(this.conn, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));


        this.des = new DescriptionParcoursActivity();
        dessinTrajet = new mapDra();
        String coordonneeDepart;
        String coordonneeArrive;
        String[] strCoordoDepart = new String[2];
        String[] strCoordoArive = new String[2];

        Intent intentrecu = this.getIntent();
        Bundle extra = intentrecu.getExtras();

        //récupration du parcours selon différentes Activitées.
        this.parcourRecup = (Parcours) extra.getSerializable(HistoriqueActivity.PARCOURS_CLICK);
        this.utilRecup = (Utilisateur) extra.getSerializable(DepartFragment.UTILISATEUR);
        if (this.parcourRecup == null) {
            this.parcourRecup = (Parcours) extra.getSerializable(ResultatRechercheActivity.PARCOUR_CLICK);

            if (this.parcourRecup == null) {
                this.parcourRecup = (Parcours) extra.getSerializable(DepartFragment.PARCOUR_CLICK);

                if (this.parcourRecup == null) {
                    this.parcourRecup = (Parcours) extra.getSerializable(ResultatRechercheActivity.PARCOUR_CLICK);

                }
            }
        }

        //récupration de l'utilisateur selon différentes Activitées.
        if (this.utilRecup == null) {
            this.utilRecup = (Utilisateur) extra.getSerializable(HistoriqueActivity.UTILISATEURCONNECTE);

            if (this.utilRecup == null) {
                this.utilRecup = (Utilisateur) extra.getSerializable(ResultatRechercheActivity.UTIL);

            }
        }

        //initialisation des tables
        parcoursBd = new ParcoursDataSource(this);
        this.parcoursBd.open();

        //récupérations des valeurs des textView
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
        nomConducteur.setText(this.parcourRecup.getM_nomConducteure());
        nbPassagerDispo.setText(String.valueOf(this.parcourRecup.getM_nbPlaceDisponible()));
        heure.setText(this.parcourRecup.getM_Heure());
        coutPersonne.setText(String.valueOf(this.parcourRecup.getM_coutPersonne()));
        region.setText(parcoursBd.getRegionName(this.parcourRecup.getM_idRegion()));

        if (this.parcourRecup.getM_coordonneDeparts() != null && this.parcourRecup.getM_coordonneArrive() != null) {

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

        if (parcourRecup.getLstPassager().contains(utilRecup.getM_prenom())) {
            linear.setVisibility(View.INVISIBLE);
        }

        if(parcourRecup.getM_nomConducteure().equals(utilRecup.getM_nomUtilisateur()))
        {
            linear.setVisibility(View.INVISIBLE);
        }

        setUpMapIfNeeded();
        UpdateParcoursMap();
        getActionBar().setDisplayHomeAsUpEnabled(true);

        DindPathTrajet getRoute = new DindPathTrajet();
        getRoute.execute();

        //listner permettant d'afficher la distance d'un parcours lorsque l'on appit sur
        //un de ses marker.
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {

                                          @Override
                                          public boolean onMarkerClick(Marker marker) {
                                              afficherPopop();
                                              return true;
                                          }

     }
);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_description_parcour, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:

                finish();
                return true;
            case R.id.aide_DescriptionParcours:
                if(dialogConfirmation== null) {
                    dialogConfirmation = new AlertDialog.Builder(this)
                            .setTitle(R.string.AideDescriptionParcourMenu)
                            .setMessage(R.string.DescriptionParcoursAide)
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

            case R.id.idDeconnetion:
                deconnection();
                Intent iD = new Intent(this, MainActivity.class);
                this.startActivity(iD);
                break;
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

    /**
     * Méthode permettant de mettre à jours ma position lorsque je me déplace.
     */
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

    /**
     * Méthode permettant d'appeler la classe asynchrone qui envoie une demande au service web.
     * @param v
     */
    public void onClickDemander(View v) {
        ConnectivityManager conn = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifi = conn.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo c3G = conn.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        //vérification de la connexion reseau lors de l'envoit des demandes.
        if (wifi != null && wifi.isConnected() || c3G != null && c3G.isConnected()) {
            new SendDemandeListTask().execute((Void) null);

            Intent i = new Intent(this, HistoriqueActivity.class);
            i.putExtra(UTILISATEURPASSE, this.utilRecup);
        }
        else
        {
            Exception m_Exp;
            try {

                //construction de la liste d'uri afin de les envoyer au service web une fois la connexion réseau sera revenu.
                URI unUri = new URI("http", WEB_SERVICE_URL, REST_UTILISATEURS + "/" + parcourRecup.getM_nomConducteure() + REST_PARCOURS + "/" + parcourRecup.getM_idServiceWeb() + REST_DEMANDE_A + "/" + utilRecup.getM_nomUtilisateur(), null, null);
                strDemandeDeconn.add(unUri);
                Toast.makeText(this, "Votre demande sera envoyée dès que vous serez connecté à un réseau", Toast.LENGTH_SHORT).show();
            }catch(Exception e) {
                m_Exp = e;
            }
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

    /**********************************************************************************************
     * Gestion des cycles
     *********************************************************************************************/

    @Override
    public void onStart() {
        this.parcoursBd.open();
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();

        //fermeture de la connexion à la base de donnée.
        this.parcoursBd.close();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    /*****************************************************************************************
     // Classe interne permettant d'effectuer la tâche asynchrone d'envoyer une demande de parcours
     // ***************************************************************************************/
    private class SendDemandeListTask extends AsyncTask<Void, Void, ArrayList<String> > {
        Exception m_Exp;


        // Méthode exécutée ASYNChrone: la tâche en tant que telle.
        @Override
        protected ArrayList<String> doInBackground(Void... unused) {

            try {

                //création de l'uri et de le requete au service web.
                uri = new URI("http", WEB_SERVICE_URL, REST_UTILISATEURS + "/" + parcourRecup.getM_nomConducteure()  + REST_PARCOURS  + "/" + parcourRecup.getM_idServiceWeb() + REST_DEMANDE_A +  "/" + utilRecup.getM_nomUtilisateur(), null, null);
                HttpPut requetePut = new HttpPut(uri);
                m_ClientHttp.execute(requetePut);
            } catch (Exception e) {
                m_Exp = e;
            }
            return null;
        }

        // Méthode exécutée SYNChrone après l'exécution de la tâche asynchrone.
        // Elle reçoit en paramètre la valeur de retour de "doInBackground".
        @Override
        protected void onPostExecute(ArrayList<String> p_Surnom) {

            if (m_Exp == null) {
                btn.setText("demande envoyée avex succès");
            } else {
                Log.e(TAG, "Erreur lors de la récupération des personnes (GET)", m_Exp);

            }
        }
    }

    /**********************************************************************************************
     * classe asynchrone permettant de dessiner le trajet sur la carte.
     **********************************************************************************************/
    private class DindPathTrajet extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void ... unused) {
            //recupération de toutes les routes possible
            document = dessinTrajet.Doncument(new LatLng(lattitueDepart, longitudeDepart), new LatLng(lattitueArrive,longitudeArrive));
            return null;
        }

        /**
         * exécuté une fois la requête au service web fait.
         * @param unused
         */
        @Override
        protected void onPostExecute(Void unused) {

                //recupération des points à tracer sur laa carte.
                ArrayList<LatLng> directionPoint = dessinTrajet.getPath(document);
                PolylineOptions rectLine = new PolylineOptions().width(5).color(
                        Color.BLUE);

                //boucle pour parcourrant la liste de points afin de les ajouter au trajet
                for (int i = 0; i < directionPoint.size(); i++) {
                    rectLine.add(directionPoint.get(i));
                }

                //ajout des polyline reliant tous les points
                mMap.addPolyline(rectLine);
            }
        }

    /**
     * méthode permmettant d'afficher un popop
     */
    private void afficherPopop() {

        if(dialogConfirmation== null) {
            dialogConfirmation = new AlertDialog.Builder(this)
                    .setTitle(R.string.titrePopoKilo)
                    .setMessage(String.valueOf(round(parcourRecup.Distance(lattitueDepart, longitudeDepart, lattitueArrive, longitudeArrive))) + "km")
                    .setNegativeButton(android.R.string.cancel, null)
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
    }

    @Override
    public boolean onMarkerClick(final Marker marker) {
        return true;
    }


    /**
     * Méthode permettant le formatage d'un nombre de kilometre
     * @param d
     * @return
     */
    double round(double d)
    {
        DecimalFormat format = new DecimalFormat("#.##");
        return Double.valueOf(format.format(d));
    }


    /**
     * Méthode exécuté lors de la conexion au réseau. Une fois le reseau revenue, toutes les demande
     * non envoyées seronyt envoyer.
     */
    private BroadcastReceiver conn = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo wifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            NetworkInfo c3G = connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

            //vérification de la connexion reseau.
            if (wifi != null && wifi.isConnected() || c3G != null && c3G.isConnected()) {
                if (!strDemandeDeconn.isEmpty()){
                    for (int i = 0; i < strDemandeDeconn.size(); i++)
                    {
                        uri = strDemandeDeconn.get(i);

                        //envoie des demande au service web.
                        new SendDemandeListTask().execute((Void) null);
                    }
                }
            }
        }
    };
}