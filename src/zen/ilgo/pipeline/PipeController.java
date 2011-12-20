package zen.ilgo.pipeline;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import zen.ilgo.pipeline.ifaces.IHandlerFactory;

/**
 * 
 * @author roger holenweger (ilgo711@gmail.com)
 * @since Jun 18, 2009
 */
public final class PipeController<T> {

    /**
     * the blocking gate between the Pipelines and the outside world.
     */
    private final SynchronousQueue<T> gate;

    /**
     * The Inserter dispenses photos to pipes.
     */
    private final Inserter inserter;

    /**
     * the handler factory
     */
    private final IHandlerFactory<T> factory;

    private final Pool pool;

    public static Logger log = Logger.getLogger("zen.ilgo.tools.pipeline");

    /**
     * PipeController passes objects to the inserter. Signals shutdown when collector exhausts.
     * 
     * @param factory
     * @param size
     * @throws PipelineException
     */
    public PipeController(IHandlerFactory<T> factory, int size) throws PipelineException {

        this.factory = factory;
        pool = Pool.getInstance();
        gate = new SynchronousQueue<T>();
        inserter = new Inserter(size);
        pool.execute(inserter);
    }

    /**
     * Insert the Object into the gate.
     * 
     * @param t the object to be inserted
     */
    void insert(T t) {
        try {
            // signals collector exhausted
            if (t == null) {
                inserter.alive = false;
            } else {
                gate.put(t);
            }
        } catch (InterruptedException e) {
            log.error("Gate refuses Object", e);
        }
    }

    /**
     * The Inserter offers new Objects to free pipelines.
     * 
     * @author roger holenweger (ilgo711@gmail.com)
     * @since Jun 18, 2009
     */
    final class Inserter implements Runnable {

        /**
         * the list holding the pipes;
         */
        private List<Pipeline<T>> pipes;

        boolean alive;

        /**
         * the constructor
         * 
         * @param gate the gate to receive new photos from.
         * @param pool where all task are executed.
         * @param size the pipelines List size
         * @throws PipelineException
         */
        public Inserter(int size) throws PipelineException {

            pipes = new ArrayList<Pipeline<T>>(size);
            for (int n = 0; n < size; n++) {
                pipes.add(null);
            }
            alive = true;
        }

        public boolean isAlive() {
            return alive;
        }

        /**
         * this will loop until the PipeController gets a NULL and then sets the alive flag to
         * false.
         */
        @Override
        public void run() {

            while (alive) {
                for (int n = 0; n < pipes.size(); n++) {
                    // at start all pipes are null
                    if (pipes.get(n) == null || pipes.get(n).getState() == "END") {
                        try {
                            pipes.set(n, null);
                            T t = null;
                            t = gate.poll(200, TimeUnit.MILLISECONDS);
                            if (t != null) {
                                pipes.set(n, new Pipeline<T>(t, factory.getHandlerOrder()));
                                pool.execute(pipes.get(n));
                            }
                        } catch (InterruptedException e) {
                            log.error("Cannot take Object form Gate");
                        }
                    }
                }
            }
            if (! pool.shutPool()) {
                log.error("Pool did not properly terminate");
            }
            log.info("Pipelines Completed");
        }
    }
}
