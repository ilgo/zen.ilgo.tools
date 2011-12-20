package zen.ilgo.pipeline.gcdTest;

import java.math.BigInteger;

/**
 * a simple Tuple holding two BigIntegers
 * they will be processed by a Pipelines<BigIntPair>
 *
 * @author roger holenweger (ilgo711@gmail.com)
 * @since Oct 4, 2009
 */
class BigIntPair {

    private static int count = 1;
    private final int idx;
    private final BigInteger a;
    private final BigInteger b;

    public BigIntPair(String aNum, String bNum) {
        
        a = new BigInteger(aNum);
        b = new BigInteger(bNum);
        idx = count++;
    }

    public BigInteger getA() {
        return a;
    }

    public BigInteger getB() {
        return b;
    }
    
    public int getIdx() {
        return idx;
    }
    
    @Override
    public String toString() {
        return Integer.toString(idx);
    }

}
