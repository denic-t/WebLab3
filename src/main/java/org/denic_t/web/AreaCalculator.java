package org.denic_t.web;


public final class AreaCalculator {

    // Коэффициенты для геометрических расчётов
    private AreaCalculator() {
    }


    public static boolean isHit(double x, double y, double r) {
        if (x >= 0 && y >= 0) {
            return y <= r / 2 && x <= r;
        } else if (x > 0 && y < 0) {
            return y >= 0.5 * x - r / 2;
        } else if (x <= 0 && y <= 0) {
            return (x * x + y * y) <= (r * r) / 4;
        }
        return false;
    }
}
