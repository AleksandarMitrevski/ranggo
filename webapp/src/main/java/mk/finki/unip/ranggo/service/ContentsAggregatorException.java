package mk.finki.unip.ranggo.service;

public class ContentsAggregatorException extends Exception {
	public enum AggregatorMethod {GOOGLE_NEWS_RSS_FEED, FACEBOOK, TWITTER, OTHER}
	
	private AggregatorMethod _source;
	
	public ContentsAggregatorException(String message, AggregatorMethod source){
		super(message);
		
		_source = source;
	}
	
	public AggregatorMethod getSource(){
		return _source;
	}
}