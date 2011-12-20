package zen.ilgo.wget;

public interface IWgetChanges {

	/**
	 * Adding listeners to this Wget instance.
	 * 
	 * @param listener
	 */
	public abstract void addWgetChangeListener(IWgetChangeListener listener);

	/**
	 * All listeners will be removed at the end of the Thread run.
	 * 
	 * @param listener
	 */
	public abstract void removeWgetChangeListeners();

	/**
	 * 
	 * @param state the present state of the download 
	 * @param bytes how many bytes have been downloaded
	 */
	public abstract void fireWgetChangeEvent(int state, long bytes);

}