package com.castle.property.managed.scope.view;

import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;

@Slf4j
@Named("menuView")
@ViewScoped
public class MenuView implements Serializable {

    public String  menuAction1() {
        return "properties.xhtml?faces-redirect=true";
    }

    public String menuAction2() {
        return "persons.xhtml?faces-redirect=true";
    }

    public String menuAction3() {
        return "persons.xhtml?faces-redirect=true";
    }

    public String menuAction4() {
        return "persons.xhtml?faces-redirect=true";
    }

    public String menuAction5() {
        return "persons.xhtml?faces-redirect=true";
    }

}
