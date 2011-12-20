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
 * Reads and writes the *.idx part of a stardict
 *
 * @author roger holenweger (ilgo711@gmail.com)
 * @since Aug 12, 2011
 */
public class IdxFile {
    
    /**
     * the location of the *.idx part of a stardict
     */
    private final File file; 
    
    /**
     * the mapping of main keys to the dict files offset and lengths 
     */
    private final Map<String, Integer[]> idxData;
    
    private final int idxFilesize;

    /**
     * Constructor for an IdxFile
     * 
     * @param file the idx file
     * @param idxData the index data [offset, len]
     */
    public IdxFile(File file, Map<String, Integer[]> idxData) {
        this.file = file;
        this.idxData = idxData;
        idxFilesize = calculateSize();
    }

    public IdxFile(File file) {
        this(file, new HashMap<String, Integer[]>());
    }

    public void writeBinary() throws IOException {
        
        if(!file.exists()) {
            file.createNewFile();
        }
        DataOutputStream dos = null;
        try {
            dos = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(file))); 
            
            List<String> keys = new ArrayList<String>(idxData.keySet());
            Collections.sort(keys);
            for (String key : keys) {
                byte[] bytes = key.getBytes("UTF-8");
                dos.write(bytes);
                dos.write(0);
                Integer[] offLen = idxData.get(key);
                dos.writeInt(offLen[0]);
                dos.writeInt(offLen[1]);
            }
        } finally {
            if (dos != null) {
                dos.close();
            }
        }
    }

    public void readBinary() throws IOException {
        
        idxData.clear();
        
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
                    int len = dis.readInt();
                    idxData.put(key, new Integer[]{offset, len});
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
    
    /**
     * get the size of the binary idx file
     * 
     * @return the size
     */
    private int calculateSize() {

        int size = 0;
        for (String key: idxData.keySet()) {
            size += key.getBytes().length + 9;
        }        
        return size;
    }

    public Map<String, Integer[]> getIdxData() {
        return idxData;
    }
    
    public int getSize() {
        return idxFilesize;
    }
}