package mk.ukim.finki.ikt.entities;

/**
 * Created by Simona on 4/1/2016.
 */
public class TextSentiment {

    private int mixed;
    private double score;
    private String type;

    public TextSentiment(){

    }

    public TextSentiment(int mixed, double score, String type) {
        this.mixed = mixed;
        this.score = score;
        this.type = type;
    }

    public int getMixed() {
        return mixed;
    }

    public void setMixed(int mixed) {
        this.mixed = mixed;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "TextSentiment{" +
                "mixed=" + mixed +
                ", score=" + score +
                ", type='" + type + '\'' +
                '}';
    }
}
