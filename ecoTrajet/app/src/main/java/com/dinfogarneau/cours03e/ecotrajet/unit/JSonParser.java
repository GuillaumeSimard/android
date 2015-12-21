package com.dinfogarneau.cours03e.ecotrajet.unit;

import com.dinfogarneau.cours03e.ecotrajet.data.Message;
import com.dinfogarneau.cours03e.ecotrajet.data.Parcours;
import com.dinfogarneau.cours03e.ecotrajet.data.Utilisateur;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Guillaume on 2015-11-22.
 */
public class JSonParser {

    //utilisateurs
    public static final String UTIL_NOM = "_nomUtilisateur";
    public static final String UTIL_PRENOM = "prenom";
    public static final String UTIL_NOM_FAMILLE = "nom";
    public static final String UTIL_TELEPHONE = "noTelephone";
    public static final String UTIL_EMAIL = "email";
    public static final String UTIL_MOT_PASSE = "motDePasse";
    public static final String UTIL_TYPE_UTILISATEUR = "idTypePassager";
    public static final String U_LISTE_DEMANDE = "listeDemandeParcours";

    //Parcours
    public static final String PARCOUR_ID = "_id";
    public static final String PARCOUR_NOM = "nomParcour";
    public static final String PARCOUR_NOMS = "nomParcours";
    public static final String PARCOUR_NBPLACE_DISPONIBLE = "nbPlaceDisponible";
    public static final String PARCOUR_NBPLACE_PRISE = "nbPlacePrise";
    public static final String PARCOUR_DATE = "dateParcour";
    public static final String PARCOUR_HEURE = "heure";
    public static final String PARCOUR_COUT = "coutPersonne";
    public static final String PARCOUR_REGION = "idRegion";
    public static final String PARCOUR_COORDONNE_DEPART = "coordonneDepart";
    public static final String PARCOUR_COORDONNE_ARRIVE = "coordonneArrive";
    public static final String PARCOUR_CONDUCTEUR = "nomConducteur";
    public static final String PARCOUR_LISTE_PASSAGER = "listePassager";
    public static final String PARCOUR_PARCOURS_ID = "idParcours";


    public static Utilisateur deserialiserJsonUtilisateur(String utilisateurJson) throws JSONException {
        Utilisateur unUtilis ;
        // Désérialisation de l'expression JSON.
        JSONObject utiJson = new JSONObject(utilisateurJson);

        unUtilis= new Utilisateur(utiJson.getString(UTIL_NOM), utiJson.getString(UTIL_MOT_PASSE),utiJson.getInt(UTIL_TYPE_UTILISATEUR));
        JSONArray array = utiJson.getJSONArray(U_LISTE_DEMANDE);

        for (int i = 0; i < array.length(); i++)
            unUtilis.get_list_demande().add( array.getString(i));

        return unUtilis;
    }

    /**
     * Permet de sérialiser en JSON un objet Utilisateur.
     * @param u
     * @return
     * @throws JSONException
     */
    public static JSONObject serialiserJsonUtilisateur(Utilisateur u) throws JSONException {
        JSONObject jsonObj = new JSONObject();

        jsonObj.put(UTIL_NOM, u.getM_nomUtilisateur());
        jsonObj.put(UTIL_PRENOM, u.getM_prenom());
        jsonObj.put(UTIL_NOM_FAMILLE, u.getM_nom());
        jsonObj.put(UTIL_TELEPHONE, u.getM_noTelephone());
        jsonObj.put(UTIL_EMAIL, u.getM_courriel());
        jsonObj.put(UTIL_MOT_PASSE, u.getM_motDePasse());
        jsonObj.put(UTIL_TYPE_UTILISATEUR, u.getM_idTypePassager());

        return jsonObj;
    }

