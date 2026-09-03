package com.castle.property.service;

import com.castle.property.application.config.exception.ApplicationOperationException;
import com.castle.property.dto.PersonRequest;
import com.castle.property.entity.Person;
import com.castle.property.repository.PersonRepository;
import de.rtner.security.auth.spi.PBKDF2Engine;
import de.rtner.security.auth.spi.PBKDF2Formatter;
import de.rtner.security.auth.spi.PBKDF2HexFormatter;
import de.rtner.security.auth.spi.PBKDF2Parameters;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

@Slf4j
@Service
@Validated
@RequiredArgsConstructor
public class PersonServiceImpl implements PersonService {
    @PersistenceContext
    private EntityManager entityManager;

    private final PersonRepository personRepository;

    @Override
    public Person createPerson(@Valid PersonRequest request) {

        Optional<Person> optionalPerson = personRepository.findByFirstNameIgnoreCaseAndLastNameIgnoreCaseAndOtherNameIgnoreCaseAndPhoneNumberIgnoreCase(request.getFirstName(), request.getLastName(), request.getOtherName(), request.getPhoneNumber());
        if (optionalPerson.isPresent()) {
            return optionalPerson.get();
        } else {
            Person person = new Person();
            person.setFirstName(request.getFirstName());
            person.setLastName(request.getLastName());
            person.setOtherName(request.getOtherName());
            person.setIdentificationNumber(request.getIdentificationNumber());
            person.setIdentificationType(request.getIdentificationType());
            person.setNationality(request.getNationality());
            person.setPhoneNumber(request.getPhoneNumber());
            return personRepository.save(person);
        }
    }

    @Override
    public Person updatePerson(@NotNull UUID personPublicId, @Valid PersonRequest request) {
        Person person = getPerson(personPublicId);

        person.setFirstName(request.getFirstName());
        person.setLastName(request.getLastName());
        person.setOtherName(request.getOtherName());
        person.setIdentificationNumber(request.getIdentificationNumber());
        person.setIdentificationType(request.getIdentificationType());
        person.setNationality(request.getNationality());
        person.setPhoneNumber(request.getPhoneNumber());
        return personRepository.save(person);
    }

    @Override
    public Person getPerson(@NotNull UUID personPublicId) {
        return personRepository.findByPublicId(personPublicId).orElseThrow(() -> new ApplicationOperationException("operation.record.not.found"));
    }

    @Override
    public List<Person> listPersons(String searchParam) {
        return searchPersons(null, null, searchParam, PageRequest.of(0, Integer.MAX_VALUE)).getContent();
    }

    @Override
    public Page<Person> getPersons(String searchParam, Pageable pageable) {
        return searchPersons(null, null, searchParam, pageable);
    }

    @Override
    public Page<Person> searchPersons(String identificationNumber, String phoneNumber, String searchParam, Pageable pageable) {
        log.info("start searchPersons {} {} {}", identificationNumber, phoneNumber, searchParam);
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Person> mainQuery = cb.createQuery(Person.class);
        Root<Person> root = mainQuery.from(Person.class);

        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<Person> countRoot = countQuery.from(Person.class);

        mainQuery.distinct(true);

        final List<Predicate> mainQueryPredicates = createPredicates(identificationNumber, phoneNumber, searchParam, cb, root);
        final List<Predicate> countQueryPredicates = createPredicates(identificationNumber, phoneNumber, searchParam, cb, countRoot);

        mainQuery.where(mainQueryPredicates.toArray(new Predicate[mainQueryPredicates.size()])).orderBy(cb.desc(root.get("id")));

        TypedQuery<Person> query = entityManager
                .createQuery(mainQuery)
                .setMaxResults(pageable.getPageSize())
                .setFirstResult(pageable.getPageNumber() * pageable.getPageSize());
        List<Person> queryResultList = query.getResultList();

        countQuery.select(cb.count(countRoot));
        countQuery.where(countQueryPredicates.toArray(new Predicate[countQueryPredicates.size()]));
        Long count = entityManager.createQuery(countQuery).getSingleResult();

        log.info("end searchPersons found {}", queryResultList.size());

        return new PageImpl<>(queryResultList, pageable, count);
    }

