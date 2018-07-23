package junit.framework;

import java.util.Vector;
import java.util.Enumeration;

/**
 * A <code>TestResult</code> collects the results of executing
 * a test case. It is an instance of the Collecting Parameter pattern.
 * The test framework distinguishes between <i>failures</i> and <i>errors</i>.
 * A failure is anticipated and checked for with assertions. Errors are
 * unanticipated problems like an <code>ArrayIndexOutOfBoundsException</code>.
 *
 * @see Test
 */
public class TestResult extends Object {
	protected Vector fFailures; //观察者 失败信息 TestFailure对象
	protected Vector fErrors; //观察者 错误信息 TestFailure 对象
	protected Vector fListeners;//观察者 监听器列表 TestListener
	protected int fRunTests; //总共执行测试用例的次数
	private boolean fStop;
	
	public TestResult() {
		fFailures= new Vector();
		fErrors= new Vector();
		fListeners= new Vector();
		fRunTests= 0;
		fStop= false;
	}
	/**
	 * Adds an error to the list of errors. The passed in exception
	 * caused the error.
	 */
	public synchronized void addError(Test test, Throwable t) {
		fErrors.addElement(new TestFailure(test, t));
		for (Enumeration e= cloneListeners().elements(); e.hasMoreElements(); ) {
			((TestListener)e.nextElement()).addError(test, t);
		}
	}
	/**
	 * Adds a failure to the list of failures. The passed in exception
	 * caused the failure.
	 */
	public synchronized void addFailure(Test test, AssertionFailedError t) {
		fFailures.addElement(new TestFailure(test, t));
		for (Enumeration e= cloneListeners().elements(); e.hasMoreElements(); ) {
			((TestListener)e.nextElement()).addFailure(test, t);
		}
	}
	/**
	 * Registers a TestListener
	 */
	public synchronized void addListener(TestListener listener) {
		fListeners.addElement(listener);
	}
	/**
	 * Unregisters a TestListener
	 */
	public synchronized void removeListener(TestListener listener) {
		fListeners.removeElement(listener);
	}
	/**
	 * Returns a copy of the listeners.
	 */
	private synchronized Vector cloneListeners() {
		return (Vector)fListeners.clone();
	}
	/**
	 * Informs the result that a test was completed.
	 */
	public void endTest(Test test) {
		for (Enumeration e= cloneListeners().elements(); e.hasMoreElements(); ) {
			((TestListener)e.nextElement()).endTest(test);
		}
	}
	/**
	 * Gets the number of detected errors.
	 */
	public synchronized int errorCount() {
		return fErrors.size();
	}
	/**
	 * Returns an Enumeration for the errors
	 */
	public synchronized Enumeration errors() {
		return fErrors.elements();
	}
	/**
	 * Gets the number of detected failures.
	 */
	public synchronized int failureCount() {
		return fFailures.size();
	}
	/**
	 * Returns an Enumeration for the failures
	 */
	public synchronized Enumeration failures() {
		return fFailures.elements();
	}
	/**
	 * Runs a TestCase. 运行测试用例
	 */
	protected void run(final TestCase test) {
		startTest(test);
		Protectable p= new Protectable() {
			public void protect() throws Throwable {
				test.runBare();
			}
		};
		runProtected(test, p);//执行test方法

		endTest(test);
	}
	/**
	 * Gets the number of run tests.
	 */
	public synchronized int runCount() {
		return fRunTests;
	}
	/**
	 * Runs a TestCase.
	 */
	public void runProtected(final Test test, Protectable p) {
		try {
			p.protect();
		} 
		catch (AssertionFailedError e) {
			addFailure(test, e); //如果计算结果和assert预期结果不一样，则出现这个错误
		}
		catch (ThreadDeath e) { // don't catch ThreadDeath by accident
			throw e;
		}
		catch (Throwable e) {
			addError(test, e);
		}
	}
	/**
	 * Checks whether the test run should stop
	 */
	public synchronized boolean shouldStop() {
		return fStop;
	}
	/**
	 * Informs the result that a test will be started. 通知result 测试用例即将开始
	 */
	public void startTest(Test test) {
		final int count= test.countTestCases(); //计算执行测试用例的个数
		synchronized(this) {
			fRunTests+= count;
		}
		for (Enumeration e= cloneListeners().elements(); e.hasMoreElements(); ) {
			((TestListener)e.nextElement()).startTest(test); //执行测试
		}
	}
	/**
	 * Marks that the test run should stop.
	 */
	public synchronized void stop() {
		fStop= true;
	}
	/**
	 * Returns whether the entire test was successful or not.
	 */
	public synchronized boolean wasSuccessful() {
		return failureCount() == 0 && errorCount() == 0;
	}
}
