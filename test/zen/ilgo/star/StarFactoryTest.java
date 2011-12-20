package zen.ilgo.star;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.text.DateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import zen.ilgo.star.StarFactory.StarTuple;
import zen.ilgo.star.xml.Entries;
import zen.ilgo.star.xml.Entry;
import zen.ilgo.star.xml.Meta;
import zen.ilgo.star.xml.ObjectFactory;

public class StarFactoryTest {

    @SuppressWarnings("unused")
    private String dictName;
    private String customStarHome;
    private File defaultStarHome;
    private File exportedXmlFile;
    
    
    @Before
    public void setUp() throws Exception {
        
        dictName = "test";
        customStarHome = "./test/resources";
        defaultStarHome = new File(System.getProperty("user.home"), "/.stardict/dic");
        exportedXmlFile = new File(customStarHome, "export.xml");
    }

    @After
    public void tearDown() throws Exception {
        
        String[] endings = new String[] {".dict", ".idx", ".syn", ".ifo", ".idx.oft", ".syn.oft"};
        for (String ending : endings) {
            new File(customStarHome, "testDict" + ending).delete();
            new File(customStarHome, "exported" + ending).delete();
            new File(defaultStarHome, "testDict" + ending).delete();
        }
        exportedXmlFile.delete();
    }

    @Test
    public void testStardictByName() {
        
        Stardict star = StarFactory.build("yusig");
        assertNotNull("StarDict must not be NULL", star);
        assertTrue("Must have valid ifo", star.getIfo().isValid());
        assertTrue("Must have existing keys", star.getAllDefinitions().size() > 0);
        assertTrue("Must have same keys as in ifo wordcount", star.getAllDefinitions().size() <= star.getIfo().getWordcount());        
    }
    
    @Test
    public void testNonExistingStardictByName() {
        
        Stardict star = StarFactory.build("abcde");
        assertNull("Non existing StarDict must be NULL", star);     
    }

    @Test
    public void testStardictByNameAndHome() {
        
        Stardict star = StarFactory.build(customStarHome, "yusig");
        assertNotNull("StarDict must not be NULL", star);
        assertTrue("Must have valid ifo", star.getIfo().isValid());
        assertTrue("Must have existing keys", star.getAllDefinitions().size() > 0);
        assertTrue("Must have same keys as in ifo wordcount", star.getAllDefinitions().size() <= star.getIfo().getWordcount()); 
    }

    @Test
    public void testStardictIfoFile() {
        
        File ifoLocation = new File(defaultStarHome, "test.ifo");
        IfoFile ifo = new IfoFile(ifoLocation);
        Stardict star = StarFactory.build(ifo);
        assertNotNull("StarDict must not be NULL", star);
        assertFalse("Must have not be valid ifo", star.getIfo().isValid());
        assertTrue("Must have not have any keys", star.getAllDefinitions().size() == 0);
    } 
    
    @Test
    public void metaToIfoTest() {
        
        ObjectFactory factory = new ObjectFactory();
        Meta meta = factory.createMeta();
        meta.setAuthor("ilgo");
        meta.setBookname("Tipitaka");
        meta.setDescription("a short test");
        meta.setEmail("ilgo711@gmail.com");
        meta.setWebsite("www.metta.lk/dict");
        
        IfoFile ifo = StarFactory.metaToIfo(meta);
        
        assertTrue("ifo author must be same", meta.getAuthor().equals(ifo.getAuthor()));
        assertTrue("ifo book must be same", meta.getBookname().equals(ifo.getBookname()));
        assertTrue("ifo description must be same", meta.getDescription().equals(ifo.getDescription()));
        assertTrue("ifo email must be same", meta.getEmail().equals(ifo.getEmail()));
        assertTrue("ifo website must be same", meta.getWebsite().equals(ifo.getWebsite()));
        
        assertTrue("ifo idx size must be 0", ifo.getIdxfilesize() == 0);
        assertTrue("ifo word count must be 0", ifo.getWordcount() == 0);
        assertTrue("ifo syn count must be 0", ifo.getSynwordcount() == 0);
        
        XMLGregorianCalendar xmlDate = meta.getDate();
        
        if (xmlDate == null) {
            DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM);    
            String today = df.format(Calendar.getInstance().getTime());
            assertTrue("ifo date must be today", today.equals(ifo.getDate()));
       
        } else {
            String date = xmlDate.toString();
            assertTrue("ifo date must be same", date.equals(ifo.getDate()));
        }
        
