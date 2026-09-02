package com.castle.property.service;

import com.castle.property.application.config.exception.ApplicationOperationException;
import com.castle.property.datatype.Floor;
import com.castle.property.datatype.HouseStatus;
import com.castle.property.dto.HouseActionRequest;
import com.castle.property.dto.HouseRequest;
import com.castle.property.entity.House;
import com.castle.property.entity.Property;
import com.castle.property.repository.HouseRepository;
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
public class HouseServiceImpl implements HouseService {
    @PersistenceContext
    private EntityManager entityManager;

    private final HouseRepository houseRepository;
    private final PropertyService propertyService;

    @Override
    public House createHouse(@Valid HouseRequest request) {
        Property property = propertyService.getProperty(request.getPropertyPublicId());
        boolean present = houseRepository.findByNumberIgnoreCaseAndFloorAndProperty(request.getNumber(), request.getFloor(), property).isPresent();
        if (present) {
            throw new ApplicationOperationException("operation.record.exist");
        }
        House house = new House();
        house.setFloor(request.getFloor());
        house.setNumber(request.getNumber());
        house.setProperty(property);
        house.setStatus(HouseStatus.Vacant);
        return houseRepository.save(house);
    }

    @Override
    public House updateHouse(@NotNull UUID housePublicId, @Valid HouseRequest request) {
        House house = getHouse(housePublicId);

        houseRepository.findByNumberIgnoreCaseAndFloorAndProperty(request.getNumber(), request.getFloor(), house.getProperty()).ifPresent(existingHouse -> {
            if (!ObjectUtils.nullSafeEquals(existingHouse.getId(), house.getId())) {
                throw new ApplicationOperationException("operation.record.exist");
            }
        });

        house.setFloor(request.getFloor());
        house.setNumber(request.getNumber());
        return houseRepository.save(house);
    }

    @Override
    public House houseActions(UUID housePublicId, HouseActionRequest request) {
        House house = getHouse(housePublicId);

        if (request.getActionType() == HouseActionRequest.ActionTypes.Occupied) {
            house.setStatus(HouseStatus.Occupied);
            house.setCurrentMonthlyRent(request.getAmount());
        } else if (request.getActionType() == HouseActionRequest.ActionTypes.Vacant) {
            house.setStatus(HouseStatus.Vacant);
            house.setCurrentMonthlyRent(null);
        } else if (request.getActionType() == HouseActionRequest.ActionTypes.ChangeAmount) {
            house.setCurrentMonthlyRent(request.getAmount());
        }

        return houseRepository.save(house);
    }

    @Override
    public House getHouse(@NotNull UUID housePublicId) {
        return houseRepository.findByPublicId(housePublicId).orElseThrow(() -> new ApplicationOperationException("operation.record.not.found"));
    }

    @Override
    public List<House> listHouses(UUID propertyPublicId) {
        if (propertyPublicId != null) {
            return houseRepository.findByPropertyPublicId(propertyPublicId);
        }
        return houseRepository.findAllOrderByProperty();
    }

    @Override
    public List<Property> listProperties() {
        return propertyService.listProperties();
    }

    @Override
    public Page<House> getHouses(Pageable pageable) {
        return houseRepository.findAll(pageable);
    }

    private List<Predicate> housePredicates(String searchParam, UUID propertyPublicId, CriteriaBuilder cb, Root<House> root) {
        final List<Predicate> andPredicates = new ArrayList<>();

        if (StringUtils.hasText(searchParam) && searchParam.length() > 2) {
            final List<Predicate> orPredicates = new ArrayList<>();
            orPredicates.add(cb.like(cb.upper(root.get("number")), "%" + searchParam.toUpperCase() + "%"));
            try {
                Floor floor = Floor.forValue(searchParam);
                orPredicates.add(cb.equal(root.get("floor"), floor));
            } catch (Exception e) {
            }
            Predicate newPredicate = cb.or(orPredicates.toArray(new Predicate[orPredicates.size()]));
            andPredicates.add(newPredicate);
        }
        if (propertyPublicId != null) {
            Predicate newPredicate = cb.equal(root.get("property").get("publicId"), propertyPublicId);
            andPredicates.add(newPredicate);
        }
        return andPredicates;
    }

    @Override
    public Page<House> getHouses(String searchParam, UUID propertyPublicId, Pageable pageable) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<House> cq = cb.createQuery(House.class);
        Root<House> root = cq.from(House.class);

        cq.distinct(true);

        final List<Predicate> andPredicates = housePredicates(searchParam, propertyPublicId, cb, root);

        cq.where(andPredicates.toArray(new Predicate[andPredicates.size()])).orderBy(cb.asc(root.get("property")), cb.asc(root.get("floor")));

        TypedQuery<House> query = entityManager.createQuery(cq)
                .setMaxResults(pageable.getPageSize())
                .setFirstResult(pageable.getPageNumber() * pageable.getPageSize());

        Long count = getHouseCount(searchParam, propertyPublicId);
        return new PageImpl<>(query.getResultList(), pageable, count);
    }

    @Override
    public House getHouseById(String rowKey) {
        return houseRepository.findById(Long.parseLong(rowKey)).orElseThrow(() -> new ApplicationOperationException("operation.record.not.found"));
    }

    @Override
    public Long getHouseCount(String searchParam, UUID propertyPublicId) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<House> countRoot = countQuery.from(House.class);
        final List<Predicate> countPredicates = housePredicates(searchParam, propertyPublicId, cb, countRoot);
        countQuery.select(cb.count(countRoot));
        countQuery.where(countPredicates.toArray(new Predicate[countPredicates.size()]));

        return entityManager.createQuery(countQuery).getSingleResult();
    }
}
