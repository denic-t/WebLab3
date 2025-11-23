package org.denic_t.web.service;

import org.denic_t.web.AreaCalculator;
import org.denic_t.web.CheckResult;
import org.denic_t.web.Params;
import org.denic_t.web.ParamsFactory;
import org.denic_t.web.ValidationException;

/**
 * Сервисный слой, объединяющий валидацию и вычисление попадания точки в область.
 * Оставляет сервлетам только ответственность за работу с HTTP.
 */
public class AreaCheckService {

    public CheckResult check(String x, String y, String r) throws ValidationException {
        return check(x, y, r, null);
    }

    public CheckResult check(String x, String y, String r, String source) throws ValidationException {
        Params params = ParamsFactory.create(x, y, r, source);

        long startTime = System.nanoTime();
        boolean hit = AreaCalculator.isHit(params.getX(), params.getY(), params.getR());
        long executionTime = System.nanoTime() - startTime;

        return new CheckResult(params.getX(), params.getY(), params.getR(), hit, executionTime);
    }
}
