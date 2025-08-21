package com.castle.property.service;

import com.castle.property.application.config.exception.ApplicationOperationException;
import com.castle.property.dto.RentalPaymentActionRequest;
import com.castle.property.dto.RentalPaymentFilterRequest;
import com.castle.property.dto.RentalPaymentRequest;
import com.castle.property.entity.Rental;
import com.castle.property.entity.RentalPayment;
import com.castle.property.repository.RentalPaymentRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@Validated
@RequiredArgsConstructor
public class RentalPaymentServiceImpl implements RentalPaymentService {

    @PersistenceContext
    private EntityManager entityManager;
    private final RentalService rentalService;
    private final RentalPaymentRepository rentalPaymentRepository;

    private String convertPaymentMonthString(LocalDate paymentMonth) {
        return paymentMonth.format(DateTimeFormatter.ofPattern("yyyy-MMMM"));
    }

    private List<Predicate> createPredicates(RentalPaymentFilterRequest rentalPaymentFilterRequest, CriteriaBuilder cb, Root<RentalPayment> root) {
        final List<Predicate> andPredicates = new ArrayList<>();

        Predicate deletedPredicate = cb.equal(root.get("deleted"), Boolean.FALSE);

        andPredicates.add(deletedPredicate);

        if (rentalPaymentFilterRequest.getPropertyPublicId() != null) {
            Predicate newPredicate = cb.equal(root.get("rental").get("house").get("property").get("publicId"), rentalPaymentFilterRequest.getPropertyPublicId());
            andPredicates.add(newPredicate);
        }

        if (rentalPaymentFilterRequest.getHousePublicId() != null) {
            Predicate newPredicate = cb.equal(root.get("rental").get("house").get("publicId"), rentalPaymentFilterRequest.getHousePublicId());
            andPredicates.add(newPredicate);
        }

        if (rentalPaymentFilterRequest.getRentalPublicId() != null) {
            Predicate newPredicate = cb.equal(root.get("rental").get("publicId"), rentalPaymentFilterRequest.getHousePublicId());
            andPredicates.add(newPredicate);
        }

        if (rentalPaymentFilterRequest.getStartPaymentDate() != null && rentalPaymentFilterRequest.getEndPaymentDate() != null) {
            Predicate newPredicate = cb.between(root.get("paymentDate"), rentalPaymentFilterRequest.getStartPaymentDate(), rentalPaymentFilterRequest.getEndPaymentDate());
            andPredicates.add(newPredicate);
        }

        if (rentalPaymentFilterRequest.getPaymentMonth() != null) {
            Predicate newPredicate = cb.equal(root.get("paymentMonth"), convertPaymentMonthString(rentalPaymentFilterRequest.getPaymentMonth()));
            andPredicates.add(newPredicate);
        }

        if (rentalPaymentFilterRequest.getSearchParam() != null && rentalPaymentFilterRequest.getSearchParam().trim().length() >= 2) {
            final List<Predicate> orPredicates = new ArrayList<>();
            orPredicates.add(cb.like(cb.upper(root.get("rental").get("person").get("firstName")), "%" + rentalPaymentFilterRequest.getSearchParam().toUpperCase() + "%"));
            orPredicates.add(cb.like(cb.upper(root.get("rental").get("person").get("lastName")), "%" + rentalPaymentFilterRequest.getSearchParam().toUpperCase() + "%"));
            orPredicates.add(cb.like(cb.upper(root.get("rental").get("person").get("otherName")), "%" + rentalPaymentFilterRequest.getSearchParam().toUpperCase() + "%"));
            orPredicates.add(cb.like(cb.upper(root.get("rental").get("person").get("identificationNumber")), "%" + rentalPaymentFilterRequest.getSearchParam().toUpperCase() + "%"));
            orPredicates.add(cb.like(cb.upper(root.get("rental").get("person").get("nationality")), "%" + rentalPaymentFilterRequest.getSearchParam().toUpperCase() + "%"));
            orPredicates.add(cb.like(cb.upper(root.get("rental").get("person").get("phoneNumber")), "%" + rentalPaymentFilterRequest.getSearchParam().toUpperCase() + "%"));

            Predicate p = cb.or(orPredicates.toArray(new Predicate[orPredicates.size()]));
            andPredicates.add(p);
        }
        return andPredicates;
    }

