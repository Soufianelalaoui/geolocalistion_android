package fr.ccm.m1.android.projet.model;

import java.util.List;

public class Voyage{

    //region attribut
    private String numero;
    private String voyageId;
    private List<String> trajet;

    //endregion

    //region getters and setters


    public String getVoyageId() {
        return voyageId;
    }

    public void setVoyageId(String voyageId) {
        this.voyageId = voyageId;
    }

    public List<String> getTrajet() {
        return trajet;
    }

    public void setTrajet(List<String> trajet) {
        this.trajet = trajet;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    //endregion
}
