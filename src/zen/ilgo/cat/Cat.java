package zen.ilgo.cat;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.List;

/**
 * a Java version of a simple 'cat' command 
 * 
 * should call flush after adding all resources.
 *
 * @author roger holenweger (ilgo711@gmail.com)
 * @since Jan 2, 2011
 */
public class Cat {

    private final BufferedOutputStream target;
    
    public Cat(File target) throws FileNotFoundException {
        this(new FileOutputStream(target));
    }
    
    public Cat(OutputStream target) {
        if (!(target instanceof BufferedOutputStream)) {
            this.target = new BufferedOutputStream(target);
        } else {
            this.target = (BufferedOutputStream) target;
        }
    }

    /**
     * add the content of all theses inputstreams
     * 
     * @param sources a collection of 
     * @return
     * @throws IOException 
     */
    public int add(List<InputStream> sources) throws IOException {
        
        int bytes = 0;
        for (InputStream source : sources) {
            bytes += add(source);
        }
        return bytes;
    }    
    
    
    public int add(InputStream source) throws IOException {
        
        BufferedInputStream bis;
        if (!(source instanceof BufferedInputStream)) {
            bis = new BufferedInputStream(source);
        } else {
            bis = (BufferedInputStream) source;
        }
        int totalRead = 0;
        
        byte[] buffer = new byte[1024];
        int read;
        while((read = bis.read(buffer)) != -1) {
            target.write(buffer, 0, read);
            totalRead += read;
        }
        if (bis != null) {
            try {
                bis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return totalRead;
    }
    
    /**
     * Add this File to the cat stream
     * 
     * @param source the file to add
     * @return the amount of bytes added
     * @throws IOException 
     */
    public int add(File source) throws IOException {
        
        InputStream fis = new FileInputStream(source);
        int bytes = add(fis);
        if (fis != null) {
            try {
                fis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return bytes;
    }
    
    /**
     * add all files in this directory
     * 
     * @param dir all files inside this directory will be added
     * @return the amount of bytes added
     * @throws IOException 
     */
    public int add(Collection<File> files) throws IOException {

        int bytes = 0;
        for (File source : files) {
            bytes += add(source); 
        }
        return bytes;        
    }
    
    /**
     * Call this after having added all resources.
     * 
     * @return
     */
    public boolean flush() {
        
        boolean result = false;
        try {
            if (target != null) {
                target.flush();
                target.close();
                result = true;
            }           
        } catch (IOException e) {
            try {
                target.close();
                result = false;
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
        return result;
    }
}
