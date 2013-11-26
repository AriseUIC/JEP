package org.lsmp.djep;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.lsmp.djep.matrixJep.MatrixJep;
import org.lsmp.djep.vectorJep.VectorJep;
import org.nfunk.jep.JEP;
import org.nfunk.jep.Node;
import org.nfunk.jep.ParseException;

public class MatrixTest {

	JEP jep;

	@Before
	public void setup(){
		jep = new MatrixJep();
		jep.addStandardConstants();
		jep.addStandardFunctions();
		jep.addComplex();
		jep.setAllowAssignment(true);
		jep.setAllowUndeclared(true);
		jep.setImplicitMul(true);
		((MatrixJep) jep).addStandardDiffRules();
	}
	
	public void evalNum(String expr,double dval) throws ParseException{

		Double d = new Double(dval);
		Node n = jep.parse(expr);
		Node matEqn = ((MatrixJep) jep).preprocess(n);
		Object val = ((MatrixJep) jep).evaluate(matEqn);
		
		assertEquals("<"+ expr +">",(Object) d, val);
		System.out.println("Evaluated: Value of \""+expr+"\" is "+dval+"");

	}

	public void evalStr(String expr,String expected) throws ParseException{

		Node n = jep.parse(expr);
		Node matEqn = ((MatrixJep) jep).preprocess(n);
		Object val = ((MatrixJep) jep).evaluate(matEqn);
		
		assertEquals("<"+ expr +">",expected, val.toString());
		System.out.println("Evaluated: Value of \""+expr+"\" is "+expected+"");

	}
	
	@Test
	public void MatrixTest() throws ParseException{
		jep.getSymbolTable().clearValues();
		evalNum("x=2",2);
		evalNum("y=4",4);
		evalNum("z=0",0);
		evalNum("x*y", 8.0);
	}
	
	@Test
	public void MatrixMultiplicationTest() throws ParseException{
		String [] expr = {"x=2", "y=[x^3,x^2,x]", "z=diff(y,x)", "3*y",
				"y*4","y*z"};
		String [] results = {"2.0","[8.0,4.0,2.0]", "[12.0,4.0,1.0]","[24.0,12.0,6.0]",
				"[32.0,16.0,8.0]","[[96.0,32.0,8.0],[48.0,16.0,4.0],[24.0,8.0,2.0]]"};
		
		int i = 0;
		
		while(i < expr.length){
			evalStr(expr[i], results[i]);
			i++;
		}
		
	}
	
	@Test
	public void MatrixLengthTest() throws ParseException{
		
		String [] expr = {"len([1,2,3,4,5,6])","len([[1,2,3],[4,5,6]])","size(5)",
				"size([[[1,2],[3,4],[5,6]],[[7,8],[9,10],[11,12]]])"};
		String [] results = {"6", "6", "1", "[2,3,2]"};
		int i =0;
		
		while(i < expr.length){
			evalStr(expr[i], results[i]);
			i++;
		}
	}
	
	@Test
	public void MatrixOps() throws ParseException{
		String [] expr = {"[1,2,3]==[1,2,3]","diag([1,2,3])", "getdiag([[1,2],[3,4]])",
				"trans([[1,2],[3,4]])"};
		String [] results = {"1.0","[[1.0,0.0,0.0],[0.0,2.0,0.0],[0.0,0.0,3.0]]","[1.0,4.0]",
				"[[1.0,3.0],[2.0,4.0]]"};
		int i = 0;
		
		while(i < expr.length){
			evalStr(expr[i], results[i]);
			i++;
		}
	}
	
	@Test
	public void MatrixComparision() throws ParseException{
		String [] expr = {"[1,2,3]==[1,2,3]","[1,2,3]==[1,2,4]"};
		double [] results = {1,0};
		int i = 0;
		while(i < expr.length){
			evalNum(expr[i], results[i]);
			i++;
		}
	}
	
	@Test
	public void MatrixSum() throws ParseException{
		evalStr("Sum([x,x^2],x,1,10)","[55.0,385.0]");
	}
}
