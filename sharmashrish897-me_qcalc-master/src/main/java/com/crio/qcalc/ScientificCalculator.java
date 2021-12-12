package com.crio.qcalc;

import java.lang.Math;

public class ScientificCalculator extends StandardCalculator {
    
    @Override
    public void add(int a, int b) {
        long temp = (long)a + (long)b;
        if(temp > Integer.MAX_VALUE) {
            throw new ArithmeticException();
        }
        this.add((double)a, (double)b);
    }
    
    @Override
    public void subtract(int a, int b) {
        this.subtract((double)a, (double)b);
    }

    @Override
    public void multiply(int a, int b) {
        this.multiply((double)a, (double)b);
    }

    @Override
    public void divide(int a, int b) {
        this.divide((double)a, (double)b);
    }
    
    public void add(double a, double b) {
        double temp = a + b;
        if(Double.isInfinite(temp) || Double.isNaN(temp)) {
            throw new ArithmeticException();
        }

        result = a + b;
    }

    public void subtract(double a, double b) {
        double temp = a - b;
        if(Double.isInfinite(temp) || Double.isNaN(temp)) {
            throw new ArithmeticException();
        }

        result = a - b;
    }

    public void multiply(double a, double b) {
        double temp = a * b;
        if(Double.isInfinite(temp) || Double.isNaN(temp)) {
            throw new ArithmeticException();
        }

        result = a * b;
    }

    public void divide(double a, double b) {
        if(b == 0.0) {
            throw new ArithmeticException();
        }

        result = (double)a / (double)b;
    }

    public void pow(double a, double b) {
        result = Math.pow(a, b);
    }
}