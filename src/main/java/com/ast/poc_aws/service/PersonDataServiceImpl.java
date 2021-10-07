package com.ast.poc_aws.service;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.spec.GetItemSpec;
import com.ast.poc_aws.model.ModelObject;
import com.ast.poc_aws.model.PersonData;
import com.ast.poc_aws.repository.PersonDataRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Service
@Slf4j
public class PersonDataServiceImpl implements PersonDataService {

    @Autowired
    private PersonDataRepository personDataRepository;

    @Value("${amazon.dynamodb.endpoint}")
    private String amazonDynamoDBEndpoint;

    @Value("${amazon.aws.accesskey}")
    private String amazonAWSAccessKey;

    @Value("${amazon.aws.secretkey}")
    private String amazonAWSSecretKey;

    private final String SOURCE_DIRECT = "DIRECT";
    private final String SOURCE_SP = "STORED_PROCEDURE";

    @Override
    public String getJsonDoc() {
        // Perform steps to get data from dynamo
//        return ("{\"name\":\"John Doe\", \"birthdate\":\"20210212\", \"weight\":\"50\", \"weight_unit\":\"KG\"}");
        log.info("********** Connecting with AmazonDynamoDB **********");
        AmazonDynamoDB client = new AmazonDynamoDBClient(
                new BasicAWSCredentials(amazonAWSAccessKey, amazonAWSSecretKey));
        if (!StringUtils.isEmpty(amazonDynamoDBEndpoint)) {
            log.info("Connecting to: " + amazonDynamoDBEndpoint);
            client.setEndpoint(amazonDynamoDBEndpoint);
        }
        DynamoDB dynamoDB = new DynamoDB(client);
        log.info("Connecting to Table : 'PersonData'");
        Table table = dynamoDB.getTable("PersonData");
        String name = "John Doe";
        log.info("Fetching Data by name");
        GetItemSpec spec = new GetItemSpec().withPrimaryKey("name", name);
        String result = table.getItem(spec).toJSON();
        log.info("Data: " + result + "\n\n");
        return result;
    }

    @Override
    public boolean validateData(Date dateOfBirth, int weight) {
        try {
            log.info("Validating Data");
            // weight should be in ranges 10, 200
            if (weight < 10 || weight > 200) {
                log.info("Weight is out of the range [10, 200]");
                return Boolean.FALSE;
            }

            Date rangeStart, rangeEnd;
            try {
                DateFormat sdf = new SimpleDateFormat("yyyyMMdd");
                sdf.setLenient(false);
                rangeStart = sdf.parse("19000101"); // Jan 1, 1900
                rangeEnd = sdf.parse("21000101");    // Jan 1, 2100
            } catch (ParseException e) {
                return Boolean.FALSE;
            }
            //Date should be between Jan 1, 1900 and Jan 1, 2100
            if (!dateOfBirth.after(rangeStart) && !dateOfBirth.before(rangeEnd)) {
                log.info("Date of Birth is out of the range [Jan 1, 1900 - Jan 1, 2100]");
                return Boolean.FALSE;
            }
        } catch (Exception exception) {
            exception.printStackTrace();
            return Boolean.FALSE;
        }
        log.info("Data is Valid\n");
        return Boolean.TRUE;
    }

    @Override
    public PersonData getPersonDataFromJSON(String jsonPersonData) throws JsonProcessingException, ParseException {
        log.info("Mapping the json document to PersonData Object");
        ObjectMapper mapper = new ObjectMapper();
        ModelObject personObj = mapper.readValue(jsonPersonData, ModelObject.class);

        PersonData pd = new PersonData();
        pd.setName(personObj.getName());
        pd.setDateOfBirth(new SimpleDateFormat("yyyyMMdd").parse(personObj.getDob()));
        pd.setWeight(Integer.parseInt(personObj.getWeight()));
        pd.setWeightUnit(personObj.weightUnit);
        log.info("PersonData Object: " + pd + toString());
        return pd;
    }

    @Override
    public PersonData insertDirectIntoPG(PersonData personData) {
        log.info("Inserting PersonData Directly in PGSQL");
        personData.setSource(this.SOURCE_DIRECT);
        personData = personDataRepository.save(personData);
        log.info("********** Record inserted **********");
        log.info("Source: " + SOURCE_DIRECT + "  ID: " + personData.getPersonId());
        log.info("*************************************");
        return personData;
    }

    @Override
    public int insertUsingSpIntoPG(PersonData personData) {
        try {
            log.info("Inserting PersonData using Stored Procedure in PGSQL");
            int id = personDataRepository.savePersonDataUsingProcedure(
                    personData.getName(), personData.getDateOfBirth(),
                    personData.getWeight(), personData.getWeightUnit(), this.SOURCE_SP, 0);
            log.info("********** Record inserted **********");
            log.info("Source: " + SOURCE_SP + "  ID: " + id);
            log.info("*************************************");
            return id;
        } catch (Exception ex) {
            log.info("insertUsingSpIntoPG -> " + ex.getLocalizedMessage());
        }
        return -1;
    }

    @Override
    public String getNameOnly(String jsonPersonData) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            ModelObject personObj = mapper.readValue(jsonPersonData, ModelObject.class);

            return personObj.getName();
        } catch (Exception ex) {
            log.info("Unable to get person's name. Returning 'Dummy Name'.\n");
            return "Dummy Name";
        }
    }
}
