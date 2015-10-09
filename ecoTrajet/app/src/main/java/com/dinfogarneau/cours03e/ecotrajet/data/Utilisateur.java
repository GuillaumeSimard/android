package com.dinfogarneau.cours03e.ecotrajet.data;

import java.io.Serializable;

/**
 * Created by Guillaume on 2015-10-02.
 */
public class Utilisateur implements Serializable {

    //attributs de la classe.
    private String m_nomUtilisateur;
    private String m_prenom;
    private String m_nom;
    private String m_noTelephone;
    private String m_courriel;
    private String m_motDePasse;
    private int m_idTypePassager;


    /*
        Constructeur par défaut
     */
    public Utilisateur(){
        this.m_nomUtilisateur = "";
        this.m_prenom = "";
        this.m_nom = "";
        this.m_noTelephone = "";
        this.m_courriel = "";
        this.m_motDePasse = "";
        this.m_idTypePassager = -1;
    }

    public Utilisateur(String nomUtil, String prenom, String nom, String noTel,
                       String courriel, String motPasse, int idType){
        this.m_nomUtilisateur = nomUtil;
        this.m_prenom = prenom;
        this.m_nom = nom;
        this.m_noTelephone = noTel;
        this.m_courriel = courriel;
        this.m_motDePasse = motPasse;
        this.m_idTypePassager = idType;
    }

    public String getM_nomUtilisateur() {
        return m_nomUtilisateur;
    }

    public void setM_nomUtilisateur(String m_nomUtilisateur) {
        this.m_nomUtilisateur = m_nomUtilisateur;
    }

    public String getM_prenom() {
        return m_prenom;
    }

    public void setM_prenom(String m_prenom) {
        this.m_prenom = m_prenom;
    }

    public String getM_nom() {
        return m_nom;
    }

    public void setM_nom(String m_nom) {
        this.m_nom = m_nom;
    }

    public String getM_noTelephone() {
        return m_noTelephone;
    }

    public void setM_noTelephone(String m_noTelephone) {
        this.m_noTelephone = m_noTelephone;
    }

    public String getM_courriel() {
        return m_courriel;
    }

    public void setM_courriel(String m_courriel) {
        this.m_courriel = m_courriel;
    }

    public String getM_motDePasse() {
        return m_motDePasse;
    }

    public void setM_motDePasse(String m_motDePasse) {
        this.m_motDePasse = m_motDePasse;
    }

    public int getM_idTypePassager() {
        return m_idTypePassager;
    }

    public void setM_idTypePassager(int m_idTypePassager) {
        this.m_idTypePassager = m_idTypePassager;
    }
}
