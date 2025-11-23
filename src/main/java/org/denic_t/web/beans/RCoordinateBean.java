package org.denic_t.web.beans;

import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Named;
import java.io.Serializable;

@Named
@SessionScoped
public class RCoordinateBean implements Serializable {
    private Double r = 1.0;

    public Double getR() {
        return r;
    }

    public void setR(Double r) {
        this.r = r;
    }
}
