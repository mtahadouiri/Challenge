package carsapp.douirimohamedtaha.com.nitday.Entities;

/**
 * Created by PC on 07/04/2018.
 */
enum EstabType{
    Municipalite,Fourriere
}
public class Municipalite {

    private String name;
    private String address;
    private double lat;
    private double longi;
    private EstabType type;

    public Municipalite() {
    }

    public Municipalite(String name, String address, double lat, double longi, EstabType type) {
        this.name = name;
        this.address = address;
        this.lat = lat;
        this.longi = longi;
        this.type = type;
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

    @Override
    public String toString() {
        return "Municipalite{" +
                "name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", lat=" + lat +
                ", longi=" + longi +
                ", type=" + type +
                '}';
    }
}
