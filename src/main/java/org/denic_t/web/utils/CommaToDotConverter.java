package org.denic_t.web.utils;

import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.faces.convert.FacesConverter;

@FacesConverter("commaToDotConverter")
public class CommaToDotConverter implements Converter<Double> {

    @Override
    public Double getAsObject(FacesContext context, UIComponent component, String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        try {
            String normalized = value.replace(',', '.');
            return Double.parseDouble(normalized);
        } catch (NumberFormatException e) {
            // Let the validator handle it or throw ConverterException
            return null;
        }
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Double value) {
        if (value == null) {
            return "";
        }
        return value.toString();
    }
}
