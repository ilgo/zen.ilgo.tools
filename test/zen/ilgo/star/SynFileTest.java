package zen.ilgo.star;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class SynFileTest {

    private File file;
    private Map<String, Integer> synData;
    private byte[] bytes;

    @Before
    public void setUp() throws Exception {
        file = new File("./file.syn");
        file.createNewFile();
        synData = new HashMap<String, Integer>();
        synData.put("A1", 123);
        synData.put("kañhi", 123);
        synData.put("熱力效能", 9876543);
        synData.put("Z1", Integer.MAX_VALUE);

        bytes = new byte[] { 65, 49, 0, 0, 0, 0, 123, 90, 49, 0, 127, -1, -1, -1, 107, 97, -61,
                -79, 104, 105, 0, 0, 0, 0, 123, -25, -122, -79, -27, -118, -101, -26, -107, -120,
                -24, -125, -67, 0, 0, -106, -76, 63};
    }

    @After
    public void tearDown() throws Exception {
        file.delete();
    }

    @Test
    public void testConstructorSynFile() {
        SynFile synFile = new SynFile(file, synData);
        assertNotNull("SynFile must be instantiated with a filename and the synonym data", synFile);
        
        synFile = new SynFile(file);
        assertTrue("file only constructor must have empty data", synFile.getSynData().isEmpty());
    }

    @Test
    public void testSetGetSynData() {

        Map<String, Integer> setData = new HashMap<String, Integer>(synData);
        SynFile synFile = new SynFile(file, setData);

        Map<String, Integer> getData = synFile.getSynData();

        assertTrue("syndata contains same mappings for get and set", setData.equals(getData));
    }

    @Test
    public void testWriteBinary() {

        SynFile synFile = new SynFile(file, synData);

        FileInputStream bis;
        try {
            synFile.writeBinary();
            bis = new FileInputStream(file);
            byte[] fileBytes = new byte[bytes.length];
            bis.read(fileBytes);

            assertTrue("binary syndata is incorrect", Arrays.equals(fileBytes, bytes));
        } catch (FileNotFoundException e) {
            fail("synFile FileNotFoundException");
        } catch (IOException e) {
            fail("synFile IOException");
        }
    }
    
    @Test
    public void testReadBinary() {        

        try {
            Map<String, Integer> oldData = new HashMap<String, Integer>(synData);
            
            SynFile synFile = new SynFile(file, synData);
            synFile.writeBinary();            

            synFile.readBinary();                
            Map<String, Integer> newData = synFile.getSynData();
            
            assertTrue("Reading from binary synFile, must return same mapping", oldData.equals(newData));
            
        } catch (IOException e) {
            fail("Cannot read and write synFile");
        }
    }
}
