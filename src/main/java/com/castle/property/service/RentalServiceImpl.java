package com.castle.property.service;

import com.castle.property.application.config.exception.ApplicationOperationException;
import com.castle.property.datatype.Floor;
import com.castle.property.datatype.HouseStatus;
import com.castle.property.datatype.RentalAccountStatus;
import com.castle.property.datatype.RentalArrearStatus;
import com.castle.property.dto.*;
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
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Validated
public class RentalServiceImpl implements RentalService {
    @PersistenceContext
    private EntityManager entityManager;

    private final HouseService houseService;
    private final PersonService personService;
    private final RentalRepository rentalRepository;

    private List<Predicate> createPredicates(RentalFilterRequest rentalFilterRequest, CriteriaBuilder cb, Root<Rental> root) {
        final List<Predicate> andPredicates = new ArrayList<>();

        Predicate deletedPredicate = cb.equal(root.get("deleted"), Boolean.FALSE);

        andPredicates.add(deletedPredicate);

        if (rentalFilterRequest.getIdentificationNumber() != null && rentalFilterRequest.getIdentificationNumber().trim().length() >= 2) {
            final List<Predicate> orPredicates = new ArrayList<>();
            orPredicates.add(cb.like(cb.upper(root.get("person").get("identificationNumber")), "%" + rentalFilterRequest.getIdentificationNumber().toUpperCase() + "%"));

            Predicate p = cb.or(orPredicates.toArray(new Predicate[orPredicates.size()]));
            andPredicates.add(p);
        }

        if (rentalFilterRequest.getPropertyPublicId() != null) {
            Predicate newPredicate = cb.equal(root.get("house").get("property").get("publicId"), rentalFilterRequest.getPropertyPublicId());
            andPredicates.add(newPredicate);
        }

        if (rentalFilterRequest.getHousePublicId() != null) {
            Predicate newPredicate = cb.equal(root.get("house").get("publicId"), rentalFilterRequest.getHousePublicId());
            andPredicates.add(newPredicate);
        }

        if (rentalFilterRequest.getAccountStatus() != null) {
            Predicate newPredicate = cb.equal(root.get("accountStatus"), rentalFilterRequest.getAccountStatus());
            andPredicates.add(newPredicate);
        }

        if (rentalFilterRequest.getArrearStatus() != null) {
            Predicate newPredicate = cb.equal(root.get("arrearStatus"), rentalFilterRequest.getArrearStatus());
            andPredicates.add(newPredicate);
        }

        if (rentalFilterRequest.getPhoneNumber() != null && rentalFilterRequest.getPhoneNumber().trim().length() >= 2) {
            final List<Predicate> orPredicates = new ArrayList<>();
            orPredicates.add(cb.like(cb.upper(root.get("person").get("phoneNumber")), "%" + rentalFilterRequest.getPhoneNumber().toUpperCase() + "%"));

            Predicate p = cb.or(orPredicates.toArray(new Predicate[orPredicates.size()]));
            andPredicates.add(p);
        }

        if (rentalFilterRequest.getSearchParam() != null && rentalFilterRequest.getSearchParam().trim().length() >= 2) {
            final List<Predicate> orPredicates = new ArrayList<>();
            try {
                Floor floor = Floor.forValue(rentalFilterRequest.getSearchParam());
                orPredicates.add(cb.equal(root.get("house").get("floor"), floor));
            } catch (Exception e) {
            }
            orPredicates.add(cb.like(cb.upper(root.get("house").get("number")), "%" + rentalFilterRequest.getSearchParam().toUpperCase() + "%"));
            orPredicates.add(cb.like(cb.upper(root.get("person").get("firstName")), "%" + rentalFilterRequest.getSearchParam().toUpperCase() + "%"));
            orPredicates.add(cb.like(cb.upper(root.get("person").get("lastName")), "%" + rentalFilterRequest.getSearchParam().toUpperCase() + "%"));
            orPredicates.add(cb.like(cb.upper(root.get("person").get("otherName")), "%" + rentalFilterRequest.getSearchParam().toUpperCase() + "%"));
            orPredicates.add(cb.like(cb.upper(root.get("person").get("identificationNumber")), "%" + rentalFilterRequest.getSearchParam().toUpperCase() + "%"));
            orPredicates.add(cb.like(cb.upper(root.get("person").get("nationality")), "%" + rentalFilterRequest.getSearchParam().toUpperCase() + "%"));
            orPredicates.add(cb.like(cb.upper(root.get("person").get("phoneNumber")), "%" + rentalFilterRequest.getSearchParam().toUpperCase() + "%"));

            Predicate p = cb.or(orPredicates.toArray(new Predicate[orPredicates.size()]));
            andPredicates.add(p);
        }
        return andPredicates;
    }

    @Override
    @Transactional
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
    @Transactional
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
    public Page<Rental> getRentals(RentalFilterRequest rentalFilterRequest, Pageable pageable) {
        log.info("start getRentals {}", rentalFilterRequest.toString());
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Rental> mainQuery = cb.createQuery(Rental.class);
        Root<Rental> root = mainQuery.from(Rental.class);

        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<Rental> countRoot = countQuery.from(Rental.class);

        mainQuery.distinct(true);

        final List<Predicate> mainQueryPredicates = createPredicates(rentalFilterRequest, cb, root);
        final List<Predicate> countQueryPredicates = createPredicates(rentalFilterRequest, cb, countRoot);

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

    @Override
    public Rental getRentalById(String rowKey) {
        return rentalRepository.findById(Long.parseLong(rowKey)).orElseThrow(() -> new ApplicationOperationException("operation.record.not.found"));
    }

    @Override
    public Number getRentalsCount(RentalFilterRequest rentalFilterRequest) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<Rental> countRoot = countQuery.from(Rental.class);

        final List<Predicate> countQueryPredicates = createPredicates(rentalFilterRequest, cb, countRoot);

        countQuery.select(cb.count(countRoot));
        countQuery.where(countQueryPredicates.toArray(new Predicate[countQueryPredicates.size()]));

        return entityManager.createQuery(countQuery).getSingleResult();
    }

}
