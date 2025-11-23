package org.denic_t.web.bean;

import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.denic_t.web.AreaCalculator;
import org.denic_t.web.CheckResult;

import java.time.LocalDateTime;
import java.util.Map;

@Named("pointBean")
@RequestScoped
public class PointBean {

    private Double x;
    private Double y;
    private Double r;

    @Inject
    private ResultsBean resultsBean;

    /**
     * Проверяет попадание точки в область.
     * Вызывается по нажатию кнопки "Проверить" на форме.
     */
    public String checkPoint() {
        // Валидация
        if (x == null || y == null || r == null) {
            addErrorMessage("Заполните все поля: X, Y, R");
            return null;
        }

        // Проверка диапазонов (согласно варианту)
        if (x < -3 || x > 5) {
            addErrorMessage("X должен быть в диапазоне [-3, 5]");
            return null;
        }
        if (y < -5 || y > 5) {
            addErrorMessage("Y должен быть в диапазоне [-5, 5]");
            return null;
        }
        if (r < 1 || r > 3) {
            addErrorMessage("R должен быть в диапазоне [1, 3]");
            return null;
        }

        // Проверка попадания
        long startTime = System.nanoTime();
        boolean hit = AreaCalculator.isHit(x, y, r);
        long executionTime = System.nanoTime() - startTime;

        // Создаём результат
        CheckResult result = new CheckResult(x, y, r, hit, executionTime, LocalDateTime.now());

        // Добавляем в список и сохраняем в БД
        resultsBean.addResult(result);

        // Сообщение пользователю
        String message = hit ? "Точка попала в область!" : "Точка не попала в область.";
        addInfoMessage(message);

        return null; // остаёмся на той же странице
    }

    private void addErrorMessage(String message) {
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка", message));
    }

    private void addInfoMessage(String message) {
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Результат", message));
    }

    // Getters and Setters
    public Double getX() {
        return x;
    }

    public void setX(Double x) {
        this.x = x;
    }

    public Double getY() {
        return y;
    }

    public void setY(Double y) {
        this.y = y;
    }

    public Double getR() {
        return r;
    }

    public void setR(Double r) {
        this.r = r;
    }

    public void checkPointFromPlot() {
        Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        try {
            String xParam = params.get("x");
            String yParam = params.get("y");
            String rParam = params.get("r");

            if (xParam == null || yParam == null || rParam == null) {
                addErrorMessage("Некорректные данные от графика");
                return;
            }

            this.x = Double.parseDouble(xParam);
            this.y = Double.parseDouble(yParam);
            this.r = Double.parseDouble(rParam);

            checkPoint();
        } catch (NumberFormatException e) {
            addErrorMessage("Ошибка формата данных от графика");
        }
    }
}
