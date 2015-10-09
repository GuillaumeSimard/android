package com.dinfogarneau.cours03e.ecotrajet.sqlHelper;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


/**
 * Created by Guillaume on 2015-03-03.
 */
public class sqlDataSource  extends SQLiteOpenHelper {

    public sqlDataSource(Context context) {
        super(context, "site.sqlite", null, DB_VERSION);
    }

    // Constantes pour le nom de la table et la version de la BD.
    private final static int DB_VERSION = 1;
    private final static String TABLE_NAME_REGION_ADMINISTRATIVE = "RegionAdministrative";
    private final static String TABLE_NAME_UTILISATEURS  = "Utilisateura";
    private final static String TABLE_NAME_PARCOURS  = "Parcours";
    private final static String TABLE_NAME_TYPE_UTILISATEUR  = "TypeUtilisateur";

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "create table " + TABLE_NAME_REGION_ADMINISTRATIVE
                        + "(_id integer primary key autoincrement,"
                        +"nomRegion text)");

        db.execSQL("create table " + TABLE_NAME_TYPE_UTILISATEUR
                        + "(_id integer primary key autoincrement, "
                        + "nomType text)");


        db.execSQL( "create table " + TABLE_NAME_UTILISATEURS
                + "(_nomUtilisateur text primary key,"
                        + "prenom text, nom text, noTelephone text,"
                        +" email text, motDePasse text, idTypePassager text,"
                        + "foreign key (idTypePassager) REFERENCES "  + TABLE_NAME_TYPE_UTILISATEUR +  "(_id))");

        db.execSQL( "create table " + TABLE_NAME_PARCOURS
                + " (_id integer primary key autoincrement,"
                + "nomParcour text, nbPlaceDisponible integer, dateParcour string,"
                + "coutPersonne double, idRegion integer, coordonneDepart text,"
                + "coordonneArrive text, nomConducteur text, "
                + "foreign key (idRegion) REFERENCES" + TABLE_NAME_REGION_ADMINISTRATIVE + "(_id),"
                +  "foreign key (nomConducteur) REFERENCES" + TABLE_NAME_UTILISATEURS + "(_nomUtilisateur))");



        //insertion dans la table RegionAdministrative
        db.execSQL("INSERT INTO RegionAdministrative VALUES (1,'Bas-Saint-Laurent')");
        db.execSQL("INSERT INTO RegionAdministrative VALUES (2,'Saguenay Lac-St-Jean')");
        db.execSQL("INSERT INTO RegionAdministrative VALUES (3,'Capitale Nationnale')");
        db.execSQL("INSERT INTO RegionAdministrative VALUES (4,'Mauricie')");
        db.execSQL("INSERT INTO RegionAdministrative VALUES (5,'Estrie')");
        db.execSQL("INSERT INTO RegionAdministrative VALUES (6,'Montréal')");
        db.execSQL("INSERT INTO RegionAdministrative VALUES (7,'Outaouais')");
        db.execSQL("INSERT INTO RegionAdministrative VALUES (8,'Abitibi-Témiscamingue')");
        db.execSQL("INSERT INTO RegionAdministrative VALUES (9,'Côte-Nord')");
        db.execSQL("INSERT INTO RegionAdministrative VALUES (10,'Nord-du-Québec')");
        db.execSQL("INSERT INTO RegionAdministrative VALUES (11,'Gaspésie-Îles-de-la-Madelaine')");
        db.execSQL("INSERT INTO RegionAdministrative VALUES (12,'Chaudière-Appalaches')");
        db.execSQL("INSERT INTO RegionAdministrative VALUES (13,'Laval')");
        db.execSQL("INSERT INTO RegionAdministrative VALUES (14,'Lanaudière')");
        db.execSQL("INSERT INTO RegionAdministrative VALUES (15,'Laurentides')");
        db.execSQL("INSERT INTO RegionAdministrative VALUES (16,'Montérigie')");
        db.execSQL("INSERT INTO RegionAdministrative VALUES (17,'Centre-du-Québec')");

    }

    /*
        Lors de la mise à jour de la BD; installation d'une nouvelle version
        de l'application et de la BD.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion,
                          int newVersion) {
        db.execSQL("drop table if exists " + TABLE_NAME_PARCOURS);
        db.execSQL("drop table if exists " + TABLE_NAME_UTILISATEURS);
        db.execSQL("drop table if exists " + TABLE_NAME_TYPE_UTILISATEUR);
        db.execSQL("drop table if exists " + TABLE_NAME_REGION_ADMINISTRATIVE);
        this.onCreate(db);
    }
}
