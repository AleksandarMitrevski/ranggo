package mk.finki.ranggo.aggregator.crawlers;

import java.util.List;

import mk.finki.ranggo.aggregator.ContentsAggregatorImpl.AlchemyAPIAnalysisResult;

/**
 * Created by Simona on 4/3/2016.
 */
public interface Crawler {

	 List<AlchemyAPIAnalysisResult> crawl();
}