    /**
     *  Permet de sérialiser en JSON un objet Utilisateur.
     * @param ParcoursAdd
     * @return
     * @throws JSONException
     */
    public static JSONObject serialiserJsonParcours(Parcours ParcoursAdd) throws JSONException {
        JSONObject jsonObj = new JSONObject();

        //jsonObj.put(PARCOUR_ID, ParcoursAdd.getM_idServiceWeb());
        jsonObj.put(PARCOUR_NOM, ParcoursAdd.getM_nomParcour());
        jsonObj.put(PARCOUR_NBPLACE_DISPONIBLE, ParcoursAdd.getM_nbPlaceDisponible());
        jsonObj.put(PARCOUR_NBPLACE_PRISE, ParcoursAdd.getM_nbPlacePrise());
        jsonObj.put(PARCOUR_DATE, ParcoursAdd.getM_dateParcours());
        jsonObj.put(PARCOUR_HEURE, ParcoursAdd.getM_Heure());
        jsonObj.put(PARCOUR_COUT, ParcoursAdd.getM_coutPersonne());
        jsonObj.put(PARCOUR_REGION, ParcoursAdd.getM_idRegion());
        jsonObj.put(PARCOUR_COORDONNE_DEPART, ParcoursAdd.getM_coordonneDeparts());
        jsonObj.put(PARCOUR_COORDONNE_ARRIVE, ParcoursAdd.getM_coordonneArrive());


        return jsonObj;
    }

    /**
     * Méthode permettant de transformer le json d'une liste de Parcours en liste de Parcours
     * @param jsonParcour
     * @return
     * @throws JSONException
     */
    public static ArrayList<Parcours> deserialiserJsonListePat(String jsonParcour) throws JSONException {
        ArrayList<Parcours> lstParcours = new ArrayList<Parcours>();
        JSONArray array = new JSONArray(jsonParcour);

        //ajout des Parcours à la liste
        for(int i = 0; i < array.length(); i++){
            JSONObject ParcoursJson = array.getJSONObject(i);
            Parcours unParcours = new Parcours(
                    ParcoursJson.getString(PARCOUR_NOMS),
                    ParcoursJson.getInt(PARCOUR_NBPLACE_DISPONIBLE),
                    ParcoursJson.getInt(PARCOUR_NBPLACE_PRISE),
                    ParcoursJson.getString(PARCOUR_DATE),
                    ParcoursJson.getString(PARCOUR_HEURE),
                    ParcoursJson.getDouble(PARCOUR_COUT),
                    ParcoursJson.getInt(PARCOUR_REGION),
                    ParcoursJson.getString(PARCOUR_COORDONNE_DEPART),
                    ParcoursJson.getString(PARCOUR_COORDONNE_ARRIVE),
                    ParcoursJson.getString(PARCOUR_CONDUCTEUR)
            );

            unParcours.setM_idServiceWeb(ParcoursJson.getString(PARCOUR_PARCOURS_ID));
            List<String> listPoissons = new ArrayList<String>();
            JSONArray arrayPassager = ParcoursJson.getJSONArray(PARCOUR_LISTE_PASSAGER);

            for(int ipers = 0; ipers < arrayPassager.length(); ipers++){
                unParcours.getLstPassager().add((arrayPassager.getString(ipers)));
            }
            unParcours.setLstPassagers(listPoissons);
            lstParcours.add(unParcours);
        }

        return lstParcours;
    }

    /**
     * Méthode permettant de transformer le json d'une liste de Parcours en liste de Parcours
     * @param jsonParcour
     * @return
     * @throws JSONException
     */
    public static ArrayList<Parcours> deserialiserJsonListeParcours(String jsonParcour) throws JSONException {
        ArrayList<Parcours> lstParcours = new ArrayList<Parcours>();
        JSONArray array = new JSONArray(jsonParcour);
        ArrayList<String> lstNom = new ArrayList<String>();

        //ajout des Parcours à la liste
        for(int i = 0; i < array.length(); i++){
            JSONObject ParcoursJson = array.getJSONObject(i);
            Parcours unParcours = new Parcours(
                    ParcoursJson.getString(PARCOUR_NOMS),
                    ParcoursJson.getInt(PARCOUR_NBPLACE_DISPONIBLE),
                    ParcoursJson.getInt(PARCOUR_NBPLACE_PRISE),
                    ParcoursJson.getString(PARCOUR_DATE),
                    ParcoursJson.getString(PARCOUR_HEURE),
                    ParcoursJson.getDouble(PARCOUR_COUT),
                    ParcoursJson.getInt(PARCOUR_REGION),
                    ParcoursJson.getString(PARCOUR_COORDONNE_DEPART),
                    ParcoursJson.getString(PARCOUR_COORDONNE_ARRIVE),
                    ParcoursJson.getString(PARCOUR_CONDUCTEUR)
            );

            unParcours.setM_idServiceWeb(ParcoursJson.getString(PARCOUR_ID));
            List<String> listPoissons = new ArrayList<String>();
            JSONArray arrayPassager = ParcoursJson.getJSONArray(PARCOUR_LISTE_PASSAGER);

            for(int ipers = 0; ipers < arrayPassager.length(); ipers++){
                lstNom.add(arrayPassager.getString(ipers));
            }
            unParcours.setLstPassagers(lstNom);
            lstParcours.add(unParcours);
        }

        return lstParcours;
    }

