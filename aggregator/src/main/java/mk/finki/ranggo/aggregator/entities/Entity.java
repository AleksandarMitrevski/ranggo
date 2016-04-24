package mk.finki.ranggo.aggregator.entities;

import javax.xml.xpath.XPathConstants;

/**
 * Created by Simona on 3/31/2016.
 */
public class Entity {
    private String type;
    private double relevance;
    private String sentiment;
    private int count;
    private String text;
    private Disambiguated disambiguated;

    public Entity(){

    }

    public Entity(String type, double relevance, String sentiment, int count, String text, Disambiguated disambiguated) {
        this.type = type;
        this.relevance = relevance;
        this.sentiment = sentiment;
        this.count = count;
        this.text = text;
        this.disambiguated = disambiguated;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getRelevance() {
        return relevance;
    }

    public void setRelevance(double relevance) {
        this.relevance = relevance;
    }

    public String getSentiment() {
        return sentiment;
    }

    public void setSentiment(String sentiment) {
        this.sentiment = sentiment;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Disambiguated getDisambiguated() {
        return disambiguated;
    }

    public void setDisambiguated(Disambiguated disambiguated) {
        this.disambiguated = disambiguated;
    }

    @Override
    public String toString() {
        return "Entity{" +
                "type='" + type + '\'' +
                ", relevance=" + relevance +
                ", sentiment='" + sentiment + '\'' +
                ", count=" + count +
                ", text='" + text + '\'' +
                ", disambiguated=" + disambiguated +
                '}';
    }
}
