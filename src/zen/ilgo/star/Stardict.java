package zen.ilgo.star;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * The Main Stardict object.
 * Querying and manipulating the stardict
 * 
 * @author roger holenweger (ilgo711@gmail.com)
 * @since Aug 14, 2011
 */
public class Stardict {

    private IfoFile ifo;

    private Map<String, List<String>> dataMap;
    

    public void setDataMap(Map<String, List<String>> dataMap) {
        this.dataMap = dataMap;
    }
    
    Map<String, List<String>> getDataMap() {
        return dataMap;
    }

    /**
     * Set a new Ifo File for this dictionary
     * 
     * @param ifo the MetaData in the ifo
     */
    public void setIfo(IfoFile ifo) {
        this.ifo = ifo;        
    }

    /**
     * get the MetaData ifo file
     * 
     * @return the ifo file
     */
    public IfoFile getIfo() {
        return ifo;
    }

    /**
     * Get all Definitions in this dictionary
     * 
     * @return the list of all keys
     */
    public List<String> getAllDefinitions() {
        return new ArrayList<String>(dataMap.keySet());
    }

    /**
     * Return all keys & synonyms for the given definition
     * 
     * @param definition the definition
     * @return the keys associated with this definition
     */
    public List<String> getKeysFor(String definition) {
        
        List<String> keys = new ArrayList<String>();
        if (dataMap.containsKey(definition)) {
            keys.addAll(dataMap.get(definition));
        }
        return keys;
    }

    /**
     * Get the definition for the key term
     * 
     * @param key the key
     * @return the definition of the key
     */
    public String definitionOf(String key) {
        
        for (Entry<String, List<String>> entry : dataMap.entrySet()) {
            if (entry.getValue().contains(key)) {
                return entry.getKey();
            }
        }
        return "";
    }

    /**
     * Adds a new Entry to the dictionary
     * 
     * @param key the entry key
     * @param definition the entry definition
     */
    public void addEntry(String definition, String key) {
        List<String> keys = new ArrayList<String>();
        keys.add(key);
        addEntry(definition, keys);
    }
    
    /**
     * Adds a new Entry to the dictionary
     * 
     * @param key the keys 
     * @param definition the entry definition
     */
    public void addEntry(String definition, List<String> keys) {

        dataMap.put(definition, keys);        
    }

    /**
     * Add another synonym for a root key
     * 
     * @param rootKey the key that is already defined
     * @param synonym the synonym for the root key
     */
    public void addSynonym(String rootKey, String synonym) {

        String definition = definitionOf(rootKey);
        List<String> keys = dataMap.get(definition);
        if (keys != null) {
            keys.add(synonym);
        }
    }

    /**
     * Remove the key/synonym. Shall also remove the definition if no keys are left
     * 
     * @param key the key/synonym to be removed
     */
    public void removeKey(String key) {        

        String definition = definitionOf(key);
        List<String> keys = dataMap.get(definition);
        if (keys != null) {
            keys.remove(key);     
        }
    }
    
    /**
     * remove a definition and its keys from the data map
     * 
     * @param definition the definition to be removed
     */
    public void removeDefinition(String definition) {
        
        dataMap.remove(definition);
    }
    
    /**
     * Test if the data map contains any key in two different locations;
     * @return
     */
    public boolean hasUniqueKeys() {
        
        List<String> allKeys = new ArrayList<String>();
        for (List<String> keys : dataMap.values()) {
            allKeys.addAll(keys);
        }
        Set<String> uniqueKeys = new HashSet<String>(allKeys);
        
        // prints out the duplicate key and the key sets
        if (uniqueKeys.size() != allKeys.size()) {
            List<String> duplicates = new ArrayList<String>(allKeys);
            for (String key : uniqueKeys) {
                int pos = duplicates.indexOf(key);
                duplicates.remove(pos);
            }
            for (String duplicateKey : duplicates) {
                for (String definition : dataMap.keySet()) {
                    List<String> keys = getKeysFor(definition);
                    if (keys.contains(duplicateKey)) {
                        System.out.println(duplicateKey + " => " + Arrays.toString(keys.toArray(new String[keys.size()])));
                    }
                }
            }           
        }        
        return allKeys.size() == uniqueKeys.size();
    }
}