    /**
     * Méthode permettant de transformer le json d'une liste de Parcours en liste de Parcours
     * @param jsonParcour
     * @return
     * @throws JSONException
     */
    public static ArrayList<Parcours> deserialiserJsonListePa(String jsonParcour) throws JSONException {
        ArrayList<Parcours> lstParcours = new ArrayList<Parcours>();
        JSONArray array = new JSONArray(jsonParcour);

        //ajout des Parcours à la liste
        for(int i = 0; i < array.length(); i++){
            JSONObject ParcoursJson = array.getJSONObject(i);
            Parcours unParcours = new Parcours( ParcoursJson.getString("_id"),
                    ParcoursJson.getString(PARCOUR_NOMS),
                    ParcoursJson.getInt(PARCOUR_NBPLACE_DISPONIBLE),
                    ParcoursJson.getInt(PARCOUR_NBPLACE_PRISE),
                    ParcoursJson.getString(PARCOUR_DATE),
                    ParcoursJson.getString(PARCOUR_HEURE),
                    ParcoursJson.getDouble(PARCOUR_COUT),
                    ParcoursJson.getInt(PARCOUR_REGION),
                    ParcoursJson.getString(PARCOUR_COORDONNE_DEPART),
                    ParcoursJson.getString(PARCOUR_COORDONNE_ARRIVE),
                    ParcoursJson.getString(PARCOUR_CONDUCTEUR)
            );

            unParcours.setM_idServiceWeb(ParcoursJson.getString(PARCOUR_ID));
            unParcours.setM_nomConducteure(ParcoursJson.getString(PARCOUR_CONDUCTEUR));
            List<String> listPassager = new ArrayList<String>();
            JSONArray arrayPassager = ParcoursJson.getJSONArray(PARCOUR_LISTE_PASSAGER);

            for(int ipers = 0; ipers < arrayPassager.length(); ipers++){
                listPassager.add((arrayPassager.getString(ipers)));
            }
            unParcours.setLstPassagers(listPassager);
            lstParcours.add(unParcours);
        }

        return lstParcours;
    }


    /**
     * Méthode permettant de transformer le json d'une liste de demandes en liste de String
     * @param jSonDemande
     * @return
     * @throws JSONException
     */
    //methode permettant de retourner une list de string qui sont les amis d'une personne
    public static ArrayList<String> deserialiserJsonListeDemande(String jSonDemande) throws JSONException{
        ArrayList<String> listDemande = new ArrayList<String>();
        JSONArray array = new JSONArray(jSonDemande);

        //permet d'ajouter les nom à la liste
        for(int i = 0; i < array.length(); i++){
            //JSONObject amiJson = array.getJSONObject(i);
            listDemande.add(array.getString(i));
        }

        return listDemande;
    }

    /**
     * Méthode permettant de transformer le json d'une demande en Message.
     * @param body
     * @param nomConducteur
     * @return
     * @throws JSONException
     */
    public static Message parseMessage(String body, String nomConducteur)throws JSONException{
        Message message = null;

        JSONArray array = new JSONArray(body);
        String[] infoDemande = new String[2];
        String nomPassager, idParcour;

        //permet d'ajouter les nom à la liste
        for(int i = 0; i < array.length(); i++){
            infoDemande = array.getString(i).split(";");
            idParcour = infoDemande[0];
            nomPassager = infoDemande[1];
            message = new Message(nomPassager,idParcour,nomConducteur);
        }
        return message;
    }

}
