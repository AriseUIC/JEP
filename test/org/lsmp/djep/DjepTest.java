package org.lsmp.djep;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.lsmp.djep.djep.DJep;
import org.lsmp.djep.djep.DSymbolTable;
import org.lsmp.djep.matrixJep.MatrixJep;
import org.lsmp.djep.xjep.XJep;
import org.nfunk.jep.JEP;
import org.nfunk.jep.Node;
import org.nfunk.jep.ParseException;

public class DjepTest {

	JEP jep = new JEP(); 

	@Before
	public void setup(){
		jep = new DJep();
		jep.addStandardConstants();
		jep.addStandardFunctions();
		jep.addComplex();
		jep.setAllowAssignment(true);
		jep.setAllowUndeclared(true);
		jep.setImplicitMul(true);
		((DJep) jep).addStandardDiffRules();
	}

	public void evalExpr(String expr,String expectedVal) throws ParseException
	{
		XJep xj = (XJep) jep;
		Node node = xj.parse(expr);
		Node processed = xj.preprocess(node);
		Node simp = xj.simplify(processed);
		String res = xj.toString(simp);
		assertEquals("<"+expr+">",expectedVal,res);
		System.out.println("Evaluated: Value of: " +expr+ " is "+res);
	}

	public void evalNum(String expr,double dval) throws ParseException{

		Double d = new Double(dval);
		Node n = jep.parse(expr);
		Object val = jep.evaluate(n);

		assertEquals("<"+ expr +">",(Object) d, val);
		System.out.println("Evaluated Value of: " +expr+ " is "+dval+"");

	}

	public void evalStr(String expr,String expected) throws ParseException{

		Node n = jep.parse(expr);
		Object val = jep.evaluate(n);

		assertEquals("<"+ expr +">",expected, val.toString());
		System.out.println("Evaluated: Value of \""+expr+"\" is "+expected+"");

	}

	@Test
	public void DiffOps(){
		String expr = "diff(x^2,x)";
		String expected = "2 x";

		try {
			XJep xj = (XJep) jep;
			Node node2;

			Node node = xj.parse(expr);
			Node processed = xj.preprocess(node);
			Node simp = xj.simplify(processed);
			String res = xj.toString(simp);

			node2 = xj.parse(expected);
			Node processed2 = xj.preprocess(node2);
			Node simp2 = xj.simplify(processed2);
			String res2 = xj.toString(simp2);

			if(!res2.equals(res))		
				System.out.println("Error: Value of \""+expr+"\" is \""+res+"\" should be \""+expected+"\"");
			assertEquals("<"+expr+">",res2,res);
			System.out.println("Evaluate: Value of: " +expr+ "is " +res+ "");
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void DiffAssignOps() throws Exception
	{
		evalExpr("y=x^5","y=x^5.0");
		evalExpr("z=diff(y,x)","z=5.0*x^4.0");
		
		Node n1 = ((DSymbolTable) jep.getSymbolTable()).getPartialDeriv("y",new String[]{"x"}).getEquation();

		assertEquals("<"+ "dy/dx" +">",((DJep) jep).toString(n1),"5.0*x^4.0");
		System.out.println("Evaluated: Value of \""+ "dy/dx" +"\" is "+ "5.0*x^4.0" +"");

		evalExpr("w=diff(z,x)","w=20.0*x^3.0");
		Node n2 = ((DSymbolTable) jep.getSymbolTable()).getPartialDeriv("y",new String[]{"x","x"}).getEquation();


		assertEquals("<"+ "d^2y/dxdx" +">",((DJep) jep).toString(n2),"20.0*x^3.0");
		System.out.println("Evaluated: Value of \""+ "dy/dx" +"\" is "+ "20.0*x^3.0" +"");

		evalNum("x=2",2);
		evalNum("y",32); 
		evalNum("z",80); 
		evalNum("w",160);

	}
	
	public void DiffEqns() throws Exception
	{
		
	}
}

