package zen.ilgo.star;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Reads and writes the *.syn part of a stardict
 *
 * @author roger holenweger (ilgo711@gmail.com)
 * @since Aug 12, 2011
 */
public class SynFile {

    /**
     * the location of the *.syn part of a stardict 
     */
    private final File file;    

    /**
     * the maapings of synonymous keys to the dict offset
     */
    private final Map<String, Integer> synData;
    
    /**
     * 
     * @param file
     * @param synData
     */
    public SynFile(File file, Map<String, Integer> synData) {
        this.file = file;
        this.synData = synData;
    }
    
    public SynFile(File file) {
        this(file, new HashMap<String, Integer>());
    }

    /**
     * write the syn data as binary
     */
    public void writeBinary() throws IOException {
        
        if(!file.exists()) {
            file.createNewFile();
        }
        DataOutputStream dos = null;
        try {
            dos = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(file))); 
            
            List<String> keys = new ArrayList<String>(synData.keySet());
            Collections.sort(keys);
            for (String key : keys) {
                byte[] bytes = key.getBytes("UTF-8");
                dos.write(bytes);
                dos.write(0);
                dos.writeInt(synData.get(key));
            }
        } finally {
            if (dos != null) {
                dos.close();
            }
        }
    }

    /**
     * read the syn data from a binary
     */
    public void readBinary() throws IOException {
        
        synData.clear();
        
        if(!file.exists()) {
            return;
        }
        
        DataInputStream dis = null;
        try {
            byte[] buffer = new byte[128];
            int idx = 0;
            int b = 0;
            
            dis = new DataInputStream(new BufferedInputStream(new FileInputStream(file))); 

            while((b = dis.read()) != -1) {
                if (b == 0) {
                    String key = new String(buffer, 0, idx, "UTF-8");
                    int offset = dis.readInt();
                    synData.put(key, offset);
                    idx = 0;
                } else {
                    if (idx == buffer.length) {
                        buffer = Arrays.copyOf(buffer, buffer.length * 2);
                    }
                    buffer[idx] = (byte) b;
                    idx ++;
                }
            }
        } finally {
            if (dis != null) {
                dis.close();
            }
        }
    }

    public Map<String, Integer> getSynData() {
        return synData;
    }
}