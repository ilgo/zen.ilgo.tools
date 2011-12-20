package zen.ilgo.wget;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

/**
 * Downloads content of source URL to local target. 
 * Since the local target is an OutputStream, the data 
 * can be directly piped into other processors.
 * 
 * Interested parties can register an WgetChangeListener
 * and receive notifications on the state of the download
 * and on how much content has been downloaded. Wget will remove all
 * listeners whenever the Thread reaches its end.
 * A download can be terminated before downloading finishes.
 * 
 * @author ilgo
 * @since October 13, 2008
 */
public class Wget implements Runnable, IWgetChanges {

	// read the lenght of the data to be read
	public static final int WGETSTATE_CONTENT = 1;
	// is int the process of reading data
	public static final int WGETSTATE_READ = 2;
	// has completed the transfer successfully
	public static final int WGETSTATE_TOTAL = 3;
	// has failed to transfer all data
	public static final int WGETSTATE_FAIL = 4;
	
	private final URL source;
	private final OutputStream target;
	protected final String name;
	private int bufSize;
	private int fireEventInterval;
	private List<IWgetChangeListener> listeners;

	public Wget(URL source, OutputStream target) {

		this.source = source;
		this.target = target;
		this.bufSize = 2048;
		this.fireEventInterval = 50;
		String[] pathParts = source.getFile().split("/");
		this.name = pathParts[pathParts.length - 1];
	}

	/**
	 * Sets the buffersize for the In/Out Streams. Defaults to 2048
	 * 
	 * @param bufSize
	 *            the size of the buffer
	 */
	public void setBufferSize(int bufSize) {
		this.bufSize = bufSize;
	}

	/**
	 * Set the interval, how often to send notifications.
	 * After interval * bufSize bytes were read,
	 * notify the listeners
	 * 
	 * @param interval the notification interval
	 */
	public void setEventInterval(int interval) {
		fireEventInterval = interval;
	}

	@Override
	public void run() {

		int contentLenght = 0;
		int total = 0;
		byte[] buffer = new byte[bufSize];
		BufferedInputStream bis = null;
		BufferedOutputStream bos = null;
		try {

			URLConnection conn = source.openConnection();
			contentLenght = conn.getContentLength();
			fireWgetChangeEvent(WGETSTATE_CONTENT, contentLenght);

			bis = new BufferedInputStream(conn.getInputStream(), bufSize);
			bos = new BufferedOutputStream(target, bufSize);

			int read = 0;
			int subtotal = 0;
			int counter = 0;
			
			while ((read = bis.read(buffer)) != -1) {
				bos.write(buffer, 0, read);
				total += read;
				subtotal += read;
				counter++;
				
				// only send an event every fireEventInterval
				if (counter % fireEventInterval == 0) {
					fireWgetChangeEvent(WGETSTATE_READ, subtotal);
					subtotal = 0;
				}
				if (Thread.currentThread().isInterrupted()) {
					throw new InterruptedException("Wget cancelled.");
				}
			}
			if (subtotal != 0) {
				fireWgetChangeEvent(WGETSTATE_READ, subtotal);
			}
		} catch (Exception e) {
			total = -1;

		} finally {
			if (total == -1 || total != contentLenght) {
				fireWgetChangeEvent(WGETSTATE_FAIL, total);
			} else {
				fireWgetChangeEvent(WGETSTATE_TOTAL, total);
			}
			if (bis != null) {
				try {
					bis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}			
			if (bos != null) {
				try {
					bos.flush();
					bos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			removeWgetChangeListeners();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * zen.ilgo.tools.wget.IWgetChanges#addWgetChangeListener(zen.ilgo.tools
	 * .wget.IWgetChangeListener)
	 */
	public void addWgetChangeListener(IWgetChangeListener listener) {
		if (listeners == null) {
			listeners = new ArrayList<IWgetChangeListener>();
		}
		listeners.add(listener);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see zen.ilgo.tools.wget.IWgetChanges#removeWgetChangeListeners()
	 */
	public void removeWgetChangeListeners() {
		if (listeners != null) {
			listeners.clear();
		}
	}

	/**
	 * 
	 * @param state 
	 * @param bytes how many bytes were read since last notification
	 */
	public void fireWgetChangeEvent(int state, long bytes) {

		if (listeners != null) {
			WgetChangeEvent e = new WgetChangeEvent(name, state, bytes);
			for (IWgetChangeListener listener : listeners) {
				listener.wgetStateChanged(e);
			}
		}
	}

	/**
	 * Cancel this running Wget Thread.
	 * 
	 * @return true if the Thread is terminated
	 */
	public boolean cancelWget() {

		boolean cancelled = false;
		Thread thisWget = Thread.currentThread();
		if (thisWget.isAlive()) {
			thisWget.interrupt();
		} else {
			cancelled = true;
		}
		return cancelled;
	}
	
	public String getTargetName() {
	    return name;
	}
}
