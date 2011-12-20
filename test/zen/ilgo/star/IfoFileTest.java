package zen.ilgo.star;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class IfoFileTest {

    private final int wordCount = 1234;
    private final int idxFileSize = 223414;
    private final int synWordCount = 23;
    
    private File file;
    private String fileContent;
    
    @Before
    public void setUp() throws Exception {
        file = new File("./file.ifo");
        fileContent = createIfoContent();
    }

    @After
    public void tearDown() throws Exception {
        file.delete();
    }

    @Test
    public void testIfoFile() {
        IfoFile ifoFile = new IfoFile(file, "test");
        
        String book = ifoFile.getBookname();
        assertTrue("Bookname must be set", book != null && !"".equals(book));
        
        String author = ifoFile.getAuthor();
        assertTrue("Author must be set", author != null && !"".equals(author));
        
        String date = ifoFile.getDate();
        assertTrue("Date must be set", date != null && !"".equals(date));
    }
    
    @Test
    public void testIsValid() {
        
        IfoFile ifoFile = new IfoFile(file, "test");        
        assertTrue("Some needed ifo values must not be set, this ifo is not valid.", ! ifoFile.isValid());
        
        ifoFile.setIdxfilesize(idxFileSize);
        ifoFile.setWordcount(wordCount);
        ifoFile.setSynwordcount(synWordCount);
        assertTrue("All needed ifo values must  be set", ifoFile.isValid());
        
        ifoFile = new IfoFile(file);        
        assertTrue("File only constructor is not valid ifo", ! ifoFile.isValid());
    }
    
    @Test
    public void testResetFieldValues() {
        IfoFile ifoFile = new IfoFile(file, "test");  
        ifoFile.resetFieldValues();
        assertTrue("After resetting values, ifo is not valid.", ! ifoFile.isValid());
    }

    @Test
    public void testeIfoContent() {
        
        IfoFile ifoFile = new IfoFile(file, "test");  
        ifoFile.setIdxfilesize(idxFileSize);
        ifoFile.setWordcount(wordCount);
        ifoFile.setSynwordcount(synWordCount);
        ifoFile.setDate("Aug 13, 2011");
        
        String ifo = ifoFile.toString();        
        assertTrue("the ifo content must match the string", ifo.equals(fileContent));
    }
    
    @Test
    public void testWriteIfo() {
        
        IfoFile ifoFile = new IfoFile(file, "test");  
        ifoFile.setIdxfilesize(idxFileSize);
        ifoFile.setWordcount(wordCount);
        ifoFile.setSynwordcount(synWordCount);
        ifoFile.setDate("Aug 13, 2011");

        int fileLen = ifoFile.toString().getBytes().length;

        try {
            ifoFile.writeIfo();
        } catch (IOException e) {
            fail("Cannot write ifo file");
        }
        
        assertTrue("ifo file must be created", file.exists());
        assertTrue("ifo file must be same length as content", file.length() == fileLen);
    }

    @Test
    public void testReadIfo() {
        
        IfoFile ifoFile = new IfoFile(file, "test");  
        ifoFile.setIdxfilesize(idxFileSize);
        ifoFile.setWordcount(wordCount);
        ifoFile.setSynwordcount(synWordCount);
        ifoFile.setDate("Aug 13, 2011");

        String content = ifoFile.toString();
        try {
            ifoFile.writeIfo();            
        } catch (IOException e) {
            fail("Cannot write ifo file in readTest");
        }
        
        ifoFile = new IfoFile(file);
        try {
            ifoFile.readIfo();
        } catch (IOException e) {
            fail("Cannot read ifo file");
        }
        String newContent = ifoFile.toString();        
        assertTrue("old and new content must match", content.equals(newContent));
    }
    
    @Test    
    public void testSetPangoStyle() {
        
        IfoFile ifoFile = new IfoFile(file, "test");  
        ifoFile.setPangoStyle(true);
        assertTrue("SameTypeSequence must have added 'g' for pango", "mg".equals(ifoFile.getSameTypeSequence()));
    
        ifoFile.setPangoStyle(false);
        assertTrue("SameTypeSequence must m only", "m".equals(ifoFile.getSameTypeSequence()));
    }
    
    private String createIfoContent() {
        
        String sep = System.getProperty("line.separator");
        String fmt = new String("%s=%s" + sep);
        StringBuilder sb = new StringBuilder();
        sb.append("StarDict's dict ifo file" + sep);
        sb.append(String.format(fmt, "version", "2.4.2"));
        sb.append(String.format(fmt, "bookname", "test"));
        sb.append(String.format(fmt, "wordcount", Integer.toString(wordCount)));
        sb.append(String.format(fmt, "idxfilesize", Integer.toString(idxFileSize)));
        sb.append(String.format(fmt, "synwordcount", Integer.toString(synWordCount)));
        sb.append(String.format(fmt, "author", "ilgo"));
        sb.append(String.format(fmt, "email", ""));
        sb.append(String.format(fmt, "website", ""));
        sb.append(String.format(fmt, "description", ""));
        sb.append(String.format(fmt, "date", "Aug 13, 2011"));
        sb.append(String.format(fmt, "sametypesequence", "m"));
        
        return sb.toString();
        
    }
}
