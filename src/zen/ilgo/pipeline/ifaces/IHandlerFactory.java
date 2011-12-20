package zen.ilgo.pipeline.ifaces;

import java.util.List;

/**
 * The IHandlerFactory produces lists of ordered handlers.
 * Each Pipeline will have its own list of handlers to be processed.
 * The point of this Framework is that pipelines can execute
 * in parallel. So the task should not have to wait for synchronization
 * issues. 
 *
 * @author roger holenweger (ilgo711@gmail.com)
 * @since Sep 18, 2009
 */
public interface IHandlerFactory<T> {
    
    /**
     * Create an ordered list of Handlers.
     * The Pipeline will execute the handlers in
     * the order they are returned from this factory
     *
     * @return the list of handler
     */
	public List<IHandler<T>> getHandlerOrder();
}
