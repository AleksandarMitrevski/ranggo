package mk.finki.ranggo.aggregator;

import java.util.Date;

public interface ContentsAggregator {
	
	public void aggregateGoogleNewsRSSFeed(Date date) throws ContentsAggregatorException;
	
	public void aggregateHuffingtonPost() throws ContentsAggregatorException;
	
	public void aggregateDnevnik() throws ContentsAggregatorException;
	
	public void aggregateFokus() throws ContentsAggregatorException;
	
	public void aggregateKurir() throws ContentsAggregatorException;
	
	public void aggregateLibertas() throws ContentsAggregatorException;
	
	public void aggregateNovaTV() throws ContentsAggregatorException;
	
	public void aggregateRepublika() throws ContentsAggregatorException;
	
	public void aggregateTelma() throws ContentsAggregatorException;
	
	public void aggregateUtrinskiVesnik() throws ContentsAggregatorException;
	
	public void aggregateVecher() throws ContentsAggregatorException;
	
	public void aggregateVest() throws ContentsAggregatorException;
	
	public void aggregateVesti24() throws ContentsAggregatorException;
	
	public void aggregateTest() throws ContentsAggregatorException;
	
}