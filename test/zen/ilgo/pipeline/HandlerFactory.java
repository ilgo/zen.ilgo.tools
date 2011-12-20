package zen.ilgo.pipeline;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.apache.log4j.Logger;

import zen.ilgo.pipeline.PipelineException;
import zen.ilgo.pipeline.Pool;
import zen.ilgo.pipeline.ifaces.IHandler;
import zen.ilgo.pipeline.ifaces.IHandlerFactory;

public class HandlerFactory implements IHandlerFactory<Data> {

    public static Logger log = Logger.getLogger("zen.ilgo.tools.pipeline");
    private static IHandlerFactory<Data> factory;
    private static Random rand = new Random();

    private HandlerFactory() {

    }

    public static IHandlerFactory<Data> getFactory() {
        if (factory == null) {
            factory = new HandlerFactory();
        }
        return factory;
    }

    /**
     * The handlers to be processed by a pipeline. Each Pipeline gats its own sets of Handlers, so
     * that we dont have concurrent issues.
     * 
     * @return the handler list
     */
    public List<IHandler<Data>> getHandlerOrder() {

        List<IHandler<Data>> handlers = new ArrayList<IHandler<Data>>();

        handlers.add(new AddHandler());
        handlers.add(new MultHandler());
        handlers.add(new SubHandler());
        handlers.add(new ModHandler());
        handlers.add(new LogHandler());

        return handlers;
    }

    abstract class NumBaseHandler implements IHandler<Data> {

        public String getHandlerName() {
            return ("NumBase");
        }
    }

    class AddHandler extends NumBaseHandler {

        public AddHandler() {
            super();
        }

        @Override
        public void handle(Data t) throws PipelineException {
            int add = rand.nextInt(10000);
            t.setNum(t.getNum() + add);
        }

        @Override
        public String getHandlerName() {
            return ("Add");
        }

    }

    class SubHandler extends NumBaseHandler {

        public SubHandler() {
            super();
        }

        @Override
        public void handle(Data t) throws PipelineException {
            int sub = rand.nextInt(10000);
            t.setNum(t.getNum() - sub);
        }

        @Override
        public String getHandlerName() {
            return ("Sub");
        }
    }

    class MultHandler extends NumBaseHandler {

        public MultHandler() {
            super();
        }

        @Override
        public void handle(final Data t) throws PipelineException {

            Pool pool = Pool.getInstance();
            Future<String> future = pool.submit(new Callable<String>() {
                public String call() {
                    try {
                        Thread.sleep(rand.nextInt(2000) + 3000);
                        int mul = rand.nextInt(10000);
                        t.setNum(t.getNum() * mul);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    return Integer.toString(t.getNum());
                }
            });
            try {
                log.debug(future.get());
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (ExecutionException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        @Override
        public String getHandlerName() {
            return ("Mul");
        }
    }

    class ModHandler extends NumBaseHandler {

        public ModHandler() {
            super();
        }

        @Override
        public void handle(Data t) throws PipelineException {
            int mod = rand.nextInt(10000);
            t.setNum(t.getNum() % mod);
        }

        @Override
        public String getHandlerName() {
            return ("Mod");
        }
    }

    class LogHandler extends NumBaseHandler {

        public LogHandler() {
            super();
        }

        @Override
        public void handle(final Data t) throws PipelineException {

            // try {
            // Thread.sleep(rand.nextInt(5000) + 5000);
            // } catch (InterruptedException e) {
            // // TODO Auto-generated catch block
            // e.printStackTrace();
            // }
            log.debug("log: " + t + " - " + t.getNum());
        }

        @Override
        public String getHandlerName() {
            return ("Log");
        }
    }
}
