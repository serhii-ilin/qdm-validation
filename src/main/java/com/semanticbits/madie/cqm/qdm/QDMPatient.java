package com.semanticbits.madie.cqm.qdm;

import org.hl7.elm_types.r1.DateTime;

import lombok.Data;

@Data
public class QDMPatient {
    private DateTime birthDatetime;
    private String qdmVersion;
    private Object[] dataElements;
    private Object extendedData;
}
