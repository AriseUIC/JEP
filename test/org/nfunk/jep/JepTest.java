package org.nfunk.jep;

import static org.junit.Assert.*;
import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.nfunk.jep.type.Complex;

public class JepTest {

	JEP myParser = new JEP();
	private String errorStr;


	private boolean equal(Object param1, Object param2)
	{
		double tolerance = 1e-15;
		if ((param1 instanceof Complex) && (param2 instanceof Complex)) {
			return ((Complex)param1).equals((Complex)param2, tolerance);
		}
		if ((param1 instanceof Complex) && (param2 instanceof Number)) {
			return ((Complex)param1).equals(new Complex((Number) param2), tolerance);
		}
		if ((param2 instanceof Complex) && (param1 instanceof Number)) {
			return ((Complex)param2).equals(new Complex((Number) param1), tolerance);
		}
		if ((param1 instanceof Number) && (param2 instanceof Number)) {
			return Math.abs(((Number)param1).doubleValue()-((Number)param2).doubleValue())
					< tolerance;
		}
		// test any other types here
		return param1.equals(param2);
	}


	@Before
	public void setup(){
		myParser.setImplicitMul(true);
		myParser.addStandardFunctions();
		myParser.addStandardConstants();
		myParser.addComplex();
		myParser.setTraverse(false);
	}


	@Test
	public void simpleArithmetic() {
		try{
			String [] tStr = {"1+1", "2+3+4+9", "1+(2+3)"};
			double [] rStr = {2.0, 18.0, 6.0};
			int i = 0;
			while(i < tStr.length){
				myParser.parseExpression(tStr[i]);
				double value = myParser.getValue();
				if (myParser.hasError()) {
					errorStr = myParser.getErrorInfo();
					throw new Exception("Error while evaluating" + tStr[i]);
				}

				int eq = Double.compare(value, rStr[i]);

				if (eq != 0){
					System.out.println("Failed evaluating:" + tStr[i]);
					Assert.fail();
				}
				i++;
			}
		}catch(Exception e){
			fail("Not yet implemented");
		}
	}

	@Test
	public void logOps() {
		try{
			String [] tStr = {"ln(1)", "ln(e)", "ln(e^32)", "log(1)", "log(10)"};
			double [] rStr = {0.0, 1.0, 32.0, 0.0, 1.0};
			int i = 0;
			while(i < tStr.length){
				myParser.parseExpression(tStr[i]);
				double value = myParser.getValue();
				if (myParser.hasError()) {
					errorStr = myParser.getErrorInfo();
					throw new Exception("Error while evaluating:" + tStr[i]);
				}

				int eq = Double.compare(value, rStr[i]);

				if (eq != 0){
					System.out.println("Failed evaluating:" + tStr[i]);
					Assert.fail();
				}
				i++;
			}
		}catch(Exception e){
			fail("e.getMessage()");
		}
	}
	
	
	@Test
	public void complexOps() {
		try{
			int i = 0;
			String [] tStr = {"i^2", "i^2", "i-i", "(1+1*i)*(2+2*i)"};
			String [] rStr = {"-1", "i*i", "0", "(0+4*i)"};

			while(i < tStr.length){
				myParser.parseExpression(tStr[i]);
				Object value = myParser.getValueAsObject();

				myParser.parseExpression(rStr[i]);
				Object expValue = myParser.getValueAsObject();

				if (value == null || myParser.hasError()) {
					errorStr = myParser.getErrorInfo();
					throw new Exception("Error while evaluating: " + tStr[i]);
				}

				if (!equal(value,expValue)) {
					System.out.println("Failed evaluating:" + tStr[i]);
					Assert.fail();
				}
				i++;
			}
		}
		catch(Exception e){
			fail("Exception was thrown");
		}
	}
	
	@Test
	public void booleanOps() {
		try{
			String [] tStr = {"1==1", "1 != 0", "1==0", "!1"};
			double [] rStr = {1.0, 1.0, 0.0, 0.0};
			int i = 0;
			while(i < tStr.length){
				myParser.parseExpression(tStr[i]);
				double value = myParser.getValue();
				if (myParser.hasError()) {
					errorStr = myParser.getErrorInfo();
					throw new Exception("Error while evaluating" + tStr[i]);
				}

				int eq = Double.compare(value, rStr[i]);

				if (eq != 0){
					System.out.println("Failed evaluating:" + tStr[i]);
					Assert.fail();
				}
				i++;
			}
		}catch(Exception e){
			fail("Not yet implemented");
		}
	}
	
