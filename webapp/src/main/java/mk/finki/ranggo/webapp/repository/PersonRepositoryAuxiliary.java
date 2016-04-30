package mk.finki.ranggo.webapp.repository;

import mk.finki.ranggo.webapp.model.Person;

public interface PersonRepositoryAuxiliary {
	
	Person findByName(String name);
	Person findByDbpediaUrl(String dbpediaUrl);
}