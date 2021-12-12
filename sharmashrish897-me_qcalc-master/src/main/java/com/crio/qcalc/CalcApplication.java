package com.crio.qcalc;

// import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CalcApplication {

	public static void main(String[] args) {
		// SpringApplication.run(QcalcApplication.class, args);
		
		ScientificCalculator calc = new ScientificCalculator();
		calc.divide(5, 2);
		System.out.println(calc.getResult());

	}

}
