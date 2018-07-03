package junit;

import junit.framework.TestCase;

import java.util.Arrays;

/**
 * A helper test case for testing whether the testing method
 * is run.
 */
public class WasRun extends TestCase {
	public boolean fWasRun= false;
		@Override
		protected void runTest() {
			fWasRun= true;
		}

    public static void main(String[] args) {
        System.out.println(1);
        System.out.println(Arrays.toString(args));
    }
}
