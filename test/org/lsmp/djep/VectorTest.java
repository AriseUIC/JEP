package org.lsmp.djep;

import static org.junit.Assert.*;
import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.lsmp.djep.vectorJep.VectorJep;
import org.nfunk.jep.JEP;
import org.nfunk.jep.Node;
import org.nfunk.jep.ParseException;

public class VectorTest{

JEP dJep;
	
	@Before
	public void setUp() {
		dJep = new VectorJep();
		dJep.addStandardConstants();
		dJep.addStandardFunctions();
		dJep.addComplex();
		dJep.setAllowAssignment(true);
		dJep.setAllowUndeclared(true);
		dJep.setImplicitMul(true);
	}
	
	public void evalNum(String expr,double dub) throws Exception
	{
		Object expected = new Double(dub);
		Node n = dJep.parse(expr);
		Object val = dJep.evaluate(n);
		assertEquals("<"+expr+">",expected,val);
		System.out.println("Evaluated: value of <"+expr+"> is "+val);
	}
	
	
	public void evalStr(String expr,String expected) throws Exception
	{
		Node n = dJep.parse(expr);
		Object val = dJep.evaluate(n);
		Assert.assertEquals("<"+expr+">",expected,val.toString());
		System.out.println("Evaluated: Value of \""+expr+"\" is "+val.toString()+"");
	}

	@Test 
	public void testVector1() throws Exception
	{
		String [] expr = {"y=[x^3,x^2,x]",
				"z=[3*x^2,2*x,1]", "w=y^^z","w.y","w.z","sqrt(w . z)",
				"sqrt([3,4] . [3,4])","y+z", "y-z", "3*y", "y*4", "y*z","z*y"};
		String [] results = {"[8.0,4.0,2.0]", "[12.0,4.0,1.0]", "[-4.0,16.0,-16.0]",
				"0.0", "0.0", "0.0", "5.0","[20.0,8.0,3.0]","[-4.0,0.0,1.0]",
				"[24.0,12.0,6.0]", "[32.0,16.0,8.0]", "[[96.0,32.0,8.0],[48.0,16.0,4.0],[24.0,8.0,2.0]]",
				"[[96.0,48.0,24.0],[32.0,16.0,8.0],[8.0,4.0,2.0]]"};
		
		int i = 0;
		dJep.getSymbolTable().clearValues();
		evalNum("x=2",2);
		evalNum("(x*x)*x*(x*x)",32.0);
		
		while( i< expr.length){
			evalStr(expr[i],results[i]);
			i++;
		}
		
		
	}

	
	@Test
	public void testVector2() throws Exception{
		String [] expr = {"x=[[1,2],[3,4]]","y=[1,-1]","x*y",
				"y*x", "x+[y,y]", "ele(y,1)","ele(y,2)", "ele(x,[1,1])", "ele(x,[1,2])",
				"ele(x,[2,1])","ele(x,[2,2])"};
		
		String [] results = {"[[1.0,2.0],[3.0,4.0]]","[1.0,-1.0]","[-1.0,-1.0]",
				"[-2.0,-2.0]","[[2.0,1.0],[4.0,3.0]]","1.0","-1.0","1.0","2.0",
				"3.0","4.0"};
		
		int i = 0;
		
		
		while(i < expr.length){
			evalStr(expr[i], results[i]);
			i++;
		}
	}
	
	@Test
	public void testVector3() throws Exception{
		dJep.getSymbolTable().clearValues();
		evalNum("x=2",2);
		dJep.evaluate(dJep.parse("y=[cos(x),sin(x)]"));
		dJep.evaluate(dJep.parse("z=[-sin(x),cos(x)]"));
		evalStr("y . y","1.0");
		evalStr("y . z","0.0");
		evalStr("z . z","1.0");
	}
	
	
	
	@Test
	public void testLength() throws ParseException,Exception
	{
		String [] expr = {"len(5)","len([1,2,3])", "len([[1,2,3],[4,5,6]])", 
				"diag([1,2,3])","getdiag([[1,2],[3,4]])",
				"det([[1,2],[3,4]])","trace([[1,2],[3,4]])","vsum([[1,2],[3,4]])", 
				"Map(x^3,x,[1,2,3])",};
		String [] results = {"1", "3", "6","[[1.0,0.0,0.0],[0.0,2.0,0.0],[0.0,0.0,3.0]]",
				"[1.0,4.0]", "-2.0", "5.0", "10.0","[1.0,8.0,27.0]"};
		
		int i = 0;
		
		while(i < expr.length){
			evalStr(expr[i], results[i]);
			i++;
		}
	}
	

	@Test
	public void vectorComparision() throws Exception {
		evalNum("[2,4,6]==[2,4,6]",1);
		evalNum("[1,2,3]==[1,2,4]",0);
	}

	
	@Test
	public void testArrayAccess() throws Exception {
		evalStr("a=[1,2,3]","[1.0,2.0,3.0]");
		evalNum("a[2]=4",4);
		evalNum("b=a[2]",4);
		evalNum("b",4);
		evalStr("c=[[1,2],[3,4]]","[[1.0,2.0],[3.0,4.0]]");
		evalNum("c[1,2]=5",5);
		evalStr("c","[[1.0,5.0],[3.0,4.0]]");
		evalNum("c[2,1]",3);
	}
	
	@Test
	public void testElementOperations() throws Exception {
		((VectorJep) dJep).setElementMultiply(true);
		
		String [] expr = {"[1,2,3] == [2,2,2]","[1,2,3] < [2,2,2]",
				"[1,2,3] * [2,2,2]","[1,2,3] / [2,2,2]"};
		
		String [] results = {"[0.0,1.0,0.0]","[1.0,0.0,0.0]","[2.0,4.0,6.0]",
				"[0.5,1.0,1.5]"};
		
		int i = 0; 
		while(i < expr.length){
			evalStr(expr[i],results[i]);
			i++;
		}
   }
	
	@Test
	public void ComplexMatrix() throws Exception {
		
		String [] expr = {"v=[1+i,1-2i]","vsum(v)","m=[[1+i,-1+i],[1-i,-1-i]]",
				"m*v","trans(m)"};
		String [] results = {"[(1.0, 1.0),(1.0, -2.0)]","(2.0, -1.0)",
				"[[(1.0, 1.0),(-1.0, 1.0)],[(1.0, -1.0),(-1.0, -1.0)]]",
				"[(1.0, 5.0),(-1.0, 1.0)]","[[(1.0, 1.0),(1.0, -1.0)],[(-1.0, 1.0),(-1.0, -1.0)]]"};
		
		int i = 0;
		while(i < expr.length){
			evalStr(expr[i],results[i]);
			i++;
		}
	}


}
