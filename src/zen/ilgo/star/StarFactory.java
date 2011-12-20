package zen.ilgo.star;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.xml.sax.SAXException;

import zen.ilgo.star.xml.Dictionary;
import zen.ilgo.star.xml.Entries;
import zen.ilgo.star.xml.Meta;
import zen.ilgo.star.xml.ObjectFactory;

/**
 * Creates instances of Stardict
 * Imports and Exports stardict to and from binary, to and from xml file 
 *
 * @author roger holenweger (ilgo711@gmail.com)
 * @since Aug 16, 2011
 */
public class StarFactory {

    public static final String STAR_HOME = System.getProperty("user.home") + "/.stardict/dic";
    public static final String STAR_XSD = "resources/stardict.xsd";
    
    /**
     * Loads a dictionary from default starHome;
     * 
     * @param name of the dictionary to load
     */
    public static Stardict build(String name) {
        
        return build(STAR_HOME, name);
    }

    /**
     * load a dictionary from a custom location
     * 
     * @param starHome the directory where the dict can be found
     * @param name the name of the dictionary
     */
    public static Stardict build(String starHome, String name) {

        IfoFile ifoFile = new IfoFile(new File(starHome, name + ".ifo"));
        try {            
            ifoFile.readIfo();
        } catch (IOException e) {
            System.out.println(e.getMessage());
            return null;
        }
        
        Map<String, List<String>> dataMap = null;    
        try {
            IdxFile idxFile = new IdxFile(new File(starHome, name + ".idx"));  
            idxFile.readBinary();
            SynFile synFile = new SynFile(new File(starHome, name + ".syn"));
            synFile.readBinary();
            DictFile dictFile = new DictFile(new File(starHome, name + ".dict"));
            
            List<Integer[]> offsetData = new ArrayList<Integer[]>(idxFile.getIdxData().values());
            dictFile.readBinary(offsetData);
            dataMap = createDataMap(dictFile, idxFile, synFile);
        
        } catch (IOException e) {
            System.out.println(e.getMessage());
            return null;
        }
        
        Stardict stardict = new Stardict();
        stardict.setIfo(ifoFile);
        stardict.setDataMap(dataMap);
        
        return stardict;
    }

    /**
     * Create a new empty StarDict, with given MetaData
     * 
     * @param ifo the ifoFile
     */
    public static Stardict build(IfoFile ifo) {
        
        Stardict stardict = new Stardict();
        stardict.setIfo(ifo);
        stardict.setDataMap(new HashMap<String, List<String>>());
        
        return stardict;
    }

