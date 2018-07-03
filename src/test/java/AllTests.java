import junit.framework.*;

/**
 * TestSuite that runs all the JUnit tests
 *
 */
public class AllTests {

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}
	
	public static Test suite() {
		TestSuite suite= new TestSuite("Framework Tests");
		suite.addTest(AllTests.suite());
		suite.addTest(AllTests.suite());
		suite.addTest(extensions.AllTests.suite());
		return suite;
	}
}
