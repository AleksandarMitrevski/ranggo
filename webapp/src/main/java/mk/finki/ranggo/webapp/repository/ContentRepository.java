package mk.finki.ranggo.webapp.repository;

import org.springframework.data.repository.PagingAndSortingRepository;

import mk.finki.ranggo.webapp.model.Content;

public interface ContentRepository extends PagingAndSortingRepository<Content, String>, ContentRepositoryAuxiliary {
	
}