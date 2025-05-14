package com.castle.property.managed.scope.view;

import com.castle.property.datatype.IdentificationType;
import com.castle.property.dto.PersonRequest;
import com.castle.property.entity.Person;
import com.castle.property.service.PersonService;
import jakarta.annotation.PostConstruct;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
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

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Slf4j
@Setter
@Getter
@Named
@ViewScoped
public class PersonView implements Serializable {
    private LazyDataModel<Person> persons;
    private Person selectedPerson;
    private String searchParam;
    private int rowCount;
    private boolean create;
    private boolean view;
    private PersonRequest personRequest;

    @Autowired
    private PersonService personService;


    @PostConstruct
    public void init() {
        filter();
        setPersonRequest(new PersonRequest());
    }


    public void filter() {
        persons = new LazyDataModel<Person>() {
            @Override
            public String getRowKey(Person object) {
                return String.valueOf(object.getId());
            }

            @Override
            public Person getRowData(String rowKey) {
                return personService.getPersonById(rowKey);
            }

            @Override
            public int count(Map<String, FilterMeta> map) {
                return personService.getPersonsCount(getSearchParam()).intValue();
            }

            @Override
            public List<Person> load(int first, int pageSize, Map<String, SortMeta> map, Map<String, FilterMeta> filterMetaMap) {
                return personService.getPersons(getSearchParam(), PageRequest.of((first / pageSize), pageSize)).getContent();
            }
        };
        DataTable dataTable = (DataTable) FacesContext.getCurrentInstance().getViewRoot().findComponent("dataForm:personsTable");
        dataTable.setFirst(0);
        setView(false);
        setCreate(false);
    }

    public void clear() {
        filter();
        setView(false);
        setCreate(false);
        setSearchParam(null);
    }

    public IdentificationType[] getIdentificationTypes() {
        return IdentificationType.values();
    }

    public void onRowSelect(SelectEvent<Person> event) {
        if (getSelectedPerson() != null) {
            getPersonRequest().setFirstName(getSelectedPerson().getFirstName());
            getPersonRequest().setLastName(getSelectedPerson().getLastName());
            getPersonRequest().setOtherName(getSelectedPerson().getOtherName());
            getPersonRequest().setIdentificationType(getSelectedPerson().getIdentificationType());
            getPersonRequest().setIdentificationNumber(getSelectedPerson().getIdentificationNumber());
            getPersonRequest().setNationality(getSelectedPerson().getNationality());
            getPersonRequest().setPhoneNumber(getSelectedPerson().getPhoneNumber());
            setView(true);
            setCreate(false);
        }
    }

    public void savePerson() {
        log.info("saveCalled getSelectedPerson() = {}", getSelectedPerson() == null);
        if (getSelectedPerson() != null) {
            personService.updatePerson(getSelectedPerson().getPublicId(), getPersonRequest());
            setSelectedPerson(null);
//            setPersonRequest(null);
            filter();
        }
        PrimeFaces.current().dialog().closeDynamic(null);
    }

}
