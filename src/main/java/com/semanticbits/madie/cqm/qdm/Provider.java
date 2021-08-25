package com.semanticbits.madie.cqm.qdm;

import lombok.Data;

@Data
public class Provider {
    private String[] givenNames;
    private String familyName;
    private String specialty;
    private String title;
    private Address[] addresses;
    private Telecom[] telecoms;
}
