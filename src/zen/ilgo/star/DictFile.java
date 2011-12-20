package zen.ilgo.star;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Reads and writes the *.dict part of a stardict
 *
 * @author roger holenweger (ilgo711@gmail.com)
 * @since Aug 12, 2011
 */
class DictFile {

    /**
     * the location of the *.dict part of a stardict
     */
    private final File file;
    
    private static Comparator<Integer[]> idxDataComparator;
    
    /**
     * all definitions
     */
    private final List<String> dictData;

    public DictFile(File file) {
        this(file, new ArrayList<String>());
    }

    /**
     * Construct a DictFile
     * 
     * @param file the *.dict file
     * @param dictData the dictData structure, can be empty or filled
     */
    public DictFile(File file, List<String> dictData) {
        this.file = file;
        this.dictData = dictData;
    }

    /**
     * writes the List of definitions to \0 terminated strings
     */
    public void writeBinary() throws IOException {
        
        if(!file.exists()) {
            file.createNewFile();
        }
        BufferedOutputStream bos = null;
        try {
            bos = new BufferedOutputStream(new FileOutputStream(file)); 
            for (String definition : dictData) {
                bos.write(definition.getBytes("UTF-8"));
            }
            
        } finally {
            if (bos != null) {
                bos.close();
            }
        }
    }

    /**
     * reads the binary \0 terminated strings
     * @param idxData 
     */
    public void readBinary(List<Integer[]> offsetDatas) throws IOException {
        
        dictData.clear();
        Collections.sort(offsetDatas, getComparator());
        BufferedInputStream bis = null;
        try {
            byte[] buffer = new byte[1024];
            bis = new BufferedInputStream(new FileInputStream(file)); 
            for(Integer[] offsetData : offsetDatas) {
                
                int len = offsetData[1];
                while (len >= buffer.length) {
                    buffer = new byte[buffer.length * 2];
                }
                bis.read(buffer, 0, len);
                String definition = new String(buffer, 0, len, "UTF-8");
                if (! "".equals(definition)) {
                    dictData.add(definition);  
                }
            }
       
        } finally {
            if (bis != null) {
                bis.close();
            }
        }
    }

    public List<String> getDictData() {
        return dictData;
    }
    

    public Comparator<Integer[]> getComparator() {
        
        if (idxDataComparator == null) {
            
            idxDataComparator = new Comparator<Integer[]>() {

                @Override
                public int compare(Integer[] i1, Integer[] i2) {
                    return i1[0].compareTo(i2[0]);
                }                
            };
        }
        return idxDataComparator;
    }
}