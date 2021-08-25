package com.semanticbits.madie.cqm.qdm;

import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Document
@Data
@JsonIgnoreProperties(ignoreUnknown = false)
public class Patient {
    @JsonProperty(required = true)
    private String[] givenNames;
    @JsonProperty(required = true)
    private String familyName;
    private String bundleId;
    private String notes;
    @JsonProperty(required = true)
    private Object qdmPatient;
    @JsonProperty(value = "expected_values")
    private Object[] expectedValues;
    private Provider[] providers;
    @JsonProperty("measure_ids")
    private String[] measureIds;
}
