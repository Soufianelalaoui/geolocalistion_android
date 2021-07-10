package fr.ccm.m1.android.projet.model;

import android.location.Location;
import java.util.List;

public class Localisation {

    //region attribut

    private String localisationId;
    private String referenceUtilisateurId;
    private String date;
    private double longitude;
    private double latitude;
    private String adresseDetail;
    //endregion

    //region getters and setters


    public String getLocalisationId() {
        return localisationId;
    }

    public void setLocalisationId(String localisationId) {
        this.localisationId = localisationId;
    }

    public String getReferenceUtilisateurId() {
        return referenceUtilisateurId;
    }

    public void setReferenceUtilisateurId(String referenceUtilisateurId) {
        this.referenceUtilisateurId = referenceUtilisateurId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public String getAdresseDetail() {
        return adresseDetail;
    }

    public void setAdresseDetail(String adresseDetail) {
        this.adresseDetail = adresseDetail;
    }

    //endregion


    public Localisation localisationDuTelephoneLePlusProche(List<Localisation> localisationList){
        Localisation res = null;
        Float distanceLaPlusPetite = null;
        Location maLocalisation = new Location("myLocation");
        maLocalisation.setLatitude(getLatitude());
        maLocalisation.setLongitude(getLongitude());
        Location tempLocation = new Location("tempLocation");
        for (Localisation localisation : localisationList){
            if (!localisation.getLocalisationId().equals(getLocalisationId())){
                tempLocation.setLatitude(localisation.getLatitude());
                tempLocation.setLongitude(localisation.getLongitude());
                if(distanceLaPlusPetite == null || maLocalisation.distanceTo(tempLocation) < distanceLaPlusPetite){
                    res = localisation;
                    distanceLaPlusPetite = maLocalisation.distanceTo(tempLocation);
                }
            }

        }
        return res;

    }

}
