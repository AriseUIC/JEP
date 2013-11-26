package org.lsmp.djep;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.lsmp.djep.matrixJep.MatrixJep;
import org.lsmp.djep.mrpe.MRpCommandList;
import org.lsmp.djep.mrpe.MRpEval;
import org.lsmp.djep.mrpe.MRpRes;
import org.lsmp.djep.vectorJep.values.MVector;
import org.lsmp.djep.vectorJep.values.Matrix;
import org.lsmp.djep.vectorJep.values.MatrixValueI;
import org.nfunk.jep.Node;
import org.nfunk.jep.ParseException;
import org.nfunk.jep.type.Complex;

public class MatrixTest2 {
	MatrixJep mJep;
	String matStrs[][] = new String[10][10];
	String vecStrs[] = new String[10];

	@Before
	public void setUp() {
		mJep = new MatrixJep();
		mJep.addStandardConstants();
		mJep.addStandardFunctions();
		mJep.addComplex();
		mJep.setAllowAssignment(true);
		mJep.setAllowUndeclared(true);
		mJep.setImplicitMul(true);
		mJep.addStandardDiffRules();

		for(int i=2;i<=9;++i)
			for(int j=2;j<=9;++j)
			{
				int num=1;
				StringBuffer sb = new StringBuffer("[");
				for(int k=0;k<i;++k)
				{
					if(k>0)sb.append(",");
					sb.append("[");
					for(int l=0;l<j;++l)
					{
						if(l>0)sb.append(",");
						sb.append(String.valueOf(num++));
					}
					sb.append("]");
				}
				sb.append("]");
				matStrs[i][j] = sb.toString();
			}

		for(int i=2;i<=9;++i)
		{
			int num=1;
			StringBuffer sb = new StringBuffer("[");
			for(int k=0;k<i;++k)
			{
				if(k>0)sb.append(",");
				sb.append(String.valueOf(num++));
			}
			sb.append("]");
			vecStrs[i] = sb.toString();
		}
	}

	public void myAssertEquals(String msg,String actual,String expected)
	{
		if(!actual.equals(expected))
			System.out.println("Error \""+msg+"\" is \n<"+actual+"> should be \n<"+expected+">");
		assertEquals("<"+msg+">",expected,actual);
		System.out.println("Evaluated: Value of <"+msg+"> is <"+actual+">");
	}

	void matTest(String eqns[], String eqn2) throws ParseException
	{
		for(int i=0;i<eqns.length;++i)	{
			System.out.println("eqns "+eqns[i]);
			Node node = mJep.simplify(mJep.preprocess(mJep.parse(eqns[i])));
			mJep.evaluate(node);
		}
		
		Node node3 = mJep.simplify(mJep.preprocess(mJep.parse(eqn2)));
		MRpEval rpe = new MRpEval(mJep);
		MRpCommandList list = rpe.compile(node3);
		MRpRes rpRes = rpe.evaluate(list);
		MatrixValueI mat = rpRes.toVecMat();

		Object matRes = mJep.evaluateRaw(node3);
		if(mJep.hasError())
			fail("Evaluation Failure: "+eqn2+mJep.getErrorInfo());
		myAssertEquals("<"+eqn2+">",rpRes.toString(),matRes.toString());

		if(!mat.equals(matRes))
			fail("Expected <"+matRes+"> found <"+mat+">");

		if(rpRes.getDims().is1D())
		{
			double vecArray[] = (double []) rpRes.toArray();
			for(int i=0;i<vecArray.length;++i)
				if(vecArray[i] != ((Double) ((MVector) matRes).getEle(i)).doubleValue())
					fail("Problem with toArray");
		}
		else if(rpRes.getDims().is2D())
		{
			double matArray[][] = (double [][]) rpRes.toArray();
			for(int i=0;i<matArray.length;++i)
				for(int j=0;j<matArray[i].length;++j)
					if(matArray[i][j] != ((Double) ((Matrix) matRes).getEle(i,j)).doubleValue())
						fail("Problem with toArray");
		}
		rpe.cleanUp();
	}

