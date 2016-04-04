package mk.finki.unip.ranggo.repository.impl;

import org.springframework.beans.factory.annotation.Autowired;
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
}