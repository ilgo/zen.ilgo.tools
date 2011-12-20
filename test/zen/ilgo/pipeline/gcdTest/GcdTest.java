package zen.ilgo.pipeline.gcdTest;

import zen.ilgo.pipeline.PipelineException;
import zen.ilgo.pipeline.Pipelines;
import zen.ilgo.pipeline.ifaces.ICollector;
import zen.ilgo.pipeline.ifaces.IHandlerFactory;

public class GcdTest {

    //TODO: check the values when using pipelines. 
    
    /**
     * @param args
     */
    public static void main(String[] args) {
        GcdTest t = new GcdTest();
        t.test();
    }

    private void test() {
        
        long start = System.currentTimeMillis();
        
        try {            
            IHandlerFactory<BigIntPair> factory = BigIntHandlerFactory.getFactory();
            ICollector<BigIntPair> collector = new BigIntCollector();
            Pipelines<BigIntPair> pipes = new Pipelines<BigIntPair>(collector, factory, 2);
            pipes.process();
        } catch (PipelineException pe) {
            pe.printStackTrace();
        }
        double secs = (System.currentTimeMillis() - start) / 1000.0;
        System.out.println(String.format("%.1f", secs));
    }
}
