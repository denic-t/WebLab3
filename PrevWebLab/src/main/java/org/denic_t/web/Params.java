package org.denic_t.web;

/**
 * Input Data Transfer Object (Input DTO) для хранения валидированных входных параметров.
 * Передача данных в бизнес-логику
 */
public class Params {

    private final double x;
    private final double y;
    private final double r;


    public Params(double x, double y, double r) {
        this.x = x;
        this.y = y;
        this.r = r;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getR() {
        return r;
    }
}