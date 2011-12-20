package zen.ilgo.utils;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;

import zen.ilgo.utils.Utils;

public class UtilsTest extends TestCase {

    List<String[]> highUnicodes;
    
    @Before
    public void setUp() throws Exception {
        
        highUnicodes = new ArrayList<String[]>();
        highUnicodes.add(new String[]{"20DD0", "\\ud843\\uddd0"});
        highUnicodes.add(new String[]{"64321", "\\ud950\\udf21"});
        highUnicodes.add(new String[]{"10001", "\\ud800\\udc01"});
        highUnicodes.add(new String[]{"10000", "\\ud800\\udc00"});
        highUnicodes.add(new String[]{"FFFFF", "\\udbbf\\udfff"});
        
        // lower than needed
        highUnicodes.add(new String[]{"FFFF", "\\uffff"});
        highUnicodes.add(new String[]{"FFFE", "\\ufffe"});
        highUnicodes.add(new String[]{"0", "\\u0"});
    }

    @Test
    public void testGetUtf16() {

        for (String[] pair : highUnicodes) {
            String expected = pair[1];
            String result = Utils.getUtf16(pair[0]);
            assertTrue(expected + ": " + result, expected.equals(result));
        }
    }
}
