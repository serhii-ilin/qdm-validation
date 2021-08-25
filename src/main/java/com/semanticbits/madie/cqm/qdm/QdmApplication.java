package com.semanticbits.madie.cqm.qdm;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

import org.apache.commons.io.IOUtils;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.core.MongoTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.cloudyrock.spring.v5.EnableMongock;
import com.github.victools.jsonschema.generator.OptionPreset;
import com.github.victools.jsonschema.generator.SchemaGenerator;
import com.github.victools.jsonschema.generator.SchemaGeneratorConfig;
import com.github.victools.jsonschema.generator.SchemaGeneratorConfigBuilder;
import com.github.victools.jsonschema.generator.SchemaVersion;
import com.github.victools.jsonschema.module.jackson.JacksonModule;
import com.github.victools.jsonschema.module.jackson.JacksonOption;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import com.networknt.schema.ValidationResult;
import com.semanticbits.madie.cqm.qdm.service.MeasureService;
import com.semanticbits.madie.cqm.qdm.service.PatientService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@EnableMongock
@SpringBootApplication
public class QdmApplication implements ApplicationRunner {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private PatientService patientService;

    @Autowired
    private MeasureService measureService;

    public static void main(String[] args) {
        log.info("STARTING THE APPLICATION");
        SpringApplication.exit(SpringApplication.run(QdmApplication.class, args));
        log.info("APPLICATION FINISHED");
    }

    @Override
    public void run(ApplicationArguments args) throws IOException {
        // TODO: it's just a PoC
        // The methods above could be replaced with corresponding REST controller methods
        // accepting Measure / Patient as a requests.
        // Ideally validation can be moved to a validating request interceptor, invoked with @Validated request.

        savePatientWrapped("/patients/CMS123v7/Test_PatientCharacteristic.json");
        savePatientRaw("/patients/CMS123v7/Test_PatientCharacteristic.json");

        savePatient("/patients/CMS123v7/Test_PatientCharacteristic.json");
        savePatient("/patients/CMS134v6/Fail_Hospice_Not_Performed_Denex.json");
        savePatient("/patients/CMS134v6/Fail_Hospice_Not_Performed_Denex.json");
        savePatient("/patients/CMS134v6/Pass_Numer.json");
        savePatient("/patients/invalid/ExtraField.json");
        savePatient("/patients/invalid/Missing_QdmPatient.json");

        saveMeasure("/CMS13v2.json");
    }

    private void savePatientRaw(String patientFile) throws IOException {
        Document document = readDocument(patientFile);

        // Optionally validate the JSON payload and store as id.
        // In simple cases there is no need to perform any logic on the domain object thus
        // constructing a domain object can be skipped?

        patientService.saveRawDocument(document);
    }

    private void savePatient(String patientFile) {
        try {
            patientService.validateAndSave(readDocument(patientFile));
        } catch (ValidationException | IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void savePatientWrapped(String patientFile) throws IOException {
        Document payload = readDocument(patientFile);

        // In theory there is no need to pass some app specific metadata (like ownership, roles, etc to the client side)
        // So, we might want to query / update a parent wrapping object with a payload
        // Parent object can have audit data,relations, version, other metadata etc)

        PatientWrapper w = new PatientWrapper();
        w.setQdmPatient(payload);

        patientService.saveWrappedDocument(w);
    }

    private void saveMeasure(String measurePath) throws IOException {
        // Don't have to parse and instantiate the model, can be stored as is with
        // minimum modifications through Document/BSON api
        Document measureDoc = readDocument(measurePath);
        measureService.save(measureDoc);
    }

    private Document readDocument(String resource) throws IOException {
        InputStream stream = QdmApplication.class.getResourceAsStream(resource);
        String result = IOUtils.toString(stream, StandardCharsets.UTF_8);
        Document bson = Document.parse(result);
        // _id to ObjectId hack
        if (bson.containsKey("_id")) {
            String id = bson.remove("_id").toString();
            bson.put("_id", new ObjectId(id));
        }
        return bson;
    }

}
