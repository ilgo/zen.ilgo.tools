package zen.ilgo.star;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class StardictTest {

    private static String dictName;
    private static String starHome;
    private static String customStarHome;
    
    private Stardict star;
    
    @BeforeClass
    public static void classSetup() {        
        dictName = "test";
        starHome = "./"; 
        customStarHome = "./test/resources";
    }
    
    @Before
    public void setUp() throws Exception {

        File xmlFile = new File(customStarHome, "test.xml");
        star = StarFactory.build(xmlFile);
    }

    @After
    public void tearDown() throws Exception {
        
        String[] endings = new String[] {".dict", ".idx", ".syn", ".ifo"};
        for (String ending : endings) {
            new File(starHome, dictName + ending).delete();
        }
    }

    @Test
    public void testGetDefinitions() {
        
        assertNotNull("star must not be null", star);
        
        Set<String> expected = new HashSet<String>();
        expected.add("[nt.] : water.");
        expected.add("수; SHUI3; 水.0.4; water, liquid, lot5on, juice water, river");
        
        Set<String> definitions = new HashSet<String>(star.getAllDefinitions());
        
        assertTrue("Definition sets must match", definitions.equals(expected));
    }

    @Test
    public void testGetKeysFor() {
        
        List<String> expectKeys0 = Arrays.asList(new String[]{"daka"});
        List<String> expectKeys1 = Arrays.asList(new String[]{"水", "su", "mul"});
        
        assertNotNull("star must not be null", star);
        List<String> definitions = star.getAllDefinitions();
        
        String definition = definitions.get(0);        
        if (definition.startsWith("[nt.]")) {
            List<String> keys = star.getKeysFor(definition);
            assertTrue("Keys must match", expectKeys0.equals(keys));
        }
        
        definition = definitions.get(1);        
        if (definition.startsWith("수;")) {
            List<String> keys = star.getKeysFor(definition);
            assertTrue("Keys must match", expectKeys1.equals(keys));
        }
    }

    @Test
    public void testDefinitionOf() {
        
        List<String> keys = Arrays.asList(new String[]{"水", "su", "mul"});
        String expected = "수; SHUI3; 水.0.4; water, liquid, lot5on, juice water, river";
        for (String key : keys) {
            String definition = star.definitionOf(key);
            assertTrue("definitions for key must match", expected.equals(definition));
        }
        
        String key = "daka";
        expected = "[nt.] : water.";
        String definition = star.definitionOf(key);
        assertTrue("definitions for key must match", expected.equals(definition));
        
        key = "12345";
        expected = "";
        definition = star.definitionOf(key);
        assertTrue("not existing definition has no key", expected.equals(definition));
    }

    @Test
    public void testAddEntry() {
        
        String definition = "new Def 1";
        String key = "key1";
        star.addEntry(definition, key);
        
        assertTrue("new def must match", star.definitionOf(key).equals(definition));
        
        definition = "new Def 2";
        List<String> keys = Arrays.asList(new String[]{"key 2", "key 3"});
        star.addEntry(definition, keys);
        
        assertTrue("new def must match", star.definitionOf("key 2").equals(definition));
    }

    @Test
    public void testAddSynonym() {
        
        String synonym = "synonym";
        String key = "daka";
        star.addSynonym(key, synonym);
        
        String def1 = star.definitionOf(key);
        String def2 = star.definitionOf(synonym);
        assertTrue("synonym must have same def as key", def1.equals(def2));
    }

    @Test
    public void testRemoveKey() {
        
        List<String> keys = Arrays.asList(new String[]{"水", "mul"});
        for (String key : keys) {
            star.removeKey(key);
            assertTrue("removed key has no definition", "".equals(star.definitionOf(key)));
        }
        
        try {
            star.removeKey("not existing key");
            assertTrue("removing non-key does not throw exception", true);
        } catch (Exception e) {
            fail("removing non-key throws exception");
        }
    }
    
    @Test
    public void testRemoveDefinition() {
        
        String definition = "수; SHUI3; 水.0.4; water, liquid, lot5on, juice water, river";
        List<String> keys = star.getKeysFor(definition);
        
        star.removeDefinition(definition);
        
        for(String key : keys) {
            assertTrue("key without definition", "".equals(star.definitionOf(key)));
        }
        
        try {
            star.removeDefinition("not existing definition");
            assertTrue("removing non-def does not throw exception", true);
        } catch (Exception e) {
            fail("removing non-def throws exception");
        }
    }
    
    @Test
    public void testUniquesKeys() {
        
        assertTrue("must have unique keys", star.hasUniqueKeys());
        
        String synonym = "wasser";
        String key = "daka";
        star.addSynonym(key, synonym);
        
        assertTrue("must have unique keys", star.hasUniqueKeys());
        
        synonym = "mul";
        key = "daka";
        star.addSynonym(key, synonym);
        
        assertFalse("must have a duplicate key", star.hasUniqueKeys());
    }
}
