package zen.ilgo.star;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class DictFileTest {

    private File file;
    private List<String> dictData;
    private List<Integer[]> offsetData;
    private byte[] bytes;
    
    @Before
    public void setUp() throws Exception {
        file = new File("./file.dict");
        file.createNewFile();
        dictData = new ArrayList<String>(Arrays.asList(new String[]{"AAA", "kañhi", "熱力效能", "Tīkā", "CCC"}));
        String s = "AAA" + "kañhi" + "熱力效能" + "Tīkā" + "CCC";
        bytes = s.getBytes();   
        
        offsetData = new ArrayList<Integer[]>();
        offsetData.add(new Integer[]{21,6});        
        offsetData.add(new Integer[]{3,6});        
        offsetData.add(new Integer[]{0,3});
        offsetData.add(new Integer[]{27,3});  
        offsetData.add(new Integer[]{9,12});
    }

    @After
    public void tearDown() throws Exception {
        file.delete();
    } 
    
    @Test
    public void testConstructorDictData() {
        DictFile dictFile = new DictFile(file, dictData);
        assertNotNull("DictFile must be instantiated with a filename and the list of definitions", dictFile);
    }    

    @Test
    public void testSetGetDictData() {
        
        List<String> setData = new ArrayList<String>(dictData); 
        DictFile dictFile = new DictFile(file, setData);        
        List<String> getData = dictFile.getDictData();
        
        Collections.sort(setData);
        Collections.sort(getData);        
        assertTrue("data contains same elements for get and set",setData.equals(getData));
        
        dictFile = new DictFile(file);
        getData = dictFile.getDictData(); 
        assertTrue("file only constructor must have empty data", getData.isEmpty());
    }
    
    @Test
    public void testOffsetDataComparator() {
        
        List<String> setData = new ArrayList<String>(dictData); 
        DictFile dictFile = new DictFile(file, setData); 
        Comparator<Integer[]> comparator = dictFile.getComparator();
        Collections.sort(offsetData, comparator);
        
        assertTrue("Offset data must be ordered", offsetData.get(0)[0] == 0);
        assertTrue("Offset data must be ordered", offsetData.get(1)[0] == 3);
        assertTrue("Offset data must be ordered", offsetData.get(2)[0] == 9);
        assertTrue("Offset data must be ordered", offsetData.get(3)[0] == 21);
        assertTrue("Offset data must be ordered", offsetData.get(4)[0] == 27);
    }

    @Test
    public void testWriteBinary() {
        DictFile dictFile = new DictFile(file, dictData);
        
        FileInputStream bis;
        try {
            dictFile.writeBinary();
            bis = new FileInputStream(file);
            byte[] fileBytes = new byte[bytes.length];
            bis.read(fileBytes);
            
            //System.out.println(Arrays.toString(bytes));
            //System.out.println(Arrays.toString(fileBytes));
            
            assertTrue(Arrays.equals(fileBytes, bytes));
        } catch (FileNotFoundException e) {
            fail("dictFile FileNotFoundException");
        } catch (IOException e) {
            fail("dictFile IOException");
        }        
    }

    @Test
    public void testReadBinary() {        

        try {
            List<String> oldData = new ArrayList<String>(dictData);
            
            DictFile dictFile = new DictFile(file, dictData);
            dictFile.writeBinary();            

            dictFile.readBinary(offsetData);                
            List<String> newData = dictFile.getDictData();
            
            Collections.sort(oldData);
            Collections.sort(newData);
            assertTrue("Reading from binary dictFile, must return same definitions", oldData.equals(newData));
            
        } catch (IOException e) {
            fail("Cannot read and write dictFile");
        }
    }
}
