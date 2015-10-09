package com.dinfogarneau.cours03e.ecotrajet.DataSource;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.dinfogarneau.cours03e.ecotrajet.data.Parcours;
import com.dinfogarneau.cours03e.ecotrajet.sqlHelper.sqlDataSource;

import java.text.SimpleDateFormat;

/**
 * Created by Guillaume on 2015-10-02.
 */
public class ParcoursDataSource {

    // Constantes pour le nom de la table et la version de la BD.
    private final static int DB_VERSION = 1;

    private final static String TABLE_NAME_PARCOURS = "Pacours";

    // Constantes pour le noms des champs de la table Parcours.
    private static final String COL_ID = "_id";
    private static final String COL_NOM_PARCOUR = "nomParcour";
    private static final String COL_NB_PLACE = "nbPlaceDisponible";
    private static final String COL_DATE_PARCOUR = "dateParcour";
    private static final String COL_COUT_PERSONNE = "coutPersonne";
    private static final String COL_IDREGION = "idRegion";
    private static final String COL_COORDONNE_DEPART = "coordonneDepart";
    private static final String COL_COORDONNE_ARRIVE = "coordonneArrive";
    private static final String COL_NOM_UTIL = "nomConducteur";


    // Constantes pour les indices des champs dans la BD.
    private static final int IDX_ID = 0;
    private static final int IDX_NOM_PARCOUR = 1;
    private static final int IDX_NB_PLACE = 2;
    private static final int IDX_DATE_PARCOUR = 3;
    private static final int IDX_COUT_PERSONNE = 4;
    private static final int IDX_IDREGION = 5;
    private static final int IDX_COORDONNE_DEPART = 6;
    private static final int IDX_COORDONNE_ARRIVE = 7;
    private static final int IDX_NOM_UTIL = 8;

    private sqlDataSource m_HelperSite;
    private SQLiteDatabase m_Db;


    public ParcoursDataSource(Context context) {
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
     * Conversion d'un objet Parcours en ContentValues.
     */
    private static ContentValues faireLigne(Parcours unParcours) {

        final SimpleDateFormat dateParser = new SimpleDateFormat("yyyy-MM-ddTHH:mm:ss.SSS");
        ContentValues row = new ContentValues();
        row.put(COL_ID, unParcours.getM_idParcour());
        row.put(COL_NOM_PARCOUR, unParcours.getM_nomParcour());
        row.put(COL_NB_PLACE, unParcours.getM_nbPlaceDisponible());
        row.put(COL_DATE_PARCOUR, dateParser.format(unParcours.getM_dateParcours()));
        row.put(COL_COUT_PERSONNE, unParcours.getM_coutPersonne());
        row.put(COL_IDREGION, unParcours.getM_idRegion());
        row.put(COL_COORDONNE_DEPART, unParcours.getM_coordonneDeparts());
        row.put(COL_COORDONNE_ARRIVE, unParcours.getM_coordonneArrive());
        row.put(COL_NOM_UTIL, unParcours.getM_nomConducteur());
        return row;
    }

    /**
     * Conversion d'un ligne de la BD en objet Parcours.
     */
    private static Parcours lireLigneSite(Cursor cursor) {
        Parcours unParcours = new Parcours();
        unParcours.setM_idParcour(cursor.getInt(IDX_ID));
        unParcours.setM_nomParcour(cursor.getString(IDX_NOM_PARCOUR));
        unParcours.setM_nbPlaceDisponible(cursor.getInt(IDX_NB_PLACE));
        unParcours.setM_dateParcours((cursor.getString(IDX_DATE_PARCOUR)));
        unParcours.setM_coutPersonne((cursor.getDouble(IDX_COUT_PERSONNE)));
        unParcours.setM_idRegion(cursor.getInt(IDX_IDREGION));
        unParcours.setM_coordonneDeparts(cursor.getString(IDX_COORDONNE_DEPART));
        unParcours.setM_coordonneArrive(cursor.getString(IDX_COORDONNE_ARRIVE));
        unParcours.setM_nomConducteur(cursor.getString(IDX_NOM_UTIL));
        return unParcours;

    }

    /**
     * Insertion d'un objet Parcours dans la BD.
     */
    public int insert(Parcours unParcour) {
        ContentValues ligne = faireLigne(unParcour);
        int newId = (int) m_Db.insert(TABLE_NAME_PARCOURS, null, ligne);

        // Conservation de l'identifiant dans l'objet Personne qui en avait pas avant l'ajout.
        unParcour.setM_idParcour(newId);
        return newId;
    }

    /**
     * Mise à jour d'un objet Parcours dans la BD.
     */
    public void update(Parcours unParcour) {
        ContentValues row = faireLigne(unParcour);
        m_Db.update(TABLE_NAME_PARCOURS, row, COL_ID + "=" + unParcour.getM_idParcour(), null);
    }

    /**
     * Destruction d'un objet Parcours dans la BD.
     */
    public void delete(int id) {
        m_Db.delete(TABLE_NAME_PARCOURS, COL_ID + "=" + id, null);
    }

    /**
     * Permet d'obtenir le nom de la region administrative associé au parcours
     *
     * @param idRegion
     * @return
     */
    public String getRegionName(int idRegion) {

        String nomRegion;

        Cursor c = m_Db.rawQuery("SELECT nomRegion FROM RegionAdministrative WHERE _id = " + idRegion, null);
        c.moveToFirst();
        nomRegion = c.getString(c.getColumnIndex("nomRegion"));
        return nomRegion;
    }
}