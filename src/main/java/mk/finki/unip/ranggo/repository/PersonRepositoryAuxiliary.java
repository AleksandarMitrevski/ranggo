package mk.finki.unip.ranggo.repository;

import mk.finki.unip.ranggo.model.Person;

public interface PersonRepositoryAuxiliary {
	
	Person findByName(String name);
	Person findByDbpediaUrl(String dbpediaUrl);
}