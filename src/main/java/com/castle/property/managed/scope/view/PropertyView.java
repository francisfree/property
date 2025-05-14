package com.castle.property.managed.scope.view;

import com.castle.property.dto.PropertyRequest;
import com.castle.property.entity.Property;
import com.castle.property.service.PropertyService;
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
public class PropertyView implements Serializable {
    private LazyDataModel<Property> properties;
    private Property selectedProperty;
    private String searchParam;
    private int rowCount;
    private PropertyRequest propertyRequest;
    private String inputDialogTitle;
    private String dialogButtonTitle;

    @Autowired
    private PropertyService propertyService;

    @PostConstruct
    public void init() {
        clear();
    }

    public void filter() {
        DataTable dataTable = (DataTable) FacesContext.getCurrentInstance().getViewRoot().findComponent("dataForm:recordsTable");

        properties = new LazyDataModel<Property>() {
            @Override
            public String getRowKey(Property object) {
                return String.valueOf(object.getId());
            }

            @Override
            public Property getRowData(String rowKey) {
                return propertyService.getPropertyById(rowKey);
            }

            @Override
            public int count(Map<String, FilterMeta> map) {
                return propertyService.getPropertiesCount(getSearchParam()).intValue();
            }

            @Override
            public List<Property> load(int first, int pageSize, Map<String, SortMeta> map, Map<String, FilterMeta> filterMetaMap) {
                return propertyService.getProperties(getSearchParam(),PageRequest.of((first / pageSize), pageSize)).getContent();
            }
        };

        if (dataTable != null) {
            dataTable.setFirst(0);
        }
    }

    public void clear() {
        filter();
        setSearchParam(null);
        setSelectedProperty(null);
        setPropertyRequest(new PropertyRequest());
        setInputDialogTitle("New Property");
        setDialogButtonTitle("New");

    }

    public void onRowSelect(SelectEvent<Property> event) {
        if (getSelectedProperty() != null) {
            getPropertyRequest().setName(getSelectedProperty().getName());
            getPropertyRequest().setLocation(getSelectedProperty().getLocation());
            getPropertyRequest().setArea(getSelectedProperty().getArea());
            setInputDialogTitle("Edit Property");
            setDialogButtonTitle("Edit");
        }
    }

    public void saveProperty() {
        try {
            FacesMessage message;
            if (getSelectedProperty() != null) {
                propertyService.updateProperty(getSelectedProperty().getPublicId(), getPropertyRequest());
                message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Update Record", "Details Saved");
            } else {
                propertyService.createProperty(getPropertyRequest());
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
