package org.denic_t.web.beans;

import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Named;
import java.io.Serializable;

@Named
@SessionScoped
public class YCoordinateBean implements Serializable {
    private Double y = 0.0;

    public Double getY() {
        return y;
    }

    public void setY(Double y) {
        this.y = y;
    }
}