	@Test
	public void expOps() {
		try{
			int i = 0;
			String [] tStr = {"exp(0)", "exp(1)", "exp(2)"};
			
			String [] rStr = {"1.0", "e", "e^2"};

			while(i < tStr.length){
				myParser.parseExpression(tStr[i]);
				Object value = myParser.getValueAsObject();

				myParser.parseExpression(rStr[i]);
				Object expValue = myParser.getValueAsObject();

				if (value == null || myParser.hasError()) {
					errorStr = myParser.getErrorInfo();
					throw new Exception("Error while evaluating: " + tStr[i]);
				}

				if (!equal(value,expValue)) {
					System.out.println("Failed evaluating:" + tStr[i]);
					Assert.fail();
				}
				i++;
			}
		}
		catch(Exception e){
			fail("Exception was thrown");
		}
	}

	
	@Test
	public void absOps() {
		try{
			int i = 0;
			String [] tStr = {"abs(-1)", "abs(1)"};
			
			String [] rStr = {"1.0", "1.0"};

			while(i < tStr.length){
				myParser.parseExpression(tStr[i]);
				Object value = myParser.getValueAsObject();

				myParser.parseExpression(rStr[i]);
				Object expValue = myParser.getValueAsObject();

				if (value == null || myParser.hasError()) {
					errorStr = myParser.getErrorInfo();
					throw new Exception("Error while evaluating: " + tStr[i]);
				}

				if (!equal(value,expValue)) {
					System.out.println("Failed evaluating:" + tStr[i]);
					Assert.fail();
				}
				i++;
			}
		}
		catch(Exception e){
			fail("Exception was thrown");
		}
	}

	@Test
	public void randOps() {
		try{
			int i = 0;
			String [] tStr = {"rand() < 1", "rand() > 0"};
			
			String [] rStr = {"1.0", "1.0"};

			while(i < tStr.length){
				myParser.parseExpression(tStr[i]);
				Object value = myParser.getValueAsObject();

				myParser.parseExpression(rStr[i]);
				Object expValue = myParser.getValueAsObject();

				if (value == null || myParser.hasError()) {
					errorStr = myParser.getErrorInfo();
					throw new Exception("Error while evaluating: " + tStr[i]);
				}

				if (!equal(value,expValue)) {
					System.out.println("Failed evaluating:" + tStr[i]);
					Assert.fail();
				}
				i++;
			}
		}
		catch(Exception e){
			fail("Exception was thrown");
		}
	}

	@Test
	public void sqrtOps() {
		try{
			int i = 0;
			String [] tStr = {"sqrt(0)", "sqrt(25)", "sqrt(1)", "sqrt(100)"};
			
			String [] rStr = {"0.0", "5.0", "1.0", "10.0"};

			while(i < tStr.length){
				myParser.parseExpression(tStr[i]);
				Object value = myParser.getValueAsObject();

				myParser.parseExpression(rStr[i]);
				Object expValue = myParser.getValueAsObject();

				if (value == null || myParser.hasError()) {
					errorStr = myParser.getErrorInfo();
					throw new Exception("Error while evaluating: " + tStr[i]);
				}

				if (!equal(value,expValue)) {
					System.out.println("Failed evaluating:" + tStr[i]);
					Assert.fail();
				}
				i++;
			}
		}
		catch(Exception e){
			fail("Exception was thrown");
		}
	}

	@Test
	public void modulusOps() {
		try{
			int i = 0;
			String [] tStr = {"mod(11,10)", "mod(523,10)"};
			
			String [] rStr = {"1.0", "3.0"};

			while(i < tStr.length){
				myParser.parseExpression(tStr[i]);
				Object value = myParser.getValueAsObject();

				myParser.parseExpression(rStr[i]);
				Object expValue = myParser.getValueAsObject();

				if (value == null || myParser.hasError()) {
					errorStr = myParser.getErrorInfo();
					throw new Exception("Error while evaluating: " + tStr[i]);
				}

				if (!equal(value,expValue)) {
					System.out.println("Failed evaluating:" + tStr[i]);
					Assert.fail();
				}
				i++;
			}
		}
		catch(Exception e){
			fail("Exception was thrown");
		}
	}

}
