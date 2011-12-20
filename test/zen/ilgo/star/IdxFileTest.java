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

public class IdxFileTest {

    private File file;
    private Map<String, Integer[]> idxData;
    private byte[] bytes;

    @Before
    public void setUp() throws Exception {
        file = new File("./file.idx");
        file.createNewFile();
        idxData = new HashMap<String, Integer[]>();
        idxData.put("A1", new Integer[] { 123, 10 });
        idxData.put("kañhi", new Integer[] { 11, 1000 });
        idxData.put("熱力效能", new Integer[] { 9876543, 2301 });
        idxData.put("Z1", new Integer[] { Integer.MAX_VALUE, 1000000 });
        
        bytes = new byte[] { 65, 49, 0, 0, 0, 0, 123, 0, 0, 0, 10, 90, 49, 0, 127, -1, -1, -1, 0,
                15, 66, 64, 107, 97, -61, -79, 104, 105, 0, 0, 0, 0, 11, 0, 0, 3, -24, -25, -122,
                -79, -27, -118, -101, -26, -107, -120, -24, -125, -67, 0, 0, -106, -76, 63, 0, 0,
                8, -3 };
    }

    @After
    public void tearDown() throws Exception {
        file.delete();
    }

    @Test
    public void testIdxFile() {
        IdxFile idxFile = new IdxFile(file, idxData);
        assertNotNull("IdxFile must be instantiated with a filename and the index data", idxFile);
        
        idxFile = new IdxFile(file);
        assertTrue("file only constructor must have empty data", idxFile.getIdxData().isEmpty());
    }

    @Test
    public void testGetIdxData() {
        Map<String, Integer[]> setData = new HashMap<String, Integer[]>(idxData);
        IdxFile idxFile = new IdxFile(file, setData);
        Map<String, Integer[]> getData = idxFile.getIdxData();

        assertTrue("idxData must contain same mappings for get and set", setData.equals(getData));
    }

    @Test
    public void testWriteBinary() {

        IdxFile idxFile = new IdxFile(file, idxData);

        FileInputStream bis;
        try {
            idxFile.writeBinary();
            bis = new FileInputStream(file);
            byte[] fileBytes = new byte[bytes.length];
            bis.read(fileBytes);

            assertTrue("binary idx data is incorrect", Arrays.equals(fileBytes, bytes));
        } catch (FileNotFoundException e) {
            fail("idxFile FileNotFoundException");
        } catch (IOException e) {
            fail("idxFile IOException");
        }
    }

    @Test
    public void testReadBinary() {
        
        try {
            Map<String, Integer[]> oldData = new HashMap<String, Integer[]>(idxData);
            
            IdxFile idxFile = new IdxFile(file, idxData);
            idxFile.writeBinary();            

            idxFile.readBinary();                
            Map<String, Integer[]> newData = idxFile.getIdxData();
            
            assertTrue("Reading from binary idxFile, must return same keys", oldData.keySet().equals(newData.keySet()));
            for (String key : oldData.keySet()) {
                Integer[] offLen1 = oldData.get(key);
                Integer[] offLen2 = newData.get(key);                
                assertTrue("Offset & Lentgh must be same for same key", offLen1[0].equals(offLen2[0]) && offLen1[1].equals(offLen2[1]));
            }
            
        } catch (IOException e) {
            fail("Cannot read and write idxFile");
        }
    }
    
    @Test
    public void testCalculateIdxFileSize() {
        
        idxData = new HashMap<String, Integer[]>();
        idxData.put("abcdef", new Integer[] {0, 6});
        idxData.put("kañhi", new Integer[] {7, 6});
        idxData.put("熱力效能", new Integer[] {13, 12});
        idxData.put("1234567890", new Integer[] {25, 10});
        
        IdxFile idxFile = new IdxFile(file, idxData);
        try {
            idxFile.writeBinary();
        } catch (IOException e) {
            fail("Cannot write idxFile");
        }
        assertTrue("Written Bytes and calculated size must be same", file.length() == idxFile.getSize());
    }
}
