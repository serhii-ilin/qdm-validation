package com.semanticbits.madie.cqm.qdm;

import java.time.Instant;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Data
@Document
public class MeasureWrapper {

    //    QMD measure as is
    private org.bson.Document qdmMeasure;

    //    Audit Meta data
//    Can add User
    @CreatedDate
    private Instant createdDate;
}
