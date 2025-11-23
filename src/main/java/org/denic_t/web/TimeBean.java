package org.denic_t.web;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Named
@ApplicationScoped
public class TimeBean implements Serializable {

    private LocalDateTime now;
    private DateTimeFormatter formatter;

    public TimeBean() {
        now = LocalDateTime.now();
        formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");
    }

    public void update() {
        now = LocalDateTime.now();
    }

    public String getFormattedNow() {
        return now.format(formatter);
    }
}
