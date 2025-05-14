package com.castle.property.service;

import com.castle.property.PropertyApplicationTests;
import com.castle.property.datatype.IdentificationType;
import com.castle.property.dto.PersonRequest;
import com.castle.property.entity.Person;
import com.github.javafaker.Faker;
import org.apache.commons.lang3.RandomStringUtils;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.containsStringIgnoringCase;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;

public class PersonServiceTests extends PropertyApplicationTests {

    @Autowired
    private PersonService personService;

    private final Faker faker = new Faker();

    @Test(expected = jakarta.validation.ConstraintViolationException.class)
    public void createPersonFailsFieldsInvalid() {

        PersonRequest request = new PersonRequest();
        request.setFirstName(RandomStringUtils.randomAlphabetic(260));
        request.setOtherName(RandomStringUtils.randomAlphabetic(260));
        request.setLastName(RandomStringUtils.randomAlphabetic(260));
        request.setIdentificationNumber(RandomStringUtils.randomAlphabetic(260));
        request.setNationality(RandomStringUtils.randomAlphabetic(260));
        request.setPhoneNumber(RandomStringUtils.randomAlphabetic(50));

        Person savedPerson = personService.createPerson(request);

    }

    @Test
    public void createPersonWorks() {
        PersonRequest request = new PersonRequest();
        request.setFirstName(faker.name().firstName());
        request.setOtherName(faker.name().lastName());
        request.setLastName(faker.name().nameWithMiddle());
        request.setIdentificationNumber(faker.idNumber().valid());
        request.setIdentificationType(IdentificationType.NATIONAL_ID);
        request.setNationality("Kenyan");
        request.setPhoneNumber("07"+RandomStringUtils.randomNumeric(8));

        Person savedPerson = personService.createPerson(request);

    }


    @Test(expected = com.castle.property.application.config.exception.ApplicationOperationException.class)
    public void updatePersonFailsInvalidPublicId() {

        PersonRequest request = new PersonRequest();
        request.setFirstName(faker.name().firstName());
        request.setOtherName(faker.name().lastName());
        request.setLastName(faker.name().nameWithMiddle());
        request.setIdentificationNumber(faker.idNumber().valid());
        request.setIdentificationType(IdentificationType.NATIONAL_ID);
        request.setNationality("Kenyan");
        request.setPhoneNumber("07"+RandomStringUtils.randomNumeric(8));

        Person savedPerson = personService.updatePerson(UUID.randomUUID(), request);

    }


    @Test
    public void updatePersonWorks() {
        PersonRequest request = new PersonRequest();
        request.setFirstName(faker.name().firstName());
        request.setOtherName(faker.name().lastName());
        request.setLastName(faker.name().nameWithMiddle());
        request.setIdentificationNumber(faker.idNumber().valid());
        request.setIdentificationType(IdentificationType.NATIONAL_ID);
        request.setNationality("Kenyan");
        request.setPhoneNumber("07"+RandomStringUtils.randomNumeric(8));

        Person savedPerson = personService.updatePerson(UUID.fromString("563ce202-1de7-11f0-9cd2-0242ac120002"), request);

    }

    @Test
    public void listPersonWorks() {
        List<Person> persons = personService.listPersons(null);
        MatcherAssert.assertThat(persons.size(), greaterThanOrEqualTo(1));
    }

    @Test
    public void searchPersonWorks() {
        List<Person> persons = personService.listPersons("Rwandan");
        MatcherAssert.assertThat(persons.size(), greaterThanOrEqualTo(1));
        MatcherAssert.assertThat(persons.get(0).getNationality(), containsStringIgnoringCase("Rwandan"));
    }

    @Test
    public void testCreatePassword() {
        StringBuilder saltString = new StringBuilder();
        String password = personService.generatePassword("ELYTRON", "ja27pass@tn", saltString);
        System.out.println(password);
        System.out.println(saltString);
    }
}
