package org.denic_t.web;

import java.util.HashMap;
import java.util.Map;

public final class ParamsFactory {
    private ParamsFactory() {
    }

    public static Params createFromQuery(String query) throws ValidationException {
        if (query == null || query.isEmpty()) {
            throw new ValidationException("Строка запроса отсутствует.");
        }

        Map<String, String> paramsMap = QueryParser.parse(query);
        return createFromMap(paramsMap);
    }

    public static Params create(String x, String y, String r) throws ValidationException {
        return create(x, y, r, null);
    }

    public static Params create(String x, String y, String r, String source) throws ValidationException {
        Map<String, String> paramsMap = new HashMap<>();
        paramsMap.put("x", x);
        paramsMap.put("y", y);
        paramsMap.put("r", r);
        if (source != null) {
            paramsMap.put("source", source);
        }
        return createFromMap(paramsMap);
    }

    public static Params createFromMap(Map<String, String> paramsMap) throws ValidationException {
        if (paramsMap == null || paramsMap.isEmpty()) {
            throw new ValidationException("Параметры запроса отсутствуют.");
        }

        ParamsValidator.validate(paramsMap);

        try {
            double x = Double.parseDouble(paramsMap.get("x"));
            double y = Double.parseDouble(paramsMap.get("y"));
            double r = Double.parseDouble(paramsMap.get("r"));
            return new Params(x, y, r);
        } catch (NumberFormatException e) {
            throw new ValidationException("Ошибка преобразования одного из параметров в число.", e);
        }
    }
}
