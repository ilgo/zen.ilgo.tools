package zen.ilgo.star;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Calendar;

/**
 * ifo file bean with setters and getters
 */
public class IfoFile {

    /**
     * the location of the *.ifo part of a stardict 
     */
    private File file;    
    
    /* the next 3 fields are set, API user cannot change them */
    private final String header = "StarDict's dict ifo file";      
    
    private final String version = "2.4.2";    

    private String sametypesequence = "m";
    
    /* these fields can be modified by the user */
    private String bookname;

    private int wordcount = 0;

    private int synwordcount = 0;

    private int idxfilesize = 0;

    private String author = "";

    private String email  = "";    

    private String website = "";

    private String description = "";

    private String date = "";

    /**
     * 
     * @param file with absolute path pointing to the binary ifo location
     * @param dictionaryName the name of the dictionary
     */
    public IfoFile(File file, String dictionaryName) {
        this.file = file;
        this.bookname = dictionaryName; 
        this.author = System.getProperty("user.name");
        DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM);    
        this.date = df.format(Calendar.getInstance().getTime());
    }
    
    public IfoFile(File file) {
        this(file, "");
    }
    
    /**
     * Set the location where the ifo and the binary parts will be written to.
     * 
     * @param file the new location
     */
    public void setFile(File file) {
        this.file = file;
    }
    
    /** 
     * gets the location where this dict will be written
     * consists of a the dir and the name, without any dict specific ending
     * 
     * @return the resulting dict location
     */ 
    public String getDictLocation() {
        return file.getAbsolutePath().replace(".ifo", "");
    }
    
    public void writeIfo() throws IOException {        
        
        if (isValid()) {
            if (!file.exists()) {
                file.createNewFile();
            }
            BufferedWriter bw = null;
            try {
                bw = new BufferedWriter(new FileWriter(file));
                bw.write(this.toString());
                
            } finally {
                if (bw != null) {
                    bw.close();
                }
            }
        }
    }    

    public void readIfo() throws IOException {
        
        resetFieldValues();
        
        BufferedReader br = null;
        try {
           br = new BufferedReader(new FileReader(file));
           String line = null;
           while ((line = br.readLine()) != null) {
               String[] parts = line.split("=");
               if (parts.length == 2) {
                   setFieldValue(parts[0], parts[1]);
               }
           }            
        } finally {
            if (br != null) {
                br.close();
            }
        }
    }

    /**
     * Checks if all needed field values are set.
     * 
     * @return true if all needed fields contain correct values
     */
    public boolean isValid() {
        
        boolean bookValid = bookname != null && !"".equals(bookname);
        boolean authorValid = author != null && !"".equals(author);
        boolean dateValid = date != null && !"".equals(date);
        boolean wordCountValid = wordcount > 0;
        boolean idxFileSizeValid = idxfilesize > 0;
        boolean synWordCountValid = synwordcount >= 0;
        
        return bookValid
                && wordCountValid
                && synWordCountValid
                && idxFileSizeValid
                && authorValid
                && dateValid;
    }
    
    void resetFieldValues() {
        bookname = "";
        author = "";
        date = "";
        wordcount = 0;
        idxfilesize = 0;
        synwordcount = 0;
    }
    
    void setFieldValue(String field, String value) {
        
        if ("bookname".equals(field)) {
            setBookname(value);
       
        } else if ("wordcount".equals(field)) {
            setWordcount(Integer.valueOf(value));
       
        } else if ("idxfilesize".equals(field)) {
            setIdxfilesize(Integer.valueOf(value));
       
        } else if ("synwordcount".equals(field)) {
            setSynwordcount(Integer.valueOf(value));
        
        } else if ("author".equals(field)) {
            setAuthor(value);
        
        } else if ("email".equals(field)) {
            setEmail(value);
        
        } else if ("website".equals(field)) {
            setWebsite(value);
        
        } else if ("description".equals(field)) {
            setDescription(value);
        
        } else if ("date".equals(field)) {
            setDate(value);
        }
    }
    
    @Override
    public String toString() {
        
        String sep = System.getProperty("line.separator");        
        String fmt = new String("%s=%s" + sep);
        StringBuilder sb = new StringBuilder();
        sb.append(header + sep);
        sb.append(String.format(fmt, "version", version));
        sb.append(String.format(fmt, "bookname", bookname));
        sb.append(String.format(fmt, "wordcount", Integer.toString(wordcount)));
        sb.append(String.format(fmt, "idxfilesize", Integer.toString(idxfilesize)));
        if (synwordcount > 0) {
            sb.append(String.format(fmt, "synwordcount", Integer.toString(synwordcount)));
        }
        sb.append(String.format(fmt, "author", author));
        sb.append(String.format(fmt, "email", email));
        sb.append(String.format(fmt, "website", website));
        sb.append(String.format(fmt, "description", description));
        sb.append(String.format(fmt, "date", date));
        sb.append(String.format(fmt, "sametypesequence", sametypesequence));
        
        return sb.toString();
    }

    public String getBookname() {
        return bookname;
    }
    
    public void setBookname(String bookname) {
        this.bookname = bookname;
    }

    public int getWordcount() {
        return wordcount;
    }

    public void setWordcount(int wordcount) {
        this.wordcount = wordcount;
    }

    public int getSynwordcount() {
        return synwordcount;
    }

    public void setSynwordcount(int synwordcount) {
        this.synwordcount = synwordcount;
    }

    public int getIdxfilesize() {
        return idxfilesize;
    }

    public void setIdxfilesize(int idxfilesize) {
        this.idxfilesize = idxfilesize;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setPangoStyle(boolean usePangoStyle) {        
        sametypesequence = usePangoStyle ? "g" : "m";
    }

    String getSameTypeSequence() {
        return sametypesequence;
    }
}