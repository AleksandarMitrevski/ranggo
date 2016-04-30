package mk.finki.ranggo.aggregator.repository.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import mk.finki.ranggo.aggregator.repository.ContentRepositoryAuxiliary;
import mk.finki.ranggo.aggregator.model.Content;

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