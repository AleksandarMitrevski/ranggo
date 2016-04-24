package mk.finki.ranggo.aggregator.entities;

import org.w3c.dom.NodeList;

import javax.xml.xpath.XPathConstants;
import java.util.ArrayList;

/**
 * Created by Simona on 3/31/2016.
 */
public class Disambiguated {

    private String disName;
    private String disGeo;
    private String disWebsite;
    private String disDbpedia;
    private String disFreebase;
    private String disYago;
    private String disGeonames;
    private ArrayList<String> subTypes;

    public Disambiguated(){

    }

    public Disambiguated(String disName, String disGeo, String disWebsite, String disDbpedia, String disFreebase, String disYago, String disGeonames, ArrayList<String> subTypes) {
        this.disName = disName;
        this.disGeo = disGeo;
        this.disWebsite = disWebsite;
        this.disDbpedia = disDbpedia;
        this.disFreebase = disFreebase;
        this.disYago = disYago;
        this.disGeonames = disGeonames;
        this.subTypes = subTypes;
    }

    public String getDisName() {
        return disName;
    }

    public void setDisName(String disName) {
        this.disName = disName;
    }

    public String getDisGeo() {
        return disGeo;
    }

    public void setDisGeo(String disGeo) {
        this.disGeo = disGeo;
    }

    public String getDisWebsite() {
        return disWebsite;
    }

    public void setDisWebsite(String disWebsite) {
        this.disWebsite = disWebsite;
    }

    public String getDisDbpedia() {
        return disDbpedia;
    }

    public void setDisDbpedia(String disDbpedia) {
        this.disDbpedia = disDbpedia;
    }

    public String getDisFreebase() {
        return disFreebase;
    }

    public void setDisFreebase(String disFreebase) {
        this.disFreebase = disFreebase;
    }

    public String getDisYago() {
        return disYago;
    }

    public void setDisYago(String disYago) {
        this.disYago = disYago;
    }

    public String getDisGeonames() {
        return disGeonames;
    }

    public void setDisGeonames(String disGeonames) {
        this.disGeonames = disGeonames;
    }

    public ArrayList<String> getSubTypes() {
        return subTypes;
    }

    public void setSubTypes(ArrayList<String> subTypes) {
        this.subTypes = subTypes;
    }

    @Override
    public String toString() {
        return "Disambiguated{" +
                "disName='" + disName + '\'' +
                ", disGeo='" + disGeo + '\'' +
                ", disWebsite='" + disWebsite + '\'' +
                ", disDbpedia='" + disDbpedia + '\'' +
                ", disFreebase='" + disFreebase + '\'' +
                ", disYago='" + disYago + '\'' +
                ", disGeonames='" + disGeonames + '\'' +
                ", subTypes=" + subTypes +
                '}';
    }
}
