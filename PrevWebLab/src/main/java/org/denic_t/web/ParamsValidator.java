package org.denic_t.web;

import java.util.Map;

/**
 * Утилитный класс, отвечающий только за валидацию параметров.
 */
public final class ParamsValidator {

    private ParamsValidator() {}

    public static void validate(Map<String, String> params) throws ValidationException {
        validateX(params.get("x"), params.get("source"));
        validateY(params.get("y"));
        validateR(params.get("r"));
    }

    private static void validateX(String x, String source) throws ValidationException {
        if (x == null || x.isEmpty()) {
            throw new ValidationException("Параметр X отсутствует или пуст.");
        }
        try {
            double xDouble = Double.parseDouble(x);
            if ("plot".equalsIgnoreCase(source)) {
                if (xDouble < -4 || xDouble > 4) {
                    throw new ValidationException("X для клика по графику должно быть в диапазоне [-4; 4].");
                }
            } else {
                double[] validXValues = {-2, -1.5, -1, -0.5, 0, 0.5, 1, 1.5, 2};
                boolean validX = false;
                for (double validXValue : validXValues) {
                    if (Math.abs(xDouble - validXValue) < 1e-10) {
                        validX = true;
                        break;
                    }
                }
                if (!validX) {
                    throw new ValidationException("X должно быть из множества: -2, -1.5, -1, -0.5, 0, 0.5, 1, 1.5, 2");
                }
            }
        } catch (NumberFormatException e) {
            throw new ValidationException("X должно быть числом.");
        }
    }

    private static void validateY(String y) throws ValidationException {
        if (y == null || y.isEmpty()) {
            throw new ValidationException("Параметр Y отсутствует или пуст.");
        }
        try {
            double yDouble = Double.parseDouble(y);
            if (yDouble <= -3 || yDouble >= 3) {
                throw new ValidationException("Y должно быть в интервале (-3 3).");
            }
        } catch (NumberFormatException e) {
            throw new ValidationException("Y должно быть числом.");
        }
    }

    private static void validateR(String r) throws ValidationException {
        if (r == null || r.isEmpty()) {
            throw new ValidationException("Параметр R отсутствует или пуст.");
        }
        try {
            double rDouble = Double.parseDouble(r);
            if (rDouble <= 1 || rDouble >= 4) {
                throw new ValidationException("R должно быть в интервале (1 4).");
            }
        } catch (NumberFormatException e) {
            throw new ValidationException("R должно быть числом.");
        }
    }
}