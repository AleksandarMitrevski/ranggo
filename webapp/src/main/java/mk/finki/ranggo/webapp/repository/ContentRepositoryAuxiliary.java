package mk.finki.ranggo.webapp.repository;

import java.util.List;

import mk.finki.ranggo.webapp.model.Content;

public interface ContentRepositoryAuxiliary {
	
	Content findBySourceUrl(String sourceUrl);
	
	List<Content> getContentsForPerson(String id);
	
	List<Content> getNewestContents();
}