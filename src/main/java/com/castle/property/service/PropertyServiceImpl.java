package com.castle.property.service;

import com.castle.property.application.config.exception.ApplicationOperationException;
import com.castle.property.dto.PropertyRequest;
import com.castle.property.entity.Property;
import com.castle.property.repository.PropertyRepository;
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
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@Validated
@RequiredArgsConstructor
public class PropertyServiceImpl implements PropertyService {
    @PersistenceContext
    private EntityManager entityManager;

    private final PropertyRepository propertyRepository;

    @Override
    public Property createProperty(@Valid PropertyRequest request) {
        boolean present = propertyRepository.findByNameIgnoreCaseAndAreaIgnoreCaseAndLocationIgnoreCase(request.getName(), request.getArea(), request.getLocation()).isPresent();
        if (present) {
            throw new ApplicationOperationException("operation.record.exist");
        }
        Property property = new Property();
        property.setArea(request.getArea());
        property.setName(request.getName());
        property.setLocation(request.getLocation());
        return propertyRepository.save(property);
    }

    @Override
    public Property updateProperty(@NotNull UUID propertyPublicId, @Valid PropertyRequest request) {
        Property property = getProperty(propertyPublicId);

        propertyRepository.findByNameIgnoreCaseAndAreaIgnoreCaseAndLocationIgnoreCase(request.getName(), request.getArea(), request.getLocation()).ifPresent(existingProperty -> {
            if (!ObjectUtils.nullSafeEquals(existingProperty.getId(), property.getId())) {
                throw new ApplicationOperationException("operation.record.exist");
            }
        });

        property.setArea(request.getArea());
        property.setName(request.getName());
        property.setLocation(request.getLocation());
        return propertyRepository.save(property);
    }

    @Override
    public Property getProperty(@NotNull UUID propertyPublicId) {
        return propertyRepository.findByPublicId(propertyPublicId).orElseThrow(() -> new ApplicationOperationException("operation.record.not.found"));
    }

    @Override
    public List<Property> listProperties() {
        return propertyRepository.listAllOrderByDateCreated();
    }

    @Override
    public Page<Property> getProperties(String searchParam, Pageable pageable) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Property> cq = cb.createQuery(Property.class);
        Root<Property> root = cq.from(Property.class);

        cq.distinct(true);

        final List<Predicate> andPredicates = propertyPredicates(searchParam, cb, root);

        cq.where(andPredicates.toArray(new Predicate[andPredicates.size()])).orderBy(cb.asc(root.get("id")));

        TypedQuery<Property> query = entityManager.createQuery(cq)
                .setMaxResults(pageable.getPageSize())
                .setFirstResult(pageable.getPageNumber() * pageable.getPageSize());

        Long count = getPropertiesCount(searchParam);
        return new PageImpl<>(query.getResultList(), pageable, count);
    }

    private List<Predicate> propertyPredicates(String searchParam, CriteriaBuilder cb, Root<Property> root) {
        final List<Predicate> andPredicates = new ArrayList<>();

        if (StringUtils.hasText(searchParam) && searchParam.length() > 2) {
            final List<Predicate> orPredicates = new ArrayList<>();
            orPredicates.add(cb.like(cb.upper(root.get("name")), "%" + searchParam.toUpperCase() + "%"));
            orPredicates.add(cb.like(cb.upper(root.get("location")), "%" + searchParam.toUpperCase() + "%"));
            orPredicates.add(cb.like(cb.upper(root.get("area")), "%" + searchParam.toUpperCase() + "%"));
            Predicate newPredicate = cb.or(orPredicates.toArray(new Predicate[orPredicates.size()]));
            andPredicates.add(newPredicate);
        }
        return andPredicates;
    }

    @Override
    public Property getPropertyById(String rowKey) {
        return propertyRepository.findById(Long.parseLong(rowKey)).orElseThrow(() -> new ApplicationOperationException("operation.record.not.found"));
    }

    @Override
    public Long getPropertiesCount(String searchParam) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<Property> countRoot = countQuery.from(Property.class);
        final List<Predicate> countPredicates = propertyPredicates(searchParam, cb, countRoot);
        countQuery.select(cb.count(countRoot));
        countQuery.where(countPredicates.toArray(new Predicate[countPredicates.size()]));

        return entityManager.createQuery(countQuery).getSingleResult();
    }
}
