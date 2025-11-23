package org.denic_t.web.utils;

public class AreaChecker {
    public static boolean isInArea(double x, double y, double r) {
        if (x >= 0 && y >= 0) {
            // 1st Quadrant: Rectangle (x <= R, y <= R/2)
            return x <= r && y <= r / 2.0;
        } else if (x > 0 && y < 0) {
            // 4th Quadrant: Triangle (y > x - R/2)
            return y > x - r / 2.0;
        } else if (x <= 0 && y <= 0) {
            // 3rd Quadrant: Sector (x^2 + y^2 <= R^2/4)
            return (x * x + y * y) <= (r * r) / 4.0;
        }
        // 2nd Quadrant (x < 0, y > 0) -> False
        return false;
    }
}
