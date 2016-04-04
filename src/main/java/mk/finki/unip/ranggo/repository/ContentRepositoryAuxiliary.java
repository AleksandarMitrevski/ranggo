package mk.finki.unip.ranggo.repository;

import mk.finki.unip.ranggo.model.Content;

public interface ContentRepositoryAuxiliary {
	
	Content findBySourceUrl(String sourceUrl);
}