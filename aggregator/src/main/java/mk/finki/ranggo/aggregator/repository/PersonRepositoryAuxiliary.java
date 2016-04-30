package mk.finki.ranggo.aggregator.repository;

import mk.finki.ranggo.aggregator.model.Person;

public interface PersonRepositoryAuxiliary {
	
	Person findByName(String name);
	Person findByDbpediaUrl(String dbpediaUrl);
}