    @Override
    public RentalPayment createRentalPayment(@Valid RentalPaymentRequest request) {
        Rental rental = rentalService.getRental(request.getRentalPublicId());
        String paymentMonth = convertPaymentMonthString(request.getPaymentMonth());

        BigDecimal balance = getRentalBalance(rental.getPublicId(), request.getPaymentMonth());

        if (request.getAmount().compareTo(balance) > 0) {
            throw new ApplicationOperationException("rental.payment.amount.greater.than.balance");
        }

        RentalPayment rentalPayment = new RentalPayment();
        rentalPayment.setRental(rental);
        rentalPayment.setAmount(request.getAmount());
        rentalPayment.setPaymentDate(request.getPaymentDate());
        rentalPayment.setPaymentMonth(paymentMonth);
        rentalPayment.setPaymentMode(request.getPaymentMode());
        rentalPayment.setPaymentMessage(request.getMessage());

        return rentalPaymentRepository.save(rentalPayment);
    }

    @Override
    public BigDecimal getRentalBalance(UUID rentalPaymentPublicId, LocalDate paymentMonth) {
        Rental rental = rentalService.getRental(rentalPaymentPublicId);
        String strPaymentMonth = convertPaymentMonthString(paymentMonth);
        List<RentalPayment> rentalPayments = rentalPaymentRepository.findByRentalAndPaymentMonth(rental, strPaymentMonth);

        BigDecimal totalAmountPaid = rentalPayments.stream().map(RentalPayment::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        return rental.getAmount().subtract(totalAmountPaid);
    }

    @Override
    public RentalPayment rentalPaymentActions(UUID rentalPaymentPublicId, RentalPaymentActionRequest request) {
        return null;
    }

    @Override
    public RentalPayment getRentalPayment(UUID rentalPaymentPublicId) {
        return rentalPaymentRepository.findByPublicId(rentalPaymentPublicId).orElseThrow(() -> new ApplicationOperationException("operation.record.not.found"));
    }

    @Override
    public RentalPayment getRentalPaymentById(String rowKey) {
        return rentalPaymentRepository.getReferenceById(Long.parseLong(rowKey));
    }

    @Override
    public Page<RentalPayment> getRentalPayments(RentalPaymentFilterRequest rentalPaymentFilterRequest, Pageable pageable) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<RentalPayment> mainQuery = cb.createQuery(RentalPayment.class);
        Root<RentalPayment> root = mainQuery.from(RentalPayment.class);

        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<RentalPayment> countRoot = countQuery.from(RentalPayment.class);

        mainQuery.distinct(true);

        final List<Predicate> mainQueryPredicates = createPredicates(rentalPaymentFilterRequest, cb, root);
        final List<Predicate> countQueryPredicates = createPredicates(rentalPaymentFilterRequest, cb, countRoot);

        mainQuery.where(mainQueryPredicates.toArray(new Predicate[mainQueryPredicates.size()])).orderBy(cb.desc(root.get("paymentMonth")));

        TypedQuery<RentalPayment> query = entityManager
                .createQuery(mainQuery)
                .setMaxResults(pageable.getPageSize())
                .setFirstResult(pageable.getPageNumber() * pageable.getPageSize());
        List<RentalPayment> queryResultList = query.getResultList();

        countQuery.select(cb.count(countRoot));
        countQuery.where(countQueryPredicates.toArray(new Predicate[countQueryPredicates.size()]));
        Long count = entityManager.createQuery(countQuery).getSingleResult();

        return new PageImpl<>(queryResultList, pageable, count);
    }

    @Override
    public Number getRentalPaymentsCount(RentalPaymentFilterRequest rentalPaymentFilterRequest) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<RentalPayment> countRoot = countQuery.from(RentalPayment.class);

        final List<Predicate> countQueryPredicates = createPredicates(rentalPaymentFilterRequest, cb, countRoot);

        countQuery.select(cb.count(countRoot));
        countQuery.where(countQueryPredicates.toArray(new Predicate[countQueryPredicates.size()]));

        return entityManager.createQuery(countQuery).getSingleResult();
    }
}
