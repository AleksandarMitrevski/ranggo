package mk.finki.unip.ranggo.repository;

import org.springframework.data.repository.PagingAndSortingRepository;

import mk.finki.unip.ranggo.model.Person;

public interface PersonRepository extends PagingAndSortingRepository<Person, String>, PersonRepositoryAuxiliary {
	
}