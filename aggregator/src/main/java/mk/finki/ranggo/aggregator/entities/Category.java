package mk.finki.ranggo.aggregator.entities;

/**
 * Created by Simona on 3/31/2016.
 */
public class Category {

    private static String category;
    private static double score;

    public Category(){

    }

    public Category(String category, double score){
        this.category = category;
        this.score = score;
    }

    public void setCategory(String category){
        this.category = category;
    }

    public void setScore(double score){
        this.score = score;
    }

    public String getCategory(){
        return category;
    }

    public double getScore(){
        return score;
    }

    @Override
    public String toString(){
        return category + " " + score;
    }
}
