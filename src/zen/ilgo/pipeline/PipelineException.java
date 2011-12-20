package zen.ilgo.pipeline;

/**
 * An Exception to collect errors in a Pipeline.
 *
 * @author roger holenweger (ilgo711@gmail.com)
 * @since Jun 26, 2009
 */
@SuppressWarnings("serial")
public class PipelineException extends Exception {

	/**
	 * 
	 */
	public PipelineException() {
	}

	/**
	 * @param message
	 */
	public PipelineException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public PipelineException(Throwable cause) {
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public PipelineException(String message, Throwable cause) {
		super(message, cause);
	}

}
