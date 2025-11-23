package org.denic_t.web;

/**
 * Утилитарный класс, содержащий только геометрическую логику
 * проверки попадания точки в область согласно варианту лабораторной.
 */
public final class AreaCalculator {

    private AreaCalculator() {
    }

    /**
     * Проверяет попадание точки (x, y) в область с радиусом r.
     * @param x координата X точки
     * @param y координата Y точки
     * @param r радиус области
     * @return true, если точка попадает в область, false - иначе
     */
    public static boolean isHit(double x, double y, double r) {
        if (x >= 0 && y >= 0) {
            return x <= r && y <= r;
        } else if (x > 0 && y < 0) {
            return y >= 2 * x - r;
        } else if (x <= 0 && y <= 0) {
            return r*r / 4 >= x * x + y * y;
        } else {
            return false;
        }
    }
}
