package com.castle.property.managed.scope.view;

import com.castle.property.datatype.Floor;
import com.castle.property.dto.HouseRequest;
import com.castle.property.entity.House;
import com.castle.property.entity.Property;
import com.castle.property.service.HouseService;
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
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Setter
@Getter
@Named
@ViewScoped
public class HouseView implements Serializable {
    private LazyDataModel<House> houses;
    private House selectedHouse;
    private String searchParam;
    private UUID filterPropertyPublicId;
    private int rowCount;
    private HouseRequest houseRequest;
    private String inputDialogTitle;
    private String dialogButtonTitle;
    private List<Floor> floors;
    private List<Property> properties;

    @Autowired
    private HouseService houseService;

    @Autowired
    private PropertyService propertyService;

    @PostConstruct
    public void init() {
        clear();
        setFloors(Arrays.asList(Floor.values()));
        setProperties(propertyService.listProperties());
    }

    public void filter() {
        DataTable dataTable = (DataTable) FacesContext.getCurrentInstance().getViewRoot().findComponent("dataForm:recordsTable");

        houses = new LazyDataModel<House>() {
            @Override
            public String getRowKey(House object) {
                return String.valueOf(object.getId());
            }

            @Override
            public House getRowData(String rowKey) {
                return houseService.getHouseById(rowKey);
            }

            @Override
            public int count(Map<String, FilterMeta> map) {
                return houseService.getHouseCount(getSearchParam(), getFilterPropertyPublicId()).intValue();
            }

            @Override
            public List<House> load(int first, int pageSize, Map<String, SortMeta> map, Map<String, FilterMeta> filterMetaMap) {
                return houseService.getHouses(getSearchParam(), getFilterPropertyPublicId(), PageRequest.of((first / pageSize), pageSize)).getContent();
            }
        };

        if (dataTable != null) {
            dataTable.setFirst(0);
        }
    }

    public void clear() {
        setSearchParam(null);
        setSelectedHouse(null);
        setHouseRequest(new HouseRequest());
        setInputDialogTitle("New House");
        setDialogButtonTitle("New");
        setFilterPropertyPublicId(null);
        filter();
        log.warn("called.........");
    }

    public void onRowSelect(SelectEvent<House> event) {
        if (getSelectedHouse() != null) {
            getHouseRequest().setNumber(getSelectedHouse().getNumber());
            getHouseRequest().setFloor(getSelectedHouse().getFloor());
            getHouseRequest().setPropertyPublicId(getSelectedHouse().getProperty().getPublicId());
            setInputDialogTitle("Edit House");
            setDialogButtonTitle("Edit");
        }
    }

    public void saveHouse() {
        try {
            FacesMessage message;
            if (getSelectedHouse() != null) {
                houseService.updateHouse(getSelectedHouse().getPublicId(), getHouseRequest());
                message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Update Record", "Details Saved");
            } else {
                houseService.createHouse(getHouseRequest());
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
