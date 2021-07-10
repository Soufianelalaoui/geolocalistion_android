package fr.ccm.m1.android.projet.model;

import java.util.List;

public class Utilisateur {

    // region attributs
    private String nom;
    private String derniereLocalisationId;
    private List<String> avatarInviteListe;
    private int frequenceUpdateLocalisation;
    // endregion

    //region methodes

    //endregion


    //region getters and setters

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getDerniereLocalisationId() {
        return derniereLocalisationId;
    }

    public void setDerniereLocalisationId(String derniereLocalisationId) {
        this.derniereLocalisationId = derniereLocalisationId;
    }

    public List<String> getAvatarInviteListe() {
        return avatarInviteListe;
    }

    public void setAvatarInviteListe(List<String> avatarInviteListe) {
        this.avatarInviteListe = avatarInviteListe;
    }

    public int getFrequenceUpdateLocalisation() {
        return frequenceUpdateLocalisation;
    }

    public void setFrequenceUpdateLocalisation(int frequenceUpdateLocalisation) {
        this.frequenceUpdateLocalisation = frequenceUpdateLocalisation;
    }

    //endregion



}
