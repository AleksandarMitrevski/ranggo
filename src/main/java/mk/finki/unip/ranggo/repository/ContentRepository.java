package mk.finki.unip.ranggo.repository;

import org.springframework.data.repository.PagingAndSortingRepository;

import mk.finki.unip.ranggo.model.Content;

public interface ContentRepository extends PagingAndSortingRepository<Content, String>, ContentRepositoryAuxiliary {
	
}