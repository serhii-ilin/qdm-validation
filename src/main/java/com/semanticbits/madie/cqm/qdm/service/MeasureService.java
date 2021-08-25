package com.semanticbits.madie.cqm.qdm.service;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

@Service
public class MeasureService {
    @Autowired
    private MongoTemplate mongoTemplate;
    public void save(Document measure) {
        mongoTemplate.save(measure, "measure_as_raw_payload");
    }
}
