package com.crio.qcalc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;


public class StandardCalculatorTest {




	@Test
	void testAdditionOverflow() {
		StandardCalculator calc = new StandardCalculator();
		assertThrows(ArithmeticException.class, new Executable(){
			@Override
			public void execute() throws Throwable {
				calc.add(Integer.MAX_VALUE, 1);
			}
		});
	}

	@Test
    void testAdd() {
    	StandardCalculator calc = new StandardCalculator();
    	calc.add(1, 1);
    	assertEquals(2, calc.getResult());
	}

	@Test
	void testSubtract() {
		StandardCalculator calc = new StandardCalculator();
		calc.subtract(1, 1);
		assertEquals(0, calc.getResult());
	}

	@Test
	void testResult() {
		StandardCalculator calc = new StandardCalculator();
		calc.add(1, 5);
		assertEquals(6, (int)calc.getResult());
		calc.subtract((int)calc.getResult(), 6);
		assertEquals(0, (int)calc.getResult());
	}

}

