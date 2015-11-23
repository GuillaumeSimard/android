package com.dinfogarneau.cours03e.ecotrajet.unit;

import com.dinfogarneau.cours03e.ecotrajet.data.Utilisateur;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Guillaume on 2015-11-22.
 */
public class JSonParser {

    public static final String UTIL_NOM = "_nomUtilisateur";
    public static final String UTIL_PRENOM = "prenom";
    public static final String UTIL_NOM_FAMILLE = "nom";
    public static final String UTIL_TELEPHONE = "noTelephone";
    public static final String UTIL_EMAIL = "email";
    public static final String UTIL_MOT_PASSE = "motDePasse";
    public static final String UTIL_TYPE_UTILISATEUR = "idTypePassager";
    public static final String U_LISTE_DEMANDE = "listedemande";

    public static Utilisateur deserialiserJsonUtilisateur(String utilisateurJson) throws JSONException {
        Utilisateur unUtilis ;
        // Désérialisation de l'expression JSON.
        JSONObject utiJson = new JSONObject(utilisateurJson);

        unUtilis= new Utilisateur(utiJson.getString(UTIL_NOM), utiJson.getString(UTIL_PRENOM), utiJson.getString(UTIL_NOM_FAMILLE),utiJson.getString(UTIL_TELEPHONE),utiJson.getString(UTIL_EMAIL), utiJson.getString(UTIL_MOT_PASSE),utiJson.getInt( UTIL_TYPE_UTILISATEUR));
        JSONArray array = utiJson.getJSONArray(U_LISTE_DEMANDE);

        for (int i = 0; i < array.length(); i++)
            unUtilis.get_list_demande().add( array.getString(i));

        return unUtilis;
    }

    // Permet de sérialiser en JSON un objet Utilisateur.
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
}
