package com.dinfogarneau.cours03e.ecotrajet.data;

import java.io.Serializable;

/**
 * Created by Guillaume on 2015-10-23.
 */
public class ParcoursUtil implements Serializable {

    /**
     * Interface Serializable.
     */
    private static final long serialVersionUID = -6735796385394522140L;

    /**
     * Identifiant inconnu.
     */
    public static final int ID_NON_DEFINI = -1;

    //attrubuts des la classe Descente
    private int _idParcours;
    private String  nomUtilisateur;

    /**
     * Constructeur par défaut (sans paramètres).
     * Met un nom vide, l'âge à zéro et célibat.
     */
    public ParcoursUtil() {
        this(-1,"");
    }

    //constructeur d'initialisation
    public ParcoursUtil(int idParcours, String nomUtilisateur) {

        this._idParcours = idParcours;
        this.nomUtilisateur = nomUtilisateur;

    }

    public int get_idParcours() {
        return _idParcours;
    }

    public void set_idParcours(int _idParcours) {
        this._idParcours = _idParcours;
    }

    public String getNomUtilisateur() {
        return nomUtilisateur;
    }

    public void setNomUtilisateur(String nomUtilisateur) {
        this.nomUtilisateur = nomUtilisateur;
    }
}
