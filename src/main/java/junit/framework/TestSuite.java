package junit.framework;

import java.util.Vector;
import java.util.Enumeration;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.*;
import java.lang.reflect.Constructor;

/**
 * A <code>TestSuite</code> is a <code>Composite</code> of Tests.
 * It runs a collection of test cases. Here is an example using
 * the dynamic test definition.
 * <pre>
 * TestSuite suite= new TestSuite();
 * suite.addTest(new MathTest("testAdd"));
 * suite.addTest(new MathTest("testDivideByZero"));
 * </pre>
 * Alternatively, a TestSuite can extract the tests to be run automatically.
 * To do so you pass the class of your TestCase class to the
 * TestSuite constructor.
 * <pre>
 * TestSuite suite= new TestSuite(MathTest.class);
 * </pre>
 * This constructor creates a suite with all the methods
 * starting with "test" that take no arguments.
 *
 * @see Test
 */
public class TestSuite implements Test {
    //testCase集合
	private Vector fTests= new Vector(10);
	private String fName;

    /**
	 * Constructs an empty TestSuite.
	 */
	public TestSuite() {
	}
	
	/**
	 * Constructs a TestSuite from the given class with the given name.
	 * @see TestSuite#TestSuite(Class)
	 */
	public TestSuite(Class theClass, String name) {
		this(theClass);
		setName(name);
	}
	
	/**
	 * Constructs a TestSuite from the given class. Adds all the methods
	 * starting with "test" as test cases to the suite.
	 * Parts of this method was written at 2337 meters in the H�ffih�tte,
	 * Kanton Uri
	 */
	 public TestSuite(final Class theClass) {
		fName= theClass.getName();
		try {//判断是否有公共的带String参数的构造器或者无参构造器，如果没有打印错误信息
			getTestConstructor(theClass); // Avoid generating multiple error messages
		} catch (NoSuchMethodException e) {
			addTest(warning("Class "+theClass.getName()+" has no public constructor TestCase(String name) or TestCase()"));
			return;
		}
		//判断类的修饰符是否是共有的，如果不是打印错误信息
		if (!Modifier.isPublic(theClass.getModifiers())) {
			addTest(warning("Class "+theClass.getName()+" is not public"));
			return;
		}

		Class superClass= theClass;
		Vector names= new Vector();
		while (Test.class.isAssignableFrom(superClass)) {//判断当前class是否实现或者继承了Test类
			Method[] methods= superClass.getDeclaredMethods();
			for (int i= 0; i < methods.length; i++) {
				addTestMethod(methods[i], names, theClass);
			}
			superClass= superClass.getSuperclass(); //获取父类并重复上述操作
		}
		if (fTests.size() == 0)
			addTest(warning("No tests found in "+theClass.getName()));
	}
	
   	/**
	 * Constructs an empty TestSuite.
	 */
	public TestSuite(String name) {
		setName(name);
	}
	
	/**
	 * Adds a test to the suite.
	 */
	public void addTest(Test test) {
		fTests.addElement(test);
	}

	/**
	 * Adds the tests from the given class to the suite
	 */
	public void addTestSuite(Class testClass) {
		addTest(new TestSuite(testClass));
	}

	private void addTestMethod(Method m, Vector names, Class theClass) {
		String name= m.getName();
		if (names.contains(name))
			return;
		if (! isPublicTestMethod(m)) {//判断是否是无参数无返回值方法，是则加入到 names集合里
			if (isTestMethod(m))
				addTest(warning("Test method isn't public: "+m.getName()));
			return;
		}
		names.addElement(name);
		addTest(createTest(theClass, name));
	}

	/**
	 * ...as the moon sets over the early morning Merlin, Oregon
	 * mountains, our intrepid adventurers type...
	 */
	static public Test createTest(Class theClass, String name) {
		Constructor constructor;
		try {
			constructor= getTestConstructor(theClass);
		} catch (NoSuchMethodException e) {
			return warning("Class "+theClass.getName()+" has no public constructor TestCase(String name) or TestCase()");
		}
		Object test;
		try {
			if (constructor.getParameterTypes().length == 0) {
				test= constructor.newInstance(new Object[0]);
				if (test instanceof TestCase)
					((TestCase) test).setName(name);
			} else {
				test= constructor.newInstance(new Object[]{name});
			}
		} catch (InstantiationException e) {
			return(warning("Cannot instantiate test case: "+name+" ("+exceptionToString(e)+")"));
		} catch (InvocationTargetException e) {
			return(warning("Exception in constructor: "+name+" ("+exceptionToString(e.getTargetException())+")"));
		} catch (IllegalAccessException e) {
			return(warning("Cannot access test case: "+name+" ("+exceptionToString(e)+")"));
		}
		return (Test) test;
	}

	/**
	 * Converts the stack trace into a string
	 */
	private static String exceptionToString(Throwable t) {
		StringWriter stringWriter= new StringWriter();
		PrintWriter writer= new PrintWriter(stringWriter);
		t.printStackTrace(writer);
		return stringWriter.toString();

	}
	
	/**
	 * Counts the number of test cases that will be run by this test.
	 */
	public int countTestCases() {
		int count= 0;
		for (Enumeration e= tests(); e.hasMoreElements(); ) {
			Test test= (Test)e.nextElement();
			count= count + test.countTestCases();
		}
		return count;
	}
	
	/**
	 * Gets a constructor which takes a single String as
	 * its argument or a no arg constructor.
	 */
	public static Constructor getTestConstructor(Class theClass) throws NoSuchMethodException {
		Class[] args= { String.class };
		try {
			return theClass.getConstructor(args);	
		} catch (NoSuchMethodException e) {
			// fall through
		}
		return theClass.getConstructor(new Class[0]);
	}

	private boolean isPublicTestMethod(Method m) {
		return isTestMethod(m) && Modifier.isPublic(m.getModifiers());
	 }
	 
	private boolean isTestMethod(Method m) {
		String name= m.getName();
		Class[] parameters= m.getParameterTypes();
		Class returnType= m.getReturnType();
		return parameters.length == 0 && name.startsWith("test") && returnType.equals(Void.TYPE);
	 }
	 
	/**
	 * Runs the tests and collects their result in a TestResult. 运行测试用例并返回结果集
	 */
	@Override
	public void run(TestResult result) {
		for (Enumeration e= tests(); e.hasMoreElements(); ) {//遍历测试方法
	  		if (result.shouldStop() ) //是否停止继续运行
	  			break;
			Test test= (Test)e.nextElement();
			runTest(test, result);//运行具体测试用例方法
		}
	}

	public void runTest(Test test, TestResult result) {
		test.run(result);
	}

	/**
	 * Returns the test at the given index
	 */
	public Test testAt(int index) {
		return (Test)fTests.elementAt(index);
	}
	
	/**
	 * Returns the number of tests in this suite
	 */
	public int testCount() {
		return fTests.size();
	}
	
	/**
	 * Returns the tests as an enumeration
	 */
	public Enumeration tests() {
		return fTests.elements();
	}
	
	/**
	 */
	public String toString() {
		if (getName() != null)
			return getName();
		return super.toString();
	 }
	 
	/**
	 * Sets the name of the suite.
	 * @param name The name to set
	 */
	public void setName(String name) {
		fName= name;
	}

	/**
	 * Returns the name of the suite. Not all
	 * test suites have a name and this method
	 * can return null.
	 */
	public String getName() {
		return fName;
	}

	/**
	 * Returns a test which will fail and log a warning message.
	 */
	private static Test warning(final String message) {
		return new TestCase("warning") {
			protected void runTest() {
				fail(message);
			}
		};
	}
}