    private List<Predicate> createPredicates(String identificationNumber, String phoneNumber, String searchParam, CriteriaBuilder cb, Root<Person> root) {
        final List<Predicate> andPredicates = new ArrayList<>();

        Predicate deletedPredicate = cb.equal(root.get("deleted"), Boolean.FALSE);

        andPredicates.add(deletedPredicate);

        if (identificationNumber != null && identificationNumber.trim().length() >= 3) {
            final List<Predicate> orPredicates = new ArrayList<>();
            orPredicates.add(cb.like(cb.upper(root.get("identificationNumber")), "%" + identificationNumber.toUpperCase() + "%"));

            Predicate p = cb.or(orPredicates.toArray(new Predicate[orPredicates.size()]));
            andPredicates.add(p);
        }

        if (phoneNumber != null && phoneNumber.trim().length() >= 3) {
            final List<Predicate> orPredicates = new ArrayList<>();
            orPredicates.add(cb.like(cb.upper(root.get("phoneNumber")), "%" + phoneNumber.toUpperCase() + "%"));

            Predicate p = cb.or(orPredicates.toArray(new Predicate[orPredicates.size()]));
            andPredicates.add(p);
        }

        if (searchParam != null && searchParam.trim().length() >= 3) {
            final List<Predicate> orPredicates = new ArrayList<>();
            orPredicates.add(cb.like(cb.upper(root.get("firstName")), "%" + searchParam.toUpperCase() + "%"));
            orPredicates.add(cb.like(cb.upper(root.get("lastName")), "%" + searchParam.toUpperCase() + "%"));
            orPredicates.add(cb.like(cb.upper(root.get("otherName")), "%" + searchParam.toUpperCase() + "%"));
            orPredicates.add(cb.like(cb.upper(root.get("identificationNumber")), "%" + searchParam.toUpperCase() + "%"));
            orPredicates.add(cb.like(cb.upper(root.get("nationality")), "%" + searchParam.toUpperCase() + "%"));
            orPredicates.add(cb.like(cb.upper(root.get("phoneNumber")), "%" + searchParam.toUpperCase() + "%"));

            Predicate p = cb.or(orPredicates.toArray(new Predicate[orPredicates.size()]));
            andPredicates.add(p);
        }
        return andPredicates;
    }

    @Override
    public Person getPersonById(String rowKey) {
        return personRepository.findById(Long.parseLong(rowKey)).orElseThrow(() -> new ApplicationOperationException("operation.record.not.found"));
    }

    @Override
    public Number getPersonsCount(String searchParam) {
        return searchPersonsCount(null, null, searchParam);
    }

    @Override
    public Number searchPersonsCount(String identificationNumber, String phoneNumber, String searchParam) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<Person> countRoot = countQuery.from(Person.class);

        final List<Predicate> countQueryPredicates = createPredicates(identificationNumber, phoneNumber, searchParam, cb, countRoot);

        countQuery.select(cb.count(countRoot));
        countQuery.where(countQueryPredicates.toArray(new Predicate[countQueryPredicates.size()]));

        return entityManager.createQuery(countQuery).getSingleResult();
    }

    @Override
    public String generatePassword(String si, String password, StringBuilder saltString) {
        int PBKDF2_ITERATIONS = 10000;
        try {
            SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
            PBKDF2Formatter formatter = new PBKDF2HexFormatter();
            byte[] salt = new byte[16];
            sr.nextBytes(salt);
            saltString.append(Base64.getEncoder().encodeToString(salt));
            if (null == si) {
                return null;
            } else {
                switch (si) {
                    case "LEGACY":
                        PBKDF2Parameters p = new PBKDF2Parameters("HmacSHA1", "UTF-8", salt, PBKDF2_ITERATIONS);
                        PBKDF2Engine e = new PBKDF2Engine(p);
                        p.setDerivedKey(e.deriveKey(password));
                        String candidate = formatter.toString(p);

                        if (veirfy(candidate, password)) {
                            return candidate;
                        } else {
                            return null;
                        }
                    case "ELYTRON":
                        /**
                         * SHA-1 value 160 bit long SHA-256 value 256 bit long
                         * SHA-512 value 512 bit long
                         */
                        int keylength = 256;
                        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
                        PBEKeySpec keySpec = new PBEKeySpec(password.toCharArray(), salt, PBKDF2_ITERATIONS, keylength);
                        SecretKey secretKey = keyFactory.generateSecret(keySpec);
                        byte[] encoded = secretKey.getEncoded();
                        return Base64.getEncoder().encodeToString(encoded);
                    default:
                        return null;
                }
            }
        } catch (NoSuchAlgorithmException | InvalidKeySpecException ex) {
            log.error(ex.getMessage(), ex);
            return null;
        }
    }

    private boolean veirfy(String candidate, String password) {
        // Verification mode
        int iterations = 1000;
        PBKDF2Formatter formatter = new PBKDF2HexFormatter();
        PBKDF2Parameters p = new PBKDF2Parameters();
        p.setHashAlgorithm("HmacSHA1");
        p.setHashCharset("ISO-8859-1");
        if (formatter.fromString(p, candidate)) {
            throw new IllegalArgumentException(
                    "Candidate data does not have correct format (\""
                            + candidate + "\")");
        }
        PBKDF2Engine e = new PBKDF2Engine(p);
        boolean verifyOK = (p.getIterationCount() >= iterations) && e.verifyKey(password);
        return verifyOK;
    }
}
