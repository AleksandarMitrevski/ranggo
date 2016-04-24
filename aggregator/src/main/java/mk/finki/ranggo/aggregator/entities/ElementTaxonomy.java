package mk.finki.ranggo.aggregator.entities;

/**
 * Created by Simona on 4/1/2016.
 */
public class ElementTaxonomy {
    private String confident;
    private String label;
    private double score;

    public ElementTaxonomy(){

    }

    public ElementTaxonomy(String confident, String label, double score) {
        if(!confident.equals("")) {
            this.confident = confident;
        } else {
            this.confident = "yes";
        }
        this.label = label;
        this.score = score;
    }

    public String getConfident() {
        return confident;
    }

    public void setConfident(String confident) {
        if(!confident.equals("")) {
            this.confident = confident;
        } else {
            this.confident = "yes";
        }
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    @Override
    public String toString() {
        return "ElementTaxonomy{" +
                "confident='" + confident + '\'' +
                ", label='" + label + '\'' +
                ", score=" + score +
                '}';
    }
}
