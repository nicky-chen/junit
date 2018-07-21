import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestResult;
import junit.textui.TestRunner;

import static junit.textui.TestRunner.EXCEPTION_EXIT;
import static junit.textui.TestRunner.FAILURE_EXIT;
import static junit.textui.TestRunner.SUCCESS_EXIT;

/**
 * @author nicky_chin [shuilianpiying@163.com]
 * @since --created on 2018/2/28 at 14:14
 */
public class CalculatorTest extends TestCase {


    private Calculator calculator = null;

    public static void main(String args[]) {
        TestRunner aTestRunner= new TestRunner();
        try {
            TestResult r= aTestRunner.start(new String[]{"CalculatorTest"});
            if (!r.wasSuccessful())
                System.exit(FAILURE_EXIT);
            System.exit(SUCCESS_EXIT);
        } catch(Exception e) {
            System.err.println(e.getMessage());
            System.exit(EXCEPTION_EXIT);
        }
    }

    @Override
    public void setUp() throws Exception
    {
        System.out.println("set up");
        // 生成成员变量的实例
        calculator = new Calculator();
    }

    public void testAdd() {
        System.out.println("testAdd");
        int result = calculator.add(1, 2);
        System.out.println(result);
        // 判断方法的返回结果
        Assert.assertEquals(3, result);// 第`一个参数是期望值，第二个参数是要验证的值
    }

    public void testSubtract() {
        System.out.println("testSubtract");
        int result = calculator.subtract(1, 2);
        // 判断方法的返回结果
        Assert.assertEquals(-1, result);// 第一个参数是期望值，第二个参数是要验证的值

    }

    public void testMultiply() {
        System.out.println("testMultiply");
        int result = calculator.multiply(2, 3);
        // 判断方法的返回结果
        Assert.assertEquals(5, result);// 第一个参数是期望值，第二个参数是要验证的值

    }

    public void testDivide() {
        System.out.println("testDivide");
        int result = calculator.divide(12, 3);
        // 判断方法的返回结果
        Assert.assertEquals(4, result);// 第一个参数是期望值，第二个参数是要验证的值

    }

}
