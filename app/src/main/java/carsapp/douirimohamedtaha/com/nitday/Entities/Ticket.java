package carsapp.douirimohamedtaha.com.nitday.Entities;

/**
 * Created by PC on 08/04/2018.
 */

public class Ticket {
    String date;
    String etat;
    String pic;
    String utilisateur;
    String agent;
    String description;
    double lat;
    double longi;
    public Ticket() {
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getEtat() {
        return etat;
    }

    public void setEtat(String etat) {
        this.etat = etat;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public String getUtilisateur() {
        return utilisateur;
    }

    public void setUtilisateur(String utilisateur) {
        this.utilisateur = utilisateur;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLongi() {
        return longi;
    }

    public void setLongi(double longi) {
        this.longi = longi;
    }

    public String getAgent() {
        return agent;
    }

    public void setAgent(String agent) {
        this.agent = agent;
    }

    @Override
    public String toString() {
        return "Ticket{" +
                "date='" + date + '\'' +
                ", etat='" + etat + '\'' +
                ", pic='" + pic + '\'' +
                ", utilisateur='" + utilisateur + '\'' +
                ", agent='" + agent + '\'' +
                ", description='" + description + '\'' +
                ", lat=" + lat +
                ", longi=" + longi +
                '}';
    }
}
