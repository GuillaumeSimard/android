package com.dinfogarneau.cours03e.ecotrajet.data;

/**
 * Created by Remy Huot on 2015-12-18.
 */
public class Message {

    //Attributs de la classe
    private String m_conducteur;
    private String m_idParcours;
    private String m_Passager;

    //constructeur de la classe Message
    public Message(String nomConducteur, String nomPassager, String idParcours)
    {
        this.m_conducteur = nomConducteur;
        this.m_Passager = nomPassager;
        this.m_idParcours = idParcours;
    }


    public String getM_conducteur() {
        return m_conducteur;
    }

    public void setM_conducteur(String m_conducteur) {
        this.m_conducteur = m_conducteur;
    }

    public String getM_idParcours() {
        return m_idParcours;
    }

    public void setM_idParcours(String m_idParcours) {
        this.m_idParcours = m_idParcours;
    }

    public String getM_Passager() {
        return m_Passager;
    }

    public void setM_Passager(String m_Passager) {
        this.m_Passager = m_Passager;
    }
}
