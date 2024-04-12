package sk.pelikan.postsservice.mongo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;


@Service
public class SequenceGeneratorService {
    @Autowired
    private MongoOperations mongoOperations;

    public int generateSequence(String seqName) {
        Query query = new Query(Criteria.where("_id").is(seqName));
        Update update = new Update().inc("seq", 1);
        PostSequence sequence = mongoOperations.findAndModify(query, update, PostSequence.class);
        return sequence.getSeq();
    }
}
