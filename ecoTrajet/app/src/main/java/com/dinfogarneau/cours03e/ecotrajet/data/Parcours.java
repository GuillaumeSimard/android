package com.dinfogarneau.cours03e.ecotrajet.data;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Guillaume on 2015-10-02.
 */
public class Parcours implements Serializable {

    /**
     * Interface Serializable.
     */
    private static final long serialVersionUID = -6735796385394522140L;

    //attribut de la classe Parcours
    private int m_idParcour;
    private String m_nomParcour;
    private int m_nbPlaceDisponible;
    private String m_dateParcours;
    private double m_coutPersonne;
    private int m_idRegion;
    private String m_coordonneDeparts;
    private String m_coordonneArrive;
    private String m_nomConducteur;

    public Parcours()
    {
        this.m_idParcour = -1;
        this.m_nomParcour = "";
        this.m_nbPlaceDisponible = -1;
        this.m_dateParcours = "";
        this.m_coutPersonne = -1;
        this.m_idRegion = -1;
        this.m_coordonneDeparts = "";
        this.m_coordonneArrive = "";
        this.m_nomConducteur = "";
    }

    public Parcours(int id, String nom, int nbPlace, String date, double cout,
                    int idRegion,String depart, String arrive, String nomConducteur ){
        this.m_idParcour = id;
        this.m_nomParcour = nom;
        this.m_nbPlaceDisponible = nbPlace;
        this.m_dateParcours = date;
        this.m_coutPersonne = cout;
        this.m_idRegion = idRegion;
        this.m_coordonneDeparts = depart;
        this.m_coordonneArrive = arrive;
        this.m_nomConducteur = nomConducteur;
    }


    public int getM_idParcour() {
        return m_idParcour;
    }

    public void setM_idParcour(int m_idParcour) {
        this.m_idParcour = m_idParcour;
    }

    public String getM_nomParcour() {
        return m_nomParcour;
    }

    public void setM_nomParcour(String m_nomParcour) {
        this.m_nomParcour = m_nomParcour;
    }

    public int getM_nbPlaceDisponible() {
        return m_nbPlaceDisponible;
    }

    public void setM_nbPlaceDisponible(int m_nbPlaceDisponible) {
        this.m_nbPlaceDisponible = m_nbPlaceDisponible;
    }

    public String getM_dateParcours() {
        return m_dateParcours;
    }

    public void setM_dateParcours(String m_dateParcours) {
        this.m_dateParcours = m_dateParcours;
    }

    public double getM_coutPersonne() {
        return m_coutPersonne;
    }

    public void setM_coutPersonne(double m_coutPersonne) {
        this.m_coutPersonne = m_coutPersonne;
    }

    public int getM_idRegion() {
        return m_idRegion;
    }

    public void setM_idRegion(int m_idRegion) {
        this.m_idRegion = m_idRegion;
    }

    public String getM_coordonneDeparts() {
        return m_coordonneDeparts;
    }

    public void setM_coordonneDeparts(String m_coordonneDeparts) {
        this.m_coordonneDeparts = m_coordonneDeparts;
    }

    public String getM_coordonneArrive() {
        return m_coordonneArrive;
    }

    public void setM_coordonneArrive(String m_coordonneArrive) {
        this.m_coordonneArrive = m_coordonneArrive;
    }

    public String getM_nomConducteur() {
        return m_nomConducteur;
    }

    public void setM_nomConducteur(String m_nomConducteur) {
        this.m_nomConducteur = m_nomConducteur;
    }
}
