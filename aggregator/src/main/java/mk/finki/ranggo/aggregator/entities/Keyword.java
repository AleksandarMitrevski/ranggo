package mk.finki.ranggo.aggregator.entities;


/**
 * Created by Simona on 4/1/2016.
 */
public class Keyword {
    private double relevance;
    private double sentimentScore;
    private String sentimentType;
    private String text;

    public Keyword(){

    }

    public Keyword(double relevance, double sentimentScore, String sentimentType, String text) {
        this.relevance = relevance;
        this.sentimentScore = sentimentScore;
        this.sentimentType = sentimentType;
        this.text = text;
    }

    public double getRelevance() {
        return relevance;
    }

    public void setRelevance(double relevance) {
        this.relevance = relevance;
    }

    public double getSentimentScore() {
        return sentimentScore;
    }

    public void setSentimentScore(double sentimentScore) {
        this.sentimentScore = sentimentScore;
    }

    public String getSentimentType() {
        return sentimentType;
    }

    public void setSentimentType(String sentimentType) {
        this.sentimentType = sentimentType;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return "Keyword{" +
                "relevance=" + relevance +
                ", sentimentScore=" + sentimentScore +
                ", sentimentType='" + sentimentType + '\'' +
                ", text='" + text + '\'' +
                '}';
    }
}
