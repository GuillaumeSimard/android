package com.dinfogarneau.cours03e.ecotrajet.DataSource;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.dinfogarneau.cours03e.ecotrajet.data.ParcoursUtil;
import com.dinfogarneau.cours03e.ecotrajet.data.Utilisateur;
import com.dinfogarneau.cours03e.ecotrajet.sqlHelper.sqlDataSource;

/**
 * Created by Guillaume on 2015-10-23.
 */
public class ParcourPassagerDataSource {
    // Constantes pour le nom de la table et la version de la BD.
    private final static int DB_VERSION = 1;

    private final static String TABLE_NAME_PARCOURS_UTIL = "ParcoursUtilisateur";

    // Constantes pour le noms des champs de la table Parcours.
    private static final String COL_ID_PARCOUR = "_idParcours";
    private static final String COL_NOM_UTIL = "nomUtil";


    // Constantes pour les indices des champs dans la BD.
    private static final int IDX_ID_PARCOURS = 0;
    private static final int IDX_NOM_UTIL = 1;

    private sqlDataSource m_HelperSite;
    private SQLiteDatabase m_Db;

    public ParcourPassagerDataSource(Context context) {
        m_HelperSite = new sqlDataSource(context);
    }

    /**
     * Ouverture de la connexion à la BD.
     */
    public void open() {
        m_Db = this.m_HelperSite.getWritableDatabase();
    }

    /**
     * Fermeture de la connexion à la BD.
     */
    public void close() {
        m_Db.close();
    }

    /**
     * Insertion d'un objet Personne dans la BD.
     */
    public int insert(ParcoursUtil parcoursUtil) {
        ContentValues ligne = faireLignePoisonsSite(parcoursUtil);
        int newId = (int) m_Db.insert(TABLE_NAME_PARCOURS_UTIL, null, ligne);
        return newId;
    }

    /**
     * Conversion d'un objet Personne en ContentValues.
     */
    private static ContentValues faireLignePoisonsSite(ParcoursUtil unParcoursUtil) {
        ContentValues row = new ContentValues();
        row.put(COL_ID_PARCOUR, unParcoursUtil.get_idParcours());
        row.put(COL_NOM_UTIL, unParcoursUtil.getNomUtilisateur());
        return row;
    }

    /**
     * Destruction d'un objet Personne dans la BD.
     */
    public void delete(int idParcours, String nomUtil) {
        m_Db.delete(TABLE_NAME_PARCOURS_UTIL, COL_ID_PARCOUR + "=" + idParcours + " AND " + COL_NOM_UTIL + " = '" + nomUtil + "'", null);
    }

    /**
     * Conversion d'un ligne de la BD en objet Personne.
     */
    private static ParcoursUtil lireLigneParcoursUtili(Cursor cursor) {
        ParcoursUtil parcoursUtil = new ParcoursUtil();

        parcoursUtil.set_idParcours(cursor.getInt(IDX_ID_PARCOURS));
        parcoursUtil.setNomUtilisateur(cursor.getString(IDX_NOM_UTIL));
        return parcoursUtil;
    }

    public String findParcourConduc(int id){

        String nom = null;
        Cursor c = m_Db.rawQuery("Select Utilisateurs._nomUtilisateur " +
                                "FROM Utilisateurs INNER JOIN ParcoursUtilisateur ON" +
                                " Utilisateurs._nomUtilisateur = ParcoursUtilisateur.nomUtil"+
                                " WHERE ParcoursUtilisateur._idParcours = " + id +
                                " AND Utilisateurs.idTypePassager = 1",null);

        if(c.getCount() > 0)
        {
            c.moveToFirst();
            nom = c.getString(c.getColumnIndex("Utilisateurs._nomUtilisateur"));
        }
        return nom;
    }

}