	void matTest2(String eqns[]) throws ParseException
	{
		Node nodes[] = new Node[eqns.length];
		MatrixValueI rpMats[] = new MatrixValueI[eqns.length];
		MRpEval rpe = new MRpEval(mJep);
		for(int i=0;i<eqns.length;++i)	{
			System.out.println("eqns "+eqns[i]);
			nodes[i] = mJep.simplify(mJep.preprocess(mJep.parse(eqns[i])));
			MRpCommandList list = rpe.compile(nodes[i]);
			MRpRes rpRes = rpe.evaluate(list);
			rpMats[i] = rpRes.toVecMat();
			System.out.println("<"+eqns[i]+"> "+rpRes.toString());
		}
		for(int i=0;i<eqns.length;++i)	{
			Object matRes = mJep.evaluateRaw(nodes[i]);
			if(!rpMats[i].equals(matRes))
				fail("Expected <"+matRes+"> found <"+rpMats[i]+">");
		}		
		rpe.cleanUp();
	}

	@Test
	public void testMat1() throws ParseException
	{
		String [] [] exp = new String [][]{{"y=[[1,2,3],[4,5,6],[7,8,9]]"},{"x=[1,2]","y=[[1,2],[3,4]]"},
		{"x=[1,2,3]","y=[[1,2,3],[4,5,6],[7,8,9]]"}, {"y=[1,2,3,4]"},{"y=[[1,2],[3,4]]"}, {"y=[1,2,3]"},
		{"y=[1,2,3]"},{"x1=1","x2=2","x3=3","x4=4","x5=5","x6=6","x7=7","x8=8","x9=9"},{"y=[1,2,3,4]"}};
		String [] results = {"y*y+y","x*y","x*y","y+y","y*y+y","y^^y","y . y","x1*x2*x3+x4*x5*x6+x7*x8*x9",
				"y-y"};
		int i = 0;
		
		for(String[] s:exp){
				matTest(s, results[i]);
				i++;
		}
	}
	
	

	@Test
	public void testRpAllDim() throws ParseException
	{

		for(int i=2;i<=4;++i)
			for(int j=2;j<=4;++j)
			{
				int num=1;
				StringBuffer sb = new StringBuffer("x=[");
				for(int k=0;k<i;++k)
				{
					if(k>0)sb.append(",");
					sb.append("[");
					for(int l=0;l<j;++l)
					{
						if(l>0)sb.append(",");
						sb.append(String.valueOf(num++));
					}
					sb.append("]");
				}
				sb.append("]");
				String varStr = sb.toString();
				matTest(new String[]{varStr},"x+x");
				matTest(new String[]{varStr},"x-x");

				matTest(new String[]{varStr},"3*x");
				matTest(new String[]{varStr},"x*5");
				matTest(new String[]{varStr},"-x");
			}
	}

	@Test
	public void testMul() throws ParseException
	{
		String [] [] exp = new String [][]{{"x=[1,2]","y="+matStrs[2][2]},
				{"x=[1,2,3,4]","y="+matStrs[4][3]},{"x=[1,2]","y="+matStrs[4][2]},
				{"x="+matStrs[2][2],"y="+matStrs[2][3]},{"x="+matStrs[2][4],"y="+matStrs[4][4]},
				{"x="+matStrs[3][4],"y="+matStrs[4][2]},{"x="+matStrs[4][4],"y="+matStrs[4][3]},
				{"x="+matStrs[2][3],"y="+matStrs[3][4]},{"x="+matStrs[3][2],"y="+matStrs[2][2]},
				{"x="+matStrs[3][3],"y="+matStrs[3][4]},{"x="+matStrs[3][4],"y="+matStrs[4][2]},
				{"x="+matStrs[4][2],"y="+matStrs[2][4]},{"x="+matStrs[4][3],"y="+matStrs[3][3]},
				{"x="+matStrs[4][4],"y="+matStrs[4][4]}};
		
		String [] results = {"x*y","x*y","y*x","x*y","x*y", "x*y", "x*y","x*y","x*y","x*y","x*y",
				"x*y","x*y","x*y"};
		
		int i = 0;
		
		for(String[] s:exp){
				matTest(s, results[i]);
				i++;
		}
	}

