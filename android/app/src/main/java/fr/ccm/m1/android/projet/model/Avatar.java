package fr.ccm.m1.android.projet.model;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.ccm.m1.android.projet.BR;

public class Avatar extends BaseObservable {


    public Avatar() {}

    //region attribut
    private String nomUtilisateur;
    private String utilisateurId;
    private String derniereLocalisationId;
    private boolean enVoyage;
    private String voyageEnCoursId;
    private List<String> historiqueDesVoyagesId;
    private int tempsDuVoyage; // en minutes
    private int tempsSurUnTelephone; // en minutes
    private int frequenceCollecteLocalisation; //en minutes
    private int tempsTotalParcourru;
    private int tempsParcouruSurTelephone;
    private int nbVoyage;
    //endregion


    //region getters and setters


    public String getNomUtilisateur() {
        return nomUtilisateur;
    }

    public void setNomUtilisateur(String nomUtilisateur) {
        this.nomUtilisateur = nomUtilisateur;
    }

    public boolean isEnVoyage() {
        return enVoyage;
    }

    public void setEnVoyage(boolean enVoyage) {

        this.enVoyage = enVoyage;
    }

    public String getVoyageEnCoursId() {
        return voyageEnCoursId;
    }

    public void setVoyageEnCoursId(String voyageEnCoursId) {
        this.voyageEnCoursId = voyageEnCoursId;
    }

    public List<String> getHistoriqueDesVoyagesId() {
        return historiqueDesVoyagesId;
    }

    public void setHistoriqueDesVoyagesId(List<String> historiqueDesVoyagesId) {
        this.historiqueDesVoyagesId = historiqueDesVoyagesId;

    }

    public int getTempsDuVoyage() {
        return tempsDuVoyage;
    }

    public void setTempsDuVoyage(int tempsDuVoyage) {

        this.tempsDuVoyage = tempsDuVoyage;

    }

    public int getTempsSurUnTelephone() {
        return tempsSurUnTelephone;
    }

    public void setTempsSurUnTelephone(int tempsSurUnTelephone) {
        this.tempsSurUnTelephone = tempsSurUnTelephone;
    }

    public int getFrequenceCollecteLocalisation() {
        return frequenceCollecteLocalisation;
    }

    public void setFrequenceCollecteLocalisation(int frequenceCollecteLocalisation) {
        this.frequenceCollecteLocalisation = frequenceCollecteLocalisation;

    }

    public String getDerniereLocalisationId() {
        return derniereLocalisationId;
    }

    public void setDerniereLocalisationId(String derniereLocalisationId) {
        this.derniereLocalisationId = derniereLocalisationId;
    }


    public int getTempsTotalParcourru() {
        return tempsTotalParcourru;
    }

    public void setTempsTotalParcourru(int tempsTotalParcourru) {
        this.tempsTotalParcourru = tempsTotalParcourru;
    }

    public int getTempsParcouruSurTelephone() {
        return tempsParcouruSurTelephone;
    }

    public void setTempsParcouruSurTelephone(int tempsParcouruSurTelephone) {
        this.tempsParcouruSurTelephone = tempsParcouruSurTelephone;
    }

    public String getUtilisateurId() {
        return utilisateurId;
    }

    public void setUtilisateurId(String utilisateurId) {
        this.utilisateurId = utilisateurId;
    }

    public int getNbVoyage() {
        return nbVoyage;
    }

    public void setNbVoyage(int nbVoyage) {
        this.nbVoyage = nbVoyage;
    }



    //endregion
}
