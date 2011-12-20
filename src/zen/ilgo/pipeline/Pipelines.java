package zen.ilgo.pipeline;

import java.awt.event.TextEvent;
import java.awt.event.TextListener;

import org.apache.log4j.Logger;

import zen.ilgo.pipeline.ifaces.ICollector;
import zen.ilgo.pipeline.ifaces.IHandlerFactory;

/**
 * The outer shell to the pipelines framework.
 * It gets all needed classes in the constructor.
 * 
 * For an example on how to use it:
 *     zen.ilgo.tools.pipeline.test.Tester
 * 
 * @author roger holenweger (ilgo711@gmail.com)
 * @since Sep 18, 2009
 */
public class Pipelines<T> {
    
    /**
     * Dispenses objects to the Framework
     */
    private final ICollector<T> collector;
    
    /**
     * receives the objects dispensed by the collector.
     */
    private final PipeController<T> controller;

    private TextListener listener;

    public static Logger log = Logger.getLogger("zen.ilgo.tools.pipeline");

    /**
     * The Constructor
     * 
     * @param collector Dispenses the T Objects
     * @param size the amount of Pipelines to be used in parallel
     * @throws PipelineException
     */
    public Pipelines(ICollector<T> collector, IHandlerFactory<T> factory, int size)
            throws PipelineException {

        controller = new PipeController<T>(factory, size);
        this.collector = collector;
    }

    /**
     * Process all T objects.
     */
    public void process() {

        message("Started Pipelines");
        T t;
        // String date = null;
        while (collector.hasNext()) {
            t = collector.next();
            message("insert: " + t);
            controller.insert(t);
        }
        t = null;
        message("Collector exhausted");
        controller.insert(t);
        removeTextListener();
    }

    /**
     * send a message to log or listeners.
     * 
     * @param msg the message
     */
    void message(String msg) {
        log.debug(msg);
        if (listener != null)
            listener.textValueChanged(new TextEvent(msg, TextEvent.TEXT_VALUE_CHANGED));
    }

    /**
     * we only allow a single TextListener.
     * 
     * @param listener the listener
     */
    public void registerTextListener(TextListener listener) {
        this.listener = listener;
    }

    public void removeTextListener() {
        if (listener != null) {
            listener = null;
        }
    }
}
