package zen.ilgo.pipeline.gcdTest;

import java.math.BigInteger;

import zen.ilgo.pipeline.ifaces.ICollector;

public class SimpleGcdTest {

    /**
     * @param args
     */
    public static void main(String[] args) {
        SimpleGcdTest t = new SimpleGcdTest();
        t.test();
    }
    
    void test() {
        long start = System.currentTimeMillis();
        ICollector<BigIntPair> collector = new BigIntCollector();
        while (collector.hasNext()) {
            
            BigIntPair pair = collector.next();            
            BigInteger bigA = pair.getA();
            BigInteger bigB = pair.getB();
            BigInteger gcd = bigA.gcd(bigB);
            
            System.out.println("GCD " + pair + " calculated: " + gcd);     
        }
        
        double secs = (System.currentTimeMillis() - start) / 1000.0;
        System.out.println(String.format("%.1f", secs));
    }
}
