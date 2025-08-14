package com.castle.property.managed.scope.view;

import com.castle.property.datatype.IdentificationType;
import com.castle.property.dto.RentalRequest;
import com.castle.property.entity.House;
import com.castle.property.entity.Person;
import com.castle.property.entity.Property;
import com.castle.property.entity.Rental;
import com.castle.property.service.HouseService;
import com.castle.property.service.PersonService;
import com.castle.property.service.RentalService;
import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import jakarta.validation.ConstraintViolationException;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.primefaces.PrimeFaces;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.FilterMeta;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortMeta;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Setter
@Getter
@Named
@ViewScoped
public class RentalView implements Serializable {
    private LazyDataModel<Rental> rentals;
    private List<Person> persons;
    private List<Person> availablePersons;
    private List<House> houses;
    private List<House> filterHouses;
    private List<Property> properties;
    private UUID propertyPublicId;
    private UUID filterPropertyPublicId;
    private UUID filterHousePublicId;
    private Rental selectedRental;
    private String searchParam;
    private int rowCount;
    private RentalRequest rentalRequest;
    private String inputDialogTitle;
    private String dialogButtonTitle;

    @Autowired
    private RentalService rentalService;

    @Autowired
    private PersonService personService;

    @Autowired
    private HouseService houseService;

    @PostConstruct
    public void init() {
        clear();
        setPersons(personService.listPersons(null));
        setHouses(houseService.listHouses(null));
        setProperties(houseService.listProperties());
    }

    public void filter() {
        rentals = new LazyDataModel<Rental>() {
            @Override
            public String getRowKey(Rental object) {
                return String.valueOf(object.getId());
            }

            @Override
            public Rental getRowData(String rowKey) {
                return rentalService.getRentalById(rowKey);
            }

            @Override
            public int count(Map<String, FilterMeta> map) {
                return rentalService.getRentalsCount(getSearchParam(), getFilterHousePublicId()).intValue();
            }

            @Override
            public List<Rental> load(int first, int pageSize, Map<String, SortMeta> map, Map<String, FilterMeta> filterMetaMap) {
                return rentalService.getRentals(getSearchParam(), getFilterHousePublicId(), PageRequest.of((first / pageSize), pageSize)).getContent();
            }
        };
        DataTable dataTable = (DataTable) FacesContext.getCurrentInstance().getViewRoot().findComponent("dataForm:recordsTable");
        dataTable.setFirst(0);
    }

    public void clear() {
        setSearchParam(null);
        setPropertyPublicId(null);
        setRentalRequest(new RentalRequest());
        setInputDialogTitle("New Rental");
        setDialogButtonTitle("New");
        setHouses(null);
        setPropertyPublicId(null);
        setFilterHouses(null);
        setFilterPropertyPublicId(null);
        setFilterHousePublicId(null);
        setSelectedRental(null);
        filter();
    }

    public IdentificationType[] getIdentificationTypes() {
        return IdentificationType.values();
    }

    public void onRowSelect(SelectEvent<Rental> event) {
        if (getSelectedRental() != null) {

        }
    }

    public void onPropertyChange() {
        UUID newPropertyPublicId = getPropertyPublicId();
        log.info("present {}", newPropertyPublicId != null);
        if (newPropertyPublicId != null) {
            setHouses(houseService.listHouses(newPropertyPublicId));
        } else {
            setHouses(houseService.listHouses(null));
        }
    }

    public void onFilterPropertyChange() {
        UUID newPropertyPublicId = getFilterPropertyPublicId();
        log.info("present {}", newPropertyPublicId != null);
        if (newPropertyPublicId != null) {
            setFilterHouses(houseService.listHouses(newPropertyPublicId));
        } else {
            setFilterHouses(houseService.listHouses(null));
        }
    }

    public List<Person> searchPersons(String query) {
        List<Person> personList = personService.getPersons(query, Pageable.ofSize(20)).getContent();
        setAvailablePersons(personList);
        return personList;
    }

    public void onItemSelect(SelectEvent<String> event) {
        log.info("selected person {}", event.getObject());
    }

    public void saveRental() {
        try {
            FacesMessage message;
            if (getSelectedRental() == null) {
                rentalService.createRental(getRentalRequest());
                message = new FacesMessage(FacesMessage.SEVERITY_INFO, "New Record", "Details Saved");
                clear();
                PrimeFaces.current().executeScript("PF('dlg').hide()");
                FacesContext.getCurrentInstance().addMessage("sticky-key", message);
            }
        } catch (ConstraintViolationException e) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("\n");
            e.getConstraintViolations().forEach(constraintViolation -> {
                stringBuilder.append(constraintViolation.getMessage()).append("\n");
            });
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error!", stringBuilder.toString());
            FacesContext.getCurrentInstance().addMessage("sticky-key", message);
        } catch (Exception e) {
            log.error(e.getMessage());
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error!", e.getMessage());
            FacesContext.getCurrentInstance().addMessage("sticky-key", message);
        }
    }

    public Person getPersonByPublicId(String publicId) {
        return personService.getPerson(UUID.fromString(publicId));
    }
}
