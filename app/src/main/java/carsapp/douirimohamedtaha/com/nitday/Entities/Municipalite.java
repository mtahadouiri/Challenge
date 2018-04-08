package carsapp.douirimohamedtaha.com.nitday.Entities;

import carsapp.douirimohamedtaha.com.nitday.Entities.Enum.EstabType;

public class Municipalite {

    private String name;
    private String address;
    private double lat;
    private double longi;
    private EstabType type;
    private int number;
    private String mail;
    private String heureFin;
    private String heureDebut;
    private String tarif;
    private int capacite;


    public Municipalite() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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

    public EstabType getType() {
        return type;
    }

    public void setType(EstabType type) {
        this.type = type;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public void setHeureFin(String heureFin) {
        this.heureFin = heureFin;
    }

    public String getHeureFin() {
        return heureFin;
    }

    public String getHeureDebut() {
        return heureDebut;
    }

    public void setHeureDebut(String heureDebut) {
        this.heureDebut = heureDebut;
    }

    public String getTarif() {
        return tarif;
    }

    public void setTarif(String tarif) {
        this.tarif = tarif;
    }

    public int getCapacite() {
        return capacite;
    }

    public void setCapacite(int capacite) {
        this.capacite = capacite;
    }

    @Override
    public String toString() {
        return "Municipalite{" +
                "name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", lat=" + lat +
                ", longi=" + longi +
                ", type=" + type +
                ", number=" + number +
                ", mail='" + mail + '\'' +
                ", heureFin='" + heureFin + '\'' +
                ", heureDebut='" + heureDebut + '\'' +
                ", tarif='" + tarif + '\'' +
                ", capacite=" + capacite +
                '}';
    }
}
