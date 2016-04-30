package mk.finki.ranggo.aggregator.repository;

import org.springframework.data.repository.PagingAndSortingRepository;

import mk.finki.ranggo.aggregator.model.Content;

public interface ContentRepository extends PagingAndSortingRepository<Content, String>, ContentRepositoryAuxiliary {
	
}