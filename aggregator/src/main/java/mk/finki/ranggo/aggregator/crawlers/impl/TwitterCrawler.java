package mk.finki.ranggo.aggregator.crawlers.impl;

import mk.finki.ranggo.aggregator.crawlers.Crawler;
import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;

import java.util.List;

/**
 * Created by Simona on 4/18/2016.
 */
public class TwitterCrawler implements Crawler{
    private static final String TWITTER_CONSUMER_KEY = "gbeAbHPsBhTZV0ZZyrywO1Jzm";
    private static final String TWITTER_SECRET_KEY = "0yE8FpYa6iiJWKAlQaYnojnm2OTd8GqmeU9cUE37wrX4AqTbix";
    private static final String TWITTER_ACCESS_TOKEN = "2928460595-MdCEcTojWaOBw4cW01VkcxfC61MTfQqHPzSDbSV";
    private static final String TWITTER_ACCESS_TOKEN_SECRET = "h9yzdhbMWZ3qs7hhIUxZ7OsMWU5h9jMOEk2hsMIxNhqe6";

    public static void main(String[] args){
        TwitterCrawler crawler = new TwitterCrawler();
        crawler.crawl();
    }

    public void crawl() {
        //TODO: za site lichnosti koi gi imame vo baza da se povikuva metodot getDataForKeyword
        getDataForKeyword("Barack Obama");
        getDataForKeyword("Trump");
    }

    public void getDataForKeyword(String keyword){
        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(true)
                .setOAuthConsumerKey(TWITTER_CONSUMER_KEY)
                .setOAuthConsumerSecret(TWITTER_SECRET_KEY)
                .setOAuthAccessToken(TWITTER_ACCESS_TOKEN)
                .setOAuthAccessTokenSecret(TWITTER_ACCESS_TOKEN_SECRET);
        TwitterFactory tf = new TwitterFactory(cb.build());
        Twitter twitter = tf.getInstance();
        try {
            Query query = new Query(keyword);
            QueryResult result;
            do {
                result = twitter.search(query);
                List<Status> tweets = result.getTweets();
                for (Status tweet : tweets) {
                    //restrict only english tweets
                    if(tweet.getUser().getLang() == "en") {
                        System.out.println("@" + tweet.getUser().getScreenName() + " - " + tweet.getText());
                    }
                }
            } while ((query = result.nextQuery()) != null);
            System.exit(0);
        } catch (TwitterException te) {
            te.printStackTrace();
            System.out.println("Failed to search tweets: " + te.getMessage());
            System.exit(-1);
        }
    }
}
