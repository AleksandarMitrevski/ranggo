package mk.finki.ranggo.aggregator.repository;

import org.springframework.data.repository.PagingAndSortingRepository;

import mk.finki.ranggo.aggregator.model.Person;

public interface PersonRepository extends PagingAndSortingRepository<Person, String>, PersonRepositoryAuxiliary {
	
}