package com.dinfogarneau.cours03e.ecotrajet.drawMap;

import com.google.android.gms.maps.model.LatLng;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.InputStream;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/***************************************************************************************
 * Classe permettant de désinner le trajet sur la google map.
 **************************************************************************************/
public class mapDra{
    public mapDra() { }

    //Méthode qui récupère les uri et initianise les variables du protocole http.
    public Document Doncument(LatLng debut, LatLng fin) {

        //initialisation de l'url et des méthodes de http
        Document doc = null;
        String url = "http://maps.googleapis.com/maps/api/directions/xml?"
                + "origin=" + debut.latitude + "," + debut.longitude
                + "&destination=" + fin.latitude + "," + fin.longitude
                + "&sensor=false&units=metric&mode=driving";

        try {
            HttpClient Client = new DefaultHttpClient();
            HttpContext Context = new BasicHttpContext();
            HttpPost Post = new HttpPost(url);
            HttpResponse response = Client.execute(Post, Context);
            InputStream in = response.getEntity().getContent();
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
             doc = builder.parse(in);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return doc;
    }

    /**
     * Méthode permettant de trouver toutes les points afin de tracer un polygone.
     * @param doc
     * @return
     */
    public ArrayList<LatLng> getPath(Document doc) {

        //Déclaration des variable
        NodeList premierNoeux;
        NodeList DeuxièmeNoeu;
        NodeList TroisiemeNoeux;
        Node node1;
        double lattitue;
        double longitude;

        ArrayList<LatLng> geo = new ArrayList<LatLng>();
        premierNoeux = doc.getElementsByTagName("step");

        //vérification qu'il y a des premier noeux.
        if (premierNoeux.getLength() > 0) {

            //boucle sur chacun de ceux-si.
            for (int i = 0; i < premierNoeux.getLength(); i++) {
                node1 = premierNoeux.item(i);
                DeuxièmeNoeu = node1.getChildNodes();

                Node locationNode = DeuxièmeNoeu.item(getNodeIndex(DeuxièmeNoeu, "start_location"));
                TroisiemeNoeux = locationNode.getChildNodes();
                Node latNode = TroisiemeNoeux.item(getNodeIndex(TroisiemeNoeux, "lat"));
                lattitue = Double.parseDouble(latNode.getTextContent());
                Node lngNode = TroisiemeNoeux.item(getNodeIndex(TroisiemeNoeux, "lng"));
                longitude = Double.parseDouble(lngNode.getTextContent());
                geo.add(new LatLng(lattitue, longitude));

                locationNode = DeuxièmeNoeu.item(getNodeIndex(DeuxièmeNoeu, "polyline"));
                TroisiemeNoeux = locationNode.getChildNodes();
                latNode = TroisiemeNoeux.item(getNodeIndex(TroisiemeNoeux, "points"));
                ArrayList<LatLng> arr = decodePoly(latNode.getTextContent());

                //boucle permettant de parcourrir l'ensemble des points
                for(int j = 0 ; j < arr.size() ; j++) {
                    geo.add(new LatLng(arr.get(j).latitude, arr.get(j).longitude));
                }
            }
        }
        return geo;
    }

    private int getNodeIndex(NodeList nl, String nodename) {
        for(int i = 0 ; i < nl.getLength() ; i++) {
            if(nl.item(i).getNodeName().equals(nodename))
                return i;
        }
        return -1;
    }

    /****************************************************************************************
     * Méthode permettant de décoder et de determiner les points du polygone.
     * @param encoded
     * @return
     ***************************************************************************************/
    private ArrayList<LatLng> decodePoly(String encoded) {
        ArrayList<LatLng> poly = new ArrayList<LatLng>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;
        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;
            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng position = new LatLng((double) lat / 1E5, (double) lng / 1E5);
            poly.add(position);
        }
        return poly;
    }
}