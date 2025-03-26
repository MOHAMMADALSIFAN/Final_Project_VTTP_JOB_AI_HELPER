package com.resume.backend.repository;

import com.resume.backend.model.EmailHistory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class EmailHistoryRepository {

    @Autowired
    private MongoTemplate mongoTemplate;
    
    
    /**
     * Save email history to MongoDB
     */
    public EmailHistory save(EmailHistory emailHistory) {
        return mongoTemplate.save(emailHistory, "email_history");
    }
    
    /**
     * Find all email histories sorted by creation date (newest first)
     */
    public List<EmailHistory> findAll() {
        Query query = new Query();
        query.with(Sort.by(Sort.Direction.DESC, "createdAt"));
        return mongoTemplate.find(query, EmailHistory.class, "email_history");
    }
    
    /**
     * Find email histories by tone
     */
    public List<EmailHistory> findByTone(String tone) {
        Query query = new Query(Criteria.where("tone").is(tone));
        query.with(Sort.by(Sort.Direction.DESC, "createdAt"));
        return mongoTemplate.find(query, EmailHistory.class, "email_history");
    }
    
    /**
     * Find email history by ID
     */
    public EmailHistory findById(String id) {
        return mongoTemplate.findById(id, EmailHistory.class, "email_history");
    }
    
    /**
     * Search in original content or generated replies
     */
    public List<EmailHistory> searchInContent(String searchTerm) {
        Query query = new Query(new Criteria().orOperator(
            Criteria.where("originalContent").regex(searchTerm, "i"),
            Criteria.where("generatedReply").regex(searchTerm, "i")
        ));
        query.with(Sort.by(Sort.Direction.DESC, "createdAt"));
        return mongoTemplate.find(query, EmailHistory.class, "email_history");
    }
    
    /**
     * Delete email history by ID
     */
    public void deleteById(String id) {
        Query query = new Query(Criteria.where("id").is(id));
        mongoTemplate.remove(query, EmailHistory.class, "email_history");
    }
}