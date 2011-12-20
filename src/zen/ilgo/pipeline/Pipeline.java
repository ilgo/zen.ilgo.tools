package zen.ilgo.pipeline;

import java.util.List;

import org.apache.log4j.Logger;

import zen.ilgo.pipeline.ifaces.IHandler;

/**
 * A Pipeline will execute all tasks for a T-object to be completed. A template pattern that will
 * take a object and then gradually fill in all missing information.
 * 
 * @author roger holenweger (ilgo711@gmail.com)
 * @since Jun 18, 2009
 */
public class Pipeline<T> implements Runnable {

    public static Logger log = Logger.getLogger("zen.ilgo.tools.pipeline");

    /**
     * The list of handlers to be executed on the Object t
     */
    private final List<IHandler<T>> handlers;

    /**
     * The object that is to be passed through the handlers.
     */
    private final T t;

    /**
     * to keep track where the pipeline is executing. the state names are generated from the
     * Handlers name.
     */
    public String state;

    /**
     * the constructor.
     * 
     * @param t the object to be processed.
     * @param handlers the handlers that process the object
     */
    public Pipeline(T t, List<IHandler<T>> handlers) {
        this.t = t;
        this.handlers = handlers;
    }

    @Override
    public void run() {

        state = "START";
        try {
            for (IHandler<T> handler : handlers) {
                state = handler.getHandlerName();
                handler.handle(t);
            }
            state = "END";
            log.info(t + " Sucess.");
        } catch (Exception e) {
            log.error(t + " at " + state + " : " + e);
            e.printStackTrace();
        }
    }

    public String getState() {
        return state;
    }
}