    /**
     * Load a dict from an xml representation of the star-data
     * 
     * @param xmlFile the file to load
     */
    public static Stardict build(File xmlFile) {
        
        Stardict star = null;
        try {
            JAXBContext context = JAXBContext.newInstance(zen.ilgo.star.xml.Dictionary.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            InputStream is = new FileInputStream(xmlFile);
            
            SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            URL url = ClassLoader.getSystemResource(STAR_XSD);
            //File schemaFile = new File(url.toURI());
            Schema schema = schemaFactory.newSchema(url);
            unmarshaller.setSchema(schema);
            
            Dictionary dictionary = (Dictionary) unmarshaller.unmarshal(is);
            if (dictionary != null) {
                
                IfoFile ifo = metaToIfo(dictionary.getMeta());
                Map<String, List<String>> dataMap = entriesToDatamap(dictionary.getEntries());
                
                if (ifo != null && dataMap != null) {
                    star = new Stardict();
                    star.setIfo(ifo);
                    star.setDataMap(dataMap);
                }
            }          
            
        } catch (JAXBException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }
        return star;
    }
    
    /**
     * Write the dict as a 'real' stardict to the default location
     * so it can be used by the Stardict GUI
     * 
     * @param star the dictionary to be written
     */
    public static void export(Stardict star) {

        IfoFile ifoFile = star.getIfo();
        StarTuple binTuple = calculateBinaryParts(star);
        updateIfo(ifoFile, binTuple);
        
        try {
            ifoFile.writeIfo();
            binTuple.dictFile.writeBinary();
            binTuple.idxFile.writeBinary();
            if (binTuple.synFile != null) {
                binTuple.synFile.writeBinary();
            }
            
        } catch (IOException e) {
            
        }
    }

    /**
     * Write the dict as a 'real' dict to a custom directory. This can be used by the Stardict GUI
     * if it is moved to the default stardict location
     * 
     * @param starHome the custom directory
     */
    public static void export(Stardict star, String starHome) {

        IfoFile ifoFile = star.getIfo();
        String fileName = ifoFile.getBookname() + ".ifo";
        ifoFile.setFile(new File(starHome, fileName));
        export(star);
    }

    /**
     * write the dict to an xmlFile
     * 
     * @param file the file to write the dict into.
     */
    public static void export(Stardict star, File file) {

        ObjectFactory factory = new ObjectFactory();
        Dictionary dict = factory.createDictionary();
        Meta meta = ifoToMeta(star.getIfo());
        Entries entries = dataMapToEntries(star.getDataMap());
        
        dict.setMeta(meta);
        dict.setEntries(entries);
        
        JAXBContext context;
        OutputStream os = null;
        try {
            context = JAXBContext.newInstance(zen.ilgo.star.xml.Dictionary.class);
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty("jaxb.formatted.output", true);
            //marshaller.setProperty("jaxb.schemaLocation", "http://star.ilgo.zen/star");
            
            SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            URL url = ClassLoader.getSystemResource(STAR_XSD);
            //File schemaFile = new File(url.toURI());
            Schema schema = schemaFactory.newSchema(url);
            marshaller.setSchema(schema);
            
            XMLOutputFactory xmlFactory = XMLOutputFactory.newFactory();
            xmlFactory.setProperty("javax.xml.stream.isRepairingNamespaces",new Boolean(false));
            
            os = new FileOutputStream(file); 
            XMLStreamWriter xmlWriter = xmlFactory.createXMLStreamWriter(os);
            xmlWriter.setPrefix("star", "http://star.ilgo.zen/star");
            xmlWriter.setDefaultNamespace("http://star.ilgo.zen/star");
            marshaller.marshal(dict, os);
            
        } catch (JAXBException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (XMLStreamException e) {
            e.printStackTrace();
        } catch (FactoryConfigurationError e) {
            e.printStackTrace();        
        } catch (SAXException e) {
            e.printStackTrace();            
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    } 
    
    /**
     * Transform a Meta object into an ifo
     * 
     * @param meta the meta
     * @return the ifo
     */
    public static IfoFile metaToIfo(Meta meta) {
        
        String name = meta.getBookname();
        File ifoLocation = new File(STAR_HOME, name + ".ifo");
        IfoFile ifo = new IfoFile(ifoLocation);
        ifo.setBookname(name);
        ifo.setAuthor(meta.getAuthor());
        ifo.setDescription(meta.getDescription());
        ifo.setEmail(meta.getEmail());
        ifo.setWebsite(meta.getWebsite());
        
        XMLGregorianCalendar xmlDate = meta.getDate();
        if (xmlDate != null) {
            ifo.setDate(xmlDate.toString());
        }        
        return ifo;
    }
    
    /**
     * transform an ifo to a Meta object
     * 
     * @param ifo the ifoFile
     * @return the meta object
     */
    public static Meta ifoToMeta(IfoFile ifo) {
        
        Meta meta = new ObjectFactory().createMeta();
        meta.setBookname(ifo.getBookname());
        meta.setAuthor(ifo.getAuthor());
        meta.setDescription(ifo.getDescription());
        meta.setEmail(ifo.getEmail());
        meta.setWebsite(ifo.getWebsite());
        
        return meta;
    }
    
    /**
     * Transform a List of dict Entries toa  dataMap
     * 
     * @param entries the Entries object
     * @return the data Map
     */
    public static Map<String, List<String>> entriesToDatamap(Entries entries) {
        
        Map<String, List<String>> dataMap = new HashMap<String, List<String>>();
        for(zen.ilgo.star.xml.Entry entry : entries.getEntry()) {
            String definition = entry.getDefinition();
            List<String> keys = entry.getKey();
            dataMap.put(definition, keys);
        }
        return dataMap;        
    }
    
    /**
     * Transform a star datamap to an jaxb Entries object
     * 
     * @param dataMap the datamap
     * @return the Entries
     */
    public static Entries dataMapToEntries(Map<String, List<String>> dataMap) {
        
        ObjectFactory factory = new ObjectFactory();
        Entries entries = factory.createEntries();
        
        for (Entry<String, List<String>> dataEntry : dataMap.entrySet()) {
            zen.ilgo.star.xml.Entry entry = factory.createEntry();
            
            entry.setDefinition(dataEntry.getKey());
            entry.getKey().addAll(dataEntry.getValue());
            entries.getEntry().add(entry);
        }        
        return entries;
    }

    /**
     * Creates the dataMap structure by parsing and joining the data in the dict, idx and syn files
     * 
     * @param dictFile the *.dict file
     * @param idxFile the *.idx file
     * @param synFile the *.syn file (optional data)
     * @return the assembled dataMap
     */
    static Map<String, List<String>> createDataMap(DictFile dictFile, IdxFile idxFile, SynFile synFile) {

        Map<String, List<String>> dataMap = new HashMap<String, List<String>>();
        
        Map<Integer, List<String>> idxMap = new HashMap<Integer, List<String>>();
        for (Entry<String, Integer[]> entry : idxFile.getIdxData().entrySet()) {
            
            int offset = entry.getValue()[0];
            List<String> keys = idxMap.get(offset);
            if (keys == null) {
                keys = new ArrayList<String>();
                idxMap.put(offset, keys);
            }
            if (! keys.contains(entry.getKey())) {
                keys.add(entry.getKey());
            }
        }
        
        List<String> mainKeys = new ArrayList<String>(idxFile.getIdxData().keySet());
        Collections.sort(mainKeys);
        for (Entry<String, Integer> entry : synFile.getSynData().entrySet()) {
            
            int offset = entry.getValue();
            String mainKey = mainKeys.get(offset);
            List<String> keys = idxMap.get(idxFile.getIdxData().get(mainKey)[0]);
            if (keys == null) {
                throw new IllegalArgumentException("Main Key for " + entry.getKey() + " not existing.");
            }
            if (! keys.contains(entry.getKey())) {
                keys.add(entry.getKey());
            }
        }
        
        List<String> dictData = dictFile.getDictData();
        if (idxMap.size() == dictData.size()) {
            
            List<Integer> idxKeys = new ArrayList<Integer>(idxMap.keySet());
            Collections.sort(idxKeys);
            for (int n = 0; n < idxKeys.size(); n++) {
                String definition = dictData.get(n);
                int offset = idxKeys.get(n);
                List<String> keys = idxMap.get(offset);
                dataMap.put(definition, keys);
            }
        }        
        return dataMap;
    }     

    /**
     * Separates the stardict's dataMap into its binary parts
     * 
     * @param star the stardict
     * @return a tuple holding the binary parts
     */
    public static StarTuple calculateBinaryParts(Stardict star) {

        if (star.hasUniqueKeys()) {
            DictFile dictFile = createDictFile(star);
            IdxFile idxFile = createIdxFile(star, dictFile.getDictData());
            SynFile synFile = createSynFile(star, idxFile.getIdxData().keySet());
            
            return new StarTuple(idxFile, dictFile, synFile);
        
        } else {
            throw new IllegalArgumentException("Dictionary keys are not unique");
        }
    }    

    private static DictFile createDictFile(Stardict star) {
        
        String location = star.getIfo().getDictLocation() + ".dict";
        return new DictFile(new File(location), star.getAllDefinitions());
    }
    
    private static IdxFile createIdxFile(Stardict star, List<String> dictData) {
        
        Map<String, Integer[]> idxMap = new HashMap<String, Integer[]>();
       
        int offset = 0;
        for(String definition : dictData) {
            
            String key = star.getKeysFor(definition).get(0);
            int size = definition.getBytes().length;
            idxMap.put(key, new Integer[]{offset, size});
            offset += size;
        }
        
        String location = star.getIfo().getDictLocation() + ".idx";
        return new IdxFile(new File(location), idxMap);
    }
    
    /**
     * create a synfile from the stardict
     * 
     * @param star the stardict
     * @param set the idx data map
     * @return the synfile or if no synonym keys are found a NULL
     */
    private static SynFile createSynFile(Stardict star, Set<String> mainKeys) {        
        
        Map<String, Integer> synMap = new HashMap<String, Integer>(); 
        
        List<String> idxKeys = new ArrayList<String>(mainKeys);
        Collections.sort(idxKeys);

        for (int n = 0; n < idxKeys.size(); n++) {
            String mainKey = idxKeys.get(n);
            String definition = star.definitionOf(mainKey);
            List<String> keys = star.getKeysFor(definition);
            keys.remove(mainKey);
            for (String key : keys) {
               synMap.put(key, n);
            }
        }        
        
        if (synMap.isEmpty()) {
            return null;
        
        } else {
            String location = star.getIfo().getDictLocation() + ".syn";
            return new SynFile(new File(location), synMap);
        }        
    }

    /**
     * Setting the necessary integer values for dict, idx and syn;
     * 
     * @param ifoFile the ifoFile to be updated
     * @param binTuple the tuple holding the binary parts
     */
    public static void updateIfo(IfoFile ifoFile, StarTuple binTuple) {
        
        ifoFile.setWordcount(binTuple.dictFile.getDictData().size());
        ifoFile.setIdxfilesize(binTuple.idxFile.getSize());
        if (binTuple.synFile != null) {
            ifoFile.setSynwordcount(binTuple.synFile.getSynData().size());
        }
    }
    
    /**
     * A simple class to return multiple values from a method
     *
     * @author roger holenweger (ilgo711@gmail.com)
     * @since Aug 23, 2011
     */
    static class StarTuple {
        
        public final IdxFile idxFile;
        public final DictFile dictFile;
        public final SynFile synFile;
        
        public StarTuple(IdxFile idxFile, DictFile dictFile, SynFile synFile) {
            this.idxFile = idxFile;
            this.dictFile = dictFile;
            this.synFile = synFile;
        }         
    }
}
