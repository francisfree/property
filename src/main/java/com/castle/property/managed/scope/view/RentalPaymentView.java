package com.castle.property.managed.scope.view;

import com.castle.property.datatype.PaymentMode;
import com.castle.property.datatype.RentalAccountStatus;
import com.castle.property.dto.*;
import com.castle.property.entity.Property;
import com.castle.property.entity.Rental;
import com.castle.property.entity.RentalPayment;
import com.castle.property.service.PropertyService;
import com.castle.property.service.RentalPaymentService;
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
import org.primefaces.model.menu.DefaultMenuItem;
import org.primefaces.model.menu.DefaultMenuModel;
import org.primefaces.model.menu.MenuModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;

import java.io.Serializable;
import java.util.*;

@Slf4j
@Setter
@Getter
@Named
@ViewScoped
public class RentalPaymentView implements Serializable {
    private LazyDataModel<RentalPayment> rentalPayments;
    private List<Rental> rentals;
    private List<Rental> filterRentals;
    private List<Property> properties;


    private UUID propertyPublicId;
    private RentalPayment selectedPaymentRental;
    private int rowCount;
    private String inputDialogTitle;
    private String dialogButtonTitle;
    private RentalPaymentRequest rentalPaymentRequest;
    private RentalActionRequest rentalActionRequest;
    private RentalPaymentFilterRequest rentalPaymentFilterRequest;

    @Autowired
    private RentalPaymentService rentalPaymentService;

    @Autowired
    private PropertyService propertyService;

    @Autowired
    private RentalService rentalService;

    @PostConstruct
    public void init() {
        clear();
    }

    public void filter() {
        rentalPayments = new LazyDataModel<RentalPayment>() {
            @Override
            public String getRowKey(RentalPayment object) {
                return String.valueOf(object.getId());
            }

            @Override
            public RentalPayment getRowData(String rowKey) {
                return rentalPaymentService.getRentalPaymentById(rowKey);
            }

            @Override
            public int count(Map<String, FilterMeta> map) {
                return rentalPaymentService.getRentalPaymentsCount(getRentalPaymentFilterRequest()).intValue();
            }

            @Override
            public List<RentalPayment> load(int first, int pageSize, Map<String, SortMeta> map, Map<String, FilterMeta> filterMetaMap) {
                return rentalPaymentService.getRentalPayments(getRentalPaymentFilterRequest(), PageRequest.of((first / pageSize), pageSize)).getContent();
            }
        };
        DataTable dataTable = (DataTable) FacesContext.getCurrentInstance().getViewRoot().findComponent("dataForm:recordsTable");
        dataTable.setFirst(0);
    }

    public void clear() {
        setPropertyPublicId(null);
        setFilterRentals(null);
        setSelectedPaymentRental(null);
        setInputDialogTitle("New Rental Payment");
        setDialogButtonTitle("New");
        setRentalPaymentRequest(new RentalPaymentRequest());
        setRentalActionRequest(new RentalActionRequest());
        setRentalPaymentFilterRequest(new RentalPaymentFilterRequest());
        setRentals(new ArrayList<>());
        setProperties(propertyService.listProperties());
        filter();
    }

    public PaymentMode[] getPaymentModes() {
        return PaymentMode.values();
    }

    public void onRowSelect(SelectEvent<Rental> event) {
        if (getSelectedPaymentRental() != null) {

        }
    }

    public void onPropertyChange() {
        UUID newPropertyPublicId = getPropertyPublicId();
        log.info("present {}", newPropertyPublicId != null);
        if (newPropertyPublicId != null) {
            setRentals(rentalService.getRentalsByPropertyAndRentalAccountStatus(newPropertyPublicId, RentalAccountStatus.Active));
        } else {
            setRentals(new ArrayList<>());
        }
    }

    public void onFilterPropertyChange() {
        if (rentalPaymentFilterRequest.getPropertyPublicId() != null) {
            setFilterRentals(rentalService.getRentalsByPropertyAndRentalAccountStatus(rentalPaymentFilterRequest.getPropertyPublicId(), RentalAccountStatus.Active));
        } else {
            setFilterRentals(new ArrayList<>());
        }
    }

    public void onItemSelect(SelectEvent<String> event) {
        log.info("selected rental {}", event.getObject());
    }

    public void saveRentalPayment() {
        try {
            FacesMessage message;
            if (getSelectedPaymentRental() == null) {
                rentalPaymentService.createRentalPayment(getRentalPaymentRequest());
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

    public void closeViewMore() {
        clear();
        PrimeFaces.current().ajax().update("viewMoreForm");
        PrimeFaces.current().executeScript("PF('view-more-dlg').hide()");
    }

    /**
     * Creates a context menu model for each row item
     */
    public MenuModel getMenuModel(RentalPayment item) {
        DefaultMenuModel model = new DefaultMenuModel();

        // View action
        DefaultMenuItem viewItem = DefaultMenuItem.builder()
                .value("View")
                .icon("pi pi-eye")
                .onclick("PF('view-more-dlg').show()")
                .build();
        model.getElements().add(viewItem);

        return model;
    }

}
