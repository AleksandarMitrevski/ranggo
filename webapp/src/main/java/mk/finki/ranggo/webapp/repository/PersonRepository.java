package mk.finki.ranggo.webapp.repository;

import org.springframework.data.repository.PagingAndSortingRepository;

import mk.finki.ranggo.webapp.model.Person;

public interface PersonRepository extends PagingAndSortingRepository<Person, String>, PersonRepositoryAuxiliary {
	
}