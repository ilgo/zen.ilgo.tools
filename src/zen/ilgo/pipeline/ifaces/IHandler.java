package zen.ilgo.pipeline.ifaces;

import zen.ilgo.pipeline.PipelineException;

/**
 * Handlers will execute one Task in a Pipeline.
 *
 * @author roger holenweger (ilgo711@gmail.com)
 * @since Jun 26, 2009
 */
public interface IHandler<T> {

    /**
     * Apply a procedure to T.
     * 
     * @param t the Object of type T to be handled
     * @throws PipelineException
     */
	public void handle(T t) throws PipelineException;
	
	/**
	 * The Handlers Name.
	 * Used when dynamically generating Pipeline-States.
	 * 
	 * @return the Handlers name.
	 */
	public String getHandlerName();

}
