package zen.ilgo.wget;

/**
 * Encapsulating the Events that Wget sends to Listeners.
 *
 * @author ilgo (ilgo711@gmail.com)
 * @since Apr 4, 2009
 */
public class WgetChangeEvent {

	private final String name;
    private final int wgetState;
    private final long bytes;

    /**
     * @param name the name of the file to download
     * @param wgetState the state of the download
     * @param bytes how many bytes have been downloaded
     */
    public WgetChangeEvent(String name, int wgetState, long bytes) {

    	this.name = name;
        this.wgetState = wgetState;
        this.bytes = bytes;
    }
        
    /**
     * 
     * @return the name
     */
    public String getName() {
		return name;
	}

	/**
     * 
     * @return the state
     */
    public int getWgetState() {
        return wgetState;
    }

    /**
     * 
     * @return the bytes count
     */
    public long getBytes() {
        return bytes;
    }
    
    public String toString() {
    	return hashCode() + " : " + name + " : " + bytes + " : state " + wgetState;
    }
}