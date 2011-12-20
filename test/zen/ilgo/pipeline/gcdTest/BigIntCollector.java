package zen.ilgo.pipeline.gcdTest;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import zen.ilgo.pipeline.ifaces.ICollector;

/**
 * Collects the 10 pairs of bigInts
 * 
 * @author roger holenweger (ilgo711@gmail.com)
 * @since Oct 4, 2009
 */
public class BigIntCollector implements ICollector<BigIntPair> {

    private final String bigIntLocation;
    private final int pairs;
    private int count = 1;
    private String format = "biggies%02d";

    private byte[] bytes;

    public BigIntCollector() {
        pairs = 10 * 2;
        bigIntLocation = "/home/ilgo/Code/workspace/zen.ilgo.tools/test/zen/ilgo/pipeline/gcdTest/BigIntegers";
    }

    /**
     * Testing constructor
     * 
     * @param pairs
     * @param location
     */
    public BigIntCollector(int pairs, String location) {
        this.pairs = pairs * 2;
        bigIntLocation = location;
    }

    @Override
    public boolean hasNext() {
        return count < pairs ? true : false;
    }

    @Override
    public BigIntPair next() {

        //System.out.println("Get Big: " + count);
        File bigFile1 = new File(bigIntLocation, String.format(format, count));
        String biggie1 = getBiggie(bigFile1);
        count++;

        //System.out.println("Get Big: " + count);
        File bigFile2 = new File(bigIntLocation, String.format(format, count));
        String biggie2 = getBiggie(bigFile2);
        count++;

        return new BigIntPair(biggie1.trim(), biggie2.trim());
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }

    String getBiggie(File biggie) {

        try {
            // my files are only 300k so i can cast to int
            int len = (int) biggie.length();
            bytes = new byte[len];
            BufferedInputStream bis = new BufferedInputStream(new FileInputStream(biggie));
            int readBytes = bis.read(bytes);
            if (readBytes != len) {
                throw new IOException();
            }
        } catch (IOException e) {
            bytes = new byte[0];
        }
        return new String(bytes);
    }
}
