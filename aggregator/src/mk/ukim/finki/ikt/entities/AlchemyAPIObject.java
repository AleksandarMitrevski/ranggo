package mk.ukim.finki.ikt.entities;

import java.util.List;

/**
 * Created by Simona on 4/3/2016.
 */
public class AlchemyAPIObject {
    private String author;
    private String text;
    private Category category;
    private List<Concept> concepts;
    private List<Entity> entities;
    private List<Keyword> keywords;
    private TextSentiment textSentiment;
    private List<ElementTaxonomy> taxonomies;

    public AlchemyAPIObject(){

    }

    public AlchemyAPIObject(String author, String text, Category category, List<Concept> concepts, List<Entity> entities, List<Keyword> keywords, TextSentiment textSentiment, List<ElementTaxonomy> taxonomies) {
        this.author = author;
        this.text = text;
        this.category = category;
        this.concepts = concepts;
        this.entities = entities;
        this.keywords = keywords;
        this.textSentiment = textSentiment;
        this.taxonomies = taxonomies;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public List<Concept> getConcepts() {
        return concepts;
    }

    public void setConcepts(List<Concept> concepts) {
        this.concepts = concepts;
    }

    public List<Entity> getEntities() {
        return entities;
    }

    public void setEntities(List<Entity> entities) {
        this.entities = entities;
    }

    public List<Keyword> getKeywords() {
        return keywords;
    }

    public void setKeywords(List<Keyword> keywords) {
        this.keywords = keywords;
    }

    public TextSentiment getTextSentiment() {
        return textSentiment;
    }

    public void setTextSentiment(TextSentiment textSentiment) {
        this.textSentiment = textSentiment;
    }

    public List<ElementTaxonomy> getTaxonomies() {
        return taxonomies;
    }

    public void setTaxonomies(List<ElementTaxonomy> taxonomies) {
        this.taxonomies = taxonomies;
    }

    @Override
    public String toString() {
        return "AlchemyAPIObject{" +
                "author='" + author + '\'' +
                ", text='" + text + '\'' +
                ", category=" + category +
                ", concepts=" + concepts +
                ", entities=" + entities +
                ", keywords=" + keywords +
                ", textSentiment=" + textSentiment +
                ", taxonomies=" + taxonomies +
                '}';
    }
}
