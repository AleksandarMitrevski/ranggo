package mk.finki.ranggo.aggregator.repository;

import mk.finki.ranggo.aggregator.model.Content;

public interface ContentRepositoryAuxiliary {
	
	Content findBySourceUrl(String sourceUrl);
}