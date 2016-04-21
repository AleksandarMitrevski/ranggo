package mk.finki.unip.ranggo.service;

import java.util.Date;

public interface ContentsAggregator {
	
	public void aggregateGoogleNewsRSSFeed(Date date) throws ContentsAggregatorException;
	
	public void aggregateFacebook(Date date) throws ContentsAggregatorException;
	
	public void aggregateTwitter(Date date) throws ContentsAggregatorException;
	
	public void aggregateOther(Date date) throws ContentsAggregatorException;
}