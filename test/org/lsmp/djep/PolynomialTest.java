package org.lsmp.djep;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.lsmp.djep.djep.DJep;
import org.lsmp.djep.sjep.PNodeI;
import org.lsmp.djep.sjep.PolynomialCreator;
import org.lsmp.djep.xjep.XOperator;
import org.nfunk.jep.Node;
import org.nfunk.jep.OperatorSet;
import org.nfunk.jep.ParseException;

public class PolynomialTest {
	DJep dJep;
	PolynomialCreator pc;
	
	@Before
	public void setup(){
		dJep = new DJep();
		dJep.addStandardConstants();
		dJep.addStandardFunctions();
		dJep.addComplex();
		dJep.setAllowAssignment(true);
		dJep.setAllowUndeclared(true);
		dJep.setImplicitMul(true);
		dJep.addStandardDiffRules();
		pc = new PolynomialCreator(dJep);
	}
	
	public void evalNum(String expr,double dub) throws Exception
	{
		Object expected = new Double(dub);
		Node node = dJep.parse(expr);
		Node n2 = dJep.preprocess(node);
		Object res = dJep.evaluate(n2);
		assertEquals("<"+expr+">",expected,res);
		System.out.println("Evaluated: value of <"+expr+"> is "+res);
	}
	
	public void evalStr(String expr,String expected) throws ParseException
	{
		Node node = dJep.parse(expr);
		Node processed = dJep.preprocess(node);
		PNodeI poly = pc.createPoly(processed);
		String res = poly.toString();
		
		Node node2 = dJep.parse(expected);
		Node processed2 = dJep.preprocess(node2);
		PNodeI poly2 = pc.createPoly(processed2);
		String res2 = poly2.toString();

		if(!res2.equals(res))		
			System.out.println("Error: Value of \""+expr+"\" is \""+res+"\" should be \""+res2+"\"");
		assertEquals("<"+expr+">",res2,res);
		System.out.println("Evaluated: Value of \""+expr+"\" is \""+res+"\"");
	}
	
	public void evalExpr(String expr,String expected) throws ParseException
	{
		Node node = dJep.parse(expr);
		Node processed = dJep.preprocess(node);
		PNodeI poly = pc.createPoly(processed);
		String res = poly.toString();
		
		if(!expected.equals(res))		
			System.out.println("Error: Value of \""+expr+"\" is \""+res+"\" should be \""+expected+"\"");
		assertEquals("<"+expr+">",expected,res);
		System.out.println("Evaluted: Value of \""+expr+"\" is \""+res+"\"");
	}
	
	public void expandString(String expr,String expected) throws ParseException
	{
		Node node = dJep.parse(expr);
		Node processed = dJep.preprocess(node);
		PNodeI poly = pc.createPoly(processed);
		PNodeI expand = poly.expand();
		String res = expand.toString();
		
		if(!expected.equals(res))		
			System.out.println("Error: Value of \""+expr+"\" is \""+res+"\" should be \""+expected+"\"");
		assertEquals("<"+expr+">",expected,res);
		System.out.println("Evaluated: Value of \""+expr+"\" is \""+res+"\"");
	}
	
	public Object setValue(String expr) throws ParseException
	{
		Node node = dJep.parse(expr);
		Node n2 = dJep.preprocess(node);
		Object res = dJep.evaluate(n2);
		return res;
	}

	
	@Test
	public void polynomialEqns() throws Exception{
		OperatorSet opSet = dJep.getOperatorSet();
		if(!((XOperator) opSet.getMultiply()).isDistributiveOver(opSet.getAdd()))
			fail("* should be distrib over +");
		if(((XOperator) opSet.getMultiply()).isDistributiveOver(opSet.getDivide()))
			fail("* should not be distrib over /");
		if(((XOperator) opSet.getMultiply()).getPrecedence() > ((XOperator) opSet.getAdd()).getPrecedence())
			fail("* should have a lower precedence than +");
		
		evalNum("T=1",1);
		evalNum("F=0",0);
		setValue("a=F"); 
		setValue("b=F"); 
		setValue("c=F");
		evalNum("(a&&(b||c)) == ((a&&b)||(a&&c))",1);
		evalNum("(a||(b&&c)) == ((a||b)&&(a||c))",1);
	}
	
	
	@Test
	public void SimpleEqns() throws ParseException
	{
		String [] expr = {"(a+b)+c", "(a-b)+c", "(a+b)-c", "(a-b)-c"};
		String [] results = {"a+b+c", "a-b+c", "a+b-c", "a-b-c"};
		int i = 0;
		
		while(i < expr.length){
		evalExpr(expr[i],results[i]);
		i++;
		}
	}
	
	@Test
	public void SimplyfyEqns() throws ParseException
	{
		String [] expr = {"2+3", "0+x", "x*x*x", "3/2", "2*(3+4)", "x+0", "0-x",
				"(x^2)/(x^4)", "1/x", "-2/x"};
		
		String [] results = {"5", "x", "x^3", "1.5", "14", "x", "0-x", "1/x^2",
				"1/x", "-2/x"};
		int i = 0;
		
		while(i<expr.length){
			evalStr(expr[i], results[i]);
			i++;
		}
	}
	
	@Test
	public void expandEqns() throws ParseException,Exception
	{
		String [] expr = {"1+2*(1+x)","6x+3y+4x+3(15x+7y)+40","x*y+2*x","(1+x+y)^2"};
		
		String [] results = {"3+2*x", "40+55*x+24*y", "2*x+x*y", "1+2*x+2*x*y+x^2+2*y+y^2"};
		int i = 0;
		
		while(i< expr.length){
		expandString(expr[i],results[i]);
		i++;
		}
	}
	
}	
