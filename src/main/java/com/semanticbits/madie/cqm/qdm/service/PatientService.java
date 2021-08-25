package com.semanticbits.madie.cqm.qdm.service;

import java.util.stream.Collectors;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.victools.jsonschema.generator.Option;
import com.github.victools.jsonschema.generator.OptionPreset;
import com.github.victools.jsonschema.generator.SchemaGenerator;
import com.github.victools.jsonschema.generator.SchemaGeneratorConfig;
import com.github.victools.jsonschema.generator.SchemaGeneratorConfigBuilder;
import com.github.victools.jsonschema.generator.SchemaVersion;
import com.github.victools.jsonschema.module.jackson.JacksonModule;
import com.github.victools.jsonschema.module.jackson.JacksonOption;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.spi.json.JacksonJsonProvider;
import com.jayway.jsonpath.spi.mapper.JacksonMappingProvider;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import com.networknt.schema.ValidationResult;
import com.semanticbits.madie.cqm.qdm.Patient;
import com.semanticbits.madie.cqm.qdm.PatientWrapper;
import com.semanticbits.madie.cqm.qdm.ValidationException;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PatientService {

    @Autowired
    private MongoTemplate mongoTemplate;


    public void saveRawDocument(Document patient) {
        mongoTemplate.save(patient, "cqm_patient_raw");
    }

    public void saveWrappedDocument(PatientWrapper patient) {
        mongoTemplate.save(patient, "cqm_patient_wrapped");
    }

    public void validateAndSave(Document patient) {
        validatePatient(patient);
        mongoTemplate.save(patient, "cqm_patient");
    }

    @SneakyThrows
    private void validatePatient(Document patient) {
        JsonNode patientJsonSchema = jsonSchemaFor(Patient.class);
        ObjectMapper om = new ObjectMapper();
        JsonNode jsonNode = om.readTree(patient.toJson());
        validateWithSchema(patientJsonSchema, jsonNode);
        Configuration jsonPathConfig = Configuration.builder()
                .mappingProvider(new JacksonMappingProvider())
                .jsonProvider(new JacksonJsonProvider())
                .options()
                .build();
        DocumentContext ctx = JsonPath.using(jsonPathConfig).parse(patient.toJson());

        String qdmVersion = ctx.read("$.qdmPatient.qdmVersion");

        // FIXME validate qdmPatient and its data elements
        // TODO: dynamic version schema pick up
        if (qdmVersion.equals("5.5")) {
            JsonNode dataElementsNode = ctx.read("$.qdmPatient.dataElements", JsonNode.class);
            for (int i = 0; i < dataElementsNode.size(); i++) {
                JsonNode dataElement = dataElementsNode.get(i);
                String type = dataElement.get("_type").asText().replace("QDM::", "");
                JsonNode schema = jsonSchemaFor(Class.forName("gov.healthit.qdm.v5_5." + type));
                validateWithSchema(schema, dataElement);
                log.info("Data element passed validation " + type + qdmVersion);
            }
            System.out.println(dataElementsNode);
        }
    }

    private JsonNode jsonSchemaFor(Class clazz) {
        // Schema can be pre-generated in advance with a maven plugin
        // Need to add Jackson Module to support *Required fields validation
        JacksonModule module = new JacksonModule(JacksonOption.RESPECT_JSONPROPERTY_REQUIRED );
        SchemaGeneratorConfigBuilder configBuilder = new SchemaGeneratorConfigBuilder(SchemaVersion.DRAFT_2019_09, OptionPreset.PLAIN_JSON);
        //                .with(module).with(Option.FORBIDDEN_ADDITIONAL_PROPERTIES_BY_DEFAULT);
        // FIXME FORBIDDEN_ADDITIONAL_PROPERTIES_BY_DEFAULT doesn't seems to work well if Jackson property name
        //  is different from a JSON one.  Affects expectedValues vs expected-values. Perhaps can be tweaked or
        //  re-designed in the schema. Possible can be tweaked as a new Module or an Option.

        // Don't allow extra properties an all sub elements.
//		configBuilder.forTypesInGeneral()
//				.withAdditionalPropertiesResolver(scope ->
//						scope.isContainerType()
//								|| scope.getType().isInstanceOf(Number.class)
//								|| scope.getType().isInstanceOf(String.class)
//								|| scope.getType().isPrimitive()
//								? null
//								: Void.class);

        // Another way to disallow additional properties is to set "additionalProperties": false manually as a JsonNode

        SchemaGeneratorConfig config = configBuilder.build();
        SchemaGenerator generator = new SchemaGenerator(config);
        JsonNode jsonSchema = generator.generateSchema(clazz);
        log.info("Schema for {} : {}", clazz.getName(), jsonSchema.toString());

        return jsonSchema;
    }

    private void validateWithSchema(JsonNode jsonSchema, JsonNode json) {
        JsonSchema schema = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V201909).getSchema(jsonSchema);
        ValidationResult validationResult = schema.validateAndCollect(json);
        if (!validationResult.getValidationMessages().isEmpty()) {
            log.error("Failed validation:");
            log.error("Schema: {}", schema);
            log.error("Object: {}", json);
            throw new ValidationException(validationResult.getValidationMessages().stream().map(msg -> msg.getMessage()).collect(Collectors.toList()));
        }
    }
}
