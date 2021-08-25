package com.semanticbits.madie.cqm.qdm;

import java.time.Instant;

import org.hl7.elm_types.r1.Concept;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Document
@Data
public class ValueSet {
    private String oid;
    @JsonProperty("display_name")
    private String displayName;
    private String version;
    private Concept[] concepts;

    @CreatedDate
    private Instant createdDate;
}
