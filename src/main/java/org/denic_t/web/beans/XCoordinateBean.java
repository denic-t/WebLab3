package org.denic_t.web.beans;

import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Named;
import java.io.Serializable;

@Named
@SessionScoped
public class XCoordinateBean implements Serializable {
    private Double x = 0.0;

    public Double getX() {
        return x;
    }

    public void setX(Double x) {
        this.x = x;
    }
}
