package com.castle.property.service;

import com.castle.property.application.config.exception.ApplicationOperationException;
import com.castle.property.datatype.Floor;
import com.castle.property.datatype.HouseStatus;
import com.castle.property.datatype.RentalAccountStatus;
import com.castle.property.datatype.RentalArrearStatus;
import com.castle.property.dto.HouseActionRequest;
import com.castle.property.dto.PersonRequest;
import com.castle.property.dto.RentalActionRequest;
import com.castle.property.dto.RentalRequest;
import com.castle.property.entity.House;
import com.castle.property.entity.Person;
import com.castle.property.entity.Rental;
import com.castle.property.repository.RentalRepository;
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

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class RentalServiceImpl implements RentalService {
    @PersistenceContext
    private EntityManager entityManager;

    private final HouseService houseService;
    private final PersonService personService;
    private final RentalRepository rentalRepository;

    @Override
    public Rental createRental(RentalRequest request) {
        House house = houseService.getHouse(request.getHousePublicId());

        if (house.getStatus() == HouseStatus.Occupied) {
            throw new ApplicationOperationException("house.occupied");
        }

        PersonRequest personRequest = new PersonRequest();
        personRequest.setFirstName(request.getFirstName());
        personRequest.setLastName(request.getLastName());
        personRequest.setOtherName(request.getOtherName());
        personRequest.setIdentificationType(request.getIdentificationType());
        personRequest.setIdentificationNumber(request.getIdentificationNumber());
        personRequest.setPhoneNumber(request.getPhoneNumber());
        personRequest.setNationality(request.getNationality());

        Person person = personService.createPerson(personRequest);

        boolean present = rentalRepository.findByHouseAndPerson(house, person).isPresent();
        if (present) {
            throw new ApplicationOperationException("operation.record.exist");
        }
        Rental rental = new Rental();
        rental.setAmount(request.getAmount());
        rental.setHouse(house);
        rental.setPerson(person);
        rental.setArrearStatus(RentalArrearStatus.None);
        rental.setAccountStatus(RentalAccountStatus.Active);

        Rental savedRental = rentalRepository.save(rental);

        HouseActionRequest houseActionRequest = new HouseActionRequest();
        houseActionRequest.setActionType(HouseActionRequest.ActionTypes.Occupied);
        houseActionRequest.setAmount(savedRental.getAmount());
        houseService.houseActions(house.getPublicId(), houseActionRequest);

        return savedRental;
    }

    @Override
    public Rental rentalActions(@NotNull UUID rentalPublicId, @Valid RentalActionRequest request) {
        Rental rental = getRental(rentalPublicId);
        if (request.getActionType() == RentalActionRequest.ActionTypes.ChangeAmount) {
            if (request.getAmount() == null) {
                throw new ApplicationOperationException("rental.action.change.amount.missing");
            }
            rental.setAmount(request.getAmount());

            HouseActionRequest houseActionRequest = new HouseActionRequest();
            houseActionRequest.setActionType(HouseActionRequest.ActionTypes.ChangeAmount);
            houseActionRequest.setAmount(request.getAmount());
            houseService.houseActions(rental.getHouse().getPublicId(), houseActionRequest);
        } else if (request.getActionType() == RentalActionRequest.ActionTypes.CloseAccount) {
            HouseActionRequest houseActionRequest = new HouseActionRequest();
            houseActionRequest.setActionType(HouseActionRequest.ActionTypes.Vacant);
            houseService.houseActions(rental.getHouse().getPublicId(), houseActionRequest);
            rental.setAccountStatus(RentalAccountStatus.Closed);
        }

        return rentalRepository.save(rental);
    }

    @Override
    public Rental getRental(UUID rentalPublicId) {
        return rentalRepository.findByPublicId(rentalPublicId).orElseThrow(() -> new ApplicationOperationException("operation.record.not.found"));
    }

    @Override
    public List<Rental> listRentals(String searchParam) {
        return searchRentals(null, null, null, null, searchParam, PageRequest.of(0, Integer.MAX_VALUE)).getContent();
    }

    @Override
    public Page<Rental> getRentals(String searchParam, UUID housePublicId, Pageable pageable) {
        return searchRentals(null, housePublicId, null, null, searchParam, pageable);
    }

    @Override
    public Page<Rental> searchRentals(UUID propertyPublicId, UUID housePublicId, String identificationNumber, String phoneNumber, String searchParam, Pageable pageable) {
        log.info("start searchRentals {} {} {} {}", propertyPublicId, identificationNumber, phoneNumber, searchParam);
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Rental> mainQuery = cb.createQuery(Rental.class);
        Root<Rental> root = mainQuery.from(Rental.class);

        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<Rental> countRoot = countQuery.from(Rental.class);

        mainQuery.distinct(true);

        final List<Predicate> mainQueryPredicates = createPredicates(propertyPublicId, housePublicId, identificationNumber, phoneNumber, searchParam, cb, root);
        final List<Predicate> countQueryPredicates = createPredicates(propertyPublicId, housePublicId, identificationNumber, phoneNumber, searchParam, cb, countRoot);

        mainQuery.where(mainQueryPredicates.toArray(new Predicate[mainQueryPredicates.size()])).orderBy(cb.desc(root.get("id")));

        TypedQuery<Rental> query = entityManager
                .createQuery(mainQuery)
                .setMaxResults(pageable.getPageSize())
                .setFirstResult(pageable.getPageNumber() * pageable.getPageSize());
        List<Rental> queryResultList = query.getResultList();

        countQuery.select(cb.count(countRoot));
        countQuery.where(countQueryPredicates.toArray(new Predicate[countQueryPredicates.size()]));
        Long count = entityManager.createQuery(countQuery).getSingleResult();

        log.info("end searchRentals found {}", queryResultList.size());

        return new PageImpl<>(queryResultList, pageable, count);
    }

    private List<Predicate> createPredicates(UUID propertyPublicId, UUID housePublicId, String identificationNumber, String phoneNumber, String searchParam, CriteriaBuilder cb, Root<Rental> root) {
        final List<Predicate> andPredicates = new ArrayList<>();

        Predicate deletedPredicate = cb.equal(root.get("deleted"), Boolean.FALSE);

        andPredicates.add(deletedPredicate);

        if (identificationNumber != null && identificationNumber.trim().length() >= 2) {
            final List<Predicate> orPredicates = new ArrayList<>();
            orPredicates.add(cb.like(cb.upper(root.get("person").get("identificationNumber")), "%" + searchParam.toUpperCase() + "%"));

            Predicate p = cb.or(orPredicates.toArray(new Predicate[orPredicates.size()]));
            andPredicates.add(p);
        }
        if (propertyPublicId != null) {
            Predicate newPredicate = cb.equal(root.get("property").get("publicId"), propertyPublicId);
            andPredicates.add(newPredicate);
        }
        if (housePublicId != null) {
            Predicate newPredicate = cb.equal(root.get("house").get("publicId"), housePublicId);
            andPredicates.add(newPredicate);
        }

        if (phoneNumber != null && phoneNumber.trim().length() >= 2) {
            final List<Predicate> orPredicates = new ArrayList<>();
            orPredicates.add(cb.like(cb.upper(root.get("person").get("phoneNumber")), "%" + searchParam.toUpperCase() + "%"));

            Predicate p = cb.or(orPredicates.toArray(new Predicate[orPredicates.size()]));
            andPredicates.add(p);
        }

        if (searchParam != null && searchParam.trim().length() >= 2) {
            final List<Predicate> orPredicates = new ArrayList<>();
            try {
                Floor floor = Floor.forValue(searchParam);
                orPredicates.add(cb.equal(root.get("house").get("location"), floor));
            } catch (Exception e) {
            }
            orPredicates.add(cb.like(cb.upper(root.get("house").get("number")), "%" + searchParam.toUpperCase() + "%"));
            orPredicates.add(cb.like(cb.upper(root.get("person").get("firstName")), "%" + searchParam.toUpperCase() + "%"));
            orPredicates.add(cb.like(cb.upper(root.get("person").get("lastName")), "%" + searchParam.toUpperCase() + "%"));
            orPredicates.add(cb.like(cb.upper(root.get("person").get("otherName")), "%" + searchParam.toUpperCase() + "%"));
            orPredicates.add(cb.like(cb.upper(root.get("person").get("identificationNumber")), "%" + searchParam.toUpperCase() + "%"));
            orPredicates.add(cb.like(cb.upper(root.get("person").get("nationality")), "%" + searchParam.toUpperCase() + "%"));
            orPredicates.add(cb.like(cb.upper(root.get("person").get("phoneNumber")), "%" + searchParam.toUpperCase() + "%"));

            Predicate p = cb.or(orPredicates.toArray(new Predicate[orPredicates.size()]));
            andPredicates.add(p);
        }
        return andPredicates;
    }

    @Override
    public Rental getRentalById(String rowKey) {
        return rentalRepository.findById(Long.parseLong(rowKey)).orElseThrow(() -> new ApplicationOperationException("operation.record.not.found"));
    }

    @Override
    public Number getRentalsCount(String searchParam, UUID housePublicId) {
        return searchRentalsCount(null, housePublicId, null, null, searchParam);
    }

    @Override
    public Number searchRentalsCount(UUID propertyPublicId, UUID housePublicId, String identificationNumber, String phoneNumber, String searchParam) {

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<Rental> countRoot = countQuery.from(Rental.class);

        final List<Predicate> countQueryPredicates = createPredicates(propertyPublicId, housePublicId, identificationNumber, phoneNumber, searchParam, cb, countRoot);

        countQuery.select(cb.count(countRoot));
        countQuery.where(countQueryPredicates.toArray(new Predicate[countQueryPredicates.size()]));

        return entityManager.createQuery(countQuery).getSingleResult();
    }
}
