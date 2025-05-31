package com.castle.property.managed.scope.view;

import com.castle.property.datatype.IdentificationType;
import com.castle.property.dto.PersonRequest;
import com.castle.property.entity.Person;
import com.castle.property.service.PersonService;
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
    private PersonRequest personRequest;
    private String inputDialogTitle;
    private String dialogButtonTitle;

    @Autowired
    private PersonService personService;

    @PostConstruct
    public void init() {
        clear();
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
        DataTable dataTable = (DataTable) FacesContext.getCurrentInstance().getViewRoot().findComponent("dataForm:recordsTable");
        dataTable.setFirst(0);
    }

    public void clear() {
        setSearchParam(null);
        setPersonRequest(new PersonRequest());
        setInputDialogTitle("New Person");
        setDialogButtonTitle("New");
        filter();
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
        }
    }

    public void savePerson() {
        try {
            FacesMessage message;
            if (getSelectedPerson() != null) {
                personService.updatePerson(getSelectedPerson().getPublicId(), getPersonRequest());
                message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Update Record", "Details Saved");
            } else {
                personService.createPerson(getPersonRequest());
                message = new FacesMessage(FacesMessage.SEVERITY_INFO, "New Record", "Details Saved");
            }
            clear();
            PrimeFaces.current().executeScript("PF('dlg').hide()");
            FacesContext.getCurrentInstance().addMessage("sticky-key", message);
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

}
