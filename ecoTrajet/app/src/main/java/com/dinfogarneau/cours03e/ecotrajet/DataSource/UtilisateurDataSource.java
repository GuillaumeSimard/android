package com.dinfogarneau.cours03e.ecotrajet.DataSource;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.dinfogarneau.cours03e.ecotrajet.data.Utilisateur;
import com.dinfogarneau.cours03e.ecotrajet.sqlHelper.sqlDataSource;

import java.util.ArrayList;

/**
 * Created by Guillaume on 2015-10-09.
 */
public class UtilisateurDataSource {

    private final static String TABLE_NAME_UTILISATEUR = "Utilisateurs";

    // Constantes pour le noms des champs de la table Utilisateur.
    private static final String COL_NOM_UTIL = "_nomUtilisateur";
    private static final String COL_PRENOM = "prenom";
    private static final String COL_NOM = "nom";
    private static final String COL_TELEPHONE = "noTelephone";
    private static final String COL_COURRIEL = "email";
    private static final String COL_MOTPASSE = "motDePasse";
    private static final String COL_TYPEUTILISATEUR = "idTypePassager";


    // Constantes pour les indices des champs dans la BD.
    private static final int IDX_NOMUTIL = 0;
    private static final int IDX_PRENOM = 1;
    private static final int IDX_NOM = 2;
    private static final int IDX_TELEPHONE = 3;
    private static final int IDX_COURRIEL = 4;
    private static final int IDX_MOTPASSE = 5;
    private static final int IDX_TYPEUTILISATEUR  = 6;

    private sqlDataSource m_HelperSite;
    private SQLiteDatabase m_Db;


    public UtilisateurDataSource(Context context) {
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
    private static ContentValues faireLigne(Utilisateur util) {

        ContentValues row = new ContentValues();
        row.put(COL_NOM_UTIL, util.getM_nomUtilisateur());
        row.put(COL_PRENOM, util.getM_prenom());
        row.put(COL_NOM, util.getM_nom());
        row.put(COL_TELEPHONE,util.getM_noTelephone());
        row.put(COL_COURRIEL, util.getM_courriel());
        row.put(COL_MOTPASSE, util.getM_motDePasse());
        row.put(COL_TYPEUTILISATEUR, util.getM_idTypePassager());
        return row;
    }

    /**
     * Conversion d'un ligne de la BD en objet Parcours.
     */
    private static Utilisateur lireLigneSite(Cursor cursor) {
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setM_nomUtilisateur(cursor.getString(IDX_NOMUTIL));
        utilisateur.setM_prenom(cursor.getString(IDX_PRENOM));
        utilisateur.setM_nom(cursor.getString(IDX_NOM));
        utilisateur.setM_noTelephone(cursor.getString(IDX_TELEPHONE));
        utilisateur.setM_courriel(cursor.getString(IDX_COURRIEL));
        utilisateur.setM_motDePasse(cursor.getString(IDX_MOTPASSE));
        utilisateur.setM_idTypePassager(cursor.getInt(IDX_TYPEUTILISATEUR));
        return utilisateur;
    }

    /**
     * Insertion d'un objet Parcours dans la BD.
     */
    public void insert(Utilisateur util) {
        ContentValues ligne = faireLigne(util);
         m_Db.insert(TABLE_NAME_UTILISATEUR, null, ligne);

    }

    /**
     * Destruction d'un objet Parcours dans la BD.
     */
    public void delete(String nomUtil) {
        m_Db.delete(TABLE_NAME_UTILISATEUR, COL_NOM_UTIL + "=" + nomUtil, null);
    }

    public ArrayList<String> getAllType()
    {
        ArrayList<String> lstType = new ArrayList<String>();

        Cursor c = m_Db.rawQuery("SELECT nomType FROM TypeUtilisateur", null);
        c.moveToFirst();
        while(!c.isAfterLast())
        {
            lstType.add(c.getString(c.getColumnIndex("nomType")));
            c.moveToNext();
        }
        return lstType;
    }
    /**
     * Méthode permettant de récuperer un utilisateur lors de la connexion.
     * @param nom_util
     * @return
     */
    public Utilisateur RecupUtilisateur(String nom_util) {
        Utilisateur utilConnect = null;
        Cursor c = m_Db.rawQuery("SELECT _nomUtilisateur, prenom,nom, noTelephone, email, motDePasse, idTypePassager FROM Utilisateurs WHERE  _nomUtilisateur =  '"+ nom_util + "'", null);
        if(c.getCount() > 0){
            c.moveToFirst();
            utilConnect = lireLigneSite(c);
        }
        return utilConnect;
    }


}
