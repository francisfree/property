package com.castle.property.managed.scope.view;

import com.castle.property.datatype.IdentificationType;
import com.castle.property.datatype.RentalAccountStatus;
import com.castle.property.datatype.RentalArrearStatus;
import com.castle.property.dto.RentalActionRequest;
import com.castle.property.dto.RentalFilterRequest;
import com.castle.property.dto.RentalRequest;
import com.castle.property.entity.House;
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
import org.primefaces.model.menu.DefaultMenuItem;
import org.primefaces.model.menu.DefaultMenuModel;
import org.primefaces.model.menu.MenuModel;
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
public class RentalView implements Serializable {
    private LazyDataModel<Rental> rentals;
    private List<House> houses;
    private List<House> filterHouses;
    private List<Property> properties;

    private UUID propertyPublicId;
    private Rental selectedRental;
    private int rowCount;
    private RentalRequest rentalRequest;
    private String inputDialogTitle;
    private String dialogButtonTitle;
    private RentalActionRequest rentalActionRequest;
    private RentalFilterRequest rentalFilterRequest;

    @Autowired
    private RentalService rentalService;

    @Autowired
    private PersonService personService;

    @Autowired
    private HouseService houseService;

    @PostConstruct
    public void init() {
        clear();
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
                return rentalService.getRentalsCount(getRentalFilterRequest()).intValue();
            }

            @Override
            public List<Rental> load(int first, int pageSize, Map<String, SortMeta> map, Map<String, FilterMeta> filterMetaMap) {
                return rentalService.getRentals(getRentalFilterRequest(), PageRequest.of((first / pageSize), pageSize)).getContent();
            }
        };
        DataTable dataTable = (DataTable) FacesContext.getCurrentInstance().getViewRoot().findComponent("dataForm:recordsTable");
        dataTable.setFirst(0);
    }

    public void clear() {
        setPropertyPublicId(null);
        setPropertyPublicId(null);
        setFilterHouses(null);
        setSelectedRental(null);
        setInputDialogTitle("New Rental");
        setDialogButtonTitle("New");
        setRentalRequest(new RentalRequest());
        setRentalActionRequest(new RentalActionRequest());
        setRentalFilterRequest(new RentalFilterRequest());
        setHouses(houseService.listHouses(null));
        setProperties(houseService.listProperties());
        filter();
    }

    public IdentificationType[] getIdentificationTypes() {
        return IdentificationType.values();
    }

    public List<RentalAccountStatus> getRentalAccountStatuses() {
        return Arrays.asList(RentalAccountStatus.values());
    }

    public List<RentalArrearStatus> getRentalArrearStatuses() {
        return Arrays.asList(RentalArrearStatus.values());
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
        if (rentalFilterRequest.getPropertyPublicId() != null) {
            setFilterHouses(houseService.listHouses(rentalFilterRequest.getPropertyPublicId()));
        } else {
            setFilterHouses(houseService.listHouses(null));
        }
    }

    public void onItemSelect(SelectEvent<String> event) {
        log.info("selected rental {}", event.getObject());
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

    public void changeAmount() {
        try {
            if (getSelectedRental() != null) {
                FacesMessage message;
                rentalActionRequest.setActionType(RentalActionRequest.ActionTypes.ChangeAmount);
                rentalService.rentalActions(getSelectedRental().getPublicId(), rentalActionRequest);

                clear();

                PrimeFaces.current().ajax().update("changeAmountForm");
                PrimeFaces.current().executeScript("PF('change-amount-dlg').hide()");

                message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Amount Changed", "Details Saved");
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

    public void closeAccount() {
        try {
            if (getSelectedRental() != null) {
                FacesMessage message;
                rentalActionRequest.setActionType(RentalActionRequest.ActionTypes.CloseAccount);
                rentalService.rentalActions(getSelectedRental().getPublicId(), rentalActionRequest);

                clear();

                PrimeFaces.current().ajax().update("closeAccountForm");
                PrimeFaces.current().executeScript("PF('close-account-dlg').hide()");

                message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Account Closed", "Details Saved");
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

    /**
     * Creates a context menu model for each row item
     */
    public MenuModel getMenuModel(Rental item) {
        DefaultMenuModel model = new DefaultMenuModel();

        // View action
        DefaultMenuItem viewItem = DefaultMenuItem.builder()
                .value("Rental Payment")
                .icon("pi pi-eye")
                .command("#{rentalView.delete}")
                .build();
        model.getElements().add(viewItem);

        if (item.getAccountStatus() == RentalAccountStatus.Active) {
            // Change Amount action
            DefaultMenuItem changeAmountMenuItem = DefaultMenuItem.builder()
                    .value("Change Amount")
                    .icon("pi pi-pencil")
                    .onclick("PF('change-amount-dlg').show()")
                    .build();
            model.getElements().add(changeAmountMenuItem);

            DefaultMenuItem closeAccountMenuItem = DefaultMenuItem.builder()
                    .value("Close Account")
                    .icon("pi pi-times-circle")
                    .onclick("PF('close-account-dlg').show()")
                    .build();
            model.getElements().add(closeAccountMenuItem);
        }

//        // Delete action with confirmation
//        DefaultMenuItem deleteItem = DefaultMenuItem.builder()
//                .value("Delete")
//                .icon("pi pi-trash")
//                .command("#{rentalView.menuItemActionPreChecks}")
//                .update(":dataForm:messages :dataForm:recordsTable")
//                .onclick("return confirm('Are you sure you want to delete this item?')")
//                .build();
//        model.getElements().add(deleteItem);
//
//        // Conditional menu items based on status
//        if (item.getAccountStatus() == RentalAccountStatus.Active) {
//            DefaultMenuItem deactivateItem = DefaultMenuItem.builder()
//                    .value("Deactivate")
//                    .icon("pi pi-ban")
//                    .command("#{rentalView.delete}")
//                    .update(":dataForm:messages :dataForm:recordsTable")
//                    .build();
//            model.getElements().add(deactivateItem);
//        } else if (item.getAccountStatus() == RentalAccountStatus.Closed) {
//            DefaultMenuItem activateItem = DefaultMenuItem.builder()
//                    .value("Activate")
//                    .icon("pi pi-check")
//                    .command("#{rentalView.delete}")
//                    .update(":dataForm:messages :dataForm:recordsTable")
//                    .build();
//            model.getElements().add(activateItem);
//        }

        return model;
    }

}
