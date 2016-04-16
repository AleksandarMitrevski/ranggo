package mk.finki.unip.ranggo.repository.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import mk.finki.unip.ranggo.repository.ContentRepositoryAuxiliary;
import mk.finki.unip.ranggo.model.Content;

@Repository
public class ContentRepositoryImpl implements ContentRepositoryAuxiliary {

	@Autowired
	private MongoTemplate mongoTemplate;
	
	public Content findBySourceUrl(String sourceUrl) {
		if(sourceUrl != null){
			return mongoTemplate.findOne(new Query(Criteria.where("sourceUrl").is(sourceUrl)), Content.class);
		}else{
			//sourceUrl can not be null
			return null;
		}
	}

	
	public List<Content> getContentsForPerson(String id) {
		if(id != null){
			return mongoTemplate.find(new Query(Criteria.where("personEntities.person.id").is(id)), Content.class);
		}else{
			return null;
		}
	}

	public List<Content> getNewestContents() {
		Query query = new Query();
		query.with(new Sort(Sort.Direction.DESC, "_id"));
		query.limit(10);
		
		return mongoTemplate.find(query, Content.class);
	}
}