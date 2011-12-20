package zen.ilgo.test;

import junit.framework.TestSuite;
import zen.ilgo.cat.CatTest;
import zen.ilgo.utils.MyStringBuilderTest;

public class ToolsTestSuite {

    /**
     * Runs the test suite using the textual runner.
     */
    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }

    public static TestSuite suite() {

        TestSuite suite = new TestSuite();
        
        suite.addTestSuite(CatTest.class);
        suite.addTestSuite(MyStringBuilderTest.class);
        return suite;
    }
}
