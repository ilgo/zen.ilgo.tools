package zen.ilgo.pipeline;

import zen.ilgo.pipeline.PipelineException;
import zen.ilgo.pipeline.Pipelines;
import zen.ilgo.pipeline.ifaces.ICollector;
import zen.ilgo.pipeline.ifaces.IHandlerFactory;

public class PipelineTester {

    /**
     * @param args
     */
    public static void main(String[] args) {

        PipelineTester t = new PipelineTester();
        t.test();
    }

    private void test() {
        try {
            IHandlerFactory<Data> factory = HandlerFactory.getFactory();
            ICollector<Data> collector = new TestCollector(10);
            Pipelines<Data> pipes = new Pipelines<Data>(collector, factory, 4);
            pipes.process();
        } catch (PipelineException pe) {
            pe.printStackTrace();
        }
    }
}
