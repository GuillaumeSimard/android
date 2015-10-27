package com.dinfogarneau.cours03e.ecotrajet.DataSource;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.dinfogarneau.cours03e.ecotrajet.data.Parcours;
import com.dinfogarneau.cours03e.ecotrajet.sqlHelper.sqlDataSource;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Guillaume on 2015-10-02.
 */
public class ParcoursDataSource {

    // Constantes pour le nom de la table et la version de la BD.
    private final static int DB_VERSION = 1;

    private final static String TABLE_NAME_PARCOURS = "Parcours";

    // Constantes pour le noms des champs de la table Parcours.
    private static final String COL_ID = "_id";
    private static final String COL_NOM_PARCOUR = "nomParcour";
    private static final String COL_NB_PLACE = "nbPlaceDisponible";
    private static final String COL_NB_PLACE_PRISE = "nbPlacePrise";
    private static final String COL_DATE_PARCOUR = "dateParcour";
    private static final String COL_HEURE_PARCOUR = "heure";
    private static final String COL_COUT_PERSONNE = "coutPersonne";
    private static final String COL_IDREGION = "idRegion";
    private static final String COL_COORDONNE_DEPART = "coordonneDepart";
    private static final String COL_COORDONNE_ARRIVE = "coordonneArrive";


    // Constantes pour les indices des champs dans la BD.
    private static final int IDX_ID = 0;
    private static final int IDX_NOM_PARCOUR = 1;
    private static final int IDX_NB_PLACE = 2;
    private static final int IDX_NB_PLACE_PRICE = 3;
    private static final int IDX_DATE_PARCOUR = 4;
    private static final int IDX_HEURE_PARCOURS = 5;
    private static final int IDX_COUT_PERSONNE = 6;
    private static final int IDX_IDREGION = 7;
    private static final int IDX_COORDONNE_DEPART = 8;
    private static final int IDX_COORDONNE_ARRIVE = 9;

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

        ContentValues row = new ContentValues();
        row.put(COL_ID, unParcours.getM_idParcour());
        row.put(COL_NOM_PARCOUR, unParcours.getM_nomParcour());
        row.put(COL_NB_PLACE, unParcours.getM_nbPlaceDisponible());
        row.put(COL_NB_PLACE_PRISE, unParcours.getM_nbPlacePrise());
        row.put(COL_DATE_PARCOUR, String.valueOf(unParcours.getM_dateParcours()));
        row.put(COL_HEURE_PARCOUR, unParcours.getM_Heure());
        row.put(COL_COUT_PERSONNE, unParcours.getM_coutPersonne());
        row.put(COL_IDREGION, unParcours.getM_idRegion());
        row.put(COL_COORDONNE_DEPART, unParcours.getM_coordonneDeparts());
        row.put(COL_COORDONNE_ARRIVE, unParcours.getM_coordonneArrive());
        return row;
    }

    /**
     * Conversion d'un ligne de la BD en objet Parcours.
     */
    private static Parcours lireLigneSite(Cursor cursor) {
        Parcours unParcours = new Parcours();
        unParcours.setM_nomParcour(cursor.getString(IDX_NOM_PARCOUR));
        unParcours.setM_nbPlaceDisponible(cursor.getInt(IDX_NB_PLACE));
        unParcours.setM_nbPlacePrise(cursor.getInt(IDX_NB_PLACE_PRICE));
        unParcours.setM_dateParcours((cursor.getString(IDX_DATE_PARCOUR)));
        unParcours.setM_Heure(cursor.getString(IDX_HEURE_PARCOURS));
        unParcours.setM_coutPersonne((cursor.getDouble(IDX_COUT_PERSONNE)));
        unParcours.setM_idRegion(cursor.getInt(IDX_IDREGION));
        unParcours.setM_coordonneDeparts(cursor.getString(IDX_COORDONNE_DEPART));
        unParcours.setM_coordonneArrive(cursor.getString(IDX_COORDONNE_ARRIVE));
        return unParcours;

    }

    /**
     * Insertion d'un objet Parcours dans la BD.
     */
    public void insert(Parcours unParcour) {
        ContentValues ligne = faireLigne(unParcour);
         m_Db.insert(TABLE_NAME_PARCOURS, null, ligne);

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

    public ArrayList<Parcours> getAllParcoursConduc(String nom)
    {
        ArrayList<Parcours> list = new ArrayList<Parcours>();

        Cursor c = m_Db.rawQuery("SELECT _id,nomParcour,nbPlaceDisponible,nbPlacePrise,dateParcour,heure,coutPersonne,idRegion,coordonneDepart,coordonneArrive FROM Parcours INNER JOIN ParcoursUtilisateur ON Parcours._id = ParcoursUtilisateur._idParcours  WHERE ParcoursUtilisateur.nomUtil = '" + nom + "'", null);

        c.moveToFirst();

        while (!c.isAfterLast())
        {
            list.add(new Parcours(c.getInt(c.getColumnIndex("_id")),c.getString(c.getColumnIndex("nomParcour")),c.getInt(c.getColumnIndex("nbPlaceDisponible")),
                                  c.getInt(c.getColumnIndex("nbPlacePrise")),c.getString(c.getColumnIndex("dateParcour")),c.getString(c.getColumnIndex("heure")),c.getDouble(c.getColumnIndex("coutPersonne")),c.getInt(c.getColumnIndex("idRegion")),
                                  c.getString(c.getColumnIndex("coordonneDepart")),c.getString(c.getColumnIndex("coordonneArrive"))));

            c.moveToNext();
        }

        return list;
    }

    public ArrayList<Parcours> getAllParcours()
    {
        ArrayList<Parcours> list = new ArrayList<Parcours>();

        Cursor c = m_Db.rawQuery("SELECT _id,nomParcour,nbPlaceDisponible,nbPlacePrise, dateParcour,heure,coutPersonne,idRegion,coordonneDepart,coordonneArrive FROM Parcours", null);

        c.moveToFirst();

        while (!c.isAfterLast())
        {
            list.add(new Parcours(c.getInt(c.getColumnIndex("_id")),c.getString(c.getColumnIndex("nomParcour")),c.getInt(c.getColumnIndex("nbPlaceDisponible")),
                    c.getInt(c.getColumnIndex("nbPlacePrise")),c.getString(c.getColumnIndex("dateParcour")),c.getString(c.getColumnIndex("heure")),c.getDouble(c.getColumnIndex("coutPersonne")),c.getInt(c.getColumnIndex("idRegion")),
                    c.getString(c.getColumnIndex("coordonneDepart")),c.getString(c.getColumnIndex("coordonneArrive"))));

            c.moveToNext();
        }

        return list;
    }

    public ArrayList<String> getAllRegion()
    {
        ArrayList<String> lstRegion = new ArrayList<String>();

        Cursor c = m_Db.rawQuery("SELECT nomRegion  FROM RegionAdministrative", null);
        c.moveToFirst();

        while(!c.isAfterLast())
        {
            lstRegion.add(c.getString(c.getColumnIndex("nomRegion")));
            c.moveToNext();
        }
        return lstRegion;
    }

    public int LstId()
    {
        int iNombre = 0;
        Cursor c = m_Db.rawQuery("SELECT _id FROM Parcours ORDER BY  _id ASC limit 1", null);

        if(c.getCount() == 0)
        {
            iNombre = 0;
        }
        else
        {
            c.moveToFirst();
            iNombre = c.getInt(c.getColumnIndex("_id"));
        }


        return iNombre;
    }


}
