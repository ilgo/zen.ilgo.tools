package zen.ilgo.cat;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import junit.framework.TestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import zen.ilgo.cat.Cat;

public class CatTest extends TestCase{

    private static final String CAT_FILE = "testRes/resources/CatTest";
    private static final String CATTED = "testRes/expected/catted";
    private File catTest;
    private File catted;
    private int len;
    private Cat instance;    
    
    @Before
    public void setUp() throws Exception {        
        
        catted = new File(CATTED);
        len = (int) catted.length();
        
        catTest = new File(CAT_FILE);
        instance = new Cat(catTest);          
    }
    
    @After
    public void tearDown() throws Exception {
        
        catTest.delete();
    }

    @Test
    public void testAddCollectionOfFile() {
          
        try {
            instance.add(getTestFiles());
            instance.flush();
            
            assertEquals(len, catted.length());
            
            byte[] expect = new byte[(int) catted.length()]; 
            RandomAccessFile raf1 = new RandomAccessFile(catted, "r");
            raf1.readFully(expect);
            raf1.close();
            
            byte[] result = new byte[len];
            RandomAccessFile raf2 = new RandomAccessFile(CAT_FILE, "r");
            raf2.readFully(result);
            raf2.close();
            
            int hash1 = Arrays.hashCode(expect);
            int hash2 = Arrays.hashCode(result);
            assertEquals(hash1, hash2);
            
            
        } catch (IOException e) {            
            e.printStackTrace();
            fail("TestFiles exception");
        }        
    }
    
    private final List<File> getTestFiles() {
        
        String prefix = "testRes/resources/";
        
        List<File> testFiles = new ArrayList<File>();
        testFiles.add(new File(prefix + "black.png"));
        testFiles.add(new File(prefix + "blue.png"));
        testFiles.add(new File(prefix + "green.png"));
        testFiles.add(new File(prefix + "red.png"));
        
        return testFiles;   
    }

}
