package com.semanticbits.madie.cqm.qdm;

import java.time.Instant;

import org.bson.Document;
import org.springframework.data.annotation.CreatedDate;

import lombok.Data;

@Data
@org.springframework.data.mongodb.core.mapping.Document
public class PatientWrapper {

    //    QMD measure as is
    Document qdmPatient;

    //    Audit Meta data
//    Can add User
    @CreatedDate
    private Instant createdDate;
}