        XMLGregorianCalendar calendar;
        try {
            calendar = DatatypeFactory.newInstance().newXMLGregorianCalendarDate(1988, 8, 8, 600);
            meta.setDate(calendar);        
            ifo = StarFactory.metaToIfo(meta);
            xmlDate = meta.getDate();
            
            if (xmlDate == null) {
                DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM);    
                String today = df.format(Calendar.getInstance().getTime());
                assertTrue("ifo date must be today", today.equals(ifo.getDate()));
           
            } else {
                String date = xmlDate.toString();
                assertTrue("ifo date must be same", date.equals(ifo.getDate()));
            }
            
        } catch (DatatypeConfigurationException e) {
            fail("cannot create XML Calendar");
        }
    }    
    
    @Test
    public void entriesToDatamapTest() {
        
        ObjectFactory factory = new ObjectFactory();
        Entries entries = factory.createEntries();
        Entry e1 = factory.createEntry();
        e1.setDefinition("definition 1");
        e1.getKey().addAll(Arrays.asList(new String[]{"key 1", "key 2"}));
        entries.getEntry().add(e1);
        
        Entry e2 = factory.createEntry();
        e2.setDefinition("definition 2");
        e2.getKey().addAll(Arrays.asList(new String[]{"key 3", "key 4", "key"}));
        entries.getEntry().add(e2);
        
        Entry e3 = factory.createEntry();
        e3.setDefinition("definition 3");
        e3.getKey().addAll(Arrays.asList(new String[]{"key 5"}));
        entries.getEntry().add(e3);
        
        Map<String, List<String>> dataMap = StarFactory.entriesToDatamap(entries);
        assertNotNull("dataMap must be created", dataMap);
        assertTrue("must have 3 entries", dataMap.size() == 3);
        assertTrue("def 1 has 2 keys", dataMap.get(e1.getDefinition()).size() == 2);
        assertTrue("def 2 has 3 keys", dataMap.get(e2.getDefinition()).size() == 3);
        assertTrue("def 3 has 1 key", dataMap.get(e3.getDefinition()).size() == 1);
    }

    @Test
    public void testStardictFile() {

        List<String> xmlFiles = Arrays.asList(new String[]{"test.xml","test2.xml"});        
        for (String fileName : xmlFiles) {
            
            File xmlFile = new File(customStarHome, fileName);
            Stardict star = StarFactory.build(xmlFile);
            assertNotNull("StarDict must not be NULL", star);
            assertTrue("Must have an ifo", star.getIfo() != null);
            assertTrue("Must have keys", star.getAllDefinitions().size() > 0);
            assertTrue("Must have 2 definitions", star.getAllDefinitions().size() == 2);
        }
    }
    
    @Test
    public void testDatamapToEntries() {
        
        Map<String, List<String>> dataMap = new HashMap<String, List<String>>();
        dataMap.put("A", Arrays.asList(new String[]{"a1", "a2"}));
        dataMap.put("B", Arrays.asList(new String[]{"b3"}));
        dataMap.put("C", Arrays.asList(new String[]{"c4", "c5", "c6"}));
        
        Entries entries = StarFactory.dataMapToEntries(dataMap);
        for (Entry entry : entries.getEntry()) {
            if (entry.getDefinition().equals("A")) {
                assertTrue("A must have same keys count", entry.getKey().size() == 2);
                assertTrue("A must have same key a1", entry.getKey().contains("a1"));
                assertTrue("A must have same key a2", entry.getKey().contains("a2"));
                
            } else if (entry.getDefinition().equals("B")) {
                assertTrue("B must have same keys count", entry.getKey().size() == 1);
                assertTrue("B must have same key b3", entry.getKey().contains("b3"));
                
            } else if (entry.getDefinition().equals("C")) {
                assertTrue("C must have same keys count", entry.getKey().size() == 3);
                assertTrue("C must have same key c4", entry.getKey().contains("c4"));
                assertTrue("C must have same key c5", entry.getKey().contains("c5"));
                assertTrue("C must have same key c6", entry.getKey().contains("c6"));
                
            } else {
                fail("unknown definition");
            }
        }        
    }

    @Test
    public void testIfoToMeta() {

        String name = "test";
        File ifoLocation = new File(defaultStarHome, name + ".ifo");
        IfoFile ifo = new IfoFile(ifoLocation);
        ifo.setAuthor("ilgo");
        ifo.setBookname("Tipitaka");
        ifo.setDescription("a short test");
        ifo.setEmail("ilgo711@gmail.com");
        ifo.setWebsite("www.metta.lk/dict");
        
        Meta meta = StarFactory.ifoToMeta(ifo);
        
        assertTrue("meta author must be same", meta.getAuthor().equals(ifo.getAuthor()));
        assertTrue("meta book must be same", meta.getBookname().equals(ifo.getBookname()));
        assertTrue("meta description must be same", meta.getDescription().equals(ifo.getDescription()));
        assertTrue("meta email must be same", meta.getEmail().equals(ifo.getEmail()));
        assertTrue("meta website must be same", meta.getWebsite().equals(ifo.getWebsite()));
        
        assertTrue("meta idx size must be 0", ifo.getIdxfilesize() == 0);
        assertTrue("meta word count must be 0", ifo.getWordcount() == 0);
        assertTrue("meta syn count must be 0", ifo.getSynwordcount() == 0);
    }

    @Test
    public void testWriteXml() {
        
        File xmlFile = new File(customStarHome, "test.xml");     
        File exportFile = new File(customStarHome, "export.xml");
        Stardict star1 = StarFactory.build(xmlFile);
        
        StarFactory.export(star1, exportFile);
        Stardict star2 = StarFactory.build(exportFile);
        compareStarDictDataMap(star1, star2);
    }  

    @Test
    public void testCalculateBinaryParts() {
        
        File xmlFile = new File(customStarHome, "test.xml");     
        Stardict star = StarFactory.build(xmlFile); 
        StarTuple binTuple = StarFactory.calculateBinaryParts(star);
        
        assertNotNull("Must return an Object", binTuple);
        assertNotNull("Must have DictFile set", binTuple.dictFile);
        assertNotNull("Must have IdxFile set", binTuple.idxFile);
        assertNotNull("Must have SynFile set", binTuple.synFile);
    }
    
    @Test
    public void testUpdateIfo() {
        
        File xmlFile = new File(customStarHome, "test.xml");     
        Stardict star = StarFactory.build(xmlFile); 
        IfoFile ifoFile = star.getIfo();
        StarTuple binTuple = StarFactory.calculateBinaryParts(star);
        StarFactory.updateIfo(ifoFile, binTuple);
        
        assertTrue("Wordcount is set", ifoFile.getWordcount() == binTuple.dictFile.getDictData().size());
        assertTrue("IdxFileSize must be set", ifoFile.getIdxfilesize() == binTuple.idxFile.getSize());
        if (binTuple.synFile != null) {
            assertTrue("synword count must be set", ifoFile.getSynwordcount() == binTuple.synFile.getSynData().size());
        } else {
            assertTrue("no synFile, synword count must be 0", ifoFile.getSynwordcount() == 0);
        }
        
    }  

    @Test
    public void testWriteBinary() {        
        
        try {
            File xmlFile = new File(customStarHome, "test.xml");     
            Stardict star1 = StarFactory.build(xmlFile);        
            StarFactory.export(star1);        
            Stardict star2 = StarFactory.build("testDict");
            
            compareStarDictDataMap(star1, star2);
            
        } finally {
            String[] endings = new String[] {".dict", ".idx", ".syn", ".ifo"};
            for (String ending : endings) {
                new File(defaultStarHome, "testDict" + ending).delete();
            }
        }          
    }
    
    @Test
    public void testDataMapBinaryPartsCycle() {
        
        File xmlFile = new File(customStarHome, "test.xml");     
        Stardict star = StarFactory.build(xmlFile);
        star.addEntry("AAA", Arrays.asList(new String[]{"A1", "A2"}));
        StarFactory.export(star);
        StarTuple binTu = StarFactory.calculateBinaryParts(star);
        Map<String, List<String>> dataMap = StarFactory.createDataMap(binTu.dictFile, binTu.idxFile, binTu.synFile);
        Stardict star2 = StarFactory.build(star.getIfo());
        star2.setDataMap(dataMap);
        
        compareStarDictDataMap(star, star2);        
    }

    @Test
    public void testWriteBinaryAtCustomLocation() {
        
        try {
            File xmlFile = new File(customStarHome, "test.xml");     
            Stardict star1 = StarFactory.build(xmlFile);        
            StarFactory.export(star1, customStarHome);        
            Stardict star2 = StarFactory.build(customStarHome, "testDict");
            
            compareStarDictDataMap(star1, star2);
            
        } finally {
            String[] endings = new String[] {".dict", ".idx", ".syn", ".ifo"};
            for (String ending : endings) {
                new File(customStarHome, "testDict" + ending).delete();
            }
        } 
    }
    
    /**
     * read write cycle of a real dictionary from binary to xml and back
     */
    @Test
    public void testXmlBinaryCircle() {
        
        Stardict yusig = StarFactory.build(customStarHome, "yusig");
        StarFactory.export(yusig, exportedXmlFile);
        Stardict exported = StarFactory.build(exportedXmlFile);
        exported.getIfo().setBookname("exported");
        StarFactory.export(exported, customStarHome);
        Stardict recycled = StarFactory.build(customStarHome, "exported");
        
        compareStarDictDataMap(yusig, exported);
        compareStarDictDataMap(yusig, recycled);
        compareStarDictDataMap(exported, recycled);
    }
    

    
    private void compareStarDictDataMap(Stardict star1, Stardict star2) {
        
        List<String> defs1 = star1.getAllDefinitions();
        List<String> defs2 = star2.getAllDefinitions();
        Collections.sort(defs1);
        Collections.sort(defs2);
        
        assertTrue("Both dict must have same definitions", defs1.equals(defs2));  
        
        for (String definition : defs1) {
            List<String> keys1 = star1.getKeysFor(definition);
            List<String> keys2 = star2.getKeysFor(definition);
            Collections.sort(keys1);
            Collections.sort(keys2);
            assertTrue("Both dict must have same keys for same def", keys1.equals(keys2));  
        }        
    }
}
