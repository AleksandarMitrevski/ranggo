package mk.finki.unip.ranggo.repository;

import java.util.List;

import mk.finki.unip.ranggo.model.Content;

public interface ContentRepositoryAuxiliary {
	
	Content findBySourceUrl(String sourceUrl);
	
	List<Content> getContentsForPerson(String id);
	
	List<Content> getNewestContents();
}