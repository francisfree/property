package com.castle.property.application.converters;

import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.faces.convert.FacesConverter;

import java.util.UUID;

@FacesConverter("uuidConverter")
public class UUIDConverter implements Converter {

    @Override
    public String getAsString(FacesContext facesContext, UIComponent uiComponent, Object object) {
        if (object != null) {
            if(object instanceof UUID){
                return object.toString();
            }
            return String.valueOf(object);
        }
        return null;
    }

    @Override
    public UUID getAsObject(FacesContext facesContext, UIComponent uiComponent, String value) {
        try {
            return (value != null && !value.isEmpty()) ? UUID.fromString(value) : null;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
