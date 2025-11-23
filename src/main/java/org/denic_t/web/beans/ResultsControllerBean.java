package org.denic_t.web.beans;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;
import org.denic_t.web.dao.ResultDAO;
import org.denic_t.web.dao.ResultDAOImpl;
import org.denic_t.web.entity.ResultEntity;
import org.denic_t.web.utils.AreaChecker;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

@Named
@SessionScoped
public class ResultsControllerBean implements Serializable {
    private ResultDAO resultDAO;
    private List<ResultEntity> results;

    @PostConstruct
    public void init() {
        resultDAO = new ResultDAOImpl();
        results = resultDAO.getAllResults();
    }

    public void addResult(Double x, Double y, Double r) {
        long startTime = System.nanoTime();
        if (x == null || y == null || r == null) {
            return;
        }

        boolean hit = AreaChecker.isInArea(x, y, r);
        long executionTime = System.nanoTime() - startTime;

        ResultEntity entity = new ResultEntity(x, y, r, hit, LocalDateTime.now(), executionTime);
        resultDAO.addResult(entity);
        results.add(0, entity); // Add to top

        // Draw on canvas
        String script = String.format(Locale.US, "window.drawDotOnCanvas(%f, %f, %f, %b, true, true);", x, y, r, hit);
        FacesContext.getCurrentInstance().getPartialViewContext().getEvalScripts().add(script);
    }

    public void addResultRemote() {
        FacesContext context = FacesContext.getCurrentInstance();
        java.util.Map<String, String> params = context.getExternalContext().getRequestParameterMap();

        System.out.println("DEBUG: addResultRemote called");
        System.out.println("DEBUG: Params: " + params);

        try {
            String xParam = params.get("x");
            String yParam = params.get("y");
            String rParam = params.get("r");

            System.out.println("DEBUG: x=" + xParam + ", y=" + yParam + ", r=" + rParam);

            if (xParam == null || yParam == null || rParam == null) {
                System.err.println("Missing parameters in addResultRemote");
                return;
            }

            Double x = Double.parseDouble(xParam);
            Double y = Double.parseDouble(yParam);
            Double r = Double.parseDouble(rParam);

            addResult(x, y, r);
        } catch (NumberFormatException | NullPointerException e) {
            System.err.println("Error parsing parameters: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void updateCanvas(Double r) {
        if (r == null)
            return;

        for (ResultEntity en : results) {
            boolean isHit = AreaChecker.isInArea(en.getX(), en.getY(), r);
            String script = String.format(Locale.US, "window.drawDotOnCanvas(%f, %f, %f, %b, true, false);",
                    en.getX(),
                    en.getY(), r, isHit);
            FacesContext.getCurrentInstance().getPartialViewContext().getEvalScripts().add(script);
        }
    }

    public void clearResults() {
        resultDAO.clearResults();
        results.clear();
    }

    public List<ResultEntity> getResults() {
        return results;
    }
}
