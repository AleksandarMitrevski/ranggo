package mk.ukim.finki.ikt.entities;

/**
 * Created by Simona on 3/31/2016.
 */
public class Concept {
    private String text;
    private double relevance;
    private String geo;
    private String website;
    private String dbpedia;
    private String ciaFacebook;
    private String freebase;
    private String opencyc;
    private String yago;
    private String geoNames;

    public Concept(){

    }

    @Override
    public String toString() {
        return "Concept{" +
                "text='" + text + '\'' +
                ", relevance=" + relevance +
                ", geo='" + geo + '\'' +
                ", website='" + website + '\'' +
                ", dbpedia='" + dbpedia + '\'' +
                ", ciaFacebook='" + ciaFacebook + '\'' +
                ", freebase='" + freebase + '\'' +
                ", opencyc='" + opencyc + '\'' +
                ", yago='" + yago + '\'' +
                ", geoNames='" + geoNames + '\'' +
                '}';
    }

    public Concept(String text, double relevance, String geo, String website, String dbpedia, String ciaFacebook, String freebase, String opencyc, String yago, String geoNames) {
        this.text = text;
        this.relevance = relevance;
        this.geo = geo;
        this.website = website;
        this.dbpedia = dbpedia;
        this.ciaFacebook = ciaFacebook;
        this.freebase = freebase;
        this.opencyc = opencyc;
        this.yago = yago;
        this.geoNames = geoNames;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public double getRelevance() {
        return relevance;
    }

    public void setRelevance(double relevance) {
        this.relevance = relevance;
    }

    public String getGeo() {
        return geo;
    }

    public void setGeo(String geo) {
        this.geo = geo;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getDbpedia() {
        return dbpedia;
    }

    public void setDbpedia(String dbpedia) {
        this.dbpedia = dbpedia;
    }

    public String getCiaFacebook() {
        return ciaFacebook;
    }

    public void setCiaFacebook(String ciaFacebook) {
        this.ciaFacebook = ciaFacebook;
    }

    public String getFreebase() {
        return freebase;
    }

    public void setFreebase(String freebase) {
        this.freebase = freebase;
    }

    public String getOpencyc() {
        return opencyc;
    }

    public void setOpencyc(String opencyc) {
        this.opencyc = opencyc;
    }

    public String getYago() {
        return yago;
    }

    public void setYago(String yago) {
        this.yago = yago;
    }

    public String getGeoNames() {
        return geoNames;
    }

    public void setGeoNames(String geoNames) {
        this.geoNames = geoNames;
    }
}
