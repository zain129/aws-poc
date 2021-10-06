package com.ast.poc_aws.service;

import com.ast.poc_aws.model.ModelObject;
import com.ast.poc_aws.model.PersonData;
import com.ast.poc_aws.repository.PersonDataRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

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
        return ("{\"name\":\"John Doe\", \"birthdate\":\"20210212\", \"weight\":\"50\", \"weight_unit\":\"KG\"}");
//        AmazonDynamoDB client = new AmazonDynamoDBClient(
//                new BasicAWSCredentials(amazonAWSAccessKey, amazonAWSSecretKey));
//        if (!StringUtils.isEmpty(amazonDynamoDBEndpoint)) {
//            client.setEndpoint(amazonDynamoDBEndpoint);
//        }
//        DynamoDB dynamoDB = new DynamoDB(client);
//        Table table = dynamoDB.getTable("PersonData");
//        String name = "John Doe";
//        GetItemSpec spec = new GetItemSpec().withPrimaryKey("name", name);
//        return table.getItem(spec).toJSON();
    }

    @Override
    public boolean validateData(Date dateOfBirth, int weight) {
        try {
            // weight should be in ranges 10, 200
            if (weight < 10 || weight > 200) {
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
                return Boolean.FALSE;
            }
        } catch (Exception exception) {
            exception.printStackTrace();
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }

    @Override
    public PersonData getPersonDataFromJSON(String jsonPersonData) throws JsonProcessingException, ParseException {
        ObjectMapper mapper = new ObjectMapper();
        ModelObject personObj = mapper.readValue(jsonPersonData, ModelObject.class);

        PersonData pd = new PersonData();
        pd.setName(personObj.getName());
        pd.setDateOfBirth(new SimpleDateFormat("yyyyMMdd").parse(personObj.getDob()));
        pd.setWeight(Integer.parseInt(personObj.getWeight()));
        pd.setWeightUnit(personObj.weightUnit);
        return pd;
    }

    @Override
    public PersonData insertDirectIntoPG(PersonData personData) {
        personData.setSource(this.SOURCE_DIRECT);
        personData = personDataRepository.save(personData);
        log.info("********** Record inserted **********");
        log.info("Source: " + SOURCE_DIRECT + "\nID: " + personData.getPersonId());
        log.info("*************************************");
        return personData;
    }

    @Override
    public int insertUsingSpIntoPG(PersonData personData) {
        try {
            int id = personDataRepository.savePersonDataUsingProcedure(
                    personData.getName(), personData.getDateOfBirth(),
                    personData.getWeight(), personData.getWeightUnit(), this.SOURCE_SP, 0);
            log.info("********** Record inserted **********");
            log.info("Source: " + SOURCE_SP + "\nID: " + id);
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
            return "Dummy Name";
        }
    }
}