	@Test
	public void testAssign() throws ParseException
	{
		matTest2(new String[]{"x=[[5,6],[7,8]]","x+x"});
		matTest2(new String[]{"x=[5,6]","x+x"});
		matTest2(new String[]{"x=[5,6,7]","x+x"});
		matTest2(new String[]{"x=[5,6,7,8]","x+x"});
		matTest2(new String[]{"x=5","x+x"});

		for(int i=2;i<=4;++i)
			for(int j=2;j<=4;++j)
			{
				matTest2(new String[]{"x="+matStrs[i][j],"x+x"});
			}
	}

	@Test
	public void testLogical() throws ParseException
	{
		matTest2(new String[]{"1&&1","1&&0","0&&0","0&&1","3.14&&1"});
		matTest2(new String[]{"!0","!1","!3.14","!-3.14"});

		matTest2(new String[]{"1>1","1>0","0>0","0>1","3.14>1"});
		matTest2(new String[]{"1<=1","1<=0","0<=0","0<=1","3.14<=1"});
		matTest2(new String[]{"[1,2]==[1,2]"});		
		matTest2(new String[]{"[1,2]!=[5,6]"});		
		matTest2(new String[]{"[1,2,3,4]==[1,2,3,4]"});				
		matTest2(new String[]{"[1,2,3,4]!=[5,6,7,8]"});	
		matTest2(new String[]{matStrs[2][2]+"=="+matStrs[2][2]});	
		matTest2(new String[]{matStrs[2][2]+"=="+matStrs[2][2]});	
		matTest2(new String[]{matStrs[2][2]+"!="+matStrs[2][2]});	
		matTest2(new String[]{matStrs[2][2]+"!="+matStrs[2][2]});	
		matTest2(new String[]{matStrs[2][2]+"!="+matStrs[2][2]});	
	}

	boolean TESTALL = false;

	@Test
	public void testVn() throws ParseException {
		matTest2(new String[]{"x=[5,6,7,8,9]","x+x","x-x","2*x","x*3","x.x"});
		matTest2(new String[]{"x=[[1,2,3,4,5],[5,6,7,8,9]]","x+x","x-x","2*x","x*3"});
		matTest2(new String[]{"x=[[1,2],[3,4]]","y=[[1,2,3,4,5],[5,6,7,8,9]]","x*y"});
		matTest2(new String[]{"x=[[1,2],[3,4]]","y=[[1,2],[3,4],[5,6],[7,8],[9,10]]","y*x"});
		matTest2(new String[]{"x=[[1,2,3,4,5],[5,6,7,8,9]]","y=[[1,2],[3,4],[5,6],[7,8],[9,10]]","y*x"});
	}

	@Test
	public void testFun() throws ParseException {
		matTest2(new String[]{"x=5","y=4","x/y","x%y","x^y"});
		matTest2(new String[]{"x=0.5","cos(x)","sin(x)","tan(x)","asin(x)","acos(x)","atan(x)"});
		matTest2(new String[]{"x=0.5","cosh(x)","sinh(x)","tanh(x)","asinh(x)","acosh(x+1)","atanh(x)"});
		matTest2(new String[]{"x=0.5","sqrt(x)","ln(x)","log(x)","exp(x)","abs(x)"});
		matTest2(new String[]{"x=0.5","sec(x)","cosec(x)","cot(x)"});
	}

	@Test
	public void testUndecVar() throws ParseException {
		mJep.setAllowUndeclared(true);
		MRpEval rpe = new MRpEval(mJep);
		Node node1 = mJep.parse("zap * gosh");
		Node node3 = mJep.preprocess(node1);
		rpe.compile(node3);
	}
}
